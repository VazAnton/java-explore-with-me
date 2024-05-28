FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar service.jar
LABEL authors="vazev"

ENTRYPOINT ["java","-jar","/service.jar"]