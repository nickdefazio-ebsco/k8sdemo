# Circuit Breakers

## Circuit Breaking in Istio

Circuit breaking is currently implemented as part of Istio's [destination policy definition](https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#CircuitBreaker). At it's core, it's using [Envoy's outlier detection](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/outlier)

### Which version to use?
At the time of writing this, the docs show use of v1alpha1, the examples use v1alpha2, and v1alpha3 is on the horizon. v1alpha3 will [rework how circuit breakers are configured](https://istio.io/docs/reference/config/istio.networking.v1alpha3.html#OutlierDetection), [but is still under active development](https://istio.io/about/notes/0.7.html), so for the time being, v1alpha2 is recommended, and used in this project along with istio 0.7.1.

## General resiliency patterns with Istio
[Docs](https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#CircuitBreaker.SimpleCircuitBreakerPolicy)

* TODO: Figure out how we can fine tune max connections, and requests. Is this a question of setting hard limits, or best practices for determining custom config? How does this affect auto scaling?
* `httpConsecutiveErrors` should be tuned to a reasonable level. Default to 5 seems too low at scale.
* `httpMaxEjectionPercent` should be set to 100 to allow _all_ hosts within the load balancer pool to be ejected if necessary.



## Application design patterns

### Hystrix vs Istio circuit breaking
* TODO: There are some major differences in hystrix app level circuit breaking vs istio circuit breaking, specifically around granularity. Give a high level overview of these differences