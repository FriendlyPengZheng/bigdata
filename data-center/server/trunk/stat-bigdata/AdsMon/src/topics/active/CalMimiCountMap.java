package topics.active;

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
 * @brief  Calculate mimi count 
 */
public class CalMimiCountMap extends MapReduceBase
    implements Mapper<Text, Text, Text, LongWritable>
{
    private Text realKey = new Text();
    private LongWritable one = new LongWritable(1);

    // gameid,mimi
    //      =>
    // game,"all",-1,-1,-1 \t mimicount 
    public void map(Text key, Text value,
            OutputCollector<Text,LongWritable> output, 
            Reporter reporter) throws IOException
    {

        String gamemimi = key.toString();
        String[] keys = gamemimi.split(",");

        if (keys.length != 2) {
            System.err.printf("error gameid,mimi format: key = %s\n", gamemimi);
            return;
        }

        String gameid = keys[0];
        realKey.set(String.format("%s,%s,%s,%d,%d", 
                    gameid, "all", -1, -1 , -1));

        output.collect(realKey, one);
    }
}
