#!/bin/bash


date=$1
end=$2

HADOOP_PATH=/ads/newads/day
LOCAL_PATH=/opt/taomee/hadoop/kendy/data/ct/lgac

tad="{empty_or_0} unknown 4399.com 7k7k.com innermedia.seer\. innermedia.taomee\. innermedia.jl\. innermedia.seer2\. innermedia.gf\. innermedia.youxiye\. innermedia.ct\."
while [[ $date -le $end ]]
do

	tt=$date
	for i in $tad
	do
	    echo  $tt $i

	   hadoop fs -text $HADOOP_PATH/$tt/active_gamemimitad/part* |awk -F "," '{if($1==19) print $2}' |grep $i |grep -v 'account_set_unknown'|awk -F " " '{print $1}' |sort -u > $LOCAL_PATH/$i-$tt
	done

        date=`date -d "$date +1 day" +%Y%m%d`
done
