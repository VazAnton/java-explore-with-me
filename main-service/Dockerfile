FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar main-service.jar
LABEL authors="vazev"
#main-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/main-service.jar"]