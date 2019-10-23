package prepare;
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
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

/**
 */
public class MergeRoleReduce extends MapReduceBase
    implements Reducer<MergeKey, Text, Text, NullWritable>
{
    private Text realKey = new Text();
    //private Text realValue = new Text();
    //private ArrayList<String> arrlist = new ArrayList<String>();
    private JobConf conf = null;
    public void configure(JobConf job) {
        conf = job;
    }

    // input: {gameid,mimi} "time tad idc ip"
    //      = reproduce login log =>
    // output: time mimi tad gameid idc
    public void reduce(MergeKey key, Iterator<Text> values,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {
        boolean outmerge = false;
        int mintime = 2147483647;
        String tad = null;
        String time = null;
        String idc = null;
        while(values.hasNext()) {
            Text v = values.next();
            String value = v.toString();
            if (value.length() == 0) { outmerge = true; continue;}
            String[] items = value.split("\t");
            try { 
                int timetmp = Integer.parseInt(items[0]);
                if (timetmp < mintime) {
                    mintime = timetmp;
                    time = items[0];
                    tad = items[1];
                    idc = items[2];
                }
            } catch (Exception ex) { // drop this role
                System.err.println("error login time: " + value);
                return;
            }
        }
        if (!outmerge) { return; }

        int gameid = key.getGameid();
        long mimi = key.getMimi();
        //System.out.println("dbg: " + key.toString() + "  size: " + arrlist.size());

        realKey.set(String.format("%s\t%d\t%s\t%d\t%s", time, mimi, tad, gameid, idc));
        output.collect(realKey, NullWritable.get());
    }
}

