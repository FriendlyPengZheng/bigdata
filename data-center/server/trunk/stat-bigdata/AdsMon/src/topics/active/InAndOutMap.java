package topics.active;
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
 * @brief  Map process for login & logout record
 */
public class InAndOutMap extends MapReduceBase 
    implements Mapper<LongWritable, Text, Text, Text>
{
    private Text realKey = new Text();
    private Text realValue = new Text();
    private IPDistr ipdistr = null;

    private IPCountryDistr ip_country_distr = null;

    // 台湾的地区码
    private static final int REGION_CODE_TW = 710000;
    // 香港的
    private static final int REGION_CODE_HK = 810000;
    // 澳门的
    private static final int REGION_CODE_MC = 820000;
    // 海外其他国家
    private static final int REGION_CODE_OS = 830000;

    public void configure(JobConf job) {
        try { 
            String ipdistr_dburi = job.get("ip.distr.dburi");
            if (ipdistr_dburi == null) {
                System.err.println("cannot get ip distr dburi for login record");
                return;
            }
            ipdistr = new IPDistr(ipdistr_dburi);


            ip_country_distr = new IPCountryDistr(ipdistr_dburi);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private LoginParser inparser = new LoginParser();
    private LogoutParser outparser = new LogoutParser();
    public void map(LongWritable key, Text value,
            OutputCollector<Text,Text> output, Reporter reporter) throws IOException {

        //byte[] raw = value.getBytes();
        //for (int i = 0 ; i < raw.length ; ++i) {
            //System.out.printf("%02x", raw[i]);
        //} /* - end for - */
        //System.out.println();
        
        String line = value.toString();
        //raw = line.getBytes();
        //for (int i = 0 ; i < raw.length ; ++i) {
            //System.out.printf("%02x", raw[i]);
        //} /* - end for - */
        //System.out.println();

        String[] items = line.split("\t", -1);
        //System.err.println("process line: " + line + "\ncollums num: " + items.length);

        switch (items.length)
        {
            case 6 :
                //inparser.init(items);
                //if (!inparser.isValid()) { 
                if (!inparser.init(items)) {
                    System.err.println("error login format: " + line);
                    return; 
                }

                String ip = inparser.getIp();
                int regioncode = ipdistr.getIPProvinceCode(ip);
                if (0 == regioncode)
                {
                    // 国内ip库未匹配到数据
                    // 需要去国外ip库找
                    String country_code = ip_country_distr.getIPCountryCode(ip);
                    if (country_code.equals("0"))
                    {
                        regioncode = 0;
                    }
                    else if (country_code.equals("HK"))
                    {
                        regioncode = REGION_CODE_HK;
                    }
                    else if (country_code.equals("MO"))
                    {
                        regioncode = REGION_CODE_MC;
                    }
                    else if (country_code.equals("TW"))
                    {
                        regioncode = REGION_CODE_TW;
                    }
                    else
                    {
                        regioncode = REGION_CODE_OS;
                    }
                }

                String tad = inparser.getTad();

                realKey.set(inparser.getMimi());

                int gameid = inparser.getNumGameid();

                //System.err.printf("gameid %d | regioncode: %d\n", gameid, regioncode);

                realValue.set(String.format("in\t%s\t%d\t%s\t%d", inparser.getTimestamp(), 
                            gameid, tad, regioncode));
                break;

            case 5 :
                if (!outparser.init(items)) { 
                    System.err.println("error logout format: " + line);
                    return; 
                }

                realKey.set(String.format("%d", outparser.getNumMimi()));
                realValue.set(String.format("out\t%d\t%d", outparser.getLogoutTimeNum(), 
                            outparser.getNumGameid()));
                break;
            default :
                System.err.println("error format: not login / logout" + line);
                return;
        }  /* end of switch */

        //System.err.printf("out: %s | %s\n", realKey.toString(), realValue.toString());
        output.collect(realKey, realValue);
    }

    //public void close() {}
}
