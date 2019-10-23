package trans;

import java.util.HashMap;
import java.util.Map;

public class IpCombine {
	public static void main(String[] args) {
		Long a = 10000000000l;
		long b = a + 1l;
		System.out.println(b);
		System.out.println(a);
		String s1 = "打打卡机的垃圾";
		String s2 = "打打卡机的垃圾";
		System.out.println(s1.hashCode());
		System.out.println(s2.hashCode());
		Map<String, String> map = new HashMap<String, String>();
		map.clear();
		System.out.println(map.isEmpty());
		System.out.println(map == null);
 	}
}
