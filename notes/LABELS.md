# Labels
https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/

## Required
Labels that will be required for kubernetes and/or istio features

### App
The name of the application. Should be consistent with the code base being build/deployed. For example, the repo labeled title-edge should be labeled as `app: title-edge`. This is later used for a k8s service selector, and ultimately becomes part of Istio's traffic management definitions.

### Version
The version of the application being deployed into a pod. I believe this should use [semver](https://semver.org/), or some other versioning system. The tagged version of the code should mirror this label. This label ultimately becomes a selector in Istio destination policies, specifically for weighted traffic(used in things like canary deploys).


## Labels and Istio/Prometheus Metrics
> Can they(labels) be added as metrics tags though istio mixer and sent to Prometheus?

Yes. Labels can be used as metric values as shown in [this tutorial](https://istio.io/docs/tasks/telemetry/metrics-logs/#collecting-new-telemetry-data) and explained in this [section](https://istio.io/docs/tasks/telemetry/metrics-logs/#understanding-the-telemetry-configuration).

In short, you can use kubernetes labels as fields(dimensions) in new metrics that are defined at a system level:

```
# Configuration for metric instances
apiVersion: "config.istio.io/v1alpha2"
kind: metric
metadata:
  name: typecount
  namespace: istio-system
spec:
  value: "1"
  dimensions:
    source: source.service | "unknown"
    destination: destination.service | "unknown"
    type: source.labels["type"] | "unknown" //Deployment tags can be referenced as such
    version: source.labels["version"] | "unknown"
  monitored_resource_type: '"UNSPECIFIED"'
```

Note that you will still need metrics handlers, as specified in the previous tutorial links.