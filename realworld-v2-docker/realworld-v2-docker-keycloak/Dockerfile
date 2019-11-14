FROM adoptopenjdk/openjdk11:alpine-jre

ARG project_version
ARG keycloak_version

ADD target/keycloak-${keycloak_version}.tar.gz /opt

COPY src/main/resources/create-realworld-provider.jboss-cli /opt/keycloak-${keycloak_version}/bin
COPY src/main/resources/no-file-log.jboss-cli /opt/keycloak-${keycloak_version}/bin
COPY src/main/resources/EXPORT /opt/keycloak-${keycloak_version}/bin/EXPORT
COPY src/main/resources/start-keycloak.sh /opt/keycloak-${keycloak_version}/bin
COPY target/dependency/realworld-v2-keycloak-event-listener-${project_version}.jar /opt/keycloak-${keycloak_version}/standalone/deployments/
COPY target/dependency/realworld-v2-keycloak-theme-${project_version}.jar /opt/keycloak-${keycloak_version}/standalone/deployments/

WORKDIR /opt/keycloak-${keycloak_version}/bin
ENV KEYCLOAK_VERSION ${keycloak_version}

RUN apk add --no-cache bash curl \
	&& ./jboss-cli.sh --file=create-realworld-provider.jboss-cli \
	&& rm -f create-realworld-provider.jboss-cli \
	&& ./jboss-cli.sh --file=no-file-log.jboss-cli \
	&& rm -f no-file-log.jboss-cli \
	&& rm -rf ../standalone/configuration/standalone_xml_history \
	&& chmod a+x /opt/keycloak-${KEYCLOAK_VERSION}/bin/start-keycloak.sh \
	&& ./add-user-keycloak.sh -r master -u admin -p admin

EXPOSE 8580 10499

CMD ["./start-keycloak.sh", "-Djboss.socket.binding.port-offset=500", "-b=0.0.0.0"]
