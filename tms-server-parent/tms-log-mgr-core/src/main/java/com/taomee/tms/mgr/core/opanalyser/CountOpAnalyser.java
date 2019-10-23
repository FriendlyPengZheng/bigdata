package com.taomee.tms.mgr.core.opanalyser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CountOpAnalyser extends BaseOpAnalyser implements Serializable {
	private static final long serialVersionUID = -6191880507130663746L;
	public static List<String> fakeOpValues = new ArrayList<String>();

	@Override
	public boolean IsRealtime() {
		return true;
	}

	@Override
	public boolean IsNonRealtime() {
		return true;
	}
	
	@Override
	public String GetOp() {
		return "count";
	}

	// 默认处理
	public boolean Init(List<String> keys) {
		if (keys.size() == 0) {
			return true;
		}
		return false;
	}

	// 返回空的List，但不返回null（返回null表示获取失败）
	public List<String> GetOpValues(Map<String, String> attrMap) {
		// 出于性能考虑，返回一个static的数组
		// 但不能返回null，这样和其他op统一，在后续的拼key的过程中可以进行统一的一致性判断
		return fakeOpValues;
	}

}
