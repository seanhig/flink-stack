# Deploying to Kubernetes via the Operator

Flink supports two types of K8s deployment: Native and Operator.

Native deployment appears to require a local flink binary and is somewhat imperative.

The `Flink K8s Operator` enables declarative deployments using `YAML`, so that is what we will use here.

## Requirements

- Docker 
- Kubernetes Cluster with configured `kubectl` (Docker Desktop version works ootb with examples)
- Helm

## Deploy the Flink Kubernetes Operator

Cert-manager dependency first:
```
kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
```

Helm install of the `flink-kubernetes-operator`:
```
helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.9.0/
helm install flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator
```

## Deploy Enriched Orders Job

This will deploy the `enriched-orders-job-1.0.0.jar` to a new dedicated Flink cluster deployment in K8s.
```
kubectl create -f enriched-orders-cluster.yaml
```

Note that the JAR, which must be already packaged in the [streaming-etl-java](../streaming-etl-java/) example is pre-built into a Docker container, which appears to be the best way to manage these sorts of deployments.  The pre-built custom Flink image also contains all required CDC and JDBC drivers.

```
kubectl port-forward svc/enriched-orders-cluster-rest 8081
```

Now we can open the browser to [localhost:8081](http://localhost:8081) and see our cluster with the running job.

#### Using a custom built flink-kubernetes-operator image
> Docs have the 2nd command quite wrong, below is the working version
```
helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.9.0/
helm install --set image.repository=idstudios/flink-kubernetes-operator --set image.tag=latest flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator
```
