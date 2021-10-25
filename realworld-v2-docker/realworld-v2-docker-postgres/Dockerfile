FROM adoptopenjdk/openjdk11:alpine-jre AS dbfiles

ADD target/liquibase.tar.gz /opt/liquibase/
COPY target/dbdriver*.jar /opt/liquibase/lib/
COPY target/migrations*.jar /opt/liquibase/

################################################################
# See this: https://dzone.com/articles/using-liquibase-without #
################################################################
WORKDIR /opt/liquibase
RUN apk add --no-cache bash \
	&& ./liquibase --classpath=migrations-user.jar --changeLogFile=db.changelog.xml --url=offline:postgresql --driver=org.postgresql.Driver --outputDefaultSchema=true --defaultSchemaName=rwlv2_user updateSQL > 1-user.sql \
	&& ./liquibase --classpath=migrations-comments.jar --changeLogFile=db.changelog.xml --url=offline:postgresql --driver=org.postgresql.Driver --outputDefaultSchema=true --defaultSchemaName=rwlv2_comments updateSQL > 3-comments.sql \
	&& ./liquibase --classpath=migrations-article.jar --changeLogFile=db.changelog.xml --url=offline:postgresql --driver=org.postgresql.Driver --outputDefaultSchema=true --defaultSchemaName=rwlv2_article updateSQL > 2-article.sql

FROM postgres:14-alpine

COPY src/main/resources/0-create-dbs.sql /docker-entrypoint-initdb.d/
COPY --from=dbfiles /opt/liquibase/*.sql /docker-entrypoint-initdb.d/

ENV POSTGRES_DB rwlv2
