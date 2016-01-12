#!/bin/bash

sed -i '1d' numbers;

epoch=`head -1 numbers | awk '{print $2}'`;

awk '{print $1, $2-"'"$epoch"'", $3}' < numbers | awk '{print $1, strftime("%H:%M:%S" , $2, 1), $3}' > numbers2;

sed 's/\([0-9][0-9]*\) \([0-9][0-9]*\)/\1 01 2010 \2/' < numbers2 > numbers3;

awk '{print $1, "\""$2, $3, $4"\"", $5}' < numbers3 > numbers_asi;

rm -rf numbers2 numbers3;

