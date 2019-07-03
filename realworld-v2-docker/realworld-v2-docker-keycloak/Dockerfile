FROM adoptopenjdk/openjdk11:alpine-jre

ARG project_version

ADD target/keycloak-6.0.1.tar.gz /opt

COPY src/main/resources/create-realworld-provider.jboss-cli /opt/keycloak-6.0.1/bin
COPY src/main/resources/no-file-log.jboss-cli /opt/keycloak-6.0.1/bin
COPY src/main/resources/EXPORT /opt/keycloak-6.0.1/bin/EXPORT
COPY src/main/resources/start-keycloak.sh /opt/keycloak-6.0.1/bin
COPY target/dependency/realworld-v2-keycloak-event-listener-${project_version}.jar /opt/keycloak-6.0.1/standalone/deployments/
COPY target/dependency/realworld-v2-keycloak-theme-${project_version}.jar /opt/keycloak-6.0.1/standalone/deployments/

WORKDIR /opt/keycloak-6.0.1/bin

RUN apk add --no-cache bash curl \
	&& ./jboss-cli.sh --file=create-realworld-provider.jboss-cli \
	&& rm -f create-realworld-provider.jboss-cli \
	&& ./jboss-cli.sh --file=no-file-log.jboss-cli \
	&& rm -f no-file-log.jboss-cli \
	&& rm -rf ../standalone/configuration/standalone_xml_history \
	&& chmod a+x /opt/keycloak-6.0.1/bin/start-keycloak.sh \
	&& ./add-user-keycloak.sh -r master -u admin -p admin

EXPOSE 8580 10499

CMD ["./start-keycloak.sh", "-Djboss.socket.binding.port-offset=500", "-b=0.0.0.0"]
