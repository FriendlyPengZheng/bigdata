/*
 * =====================================================================================
 *
 *       Filename:  main.cpp
 *
 *    Description:
 *
 *        Version:  1.0
 *        Created:  2013年10月30日 11时59分22秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  henry (韩林), henry@taomee.com
 *        Company:  TaoMee.Inc, ShangHai
 *
 * =====================================================================================
 */


#include <iostream>
#include <stdlib.h>
#include "statlogger.h"

using namespace std;

int main()
{
	StatLogger logger(169);

//	srand(time(0));
	//for (int i = 0; i != 100000; ++i) {
    //
    //在线人数
//    logger.online_count(12345);

    //注册米米号
//    logger.reg_account("47159775", "10.1.6.130", "4399.com", "ie 7", "iphone4s", "debian 7", "1024*768", "3G", "dianxin");

    //注册角色
//    logger.reg_role("456789321", "1380562965", "兔子", "10.1.6.130", "4399.com", "ie 7", "iphone4s", "debian 7", "1024*768", "3G", "dianxin");

    //验证用户名和密码
//    logger.verify_passwd("46789765", "202.19.89.32", "innermedia.taomee.seer.topbar", "firefox", "ipad3.0", "windows xp", "1280*768", "3G", "education");

    //登录online
//    logger.login_online("85642358", "13895623541", "魔法师", 0, 14, "202.19.89.32", "innermedia.taomee.seer.topbar", "firefox", "ipad3.0", "123421341jkdfjksd" "windows xp", "1280*768", "3G", "education");
//登出游戏
   // logger.logout("85642358", 0, 16, 13456 );

    //在线时长
//		logger.accumulate_online_time("82333", "骑士", 600);
//用户升级时
//	logger.level_up("82333", "骑士", 13);

        //用户付费时 b03 用人民币购买游戏币
//		logger.pay("82333",  true, 13, "192.168.1.100", "神祗", "新手任务1", "第一关",
//					100, StatLogger::ccy_cny, StatLogger::pay_charge, "金币", 100, "网银");
        //用户付费时 b01 购买道具
//		logger.pay("82333234",  false, 15, "192.168.1.100", "打斗场", "新手任务", "第i关",
//					100, StatLogger::ccy_mibi, StatLogger::pay_buy, "双倍经验药剂", 10, "支付宝");
        //用户付费时 b01 开通3个月VIP
//		logger.pay("92354234",  false, 18, "192.168.1.100", "比武大会", "新手任务", "第i关",
//					100, StatLogger::ccy_mibi, StatLogger::pay_vip, "3个月VIP", 3, "短信");

    //b03用户使用游戏币购买道具
//		logger.use_golds("b03-82333", true,  "购买道具", 100, 14);
//		logger.buy_item("b03-82333",  true, 14, 1000,  "购买道具", "新手卡", 10);

//		logger.accept_task(StatLogger::task_newbie, "82333", "新手任务1");
//		logger.abort_task(StatLogger::task_newbie, "82333", "新手任务1");
//		logger.finish_task(StatLogger::task_newbie, "82333", "新手任务1");

//logger.pay("432470", false, 100, StatLogger::ccy_mibi, StatLogger::pay_buy, "teta", 1, "7");
        //logger.obtain_spirit("432470", false, 10, "小火候");
        //logger.lose_spirit("432470", false, 10, "小火候");
  //      logger.pay_item("432470", false, 100, StatLogger::ccy_mibi,"teta", 1);
  //      账户系统
        logger.reg_account_system(2, 1123, "", 1123456,StatLogger::gtype_mobile,StatLogger::rtype_username,"a,b;c:d|e");
        logger.login_account_system(2, 1123, "#http://youxi.tao123.com/?index==php\t", 1123456,StatLogger::gtype_web,StatLogger::ltype_unknown,"dddd?=:,;|aaa\t");
        logger.frozen_account_system(123456);
        logger.activate_account_system(123456);

    //比如统计各个运营活动的参与人数人次、每个活动输出的经验值总量、参与该活动的用户的等级分布
		//StatInfo info;
		//info.add_info("exp", 843);
		//info.add_info("lv", 10);
        //info.add_info("item", 1001);
		//info.add_info("coins", 100);
		//info.add_op(StatInfo::op_item, "lv");
		//info.add_op(StatInfo::op_item, "item");
		//info.add_op(StatInfo::op_sum, "exp");
		//info.add_op(StatInfo::op_sum, "coins");
		//logger.log("运营活动", "宇宙大漫游", "8344342333", "");
		//usleep((rand() % 20 + 1) * 1000);
	//}
	return 0;
}
