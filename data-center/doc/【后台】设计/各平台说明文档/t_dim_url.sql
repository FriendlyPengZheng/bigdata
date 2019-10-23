CREATE TABLE `t_dim_url` (
  `urlid` int(11) unsigned NOT NULL auto_increment,
  `urlname` char(255) character set utf8 collate utf8_bin NOT NULL default '' COMMENT '到这一级为止的url完整链接',
  `partname` varchar(255) NOT NULL default '',
  `rootid` int(11) NOT NULL default '0',
  `parentid` int(11) NOT NULL default '0',
  `updatetime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `flag` tinyint(1) NOT NULL default '0' COMMENT '是否在Web上显示',
  `dlt_flag` tinyint(1) NOT NULL default '0' COMMENT '删除标志位，默认为非删除状态',
  `isleaf` tinyint(1) NOT NULL default '1' COMMENT '叶节点标志位，0-非叶子节点，1-叶子节点',
  PRIMARY KEY  (`urlid`),
  UNIQUE KEY `url_uniq_idx` (`urlname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#select * from t_dim_url;
#+-------+-----------------------------+--------------+--------+----------+---------------------+------+----------+
#| urlid | urlname                     | partname     | rootid | parentid | updatetime          | flag | dlt_flag |
#+-------+-----------------------------+--------------+--------+----------+---------------------+------+----------+
#|     1 | 4399.com                    | 4399.com     |      0 |        0 | 2016-01-07 11:08:25 |    0 |        0 | 
#|     2 | www.4399.com                | www.4399.com |      1 |        1 | 2016-01-07 11:09:00 |    0 |        0 | 
#|     3 | www.4399.com/flash          | flash        |      1 |        2 | 2016-01-07 11:09:32 |    0 |        0 | 
#|     4 | www.4399.com/flash/seer.htm | seer.htm     |      1 |        3 | 2016-01-07 11:09:45 |    0 |        1 | 
#+-------+-----------------------------+--------------+--------+----------+---------------------+------+----------+