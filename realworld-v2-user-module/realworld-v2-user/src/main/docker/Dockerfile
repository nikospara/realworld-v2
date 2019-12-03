FROM adoptopenjdk/openjdk11:alpine-jre

COPY target/lib/* /deployments/lib/
COPY target/*-runner.jar /deployments/app.jar

ENV CONFIG_JWK_URL=http://keycloak:8580/auth/realms/realworld/protocol/openid-connect/certs \
	MP_MESSAGING_INCOMING_USERS_STREAM_BOOTSTRAP_SERVERS=kafka:9094

EXPOSE 8180

# run with user 1001 and be prepared for be running in OpenShift too
RUN adduser -G root --no-create-home --disabled-password 1001 \
	&& chown -R 1001 /deployments \
	&& chmod -R "g+rwX" /deployments \
	&& chown -R 1001:root /deployments
USER 1001

ENTRYPOINT [ "java", "-jar", "/deployments/app.jar" ]
