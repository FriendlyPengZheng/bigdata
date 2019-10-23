package myDemo_2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DemoReducer2 extends Reducer<Text, Text, Text, Text>{

	@Override
	protected void reduce(Text miNum, Iterable<Text> tad_time,Context context)
			throws IOException, InterruptedException {
		
		String tad = null;
		boolean T = false;
		boolean D = false;
		for(Text t : tad_time){
			
			String data = t.toString();
			
			if(data.charAt(0) == 'T'){
				T = true;
				tad = data.substring(1);
			}
			
			if(data.charAt(0) == 'D'){
				D = true;
			}
		}
		//tad存在，时间戳不存在（不需要的数据）
		if(T && !D){
			tad = null;
		}
		//tad不存在，时间戳存在（需要的数据，且tad=unknown）
		if(!T && D){
			tad = "unknown";
		}
		
		if(tad != null && !"".equals(tad)){
			context.write(miNum, new Text(tad));
		}
	}

	
}
