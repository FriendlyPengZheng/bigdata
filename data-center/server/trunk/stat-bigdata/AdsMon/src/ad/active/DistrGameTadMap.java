package ad.active;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

// from user game login ad intermediate result to get activers' distr data
// in:: gameid,mimi \t tad
//      =>
//  out:: ad(all),gameid \t 1
public class DistrGameTadMap extends MapReduceBase implements Mapper<Text, Text, Text, LongWritable>{
	private static AdParser adp = new AdParser();
    private JobConf jobConf = null;
    private Text realKey = new Text();
    private LongWritable one = new LongWritable(1);
//    private AdParser adp = new AdParser();
    public void configure(JobConf job) {
        this.jobConf = job;
    }

    public void map(Text key, Text value,
            OutputCollector<Text,LongWritable> output, Reporter reporter)
        throws IOException
    {
        String [] items = key.toString().split(",");
        if (items.length != 2) {
            System.err.printf("middle login error format: %s\n", key.toString());
            return;
        }

        adp.init(value.toString());
        Iterator<String> adit = adp.iterator();
        while (adit.hasNext()) {
            // format ::= ad,gameid \t 1
            realKey.set(String.format("%s,%s", adit.next(), items[0]));
            output.collect(realKey, one);
        }
        // for all ads to gameid
        realKey.set(String.format("all,%s", items[0]));
        output.collect(realKey, one);
    }
    public static void main(String[] args){
    	String teststr="#http://as.61.com/patchclient/paid/index.html";
    	adp.init(teststr);
    	Iterator<String> it = adp.iterator();
    	if(it.hasNext()){
    		System.out.println(it.next());
    	}
    	
    }
}
