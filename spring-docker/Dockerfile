FROM openjdk:8-jre
EXPOSE 8080
ARG JAR_FILE
ADD *.jar spring-docker.jar
ENTRYPOINT ["java", "-jar","/spring-docker.jar"]
