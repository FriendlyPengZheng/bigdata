package topics.active;
import util.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;

public class LoadActiveDistr  extends LoadMYDriver
{
    protected static int IGNORE_ERROR_CODE = -128;

    private AdParser adp = new AdParser();

    private HashMap<String, Integer> m_admap = new HashMap<String, Integer>();

    private Connection mysqlConn = null;
    private Statement statement = null;
    protected void configure(Configuration conf) throws Exception{
        String dimDBUri = conf.get("result.mysql.uri", "");
        if (dimDBUri.equals("")) {
            System.err.println("[ERROR] cannot get dimDBUri ");
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("cannot find mysql lib");
            e.printStackTrace();
            throw e;
        }
        MysqlUriParser myp = new MysqlUriParser(dimDBUri);
        mysqlConn = DriverManager.getConnection(myp.getJdbcURL(),
                myp.getUser(), myp.getPassword());
        statement = mysqlConn.createStatement();

        ResultSet adrs = statement.executeQuery("Select adid,adname from v_dim_ad where end = 1 " +
                "or adname in ('all', 'none', 'unknown');");
        int id = -1;
        String name = null;
        while (adrs.next()) {
            id = adrs.getInt(1);
            name = adrs.getString(2);
            m_admap.put(name, id);
        }
        adrs.close();
    }

    protected Integer getAdFromDb(String adname) {
        String escStr = DBUtil.escapeString(adname);
        String sqlstr = String.format("select adid from t_dim_ad where adname='%s'", escStr);
        if (dryrun) { System.out.println(sqlstr); }

        Integer id = null;
        try {
            ResultSet sharedRS = statement.executeQuery(sqlstr);
            if (sharedRS.next()) {
                id = sharedRS.getInt(1);
            }
            sharedRS.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return IGNORE_ERROR_CODE;
        }

        return id;
    }

    protected int insertAd(String adname) {

        String [] items = new String[]{"", "", "", ""};
        String name = null;
        if (adname.startsWith("#")) {
            name = adname.substring(1, adname.length());

            adp.init(adname);
            Iterator<String> adit = adp.iterator();
            int idx = 0;
            while (adit.hasNext()) {
                String adi = adit.next();
                items[idx++] = DBUtil.escapeString(adi.substring(1,adi.length()));
            }
        } else {
            name = adname;

            String[] tmps = adname.split("\\.");
            int tmpscnt = tmps.length;
            int i = 0 ;
            for (; i < tmpscnt && i < 3 ; ++i) {
                items[i] = tmps[i];
            }

            if (tmpscnt > 3) {
                StringBuilder sb = new StringBuilder();
                for (int j = i ; j < tmpscnt ; ++j) {
                    sb.append(tmps[j]);
                    sb.append(".");
                }
                sb.deleteCharAt(sb.length() - 1);
                items[3] = sb.toString();
            }
        }

        String escStr = DBUtil.escapeString(name);
	String escItems = DBUtil.escapeString(items[3]);
        String sqlstr = String.format("insert into t_dim_ad(adname,part1,part2,part3,part4) "+
                " values ('%s', '%s', '%s', '%s', '%s')", escStr, items[0], items[1], items[2], escItems);
        if (dryrun) { System.out.println(sqlstr); }

        try {
            statement.executeUpdate(sqlstr);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.printf("adname: %s\nsqlstr: %s\n", adname, sqlstr);
            return IGNORE_ERROR_CODE;
        }

        Integer newid = getAdFromDb(name);
        m_admap.put(name, newid);

        return newid;
    }

    protected int getAdid(String adname) {
        String tmpname = adname;
        if (adname.startsWith("#")) {
            tmpname = adname.substring(1);
        }
        // fetch in local cache
        Integer adid = m_admap.get(tmpname);
        // fetch from db store
        adid = getAdFromDb(tmpname);
        // make new adid
        if (adid == null) {
            adid = insertAd(adname);
        }
        return adid;
    }

    protected String[] getFormalValues(String line) throws Exception
    {
        //gameid,adname(|-1),regioncode,freq-point,time-point uniq-players
        String[] items = line.split("\t|,", -1);

        String adname = items[1];

        //if (!(adname.equals("-1") || adname.equals("0"))) {
        Integer adid = getAdid(adname);
        if (adid == IGNORE_ERROR_CODE) {
            adid = getAdFromDb(adname);
            if (adid == IGNORE_ERROR_CODE) {
                return null;
            }
        }
        items[1] = adid.toString();
        //}

        return items;
    }

    public static void main(String args[]) throws Exception
    {
        int ret = ToolRunner.run(new Configuration(), new LoadActiveDistr(), args);
        System.exit(ret);
    }
}
