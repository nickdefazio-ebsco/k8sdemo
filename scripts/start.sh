echo 'Building and tagging apps...'
cd title-edge
gradle clean build
cd ..
cd title-middle/v1
gradle clean build
cd ..
cd v2
gradle clean build
cd ../..
cd search-edge
gradle clean build
cd ..
docker build title-edge/ --tag title-edge:0.1.0
docker build title-middle/v1/ --tag title-middle:0.1.0
docker build title-middle/v2/ --tag title-middle:0.2.0
docker build search-edge/ --tag search-edge:0.1.0

echo 'Building and tagging complete. Creating k8s deployment and services...'

echo 'Creating title-middle v1 deployment...'
kubectl apply -f title-middle/k8s/deployment-v1.yaml --namespace=rma
echo 'Creating title-middle v2 deployment...'
kubectl apply -f title-middle/k8s/deployment-v2.yaml --namespace=rma
echo 'Creating title-edge deployment...'
kubectl apply -f title-edge/k8s/deployment.yaml --namespace=rma
echo 'Creating title-middle service...'
kubectl apply -f title-middle/k8s/service.yaml --namespace=rma
echo 'Creating search-edge deployment...'
kubectl apply -f search-edge/k8s/deployment.yaml --namespace=dh

echo 'k8s deployment complete'
