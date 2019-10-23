WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR

date=`date +%Y%m%d`

sh calc.sh $date
