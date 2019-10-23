USE `db_td_config`; 
SET NAMES UTF8;

#定时任务配置信息表
DROP TABLE IF EXISTS `t_timing_task`;
CREATE TABLE `t_timing_task` (
    `task_id` int(11) auto_increment NOT NULL COMMENT '自增ID',
    `timing_type` tinyint(2) NOT NULL DEFAULT 1 COMMENT '定时类型。1：每N分钟；2：每周第几天发布',
    `timing_options` varchar(255) NOT NULL COMMENT '定时配置，存为json格式',
    `timing_interval` int(11) NOT NULL DEFAULT 0 COMMENT '间隔时间',
    `status` tinyint(2) NOT NULL DEFAULT  1 COMMENT '状态。1：初始化；2：执行中',
    `next_execute_time` int(11) NOT NULL DEFAULT 0 COMMENT '下一次执行时间',
    `last_execute_time` int(11) NOT NULL DEFAULT 0 COMMENT '上一次执行时间',
    `command` varchar(1024) NOT NULL COMMENT '执行的命令',
    `delete_from` varchar(218) NOT NULL COMMENT '删除的依据',
    PRIMARY KEY(`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '定时任务配置信息表';

#自动扫描的乱码日志
DROP TABLE IF EXISTS `t_web_encode_log`;
CREATE TABLE `t_web_encode_log` (
    `encode_log_id` int(11) auto_increment NOT NULL COMMENT '自增ID',
    `table_name` varchar(255) NOT NULL COMMENT '表格名称',
    `to_hide_id` int(11) NOT NULL COMMENT '处理的ID',
    `garbled` varchar(1024) NOT NULL COMMENT '乱码',
    `time` int(11) NOT NULL COMMENT '操作时间',
    PRIMARY KEY(`encode_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '自动扫描的乱码日志';

#修改t_web_tree
ALTER TABLE `t_web_tree` ADD COLUMN `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '节点状态，用户检测。0：未检测；2：已检测';
