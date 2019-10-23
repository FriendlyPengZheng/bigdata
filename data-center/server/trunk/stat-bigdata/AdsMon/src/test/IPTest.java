package test;
import util.*;

public class IPTest {
    //hadoop jar TestJar.jar test.IPTest 33588248 83904574 1765253300 1950159420 3869724382 0 201326592
    public static void main(String args[]) throws Exception
    {
        String ipdburi = "mysql://root:pwd@60@10.1.1.60/db_ip_distribution_12_Q1?useUnicode=true&characterEncoding=utf8";
        IPCountryDistr ipcd = new IPCountryDistr(ipdburi);

        for(String arg: args) {
            String ccode = ipcd.getIPCountryCode(arg);
            System.out.printf("ip: %s, countrycode: %s, coutryName:%s\n", 
                    arg, ccode, ipcd.getIPCountryNameByCode(ccode));
        }
        System.out.println("handle ip count: " + args.length);
    }
}
