package test;
import util.*;

import java.util.*;

public class AdTest
{
    public static void main(String args[]) throws Exception
    {
        AdParser adp = new AdParser("conf/tadmap.conf");

        //adp.init("7");
        //Iterator<String> adit =  adp.iterator();
        //while (adit.hasNext()) {
            //System.out.println(adit.next());
        //}
        //adp.init("#http://www.4399.com/news/ab.html");
        //adp.init("#www.4399.com/");

        System.out.printf("args len: %d\n", args.length);
        adp.init(args[0]);
        Iterator<String> adit =  adp.iterator();
        while (adit.hasNext()) {
            System.out.println(adit.next());
        }
    }
}
