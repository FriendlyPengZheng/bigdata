package com.taomee.tms.bigdata.hive.UDTF;

import com.taomee.bigdata.lib.Distr;
import java.util.ArrayList;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class GetDistrNamesUDTF extends GenericUDTF {
	Object[] forwardObj;

	public StructObjectInspector initialize(ObjectInspector[] args)
			throws UDFArgumentLengthException {
		if (args.length != 1) {
			throw new UDFArgumentLengthException("param:distrStr");
		}
		if (args[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
			throw new UDFArgumentLengthException("param:distrStr");
		}

		this.forwardObj = new Object[1];
		ArrayList fieldNames = new ArrayList();
		ArrayList fieldOIs = new ArrayList();
		fieldNames.add("distr");
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		return ObjectInspectorFactory.getStandardStructObjectInspector(
				fieldNames, fieldOIs);
	}

	public void process(Object[] args) throws HiveException {
		String[] distrStrs = args[0].toString().split(",");
		Integer[] distr = new Integer[distrStrs.length];
		for (int i = 0; i <= distrStrs.length - 1; i++) {
			distr[i] = Integer.valueOf(distrStrs[i]);
		}
		for (int i = 0; i <= distrStrs.length; i++) {
			this.forwardObj[0] = Distr.getDistrName(distr, i);
			forward(this.forwardObj);
		}
	}

	public void close() throws HiveException {
	}
}