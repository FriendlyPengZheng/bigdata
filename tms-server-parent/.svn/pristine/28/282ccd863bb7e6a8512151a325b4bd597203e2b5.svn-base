package com.taomee.tms.storm.function;

//import storm.trident.operation.BaseFunction;
//import storm.trident.operation.TridentCollector;
//import storm.trident.operation.TridentOperationContext;
//import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;

public class CountCacheParamGenFunction extends BaseCacheParamGenFunction {
	private static final long serialVersionUID = 1384422455258002077L;
	private static final Logger LOG = LoggerFactory.getLogger(CountCacheParamGenFunction.class);
	private Integer partitionIndex;

	protected boolean GetParamsFromTuple(TridentTuple tuple) {
		if (setTuple(tuple) && getSchemaId() && getServerId() && getCascadeValue() && getDateTime()) {
			return true;
		}
		
		return false;
    }
	
	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		super.prepare(conf, context);
		this.partitionIndex = context.getPartitionIndex();
	}

	public void execute(TridentTuple tuple, TridentCollector collector) {
		if (!GetParamsFromTuple(tuple)) {
			LOG.error("CountCacheParamGenFunction execute, GetParamsFromTuple failed");
			return;
		}
				
		List<Object> values = new ArrayList<Object>();
		values.add(GetCacheKey());
		collector.emit(values);
//		LOG.debug("CountCacheParamGenFunction emit field dateTime_schemaId_serverId_cascadeValue with values:{} for tuple:schemaId {}, serverId {}, cascadeValue {}, opValues {}, dateTime {}",
//				values,
//				tuple.getString(0),
//				tuple.getString(1),
//				tuple.getString(2),
//				tuple.get(3),
//				tuple.getString(4)
//		);
		LOG.debug("partition {} emitting [{},{},{},{},{},{}]",
				this.partitionIndex,
				tuple.getString(0),
				tuple.getString(1),
				tuple.getString(2),
				tuple.get(3),
				tuple.getString(4),
				values
		);
	}
}
















