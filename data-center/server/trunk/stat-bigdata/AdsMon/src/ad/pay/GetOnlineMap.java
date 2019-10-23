package ad.pay;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import util.LoginParser;

/**
 * 
 * @author cheney
 * @date 2013-12-05
 */
public class GetOnlineMap extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, LongWritable> {

	private LoginParser lp = new LoginParser();
	
	private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable();
    
    protected int tags = UserTag.USER_ACTIVE;
    
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, LongWritable> output, Reporter reporter)
			throws IOException {
		
		String line = value.toString();
        if(!lp.init(line)) {
            System.err.printf("login err fmt: %s\n", line);
            return;
        }
        
        int gameid = lp.getNumGameid();
        long mimi = lp.getNumMimi();
        
        outputKey.set(String.format("%d\t%d", gameid, mimi));
        outputValue.set(tags);
        
        output.collect(outputKey, outputValue);
        
	}

}
