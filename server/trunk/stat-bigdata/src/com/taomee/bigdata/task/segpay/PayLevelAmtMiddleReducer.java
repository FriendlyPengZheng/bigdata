package com.taomee.bigdata.task.segpay;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * 
 * @author cheney
 * @date 2013-11-21
 */
public class PayLevelAmtMiddleReducer extends MRBase implements Reducer<Text, Text, Text, Text> {

	private Text outputValue = new Text();
	
	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		String[] vs;
		
		int level = 0;
		Double amt = 0.0;
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
		
		while(values.hasNext()) {
			
			vs = values.next().toString().split("\t");
			
			if("1".equals(vs[0])){
				
				amt += Double.parseDouble(vs[1]);
				
			} else if("2".equals(vs[0])) {
				
				if(level < Integer.parseInt(vs[1])) {
					level = Integer.parseInt(vs[1]);
				}
				
			}
			
		}
		
		if(amt > 0) { //输出有付费的级别
			outputValue.set(String.format("%d\t%s", level, amt));
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
			//output.collect(key, outputValue);
		}
		
	}

}
