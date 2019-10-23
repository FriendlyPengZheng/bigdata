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
public class MergeLoginMap extends MapReduceBase 
    implements Mapper<LongWritable, Text, MergeKey, Text>
{
    //private Text realKey = new Text();
    private MergeKey mkey = new MergeKey();
    private Text realValue = new Text();
    private Text nullValue = new Text("");
    private LoginParser inparser = new LoginParser();
    //private MergeLog mergelog = null;
    //public void configure(JobConf job) {
        //try { 
            //String classLoginImpl = job.get("merge.login.refclass.impl", "util.StatLogParser");
            //mergelog = Class.forName(classLoginImpl).newInstance();
        //} catch (Exception ex) {
            //System.err.println("error when new 'merge.login.refclass.impl' instance" + ex.toString());
        //}
    //}

    public void map(LongWritable key, Text value,
            OutputCollector<MergeKey,Text> output, Reporter reporter) throws IOException {
        String line = value.toString();
        String[] items = line.split("\t");
        switch (items.length)
        {
            case 6 : // time mimi tad gameid idc ip
                inparser.init(items);
                if (!inparser.isValid()) {
                    System.err.println("error login merge: " + line);
                    return;
                }
                mkey.setGameid(inparser.getNumGameid());
                mkey.setMimi(inparser.getNumMimi());
                // time tad idc ip
                realValue.set(String.format("%s\t%s\t%s\t%s", items[0], items[2], items[4], items[5]));
                break;
            default : // error input
                System.err.println("error merge: " + line);
                return;
        }  /* end of switch */

        //System.out.println(mkey.toString() + "\t" + realValue.toString());

        output.collect(mkey, realValue);
    }



}
        
