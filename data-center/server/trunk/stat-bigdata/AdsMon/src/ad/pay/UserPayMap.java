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
public class UserPayMap extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {

	private Text outputKey = new Text();
    private Text outputValue = new Text();
    
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
        
		//gameid,mimi,type,mb
		String[] vs = value.toString().split("\t");
		
		outputKey.set(String.format("%s\t%s", vs[0], vs[2]));
		
		outputValue.set(String.format("%s\t%s", vs[1], vs[3]));
		
        output.collect(outputKey, outputValue);
        
	}

}
