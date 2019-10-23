#新版统计平台的数据库表创建语句
#td = taomee data
create database db_td_config;
use db_td_config;
set names utf8;
#游戏基本信息表
create table t_game_info(
    game_id int(11) not null,
    game_name char(64) not null default '',
    game_type enum('webgame', 'clientgame', 'mobilegame', 'test') not null default 'webgame' comment 'webgame:页游 clientgame:端游 mobilegame:手游 test:测试',
    auth_id varchar(32) not null default '0' comment '查看权限ID',
	manage_auth_id varchar(32) NOT NULL default '0' COMMENT '管理权限ID',
	`ignore` varchar(255) NOT NULL default '' COMMENT '忽略项代号，下划线隔开',
    status tinyint(4) not null default 0 comment '0:未使用 1：使用 2：删除',
    primary key(game_id),
    unique key(game_name)
)ENGINE=InnoDB Default charset = utf8;
#导航表
create table `t_web_navi` (
  `navi_id` int not null auto_increment comment '导航ID',
  `navi_name` varchar(128) not null default '' comment '导航名称',
  `navi_url` varchar(128) not null default '' comment '导航地址',
  `navi_key` varchar(128) not null default '' comment '导航键',
  `parent_id` int not null default '0' comment '父级导航ID',
  `auth_id` varchar(32) not null default '0' comment '权限ID',
  `level` tinyint not null default '0' comment '导航层级',
  `is_page` tinyint NOT NULL default '0' COMMENT '1-页面, 0-非页面',
  `display_order` int not null default '1' comment '显示顺序',
  `status` tinyint not null default '1' comment '1：显示，0：不显示',
  primary key  (`navi_id`)
) ENGINE=InnoDB default charset=utf8 collate=utf8_bin comment='导航';
#树结构表
create table t_web_tree(
    node_id int not null auto_increment,
    node_name varchar(255) not null default '' comment '节点名称',
    game_id int not null default 0,
    parent_id int not null default 0,
    is_leaf tinyint(4) not null default 1 comment '2:未知 1：叶子节点 0：非叶子节点',
    is_basic tinyint(4) not null default 0 comment '1:基础统计项 0:非基础统计项',
    hide tinyint(4) not null default 0 comment '0:显示 1：隐藏',
    primary key(node_id)
)ENGINE = InnoDB Default charset = utf8;
#游戏平台区服映射表
create table t_gpzs_info(
    gpzs_id bigint not null auto_increment,
    game_id int not null default 0,
    platform_id int not null default 0,
    zone_id int not null default '-1',
    server_id int not null default '-1',
    gpzs_name char(64) not null default '' comment '平台区服名字',
    status tinyint(4) not null default 0  comment '0:正常 1：下架',
    add_time timestamp NOT NULL default CURRENT_TIMESTAMP,
    primary key(gpzs_id),
    unique key(game_id, platform_id, zone_id, server_id)
)ENGINE = InnoDB Default charset = utf8;
#report_id映射表
create table t_report_info(
    report_id bigint not null auto_increment,
    report_name char(64) not null default '' comment '显示用的名字',
    game_id int not null default 0,
    stid char(64) not null default '',
    sstid char(64) not null default '',
    op_fields char(64) comment '操作的字段',
    op_type enum('count', 'ucount', 'sum', 'max', 'set', 'ip_distr', 'distr_max', 'distr_sum', 'distr_set') not null default 'count' comment '操作类型 人次:count 人数:ucount 求和:sum 求最大:max 覆盖:set ip分布:ip_distr 等级分布:distr_max,distr_sum,distr_set',
    is_multi tinyint not null default 0 comment '0-非multi类型，1-multi类型',
	is_setted tinyint not null default 0 comment '是否设置过分布区间或者Item：0-否，1-是',
	node_id int not null default 0 comment '树结构结点ID',
	`sstid_name` varchar(255) NOT NULL default '' COMMENT 'sstid名称',
    primary key(report_id),
    unique key(game_id, stid, sstid, op_fields, op_type)
)ENGINE= InnoDB Default charset = utf8;
#数据项映射表
create table t_data_info(
    data_id bigint not null auto_increment,
    data_name char(64) not null default '' comment '初始值设置为range+data_type',
    r_id bigint not null default 0 comment 'type为report时，r_id为report_id  type为result时，r_id为result_id',
    type enum('report', 'result') not null default 'report',
    range char(64) not null default '' comment '区间范围',
    display_order int not null default 0 comment '显示顺序',
    hide tinyint(4) not null default 0 comment '是否隐藏 0:显示 1：隐藏',
    sthash int(11) not null default 0,
	`range_name` varchar(255) NOT NULL default '' COMMENT 'range名称',
    primary key(data_id),
    unique key(r_id, type, range),
    key(r_id, type, display_order)
)ENGINE = InnoDB Default charset = utf8;
#分布区间配置表
create table t_distr_range_info(
    r_id bigint not null default 0,
    `type` enum('report', 'result') not null default 'report' comment '统计项类型',
    data_name char(64) not null default '' comment '数据名称',
    lower_bound int not null default 0,
    upper_bound int not null default 0,
	`range` char(64) NOT NULL default '' COMMENT '区间范围',
    primary key(r_id, type, lower_bound)
)ENGINE = InnoDB Default charset = utf8;
#range-item配置表
CREATE TABLE `t_item_config_info` (
  `r_id` bigint NOT NULL default '0' COMMENT '统计项ID',
  `type` enum('report','result') NOT NULL default 'report' COMMENT '统计项类型',
  `data_name` char(64) NOT NULL default '' COMMENT '数据名称',
  PRIMARY KEY (`r_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#任务分类配置表
create table t_task_cat_info(
    cat_id int not null auto_increment,
    cat_name char(64) not null default '',
    game_id int not null default 0,
    task_id char(64) not null default '',
    primary key(cat_id)
)ENGINE = InnoDB Default charset = utf8;
#加工配置表
create table t_task_config(
    result_id bigint not null auto_increment,
    result_name char(64) not null default '',
    uexpr varchar(512) not null default '',
    status tinyint(4) not null default 0 comment '0:正常 1：下架',
    node_id int not null default 0,
    primary key(result_id) 
)ENGINE = InnoDB Default charset = utf8;
#通用加工配置表
CREATE TABLE `t_common_task` (
    `task_id` int NOT NULL auto_increment COMMENT '加工ID',
    `task_name` varchar(255) NOT NULL default '' COMMENT '加工名称',
    `script` varchar(255) NOT NULL default '' COMMENT '加工脚本',
    PRIMARY KEY(`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '通用加工配置';
#通用加工结果表
CREATE TABLE `t_common_result` (
    `result_id` bigint NOT NULL auto_increment COMMENT '加工结果ID',
    `result_name` varchar(255) NOT NULL default '' COMMENT '加工结果名称',
    `game_id` int NOT NULL default 0 COMMENT '游戏ID',
    `task_id` int NOT NULL default 0 COMMENT '加工ID',
    PRIMARY KEY(`result_id`),
    UNIQUE KEY `gid_tid`(`game_id`, `task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '通用加工结果';
#道具销售数据表
CREATE TABLE `t_item_sale_data` (
    `sstid` enum('_coinsbuyitem_', '_mibiitem_') NOT NULL default '_coinsbuyitem_' COMMENT '一级货币道具/米币道具',
    `time` int NOT NULL default 0 COMMENT '时间',
    `game_id` int NOT NULL default 0 COMMENT '游戏ID',
    `platform_id` int NOT NULL default 0 COMMENT '平台ID',
    `zone_id` int NOT NULL default 0 COMMENT '区ID',
    `server_id` int NOT NULL default 0 COMMENT '服ID',
    `item_id` char(128) NOT NULL default '' COMMENT '道具ID/名称',
    `vip` tinyint NOT NULL default 0 COMMENT '0：非VIP，1：VIP',
    `buycount` double NOT NULL default 0 COMMENT '购买人次',
    `buyucount` double NOT NULL default 0 COMMENT '购买人数',
    `salenum` double NOT NULL default 0 COMMENT '销售数量',
    `salemoney` double NOT NULL default 0 COMMENT '销售总价钱',
    PRIMARY KEY(`sstid`, `time`, `game_id`, `platform_id`, `zone_id`, `server_id`, `item_id`, `vip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '道具销售数据';
#道具信息表
CREATE TABLE `t_item_info` (
    `sstid` enum('_coinsbuyitem_', '_mibiitem_') NOT NULL default '_coinsbuyitem_' COMMENT '一级货币道具/米币道具',
    `game_id` int NOT NULL default 0 COMMENT '游戏ID',
    `item_id` char(128) NOT NULL default '' COMMENT '道具ID/名称',
    `item_name` varchar(255) NOT NULL default '' COMMENT '道具名称',
	`hide` tinyint NOT NULL default 0 COMMENT '是否隐藏：0-否，1-是',
    PRIMARY KEY(`sstid`, `game_id`, `item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '道具信息';
#道具销售数据视图
CREATE DEFINER=CURRENT_USER SQL SECURITY INVOKER VIEW `v_item_sale_data` AS 
    SELECT item_data.sstid, item_data.time, item_data.item_id, ifnull(item.item_name, item_data.item_id) as item_name, item_data.vip, gpzs.gpzs_id, item_data.buycount, item_data.buyucount, item_data.salenum, item_data.salemoney
    FROM `t_item_sale_data` AS `item_data`
    INNER JOIN `t_gpzs_info` AS `gpzs`
        ON item_data.game_id = gpzs.game_id 
        AND item_data.platform_id = gpzs.platform_id 
        AND item_data.zone_id = gpzs.zone_id 
        AND item_data.server_id = gpzs.server_id
    LEFT JOIN `t_item_info` AS `item`
        ON item_data.sstid = item.sstid
        AND item_data.game_id = item.game_id
        AND item_data.item_id = item.item_id
    WHERE gpzs.status = 0 AND (item.hide = 0 OR item.hide is null);
#游戏任务数据表
CREATE TABLE `t_gametask_data` (
    `type` enum('new', 'main', 'aux', 'etc') NOT NULL default 'new' COMMENT '新手/主线/支线/其他',
    `time` int NOT NULL default 0 COMMENT '时间',
    `game_id` int NOT NULL default 0 COMMENT '游戏ID',
    `platform_id` int NOT NULL default 0 COMMENT '平台ID',
    `zone_id` int NOT NULL default 0 COMMENT '区ID',
    `server_id` int NOT NULL default 0 COMMENT '服ID',
    `sstid` char(64) NOT NULL default '' COMMENT '游戏任务ID/名称',
    `getucount` double NOT NULL default 0 COMMENT '接取人数',
    `doneucount` double NOT NULL default 0 COMMENT '完成人数',
    `abrtucount` double NOT NULL default 0 COMMENT '放弃人数',
    PRIMARY KEY(`type`, `time`, `game_id`, `platform_id`, `zone_id`, `server_id`, `sstid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '游戏任务数据';
# 游戏任务配置
CREATE TABLE `t_gametask_info` (
    `type` enum('new', 'main', 'aux', 'etc') NOT NULL DEFAULT 'new' COMMENT '新手任务/主线任务/支线任务/其他任务',
    `game_id` int NOT NULL DEFAULT 0 COMMENT '游戏ID',
    `sstid` char(64) NOT NULL DEFAULT '' COMMENT '游戏传入的任务标识ID',
    `gametask_name` varchar(255) NOT NULL default '' COMMENT '自定义任务名称',
    `order` int(11) NOT NULL DEFAULT 0 COMMENT '显示顺序',
	`hide` tinyint NOT NULL default 0 COMMENT '是否隐藏：0-否，1-是',
    PRIMARY KEY(`type`, `game_id`, `sstid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '游戏任务配置';
# 游戏任务视图
CREATE DEFINER=CURRENT_USER SQL SECURITY INVOKER VIEW `v_gametask_data` AS 
    SELECT task_data.`type`, task_data.time, task_data.sstid, ifnull(gametask.gametask_name, task_data.sstid) as gametask_name, ifnull(gametask.`order`, 0) as `order`, gpzs.gpzs_id, task_data.getucount, task_data.doneucount, task_data.abrtucount
    FROM `t_gametask_data` AS `task_data` 
    INNER JOIN `t_gpzs_info` AS `gpzs`
        ON task_data.game_id = gpzs.game_id 
        AND task_data.platform_id = gpzs.platform_id 
        AND task_data.zone_id = gpzs.zone_id 
        AND task_data.server_id = gpzs.server_id 
    LEFT JOIN `t_gametask_info` AS `gametask`
        ON task_data.`type` = gametask.`type`
        AND task_data.game_id = gametask.game_id 
        AND task_data.sstid = gametask.sstid
    WHERE gpzs.status = 0 AND (gametask.hide = 0 OR gametask.hide is null);
# 收藏表
CREATE TABLE `t_web_favor` (
  `favor_id` int NOT NULL auto_increment COMMENT '收藏ID',
  `favor_name` varchar(255) NOT NULL default '' COMMENT '收藏名称',
  `favor_type` tinyint NOT NULL default '1' COMMENT '收藏类型：1-单游戏，2-多游戏',
  `layout` tinyint NOT NULL default '1' COMMENT '收藏布局：1-100%，2-50%',
  `game_id` int NOT NULL default '0' COMMENT '收藏游戏ID',
  `user_id` int NOT NULL default '0' COMMENT '用户ID',
  `ctime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_default` tinyint NOT NULL default '0' COMMENT '0-非默认收藏，1-默认收藏',
  `display_order` int NOT NULL default '0' COMMENT '显示顺序',
  PRIMARY KEY (`favor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收藏表';
# 集合表
CREATE TABLE `t_web_collect` (
  `collect_id` int NOT NULL auto_increment COMMENT '集合ID',
  `collect_name` varchar(255) NOT NULL default '' COMMENT '集合名称',
  `favor_id` int NOT NULL default '0' COMMENT '收藏ID',
  `draw_type` tinyint NOT NULL default '3' COMMENT '集合类型：1-线图，2-堆积柱形图，3-表格，4-百分比堆积柱形图，5-簇状柱形图',
  `display_order` int NOT NULL default '0' COMMENT '显示顺序',
  `user_id` int NOT NULL default '0' COMMENT '用户ID',
  `ctime` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`collect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='集合表';
# 元数据表
CREATE TABLE `t_web_metadata` (
  `metadata_name` varchar(255) NOT NULL default '' COMMENT '元数据名称',
  `r_id` bigint NOT NULL default '0' COMMENT '统计项ID',
  `type` enum('report','result') NOT NULL default 'report' COMMENT '统计项类型',
  `data_id` bigint NOT NULL default '0' COMMENT '数据ID',
  `gpzs_id` bigint NOT NULL default '0' COMMENT '平台（渠道）区服ID',
  `collect_id` int NOT NULL default '0' COMMENT '集合ID',
  `game_id` int NOT NULL default '0' COMMENT '游戏ID',
  `platform_id` int NOT NULL default '0' COMMENT '平台（渠道）',
  `display_order` int NOT NULL default '0' COMMENT '显示顺序',
  PRIMARY KEY (`data_id`, `gpzs_id`, `collect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据表';

#数据表
create database db_td_data_[0_99];

create table t_db_data_minute_[0_99](
    gpzs_id bigint not null default 0,
    data_id bigint not null default 0,
    time int not null default 0, 
    value double not null default 0,
    primary key(gpzs_id, data_id, time)
)ENGINE = InnoDB Default charset = utf8;

create table t_db_data_day_[0_99](
    gpzs_id bigint not null default 0,
    data_id bigint not null default 0,
    time int not null default 0, 
    value double not null default 0,
    primary key(gpzs_id, data_id, time)
)ENGINE = InnoDB Default charset = utf8;