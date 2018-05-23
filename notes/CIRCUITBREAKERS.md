# Circuit Breakers

## Circuit Breaking in Istio

Circuit breaking is currently implemented as part of Istio's [destination policy definition](https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#CircuitBreaker). At it's core, it's using [Envoy's outlier detection](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/outlier)

### Which version to use?
At the time of writing this, the docs show use of v1alpha1, the examples use v1alpha2, and v1alpha3 is on the horizon. v1alpha3 will rework how circuit breakers are configured, so for the time being, v1alpha2 is recommended, and used in this project.

## Application design patterns

## General resiliency patterns with Istio