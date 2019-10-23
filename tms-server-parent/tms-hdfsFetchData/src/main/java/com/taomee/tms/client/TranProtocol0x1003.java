package com.taomee.tms.client;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 更新离线数据
 * @author cheney
 * @date 2013-11-11
 */
public class TranProtocol0x1003 extends TranProtocol {

	//head
	protected int cmd_id=0x1003;		//命令号，更新离线数据
	
	//body
	private byte stid_len;			//stid长度
	private String stid="";			//stid，不包括最后的’\0’
	private byte sstid_len;			//sstid长度
	private String sstid="";		//sstid，不包括最后的’\0’
	private byte op_type;			//op类型
	private byte field_len;			//op_field长度，没有op_field则field_len为0
	private String op_field="";		//op操作的字段，不包括最后的’\0’
	private byte key_len;			//key长度，没有key则key_len为0
	private String key="";			//op_field字段中，key的具体值，不包括最后的’\0’
	
	public byte[] pack(){
		try {
			this.stid_len = (byte)stid.getBytes("UTF-8").length;
			this.sstid_len = (byte)sstid.getBytes("UTF-8").length;
			this.field_len = (byte)op_field.getBytes("UTF-8").length;
			this.key_len = (byte)key.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
            return null;
		}
		
        try {
            int size = 52 + this.stid_len + this.sstid_len + this.field_len + this.key_len;
            this.pkg_len = (short)size; 

            byteBuffer = ByteBuffer.allocate(size);

            byteBuffer.put(toLH(pkg_len));
            byteBuffer.put(toLH(cmd_id));
            byteBuffer.put(toLH(version));
            byteBuffer.put(toLH(seq_no));
            byteBuffer.put(toLH(return_value));

            //head
            byteBuffer.put(data_type);
            byteBuffer.put(toLH(time));
            byteBuffer.put(toLH(value));
            byteBuffer.put(toLH(platform_id));
            byteBuffer.put(toLH(zone_id));
            byteBuffer.put(toLH(server_id));
            byteBuffer.put(toLH(game_id));

            //private
            byteBuffer.put(stid_len);
            byteBuffer.put(toLH(stid));
            byteBuffer.put(sstid_len);
            byteBuffer.put(toLH(sstid));
            byteBuffer.put(op_type);
            byteBuffer.put(field_len);
            byteBuffer.put(toLH(op_field));
            byteBuffer.put(key_len);
            byteBuffer.put(toLH(key));
        } catch (java.nio.BufferOverflowException e) {
            return null;
        }
		
		return byteBuffer.array();
	}
		
	public String getStid() {
		return stid;
	}

	public void setStid(String stid) {
		this.stid = stid;
	}

	public String getSstid() {
		return sstid;
	}

	public void setSstid(String sstid) {
		this.sstid = sstid;
	}

	public byte getOp_type() {
		return op_type;
	}

	public void setOp_type(byte op_type) {
		this.op_type = op_type;
	}

	public String getOp_field() {
		return op_field;
	}

	public void setOp_field(String op_field) {
		this.op_field = op_field;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
