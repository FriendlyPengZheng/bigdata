package ad.active;
import util.*;

import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class LoadDayKeepDistr extends LoadAds
{
    protected String[] getFormalValues(String line) throws Exception
    {
        if (dryrun) { System.out.printf("Line: %s\n", line); }

        // format: regtime,dayinterval,gameid,ad \t keepers
        String[] cols = line.split("\t|,", -1);

        String ad = cols[3];
        Integer adid = getAdid(ad);
        if (adid == null) { adid = insertAd(ad); }
        cols[3] = adid.toString();

        return cols;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new LoadDayKeepDistr(), args);
        System.exit(ret);
    }

}
