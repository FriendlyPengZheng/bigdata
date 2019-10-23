package com.taomee.tms.mgr.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.CustomSetNameParams;
import com.taomee.tms.mgr.entity.TreeInfo;



public interface TreeInfoDao {
	List<TreeInfo> getTreeInfos(Integer gameId);
	//搜索节点名称
	List<TreeInfo> searchNodeName(Integer gameId, String nodeName); 
	//TreeInfo getTreeInfosBynameGameId(Integer gameId, String nodeName);  //nodeName:stid/sstid
	TreeInfo getTreeInfosBynameGameId(Integer gameId, String nodeName, Integer parentId);
	
	Map<Integer, Integer> getParentId(@Param("params")Map<Integer, Integer> nodeIdList);  //使用@param注解，显式指定集合参数类的别名
	List<TreeInfo> getTreeInfosByGameIdParentId(Integer gameId, Integer parentId);
	
	void updateNode(TreeInfo treeInfo);	
	void updateNodeByParams(TreeInfo treeInfo);
	TreeInfo getNodeById(Integer nodeId);
	Integer insertTreeInfo(TreeInfo treeInfo);
	List<TreeInfo> getNodeByPid(TreeInfo treeInfo); 
	void deleteNode(@Param("nodeId")Integer nodeId);
	
}
