package com.taomee.tms.test.taskBeginSetup;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

/**
 * 
 * @author looper
 * @date 2016年10月26日
 */
public class SplitReducerBackUp extends Reducer<Text, Text, Text, Text> {

	private MultipleOutputs mos;
	private String m_id;
	//private NullWritable
	private Text outkey=new Text();
	private Text outvalue=new Text(); 

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		// super.setup(context);
		mos = new MultipleOutputs(context);
	}

	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		for (Text val : values) {
			// context.write(key, val);

			/* mos.write(key, val, key.toString()); */
			switch (key.toString()) {
			case "count":
				/*mos.write(key2, val, key.toString());
				
				break;*/
			case "sum":
				/*mos.write(key2, val, key.toString());
				break;*/
			case "distinct_count":
				
				/*val.set(val.toString().substring(0, val.toString().lastIndexOf("\t")));
				mos.write(key2, val, key.toString());*/
				outkey.set(val.toString().split("\t")[0]);
				outvalue.set(val.toString().substring((val.toString().split("\t")[0]+"\t").length(), val.toString().lastIndexOf("\t")));
				mos.write(outkey, outvalue, key.toString());
				break;
			case "material":
				/*m_id = val.toString().split("\t")[4];
				val.set(val.toString().substring(0, val.toString().lastIndexOf("\t")));
				mos.write(key2, val, m_id + "_" + key.toString());*/
				outkey.set(val.toString().split("\t")[0]);
				outvalue.set(val.toString().substring((val.toString().split("\t")[0]+"\t").length(), val.toString().lastIndexOf("\t")));
				m_id = val.toString().split("\t")[4];
				mos.write(outkey, outvalue, m_id+"_"+key.toString());
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		super.cleanup(context);
		mos.close();
	}

}
