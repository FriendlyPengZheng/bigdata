package com.taomee.statlogger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class StatInfo {
	
	private LinkedHashMap<String, String> m_info;
	private LinkedHashSet<String>[] m_ops;
	private boolean m_has_op;
	
	public HashMap<String, String> getM_info() {
		return m_info;
	}

//	public ArrayList<String>[] getM_ops() {
//		return m_ops;
//	}
//	
	public LinkedHashSet<String>[] getM_ops() {
		return m_ops;
	}

	public boolean isM_has_op() {
		return m_has_op;
	}
	
	public StatInfo(){
		this.m_info = new LinkedHashMap<String,String>();
		this.m_has_op = false;
		m_ops = new LinkedHashSet[OpCode.op_end.getValue()+1];
		for(int i = OpCode.op_begin.getValue();i != OpCode.op_end.getValue();i++){
			m_ops[i] = new LinkedHashSet<>();
		}
	}
	
	public void add_info(String key,double value){
		
		if(value <= 0)return;
		this.add_info(key, String.valueOf(value));
		
//		key = StatCommon.stat_trim_underscore(key);
//		if(!(is_valid_key(key) && value>0 && m_info.size()<=30)){
//			return;
//		}
//		m_info.put(key, String.valueOf(value));
//		
	}
	
	public void add_info(String key,int value){
		
		if(value <= 0)return;
		this.add_info(key,String.valueOf(value));
		
//		key = StatCommon.stat_trim_underscore(key);
//		if(!(is_valid_key(key) && value>0 && m_info.size()<=30)){
//			return;
//		}
//		m_info.put(key, String.valueOf(value));
//		
	}
	
	public void add_info(String key,String value){
		
		key = StatCommon.stat_trim_underscore(key);
		if(!(is_valid_key(key) && is_valid_value(value) && m_info.size()<30)){
			return;
		}
		m_info.put(key, value);
		
	}

	public void add_op(OpCode op,String key1,String key2){
		
		StatCommon.stat_trim_underscore(key1);
		StatCommon.stat_trim_underscore(key2);
		
		if(!(is_valid_op(op) && m_info.containsKey(key1))){
			return;
		}
		
		switch(op){
		case op_item_sum:
		case op_item_max:
		case op_item_set:
			if(!m_info.containsKey(key2))return;
			key1 = key1+","+key2;
			break;
		default:
			if(key2.length() != 0)return;
			break;
		}
		
		m_ops[op.getValue()].add(key1);
		m_has_op = true;
	}
	
	public void clear(){
		m_info.clear();
		for(int i = 0;i<= m_ops.length-1;i++){
			m_ops[i].clear();
		}
		m_has_op = false;
	}
	
	/*
	 * 返回该StatInfo对象的key-value对以及OpCode信息连接而成的字符串
	 */
	public String serialize() throws IOException{
		String out = "";
		
		Iterator it = m_info.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			out+='\t';
			out+=((String) entry.getKey());
			out+='=';
			out+=((String) entry.getValue());
		}
		
		if(m_has_op){
			
			String[] op = new String[]{
					"", 
					"sum:", "max:", "set:", "ucount:",
					"item:", "item_sum:", "item_max:", "item_set:",
					"sum_distr:", "max_distr:", "min_distr:",
		            "set_distr:",
					"ip_distr:"};
			String vline = "";
			
			out += "\t_op_=";
			for(int i = OpCode.op_begin.getValue();i != OpCode.op_end.getValue();i++){
				if(m_ops[i].size() != 0){
					out += vline;
					out += serilize_op(op[i],m_ops[i]);
					vline = "|";
				}
			}
		}
		return out;
		
	}

	//private
	private String serilize_op(String opCode, LinkedHashSet<String> keys) {
		String vline = "";
		Iterator it = keys.iterator();
		String oss = "";
		while(it.hasNext()){
			oss += (vline+opCode+it.next());
			vline = "|";
		}
		return oss;
	}

	//private
	private static boolean is_valid_op(OpCode op) {
		return op.compareTo(OpCode.op_begin) > 0 && op.compareTo(OpCode.op_end) < 0;
	}

	//private
	private static boolean is_valid_key(String key){
		if(key == null || key.length() ==0){
			return false;
		}
		return StatCommon.size_between(key, 1, 64) 
				&& StatCommon.key_no_invalid_chars(key)
				&& StatCommon.string_firstend_no_invalid_chars(key, "_")
				&& StatCommon.stat_is_utf8(key);
	}

	//private
	public static boolean is_valid_value(String value){
		if(!(StatCommon.size_between(value, 1, 64) 
				&& StatCommon.string_no_invalid_chars(value, "=| \t")
				&& StatCommon.stat_is_utf8(value))){
			return false;
		}
		return true;
	}
}
