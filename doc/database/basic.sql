#注册米米号
#insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(6, '注册米米号', 1, 0, 1, 1, 0, '_newuid_', '_newuid_');
#验证用户名和密码
#insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(7, '验证用户名和密码', 1, 0, 1, 1, 0, '_veripass_', '_veripass_');
#创建游戏中角色
insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(8, '游戏玩家', 1, 0, 0, 1, 0, '', '');
insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(9, '在线玩家', 1, 8, 1, 1, 0, '_olvnt_', '_olcnt_');
insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(10, '新增玩家', 1, 8, 1, 1, 0, '_newac_', '_newac_');
insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(11, '新增玩家', 1, 8, 1, 1, 0, '_newpl_', '_newpl_');
insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(12, '新增玩家', 1, 8, 1, 1, 0, '_newrace_', '_newrace_');

insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(13, '活跃玩家', 1, 8, 1, 1, 0, '_lgac_', '_lgac_');
insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(14, '活跃玩家', 1, 8, 1, 1, 0, '_lgpl_', '_lgpl_');
insert into t_web_tree(node_id, node_name, game_id, parent_id, is_leaf, is_basic, hide, stid, sstid) values(15, '活跃玩家', 1, 8, 1, 1, 0, '_lgrace_', '_lgrace_');


insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(10, '在线玩家', 1, '_olcnt_', '_olcnt_', '_olcnt_', 'max');
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(14, '在线玩家', 10, 'report', '', 14);

insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(11, '新增用户', 1, '_newac_', '_newac_', '', 'count');
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(15, '新增用户人次', 11, 'report', '', 14);
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(12, '新增用户', 1, '_newac_', '_newac_', '', 'ucount');
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(16, '新增用户人数', 12, 'report', '', 14);
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(15, '新增用户浏览器分布', 1, '_newac_', '_newac_', '_ie_', 'count');
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(17, '新增用户人次浏览器分布', 15, 'report', 'ie7', 14);
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(18, '新增用户人次浏览器分布', 15, 'report', 'ie8', 14);
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(19, '新增用户人次浏览器分布', 15, 'report', 'firefox', 14);
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(16, '新增用户浏览器分布', 1, '_newac_', '_newac_', '_ie_', 'ucount');
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(20, '新增用户人数浏览器分布', 16, 'report', 'ie7', 14);
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(21, '新增用户人数浏览器分布', 16, 'report', 'ie8', 14);
insert into t_data_info(data_id, data_name, r_id, type, range, sthash) values(22, '新增用户人数浏览器分布', 16, 'report', 'firefox', 14);
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(17, '新增用户设备分布', 1, '_newac_', '_newac_', '_dev_', 'count');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(18, '新增用户设备分布', 1, '_newac_', '_newac_', '_dev_', 'ucount');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(19, '新增用户操作系统分布', 1, '_newac_', '_newac_', '_os_', 'count');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(20, '新增用户操作系统分布', 1, '_newac_', '_newac_', '_os_', 'ucount');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(21, '新增用户屏幕分辨率分布', 1, '_newac_', '_newac_', '_res_', 'count');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(22, '新增用户屏幕分辨率分布', 1, '_newac_', '_newac_', '_res_', 'ucount');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(23, '新增用户网络分布', 1, '_newac_', '_newac_', '_net_', 'count');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(24, '新增用户网络分布', 1, '_newac_', '_newac_', '_net_', 'ucount');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(25, '新增用户服务提供商分布', 1, '_newac_', '_newac_', '_isp_', 'count');
insert into t_report_info(report_id, report_name, game_id, stid, sstid, op_fields, op_type) values(26, '新增用户服务提供商分布', 1, '_newac_', '_newac_', '_isp_', 'ucount');



