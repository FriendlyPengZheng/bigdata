package com.taomee.bigdata.task.segpay;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.driver.MysqlDao;

/**
 * 付费：
 * 金额、人数、人次
 * 次数、额度分布
 * @author cheney
 * @date 2013-11-21
 */
public class PayAmtCountUcountDistNDayReducer extends MRBase implements
		Reducer<Text, Text, Text, Text> {

	private Text outputKey= new Text();
	private Text outputValue = new Text();
	
	private static boolean isInit = false;
	private static ReentrantLock lock = new ReentrantLock();
	
	//分布
	private static Map<DistInterval, AtomicInteger> dist_amount;
	private static Map<DistInterval, AtomicInteger> dist_count;
	
	//reducer prefix
	private static String MOSNAME_AMOUNT = "mosname.amount";
	private static String MOSNAME_UCOUNT = "mosname.ucount";
	private static String MOSNAME_COUNT  = "mosname.count";
	private static String MOSNAME_ARPPU  = "mosname.arppu";
	
	private static String MOSNAME_AMOUNT_DIST = "mosname.amount.dist";
	private static String MOSNAME_COUNT_DIST = "mosname.count.dist";

    private static String amount = null;
    private static String ucount = null;
    private static String count  = null;
    private static String arppu  = null;
    private static String amount_distr = null;
    private static String count_distr  = null;

    private static String nNum = "";
	
	@Override
	@SuppressWarnings("unchecked")
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		
		String[] items = null;
		
		Double amt = 0.0;	//付费金额
		int ucount = 0;		//付费人数
		int count = 0;		//付费人次
        if(this.amount_distr != null) {
            dist_amount = DistInterval.get(conf.get(DistInterval.DISTR_PAY_AMOUNT));
        }
        if(this.count_distr != null) {
            dist_count = DistInterval.get(conf.get(DistInterval.DISTR_PAY_COUNT));
        }
        String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);		
		while(values.hasNext()){
			items = values.next().toString().split("\t");
			amt += Double.parseDouble(items[1]);
			count += Integer.parseInt(items[2]);
			ucount++;
            if(amount_distr != null){
                calcAmountDist(Double.parseDouble(items[1]));
            }
            if(count_distr != null){
                calcCountDist(Integer.parseInt(items[2]));
            }
		}
		
		//付费金额
		if(this.amount != null){
			outputValue.set(String.format("%s\t%.1f", nNum,amt));
			mos.getCollector(this.amount + gameinfo, reporter).collect(key, outputValue);
			//mos.getCollector(this.amount, reporter).collect(key, outputValue);
		}
		
		//付费人数
		if(this.ucount != null){
			outputValue.set(String.format("%s\t%d", nNum,ucount));
			mos.getCollector(this.ucount + gameinfo, reporter).collect(key, outputValue);
			//mos.getCollector(this.ucount, reporter).collect(key, outputValue);
		}
		
		//付费人次
		if(this.count != null){
			outputValue.set(String.format("%s\t%d", nNum,count));
			mos.getCollector(this.count + gameinfo, reporter).collect(key, outputValue);
			//mos.getCollector(this.count, reporter).collect(key, outputValue);
		}
		
		//付费arppu
		if(this.arppu != null){
			outputValue.set(String.format("%s\t%.4f",nNum, amt/ucount));
			mos.getCollector(this.arppu + gameinfo, reporter).collect(key, outputValue);
			//mos.getCollector(this.arppu, reporter).collect(key, outputValue);
		}
		
		//distribute
		Iterator<Entry<DistInterval, AtomicInteger>> it;
        Entry<DistInterval, AtomicInteger> entry;
        DistInterval dist;
		AtomicInteger v;
		
        items = key.toString().split("\t");
        if(items.length <= 4 || items[4].compareTo("_acpay_") != 0)  return;
		if(this.amount_distr != null){
            outputKey.set(String.format("%s\t%s\t%s\t%s",
                        items[0], items[1], items[2], items[3]));
			//金额分布
			it = dist_amount.entrySet().iterator();
			while(it.hasNext()){
                entry = it.next();
                dist = entry.getKey();
				v = entry.getValue();
				outputValue.set(nNum + "\t" +dist.toString(0.01) + "\t" + v.get());
				mos.getCollector(this.amount_distr + gameinfo, reporter).collect(outputKey, outputValue);
				//mos.getCollector(this.amount_distr, reporter).collect(outputKey, outputValue);
			}
		}
		
		if(this.count_distr != null){
            outputKey.set(String.format("%s\t%s\t%s\t%s",
                        items[0], items[1], items[2], items[3]));
			//人次分布
			it = dist_count.entrySet().iterator();
			while(it.hasNext()){
                entry = it.next();
                dist = entry.getKey();
				v = entry.getValue();
				outputValue.set(nNum + "\t" + dist.toString() + "\t" + v.get());
				mos.getCollector(this.count_distr + gameinfo, reporter).collect(outputKey, outputValue);
				//mos.getCollector(this.count_distr, reporter).collect(outputKey, outputValue);
			}
		}
		
	}
	
	private void calcAmountDist(Double amt){
		Iterator<Entry<DistInterval, AtomicInteger>> it = dist_amount.entrySet().iterator();
		Entry<DistInterval, AtomicInteger> et = null;
		AtomicInteger v;
		while(it.hasNext()){
			et = it.next();
			v = et.getValue();
			if(et.getKey().compare(amt)) {
				v.incrementAndGet();
				break;
			}
		}
	}
	
	private void calcCountDist(int count){
		Iterator<Entry<DistInterval, AtomicInteger>> it = dist_count.entrySet().iterator();
		Entry<DistInterval, AtomicInteger> et = null;
		AtomicInteger v;
		while(it.hasNext()){
			et = it.next();
			v = et.getValue();
			if(et.getKey().compare(count)) {
				v.incrementAndGet();
				break;
			}
		}
	}
	
	
	public void configure(JobConf conf) {
		super.configure(conf);
		if(!isInit) init(conf);
        this.nNum = conf.get("n");
    }
	
	private void init(JobConf conf) {
		//lock.lock();
		//MysqlDao dao = null;
		//try{
		//	
		//	if(isInit) return;
		//	
		//	String mysqlUrl = conf.get("mysql.url");
	    //    String mysqlUser = conf.get("mysql.user");
	    //    String mysqlPasswd = conf.get("mysql.passwd");
	    //    dao = new MysqlDao(mysqlUrl, mysqlUser, mysqlPasswd);
	    //    
		//	String ia = conf.get(DistInterval.EVENTID_PAY_AMOUNT);
		//	if(ia == null) return;
		//	dist_amount = DistInterval.get(ia, dao);
		//	
		//	String iu = conf.get(DistInterval.EVENTID_PAY_COUNT);
		//	dist_count = DistInterval.get(iu, dao);
		//	
		//	
		//} finally {
		//	if(dao != null) dao.close();
		//	lock.unlock();
		//}
        amount = conf.get(MOSNAME_AMOUNT);
        count = conf.get(MOSNAME_COUNT);
        ucount = conf.get(MOSNAME_UCOUNT);
        arppu = conf.get(MOSNAME_ARPPU);
        count_distr = conf.get(MOSNAME_COUNT_DIST);
        amount_distr = conf.get(MOSNAME_AMOUNT_DIST);
	}

}