#!/bin/bash

rebuild=0

for arg in $@
do
	if [ "$arg" = "a" ] ; then
		rebuild=1
	fi
done

cd ./src
if [ $rebuild -eq 1 ] ; then
	make clean all
else
	make
fi

cd ..

