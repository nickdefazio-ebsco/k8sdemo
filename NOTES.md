# Notes

## Namespaces
Namespaces affect istio by being a qualifier for finding a service:
https://istio.io/docs/concepts/traffic-management/rules-configuration.html#qualify-rules-by-destination
https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#DestinationPolicy

### Namespaces and Route Rules/Destination Policies/etc
Route Rules and destination policies still apply when going across namespaces as well. In this demo repo, this is displayed by the title-middle service (rma namespace) being accessile by both title-edge(same namespace) and search-edge(dh namespace). Both edge services are subject to the 50/50 weight rule, and the circuit breaker destination policy.


### Namespaces and Ingress
Ingress can be applied on a per namespace basis, [example 1](scripts/k8s/ingress_rma.yaml), example 2(scripts/k8s/ingress_dh.yaml)


### Namespacing best practices
For clarity, and for proper application, route rules should specify the namespace as part of the destination. The route rule should also be applied within the namespace where the service resides(or default). [Example definition](title-middle/k8s/route/title-middle-route.yaml), and example application:
`kubectl create -f routerule.yaml --namespace=ns1`

### Pros
* Allows opportunity for better organization of services
* Self documenting, explicit use
* Lessens naming collisions
* Istio handles them without issue

### Cons
* Additional configuration in both service definition and pipeline



## Labels
https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/

### Required
Labels that will be required for kubernetes and/or istio features

#### App
The name of the application. Should be consistent with the code base being build/deployed. For example, the repo labeled title-edge should be labeled as `app: title-edge`. This is later used for a k8s service selector, and ultimately becomes part of Istio's traffic management definitions.

#### Version
The version of the application being deployed into a pod. I believe this should use [semver](https://semver.org/), or some other versioning system. The tagged version of the code should mirror this label. This label ultimately becomes a selector in Istio destination policies, specifically for weighted traffic(used in things like canary deploys).


## General Notes/Gotchas


### Ingress
> WARNING: This API is experimental and under active development
https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#IngressRule

https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#DestinationPolicy
> Note: Destination policies will be applied only if the corresponding tagged instances are explicitly routed to. In other words, for every destination policy defined, at least one route rule must refer to the service version indicated in the destination policy.
