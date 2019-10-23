package com.taomee.bigdata.task.channel;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import com.taomee.bigdata.util.GetGameinfo;

/**
 * 新增用户获取注册渠道ad
 * 关联的MR有NewUserAdSourceMapper、SourceLgacAdMapper、NewUserGetChannelFlagReducer
 * @author looper
 * @date 2017年2月14日 下午5:01:56
 * @project Tongji_version2 NewUserGetChannelFlagReducer
 */
public class NewUserGetChannelFlagReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {
	/**
	 * 输入数据格式1:
	 * 657     -1      -1      -1      1000643-1000643   nochannel
	 * 输入数据格式2:
	 * 657     -1      -1      -1      1000643-1000643	 ad
	 * 
	 * 输出格式:
	 * 657     -1      -1      -1      1000643-1000643  ad
	 */
	private Text outputValue = new Text();
	private GetGameinfo getGameinfo = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;
	private String ad;
	private Set<String> channelInfo = new HashSet<String>();

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}

	public void close() throws IOException {
		mos.close();
	}

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		channelInfo.clear();
		boolean flag = false;
		String gameid = key.toString().split("\t")[0];
        String gameinfo = getGameinfo.getValue(gameid);	
        while(values.hasNext())
        {
        	ad = values.next().toString();
        	if(ad.equals("nochannel"))
        	{
        		flag = true;
        	}
        	channelInfo.add(ad);
        }
        if(channelInfo.size() < 2)
        {
        	return;
        }
        else if(channelInfo.size() >=2 && flag == true)
        {
        	Iterator<String> it=channelInfo.iterator();
        	String s;
        	while(it.hasNext())
        	{
        		s=it.next().toString();
        		if(!s.equals("nochannel"))
        		{
        			outputValue.set(s);
        			mos.getCollector("part" + gameinfo, reporter).collect(key, outputValue);
        		}
        	}
        }
        
	}

}
