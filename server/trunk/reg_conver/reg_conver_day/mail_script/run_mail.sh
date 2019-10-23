#! /bin/bash

if [ $# -lt 2 ];then
    echo "usage: [发送模式, 测试:1, 正式: 2] [内容模式, 工作室模式:1, 单项目模式 :2] [选填, 发送日期]"
    exit 1;
fi

# 发送模式
send_mode=$1

# 内容模式: 工作室模式:1 , 单项目模式: 2
content_mode=$2


# 不论什么模式，只要第3个参数指定日期，则按日期发送，否则默认发送昨天
if [ $# == 3 ];then
    # 第3个参数指定日期
    compute_day=`date -d"$3" +"%Y-%m-%d"`
else
    # 未指定则默认拉昨天的
    compute_day=`date -d"-1 day" +"%Y-%m-%d"`
fi

echo "准备发送注册转化邮件, 发送模式: $send_mode, 内容模式：$content_mode, 发送日期: $compute_day"

# 漏斗转化率
php mail_reg_daily.php $compute_day $send_mode $content_mode
if [ $send_mode == 1 ];then
    # 按步骤计算转化率（只发给部门内部人员）
    php mail_reg_daily_step.php $compute_day 1 1
fi

#将HTML文件传送到微信服务器 用于微信发送注册转化日报
sh transmit_file.sh
