FROM openjdk:17-alpine
ADD target/authentication*.jar /opt/authentication.jar
ENTRYPOINT ["java", "-jar", "/opt/authentication.jar"]
