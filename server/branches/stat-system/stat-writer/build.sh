#!/bin/bash

rebuild_libant=0
rebuild_writer=0

for arg in $@
do
	if [ "$arg" = "lib" ] ; then
		rebuild_libant=1
	elif [ "$arg" = "a" ] ; then
		rebuild_writer=1
	fi
done

if [ $rebuild_libant -eq 1 ] ; then
	./libant.sh
fi

cd ./src
if [ $rebuild_writer -eq 1 ] ; then
	make clean all
else
	make all
fi

cd ..

