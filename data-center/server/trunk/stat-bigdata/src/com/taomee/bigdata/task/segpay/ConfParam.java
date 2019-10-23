package com.taomee.bigdata.task.segpay;

/**
 * 
 * @author cheney
 *
 */
public interface ConfParam {

	//取指定键的值
	public final static String PARAM_KEY = "param.key";
	
	//扩展键，gzspa + ext
	public final static String EXT_KEY = "ext.key";
	
	//middlegzspmapper key个数
	public final static String KEY_NUM = "key.num";
	
	//计算类型
	public static String PARAM_CALC_TYPE = "calc.type";
	public static String PARAM_CALC_INT_NUM = "calc.int.num";
	
	//交集
	public static String VALUE_CALC_INT = "int";
	//差集
	public static String VALUE_CALC_DIF = "dif";
    //并集
	public static String VALUE_CALC_UNION = "union";
	
}
