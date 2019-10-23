USE `db_td_config`;
SET NAMES UTF8;

# 更新导航表
ALTER TABLE `t_web_navi` ADD COLUMN `func_slot` tinyint(2) NOT NULL COMMENT '功能标志位，与游戏功能相关';

UPDATE `t_web_navi` SET auth_id = 24127219999, func_slot = 5 WHERE navi_id = 1;
UPDATE `t_web_navi` SET auth_id = 24127249999 WHERE navi_id = 7;
UPDATE `t_web_navi` SET auth_id = 24127259999 WHERE navi_id = 8;
UPDATE `t_web_navi` SET auth_id = 0 WHERE navi_id = 9;
UPDATE `t_web_navi` SET auth_id = 24128289999 WHERE navi_id = 10;
UPDATE `t_web_navi` SET auth_id = 24138789999 WHERE navi_id = 104;

UPDATE `t_web_navi` SET auth_id = 24127299999 WHERE navi_id = 35;
UPDATE `t_web_navi` SET auth_id = 24127319999 WHERE navi_id = 36;
UPDATE `t_web_navi` SET auth_id = 24127289999 WHERE navi_id = 37;
UPDATE `t_web_navi` SET auth_id = 24127329999 WHERE navi_id = 38;
UPDATE `t_web_navi` SET auth_id = 24127349999 WHERE navi_id = 40;
UPDATE `t_web_navi` SET auth_id = 24127359999 WHERE navi_id = 41;
UPDATE `t_web_navi` SET auth_id = 24127389999 WHERE navi_id = 11;
UPDATE `t_web_navi` SET auth_id = 24127409999 WHERE navi_id = 12;
UPDATE `t_web_navi` SET auth_id = 24127379999 WHERE navi_id = 15;
UPDATE `t_web_navi` SET auth_id = 24127419999 WHERE navi_id = 26;
UPDATE `t_web_navi` SET auth_id = 24127439999 WHERE navi_id = 31;
UPDATE `t_web_navi` SET auth_id = 24127449999 WHERE navi_id = 34;
UPDATE `t_web_navi` SET auth_id = 24128299999 WHERE navi_id = 65;
UPDATE `t_web_navi` SET auth_id = 24128309999 WHERE navi_id = 66;
UPDATE `t_web_navi` SET auth_id = 24128329999 WHERE navi_id = 68;
UPDATE `t_web_navi` SET auth_id = 24128349999 WHERE navi_id = 70;
UPDATE `t_web_navi` SET auth_id = 24138799999 WHERE navi_id = 105;
UPDATE `t_web_navi` SET auth_id = 24138809999 WHERE navi_id = 150;


UPDATE `t_web_navi` SET auth_id = -2 WHERE navi_id = 99;
UPDATE `t_web_navi` SET auth_id = -2 WHERE navi_id = 103;


UPDATE `t_web_navi` SET auth_id = 24136119999 WHERE navi_id = 6;

UPDATE `t_web_navi` SET auth_id = 24136139999 WHERE navi_id = 102;
UPDATE `t_web_navi` SET auth_id = 24137139999 WHERE navi_id = 114;
UPDATE `t_web_navi` SET auth_id = 24136139999 WHERE navi_id = 121;
UPDATE `t_web_navi` SET auth_id = 24137869999 WHERE navi_id = 122;

UPDATE `t_game_info` SET func_slot = func_slot + (1<<5) WHERE game_id IN (1, 2, 5, 6, 10, 14, 15, 16, 19, 169, 170, 204, 10000, 10001, 20000);

