FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/edakafka-0.0.1-SNAPSHOT.jar /app/edakafka.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/edakafka.jar"]
