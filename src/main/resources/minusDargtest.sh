#!/usr/bin/env bash

#/usr/bin/env bash /i/p/ralfoenning/client/src/main/resources/minusDargtest.sh -Dlines=5 first second third
#===>
# first second third
#minusd=-Dlines=5

#/usr/bin/env bash /i/p/ralfoenning/client/src/main/resources/minusDargtest.sh first second third -Dlines=5
#===>
#first second third -Dlines=5
#minusd=

#
#shift if first arg starts with -D

if [[ $1 == -D* ]];
    then minusd=$1; shift
fi


echo "$@"

echo "minusd="$minusd


