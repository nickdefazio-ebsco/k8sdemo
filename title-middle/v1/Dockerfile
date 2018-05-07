FROM openjdk:8u111-jdk-alpine

VOLUME /tmp

RUN apk --no-cache add curl
RUN apk --no-cache add jq

COPY ./build/libs/*.jar app.jar
ADD ./start.sh start.sh
RUN chmod 755 start.sh

ENTRYPOINT ["./start.sh"]