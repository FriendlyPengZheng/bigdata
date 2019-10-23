package ad.pay;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * key: gameid,type
 * value: mimi,mb
 * @author cheney
 * @date 2013-12-05
 */
public class ArpuReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {

	private Text outputValue = new Text();
	
	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		int sum = 0;

		String[] vs = null;
		
		Set<String> user = new HashSet<String>();
		
		int paycount = 0;
		
		while(values.hasNext()) {
			vs = values.next().toString().split("\t");
			int mb = Integer.parseInt(vs[1]);
			if(mb > 0) {
				paycount++;
				sum += mb;
			}
			user.add(vs[0]);
		}
		
		double rate = MathUtil.divMethod(paycount, user.size(), 4);
		double arpu = 0;
		if(paycount > 0)
			arpu = MathUtil.divMethod(sum/100, paycount, 4);
		outputValue.set(String.format("%s\t%s", rate, arpu));
		
		output.collect(key, outputValue);
		
	}
	
	public static void main(String[] args) {
		System.out.println(String.format("%s", 126.196d));
	}

}
