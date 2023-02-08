FROM openjdk:11.0.14-jre

RUN mkdir /app
COPY temporal-workers-fat-jar.jar /app/workers.jar
