#! /bin/bash


### 此脚本用于把一个新游戏加入到集合邮件中
# author: lynn
# created   on 20160202
# modified  on 20160205

### 今天
today=`date +"%Y%m%d"`

##### 配置参数 以下所有参数根据当前情况修改
### 新生成的email的id
new_email_id=118
### 要加入的集合email的id
group_email_id=111
### 在汇总邮件里显示的名称
data_name="7k7k平台"

##### 注意db的账号密码已做特殊处理，使用时改为线上密码



##### 具体步骤
### 第0步：在tongji页面生成一个新的email，此为前提条件



### 第一步：拉取新游戏的email_content_id
#mysql -umodelreg -pxxx -h192.168.71.76 -P3306 -Ne"
#USE db_td_config;
#set names utf8;
#select email_content_id
#from t_web_email_content
#where email_id = $new_email_id; " > new_game_email_content_id_$new_email_id.txt



### 第二步: 修改新游戏的email在t_web_email_data中的data_name
### 注意！！！
### 1、对于改了content_title的数据项，才需要相应地改data_name,请尽量保持
### 汇总邮件的content_title与模板生成的data_name一致，如果修改，必须保证两边一致才能在第三步匹配到
### 2、第二步完成后，后续步骤才能执行


### 第三步：把t_web_email_data中数据通过data_name与t_web_email_content联系起来 获得content_id
#while read id
#do
#mysql -umodelreg -pxxx -h192.168.71.76 -P3306 -Ne"
#USE db_td_config;
#set names utf8;
#SELECT a.email_data_id,
#a.email_content_id,
#a.data_name,
#b.email_content_id,
#b.content_title
#FROM t_web_email_data AS a
#inner join t_web_email_content AS b
#on b.content_title = a.data_name
#WHERE a.email_content_id = $id
#and b.email_id = $group_email_id" >> new_game_raw_info_$new_email_id.txt
#done < new_game_email_content_id_$new_email_id.txt




#### 第四步：删除重复数据项 只保留汇总邮件需要的数据项


#### 第五步：把t_web_email_data中的email_content_id改为集合邮件中的 email_content_id
#cat new_game_raw_info_$new_email_id.txt | \
#awk -v var1=$data_name '{print "update t_web_email_data set email_content_id ="$4", " \
#"data_name = \"" var1 "\" where email_content_id = "$2" and email_data_id = "$1";"}' > "$new_email_id"_"$today".sql


#### 第六步：执行update语句
#mysql -umodelreg -pxxx -h192.168.71.76 -P3306 db_td_config --default-character-set='utf8' < "$new_email_id"_"$today".sql



