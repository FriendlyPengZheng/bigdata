UPDATE `t_web_navi` SET navi_url = 'admin/manage/index/01' WHERE navi_id = 103;
ALTER TABLE `t_web_navi` ADD COLUMN `game_related` tinyint(2) NOT NULL DEFAULT 0 COMMENT '是否跟游戏关联';
UPDATE `t_web_navi` SET game_related = 1 WHERE navi_key = 'game';
INSERT INTO `t_web_navi` VALUES (118, '模板管理子页', '', 'admin/manage/displaypage/01', 103, -2, 2, 1, 0, 1, 1, 0);

update t_web_component set module_key = replace(module_key, 'webgame', 'gameanalysis');
update t_web_component set module_key = replace(module_key, 'mobilegame', 'gameanalysis');