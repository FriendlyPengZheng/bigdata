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

//输入: timestamp zid sid gid uid vipornotvip(0:非VIP 1:VIP) itemid itemtype(1:米币商品 2：二级货币商品) itemnum price
//输出key: time zid sid gid uid vip itemid itemtype
//输出value: 1 购买数量 购买价钱 这里的1用于统计人次
public class ItemMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
	private Text realKey = new Text();
	private Text realValue = new Text();

	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
	{
		String line = value.toString();
		String[] items = line.split("\t");
		if(items.length > 9)
		{
			String day = DateUtil.getYMDFormat(items[0]);
			int zid = 0;
			int sid = 0;
			try
			{
				zid = Integer.valueOf(items[1]);
				sid = Integer.valueOf(items[2]);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				System.err.printf("convert zid and sid to integer failed(zid=%s, sid=%s).", items[1], items[2]);
				return;
			}	
			String gid = items[3];
			String uid = items[4];
			String vip = items[5];
			String itemid = items[6];

			String itemtype = items[7];
			String itemnum = items[8];
			String itemprice = items[9];
			
			realKey.set(String.format("%s\t%d\t%d\t%s\t%s\t%s\t%s\t%s", day, zid, sid, gid, uid, vip, itemid, itemtype));
			realValue.set(String.format("1\t%s\t%s", itemnum, itemprice));
			output.collect(realKey, realValue);

			//添加一条用于所有道具的统计
			realKey.set(String.format("%s\t%d\t%d\t%s\t%s\t%s\t-1\t%s", day, zid, sid, gid, uid, vip, itemtype));
			realValue.set(String.format("1\t%s\t%s", itemnum, itemprice));
			output.collect(realKey, realValue);

			//添加一条不区分VIP用户和非VIP用户的
			realKey.set(String.format("%s\t%d\t%d\t%s\t%s\t-1\t%s\t%s", day, zid, sid, gid, uid, itemid, itemtype));
			realValue.set(String.format("1\t%s\t%s", itemnum, itemprice));
			output.collect(realKey, realValue);

			realKey.set(String.format("%s\t%d\t%d\t%s\t%s\t-1\t-1\t%s", day, zid, sid, gid, uid, itemtype));
			realValue.set(String.format("1\t%s\t%s", itemnum, itemprice));
			output.collect(realKey, realValue);

			if(zid != -1 || sid != -1)
			{//全区全服
				realKey.set(String.format("%s\t-1\t-1\t%s\t%s\t%s\t%s\t%s", day, gid, uid, vip, itemid, itemtype));
				realValue.set(String.format("1\t%s\t%s", itemnum, itemprice));
				output.collect(realKey, realValue);

				realKey.set(String.format("%s\t-1\t-1\t%s\t%s\t-1\t%s\t%s", day, gid, uid, itemid, itemtype));
				realValue.set(String.format("1\t%s\t%s", itemnum, itemprice));
				output.collect(realKey, realValue);

				realKey.set(String.format("%s\t-1\t-1\t%s\t%s\t-1\t-1\t%s", day, gid, uid, itemtype));
				realValue.set(String.format("1\t%s\t%s", itemnum, itemprice));
				output.collect(realKey, realValue);
			}
		}
		return;
	}
}
