FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar main-service.jar
LABEL authors="vazev"

ENTRYPOINT ["java","-jar","/main-service.jar"]