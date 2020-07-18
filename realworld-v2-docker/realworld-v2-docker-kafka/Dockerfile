FROM adoptopenjdk/openjdk11:alpine-jre

ARG kafka_version=2.3.1
ARG scala_version=2.12

ADD target/kafka_${scala_version}-${kafka_version}.tgz /opt

ENV KAFKA_HOME=/opt/kafka_${scala_version}-${kafka_version}
ENV PATH=${PATH}:${KAFKA_HOME}/bin
ENV KAFKA_GC_LOG_OPTS=-Dnogclog

COPY src/main/resources/start-kafka.sh ${KAFKA_HOME}/bin/start-kafka.sh

RUN apk add --no-cache bash \
	&& chmod a+x ${KAFKA_HOME}/bin/start-kafka.sh \
	&& mkdir /var/kafka-logs

EXPOSE 9094

CMD ["start-kafka.sh"]
