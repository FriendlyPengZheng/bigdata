package com.taomee.bigdata.task.model;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.util.MysqlConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModelMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private HashMap<String, HashSet<String>> modelInfoMap = new HashMap<String, HashSet<String>>();

    public void configure(JobConf job) {
        MysqlConnection mysql = new MysqlConnection();
        mysql.connect(job.get("mysql.url"),
                job.get("mysql.user"),
                job.get("mysql.passwd"));
        if(mysql == null) {
            throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                        job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
        }
        ResultSet result = mysql.doSql("select model_id,model_step,game_id,stid,sstid from t_model_info order by game_id,model_id,model_step");
        if(result == null) return;
        try {
            while(result.next()) {
                try {
                    int modelid = result.getInt(1);
                    int modelstep = result.getInt(2);
                    int gameid = result.getInt(3);
                    String stid = result.getString(4);
                    String sstid = result.getString(5);
                    String key = String.format("%d\t%s\t%s", gameid, stid, sstid);
                    HashSet<String> value = modelInfoMap.get(key);
                    if(value == null) {
                        value = new HashSet<String>();
                    }
                    value.add(String.format("%d\t%d", modelid, modelstep));
                    modelInfoMap.put(key, value);
                } catch (java.lang.IllegalArgumentException e) { }
            }
        } catch (SQLException e) {
            ReturnCode.get().setCode("E_GET_MODEL_FROM_DB", e.getMessage());
        }
        mysql.close();
        rOutput = new ReturnCodeMgr(job);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Integer opCode;
        String[] items;
        String stid;
        String sstid;
        String uid;
        int game; 
        String hip;
        String time; 
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            uid = logAnalyser.getAPid();
            stid = logAnalyser.getValue("_stid_");
            sstid = logAnalyser.getValue("_sstid_");
            game = Integer.valueOf(logAnalyser.getValue("_gid_"));
            time = logAnalyser.getValue("_ts_");
            String modelkey = String.format("%d\t%s\t%s", game, stid, sstid);
            HashSet<String> modelvalue = modelInfoMap.get(modelkey);
            if(modelvalue == null) {
                modelkey = String.format("-1\t%s\t%s", stid, sstid);
                modelvalue = modelInfoMap.get(modelkey);
            }
            if(modelvalue != null) {
                Iterator<String> it = modelvalue.iterator();
                while(it.hasNext()) {
                    items = it.next().split("\t");
                    outputKey.set(String.format("%d\t%s\t%s", game, items[0], uid));
                    outputValue.set(String.format("%s\t%s", items[1], time));
                    output.collect(outputKey, outputValue);
                }
            }
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

}
