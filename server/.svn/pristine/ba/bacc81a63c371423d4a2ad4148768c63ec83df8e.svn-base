package compression;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import java.io.IOException;
import java.util.Iterator;

public class CompressionReducer extends MapReduceBase implements Reducer<LongWritable, Text, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();
    private String type = null;
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        type = job.get("type");
        if(type != null) mos = new MultipleOutputs(job);
    }

    public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        while(values.hasNext()) {
            if(type == null) {
                output.collect(values.next(), outputValue);
            } else {
                mos.getCollector(type, reporter).collect(values.next(), outputValue);
            }
        }
    }

    public void close() throws IOException
    {
        if(type != null) mos.close();
    }
}
