Kubernetes resources and instructions
=====================================

1. Enable the `storage` addon:

    ```shell script
    microk8s.enable storage
    ```

    (OUTDATED - ENABLE THE storage ADDON) Create a persistent volume having the same `spec.storageClassName` as the _postgres-pv-claim_ in the deployment, e.g. as follows:

    - Put the following in the file `pv-local.yaml` - source https://kubernetes.io/docs/concepts/storage/volumes/#local:

        ```yaml
        apiVersion: v1
        kind: PersistentVolume
        metadata:
          name: local-pv
        spec:
          capacity:
            storage: 1Gi
          volumeMode: Filesystem
          accessModes:
          - ReadWriteOnce
          persistentVolumeReclaimPolicy: Delete
          storageClassName: local-storage
          local:
            path: /mnt/kube-local-pv
          nodeAffinity:
            required:
              nodeSelectorTerms:
              - matchExpressions:
                - key: kubernetes.io/hostname
                  operator: In
                  values:
                  - udocker
        ```

    - Run:

        ```shell script
        kubectl apply -f pv-local.yaml
        ```

2. Create the file `deployment-postgres.yaml` (contents besides this README)

3. Create the file `kustomization.yaml`:

    ```yaml
    resources:
      - deployment-postgres.yaml
      - statefulset-zookeeper.yaml
      - statefulset-kafka.yaml
    ```

4. Run:

    ```shell script
    kubectl apply -k ./
    ```

Database
--------

The service named _rwlv2-postgres_ is exposed on port 32543 on the Kubernetes node.

Zookeeper/Kafka
---------------

The service name _rwlv2-kafka-ext_ is exposed on port 30094 on the Kubernetes node.
Inside the cluster, Zookeeper is exposed under _rwlv2-zk-cs_ and Kafka as _rwlv2-kafka-int_.


Destroying
----------

```shell script
kubectl delete -k ./
kubectl delete -f pv-local.yaml
```


Using a local Docker repo
-------------------------

(From https://microk8s.io/docs/registry-images)

Explanation: Kubernetes needs the local tag (reference?). So you need to tag an already built image first.

```shell script
docker tag myimage myimage:local
docker save myimage:local > myimage.tar
microk8s.ctr image import myimage.tar
```

Verify with `microk8s.ctr images ls`.
