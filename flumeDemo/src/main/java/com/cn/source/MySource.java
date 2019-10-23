package com.cn.source;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

public class MySource extends AbstractSource implements PollableSource, Configurable{

	private String prefix;
	private String subfix;

	public void configure(Context context) {
		prefix = context.getString("prefix");
		subfix = context.getString("subfix","friendly");
	}

	public Status process() throws EventDeliveryException {
		
		
		Event event = new SimpleEvent();
		event.setBody(body);
		event.setHeaders(headers);
		
		getChannelProcessor().processEvent(event);
		return null;
	}

	public long getBackOffSleepIncrement() {
		return 0;
	}

	public long getMaxBackOffSleepInterval() {
		return 0;
	}
	
	public void start() {
		
	}

	public void stop() {
		
	}

}
