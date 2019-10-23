#mysql -h192.168.71.68 -usrvmgr -psrvmgr@pwd

CREATE TABLE `t_sdk_route_info` (
	`db_name` varchar(255) NOT NULL,
	`tbl_start_num` int    NOT NULL,
	`tbl_end_num`   int    NOT NULL,
	`db_host` varchar(255) NOT NULL default 'localhost',
	`db_port` int          NOT NULL default '3306',
	`db_user` varchar(255) NOT NULL,
	`db_pswd` varchar(255) NOT NULL,
	UNIQUE KEY  (`db_name`, `tbl_start_num`),
	UNIQUE KEY  (`db_name`, `tbl_end_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 
