FROM openjdk:8-jdk-alpine
COPY ./target/user:1.0.0
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","/user:1.0.0"]


