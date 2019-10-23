package topics.active;

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
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

/**
 * @brief  aggregate reduce record format::= gameid,mimi tad region login_times avg_sessionlength
 */
public class AggLoginInfoReduce extends MapReduceBase
    implements Reducer<Text, Text, Text, Text>
{
    private Text realValue = new Text();
    private JobConf conf = null;
    private AggGameSummary ags = new AggGameSummary();
    public void configure(JobConf job) {
        conf = job;
    }

    // gameid,mimi tad region login_times sessionlength
    //      =>
    // gameid,mimi tad(max) region(max) login_times(+) sessionlength(+)
    public void reduce(Text key, Iterator<Text> values,
            OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
        ags.clear();
        while (values.hasNext()) {
            String value = values.next().toString();
            String[] items = value.split("\t");
            try {
                ags.addTad(items[0]);
                ags.addRegionCode(items[1]);
                int times = Integer.parseInt(items[2]);
                ags.times += times;
                int slen = Integer.parseInt(items[3]);
                ags.sessionlength += slen;
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        realValue.set(String.format("%s\t%s\t%d\t%d", ags.getTad(), 
                    ags.getRegionCode(), ags.times, ags.sessionlength));
        output.collect(key, realValue);
    }

    //public void close() {}
}

class AggGameSummary
{
    // month times
    public int times = 0;
    // month session all length
    public int sessionlength = 0;

    // tad => times
    private TreeMap<String, Integer> tad_count = new TreeMap<String, Integer>();
    // regioncode => times
    private TreeMap<String, Integer> region_count = new TreeMap<String, Integer>();

    public void clear() {
        times = 0; sessionlength = 0;
        tad_count.clear();
        region_count.clear();
    }

    public void addTad(String tad) {
        if (tad_count.containsKey(tad)) {
            tad_count.put(tad, tad_count.get(tad) + 1);
        } else {
            tad_count.put(tad, 1);
        }
    }
    public void addRegionCode(String region) {
        if (region_count.containsKey(region)) {
            region_count.put(region, region_count.get(region) + 1);
        } else {
            region_count.put(region, 1);
        }
    }

    public String getTad() {
        String Tad = "unknown";
        int Max = 0;
        Set<String> ts = tad_count.keySet();
        Iterator<String> tsit = ts.iterator();
        while (tsit.hasNext()) {
            String tmp = tsit.next();
            int count = tad_count.get(tmp);
            if (count > Max ||
                    (count == Max && ("unknown".equals(Tad) || "none".equals(Tad)))) {
                Max = count ;
                Tad = tmp;
            }
        }
        return Tad;
    }
    public String getRegionCode() {
        String Region = "0";
        int Max = 0;
        Set<String> rcs = region_count.keySet();
        Iterator<String> rcsit = rcs.iterator();
        while (rcsit.hasNext()) {
            String tmp = rcsit.next();
            int count = region_count.get(tmp);
            if (count > Max || 
                (count == Max && "0".equals(Region))) {
                Max = count;
                Region = tmp;
            }
        }
        return Region;
    }
}
