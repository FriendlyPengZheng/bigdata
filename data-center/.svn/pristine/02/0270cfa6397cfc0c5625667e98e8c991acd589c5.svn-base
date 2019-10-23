package com.taomee.bigdata.client;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 更新加工数据
 * @author cheney
 * @date 2013-11-11
 */
public class TranProtocol0x1004 extends TranProtocol {

	//head
	protected int cmd_id=0x1004;		//命令号，更新加工数据
	
	//body	
	private int task_id;			//加工项id
	private byte range_len;			//range长度，没有range则range_len为0
	private String range="";		//range字段，不包括最后的’\0’
	
	public byte[] pack(){
		try {
			this.range_len = (byte)range.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
            return null;
		}
		
		int size = 52 + this.range_len;
		this.pkg_len = (short)size; 
		
		byteBuffer = ByteBuffer.allocate(size);
		
		//head
		byteBuffer.put(toLH(pkg_len));
		byteBuffer.put(toLH(cmd_id));
		byteBuffer.put(toLH(version));
		byteBuffer.put(toLH(seq_no));
		byteBuffer.put(toLH(return_value));
		
		//common
		byteBuffer.put(data_type);
		byteBuffer.put(toLH(time));
		byteBuffer.put(toLH(value));
		byteBuffer.put(toLH(platform_id));
		byteBuffer.put(toLH(zone_id));
		byteBuffer.put(toLH(server_id));
		byteBuffer.put(toLH(game_id));
		
		//private
		byteBuffer.put(toLH(task_id));
		byteBuffer.put(range_len);
		byteBuffer.put(toLH(range));
		
		return byteBuffer.array();
	}

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public byte getRange_len() {
		return range_len;
	}

	public void setRange_len(byte range_len) {
		this.range_len = range_len;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}
	
}
