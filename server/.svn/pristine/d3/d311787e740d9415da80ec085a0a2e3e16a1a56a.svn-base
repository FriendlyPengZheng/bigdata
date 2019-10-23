package com.taomee.bigdata.task.combat;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;

import java.io.*;
/**
 * 处理极战联盟用户每天游戏胜率
 * 输入格式:
 * hip_=10.1.1.63 _stid_=_combat_ _sstid_=_combat_    _gid_=169   _zid_=-1    _sid_=-1    _pid_=-1    _ts_=1467701209 _acid_=331025611    _plid_=-1   _win_=1 _cbtime_=30
 * 输出格式:
 * key:
 * gid  zone  server  platform  uid
 * value:
 * 0/1
 * @author looper
 * @date 2016年7月5日
 */
public class SourceCombatMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable>
{
    private Text outputKey = new Text();
    //protected IntWritable outputValue = new IntWritable(0);
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }


    public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_stid_").compareTo("_combat_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            DoubleWritable outputValue=null;
           //校验传递进来的win参数是不是整数0，1
            if(CombatUtil.isNumeric(logAnalyser.getValue("_win_")))
            {
            	outputValue=new DoubleWritable(Double.parseDouble(logAnalyser.getValue("_win_")));
            }else
            {
            	return;
            }
          //  double number=Double.parseDouble(logAnalyser.getValue("_win_"));
           // DoubleWritable outputValue=new DoubleWritable(Double.parseDouble(logAnalyser.getValue("_win_")));
            if(game == null ||
                platform == null ||
                zone == null ||
                server == null ||
                uid == null) {
                r.setCode("E_SOURCEACTIVE_MAPPER", String.format("get info error game=[%s], platform=[%s], zone=[%s], server=[%s], uid=[%s], stid=[%s] from [%s]",
                        game, platform, zone, server, uid, value.toString()));
                return;
            }
            outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                        game, zone, server, platform, uid));
            output.collect(outputKey, outputValue);
        }
    }

}
