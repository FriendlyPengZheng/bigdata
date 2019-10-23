package com.taomee.tms.storm.monitor;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import backtype.storm.tuple.Values;
//import storm.trident.operation.*;
//import storm.trident.tuple.TridentTuple;

public class MovingAverageFunction extends BaseFunction {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(MovingAverageFunction.class);
    private EWMA.Time emitRatePer;
    private Map<String, EWMA> ewmaMap = new HashMap<String, EWMA>();
    
    public MovingAverageFunction(EWMA.Time emitRatePer){
        this.emitRatePer = emitRatePer;
    }
    
    public void execute(TridentTuple tuple, TridentCollector collector) {
    	String cmd_key = tuple.getString(1);
 
    	if(!this.ewmaMap.containsKey(cmd_key))
    	{
    		ewmaMap.put(cmd_key, new EWMA().sliding(1.0, EWMA.Time.SECONDS).withAlpha(0.5));
    	}
    	
        this.ewmaMap.get(cmd_key).mark(Long.parseLong(tuple.getString(0)));
        collector.emit(new Values(this.ewmaMap.get(cmd_key).getAverageRatePer(this.emitRatePer)));
    }
}
