FROM registry.access.redhat.com/ubi9/openjdk-21:1.21-3
USER root
RUN groupadd -r myappgroup && useradd -r -g myappgroup myappuser
RUN microdnf install -y maven && microdnf clean all

WORKDIR /app

COPY build.gradle settings.gradle main.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

COPY api-first ./api-first
COPY app-service ./app-service
COPY application ./application
COPY domain ./domain
COPY infrastructure ./infrastructure

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

RUN chown myappuser:myappgroup $(find . -name "*.jar")

USER myappuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app-service/build/libs/app-service.jar"]
