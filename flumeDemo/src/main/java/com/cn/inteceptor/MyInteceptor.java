package com.cn.inteceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

public class MyInteceptor implements Interceptor{

	public void initialize() {
		System.out.println("initialized............");
	}

	public Event intercept(Event event) {
		Map<String,String> headers = event.getHeaders();
		String body = new String(event.getBody());
		if(body.contains("basic")){
			headers.put("type", "basic");
		}
		if(body.contains("custom")){
			headers.put("type", "custom");
		}
		event.setHeaders(headers);
		return event;
	}

	public List<Event> intercept(List<Event> events) {
		List<Event> list = new ArrayList<Event>();
		for(Event event:events){
			list.add(intercept(event));
		}
		return list;
	}

	public void close() {
		System.out.println("close..........");
	}
	
	public static class Builder implements Interceptor.Builder{

		public void configure(Context context) {
			
		}

		public Interceptor build() {
			return new MyInteceptor();
		}
		
	}

}
