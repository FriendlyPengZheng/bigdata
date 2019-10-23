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

public class FixTongjiTadJoinReducer extends MapReduceBase implements Reducer<Text,Text,Text,NullWritable>{
	private HashMap<String,Integer[]> tad2Gpzs = new HashMap<String,Integer[]>();
	private LinkedList<String>[] tmpSourceStrs;;
	private AdParser adp = new AdParser();
	private ReturnCodeMgr rOutput;
	private LogAnalyser logAnalyser = new LogAnalyser();
	private Text outputKey = new Text();
	private MultipleOutputs mos = null;
	private NullWritable outputValue = NullWritable.get();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	private String[] mosName;

	@Override
	public void configure(JobConf job) {
		rOutput = new ReturnCodeMgr(job);
		mos = new MultipleOutputs(job);
		initTad2GpzsMap(tad2Gpzs);
		tmpSourceStrs = new LinkedList[job.getInt("maxFlag", 1)];
		for(int i = 0;i<=tmpSourceStrs.length-1;i++){
			tmpSourceStrs[i] = new LinkedList<String>();
		}
		mosName = new String[job.getInt("maxFlag", 1)];
		for(int i = 0;i<=tmpSourceStrs.length-1;i++){
			mosName[i] = job.get("mos"+(i+1), "part");
		}
	}

	private void initTad2GpzsMap(HashMap<String,Integer[]> map) {
		map.put("2	-1	-1	-1	#7k7k.com", new Integer[]{10004,2,1,-1});
		map.put("2	-1	-1	-1	#4399.com", new Integer[]{10004,2,2,-1});
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, NullWritable> output, Reporter reporter)throws IOException {
		for(int i = 0;i<=tmpSourceStrs.length-1;i++){
			tmpSourceStrs[i].clear();
		}
		String tad = "";
		boolean uidHasData = false;
		boolean uidHasTad = false;
		while(values.hasNext()){
			String value = values.next().toString();
			if(value.split("\t")[0].equals("0")){
				uidHasTad = true;
				adp.init(value.split("\t")[1]);
				Iterator<String> it= adp.iterator();
				if(it.hasNext()){
					tad = it.next();
				}
			}else{
				uidHasData = true;
				tmpSourceStrs[Integer.valueOf(value.split("\t")[0])-1].add(value.substring(value.indexOf("\t")+1));
			}
		}
		if( uidHasData && uidHasTad ){
			for(int i = 0 ;i<=tmpSourceStrs.length-1;i++){
				for(String sourceStr:tmpSourceStrs[i]){
					String[] items = sourceStr.split("\t");
					Integer[] gpzs = tad2Gpzs.get(String.format("%s\t%s\t%s\t%s\t%s", items[0],items[1],items[2],items[3],tad));
					if(gpzs != null){
						String resultStr = sourceStr.replace((String.format("%s\t%s\t%s\t%s", items[0],items[1],items[2],items[3])), (String.format("%s\t%s\t%s\t%s", gpzs[0],gpzs[1],gpzs[2],gpzs[3])));
						outputKey.set(resultStr);
						String gameinfo = getGameinfo.getValue(""+gpzs[0]);
						mos.getCollector(mosName[i] + gameinfo, reporter).collect(outputKey, outputValue);
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
