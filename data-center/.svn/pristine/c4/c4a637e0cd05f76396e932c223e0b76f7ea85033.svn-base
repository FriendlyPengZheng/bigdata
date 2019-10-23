package com.taomee.bigdata.task.repair25;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * 极战联盟all数据修复reducer
 * 
 * @author looper
 * @date 2016年11月3日 数据格式1:1186608 25 -1 -1 -1 17003 1 数据格式2:1186608 506934458
 */
public class Analyze_Reduce extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {

	private Text outputKey = new Text();
	private Text outputValue = new Text();

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		/* List<String> vals=new ArrayList<String>(); */
		String v1 = new String();
		String v2 = new String();
		Map<Integer, String> v_maps = new HashMap<Integer, String>();
		int i = 1;
		while (values.hasNext()) {
			/* vals.add(values.next().toString()); */
			v_maps.put(i, values.next().toString());
			i++;
		}
		//两张映射表都有的
		if (v_maps.size() == 2) {
			v1 = v_maps.get(1);
			v2 = v_maps.get(2);
			if (v1.length() > v2.length()) {
				String vals[] = v1.toString().split("\t");
				outputKey.set(vals[0]);
				outputValue.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
						vals[1], vals[2], vals[3], v2 + "-" + "-1", vals[4],
						vals[5]));
				output.collect(outputKey, outputValue);
			} else {
				String vals[] = v2.toString().split("\t");
				outputKey.set(vals[0]);
				outputValue.set(String.format("%s\t%s\t%s\t%s\t%s\t%s",
						vals[1], vals[2], vals[3], v1 + "-" + "-1", vals[4],
						vals[5]));
				output.collect(outputKey, outputValue);
			}
		}

		/*
		 * for(String s:vals) {
		 * 
		 * }
		 */

	}

}
