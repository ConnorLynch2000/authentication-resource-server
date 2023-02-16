FROM openjdk:17-alpine
RUN ["apt-get", "update"]
RUN ["apt-get", "install", "-y", "curl"]
ADD target/authentication*.jar /opt/authentication.jar
ENTRYPOINT ["java", "-jar", "/opt/authentication.jar"]
