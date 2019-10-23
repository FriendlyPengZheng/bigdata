package com.taomee.tms.storm.aggregator;

import org.apache.storm.trident.operation.CombinerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Sum implements CombinerAggregator<Long> {
	private static final long serialVersionUID = 4422276397111974338L;
	private static final Logger LOG = LoggerFactory.getLogger(Sum.class);

	@Override
    public Long init(TridentTuple tuple) {
        return tuple.getLong(0);
    }

    @Override
    public Long combine(Long val1, Long val2) {
        return val1 + val2;
    }

    @Override
    public Long zero() {
        return 0L;
    }
    
}
