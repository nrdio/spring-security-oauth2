FROM azul/zulu-openjdk-alpine:11

VOLUME /tmp

COPY ./target/api-gateway-*.jar api-gateway.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/api-gateway.jar"]

EXPOSE 8888