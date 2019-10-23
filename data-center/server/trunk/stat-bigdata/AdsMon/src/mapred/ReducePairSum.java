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
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

/**
 * @brief  recieve pairsum => text: key1 \t key2
 */
public class ReducePairSum extends MapReduceBase 
    implements Reducer<Text, PairSumWritable, Text, PairSumWritable>
{
    //private Text realValue = new Text();
    PairSumWritable sum = new PairSumWritable();
    public void reduce(Text key, Iterator<PairSumWritable> values,
            OutputCollector<Text,PairSumWritable> output, Reporter reporter) throws IOException {
        sum.reset();
        while (values.hasNext()) {
            PairSumWritable ps = values.next();
            sum.key1 += ps.key1;
            sum.key2 += ps.key2;
        }
        output.collect(key, sum);
        //realValue.set(sum.toString());
        //output.collect(key, realValue);
    }
}

