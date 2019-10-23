package com.taomee.bigdata.basic;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.LogAnalyser;
import com.taomee.bigdata.util.MysqlConnection;
import com.taomee.bigdata.util.GetGameinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicSplitMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();
    private boolean divide = false;
    private MultipleOutputs mos = null;
    private HashMap<String, String> basicStid = new HashMap<String, String>();
    private GetGameinfo gameInfo = GetGameinfo.getInstance();
    private String gameIgnore = new String();

    public void configure(JobConf job) {

		//获取黑名单游戏id信息
		gameIgnore = job.get("gameIgnore");
        //获取每个基础项的mosName
        String divide = job.get("divide");
        if(divide != null && divide.compareToIgnoreCase("true") == 0) {
            //获取当天所有有日志的游戏id
            HashSet<String> gameSet = new HashSet<String>();
            String calcDate = job.get("date");
            try {
                FileSystem fs = FileSystem.get(job);
                String inputPath = "/bigdata/input/" + calcDate + "/*/*basic*";
                FileStatus[] status = fs.globStatus(new Path(inputPath));
                for(int j=0; j<status.length; j++) {
                    Path path = status[j].getPath();
                    gameSet.add(path.toString().split("/")[6]);
                }
            } catch (IOException e) { }

            //将游戏id传给gameinfo
            Iterator<String> itGame = gameSet.iterator();
            String r = new String();
            while(itGame.hasNext()) {
                r += (itGame.next() + ",");
            }
            JobConf gameInfoConf = new JobConf();
            gameInfoConf.set("GameInfo", r);
            gameInfo.config(gameInfoConf);

            MysqlConnection mysql = new MysqlConnection();
            mysql.connect("jdbc:mysql://10.25.221.237/db_td_config?characterEncoding=utf8","tdconfig","tdconfig@mysql");
            if(mysql == null) {
                throw new RuntimeException(String.format("url=[%s] user=[%s] pwd=[%s]",
                            job.get("mysql.url"), job.get("mysql.user"), job.get("mysql.passwd")));
            }
            ResultSet result = mysql.doSql("select stid, mosname from t_common_stid");
            if(result == null) return;
            basicStid.clear();
            try {
                while(result.next()) {
                    try {
                        String stid = result.getString(1);
                        String mosname = result.getString(2);
                        mosname = mosname.substring(1, mosname.length() - 1);
                        MultipleOutputs.addNamedOutput(
                                job, mosname,
                                Class.forName("org.apache.hadoop.mapred	.TextOutputFormat").asSubclass(OutputFormat.class),
                                Class.forName("org.apache.hadoop.io.Text").asSubclass(WritableComparable.class),
                                Class.forName("org.apache.hadoop.io.NullWritable").asSubclass(Writable.class));

                        MultipleOutputs.addNamedOutput(
                                job, mosname + "Gother",
                                Class.forName("org.apache.hadoop.mapred.TextOutputFormat").asSubclass(OutputFormat.class),
                                Class.forName("org.apache.hadoop.io.Text").asSubclass(WritableComparable.class),
                                Class.forName("org.apache.hadoop.io.NullWritable").asSubclass(Writable.class));

                        itGame = gameSet.iterator();
                        while(itGame.hasNext()) {
                            MultipleOutputs.addNamedOutput(
                                    job, mosname + "G" + itGame.next(),
                                    Class.forName("org.apache.hadoop.mapred.TextOutputFormat").asSubclass(OutputFormat.class),
                                    Class.forName("org.apache.hadoop.io.Text").asSubclass(WritableComparable.class),
                                    Class.forName("org.apache.hadoop.io.NullWritable").asSubclass(Writable.class));
                        }
                        basicStid.put(stid, mosname);
                    } catch (java.lang.ClassNotFoundException e) {
                        ReturnCode.get().setCode("E_CONF_CLASS_NOT_FOUND", e.getMessage());
                    } catch (java.lang.IllegalArgumentException e) { }
                }
            } catch (SQLException e) {
                ReturnCode.get().setCode("E_GET_STID_FROM_DB", e.getMessage());
            }
            mysql.close();
            this.divide = true;
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Integer opCode;
        String[] items;
        String stid;
        String sstid;
        String game; 
        String hip;
        String time; 
        if(logAnalyser.analysisAndGet(value.toString()) == ReturnCode.G_OK) {
            stid = logAnalyser.getValue("_stid_");
            sstid = logAnalyser.getValue("_sstid_");
            game = logAnalyser.getValue("_gid_");
            hip = logAnalyser.getValue("_hip_");
            time = logAnalyser.getValue("_ts_");
			//此处判断游戏id，若在黑名单，则不进行后续处理，直接跳出
			/*if(gameIgnore.contains(","+game+",")){
				return;
			}*/
            ArrayList<String[]> o = logAnalyser.getOutput();
            for(int i=0; i<o.size(); i++) {
                items = o.get(i);
                //items[0] = op, items[1] = time, items[2] = value, items[3] = key
                opCode = Operator.getOperatorCode(items[0]);
                if(items[3].contains("_acpay_\t_acpay_")) {
                    continue;
                }
                outputKey.set(String.format("%d\t%s", opCode, items[3]));
                if(opCode == Operator.SET ||
                        opCode == Operator.DISTR_SET) {//set类型需要在value后面多加一个时间字段，用来判断哪个value才是最后一个
                    outputValue.set(String.format("%s\t%s", items[2], items[1]));
                } else {//value值默认为一，这样ucount和count的有些步骤也可以和sum，max一样处理了
                    //热血 金额*100
                    if(opCode == Operator.SUM &&
                            (game.compareTo("16") == 0 || game.compareTo("19") == 0) &&
                            (stid.compareTo("_usegold_") == 0 ||
                             (stid.compareTo("_getgold_") == 0 && sstid.compareTo("_systemsend_") == 0))) {
                        outputValue.set(String.format("%d", Integer.valueOf(items[2]) * 100));
                    } else {
                        outputValue.set(items[2]==null ? "1" : items[2]);
                    }
                }
                output.collect(outputKey, outputValue);

                if(items[3].contains("_acpay_") &&
                        !items[3].contains("_acpay_\t_buycoins_") && !items[3].contains("_acpay_\t_costfree_")) {
                    items[3] = items[3].replace(logAnalyser.getValue("_sstid_"), "_acpay_");
                    outputKey.set(String.format("%d\t%s", opCode, items[3]));
                    if(opCode == Operator.SET ||
                            opCode == Operator.DISTR_SET) {//set类型需要在value后面多加一个时间字段，用来判断哪个value才是最后一个
                        outputValue.set(String.format("%s\t%s", items[2], items[1]));
                    } else {//value值默认为一，这样ucount和count的有些步骤也可以和sum，max一样处理了
                        outputValue.set(items[2]==null ? "1" : items[2]);
                    }
                    output.collect(outputKey, outputValue);
                }
                //game630 落购买游戏币
                if(game.compareTo("630") == 0 &&
                        items[3].contains("_acpay_\t_buycoins_")) {
                    outputKey.set(String.format("%d\t%s", opCode, items[3]));

                    if(opCode == Operator.SET ||
                            opCode == Operator.DISTR_SET) {//set类型需要在value后面多加一个时间字段，用来判断哪个value才是最后一个
                        outputValue.set(String.format("%s\t%s", items[2], items[1]));
                    } else {//value值默认为一，这样ucount和count的有些步骤也可以和sum，max一样处理了
                        outputValue.set(items[2]==null ? "1" : items[2]);
                    }
                    output.collect(outputKey, outputValue);

                    items[3] = items[3].replace(logAnalyser.getValue("_sstid_"), "_acpay_");
                    outputKey.set(String.format("%d\t%s", opCode, items[3]));
                    output.collect(outputKey, outputValue);
                }
            }
        } else {
            return;
        }
        outputKey.set(String.format("%d\t%s\t%s\t%s\t%s", Operator.HIP_COUNT, stid, sstid, game, hip));
        outputValue.set(time);
        output.collect(outputKey, outputValue);
        //r.setCode("D_HIP_COUNT", outputKey.toString() + ":" + outputValue.toString());
        items = value.toString().split("\t");
        boolean isAcpay = false;
        boolean isCplanItem = false;
        if(divide && stid != null && stid.startsWith("_")) {
            if(stid.compareTo("_acpay_") == 0
                    && sstid.compareTo("_acpay_") == 0)
                return ;
            if(stid.compareTo("_acpay_") == 0) {
                isAcpay = true;
            }
            //热血 金额*100
            if((game.compareTo("16") == 0 || game.compareTo("19") == 0) &&
                    (stid.compareTo("_usegold_") == 0 ||
                    (stid.compareTo("_getgold_") == 0 && sstid.compareTo("_systemsend_") == 0))) {
                isCplanItem = true;
            }
            String mosName = basicStid.get(stid);
            if(mosName != null) {
                String pzs[] = logAnalyser.combine();
                for(int i=0; i<pzs.length; i++) {
                    String p[] = pzs[i].split("\t");
                    StringBuffer buffer = new StringBuffer();
                    for(int j=0; j<items.length; j++) {
                        if(items[j].startsWith("_zid_=")) {
                            buffer.append("_zid_=" + p[0]);
                        } else if(items[j].startsWith("_sid_=")) {
                            buffer.append("_sid_=" + p[1]);
                        } else if(items[j].startsWith("_pid_=")) {
                            buffer.append("_pid_=" + p[2]);
                        } else if(isCplanItem && items[j].startsWith("_golds_=")){
                            buffer.append(String.format("_golds_=" + Integer.valueOf(logAnalyser.getValue("_golds_")) * 100));
                        } else {
                            buffer.append(items[j]);
                        }
                        if(j != items.length - 1)   buffer.append("\t");
                    }
                    outputKey.set(buffer.toString());
                    mos.getCollector(mosName + gameInfo.getValue(game), reporter).collect(outputKey, NullWritable.get());
                    if(isAcpay && (sstid.compareTo("_buycoins_") != 0 && sstid.compareTo("_costfree_") != 0 && sstid.compareTo("_acpay_") != 0)) {
                        outputKey.set(buffer.toString().replace("_sstid_=" + sstid, "_sstid_=_acpay_"));
                        mos.getCollector(mosName + gameInfo.getValue(game), reporter).collect(outputKey, NullWritable.get());
                    }

                    if(isAcpay && (game.compareTo("630") == 0) && (sstid.compareTo("_buycoins_") == 0)) {
                        outputKey.set(buffer.toString().replace("_sstid_=" + sstid, "_sstid_=_acpay_"));
                        mos.getCollector(mosName + gameInfo.getValue(game), reporter).collect(outputKey, NullWritable.get());
                    }
                }
            }
        }
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

}
