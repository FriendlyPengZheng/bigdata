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
import com.taomee.tms.mgr.form.TaskForm;
import com.taomee.tms.mgr.tools.IntegerTools;
import com.taomee.tms.mgr.entity.TaskInfo;

@Controller
@RequestMapping("/common/task")

public class TaskController {
	private static final Logger logger = LoggerFactory
			.getLogger(CommentController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	@RequestMapping(value = "/index/{id}")
	public String index(Model model) {
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		/*TaskInfo task = new TaskInfo();
		task.setTaskId(1);
		task.setTaskName("test");
		task.setOp("aaaaa");
		task.setPriority(1);
		task.setExecuteTime("bbbbbb");
		tasks.add(task);*/
		tasks = logMgrService.getTaskInfos();
		model.addAttribute("tasks", tasks);
		return "conf/task";
	}
	
	//period:0日 1周 2月 3版本周
	@RequestMapping(value = "/save")
	public void save(TaskForm form, PrintWriter printWriter) {
		System.out.println("TaskController save :" + JSON.toJSONString(form));
		
		TaskInfo task = new TaskInfo();
		task.setTaskName(form.getTaskName());
		task.setOp(form.getOp());
		task.setPeriod(IntegerTools.safeStringToInt(form.getPeriod()));
		task.setResult(IntegerTools.safeStringToInt(form.getResult()));
		task.setExecuteTime(form.getExecuteTime());
		System.out.println(form.getPriority());
		
		if(form.getPriority() == null) {
			task.setPriority(0);
		}else {
			task.setPriority(form.getPriority());
		}
		
		if(form.getTaskId() == null){
			task.setTaskId(0);
		} else {
			task.setTaskId(Integer.parseInt(form.getTaskId()));
		}
		
		Integer taskId = task.getTaskId();
		if(taskId == 0){
			try {
				taskId = logMgrService.insertTaskInfo(task);
			} catch(Exception e) {
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"reuslt\":1}");
				printWriter.flush();
				printWriter.close();
			}
		} else {
			try {
				logMgrService.updateTaskInfo(task);
			} catch(Exception e){
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"reuslt\":1}");
				printWriter.flush();
				printWriter.close();
			}
		}
		
		printWriter.write("{\"result\":0,\"data\":" + "{\"taskId\":" + taskId.toString() + "}" + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/delete")
	public void delete(TaskForm form, PrintWriter printWriter) {
		System.out.println("TaskController delete :"
				+ JSON.toJSONString(form));
		
		int result = 0;
		String strTaskId = form.getTaskId();
		if(strTaskId == null || Integer.parseInt(strTaskId) == 0){
			result = 1;
		} else {
			int taskId = Integer.parseInt(strTaskId);
			logMgrService.deleteTaskInfo(taskId);
			result = 0;
		}
		
		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}
}
