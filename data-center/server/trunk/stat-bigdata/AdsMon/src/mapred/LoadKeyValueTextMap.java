package mapred;

import util.*;

import java.io.*;
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
import org.apache.hadoop.filecache.DistributedCache;

// split only by first \t
// out format:: Text Text

public class LoadKeyValueTextMap extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, Text>
{
    private Text realKey = new Text();
    private Text realValue = new Text();

    private HashSet<Integer> joinData = new HashSet<Integer>();
    private JobConf jobConf = null;
    public void configure(JobConf job) {
        this.jobConf = job;
	try
	{
		Path[] cacheFile = DistributedCache.getLocalCacheFiles(job);
		if(cacheFile != null && cacheFile.length > 0)
		{
			String line;
			String[] tokens;

			BufferedReader joinReader = new BufferedReader(new FileReader(cacheFile[0].toString()));
			try
			{//文件格式： gid,uid cost
				while((line = joinReader.readLine()) != null)
				{
					tokens = line.split(",|\t");	
					if(tokens.length < 3)
					{
						continue;
					}
					if(MathUtil.isNumber(tokens[1]))
					{
						joinData.add(Integer.valueOf(tokens[1]));
					}
				}
				System.err.println("while end joinData size " + joinData.size());

			}
			catch(Exception ex)
			{
				System.err.println("Exception occured  " + ex);
			}
			finally
			{
				joinReader.close();
			}
			
		}
		else
		{
			if(cacheFile == null)
				System.err.println("cacheFile is null");
			else
				System.err.println("cacheFile length < 0");
		}
	}catch(IOException e)
	{
		System.err.println("Exception reading DistributedCache: " + e);
    	}

    }

    public void map(LongWritable key, Text value,
            OutputCollector<Text,Text> output, Reporter reporter) 
        throws IOException  
    {
        String line = value.toString();
        String[] items = line.split("\t", 2);

        String secondValue = null;
        if (items.length != 2) { secondValue = ""; }
        else { secondValue = items[1]; }


	if(joinData.isEmpty() || joinData.contains(Integer.valueOf(items[0])))
	{//过滤掉不需要传递给reduce的key
		realKey.set(items[0]);
       		realValue.set(secondValue);

        	output.collect(realKey, realValue);
    	}
    }
}
