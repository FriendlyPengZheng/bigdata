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

#月新增、留存、回流用户付费额、人数
sh calc_pay_monthuser.sh	$date	new  > $WORKDIR/log/$date/calc_pay_month_new.log		2>&1 &
sh calc_pay_monthuser.sh	$date	keep > $WORKDIR/log/$date/calc_pay_month_keep.log		2>&1 &
sh calc_pay_monthuser.sh	$date	back > $WORKDIR/log/$date/calc_pay_month_back.log		2>&1 &
