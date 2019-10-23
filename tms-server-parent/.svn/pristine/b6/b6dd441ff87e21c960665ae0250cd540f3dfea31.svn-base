package test.taomee.bigdata.lib;

import org.apache.log4j.Logger;

import com.taomee.bigdata.lib.Utils;

public class getAllPreTasks {
	public static void main(String args[]){
		Logger logger = Logger.getLogger(getAllPreTasks.class);
		int[] tasks = new int[]{10,11,12,13,14,15,16,17,18,19,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,69,7,70,71,72,77,78,79,8,80,81,82,89,9,90,91,92};
		for(int i :tasks){
			System.out.print("task"+i+" :");
			for(int preTask:Utils.getAllPreTask(i)){
				System.out.print(preTask+" ");
			}
			System.out.println();
		}
	}
}


