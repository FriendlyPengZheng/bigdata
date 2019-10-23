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

public class DataInfoKeyGenFunction extends BaseFunction {
	private static final long serialVersionUID = 7705114279721616395L;
	private static final Logger LOG = LoggerFactory.getLogger(DataInfoKeyGenFunction.class);
	
	protected TridentTuple tuple = null;
	
	protected String strSchemaId;
	protected String cascadeValue;
	
	protected List<String> opValues;
	protected Long opValue = 0L;
	
	protected String dateTime;
	
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context) {
    }

    protected boolean SetTuple(TridentTuple tuple) {
    	if (tuple == null) {
    		LOG.error("DataInfoKeyGenFunction SetTuple, null tuple");
    		return false;
    	}
    	
    	this.tuple = tuple;
    	return true;
    }
    
    protected boolean GetSchemaId() {
    	strSchemaId = tuple.getString(0);
    	if (strSchemaId == null) {
			LOG.error("DataInfoKeyGenFunction GetSchemaId, null strSchemaId in tuple");
			return false;
		}
    	return true;
    }
    
    protected boolean GetCascadeValue() {
    	cascadeValue = tuple.getString(1);
    	if (cascadeValue == null) {
			LOG.error("DataInfoKeyGenFunction GetCascadeValue, null cascadeValue in tuple");
			return false;
		}
    	return true;
    }

    // "schemaId", "serverId", "cascadeValue", "opValues", "dataTime"
	protected boolean GetParamsFromTuple(TridentTuple tuple) {
		if (SetTuple(tuple) && GetSchemaId() && GetCascadeValue()) {
			return true;
		}
		
		return false;
    }
    
	protected String GetDataIdKey() {
		StringBuffer buffer = new StringBuffer();
		
		// 去掉秒，如"2017-01-12 07:44:23"变成"2017-01-12 07:44"
		buffer.append(strSchemaId);
		buffer.append(" ");
		buffer.append(cascadeValue);
		
		return buffer.toString();
	}
	
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		LOG.info("BaseCacheParamGenFunction execute called");
		if (!GetParamsFromTuple(tuple)) {
			LOG.error("DataInfoKeyGenFunction execute, GetParamsFromTuple failed");
			return;
		}
				
		List<Object> values = new ArrayList<Object>();
		values.add(GetDataIdKey());
		collector.emit(values);
	}

	public static void main(String[] args) {
		
	}
}

























