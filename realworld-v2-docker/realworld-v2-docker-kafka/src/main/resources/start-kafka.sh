#!/bin/bash

# Based on https://github.com/wurstmeister/kafka-docker/blob/master/start-kafka.sh

KAFKA_CONFIG_FILE=$KAFKA_HOME/config/server-realworld-v2.properties
LOG4J_CONFIG_FILE=$KAFKA_HOME/config/log4j.properties
HOSTNAME_VALUE=`curl -s --unix-socket /var/run/docker.sock http:/v1.39/info | jq -r .Name`

function updateConfig() {
	key=$1
	value=$2
	file=$3

	echo "[Configuring] '$key' in '$file'"

	if grep -E -q "^#?$key=" "$file"; then
		sed -r -i "s@^#?$key=.*@$key=$value@g" "$file"
	else
		echo "$key=$value" >> "$file"
	fi
}

if [ ! -f $KAFKA_CONFIG_FILE ]; then
	cp $KAFKA_HOME/config/server.properties $KAFKA_CONFIG_FILE
	echo "" >> $KAFKA_CONFIG_FILE
fi

updateConfig listeners                       INSIDE://:9092,OUTSIDE://:9094                $KAFKA_CONFIG_FILE
updateConfig advertised.listeners            INSIDE://:9092,OUTSIDE://$HOSTNAME_VALUE:9094 $KAFKA_CONFIG_FILE
updateConfig listener.security.protocol.map  INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT            $KAFKA_CONFIG_FILE
updateConfig log.dirs                        /var/kafka-logs                               $KAFKA_CONFIG_FILE
updateConfig log.retention.hours             -1                                            $KAFKA_CONFIG_FILE
updateConfig log.retention.check.interval.ms 30000000                                      $KAFKA_CONFIG_FILE
updateConfig zookeeper.connect               zookeeper:2181                                $KAFKA_CONFIG_FILE
updateConfig inter.broker.listener.name      INSIDE                                        $KAFKA_CONFIG_FILE

updateConfig log4j.rootLogger                           "INFO, stdout"  $LOG4J_CONFIG_FILE
updateConfig log4j.logger.state.change.logger           TRACE           $LOG4J_CONFIG_FILE
updateConfig log4j.logger.kafka.request.logger          WARN            $LOG4J_CONFIG_FILE
updateConfig log4j.logger.kafka.network.RequestChannel$ WARN            $LOG4J_CONFIG_FILE
updateConfig log4j.logger.kafka.log.LogCleaner          WARN            $LOG4J_CONFIG_FILE
updateConfig log4j.logger.kafka.controller              TRACE           $LOG4J_CONFIG_FILE
updateConfig log4j.logger.kafka.authorizer.logger       INFO            $LOG4J_CONFIG_FILE

exec "$KAFKA_HOME/bin/kafka-server-start.sh" "$KAFKA_CONFIG_FILE"
