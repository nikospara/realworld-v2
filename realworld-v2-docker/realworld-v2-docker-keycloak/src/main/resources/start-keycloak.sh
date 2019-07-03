#!/bin/bash

if [ -f ./EXPORT/exported ]; then
	exec ./standalone.sh $@
else
	touch ./EXPORT/exported
	exec ./standalone.sh -Dkeycloak.import=EXPORT/realworld-realm.json $@
fi
