FROM amazoncorretto:17-alpine3.19-jdk
EXPOSE 8080
COPY ./target/user-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "user-0.0.1-SNAPSHOT.jar"]

