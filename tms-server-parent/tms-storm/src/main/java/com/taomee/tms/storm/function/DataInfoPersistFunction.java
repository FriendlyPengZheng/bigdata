package com.taomee.tms.storm.function;

//import storm.trident.operation.BaseFunction;
//import storm.trident.operation.TridentCollector;
//import storm.trident.operation.TridentOperationContext;
//import storm.trident.tuple.TridentTuple;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;

//import com.alibaba.dubbo.rpc.config.ApplicationConfig;
//import com.alibaba.dubbo.rpc.config.RegistryConfig;
//import com.alibaba.dubbo.rpc.config.ConsumerConfig;
//import com.alibaba.dubbo.rpc.config.ReferenceConfig;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.api.LogMgrService;

public class DataInfoPersistFunction extends BaseFunction {
	private static final long serialVersionUID = 1369944251676927977L;
	private static final Logger LOG = LoggerFactory.getLogger(DataInfoPersistFunction.class);
	
	protected TridentTuple tuple = null;
	protected String dataInfoKey;
	protected Set<String> dataInfoKeySet = new HashSet<String>();
	ClassPathXmlApplicationContext appContext = null;
	protected LogMgrService logMgrService = null;
	
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context) {
    	// 通过spring的配置文件方式启动
//    	ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
//				new String[] { "applicationContext.xml" });
//    	appContext.start();
//    	logMgrService = (LogMgrService) appContext.getBean("LogMgrService"); // 获取bean
    	
    	// 通过dubbo的api启动
    	// 当前应用配置
    	ApplicationConfig application = new ApplicationConfig();
    	application.setName("tms-storm-datainfo-persist-function");
    	 
    	// 连接注册中心配置
    	RegistryConfig registry = new RegistryConfig();
//    	registry.setAddress("10.20.130.230:9090");
//    	registry.setUsername("aaa");
//    	registry.setPassword("bbb");
    	registry.setProtocol("zookeeper");
    	registry.setAddress("10.1.1.35:2181");
    	 
    	// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
    	 
    	// 引用远程服务
    	ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
    	reference.setApplication(application);
    	reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
    	reference.setInterface(LogMgrService.class);
//    	reference.setVersion("1.0.0");
    	 
    	// 和本地bean一样使用xxxService
    	logMgrService = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
    }
    
    @Override
    public void cleanup() {
    	// 通过spring的配置文件方式启动
//    	if (appContext != null) {
//    		appContext.close();
//    		appContext = null;
//    	}
    	
    	// TODO 通过dubbo的api启动
    }

    protected boolean GetDataInfoKey() {
    	dataInfoKey = tuple.getString(0);
    	if (dataInfoKey == null) {
			LOG.error("DataInfoPersistFunction GetDataInfoKey, null dataInfoKey in tuple");
			return false;
		}
    	return true;
    }
    
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		LOG.info("DataIdPersistFunction execute called");
		
		String strDataIdKey = tuple.getString(0);
    	if (strDataIdKey == null) {
			LOG.error("DataInfoPersistFunction execute, null strDataIdKey in tuple");
			return;
		}
    	
    	if (!dataInfoKeySet.contains(strDataIdKey)) {
    		String[] splices = strDataIdKey.split("\\s+", -1);
    		if (splices.length != 2) {
    			LOG.error("DataInfoPersistFunction execute, invalid strDataIdKey [" + strDataIdKey + "]");
    			return;
    		}
    		
    		int schemaId = 0;
    		try {
    			schemaId = Integer.parseInt(splices[0]);
    		} catch (NumberFormatException ex) {
    			LOG.error("DataInfoPersistFunction execute, invalid schemaId in strDataIdKey [" + strDataIdKey + "]");
    			return;
    		} catch (Exception ex) {
    			LOG.error("DataInfoPersistFunction execute, unknown exception[" + ex.getMessage() + "] catched, perhaps strDataIdKey [" + strDataIdKey + "] invalid");
    			return;
    		}
    		
    		if (schemaId <= 0) {
    			LOG.error("DataInfoPersistFunction execute, schemaId [" + schemaId + "] < 0");
    			return;
    		}
    		
    		String cascadeValue = splices[1].trim();	// 允许为空字符串
//    		if (cascadeValue.length() == 0) {
//    			LOG.error("DataInfoPersistFunction execute, empty cascadeValue");
//    			return;
//    		}
    		
    		// TODO 捕获异常
    		LOG.info("DataInfoPersistFunction execute, setDataInfoByschemaId with schemaId [" + schemaId + "], cascadeValue [" + cascadeValue + "]");
    		Integer ret = logMgrService.insertUpdateDataInfoByschemaId(schemaId, cascadeValue);
    		if (ret == null || ret.intValue() <= 0) {
    			LOG.error("DataInfoPersistFunction execute, logMgrService setDataInfoByschemaId failed");
    			return;
    		}
    		
    		dataInfoKeySet.add(strDataIdKey);
    	}
		
//		List<Object> values = new ArrayList<Object>();
//		collector.emit(values);
	}

	public static void main(String[] args) {
		
	}
}

























