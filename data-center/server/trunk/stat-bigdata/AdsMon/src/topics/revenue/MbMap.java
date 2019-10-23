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


//输入: 时间戳 米米号 收到商品的米米号 交易渠道  消费米币得到的商品id  消费米币得到的商品数量  充值/消费米币额 本次交易后的米币余额
//输出
//key: day  游戏id	米米号
//value: 金额
public class MbMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
{
	private Text realKey = new Text();
	private LongWritable realValue = new LongWritable(0);

	private KeyValueParse kvp = null;
	private JobConf jobConf = null;
	public void configure(JobConf job)
	{
		this.jobConf = job;
		String fsname = jobConf.get("fs.default.name");
		String mbChannelFile = fsname.equals("file:///") ? "/opt/taomee/hadoop/ads/mapred/work/conf/channelgame.conf" : "channelgame.conf";
		kvp = new KeyValueParse(mbChannelFile);
	}

	public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
	{
		String line = value.toString();
		String[] items = line.split("\t");
		if(items.length > 7)
		{
			String day = DateUtil.getYMDFormat(items[0]);
			String uid = items[1];

			String channel = items[3];
			int mbnum = 0;
			try
			{
				mbnum = Integer.valueOf(items[6]);
			}
			catch(Exception ex)
			{
				System.out.printf("convert channel and mbnum to int failed(%s %s %s).", items[3], items[6]);
				return;
			}
			
			String gameid = kvp.getGameId(channel);
			if(gameid != null)
			{
				realKey.set(String.format("%s\t%s\t%s\t1", day, gameid, uid));
				realValue.set(-mbnum);
				output.collect(realKey, realValue);

				realKey.set(String.format("%s\t%s\t%s\t2", day, gameid, uid));
				realValue.set(-mbnum);
				output.collect(realKey, realValue);
			}
			
		}
		return;
	}
}
