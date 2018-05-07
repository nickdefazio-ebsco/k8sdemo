kubectl delete services/title-middle --namespace=rma
kubectl delete services/title-edge --namespace=rma
kubectl delete services/search-edge --namespace=dh
kubectl delete deployments/title-middle-v1 --namespace=rma
kubectl delete deployments/title-middle-v2 --namespace=rma
kubectl delete deployments/title-edge --namespace=rma
kubectl delete deployments/search-edge --namespace=dh

kubectl delete routerules title-middle-90-10 --namespace=rma
kubectl delete ingress rma-ingress --namespace=rma
