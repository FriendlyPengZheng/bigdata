package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.Long;
import java.lang.Double;
import java.lang.StringBuffer;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.taomee.bigdata.lib.Distr;
import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.MysqlConnection;

public class DistrMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
{
    private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable(1l);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private StringBuffer buffer;
    private MysqlConnection mysql = null;
    private HashMap<String, Integer[]> distrMap = new HashMap<String, Integer[]>();
    private String opType = null;
    private boolean isSet = false;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
        mysql = new MysqlConnection();
        mysql.connect(job.get("mysql.url"),
                job.get("mysql.user"),
                job.get("mysql.passwd"));
        if(mysql == null) {
            throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                        job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
        }
        opType = job.get("op_type");
        if(opType == null) {
            throw new RuntimeException("op_type not configured");
        }

        if(opType.compareToIgnoreCase("distr_set") == 0) {
            isSet = true;
            return;
        }

        //查找所有符合操作类型的report
        String sql = String.format("select report_id, stid, sstid, op_fields from t_report_info where op_type='%s'", opType);
        ResultSet rSet = mysql.doSql(sql);
        if(rSet == null) {
            throw new RuntimeException(sql);
        }
        HashMap<Integer, String> reportKeys = new HashMap<Integer, String>();
        try {
            while(rSet.next()) {
                String value = String.format("%s\t%s\t%s", rSet.getString(2), rSet.getString(3), rSet.getString(4));
                reportKeys.put(rSet.getInt(1), value);
            }
        } catch(SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        //查找每个report对应的分布区间
        Iterator<Integer> it = reportKeys.keySet().iterator();
        while(it.hasNext()) {
            Integer reportId = it.next();
            sql = String.format("select upper_bound from t_distr_range_info where r_id=%d and type='report' and upper_bound>0 order by upper_bound", reportId);
            rSet = mysql.doSql(sql);
            Integer distr[] = Distr.getDistrFromResult(rSet);
            if(distr != null) {
                distrMap.put(reportKeys.get(reportId), distr);
            }
        }
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        String[] items = value.toString().split("\t");
        Integer distr[] = null;
        if(!isSet) {
            String reportKey = String.format("%s\t%s\t%s", items[6], items[7], items[8]);
            distr = distrMap.get(reportKey);
            if(distr == null) {
                r.setCode("W_DISTR_REDUCER", String.format("not distr for %s:%s", reportKey, opType));
                return;
            }
        }
        //uid value key...
        buffer = new StringBuffer(items[2]);
        for(int i=3; i<items.length; i++)   buffer.append("\t" + items[i]);
        buffer.append("\t" + Distr.getDistrName(distr, Distr.getRangeIndex(distr, Double.valueOf(items[1]))));
        outputKey.set(buffer.toString());
        output.collect(outputKey, outputValue);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }
}
