FROM azul/zulu-openjdk-alpine:11

VOLUME /tmp

COPY ./target/resource-server-*.jar resource-server.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/resource-server.jar"]

EXPOSE 8086