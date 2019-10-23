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
public class BeginBasicSplitReducer extends Reducer<Text, Text, Text, Text> {

	private MultipleOutputs mos;
	private String m_id;
	private String gid;//分游戏
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
				//outvalue.set(val.toString().substring((val.toString().split("\t")[0]+"\t").length(), val.toString().lastIndexOf("\t")));
				outvalue.set(val.toString().split("\t")[1]+"\t"+val.toString().split("\t")[2]+"\t"+val.toString().split("\t")[3]);
				gid=val.toString().split("\t")[5];
				mos.write(outkey, outvalue, key+"G"+gid.toString());
				break;
			case "material":
				/*m_id = val.toString().split("\t")[4];
				val.set(val.toString().substring(0, val.toString().lastIndexOf("\t")));
				mos.write(key2, val, m_id + "_" + key.toString());*/
				//outkey.set(val.toString().split("\t")[0]);
				//outvalue.set(val.toString().substring((val.toString().split("\t")[0]+"\t").length(), val.toString().lastIndexOf("\t")));
				String value_tmp=val.toString().substring((val.toString().split("\t")[0]+"\t").length(), val.toString().lastIndexOf("\t"));
				outkey.set(value_tmp.toString().split("\t")[0]);
				//outvalue.set(value_tmp.toString().substring((value_tmp.toString().split("\t")[0]+"\t").length(), value_tmp.toString().lastIndexOf("\t")));
				outvalue.set(value_tmp.toString().split("\t")[1]+"\t"+value_tmp.toString().split("\t")[2]);
				m_id = val.toString().split("\t")[4];
				gid=val.toString().split("\t")[5];
				mos.write(outkey, outvalue, m_id+"_"+key+"G"+gid.toString());
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
