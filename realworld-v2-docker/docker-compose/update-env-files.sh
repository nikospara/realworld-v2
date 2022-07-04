#!/bin/bash

BASEDIR=`dirname $0`/../..
cp $BASEDIR/realworld-v2-article-module/realworld-v2-article/target/docker/env-article $BASEDIR/realworld-v2-docker/docker-compose/
cp $BASEDIR/realworld-v2-comments-module/realworld-v2-comments/target/docker/env-comments $BASEDIR/realworld-v2-docker/docker-compose/
cp $BASEDIR/realworld-v2-user-module/realworld-v2-user/target/docker/env-user $BASEDIR/realworld-v2-docker/docker-compose/
