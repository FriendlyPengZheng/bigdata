package com.taomee.bigdata.itemsPlus;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.GetGameinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.lang.Double;

public class ItemPlusReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
	private GetGameinfo getGameinfo  = GetGameinfo.getInstance();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
		getGameinfo.config(job);
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        //game platform zone server uid | this sstid vip item itmcnt golds cnt
        //game platform zone server uid | new
        //game platform zone server uid | active
        //game platform zone server uid | back
        
        //[new,active,back,this sstid vip item itmcnt golds cnt,...]
		String gameinfo = getGameinfo.getValue(key.toString().split("\t")[0]);
        boolean isNew = false;
        boolean isActive = false;
        boolean iitemSBufferack = false;
        List<StringBuffer> itemList = new ArrayList<StringBuffer>();
        
        while(values.hasNext()) {
        	String str = values.next().toString();
        	StringBuffer value = new StringBuffer(str);
        	String item[] = str.split("\t");
            
            if(item.length == 7){//this sstid vip item itmcnt golds cnt
            	itemList.add(value);
            }else if(item.length == 1){//new/active/back
            	if("new".equals(item[0])){
            		isNew = true;
            	}
            	if("active".equals(item[0])){
            		isActive = true;
            	}
            	if("back".equals(item[0])){
            		iitemSBufferack = true;
            	}
            }else{
            	r.setCode("E_ITEM_PLUSMREDUCE_SPLIT", String.format("%d != 1 or 6", item.length));
                return;
            }
        }
      /* this sstid vip item itmcnt golds cnt  ==> 
       * 		this sstid vip item itmcnt golds cnt new_cnt active_cnt back_cnt
       */
        for(StringBuffer itemSBuffer : itemList){
        	String[] strs = itemSBuffer.toString().split("\t");
        	if("this".equals(strs[0])){
        		//加一列记录新增用户人次
        		if(isNew == true){
        			itemSBuffer.append(String.format("\t%s", strs[6]));
        		}else{
        			itemSBuffer.append(String.format("\t%d", 0));
        		}
        		//加一列记录活跃用户人次
        		if(isActive == true){
        			itemSBuffer.append(String.format("\t%s", strs[6]));
        		}else{
        			itemSBuffer.append(String.format("\t%d", 0));
        		}
        		//加一列记录回流用户人次
        		if(iitemSBufferack == true){
        			itemSBuffer.append(String.format("\t%s", strs[6]));
        		}else{
        			itemSBuffer.append(String.format("\t%d", 0));
        		}
        		String[] items = itemSBuffer.toString().split("\t");
        		
                outputValue.set(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%d", 
                		items[1], items[2], items[3], items[4], items[5], items[6], Integer.parseInt(items[7]), Integer.parseInt(items[8]), Integer.parseInt(items[9])));
                
                mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
                
        	}else{
        		r.setCode("E_ITEM_PLUSMREDUCE_SPLIT", String.format("%s should be this", strs[0]));
                return;
        	}
        	
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        mos.close();
    }
}
