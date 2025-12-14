# 1. Build Stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. Run Stage
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/service-management-tool-1.0.0.jar app.jar
# Limit memory so it fits in the free tier
ENTRYPOINT ["java","-Xmx350m","-jar","app.jar"]