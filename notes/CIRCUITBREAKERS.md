# Circuit Breakers

## Circuit Breaking in Istio

Circuit breaking is currently implemented as part of Istio's [destination policy definition](https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#CircuitBreaker). At it's core, it's using [Envoy's outlier detection](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/outlier)

### Which version to use?
At the time of writing this, the docs show use of v1alpha1, the examples use v1alpha2, and v1alpha3 is on the horizon. v1alpha3 will [rework how circuit breakers are configured](https://istio.io/docs/reference/config/istio.networking.v1alpha3.html#OutlierDetection), [but is still under active development](https://istio.io/about/notes/0.7.html), so for the time being, v1alpha2 is recommended, and used in this project along with istio 0.7.1.

## General resiliency patterns with Istio
[Docs](https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#CircuitBreaker.SimpleCircuitBreakerPolicy)

* `httpConsecutiveErrors` should be tuned to a reasonable level. Default to 5 seems too low at scale.
* `httpMaxEjectionPercent` should be set to 100 to allow _all_ hosts within the load balancer pool to be ejected if necessary.



## Application design patterns

### Istio

Rely on istio for most circuit breaker concerns. Istio(Envoy) can detect HTTP consecutive 5xx errors and eject hosts accordingly. This should be able to handle most applications needs for failing fast and not causing partial/full cascading failures. Additionally, most applications can likely implement fallback logic by way of standard exception/error handling. Note that this pattern does result in another hop to the envoy proxy, but due to it being a container to container network hop within the same pod, this should be negligible.

As part of overall setup and configuration, keep [cluster panic](https://www.envoyproxy.io/docs/envoy/latest/configuration/cluster_manager/cluster_runtime#core) in mind.


### Circuit Breaker Libraries (Hystrix, etc)

Envoy's circuit breaking and circuit breaker libraries like Hystrix are not mutually exlusive - they can work alongside one another. If using Istio, you likely do not need both, aside from specialized concerns such as:
* Thread pool management
* Highly specific error handling/fallback logic
* You want to use the Hystrix metrics/dashboard features(though Istio should provide something similar)