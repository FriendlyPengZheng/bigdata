package com.taomee.bigdata.basic;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.lang.Long;
import java.lang.Double;
import java.sql.SQLException;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.MysqlConnection;

public class BasicReducerRoll extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private MultipleOutputs mos = null;
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private OutputCollector<Text, NullWritable> collect;
    private MysqlConnection mysql = null;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
        mysql = new MysqlConnection();
        mysql.connect(job.get("mysql.url"),
                job.get("mysql.user"),
                job.get("mysql.passwd"));
        if(mysql == null) {
            throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                        job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
        }
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String[] items = key.toString().split("\t");
        String[] valueItems;
        Integer op = Integer.valueOf(items[0]);
        StringBuffer buffer = new StringBuffer(items[1]);
        Double value = 0.0;
        Double tmp;
        Long time = 0l;
		//对于首服滚服数据，只输出各服数据，不输出汇总数据
		String sid = items[4];
		if(sid.compareTo("-1") == 0) return;

		
        //TODO 多输出
        switch(op) {
            case Operator.UCOUNT:
				for(int i=2; i<items.length; i++)  buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("UCOUNT", reporter);
                break;
            case Operator.COUNT:
                while(values.hasNext()) {
                    value += Double.valueOf(values.next().toString());
                }
                buffer.append("\t" + value);
                for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("COUNT", reporter);
                break;
            case Operator.SUM:
                while(values.hasNext()) {
                    try {
                        value += Double.valueOf(values.next().toString());
                     } catch (java.lang.NumberFormatException e) {

                     }
                }
                buffer.append("\t" + value);
                for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("SUM", reporter);
                break;
            case Operator.DISTR_SUM:
                while(values.hasNext()) {
                    value += Double.valueOf(values.next().toString());
                }
                buffer.append("\t" + value);
                for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("DISTRSUM", reporter);
                break;
            case Operator.MAX:
                while(values.hasNext()) {
                    tmp = Double.valueOf(values.next().toString());
                    if(tmp > value) value = tmp;
                }
                buffer.append("\t" + value);
                for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("MAX", reporter);
                break;
            case Operator.DISTR_MAX:
                while(values.hasNext()) {
                    tmp = Double.valueOf(values.next().toString());
                    if(tmp > value) value = tmp;
                }
                buffer.append("\t" + value);
                for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("DISTRMAX", reporter);
                break;
            case Operator.SET:
                while(values.hasNext()) {
                    valueItems = values.next().toString().split("\t");
                    if(time < Long.valueOf(valueItems[1])) {
                        value = Double.valueOf(valueItems[0]);
                        time = Long.valueOf(valueItems[1]);
                    }
                }
                buffer.append("\t" + value);
                buffer.append("\t" + time);
                for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("SET", reporter);
                break;
            case Operator.DISTR_SET:
                while(values.hasNext()) {
                    valueItems = values.next().toString().split("\t");
                    if(time < Long.valueOf(valueItems[1]));
                    value = Double.valueOf(valueItems[0]);
                }
                buffer.append("\t" + value);
                for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                outputKey.set(buffer.toString());
                collect = mos.getCollector("DISTRSET", reporter);
                break;
            case Operator.IP_DISTR:
                while(values.hasNext()) {
                    buffer = new StringBuffer(items[1]);
                    buffer.append("\t" + values.next());
                    for(int i=2; i<items.length; i++)   buffer.append("\t" + items[i]);
                    outputKey.set(buffer.toString());
                    mos.getCollector("IPDISTR", reporter).collect(outputKey, outputValue);
                }
                return ;
            case Operator.HIP_COUNT:
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                while(values.hasNext()) {
                    String times[] = values.next().toString().split("\t");
                    for(int i=0; i<times.length; i++) {
                        long stamp = Long.valueOf(times[i]);
                        min = stamp < min ? stamp : min;
                        max = stamp > max ? stamp : max;
                    }
                }
                String sql = String.format("insert into t_client_stat_info set stid='%s',sstid='%s',game='%s',hip='%s',first_time=%d,last_time=%d ON DUPLICATE KEY update first_time=if(first_time<%d,first_time,%d),last_time=if(last_time>%d,last_time,%d)", items[1], items[2], items[3], items[4], min, max, min, min, max, max);
                mysql.doUpdate(sql);
                return ;
            default:
                r.setCode("E_OP_NOT_FOUND", String.format("[%d]", op));
                return ;
        }
        collect.collect(outputKey, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
        //mos.close();
        mysql.close();
    }
}
