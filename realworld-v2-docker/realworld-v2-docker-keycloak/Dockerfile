FROM registry.access.redhat.com/ubi8/openjdk-17:1.11

ARG project_version
ARG keycloak_version

ADD --chown=185 target/keycloak-${keycloak_version}.tar.gz /opt

# XXX For some reason ADD --chown is not working
USER 0
RUN (cd /opt; chown -R 185:185 keycloak-${keycloak_version})

COPY --chown=185 src/main/resources/EXPORT /opt/keycloak-${keycloak_version}/data/import
COPY --chown=185 src/main/resources/start-keycloak.sh /opt/keycloak-${keycloak_version}/bin/
COPY --chown=185 target/dependency/realworld-v2-keycloak-event-listener-${project_version}.jar /opt/keycloak-${keycloak_version}/providers/
COPY --chown=185 target/dependency/realworld-v2-keycloak-theme-${project_version}.jar /opt/keycloak-${keycloak_version}/providers/

WORKDIR /opt/keycloak-${keycloak_version}/bin
ENV KEYCLOAK_VERSION ${keycloak_version}
ENV KEYCLOAK_ADMIN admin
ENV KEYCLOAK_ADMIN_PASSWORD admin
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

USER 185

RUN chmod a+x /opt/keycloak-${KEYCLOAK_VERSION}/bin/start-keycloak.sh \
	&& ./kc.sh build --db postgres --spi-events-listener-realworld-topic-name users

EXPOSE 8580

CMD ["./start-keycloak.sh", "--db", "postgres", "--db-url-host", "${DB_ADDR:-postgres}", "--db-url-database", "${DB_DATABASE:-rwlv2}", "--db-username", "${DB_USER:-keycloak}", "--db-password", "${DB_PASSWORD:-keycloak}", "--db-schema", "${DB_SCHEMA:-keycloak}", "--spi-events-listener-realworld-bootstrap-servers", "${KAFKA_BOOTSTRAP_SERVERS:-kafka:9092}",  "--spi-events-listener-realworld-topic-name", "users",  "--http-port", "8580"]
