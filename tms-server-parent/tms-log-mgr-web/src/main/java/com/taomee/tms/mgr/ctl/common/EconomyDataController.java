package com.taomee.tms.mgr.ctl.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.CategoryInfo;
import com.taomee.tms.mgr.entity.EconomyInfo;
import com.taomee.tms.mgr.entity.ItemCategoryInfo;
import com.taomee.tms.mgr.entity.ItemInfo;
import com.taomee.tms.mgr.tools.DateTools;
import com.taomee.tms.mgr.tools.Message;

@Controller
@Scope("session")
@RequestMapping(value="/common/economy")
public class EconomyDataController {
	
	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(DataController.class);
	
	Integer platformId = 0;
	Integer zoneId = 0;
	Integer sId = 0;
	Integer gameId = 0;
	double factor = 0d;
	String timeFrom = null;
	String timeTo = null;
	String sstid = null;
	//Integer server_id=0;
	
	@RequestMapping(value="/getItemSaleTop")
	@ResponseBody
	public Message getItemSaleTop(
			HttpServletRequest request, 
			HttpServletResponse response){
		
		getParam(request, response);
		Integer server_id = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		List<EconomyInfo> economyInfoList = logMgrService.getItemSaleTop10(sstid, -1, timeFrom, timeTo, server_id);		
		if(economyInfoList.size()==0){
			return new Message().setSuccessMessage().setData(new JSONArray().add(null));
		}
		/*Comparator<EconomyInfo> comp = Collections.reverseOrder();
		Collections.sort(economyInfoList, comp);*/
		System.out.println("server_id="+server_id);
		System.out.println("factor="+factor);
		System.out.println("sstid="+sstid);
		String key = "\"key\":[";
		String data1 = "{"+"\"name\""+":"+"\"销售金额\""+","+"\"data\""+":[";
		String data2 = "{"+"\"name\""+":"+"\"销售数量\""+","+"\"data\""+":[";
		String data0 = "\"data\""+":[";
		System.out.println("economyInfoList.size()="+economyInfoList.size());
		for (int i = 0 ; i < economyInfoList.size() ; i++) {
			
			if(i==economyInfoList.size()-1){
				key = key + "\""+economyInfoList.get(i).getItem_name()+"\""+"],";
			}else{
				key = key + "\""+economyInfoList.get(i).getItem_name()+"\""+",";
			}
			
			if(i==economyInfoList.size()-1){
				data1 = data1 + "\""+economyInfoList.get(i).getsalemoney()*factor+"\""+"]}";
			}else{
				data1 = data1 + "\""+economyInfoList.get(i).getsalemoney()*factor+"\""+",";
			}
			
			if(i==economyInfoList.size()-1){
				data2 = data2 + "\""+economyInfoList.get(i).getSalenum()+"\""+"]}";
			}else{
				data2 = data2 + "\""+economyInfoList.get(i).getSalenum()+"\""+",";
			}
			
		}
		data0 = data0 + data1 + "," + data2 +"]";
		String dataStr = "[{" + key + data0 + "}]";
		//Object data = JSON.toJSON(dataStr);

		Message message = new Message().setSuccessMessage().setData(JSON.parse(dataStr));
		return message;
		
	}
	
	@RequestMapping(value="/getItemSaleList")
	@ResponseBody
	public Message getItemSaleList(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		Integer server_id = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		Integer start = 0;
		if(request.getParameter("start")!=null){
			start = Integer.parseInt(request.getParameter("start"));
		}
		Integer end = 100;
		if(request.getParameter("end")!=null){
			end = Integer.parseInt(request.getParameter("end"));
		}
		List<EconomyInfo> itemList = logMgrService.getItemSaleList(sstid, -1, timeFrom, timeTo, server_id,start,end);
		if(itemList.size()==0){
			return new Message().setSuccessMessage().setData(new JSONArray().add(null));
		}
		JSONArray jsonArray = new JSONArray();
		for (EconomyInfo economyInfo : itemList) {
			JSONObject jsonObject = new JSONObject();
			String item_id = economyInfo.getItem_id();
			String item_name = economyInfo.getItem_name();
			Integer buycount = economyInfo.getBuycount();
			Integer saleMoney = economyInfo.getsalemoney();
			Integer saleNum = economyInfo.getSalenum();
			jsonObject.put("item_id",item_id );
			jsonObject.put("item_name",item_name );
			jsonObject.put("buycount",buycount );
			jsonObject.put("salemoney", saleMoney*factor);
			jsonObject.put("salenum", saleNum);
			jsonArray.add(jsonObject);
		}
		Message message = new Message().setSuccessMessage().setData(jsonArray);
		return message;
	}

	@RequestMapping(value="/getItemSaleListTotal")
	@ResponseBody
	public Message getItemSaleListTotal(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		
		Integer server_id = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		Integer total = logMgrService.getItemSaleListTotal(sstid, -1, timeFrom, timeTo, server_id);
		if(total == 0||total==null){
			total = 712;//随便填写的数字,为了测试生成页码
		}
		Message message = new Message().setSuccessMessage().setData(JSON.parse(total.toString()));
		return message;
	}
	
	@RequestMapping("/getItemSaleDetail")
	@ResponseBody
	public Message getItemSaleDetail(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		String item_id = request.getParameter("item_id");
		Integer server_id = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		List<EconomyInfo> itemDetailList = logMgrService.getItemSaleDetail(sstid, -1, timeFrom, timeTo, server_id,item_id);
		if(itemDetailList.size()==0){
			return new Message().setSuccessMessage().setData(new JSONArray().add(null));
		}
		JSONArray keyArray = new JSONArray();
		JSONArray jsonArrayDataMoney = new JSONArray();
		JSONArray jsonArrayDataNum = new JSONArray();
		JSONArray jsonArrayDataCount = new JSONArray();
		for (EconomyInfo economyInfo : itemDetailList) {
			keyArray.add(DateTools.timeStamp2Date(Long.parseLong(economyInfo.getTime()), "yyyy-MM-dd"));
			jsonArrayDataMoney.add(economyInfo.getsalemoney()*factor);
			jsonArrayDataNum.add(economyInfo.getSalenum());
			jsonArrayDataCount.add(economyInfo.getBuyucount());
		}
		JSONObject jsonObjectMoney = new JSONObject();
		JSONObject jsonObjectNum = new JSONObject();
		JSONObject jsonObjectCount = new JSONObject();
		jsonObjectMoney.put("name", "销售金额");
		jsonObjectMoney.put("data", jsonArrayDataMoney);
		jsonObjectNum.put("name", "销售数量");
		jsonObjectNum.put("data", jsonArrayDataNum);
		jsonObjectCount.put("name", "购买人数");
		jsonObjectCount.put("data", jsonArrayDataCount);
		JSONArray dataArray = new JSONArray();
		dataArray.add(jsonObjectMoney);
		dataArray.add(jsonObjectNum);
		dataArray.add(jsonObjectCount);
		/*JSONObject dataObject = new JSONObject();
		dataObject.put("data", dataArray);
		JSONObject keyObject = new JSONObject();
		keyObject.put("key", keyArray);*/
		JSONObject dataInfoObject = new JSONObject();
		dataInfoObject.put("data", dataArray);
		dataInfoObject.put("key", keyArray);
		JSONArray dataInfoArray = new JSONArray();
		dataInfoArray.add(dataInfoObject);
		Message message = new Message().setSuccessMessage().setData(dataInfoArray);
		return message;
	}
	

	@RequestMapping(value="/getCategorySaleList")
	@ResponseBody
	public Message getCategorySaleList(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		Integer start = 0;
		if(request.getParameter("start")!=null){
			start = Integer.parseInt(request.getParameter("start"));
		}
		Integer end = 100;
		if(request.getParameter("end")!=null){
			end = Integer.parseInt(request.getParameter("end"));
		}
		Integer parent_id = 0;
		if(request.getParameter("parent_id") !=null){
			parent_id = Integer.parseInt(request.getParameter("parent_id"));
		}else{
			parent_id = 0;
		}
		Integer server_id = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		List<CategoryInfo> categoryInfoList = logMgrService.getCategorySaleList(server_id, sstid, timeFrom, timeTo,start,end, "tmp_category_table", parent_id);
//		JSONObject jsonObject0 = new JSONObject();
//		JSONObject jsonObject1 = new JSONObject();
		if(categoryInfoList.size()==0){
			return new Message().setSuccessMessage().setData(new JSONArray().add(null));
		}
		JSONArray jsonArray = new JSONArray();
		for (CategoryInfo categoryInfo : categoryInfoList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("category_id", categoryInfo.getCategory_id());
			jsonObject.put("category_name", categoryInfo.getCategory_name());
			jsonObject.put("is_leaf", categoryInfo.getIs_leaf());
			jsonObject.put("buycount", categoryInfo.getBuycount());
			jsonObject.put("salenum", categoryInfo.getSalenum());
			jsonObject.put("salemoney", categoryInfo.getSalemoney());
			jsonArray.add(jsonObject);
		}
		Message message = new Message().setSuccessMessage().setData(jsonArray);
		return message;
	}
	
	
	@RequestMapping("/getCategorySaleDetail")
	@ResponseBody
	public Message getCategorySaleDetail(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		Integer category_id = Integer.parseInt(request.getParameter("category_id"));
		Integer server_id = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		List<CategoryInfo> categoryDetailList = logMgrService.getCategorySaleDetail(category_id, sstid, server_id, timeFrom, timeTo);
		System.out.println("categoryDetailList.size()="+categoryDetailList.size());
		if(categoryDetailList.size()==0){
			return new Message().setSuccessMessage().setData(new JSONArray().add(null));
		}
		JSONArray keyArray = new JSONArray();
		JSONArray jsonArrayDataMoney = new JSONArray();
		JSONArray jsonArrayDataNum = new JSONArray();
		JSONArray jsonArrayDataCount = new JSONArray();
		for (CategoryInfo categoryInfo : categoryDetailList) {
			keyArray.add(DateTools.timeStamp2Date(Long.parseLong(categoryInfo.getTime()), "yyyy-MM-dd"));
			jsonArrayDataMoney.add(categoryInfo.getSalemoney()*factor);
			jsonArrayDataNum.add(categoryInfo.getSalenum());
			jsonArrayDataCount.add(categoryInfo.getBuyucount());
		}
		JSONObject jsonObjectMoney = new JSONObject();
		JSONObject jsonObjectNum = new JSONObject();
		JSONObject jsonObjectCount = new JSONObject();
		jsonObjectMoney.put("name", "销售金额");
		jsonObjectMoney.put("data", jsonArrayDataMoney);
		jsonObjectNum.put("name", "销售数量");
		jsonObjectNum.put("data", jsonArrayDataNum);
		jsonObjectCount.put("name", "购买人数");
		jsonObjectCount.put("data", jsonArrayDataCount);
		JSONArray dataArray = new JSONArray();
		dataArray.add(jsonObjectMoney);
		dataArray.add(jsonObjectNum);
		dataArray.add(jsonObjectCount);
		/*JSONObject dataObject = new JSONObject();
		dataObject.put("data", dataArray);
		JSONObject keyObject = new JSONObject();
		keyObject.put("key", keyArray);*/
		JSONObject dataInfoObject = new JSONObject();
		dataInfoObject.put("data", dataArray);
		dataInfoObject.put("key", keyArray);
		JSONArray dataInfoArray = new JSONArray();
		dataInfoArray.add(dataInfoObject);
		Message message = new Message().setSuccessMessage().setData(dataInfoArray);
		return message;
	}
	
	@RequestMapping(value="/getCategorySaleListTotal")
	@ResponseBody
	public Message getCategorySaleListTotal(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		Integer parent_id = 0;
		if(request.getParameter("parent_id") !=null){
			parent_id = Integer.parseInt(request.getParameter("parent_id"));
		}else{
			parent_id = 0;
		}
		Integer server_id = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		Integer total = logMgrService.getCategorySaleListTotal(server_id, sstid, timeFrom, timeTo, "tmp_category_table", parent_id);
		System.out.println(total);
		if(total == null||total==0){
			total = 1;//随便填写的数字,为了测试生成页码
		}
		Message message = new Message().setSuccessMessage().setData(JSON.parse(total.toString()));
		return message;
	}
	
	@RequestMapping(value="getItemList")
	@ResponseBody
	public Message getItemList(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		Integer is_leaf = 1;
		Integer start = 0;
		if(request.getParameter("start")!=null){
			start = Integer.parseInt(request.getParameter("start"));
		}
		Integer end = 100;
		if(request.getParameter("end")!=null){
			end = Integer.parseInt(request.getParameter("end"));
		}
		if(request.getParameter("is_leaf") != null && request.getParameter("is_leaf") != "")
		{
			is_leaf = Integer.parseInt(request.getParameter("is_leaf"));
		}
		if(gameId == 2){
			itemInfoList = logMgrService.getItemListIfGameIdIs2(sstid, gameId, is_leaf,start,end);
		}else {
			itemInfoList = logMgrService.getItemList(sstid, gameId, is_leaf,start,end);
		}
		JSONArray data = new JSONArray();

		Map<String, ItemInfo> itemMap = new LinkedHashMap<String, ItemInfo>();
		for (ItemInfo itemInfo : itemInfoList) {
			if(itemMap.containsKey(itemInfo.getId())){
				String cName1 = itemMap.get(itemInfo.getId()).getCategory_name();
				ItemInfo item = new ItemInfo();
				item.setId(itemInfo.getId());
				item.setName(itemInfo.getName());
				item.setHide(itemInfo.getHide());
				item.setCategory_name(cName1+";"+itemInfo.getCategory_name());
				itemMap.put(itemInfo.getId(), item);
			}else{
				itemMap.put(itemInfo.getId(), itemInfo);
			}
		}
		for(Map.Entry<String, ItemInfo> entry : itemMap.entrySet()){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", entry.getValue().getId());
			jsonObject.put("name", entry.getValue().getName());
			jsonObject.put("hide", entry.getValue().getHide());
			jsonObject.put("category_name", entry.getValue().getCategory_name());
			data.add(jsonObject);
		}
		Message message = new Message().setSuccessMessage().setData(data);
		return message;
	}
	
	@RequestMapping(value="getItemListTotal")
	@ResponseBody
	public Message getItemListTotal(HttpServletRequest request,HttpServletResponse response){
		getParam(request, response);
		int is_leaf = 1;
		if(request.getParameter("is_leaf") != null && request.getParameter("is_leaf") != "")
		{
			is_leaf = Integer.parseInt(request.getParameter("is_leaf"));
		}
		Integer size = 0;
		if(gameId == 2){
			size = logMgrService.getItemListTotalIfGameIdIs2(sstid, gameId, is_leaf);
		}else {
			size = logMgrService.getItemListTotal(sstid, gameId, is_leaf);
		}
		Message message = new Message().setSuccessMessage().setData(JSON.parse(size.toString()));
		return message;
	}
	
	@RequestMapping(value="setName")
	@ResponseBody
	public Message setName(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		String item_name = null;
		String item_id = null;
		if(request.getParameter("id") == null || request.getParameter("id") == ""){
			System.out.println("item_id is null");
			new Throwable("item_id is null");
		}else{
			item_id = new String(request.getParameter("id").getBytes("iso-8859-1"),"utf-8");
		}
		if(request.getParameter("name") == null || request.getParameter("name") == ""){
			System.out.println("item_name is null");
			new Throwable("item_name is null");
		}else{
			item_name = new String(request.getParameter("name").getBytes("iso-8859-1"),"utf-8");
		}
		int hide = 0;
		if(request.getParameter("hide") != null && request.getParameter("hide") != "")
		{
			hide = Integer.parseInt(request.getParameter("hide"));
		}
		Integer data = logMgrService.setCategoryName(sstid, gameId, item_id, item_name, hide);
		Message message = new Message().setSuccessMessage().setData(JSON.parse(data.toString()));
		return message;
	}
	
	@RequestMapping(value="setHide")
	@ResponseBody
	public Message setHide(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		String item_name = null;
		String item_id = null;
		if(request.getParameter("id") == null || request.getParameter("id") == ""){
			System.out.println("item_id is null");
			new Throwable("item_id is null");
		}else{
			item_id = new String(request.getParameter("id").getBytes("iso-8859-1"),"utf-8");
		}
		if(request.getParameter("name") == null || request.getParameter("name") == ""){
			item_name = item_id;
		}	
		int hide = 0;
		hide = Integer.parseInt(request.getParameter("hide"));
		Integer data = logMgrService.setHide(sstid, gameId, item_id, item_name, hide);
		Message message = new Message().setSuccessMessage().setData(JSON.parse(data.toString()));
		return message;
	}

	@RequestMapping(value="getItemCategory")
	@ResponseBody
	public Message getItemCategory(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		
		List<ItemCategoryInfo> itemCategoryInfoList = new ArrayList<ItemCategoryInfo>();
		int is_leaf = 1;
		if(request.getParameter("is_leaf") != null && request.getParameter("is_leaf") != "")
		{
			is_leaf = Integer.parseInt(request.getParameter("is_leaf"));
		}
		String item_id = request.getParameter("item_id");
		if(item_id != null && item_id != ""){
			/*item_id = new String(request.getParameter("item_id").getBytes("iso-8859-1"),"utf-8");*/
		}else{
			System.out.println("item_id is null");
			new Throwable("item_id is null");
		}
		itemCategoryInfoList = logMgrService.getItemCategory(item_id, sstid, gameId, is_leaf);
		JSONArray data = new JSONArray();
		
		for (ItemCategoryInfo itemCategoryInfo : itemCategoryInfoList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("category_id", itemCategoryInfo.getCategory_id());
			jsonObject.put("category_name", itemCategoryInfo.getCategory_name());
			jsonObject.put("parent_id", itemCategoryInfo.getParent_id());
			jsonObject.put("parent_name", itemCategoryInfo.getParent_name());
			data.add(jsonObject);
		}
		
		Message message = new Message().setSuccessMessage().setData(data);
		return message;
	}
	
	@RequestMapping(value="getCategoryList")
	@ResponseBody
	public Message getCategoryList(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		
		List<ItemCategoryInfo> itemCategoryInfoList = new ArrayList<ItemCategoryInfo>();
		Integer parent_id = Integer.parseInt(request.getParameter("parent_id"));
		itemCategoryInfoList = logMgrService.getCategoryList(parent_id, sstid, gameId);
		JSONArray data = new JSONArray();
		
		for (ItemCategoryInfo itemCategoryInfo : itemCategoryInfoList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("category_id", itemCategoryInfo.getCategory_id());
			jsonObject.put("category_name", itemCategoryInfo.getCategory_name());
			jsonObject.put("game_id", itemCategoryInfo.getGame_id());
			jsonObject.put("is_leaf", itemCategoryInfo.getIs_leaf());
			jsonObject.put("parent_id", itemCategoryInfo.getParent_id());
			jsonObject.put("sstid", itemCategoryInfo.getSstid());
			data.add(jsonObject);
		}
		
		Message message = new Message().setSuccessMessage().setData(data);
		return message;
	}
	
	@RequestMapping(value="setItemCategory")
	@ResponseBody
	public Message setItemCategory(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		
		String[] category_ids = request.getParameterValues("category_id[]");
		String sstid = request.getParameter("sstid");
		Integer game_id = Integer.parseInt(request.getParameter("game_id"));
		String item_id = request.getParameter("item_id");
		String[] item_ids = request.getParameterValues("item_id[]");
		Integer data = 0;
		Integer ref_count = 1;
		if(request.getParameter("ref_count") != null && request.getParameter("ref_count") != "")
		{
			ref_count = Integer.parseInt(request.getParameter("ref_count"));
		}
		if(item_ids != null && item_ids.length != 0){
			for(int i = 0; i < item_ids.length; i++){
				item_id = item_ids[i];
				logMgrService.deleteItemCategory(item_id, sstid, game_id);
				if(category_ids != null && category_ids.length != 0){
					for (int j = 0; j < category_ids.length; j++) {
						Integer category_id = Integer.parseInt(category_ids[j]);
						data = logMgrService.setItemCategory(category_id, sstid, game_id, item_id, ref_count);
					}
				}
			}
		}else{
			logMgrService.deleteItemCategory(item_id, sstid, game_id);
			if(category_ids != null && category_ids.length != 0){
				for (int j = 0; j < category_ids.length; j++) {
					Integer category_id = Integer.parseInt(category_ids[j]);
					data = logMgrService.setItemCategory(category_id, sstid, game_id, item_id, ref_count);
				}
			}
		}
		System.out.println(data);
		Message message = new Message().setSuccessMessage().setData(JSON.parse(data.toString()));
		return message;
	}
	//修改道具类别名称
	@RequestMapping(value="setCategory")
	@ResponseBody
	public Message setCategory(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		
		Integer category_id = Integer.parseInt(request.getParameter("id"));
		String category_name = null;
		if(request.getParameter("name") == null || request.getParameter("name") == ""){
			System.out.println("category_name is null");
			new Throwable("category_name is null");
		}else{
			category_name = new String(request.getParameter("name").getBytes("iso-8859-1"),"utf-8");
		}
		Integer data = 0;
		logMgrService.updateItemCategory(category_id, category_name);
		System.out.println(data);
		Message message = new Message().setSuccessMessage();
		return message;
	}
	
	//增加子类道具类别名称
		@RequestMapping(value="addCategory")
		@ResponseBody
		public Message addCategory(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			getParam(request, response);
			Integer is_leaf = 0;
			if(request.getParameter("is_leaf")==null){
				is_leaf = 1;
			}else{
				is_leaf = Integer.parseInt(request.getParameter("is_leaf"));
			}
			Integer parent_id = Integer.parseInt(request.getParameter("parent_id"));
			String category_name = request.getParameter("category_name");
			String sstid = request.getParameter("sstid");
			
			Integer data = 0;
			logMgrService.insertItemCategory(category_name, sstid, gameId, parent_id, is_leaf);
			System.out.println(data);
			Message message = new Message().setSuccessMessage();
			return message;
		}
		
	//删除子类道具类别名称
	@RequestMapping(value="delCategory")
	@ResponseBody
	public Message delCategory(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		Integer category_id = Integer.parseInt(request.getParameter("category_id"));
		String sstid = request.getParameter("sstid");
		logMgrService.delCategory(category_id, sstid, gameId);
		Message message = new Message().setSuccessMessage();
		return message;
	}
		
	
	//移动类道具类别
	@RequestMapping(value="moveCategory")
	@ResponseBody
	public Message moveCategory(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		getParam(request, response);
		Integer category_id = Integer.parseInt(request.getParameter("category_id"));
		Integer parent_id = Integer.parseInt(request.getParameter("parent_id"));
		String sstid = request.getParameter("sstid");
		logMgrService.moveCategory(parent_id, category_id, sstid, gameId);
		Message message = new Message().setSuccessMessage();
		return message;
	}
	private void getParam(HttpServletRequest request,HttpServletResponse response){
		String fromDateString = (request.getParameter("from[0]") == null)?  request.getParameter("from") : request.getParameter("from[0]");
		String toDateString = (request.getParameter("to[0]") == null)?  request.getParameter("to") : request.getParameter("to[0]");
		timeFrom = DateTools.date2TimeStamp(fromDateString, "yyyy-MM-dd").toString();
		timeTo = DateTools.date2TimeStamp(toDateString, "yyyy-MM-dd").toString();
		System.out.println("from="+fromDateString);
		System.out.println("to="+toDateString);
		if(request.getParameter("factor")==null){
			factor = 0.01d;
		}else{
			factor = Double.parseDouble(request.getParameter("factor"));
		}
		sstid = request.getParameter("sstid");
		gameId = Integer.parseInt(request.getParameter("game_id"));
		
		if(request.getParameter("platform_id")==null){
			platformId = -1;
		}else{
			platformId = Integer.parseInt(request.getParameter("platform_id"));
		}
		
		if(request.getParameter("zone_id")==null){
			zoneId = -1;
		}else{
			zoneId = Integer.parseInt(request.getParameter("zone_id"));
		}
		
		if(request.getParameter("s_id")==null){
			sId = -1;
		}else{
			sId = Integer.parseInt(request.getParameter("s_id"));
		}
	}
	
}
