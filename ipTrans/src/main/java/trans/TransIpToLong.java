package trans;

public class TransIpToLong {
	
	public  String longToIp(Long longIp){
		StringBuffer sb = new StringBuffer();
		// 直接右移24位
		sb.append(String.valueOf(longIp >>> 24));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>>16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	public long iPToLong(String ip){
		String[] strs = ip.split("[.]");
		return 	 (Long.parseLong(strs[0]) << 24)
				+ (Long.parseLong(strs[1]) << 16)
				+ (Long.parseLong(strs[2]) << 8)
				+ (Long.parseLong(strs[3])) ;
	}
	public static void main(String[] args) {
		System.out.println(Long.valueOf(0x000000F0));
		System.out.println(Long.valueOf(0x00FFFFFF));
		System.out.println(new TransIpToLong().longToIp(22949671L));
		System.out.println(new TransIpToLong().iPToLong("255.255.255.0"));
		System.out.println(new TransIpToLong().iPToLong("255.255.255.255"));
	}

}
