# Food Ordering System

## Quick start
1. Build the entire project with all submodules, run: `./mvnw clean install`
2. Run Kafka and DB using: `docker-compose -p food-ordering-kafka -f docker-compose-common.yml -f docker-compose-kafka.yml up -d`
   1. To initialize Kafka topics, run: `docker-compose -p food-ordering-kafka -f docker-compose-common.yml docker-compose-init.yml up`
   2. You may delete the `init-kafka` container once topics are created
3. Open `http://localhost:9021` to view Kafka Control Center

Other useful commands:
1. To build a specific submodules, run: `./mvnw clean install -pl groupId:artifactId`
2. To build a specific submodules with dependents, run: `./mvnw clean install -pl groupId:artifactId -amd`

## Resources:
1. [Udemy - Microservices: Clean Architecture, DDD, Saga, Outbox & Kafka](https://udemy.com/course/microservices-clean-architecture-ddd-saga-outbox-kafka-kubernetes)
2. [Guide to Working with Multiple Modules 3 & 4](https://maven.apache.org/guides/mini/guide-multiple-modules-4.html)