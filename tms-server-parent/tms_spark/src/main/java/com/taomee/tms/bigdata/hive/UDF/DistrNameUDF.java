package com.taomee.tms.bigdata.hive.UDF;

import org.apache.hadoop.hive.ql.exec.UDF;

import com.taomee.bigdata.lib.Distr;

public class DistrNameUDF extends UDF{
	private static String distrStr = "";
	private static Integer[] distr;
	
	private void setDistr(String range){
		if(range == null){
			throw new IllegalArgumentException("range could not be null");
		}
		if(range == ""){
			distr=null;
		}else{
			String[] distrStrs = range.split(",");
			distr = new Integer[distrStrs.length];
			for(int i = 0;i<distrStrs.length;i++){
				distr[i] = Integer.valueOf(distrStrs[i]);
			}
		}
	}
	
	public String evaluate(String range,int value){
		if(range == null){
			throw new IllegalArgumentException("distr range could not be null!");
		}
		if(range.equals(""))return String.valueOf(value);
		if(!range.equals(distrStr)){
			distrStr=range;
			setDistr(range);
		}
		return Distr.getDistrName(distr, Distr.getRangeIndex(distr,value));
	}
	
	public String evaluate(String range,long value){
		if(range == null){
			throw new IllegalArgumentException("distr range could not be null!");
		}
		if(range.equals(""))return String.valueOf(value);
		if(!range.equals(distrStr)){
			distrStr=range;
			setDistr(range);
		}
		return Distr.getDistrName(distr, Distr.getRangeIndex(distr,value));
	}
	
	public String evaluate(String range,double value){
		if(range == null){
			throw new IllegalArgumentException("distr range could not be null!");
		}
		if(range.equals(""))return String.valueOf(value);
		if(!range.equals(distrStr)){
			distrStr=range;
			setDistr(range);
		}
		return Distr.getDistrName(distr, Distr.getRangeIndex(distr,value));
	}
	
	public String evaluate(String range,int value,int ratio){
		if(range == null){
			throw new IllegalArgumentException("distr range could not be null!");
		}
		if(range.equals(""))return String.valueOf(value/ratio);
		if(!range.equals(distrStr)){
			distrStr=range;
			setDistr(range);
		}
		return Distr.getDistrName(distr, Distr.getRangeIndex(distr,value),ratio); 
	}
	
	public String evaluate(String range,long value,int ratio){
		if(range == null){
			throw new IllegalArgumentException("distr range could not be null!");
		}
		if(range.equals(""))return String.valueOf(value/ratio);
		if(!range.equals(distrStr)){
			distrStr=range;
			setDistr(range);
		}
		return Distr.getDistrName(distr, Distr.getRangeIndex(distr,value),ratio);
	}
	
	public String evaluate(String range,double value,int ratio){
		if(range == null){
			throw new IllegalArgumentException("distr range could not be null!");
		}
		if(range.equals(""))return String.valueOf(value/ratio);
		if(!range.equals(distrStr)){
			distrStr=range;
			setDistr(range);
		}
		return Distr.getDistrName(distr, Distr.getRangeIndex(distr,value),ratio);
	}
	
	
	
	public static void main(String[] args){
		System.out.println(new DistrNameUDF().evaluate("100,500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000",1000.0,100));
		System.out.println(new DistrNameUDF().evaluate("1,5,10,11,15,20,30,40,50,60,70,80,90,100,101,120,121,150,200,300,500,1000",10.0));
//		System.out.println(new DistrNameUDF().evaluate("1,10,30,60,80",90));
//		System.out.println(new DistrNameUDF().evaluate("",2));
//		System.out.println(new DistrNameUDF().evaluate("",80));
//		System.out.println(new DistrNameUDF().evaluate("1,20,80",50));
//		System.out.println(new DistrNameUDF().evaluate("1,30,60",20));
//		System.out.println(new DistrNameUDF().evaluate("1,30,60",20,10));
//		System.out.println(new DistrNameUDF().evaluate("1,30,60",20));
//		System.out.println(new DistrNameUDF().evaluate("100,300,600",20,100));
//		System.out.println(new DistrNameUDF().evaluate("100,300,600",200,100));
	}
}
