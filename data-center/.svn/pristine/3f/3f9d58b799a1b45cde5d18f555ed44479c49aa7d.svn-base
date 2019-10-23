package com.taomee.bigdata.task.coins;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.util.MysqlConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Iterator;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
{
    private Text outputKey = new Text();
    private LongWritable outputValue = new LongWritable();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private HashMap<String, Integer> coinsValueMap = new HashMap<String, Integer>();
    private HashSet<String> mobileGame = new HashSet<String>();

    public void configure(JobConf job) {
        MysqlConnection mysql = new MysqlConnection();
        mysql.connect(job.get("mysql.url"),
                job.get("mysql.user"),
                job.get("mysql.passwd"));
        if(mysql == null) {
            throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                        job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
        }
        ResultSet result = mysql.doSql("select game_id from t_game_info where game_type='mobilegame'");
        if(result == null) return;
        try {
            while(result.next()) {
                try {
                    mobileGame.add(result.getString(1));
                } catch (java.lang.IllegalArgumentException e) { }
            }
        } catch (SQLException e) {
            ReturnCode.get().setCode("E_GET_STID_FROM_DB", e.getMessage());
        }

        result = mysql.doSql("select game_id,item_id,coins from t_game_coins_config");
        if(result == null) return;
        try {
            while(result.next()) {
                try {
                    int g = result.getInt(1);
                    int i = result.getInt(2);
                    int c = result.getInt(3);
                    
                    coinsValueMap.put(g+"\t"+i, c);

                    //测试
                    g += 100000;
                    coinsValueMap.put(g+"\t"+i, c);
                } catch (java.lang.IllegalArgumentException e) { }
            }
        } catch (SQLException e) {
            ReturnCode.get().setCode("E_GET_GAME_COINS_FROM_DB", e.getMessage());
        }

        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    //输出 key=game,platform,zone,server,udid  value=1,付费额
    public void map(LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            String stid = logAnalyser.getValue(logAnalyser.STID);
            String sstid = logAnalyser.getValue(logAnalyser.SSTID);
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null &&
                stid != null &&
                sstid != null) {
                if(stid.compareTo("_buyitem_") == 0 && sstid.compareTo("_mibiitem_") == 0) {
                    String item = logAnalyser.getValue("_item_");
                    Long itmcnt = Double.valueOf(logAnalyser.getValue("_itmcnt_")).longValue();
                    Integer values = coinsValueMap.get(game+"\t"+item);
                    if(values != null) { //配置道具id对应获得的游戏币数量
                        Long golds = values * itmcnt;
                        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                    game, zone, server, platform, uid));
                        outputValue.set(golds * 100);
                        output.collect(outputKey, outputValue);
                        outputValue.set(golds);
                        mos.getCollector("userbuy", reporter).collect(outputKey, outputValue);
                    }
                } else if(stid.compareTo("_getgold_") == 0 && sstid.compareTo("_systemsend_") == 0) {
                    Long golds = Double.valueOf(logAnalyser.getValue("_golds_")).longValue();
                    if(mobileGame.contains(game))   golds *= 100l;
                    outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                game, zone, server, platform, uid));
                    outputValue.set(golds);
                    output.collect(outputKey, outputValue);
                } else if(stid.compareTo("_usegold_") == 0 && sstid.compareTo("_usegold_") == 0) {
                    Long golds = Double.valueOf(logAnalyser.getValue("_golds_")).longValue();
                    if(mobileGame.contains(game))   golds *= 100l;
                    outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                                game, zone, server, platform, uid));
                    outputValue.set(-golds);
                    output.collect(outputKey, outputValue);
                    outputValue.set(golds);
                    mos.getCollector("useruse", reporter).collect(outputKey, outputValue);
                }
            }
        }
    }

}
