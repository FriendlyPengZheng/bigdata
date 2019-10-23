package ad.pay;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import util.RegisterParser;

/**
 * 
 * @author cheney
 * @date 2013-12-05
 */
public class GetRegisterMap extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, LongWritable> {

	private RegisterParser pr = new RegisterParser();
	
	private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable();
    
    protected int tags = UserTag.USER_NEW;
    
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, LongWritable> output, Reporter reporter)
			throws IOException {
		
		String line = value.toString();
        if(!pr.init(line)) {
            System.err.printf("error format: %s\n", line);
            return;
        }
        
        int gameid = pr.getNumGameid();
        long mimi = pr.getNumMimi();
        
        outputKey.set(String.format("%d\t%d", gameid, mimi));
        outputValue.set(tags);
        
        output.collect(outputKey, outputValue);
        
	}

}
