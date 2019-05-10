FROM azul/zulu-openjdk-alpine:11

VOLUME /tmp

COPY ./target/authorization-server-*.jar authorization-server.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/authorization-server.jar"]

EXPOSE 8085