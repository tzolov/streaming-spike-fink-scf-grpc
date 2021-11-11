# Getting Started

### Kubernetes (MiniKube) Deployment

##### 1. Start a Minikube cluster

```
minikube start --memory 8192
```

#### 2. Deploy Kafka, Schema Registry and RabbitMQ

From within the root folder run:
```
kubectl apply -f ./k8s-templates/kafka
kubectl apply -f ./k8s-templates/rabbitmq
```
then port-forward the Kafka, Registry and Rabbit (each in a separate terminal) to make them accessible from the Host:
```
kubectl port-forward svc/s-registry 8081:8081
kubectl port-forward svc/kafka 9094:9094
kubectl port-forward svc/rabbitmq 5672:5672
```

#### 3. Start play songs data generator

From the root folder build the `play-songs-generator` project:
```
./mvnw clean install -pl play-songs-generator
```
and start it like this:
```
java -jar ./play-songs-generator/target/play-songs-generator-0.0.1-SNAPSHOT.jar
```

#### 4. Deploy the Music stream processing app

```
kubectl apply -f ./k8s-templates/app
```


#### 5. Check the topics

```
kubectl port-forward svc/multibinder-grpc 8089:8089
```9

```
/opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

/opt/kafka/bin/kafka-console-consumer.sh --topic song-feed --from-beginning --bootstrap-server localhost:9092
```


```
 docker build -t tzolov/poc-sql-aggregator:latest .
 docker push tzolov/poc-sql-aggregator:latest  
```

#### 6. Delete cluster 

```
kubectl delete cm,pod,deployment,rc,service -l type="streaming-spike"
```
