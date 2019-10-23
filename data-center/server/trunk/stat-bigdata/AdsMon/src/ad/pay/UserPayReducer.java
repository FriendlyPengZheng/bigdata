package ad.pay;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * 
 * @author cheney
 * @date 2013-12-05
 */
public class UserPayReducer extends MapReduceBase implements
		Reducer<Text, LongWritable, Text, Text> {

	private Text outputValue = new Text();
	
	@Override
	public void reduce(Text key, Iterator<LongWritable> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		int sum = 0;
		long tags = 0;
		
		while(values.hasNext()) {
			long v = values.next().get();
			if(v == UserTag.USER_NEW || v == UserTag.USER_KEEP || v == UserTag.USER_BACK){
				tags = v;
			} else if(v > 0){
				sum += v;
			}
		}
		
		if(tags < 0) {
			outputValue.set(String.format("%d\t%d", -tags, sum));
			
			output.collect(key, outputValue);
		}
		
	}

}
