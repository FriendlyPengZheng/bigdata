package test;
import util.*;

import java.util.*;
import java.util.Map.Entry;

public class CommTest {
    public static void main(String args[]) throws Exception
    {
        //System.out.println(DBUtil.escapeString(args[0]));

        if (args==null || args.length < 1) { Usage(); }

        String type = args[0];
        if ("distr".equals(type)) {
            DistrImpl di = new DistrImpl("3,6,10");
            Integer i = Integer.parseInt(args[1]);
            echo(di.getLow(i));
            echo(di.getLowInclude(i));
            echo(di.getHigh(i));
            echo(di.getHighInclude(i));
        } 
        else if ("mbp".equals(type)){
            MBProduct mbp = new MBProduct("../../conf/prdmap.conf");

            System.out.println(mbp.getGameid(1291090)); //0
            System.out.println(mbp.getGameid(905)); //0
            System.out.println(mbp.getGameid(906)); //7

            System.out.println(mbp.getGameid(200000)); //2
            System.out.println(mbp.getGameid(299999)); //2
            System.out.println(mbp.getGameid(300000)); //0
            System.out.println(mbp.getGameid(300001)); //3
        } 
        else {
            Usage();
        }
    }

    public static void Usage() {
        echo("Usage: Test type [args]");
        System.exit(0);
    }
    public static void echo(int xx) {
        System.out.println(xx);
    }
    public static void echo(String xx) {
        System.out.println(xx);
    }
}
