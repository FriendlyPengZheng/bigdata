package com.taomee.bigdata.task.account_phone;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class SeerAccountReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, NullWritable> {
	private HashSet<String> gidMobile = new HashSet<String>();
	private HashSet<String> account = new HashSet<String>();

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
		// TODO Auto-generated method stub
		gidMobile.clear();
		account.clear();

		boolean hasFlag1 = false;
		boolean hasFlag2 = false;

		while (values.hasNext()) {
			String items[] = values.next().toString().split(",");
			if (items[0].equals("1")) {
				hasFlag1 = true;
				gidMobile.add(items[1]);
			}
			if (items[0].equals("2")) {
				hasFlag2 = true;
				account.add(items[1]);
			}
		}
		if (hasFlag1 && hasFlag2) {
			for (String gm : gidMobile) {
				output.collect(new Text(gm), NullWritable.get());
			}
		}
	}
}
