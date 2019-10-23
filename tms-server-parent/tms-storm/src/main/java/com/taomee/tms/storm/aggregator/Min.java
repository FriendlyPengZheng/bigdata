package com.taomee.tms.storm.aggregator;

import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//public class Min implements ReducerAggregator<Number> {
//	private static final long serialVersionUID = 7132606788446332838L;
//	private static final Logger LOG = LoggerFactory.getLogger(Min.class);
//
//	@Override
//	public Number init() {
//		return 0;
//	}
//
//	@Override
//	public Number reduce(Number curr, TridentTuple tuple) {
//		LOG.info("Min reduce cur is " + curr.intValue() + ", tuple is " + ((Number)tuple.getValue(0)).intValue());
//		Number tmp = (Number) tuple.getValue(0);
//		return tmp.intValue() > curr.intValue() ? curr : tmp;
//	}
//}

public class Min implements ReducerAggregator<Long> {
	private static final long serialVersionUID = 7132606788446332838L;
	private static final Logger LOG = LoggerFactory.getLogger(Min.class);

	@Override
	public Long init() {
		return Long.MAX_VALUE;
	}

	@Override
	public Long reduce(Long curr, TridentTuple tuple) {
//		LOG.debug("Min reduce cur is " + curr + ", tuple is " + tuple.getLong(0));
		Long tmp = tuple.getLong(0);
		return tmp > curr ? curr : tmp;
	}
}