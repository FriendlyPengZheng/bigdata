USE `db_td_config`; 
SET NAMES UTF8;

DROP TABLE IF EXISTS `t_web_email_config`;
CREATE TABLE `t_web_email_config` (
    `email_id` int(11) auto_increment NOT NULL COMMENT '主键，自增ID',
    `sender` int(11) NOT NULL COMMENT '发件人',
    `receviers` text NOT NULL COMMENT '收件人email列表，多个用”;”隔开',
    `cc` text NOT NULL COMMENT '抄送人列表，多个用”;”隔开',
    `subject` varchar(512) NOT NULL COMMENT '主题',
    `remarks` text NOT NULL COMMENT '邮件备注（放在最下面）',
    `frequency_type` enum('DAILY','WEEKLY','MONTHLY','QUARTERLY') NOT NULL COMMENT '邮件发送间隔类型',
    `frequency` smallint NOT NULL COMMENT '邮件发送间隔（从1开始，一个月最后一天为-1）',
    PRIMARY KEY(`email_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '邮件配置表';

DROP TABLE IF EXISTS `t_web_email_content`;
CREATE TABLE `t_web_email_content` (
    `email_content_id` int(11) auto_increment NOT NULL COMMENT '主键，自增ID',
    `email_id` int(11) NOT NULL COMMENT '邮件配置ID',
    `content_type` enum('BOTH','TABLE','GRAPH') NOT NULL DEFAULT 'BOTH' COMMENT '表格与图像',
    `order` smallint NOT NULL COMMENT '顺序',
    PRIMARY KEY(`email_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '邮件内容配置表';

DROP TABLE IF EXISTS `t_web_email_data`;
CREATE TABLE `t_web_email_data` (
    `email_data_id` int(11) auto_increment NOT NULL COMMENT '主键，自增ID',
    `email_content_id` int(11) NOT NULL COMMENT '邮件内容配置ID',
    `data_date_type` enum('MINUTE','DAY') NOT NULL DEFAULT 'DAY' COMMENT '数据时间类型',
    `data_expr` varchar(512) NOT NULL COMMENT '数据表达式',
    `data_name` varchar(215) NOT NULL COMMENT '数据名称',
    `gpzs_id` int(11) NOT NULL COMMENT 'gpzs ID',
    `offset` smallint NOT NULL DEFAULT 0 COMMENT '时间偏移',
    `unit` varchar(16) NOT NULL COMMENT '数据单位',
    `in_table` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：在表格中；0：不在',
    `in_graph` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：在图像中；0：不在',
    PRIMARY KEY(`email_data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '邮件数据配置表';
