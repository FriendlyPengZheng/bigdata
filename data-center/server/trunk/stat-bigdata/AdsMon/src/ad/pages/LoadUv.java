package ad.pages;

import util.*;

import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class LoadUv extends LoadAds {
	protected String[] getFormalValues(String line) throws Exception {
		if (dryrun) {
			System.out.printf("Line: %s", line);
		}

		// format: tad,url,<uniq|times> num
		String[] cols = line.split("\t|,", -1);

		// check for page views
		String type = cols[2];
		if (!type.equals("uniq")) {
			if (dryrun) {
				System.out.printf("    ....... skip \n");
			}
			return null;
		}
		if (dryrun) {
			System.out.println("");
		}

		String[] formalCols = new String[cols.length - 1];
		String tad = cols[0];
		if (tad.lastIndexOf('#') != 0) {
			String[] realTad = tad.split("/#|#");
			tad = realTad[0];
		}
		Integer adid = getAdid(tad);
		if (adid == null) {
			adid = insertAd(tad);
			if (adid == IGNORE_ERROR_CODE) {
				return null;
			}
		}
		formalCols[0] = adid.toString();

		String url = cols[1];
		if (url.lastIndexOf('#') != 0) {
			String[] realUrl = url.split("/#|#");
			url = realUrl[0];
		}
		Integer urlid = getUrlid(url);
		if (urlid == null) {
			urlid = insertUrl(url);
			if (urlid == IGNORE_ERROR_CODE) {
				return null;
			}
		}
		formalCols[1] = urlid.toString();

		formalCols[2] = cols[3];

		return formalCols;
	}

	public static void main(String args[]) throws Exception {
		int ret = ToolRunner.run(new Configuration(), new LoadUv(), args);
		System.exit(ret);
	}
}
