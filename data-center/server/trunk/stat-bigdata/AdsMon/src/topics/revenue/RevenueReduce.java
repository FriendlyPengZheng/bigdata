package topics.revenue;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;


//输入key: time gid uid type   value: 金额 
//输出key: time gid uid type(3:米币购买VIP 2:米币购买二级货币 1:all)     value: 金额

public class RevenueReduce extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
{
	private Text realKey = new Text();
	private LongWritable realValue = new LongWritable(0);
	public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
	{

		long totalMoney = 0;
		while(values.hasNext())
		{
			totalMoney += values.next().get();

		}
		realKey.set(String.format("%s", key.toString()));
		realValue.set(totalMoney);

		output.collect(realKey, realValue);	
	}
}
