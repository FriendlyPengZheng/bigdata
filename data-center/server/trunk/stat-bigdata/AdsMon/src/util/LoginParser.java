package util;
import java.util.*;

/**
 * @class LoginParser
 * @brief parse login log record line, using inner status
 *       line format ::= timestamp mimi tad gameid idc(dx/wt) ip
 */
public class LoginParser implements MergeLog
{
    public static int collumCount = 6;
    public static String sep = "\t";
    private String[] items = null;

    public LoginParser () {}
    public LoginParser(String line) 
    { init(line); }
    //public void init(String line) {
        //items = line.split("\t");
    //}
    //public void init(String[] xx) {
        //items = xx;
    //}
    public boolean init(String line) {
        items = line.split("\t");
        return isValid();
    }
    public boolean init(String[] xx) {
        items = xx;
        return isValid();
    }

    public boolean isValid() {
        if (items != null && items.length == collumCount) {
            return true;
        } else {
            return false;
        }
    }

    public String getTimestamp() {
        return isValid() ? items[0] : null;
    }
    public String getTad() {
        if (!isValid()) { return null; }
        if (items[2].startsWith("#")) {  // eg. tad ::= #http://www.4399.com
            int schemaIdx = items[2].indexOf("://");
            if (schemaIdx != -1) {
                return "#" + items[2].substring(schemaIdx + 3);
            } else {
                return "#" + items[2].substring(0 + 1);
            }
        } else {
            return items[2];
        }
    }
    public String getIp() {
        return items[5];
    }

    //public String getGameid() {
        //return items[3];
    //}
    public int getNumGameid() {
        if (!isValid()) { return 0;}
        try { 
            return Integer.parseInt(items[3]);
        } catch (Exception ex) {
            return 0;
        }
    }

    public String getMimi() { 
        return items[1];
    }
    public long getNumMimi() {
        if (!isValid()) { return 0;}
        try { 
            return Long.parseLong(items[1]);
        } catch (Exception ex) {
            return 0;
        }
    }
}
