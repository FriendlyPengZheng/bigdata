package util;

import java.util.*;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class LoadAds extends LoadMYDriver {
	protected static int IGNORE_ERROR_CODE = -128;
	protected Configuration conf = null;
	protected HashMap<String, Integer> urlHM = new HashMap<String, Integer>(50,
			0.8f);
	protected HashMap<String, Integer> adHM = new HashMap<String, Integer>(50,
			0.8f);

	private AdParser adp = new AdParser();
	private String dimDbUri = null;
	private Connection mysqlConn = null;
	private Statement statement = null;

	protected void configure(Configuration conf) throws Exception {
		dimDbUri = conf.get("result.mysql.uri");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("cannot find mysql lib");
			e.printStackTrace();
			throw e;
		}
		MysqlUriParser myp = new MysqlUriParser(dimDbUri);
		mysqlConn = DriverManager.getConnection(myp.getJdbcURL(),
				myp.getUser(), myp.getPassword());
		statement = mysqlConn.createStatement();

		// statement.setEscapeProcessing(true);

		ResultSet urlrs = statement
				.executeQuery("Select urlid,urlname from t_dim_url");
		int urlid = -1;
		String urlname = null;
		while (urlrs.next()) {
			urlid = urlrs.getInt(1);
			urlname = urlrs.getString(2);
			urlHM.put(urlname, urlid);
		}
		urlrs.close();

		ResultSet adrs = statement
				.executeQuery("Select adid,adname from t_dim_ad");
		int adid = -1;
		String adname = null;
		while (adrs.next()) {
			adid = adrs.getInt(1);
			adname = adrs.getString(2);
			adHM.put(adname, adid);
		}
		adrs.close();
	}

	protected Integer getUrlid(String url) {
		return urlHM.get(url);
	}

	protected Integer getAdid(String ad) {
		String adname = null;
		if (ad.startsWith("#")) {
			adname = ad.substring(1, ad.length());
		} else {
			adname = ad;
		}
		return adHM.get(adname);
	}

	// sql like: select urlid from t_dim_url where urlname = 'xxxx';
	// fetch only first one
	protected int fillHMKV(HashMap<String, Integer> hm, String name, String sql)
			throws Exception {
		ResultSet sharedRS = statement.executeQuery(sql);
		sharedRS.next();

		Integer id = sharedRS.getInt(1);
		hm.put(name, id);

		sharedRS.close();
		return id;
	}

	protected int insertUrl(String urlname) throws Exception {
		String escStr = DBUtil.escapeString(urlname);
		String sqlstr = String.format(
				"insert into t_dim_url (urlname) values ('%s')", escStr);
		if (dryrun) {
			System.out.println(sqlstr);
		}
		try {
			statement.executeUpdate(sqlstr);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.printf("urlname: %s\nsqlstr: %s\n", urlname, sqlstr);
			return IGNORE_ERROR_CODE;
		}

		sqlstr = String.format(
				"select urlid from t_dim_url where urlname='%s'", escStr);
		if (dryrun) {
			System.out.println(sqlstr);
		}
		return fillHMKV(urlHM, urlname, sqlstr);
	}

	protected int insertAd(String adname) throws Exception {
		// adname should be splitted to 4 parts :
		// eg. a.b.c.d.e.f, split to => a | b | c | d.e.f
		// eg. #www.4399.com/news, split to => 4399.com | www.4399.com/news | ""
		// | ""
		String[] items = new String[] { "", "", "", "" };
		String name = null;
		if (adname.startsWith("#")) {
			name = adname.substring(1, adname.length());

			adp.init(adname);
			Iterator<String> adit = adp.iterator();
			int idx = 0;
			while (adit.hasNext()) {
				String adi = adit.next();
				items[idx++] = DBUtil.escapeString(adi.substring(1,
						adi.length()));
			}
		} else {
			name = adname;

			String[] tmps = adname.split("\\.");
			int tmpscnt = tmps.length;
			int i = 0;
			for (; i < tmpscnt && i < 3; ++i) {
				items[i] = tmps[i];
			}

			if (tmpscnt > 3) {
				StringBuilder sb = new StringBuilder();
				for (int j = i; j < tmpscnt; ++j) {
					sb.append(tmps[j]);
					sb.append(".");
				}
				sb.deleteCharAt(sb.length() - 1);
				items[3] = sb.toString();
			}
		}

		String escStr = DBUtil.escapeString(name);
		String escItems = DBUtil.escapeString(items[3]);
		String sqlstr = String.format(
				"insert into t_dim_ad(adname,part1,part2,part3,part4) "
						+ " values ('%s', '%s', '%s', '%s', '%s')", escStr,
				items[0], items[1], items[2], escItems);
		if (dryrun) {
			System.out.println(sqlstr);
		}
		try {
			statement.executeUpdate(sqlstr);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.printf("adname: %s\nsqlstr: %s\n", adname, sqlstr);
			return IGNORE_ERROR_CODE;
		}

		sqlstr = String.format("select adid from t_dim_ad where adname='%s'",
				escStr);
		if (dryrun) {
			System.out.println(sqlstr);
		}
		return fillHMKV(adHM, name, sqlstr);
	}

	// protected String[] getFormalValues(String line) throws Exception
	// {
	// }

	public static void main(String args[]) throws Exception {
		int ret = ToolRunner.run(new Configuration(), new LoadAds(), args);
		System.exit(ret);
	}
}
