package compression;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import java.io.IOException;

public class CompressionMapper extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text>
{
    private long cnt = 0l;
    private LongWritable outputKey = new LongWritable(cnt);
    public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
        outputKey.set(cnt++);//平均分布所有输出，避免单个reducer压力过大
        output.collect(outputKey, value);
    }
}
