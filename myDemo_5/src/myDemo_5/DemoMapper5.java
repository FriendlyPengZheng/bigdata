package myDemo_5;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DemoMapper5 extends Mapper<LongWritable, Text, Text, LongWritable>{

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {

		String tad_split = null;
		String[] line = value.toString().split(",");
		String tad = line[1];
		
		AdParser adParser = new AdParser();
		adParser.init(tad);
		Iterator<String> iterator = adParser.iterator();
		if(iterator.hasNext()){
			tad_split = iterator.next();
		}
		
		context.write(new Text(tad_split), new LongWritable(1));
	}

}
