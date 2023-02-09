FROM openjdk:11.0.14-jre

RUN mkdir /app
COPY checkout-workflow-fat-jar.jar /app/workflow-worker.jar
