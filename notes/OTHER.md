# General Notes/Gotchas


## Ingress
> WARNING: This API is experimental and under active development
https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#IngressRule

https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#DestinationPolicy
> Note: Destination policies will be applied only if the corresponding tagged instances are explicitly routed to. In other words, for every destination policy defined, at least one route rule must refer to the service version indicated in the destination policy.
