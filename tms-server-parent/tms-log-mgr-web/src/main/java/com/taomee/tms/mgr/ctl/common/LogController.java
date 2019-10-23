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
import com.taomee.tms.mgr.form.LogForm;

@Controller
@RequestMapping("/common/log")
public class LogController {
	private static final Logger logger = LoggerFactory
			.getLogger(LogController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;

	// 获取所有的评论
	@RequestMapping(value = "/index/{id}")
	public String index(Model model) {
		List<LogInfo> logs = new ArrayList<LogInfo>();
		logs = logMgrService.getLogInfos();
		// ----------------------
		/*
		 * LogInfo a1 = new LogInfo(); a1.setLogId(1); a1.setLogName("test1");
		 * a1.setType(0); logs.add(a1);
		 * 
		 * LogInfo a2 = new LogInfo(); a2.setLogId(2); a2.setLogName("test2");
		 * a2.setType(1); logs.add(a2);
		 */
		// ---------------------------
		model.addAttribute("logs", logs);
		return "conf/log";
	}

	@RequestMapping(value = "/save")
	public void save(LogForm form, PrintWriter printWriter) {
		System.out.println("LogController save :" + JSON.toJSONString(form));

		LogInfo logInfo = new LogInfo();
		logInfo.setLogName(form.getLogName());
		logInfo.setType(form.getType());
		logInfo.setStatus(form.getStatus());
		if (form.getLogId() == null) {
			logInfo.setLogId(0);
		} else {
			System.out.println(form.getLogId());
			logInfo.setLogId(Integer.parseInt(form.getLogId()));
		}

		Integer logId = logInfo.getLogId();
		if (logId == 0) {
			try {
				logId = logMgrService.insertLogInfo(logInfo);
			} catch (Exception e) {
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"reuslt\":1}");
				printWriter.flush();
				printWriter.close();
			}
		} else {
			logMgrService.updateLogInfo(logInfo);
		}

		printWriter.write("{\"result\":0,\"data\":" + "{\"logId\":"
				+ logId.toString() + "}" + "}");
		printWriter.flush();
		printWriter.close();
	}

	@RequestMapping(value = "/delete")
	public void delete(LogForm form, PrintWriter printWriter) {
		System.out.println("LController delete :" + JSON.toJSONString(form));

		int result = 0;
		LogInfo logInfo = new LogInfo();
		String strLogId = form.getLogId();
		logInfo.setLogName(form.getLogName());
		logInfo.setType(form.getType());
		logInfo.setStatus(form.getStatus());

		if (strLogId == null || Integer.parseInt(strLogId) == 0) {
			// 非0,参数错误
			result = 1;
		} else {
			logInfo.setLogId(Integer.parseInt(form.getLogId()));
			logMgrService.updateLogInfoByStatus(logInfo);
			result = 0;
		}
		// TODO 返回值需要修改
		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}
}
