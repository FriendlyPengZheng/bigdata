package com.taomee.bigdata.task.channel;

import java.io.IOException;
import java.util.HashSet;
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
 * 
 * @author looper
 * @date 2017年2月15日 下午4:24:34
 * @project Tongji_version2 TodayLoginUserChannelInfoReducer
 */
public class TodayLoginUserChannelInfoReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {
	/**
	 * 输入格式 1: key = 657 -1 -1 -1 1000643-1000643 ad , value = 0 表示某一天新增的用户
	 * 输入格式2: key = 657 -1 -1 -1 1000643-1000643 ad , value = 1/7/14
	 * 表示当天的用户在前些天的活跃
	 */

	private Text outputValue = new Text();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	private HashSet<String> values = new HashSet<String>();
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
		String i;
		boolean need = false;
		boolean doubleSize = false;
		int size = 0;
		this.values.clear();
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
		while (values.hasNext()) {
			// boolean need = false;
			i = values.next().toString();
			if (i.equals("0")) {   //表示前一天的新增用户
				need = true;
				this.values.add(i);
			} else {            //表示当天的登录用户
				this.values.add(i);
			}
		}
		size = this.values.size();
		if (size >= 2) {
			doubleSize = true;
		}
		//System.out.println("doubleSize:"+doubleSize+"\t"+"need:"+need);
		if ((doubleSize && need)) { // 
			String s;
			Iterator<String> it = this.values.iterator();
			while (it.hasNext()) {
				s = it.next().toString();
				//System.out.println("s:" + s);
				//if (!s.equals("0")) {
					outputValue.set(s);
					mos.getCollector("part" + gameinfo, reporter).collect(key,
							outputValue);
				//}
			}
		} else if ((!doubleSize)) {
			Iterator<String> it = this.values.iterator();
			String s;
			while (it.hasNext()) {
				s = it.next().toString();
				if(s.equals("0"))
				{
				outputValue.set(s);
				mos.getCollector("part" + gameinfo, reporter).collect(key,
						outputValue);
				}

			}
		}
	}

}
