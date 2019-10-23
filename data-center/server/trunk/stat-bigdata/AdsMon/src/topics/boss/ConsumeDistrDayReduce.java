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

public class ConsumeDistrDayReduce extends MapReduceBase
        implements Reducer<Text, ConsumeDistrWritable, Text, ConsumeDistrWritable>
{
    Text realKey = new Text();
    Text realValue = new Text();
    ConsumeDistrWritable sumdistr = new ConsumeDistrWritable();

    public void reduce(Text key, Iterator<ConsumeDistrWritable> values,
            OutputCollector<Text, ConsumeDistrWritable> output, Reporter reportor) throws IOException
    {
        sumdistr.reset();
        while (values.hasNext()) {
            ConsumeDistrWritable dv = values.next();
            sumdistr.allUniq += dv.allUniq;
            sumdistr.allCost += dv.allCost;
            sumdistr.allmbvip += dv.allmbvip;
        }
        output.collect(key, sumdistr);
        //realValue.set(sumdistr.toString());
        //output.collect(key, realValue);
    }
}
