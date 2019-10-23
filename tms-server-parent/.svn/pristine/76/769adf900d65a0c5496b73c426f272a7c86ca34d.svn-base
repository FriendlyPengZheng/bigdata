package com.taomee.bigdata.lib;

import com.taomee.tms.mgr.entity.TaskInfo;

public class GetTaskType {
	public static void main(String[] args){
		try{
			if(args.length < 1){
				throw new IllegalArgumentException("param:taskID could not be null");
			}
			int taskID = Integer.valueOf(args[0]);
			TaskInfo taskInfo = Utils.getTaskInfo(taskID);
			if(taskInfo == null){
				throw new IllegalArgumentException("could not find relating task:task"+taskID);
			}
			String type = Utils.getTaskInfo(taskID).getType();
			switch(type.toLowerCase()){
			case "mr":
				System.exit(1);
				break;
			case "hql":
				System.exit(2);
				break;
			default:
				System.exit(255);
				break;
			}
		}catch(Throwable e){
			e.printStackTrace();
			System.exit(255);
		}
	}
}
