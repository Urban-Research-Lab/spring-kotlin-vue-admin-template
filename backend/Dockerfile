FROM adoptopenjdk/openjdk11:alpine

ENV JAVA_OPTS=""

ARG JAR_FILE=build/libs/\*.jar
ARG CONFIG_FILE=build/resources/main/docker.application.yml

COPY ${JAR_FILE} app.jar
COPY ${CONFIG_FILE} /etc/adminservice/application.yml

ENV TZ=Europe/Moscow
RUN apk update && apk add tzdata && cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && apk del tzdata

EXPOSE 8080

ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar