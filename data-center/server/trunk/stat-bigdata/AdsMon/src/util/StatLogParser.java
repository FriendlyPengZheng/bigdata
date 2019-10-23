package util;

import java.util.*;

// parser log imported from stat system
// format::= mimi gameid
public class StatLogParser implements MergeLog {
    private String line = null;
    private String[] items = null;
    public StatLogParser() { } 
    public StatLogParser(String log) {
        init(line);
    }
    public boolean init(String log) {
        line = log;
        items = line.split("\t");
        return valid();
    }
    public boolean init(String[] cols) {
        line = "";  // pass valid check
        items = cols;
        return valid();
    }

    protected boolean valid() {
        if (line == null || items == null ||
                items.length != 2) {
            return false;
        }
        return true;
    }

    public int getNumGameid() {
        try { 
            return Integer.parseInt(items[1]);
        } catch (Exception ex) {
            System.err.println("Error Statlog: " + line);
            return 0;
        }
    }

    public long getNumMimi() {
        try { 
            return Long.parseLong(items[0]);
        } catch (Exception ex) {
            System.err.println("Error Statlog: " + line);
            return 0;
        }
    }
}
