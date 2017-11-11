#!/usr/bin/env bash


#https://stackoverflow.com/questions/2172352/in-bash-how-can-i-check-if-a-string-begins-with-some-value
#https://unix.stackexchange.com/questions/225943/except-the-1st-argument
#https://stackoverflow.com/questions/255898/how-to-iterate-over-arguments-in-a-bash-script

first_arg=$1
shift # short for shift 1
for i
do echo "$i"
done

second_arg=$2

echo === \"\$@\": all or whats remaining after shift
echo "$@"
echo ===

if [[ first_arg == z* ]] || [[ second_arg == sec* ]];           #starts with
 then echo $first_arg
fi

