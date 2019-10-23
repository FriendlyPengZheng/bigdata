date=$1
end=$2
if [[ $date == "" ]]; then
	echo $0 start end
	exit
fi
if [[ $end == "" ]]; then
	echo $0 start end
	exit
fi
while [[ $date -lt $2 ]]
do
	echo $date
	sh calc_activekeep14.sh $date > log/ads/calc_activekeep14$date.log 2>&1
	date=`date -d "$date +1 day" +%Y%m%d`
done

#for date in 20131130 20131231 20140131 20140228 20140319
#do
#	sh calc_pay_month.sh $date > $date.log 2>&1 &
#done

