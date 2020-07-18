#!/bin/bash
# This file is a mix of these:
# - https://github.com/31z4/zookeeper-docker/blob/master/3.5.7/docker-entrypoint.sh - This is the base
# - https://github.com/kubernetes-retired/contrib/blob/master/statefulsets/zookeeper/zkGenConfig.sh
# - https://kubernetes.io/docs/tasks/run-application/run-replicated-stateful-application/

set -e

if [ ! -d $ZOO_DATA_DIR ]; then
	mkdir -p $ZOO_DATA_DIR
fi
if [ ! -d $ZOO_DATA_LOG_DIR ]; then
	mkdir -p $ZOO_DATA_LOG_DIR
fi

# Allow the container to be started with `--user`
if [[ "$1" = 'zkServer.sh' && "$(id -u)" = '0' ]]; then
	chown -R zookeeper "$ZOO_DATA_DIR" "$ZOO_DATA_LOG_DIR" "$ZOO_LOG_DIR" "$ZOO_CONF_DIR"
	exec gosu zookeeper "$0" "$@"
fi

# Generate the config only if it doesn't exist
if [[ ! -f "$ZOO_CONF_DIR/zoo.cfg" ]]; then
	if [ -z $ZOO_REPLICAS ]; then
		ZOO_REPLICAS=1
	fi

	HOST=`hostname -s`
	DOMAIN=`hostname -d`
	if [[ $HOST =~ (.*)-([0-9]+)$ ]]; then
		NAME=${BASH_REMATCH[1]}
		ORD=${BASH_REMATCH[2]}
		ZOO_MY_ID=$((${BASH_REMATCH[1]}+1))
	elif [ $ZOO_REPLICAS -gt 1 ]; then
		echo "Failed to extract ordinal from hostname $HOST"
		exit 1
	else
		ZOO_MY_ID=1
	fi

	CONFIG="$ZOO_CONF_DIR/zoo.cfg"
	{
		echo "dataDir=$ZOO_DATA_DIR"
		echo "dataLogDir=$ZOO_DATA_LOG_DIR"

		echo "clientPort=2181"

		echo "tickTime=$ZOO_TICK_TIME"
		echo "initLimit=$ZOO_INIT_LIMIT"
		echo "syncLimit=$ZOO_SYNC_LIMIT"

		echo "autopurge.snapRetainCount=$ZOO_AUTOPURGE_SNAPRETAINCOUNT"
		echo "autopurge.purgeInterval=$ZOO_AUTOPURGE_PURGEINTERVAL"
		echo "maxClientCnxns=$ZOO_MAX_CLIENT_CNXNS"
		echo "standaloneEnabled=$ZOO_STANDALONE_ENABLED"
		echo "admin.enableServer=$ZOO_ADMINSERVER_ENABLED"
	} >> "$CONFIG"

#	if [[ -z $ZOO_SERVERS ]]; then
#		ZOO_SERVERS="server.1=localhost:2888:3888;2181"
#	fi
	if [ $ZOO_REPLICAS -gt 1 ]; then
		for (( i=1; i<=$ZOO_REPLICAS; i++ )); do
			echo "server.$i=$NAME-$((i-1)).$DOMAIN:${ZK_SERVER_PORT:-2888}:${ZK_ELECTION_PORT:-3888}" >> "$CONFIG"
		done
	else
		echo "server.1=localhost:${ZK_SERVER_PORT:-2888}:${ZK_ELECTION_PORT:-3888}" >> "$CONFIG"
	fi

#	for server in $ZOO_SERVERS; do
#		echo "$server" >> "$CONFIG"
#	done

	if [[ -n $ZOO_4LW_COMMANDS_WHITELIST ]]; then
		echo "4lw.commands.whitelist=$ZOO_4LW_COMMANDS_WHITELIST" >> "$CONFIG"
	fi
fi

# Write myid only if it doesn't exist
if [[ ! -f "$ZOO_DATA_DIR/myid" ]]; then
	echo "${ZOO_MY_ID:-1}" > "$ZOO_DATA_DIR/myid"
fi

exec "$@"
