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
public class MergeLoginReduce extends MapReduceBase
    implements Reducer<MergeKey, Text, Text, NullWritable>
{
    private Text realKey = new Text();
    //private Text realValue = new Text();
    private ArrayList<String> arrlist = new ArrayList<String>();
    private JobConf conf = null;
    public void configure(JobConf job) {
        conf = job;
    }

    // input: {gameid,mimi} "time tad idc ip"
    //      = reproduce login log =>
    // output: time mimi tad gameid idc ip
    public void reduce(MergeKey key, Iterator<Text> values,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {
        boolean outall = false;
        arrlist.clear();
        while(values.hasNext()) {
            Text v = values.next();
            String value = v.toString();
            if (value.length() == 0) { outall = true; continue;}
            arrlist.add(value);
        }

        if (!outall) { return; }

        int gameid = key.getGameid();
        long mimi = key.getMimi();
        //System.out.println("dbg: " + key.toString() + "  size: " + arrlist.size());

        Iterator<String> itText = arrlist.iterator();
        while (itText.hasNext()) {
            String strValue = itText.next();

            String[] items = strValue.split("\t");
            if (items.length != 4) {
                System.err.println("error base: " + strValue);
                continue;
            }

            realKey.set(String.format("%s\t%d\t%s\t%d\t%s\t%s", items[0], mimi,
                        items[1], gameid, items[2], items[3]));
            output.collect(realKey, NullWritable.get());
        } /* - end while - */
    }
}

