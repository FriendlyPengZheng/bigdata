package util;
import java.util.*;

/**
 * @class LogoutParser
 * @brief parse logout record to fetch session length
 *      format::= timestamp mimi gameid logintime logouttime
 */
public class LogoutParser implements MergeLog
{
    public static int collumCount = 5;
    private String [] items = null;

    public LogoutParser() {}
    public LogoutParser(String line) 
    { init(line); }
    public void init(String line) {
        items = line.split("\t");
    }

    //public void init(String[] xx) {
        //items = xx;
    //}
    public boolean init(String[] cols) {
        items = cols;
        return isValid();
    }

    public boolean isValid() {
        if (items != null && items.length == collumCount) {
            return true;
        } else {
            return false;
        }
    }

    public int getNumGameid() {
        try { 
            return Integer.parseInt(items[2]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public long getNumMimi() {
        try { 
            return Long.parseLong(items[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public long getLogoutTimeNum() { 
        try { 
            return Long.parseLong(items[4]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Long.parseLong(items[0]);
        }
    }

    public String getMimi() { return items[1]; }
    public String getGameid() { return items[2]; }
    public String getLogoutTime() { return items[4]; }
}
