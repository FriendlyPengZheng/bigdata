USE `db_td_config`;
SET NAMES UTF8;

# 集合分享表
DROP TABLE IF EXISTS `t_web_shared_collect`;
CREATE TABLE `t_web_shared_collect` (
    `collect_id` int(11) NOT NULL COMMENT '集合ID',
    `favor_id` int(11) NOT NULL COMMENT '收藏ID',
    `user_id` int(11) NOT NULL COMMENT '被分享人的ID',
    PRIMARY KEY  (`collect_id`, `favor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集合分享表';
