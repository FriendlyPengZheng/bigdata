package com.taomee.statlogger.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.taomee.statlogger.CurrencyType;
import com.taomee.statlogger.NewTransStep;
import com.taomee.statlogger.PayReason;
import com.taomee.statlogger.StatCommon;
import com.taomee.statlogger.StatLogger;
import com.taomee.statlogger.TaskType;
import com.taomee.statlogger.UnsubscribeChannel;

public class TestStatLogger2 {
	
	static final StatLogger statLogger=new StatLogger(652, -1, -1, -1, 1);
	
	@Test
	public void test_all() throws InterruptedException {
		long ts = System.currentTimeMillis() / 1000;
		//statLogger.write_basic_log("测试verify_passwd接口\n",ts);
		statLogger.verify_passwd("47159775", "106.235.12.11", "innermedia.taomee.seer.topbar");
		//statLogger.write_basic_log("测试reg_role接口\n",ts);
		statLogger.reg_role("47159775", "1383019208", "魔法师"," 61.155.182.56","innermedia.taomee.mole.banner");
		//statLogger.write_basic_log("测试login_online接口\n",ts);
		statLogger.login_online("47159775", "1383019208", "魔法师", true, 15, "115.12.116.57", "", "网通一区");
		//statLogger.write_basic_log("测试login_online接口\n",ts);
		statLogger.login_online("47159775",  "1383019208",  "",  true,  15,"115.12.116.57", "","网通一区");
		//statLogger.write_basic_log("测试logout接口\n",ts);
		statLogger.logout("47159775", true, "13", 3204);
		//statLogger.write_basic_log("测试level_up接口\n",ts);
		statLogger.level_up("47159775","",20);
		//statLogger.write_basic_log("测试pay接口\n",ts);
		statLogger.pay("47159775", true, 2000, CurrencyType.ccy_mibi, PayReason.pay_buy, "320001", 2, "1");
		//statLogger.write_basic_log("测试obtain_golds接口\n",ts);
		statLogger.obtain_golds("47159876", 10);
		//statLogger.write_basic_log("测试buy_item接口\n",ts);
		statLogger.buy_item("34159876", true, 13, 20, "元旦礼包", 10);
		//statLogger.write_basic_log("测试use_golds接口\n",ts);
		statLogger.use_golds("47169879", true, "_开启新功能_", 18, 17);
		//statLogger.write_basic_log("测试accept_task接口\n",ts);
		statLogger.accept_task(TaskType.task_newbie, "3781654", "打开背包", 20);
		//statLogger.write_basic_log("测试finish_task接口\n",ts);
		statLogger.finish_task(TaskType.task_newbie, "3781654", "打开背包", 18);
		//statLogger.write_basic_log("测试abort_task接口\n",ts);
		statLogger.abort_task(TaskType.task_newbie, "3781654", "打开背包", 19);
		//statLogger.write_basic_log("测试obtain_spirit接口\n",ts);
		statLogger.obtain_spirit("47159775",false,20,"小火猴");
		//statLogger.write_basic_log("测试lose_spirit接口\n",ts);
		statLogger.lose_spirit("17459775", false, 25, "小火猴");
		//statLogger.write_basic_log("测试unsubscribe接口\n",ts);
		statLogger.unsubscribe("342352345", UnsubscribeChannel.uc_duanxin);
		//statLogger.write_basic_log("测试unsubscribe接口\n",ts);
		statLogger.unsubscribe("342352345", UnsubscribeChannel.uc_mibi);
		//statLogger.write_basic_log("测试cancel_acct接口\n",ts);
		statLogger.cancel_acct("342352345", "zhifubao");
		//statLogger.write_basic_log("测试new_trans接口\n",ts);
		statLogger.new_trans(NewTransStep.bgetloginreq, "342352345");
	}
	
//	@Test
//	public void test_is_valid_ip(){
//		assertTrue(statLogger.is_valid_ip("192.168.0.1"));
//	}
	
}
