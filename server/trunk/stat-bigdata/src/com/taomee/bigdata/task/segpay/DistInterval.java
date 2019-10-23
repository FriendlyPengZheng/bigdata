package com.taomee.bigdata.task.segpay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.math.BigDecimal;

import com.taomee.bigdata.driver.MysqlDao;

/**
 * 分布区间
 * @author cheney
 * @date 2013-11-21
 */
public class DistInterval {

	public static final String EVENTID_PAY_COUNT = "eventid.pay.count";
	public static final String EVENTID_PAY_AMOUNT = "eventid.pay.amount";
	public static final String DISTR_PAY_COUNT = "distr.pay.count";
	public static final String DISTR_PAY_AMOUNT = "distr.pay.amount";
	
	private static String SPLITOR_ACROSS = "-";
	private static String SPLITOR_WAVE = "~";
	private static String SPLITOR_GT = ">";
	private static String SPLITOR_EQ = "=";
	
	private double min = 0;
	private double max = 0;
	
	private int order = 0;
	private String splitor = SPLITOR_ACROSS;
	
	public DistInterval(){}
	public DistInterval(double min, double max, int order, String splitor){
		this.min = min;
		this.max = max;
		this.order = order;
		this.splitor = splitor;
	}
	
	public DistInterval(double min, double max, int order){
		this.min = min;
		this.max = max;
		this.order = order;
	}
	
	public static Map<DistInterval, AtomicInteger> generate(String[] intervals)
			throws IllegalArgumentException {
		if(intervals == null || intervals.length == 0){
			throw new IllegalArgumentException("empty intervals: " + intervals);
		}
		
		Map<DistInterval, AtomicInteger> its = new HashMap<DistInterval, AtomicInteger>();
		
		int i = 0;
		DistInterval di = null;
		for(String it : intervals) {
			di = create(it, i++);
			if(di != null)
				its.put(di , new AtomicInteger(0));
			else 
				throw new IllegalArgumentException("invalid intervals: " + intervals);
		}
		
		return its;
	}

    public static Map<DistInterval, AtomicInteger> get(String distr) {
        Map<DistInterval, AtomicInteger> its = new HashMap<DistInterval, AtomicInteger>();
        String items[] = distr.trim().split(",");
        its.put(new DistInterval(0, Integer.valueOf(items[0]), 0), new AtomicInteger(0));
        for(int i=1; i<items.length; i++) {
            its.put(new DistInterval(Integer.valueOf(items[i-1]), Integer.valueOf(items[i]), i), new AtomicInteger(0));
        }
        its.put(new DistInterval(Integer.valueOf(items[items.length-1]), Integer.MAX_VALUE, items.length), new AtomicInteger(0));
        return its;
    }
	
	//get from database
	public static Map<DistInterval, AtomicInteger> get(String eventid, MysqlDao dao)
			throws IllegalArgumentException {
		
		Map<DistInterval, AtomicInteger> its = new HashMap<DistInterval, AtomicInteger>();
		ResultSet rs = null;
		try {
			rs = dao.query("select range_low, range_high from t_distr_range where event_id = '" + eventid + "'");
			int i = 0;
			while(rs.next()){
				int high = rs.getInt(2);
				high = high == 0 ? Integer.MAX_VALUE:high;
				its.put(new DistInterval(rs.getInt(1), high, i), new AtomicInteger(0));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return its;
	}
	
	private static DistInterval create(String it, int order){
		
		//最大: >10000
		if(it.contains(SPLITOR_GT)){
			String sp = SPLITOR_GT;
			if(it.contains(SPLITOR_EQ)) sp += SPLITOR_EQ;
			return new DistInterval(
						Double.parseDouble(it.replace(SPLITOR_GT, "")
											 .replace(SPLITOR_EQ, ""))
					   ,Integer.MAX_VALUE
					   ,order
					   ,sp);
		}
		
		//区间: 1-100 or 101~200
		String sp = SPLITOR_ACROSS;
		String[] ts = it.split(sp);
		if(ts == null || ts.length < 1){
			sp = SPLITOR_WAVE;
			ts = it.split(sp);
			if(ts == null || ts.length < 1) return null;
		}
		return new DistInterval(Double.parseDouble(ts[0])
							   ,Double.parseDouble(ts[0])
							   ,order
							   ,sp);
	}
	
	public String toString(){
        return toString(1.0);
	}
	
	public String toString(double ratio){
        BigDecimal min = BigDecimal.valueOf(this.min*ratio);
        BigDecimal max = BigDecimal.valueOf(this.max*ratio);
        if(order == 0) {
            if(Math.abs(this.max*ratio-2.0) <= 0.00001) {
                return String.format("%d:1", order);
            }
            return String.format("%d:<%s", order, max.stripTrailingZeros().toPlainString());
        } else if(this.max == Integer.MAX_VALUE) {
            return String.format("%d:>=%s", order, min.stripTrailingZeros().toPlainString());
        } else {
            return String.format("%d:[%s,%s)", order, min.stripTrailingZeros().toPlainString(), max.stripTrailingZeros().toPlainString());
        }
	}
	
	public boolean compare(double d){
		if(d >= min && d < max) return true;
		return false;
	}
	
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getSplitor() {
		return splitor;
	}
	public void setSplitor(String splitor) {
		this.splitor = splitor;
	}
	
}
