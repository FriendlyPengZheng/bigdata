#! /bin/bash

# author: lynn
# created   on 20130403
# modified  on 20131220
# modified  on 20151026 把拉取小时数据提取到计算之前
#### 本脚本用于对注册转化数据的小时数据进行监控

# start time
echo start on `date +"%Y-%m-%d %H:%M:%S"`

if [ $# -lt 1 ];then
    echo "usage: [必填: gameid] [可选: must_day must_hour]"
    exit 1;
elif [ $# == 1 ];then
    last=`date -d"-1 hour" +"%Y%m%d %H"`
    # 要计算的日期和小时
    must_day=${last:0:8}
    must_hour=${last:9:2}
else
    must_day=$2
    must_hour=$3
fi

# 是否强制重拉,默认不用强制重拉
mode=1
gameid=$1

# 当前日期和小时
cur_day=`date +"%Y%m%d"`
cur_hour=`date +"%H"`

# 要计算的上一小时的日期和小时，格式：20131220 18
echo ""
echo ""
echo 当前日期:$cur_day, 当前小时:$cur_hour, 要计算的日期: $must_day, 要计算的小时: $must_hour


# 数据定义
WORKDIR=`dirname "$0"`
WORKDIR=`cd ${WORKDIR}; pwd`
ROOTDIR=`cd ${WORKDIR}; cd ../ ; pwd`
CONFDIR=`cd ${ROOTDIR}/"conf"; pwd`

### 加载所有公共
source ${CONFDIR}/"setup.sh"
source ${WORKDIR}/"common_config.sh"

game=${games[$gameid]}
gamename=${gamename[$gameid]}

### 分游戏的数据路径定义
data_game=$WORKDIR/$game
data_game_reg=$data_game/register
data_game_login=$data_game/login
data_game_role=$data_game/role
data_game_online=$data_game/online
data_game_active=$data_game/active
data_game_result=$data_game/result
# 中文数据包含步骤
ret_file=$data_game_result/ret_hour_monitor_"$must_day"_"$must_hour".txt
# 只包含数值
ret_en_file=$data_game_result/ret_en_hour_monitor_"$must_day"_"$must_hour".txt


### 20151026注释 将小时数据提取到最前面计算
### 拉取当前小时段的数据
#sh get_reg_hourly.sh $must_day $must_hour $mode


### 开始计算数据
## 挑出某游戏的数据

# 注册数据
cat $data_all_reg/"$must_day"_"$must_hour" \
| awk -v var=$gameid '{if($4 == var) print $2}' | sort -u |  sort -k1b,1 > \
$data_game_reg/"$must_day"_"$must_hour"


# 平台登录 login
cat $data_all_login/"$must_day"_"$must_hour" \
| awk -v var=$gameid '{if($4 == var) print $2}' \
| sort -u | sort -k1b,1 > $data_game_login/"$must_day"_"$must_hour"


# 创建角色 role
cat $data_all_role/"$must_day"_"$must_hour"  \
| awk -v var=$gameid '{if($4 == var) print $2}' \
| sort -u | sort -k1b,1 > $data_game_role/"$must_day"_"$must_hour"


# 登录online
cat $data_all_online/"$must_day"_"$must_hour" \
| awk -v var=$gameid '{if($4 == var) print $2}' \
| sort -u | sort -k1b,1 > $data_game_online/"$must_day"_"$must_hour"


### 检测及建立计算日的结果文件夹, 如 ../seer/result/20130403
cd $data_game_result;
if [ ! -d $must_day ];then
    mkdir $must_day;
fi
cd $must_day;


### 计算并产生米米号列表

if [ $game == "mole" ];then
    # 1-2 注册米米号到验证密码
    join $data_game_reg/"$must_day"_"$must_hour" $data_game_login/"$must_day"_"$must_hour" \
    | sort -k1b,1 > 1to2_$must_hour.txt

    # 2-3 验证密码到验证session
    join 1to2_$must_hour.txt $data_game_online/"$must_day"_"$must_hour"  \
    | sort -k1b,1 > 2to3_$must_hour.txt

    # 3-4 验证session到创建角色
    join 2to3_$must_hour.txt $data_game_role/"$must_day"_"$must_hour"  \
    | sort -k1b,1 > 3to4_$must_hour.txt
else
    # 1-2 注册米米号到验证密码
    join $data_game_reg/"$must_day"_"$must_hour" $data_game_login/"$must_day"_"$must_hour" \
    | sort -k1b,1 > 1to2_$must_hour.txt

    # 2-3 验证密码到注册角色
    join 1to2_$must_hour.txt $data_game_role/"$must_day"_"$must_hour"  \
    | sort -k1b,1 > 2to3_$must_hour.txt

    # 3-4 注册角色到验证session
    join 2to3_$must_hour.txt $data_game_online/"$must_day"_"$must_hour"  \
    | sort -k1b,1 > 3to4_$must_hour.txt
fi

## 步骤定义
std_s1="注册米米号"
std_s2="验证密码"
std_s3="创建角色"
std_s4="登录online"

step_seer=("$std_s1"
"$std_s1->$std_s2"
"$std_s2->$std_s3"
"$std_s3->$std_s4")

step_mole=("$std_s1"
"$std_s1->$std_s2"
"$std_s2->$std_s4"
"$std_s4->$std_s3")

# 计算数值
num_reg=`wc -l $data_game_reg/"$must_day"_"$must_hour" | cut -d" " -f1`
num_s1=`wc -l 1to2_$must_hour.txt | cut -d" " -f1`
num_s2=`wc -l 2to3_$must_hour.txt | cut -d" " -f1`
num_s3=`wc -l 3to4_$must_hour.txt | cut -d" " -f1`

# 计算转化率
rate_s1=`awk -v var1=$num_reg -v var2=$num_s1 \
'BEGIN {printf("%0.4f", var2/var1)}'`
rate_s2=`awk -v var1=$num_reg -v var2=$num_s2 \
'BEGIN {printf("%0.4f", var2/var1)}'`
rate_s3=`awk -v var1=$num_reg -v var2=$num_s3 \
'BEGIN {printf("%0.4f", var2/var1)}'`

if [ -e $ret_en_file ];then
    rm -f $ret_en_file
fi

if [ -e $ret_file ];then
    rm -f $ret_file
fi

if [ $game == "mole" ];then
    echo "当前计算日期是: $must_day, 计算的小时是: $must_hour" >> $ret_file
    echo "各环节人数:" >> $ret_file
    echo "${step_mole[0]} : $num_reg"    >> $ret_file
    echo "${step_mole[1]} : $num_s1"    >> $ret_file
    echo "${step_mole[2]} : $num_s2"     >> $ret_file
    echo "${step_mole[3]} : $num_s3"    >> $ret_file
    echo "" >> $ret_file
    echo "各环节与第一步的转化率:" >> $ret_file
    echo "${step_mole[1]}: $rate_s1" >> $ret_file
    echo "${step_mole[2]}: $rate_s2" >> $ret_file
    echo "${step_mole[3]}: $rate_s3" >> $ret_file
else
    echo "当前计算日期是: $must_day, 计算的小时是: $must_hour" >> $ret_file
    echo "各环节人数:" >> $ret_file
    echo "${step_seer[0]} : $num_reg"    >> $ret_file
    echo "${step_seer[1]} : $num_s1"    >> $ret_file
    echo "${step_seer[2]} : $num_s2"     >> $ret_file
    echo "${step_seer[3]} : $num_s3"    >> $ret_file
    echo "" >> $ret_file
    echo "各环节与第一步的转化率:" >> $ret_file
    echo "${step_seer[1]}: $rate_s1" >> $ret_file
    echo "${step_seer[2]}: $rate_s2" >> $ret_file
    echo "${step_seer[3]}: $rate_s3" >> $ret_file

fi


echo $num_reg >> $ret_en_file
echo $num_s1 >> $ret_en_file
echo $num_s2 >> $ret_en_file
echo $num_s3 >> $ret_en_file
echo $rate_s1 >> $ret_en_file
echo $rate_s2 >> $ret_en_file
echo $rate_s3 >> $ret_en_file

## 12.20~12.23收件人
#mail -s"赛尔号注册转化数据-$must_day-$must_hour" \
#aray@taomee.com \
#shawnluo@taomee.com \
#rooney@taomee.com \
#billy@taomee.com \
#kavy@taomee.com \
#lynn@taomee.com \
#henry@taomee.com \
#< $ret_file

## 12.23每小时发给自己
#subject="$gamename""注册转化数据/$must_day-$must_hour"
#mail -s"$subject" \
#lynn@taomee.com \
#< $ret_file


# end time
echo end on `date +"%Y-%m-%d %H:%M:%S"`
