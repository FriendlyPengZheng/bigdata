package topics.boss;
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

public class MBConsumeDistrReduce extends MapReduceBase
        implements Reducer<Text, ConsumeDistrWritable, Text, Text>
{
    Text realKey = new Text();
    Text realValue = new Text();
    ConsumeDistrWritable sumdistr = new ConsumeDistrWritable();

    public void reduce(Text key, Iterator<ConsumeDistrWritable> values,
            OutputCollector<Text, Text> output, Reporter reportor) throws IOException
    {
        sumdistr.reset();
        while (values.hasNext()) {
            ConsumeDistrWritable dv = values.next();
            sumdistr.allUniq += dv.allUniq;
            sumdistr.allCost += dv.allCost;
            //sumdistr.allmbvip += dv.allmbvip;
        }
        realValue.set(sumdistr.toStringUniqCost());
        output.collect(key, realValue);
    }
}
