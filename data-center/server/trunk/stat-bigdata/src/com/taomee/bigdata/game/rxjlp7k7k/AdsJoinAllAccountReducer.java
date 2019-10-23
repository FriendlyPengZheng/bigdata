package com.taomee.bigdata.game.rxjlp7k7k;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class AdsJoinAllAccountReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>{
	private HashSet<String> AdsMbStringSet;
	private HashSet<String> AdsVipStringSet;
	
	@Override
	public void configure(JobConf job) {
		this.AdsMbStringSet = new HashSet<String>();
		this.AdsVipStringSet = new HashSet<String>();
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, NullWritable> output, Reporter reporter)
			throws IOException {
		boolean hasFlag1 = false;
		boolean hasFlag2 = false;
		boolean hasFlag3 = false;
		this.AdsMbStringSet.clear();
		this.AdsVipStringSet.clear();
		while(values.hasNext()){
			String value = values.next().toString();
			String[] items = value.split(",");
			if(items[0].equals("1")){
				hasFlag1 = true;
			}else if(items[0].equals("2")){
				if(items.length != 2)return;
				hasFlag2 = true;
				this.AdsMbStringSet.add(items[1]);
			}else if(items[0].equals("3")){
				if(items.length != 2)return;
				hasFlag3 = true;
				this.AdsVipStringSet.add(items[1]);
			}
		}
		if(hasFlag1 && hasFlag2){
			for(String adsMbString:AdsMbStringSet){
				output.collect(new Text(convertAdsMbString(adsMbString)),NullWritable.get());
			}
		}
		if(hasFlag1 && hasFlag3){
			for(String adsVipString:AdsVipStringSet){
				String convertedVipString = convertAdsVipString(adsVipString);
				if(!convertedVipString.equals("")){
					output.collect(new Text(convertedVipString),NullWritable.get());
				}
			}
		}
	}
	
	public String convertAdsMbString(String adsMbString) {
		String[] items = adsMbString.split("\t");
        Integer time = Integer.valueOf(items[0]);
        Integer uid  = Integer.valueOf(items[2]);   //米米号取收到物品的米米号
        Integer chan = Integer.valueOf(items[3]);   //渠道id，每个游戏对应一个渠道，按条收入只需要关心这一个渠道
        Integer cost = Integer.valueOf(items[6]);   //消耗为负数
		String line1 = getAcpayLogger("_acpay_", 661, uid, time, chan, -cost, 0);
		String line2 = getAcpayLogger("_buyitem_", 661, uid, time, chan, -cost, 0);
		return line1+"\n"+line2;
	}

	public String convertAdsVipString(String adsVipString) {
		String[] items = adsVipString.split("\t");
		Integer time = Integer.valueOf(items[0]);
        Integer uid  = Integer.valueOf(items[1]);
        Integer game = Integer.valueOf(items[2]);   
        Integer chan = Integer.valueOf(items[3]);   //渠道
        Integer type = Integer.valueOf(items[4]);   //操作类型：0未知，1新增VIP，2续费VIP， 3清理VIP，4减少VIP时间，5取消自动取费，6短信退订
        Integer len  = Integer.valueOf(items[5]);   //操作天数
        if(calcValue(game, len)<=0){
        	return "";
        }
		String line1 = getAcpayLogger("_acpay_", 661, uid, time, chan, calcValue(game, len), type);
		String line2 = getAcpayLogger("_vipmonth_", 661, uid, time, chan, calcValue(game, len), type);
		return line1+"\n"+line2;
	}

	public String getAcpayLogger(String sstid, Integer game, Integer uid, Integer time, Integer chan, Integer cost, Integer type) {
        //hip_=192.168.1.61      _stid_=_acpay_  _sstid_=_vipmonth_      _gid_=6 _zid_=-1        _sid_=-1        _pid_=-1        _ts_=1400586674 _acid_=575975054        _plid_=-1     _vip_=0 _amt_=1000      _ccy_=1 _paychannel_=16 _op_=sum:_amt_|item_sum:_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_
        if(type != 2 || chan != 18) {
            return String.format("_hip_=ads\t_stid_=_acpay_\t_sstid_=%s\t_gid_=%d\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=%d\t_acid_=%d\t_plid_=-1\t_vip_=0\t_amt_=%d\t_ccy_=1\t_paychannel_=%d\t_op_=sum:_amt_|item_sum:_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_",
                    sstid, game, time, uid, cost, chan);
        } else {
            return String.format("_hip_=ads\t_stid_=_acpay_\t_sstid_=%s\t_gid_=%d\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=%d\t_acid_=%d\t_plid_=-1\t_vip_=0\t_amt_=%d\t_ccy_=1\t_paychannel_=%d\t_type_=%d\t_op_=sum:_amt_|item_sum:_vip_,_amt_|item_sum:_paychannel_,_amt_|item_sum:_ccy_,_amt_|item:_paychannel_",
                    sstid, game, time, uid, cost, chan, type);
        }
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
        if(game == 16) {
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
