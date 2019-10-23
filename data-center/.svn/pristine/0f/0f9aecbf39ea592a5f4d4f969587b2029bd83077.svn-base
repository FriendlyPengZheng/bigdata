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

#加工项
sh calc_buyitem.sh $date > $WORKDIR/log/$date/calc_buyitem.log 2>&1 &       #道具销售
sh calc_assignment.sh $date > $WORKDIR/log/$date/calc_assignment.log 2>&1 &  #任务完成，接受，放弃
sh calc_spirit.sh $date > $WORKDIR/log/$date/calc_spirit.log 2>&1 &  #精灵总体数量
#task 340-343
sh calc_payamt.sh $date > $WORKDIR/log/$date/calc_payamt.log 2>&1 &
#task 344,345
sh calc_coins.sh $date > $WORKDIR/log/$date/calc_coins.log 2>&1 &
#task 144
sh calc_vip_day.sh $date > $WORKDIR/log/$date/calc_vip_day.log 2>&1 &
#task 129,133
sh calc_active_device.sh $date > $WORKDIR/log/$date/calc_active_device.log 2>&1 &
#task 122
sh calc_level.sh $date > $WORKDIR/log/$date/calc_level.log 2>&1 &
#task 123,124
sh calc_pay_day.sh $date > $WORKDIR/log/$date/calc_pay_day.log 2>&1 &
if [[ "$2"x != "day"x ]]; then
	#task 71-73,89-90
	sh calc_pay_month.sh $date > $WORKDIR/log/$date/calc_pay_month.log 2>&1
	#task 82,83,142
	sh calc_pay_continuous.sh $date > $WORKDIR/log/$date/calc_pay_continuous.log 2>&1 &
	#task 84,85
	sh calc_pay_intermittency.sh $date > $WORKDIR/log/$date/calc_pay_intermittency.log 2>&1 &
	#task 5
	sh calc_new_week.sh $date > $WORKDIR/log/$date/calc_new_week.log 2>&1 &
	#task 6
	sh calc_new_month.sh $date > $WORKDIR/log/$date/calc_new_month.log 2>&1 &
	#task 68-70,87-88
	sh calc_pay_week.sh $date > $WORKDIR/log/$date/calc_pay_week.log 2>&1 &
fi
sh calc_day_online.sh $date > $WORKDIR/log/$date/calc_day_online.log 2>&1
#task 48,50
sh calc_active_online.sh $date > $WORKDIR/log/$date/calc_active_online.log 2>&1 &
#task 28,96
sh calc_activelost.sh $date > $WORKDIR/log/$date/calc_activelost.log 2>&1 &
#task 65-67
sh calc_pay_all.sh $date > $WORKDIR/log/$date/calc_pay_all.log 2>&1
sh calc_pay.sh $date > $WORKDIR/log/$date/calc_pay.log 2>&1
#task 51-54,58,60-61
sh calc_pay_online.sh $date > $WORKDIR/log/$date/calc_pay_online.log 2>&1 &
#task 31,99
sh calc_paylost.sh $date > $WORKDIR/log/$date/calc_paylost.log 2>&1 &
#task 134-137
sh calc_paylevel.sh $date > $WORKDIR/log/$date/calc_paylevel.log 2>&1 &

if [[ "$2"x != "day"x ]]; then
	#task 8-9
	sh calc_player_new_week.sh $date > $WORKDIR/log/$date/calc_player_new_week.log 2>&1 &
	sh calc_player_new_month.sh $date > $WORKDIR/log/$date/calc_player_new_month.log 2>&1 &

	#task 10,22
	sh calc_account_week.sh $date > $WORKDIR/log/$date/calc_account_week.log 2>&1 
	sh calc_activekeep_week.sh $date > $WORKDIR/log/$date/calc_activekeep_week.log 2>&1 &
	#task 29,97
	sh calc_activelost_week.sh $date > $WORKDIR/log/$date/calc_activelost_week.log 2>&1 &
	#task 32,100
	sh calc_paylost_week.sh $date > $WORKDIR/log/$date/calc_paylost_week.log 2>&1 &
	#task 46-47,49,106,108
	sh calc_active_online_week.sh $date > $WORKDIR/log/$date/calc_active_online_week.log 2>&1 &
	#task 56-57,59,110,112
	sh calc_pay_online_week.sh $date > $WORKDIR/log/$date/calc_pay_online_week.log 2>&1 &

	#task 11
	sh calc_account_month.sh $date > $WORKDIR/log/$date/calc_account_month.log 2>&1 
	#task 30,98
	sh calc_activelost_month.sh $date > $WORKDIR/log/$date/calc_activelost_month.log 2>&1 &
	#task 33,101
	sh calc_paylost_month.sh $date > $WORKDIR/log/$date/calc_paylost_month.log 2>&1 &
	#task 45,107,109
	sh calc_active_online_month.sh $date > $WORKDIR/log/$date/calc_active_online_month.log 2>&1 &
	#task 55,111,113
	sh calc_pay_online_month.sh $date > $WORKDIR/log/$date/calc_pay_online_month.log 2>&1 &
	#task 23
	sh calc_activekeep_month.sh $date > $WORKDIR/log/$date/calc_activekeep_month.log 2>&1
	#task 76,77
	sh calc_pay_monthuser.sh $date keep > $WORKDIR/log/$date/calc_pay_monthuser_keep.log 2>&1 &

	#task 15-16
	sh calc_player_week.sh $date > $WORKDIR/log/$date/calc_player_week.log 2>&1 &
	sh calc_player_month.sh $date > $WORKDIR/log/$date/calc_player_month.log 2>&1 &
fi

#task 21
sh calc_activekeep.sh $date > $WORKDIR/log/$date/calc_activekeep.log 2>&1 &

#task 62-64
sh calc_payrate.sh $date > $WORKDIR/log/$date/calc_payrate.log 2>&1 &

#task 91-92
sh calc_acupcu.sh $date > $WORKDIR/log/$date/calc_acupcu.log 2>&1 &

#task 12(先计算新增用户(首次登陆))
sh calc_account_all.sh $date > $WORKDIR/log/$date/calc_account_all.log 2>&1 #等累积算完算新增
#task 34-37,41,43-44
sh calc_new_online.sh $date > $WORKDIR/log/$date/calc_new_online.log 2>&1 &
#task 13-14,132
sh calc_account_nday.sh $date > $WORKDIR/log/$date/calc_account_nday.log 2>&1 &
#tash 116-121
sh calc_device.sh $date > $WORKDIR/log/$date/calc_device.log 2>&1 &
#task 125-128,130,131
sh calc_newpay.sh $date > $WORKDIR/log/$date/calc_newpay.log 2>&1 &
#task 146-153
sh calc_pay_first_distribution.sh $date > $WORKDIR/log/$date/calc_pay_first_distribution.log 2>&1 &
#task 24
sh calc_back.sh $date > $WORKDIR/log/$date/calc_back.log 2>&1
if [[ "$2"x != "day"x ]]; then
	#task 78,79
	sh calc_pay_monthuser.sh $date back > $WORKDIR/log/$date/calc_pay_monthuser_back.log 2>&1 &
fi
#task 25,93
sh calc_newlost.sh $date > $WORKDIR/log/$date/calc_newlost.log 2>&1 &
#task 7
sh calc_account_new_all.sh $date > $WORKDIR/log/$date/calc_account_new_all.log 2>&1 &
if [[ "$2"x != "day"x ]]; then
	#task 114
	sh calc_account_new_week.sh $date > $WORKDIR/log/$date/calc_account_new_week.log 2>&1
	#task 39-40,42,102,104
	sh calc_new_online_week.sh $date > $WORKDIR/log/$date/calc_new_online_week.log 2>&1 &
	#task 26,94
	sh calc_newlost_week.sh $date > $WORKDIR/log/$date/calc_newlost_week.log 2>&1 &
	#task 115
	sh calc_account_new_month.sh $date > $WORKDIR/log/$date/calc_account_new_month.log 2>&1
	#task 27,95
	sh calc_newlost_month.sh $date > $WORKDIR/log/$date/calc_newlost_month.log 2>&1 &
	#task 38,103,105
	sh calc_new_online_month.sh $date > $WORKDIR/log/$date/calc_new_online_month.log 2>&1 &
	#task 74,75
	sh calc_pay_monthuser.sh $date new > $WORKDIR/log/$date/calc_pay_monthuser_new.log 2>&1 &
	#task 80,81
	sh calc_pay_transform.sh $date > $WORKDIR/log/$date/calc_pay_transform.log 2>&1 &
fi

#task 1-4,17-18
sh calc_device_all.sh $date > $WORKDIR/log/$date/calc_device_all.log 2>&1 &
sh calc_device_new.sh $date > $WORKDIR/log/$date/calc_device_new.log 2>&1 &
if [[ "$2"x != "day"x ]]; then
	#task 17-18
	sh calc_device_week.sh $date > $WORKDIR/log/$date/calc_device_week.log 2>&1 &
	sh calc_device_month.sh $date > $WORKDIR/log/$date/calc_device_month.log 2>&1 &
	sh calc_device_new_week.sh $date > $WORKDIR/log/$date/calc_device_new_week.log 2>&1 &
	sh calc_device_new_month.sh $date > $WORKDIR/log/$date/calc_device_new_month.log 2>&1 &
fi
sh calc_newvalue.sh $date > $WORKDIR/log/$date/calc_newvalue.log 2>&1 &
#task 19-20
sh calc_newkeep.sh $date > $WORKDIR/log/$date/calc_newkeep.log 2>&1 
sh calc_lostanaly.sh $date > $WORKDIR/log/$date/calc_lostanaly.log 2>&1 &
sh calc_keepanaly.sh $date > $WORKDIR/log/$date/calc_keepanaly.log 2>&1 &
#task 333-335
sh calc_newlevel.sh $date > $WORKDIR/log/$date/calc_newlevel.log 2>&1 &
#whale user
sh calc_topk.sh $date > $WORKDIR/log/$date/calc_topk.log 2>&1 &

sh calc_custom.sh $date > $WORKDIR/log/$date/calc_custom.log 2>&1 
php distr_name.php
