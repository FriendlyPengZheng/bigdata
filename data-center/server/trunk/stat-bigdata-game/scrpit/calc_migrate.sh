source config.sh

date=$1

if [[ $date == "" ]]; then
    echo invalid param: date
    exit
fi

$DB_UPLOAD -type 2 -date $date -path /bigdata/tmp/20140220/cplan/6
