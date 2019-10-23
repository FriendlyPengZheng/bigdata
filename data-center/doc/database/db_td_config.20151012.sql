USE `db_td_config`; 
SET NAMES UTF8;

# 游戏基本信息表
ALTER TABLE `t_game_info` ADD COLUMN `game_email` varchar(128) NOT NULL COMMENT '游戏部门邮件地址';
