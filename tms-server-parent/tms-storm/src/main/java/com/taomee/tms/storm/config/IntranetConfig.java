//package com.taomee.tms.storm.config;
//
//import java.io.Serializable;
//
////内网配置
//public class IntranetConfig implements Configer,Serializable{
//	
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 66436717020301488L;
//
//	//创建单例class
//	private static IntranetConfig instance = new IntranetConfig();
//	
//	private static String zkHosts = "10.1.1.187:2181,10.1.1.176:2181,10.1.1.175:2181";
//	private static String redisHosts = "10.1.1.35:6379,10.1.1.153:6379,10.1.1.151:6379";
//	private static String dubboServiceName = "tms-storm-realtime-stat";
//	private static String dubboRegistryProtocol = "zookeeper";
//	private static String dubboRegistryAdress = "10.1.1.35:2181";
//	
//	private static String commonTopicName = "tms-basic-online-test";
//	private static String kafaCommonGroupName = "tms";
//	private static String commonStreamName = "tms-realtime-stat-stream";
//	private static String commonToPoName = "tms-realtime-stat-topo";
//	private static String commonstateDataName = "tms-realtime-stat-state-data";
//	
//	private static String customizeTopicName = "tms-custom-online-test";
//	private static String kafaCustomizeGroupName = "tms-custom-consumer";
//	private static String customizeStreamName = "tms-realtime-stat-custom-stream";
//	private static String customizeToPoName = "tms-realtime-stat-custom-stream";
//	private static String customizeStateDataName = "tms-realtime-stat-custom-stream";
//	
//	
//	private IntranetConfig(){}
//	
//	public static IntranetConfig getInstance(){
//		return instance;
//	}
//
//	@Override
//	public String getZooKeeperHosts() {
//		return zkHosts;
//	}
//
//	@Override
//	public String getRedisHosts() {
//		// TODO Auto-generated method stub
//		return redisHosts;
//	}
//	
//	@Override
//	public String getDubboServiceName() {
//		// TODO Auto-generated method stub
//		return dubboServiceName;
//	}
//
//	@Override
//	public String getDubboRegistryProtocol() {
//		// TODO Auto-generated method stub
//		return dubboRegistryProtocol;
//	}
//
//	@Override
//	public String getDubboRegistryAdress() {
//		// TODO Auto-generated method stub
//		return dubboRegistryAdress;
//	}
//
//	@Override
//	public String getCommonTopicName() {
//		// TODO Auto-generated method stub
//		return commonTopicName;
//	}
//
//	@Override
//	public String getKafaCommonGroupName() {
//		// TODO Auto-generated method stub
//		return kafaCommonGroupName;
//	}
//
//	@Override
//	public String getCommonStreamName() {
//		// TODO Auto-generated method stub
//		return commonStreamName;
//	}
//
//	@Override
//	public String getCommonToPoName() {
//		// TODO Auto-generated method stub
//		return commonToPoName;
//	}
//
//	@Override
//	public String getCommonStateDataName() {
//		// TODO Auto-generated method stub
//		return commonstateDataName;
//	}
//
//	@Override
//	public String getCustomizeTopicName() {
//		// TODO Auto-generated method stub
//		return customizeTopicName;
//	}
//
//	@Override
//	public String getKafaCustomizeGroupName() {
//		// TODO Auto-generated method stub
//		return kafaCustomizeGroupName;
//	}
//
//	@Override
//	public String getCustomizeStreamName() {
//		// TODO Auto-generated method stub
//		return customizeStreamName;
//	}
//
//	@Override
//	public String getCustomizeToPoName() {
//		// TODO Auto-generated method stub
//		return customizeToPoName;
//	}
//
//	@Override
//	public String getCustomizeStateDataName() {
//		// TODO Auto-generated method stub
//		return customizeStateDataName;
//	}
//	
//}
