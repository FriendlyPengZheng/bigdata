package ad.pay;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * 
 * @author cheney
 * @date 2013-12-05
 */
public class UserTagMap extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, LongWritable> {

    private LongWritable outputValue = new LongWritable();
    
    protected int tags = UserTag.DIF_INT_TAG;
    
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, LongWritable> output, Reporter reporter)
			throws IOException {
        
        outputValue.set(tags);
        
        output.collect(value, outputValue);
        
	}

}
