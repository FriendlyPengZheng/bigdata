package com.taomee.tms.test.taskBeginSetup;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

/**
 * 
 * @author looper
 * @date 2016年10月26日
 */
public class SplitReducer extends Reducer<Text, Text, Text, NullWritable> {

	private MultipleOutputs mos;
	private String m_id;
	//private NullWritable
	private Text val=new Text();


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
		
		String op=key.toString().split("\t")[0];
		String value2=key.toString().substring((op+"\t").length(), key.toString().length()-1);
		System.out.println("v2:"+value2);
		/*for (Text val : values) {*/
			// context.write(key, val);

			/* mos.write(key, val, key.toString()); */
			switch (op) {
			case "count":
				/*mos.write(key2, val, key.toString());
				
				break;*/
			case "sum":
				/*mos.write(key2, val, key.toString());
				break;*/
			case "distinct_count":
				/*val.set(value2.substring(0, value2.toString().lastIndexOf("\t")));
				mos.write(key, val, key.toString());
				break;*/
			case "material":
				/*m_id = value2.toString().split("\t")[4];
				val.set(value2.toString().substring(0, value2.toString().lastIndexOf("\t")));
				mos.write(key, val,m_id + "_" + op.toString());
				break;*/

			default:
				break;
			}
		}
	/*}*/

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		super.cleanup(context);
		mos.close();
	}

}
