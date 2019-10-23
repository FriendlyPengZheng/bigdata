package com.taomee.tms.bigdata.hive.UDF;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.taomee.bigdata.lib.IPDistr;

public class ProvinceNameUDF extends UDF{
	
	private static IPDistr ipDistr = null;
	
	static
	{
		Properties prop = new Properties();
		InputStream fis = null;
    	fis = ProvinceNameUDF.class.getClassLoader().getResourceAsStream("test.properties");
    	
    	if(fis != null){
			try {
				prop.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    	try {
			ipDistr = new IPDistr(prop.getProperty("ip.distr.dburi"));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String evaluate(long ip){
		return this.evaluate(ip,true);
	}
	
	public String evaluate(long ip,boolean needToConvert){
		return ipDistr.getIPProvinceName(ip, needToConvert); 
	}
	
	public static void main(String args[]){
		ProvinceNameUDF provinceNameUDF = new ProvinceNameUDF();
		System.out.println(provinceNameUDF.evaluate(1985595616));
	}
}
