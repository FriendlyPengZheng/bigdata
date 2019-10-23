package com.taomee.bigdata.task.vip3;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

/**
 * 计算当天当前vip的数量 ( 包含赠送的vip )
 * @author looper
 * @date 2016年12月28日
 */
public class TodayVipListReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>{
	
	/**
	 * 处理的数据格式
	 */
	  private Text outputValue = new Text();
	  private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	  private MultipleOutputs mos = null;

		public void configure(JobConf job) {
			mos = new MultipleOutputs(job);
			getGameinfo.config(job);
		}

		public void close() throws IOException {
			mos.close();
		}

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		Long time = 0L;
		Integer flag = 1;
        String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);		
        while(values.hasNext()) {
            String time_value = values.next().toString();
			String items[] = time_value.toString().split("\t");
			Long time_temp = Long.valueOf(items[0]);
			Integer flag_temp = Integer.valueOf(items[1]);
			if(time_temp>time) 
			{
				time = time_temp;
				flag = flag_temp;
			}
        }
		if(flag!=0)
		{
			String tt = String.valueOf(time);
			outputValue.set(tt);
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
		}
		
	}

}
