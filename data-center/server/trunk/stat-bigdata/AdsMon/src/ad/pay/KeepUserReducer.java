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
public class KeepUserReducer extends MapReduceBase implements
		Reducer<Text, LongWritable, Text, NullWritable> {

	
	@Override
	public void reduce(Text key, Iterator<LongWritable> values,
			OutputCollector<Text, NullWritable> output, Reporter reporter)
			throws IOException {
		
		boolean yesterday = false;
		boolean today =false;
		LongWritable v = null;
		while(values.hasNext()) {
			v = values.next();
			if(v.get() == UserTag.YESTERDAY){
				yesterday = true;
			} 
			if(v.get() == UserTag.USER_ACTIVE) {
				today = true;
			}
		}
		
		if(today && yesterday) output.collect(key, NullWritable.get());
		
	}

}
