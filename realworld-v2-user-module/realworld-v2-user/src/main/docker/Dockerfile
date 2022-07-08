FROM registry.access.redhat.com/ubi8/openjdk-17:1.11

COPY --chown=185 target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/app/ /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus/ /deployments/quarkus/

ENV CONFIG_JWK_URL=http://keycloak:8580/auth/realms/realworld/protocol/openid-connect/certs \
	MP_MESSAGING_INCOMING_USERS_STREAM_BOOTSTRAP_SERVERS=kafka:9094

EXPOSE 8180
USER 185

ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar", "-Dquarkus.http.host=0.0.0.0", "-Djava.util.logging.manager=org.jboss.logmanager.LogManager" ]
