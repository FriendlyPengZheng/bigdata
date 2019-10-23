package com.taomee.statlogger.test;

import com.taomee.statlogger.CurrencyType;
import com.taomee.statlogger.PayReason;
import com.taomee.statlogger.StatLogger;
import com.taomee.statlogger.TaskType;

public class StatLoggerTest {
	public static void main(String args[]){
		StatLogger statLogger = new StatLogger(2, -1, -1, -1, 1);
		long ts = System.currentTimeMillis() / 1000;
		// statLogger.write_basic_log("测试online_count",ts);
		 statLogger.online_count(12345,"");
		// statLogger.write_basic_log("测试reg_role",ts);
		 statLogger.reg_role("456789321", "1380562965", "兔子", "10.1.6.130", "4399.com");
		// statLogger.write_basic_log("测试verify_passwd",ts);
//		 statLogger.verify_passwd("46789765", "202.19.89.32", "innermedia.taomee.seer.topbar");
		// statLogger.write_basic_log("测试login_online",ts);
//		 statLogger.login_online("85642358", "13895623541", "魔法师", false, 14, "202.19.89.32", "innermedia.taomee.seer.topbar","education");
		// statLogger.write_basic_log("测试logout",ts);
//		 statLogger.logout("85642358", false, "16", 13456 );
		// statLogger.write_basic_log("测试level_up",ts);
//		 statLogger.level_up("82333", "骑士", 13);
//		 statLogger.write_basic_log("测试pay",ts);
//		 statLogger.pay("82333",  true, 13, CurrencyType.ccy_cny, PayReason.pay_charge, "金币", 100, "网银");
		// statLogger.write_basic_log("测试pay",ts);
		 statLogger.pay("82333234",  false, 15, CurrencyType.ccy_mibi, PayReason.pay_buy, "双倍经验药剂", 10, "支付宝");
		// statLogger.write_basic_log("测试pay",ts);
//		 statLogger.pay("92354234",  false, 18,CurrencyType.ccy_mibi, PayReason.pay_vip, "3个月VIP", 3, "短信");
		// statLogger.write_basic_log("测试pay",ts);
//		 statLogger.pay("432470", false, 100, CurrencyType.ccy_mibi, PayReason.pay_buy, "teta", 1, "7");
		// statLogger.write_basic_log("测试use_golds",ts);
//		 statLogger.use_golds("b03-82333", true,  "购买道具", 100, 14);
		// statLogger.write_basic_log("测试buy_item",ts);
//		 statLogger.buy_item("b03-82333",  true, 14, 1000,  "购买道具", 10);
		// statLogger.write_basic_log("测试accept_task",ts);
//		 statLogger.accept_task(TaskType.task_newbie, "82333", "新手任务1",7);
		// statLogger.write_basic_log("测试abort_task",ts);
//		 statLogger.abort_task(TaskType.task_newbie, "82333", "新手任务1",7);
		// statLogger.write_basic_log("测试finish_task",ts);
//		 statLogger.finish_task(TaskType.task_newbie, "82333", "新手任务1",7);
		// statLogger.write_basic_log("测试obtain_spirit",ts);
//		 statLogger.obtain_spirit("432470", false, 10, "小火候");
		// statLogger.write_basic_log("测试lose_spirit",ts);
		 statLogger.lose_spirit("432470", false, 10, "小火候");
//		statLogger.write_basic_log("测试reg_account_system",ts);
//		statLogger.reg_account_system(2, 1123, "", 1123456,StatLogger::gtype_mobile,StatLogger::rtype_username,"a,b;c:d|e");
//		statLogger.write_basic_log("测试login_account_system",ts);
//		statLogger.login_account_system(2, 1123, "#http://youxi.tao123.com/?index==php\t", 1123456,StatLogger::gtype_web,StatLogger::ltype_unknown,"dddd?=:,;|aaa\t");
//		statLogger.write_basic_log("测试frozen_account_system",ts);
//		statLogger.frozen_account_system(123456);
//		statLogger.write_basic_log("测试activate_account_system",ts);
//		statLogger.activate_account_system(123456);
	}
	
	
}
