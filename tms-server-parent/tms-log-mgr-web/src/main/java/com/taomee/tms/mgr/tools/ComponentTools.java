package com.taomee.tms.mgr.tools;

//import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.entity.Component;

public class ComponentTools {
	public static String DATA_TYPE = "data";
	public static String TABS_TYPE = "tabs";
	public static String TAB_TYPE = "tab";
	public static String LISTTABLE_TYPE = "listtable";
	public static String WRAP_TYPE = "wrap";
	
	protected void putParamters(JSONObject parameters, JSONObject src){
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			System.out.print("parameters key :"+key+"\n");
			System.out.print("parameters value :"+src.get(key)+"\n");
			if(src.containsKey(key)){
				parameters.put(key, src.get(key));
			}
		}
	}
	
	protected void putComponent(Component info,JSONObject src){
		info.setParentId(src.getInteger("parent_id"));
		info.setModuleKey(src.getString("module_key"));
		if(src.containsKey("display_order")) {
			info.setDisplayOrder(src.getInteger("display_order"));
		}else {
			info.setDisplayOrder(1);
		}
		if(src.containsKey("hidden")) {
			info.setHidden(src.getInteger("hidden"));
		}else {
			info.setHidden(0);
		}
		if(src.containsKey("ignoreId")) {
			info.setIgnoreId(src.getInteger("ignoreId"));
		}else {
			info.setIgnoreId(0);
		}
		if(src.containsKey("title")) {
			info.setComponentTitle(src.getString("title"));
		}else {
			info.setComponentTitle("");
		}
		//if(src.containsKey("title")) {
		//	src.put("title",getUnicode(src.getString("title")));
		//}
		if(src.containsKey("component_desc")) {
			info.setComponentDesc(src.getString("component_desc"));
		}else {
			info.setComponentDesc("");
		}
	}
	
	public String getUnicode(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
                 
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	protected void buildAttr(JSONObject src){
		if(src.containsKey("attr_key")){
			JSONArray attrKeys = src.getJSONArray("attr_key");
			JSONArray attrValue = src.getJSONArray("attr_value");
			JSONObject attr = new JSONObject();
			for(int i = 0; i < attrKeys.size(); i++) {
				attr.put(attrKeys.getString(i), attrValue.get(i));
			}
			src.put("attr", attr);
			src.remove("attr_key");
			src.remove("attr_value");
		}
	}
	
	public String utf8ToUnicode(String inStr) {
		if(inStr == null || inStr.length() == 0){
			return inStr;
		}
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString().replace("\\uffff", "\\u");
    }
	
	public String GBK2Unicode(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char chr1 = (char) str.charAt(i);

            if (!isNeedConvert(chr1)) {
                result.append(chr1);
                continue;
            }

            result.append("\\u" + Integer.toHexString((int) chr1));
        }

        return result.toString();
    }

	private boolean isNeedConvert(char chr1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void convertArray(String key, JSONObject src) {
		// TODO Auto-generated method stub
		JSONArray arr = new JSONArray();
		for(int i = 0;;i++){
			String keys = key + "[" + i +"]";
			//System.out.print("keys :"+keys+"\n");
			if(src.containsKey(keys)){
				arr.add(src.getJSONObject(keys));
				src.remove(keys);
			} else {
				break;
			}
		}
		src.put(key, arr);
	}
	
	protected void convertArrayfromStr(String key, JSONObject src) {
		// TODO Auto-generated method stub
		JSONArray arr = new JSONArray();
		for(int i = 0;;i++){
			String keys = key + "[" + i +"]";
			System.out.print("keys :"+keys+"\n");
			if(src.containsKey(keys)){
				arr.add(i, src.getString(keys));
				System.out.print("values :"+arr.getString(i)+"\n");
				src.remove(keys);
			} else {
				break;
			}
		}
		src.put(key, arr);
	}
	
	//void build(JSONObject obj) {	
	//}
	
	/*public Component toData(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(DATA_TYPE);
		res.setParentId(jsonObject.getInteger("parent_id"));
		res.setModuleKey(jsonObject.getString("module_key"));
		res.setDisplayOrder(1);
		res.setHidden(0);
		res.setIgnoreId(0);
		res.setComponentDesc(" ");
		res.setComponentTitle(" ");
		
		JSONObject parameters = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONArray urlExtends = new JSONArray();
		
		//set ExtendUrl
		urlExtends.add(jsonObject.get("urlExtend[0]"));
		urlExtends.add(jsonObject.get("urlExtend[1]"));
		urlExtends.add(jsonObject.get("urlExtend[2]"));
		urlExtends.add(jsonObject.get("urlExtend[3]"));
		urlExtends.add(jsonObject.get("urlExtend[4]"));
		urlExtends.add(jsonObject.get("urlExtend[5]"));
		parameters.put("urlExtend", urlExtends);
		//System.out.println("urlExtends:"+urlExtends+"\n");
		
		//parameters.put("urlExtend",jsonArray);
		parameters.put("urlPage","");
		parameters.put("urlTimeDimension",1);
		parameters.put("isTimeDimensionInherit",1);
		parameters.put("show_table",1);
		parameters.put("argument",jsonArray);
		parameters.put("checkbox",0);
		parameters.put("hide",1);
		parameters.put("hugeTable",1);
		parameters.put("theadFn","");
		parameters.put("minHeight",null);
		parameters.put("theadAvg",0);
		parameters.put("show_graph",1);
		parameters.put("chartStock",0);
		parameters.put("chartPage",0);
		parameters.put("timeDimension","day");
		parameters.put("columnStack","");
		parameters.put("lineAreaColumn",0);
		parameters.put("lineColumn",0);
		parameters.put("isSetYAxisMin",0);
		parameters.put("keyUnit","");
		parameters.put("chartConfig",jsonArray);
		parameters.put("isSelControl",0);
		parameters.put("urlMatch",jsonArray);
		

		//parameters.put("urlPage", jsonObject.getString("urlPage"));
		//parameters.put("urlTimeDimension",jsonObject.getInteger("urlTimeDimension"));
		//parameters.put("isTimeDimensionInherit",jsonObject.getInteger("isTimeDimensionInherit"));
		//parameters.put("show_table",jsonObject.getInteger("show_table"));
		//parameters.put("argument",jsonObject.getJSONArray("argument"));	
		//parameters.put("checkbox",0);
		
		//System.out.println("parameters, set before:"+parameters+"\n");
		putParamters(parameters, jsonObject);
		System.out.println("parameters, set after:"+parameters+"\n");
		res.setProperties(parameters.toString());
		
		return res; 
	}*/
	
	/*public Component toTabs(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(TABS_TYPE);
		res.setParentId(jsonObject.getInteger("parent_id"));
		res.setModuleKey(jsonObject.getString("module_key"));
		res.setDisplayOrder(1);
		res.setHidden(0);
		res.setIgnoreId(0);
		res.setComponentDesc(" ");
		res.setComponentTitle(" ");
		
		JSONObject parameters = new JSONObject();
		parameters.put("tabsSkin","");
		putParamters(parameters, jsonObject);
		res.setProperties(parameters.toString());
		return res;
	}*/
	
	/*public Component toTab(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(TAB_TYPE);
		res.setParentId(jsonObject.getInteger("parent_id"));
		res.setModuleKey(jsonObject.getString("module_key"));
		
		JSONObject parameters = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		parameters.put("title","");
		parameters.put("tabsSkin","");
		parameters.put("ignore",0);
		parameters.put("ignoreId","");
		parameters.put("attr_key",jsonArray);
		parameters.put("attr_value",jsonArray);
		
		putParamters(parameters, jsonObject);
		res.setProperties(parameters.toString());
		return res;
	}*/
	
	/*public Component toListtable(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(LISTTABLE_TYPE);
		return res;
	}*/
	
	/*public Component toWrap(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(WRAP_TYPE);
		putComponent(res, jsonObject);
		
		JSONObject parameters = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//parameters.put("",);
		
		return res;
	}*/
}
