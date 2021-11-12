# Getting Started

![pipeline](../docs/flink-sql-streaming-music-ranking-pipeline.png)

### Build/push docker image
```
./mvnw clean install
 docker build -t tzolov/poc-sql-aggregator:latest .
 docker push tzolov/poc-sql-aggregator:latest
```