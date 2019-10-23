USE `db_td_config`; 
SET NAMES UTF8;

# 修改t_data_info
ALTER TABLE `t_data_info` ADD COLUMN `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '检测数据标志。0：未检测；1：已检测';

# 修改t_report_info
ALTER TABLE `t_report_info` ADD COLUMN `flag` tinyint(2) NOT NULL DEFAULT 0 COMMENT '检测数据标志。0：未检测；1：已检测';
