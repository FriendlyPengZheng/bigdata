package util;

/*
 * register format ::==
 * register: time mimi tad gameid ip
 * role: time mimi tad gameid idc
 */
public class RegisterParser implements MergeLog
{
    public String[] items = null;

    public RegisterParser () {}

    public boolean init(String line) {
        items = line.split("\t");
        return valid();
    }

    public boolean init(String[] cols) {
        items = cols;
        return valid();
    }
    public boolean valid () {
        if (items == null || items.length < 5) {
            return false;
        }
        return true;
    }

    public int getNumGameid() {
        try { 
            return Integer.parseInt(items[3]);
        } catch (Exception ex) {
            return 0;
        }
    }

    public long getNumMimi() {
        try { 
            return Long.parseLong(items[1]);
        } catch (Exception ex) {
            return 0;
        }
    }
}
