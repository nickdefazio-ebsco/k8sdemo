kubectl -n istio-system get po -l istio=ingress -o jsonpath='{.items[0].status.hostIP}'
kubectl -n istio-system get svc istio-ingress
