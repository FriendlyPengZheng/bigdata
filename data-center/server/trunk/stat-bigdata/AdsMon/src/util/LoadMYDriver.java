package util;

import java.io.*;
import java.util.ArrayList;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;

public class LoadMYDriver extends Configured implements Tool {
	// provide Formal collumn values, hook for child class
	// return null || 0-length array will skip the line
	protected String[] getFormalValues(String line) throws Exception {
		String[] cols = line.split("\t|,", -1);
		return cols;
	}

	protected void configure(Configuration conf) throws Exception {
	}

	protected boolean dryrun = false;
	protected boolean maxorset = true;// true表示取最大值,当设置了-s标志时取false

	public int run(String[] args) throws Exception {
		String clsName = this.getClass().getName();
		if (args.length < 4) {
			Usage(clsName);
		}

		Configuration conf = getConf();
		configure(conf);

		String tblFmt = args[1];
		String[] needleCols = tblFmt.split(",", -1);
		String[] specialCol = null;
		StringBuilder tmpsb = new StringBuilder();
		for (int i = 0; i < needleCols.length; ++i) {
			if (i == needleCols.length - 1) {
				String lastNeedle = needleCols[needleCols.length - 1];
				if (lastNeedle.startsWith("<")) {
					String spColStr = lastNeedle.substring(1,
							lastNeedle.length() - 2);
					specialCol = spColStr.split(";");
					int splen = specialCol.length;
					for (int j = 0; j < splen; ++j) {
						tmpsb.append(specialCol[j]);
						if (j != splen - 1) {
							tmpsb.append(",");
						}
					}
					tmpsb.append(')');
				} else {
					tmpsb.append(needleCols[i]);
				}
			} else {
				tmpsb.append(needleCols[i]);
				tmpsb.append(',');
			}
		}
		String updateTblFmt = tmpsb.toString();
		String addKeys = args[2];
		Path filePattern = new Path(args[3]);

		FileSystem fs = filePattern.getFileSystem(conf);
		Path[] paths = MiscUtil.listFiles(filePattern, conf);
		if (paths == null || paths.length == 0) {
			System.out.printf("cannot find files for path pattern: %s\n",
					filePattern.toString());
			System.exit(0);
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("LoadDistrDirver cannot find mysql lib");
			e.printStackTrace();
			throw e;
		}
		MysqlUriParser myp = new MysqlUriParser(args[0]);
		Connection mysqlConn = DriverManager.getConnection(myp.getJdbcURL(),
				myp.getUser(), myp.getPassword());
		Statement statement = mysqlConn.createStatement();

		if (args.length == 5 && args[4].equals("-z")) {
			dryrun = true;
		} else if (args.length == 5 && args[4].equals("-s")) {
			maxorset = false;
		}

		String sqlPrefix = String
				.format("%s values (%s", updateTblFmt, addKeys);
		StringBuilder sb = new StringBuilder();
		try {
			// if (!mysqlConn.isValid(3)) {
			// System.err.printf("error connect: %s\n", myp.getJdbcURL());
			// System.exit(-1);
			// }
			int totalLoadRows = 0;
			for (Path p : paths) {
				System.out.printf("proc file: %s ...\n", p.toString());

				FSDataInputStream fsin = fs.open(p);
				BufferedReader bfr = new BufferedReader(new InputStreamReader(
						fsin));
				String line = bfr.readLine();

				while (line != null && line.length() > 0) {
					sb.setLength(0);
					sb.append(sqlPrefix);
					String[] cols = null;
					try {
						cols = getFormalValues(line);
					} catch (Exception ex) {
						System.err.println("[Error] line: " + line);
						ex.printStackTrace();
						line = bfr.readLine();
						continue;
					}

					if (cols == null || cols.length == 0) {
						line = bfr.readLine();
						continue;
					}
					for (int i = 0; i < cols.length; ++i) {
						if (i == 0) { // check not null?
							if (addKeys.length() > 0) {
								sb.append(",'");
							} else {
								sb.append("'");
							}
						} else {
							sb.append(",'");
						}
						sb.append(cols[i]);
						sb.append("'");
					}
					sb.append(')');
					if (specialCol != null) {
						int spvlen = specialCol.length;
						int vlen = cols.length;
						// ? check spvlen < vlen
						sb.append(" ON DUPLICATE KEY UPDATE ");
						for (int i = vlen - spvlen, j = 0; i < vlen; ++i, ++j) {
							if (maxorset) {// 取最大值
								sb.append(String.format(
										"%s=if(%s > %s, %s, %s)",
										specialCol[j], cols[i], specialCol[j],
										cols[i], specialCol[j]));
							} else {// 直接设置
								sb.append(String.format("%s=%s", specialCol[j],
										cols[i]));
							}
							if (i != vlen - 1) {
								sb.append(", ");
							}
						}

						// old : process only one special collumn
						// String lastValue = cols[cols.length - 1];
						// sb.append(String.format(" ON DUPLICATE KEY UPDATE %s='%s'",
						// specialCol, lastValue));
					}
					String updateSqlStr = sb.toString();

					if (!dryrun) {
						try {
							statement.executeUpdate(updateSqlStr);
						} catch (java.sql.SQLException ex) {
							System.err.printf("[Error] line: %s\n", line);
							System.err
									.printf("[Error] sql: %s\n", updateSqlStr);
							System.err.printf("[Error] err: %s\n",
									ex.getMessage());
						}
					} else {
						System.out.println(updateSqlStr);
					}
					++totalLoadRows;
					line = bfr.readLine();
				}
				if (dryrun) {
					System.out.println("");
				}
			}
			System.out.printf("Total Loading %d Rows: %s\n\n", totalLoadRows,
					tblFmt);
		} catch (Exception e) {
			System.err.printf("current sql string: %s\n", sb.toString());
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new Configuration(), new LoadMYDriver(), args);
		System.exit(ret);
	}

	private void Usage(String name) {
		// final Log LOG = LogFactory.getLog("main-test");
		// LOG.info("oooo hello oooo");
		// LOG.warn("what hello oooo");
		// LOG.debug("what hello oooo");

		System.out
				.printf("Usage: %s mysqluri updateTblFormat <addkey> filesPattern [-z]\n\n"
						+ "\tmysqluri         mysql://user:password@host/db?[options(key=value)&]\n"
						+ "\tupdateTblFormat  insert/replace table([time,]col1,col2[,...][,<colm;...;coln>])\n"
						+ "\taddkey           can be time YYYYMM, or YYYYMM,'value'\n"
						+ "\tfilesPattern     path patthern in current specified filesystem\n"
						+ "\t-z               run in dryrun testing mode, output to stdout\n",
						name);
		System.exit(-1);
	}
}
