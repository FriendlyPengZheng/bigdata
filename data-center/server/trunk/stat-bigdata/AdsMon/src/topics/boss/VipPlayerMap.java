package topics.boss;
import io.*;
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
 * @brief  map process VIP records, VIP need process vip_type
 */
public class VipPlayerMap extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, PairSumWritable>
{
    private Text realKey = new Text();
    private PairSumWritable pairsum = new PairSumWritable();
    //private Text realValue = new Text();
    //public void configure(JobConf job)
    //{
    //}

    private VipParser vipp = new VipParser();
    // map => game(-1),viptype(-1),viplen(-1),mimi vip_num vip_times
    public void map(LongWritable key, Text value,
            OutputCollector<Text, PairSumWritable> output, Reporter reportor) throws IOException
    {
        String line = value.toString();
        String[] items = line.split("\t");
        String mimi = null;
        String gameid = null;
        int vipusertype = 9;
        int viplen = 0;
        String[] games = new String[] {"-1", "0"};
        int[] viptypes = new int[] {-1, 9};
        int[] viplens = new int[]{-1, 0};
        switch (items.length)
        {
            case VipParser.collumCount : // vip
                vipp.init(items);
                if (!vipp.isValid()) { return; }
                mimi = vipp.getMimi();
                gameid = vipp.getGameid();
                vipusertype = vipp.getVipUserType();
                viplen = vipp.getVipInterval();
                games[1] = gameid;
                viptypes[1] = vipusertype;
                viplens[1] = viplen;

                int vipcost = vipp.getVipCost();
                // key1 => accumlated cost sum
                pairsum.key1 = vipcost;
                // key2 => vip times
                pairsum.key2 = 1;
                break;
            default :
                System.err.println("error vip format: " + line);
                return;
        }  /* end of switch */

        for(String g: games) {
            for(int t: viptypes) {
                for(int l = 0; l < viplens.length; ++l) {
                    // filter: action=6,length=-30 => viplen = -1
                    if (l == 1 && viplens[l] == -1) { continue; }
                    realKey.set(String.format("%s,%d,%d,%s", g, t, viplens[l], mimi));
                    output.collect(realKey, pairsum);
                    //System.err.printf("%s %d %d\n", realKey.toString(), pairsum.key1, pairsum.key2);
                }
            }
        }
        //// for each game
        //realKey.set(String.format("%s,%d,%d,%s", gameid, vipusertype, viplen, mimi));
        //output.collect(realKey, pairsum);

        //// for all game 
        //realKey.set(String.format("-1,%d,%d,%s", vipusertype, viplen, mimi));
        //output.collect(realKey, pairsum);
    }

    //public void close() {}
}
