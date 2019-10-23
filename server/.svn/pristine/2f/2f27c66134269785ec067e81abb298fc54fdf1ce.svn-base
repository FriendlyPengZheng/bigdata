package com.taomee.statlogger;

public enum OpCode {
	op_begin(0),

	op_sum(1),    // 把某个字段某时间段内所有值相加
	op_max(2),    // 求某字段某时间段内最大值
	op_set(3),    // 直接取某字段最新的数值
	op_ucount(4), // 对某个字段一段时间的值做去重处理

	op_item(5),      // 求某个大类下的各个item求人数人次
	op_item_sum(6),  // 对各个item的产出数量/售价等等求和
	op_item_max(7),  // 求出各个item的产出数量/售价等等的最大值
	op_item_set(8),  // 求出每个item的最新数值

	op_sum_distr(9), // 对每个人的某字段求和，然后求出前面的“和”在各个区间下的人数
	op_max_distr(10), // 对每个人的某字段求最大值，然后求出前面的“最大值”在各个区间下的人数
	op_min_distr(11), // 对每个人的某字段求最小值，然后根据前面的最小值在各个区间下做人数分布
	op_set_distr(12), // 取某个字段的最新值，做分布

	op_ip_distr(13),     // 根据ip字段求地区分布的人数人次

	op_end(14);
	
	private int value;
	
	private OpCode(){
		;
	}
	
	private OpCode(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
}
