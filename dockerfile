FROM openjdk:8-jdk-alpine
#ENV MONGO_INITDB_ROOT_USERNAME=rootuser \
#    MONGO_INITDB_ROOT_PASSWORD=rootpass
EXPOSE 8080
COPY ./target/user-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "user-0.0.1-SNAPSHOT.jar"]

