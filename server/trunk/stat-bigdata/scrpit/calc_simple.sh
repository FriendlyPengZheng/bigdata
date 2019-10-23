WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR

date=$1

if [[ $date == "" ]]; then
    echo invalid param: date
    exit
fi

mkdir $WORKDIR/log/$date

#分事件
sh calc_basic.sh $date true > $WORKDIR/log/$date/calc_basic.log 2>&1
#基础项
for type in count ucount max set sum
do
	sh calc_${type}.sh $date > $WORKDIR/log/$date/calc_${type}.log 2>&1 &
done
for type in max set sum
do
	sh calc_distr.sh $date $type > $WORKDIR/log/$date/calc_distr_${type}.log 2>&1 &
done
for p in {1..8}
do
	wait %$p
done
sh calc_ads.sh $date > $WORKDIR/log/$date/calc_ads.log 2>&1
sh calc_tosql.sh $date > $WORKDIR/log/$date/calc_tosql.log 2>&1 &
sh calc_tosql_ads.sh $date > $WORKDIR/log/$date/calc_tosql_ads.log 2>&1 &

sh calc_custom.sh $date > $WORKDIR/log/$date/calc_custom.log 2>&1 &

#加工项
sh calc_buyitem.sh $date > $WORKDIR/log/$date/calc_buyitem.log 2>&1 &       #道具销售
sh calc_assignment.sh $date > $WORKDIR/log/$date/calc_assignment.log 2>&1 &  #任务完成，接受，放弃
sh calc_spirit.sh $date > $WORKDIR/log/$date/calc_spirit.log 2>&1 &  #精灵总体数量
sh calc_device_new.sh $date > $WORKDIR/log/$date/calc_device_new.log 2>&1 &

#tash 64
sh calc_payrate.sh $date > $WORKDIR/log/$date/calc_payrate.log 2>&1 &
#task 12(先计算新增用户(首次登陆))
sh calc_account_all.sh $date > $WORKDIR/log/$date/calc_account_all.log 2>&1 #等新增算完
#task 13-14
sh calc_account_nday.sh $date > $WORKDIR/log/$date/calc_account_nday.log 2>&1 &
#task 65-67
sh calc_pay_all.sh $date > $WORKDIR/log/$date/calc_pay_all.log 2>&1
sh calc_pay.sh $date > $WORKDIR/log/$date/calc_pay.log 2>&1
php distr_name.php
php order.php
