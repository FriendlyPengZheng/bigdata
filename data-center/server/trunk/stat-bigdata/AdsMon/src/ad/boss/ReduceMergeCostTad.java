package ad.boss;
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

// in1: mimi gameid costnum
// in2: mimi tad
// out: gameid,mimi costnum tad
public class ReduceMergeCostTad extends MapReduceBase
    implements Reducer<Text, Text, Text, NullWritable>
{
    private Text realKey = new Text();
    private HashMap<String, String> gamecostmap = new HashMap<String, String>();

    public void reduce(Text key, Iterator<Text> values,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {
        String outTad = null;
        gamecostmap.clear();
        while(values.hasNext()) {
            String value = values.next().toString();
            String[] items = value.split("\t");
            switch (items.length)
            {
                case 1 : outTad = value; break;
                case 2 : gamecostmap.put(items[0], items[1]); break;
                default : System.err.println("gamecost recv err fmt: " + value); break;
            }  /* end of switch */
        }
        if (outTad == null) { outTad = "unknown"; }

        String mimi = key.toString();
        Iterator<String> git = gamecostmap.keySet().iterator();
        while (git.hasNext()) {
            String gameid = git.next();
            String costnum = gamecostmap.get(gameid);

            realKey.set(String.format("%s,%s\t%s\t%s", gameid, mimi, costnum, outTad));
            output.collect(realKey, NullWritable.get());
        } /* - end while - */
    }
}

