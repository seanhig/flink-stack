


```
kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml

```

```
helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.9.0/
helm install flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator
```


*** Using a custom built flink-kubernetes-operator image
> Docs have the 2nd command quite wrong, below is the working version
```
helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.9.0/
helm install --set image.repository=idstudios/flink-kubernetes-operator --set image.tag=latest flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator
```


```
kubectl apply -f mysql-enriched-orders-flink-jobs.yaml
kubectl port-forward svc/mysql-enriched-orders-rest 8081