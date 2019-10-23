package myDemo_5;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DemoReducer5 extends Reducer<Text, LongWritable, Text, LongWritable>{

	@Override
	protected void reduce(Text key, Iterable<LongWritable> values,Context context)
			throws IOException, InterruptedException {
		long sum = 0;
		
		for(LongWritable l : values){
			sum += l.get(); 
		}
		
		context.write(key, new LongWritable(sum));
		
	}

	
}
