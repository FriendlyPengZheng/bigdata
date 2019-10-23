package ad.pay;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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
public class BackUserReducer extends MapReduceBase implements
		Reducer<Text, LongWritable, Text, NullWritable> {

	
	@Override
	public void reduce(Text key, Iterator<LongWritable> values,
			OutputCollector<Text, NullWritable> output, Reporter reporter)
			throws IOException {
		
		while(values.hasNext()) {
			if(values.next().get() == UserTag.DIF_INT_TAG){
				return;
			}
		}
		
		output.collect(key, NullWritable.get());
		
	}

}
