USE `db_td_config`; 
SET NAMES UTF8;

ALTER TABLE `t_web_component` ADD COLUMN `ignore_id` int(11) NOT NULL DEFAULT 0 COMMENT '屏蔽ID，默认为component_id+10000';
