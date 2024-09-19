# Deploying to Kubernetes via the Operator

Flink supports two types of Kubernetes deployment: 

1. Native 
2. Operator.

Native deployment appears to require a local flink binary and is somewhat imperative.

The `Flink K8s Operator` enables declarative deployments using `YAML`, so that is what we will use here.

## Requirements

- Docker 
- Kubernetes Cluster with configured `kubectl` (Docker Desktop version works ootb with examples)
- Helm

## Flink Operator Deployments
There are two types of deployment with the operator:

1. Application
2. Session Cluster

`Application` clusters are limited to a single job. 
With an `Application` you can package all your jars and dependencies up into an immutable docker image and deploy as a single unit.  However as it is limited to only a single Flink Job, it is not fit for our purpose.  

`Session Cluster` enables you to spin up a new Flink cluster and then assign `FlinkSessionJobs` to it.

We us the `Session Cluster` approach because we have two Flink jobs involved in the flow.

> This got complicated fairly quickly

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

## Build the Enriched Orders Job Image and Job Jar Server Image
Next we build Flink `Job Image` container containing all the required dependencies and drivers.  This image will be used by the operator to deploy our `Session Clusters`.  And also build the `Job Jar Image` which makes our custom `enriched-orders-jobs-1.0.0.jar` available within the cluster.

```
build-images.sh
```

This builds the two docker images mentioned:

1. `idstudios/flink-session-cluster:1.20`
2. `idstudios/flink-jobjars:1.1`

> __Note:__ the `idstudios/flink-session-cluster:1.20` job image is built on the base `idstudios/flink:1.20` docker flink image used by the `flink-stack`.  To have this properly tagged for re-use locally, run the [../../docker-images/flink/build-image.sh](../../docker-images/flink/build-image.sh). 

> __Note:__ the JAR must be already `packaged` in the [streaming-etl-java](../streaming-etl-java/) example as it is dependent on that `Java job jar`.  If the image build errors out ensure that the JAR has been built and exists.

The `idstudios/flink-jobjars:1.1` docker image now contains our job jar served up via `HTTP` within the K8s cluster.  This is important as the `Flink Kubernetes Operator` will pre-validate the `jarURI`, and so the job jars need to be accessible to both the operator pod and the `Session Cluster` instances. `Nginx` is used to serve up the `enriched-orders-jobs-1.0.0.jar` to the pods in the cluster.

> In production we would probably use something like S3 to host our job jars, but local `Nginx` keeps the dev environment simple.

> It makes sense to seperate the `Job Jars` from the deployed container image.  If we handled deployments with an entire image, all jobs would be affected.  The `Session Cluster` job image only contains the required dependencies for our target jobs, but the jobs themselves are free to evolve and be re-deployed independently as per the `FlinkSessionJob` configuration yaml and the `jobURI`.

## Configure K8s Secrets
Secrets are referenced in the [session-cluster.yaml](./cluster-deploy/session-cluster.yaml) job spec, and must be pre-defined in the cluster, as per:

```
# Database credentials
kubectl create secret generic enriched-orders-mysql-erpdb-password --from-literal=secret=Fender2000
kubectl create secret generic enriched-orders-postgres-shipdb-password --from-literal=secret=Fender2000
kubectl create secret generic enriched-orders-mysql-opsdb-password --from-literal=secret=Fender2000

# AWS credentials
kubectl create secret generic enriched-orders-aws-access-key-id --from-literal=key=XXX
kubectl create secret generic enriched-orders-aws-secret-access-key --from-literal=key=XXX

```

The `enriched-orders-jobs.properties` file defines properties in the form of:

```
mysql.erpdb.db.password=secret
```

which can be overridden using ENVIRONMENT variables by converting the ENV name to uppercase and replacing the `.` with `_`. Eg.

```
MYSQL_ERPDB_DB_PASSWORD=secret
```

The ENV var will override any `.properties` file setting.  This is useful for managing Kubernetes Secrets in deployments, as demonstrated in the `enriched-orders-cluster.yaml`.

## Configure Kubernetes ConfigMap for Properties
The `enriched-orders-jobs.properties` file is defined in a `K8s ConfigMap` and mapped into the target job image pod at a default location where the `Java Job Jar` expects to find it.

## Deploy Enriched Orders Session Cluster
Make sure to replace `/Users/seanhig/Workspace/flink-stack/examples/k8s/ha_data` with the correct local path to this repo in the [session-cluster.yaml](./cluster-deploy/session-cluster.yaml).

Then deploy:

```
kubectl apply -f cluster-deploy
```

> The [cluster-deploy](./cluster-deploy) folder contains the required `yaml` files in the correct order.  Worth a review.

Once running we can port-forward to the new service:
```
kubectl port-forward svc/session-cluster-rest 8081
```

Now we can open the browser to the dedicated [Job Manager UI](http://localhost:8081) and see our cluster.  At this point there should be __NO RUNNING JOBS__.

```
kubectl get pods
```

Should show something like this:

```
NAME                                            READY   STATUS    RESTARTS   AGE
flink-jobjar-repo-deployment-795d996b6d-tklwp   1/1     Running   0          21m
flink-kubernetes-operator-5f8f8c7549-m9wxg      2/2     Running   0          6h39m
session-cluster-78d69d988d-b65q7                1/1     Running   0          21m
```

## Deploy Flink Jobs
With the `Session Cluster` deployed and running, we can deploy the flink jobs:

```
kubectl apply -f flink-jobs.yaml
```

The browser console should now show our two jobs running.

> __Note:__ that the jobs are submitted as K8s CRDs and are listed and managed via `kubectl`.  There is no way to cancel or remove the jobs from within the `Flink Dashboard UI`, it can only be used to view and monitor the jobs.

### Using a custom built flink-kubernetes-operator image
In the odd use case where you need to replace the `flink-operator` image, this is also doable - but the documentation has a few errors and isn't entirely clear on the purpose.  Experimentation revealed it had no affect on the profile of the launched instances and for this we needed a job image as above.

> Note current docs have the 2nd command quite wrong, below is the working version:
```
helm repo add flink-operator-repo https://downloads.apache.org/flink/flink-kubernetes-operator-1.9.0/
helm install --set image.repository=idstudios/flink-kubernetes-operator --set image.tag=latest flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator
```

### Useful Helm Commands
```
helm install -f etc/helm-values.yaml flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator --dry-run --debug > output.txt

helm install flink-kubernetes-operator flink-operator-repo/flink-kubernetes-operator --dry-run --debug > output.txt
```