kubectl delete services/title-middle --namespace=rma
kubectl delete services/title-edge --namespace=rma
kubectl delete services/search-edge --namespace=dh
kubectl delete deployments/title-middle-v1 --namespace=rma
kubectl delete deployments/title-middle-v2 --namespace=rma
kubectl delete deployments/title-edge --namespace=rma
kubectl delete deployments/fortio-deploy --namespace=rma
kubectl delete deployments/fortio-deploy --namespace=dh
kubectl delete deployments/search-edge --namespace=dh

kubectl delete virtualservice --namespace=rma --all
kubectl delete destinationrule --namespace=rma --all
kubectl delete ingress rma-ingress --namespace=rma
kubectl delete ingress dh-ingress --namespace=dh
kubectl delete -f scripts/k8s/metric.yaml
