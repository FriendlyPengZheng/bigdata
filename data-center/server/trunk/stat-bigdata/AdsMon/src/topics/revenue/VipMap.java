package topics.revenue;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;


//输入: 时间戳	米米号	游戏id	充值渠道	操作类型	操作的天数	begin_time	end_time	timeflag
//输出
//key: day  游戏id	米米号 type(1:all 2:米币购买二级货币 3:米币购买vip)
//value: 金额
public class VipMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
{
	private Text realKey = new Text();
	private LongWritable realValue = new LongWritable(0);

	public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
	{
		String line = value.toString();
		String[] items = line.split("\t");
		if(items.length > 8)
		{
			String day = DateUtil.getYMDFormat(items[0]);
			String uid = items[1];
			String gid = items[2];

			int channel = 0;
			int actionType = 0;
			int opDay = 0;

			try
			{
				channel = Integer.valueOf(items[3]);
				actionType = Integer.valueOf(items[4]);
				opDay = Integer.valueOf(items[5]);
			}
			catch(Exception ex)
			{
				System.out.printf("convert channel actiontype and opDay to int failed(%s %s %s).", items[3], items[4], items[5]);
				return;
			}
			
			if((actionType == 1 || actionType == 2) && channel != 90 && channel != 91 && channel != 99 && channel != 100)
			{
				switch(opDay)
				{//这里的单位是 分
					case 30:
						realValue.set(1000);
						break;
					case 60:
						realValue.set(3000);
						break;
					case 180:
						realValue.set(5000);
						break;
					case 360:
						realValue.set(10000);
						break;
					default:
						return;
				}
				realKey.set(String.format("%s\t%s\t%s\t1", day, gid, uid));
				output.collect(realKey, realValue);

				realKey.set(String.format("%s\t%s\t%s\t3", day, gid, uid));
				output.collect(realKey, realValue);
			}
			
		}
		return;
	}
}
