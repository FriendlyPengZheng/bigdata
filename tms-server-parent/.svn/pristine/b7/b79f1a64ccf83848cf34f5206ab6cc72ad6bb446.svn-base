package com.taomee.tms.storm.function;

import java.util.List;
import java.util.Properties;

import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public class LogSplitFunctionBasic2 extends LogSplitFunction2 {
	
	public LogSplitFunctionBasic2(){
		
	}
	
//	public LogSplitFunctionBasic2(Configer config){
//		super(config);
//	}
//	
	public LogSplitFunctionBasic2(Properties properties){
		super(properties);
	}
	
	protected List<SchemaInfo> getInitialSchemaInfos() {
		return this.logMgrService.getSchemaInfosByLogType(0);
	}

}
