package com.taomee.tms.storm.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.trident.operation.Function;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.DataToObject;

/**
 * 
 * @author looper
 * @date 2017年6月30日 下午4:00:05
 * @project tms-storm ReadRedisDayData2HBaseFunction
 *          将redis实时更新的每天的数据往Hbase表里面插入。 步骤: 1、解析redis上面的数据，然后调用把对应的key获取到，
 *          2、然后解析list<txid,curvalue,lastvalue>,将中间的value值解析出来。
 *          3、封装成对象调用dubbo的接口往HBase里面插入。
 * 
 */
public class ReadRedisDayData2HBaseFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger LOG = LoggerFactory.getLogger(ReadRedisDayData2HBaseFunction.class);
	private LogMgrService logMgrService;
	private int partitionIndex;
	private Properties properties;
	
	//SimpleDateFormat非线程安全,偶尔会报java.lang.NumberFormatException: multiple points
	//private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 本来是没使用之前准备使用第三方的joda时间库的，但是最后发现是storm已经集成了这个第三方库，API也是一样的。
	 */
	//private DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	public ReadRedisDayData2HBaseFunction(){
		if(this.properties == null){
			this.properties = new TmsProperties(System.getProperty("user.home")+"/storm/tms-storm.properties");
		}
	}
	
	public ReadRedisDayData2HBaseFunction(Properties properties){
		this.properties = properties;
	}

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		this.partitionIndex = context.getPartitionIndex();
		LOG.info("partition {} ReadRedisDayData2HBaseFunction prepare() method starting...",this.partitionIndex);
		
		//初始化dubbo
		ApplicationConfig application = new ApplicationConfig();
		application.setName(this.getClass().toString().split(" ")[1]);
		// 连接注册中心配置
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol(properties.getProperty("dubboRegistryProtocol"));
		registry.setAddress(properties.getProperty("dubboRegistryAdress"));
		//registry.setAddress("10.1.1.187:2181");

		// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
		// 引用远程服务
		ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		reference.setApplication(application);
		reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
		reference.setInterface(LogMgrService.class);

		// 和本地bean一样使用xxxService
		logMgrService = reference.get();
		// logMgrService.insertUpdateDataResultInfo();

		if (logMgrService != null) {
			LOG.info("partition {} ReadRedisDayData2HBaseFunction prepare() method end",this.partitionIndex);
		}

	}

	@Override
	public void cleanup() {
	}

	/**
	 * 数据格式tuple d2017-06-30_92_10_ 18932320,64,59
	 */
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String key = tuple.getString(0);
		//Long value = tuple.getLongByField("count");
		Long value = tuple.getLong(1);//使用下面的方式，使最后的数据统一
//		LOG.debug("partition {} dealing with <{},{}>",this.partitionIndex,key,value);
		if (key != null && value != null) {
			String[] data_array = key.split("\\_\\|");
			DataToObject dataToObject = new DataToObject();
			if (data_array.length == 3) {
				try {
					dataToObject.setDate((int) (format.parse(data_array[0]).getTime()/1000));
					dataToObject.setSchemaId(Integer.valueOf(data_array[1]));
					dataToObject.setServId(Integer.valueOf(data_array[2]));
					dataToObject.setCascadeField(new String());
					dataToObject.setValue(Double.valueOf(value.toString()));
					logMgrService.insertUpdateDataResultInfo(dataToObject);
					LOG.debug("partition {} inserted into redis:{}",this.partitionIndex,dataToObject.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (data_array.length == 4) {
				try {
//					dataToObject.setDate((int)(format.parseMillis(data_array[0])/1000));
					dataToObject.setDate((int) (format.parse(data_array[0]).getTime()/1000));
					dataToObject.setSchemaId(Integer.valueOf(data_array[1]));
					dataToObject.setServId(Integer.valueOf(data_array[2]));
					dataToObject.setCascadeField(data_array[3]);
					dataToObject.setValue(Double.valueOf(value.toString()));
					logMgrService.insertUpdateDataResultInfo(dataToObject);
					LOG.debug("partition {} inserted into redis:{}",this.partitionIndex,dataToObject.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (data_array.length >=5 ){
				try {
					dataToObject.setDate((int) (format.parse(data_array[0]).getTime()/1000));
					dataToObject.setSchemaId(Integer.valueOf(data_array[1]));
					dataToObject.setServId(Integer.valueOf(data_array[2]));
					String cascadeValue = data_array[3];
					for(int i=4;i<=data_array.length-1;i++){
						cascadeValue += "_|"+data_array[i];
					}
					if(key.endsWith("_|")){
						cascadeValue += "_|";
					}
					dataToObject.setCascadeField(cascadeValue);
					dataToObject.setValue(Double.valueOf(value.toString()));
					logMgrService.insertUpdateDataResultInfo(dataToObject);
					LOG.debug("partition {} inserted into redis:{}",this.partitionIndex,dataToObject.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				LOG.debug("key array length is {} for key {}",data_array.length,key);
			}
		}
	}
}
