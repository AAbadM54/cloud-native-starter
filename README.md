## Work in Progress

Stay tuned for more ...

### Setup local Env

Minikube, Istio, Kiali: https://haralduebele.blog/2019/02/22/install-istio-and-kiali-on-ibm-cloud-or-minikube/

```
$ minikube ip
$ kubectl get svc
```

Kiali: https://[minikube-ip]:[kiali-nodeport]/kiali

minikube dashboard

eval $(minikube docker-env)

Jaeger Dashboard:

```
$ kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686:16686
```

http://localhost:16686

Grafana Dashboard

```
$ kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000 &
```

http://localhost:3000/dashboard/db/istio-mesh-dashboard

Prometheus Dashboard

```
$ kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=prometheus -o jsonpath='{.items[0].metadata.name}') 9090:9090 &
```

http://localhost:9090


### Deployment

Deploy and redeploy:

```
$ scripts/deploy-articles-java-jee.sh
```

Delete:

```
$ kubectl delete -f deployment/kubernetes.yaml
$ kubectl delete -f deployment/istio.yaml
```


### Run the Demo

curl [minikubeip]:[articlesnodeport]/health -i

curl 192.168.99.100:31695/health -i

curl [minikubeip]:[articlesnodeport]/articles/v1/create -i -XPOST \
  -H 'Content-Type: application/json' \
  -d '{"title":"Title 1","author":"Author 1","url":"http://heidloff.net"}'

curl 192.168.99.100:31695/articles/v1/create -i -XPOST \
  -H 'Content-Type: application/json' \
  -d '{"title":"Title 1","author":"Author 1","url":"http://heidloff.net"}'

curl [minikubeip]:[articlesnodeport]/articles/v1/getmultiple?amount=10 -i

curl 192.168.99.100:31180/articles/v1/getmultiple?amount=10 -i

http://192.168.99.100:31695/openapi/ui/

http://192.168.99.100:31695/openapi/
