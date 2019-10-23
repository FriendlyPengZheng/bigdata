package myDemo_4;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DemoReducer4 extends Reducer<Text, Text, LongWritable, LongWritable>{

	private static long num_1 = 0l;
	private static long num_2 = 0l;
	@Override
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {
		
		boolean abc = false;
		boolean ABC = false;
		
		for(Text t : values){
			
			String data = t.toString();
			
			if("abc".equals(data.substring(0, 3))){
				abc = true;
			}
			
			if("ABC".equals(data.substring(0, 3))){
				ABC = true;
			}
		}
		
		if(abc){
			num_1++;
		}
		
		if(abc && ABC){
			num_2++;
		}

		context.write(new LongWritable(num_2), new LongWritable(num_1));
	
	}

	
}
