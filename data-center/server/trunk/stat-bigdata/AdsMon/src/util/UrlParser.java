package util;

import java.util.*;

public class UrlParser
{
    private String murl = null;
    //private String mhost = null;
    //private String mpath = null;
    public UrlParser() {}

    // http://www.4399.com/abc.html?a=12&b=xx
    public void init(String url) {
        murl = url;
    }

    private static String gsUrl = "gs.61.com/";
    public String getHostPath() 
    {
        // remove schema token
        int beg = murl.indexOf("://") + 3; 
        if (beg < 5) {
            System.err.printf("error proto schema: %s\n", murl);
            return null;
        }

        String url = null;
        int end = murl.indexOf("?", beg);
        if (end == -1) {
            url = murl.substring(beg);
        } else {
            url = murl.substring(beg, end);
        }

        // mv gs.61.com/12545 => gs.61.com
        int gschkidx = gsUrl.length();
        if (url.startsWith(gsUrl) && url.length() >= gschkidx + 1) { 
            String tmp = url.substring(gschkidx);
            if (url.charAt(gschkidx) == '#' || MathUtil.isNumber(tmp)) {
                url = gsUrl; 
            }
        }

        // remove last '/'
        int llen = url.length();
        while (url.charAt(--llen) == '/') { }
        if (llen + 1 != url.length()) {
            url = url.substring(0,llen + 1);
        }

        return url;
    }

}
