package topics.item;
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

//输入: timestamp zid sid gid uid vipornotvip(0:非VIP 1:VIP) itemid itemtype 购买人次 itemnum price
//输出key: time zid sid gid vip itemid
//输出value: 1 购买人次 购买数量 购买价钱 这里的1用于统计人数
//根据uid字段去重得到人数
public class UniqUserMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
	private Text realKey = new Text();
	private Text realValue = new Text();

	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
	{
		String line = value.toString();
		String[] items = line.split("\t");
		if(items.length > 10)
		{
			String day = items[0];
			String zid = items[1];
			String sid = items[2];
			String gid = items[3];
			String vip = items[5];
			String itemid = items[6];
			String itemtype = items[7];
			String usernum = items[8];
			String itemnum = items[9];
			String itemprice = items[10];
			
			realKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s", day, zid, sid, gid, vip, itemid, itemtype));
			realValue.set(String.format("1\t%s\t%s\t%s",  usernum, itemnum, itemprice));
			output.collect(realKey, realValue);

		}
		return;
	}
}
