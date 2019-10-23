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
 * @brief  Calculate valid users
 */
public class ValidUserCountMap extends MapReduceBase
    implements Mapper<Text, Text, Text, LongWritable>
{
    private Text realKey = new Text();
    private LongWritable one = new LongWritable(1);

    // gameid,mimi tad region login_count onlinetime(sum)
    //      =>
    // game,"all",-1,-1,-1,uniqPlayers
    public void map(Text key, Text value,
            OutputCollector<Text,LongWritable> output, 
            Reporter reporter) throws IOException
    {

        String gamemimi = key.toString();
        String[] keys = gamemimi.split(",");
        String dvalue = value.toString();

        String[] values = dvalue.split("\t");
        if (keys.length != 2 || values.length < 4) {
            System.err.printf("error DimDistr format: key = %s, value = %s\n", 
                    gamemimi, dvalue);
            return;
        }

        String gameid = keys[0];

        int onlineTime = 0;
        try
        {
            onlineTime = Integer.parseInt(values[3]);
        }
        catch (Exception ex)
        {
            System.err.println("error on parse online time " + values[3]);
            ex.printStackTrace();
        } 

        if(onlineTime >= 3600)
        {
            realKey.set(String.format("%s,%s,%s,%d,%d", 
                        gameid, "all", -1, -1 , -1));

            output.collect(realKey, one);
        }
    }
}
