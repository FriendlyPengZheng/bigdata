package com.taomee.statlogger.main;

import com.taomee.statlogger.CurrencyType;
import com.taomee.statlogger.NewTransStep;
import com.taomee.statlogger.PayReason;
import com.taomee.statlogger.StatLogger;
import com.taomee.statlogger.TaskType;
import com.taomee.statlogger.UnsubscribeChannel;

public class MainClass {

	static StatLogger statLogger=new StatLogger(652, -1, -1, -1, 1);
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
//		statLogger.online_count(35, "");
//		statLogger.reg_role("331025680", "-1", "男生", "0", "0");
//		statLogger.login_online("331025680", "-1", "1战士", true, 12, "192.168.12.15", "0", "");
//		statLogger.logout("331025680", false, "20", 650);
		
		statLogger.verify_passwd("47159775", "106.235.12.11", "innermedia.taomee.seer.topbar");
		Thread.sleep(20000);
		statLogger.reg_role("47159775", "1383019208", "魔法师"," 61.155.182.56","innermedia.taomee.mole.banner");
		Thread.sleep(20000);
		statLogger.login_online("47159775", "1383019208", "魔法师", true, 15, "115.12.116.57", "", "网通一区");
		Thread.sleep(20000);
		statLogger.logout("47159775", true, "13", 3204);
		Thread.sleep(20000);
		statLogger.online_count(35, "");
		Thread.sleep(20000);
		statLogger.level_up("47159775","",20);
		Thread.sleep(20000);
		statLogger.pay("47159775", true, 2000, CurrencyType.ccy_mibi, PayReason.pay_buy, "320001", 2, "1");
		Thread.sleep(20000);
		statLogger.obtain_golds("47159876", 10);
		Thread.sleep(20000);
		statLogger.buy_item("34159876", true, 13, 20, "元旦礼包", 10);
		Thread.sleep(20000);
		statLogger.use_golds("47169879", true, "_开启新功能_", 18, 17);
		Thread.sleep(20000);
		statLogger.accept_task(TaskType.task_newbie, "3781654", "打开背包", 20);
		Thread.sleep(20000);
		statLogger.finish_task(TaskType.task_newbie, "3781654", "打开背包", 18);
		Thread.sleep(20000);
		statLogger.abort_task(TaskType.task_newbie, "3781654", "打开背包", 19);
		Thread.sleep(20000);
		statLogger.obtain_spirit("47159775",false,20,"小火猴");
		Thread.sleep(20000);
		statLogger.lose_spirit("17459775", false, 25, "小火猴");
		Thread.sleep(20000);
		statLogger.unsubscribe("342352345", UnsubscribeChannel.uc_duanxin);
		Thread.sleep(20000);
		statLogger.unsubscribe("342352345", UnsubscribeChannel.uc_mibi);
		Thread.sleep(20000);
		statLogger.cancel_acct("342352345", "zhifubao");
		Thread.sleep(20000);
		statLogger.new_trans(NewTransStep.bgetloginreq, "342352345");
		Thread.sleep(20000);
		
		
	}

}
