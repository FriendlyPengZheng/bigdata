#!/bin/bash


date=$1
end=$2

LOCAL_PATH_REG=/opt/taomee/hadoop/kendy/data/ct/reg
LOCAL_PATH_LGAC=/opt/taomee/hadoop/kendy/data/ct/lgac
LOCAL_PATH_JOIN=/opt/taomee/hadoop/kendy/data/ct/join

tad="{empty_or_0} unknown 4399.com 7k7k.com innermedia.seer\. innermedia.taomee\. innermedia.jl\. innermedia.seer2\. innermedia.gf\. innermedia.youxiye\. innermedia.ct\."
while [[ $date -le $end ]]
do

	tt1=$date
	tt2=`date -d "$date +1 day" +%Y%m%d`
	for i in $tad
	do
	    echo  $tt1 $i
	    echo $i-$tt1 $i-$tt2
	    sort $LOCAL_PATH_REG/"$i-$tt1" $LOCAL_PATH_LGAC/"$i-$tt2" |uniq -c |grep ' 2 ' |awk -F " " '{print $2}' > $LOCAL_PATH_JOIN/$i-$tt1-1

	done

        date=`date -d "$date +1 day" +%Y%m%d`
done
