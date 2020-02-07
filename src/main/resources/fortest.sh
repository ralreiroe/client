#!/usr/bin/env bash


hosts="a,b,c"
for host in $(sed 's/,/ /g' <<< $hosts)
    do
        echo $host
    done