package com.taomee.bigdata.task.frontendtrans;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

public class AnalysisFrontendSumReducer extends MapReduceBase implements
		Reducer<Text, IntWritable, Text, IntWritable> {

	private IntWritable outputValue = new IntWritable();
	private DoubleWritable doubleOutputValue = new DoubleWritable();
	private Text outputKey = new Text();
	private TreeMap<Integer, Integer> steps = new TreeMap<Integer, Integer>();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
	}

	public void close() throws IOException {
		mos.close();
	}

	@Override
	public void reduce(Text key, Iterator<IntWritable> values,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		String items[] = key.toString().split("\t");
		String game = items[0];
		String modelid = items[1];
		steps.clear();
		Integer step;
		Integer cnt;
		double sum = 0.0;
		while (values.hasNext()) {
			step = Integer.valueOf(-values.next().get());
			cnt = steps.get(step);
			if (cnt == null)
				cnt = 0;
			cnt++;
			steps.put(step, cnt);
			sum++;
		}
		Integer max = -steps.firstKey();
		Integer stepscnt[] = new Integer[max + 2];
		stepscnt[max + 1] = 0;
		stepscnt[0] = (int) sum;
		for (int i = max; i >= 1; i--) {
			cnt = steps.get(-i);
			if (cnt == null)
				cnt = 0;
			stepscnt[i] = cnt + stepscnt[i + 1];

			outputKey
					.set(String
							.format("SET\t%s\t-1\t-1\t-1\t_%sfronendtrans_\t_%sfronendtrans_\tstep,ucount\t%d:%s",
									game, modelid, modelid, i,
									getStepName(modelid, i)));
			outputValue.set(stepscnt[i]);
			output.collect(outputKey, outputValue);

			outputKey
					.set(String
							.format("SET\t%s\t-1\t-1\t-1\t_%sfronendtrans_\t_%sfronendtrans_\tstep,percent\t%d:%s",
									game, modelid, modelid, i,
									getStepName(modelid, i)));
			doubleOutputValue.set(stepscnt[i] / sum * 100.0);
			mos.getCollector("percent", reporter).collect(outputKey,
					doubleOutputValue);

		}
	}

	/**
	 * 通过模型ID和步骤ID,获取步骤名称
	 * 
	 * @param modelId
	 * @param i
	 * @return
	 */
	private String getStepName(String modelId, Integer i) {
		return "模型" + modelId + "第" + i + "步骤";
	}

}
