USE `db_td_config`;
SET NAMES UTF8;

# 数据乱码
DROP TABLE IF EXISTS `t_web_data_garbled`;
CREATE TABLE `t_web_data_garbled` (
      `data_id` int(11) NOT NULL COMMENT '数据ID',
      `data_name` char(64) NOT NULL COMMENT '数据名称',
      `report_id` bigint(20) NOT NULL COMMENT 'Report ID',
      `add_time` timestamp NOT NULL COMMENT '数据创建时间',
      `hide` tinyint(4) NOT NULL COMMENT '0：未做操作；2：已自动设置为隐藏',
      `game_id` int(11) NOT NULL COMMENT '游戏ID',
      `stid` char(64) NOT NULL COMMENT 'stid，一级节点',
      `sstid` char(64) NOT NULL COMMENT 'sstid，二级节点',
      `node_id` int(10) NOT NULL COMMENT '节点ID',
      primary key  (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 collate=utf8_bin COMMENT='数据乱码';
