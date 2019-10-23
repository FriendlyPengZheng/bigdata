package myTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.mapred.OutputFormat;

//import org.apache.hadoop.mapreduce.OutputFormat;


public class StringBuff {

	public static void main(String[] args) {
		
		byte[] bt = {'a','b','c',' '};
		System.out.println(new String(bt));
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    	long date = 0;
		try {
			date = simpleDateFormat.parse("20190202").getTime()/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(date);

		StringBuffer  stringBuffer = new StringBuffer("Peng");
		String str = "nul;";
		stringBuffer.append(str + "zheng");
		stringBuffer.append(str + "friendly");
		System.out.println(stringBuffer);
		System.out.println(Float.MAX_VALUE==9223372036854775807l);
		System.out.println(Float.MAX_VALUE);
		System.out.println(String.format("MAX\t%s", stringBuffer).hashCode());
		System.out.println( stringBuffer.hashCode());
		try {
			Class<?> aa = Class.forName("java.lang.String");
			Class<?> bb = Class.forName("org.apache.hadoop.mapred.TextOutputFormat");
			System.out.println(aa);
			System.out.println(bb);
			System.out.println(bb.asSubclass(OutputFormat.class));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(String.format("\t2:%.2f", 5d));
		System.out.println(String.format("0\t%s",3));
		for(int i=5;i<4;i++){
			System.out.println(i);
		}
		System.out.println("sss="+"好你好".codePointAt(2));
		System.out.println("你好".getBytes().getClass());
		System.out.println(System.currentTimeMillis());
		System.out.println(new Date().getTime());
		System.out.println("result="+49486/86400);
		
//		System.out.println("dadjadjka".split("#")[1]);
	}

}


        

