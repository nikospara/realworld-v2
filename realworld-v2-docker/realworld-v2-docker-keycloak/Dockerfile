FROM adoptopenjdk/openjdk11:alpine-jre

ARG project_version
ARG keycloak_version

ADD target/keycloak-${keycloak_version}.tar.gz /opt

COPY src/main/resources/EXPORT /opt/keycloak-${keycloak_version}/data/import
COPY src/main/resources/start-keycloak.sh /opt/keycloak-${keycloak_version}/bin/
COPY target/dependency/realworld-v2-keycloak-event-listener-${project_version}.jar /opt/keycloak-${keycloak_version}/providers/
COPY target/dependency/realworld-v2-keycloak-theme-${project_version}.jar /opt/keycloak-${keycloak_version}/providers/

WORKDIR /opt/keycloak-${keycloak_version}/bin
ENV KEYCLOAK_VERSION ${keycloak_version}
ENV KEYCLOAK_ADMIN admin
ENV KEYCLOAK_ADMIN_PASSWORD admin

RUN apk add --no-cache bash \
	&& chmod a+x /opt/keycloak-${KEYCLOAK_VERSION}/bin/start-keycloak.sh \
	&& ./kc.sh build --db postgres --spi-events-listener-realworld-topic-name users

EXPOSE 8580

CMD ["./start-keycloak.sh", "--db", "postgres", "--db-url-host", "${DB_ADDR:-postgres}", "--db-url-database", "${DB_DATABASE:-rwlv2}", "--db-username", "${DB_USER:-keycloak}", "--db-password", "${DB_PASSWORD:-keycloak}", "--db-schema", "${DB_SCHEMA:-keycloak}", "--spi-events-listener-realworld-bootstrap-servers", "${KAFKA_BOOTSTRAP_SERVERS:-kafka:9092}",  "--spi-events-listener-realworld-topic-name", "users",  "--http-port", "8580"]
