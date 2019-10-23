package com.taomee.bigdata.ads;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashSet;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

public class SourceVIPMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private HashSet<Integer> expectGame = new HashSet<Integer>();
    private HashSet<Integer> ignoreChan = new HashSet<Integer>();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        //-D channel=32:1;34:2;36:5;42:6;74:10;145:10;144:16
        String c = job.get("channel");
        if(c == null) {
            throw new RuntimeException("channel not configured");
        }
        String items[] = c.split(";");
        for(int i=0; i<items.length; i++) {
            String channel[] = items[i].split(":");
            expectGame.add(Integer.valueOf(channel[1]));
        }
        //-D vipchannel=90;91;99;100
        c = job.get("vipchannel");
        if(c == null) {
            throw new RuntimeException("vipchannel not configured");
        }
        items = c.split(";");
        for(int i=0; i<items.length; i++) {
            ignoreChan.add(Integer.valueOf(items[i]));
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        Integer time = Integer.valueOf(items[0]);
        //Integer uid  = Integer.valueOf(items[1]);
        String uid  = items[1];
        Integer game = Integer.valueOf(items[2]);   
        Integer chan = Integer.valueOf(items[3]);   //渠道
        Integer type = Integer.valueOf(items[4]);   //操作类型：0未知，1新增VIP，2续费VIP， 3清理VIP，4减少VIP时间，5取消自动取费，6短信退订
        Integer len  = Integer.valueOf(items[5]);   //操作天数
		Integer end_time  = Integer.valueOf(items[7]);   //上一次vip结束时间
		Integer vip_type  = Integer.valueOf(items[9]);   //VIP类型

		String gameinfo = getGameinfo.getValue(game.toString());
        if(type == 1 || type == 2) {
            if(expectGame.contains(game) &&
                    !ignoreChan.contains(chan)) {
                Integer cost = calcValue(game, len);
                //type == 2 && chan == 18  //短信续费
                if(type == 2 && chan == 18) {
                    outputKey.set(getMsremainLogger(game, uid, time, cost));
                    mos.getCollector("msremain" + gameinfo, reporter).collect(outputKey, outputValue);
                    //mos.getCollector("msremain", reporter).collect(outputKey, outputValue);
                }
                outputKey.set(getAcpayLogger("_vipmonth_", game, uid, time, chan, cost, type));
                mos.getCollector("acpay" + gameinfo, reporter).collect(outputKey, outputValue);
                //mos.getCollector("acpay", reporter).collect(outputKey, outputValue);
            }
        }
        if((type == 1 || type == 2) && vip_type == 3) {
            //年费VIP
            if(expectGame.contains(game) && !(ignoreChan.contains(chan) || chan == 18)) {
				Integer cost = calcValue(game, len);
				outputKey.set(getVipLogger("_yearvip_", "_yearvip_", game, uid, time, cost, len));
				mos.getCollector("yearvip" + gameinfo, reporter).collect(outputKey, outputValue);
            }   
        }   
        if(type == 1) {
            //新增VIP
            if(expectGame.contains(game) && !(ignoreChan.contains(chan) || chan == 18)) {
				Integer cost = calcValue(game, len);
				outputKey.set(getVipLogger("_newvip_", "_newvip_", game, uid, time, cost, len));
				mos.getCollector("newvip" + gameinfo, reporter).collect(outputKey, outputValue);
                //新增年费VIP
                if(vip_type == 3){ 
					outputKey.set(getVipLogger("_newvip_", "_yearvip_", game, uid, time, cost, len));
					mos.getCollector("newvip" + gameinfo, reporter).collect(outputKey, outputValue);
                }  
			    //首次新增VIP	
                if(end_time == 0){ 
					outputKey.set(getVipLogger("_newvip_", "_firstnewvip_", game, uid, time, cost, len));
					mos.getCollector("newvip" + gameinfo, reporter).collect(outputKey, outputValue);
                }   
            }   
        }   
        else if(type == 2) {
            //续费VIP
            if(expectGame.contains(game) && !(ignoreChan.contains(chan) || chan == 18)) {
				Integer cost = calcValue(game, len);
				outputKey.set(getVipLogger("_remainvip_", "_remainvip_", game, uid, time, cost, len));
				mos.getCollector("remainvip" + gameinfo, reporter).collect(outputKey, outputValue);
            }   
        }   
        else if(type == 3) {
            //销户VIP
            if(expectGame.contains(game) && !(ignoreChan.contains(chan) || chan == 18)) {
				Integer cost = 0;
				outputKey.set(getVipLogger("_closevip_", "_closevip_", game, uid, time, cost, len));
				mos.getCollector("closevip" + gameinfo, reporter).collect(outputKey, outputValue);
                //销户年费VIP
                if(vip_type == 3){
					outputKey.set(getVipLogger("_closevip_", "_yearvip_", game, uid, time, cost, len));
					mos.getCollector("closevip" + gameinfo, reporter).collect(outputKey, outputValue);
                }
            }
        }
        else if(type == 5 || type == 6) {
            //退订VIP
            if(expectGame.contains(game) && !(ignoreChan.contains(chan) || chan == 18)) {
				Integer cost = 0;
				outputKey.set(getVipLogger("_unsubvip_", "_unsubvip_", game, uid, time, cost, len));
				mos.getCollector("unsubvip" + gameinfo, reporter).collect(outputKey, outputValue);
            }
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    protected static String getAcpayLogger(String sstid, Integer game, String uid, Integer time, Integer chan, Integer cost, Integer type) {
        //hip_=192.168.1.61      _stid_=_acpay_  _sstid_=_vipmonth_      _gid_=6 _zid_=-1        _sid_=-1        _pid_=-1        _ts_=1400586674 _acid_=575975054        _plid_=-1     _vip_=0 _amt_=1000      _ccy_=1 _paychannel_=16 _op_=sum:_amt_|item_sum:_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_
        if(type != 2 || chan != 18) {
            return String.format("_hip_=ads\t_stid_=_acpay_\t_sstid_=%s\t_gid_=%d\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=%d\t_acid_=%s\t_plid_=-1\t_vip_=0\t_amt_=%d\t_ccy_=1\t_paychannel_=%d\t_op_=sum:_amt_|item_sum:_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_",
                    sstid, game, time, uid, cost, chan);
        } else {
            return String.format("_hip_=ads\t_stid_=_acpay_\t_sstid_=%s\t_gid_=%d\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=%d\t_acid_=%s\t_plid_=-1\t_vip_=0\t_amt_=%d\t_ccy_=1\t_paychannel_=%d\t_type_=%d\t_op_=sum:_amt_|item_sum:_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_",
                    sstid, game, time, uid, cost, chan, type);
        }
    }

    protected static String getMsremainLogger(Integer game, String uid, Integer time, Integer cost) {
        return String.format("_hip_=ads\t_stid_=_msremain_\t_sstid_=_msremain_\t_gid_=%d\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=%d\t_acid_=%s\t_plid_=-1\t_amt_=%d\t_op_=sum:_amt_",
                game, time, uid, cost);
    }
	//tat迁移vip数据，重落统计项
    protected static String getVipLogger(String stid, String sstid, Integer game, String uid, Integer time, Integer cost, Integer len) {
        return String.format("_hip_=ads\t_stid_=%s\t_sstid_=%s\t_gid_=%d\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=%d\t_acid_=%s\t_plid_=-1\t_amt_=%d\t_len_=%d\t_op_=sum:_amt_|item:_len_|item_sum:_len_,_amt_",
                stid, sstid, game, time, uid, cost, len);
    }

    private Integer calcValue(int game, int len) {
        //【计算vip的花费】
        //Interval= time_length / 30
        //1.  热血精灵派不打折(gameid=16)   
        //Cost = Interval * 10 * 100 (单位为分)
        //2.  其他游戏
        //若Interval=6：   Cost=5000
        //若Interval=12：  Cost=10000
        //其他：   Cost = Interval * 10 * 100
        Integer value;
        if(len < 30)    return 0;
        len /= 30;
        if(game == 16 || game ==25) {
            value = len * 1000;
        } else if(game == 204){
			value = len * 2000;
		} else {
            if(len == 6) {
                value = 5000;
            } else if(len == 12) {
                value = 10000;
            } else {
                value = len * 1000;
            }
        }
        return value;
    }
}
