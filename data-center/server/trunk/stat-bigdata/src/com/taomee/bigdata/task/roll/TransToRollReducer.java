package com.taomee.bigdata.task.roll;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;

public class TransToRollReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
	private Integer first_server = -1000000;
	private Integer roll_server = -2000000;


    //input key=gid,pid,zid,uid  value=0/1,sid,[value_left]
    //output key=gid,pid,sid,zid,uid  value=[value_left]
    //output key=gid,pid,sid-100000,zid,uid  value=[value_left]
    //output key=gid,pid,sid-200000,zid,uid  value=[value_left]
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
		HashSet<String> login_value = new HashSet<String>();
		String lsid = new String(); 
		String fsid = new String(); 
		String nullString = new String("");
        while(values.hasNext()) {
			String value_tmp = values.next().toString();
            String items[] = value_tmp.split("\t");
            int type = Integer.valueOf(items[0]);
            if(type == 0){
				login_value.add(value_tmp);
            }
			else if(type == 1){
				fsid = items[1];
			}else{
				//remain the source data for all games
				outputValue.set(String.format("%s", nullString));
				output.collect(key, outputValue);
			}
        }
		String key_items[] = key.toString().split("\t");
		Iterator<String> itset = login_value.iterator();
		while(itset.hasNext()){
			String items_login_value[] = itset.next().split("\t");
			lsid = items_login_value[1];
			Integer sid = new Integer(0);
			if(lsid.compareTo("-1") == 0){
				continue;
			}else if(lsid.compareTo(fsid) == 0){
				sid = first_server - Integer.valueOf(lsid);
			}else{
				sid = roll_server - Integer.valueOf(lsid);
			}
			outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", 
						key_items[0], key_items[1], sid, key_items[2], key_items[3]));
			if(items_login_value.length > 2){
				String value_left = new String();
				for(int i = 2; i < items_login_value.length; i++){
					value_left += items_login_value[i];
					value_left = i == items_login_value.length - 1 ? value_left : value_left + "\t";
				}
				outputValue.set(String.format("%s", value_left));
				output.collect(outputKey, outputValue);
			}else{
				outputValue.set(String.format("%s", nullString));
				output.collect(outputKey, outputValue);
			}
		}
			
    }
}

