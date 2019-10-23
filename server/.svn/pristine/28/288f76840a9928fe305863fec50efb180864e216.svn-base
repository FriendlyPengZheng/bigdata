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

enum TaskType {
        task_begin,

        task_newbie,     /* ! 新手任务 */
        task_story,      /* ! 主线任务 */
        task_supplement, /* ! 支线任务 */
        task_etc,        /* ! 其他任务 */

        task_end
};

 /* *
  *    统计当前在线人数 每分钟调用一次
  *    gameid :游戏ID
  *    cnt: 当前在线
  *    zoneid: -1总体在线  0电信在线 1网通在线
*/
int online_count(int gameid, int cnt, int zoneid=-1);

/* *
 * 注册角色
 * gameid:游戏id
 * uid:用户米米号
 * cli_ip: 用户的IP地址,不知道ID地址填0
*/
int reg_role(int gameid, uint32_t uid, uint32_t cli_ip=0);

/* *
 * 验证用户名和密码
 * gameid:游戏id
 * uid: 用户米米号
*/
int verify_passwd(int gameid, uint32_t uid);

/* *
 * 登录online
 * gameid:游戏id
 * uid: 用户米米号
 * lv: 用户的等级
 * cli_ip: 用户登录时的IP地址
*/
int login_online(int gameid, uint32_t uid, int lv, uint32_t cli_ip=0);

/* *
 * 登出online
 * gameid:游戏id
 * uid:用户米米号
 * oltime:用户本次在线时长，单位：秒
*/
int logout(int gameid, uint32_t uid , int oltime);

/* *
 * 升级
 * gameid:游戏id
 * uid:用户米米号
 * lv:升级后的等级
*/
int level_up(int gameid, uint32_t uid, int lv);


/* *
 * 接收任务
 * gameid:游戏ID
 * uid:用户米米号
 * type:任务类型
 * taskid:任务id
*/
int accept_task(int gameid, uint32_t uid, TaskType type, uint32_t taskid);

/* *
 * 完成任务
 * gameid:游戏id
 * uid:用户米米号
 * type:任务类型
 * taskid:任务id
*/
int finish_task(int gameid, uint32_t uid, TaskType type, uint32_t taskid);

/* *
 * 放弃任务
 * gameid:游戏id
 * uid:用户米米号
 * type: 任务类型
 * taskid:任务 id
 */
int abort_task(int gameid, uint32_t uid, TaskType type, uint32_t taskid);

/* *
 * 自定义计算人数人次
 * 对sub_stat_name做人数人次计算 比如要统计 庄园小游戏->抓猪猪 这个游戏的人数人次
 * 这样调用: selflog(1, 471834234, "庄园小游戏", "抓猪猪");
*/
int selflog(int gameid, uint32_t uid, char *stat_name, char *sub_stat_name);

/* *
 * 自定义计算item类型的人数人次
 * 除了对sub_stat_name做人数人次计算外，还对每个itemid做人数人次,
 * 比如　庄园礼包领取情况->超拉礼包领取情况 下面的一星礼包 、二星礼包、三星礼包、四星礼包领取的统计,可以如下调用：
 * selfitemlog(1, 4534636, "庄园礼包领取情况", "超拉礼包领取情况", "一星礼包" );
 * selfitemlog(1, 4534636, "庄园礼包领取情况", "超拉礼包领取情况", "二星礼包" );
 * selfitemlog(1, 4534636, "庄园礼包领取情况", "超拉礼包领取情况", "三星礼包" );
 * selfitemlog(1, 4534636, "庄园礼包领取情况", "超拉礼包领取情况", "四星礼包" );
 * 就可以统计出超拉礼包领取的人数人次，以及每星的领取人数人次
*/
int selfitemlog(int gameid, uint32_t uid, char *stat_name, char *sub_stat_name, char* itemidorname);

#endif  //H_STATLOGGER_C_H
