package com.taomee.bigdata.task.regadkeep;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

/**
 * 输入格式：657 -1 -1 -1 1067763 0/1 
 * 输出格式:657 -1 -1 -1 1067763 0/1 ad
 * 
 * @author looper
 * @date 2017年4月11日
 */
public class RegAdReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {
	private Text outputValue = new Text();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;
	//private List<String> ads = new ArrayList<String>();

	// private String ad = new String();
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
		
		List<String> ads = new ArrayList<String>();
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
		while (values.hasNext()) {
			//count++;
			ads.add(values.next().toString());
		}
			
		if (ads.size() == 2) {
			for (String e : ads) {
				if (!e.equals("0")) {
					// adoutputValue.set(e);
					// ad = e;
					outputValue.set(String.format("%s\t%s", "0", e));
					mos.getCollector("part" + gameinfo, reporter).collect(key,
							outputValue);
				}
			}

		}
		if (ads.size() == 3) {
			for (String e : ads) {
				if (!e.equals("0") && !e.equals("1")) {
					outputValue.set(String.format("%s\t%s", "0", e));
					mos.getCollector("part" + gameinfo, reporter).collect(key,
							outputValue);
					outputValue.set(String.format("%s\t%s", "1", e));
					mos.getCollector("part" + gameinfo, reporter).collect(key,
							outputValue);
				}
			}

		}

	}

}
