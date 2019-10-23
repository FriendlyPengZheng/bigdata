package com.taomee.tms.storm.filter;

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
public class LogIdFilter extends BaseFilter {	
	private static final long serialVersionUID = 4722433149907722772L;
	private static final Logger LOG = LoggerFactory.getLogger(LogIdFilter.class);
	
	private int partitionIndex;

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context)
	{
		this.partitionIndex = context.getPartitionIndex();
		LOG.info("LogIdFilter prepare() current partitionIndex is " + partitionIndex);
	}
	
	@Override
	public boolean isKeep(TridentTuple tuple) {
		String str = tuple.getString(0);
		LOG.info("LogIdFilter recv: " + str);
		return str.contains("_logid_=");
	}
}
