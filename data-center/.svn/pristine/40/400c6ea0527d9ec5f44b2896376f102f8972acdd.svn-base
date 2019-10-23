CREATE TABLE `t_web_set` (
  `set_id` int(11) NOT NULL auto_increment COMMENT '集合ID',
  `set_name` varchar(255) NOT NULL default '' COMMENT '集合名称',
  `game_id` int(11) NOT NULL default '0' COMMENT '游戏ID',
  `component_id` int(11) NOT NULL default '0' COMMENT '组件ID',
  `data_index` varchar(255) NOT NULL default '' COMMENT '数据序号',
  PRIMARY KEY  (`set_id`),
  UNIQUE KEY `gi_ci` (`game_id`,`component_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='数据集合表';

CREATE TABLE `t_web_set_data` (
  `data_name` varchar(255) NOT NULL default '' COMMENT '数据名称',
  `data_id` bigint(20) NOT NULL default '0' COMMENT '数据ID',
  `data_expr` char(255) NOT NULL default '' COMMENT '数据表达式[d1;d2|{0}*{1}*100]',
  `factor` double NOT NULL default '1' COMMENT '倍率',
  `precision` tinyint(4) NOT NULL default '2' COMMENT '精度',
  `unit` varchar(10) NOT NULL default '' COMMENT '单位',
  `game_id` int(11) NOT NULL default '0' COMMENT '游戏ID',
  `set_id` int(11) NOT NULL COMMENT '集合ID',
  PRIMARY KEY  (`data_id`,`set_id`,`data_expr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集合数据表';

BEGIN;
alter table t_web_metadata change `metadata_name` `data_name` varchar(255) NOT NULL default '' COMMENT '数据名称';
alter table t_web_metadata change `type` `type` enum('report','result','set') NOT NULL default 'report' COMMENT '统计项类型';
alter table t_web_metadata add column `data_expr` char(255) NOT NULL default '' COMMENT '数据表达式[d1;d2|{0}*{1}*100]' after `data_id`;
alter table t_web_metadata add column `factor` double NOT NULL default '1' COMMENT '倍率' after `data_expr`;
alter table t_web_metadata add column `precision` tinyint(4) NOT NULL default '2' COMMENT '精度' after `factor`;
alter table t_web_metadata add column `unit` varchar(10) NOT NULL default '' COMMENT '单位' after `precision`;
alter table t_web_metadata drop primary key;
alter table t_web_metadata add primary key(`data_id`,`gpzs_id`,`collect_id`,`data_expr`);
COMMIT;

# 鲸鱼用户视图
CREATE DEFINER=CURRENT_USER SQL SECURITY INVOKER VIEW `v_whale_user` AS 
    SELECT user.*, gpzs.gpzs_name AS platform_name
    FROM `t_whale_user` AS `user`
    INNER JOIN `t_gpzs_info` AS `gpzs`
         ON user.game_id = gpzs.game_id 
        AND user.platform_id = gpzs.platform_id 
        AND user.zone_id = gpzs.zone_id 
        AND user.server_id = gpzs.server_id
    WHERE gpzs.zone_id = -1 and gpzs.server_id = -1 and gpzs.status = 0;

# 鲸鱼用户月数据视图
CREATE DEFINER=CURRENT_USER SQL SECURITY INVOKER VIEW `v_whale_user_month` AS 
    SELECT user.game_id, user.platform_id, user.zone_id, user.server_id, user.account_id, user.platform_name, user.ctime, user.current_level, data.time, data.total_payments, data.total_count, data.total_ratio
    FROM `t_whale_user_month` AS `data`
    INNER JOIN `v_whale_user` AS `user`
         ON data.game_id = user.game_id 
        AND data.platform_id = user.platform_id 
        AND data.zone_id = user.zone_id 
        AND data.server_id = user.server_id
        AND data.account_id = user.account_id
    WHERE data.zone_id = -1 and data.server_id = -1;