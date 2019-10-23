package com.taomee.bigdata.task.model;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.MysqlConnection;

import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModelSumReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>
{
    private IntWritable outputValue = new IntWritable();
    private DoubleWritable doubleOutputValue = new DoubleWritable();
	private Text outputKey = new Text();
    private HashMap<String, String> modelInfoMap = new HashMap<String, String>();
    private TreeMap<Integer, Integer> steps = new TreeMap<Integer, Integer>();
    private MultipleOutputs mos = null;

    public void configure(JobConf job) {
        MysqlConnection mysql = new MysqlConnection();
        mysql.connect(job.get("mysql.url"),
                job.get("mysql.user"),
                job.get("mysql.passwd"));
        if(mysql == null) {
            throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                        job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
        }
        ResultSet result = mysql.doSql("select model_id,model_step,game_id,step_name from t_model_info order by game_id,model_id,model_step");
        if(result == null) return;
        try {
            while(result.next()) {
                try {
                    int modelid = result.getInt(1);
                    int modelstep = result.getInt(2);
                    int gameid = result.getInt(3);
                    String stepname = result.getString(4);
                    String key = String.format("%d\t%s\t%s", gameid, modelid, modelstep);
                    modelInfoMap.put(key, stepname);
                } catch (java.lang.IllegalArgumentException e) { }
            }
        } catch (SQLException e) {
            ReturnCode.get().setCode("E_GET_MODEL_FROM_DB", e.getMessage());
        }
        mysql.close();
        mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    //input  key=game,modelid,uid value=step,time
    //output part:game,modelid,uid,laststep
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        String items[] = key.toString().split("\t");
        String game = items[0];
        String modelid = items[1];
        steps.clear();
        Integer step;
        Integer cnt;
        double sum = 0.0;
        while(values.hasNext()) {
            step = Integer.valueOf(-values.next().get());
            cnt = steps.get(step);
            if(cnt == null) cnt = 0;
            cnt ++;
            steps.put(step, cnt);
            sum ++;
        }
        Integer max = -steps.firstKey();
        Integer stepscnt[] = new Integer[max+2];
        stepscnt[max+1] = 0;
        stepscnt[0] = (int)sum;
        for(int i=max; i>=1; i--) {
            cnt = steps.get(-i);
            if(cnt == null) cnt = 0;
            stepscnt[i] = cnt + stepscnt[i+1];

            outputKey.set(String.format("SET\t%s\t-1\t-1\t-1\t_%smodel_\t_%smodel_\tstep,ucount\t%d:%s",
                        game, modelid, modelid, i, getStepName(game, modelid, i)));
            outputValue.set(stepscnt[i]);
            output.collect(outputKey, outputValue);

            outputKey.set(String.format("SET\t%s\t-1\t-1\t-1\t_%smodel_\t_%smodel_\tstep,percent\t%d:%s",
                        game, modelid, modelid, i, getStepName(game, modelid, i)));
            doubleOutputValue.set(stepscnt[i]/sum*100.0);
            mos.getCollector("percent", reporter).collect(outputKey, doubleOutputValue);

            if(i != max) {
                outputKey.set(String.format("SET\t%s\t-1\t-1\t-1\t_%smodel_\t_%smodel_\tstep,percentbystep\t%d:%s",
                            game, modelid, modelid, i+1, getStepName(game, modelid, i+1)));
                doubleOutputValue.set(stepscnt[i+1]/(stepscnt[i]+0.0)*100.0);
                mos.getCollector("percentbystep", reporter).collect(outputKey, doubleOutputValue);
            }
        }
        outputKey.set(String.format("SET\t%s\t-1\t-1\t-1\t_%smodel_\t_%smodel_\tstep,percentbystep\t%d:%s",
                    game, modelid, modelid, 1, getStepName(game, modelid, 1)));
        doubleOutputValue.set(100.0);
        mos.getCollector("percentbystep", reporter).collect(outputKey, doubleOutputValue);
    }

    private String getStepName(String game, String modelid, Integer modelstep) {
        String key = String.format("%s\t%s\t%d", game, modelid, modelstep);
        String name = modelInfoMap.get(key);
        if(name != null) return name;
        key = String.format("-1\t%s\t%d", modelid, modelstep);
        return modelInfoMap.get(key);
    }
}
