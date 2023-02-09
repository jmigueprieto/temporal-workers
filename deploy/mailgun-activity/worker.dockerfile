FROM openjdk:11.0.14-jre

RUN mkdir /app
COPY mailgun-activity-fat-jar.jar /app/mailgun-worker.jar
