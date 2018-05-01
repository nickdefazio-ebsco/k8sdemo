echo 'Creating title-middle deployment...'
kubectl apply -f title-middle/k8s/deployment.yaml --namespace=rma
echo 'Creating title-edge deployment...'
kubectl apply -f title-edge/k8s/deployment.yaml --namespace=rma
echo 'Creating title-middle service...'
kubectl apply -f title-middle/k8s/service.yaml --namespace=rma
echo 'Creating search-edge deployment...'
kubectl apply -f search-edge/k8s/deployment.yaml --namespace=dh
