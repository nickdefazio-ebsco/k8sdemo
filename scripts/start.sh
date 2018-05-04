echo 'Creating title-middle v1 deployment...'
kubectl apply -f title-middle-v1/k8s/deployment.yaml --namespace=rma
echo 'Creating title-middle v2 deployment...'
kubectl apply -f title-middle-v2/k8s/deployment.yaml --namespace=rma
echo 'Creating title-edge deployment...'
kubectl apply -f title-edge/k8s/deployment.yaml --namespace=rma
echo 'Creating title-middle service...'
kubectl apply -f title-middle-v1/k8s/service.yaml --namespace=rma
echo 'Creating search-edge deployment...'
kubectl apply -f search-edge/k8s/deployment.yaml --namespace=dh
