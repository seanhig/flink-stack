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

## Build the Enriched Orders Job Image
Next we build an immutable Flink container containing the `enriched-orders-job-1.0.0.jar` job jar:

```
build-job-image.sh
```

or 

```
cp ../streaming-etl-java/target/enriched-orders-job-1.0.0.jar ./job-image
docker build -t idstudios/flink-enriched-orders:1.20 ./job-image
```

> __Note:__ the job image is built on the base `idstudios/flink:1.20` docker flink image used by the `flink-stack`.  To have this properly tagged for re-use locally, run the [../../docker-images/flink/build-image.sh](../../docker-images/flink/build-image.sh). 


The `idstudios/flink-enriched-orders:1.20` docker image now contains our job jar, as well as all the required CDC and JDBC driver dependencies and Iceberg related jars, and will be supplied to the `Kubernetes Operator` to use as our `Flink Image`.

Note that the JAR must be already `packaged` in the [streaming-etl-java](../streaming-etl-java/) example as it is dependent on that `Java job jar`.  If the image build errors out ensure that the JAR has been built and exists.

## Deploy Enriched Orders Job Image
This will deploy the `enriched-orders-job-1.0.0.jar` via the `idstudios/flink-enriched-orders:1.20` docker image to a new dedicated Flink cluster deployment in K8s.
```
kubectl create -f enriched-orders-cluster.yaml
```

which is defined as follows:

```
apiVersion: flink.apache.org/v1beta1
kind: FlinkDeployment
metadata:
  name: enriched-orders-cluster
spec:
  image: idstudios/flink-enriched-orders:1.20
  flinkVersion: v1_19
  flinkConfiguration:
    taskmanager.numberOfTaskSlots: "5"
  serviceAccount: flink
  jobManager:
    resource:
      memory: "2048m"
      cpu: 1
  taskManager:
    resource:
      memory: "2048m"
      cpu: 1
  job:
    jarURI: local:///flink-jobjars/enriched-orders-job-1.0.0.jar
    parallelism: 2
    upgradeMode: stateless
```

Note the `spec:image` setting uses our custom built job jar so that the `job:jarURI` setting can find it locally.  Although `jobURI` can leverage external `HTTPS://` and `S3://` and other distributed stores, pre-building a dedicated job image allows for the inclusion of all core system dependencies, which often aren't suitable for deployment in a fat job `uber.jar`.

Once running we can port-forward to the new service:
```
kubectl port-forward svc/enriched-orders-cluster-rest 8081
```

Now we can open the browser to the dedicated [Job Manager UI](http://localhost:8081) and see our cluster with the running job.

#### Using a custom built flink-kubernetes-operator image
In the odd use case where you need to replace the `flink-operator` image, this is also doable - but the documentation has a few errors and isn't entirely clear on the purpose.  Experimentation revealed it had no affect on the profile of the launched instances and for this we needed a job image as above.

> Note current docs have the 2nd command quite wrong, below is the working version:
```
helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.9.0/
helm install --set image.repository=idstudios/flink-kubernetes-operator --set image.tag=latest flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator
```
