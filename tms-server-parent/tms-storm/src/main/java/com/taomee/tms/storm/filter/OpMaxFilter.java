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
public class OpMaxFilter extends BaseFilter {
	private static final long serialVersionUID = 5775443574516663591L;
	private static final Logger LOG = LoggerFactory.getLogger(OpMaxFilter.class);
	private int partitionIndex;

	public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context)
	{
		this.partitionIndex = context.getPartitionIndex();
		LOG.info("partition {} OpMaxFilter prepare() method finished." ,this.partitionIndex);
	}
	
	public boolean isKeep(TridentTuple tuple) {
		String op = tuple.getString(0);
		if(op.equals("max")){
			LOG.debug("emitting [{},{},{},{},{},{}]",
					tuple.getString(0),
					tuple.getString(1),
					tuple.getString(2),
					tuple.getString(3),
					tuple.get(4),
					tuple.getString(5)
					);
//			LOG.debug("OpMaxFilter emitting tuple:op {},schmemaID {},serverID {},cascade value {},op values {},datetime {}",
//					tuple.getString(0),
//					tuple.getString(1),
//					tuple.getString(2),
//					tuple.getString(3),
//					tuple.get(4),
//					tuple.getString(5)
//					);
		}
		return op.equals("max");
	}
}
