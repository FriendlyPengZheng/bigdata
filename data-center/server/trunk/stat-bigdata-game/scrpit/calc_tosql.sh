export LANG=en_US.UTF-8
WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

date=$1

if [[ $date == "" ]]; then
    echo invalid param: date
    exit
fi

pids=""
path="${DAY_DIR}/$date/distr*/part*"
for type in ucount count sum set max
do
	$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/$type/part* > $WORKDIR/log/$date/${type}sql.log 2>&1 &
	path="$path:${DAY_DIR}/$date/$type/part*"
	pids="$pids $!"
done
for p in $pids
do
	wait $p
done

sh calc_tosql_ads.sh $date > $WORKDIR/log/$date/calc_tosql_ads.log 2>&1 &
pids=$!
wait $pid

sh calc_basic_check.sh $date > $WORKDIR/log/$date/calc_basic_check.log 2>&1
