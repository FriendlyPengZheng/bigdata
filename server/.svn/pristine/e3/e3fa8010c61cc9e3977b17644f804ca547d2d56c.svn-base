export LANG=en_US.UTF-8
WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

date=$1

$DB_UPLOAD -type 2 -date $date -path /bigdata/tmp/$date
sh calc_basic.sh $date true > $WORKDIR/log/$date/calc_basic.log 2>&1
sh merge.sh $date > $WORKDIR/log/$date/merge.log 2>&1
#基础项
for type in count sum
do
	sh calc_${type}.sh $date > $WORKDIR/log/$date/calc_${type}.log 2>&1 &
done
