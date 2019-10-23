package topics.boss;
import util.*;

import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

class CostCount {
    public TreeMap<Integer, Integer> cc = new TreeMap<Integer, Integer>();
    public int countsum = 0;
    public long sumall = 0;

    public void add(int cost, int count) {
        int tmp = 0;
        if (cc.containsKey(cost)) {
            tmp = cc.get(cost);
        }
        cc.put(cost, tmp + count);
        countsum += count;
        sumall += count * cost;
    }
}

class QPoint {
    // num index  from 0
    // first num idx
    public int base = 0;
    // num count sum
    public int num = 0;
    // odd: false , even: true
    public boolean mode = false;
    // Quartile point idx (from 0)
    // when mode = false: Qidx indicate the middle num index
    // when mode = true:  Qidx indicate the first middle num index
    //                  real num is the (a[Qidx] + a[Qidx + 1]) / 2
    public int Qidx = 0;
    // corresponding Quartile value
    public int value = -1;

    //public void reset() {
        //base = 0;
        //num = 0;
        //mode = false;
        //Qidx = 0;
        //value = -1;
    //}

    public void setNum(int n) {
        num = n;
        if (num <= 1) {
            Qidx = base;
            mode = false;
            return;
        }

        if ((n & 1) == 1) { // odd
            setMode(false);
        } else { // even
            setMode(true);
        }
    }
    public void setMode(boolean m) {
        mode = m;
        if (!m) { // odd
            Qidx = base + (num - 1)/2;
        } else { // even
            Qidx = base + (num)/2 - 1;
        }
    }
}

// map for compute multiple cost statistical indicator
// min Quartile(Q1 Q2/median Q3) max
//@SuppressWarnings("unchecked")
public class StatGameCostReduce extends MapReduceBase
        implements Reducer<GameTypeCostKey, LongWritable, Text, Text>
{
    private JobConf conf = null;
    private Text realKey = new Text();
    private Text realValue = new Text();

    // < gameid,bosstype(all(1)/mb(2)/vip(3)) => < costnum => costcount> >
    private HashMap<String, CostCount> gameboss_costcount =
        new HashMap<String, CostCount>();
    public void configure(JobConf job) {
        conf = job;
    }

    // game,bosstype,costnum uniqplayers
    //      =>
    // game,bosstype,costnum uniqplayers(aggregate sum)
    public void reduce(GameTypeCostKey key, Iterator<LongWritable> values,
            OutputCollector<Text, Text> output, Reporter reportor) throws IOException
    {
        String gamebosstype = key.getSubKey();
        int cost = key.cost.get();
        int sum = 0;
        while (values.hasNext()) {
            sum += values.next().get();
        } /* - end while - */
        realKey.set(key.toString());
        realValue.set("" + sum);
        output.collect(realKey, realValue);

        CostCount costcount = null;
        if (gameboss_costcount.containsKey(gamebosstype)) {
            costcount = gameboss_costcount.get(gamebosstype);
            costcount.add(cost, sum);
        } else {
            costcount = new CostCount();
            costcount.add(cost, sum);
            gameboss_costcount.put(gamebosstype, costcount);
        }
    }

    // out::= game,bosstype min Q1 Q2 Q3 max
    public void close() throws IOException{
        String outdirpath = conf.get("mapred.output.dir", "");
        if (outdirpath.equals("")) {
            System.out.println("fck, empty **mapred.output.dir**");
            return;
        }

        FileSystem fs = FileSystem.get(conf);
        Path outpath = new Path(outdirpath + "/stat-out-" + (int)(Math.random() * 100000));
        FSDataOutputStream out = fs.create(outpath);
        OutputStreamWriter outwriter = new OutputStreamWriter(out, "UTF-8");

        Set<String> gamebossset = gameboss_costcount.keySet();
        Iterator<String> gbsit = gamebossset.iterator();
        while (gbsit.hasNext()) {
            // process for each game,bosstype
            String gameboss = gbsit.next();
            CostCount costcount = gameboss_costcount.get(gameboss);
            Integer min = costcount.cc.firstKey();
            Integer max = costcount.cc.lastKey();
            //int avgcost = (int)(costcount.sumall / costcount.countsum);

            int sumcount = costcount.countsum;
            int halfcount = sumcount / 2;
            QPoint Q1idx = new QPoint();
            Q1idx.base = 0;
            Q1idx.setNum(halfcount);

            QPoint Q2idx = new QPoint();
            Q2idx.base = 0;
            Q2idx.setNum(sumcount);

            QPoint Q3idx = new QPoint();
            Q3idx.base = halfcount + 1;
            Q3idx.setNum(halfcount);

        if (sumcount == 1) {
            Q1idx.value = min;
            Q2idx.value = min;
            Q3idx.value = min;
        } else {
            // compute Q1 Q2 Q3
            TreeMap<Integer, Integer> ccmap = costcount.cc;
            Set<Integer> costSet = ccmap.keySet();
            Iterator<Integer> csIt = costSet.iterator();
            int accumCount = -1;
            int lastCost = 0;
            boolean evenMean = false;
            while (csIt.hasNext()) {
                Integer cost = csIt.next();
                Integer count = ccmap.get(cost);
                accumCount += count;
                if (accumCount >= Q1idx.Qidx && Q1idx.value == -1) {
                    if (Q1idx.mode && accumCount == Q1idx.Qidx) { // even && just equal
                        evenMean = true;
                    }
                    else if (Q1idx.mode && evenMean) {
                        Q1idx.value = (cost + lastCost) / 2;
                        evenMean = false;
                    } else {
                        Q1idx.value = cost;
                    }
                }

                if (accumCount >= Q2idx.Qidx && Q2idx.value == -1) {
                    if (Q2idx.mode && accumCount == Q2idx.Qidx) { // even && just equal
                        evenMean = true;
                    }
                    else if (Q2idx.mode && evenMean) {
                        Q2idx.value = (cost + lastCost) / 2;
                        evenMean = false;
                    } else {
                        Q2idx.value = cost;
                    }
                }

                if (accumCount >= Q3idx.Qidx && Q3idx.value == -1) {
                    if (Q3idx.mode && accumCount == Q3idx.Qidx) { // even && just equal
                        evenMean = true;
                    }
                    else if (Q3idx.mode && evenMean) {
                        Q3idx.value = (cost + lastCost) / 2;
                        evenMean = false;
                        break; // no need more iterator
                    } else {
                        Q3idx.value = cost;
                        break; // no need more iterator
                    }
                }
                lastCost = cost;
            } /* - end while - */
            if (Q3idx.value == -1) { // last border revise
                Q3idx.value = max;
            }
        } // end of if

            String outresult = String.format("%s\t%d\t%d\t%d\t%d\t%d\n", gameboss,
                    min, Q1idx.value, Q2idx.value, Q3idx.value, max);
            //out.writeChars(outresult);
            outwriter.write(outresult, 0, outresult.length());
        } /* - end while - */

        outwriter.close();
    }
}
