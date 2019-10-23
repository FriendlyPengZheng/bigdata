package com.taomee.bigdata.task.segpay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * 
 * @author cheney
 * @date 2013-11-21
 */
public class PayLevelAmtReducer extends MRBase implements Reducer<Text, Text, Text, Text> {

	private Text outputValue = new Text();
	
	private Map<Integer, Double> lv_amt  = new HashMap<Integer, Double>();
	
	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		lv_amt.clear();
		
		Integer level = 0;
		
		String[] items = null;
		
		Double sum = 0.0;
        String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);		
		while(values.hasNext()){
			items = values.next().toString().split("\t");
			
			level = Integer.parseInt(items[1]);
			
			sum = lv_amt.get(level);
			if(sum == null) {
				sum = new Double(0.0);
			}
			sum += Double.parseDouble(items[2]);
			
			lv_amt.put(level, sum);
			
		}
		
		Iterator<Entry<Integer, Double>> it = lv_amt.entrySet().iterator();
		Entry<Integer, Double> et = null;
		
		while(it.hasNext()){
			et = it.next();
			outputValue.set(String.format("%d\t%s", et.getKey().intValue(), et
					.getValue().doubleValue()));
			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
			//output.collect(key, outputValue);
		}
		
	}

}
