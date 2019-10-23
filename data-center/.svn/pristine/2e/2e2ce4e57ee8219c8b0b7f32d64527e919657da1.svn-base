USE `db_td_config`; 
SET NAMES UTF8;

DROP TABLE IF EXISTS `t_web_email_template_content`;
CREATE TABLE `t_web_email_template_content` (
    `email_template_content_id` int(11) auto_increment NOT NULL COMMENT '主键，自增ID',
    `content_type` enum('BOTH','TABLE','GRAPH') NOT NULL DEFAULT 'BOTH' COMMENT '表格与图像',
    `order` smallint NOT NULL COMMENT '顺序',
    PRIMARY KEY(`email_template_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '邮件模板内容配置表';

DROP TABLE IF EXISTS `t_web_email_template_data`;
CREATE TABLE `t_web_email_template_data` (
    `email_template_data_id` int(11) auto_increment NOT NULL COMMENT '邮件模板内容配置ID',
    `email_template_content_id` int(11) NOT NULL COMMENT '邮件模板内容配置ID',
    `data_date_type` enum('MINUTE','DAY') NOT NULL DEFAULT 'DAY' COMMENT '数据时间类型',
    `data_expr` varchar(512) NOT NULL COMMENT '数据表达式',
    `data_name` varchar(215) NOT NULL COMMENT '数据名称',
    `offset` smallint NOT NULL DEFAULT 0 COMMENT '时间偏移',
    `unit` varchar(16) NOT NULL COMMENT '数据单位',
    `in_table` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：在表格中；0：不在',
    `in_graph` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：在图像中；0：不在',
    PRIMARY KEY(`email_template_data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '邮件模板数据配置表';
