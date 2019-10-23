package util;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * @class VipParser
 * @brief paser vip record line format::=
 *  timestamp mimi gameid fee_flag action_type time_length begin_time end_time time_flag vip_type
 *      fee_flag    充值渠道 90 91 99 100,174不算作合法收入，去除,174为极战联盟的免费VIP
        action_type 0：未知类型
                    1：为新用户或已有无效用户增加时间
                    2：为已有有效用户增加时间
                    3：清理过期用户
                    4：为用户减少时间
                    5：取消自动续费
                    6：短信退订
        oprand      操作的天数：正数表示加对应的天数，负数表示减对应的天数
 *      example ::= 1351011665  300316562   1   31  2   30  1325480629  1353992629  2   1
 *
 */
public class VipParser
{
    //private SimpleDateFormat timefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //private Date lastbegin = null;
    //private Date lastend = null;
    public final static int collumCount = 10;
    private static String sep = "\t";
    private String[] items = null;

    public VipParser() {}
    public VipParser(String line) {
        init(line);
    }
    public boolean init(String line) {
        items = line.split(sep);
        return isValid();
    }
    public boolean init(String[] ii) {
        items = ii;
        return isValid();
    }

    public boolean isValid() {
        int atype = getActionType();
        int vipInterval = getVipInterval();
		//刀塔的字段数目为12个，此处判断用大于等于
        if (items != null && items.length >= collumCount &&
                (atype == 1 || atype == 2 || atype == 6) &&
                vipInterval != 0) {
            int feeFlag = getFeeFlag();
            switch (feeFlag) { // rid of custom service
                case 90 :
                case 91 :
                case 99 :
                case 100 :
                case 174 : return false;
                default : break;
            }  /* end of switch */
            return true;
        } else {
            return false;
        }
    }

    public String getMimi()     { return items[1]; }
    public String getGameid()   { return items[2]; }

    // unit in fen
    public int getVipCost() {
        int cost = 0;
        int interval = getVipInterval();
	String gameid = getGameid();
	if(gameid.equals("16") || gameid.equals("25"))
	{//热血精灵派VIP不打折 add 20160927 mo极战联盟vip不打折
		cost = interval *10*100;
	}
	else if(gameid.equals("204"))
	{//刀塔vip每月20元
		cost = interval *10*200;
	}
	else
	{
        	switch (interval)
        	{
            		case 6 : cost = 5000; break;
            		case 12 : cost = 10000; break;
            		default : cost = interval * 10 * 100; break;
        	}  /* end of switch */
	}
        int atype = getActionType();
        if (atype == 1 || atype == 2) { // +
            return cost;
        }
        else if (atype == 4 || atype == 6) { // -
            return cost;
        }
        else { // ignore other action type
            return 0;
        }
    }

    // unit in months
    public int getVipInterval() {
        int interval = 0;
        try {
            int daylength = Integer.parseInt(items[5]);
            interval = daylength / 30;
        } catch (Exception ex) {
            ex.printStackTrace();
            interval = 0;
        }
        return interval;
    }
    // vip paid channel, wipe out 90 91 99 100
    public int getFeeFlag() {
        int feeFlag = 90;
        try {
            feeFlag = Integer.parseInt(items[3]);
        } catch (Exception ex) {
            ex.printStackTrace();
            feeFlag = 90;
        }
        return feeFlag;
    }

    public int getActionType() {
        try {
            return Integer.parseInt(items[4]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * @brief  get user's viptype : pure new / old / cur
     * @param
     * @return  1   pure new    // atype = 1 && endTime == '0'
     *          2   old         // atype = 1 && endTime != '0'
     *          3   cur         // atype = 2
     *          9   unvipuser
     */
    public int getVipUserType() {
        int actiontype = getActionType();
        int utype = 0;
        switch (actiontype)
        {
            case 1 : utype = items[7].equals("0") ? 1 : 2; break;
            case 2 : utype = 3; break;
            default : utype = 9; break;
        }  /* end of switch */
        return utype;
    }
}
