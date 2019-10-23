package util;

public class TadUtil {
    public static String formalTad(String t)  {
        String result = t;
        if (t == null) {
            result = "none";
        } 
        else if ("".equals(t)) {
            result = "unknown";
        } else {
        }
        return result;
    }
}
