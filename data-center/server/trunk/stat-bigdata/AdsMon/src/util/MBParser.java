package util;
import java.util.*;

/**
 * @class MBParser
 * @brief only process pure mb cost record, ignoring mb record cost in vip 
 *      format ::= timestamp mimi(src) mimi(dest) mb_channel productid productcnt mb_num mb_balance
            mb_num      充值为正数，消费为负数
 */
public class MBParser {
    public final static int collumCount = 8;
    private static int mbproductid_threshold = 1000;
    private String[] items = null;
    private int prdid = 0;
    private int mbnum = 0;

    public MBParser() {}
    public MBParser(String line) 
    { init(line); }
    public boolean init(String line) {
        init(line.split("\t"));
        return isValid();
    }
    public boolean init(String[] ii) {
        items = ii;
        try { 
            prdid = Integer.parseInt(items[4]);
            mbnum = Integer.parseInt(items[6]);
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return isValid();
    }
    public boolean isValid() {
        if (items != null && items.length == collumCount 
                && prdid > mbproductid_threshold && mbnum <= 0) {
            return true;
        } else {
            return false;
        }
    }

    // fetch mb cost (exclude pay to recharge)
    public int getMBCost() { return mbnum < 0 ? -mbnum : 0; }
    public int getProductId() { return prdid; }
    public String getMimiSrc() { return items[1]; }
    public String getMimiDest() { return items[2]; }
}
