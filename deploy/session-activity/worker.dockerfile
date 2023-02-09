FROM openjdk:11.0.14-jre

RUN mkdir /app
COPY session-activity-fat-jar.jar /app/session-worker.jar
