/*
 * =====================================================================================
 *
 *       Filename:  statlogger.h
 *
 *    Description:
 *
 *        Version:  1.0
 *        Created:  2014年03月17日 16时23分18秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  henry (韩林), henry@taomee.com
 *        Company:  TaoMee.Inc, ShangHai
 *
 * =====================================================================================
 */
#ifndef H_STATLOGGER_C_H
#define H_STATLOGGER_C_H

#include <stdint.h>

/**
 * @brief  TaskType需跟StatLogger类中TaskType保持一致
 */
typedef enum TaskType {
    task_begin,

    task_newbie,     /* ! 新手任务 */
    task_story,      /* ! 主线任务 */
    task_supplement, /* ! 支线任务 */
    //task_etc,        /* ! 其他任务 */

    task_end
}TaskType;

/**
 *  初始化,调用接口前必须先初始化 
 *  game_id:游戏ID
 *  zone_id:区ID,不区分传-1
 *  svr_id:服ID,不区分传-1
 *  site_id:平台ID -1表示该游戏不会拿出去放在不同的平台上运营  1：表示淘米平台
 *  isgame:是否游戏,为游戏传1
 */
int init(int game_id, int32_t zone_id, int32_t svr_id, int32_t site_id, int isgame);
 /* *
  *    统计当前在线人数 每分钟调用一次
  *    cnt: 当前在线
  *    zoneid: -1总体在线  0电信在线 1网通在线
*/
int online_count(int cnt, int zoneid);

/* *
 * 注册角色
 * uid:用户米米号
 * cli_ip: 用户的IP地址,不知道ID地址填0
 * browser 用户浏览器
 * os 用户flash版本
 * ads_id 广告渠道
*/
int reg_role(int uid, uint32_t cli_ip,char* browser,char* os,int ads_id);

/* *
 * 验证用户名和密码
 * uid: 用户米米号
*/
int verify_passwd(int uid,uint32_t cli_ip);

/* *
 * 登录online
 * uid: 用户米米号
 * lv: 用户的等级
 * cli_ip: 用户登录时的IP地址,无法获取填0
*/
int login_online(int uid,int lv, uint32_t cli_ip,char* browser,char* os,int ads_id);

/* *
 * 登出online
 * uid:用户米米号
 * oltime:用户本次在线时长，单位：秒
*/
int logout(int uid , int oltime);

/* *
 * 升级
 * uid:用户米米号
 * lv:升级后的等级
*/
int level_up(int uid, int lv);


/* *
 * 接收任务
 * uid:用户米米号
 * type:任务类型
 * taskid:任务id
*/
int accept_task(int uid, TaskType type, int taskid);

/* *
 * 完成任务
 * uid:用户米米号
 * type:任务类型
 * taskid:任务id
*/
int finish_task(int uid, TaskType type, int taskid);

/* *
 * 放弃任务
 * uid:用户米米号
 * type: 任务类型
 * taskid:任务 id
 */
int abort_task(int uid, TaskType type, int taskid);

/* *
 * 自定义计算人数人次
 * 对sub_stat_name做人数人次计算 比如要统计 庄园小游戏->抓猪猪 这个游戏的人数人次
 * 这样调用: selflog(471834234, "庄园小游戏", "抓猪猪");
*/
int selflog(int uid, char *stat_name, char *sub_stat_name);

/* *
 * 自定义计算item类型的人数人次
 * 除了对sub_stat_name做人数人次计算外，还对每个itemid做人数人次,
 * 比如　庄园礼包领取情况->超拉礼包领取情况 下面的一星礼包 、二星礼包、三星礼包、四星礼包领取的统计,可以如下调用：
 * selfitemlog(4534636, "庄园礼包领取情况", "超拉礼包领取情况", "一星礼包" );
 * selfitemlog(4534636, "庄园礼包领取情况", "超拉礼包领取情况", "二星礼包" );
 * selfitemlog(4534636, "庄园礼包领取情况", "超拉礼包领取情况", "三星礼包" );
 * selfitemlog(4534636, "庄园礼包领取情况", "超拉礼包领取情况", "四星礼包" );
 * 就可以统计出超拉礼包领取的人数人次，以及每星的领取人数人次
*/
int selfitemlog(int uid, char *stat_name, char *sub_stat_name, char* itemidorname);

#endif  //H_STATLOGGER_C_H
