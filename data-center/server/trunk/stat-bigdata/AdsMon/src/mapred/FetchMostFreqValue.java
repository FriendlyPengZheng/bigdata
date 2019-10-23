package mapred;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

/**
 * @brief  fetch Most Frequnce occured Text value
 */
public class FetchMostFreqValue<K> extends MapReduceBase
    implements Reducer<K, Text, K, Text>
{
    private TreeMap<String, Integer> kvmap = new TreeMap<String, Integer>();
    private Text realValue = new Text();

    public void reduce(K key, Iterator<Text> values,
            OutputCollector<K,Text> output, Reporter reporter) throws IOException {
        kvmap.clear();

        while (values.hasNext()) {
            String v = values.next().toString();
            Integer nnum = 1;
            if (kvmap.containsKey(v)) {
                nnum = kvmap.get(v) + 1;
            }
            kvmap.put(v, nnum);
        }

        if (kvmap.size() == 1) {
            realValue.set(kvmap.firstEntry().getKey());
        } else {
            Iterator<String> its = kvmap.keySet().iterator();
            String res = null;
            int max = 0;
            while (its.hasNext()) {
                String tmp = its.next();
                int tmpcnt = kvmap.get(tmp);
                if (tmpcnt > max) {
                    max = tmpcnt;
                    res = tmp;
                }
            } /* - end while - */
            realValue.set(res);
        }
        output.collect(key, realValue);
    }
}
