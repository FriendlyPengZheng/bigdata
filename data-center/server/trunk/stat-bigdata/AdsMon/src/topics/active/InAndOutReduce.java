package topics.active;

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
 * @brief  reduce record to format::= gameid,mimi tad region login_times sessionlength
 *              login_times     one day one time at most
 */
public class InAndOutReduce extends MapReduceBase
    implements Reducer<Text, Text, Text, Text>
{
    private Text realKey = new Text();
    private Text realValue = new Text();
    private JobConf conf = null;
    private String onlinePday = null;
    private HashMap<String, GameSummary> gamestat = new HashMap<String, GameSummary>();
    public void configure(JobConf job) {
        conf = job;
    }

    // receive format ::=
    //      mimi in timestamp game tad region
    //      mimi out timestamp game
    public void reduce(Text key, Iterator<Text> values,
            OutputCollector<Text,Text> output, Reporter reporter) throws IOException {

        String mimi = key.toString();
        //System.err.println("recv mimi: " + mimi);
        gamestat.clear();
        GameSummary allgs = new GameSummary();
        allgs.gameid = "-1";
        gamestat.put("-1", allgs);
        while (values.hasNext()) {
            String value = values.next().toString();
            //System.err.println("recv value: " + value);
            String[] items = value.split("\t");
            int timestamp = 0;
            try {
                timestamp = Integer.parseInt(items[1]);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            String gameid = items[2];
            GameSummary gs = null;
            if (gamestat.containsKey(gameid)) {
                gs = gamestat.get(gameid);
            } else {
                gs = new GameSummary();
                gs.gameid = gameid;
                gamestat.put(gameid, gs);
            }

            switch (items.length)
            {
                case 5 : // in
                    gs.addTad(items[3]);
                    gs.addRegionCode(items[4]);
                    gs.addLogEvent(timestamp, 0);
                    allgs.addTad(items[3]);
                    allgs.addRegionCode(items[4]);
                    allgs.addLogEvent(timestamp, 0);
                    break;
                case 3 : // out
                    gs.addLogEvent(timestamp, 1);
                    allgs.addLogEvent(timestamp, 1);
                    break;
                default :
                    System.err.println("error format: " + value);
                    break;
            }  /* end of switch */
        } /* end of while */

        Set<String> games = gamestat.keySet();
        Iterator<String> git = games.iterator();
        while (git.hasNext()) {
            String gid = git.next();
            GameSummary gs = gamestat.get(gid);

            String tad = gs.getTad();
            String regioncode = gs.getRegionCode();
            int login_count = gs.getLoginCount();
            int sessionlength = gs.getSessionLength();
            //int avgLength = sessionlength / dayInterval;
            int avgLength = sessionlength;

            realKey.set(String.format("%s,%s", gid, mimi));
            realValue.set(String.format("%s\t%s\t%d\t%d",
                        tad, regioncode, login_count, avgLength));

            //System.err.printf("reduce out: %s | %s\n", realKey.toString(), realValue.toString());

            output.collect(realKey, realValue);
        }
    }

    //public void close() {}
}

class GameSummary
{
    public String gameid = "0";
    // tad => times
    private TreeMap<String, Integer> tad_count = new TreeMap<String, Integer>();
    // regioncode => times
    private TreeMap<String, Integer> region_count = new TreeMap<String, Integer>();
    // timestamp => in(0)/out(1)
    private TreeMap<Integer, Integer> time_type = new TreeMap<Integer, Integer>();
    // unique day token
    private TreeSet<Integer> dayset = new TreeSet<Integer>();

    public void addTad(String tad) {
        if (tad_count.containsKey(tad)) {
            tad_count.put(tad, tad_count.get(tad) + 1);
        } else {
            tad_count.put(tad, 1);
        }
    }
    public void addRegionCode(String region) {
        if (region_count.containsKey(region)) {
            region_count.put(region, region_count.get(region) + 1);
        } else {
            region_count.put(region, 1);
        }
    }

    // type = 0 : login
    // type = 1 : logout
    public void addLogEvent(int timestamp, int type) {
        // logout must consider when compute login frequncy
        // add tz offset +8h, one day one time at most
        dayset.add((timestamp + 28800)/86400);

        time_type.put(timestamp, type);
    }

    public int getLoginCount() {
        return dayset.size();
    }

    public String getTad() {
        String Tad = "unknown";
        int Max = 0;
        Set<String> ts = tad_count.keySet();
        Iterator<String> tsit = ts.iterator();
        while (tsit.hasNext()) {
            String tmp = tsit.next();
            int count = tad_count.get(tmp);
            if (count > Max) {
                Max = count;
                Tad = tmp;
            }
        }
        return Tad;
    }
    public String getRegionCode() {
        String Region = "0";
        int Max = 0;
        Set<String> rcs = region_count.keySet();
        Iterator<String> rcsit = rcs.iterator();
        while (rcsit.hasNext()) {
            String tmp = rcsit.next();
            int count = region_count.get(tmp);
            if (count > Max) {
                Max = count;
                Region = tmp;
            }
        }
        return Region;
    }

    public int getSessionLength() {
        Set<Integer> times = time_type.keySet();
        Iterator<Integer> tit = times.iterator();
        int sumlength = 0;
        boolean reap = true;
        int lastlogintime = 0;
        int lasttype = 1;
        int i = 0;
        while (tit.hasNext()) {
            int timestamp = tit.next();
            int type = time_type.get(timestamp);
            if (i++ == 0) {
                if (type == 1) {
                    sumlength += 60;
                    reap = true;
                } else {
                    lastlogintime = timestamp;
                    reap = false;
                }
            } else {
                if (type == 0 && reap == true) {
                    lastlogintime = timestamp;
                    reap = false;
                }
                else if (type == 0 && reap == false) { // ignore continual login
                    // but, if login interval > 5h, revise 1 minute
                    int tmpdiff = timestamp - lastlogintime;
                    if (tmpdiff > 5*3600) {
                        sumlength += 60;
                        lastlogintime = timestamp;
                    }
                } 
                else { // logout
                    if (lasttype == 0) {
                        int timediff = timestamp - lastlogintime;
                        sumlength += timediff > 3600 * 5 ? 7200 : timediff;
                        reap = true;
                    } else {
                        // ignore continual logout
                    }
                }
            }
            lasttype = type;

            //System.out.printf("sum %d last: %d type: %d\n", sumlength, timestamp, type);
        }
        sumlength += reap == false ? 60 : 0;

        return sumlength;
    }
}
