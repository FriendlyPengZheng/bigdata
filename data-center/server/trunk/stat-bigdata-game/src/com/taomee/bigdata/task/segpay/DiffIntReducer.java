package com.taomee.bigdata.task.segpay;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * 差集、交集
 * sum、count中间结果输出
 * @author cheney
 * @date 2013-11-21
 */
public class DiffIntReducer extends MRBase implements
		Reducer<Text, FloatWritable, Text, NullWritable> {

	private Text outputKey = new Text();
	
	@Override
	public void reduce(Text key, Iterator<FloatWritable> values,
			OutputCollector<Text, NullWritable> output, Reporter reporter)
			throws IOException {
		this.reporter = reporter;
		
		double sum = 0.0;
		int ucount = 0;
		FloatWritable fw;
		
		//交集：条件数
		String s = conf.get(ConfParam.PARAM_CALC_INT_NUM);
		int int_num = s == null ? 1 : Integer.parseInt(s);
		
		boolean calc_int = false;
		String ct = conf.get(ConfParam.PARAM_CALC_TYPE);
		int n = 0;
        
        //并集或差集
        if(ct != null && !ct.equals(ConfParam.VALUE_CALC_INT)) {
            calc_int = true;
            n = int_num;
        }
		String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);   

		while(values.hasNext()){
			fw = values.next();
			if(fw.get() < 0) {//多类型输入, 求交集，差集运算
				if(ct == null || ConfParam.VALUE_CALC_INT.equals(ct)) {
					calc_int = true;
					n++;
				} else if(ConfParam.VALUE_CALC_DIF.equals(ct)){//差集只要有一个条件不符合就不输出
					return;
				}
			} else {
				sum += fw.get();
				ucount++;
			}
            //System.out.println(String.format("%s=%f", key.toString(), fw.get()));
		}
        //System.out.println(String.format("%s=%s,%d,%d,%.4f", key.toString(), calc_int, n, int_num, sum));
		
		if(!(calc_int && n >= int_num)) return; //交集需满足所有条件,才输出（大于情况为有重复用户, 同个用户多次付费）
		
		if(sum > 0){
			outputKey.set(String.format("%s\t%.4f\t%d", key.toString(), sum, ucount));
			mos.getCollector("part" + gameinfo, reporter).collect(outputKey, NullWritable.get());
			//output.collect(outputKey, NullWritable.get());
		}
		
	}

}
