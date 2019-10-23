package com.taomee.tms.storm.function;

import java.util.List;
import java.util.Properties;

import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public class LogSplitFunctionCustom2 extends LogSplitFunction2 {
	
	public LogSplitFunctionCustom2(){
		
	}
	
//	public LogSplitFunctionCustom2(Configer config){
//		super(config);
//	}
//	
	public LogSplitFunctionCustom2(Properties properties){
		super(properties);
	}

	protected List<SchemaInfo> getInitialSchemaInfos() {
		return this.logMgrService.getSchemaInfosFromRedis();
	}

}
