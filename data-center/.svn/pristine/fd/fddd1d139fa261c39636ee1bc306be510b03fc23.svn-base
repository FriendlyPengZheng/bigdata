package com.taomee.bigdata.task.device;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import com.taomee.bigdata.util.GetGameinfo;

public class DeviceReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();
    private Text outputKey = new Text();
    private HashMap<String, String> deviceInfo = new HashMap<String, String>();//有多个设备类型只会取一个
    private GetGameinfo getGameinfo  = GetGameinfo.getInstance();
	private MultipleOutputs mos = null;

	public void configure(JobConf job) {
		mos = new MultipleOutputs(job);
		getGameinfo.config(job);
	}   

	public void close() throws IOException {
		mos.close();
	}   

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException
    {       
        boolean isNewer = false;
        deviceInfo.clear();
        String gameid = key.toString().split("\t")[0];
		String gameinfo = getGameinfo.getValue(gameid);
        while(values.hasNext()) {
            String value[] = values.next().toString().split("\t");
            if(value[0].compareTo("0") == 0) {
                isNewer = true;
            } else {
                try {
                    deviceInfo.put(value[1], value[2]);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) { }
            }
        }
        if(!isNewer)    return ;
        Iterator<String>it = deviceInfo.keySet().iterator();
        while(it.hasNext()) {
            String dev = it.next();
            outputKey.set(String.format("%s\t%s\t%s", key.toString(), dev, deviceInfo.get(dev)));
			mos.getCollector("part"+gameinfo, reporter).collect(key, outputValue);
            //output.collect(outputKey, outputValue);
        }
    }
}
