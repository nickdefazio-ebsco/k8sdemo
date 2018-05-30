# Namespaces
Namespaces affect istio by being a qualifier for finding a service:
https://istio.io/docs/concepts/traffic-management/rules-configuration.html#qualify-rules-by-destination
https://istio.io/docs/reference/config/istio.routing.v1alpha1.html#DestinationPolicy

## Namespaces and Route Rules/Destination Policies/etc
Route Rules and destination policies still apply when going across namespaces as well. In this demo repo, this is displayed by the title-middle service (rma namespace) being accessile by both title-edge(same namespace) and search-edge(dh namespace). Both edge services are subject to the 50/50 weight rule, and the circuit breaker destination policy.


## Namespaces and Ingress
Ingress can be applied on a per namespace basis, [example 1](scripts/k8s/ingress_rma.yaml), example 2(scripts/k8s/ingress_dh.yaml)


## Namespacing best practices
For clarity, and for proper application, route rules should specify the namespace as part of the destination. The route rule should also be applied within the namespace where the service resides(or default). [Example definition](title-middle/k8s/route/title-middle-route.yaml), and example application:
`kubectl create -f routerule.yaml --namespace=ns1`

Additionally, avoid using the `default` kubernetes namespace. Even if products/teams are separated by cluster, pick a namespace and use it. In theory this would make combining and rearranging clusters easier if that needs to happen.


## Pros
* Allows opportunity for better organization of services
* Self documenting, explicit use
* Lessens naming collisions
* Istio handles them without issue

## Cons
* Additional configuration in both service definition and pipeline
