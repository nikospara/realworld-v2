FROM adoptopenjdk/openjdk11:alpine-jre AS dbfiles

ARG dbusername
ARG dbpassword
ARG dburl
ARG dbdriver

ADD target/liquibase.tar.gz /opt/liquibase
COPY target/dbdriver.jar /opt/liquibase/lib
COPY target/migrations.jar /opt/liquibase/lib

ENV DB_USERNAME=${dbusername} DB_PASSWORD=${dbpassword} DB_URL=${dburl} DB_DRIVER=${dbdriver}

WORKDIR /opt/liquibase
RUN apk add --no-cache bash \
	&& ./liquibase --changeLogFile=db.changelog.xml --username=$DB_USERNAME --password=$DB_PASSWORD --url=$DB_URL --driver=$DB_DRIVER update

FROM adoptopenjdk/openjdk11:alpine-jre

COPY target/lib/* /deployments/lib/
COPY target/*-runner.jar /deployments/app.jar
COPY --from=dbfiles /deployments/* /deployments/

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
