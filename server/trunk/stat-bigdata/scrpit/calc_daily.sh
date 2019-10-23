export LANG=en_US.UTF-8
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
sh merge.sh $date > $WORKDIR/log/$date/merge.log 2>&1
for p in {1..8}
do
	wait %$p
done
#TODO 等五个基本项算完 跑calc_ads.sh 再算后面的 
sh calc_ads.sh $date > $WORKDIR/log/$date/calc_ads.log 2>&1
sh calc_tosql.sh $date > $WORKDIR/log/$date/calc_tosql.log 2>&1 &

sh calc_pay_day.sh $date > $WORKDIR/log/$date/calc_pay_day.log 2>&1 &
sh calc_activekeep_daily.sh $date > $WORKDIR/log/$date/calc_activekeep_daily.log 2>&1 &
sh calc_pay_all.sh $date > $WORKDIR/log/$date/calc_pay_all.log 2>&1
sh calc_pay.sh $date > $WORKDIR/log/$date/calc_pay.log 2>&1
sh calc_payrate.sh $date > $WORKDIR/log/$date/calc_payrate.log 2>&1 &
sh calc_acupcu.sh $date > $WORKDIR/log/$date/calc_acupcu.log 2>&1 &
sh calc_account_all.sh $date > $WORKDIR/log/$date/calc_account_all.log 2>&1 #等累积算完算新增
sh calc_account_nday.sh $date > $WORKDIR/log/$date/calc_account_nday.log 2>&1 
sh calc_newkeep_daily.sh $date > $WORKDIR/log/$date/calc_newkeep_daily.log 2>&1 &
sh calc_account_new_all.sh $date > $WORKDIR/log/$date/calc_account_new_all.log 2>&1 &
