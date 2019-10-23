package net.ipip.ipdb;

public class testMain {
	private static byte a = 4;
	private static byte b = 1;
	private static byte c = 2;
	private static byte d = 3;
	
    private static long bytesToLong(byte a, byte b, byte c, byte d) {
    	System.out.println(a & 0xff);
    	System.out.println((a & 0xff) << 24);
    	System.out.println(((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff));
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }
    
	public static void main(String[] args) {
		try {
            District db = new District("C:\\Users\\friendly\\Desktop\\mydata4vipweek2.ipdb");
            System.out.println(db.buildTime());
            System.out.println(db.languages());
            System.out.println(db.fields());
            System.out.println(db.isIPv4());
            System.out.println(db.isIPv6());
            DistrictInfo info = db.findInfo("123.121.1.69", "CN");
            System.out.println(info.getLatitude());
            System.out.println(info.getLongitude());
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
	}

}
