package com.taomee.statlogger.test;

import com.taomee.statlogger.CurrencyType;
import com.taomee.statlogger.NewTransStep;
import com.taomee.statlogger.PayReason;
import com.taomee.statlogger.StatLogger;
import com.taomee.statlogger.TaskType;
import com.taomee.statlogger.UnsubscribeChannel;

public class TestStatLogger {
	public static void main(String args[]){
		StatLogger statLogger = new StatLogger(652, -1, -1, -1, 1);
		statLogger.verify_passwd("47159775", "106.235.12.11", "innermedia.taomee.seer.topbar");
		statLogger.reg_role("47159775", "1383019208", "魔法师"," 61.155.182.56","innermedia.taomee.mole.banner");
		statLogger.login_online("47159775", "1383019208", "魔法师", true, 15, "115.12.116.57", "", "网通一区");
		statLogger.login_online("47159775",  "1383019208",  "",  true,  15,"115.12.116.57", "","网通一区");
		statLogger.logout("47159775", true, "13", 3204);
		statLogger.level_up("47159775","",20);
		statLogger.pay("47159775", true, 2000, CurrencyType.ccy_mibi, PayReason.pay_buy, "320001", 2, "1");
		statLogger.obtain_golds("47159876", 10);
		statLogger.buy_item("34159876", true, 13, 20, "元旦礼包", 10);
		statLogger.use_golds("47169879", true, "_开启新功能_", 18, 17);
		statLogger.accept_task(TaskType.task_newbie, "3781654", "打开背包", 20);
		statLogger.finish_task(TaskType.task_newbie, "3781654", "打开背包", 18);
		statLogger.abort_task(TaskType.task_newbie, "3781654", "打开背包", 19);
		statLogger.obtain_spirit("47159775",false,20,"小火猴");
		statLogger.lose_spirit("17459775", false, 25, "小火猴");
		statLogger.unsubscribe("342352345", UnsubscribeChannel.uc_duanxin);
		statLogger.unsubscribe("342352345", UnsubscribeChannel.uc_mibi);
		statLogger.cancel_acct("342352345", "zhifubao");
		statLogger.new_trans(NewTransStep.bgetloginreq, "342352345");
	}
}
