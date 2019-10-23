USE `db_td_config`; 
SET NAMES UTF8;

ALTER TABLE `t_web_basic_data` ADD COLUMN `comments` text NOT NULL COMMENT '指标注释';
