package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.CategoryInfo;
/*import com.taomee.tms.mgr.entity.CategoryItemInfo;*/
import com.taomee.tms.mgr.entity.EconomyInfo;
import com.taomee.tms.mgr.entity.ItemCategoryInfo;
import com.taomee.tms.mgr.entity.ItemInfo;

public interface EconomyInfoDao {
	
	/*@Select(value = { "select * from v_item_sale_data where sstid = #{sstid} and vip=#{vip} "
			+ "and time <![CDATA[>=]]> #{timeFrom} and time <![CDATA[<=]]> #{timeTo} and "
			+ "server_id=#{server_id}order by salemoney desc limit 0,10" })*/
	
	List<EconomyInfo> getSaleTop10(/*
			@Param("factor") double factor,*/
			@Param("sstid")String sstid,
			@Param("vip")Integer vip,
			@Param("timeFrom")String timeFrom,
			@Param("timeTo")String timeTo,
			@Param("server_id")Integer server_id);
	//List<EconomyInfo> getSaleTop10(String sstid,Integer vip,String timeFrom,String timeTo,Integer server_id);
	List<EconomyInfo> getItemSaleList(
			@Param("sstid")String sstid,
			@Param("vip")Integer vip,
			@Param("timeFrom")String timeFrom,
			@Param("timeTo")String timeTo,
			@Param("server_id")Integer server_id,
			@Param("start")Integer start,
			@Param("end")Integer end);
	List<EconomyInfo> getItemSaleDetail(
			@Param("sstid")String sstid,
			@Param("vip")Integer vip,
			@Param("timeFrom")String timeFrom,
			@Param("timeTo")String timeTo,
			@Param("server_id")Integer server_id,
			@Param("item_id")String item_id);
	Integer getItemSaleListTotal(
			@Param("sstid")String sstid,
			@Param("vip")Integer vip,
			@Param("timeFrom")String timeFrom,
			@Param("timeTo")String timeTo,
			@Param("server_id")Integer server_id);
	void dropTemTableByName(@Param("tableName")String tableName);
	void createTemporaryTable(Integer parent_id);
	List<CategoryInfo> getCategorySaleList(
			@Param("server_id")Integer server_id,
			@Param("sstid") String sstid,
			@Param("timeFrom") String timeFrom,
			@Param("timeTo") String timeTo,
			@Param("start")Integer start,
			@Param("end")Integer end);
	Integer getCategorySaleListTotal(
			@Param("sstid")String sstid,
			//@Param("vip")Integer vip,
			@Param("timeFrom")String timeFrom,
			@Param("timeTo")String timeTo,
			@Param("server_id")Integer server_id);
/*	List<CategoryItemInfo> getCategoryItemSaleList(
			@Param("category_id") Integer category_id,
			@Param("sstid") String sstid,
			@Param("game_id") Integer game_id);*/
	List<CategoryInfo> getCategorySaleDetail(
			@Param("category_id") Integer category_id,
			@Param("sstid") String sstid,
			@Param("server_id")Integer server_id,
			@Param("timeFrom")String timeFrom,
			@Param("timeTo")String timeTo);
	
	//道具管理
	
	void createCat1();
	void InitializeCat1(
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id, 
			@Param("is_leaf")Integer is_leaf);
	
	List<ItemInfo> getItemInfoListIfGameIdIs2(
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id,
			@Param("start")Integer start,
			@Param("end")Integer end);
	Integer getItemInfoListTotalIfGameIdIs2(
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id);
	
	Integer getItemInfoListTotal(
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id, 
			@Param("is_leaf")Integer is_leaf);	
	List<ItemInfo> getItemInfoList(
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id, 
			@Param("is_leaf")Integer is_leaf,
			@Param("start")Integer start,
			@Param("end")Integer end);
	
	Integer replaceItemInfo(
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id, 
			@Param("item_id")String item_id, 
			@Param("item_name")String item_name, 
			@Param("hide") Integer hide);
	
	Integer findHide(
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id, 
			@Param("item_id")String item_id, 
			@Param("hide") Integer hide);
	
	List<ItemCategoryInfo> getItemCategory(
			@Param("item_id")String item_id, 
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id, 
			@Param("is_leaf")Integer is_leaf);
	
	List<ItemCategoryInfo> getCategoryList(
			@Param("parent_id")Integer parent_id, 
			@Param("sstid")String sstid ,
			@Param("game_id")Integer game_id);
	
	Integer setItemCategory(
			@Param("category_id")Integer category_id,
			@Param("sstid")String sstid,
			@Param("game_id")Integer game_id,
			@Param("item_id")String item_id,
			@Param("ref_count")Integer ref_count);
	
	void deleteItemCategory(
			@Param("item_id")String item_id,
			@Param("sstid")String sstid,
			@Param("game_id")Integer game_id);
	
	void updateItemCategory(
			@Param("category_id")Integer category_id, 
			@Param("category_name")String category_name);
	
	void insertItemCategory(
			/*@Param("category_id")Integer category_id,自增列*/
			@Param("category_name")String category_name,
			@Param("sstid")String sstid,
			@Param("game_id")Integer game_id,
			@Param("parent_id")Integer parent_id,
			@Param("is_leaf")Integer is_leaf);
	
	void delCategory(
			@Param("category_id")Integer category_id,
			@Param("sstid")String sstid,
			@Param("game_id")Integer game_id);
	
	void moveCategory(
			@Param("parent_id")Integer parent_id,
			@Param("category_id")Integer category_id,
			@Param("sstid")String sstid,
			@Param("game_id")Integer game_id);
	
	ItemCategoryInfo selectItemCategory(Integer category_id);
}
