package com.taomee.bigdata.monitor.util;

import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import com.taomee.bigdata.upload.Uploader;

/**
 * 传输协议
 * c在intel的cpu中为低尾字节序
 * 而java为高尾字节序
 * 所以传输前需要对>1字节的字段做转换处理
 * @author ping
 * @date 2014-03-20
 */
public abstract class Protocol {
    protected static final byte MODULE_NAMENODE  = 5;
    protected static final byte MODULE_JOBTRACK  = 6;
    protected static final byte MODULE_DATANODE  = 7;
    protected static final byte MODULE_TASKTRACK = 8;

    protected static final int  CMD_REGISTER   = 0xA001;
    protected static final int  CMD_UNREGISTER = 0xA002;
    protected static final int  CMD_NAMENODE   = 0xA005;
    protected static final int  CMD_JOBTRACK   = 0xA006;
    protected static final int  CMD_DATENODE   = 0xA007;
    protected static final int  CMD_TASKTRACK  = 0xA008;
	//head
	protected int  len;
	protected int  proto_id;
	protected byte module;
	private   String  ip = Uploader.getIP();
	
	protected ByteBuffer byteBuffer;
    private   ByteBuffer headerBuffer = ByteBuffer.allocate(13);

    public final void clear() {
        if(byteBuffer != null) {
            byteBuffer.clear();
        }
        headerBuffer.clear();
    }

    public abstract byte[] pack();

    public int getHeaderLength() {
        return 13;
    }
	
	public final byte[] packHeader() {
        headerBuffer.clear();
        headerBuffer.put(toLH(len));
        headerBuffer.put(toLH(proto_id));
        headerBuffer.put(toLH(module));
        headerBuffer.put(ipToLH(ip));
        return headerBuffer.array();
    }

    public static final byte[] ipToLH(String ip) {
        byte[] b = new byte[4];
        String[] s = ip.split("\\.");
        for(int i=0; i<b.length; i++) {
            short t = Short.valueOf(s[i]);
            b[i] = (byte) (t & 0xff);
        }
        return b;
    }

    public static final byte[] toLH(byte b) {
        return new byte[] { b };
    }

	public static final byte[] toLH(short n) {
        //System.out.println("pack short " + n);
		byte[] b = new byte[2];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		return b;
	}
	
	public static final byte[] toLH(int n) {
        //System.out.println("pack int " + n);
	    byte[] b = new byte[4];
	    b[0] = (byte) (n & 0xff);
	    b[1] = (byte) (n >> 8 & 0xff);
	    b[2] = (byte) (n >> 16 & 0xff);
	    b[3] = (byte) (n >> 24 & 0xff);
	    return b;
	}

    public static final byte[] toLH(long l) {
        //System.out.println("pack long " + l);
	    byte[] b = new byte[8];
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
	
	public static final byte[] toLH(float f) {
        //System.out.println("pack float " + f);
		byte[] b = new byte[4];
		int i = Float.floatToIntBits(f);
		b[0] = (byte) (i & 0xff);
	    b[1] = (byte) (i >> 8 & 0xff);
	    b[2] = (byte) (i >> 16 & 0xff);
	    b[3] = (byte) (i >> 24 & 0xff);
		return b;
	}
	
	public static final byte[] toLH(double d) {
        //System.out.println("pack double " + d);
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
	
	public static final byte[] toLH(String s) {
        try {
            return s == null ? new byte[0] : s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) { }
        return new byte[0];
	}
	
}
