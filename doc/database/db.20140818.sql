CREATE TABLE `t_model_info` (
	`model_id` int(11) NOT NULL default 0 COMMENT '漏斗模型id',
	`model_step`	tinyint(4) NOT NULL default 0 COMMENT '漏斗模型步骤id，从0开始',
  `game_id` int(11) NOT NULL default '-1' ,
  `step_name` char(64) NOT NULL default '',
  `stid` char(64) NOT NULL default '',
  `sstid` char(64) NOT NULL default '',
  `op_fields` char(64) default NULL COMMENT '操作的字段',  
  PRIMARY KEY  (`model_id`,`model_step`,`game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;