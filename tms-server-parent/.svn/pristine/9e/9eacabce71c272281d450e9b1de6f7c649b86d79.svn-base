package com.taomee.tms.storm.monitor;

import java.util.Map;

import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import storm.trident.operation.BaseFilter;
//import storm.trident.operation.TridentOperationContext;
//import storm.trident.tuple.TridentTuple;

/*
 * filter out invalid log data.
 */
public class DebugLogFilter extends BaseFilter {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(DebugLogFilter.class);
	private int partitionIndex;

	public void prepare(Map conf, TridentOperationContext context)
	{
		this.partitionIndex = context.getPartitionIndex();
		LOG.error("DebugLogFilter prepare() current partitionIndex is " + partitionIndex);
	}
	
	@Override
	public boolean isKeep(TridentTuple tuple) {
		String str = tuple.getString(0);
		LOG.error("DebugLogFilter recv: " + str);
		return true;
//		return !str.startsWith("[") && str.contains("client return:");
	}
}
