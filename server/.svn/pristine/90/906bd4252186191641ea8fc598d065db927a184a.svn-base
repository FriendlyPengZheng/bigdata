package com.taomee.bigdata.client;

import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;

/**
 * 传输协议
 * c在intel的cpu中为低尾字节序
 * 而java为高尾字节序
 * 所以传输前需要对>1字节的字段做转换处理
 * @author cheney
 * @date 2013-11-11
 */
public abstract class TranProtocol {

	//head
	protected short pkg_len;			//包长，包括包头的18字节
	protected int cmd_id=0x1003;		//命令号，默认为更新数据
	protected int version;				//版本号，从0开始
	protected int seq_no;				//消息序列号。服务端原样返回，供客户端匹配返回包用
	protected int return_value;			//返回值。0为成功，其余为错误码
	
	//body
	protected byte data_type;			//数据类型：0-分钟，1-小时，2-天
	protected int time;					//时间戳
	protected double value;				//8字节的浮点数
	protected int platform_id;			//平台id
	protected int zone_id;				//区id
	protected int server_id;			//服id
	protected int game_id;				//游戏id
	
	protected ByteBuffer byteBuffer;
	
	public abstract byte[] pack();

	public static byte[] toLH(short n) {
		byte[] b = new byte[2];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		return b;
	}
	
	public byte[] toLH(int n) {
	    byte[] b = new byte[4];
	    b[0] = (byte) (n & 0xff);
	    b[1] = (byte) (n >> 8 & 0xff);
	    b[2] = (byte) (n >> 16 & 0xff);
	    b[3] = (byte) (n >> 24 & 0xff);
	    return b;
	}
	
	public byte[] toLH(double d) {
		byte[] b = new byte[8];
		long l = Double.doubleToLongBits(d);
		b[0] = (byte) (l & 0xff);
	    b[1] = (byte) (l >> 8 & 0xff);
	    b[2] = (byte) (l >> 16 & 0xff);
	    b[3] = (byte) (l >> 24 & 0xff);
	    b[4] = (byte) (l >> 32 & 0xff);
	    b[5] = (byte) (l >> 40 & 0xff);
	    b[6] = (byte) (l >> 48 & 0xff);
	    b[7] = (byte) (l >> 56 & 0xff);
		return b;
	}
	
	public byte[] toLH(String s) {
        try {
            return s == null ? "".getBytes("UTF-8") : s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) { }
        return new byte[0];
	}
	
	public short getPkg_len() {
		return pkg_len;
	}

	public void setPkg_len(short pkg_len) {
		this.pkg_len = pkg_len;
	}

	public int getCmd_id() {
		return cmd_id;
	}

	public void setCmd_id(int cmd_id) {
		this.cmd_id = cmd_id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getSeq_no() {
		return seq_no;
	}

	public void setSeq_no(int seq_no) {
		this.seq_no = seq_no;
	}

	public int getReturn_value() {
		return return_value;
	}

	public void setReturn_value(int return_value) {
		this.return_value = return_value;
	}

	public byte getData_type() {
		return data_type;
	}

	public void setData_type(byte data_type) {
		this.data_type = data_type;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getPlatform_id() {
		return platform_id;
	}

	public void setPlatform_id(int platform_id) {
		this.platform_id = platform_id;
	}

	public int getZone_id() {
		return zone_id;
	}

	public void setZone_id(int zone_id) {
		this.zone_id = zone_id;
	}

	public int getServer_id() {
		return server_id;
	}

	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}

	public int getGame_id() {
		return game_id;
	}

	public void setGame_id(int game_id) {
		this.game_id = game_id;
	}
	
}
