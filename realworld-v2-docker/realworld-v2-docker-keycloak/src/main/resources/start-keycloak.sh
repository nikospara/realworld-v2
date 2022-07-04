#!/bin/bash

if [ -f ./EXPORT/exported ]; then
	exec ./kc.sh --verbose start-dev $@
else
	touch ./EXPORT/exported
	exec ./kc.sh --verbose start-dev --import-realm $@
fi
