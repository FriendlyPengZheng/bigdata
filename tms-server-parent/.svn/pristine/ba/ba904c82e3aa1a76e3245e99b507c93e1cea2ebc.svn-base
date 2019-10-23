package com.taomee.tms.mgr.main;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestMain {
	public static void main(String[] args) throws IOException {
		/*
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(  
//                new String[] { "classpath*:main/resources/spring-registry.xml" });
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(  
                new String[] { "spring-registry.xml" });
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(  
//                new String[] { "file:/home/thierry/svn/tms/dubbo-springmvc-mybatis/ivan-dubbo-server/spring-registey.xml" });
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(  
//                new String[] { "file:F:\\dubbo-springmvc-mybatis\\ivan-dubbo-server\\spring-registey.xml" });
        context.start();
        System.out.println("click any button to stop");
        System.in.read(); // 按任意键退出
        System.out.println("service stopped1");
        */
		
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
//				new String[] { "..\\log-mgr-provider.xml"});
//				new String[] { "..\\main\\resources\\META-INF\\spring\\log-mgr-provider.xml"});
//				new String[] { "../main/resources/META-INF/spring/log-mgr-provider.xml"});
//				new String[] { "..\\main\\resources\\META-INF\\spring\\log-mgr-provider.xml"});
		//		new String[] { "main\\resources\\META-INF\\spring\\log-mgr-provider.xml","main/resources/META-INF/spring/spring-mybatis.xml"});
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
				new String[] { "src/main/resources/META-INF/spring/log-mgr-provider.xml",
							   "src/main/resources/META-INF/spring/spring-mybatis.xml"});
//				new String[] { "src/main/log-mgr-provider.xml"});
        context.start();  
  
        System.in.read(); // 按任意键退出  
	}
}
