# Overview

Use docker-compose to install `ZooKeeper`, `Kafka Broker`, `Schema Registry` and `RabbitMQ`.

## Install demo infrastructure
```shell
docker-compose -f ./docker-compose/docker-compose.yaml up
```

You can list the deployed containers:
```shell
docker ps 

CONTAINER ID   IMAGE                                      COMMAND                  PORTS                                                                                                         NAMES
53bc4d9d5101   confluentinc/cp-schema-registry:5.2.5-10   "/etc/confluent/dock…"   0.0.0.0:8081->8081/tcp                                                                                        schema-registry
b590d50ad884   wurstmeister/kafka:2.12-2.5.0              "start-kafka.sh"         0.0.0.0:9094->9094/tcp                                                                                        kafka
bf7012cabd96   rabbitmq:3-management                      "docker-entrypoint.s…"   4369/tcp, 5671/tcp, 0.0.0.0:5672->5672/tcp, 15671/tcp, 15691-15692/tcp, 25672/tcp, 0.0.0.0:15672->15672/tcp   rabbitmq
2c9054ca6a21   wurstmeister/zookeeper                     "/bin/sh -c '/usr/sb…"   22/tcp, 2888/tcp, 3888/tcp, 0.0.0.0:2181->2181/tcp                                                            zookeeper
```
By default deployed services are accessible at:

- `ZooKeeper` - `localhost:2181`
- `Kafka Broker` - `localhost:9094`
- `Schema Registry` - `http://localhost:8081`
- `RabbitMQ` - `localhost:5671`

## Run the demo applications

* Play songs generator
From your IDE run [PlaySongsGeneratorApplication.java](../play-songs-generator/src/main/java/net/tzolov/poc/playsongs/PlaySongsGeneratorApplication.java)
* SQL Aggregator
From your IDE run [SqlAggregatorApplication.java](../sql-aggregator/src/main/java/net/tzolov/poc/sqlaggregator/SqlAggregatorApplication.java)
* User-Defined Function (e.g. Uppercase gRPC)
From your IDE run [UppercaseGrpcApplication.java](../uppercase-grpc/src/main/java/net/tzolov/poc/uppercasegrpc/UppercaseGrpcApplication.java)
* Multibinder
  From your IDE run [MultibinderGrpcApplication.java](../multibinder-grpc/src/main/java/net/tzolov/poc/multibindergrpc/MultibinderGrpcApplication.java)

For every boot app you can build and run the jar instead.

## Clean the resources

* stop all boot apps
* run `docker-compose -f ./docker-compose/docker-compose.yaml down`
