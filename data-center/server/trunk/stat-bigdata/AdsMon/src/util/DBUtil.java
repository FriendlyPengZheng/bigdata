package util;

import java.util.*;

public class DBUtil {
    public DBUtil() {}

    public static StringBuilder sb = new StringBuilder();
    public static String escapeString(String str) {
        sb.setLength(0);
        for (int i=0 ; i < str.length() ; ++i) {
            char c = str.charAt(i);
            switch(c) {
                case '\\': sb.append("\\\\"); break;
                case '\'': sb.append("\\\'"); break;
                case '\"': sb.append("\\\""); break;
                default: sb.append(c); break;
            }
        }
       
        return sb.toString();
    }
}
