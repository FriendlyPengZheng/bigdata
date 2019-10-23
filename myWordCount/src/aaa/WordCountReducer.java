package aaa;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WordCountReducer extends Reducer<Text, LongWritable, Text, LongWritable>{

	private MultipleOutputs<Text, LongWritable> multipleOutputs;
	 

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		multipleOutputs = new MultipleOutputs<Text, LongWritable>(context);
	}

	@Override
	protected void reduce(Text k3, Iterable<LongWritable> v3,Context context) throws IOException, InterruptedException {
		//v3: 是一个集合，每个元素就是v2
		long total = 0;
		for(LongWritable l:v3){
			total = total + l.get();
		}
		
		//这里设置baseOutputPath的值为word+'/'+word后，'/'前面的内容会用作命名文件夹，后面word内容会用作命名新的文件名
		multipleOutputs.write(k3, new LongWritable(total), k3.toString()+"/"+k3.toString());
		multipleOutputs.write(k3, new LongWritable(1000), "");
	}

	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		multipleOutputs.close();
	}

   
}


