package ad.pages;
import util.*;

import java.util.*;
import java.io.IOException;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

public class MapPageUniqIp extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, LongWritable>
{
    private Text realKey = new Text();
    private LongWritable one = new LongWritable(1);
    private String processDay = null;
    private NgxLogParser nlp = new NgxLogParser();
    private AdParser adp = new AdParser();
    private UrlParser urlp = new UrlParser();

    private JobConf jobConf = null;
    public void configure(JobConf job) {
        this.jobConf = job;
        String fsname = jobConf.get("fs.default.name");
        String tadmapfile = fsname.equals("file:///") ?
            "/home/lc/hadoop/trunk/ads/mapred/conf/tadmap.conf" : "tadmap.conf";
        adp.configure(tadmapfile);
    }

    public void map (LongWritable key, Text value,
            OutputCollector<Text,LongWritable> output, Reporter reporter)
        throws IOException  {
        String logline = value.toString();
        nlp.init(logline);
        if (!nlp.isValid()) { return; }
        if (processDay == null) { processDay = nlp.getDay(); }

        String day = nlp.getDay();
        // filter out not processDay's requests
        if (!processDay.equals(day)) { return; }

        String ltad = nlp.getTad();
        String lurl = nlp.getUrl();
        String lip = nlp.getIp();
        if (ltad == null || ltad.equals("") || lurl == null || lurl.equals("")
                || lip == null || lip.equals("")) {
            System.err.println("error format: " + logline);
            return;
        }

        urlp.init(lurl);
        String hostpath = urlp.getHostPath();

        adp.init(ltad);
        if (adp.getLength() != 0) {
            Iterator<String> adit = adp.iterator();
            while (adit.hasNext()) {
                String lvlAd = adit.next();

                realKey.set(String.format("%s,%s,%s", lvlAd, hostpath, lip));
                output.collect(realKey, one);
            }
        } else {
            System.err.printf("error tad:%s\n", ltad);
        }

        // all channel accumulation
        realKey.set(String.format("all,%s,%s", hostpath, lip));
        output.collect(realKey, one);
    }

    //void close() {
    //}
}
