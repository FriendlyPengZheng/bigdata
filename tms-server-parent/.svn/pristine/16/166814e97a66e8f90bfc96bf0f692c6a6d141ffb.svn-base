package com.taomee.tms.mgr.core.serveridanalyser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.entity.ServerInfo;

// 非线程安全
public class ServerIdAnalyzer implements Serializable {
	private static final long serialVersionUID = -1930134650620011345L;
	private static final Logger LOG = LoggerFactory.getLogger(ServerIdAnalyzer.class);
	
	// 顶层serverId
	private Set<String> rootServerIds = new HashSet<String>();

	// <child, 所有的长辈(包括自己)>，用于打散
	private Map<String, List<String>> child2Ancestors = new HashMap<String, List<String>>();
	
	// <serverId, gameId>
	private Map<String, String> serverId2GameId = new HashMap<String, String>();
	
	public boolean Init(List<ServerInfo> serverInfos) {
		if (serverInfos == null || serverInfos.size() == 0) {
			LOG.error("ServerIdAnalyizer SetServerInfos, empty serverInfos");
			return false;
		}
		
		Map<String, Set<String>> parent2Children = new HashMap<String, Set<String>>();
		for (ServerInfo serverInfo : serverInfos) {
			// 处理serverId
			Integer intServerId = serverInfo.getServerId();
			if (intServerId == null || intServerId.intValue() <= 0) {
				LOG.error("ServerIdAnalyizer Init, serverId null or <= 0");
				return false;
			}
			String strServerId = serverInfo.getServerId().toString();
			
			// 处理parentId
			// parentId为0表示root节点并包含gameid
			Integer intParentId = serverInfo.getParentId();
			if (intParentId == null || intParentId.intValue() < 0) {
				LOG.error("ServerIdAnalyizer Init, parentId null or <= 0");
				return false;
			}
			int parentId = intParentId.intValue();
			String strParentId = intParentId.toString();
			
			// 处理gameId
			Integer intGameId = serverInfo.getGameId();
			if (intGameId == null || intGameId.intValue() <= 0) {
				LOG.error("ServerIdAnalyizer Init, invalid gameid, serverId is " + strServerId);
				return false;
			}
			String strGameId = intGameId.toString();
			
			// serverId不能重复
			if (serverId2GameId.containsKey(strServerId)) {
				LOG.error("ServerIdAnalyizer Init, duplicate serverId, serverId is " + strServerId);
				return false;
			}
			serverId2GameId.put(strServerId, strGameId);
			
			if (parentId == 0) {
				// 顶层的serverId
				rootServerIds.add(strServerId);
			} else {
				if (parent2Children.get(strParentId) == null) {
					parent2Children.put(strParentId, new HashSet<String>());
				}
				parent2Children.get(strParentId).add(strServerId);
			}
		}
		
		Set<String> rootGameIds = new HashSet<String>();
		for (String rootServerId : rootServerIds) {
			rootGameIds.add(serverId2GameId.get(rootServerId));
		}
		
		// 检查是否有两个rootServerId对应的gameId相同
		if (rootServerIds.size() != rootGameIds.size()) {
			LOG.error("ServerIdAnalyizer Init, some gameId has more than one root serverId, check root serverIds' config");
			return false;
		}
		
		for (String rootServerId : rootServerIds) {
			RecursiveSetAncestors(rootServerId, new ArrayList<String>(), parent2Children);
		}
		
		// 检查是否存在parentId无对应的serverId
		if (child2Ancestors.size() != serverId2GameId.size()) {
			LOG.error("ServerIdAnalyizer Init, parentId configured incorrect");
			for (Entry<String, String> entry : serverId2GameId.entrySet()) {
				if (!child2Ancestors.containsKey(entry.getKey())) {
					LOG.error("ServerIdAnalyizer Init, perhaps wrong parentId configured in serverId [" + entry.getKey() + "]");
				}
			}
			return false;
		}
		
		// 协议中ServerInfo对应的gameId已正确设置，因此无需重新设置，但需要检查 
		// Init成功以后可以确保serverId2GameId中所有的serverId都有对应的gameId，且gameId > 0
//		for (String rootServerId : rootServerIds) {
//			RecursiveSetGameId(rootServerId, serverId2GameId.get(rootServerId));
//		}
		
		// 检查每个子节点和其父节点对应的gameId是否一致
		for (Entry<String, String> entry : serverId2GameId.entrySet()) {
			// 根节点无需处理
			boolean consist = true;
			String strCurServerId = entry.getKey();
			if (rootServerIds.contains(strCurServerId)) continue;
			
			// 上面的检查已经确保所有的parentId均是正常的serverId
			List<String> ancestors = child2Ancestors.get(strCurServerId);
			if (ancestors == null || ancestors.size() < 2) {
				// 不应该能到此
				LOG.error("ServerIdAnalyizer Init, unknown error with serverId [" + strCurServerId + "]");
				return false;
			}
			
			// 取ancestor数组中倒数第二个
			String strCurParentId = ancestors.get(ancestors.size() - 2);
			if (!serverId2GameId.get(strCurServerId).equals(serverId2GameId.get(strCurParentId))) {
				LOG.error("ServerIdAnalyizer Init, parentId [" + strCurParentId + "] and childId [" + strCurServerId + "] has different gameId");
				consist = false;
			}
			
			if (!consist) {
				return false;
			}
		}
		
		// 判断gameid是否有重复
		
//		LOG.info(PrintChild2Ansestors());
		return true;
	}
	
	public String GetGameIdByServerId(String strServerId) {
		return serverId2GameId.get(strServerId);
	}

	public List<String> GetServerAncestors(String strServerId) {
		return child2Ancestors.get(strServerId);
	}
	
	// 实时计算部分使用，但所有内存数据均需要更新
	public boolean AddServerInfo(ServerInfo serverInfo) {
		// 处理serverId
		Integer intServerId = serverInfo.getServerId();
		if (intServerId == null || intServerId.intValue() <= 0) {
			LOG.error("ServerIdAnalyizer AddServerId, serverId null or <= 0");
			return false;
		}
		String strServerId = serverInfo.getServerId().toString();
		
		if (serverId2GameId.containsKey(strServerId)) {
			// 可能会重复收到，前面只能保证at least once
			LOG.warn("ServerIdAnalyizer AddServerId, serverId [" + strServerId + "] already exist");
			return true;
		}
		
		// 处理parentId
		// parentId为0表示root节点并包含gameid
		Integer intParentId = serverInfo.getParentId();
		if (intParentId == null || intParentId.intValue() < 0) {
			LOG.error("ServerIdAnalyizer AddServerId, parentId null or <= 0");
			return false;
		}
		int parentId = intParentId.intValue();
		String strParentId = intParentId.toString();
		
		// 处理gameId
		Integer intGameId = serverInfo.getGameId();
		if (intGameId == null || intGameId.intValue() <= 0) {
			LOG.error("ServerIdAnalyizer AddServerId, invalid gameid, serverId is " + strServerId);
			return false;
		}
		String strGameId = intGameId.toString();
		
		if (parentId == 0) {
			// 根节点，代表一个游戏
			if (serverId2GameId.values().contains(strGameId)) {
				// 不能两个根serverId对应一个gameId
				LOG.error("ServerIdAnalyizer AddServerId, gameid [" + strGameId +"] already has some serverIds");
				return false;
			}
			
			// 更新child2Ancestors
			List<String> ancestors = new ArrayList<String>();
			ancestors.add(strServerId);
			child2Ancestors.put(strServerId, ancestors);
			
			// 更新rootServerIds
			rootServerIds.add(strServerId);
		} else {
			// 非根节点
			if (!serverId2GameId.containsKey(strParentId)) {
				LOG.error("ServerIdAnalyizer AddServerId, parentId [" + strParentId +"] not exist");
				return false;
			}
			
			if (!serverId2GameId.get(strParentId).equals(strGameId)) {
				LOG.error("ServerIdAnalyizer AddServerId, serverId [" + strServerId + "] with gameId [" + strGameId + "], "
						+ "but parentId [" + strParentId + "] with gameId [" + serverId2GameId.get(strParentId) + "]" );
				return false;
			}
			
			// 更新child2Ancestors
			List<String> ancestors = new ArrayList<String>(child2Ancestors.get(strParentId));
			ancestors.add(strServerId);
			child2Ancestors.put(strServerId, ancestors);
		}
		
		// 更新serverId2GameId
		serverId2GameId.put(strServerId, strGameId);
		
		return true;
	}
	
	// 隐藏ServerInfo，只会关心serverId字段，实际并未从数据库删除
	public boolean DelServerInfo(ServerInfo serverInfo) {
		Integer intServerId = serverInfo.getServerId();
		if (intServerId == null || intServerId.intValue() <= 0) {
			LOG.error("ServerIdAnalyizer DelServerId, serverId null or <= 0");
			return false;
		}
		String strServerId = serverInfo.getServerId().toString();
		
		// 看其是否有子节点 Map<String, List<String>>
		int refCount = 0;
		for (Entry<String, List<String>> entry : child2Ancestors.entrySet()) {
			for (String ancestor : entry.getValue()) {
				if (ancestor.equals(strServerId)) refCount++;
			}
		}
		
		if (refCount < 1) {
			// 不应该能到此
			LOG.error("ServerIdAnalyizer DelServerId, child2Ancestors' values not contains serverId [" + strServerId + "]");
			return false;
		} else if (refCount > 1) {
			LOG.error("ServerIdAnalyizer DelServerId, serverId [" + strServerId + "] is not leaf");
			return false;
		}
		
		if (rootServerIds.contains(strServerId)) {
			rootServerIds.remove(strServerId);
		}
				
		if (!child2Ancestors.containsKey(strServerId)) {
			LOG.warn("ServerIdAnalyizer DelServerId, serverId [" + strServerId + "] not exist in child2Ancestors");
		} else {
			child2Ancestors.remove(strServerId);
		}
		
		if (!serverId2GameId.containsKey(strServerId)) {
			LOG.warn("ServerIdAnalyizer DelServerId, serverId [" + strServerId + "] not exist in serverId2GameId");
		} else {
			serverId2GameId.remove(strServerId);
		}
		
		return true;
	}
	
	private void RecursiveSetAncestors(String strServerId, List<String> ancestors, Map<String, Set<String>> parent2Children) {
		// 首先插入当前的
		List<String> newAnsestors = new ArrayList<String>(ancestors);
		newAnsestors.add(strServerId);
		child2Ancestors.put(strServerId, newAnsestors);
		
		// 再处理孩子
		if (parent2Children.get(strServerId) == null) {
			return;
		}
		
		for (String child: parent2Children.get(strServerId)) {
			RecursiveSetAncestors(child, newAnsestors, parent2Children);
		}
	}
	
	private void RecursiveSetGameId(String strServerId, String strGameId, Map<String, Set<String>> parent2Children) {
		// 首先插入当前的
		serverId2GameId.put(strServerId, strGameId);
		
		// 再处理孩子
		if (parent2Children.get(strServerId) == null) {
			return;
		}
		
		for (String child: parent2Children.get(strServerId)) {
			RecursiveSetGameId(child, strGameId, parent2Children);
		}
	}
	
	private String GetPrintChild2Ansestors() {
		StringBuilder content = new StringBuilder("child2Ansestors:\n");
		for (Entry<String, List<String>> entry : child2Ancestors.entrySet()) {
			content.append(entry.getKey());
			content.append("\t (");
			content.append(entry.getValue().toString());
			content.append(")\n");
		}
		return content.toString();
	}
	
	private String GetPrintServerId2GameId() {
		StringBuilder content = new StringBuilder("serverId2GameId:\n");
		content.append(serverId2GameId.toString());
		content.append("\n");
		return content.toString();
	}
	
	private String GetPrintrootServerIds() {
		StringBuilder content = new StringBuilder("rootServerIds:\n");
		content.append(rootServerIds.toString());
		content.append("\n");
		return content.toString();
	}
	
//	private String PrintParent2Children() {
//		StringBuilder content = new StringBuilder("parent2Children:\n");
//		for (Entry<String, Set<String>> entry : parent2Children.entrySet()) {
//			content.append(entry.getKey());
//			content.append("\t (");
//			content.append(entry.getValue().toString());
//			content.append(")\n");
//		}
//		return content.toString();
//	}
	
	public String GetPrintAllMemoryInfos() {
		return GetPrintrootServerIds() + GetPrintChild2Ansestors() + GetPrintServerId2GameId();
	}
	
	public static void main(String[] args) {		
		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		ServerInfo s1 = new ServerInfo();
		s1.setServerId(1);
		s1.setParentId(0);
		s1.setGameId(2);
		serverInfos.add(s1);
		
		ServerInfo s2 = new ServerInfo();
		s2.setServerId(2);
		s2.setParentId(1);
		s2.setGameId(2);
		serverInfos.add(s2);
		
		ServerInfo s3 = new ServerInfo();
		s3.setServerId(3);
		s3.setParentId(1);
		s3.setGameId(2);
		serverInfos.add(s3);
		
		ServerInfo s4 = new ServerInfo();
		s4.setServerId(4);
		s4.setParentId(2);
		s4.setGameId(2);
		serverInfos.add(s4);
		
		ServerInfo s5 = new ServerInfo();
		s5.setServerId(5);
		s5.setParentId(2);
		s5.setGameId(2);
		serverInfos.add(s5);
		
		ServerInfo s6 = new ServerInfo();
		s6.setServerId(6);
		s6.setParentId(3);
		s6.setGameId(2);
		serverInfos.add(s6);
		
		ServerInfo s7 = new ServerInfo();
		s7.setServerId(7);
		s7.setParentId(0);
		s7.setGameId(3);
		serverInfos.add(s7);
		
		ServerIdAnalyzer serverIdAnalyzer = new ServerIdAnalyzer();
		serverIdAnalyzer.Init(serverInfos);
		System.out.println(serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("---------------------测试AddServerInfo---------------------");
		
		// 根节点，gameId重复
//		ServerInfo s8 = new ServerInfo();
//		s8.setServerId(8);
//		s8.setParentId(0);
//		s8.setGameId(2);
//		serverInfos.add(s8);
//		serverIdAnalyzer.AddServerInfo(s8);
		
		// 根节点，正常
		ServerInfo s9 = new ServerInfo();
		s9.setServerId(9);
		s9.setParentId(0);
		s9.setGameId(4);
		serverInfos.add(s9);
		serverIdAnalyzer.AddServerInfo(s9);
		
		// 非根节点，parentId不存在
//		ServerInfo s10 = new ServerInfo();
//		s10.setServerId(10);
//		s10.setParentId(100);
//		s10.setGameId(5);
//		serverInfos.add(s10);
//		serverIdAnalyzer.AddServerInfo(s10);
		
		// 非根节点，和parentId对应的gameId不一致
//		ServerInfo s11 = new ServerInfo();
//		s11.setServerId(11);
//		s11.setParentId(7);
//		s11.setGameId(6);
//		serverInfos.add(s11);
//		serverIdAnalyzer.AddServerInfo(s11);
		
		// 非根节点，正常
		ServerInfo s12 = new ServerInfo();
		s12.setServerId(12);
		s12.setParentId(7);
		s12.setGameId(3);
		serverInfos.add(s12);
		serverIdAnalyzer.AddServerInfo(s12);
		
		System.out.println(serverIdAnalyzer.GetPrintAllMemoryInfos());
		
		System.out.println("---------------------测试DelServerInfo---------------------");
		
		// serverId存在child的
//		ServerInfo del1 = new ServerInfo();
//		del1.setServerId(3);
//		serverIdAnalyzer.DelServerInfo(del1);
		
		// root的
		ServerInfo del2 = new ServerInfo();
		del2.setServerId(9);
		serverIdAnalyzer.DelServerInfo(del2);
		
		// 非root的
		ServerInfo del3 = new ServerInfo();
		del3.setServerId(12);
		serverIdAnalyzer.DelServerInfo(del3);
		
		System.out.println(serverIdAnalyzer.GetPrintAllMemoryInfos());
	}
}





















