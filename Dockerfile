
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /subscriptionsdemo

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM openjdk:17-jdk-alpine

WORKDIR /subscriptionsdemo

COPY --from=build /subscriptionsdemo/target/subscriptions-demo-0.0.1-SNAPSHOT.jar subscriptionsdemo.jar

ENTRYPOINT ["java", "-jar", "subscriptionsdemo.jar"]