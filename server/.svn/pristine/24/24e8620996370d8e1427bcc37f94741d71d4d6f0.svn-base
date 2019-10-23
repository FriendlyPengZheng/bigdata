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

path="${DAY_DIR}/$date/distr*/part*"
for type in ucount count sum set max
do
	path="$path:${DAY_DIR}/$date/$type/part*"
done
$DB_BASIC_CHECK $date $path
