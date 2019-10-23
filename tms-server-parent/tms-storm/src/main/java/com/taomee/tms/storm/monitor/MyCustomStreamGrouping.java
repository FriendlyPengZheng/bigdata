package com.taomee.tms.storm.monitor;

import java.util.List;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.grouping.CustomStreamGrouping;
import org.apache.storm.task.WorkerTopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import backtype.storm.generated.GlobalStreamId;
//import backtype.storm.grouping.CustomStreamGrouping;
//import backtype.storm.task.WorkerTopologyContext;

public class MyCustomStreamGrouping implements CustomStreamGrouping {
	
	private static final Logger LOG = LoggerFactory.getLogger(MyCustomStreamGrouping.class);
	
	private List<Integer> tasks;
	
	public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks)
	{
		tasks = targetTasks;
		for (Integer tmp:targetTasks)
		{
			LOG.error("MyCustomStreamGrouping.prepare one element in targetTasks: " + tmp);
		}
	}
	
	public List<Integer> chooseTasks(int taskId, List<Object> values)
	{
		return tasks;
	}
}
