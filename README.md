# Food Ordering System

## Quick start
1. Build the entire project with all submodules, run: `./mvnw clean install`
2. Create a `volumes` directory under `infrastructure/docker-compose` 
3. Run Kafka and DB using: `docker-compose -p food-ordering-kafka -f docker-compose-common.yml -f docker-compose-kafka.yml up -d`
   1. To initialize Kafka topics, run: `docker-compose -p food-ordering-kafka -f docker-compose-common.yml docker-compose-init.yml up`
   2. You may delete the `init-kafka` container once topics are created
4. Open `http://localhost:9021` to view Kafka Control Center

Other useful commands:
1. To build a specific submodules, run: `./mvnw clean install -pl groupId:artifactId`
2. To build a specific submodules with dependents, run: `./mvnw clean install -pl groupId:artifactId -amd`

## Resources:
1. [Udemy - Microservices: Clean Architecture, DDD, Saga, Outbox & Kafka](https://udemy.com/course/microservices-clean-architecture-ddd-saga-outbox-kafka-kubernetes)
2. [Guide to Working with Multiple Modules 3 & 4](https://maven.apache.org/guides/mini/guide-multiple-modules-4.html)
3. [Sample Kafka IT tests](https://www.youtube.com/watch?v=XaEdtErIgjQ)

## TODOs:
1. Add unit, IT tests and E2E tests
2. Add a Delivery service
3. Email customer when order is ready to pick-up
4. Performance test
5. Integrate with HashiCorp Vault via [Spring Vault](https://spring.io/projects/spring-vault)
   1. [Setup HashiCorp Vault in docker](https://gist.github.com/Mishco/b47b341f852c5934cf736870f0b5da81)