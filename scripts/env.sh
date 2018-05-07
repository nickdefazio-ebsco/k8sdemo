echo 'Starting Minikube...'
minikube start --extra-config=controller-manager.ClusterSigningCertFile="/var/lib/localkube/certs/ca.crt" --extra-config=controller-manager.ClusterSigningKeyFile="/var/lib/localkube/certs/ca.key" --extra-config=apiserver.Admission.PluginNames=NamespaceLifecycle,LimitRanger,ServiceAccount,PersistentVolumeLabel,DefaultStorageClass,DefaultTolerationSeconds,MutatingAdmissionWebhook,ValidatingAdmissionWebhook,ResourceQuota --kubernetes-version=v1.9.0

##eval $(minikube docker-env)

echo 'Installing base istio...'
kubectl apply -f istio/istio-0.7.1/install/kubernetes/istio.yaml

echo 'Configuring for automatic sidecar injection...'
echo 'Adding certs'
./istio/istio-0.7.1/install/kubernetes/webhook-create-signed-cert.sh --service istio-sidecar-injector --namespace istio-system --secret sidecar-injector-certs
echo 'Adding sidecar injection configmap'
kubectl apply -f istio/istio-0.7.1/install/kubernetes/istio-sidecar-injector-configmap-release.yaml
echo 'Configuring webhook'
cat istio/istio-0.7.1/install/kubernetes/istio-sidecar-injector.yaml |./istio/istio-0.7.1/install/kubernetes/webhook-patch-ca-bundle.sh > istio/istio-0.7.1/install/kubernetes/istio-sidecar-injector-with-ca-bundle.yaml
echo 'Installing webhook'
kubectl apply -f istio/istio-0.7.1/install/kubernetes/istio-sidecar-injector-with-ca-bundle.yaml

export PATH=$PWD/istio/istio-0.7.1/bin:$PATH

minikube addons enable ingress

kubectl create -f scripts/k8s/namespace_dh.yaml
kubectl create -f scripts/k8s/namespace_rma.yaml

kubectl label namespace dh istio-injection=enabled
kubectl label namespace rma istio-injection=enabled
