package com.taomee.bigdata.itemsPlus;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.IOException;

public class ItemPlusSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        //game platform zone server uid sstid vip item itmcnt golds cnt new_cnt active_cnt back_cnt
        String items[] = value.toString().split("\t");
        if(items.length != 14) {
            r.setCode("E_ITEM_SUMMAPPER_SPLIT", String.format("%d != 14", items.length));
            return;
        }
        String valueStr = String.format("%s\t%s\t%s\t1",items[8], items[9], items[10]);
        
        //加入new_cnt new_ucount
        if(Integer.parseInt(items[11]) != 0){
        	valueStr = String.format(valueStr+"\t%s\t%d", items[11],1);
        }else{
        	valueStr = String.format(valueStr+"\t%s\t%d", items[11],0);
        }
        //加入active_cnt active_ucount
        if(Integer.parseInt(items[12]) != 0){
        	valueStr = String.format(valueStr+"\t%s\t%d", items[12],1);
        }else{
        	valueStr = String.format(valueStr+"\t%s\t%d", items[12],0);
        }
        //加入back_cnt back_ucount
        if(Integer.parseInt(items[13]) != 0){
        	valueStr = String.format(valueStr+"\t%s\t%d", items[13],1);
        }else{
        	valueStr = String.format(valueStr+"\t%s\t%d", items[13],0);
        }
        
        /*	sstid game platform zone server item vip 
         *	itmcnt golds cnt ucount new_cnt new_ucount active_cnt active_ucount back_cnt back_ucount
         */
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s",
                    items[5],items[0], items[1], items[2], items[3], items[7], items[6]));
        
        outputValue.set(valueStr);
        output.collect(outputKey, outputValue);
    }

}
