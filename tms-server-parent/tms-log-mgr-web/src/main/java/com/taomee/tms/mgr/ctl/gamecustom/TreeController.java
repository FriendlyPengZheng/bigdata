package com.taomee.tms.mgr.ctl.gamecustom;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.ctl.gameanalysis.OverviewController;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.entity.TreeInfo;

@Controller
@RequestMapping("/gamecustom/tree")
public class TreeController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -30);
		
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		long now = System.currentTimeMillis();
		
		String toDate = dfs.format(now);
		String fromDate = dfs.format(calendar.getTime());
		
		request.setAttribute("from", fromDate);
		request.setAttribute("to", toDate);
		
		Integer gameId = 0;
		Object gameIdObj = httpSession.getAttribute("game_id");
		if(gameIdObj == null) {
			return "gamecustom/view";
		} else {
			gameId = (Integer) gameIdObj;
		}
		ServerGPZSInfo tmp = logMgrService.getServerInfoByTopgameId(gameId);
		if(tmp == null) {
			return "gamecustom/view";
		}
		//System.out.print("Server id: " +tmp.toString()+ "\n");
		request.setAttribute("platform_id", tmp.getServerId());
		request.setAttribute("zone_id", -1);
		request.setAttribute("server_id", -1);
		
		// 初始game_id
		List<ServerInfo> list = logMgrService.getServerInfoByparentId(tmp.getServerId());//先写死写了小花仙
		if(list == null || list.size()==0) {
			System.out.print("Get ERROR\n");
		}
		request.setAttribute("platform", list);
		request.setAttribute("game_id", gameId);
		
		return "gamecustom/view";
	}
	
	@RequestMapping(value = "/getTree")
	public void getTree(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		/*if(request.getParameter("parent_id").equals("0")){
			printWriter.write("{\"result\":0,\"data\":[{\"node_id\":\"913520\",\"node_name\":\"\u7cbe\u70bc\u7b26\u6587\",\"is_leaf\":\"0\"},{\"node_id\":\"912852\",\"node_name\":\"\u5546\u57ce\u593a\u5b9d\",\"is_leaf\":\"1\"},{\"node_id\":\"910936\",\"node_name\":\"\u788e\u7247\u5546\u5e97\",\"is_leaf\":\"0\"},{\"node_id\":\"906850\",\"node_name\":\"\u65b0\u624b\u514d\u8d39\u82f1\u96c4\u7684\u6d41\u5931\u4eba\u6570\",\"is_leaf\":\"0\"},{\"node_id\":\"896421\",\"node_name\":\"\u79d8\u5883\u76f8\u5173\",\"is_leaf\":\"0\"},{\"node_id\":\"827279\",\"node_name\":\"\u6295\u964d\u529f\u80fd\",\"is_leaf\":\"0\"},{\"node_id\":\"893987\",\"node_name\":\"\u8d85\u795e\u4e4b\u8def\",\"is_leaf\":\"0\"},{\"node_id\":\"900681\",\"node_name\":\"\u795e\u79d8\u6298\u62632\",\"is_leaf\":\"1\"},{\"node_id\":\"886964\",\"node_name\":\"\u56de\u5f52\u5956\u52b1\",\"is_leaf\":\"0\"},{\"node_id\":\"883221\",\"node_name\":\"\u5bf9\u5c40\u7ed3\u675f\u53cc\u65b9\u5206\u5dee\u5927\u4e8e400\",\"is_leaf\":\"1\"},{\"node_id\":\"881482\",\"node_name\":\"\u70b9\u51fb\u82f1\u96c4\u4ecb\u7ecd\",\"is_leaf\":\"0\"},{\"node_id\":\"881400\",\"node_name\":\"\u82e5\u4e9a\u7684\u4fe1\u606f\",\"is_leaf\":\"0\"},{\"node_id\":\"881993\",\"node_name\":\"\u5546\u57ce\u793c\u5305\",\"is_leaf\":\"0\"},{\"node_id\":\"879071\",\"node_name\":\"\u5151\u6362\u6d88\u8017\",\"is_leaf\":\"0\"},{\"node_id\":\"879063\",\"node_name\":\"\u5bf9\u5c40\u7ed3\u675f\",\"is_leaf\":\"1\"},{\"node_id\":\"875904\",\"node_name\":\"\u94bb\u77f3\u6d88\u8d39\",\"is_leaf\":\"0\"},{\"node_id\":\"875903\",\"node_name\":\"\u91d1\u5e01\u6d88\u8d39\",\"is_leaf\":\"0\"},{\"node_id\":\"875898\",\"node_name\":\"\u5e38\u89c4\u6d3b\u52a8\",\"is_leaf\":\"0\"},{\"node_id\":\"875896\",\"node_name\":\"\u5468\u8fd0\u8425\u6d3b\u52a82017\",\"is_leaf\":\"0\"},{\"node_id\":\"824123\",\"node_name\":\"\u57fa\u672c\",\"is_leaf\":\"0\"},{\"node_id\":\"875796\",\"node_name\":\"\u65b0\u589e\u9053\u5177\",\"is_leaf\":\"0\"},{\"node_id\":\"875785\",\"node_name\":\"\u82f1\u96c4\u76ae\u80a4\u4f53\u9a8c\u5361\",\"is_leaf\":\"1\"},{\"node_id\":\"875784\",\"node_name\":\"\u51fa\u552e\u9053\u5177\",\"is_leaf\":\"0\"},{\"node_id\":\"872186\",\"node_name\":\"\u6df7\u6c8c\u738b\u5ea7\u76f8\u5173\u6570\u636e\",\"is_leaf\":\"0\"},{\"node_id\":\"862418\",\"node_name\":\"\u65b0\u624b\u6570\u636e1\",\"is_leaf\":\"0\"},{\"node_id\":\"827579\",\"node_name\":\"\u5468\u8fd0\u8425\u6d3b\u52a82016\",\"is_leaf\":\"0\"},{\"node_id\":\"826671\",\"node_name\":\"\u82f1\u96c4\u51fa\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"826673\",\"node_name\":\"\u82f1\u96c4\u80dc\u5229\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829487\",\"node_name\":\"\u94bb\u77f3\u4ee5\u4e0a\u82f1\u96c4\u51fa\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829570\",\"node_name\":\"\u94bb\u77f3\u4ee5\u4e0a\u82f1\u96c4\u80dc\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829437\",\"node_name\":\"\u94c2\u91d1\u82f1\u96c4\u51fa\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829439\",\"node_name\":\"\u94c2\u91d1\u82f1\u96c4\u80dc\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829431\",\"node_name\":\"\u9ec4\u91d1\u82f1\u96c4\u51fa\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829434\",\"node_name\":\"\u9ec4\u91d1\u82f1\u96c4\u80dc\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829448\",\"node_name\":\"\u767d\u94f6\u82f1\u96c4\u51fa\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829429\",\"node_name\":\"\u767d\u94f6\u82f1\u96c4\u80dc\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"829443\",\"node_name\":\"\u9752\u94dc\u82f1\u96c4\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"830080\",\"node_name\":\"\u9752\u94dc\u82f1\u96c4\u80dc\u573a\u6b21\u7edf\u8ba15V5\",\"is_leaf\":\"1\"},{\"node_id\":\"841261\",\"node_name\":\"\u6e38\u620f\u62a5\u9519\",\"is_leaf\":\"1\"},{\"node_id\":\"836838\",\"node_name\":\"\u5207\u78cb\u6a21\u5f0f\",\"is_leaf\":\"1\"},{\"node_id\":\"886959\",\"node_name\":\"\u6e38\u620f\u6570\u636e\",\"is_leaf\":\"0\"},{\"node_id\":\"829985\",\"node_name\":\"\u82f1\u96c4\u7684\u4fe1\u606f\",\"is_leaf\":\"0\"},{\"node_id\":\"828382\",\"node_name\":\"\u6bcf\u65e5\u6e38\u620f\u603b\u573a\u6b21\u4eba\u673a\",\"is_leaf\":\"0\"},{\"node_id\":\"827573\",\"node_name\":\"\u5929\u5730\u6865\",\"is_leaf\":\"0\"},{\"node_id\":\"827132\",\"node_name\":\"\u5151\u6362\u7b26\u6587\u4ea7\u51fa\",\"is_leaf\":\"0\"},{\"node_id\":\"826895\",\"node_name\":\"\u4e3bui\u89e6\u8fbe\",\"is_leaf\":\"1\"},{\"node_id\":\"847977\",\"node_name\":\"\u4e3bui\u89e6\u8fbe2\",\"is_leaf\":\"0\"},{\"node_id\":\"826612\",\"node_name\":\"\u8fd0\u8425\u6d3b\u52a8\u89e6\u8fbe\",\"is_leaf\":\"2\"},{\"node_id\":\"826579\",\"node_name\":\"\u65b0\u624b\u6570\u636e\uff08\u5f53\u65e5\uff09\",\"is_leaf\":\"0\"},{\"node_id\":\"826576\",\"node_name\":\"\u65b0\u624b\u6570\u636e\uff08\u65b0\u7248\uff09\",\"is_leaf\":\"0\"},{\"node_id\":\"826481\",\"node_name\":\"\u5fae\u7aef\u767b\u5f55\",\"is_leaf\":\"0\"},{\"node_id\":\"826443\",\"node_name\":\"\u4e3e\u62a5\u529f\u80fd\",\"is_leaf\":\"1\"},{\"node_id\":\"826133\",\"node_name\":\"\u5929\u8d4b\u7b26\u6587\",\"is_leaf\":\"0\"},{\"node_id\":\"826014\",\"node_name\":\"\u65b0\u624b\u573a\u6b21\u6570\u636e\",\"is_leaf\":\"0\"},{\"node_id\":\"810129\",\"node_name\":\"\u6d4b\u8bd5\u56fe\",\"is_leaf\":\"0\"},{\"node_id\":\"765610\",\"node_name\":\"\u73a9\u5bb6\u6d4f\u89c8\u5668\u5185\u6838\u68c0\u6d4b\",\"is_leaf\":\"1\"},{\"node_id\":\"760313\",\"node_name\":\"5v5\u4eba\u673a\",\"is_leaf\":\"0\"},{\"node_id\":\"760280\",\"node_name\":\"5v5\u4eba\u4eba\",\"is_leaf\":\"0\"},{\"node_id\":\"759152\",\"node_name\":\"\u767b\u5f55\u6e38\u620f\",\"is_leaf\":\"0\"},{\"node_id\":\"827572\",\"node_name\":\"\u8001\u6570\u636e\",\"is_leaf\":\"0\"},{\"node_id\":\"826012\",\"node_name\":\"\u65b0\u624b\u573a\u6b21\u6570\u636e\uff08\u5f53\u65e5\uff09\",\"is_leaf\":\"0\"},{\"node_id\":\"756650\",\"node_name\":\"\u4e3b\u754c\u9762\",\"is_leaf\":\"0\"},{\"node_id\":\"872185\",\"node_name\":\"\u4e71\u7801\",\"is_leaf\":\"0\"}]}");
		} else if(request.getParameter("parent_id").equals("913520")){
			printWriter.write("{\"result\":0,\"data\":[{\"node_id\":\"914927\",\"node_name\":\"\u70b9\u51fb\u5408\u6210\",\"is_leaf\":\"1\"},{\"node_id\":\"913537\",\"node_name\":\"\u53bb\u83b7\u5f97\u5f39\u51fa\",\"is_leaf\":\"1\"},{\"node_id\":\"913536\",\"node_name\":\"\u70b9\u51fb\u7b26\u6587\u6846\u8fdb\u884c\u5207\u6362\u6216\u5378\u4e0b\",\"is_leaf\":\"1\"},{\"node_id\":\"913529\",\"node_name\":\"\u70b9\u51fb\u53bb\u83b7\u5f97\",\"is_leaf\":\"1\"},{\"node_id\":\"913526\",\"node_name\":\"\u70b9\u51fb\u5de6\u4fa7\u7b26\u6587\u680f\u8fdb\u884c\u653e\u5165\",\"is_leaf\":\"1\"},{\"node_id\":\"913521\",\"node_name\":\"\u70b9\u51fb\u8fdc\u53e4\u7b26\u6587\",\"is_leaf\":\"1\"}]}");
		} else {
			printWriter.write("{\"result\":0,\"data\":[]}");
		}*/
		
		JSONArray result = getTreePrivate(request);
		
		System.out.println("result:" + result.toString());
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		printWriter.flush();
	}
	
	
	@RequestMapping(value = "/getManageTree")
	public void getManageTree(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		JSONArray result = getTreePrivate(request);
		
		if(request.getParameter("parent_id").equals("0")) {
			JSONObject treeObj = new JSONObject();
			treeObj.put("node_id", -1);
			treeObj.put("node_name", "回收站");
			treeObj.put("is_leaf", 1);
			result.add(0, treeObj);
		}
		
		System.out.println("result:" + result.toString());
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		printWriter.flush();
	}

	@RequestMapping(value = "/search")
	public void search(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String gameString = request.getParameter("game_id");
		String keyWord = request.getParameter("keyword");
		try {
			keyWord = new String(keyWord.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("game_id:" + gameString + " keyWord:" + keyWord);
		
		if(gameString == null || keyWord == null 
				|| gameString.isEmpty() || keyWord.isEmpty()) {
			printWriter.write("{\"result\":0,\"data\":[]}");
		} else { 
			List<TreeInfo> treeList = logMgrService.searchNode(Integer.parseInt(gameString), keyWord);
			System.out.println("treeList:" + treeList);
			List<Integer> nodeList = new ArrayList<Integer>();
			for(TreeInfo treeInfo: treeList) {
				nodeList.add(treeInfo.getNodeId());
			}
			printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(nodeList) + "}");
		}
		
		//printWriter.write("{\"result\":0,\"data\":[760313,746872,756650,760280,826022,826133,827579,847977,875896,878690,882003,913520,810129,875896]}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "/addNode")
	public void addNode(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String parentString = request.getParameter("parent_id");
		String gameString = request.getParameter("game_id");
		String name = request.getParameter("name");
		if(parentString == null || gameString == null || name == null) {
			return;
		}
		
		Integer parentId = Integer.parseInt(parentString);
		Integer gameId = Integer.parseInt(gameString);
		TreeInfo treeInfo = setTreeInfo(name, gameId, parentId);
		System.out.println("parentID:" + parentId + " game_id:" + gameId + " name:" + name);			
		
		TreeInfo resultInfo = logMgrService.addTreeNode(treeInfo);
		if(resultInfo ==  null) {
			return;
		}
		
		JSONObject result = treeInfoToObj(resultInfo);
		
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "/delNode")
	public void delNode(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		System.out.println("delNode:" + request.getParameter("id"));
		String idsString = request.getParameter("id");
		String gameString = request.getParameter("game_id");
		if(idsString == null || gameString == null 
				|| idsString.isEmpty() || gameString.isEmpty()) {
			return;
		}
		
		Integer gameId = Integer.parseInt(gameString);
		
		String[] idStringList = idsString.split("_");
		List<Integer> idList = new ArrayList<Integer>();
		for(String id :idStringList) {
			if(id == null || id.isEmpty()) {
				break;
			}
			
			idList.add(Integer.parseInt(id));
		}
		
		System.out.println("delNode  idList:" +idList);
		logMgrService.delTreeNode(idList, gameId);
		
		printWriter.write("{\"result\":0,\"data\":" + "null" + "}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "mergeNode")
	public void mergeNode(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		System.out.println("mergeNode:" + request.getParameter("id"));
		String idsString = request.getParameter("id");
		String gameString = request.getParameter("game_id");
		if(idsString == null || gameString == null 
				|| idsString.isEmpty() || gameString.isEmpty()) {
			return;
		}
		
		Integer gameId = Integer.parseInt(gameString);
		Integer nodeId = Integer.parseInt(idsString);
		
		TreeInfo resultInfo = logMgrService.mergeTreeNode(nodeId, gameId);
		JSONObject result = treeInfoToObj(resultInfo);
		
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "moveNode")
	public void moveNode(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		System.out.println("moveNode:" + request.getParameter("id"));
		String idsString = request.getParameter("id");
		String gameString = request.getParameter("game_id");
		String parentString = request.getParameter("parent_id");
		String afterString = request.getParameter("after_id");
		if(idsString == null || gameString == null || parentString == null || afterString == null 
				|| idsString.isEmpty() || gameString.isEmpty() || parentString.isEmpty() || afterString.isEmpty()) {
			return;
		}
		
		Integer gameId = Integer.parseInt(gameString);
		Integer nodeId = Integer.parseInt(idsString);
		Integer parentId = Integer.parseInt(parentString);
		Integer afterId = Integer.parseInt(afterString);
		
		System.out.println("gameId:" + gameId + " nodeId:" + nodeId + " parentId:" + parentId + " afterId:" + afterId);
		logMgrService.moveTreeNode(gameId, nodeId, parentId, afterId);
		printWriter.write("{\"result\":0,\"data\":" + "null" + "}");
		printWriter.flush();
	}
	
	
	@RequestMapping(value = "moveStatItem")
	public void moveStatItem(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		String idsString = request.getParameter("id");
		String gameString = request.getParameter("game_id");
		String parentString = request.getParameter("parent_id");
		if(idsString == null || gameString == null || parentString == null 
				|| idsString.isEmpty() || gameString.isEmpty() || parentString.isEmpty() ) {
			return;
		}
		
		Integer gameId = Integer.parseInt(gameString);
		Integer parentId = Integer.parseInt(parentString);
		List<Integer> nodeIdList = new ArrayList<Integer>();
		if(isNumeric(idsString)) {
			nodeIdList.add(Integer.parseInt(idsString));
		} else {
			String[] tmp = idsString.split("_");
			for(int i = 0; i < tmp.length; i ++) {
				if(isNumeric(tmp[i])) {
					nodeIdList.add(Integer.parseInt(tmp[i]));
				}
			}
		}
		
		System.out.println("gameId:" + gameId + " nodeId:" + nodeIdList + " parentId:" + parentId);
		logMgrService.moveStatItem(gameId, nodeIdList, parentId);
		printWriter.write("{\"result\":0,\"data\":" + "null" + "}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "/setName")
	public void setName(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String gameString = request.getParameter("game_id");
		String idString = request.getParameter("id");
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		
		if(gameString == null || idString == null || name == null || type == null) {
			printWriter.write("{\"result\":0,\"data\":" + "null" + "}");
			printWriter.flush();
			return;
		}
		
		
		TreeInfo treeInfo = new TreeInfo();
		treeInfo.setGameId(Integer.parseInt(gameString));
		treeInfo.setNodeId(Integer.parseInt(idString));
		treeInfo.setNodeName(name);
		logMgrService.updateNodeName(treeInfo);
		
		printWriter.write("{\"result\":0,\"data\":" + "null" + "}");
		printWriter.flush();	
	}
	
	private JSONArray getTreePrivate(HttpServletRequest request) {
		JSONArray result = new JSONArray();
		String parentString = request.getParameter("parent_id");
		String gameString = request.getParameter("game_id");
		System.out.println("parent_id:" + parentString +" game_id:" + gameString);
		
		if(gameString == null || parentString == null 
				|| gameString.isEmpty() || gameString.isEmpty()) {
			return result;
		}
		
		Integer parentId = Integer.parseInt(parentString);
		Integer gameId = Integer.parseInt(gameString);
		
		List<TreeInfo> treeList = logMgrService.getTreeInfosByGameIdParentId(gameId, parentId);
		System.out.println("tree list size:"+ treeList.size());
		for(TreeInfo treeInfo: treeList) {
			JSONObject treeObj = new JSONObject();
			treeObj.put("node_id", treeInfo.getNodeId());
			treeObj.put("node_name", treeInfo.getNodeName());
			treeObj.put("is_leaf", treeInfo.getIsLeaf());
			result.add(treeObj);
		}
		return result;
	}
	
	private TreeInfo setTreeInfo(String name, Integer gameId, Integer parentId) {
		TreeInfo treeInfo = new TreeInfo();
		treeInfo.setNodeName(name);
		treeInfo.setParentId(parentId);
		treeInfo.setGameId(gameId);
		treeInfo.setIsBasic(0);
		treeInfo.setHide(0);
		treeInfo.setStatus(0);
		treeInfo.setDisplayOrder(0);
		return treeInfo;
	}
	
	private JSONObject treeInfoToObj(TreeInfo info) {
		JSONObject result = new JSONObject();
		result.put("game_id", info.getGameId());
		result.put("is_basic", info.getIsBasic());
		result.put("is_leaf", info.getIsLeaf());
		result.put("node_id", info.getNodeId());
		result.put("node_name", info.getNodeName());
		result.put("parent_id", info.getParentId());
		return result;
	}
	
	private boolean isNumeric(String str){
		  for (int i = str.length();--i>=0;){   
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		 }
}
