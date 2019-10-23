package util;

import java.util.*;
import java.net.URLDecoder;
//import java.io.UnsupportedEncodingException;

public class NgxLogParser
{
    //private static String adPrefix = "/ad.txt?";
    private static String[] adPrefixs = new String[] {"/ad.txt?", "/tm.js?"};
    // url cut by some special characters
    //private static String [] cutnotes = new String[] {"?",";"," ","=","&","\t","\r","\n","##", ".."};
   
    private String pathPrefx = null;

    private boolean valid = true;

    private StringBuilder sb = new StringBuilder();
    private String mlogLine = null;

    private String header = null;
    private String ip = null;
    private String time = null;
    private String method = null;
    private String path = null;
    private String status = null;

    private String url = null;
    private String tad = null;
    private String uuid = null;

    public NgxLogParser() { }
    public NgxLogParser(String log)
        throws java.io.UnsupportedEncodingException
    { init(log); }
    
    //$remote_addr [$time_iso8601][$msec] "$request" $status 
    //$bytes_sent $request_time "$http_user_agent"
    public void init(String logline) 
        throws java.io.UnsupportedEncodingException
    {
        ip = null;
        url = null;
        tad = null;
        uuid = null;
        valid = true;
        pathPrefx = null;
        sb.setLength(0);

        mlogLine = logline;
        int filedIdx = 0;
        // parse log line first 4 fields
        for (int i = 0 ; i < mlogLine.length() && filedIdx < 4; ++i) {
            char c = mlogLine.charAt(i);
            switch (c)
            {
                case ' ' :
                    switch (filedIdx)
                    {
                        case 0 : ip = sb.toString(); break;
                        case 1 : time = sb.toString(); break;
                        case 2 : header = sb.toString(); break;
                        case 3: status = sb.toString(); break;
                        default : break;
                    }  /* end of switch */
                    ++filedIdx ;
                    sb.setLength(0);
                    break;
                case '"':
                    for (int j = i+1 ; j < mlogLine.length() ; ++j) {
                        char cc = mlogLine.charAt(j);
                        if (cc != '"') {
                            sb.append(cc);
                        } else {
                            i = j;
                            break;
                        }
                    }
                    break;
                default :
                    sb.append(c);
                    break;
            }  /* end of switch */
        }

        // parse header: GET path HTTP/x.x
        String[] items = header.split(" ");
        if (items.length < 3) { valid = false; return; }

        method = items[0];

        path = items[1];
        if (!isValid()) { return; }

        try { 
            // change to lowerCase string
            path = URLDecoder.decode(items[1], "utf-8");
            path = URLDecoder.decode(path, "utf-8").toLowerCase();
        } catch (Exception ex) {
            System.err.println("error urldecode: " + logline);
            valid = false; 
            return; 
        } 

        if (!isValid()) { return; }

        // parse needle
        parseSnippet();
    }

    // use path to get <url/tad/uuid>
    private void parseSnippet() {
        String tmp = path.substring(pathPrefx.length());
        String[] items = tmp.split("&");
        for (int i =0 ; i < items.length ; ++i) {
            String ts = items[i];

            // url should not end with "/"
            if (ts.startsWith("url=")) {
                if (ts.indexOf("://")  < 5 - 3) {
                    System.err.printf("error proto schema: %s\n", ts);
                    url = null; continue;
                }

                if (ts.endsWith("/")) {
                    url = ts.substring(4, ts.length() - 1);
                } else {
                    url = ts.substring(4);
                }
            } 
            else if (ts.startsWith("tad=")) {
                tad = ts.substring(4).trim();
                // filter : xxx
                if (tad.equals("") || tad.equals("#") || 
                        (tad.length() > 0 && !tad.equals("none") && tad.charAt(0) != '#' 
                            && !MiscUtil.isNumber(tad) && tad.indexOf('.') == -1)) {
                    tad = "unknown";
                    continue;
                } 

                //  eg. #http://www.4399.com/xxx.html?aaf
                //for(String note : cutnotes) {
                    //int cutidx = ts.indexOf(note, 4);
                    //if (cutidx > 0) {
                        ////System.out.println("cut : " + ts + "   " + note + cutidx);
                        //tad = ts.substring(4, cutidx);
                        //break;
                    //} 
                //}
                sb.setLength(0);
                sb.append(tad.charAt(0));
                boolean end = false;
                for (int ii = 1; ii < tad.length() && !end; ++ii) {
                    char c = tad.charAt(ii);
                    // cut binary char, special sign(picked), extended char, non ascii char
                    if (c <= 0x27 || c > 127) { break; }
                    switch (c)
                    {
                        case '?' :
                        case '=' :
                        case ',' :
                        case ';' : end = true; break;
                        default : sb.append(c); break;
                    }  /* end of switch */
                }
                int llen = sb.length();
                while (sb.charAt(--llen) == '/') {  }
                sb.setLength(llen+1);

                tad = sb.toString();
            } 
            else if (ts.startsWith("uuid=")) {
                uuid = ts.substring(5).trim();
            } else {
                continue;
            }
        }
    }

    public boolean isValid()
    {
        if (valid && method.equals("GET") && !status.equals("403")) {
            for(String adp : adPrefixs) {
                if (path.startsWith(adp)) {
                    pathPrefx = adp;
                    return (valid = true);
                } 
            }
        }
        return (valid = false);
    }

    public String getDay()
    {
        sb.setLength(0);
        for (int i = 0 ; i < time.length() ; ++i) {
            char c = time.charAt(i);
            switch (c )
            {
            case '[' :
            case '-' :
                continue;
            case 'T' :
                return sb.toString();
            default :
                sb.append(c);
                break;
            }  /* end of switch */
        }
        return null;
    }

    public String getIp() {
        return ip;
    }

    public String getUrl()
    {
        if (valid && url != null) {
            return url.length() <= 255 ? url : url.substring(0,255);
        } else {
            return null;
        }
    }

    public String getTad()
    {
        if (valid && tad != null) {
            return tad.length() <= 128 ? tad : tad.substring(0,128);
        } else {
            return null;
        }
    }

    public String getUuid()
    {
        if (valid) {
            return uuid;
        } else {
            return null;
        }
    }

    public void dump(){
        System.out.printf("time: %s\n", time);
        System.out.printf("header: %s\n", header);
        System.out.printf("status: %s\n", status);
        System.out.printf("method: %s\n", method);
        System.out.printf("path: %s\n", path);

        System.out.printf("tad: %s\n", tad);
        System.out.printf("url: %s\n", url);
        System.out.printf("uuid: %s\n", uuid);
    }

    public static void main(String args[]) throws Exception
    {
        NgxLogParser p = new NgxLogParser();
        p.init("10.1.6.67 [2012-08-08T16:52:38+08:00][1344415958.026] \"GET /ad.txt?url=http%3A%2F%2Fviolet.taomee.net%2Fadvertise%2Fjs%2Ftest.html%3Ftad%3Df78gj&uuid=8dc4292f-18b6-5721-1a0a-752d86716a84&tad=f78gj&1344416130213 HTTP/1.1\" 200 249 0.002 \"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)\"");
        p.dump();
        System.out.println(p.isValid());
        System.out.println(p.getDay());

        p.init("10.1.6.67 [2012-08-08T16:48:05+08:00][1344415685.191] \"-\" 400 0 0.000 \"-\"");
        System.out.println(p.isValid());

        p.init("114.80.98.4 [2012-08-04T06:03:14+08:00][1344031394.853] \"OPTIONS / RTSP/1.0\" 400 172 0.028 \"-\"");
        System.out.println(p.isValid());

        p.init("114.80.98.4 [2012-08-04T06:03:14+08:00][1344031394.906] \"l\\x00\\x0B\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\" 400 172 0.027 \"-\"");
        System.out.println(p.isValid());
        p.dump();

        p.init(args[0]);
        System.out.println(p.isValid());
        p.dump();

    }
}
