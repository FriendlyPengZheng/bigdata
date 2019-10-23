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

/**
 * @brief  simple aggregate all games and players consumption records
 *      reduce record to format::= gameid,mimi mb_num mb_times vip_num vip_times vipuser_type 
            *_num       denote mibi cost in unit fen
            *_times     denote mb/vip cost times
            gameid      include all(-1) already
 */
public class BossReduce extends MapReduceBase
        implements Reducer<Text, Text, Text, Text>
{
    private Text realValue = new Text();
    //public void configure(JobConf job) {}

    public void reduce(Text key, Iterator<Text> values,
            OutputCollector<Text, Text> output, Reporter reportor) throws IOException
    {
        int sum_mb_num = 0;
        int sum_mb_times = 0;
        int sum_vip_num = 0;
        int sum_vip_times = 0;
        int acc_usertype = 9; // 9 => unvipuser
        while (values.hasNext()) {
            String line = values.next().toString();
            String[] items = line.split("\t");
            if (items.length != 5) {
                System.err.println("error boss reduce format: " + line);
                continue;
            }

            try {
                int mb_num = Integer.parseInt(items[0]);
                int mb_times = Integer.parseInt(items[1]);
                int vip_num = Integer.parseInt(items[2]);
                int vip_times = Integer.parseInt(items[3]);
                int usertype = Integer.parseInt(items[4]);

                sum_mb_num += mb_num;
                sum_mb_times += mb_times;
                sum_vip_num += vip_num;
                sum_vip_times += vip_times;
                if (acc_usertype > usertype) { acc_usertype = usertype; }
            } catch (Exception ex) {
                System.err.println("error boss reduce format: " + line);
                ex.printStackTrace();
                continue;
            }
        }
        realValue.set(String.format("%d\t%d\t%d\t%d\t%d", sum_mb_num,
                    sum_mb_times, sum_vip_num, sum_vip_times, acc_usertype));
        output.collect(key, realValue);
    }

    //public void close() {}
}
