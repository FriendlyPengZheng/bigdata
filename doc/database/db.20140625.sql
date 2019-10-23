USE `db_td_config`; 
SET NAMES UTF8;

ALTER TABLE `t_web_component` ADD COLUMN `component_title` varchar(255) NOT NULL DEFAULT '' COMMENT 'component的名称';
