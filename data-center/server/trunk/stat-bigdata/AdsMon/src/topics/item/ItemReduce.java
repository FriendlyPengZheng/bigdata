package topics.item;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;


//输入key: time zid sid gid uid vip itemid itemtype     value: 1 购买数量 购买价钱 这里的1用于统计人次
//输出key: time zid sid gid uid vip itemid itemtype	value: 购买人次 购买数量 购买价钱 

public class ItemReduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
	private Text realValue = new Text();
	public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
	{

		long userTotalNum = 0;
		long itemTotalNum = 0;
		long itemTotalPrice = 0;
		long tmpValue = 0;
		while(values.hasNext())
		{
			String[] items = values.next().toString().split("\t");
			if(items.length < 3)
			{
				continue;
			}	
			try
			{
				tmpValue = Long.valueOf(items[0]);
				userTotalNum += tmpValue;
				tmpValue = Long.valueOf(items[1]);
				itemTotalNum += tmpValue;
				tmpValue = Long.valueOf(items[2]);
				itemTotalPrice += tmpValue;
			}catch(Exception ex)
			{
				System.err.printf("convert usernum, itemnum, itemprice to Long failed(userNum=%s, itemNum=%s, itemPrice=%s)", items[0], items[1], items[2]);
				continue;
			}
		}
		realValue.set(String.format("%s\t%s\t%s", userTotalNum, itemTotalNum, itemTotalPrice));

		output.collect(key, realValue);
				
	}
}
