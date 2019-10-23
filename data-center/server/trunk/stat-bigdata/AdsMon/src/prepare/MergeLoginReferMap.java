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

// merge "login log" and "stat log|online log"
// login: time mimi tad gameid idc ip
// stat: mimi gameid
// login: time mimi tad gameid idc ip  # some tad not set, ignore tad
//       ==>
// outformat: {gameid,mimi} "" | {gameid,mimi} "time tad idc ip"
public class MergeLoginReferMap extends MapReduceBase 
    implements Mapper<LongWritable, Text, MergeKey, Text>
{
    //private Text realKey = new Text();
    private MergeKey mkey = new MergeKey();
    //private LoginParser inparser = new LoginParser();
    //private Text realValue = new Text();
    private Text emptyValue = new Text("");
    private MergeLog mergelog = null;
    public void configure(JobConf job) {
        try { 
            String classLoginImpl = job.get("merge.login.refclass.impl", "util.StatLogParser");
            mergelog = Class.forName(classLoginImpl).asSubclass(MergeLog.class).newInstance();
        } catch (Exception ex) {
            System.err.println("error when new 'merge.login.refclass.impl' instance" + ex.toString());
        }
    }

    public void map(LongWritable key, Text value,
            OutputCollector<MergeKey,Text> output, Reporter reporter) throws IOException {
        String line = value.toString();
        String[] items = line.split("\t");
        // parser hint from configuration
        // case 1: gameid mimi
        // case 2: time mimi tad gameid idc ip
        if (!mergelog.init(items)) {
            System.err.println("error merge refer: " + line);
            return;
        }
        mkey.setGameid(mergelog.getNumGameid());
        mkey.setMimi(mergelog.getNumMimi());

        //System.out.println("refer" + "\t" + mkey.toString());

        output.collect(mkey, emptyValue);
    }
}
        
