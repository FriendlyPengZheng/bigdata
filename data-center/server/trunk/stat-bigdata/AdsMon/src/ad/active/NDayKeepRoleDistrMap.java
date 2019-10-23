package ad.active;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;


// distr n days' keep game ad
// in:: regtime activetime mimi tad gameid [gameid ...]
// out:: regtime nDaysInteval gameid ad keepers
// register in 20120101 keep in 20120102 => nDaysInteval = 1
public class NDayKeepRoleDistrMap extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, LongWritable>
{
	private Text realKey = new Text();
	private LongWritable one = new LongWritable(1);

	private AdParser adp = new AdParser();

	private HashMap<String, Date> dayDateMap = new HashMap<String, Date>();
	private HashMap<String, Long> dayTimeMap = new HashMap<String, Long>();

	private int gameids = 0;

	protected JobConf jobConf = null;
	public void configure(JobConf job) { this.jobConf = job; }

	public void map(LongWritable key, Text value,
			OutputCollector<Text,LongWritable> output, Reporter reporter)
		throws IOException
	{
		String line = value.toString();
		String[] items = line.split("\t");
		if (items.length < 5) { System.err.println("error merge ndays fmt: " + line); return; }

		String roleDayStr = items[0];
		Date roletime = dayDateMap.get(roleDayStr);
		if (roletime == null) {
			roletime = DateUtil.stringToDate(roleDayStr);
			dayDateMap.put(roleDayStr, roletime);
		}
		Long roleTimeStamp = dayTimeMap.get(roleDayStr);
		if (roleTimeStamp == null) {
			roleTimeStamp = roletime.getTime();
			dayTimeMap.put(roleDayStr, roleTimeStamp);
		}

		String activeDayStr = items[1];
		Date activetime = dayDateMap.get(activeDayStr);
		if (activetime == null) {
			activetime = DateUtil.stringToDate(activeDayStr);
			dayDateMap.put(activeDayStr, activetime);
		}
		Long activeTimeStamp = dayTimeMap.get(activeDayStr);
		if (activeTimeStamp == null) {
			activeTimeStamp = activetime.getTime();
			dayTimeMap.put(activeDayStr, activeTimeStamp);
		}

		long dayDiff = (int)((activeTimeStamp - roleTimeStamp) / (3600L * 24L * 1000L));

		// process tad:: "" ==> "unknown"
		String tad = items[2];
		if (tad.length() == 0) { tad = "unknown"; }

		try
		{
			gameids = Integer.valueOf(items[4]);
		}catch(Exception e)
		{
			return;
		}


		adp.init(tad);
		Iterator<String> adit = adp.iterator();
		while (adit.hasNext()) {
			String aditem = adit.next();
			// for iterator ad
			realKey.set(String.format("%s,%d,%s,%s", roleDayStr, dayDiff, gameids, aditem));
			output.collect(realKey, one);
			//all game
			realKey.set(String.format("%s,%d,-1,%s", roleDayStr, dayDiff,  aditem));
			output.collect(realKey, one);
		}
		// for all ad
		realKey.set(String.format("%s,%d,%s,all", roleDayStr, dayDiff, gameids));
		output.collect(realKey, one);

		realKey.set(String.format("%s,%d,-1,all", roleDayStr, dayDiff));
		output.collect(realKey, one);
	}
}
