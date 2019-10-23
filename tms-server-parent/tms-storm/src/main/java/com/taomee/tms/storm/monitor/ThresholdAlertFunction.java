package com.taomee.tms.storm.monitor;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import backtype.storm.tuple.Values;
//import storm.trident.operation.*;
//import storm.trident.tuple.TridentTuple;

public class ThresholdAlertFunction extends BaseFunction {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(ThresholdAlertFunction.class);
    private static enum State {
        BELOW, 
        ABOVE;
    }
    private State last = State.BELOW;
    private double threshold;
    
    // TODO: 不同的监控项，不同的阀值。
    public ThresholdAlertFunction(double threshold){
        this.threshold = threshold;
    }
    
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        double val = tuple.getDouble(1);
        LOG.info(tuple.getString(0) + ": " + "avg: " + val + " threshold: " + this.threshold);
        State newState = val < this.threshold ? State.BELOW : State.ABOVE;
        boolean stateChange = this.last != newState;
        collector.emit(new Values(stateChange, threshold));
        this.last = newState;
        // TODO: send alert
        LOG.debug("State change? --> {}", stateChange);
    }
}