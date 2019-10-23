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
HADOOP='/opt/taomee/hadoop/hadoop/bin/hadoop'
while [[ $date -lt $2 ]]
do
	echo $date
	$HADOOP fs -cat /bigdata/input/$date/*/* | grep '_stid_=_acpay_' > acpay-m-merge
	$HADOOP fs -rm -skipTrash /bigdata/output/day/$date/basic/acpay-m-me*
	$HADOOP fs -copyFromLocal acpay-m-merge /bigdata/output/day/$date/basic/acpay-m-merge
	date=`date -d "$date +1 day" +%Y%m%d`
done
