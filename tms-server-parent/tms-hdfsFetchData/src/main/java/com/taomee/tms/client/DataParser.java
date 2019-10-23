package com.taomee.tms.client;

import com.taomee.tms.lib.Operator;

/**
 * 解析器
 * 将每一行结果根据操作类型构建成传输协议格式
 * @author cheney
 * @date 2013-11-14
 */
public class DataParser {

	public static TranProtocol parser(String line, Streamer streamer) {
		
		if(streamer.getTaskid() != -1){
			TranProtocol0x1004 pt = new TranProtocol0x1004();
			pt.setVersion(1);
			pt.setSeq_no(2);
			pt.setReturn_value(0);
			
			pt.setData_type(streamer.getData_type());
			pt.setTime(streamer.getTime());
			pt.setTask_id(streamer.getTaskid());
			
			String[] fields = line.split("\t");
			
			pt.setGame_id(Integer.parseInt(fields[0]));
	        pt.setZone_id(Integer.parseInt(fields[1]));
	        pt.setServer_id(Integer.parseInt(fields[2]));
	        pt.setPlatform_id(Integer.parseInt(fields[3]));
			if(fields.length == 5){
        		pt.setValue(Double.parseDouble(fields[4]));
        	} else if(fields.length == 6){
        		pt.setRange(fields[4]);
        		pt.setValue(Double.parseDouble(fields[5]));
        	}
			
			return pt;
			
		} else {
			TranProtocol0x1002_0x1003 pt = new TranProtocol0x1002_0x1003();
            if(streamer.getData_type() == 0) {
                pt.setHour(streamer.getHour());
            }
			pt.setVersion(1);
			pt.setSeq_no(2);
			pt.setReturn_value(0);
			
			pt.setData_type(streamer.getData_type());
			pt.setTime(streamer.getTime());
			
			String[] fields = line.split("\t");
			int op = Operator.getOperatorCode(fields[0]);
			
			pt.setOp_type((byte)op);
			pt.setGame_id(Integer.parseInt(fields[1]));
	        pt.setZone_id(Integer.parseInt(fields[2]));
	        pt.setServer_id(Integer.parseInt(fields[3]));
	        pt.setPlatform_id(Integer.parseInt(fields[4]));
	        pt.setStid(fields[5]);
	        pt.setSstid(fields[6]);
	        
			switch(op) {
				case Operator.UCOUNT:
		        case Operator.COUNT:
		        case Operator.MAX:
		        case Operator.SUM:
		        case Operator.SET:
                case Operator.DISTR_SUM:
                case Operator.DISTR_MAX:
                case Operator.DISTR_SET:
		        	if(fields.length == 8){
		        		pt.setValue(Double.parseDouble(fields[7]));
		        	} else if(fields.length == 9){
		        		pt.setOp_field(fields[7]);
		        		pt.setValue(Double.parseDouble(fields[8]));
		        	} else if(fields.length == 10){
		        		pt.setOp_field(fields[7]);
		        		pt.setKey(fields[8]);
		        		pt.setValue(Double.parseDouble(fields[9]));
		        	}
		        	break;
		    }
			return pt;		
		}
	}
	
}
