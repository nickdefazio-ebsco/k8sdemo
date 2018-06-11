# Circuit Breakers

## Versions
At the time of writing this, Istio is currently on 0.8.0, which introduces the v1alpha3 networking API, and announces plans to deprecate the previous versions. This doc will cover circuit breaking within 0.8.0 or greater.

## Circuit Breaking in Istio(aka Outlier Detection)
In Istio, circuit breaking is a function of the service mesh, labeled as "Outlier Detection", and implemented as a traffic policy in the DestinationRule spec. It uses [Envoy's outlier detection](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/outlier) functionality to achieve this, and can be configured in Istio's [destination rules](https://istio.io/docs/reference/config/istio.networking.v1alpha3/#OutlierDetection). 

### Outlier Detection Capabilities
Envoy's outlier detection functions [somewhat differently than Hystrix](http://blog.christianposta.com/microservices/comparing-envoy-and-istio-circuit-breaking-with-netflix-hystrix/). In short, a good way to look at it is as a passive health check. It tracks failures(specifically as HTTP 500 errors) and timeouts, and can eject bad hosts from a load balancing pool when failures (outliers) are detected. This differs from Hystrix, which can provide fine grained application level circuit breakers and fallbacks. Envoy's outlier detection as implemented in Istio, at the time of writing this doc, is not capable of informing the application of specific errors, just returning HTTP 503.


### Tuning
There are four outlier [values that can be tuned for HTTP based services](https://istio.io/docs/reference/config/istio.networking.v1alpha3/#OutlierDetection.HTTPSettings):

**consecutiveErrors** - The number of consecutive 500 errors needed to eject a host from the load balancing pool. This can be tune if you wish to be more, or less strict about how many failures should be considered too many from your application/service. Generally speaking, this value should not get too high. Defaults to 5.

Note that this is different than something like [error threshold in Hystrix](https://github.com/Netflix/Hystrix/wiki/configuration#circuitbreakererrorthresholdpercentage) as no rolling window is used here, just a consecutive failure count. Remember that the goal here is to eject unhealthy services, not trigger fallback logic.


**interval** - How long inbetween checking for outliers. Defaults to 10s. In testing, anywhere from 10s to 30s seemed reasonable. This is somewhat comparable to the [rolling window timer in Hystrix](https://github.com/Netflix/Hystrix/wiki/configuration#metrics.rollingStats.timeInMilliseconds). 

**baseEjectionTime** - How long an host that has been deemed unhealthy should be ejected for. This value increases by the number of times the host has been ejected. So for example, if host A has been ejected 1 time, and baseEjectionTime is configured to 30s, it will be ejected(i.e not routed to) for 30s. If it is placed back in the LB pool and ejected again, it remains ejected for 60s, and so on. This can be tuned for a variety of reasons, depending on how long it takes for your application to become "healthy" again, whether thats automatically done, or based on notification and manual intervention.

**maxEjectionPercent** - The maximum percentage of hosts in the LB pool that can be ejected at once. This defaults to 10%, as a means of not removing _all_ hosts for a given service. This may seem drastically different from something like Hystrix, but remember, we are passively health checking a single host, not checking upstream application logic and integration with downstream services as a whole like we would in Hystrix. Tune to 100% if you wish to be able to eject _all_ hosts in the event of an outage or some other error.

#### maxEjectionPercent and the Panic Threshold
Envoy implements what's known as a [Panic Threshold](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/load_balancing.html#panic-threshold) which they define as:

> During load balancing, Envoy will generally only consider healthy hosts in an upstream cluster. However, if the percentage of healthy hosts in the cluster becomes too low, Envoy will disregard health status and balance amongst all hosts.

The panic threshold must be considered when tuning the maxEjectionPercent, as if (using the defaults) 50% or more of hosts are ejected, the host health is now ignored. This can make testing difficult. These values can be configured, but not yet by way of Istio - https://github.com/istio/istio/issues/4488.


## Setting up outlier detection
Using the [title-middle Istio configuration in this repo](../title-middle/k8s/istio) as an example:
```
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: title-middle-destination
spec:
  host: title-middle
  trafficPolicy:
    loadBalancer:
      simple: RANDOM
    outlierDetection:
      http:
        consecutiveErrors: 5
        interval: 30s
        baseEjectionTime: 5m
        maxEjectionPercent: 100
  subsets:
  - name: 0.1.0
    labels:
      version: 0.1.0
  - name: 0.2.0
    labels:
      version: 0.2.0
```

This also requires a VirtualService definition as well:
```
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: title-middle-route
spec:
  hosts:
  - title-middle
  http:
  - route:
    - destination:
        host: title-middle
        subset: 0.1.0
      weight: 50
    - destination:
        host: title-middle
        subset: 0.2.0
      weight: 50
```

### A Note on Weighted Traffic
Note that in this example, we are using weighted traffic to split between versions of title-middle. The outlier detection configuration listed in the DestinationRule above *is applied to both versions, but errors are counted and tracked per version*. This means that if I have tuned Istio to eject hosts after 5 consecutive errors, split traffic between two versions, and both versions always return an HTTP 500, that after 5 total requests 0 hosts will be ejected. After 10 (5 for v1, 5 for v2), the unhealthy host from the v1 pool and v2 pool will both be ejected.


## Debugging
When testing, you may run:
`kubectl exec -ti --namespace=${namespace} ${pod} -- curl localhost:15000/stats | grep outlier_detection`

On your pod with envoy running in it. This will print out some basic stats regarding outlier detection. Using this project's applications as an example:
```
kubectl exec -ti --namespace=rma title-edge-ccb74f74f-zzgm8 -- curl localhost:15000/stats | grep outlier_detection
Defaulting container name to title-edge.
Use 'kubectl describe pod/title-edge-ccb74f74f-zzgm8 -n rma' to see all of the containers in this pod.
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_active: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_consecutive_5xx: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_consecutive_5xx: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_consecutive_gateway_failure: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_success_rate: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_consecutive_5xx: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_consecutive_gateway_failure: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_success_rate: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_total: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_overflow: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_success_rate: 0
cluster.outbound|8080|0.1.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_total: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_active: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_consecutive_5xx: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_consecutive_5xx: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_consecutive_gateway_failure: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_success_rate: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_consecutive_5xx: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_consecutive_gateway_failure: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_success_rate: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_total: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_overflow: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_success_rate: 0
cluster.outbound|8080|0.2.0|title-middle.rma.svc.cluster.local.outlier_detection.ejections_total: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_active: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_consecutive_5xx: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_consecutive_5xx: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_consecutive_gateway_failure: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_detected_success_rate: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_consecutive_5xx: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_consecutive_gateway_failure: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_success_rate: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_enforced_total: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_overflow: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_success_rate: 0
cluster.outbound|8080||title-middle.rma.svc.cluster.local.outlier_detection.ejections_total: 0
```


## Connection Pools

In addition to outlier detection, Istio also offers some capabilities regarding [connection pooling](https://istio.io/docs/reference/config/istio.networking.v1alpha3/#ConnectionPoolSettings). Connection pooling allows services to define things like maximum connections, and timeouts at a service level. Though not really related to outlier detection, this functionality is comparable to things like timeout and maximum connections in [Hystrix](https://github.com/Netflix/Hystrix/wiki/Configuration#fallback). Note that this uses Envoy's [circuit breaker](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/circuit_breaking) functionality. It's goal is NOT to eject hosts from the load balancer pool, but instead to bulkhead services. [This article](http://blog.christianposta.com/microservices/comparing-envoy-and-istio-circuit-breaking-with-netflix-hystrix/) sums it up as:

> Envoy “circuit breaking” is more like Hystrix bulkhead and “outlier detection” is more similar to Hystrix circuit-breaker

### Tuning
Istio allows tuning of several [HTTP](https://istio.io/docs/reference/config/istio.networking.v1alpha3/#ConnectionPoolSettings.HTTPSettings) and [TCP](https://istio.io/docs/reference/config/istio.networking.v1alpha3/#ConnectionPoolSettings.TCPSettings)(shared with HTTP) settings:

#### TCP

**maxConnections** - Max concurrent connections for a given destination. This should be tuned based on how many hosts are present for a given service, and how many concurrent connections a single host within a service can handle. 

**connectTimeout** - Timeout duration for a single connection in a given destination. This depends highly on the service, but using something like Hystrix as a measure, 1-10s is likely sufficient.



#### HTTP

**http1MaxPendingRequests** - Maximum number of concurrent pending HTTP 1 requests. Defaults to 1024, should be tuned based on throughput.

**http2MaxRequests** - Maximum number of concurrent pending HTTP 2 requests. Defaults to 1024, should be tuned based on throughput.

**maxRequestsPerConnection** - Max requests per connection. Can be tuned to 1 to disable [HTTP persistant connections](https://en.wikipedia.org/wiki/HTTP_persistent_connection).

**maxRetries** - Max number of retries to the cluster. From the envoy docs:

> The maximum number of retries that can be outstanding to all hosts in a cluster at any given time. In general we recommend aggressively circuit breaking retries so that retries for sporadic failures are allowed but the overall retry volume cannot explode and cause large scale cascading failure.



## General Application Design Patterns

### Istio

Rely on istio for most circuit breaker concerns. Istio(Envoy) can detect HTTP consecutive 5xx errors and eject hosts accordingly. This should be able to handle most applications needs for failing fast and not causing partial/full cascading failures. Additionally, most applications can likely implement fallback logic by way of standard exception/error handling. Note that this pattern does result in another hop to the envoy proxy, but due to it being a container to container network hop within the same pod, this should be negligible.

### Circuit Breaker Libraries (Hystrix, etc)

Envoy's circuit breaking and circuit breaker libraries like Hystrix are not mutually exlusive - they can work alongside one another. If using Istio, you likely do not need both, aside from specialized concerns such as:
* Thread pool management
* Highly specific error handling/fallback logic
* You want to use the Hystrix metrics/dashboard features(though Istio should provide something similar)

In general, Hystrix can move from a standard part of app development to more of a specialized tool.

### Further Reading
http://blog.christianposta.com/microservices/comparing-envoy-and-istio-circuit-breaking-with-netflix-hystrix/
https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/circuit_breaking