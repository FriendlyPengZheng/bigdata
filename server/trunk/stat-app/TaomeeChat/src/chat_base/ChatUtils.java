package chat_base;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

import android.util.Log;

public abstract class ChatUtils {
	public static final int POROTO_PULLMSG = 0xB010;
	public static final int POROTO_REGISTER = 0xB011;
	
    public abstract byte[] pack();

	public static final byte[] packHeader(int len, int proto_id) {
		ByteBuffer headerBuffer = ByteBuffer.allocate(8);
        headerBuffer.clear();
        headerBuffer.put(toLH(len));
        headerBuffer.put(toLH(proto_id));
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
	
	public static int[] toHH(byte[] b) {
		if (b.length >= 4) {
			int intArray[] = new int[b.length/4];
			for (int j=0; j<b.length; j+=4) {
				long ret = 0;
				for(int i=j+3; i>=j && i<b.length; --i) {
					ret <<= 8;
					ret += b[i] & 0xff;
				}
				intArray[j/4] = (int)ret;
			}
			return intArray;
		}
		return null;
	}
	
	public static String timestmp2String(String time) {
		Long timestamp = Long.parseLong(time);
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").getInstance().format(new java.util.Date(timestamp));
		return date;
	}
}
