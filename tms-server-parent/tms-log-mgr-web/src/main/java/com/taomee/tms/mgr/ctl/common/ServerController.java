package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.LogInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.form.LogForm;
import com.taomee.tms.mgr.form.ServerForm;

@Controller
@RequestMapping("/common/server")
public class ServerController {
	private static final Logger logger = LoggerFactory
			.getLogger(CommentController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;

	@RequestMapping(value = "/index/{id}")
	public String index(Model model) {
		List<ServerInfo> servers = new ArrayList<ServerInfo>();
		servers = logMgrService.getAllServerInfos();
		model.addAttribute("servers", servers);
		return "conf/server";
	}

	@RequestMapping(value = "/save")
	public void save(ServerForm form, PrintWriter printWriter) {
		System.out.println("ServerController save :" + JSON.toJSONString(form));

		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setServerName(form.getServerName());
		serverInfo.setGameId(form.getGameId());
		serverInfo.setParentId(form.getParentId());
		serverInfo.setIsLeaf(form.getIsLeaf());
		serverInfo.setStatus(form.getStatus());

		if (form.getServerId() == null) {
			serverInfo.setServerId(0);
		} else {
			serverInfo.setServerId(form.getServerId());
		}

		Integer serverId = serverInfo.getServerId();
		if (serverId == 0) {
			try {
				serverId = logMgrService.insertServerInfo(serverInfo);
			} catch (Exception e) {
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"result\":1}");
				printWriter.flush();
				printWriter.close();
			}
		} else {
			try{
				System.out.print("server info update, serverInfo: "+serverInfo.toString()+"\n");
				logMgrService.updateServerInfo(serverInfo);
			}catch(Exception e) {
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"reuslt\":1}");
				printWriter.flush();
				printWriter.close();
			}
			
		}

		printWriter.write("{\"result\":0,\"data\":" + "{\"server\":"
				+ serverInfo.toString() + "}" + "}");
		printWriter.flush();
		printWriter.close();
	}

	@RequestMapping(value = "/delete")
	public void delete(ServerInfo form, PrintWriter printWriter) {
		System.out.println("ServerController delete :"
				+ JSON.toJSONString(form));

		int result = 0;
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setServerName(form.getServerName());
		serverInfo.setGameId(form.getGameId());
		serverInfo.setParentId(form.getParentId());
		serverInfo.setIsLeaf(form.getIsLeaf());
		serverInfo.setStatus(form.getStatus());
		Integer serverId = form.getServerId();
		
		if (serverId == null || serverId == 0) {
			// 非0,参数错误
			result = 1;
		}else{
			serverInfo.setServerId(serverId);
			if(logMgrService.updateServerInfoByStatus(serverInfo) == 0){
				System.out.println("updateServerInfoByStatus: "+serverInfo.toString());
				result = 0;
			}
			result = 1;
		}

		// TODO 返回值需要修改
		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}
}
