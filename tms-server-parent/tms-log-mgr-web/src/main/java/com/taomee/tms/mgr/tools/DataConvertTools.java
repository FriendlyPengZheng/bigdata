package com.taomee.tms.mgr.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.entity.Component;

public class DataConvertTools extends ComponentTools{
	private ModuleBuilder builder;
	
	public Component toData(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(DATA_TYPE);
		putComponent(res, jsonObject);
		
		JSONObject parameters = convert(jsonObject);
		res.setProperties(utf8ToUnicode(parameters.toString()));
		
		return res;
	}

	private JSONObject convert(JSONObject src) {
		// TODO Auto-generated method stub
		JSONObject parameters = new JSONObject();
		initParameters(parameters);
		//putParamters(parameters, src);
		
		//buildUrlMatch(src);
		//buildUrlExtend(src);
		convertArray("urlExtend", src);
		convertArray("urlMatch", src);
		putParamters(parameters, src);
		//buildUrl(parameters, src);
		//buildChild(parameters, src);
		
		return parameters;
	}

	private void buildChild(JSONObject src) {
		// TODO Auto-generated method stub
		JSONArray argument;
		if(src.containsKey("argument")){
			argument = src.getJSONArray("argument");
		} else {
			argument = new JSONArray();
		}
		
		JSONArray children = new JSONArray();
		if(src.getInteger("show_graph") != 0){
			/*if(src.containsKey("chartConfig")){
				if(src.getInteger("chartConfig") != 0){
					//TODO国际化翻译
				}
			}*/
			JSONObject child = new JSONObject();
			child.put("type", "graph");
			child.put("chartStock", src.getBoolean("chartStock"));
			child.put("page", src.getBoolean("chartPage"));
			child.put("timeDimension", src.get("timeDimension"));
			child.put("columnStack", src.get("columnStack"));
			child.put("lineAreaColumn", src.getBoolean("lineAreaColumn"));
			child.put("lineColumn", src.getBoolean("lineColumn"));
			if(argument.contains("average")){
				child.put("average", true);
			} else {
				child.put("average", false);
			}
			if(src.containsKey("isSetYAxisMin")){
				child.put("isSetYAxisMin", src.getBoolean("isSetYAxisMin"));
			} else {
				child.put("isSetYAxisMin", false);
			}
			child.put("keyUnit", src.get("keyUnit"));//国际化
			child.put("chartConfig", src.get("chartConfig"));
			
			children.add(child);
		}
		
		src.remove("chartStock");
		src.remove("chartPage");
		src.remove("timeDimension");
		src.remove("columnStack");
		src.remove("lineAreaColumn");
		src.remove("lineColumn");
		src.remove("average");
		src.remove("isSetYAxisMin");
		src.remove("keyUnit");
		src.remove("chartConfig");
		src.remove("show_graph");
		
		if(src.getInteger("show_table") != 0){
			JSONObject child = new JSONObject();
			if(src.getString("hugeTable").isEmpty() || src.getInteger("hugeTable") == 0){
				child.put("type", "table");
			} else {
				child.put("type", "hugeTable");
			}
			if(argument.contains("qoq")){
				child.put("qoq", true);
			} else {
				child.put("qoq", false);
			}
			if(argument.contains("yoy")){
				child.put("yoy", true);
			} else {
				child.put("yoy", false);
			}
			if(argument.contains("average")){
				child.put("average", true);
			} else {
				child.put("average", false);
			}
			if(argument.contains("sum")){
				child.put("sum", true);
			} else {
				child.put("sum", false);
			}
			if(argument.contains("percentage")){
				child.put("percentage", true);
			} else {
				child.put("percentage", false);
			}
			child.put("checkbox", src.getBoolean("checkbox"));
			child.put("hide", src.getBoolean("hide"));
			child.put("prepareData", src.get("prepareData"));
			child.put("theadAvg", src.getBoolean("theadAvg"));
			child.put("minHeight", src.get("minHeight"));
			
			JSONArray theadType;
			JSONArray theads = new JSONArray();
			if(!src.getJSONArray("thead_type").isEmpty()){
				theadType = src.getJSONArray("thead_type");
				for(int i = 0; i < theadType.size(); i++){
					JSONObject thead = new JSONObject();
					thead.put("type", theadType.get(i));
					thead.put("title", (src.getJSONArray("thead_title")).get(i));
					theads.add(thead);
				}	
			}
			
			JSONArray theadsConfig = (JSONArray) theads.clone();
			if(src.getString("theadFn").isEmpty()){
				child.put("thead", theads);
			} else {
				child.put("thead", src.get("theadFn"));
			}
			child.put("theadConfig", theadsConfig);
			children.add(child);
		}
		
		src.remove("argument");
		src.remove("checkbox");
		src.remove("hide");
		src.remove("prepareData");
		src.remove("theadAvg");
		src.remove("minHeight");
		src.remove("thead_type");
		src.remove("thead_title");
		src.remove("theadFn");
		src.remove("show_table");
		src.remove("hugeTable");
		if(children.size() != 0){
			src.put("child", children);
		}
	}

	private void buildUrl(JSONObject src) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(src.getInteger("isSelControl") != 0) {
			src.put("isSelControl", true);
			JSONArray urlMatchs = src.getJSONArray("urlMatch");
			JSONArray urlExtends = src.getJSONArray("urlExtend");
			for(int i = 0; i < urlMatchs.size(); i++){
				JSONObject urlMatch = urlMatchs.getJSONObject(i);
				urlExtends.add(urlMatch);
			}
			src.put("urlExtend", urlExtends);
		}else {
			src.put("isSelControl", false);
		}
		src.remove("urlMatch");
		
		JSONObject url = new JSONObject();
		JSONArray extend = new JSONArray();
		
		JSONArray urlExtends = src.getJSONArray("urlExtend");
		System.out.println("******urlExtends" + urlExtends.toString());
		//String urlTimeDimension = src.getString("urlTimeDimension");
		extend.add("");
		for(int i = 0; i < urlExtends.size(); i++){	
			Integer period = i;
			switch(i){
			case 0:
				period = 1;
				break;
			case 1:
				period = 2;
				break;
			case 2:
				period = 3;
				break;
			case 3:
				period = 4;
				break;
			case 4:
				period = 5;
				break;
			case 5:
				period = 6;
				break;
			default:
			}
			
			JSONObject urlExtend = urlExtends.getJSONObject(i);
			if(urlExtend.getString("data").isEmpty()){
				extend.add(urlExtend.getString("common"));
				continue;
			}

			JSONObject basicData = builder.endBasicData();
			JSONArray processList = basicData.getJSONArray("process_list");
			String processType = basicData.getString("process_type");
			//System.out.println("processType:" + processType);
			//System.out.println("processList:" + processList.toString());
			//System.out.println("------processList:" + processList.toString());
			JSONArray dataInfo = basicData.getJSONArray("data_info");
			//System.out.print("dataInfo:" +dataInfo.toString()+"\n");
			
			String common = urlExtend.getString("common");
			if(common.isEmpty() || urlExtend.getString("data").isEmpty()) continue;
			//System.out.println("-------------data:" + urlExtend.getString("data"));
			String number = urlExtend.getString("data").replaceAll("\\D", "_").replace("_+", "_");
			//System.out.print("------------------number:"+urlExtend.getString("data")+"\n");
			String[] datas = number.split("_+");
			//System.out.print("------------------datas:"+datas.length+"\n");
			
			if(processType.equals("expres")) {
				extend.add(buildExpreUrl(processList, dataInfo, extend,  common, datas, period));
			} else if(processType.equals("distr")){
				System.out.println("---------distr");
				extend.add(buildDistrUrl(processList, dataInfo, extend,  common, datas, period));
			}
		}
		
		url.put("extend", extend);
		url.put("page", src.getString("urlPage"));
		url.put("timeDimension", src.getString("urlTimeDimension"));
		src.put("url", url);
		System.out.print("url:" +src.getString("url")+ "\n");
		src.remove("urlExtend");
		src.remove("urlPage");
		src.remove("urlTimeDimension");			
	}


	private void initParameters(JSONObject parameters) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		parameters.put("urlExtend",jsonArray);
		parameters.put("urlPage","");
		parameters.put("urlTimeDimension",1);
		parameters.put("isTimeDimensionInherit",1);
		parameters.put("show_table",1);
		parameters.put("argument",jsonArray);
		parameters.put("checkbox",0);
		parameters.put("hide",1);
		parameters.put("prepareData","");
		parameters.put("thead_type",jsonArray);
		parameters.put("thead_title",jsonArray);
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
	}

	public void build(JSONObject obj, ModuleBuilder moduleBuilder) {
		// TODO Auto-generated method stub
		setBuilder(moduleBuilder);
		obj.put("isTimeDimensionInherit", obj.getBoolean("isTimeDimensionInherit"));
		buildChild(obj);
		buildUrl(obj);
		
	}

	public ModuleBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(ModuleBuilder builder) {
		this.builder = builder;
	}
	
	public String convertExpre(String expreString, String dataString, Map<String,String> dataMap) {
		String result = new String();
		String number = dataString.replaceAll("\\D", "_").replace("_+", "_");
		String[] datas = number.split("_+");
		//System.out.println("conver::expreString:"+expreString);
		
		/*System.out.println("expreString:"+expreString);
		System.out.println("dataString:"+dataString);
		System.out.println("dataMap:" + dataMap.toString());*/
		boolean flag = false;
		char c;
		String num = new String();
		for(int p = 0; p < expreString.length(); p++) {
			c = expreString.charAt(p);
			//System.out.println("p:"+p+" c:"+c);
			if(c == '{'){
				flag = true;
				result += String.valueOf(c);
			} else if(c == '}'){
				flag = false;
				if(num.isEmpty()) {
					return null;
				}
				if(Integer.parseInt(num) < datas.length){
					result += dataMap.get(datas[Integer.parseInt(num)]);
					num = "";
				} else {
					return null;
				}
				result += String.valueOf(c);
				num = "";
			} else {
				if(flag == true) {
					num += String.valueOf(c);
				} else {
					result += String.valueOf(c);
				}
			}
		}
		
		return result;
	}
	
	public String arrayToJson(JSONArray dataArray, String name) {
		String result = new String();
		for(int j = 0; j < dataArray.size(); j++){
			JSONObject tmp = dataArray.getJSONObject(j);
			//System.out.println("======tmp:"+tmp.toString());
			Set<String> keys = tmp.keySet();
			for(String key : keys){
				String tmp1 = "[" + j + "][" + key +"]";
				String tmp2 = tmp.getString(key);
				try {
					tmp1 = URLEncoder.encode(tmp1,"utf-8"); 
					tmp2 = URLEncoder.encode(tmp2,"utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//urlString = urlString + "&data_info[" + j + "][" + key +"]=" + tmp.getString(key);
				result = result + "&" + name + tmp1 +"="+ tmp2;
			}
		}
		return result;
	}
	
	public String buildExpreUrl(JSONArray processList, JSONArray dataInfo,JSONArray extend, String common, String[] datas,Integer period){
		JSONArray dataArray = new JSONArray();
		Set<Integer> dataSet = new HashSet<Integer>();
		Map<String,String> dataMap = new HashMap<String, String>();
		for(String data : datas){
			JSONObject process = processList.getJSONObject(Integer.parseInt(data));
			String dataString = process.getString("data");
			//System.out.print("------------------process:"+process.toString()+"\n");
			String[] dataList = dataString.split(",");
			//System.out.print("------------------dataString:"+dataString.toString()+"\n");
			for(String str : dataList){
				//System.out.print("------------------str:"+str+"\n");
				Integer j = Integer.parseInt(str);
				if(j < dataInfo.size()){
					if(!dataSet.contains(j)){
						dataArray.add(dataInfo.get(j));
						dataSet.add(j);
						dataMap.put(j.toString(), String.valueOf(dataSet.size() - 1));
					}
					//System.out.println("j:"+j.toString()+" val:"+dataArray.size());
				}
			}
			//System.out.print("dataArray:" +dataArray.toString()+"\n");
		}
		//String urlString = "../../common/data/getTimeSeries?&qoq=1&yoy=1&average=0&sum=0&";
		String urlString = common;
		urlString = urlString + arrayToJson(dataArray,"data_info");
		
		int k = 0;
		for(String data : datas){
			JSONObject process = processList.getJSONObject(Integer.parseInt(data));
			String expreString = process.getString("expre");
			if (expreString != null){
				expreString = convertExpre(process.getString("expre"), process.getString("data"), dataMap);
			}
			String unitString = process.getString("unit");
			String precisionString = process.getString("precision");
			String dataNameString = process.getString("data_name");
			System.out.print("------------expreString:" +expreString+"\n");
			/*System.out.print("------------unitString:" +unitString+"\n");
			System.out.print("------------precisionString:" +precisionString+"\n");
			System.out.print("------------urlTimeDimension:" +urlTimeDimension+"\n");
			System.out.print("------------dataNameString:" +dataNameString+"\n");*/
			try {
				if(dataNameString == null || dataNameString.isEmpty()){
					urlString = urlString + "&expres["+k+"][data_name]=";
				} else {
					urlString = urlString + "&expres["+k+"][data_name]=" + URLEncoder.encode(dataNameString,"utf-8");
				}
				
				if(unitString == null || unitString.isEmpty()){
					urlString = urlString + "&expres["+k+"][unit]=";
				} else {
					urlString = urlString + "&expres["+k+"][unit]=" + URLEncoder.encode(unitString,"utf-8");
				}
				
				if(precisionString == null || precisionString.isEmpty()){
					urlString = urlString + "&expres["+k+"][precision]=";
				} else {
					urlString = urlString + "&expres["+k+"][precision]=" + URLEncoder.encode(precisionString,"utf-8");
				}
				
				urlString = urlString + "&expres["+k+"][period]=" + URLEncoder.encode(period.toString(),"utf-8");
				
				if(expreString == null || expreString.isEmpty()){
					urlString = urlString + "&expres["+k+"][expre]=";
				} else {
					urlString = urlString + "&expres["+k+"][expre]=" + URLEncoder.encode(expreString,"utf-8");
				}
				//urlString = urlString + "&expres["+i+"][unit]=" + URLEncoder.encode(unitString,"utf-8");
				//urlString = urlString + "&expres["+i+"][precision]=" + URLEncoder.encode(precisionString,"utf-8");
				//urlString = urlString + "&expres["+i+"][period]=" + URLEncoder.encode(periodString,"utf-8");
				//urlString = urlString + "&expres["+i+"][expre]=" + URLEncoder.encode(expreString,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("----------------urlString:"+urlString);
			//extend.add(urlString);
			k++;
		}
		//System.out.println("----------------urlString:"+urlString);
		return urlString;
	}
	
	public String buildDistrUrl(JSONArray processList, JSONArray dataInfo,JSONArray extend, String common, String[] datas,Integer period){
		if(datas.length != 1) {
			return "";
		}
		JSONObject process = processList.getJSONObject(Integer.parseInt(datas[0]));
		String dataString = process.getString("data");
		String[] dataList = dataString.split(",");
		if(dataList.length != 1) {
			return "";
		}
		
		Integer n = Integer.parseInt(dataList[0]);
		JSONArray dataArray = new JSONArray();
		if(n < dataInfo.size()){
			dataArray.add(dataInfo.get(n));
		}
		
		String urlString = common;
		urlString = urlString + arrayToJson(dataArray,"data_info");
		
		JSONArray distrArray = new JSONArray();
		distrArray.add(process);
		urlString = urlString + arrayToJson(distrArray,"data_info");
		
		return urlString;
	}
}
