package com.taomee.bigdata.task.roll;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;

public class SourceTransToRollReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
	private Integer first_server = -1000000;
	private Integer roll_server = -2000000;


    //input key=gid,pid,zid,uid  value=0/1,sid,[value_source]
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
		HashSet<String> source_value = new HashSet<String>();
		String lsid = new String(); 
		String fsid = new String(); 
        while(values.hasNext()) {
			String value_tmp = values.next().toString();
            String items[] = value_tmp.split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 0){
				source_value.add(value_tmp);
            }
			else if(type == 1){
				fsid = items[1];
			}else{
                //remain the source data for all games
                outputValue.set(String.format("%s", ""));
                output.collect(key, outputValue);
			}
        }
		Iterator<String> itset = source_value.iterator();
		while(itset.hasNext()){
			String items_source_value[] = itset.next().split("\t");
			lsid = items_source_value[1];
			Integer sid_tmp = new Integer(0);
			if(lsid.compareTo("-1") == 0){
				continue;
			}else if(lsid.compareTo(fsid) == 0){
				sid_tmp = first_server - Integer.valueOf(lsid);
			}else{
				sid_tmp = roll_server - Integer.valueOf(lsid);
			}
			String sid = "_sid_=" + sid_tmp.toString();
			outputKey.set(String.format("%s", sid));
			if(items_source_value.length > 2){
				String value_left = new String();
				for(int i = 2; i < items_source_value.length; i++){
					if(items_source_value[i].indexOf("_sid_=") != -1) continue;
					value_left += items_source_value[i];
					value_left = i == items_source_value.length - 1 ? value_left : value_left + "\t";
				}
				outputValue.set(String.format("%s", value_left));
				output.collect(outputKey, outputValue);
			}
		}
			
    }
}

