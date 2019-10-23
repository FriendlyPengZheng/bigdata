package com.taomee.tms.storm.aggregator;

import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Max implements ReducerAggregator<Long> {
	private static final long serialVersionUID = 7132606788446332838L;
	private static final Logger LOG = LoggerFactory.getLogger(Max.class);

	@Override
	public Long init() {
		return 0L;
	}

	@Override
	public Long reduce(Long curr, TridentTuple tuple) {
//		LOG.debug("Max reduce cur is " + curr + ", tuple is " + tuple.getLong(0));
		Long tmp = tuple.getLong(0);
		return tmp > curr ? tmp : curr;
	}
}