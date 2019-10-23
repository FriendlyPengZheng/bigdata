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

$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/adsucount/part*
$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/adscount/part*
$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/adssum/part*
