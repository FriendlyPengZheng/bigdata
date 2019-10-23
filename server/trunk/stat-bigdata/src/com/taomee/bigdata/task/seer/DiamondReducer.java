package com.taomee.bigdata.task.seer;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashMap;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

public class DiamondReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private final static int GET_VIP    = 1;
    private final static int LOST_VIP   = 2;
    private final static int PAY        = 3;
    private final static int SEND       = 4;
    private final static int BUYITEM    = 5;

    public class ActiveInfo {
        //public int type = key%10; //1,2,3,4,5
        public int itemId;
        public int itemCnt;
        public int golds;

        public ActiveInfo(int id, int cnt, int g) {
            itemId = id;
            itemCnt = cnt;
            golds = g;
        }
    }

    public class ItemInfo {
        public int golds;
        public int itemCnt;

        public ItemInfo() {
            golds = 0;
            itemCnt = 0;
        }

        public ItemInfo add(int g, int c) {
            golds += g;
            itemCnt += c;
            return this;
        }
    }

    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
	private Reporter reporter;
    private MultipleOutputs mos = null;
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private TreeMap<Integer, ActiveInfo> activeInfoMap = new TreeMap<Integer, ActiveInfo>();
    private HashMap<String, HashMap<Integer, ItemInfo>> buyItemInfo = new HashMap<String, HashMap<Integer, ItemInfo>>();
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
		mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        boolean isvip = false;
        boolean ispay = false;
        int diamonedRemain = 0;
        Integer time;
        activeInfoMap.clear();
        buyItemInfo.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String items[] = values.next().toString().split("\t");
            int type = Integer.valueOf(items[0]);
            switch(type) {
                case 0://获得VIP:
                    time = (Integer.valueOf(items[1]) - 1300000000) * 10 + GET_VIP;
                    activeInfoMap.put(time, null);
                    break;
                case 1://失去VIP:
                    time = (Integer.valueOf(items[1]) - 1300000000) * 10 + LOST_VIP;
                    activeInfoMap.put(time, null);
                    break;
                case 2://购买钻石，变成付费用户
                    time = (Integer.valueOf(items[1]) - 1300000000) * 10 + PAY;
                    activeInfoMap.put(time, new ActiveInfo(0, 0, Integer.valueOf(items[2])));
                    break;
                case 3://赠送钻石
                    time = (Integer.valueOf(items[1]) - 1300000000) * 10 + SEND;
                    activeInfoMap.put(time, new ActiveInfo(0, 0, Integer.valueOf(items[2])));
                    break;
                case 4://购买道具，消耗钻石
                    time = (Integer.valueOf(items[1]) - 1300000000) * 10 + BUYITEM;
                    activeInfoMap.put(time, new ActiveInfo(Integer.valueOf(items[3]), Integer.valueOf(items[4]), Integer.valueOf(items[2])));
                    break;
                case 5://钻石库存，是否付费用户
                    diamonedRemain = Integer.valueOf(items[1]);
                    ispay = Integer.valueOf(items[2]) == 1 ? true : false;
                    break;
                case 6://历史VIP
                    isvip = true;
                    break;
                default:
            }
        }

        Iterator<Integer> it = activeInfoMap.keySet().iterator();
        String userType = null;
        HashMap<Integer, ItemInfo> buyitems = null;
        int itemId;
        ItemInfo itemInfo = null;
        while(it.hasNext()) {
            time = it.next();
            int type = time % 10;
            switch(type) {
                case GET_VIP   : 
                    isvip = true;
                    break;
                case LOST_VIP  : 
                    isvip = false;
                    break;
                case PAY       : 
                    ispay = true;
                    diamonedRemain += activeInfoMap.get(time).golds;
                    break;
                case SEND      :
                    diamonedRemain += activeInfoMap.get(time).golds;
                    break;
                case BUYITEM   :
                    userType = getKey(isvip, ispay);
                    buyitems = buyItemInfo.get(userType);
                    if(buyitems == null) {
                        buyitems = new HashMap<Integer, ItemInfo>();
                    }
                    itemId = activeInfoMap.get(time).itemId;
                    itemInfo = buyitems.get(itemId);
                    if(itemInfo == null) {
                        itemInfo = new ItemInfo();
                    }
                    buyitems.put(itemId, itemInfo.add(activeInfoMap.get(time).golds, activeInfoMap.get(time).itemCnt));
                    buyItemInfo.put(userType, buyitems);
                    diamonedRemain -= activeInfoMap.get(time).golds;
                    break;
            }
        }
        outputKey.set(String.format("1\t每日钻石库存\t%s", ispay ? "付费用户" : "免费用户"));//1:钻石库存
        outputValue.set(String.format("%d", diamonedRemain / 100));
		mos.getCollector("part" + gameinfo, reporter).collect(outputKey, outputValue);

        outputValue.set(String.format("%d\t%d", diamonedRemain, ispay ? 1 : 0));
        mos.getCollector("remain" + gameinfo, reporter).collect(key, outputValue);
        Iterator<String> itType = buyItemInfo.keySet().iterator();
        while(itType.hasNext()) {
            userType = itType.next();
            buyitems = buyItemInfo.get(userType);
            it = buyitems.keySet().iterator();
            int totalGolds = 0;
            while(it.hasNext()) {
                itemId = it.next();
                itemInfo = buyitems.get(itemId);
                totalGolds += itemInfo.golds;
                outputKey.set(String.format("2\t商品购买\t%s\t%d", userType, itemId));//2:商品购买人数，数量
                outputValue.set(String.format("%d", itemInfo.itemCnt));
				mos.getCollector("part" + gameinfo, reporter).collect(outputKey, outputValue);
            }
            outputKey.set(String.format("3\t每日钻石消耗\t%s", ispay ? "付费用户" : "免费用户"));//3:钻石消耗
            outputValue.set(String.format("%d", totalGolds/100));
			mos.getCollector("part" + gameinfo, reporter).collect(outputKey, outputValue);
        }
    }

    private String getKey(boolean isvip, boolean ispay) {
        String key = isvip ? "超No且" : "普No且";
        key += (ispay ? "付费用户" : "免费用户");
        return key;
    }
}
