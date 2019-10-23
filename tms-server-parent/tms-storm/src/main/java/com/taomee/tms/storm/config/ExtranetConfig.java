//package com.taomee.tms.storm.config;
//
//import java.io.Serializable;
//
////外网配置
//
//public class ExtranetConfig implements Configer,Serializable {
//	
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -3657651932034774012L;
//
//	//创建单例class
//	private static ExtranetConfig instance = new ExtranetConfig();
//	
//	private static String zkHosts = "10.25.14.147:2181,10.25.26.107:2181,10.25.7.83:2181/kafka";
//	private static String redisHosts = "10.25.144.75:7000,10.25.144.75:7001,10.25.144.75:7002";
//	private static String dubboServiceName = "tms-storm-realtime-stat";
//	private static String dubboRegistryProtocol = "zookeeper";
//	private static String dubboRegistryAdress = "10.25.14.147:2181";
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
//	private ExtranetConfig(){}
//	
//	public static ExtranetConfig getInstance(){
//		return instance;
//	}
//
//	@Override
//	public String getZooKeeperHosts() {
//		// TODO Auto-generated method stub
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
