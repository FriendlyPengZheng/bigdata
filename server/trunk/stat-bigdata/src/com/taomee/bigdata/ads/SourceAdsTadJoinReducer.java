package com.taomee.bigdata.ads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.AdParser;
import com.taomee.bigdata.util.GetGameinfo;
import com.taomee.bigdata.util.LogAnalyser;

public class SourceAdsTadJoinReducer extends MapReduceBase implements Reducer<Text,Text,Text,NullWritable>{
	private HashMap<String,Integer[]> tad2Gpzs = new HashMap<String,Integer[]>();
	private LinkedList<String> tmpSourceStrs = new LinkedList<String>();
	private AdParser adp = new AdParser();
	private ReturnCodeMgr rOutput;
	private LogAnalyser logAnalyser = new LogAnalyser();
	private Text outputKey = new Text();
	private MultipleOutputs mos = null;
	private NullWritable outputValue = NullWritable.get();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();

	@Override
	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		mos = new MultipleOutputs(job);
		initTad2GpzsMap(tad2Gpzs);
	}

	private void initTad2GpzsMap(HashMap<String,Integer[]> map) {
		map.put("2	-1	-1	-1	#7k7k.com", new Integer[]{10004,2,1,-1});
		map.put("2	-1	-1	-1	#4399.com", new Integer[]{10004,2,2,-1});
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, NullWritable> output, Reporter reporter)throws IOException {
		tmpSourceStrs.clear();
		String tad = "";
		boolean uidHasData = false;
		boolean uidHasTad = false;
		while(values.hasNext()){
			String value = values.next().toString();
			if(value.split("\t")[0].equals("1")){
				uidHasData = true;
				tmpSourceStrs.add(value.substring(value.indexOf("\t")+1));
			}else if(value.split("\t")[0].equals("0")){
				uidHasTad = true;
				adp.init(value.split("\t")[1]);
				Iterator<String> it= adp.iterator();
				if(it.hasNext()){
					tad = it.next();
				}
			}
		}
		if( uidHasData && uidHasTad ){
			for(String sourceStr:tmpSourceStrs){
				if (logAnalyser.analysis(sourceStr) == ReturnCode.G_OK) {
					String game = logAnalyser.getValue(logAnalyser.GAME);
					String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
					String zone = logAnalyser.getValue(logAnalyser.ZONE);
					String server = logAnalyser.getValue(logAnalyser.SERVER);
					Integer[] gpzs = tad2Gpzs.get(String.format("%s\t%s\t%s\t%s\t%s", game,platform,zone,server,tad));
					if(gpzs != null){
						String resultStr = sourceStr.replace("_gid_="+game, "_gid_="+gpzs[0]).replace("_pid_="+platform, "_pid_="+gpzs[1]).replace("_zid_="+zone, "_zid_="+gpzs[2]).replace("_sid_="+server, "_sid_="+gpzs[3]);
						outputKey.set(resultStr);
						String gameinfo = getGameinfo.getValue(""+gpzs[0]);
						mos.getCollector("part" + gameinfo, reporter).collect(outputKey, outputValue);
					}
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		mos.close();
	}

	
}
