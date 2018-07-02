# Readiness and Liveness Probes

## What are Liveness and Readiness Probes?

### Liveness Probe
> The kubelet uses liveness probes to know when to restart a Container. For example, liveness probes could catch a deadlock, where an application is running, but unable to make progress. Restarting a Container in such a state can help to make the application more available despite bugs.

### Readiness Probe
> The kubelet uses readiness probes to know when a Container is ready to start accepting traffic. A Pod is considered ready when all of its Containers are ready. One use of this signal is to control which Pods are used as backends for Services. When a Pod is not ready, it is removed from Service load balancers.


## Should I use one, or both?
Both. The level of effort to provide both, especially in a REST/Java/Spring environment is so low, and the benefits are very good, specifically the readiness probe functionality.


## Istio's Effect on Readiness and Liveness Probes
One of Istio's features is [mutual TLS authentication](https://istio.io/docs/concepts/security/mutual-tls/) between services. When this is enabled, it will [prevent liveness/readiness probes from working](https://github.com/istio/istio/issues/2628). 

At the time of writing this, the accepted workaround seems to be to expose a health check on a different port than your application, and configure the probes accordingly. In the demo application, this is accomplished by configuring Spring Actuator's port to run on 8081 instead of 8080.

## Configuration for Liveness and Readiness Probes

There's a [variety of ways](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-probes/) to configure liveness and readiness probes in Kubernetes. Since the plan is to build, mostly, REST microservices, this doc will explain how to do this with an HTTP Request. We need to:


1. Configure Spring Actuator to expose it's health check on a different port

application.properties
```
management.server.port=8081
```


2. Configure liveness and readiness probes(defined as part of the Kubernetes deployment) to ping our health check url on 8081:


deployment.yaml
```
kind: Deployment
metadata:
  name: search-edge
  labels:
    app: search-edge
    version: 0.1.0
spec:
  replicas: 1
  selector:
    matchLabels:
      app: search-edge
  template:
    metadata:
      labels:
        app: search-edge
    spec:
      containers:
      - name: search-edge
        image: search-edge:0.1.0
        ports:
        - containerPort: 8080
        imagePullPolicy: IfNotPresent #For local dev, use local docker repo
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 5
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 5
```

### Additional Configuration Options
- **initialDelaySeconds** - Time in seconds that kubelet waits before attempting to ping the given url
- **periodSeconds** - Time in seconds that the kubelet waits inbetween probes (i.e probe every x seconds)
- **timeoutSeconds** - Time in seconds the kubelet will wait before assuming a timeout
