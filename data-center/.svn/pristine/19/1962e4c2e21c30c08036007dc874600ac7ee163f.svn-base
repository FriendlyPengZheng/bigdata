package com.taomee.bigdata.task.regadkeep;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

public class RegAdSecondRegCountReducer extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable>{

	private DoubleWritable outputValue = new DoubleWritable();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	// private List<String> ads = new ArrayList<String>();

	// private String ad = new String();
	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}

	@Override
	public void reduce(Text key, Iterator<DoubleWritable> values,
			OutputCollector<Text, DoubleWritable> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
		Double yearsTodayNewRegCount = 0d;
		Double yearsTodayNewRegTodayLoginCount = 0d;
		//DecimalFormat df = new DecimalFormat("######0.00");  
		while (values.hasNext()) {
			String val = values.next().toString();
			//System.out.println("val:" + val);
			if (val.equals("0.0")) {
				yearsTodayNewRegCount++;
			} else {
				yearsTodayNewRegTodayLoginCount++;
			}
		}
		//System.out.println("reg:"+yearsTodayNewRegCount+",Login:"+yearsTodayNewRegTodayLoginCount+",rate:"+(yearsTodayNewRegTodayLoginCount/yearsTodayNewRegCount));
		//System.out.println();
		if (yearsTodayNewRegCount == 0.0) {
			return;
		} else {
			outputValue.set(yearsTodayNewRegTodayLoginCount);
			mos.getCollector("part" + gameinfo, reporter).collect(key,
					outputValue);
			outputValue.set(yearsTodayNewRegCount);
			mos.getCollector("part" + gameinfo, reporter).collect(key,
					outputValue);
		}

	}

}
