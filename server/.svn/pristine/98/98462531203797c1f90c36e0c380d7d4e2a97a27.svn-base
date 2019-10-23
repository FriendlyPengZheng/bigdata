#!/usr/bin/env node
/**
 * Created by looper on 2017/01/10.
 */
var statlogger = require("./statLogger.js");
var statInfo=require("./statInfo.js");
//var OpCode=require("../com.taomee.sdk/opCode.js");
//var statlogger2 = require("../com.taomee.sdk/statLogger2.js");
//var statlogger2=statlogger;
var d = new statlogger(10657, 1, -1, 2);
//d.initClass(652,1,1,1);
//d.modifyGid(10);
//d.modifyZid(3);
//d.modifySid(2);
d.modifyPath("/opt/taomee/stat/nodejs/data");
//var d2= new statlogger(651, 2, -1, 2);

 // d.online_count(10,"2平台1区");
 // d2.online_count(100,"2平台2区");
 //
 
 //测试接口


 //1、测试验证密码接口:
  d.verify_passwd("47159775", "10.1.1.225", "innermedia.taomee.seer.topbar");
	
 //2、注册角色(reg_role)
 //// d.reg_role('331025680',"22","",'192.168.11.120','4399');
   // d.reg_role('331025681',"","",'192.168.11.120',"");
  d.reg_role('331025682',"","","","");
 
 //3、登录(login_online),不传ads的数值，可以获取默认值-1落到日志中去。
  d.login_online('331025680',"331025680","Groller",true,1,'10.1.1.63','4399',"");

 //4、登出(logout)
  d.logout("331025680",true,0,159.84100008010864);
  //d.logout("331025680",true,0);

 //5、在线人数()
  d.online_count(10,"3服");
  
 //6、用户升级(level_up)
  d.level_up("47159775","jsks",20);
 
 //7、付费(pay)
  d.pay("331025", true, 1000,statTM.CurrencyType.CCY_CNY, statTM.PayReason.PAY_BUY, "兑换游戏CM点", 100, "2"); //该账户是vip用人民币兑换游戏CM100点
    //d.pay("331025", true, 1000,CurrencyType.CCY_CNY, PayReason.PAY_VIP, "1个月vip", 1, "2"); //该账户是vip，用人民币购买vip一个月
    //d.pay("331025", true, 6800,CurrencyType.CCY_CNY, PayReason.PAY_BUY, "购买钻石", 680, "2");//该账户是vip，用人民币在游戏内部兑换钻石68个,暂设置游戏内，人民币与钻石兑换的比例是1:10,游戏需要根据自己的兑换比例，设置这个值

 //8、免费获得游戏币(obtain_golds)
  d.obtain_golds("47159876", 0.1);//例如该账户在s计划游戏当中完成某一个任务，系统给其赠送10个金币,但是这个好像最后落下来的golds是1000，在初始化设置乘以10的标识，这个后面和游戏那边再确认下
   
//9、使用游戏币购买道具(buy_item)
  d.buy_item("34159876", true, 13, 200, "元旦礼包", 10);//该账户是vip，在13级的时候在游戏内购买10个元旦礼包，一共用了200金币,该接口和上面的免费获得游戏币的情况一致，注意:接口内部乘以了100，这个接口后面也得和游戏商量下。

//10、消耗游戏币(use_golds)
  d.use_golds("47169879", true, "购买新春礼包卡", 300, 20);//该用户是vip，在20级的时候，购买新春礼包卡，花了300游戏币,该接口也存在金币数内部乘以100的情况。

//11、接受任务(accept_task)
  d.accept_task(statTM.TaskType.TASK_NEWBIE,"3781654","新手任务1",10);
     //d.accept_task(TaskType.TASK_STORY,"3781654","主线任务1",10);
     //d.accept_task(TaskType.TASK_SUPPLEMENT,"3781654","支线任务1",10);

//12、完成任务(finish_task)
  d.finish_task(statTM.TaskType.TASK_NEWBIE,"3781654", "新手任务2", 18);
     //d.finish_task(TaskType.TASK_STORY,"3781654", "主线任务2", 18);
     //d.finish_task(TaskType.TASK_SUPPLEMENT,"3781654", "支线任务2", 18);

//13、放弃任务(abort_task)
  d.abort_task(statTM.TaskType.TASK_NEWBIE, "3781654", "新手任务3", 16);
     //d.abort_task(TaskType.TASK_STORY, "3781654", "主线任务3", 16);
     //d.abort_task(TaskType.TASK_SUPPLEMENT, "3781654", "支线任务3", 16);

//14、获得精灵(obtain_spirit)
  d.obtain_spirit("331025",true,20,'狮子狗');

//15、失去精灵(lose_spirit)
  d.lose_spirit("331025",true,30,"德莱文");

//16、退订VIP服务(unsubscribe)
  d.unsubscribe("331025682",statTM.UnsubscribeChannel.UC_MIBI);//米币
     //d.unsubscribe("331025682",UnsubscribeChannel.UC_DUANXIN);//短信


//17、销户VIP(cancle_acct)
  d.cancel_acct("342352345", "weixin");
 
//18、新用户注册转换(new_trans)
     //d.new_trans(NewTransStep.NW_BEGIN,"342352345");非法
  d.new_trans(statTM.NewTransStep.fGetRegSucc,"342352345");
     //d.new_trans(NewTransStep.bSendNewroleSucc,"342352345");
     //d.new_trans(NewTransStep.fInterGameSucc,"342352345");
     //d.new_trans(NewTransStep.NW_END,"342352345");//非法

//19、自定义统计项(log)
    /**  var info = new statInfo();
      info.add_info("k1", 10);
      info.add_op(OpCode.OP_SUM, "k1","");
      info.add_info("k2", 100);
      info.add_op(OpCode.OP_SUM, "k2","");
      d.log("经济系统", "钻石消耗途径", "8344342333", "-1",info);
*/
      var info = new statInfo();
       //info.add_info(1500,1)
       //info.add_op(statTM.OpCode.OP_SUM,1500);
       //d.log("经济系统", "钻石消耗途径", "8344342333", "-1",info);
       info.add_info("k1",10);
       info.add_info("k2", 100);
       info.add_op(statTM.OpCode.OP_ITEM_MAX, "k1","k2");
       info.add_op(statTM.OpCode.OP_SUM, "k2","");
       d.log("经济系统", "钻石消耗途径", "8344342333", "-1",info);
     // d.log("经济系统", "钻石消耗途径", "8344342333", "-1");
      //statCommon.stat_trim_underscore(10);

