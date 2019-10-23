#! /bin/bash

# author: lynn
# created   on 20130403
# modified  on 20131220
#### 本脚本用于对注册转化数据的小时数据进行监控

# start time
echo start on `date +"%Y-%m-%d %H:%M:%S"`


### 获取参数
if [ $# != 3 ];then
    echo "usage: [must_day] [must_hour] [强制模式: 重拉:1, 存在则不拉:2]"
    exit 1;
fi
must_day=$1
must_hour=$2
# 是否强制重拉
mode=$3


echo 要计算的日期: $must_day, 要计算的小时: $must_hour, 强制模式:$mode



# 数据定义
WORKDIR=`dirname "$0"`
WORKDIR=`cd ${WORKDIR}; pwd`
ROOTDIR=`cd ${WORKDIR}; cd ../ ; pwd`
CONFDIR=`cd ${ROOTDIR}/"conf"; pwd`

### 加载所有公共
source ${CONFDIR}/"setup.sh"
source ${WORKDIR}/"common_config.sh"



### 从hadoop里拉取数据
# 注册数据 register
# 源数据 register login role online game

if [ $mode == 1 ];then
    hadoop fs -text /ads/account/register/"$must_day"_"$must_hour" > \
    $data_all_reg/"$must_day"_"$must_hour"
elif [ $mode == 2 ];then
    if [[ -e $data_all_reg/"$must_day"_"$must_hour" ]];then
        echo $data_all_reg/"$must_day"_"$must_hour" exists, no need to pull!
    else
        hadoop fs -text /ads/account/register/"$must_day"_"$must_hour" > \
        $data_all_reg/"$must_day"_"$must_hour"
    fi
else
    echo "invalid mode: $mode, when htext register data"

fi


# 登录数据 login
if [ $mode == 1 ];then
    hadoop fs -text /ads/account/login/$must_day/*_$must_hour > \
    $data_all_login/"$must_day"_"$must_hour"
elif [ $mode == 2 ];then
    if [[ -e $data_all_login/"$must_day"_"$must_hour" ]];then
        echo $data_all_login/"$must_day"_"$must_hour" exists, no need to pull!
    else
        hadoop fs -text /ads/account/login/$must_day/*_$must_hour > \
        $data_all_login/"$must_day"_"$must_hour"
    fi
else
    echo "invalid mode: $mode, when htext login data"
fi



# 创建角色数据 role
if [ $mode == 1 ];then
    hadoop fs -text /ads/account/role/$must_day/*_$must_hour > \
    $data_all_role/"$must_day"_"$must_hour"
elif [ $mode == 2 ];then
    if [[ -e $data_all_role/"$must_day"_"$must_hour" ]];then
        echo $data_all_role/"$must_day"_"$must_hour" exists, no need to pull!
    else
        hadoop fs -text /ads/account/role/$must_day/*_$must_hour > \
        $data_all_role/"$must_day"_"$must_hour"
    fi
else
    echo "invalid mode: $mode, when hext role data"
fi


# online
if [ $mode == 1 ];then
    hadoop fs -text /ads/account/online/$must_day/*_$must_hour > \
    $data_all_online/"$must_day"_"$must_hour"
elif [ $mode == 2 ];then
    if [[ -e $data_all_online/"$must_day"_"$must_hour" ]];then
        echo $data_all_online/"$must_day"_"$must_hour" exists, no need to pull!
    else
        hadoop fs -text /ads/account/online/$must_day/*_$must_hour > \
        $data_all_online/"$must_day"_"$must_hour"
    fi
else
    echo "invalid mode: $mode, when hext online data"
fi

##重拉机制，文件不存在或为空
type="reg login role online"
names=(register login role online)
j=0
for i in $type
do
    localdir=`eval echo "$"data_all_$i""`
    echo $localdir/${must_day}_${must_hour}
    if [ ! -s $localdir/${must_day}_${must_hour} ];then
        if [ ${i}x = "reg"x ];then
            dir_hadoop="/ads/account/${names[$j]}/${must_day}_${must_hour}"
        else
            dir_hadoop="/ads/account/${names[$j]}/${must_day}/*_${must_hour}"
        fi
        echo "$i 文件不存在或为空"
        hadoop fs -text $dir_hadoop > $localdir/${must_day}_${must_hour}
    fi
    let j++
done

echo 注册 登录 创建角色 登录online的数据拉取完毕
echo end on `date +"%Y-%m-%d %H:%M:%S"`
