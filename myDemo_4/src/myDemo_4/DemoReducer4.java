package myDemo_4;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DemoReducer4 extends Reducer<Text, Text, Text, Text>{



	private static float num_5_5 = 0f;//五月gameId=2
	private static float num_and = 0f;//交集
	private static float percent = 0f;
	@Override
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {
		/*
		 * kye:miNum
		 * values:"标记" + tad
		 */
		
		String tad = null;
		String tad1 = null;
		
		boolean fourSeven = false;
		boolean fiveFive = false;
		
		for(Text t : values){
			
			String data = t.toString();
			
			if("4-7".equals(data.substring(0, 3))){
				fourSeven = true;
				tad1 = data.substring(3);
			}
			
			if("5-5".equals(data.substring(0, 3))){
				fiveFive = true;
				//tad2 = data.substring(3);
			}
		}
		/*//4-7月存在，5月不存在
		if(fourSeven && !fiveFive){
			tad = null;
		}*/
		//五月存在
		if(fiveFive){
			num_5_5++;
		}
		/*if(!fourSeven && fiveFive){
			tad = null;
		}*/
		//交集
		if(fiveFive && fourSeven){
			num_and++;
			tad = tad1;
		}

		percent = num_and / num_5_5*100f;
		
		if(tad != null && !"".equals(tad)){
			String tad_percent = tad + "," + percent+"%";
			context.write(new Text(key), new Text(tad_percent));
		}
	}

	
}
