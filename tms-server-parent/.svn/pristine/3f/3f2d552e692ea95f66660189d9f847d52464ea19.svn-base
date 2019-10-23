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

public class BaseCacheParamGenFunction extends BaseFunction {
	private static final long serialVersionUID = -6533372558210738896L;
	private static final Logger LOG = LoggerFactory.getLogger(BaseCacheParamGenFunction.class);
	
	protected TridentTuple tuple = null;
	
	protected String strSchemaId;
	protected String strServerId;
	protected String cascadeValue;
	
	protected List<String> opValues;
	protected Long opValue = 0L;
	
	protected String dateTime;
	private Integer partitionIndex;
	
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context) {
    	this.partitionIndex = context.getPartitionIndex();
    }

    protected boolean setTuple(TridentTuple tuple) {
    	if (tuple == null) {
    		return false;
    	}
    	
    	this.tuple = tuple;
    	return true;
    }

    protected boolean getSchemaId() {
    	this.strSchemaId = tuple.getString(0);
    	if (strSchemaId == null) {
			return false;
		}
    	return true;
    }
    
    protected boolean getServerId() {
    	this.strServerId = tuple.getString(1);
    	if (strServerId == null) {
			return false;
		}
    	return true;
    }
    
    protected boolean getCascadeValue() {
    	this.cascadeValue = tuple.getString(2);
    	if (cascadeValue == null) {
			return false;
		}
    	return true;
    }

    protected boolean getOpValues() {
    	try {
			@SuppressWarnings("unchecked")
			List<String> values = (List<String>)tuple.get(3);
			this.opValues = values;
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return false;
		} catch (ClassCastException ex) {
			ex.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		if (opValues.size() != 1 || opValues.get(0) == null) {
			return false;
		}
		
		try {
			this.opValue = Long.parseLong(opValues.get(0));
		} catch (NumberFormatException ex) {
			LOG.debug("Error:value \"{}\" is not a number" ,opValues.get(0));
			return false;
		} catch(Exception e){
			LOG.error("BaseCacheParamGenFunction GetOpValues, exception occured " + e.getMessage() + ", strValue is [" + opValues.get(0) + "]");
		}
    	
    	return true;
    }
    
    protected boolean getDateTime() {
    	dateTime = tuple.getString(4);
    	if (dateTime == null) {
			LOG.error("BaseCacheParamGenFunction GetDateTime, null dateTime in tuple");
			return false;
		}
    	
    	if (dateTime == null) {
			LOG.error("BaseCacheParamGenFunction GetDateTime, null param in tuple");
			return false;
		}
    	
    	if (dateTime.length() <= 3) {
			LOG.error("BaseCacheParamGenFunction GetDateTime, invalid dateTime [" + dateTime + "], schemaId " + strSchemaId + ", serverId " + strServerId);
			return false;
		}
    	
    	return true;
    }
    
    // "schemaId", "serverId", "cascadeValue", "opValues", "dataTime"
	protected boolean GetParamsFromTuple(TridentTuple tuple) {
		if (setTuple(tuple) && getSchemaId() && getServerId() && getCascadeValue() && getOpValues() && getDateTime()) {
			return true;
		}
		
		return false;
    }
    
	protected String GetCacheKey() {
		StringBuffer buffer = new StringBuffer();
		
		// 去掉秒，如"2017-01-12 07:44:23"变成"2017-01-12 07:44"
		buffer.append(dateTime.substring(0, dateTime.length() - 3));
		buffer.append("_");
		buffer.append(strSchemaId);
		buffer.append("_");
		buffer.append(strServerId);
		buffer.append("_");
		buffer.append(cascadeValue);
		
		return buffer.toString(); 
	}
	
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		if (!GetParamsFromTuple(tuple)) {
			return;
		}
				
		List<Object> values = new ArrayList<Object>();
		values.add(GetCacheKey());
		values.add(opValue);
		collector.emit(values);
		LOG.debug("partition {} emitting [{},{},{},{},{},{}]",
				this.partitionIndex,
				tuple.getString(0),
				tuple.getString(1),
				tuple.getString(2),
				tuple.get(3),
				tuple.getString(4),
				values
		);
//		LOG.debug("BaseCacheParamGenFunction emit field dateTime_schemaId_serverId_cascadeValue with values:{} for tuple:schemaId {}, serverId {}, cascadeValue {}, opValues {}, dateTime {}",
//				values,
//				tuple.getString(0),
//				tuple.getString(1),
//				tuple.getString(2),
//				tuple.get(3),
//				tuple.getString(4)
//				);
	}

	public static void main(String[] args) {
		String dateTime = "2017-01-12 07:44:23";
		System.out.println("[" + dateTime.substring(0, dateTime.length() - 3) + "]");
		
		try {
			String str = null;
			Object objStr = (Object)str;
			List<String> opValues = (List<String>)objStr;
			System.out.println(opValues.get(0));
//			List<String> opValues = (List<String>)str;
		} catch (NullPointerException ex) {
			System.out.println("NullPointerException occured");
		} catch (ClassCastException ex) {
			System.out.println("ClassCastException occured");
		} catch (Exception ex) {
			System.out.println("Other exception " + ex.getMessage());
		}
	}
}

























