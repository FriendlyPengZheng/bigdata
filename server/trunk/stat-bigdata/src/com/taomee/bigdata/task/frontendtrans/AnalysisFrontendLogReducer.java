package com.taomee.bigdata.task.frontendtrans;

import java.io.IOException;
import java.util.Iterator;

import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

/**
 * 输出一个用户最后一步的统计步骤
 * 
 * @author looper
 * @date 2017年5月16日
 */
public class AnalysisFrontendLogReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {

	private Text outputValue = new Text();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	private TreeSet<Integer> steps = new TreeSet<Integer>();
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
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
		steps.clear();
		int step;
		while (values.hasNext()) {
			String items[] = values.next().toString().split("\t");
			step = Integer.valueOf(items[0]);		
			steps.add(step);		
		}
		int maxstep = steps.last();
		//判断其在转换过程的哪一步
//		int i = 1;

        /*int max
		for (i = 1; i < maxstep; i++) {
			if (!steps.contains(i))
				break;
		}*/

		/**
		 * 对于大于步骤1的用户才会输出。
		 */
//		if (i > 1) {
			outputValue.set(String.valueOf(maxstep));
			mos.getCollector("part" + gameinfo, reporter).collect(key,outputValue);
//		}

	}

}
