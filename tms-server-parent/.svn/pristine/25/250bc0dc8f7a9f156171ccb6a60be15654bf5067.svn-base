package com.taomee.tms.storm.monitor;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import storm.trident.state.OpaqueValue;
//import storm.trident.state.map.IBackingMap;

import org.apache.storm.trident.state.OpaqueValue;
import org.apache.storm.trident.state.map.IBackingMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountLoginLogoutMap<T> implements IBackingMap<OpaqueValue<T>> {
	private static final Logger LOG = LoggerFactory.getLogger(AccountLoginLogoutMap.class);
	Map<String, OpaqueValue<T>> storage = new HashMap<String, OpaqueValue<T>>();

	public List<OpaqueValue<T>> multiGet(List<List<Object>> keys) {
		List<OpaqueValue<T>> values = new ArrayList<OpaqueValue<T>>();
		for(List<Object> key : keys)
		{
			OpaqueValue<T> value = storage.get(key.get(0));
            if(value != null)
				values.add(value);		
		}
		
		return values;
	}

	public void multiPut(List<List<Object>> keys, List<OpaqueValue<T>> vals) {
		for(int i = 0; i < keys.size(); ++i)
		{
			LOG.info("putting <" + keys.get(i).get(0) + ", " + vals.get(i) + ">");
			storage.put((String) keys.get(i).get(0), vals.get(i));
		}
	}
}
