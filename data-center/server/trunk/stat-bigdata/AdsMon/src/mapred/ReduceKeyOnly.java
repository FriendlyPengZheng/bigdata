package mapred;

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
 * @brief  fetch only one value for each key, ignore others
 */
public class ReduceKeyOnly<ValueType> extends MapReduceBase 
    implements Reducer<Text, ValueType, Text, NullWritable>
{
    public void reduce(Text key, Iterator<ValueType> values,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {

        output.collect(key, NullWritable.get());

        //while (values.hasNext()) {
        //}
    }
}

