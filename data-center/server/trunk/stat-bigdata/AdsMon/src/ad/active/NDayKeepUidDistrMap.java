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
public class NDayKeepUidDistrMap extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, LongWritable>
{
    private Text realKey = new Text();
    private LongWritable one = new LongWritable(1);

    private AdParser adp = new AdParser();

    private HashMap<String, Date> dayDateMap = new HashMap<String, Date>();
    private HashMap<String, Long> dayTimeMap = new HashMap<String, Long>();

    private ArrayList<String> gameids = new ArrayList<String>();

    protected JobConf jobConf = null;
    public void configure(JobConf job) { this.jobConf = job; }

    public void map(LongWritable key, Text value,
            OutputCollector<Text,LongWritable> output, Reporter reporter)
        throws IOException
    {
        String line = value.toString();
        String[] items = line.split("\t");
        if (items.length < 5) { System.err.println("error merge ndays fmt: " + line); return; }

        String regDayStr = items[0];
        Date regtime = dayDateMap.get(regDayStr);
        if (regtime == null) {
            regtime = DateUtil.stringToDate(regDayStr);
            dayDateMap.put(regDayStr, regtime);
        }
        Long regTimeStamp = dayTimeMap.get(regDayStr);
        if (regTimeStamp == null) {
            regTimeStamp = regtime.getTime();
            dayTimeMap.put(regDayStr, regTimeStamp);
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

        long dayDiff = (int)((activeTimeStamp - regTimeStamp) / (3600L * 24L * 1000L));

        // process tad:: "" ==> "unknown"
        String tad = items[3];
        if (tad.length() == 0) { tad = "unknown"; }

        // iterator process gameid
        gameids.clear();
        for (int i = 4 ; i < items.length ; ++i) { gameids.add(items[i]); } // end of for
        String[] gids = gameids.toArray(new String[0]);

        adp.init(tad);
        Iterator<String> adit = adp.iterator();
	boolean added = true;
        while (adit.hasNext()) {
            String aditem = adit.next();
			
            for (int i = 0 ; i < gids.length ; ++i) {
                // for iterator ad
                realKey.set(String.format("%s,%d,%s,%s", regDayStr, dayDiff, gids[i], aditem));
                output.collect(realKey, one);

                // for all ad
		if(added)//只在while循环第一次进入时添加，处理多级tad的情况20130619
		{
                	realKey.set(String.format("%s,%d,%s,all", regDayStr, dayDiff, gids[i]));
                	output.collect(realKey, one);
            	}
	    } /* - end for - */
	    added = false;
        }

    }
}
