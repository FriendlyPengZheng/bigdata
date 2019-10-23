package util;

import java.util.*;

public class MathUtil {
    public static boolean isNumber(String num)
    {
        if (num != null && num.length() > 0) {
            for (int i = 0 ; i < num.length() ; ++i) {
                char c = num.charAt(i);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
