echo 'Creating title-middle deployment...'
kubectl create -f title-middle/k8s/deployment.yaml
echo 'Creating title-edge deployment...'
kubectl create -f title-edge/k8s/deployment.yaml
echo 'Creating title-middle service...'
kubectl create -f title-middle/k8s/service.yaml
