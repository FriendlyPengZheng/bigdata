package com.taomee.bigdata.ads;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

public class SourceMBMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private HashMap<Integer, Integer> channelMap = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> itemMap = new HashMap<Integer, Integer>();
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
            channelMap.put(Integer.valueOf(channel[0]), Integer.valueOf(channel[1]));
        }

        //-D item=100001:199999;200000:299999;340000:349999;320000:329999;510000:519999;334360:334369
        c = job.get("item");
        if(c == null) {
            throw new RuntimeException("item not configured");
        }
        items = c.split(";");
        for(int i=0; i<items.length; i++) {
            String range[] = items[i].split(":");
            itemMap.put(Integer.valueOf(range[0]), Integer.valueOf(range[1]));
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String items[] = value.toString().split("\t");
        Integer time = Integer.valueOf(items[0]);
        Integer uid  = Integer.valueOf(items[2]);   //米米号取收到物品的米米号
        Integer chan = Integer.valueOf(items[3]);   //渠道id，每个游戏对应一个渠道，按条收入只需要关心这一个渠道
        Integer prod = Integer.valueOf(items[4]);   //商品id
        Integer cnt  = Integer.valueOf(items[5]);   //商品数量
        Integer cost = Integer.valueOf(items[6]);   //消耗为负数
        Integer game;

        if(cost < 0) { //按条收入
            if((game = channelMap.get(chan)) != null
                    && isCareItem(prod)) {
				String gameinfo = getGameinfo.getValue(game.toString());
                outputKey.set(SourceVIPMapper.getAcpayLogger("_buyitem_", game, uid, time, chan, -cost, 0));
				mos.getCollector("acpay" + gameinfo, reporter).collect(outputKey, outputValue);
                //mos.getCollector("acpay", reporter).collect(outputKey, outputValue);
                outputKey.set(getItemLogger(game, uid, time, -cost, prod, cnt));
                mos.getCollector("buyitem" + gameinfo, reporter).collect(outputKey, outputValue);
                //mos.getCollector("buyitem", reporter).collect(outputKey, outputValue);
            }
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    private String getItemLogger(Integer game, Integer uid, Integer time, Integer cost, Integer prod, Integer cnt) {
        //_hip_=192.168.32.77     _stid_=_buyitem_        _sstid_=_mibiitem_      _gid_=6 _zid_=0 _sid_=-1        _pid_=-1        _ts_=1400586693 _acid_=609991577        _plid_=-1     _isvip_=0       _item_=320001   _itmcnt_=1      _golds_=100     _lv_=0  _op_=sum:_golds_
        return String.format("_hip_=ads\t_stid_=_buyitem_\t_sstid_=_mibiitem_\t_gid_=%d\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=%d\t_acid_=%d\t_plid_=-1\t_isvip_=0\t_item_=%d\t_itmcnt_=%d\t_golds_=%d\t_lv_=0\t_op_=sum:_golds_",
                game, time, uid, prod, cnt, cost);
    }

    private boolean isCareItem(int item) {
        Iterator<Integer> it = itemMap.keySet().iterator();
        while(it.hasNext()) {
            int start = it.next();
            int end = itemMap.get(start);
            if(start <= item && item <= end)    return true;
        }
        return false;
    }
}

