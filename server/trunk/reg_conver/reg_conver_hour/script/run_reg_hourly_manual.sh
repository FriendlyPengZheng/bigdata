#! /bin/bash

# author: lynn
# created   on 20131224
# modified  on 20131224
# modified  on 20151104 (修改日志输出路径)


cur_day=`date -d "-1 day " +"%Y%m%d"`


## 要计算的游戏列表
#games=(mole seer hua gognfu seer2 jl)
#gameids=(1 2 5 6 10 16)

games=(seer hua gognfu seer2 jl)
gameids=(2 5 6 10 16)

logfile=logs/reg_trans_hourly_$cur_day.log

## 统一设置must_day must_hour

#last=`date -d"-1 hour" +"%Y%m%d %H"`
## 要计算的日期和小时
#must_day=${last:0:8}
#must_hour=${last:9:2}
#################################################################################
#sday=20140404
#eday=20140404

### 连续模式
#for i in `seq 0 5`
#do
    #gameid=${gameids[$i]}
    #game=${games[$i]}
    #must_day=$sday
    #while [[ $must_day -le $eday ]]
    #do
        #hour=0
        #while [[ $hour -le 11 ]]
        #do
            #must_hour=`printf "%02d" $hour`
            #sh compute_reg_hourly.sh $gameid $must_day $must_hour   >> $logfile
            ## usage 计算的日期 gameid game的英文 插入模式为3 计算的小时
            #php insert_all.php $must_day $gameid 3 $must_hour

            #hour=$(( $hour + 1 ))
        #done

        #must_day=`date -d "+1 day $must_day" +"%Y%m%d"`
    #done
#done




#################################################################################

#需要修复的小时注册转换的日期，例如修复20151104的某个小时的注册转换数据  looper
#全游戏修复步骤：
#1.修改repair_data的日期(需要被修正的注册转换小时的具体日期.)
#2.修改hours数组的值，然后运行脚本即可。


#单独游戏的修复
#  1.注释掉全游戏的修复语句。
#  2.在脚本最后执行下面的两条脚本语句，
#  sh compute_reg_hourly.sh 16 $repair_data 18  >> $repair_logfile
#  php insert_all.php $repair_data 16 3 18
#

repair_data=20151104;
repair_logfile=logs/reg_trans_hourly_$repair_data.log;

### 散点模式
for i in `seq 0 4`
do
    gameid=${gameids[$i]}
    game=${games[$i]}
    #sh compute_reg_hourly.sh $gameid 20140418 13  >> $logfile
    #php insert_all.php 20140418 $gameid 3 13

    #sh compute_reg_hourly.sh $gameid 20140101 04  >> $logfile
    #php insert_all.php 20140101 $gameid 3 04

    #sh compute_reg_hourly.sh $gameid 20140101 06  >> $logfile
    #php insert_all.php 20140101 $gameid 3 06

    #sh compute_reg_hourly.sh $gameid 20140101 08  >> $logfile
    #php insert_all.php 20140101 $gameid 3 08

    #sh compute_reg_hourly.sh $gameid 20140102 02  >> $logfile
    #php insert_all.php 20140102 $gameid 3 02

    #sh compute_reg_hourly.sh $gameid 20140110 00  >> $logfile
    #php insert_all.php 20140110 $gameid 3 00

    #sh compute_reg_hourly.sh $gameid 20140110 01  >> $logfile
    #php insert_all.php 20140110 $gameid 3 01

    #sh compute_reg_hourly.sh $gameid 20140110 02  >> $logfile
    #php insert_all.php 20140110 $gameid 3 02

    #sh compute_reg_hourly.sh $gameid 20140110 03  >> $logfile
    #php insert_all.php 20140110 $gameid 3 03

    #sh compute_reg_hourly.sh $gameid 20140113 02  >> $logfile
    #php insert_all.php 20140113 $gameid 3 02

    #sh compute_reg_hourly.sh $gameid 20140113 14  >> $logfile
    #php insert_all.php 20140113 $gameid 3 14

    #sh compute_reg_hourly.sh $gameid 20140127 09  >> $logfile
    #php insert_all.php 20140127 $gameid 3 09

    #sh compute_reg_hourly.sh $gameid 20140213 19  >> $logfile
    #php insert_all.php 20140213 $gameid 3 19

    #sh compute_reg_hourly.sh $gameid 20140304 01  >> $logfile
    #php insert_all.php 20140304 $gameid 3 01

    #sh compute_reg_hourly.sh $gameid 20140329 10  >> $logfile
    #php insert_all.php 20140329 $gameid 3 10

    # 20140609 重新计算0606的小时数据
    #修复20151025日6时注册转换小时数据到20151026日16时注册转换小时数据 repair looper
    #主要执行compute_reg_hourly.sh与insert_all.php脚本，依次传入(1)gameId，修复日期
    #需要修复的小时；(2)修复日期，gameId，插入模式3，需要修复的小时。
    #hours=(00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23)
    #hours=(00 01 02 03 04 05 06 07 08 09 10 11 12 13 14)
    hours=(16)
    ##hours=(11 12 13)
    for j in ${hours[*]}
    do
        sh compute_reg_hourly.sh $gameid $repair_data $j   >> $repair_logfile
        php insert_all.php $repair_data $gameid 3 $j
    done


    ## 修复操作@20150410, 0409数据库挂了，无法入库
    #php insert_all.php 20150409 $gameid 3 17
done

############################################################################################
#单独修复某一个游戏的指定日期小时内的注册转换数据
    #sh compute_reg_hourly.sh 16 20151023 18  >> $logfile
    #php insert_all.php 20151023 16 3 18

