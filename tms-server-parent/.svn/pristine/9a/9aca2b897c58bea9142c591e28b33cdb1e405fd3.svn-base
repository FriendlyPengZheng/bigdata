package com.taomee.tms.storm.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.trident.operation.Function;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author looper
 * @date 2017年6月29日 下午4:23:40
 * @project tms-storm TimeConver
 */
public class TimeConvertFunction implements Function {

	private static Logger LOG = LoggerFactory.getLogger(TimeConvertFunction.class);
	
	private Integer timeCoverCode = 0;

	public TimeConvertFunction(Integer timeCoverCode) {
		super();
		this.timeCoverCode = timeCoverCode;
	}
	private Integer partitionIndex ;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		this.partitionIndex = context.getPartitionIndex();
	}

	@Override
	public void cleanup() {
	}

	/**
	 * 格式为dataDay_schemaId_serverId_cascadeValue "2017-01-12 07:44" 变成
	 * "2017-01-12"
	 */
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		//获取上一级stream的元组信息
		String lastStreamField = tuple.getStringByField("dateTime_schemaId_serverId_cascadeValue");
//		LOG.debug("TimeConver processing with field dateTime_schemaId_serverId_cascadeValue:{}",lastStreamField);
		Long valueTmp = -10000l;//表示上一级没有value字段，比如count这种运算,设置该字段初始化值为-10000表示，默认是不会有是负数的数据统计的
		
		try {
			valueTmp = tuple.getLongByField("value");
		} catch (Exception e) {
			//不打印错误堆栈信息，因为把count、sum等一些运算放在一起处理
		}
		String[] data_array = lastStreamField.split("_");
		String dataT,schemaId,serverId,cascadeValue;
		String emitValue = new String();
		StringBuffer buffer = new StringBuffer();
		
		// 转换时间，时间去前面加d，同时数据分割以这种特殊字符"_|"分割，避免后面级联字段里面有非法字符，导致数据分割有误
		if (data_array.length == 3) {
			dataT = getConvertedTime(data_array[0], timeCoverCode);
			schemaId = data_array[1];
			serverId = data_array[2];
			cascadeValue = new String();
			emitValue = buffer.append(dataT).append("_|").append(schemaId).append("_|").append(serverId).append("_|").append(cascadeValue).toString();
		}
		else if(data_array.length == 4)
		{
			dataT = getConvertedTime(data_array[0], timeCoverCode);
			schemaId = data_array[1];
			serverId = data_array[2];
			cascadeValue = data_array[3];
			emitValue = buffer.append(dataT).append("_|").append(schemaId).append("_|").append(serverId).append("_|").append(cascadeValue).toString();
		}else if (data_array.length >= 5){
			dataT = getConvertedTime(data_array[0], timeCoverCode);
			schemaId = data_array[1];
			serverId = data_array[2];
			cascadeValue = data_array[3];
			for(int i = 4;i<=data_array.length-1;i++){
				cascadeValue += "_"+data_array[i];
			}
			if(lastStreamField.endsWith("_")){
				cascadeValue += "_";
			}
			emitValue = buffer.append(dataT).append("_|").append(schemaId).append("_|").append(serverId).append("_|").append(cascadeValue).toString();
		}
		
		// 将数据发送到下一级流级别
		List<Object> values = new ArrayList<>();
		values.add(emitValue);
		/**
		 * 如果在上一级的流中如果存在"value" field，表示数据需要发送到下一级流中
		 */
		if(!valueTmp.equals(-10000l))
		{
			values.add(valueTmp);
		}
		collector.emit(values);
//		LOG.debug("TimeConver emit field dateDay_schemaId_serverId_cascadeValue with values:{} after processing with field dateTime_schemaId_serverId_cascadeValue:{}",values, lastStreamField);
		LOG.debug("partition {} emitting [{},{}]",this.partitionIndex,lastStreamField,values);
	}

	/**
	 * 根据传进来的日期字符串以及格式码进行日期格式转换 
	 * 例：输入"2017-01-12 07:44"
	 * 1----》2017-01-12 07 
	 * 2----》2017-01-12 
	 * 3----》2017-01 
	 * 4----》2017
	 * @param dataTime 输入的时间字符串，格式要求为"yyyy-MM-dd HH:mm:ss"
	 * @param timeConverCode 格式码(1/2/3/4)
	 */
	private String getConvertedTime(String dataTime, Integer timeConverCode) {
		String date = new String();
		switch (timeConverCode) {
		case 1:
			date = dataTime.substring(0, dataTime.length() - 3);
			break;
		case 2:
			date = dataTime.substring(0, dataTime.length() - 6);
			break;
		case 3:
			date = dataTime.substring(0, dataTime.length() - 9);
			break;
		case 4:
			date = dataTime.substring(0, dataTime.length() - 12);
			break;
		}
		return date;
	}

}
