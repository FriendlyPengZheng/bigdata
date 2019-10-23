USE `db_td_config`;
SET NAMES UTF8;

# 更新在线配置表
ALTER TABLE `t_web_online_config` ADD COLUMN `order` int(11) NOT NULL DEFAULT 0 COMMENT '排序';
UPDATE `t_web_online_config` SET `order` = `game_id`;