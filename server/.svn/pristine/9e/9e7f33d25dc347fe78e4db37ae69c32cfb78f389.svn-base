package com.taomee.bigdata.util;

import java.util.*;
import java.io.*;

public class AdParser implements Iterable<String>
{
    private static String domaintype = "com|net|org|cc|edu";
    // exception: www.2144.cn
    private static String specialdomaintype = "2144|360|51saier";

    private HashMap<String,String> tadmap = new HashMap<String,String>();
    private String mtad = null;
    private int mlvladcnt = 0;
    private String[] aditems = null;
    private boolean urlad = false;

    public AdParser() {}

    // cfgfile: contain tmcid <=> tad
    // format: tmcid    tad
    // tmcid is number id, tad is dot separated string
    // example: 100 clientmedia.qq.free.qplus
    public AdParser(String cfgfile) throws Exception
    { configure(cfgfile); }
    public AdParser(String cfgfile, String tad)
    {
        configure(cfgfile);
        init(tad);
    }

    public void configure(String cfgfile)
    {
        System.out.printf("load config: %s\n", cfgfile);
        BufferedReader bfrd = null;
        try {
            FileReader inst = new FileReader(cfgfile);
            bfrd = new BufferedReader(inst);
            String line = null;
            while ((line=bfrd.readLine()) != null && line.length() > 0) {
                if (line.startsWith("#")) { continue; }

                String [] items = line.split("\t| ");
                tadmap.put(items[0], items[1]);
            }
            bfrd.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * @brief  init record's tad
     * @param  ad   advertisement token, two types:
     *          1. dot separated format, eg. a.b.c, accumulated by dot level
     *          2. # leaded url, eg. #http://www.4399.com/news/a.html, accumulated by top domain
     */
    public void init(String ad)
    {
        urlad = false;

        ad = ad.toLowerCase();
        if (!MiscUtil.isNumber(ad)) {
            int idx = ad.indexOf(',');
            switch (idx)
            {
                case -1 : mtad = ad; break;
                case 0  : mtad = "unknown"; break;
                default : mtad = ad.substring(0, idx); break;
            }  /* end of switch */
        } else {
            mtad = tadmap.get(ad);
        }

	if(mtad == null)
	{
		mtad = "unknown";
	}

	if(mtad.trim().length() <  3)
	{
		mtad = "unknown";
	}
        ////for test purpose
        //mtad = tadmap.get(ad);
        //mtad = ad;
        if (mtad != null) {
            if (mtad.startsWith("#") || mtad.indexOf("://")>0) { // #http://www.4399.com/3258.html
                aditems = getUrlAdLvls();
                mlvladcnt = aditems.length;
                urlad = true;
            } else {   // accumulate aaa[.bbb]
                aditems = mtad.split("\\.");
                mlvladcnt = aditems.length;
            }
        } else {  // not find tmcid or error
            aditems = null;
            mlvladcnt = 0;
        }
    }

    public String getTadName(String tmcid) {
        return tadmap.get(tmcid);
    }

    public int getLength() {
        return mlvladcnt;
    }

    // #www.7k7k.com/special/51seer/
    // #plain_ad_channel
    private String[] getUrlAdLvls()
    {
        String [] adlvls = null;
        int schemabeg = mtad.indexOf("://");
        if (schemabeg < 5 - 3) { schemabeg = -2; }

        // for url tad: #http://www.7k7k.com/special/51seer/?aa=xx
        // hostpath ::= www.7k7k.com/special/51seer
        // hostpart ::= www.7k7k.com
        
        String hostpath = mtad.substring(schemabeg + 3);
        int askmark = hostpath.indexOf('?');
        // remove query
        if (askmark != -1) {
            hostpath = hostpath.substring(0, askmark);
        }
        // remove ending slash
        if (hostpath.endsWith("/")) {
            hostpath = hostpath.substring(0, hostpath.length() - 1);
        }
        
        // eg. www.61.com/fk
        String hostpart = hostpath;
        String[] parts = null;
        int firstslashidx = hostpath.indexOf('/');
        if (firstslashidx != -1) {
            hostpart = hostpath.substring(0, firstslashidx);
            parts = hostpart.split("\\.");
        } else {
            parts = hostpath.split("\\.");
        }

        String urlAdFull = "#" + hostpath;
        int partscnt = parts.length;
        if (partscnt >= 3) {
            String lastpart = parts[partscnt - 1];

            if (lastpart.matches(domaintype) || 
                    parts[partscnt - 2].matches(specialdomaintype)) {  // # ...x.y.com
                adlvls = new String[2];
                adlvls[0] = String.format("#%s.%s",parts[partscnt-2], parts[partscnt -1]);
                adlvls[1] = urlAdFull;
            }
            else if (parts[partscnt - 2].matches(domaintype) || lastpart.matches("cn|jp")) { 
                if (hostpath.equals(hostpart) && partscnt == 3) {
                    if (parts[0].startsWith("www")) { // #www.xxx.cn
                        adlvls = new String[2];
                        adlvls[0] = String.format("#%s.%s", parts[partscnt-2], parts[partscnt -1]);
                        adlvls[1] = urlAdFull;
                    } else { // #sina.com.cn
                        adlvls = new String[1];
                        adlvls[0] = urlAdFull;
                    }
                } 
                else if (partscnt > 3) {// ...x.y.com.z || ...x.y.z.cn
                    adlvls = new String[2];
                    adlvls[0] = String.format("#%s.%s.%s",
                            parts[partscnt-3], parts[partscnt-2], parts[partscnt -1]);
                    adlvls[1] = urlAdFull;
                } 
                else { 
                    adlvls = new String[2];
                    adlvls[0] = String.format("#%s.%s",parts[partscnt-2], parts[partscnt -1]);
                    adlvls[1] = urlAdFull;
                }
            } 
            else if (partscnt == 3) {  // x.y.cn
                adlvls = new String[2];
                adlvls[0] = String.format("#%s.%s",parts[partscnt-2], parts[partscnt -1]);
                adlvls[1] = urlAdFull;
            } else {
                if (hostpath.equals(hostpart)) { // #58.17.218.130
                    adlvls = new String[1];
                    adlvls[0] = urlAdFull;
                } else {
                    adlvls = new String[2];
                    adlvls[0] = "#" + hostpart;
                    adlvls[1] = urlAdFull;
                }
            }
        } else {
            // eg: #http://iyoshi.tw/site_media/games/molehero.html
            if (!hostpath.equals(hostpart)) {
                adlvls = new String[2];
                adlvls[0] = "#" + hostpart;
                adlvls[1] = urlAdFull;
            } else {
                // eg: #4399.com
                adlvls = new String[1];
                adlvls[0] = urlAdFull;
            }
        }
        return adlvls;
    }

    private StringBuilder sharedsb = new StringBuilder();
    public Iterator<String> iterator() {
        if (urlad) {  // for url tad, eg.: #http://www.4399.com/news/ad.html
            return new Iterator<String>() {
                private int index = 0;
                public boolean hasNext() {
                    return  (mtad != null) && index < mlvladcnt;
                }

                public String next() {
                    return aditems[index ++];
                }

                public void remove() {
                    throw new UnsupportedOperationException("not implement remove method");
                }
            };
        } else {
            return new Iterator<String>() {
                private int index = 0;
                public boolean hasNext() {
                    if (index != 0) {
                        sharedsb.append('.');
                    } else {
                        sharedsb.setLength(0);
                    }
                    return (mtad != null && index < mlvladcnt);
                }

                public String next() {
                    sharedsb.append(aditems[index++]);
                    return sharedsb.toString();
                }

                public void remove() {
                    throw new UnsupportedOperationException("not implement remove method");
                }
            };
        }
    }

    public void dump()
    {
        for (int i = 0 ; i < mlvladcnt ; ++i) {
            System.out.println(aditems[i]);
        }
    }
}
