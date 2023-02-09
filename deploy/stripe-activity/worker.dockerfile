FROM openjdk:11.0.14-jre

RUN mkdir /app
COPY stripe-activity-fat-jar.jar /app/stripe-worker.jar
