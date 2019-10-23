WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR

date=`date -d "-1 day" +%Y%m%d`

sh calc.sh $date
