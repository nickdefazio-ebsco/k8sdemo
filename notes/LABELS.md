# Labels
https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/

## Required
Labels that will be required for kubernetes and/or istio features

### App
The name of the application. Should be consistent with the code base being build/deployed. For example, the repo labeled title-edge should be labeled as `app: title-edge`. This is later used for a k8s service selector, and ultimately becomes part of Istio's traffic management definitions.

### Version
The version of the application being deployed into a pod. I believe this should use [semver](https://semver.org/), or some other versioning system. The tagged version of the code should mirror this label. This label ultimately becomes a selector in Istio destination policies, specifically for weighted traffic(used in things like canary deploys).