package log4j;

import org.apache.log4j.Logger;

public class Test {

	static Logger logger = Logger.getLogger(Test.class);
	public static void main(String[] args) {

		String a = "李磊";
		String b = a.intern();
		System.out.println(b.hashCode());
		System.out.println(a.toCharArray()[0]*31+a.toCharArray()[1]);
		System.out.println(a.hashCode());
		long count = 0;
		Test t1 = new Test();
		Test t2 = t1;
		System.out.println(t1.toString()+"+++++"+t2.toString());
		for(int i = 0;i<=30;i++){
			count += Math.pow(2, i);
		}
		System.out.println(count);
		System.out.println((long)Math.pow(2, 31)-1);
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		System.out.println("in");
		logger.debug("I am debug");
		logger.info("I am info");
		logger.warn("I am warn");
		logger.error("I am error");
		logger.fatal("I am fatal");
		System.out.println("out");
	}

}
