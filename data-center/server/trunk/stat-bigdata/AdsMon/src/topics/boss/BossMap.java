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
 * @brief  map process all boss (MB & VIP) records
 */
public class BossMap extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, Text>
{
    private Text realKey = new Text();
    private Text realValue = new Text();
    private MBProduct mbprdp = null;
    public void configure(JobConf job)
    {
        String fsmode = job.get("fs.default.name");
        String prdMapFile = !fsmode.equals("file:///") ? "prdmap.conf" :
            "/home/lc/hadoop/trunk/ads/mapred/conf/prdmap.conf" ;
        mbprdp = new MBProduct(prdMapFile);
    }

    private MBParser mbp = new MBParser();
    private VipParser vipp = new VipParser();
    // map => game,mimi mb_num mb_times vip_num vip_times vip_usertype
    public void map(LongWritable key, Text value,
            OutputCollector<Text, Text> output, Reporter reportor) throws IOException
    {
        String line = value.toString();
        String[] items = line.split("\t");
        String gameid = null;
        String mimi = null;
        switch (items.length)
        {
            case VipParser.collumCount : // vip
                vipp.init(items);
                if (!vipp.isValid()) { return; }
                mimi = vipp.getMimi();
                gameid = vipp.getGameid();
                int vipcost = vipp.getVipCost();
                int vipusertype = vipp.getVipUserType();
                realValue.set(String.format("0\t0\t%d\t1\t%d", vipcost, vipusertype));
                break;
            case MBParser.collumCount : // mb
                mbp.init(items);
                if (!mbp.isValid()) { return; }
                mimi = mbp.getMimiSrc();
                int productid = mbp.getProductId();
                gameid = "" + mbprdp.getGameid(productid);
                int mbcost = mbp.getMBCost();
                realValue.set(String.format("%d\t1\t0\t0\t9", mbcost));
                break;
            default :
                System.err.println("error boss format: " + line);
                return;
        }  /* end of switch */

        // for each game
        realKey.set(String.format("%s,%s", gameid, mimi));
        output.collect(realKey, realValue);

        // for all game 
        realKey.set(String.format("-1,%s", mimi));
        output.collect(realKey, realValue);
    }

    //public void close() {}
}
