echo 'Starting Minikube...'
minikube start --extra-config=controller-manager.ClusterSigningCertFile="/var/lib/localkube/certs/ca.crt" --extra-config=controller-manager.ClusterSigningKeyFile="/var/lib/localkube/certs/ca.key" --extra-config=apiserver.Admission.PluginNames=NamespaceLifecycle,LimitRanger,ServiceAccount,PersistentVolumeLabel,DefaultStorageClass,DefaultTolerationSeconds,MutatingAdmissionWebhook,ValidatingAdmissionWebhook,ResourceQuota --kubernetes-version=v1.9.0

echo 'Installing base istio...'
kubectl create -f istio/istio-0.8.0/install/kubernetes/istio-demo.yaml

eval $(minikube docker-env)

kubectl create -f scripts/k8s/namespace_dh.yaml
kubectl create -f scripts/k8s/namespace_rma.yaml

kubectl label namespace dh istio-injection=enabled
kubectl label namespace rma istio-injection=enabled
