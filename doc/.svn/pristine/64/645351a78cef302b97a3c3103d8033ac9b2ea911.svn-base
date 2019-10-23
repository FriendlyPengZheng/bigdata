USE `db_td_config`; 
SET NAMES UTF8;

#鲸鱼用户
DROP TABLE IF EXISTS `t_whale_user`;
CREATE TABLE `t_whale_user` (
    `account_id` varchar(128) collate utf8_bin NOT NULL default '',
    `game_id` int(11) NOT NULL COMMENT '游戏ID',
    `platform_id` int(11) NOT NULL DEFAULT '-1' COMMENT '平台ID',
    `zone_id` int(11) NOT NULL DEFAULT '-1' COMMENT '区ID',
    `server_id` int(11) NOT NULL DEFAULT '-1' COMMENT '服ID',
    `ctime` int(11) NOT NULL COMMENT '新增日期，时间戳',
    `last_login_time` int(11) NOT NULL COMMENT '最后登录日期，时间戳',
    `current_level` int(11) NOT NULL DEFAULT '-1' COMMENT'当前等级',
    `first_buyitem_time` int(11) NOT NULL DEFAULT 0 COMMENT '首次按条付费日期，时间戳',
    `last_buyitem_time` int(11) NOT NULL DEFAULT 0 COMMENT '最后按条付费日期，时间戳',
    `buyitem_total_amount` double NOT NULL DEFAULT 0 COMMENT '累计按条付费总额',
    `buyitem_total_count` int(11) NOT NULL DEFAULT 0 COMMENT '累计按条付费次数',
    `consume_golds` double NOT NULL DEFAULT 0 COMMENT '游戏币消耗量',
    `left_golds` double NOT NULL DEFAULT '0' COMMENT '游戏币存量',
    `vip` tinyint(4) NOT NULL DEFAULT '0' COMMENT'当前是否VIP；0：非VIP； 1：VIP',
    `first_vip_time` int(11) NOT NULL DEFAULT 0 COMMENT '首次包月日期，时间戳',
    `last_vip_time` int(11) NOT NULL DEFAULT 0 COMMENT '最后包月日期，时间戳',
    `vip_total_amount` double NOT NULL DEFAULT '0' COMMENT '累计包月总额',
    `vip_total_count` int(11) NOT NULL DEFAULT '0' COMMENT '累计包月次数',
    `time` int(11) NOT NULL DEFAULT '0' COMMENT '无用，仅为入库方便',
    PRIMARY KEY (`account_id`, `game_id`, `platform_id`, `zone_id`, `server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_bin COMMENT='鲸鱼用户';

#鲸鱼用户月数据
DROP TABLE IF EXISTS `t_whale_user_month`;
CREATE TABLE `t_whale_user_month` (
  `account_id` varchar(128) collate utf8_bin NOT NULL default '',
  `game_id` int(11) NOT NULL COMMENT '游戏ID',
  `platform_id` int(11) NOT NULL default '-1' COMMENT '平台ID',
  `zone_id` int(11) NOT NULL default '-1' COMMENT '区ID',
  `server_id` int(11) NOT NULL default '-1' COMMENT '服ID',
  `time` int(11) NOT NULL COMMENT '日期，时间戳',
  `total_payments` double NOT NULL default '0' COMMENT '付费总额',
  `total_count` int(11) NOT NULL default '0' COMMENT '付费次数',
  `total_ratio` double NOT NULL default '0' COMMENT '付费额占比',
  PRIMARY KEY  (`account_id`,`game_id`,`platform_id`,`zone_id`,`server_id`,`time`),
  KEY `valueindex` (`game_id`,`platform_id`,`zone_id`,`server_id`,`time`,`total_payments`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='鲸鱼用户月数据';
