package mapred;
import io.*;

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
import org.apache.hadoop.mapreduce.Counter;

public class MapKeyOnly<K,V> extends MapReduceBase 
    implements Mapper<K, V, K, NullWritable>
{
    public void map(K key, V value,
            OutputCollector<K, NullWritable> output, Reporter reportor) throws IOException
    {
        output.collect(key, NullWritable.get());
    }
}
