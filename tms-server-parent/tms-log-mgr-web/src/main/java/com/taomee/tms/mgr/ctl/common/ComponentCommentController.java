package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javassist.expr.NewArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.taomee.tms.mgr.api.LogMgrService;
//import com.taomee.tms.mgr.ctl.LogMgrController;
import com.taomee.tms.mgr.entity.Comment;
import com.taomee.tms.mgr.entity.CommentByComponentId;
import com.taomee.tms.mgr.entity.Component;
import com.taomee.tms.mgr.form.CommentForm;
import com.taomee.tms.mgr.form.CommentQueryForm;
import com.taomee.tms.mgr.form.Outer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


@Controller
@RequestMapping("/common/componentcomment")
public class ComponentCommentController {
	private static final Logger logger = LoggerFactory
			.getLogger(ComponentCommentController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;

	// 获取所有的评论
	@RequestMapping(value = "/index")
	public String index(Model model) {
		List<Comment> comments = new ArrayList<Comment>();
		// Comment c1 = new Comment();
		// c1.setCommentId(1);
		// c1.setKeyword("keyword1");
		// c1.setComment("Comment1");
		// comments.add(c1);
		// Comment c2 = new Comment();
		// c2.setCommentId(2);
		// c2.setKeyword("keyword2");
		// c2.setComment("Comment2");
		// comments.add(c2);
		
		//////////////////////////////////////////

		//String jsonString = "{\"outerAttrInteger\":1,\"outerAttrString\":\"hello\",\"outerAttrInner\":{\"innerAttrInteger\":2,\"innerAttrString\":\"world\"},\"outerAttrInnerList\":[{\"innerAttrInteger\":3,\"innerAttrString\":\"thierry\"}, {\"innerAttrInteger\":4,\"innerAttrString\":\"sevin\"}]}";
		//Outer outer = JSON.parseObject(jsonString, Outer.class);
		//System.out.println("outer is " + JSON.toJSONString(outer));
		//////////////////////////////////////////
		
		// TODO
		comments = logMgrService.getAllCommentInfos();
		model.addAttribute("comments", comments);
		return "conf/comment";
		
	}
	
	// TODO 
	@RequestMapping(value = "/save")
	public void save(HttpServletRequest request, HttpServletResponse response, PrintWriter printWriter) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
				
		Integer component_id = Integer.valueOf(request.getParameter("component_id"));
		
		// 依据commponentid取得对象
		List<Component> components = logMgrService.getComponentInfoByComponentId(component_id);
		
		if (!components.get(0).getComponentType().equals("wrap")) {
			// errro;
			logger.error("type" + components.get(0).getComponentType());
			
		} else {
		
			    // 去重，处理空值
				String [] commentIdStrings = request.getParameterValues("comment_id[]");
				//Integer [] commentIdIntegers = new Integer[commentIdStrings.length];
				for (int i = 0; i < commentIdStrings.length; ++i) {
					//System.out.println(commentIdStrings[i]);
					
					
					
					Integer comment_id = Integer.valueOf(commentIdStrings[i]);
					System.out.println("component_id is:" + component_id + "comment_id" + comment_id);
					try {
						logMgrService.saveComponentComments(component_id, comment_id);
					} catch(Exception e){
						logger.error("mysql err"+e.getMessage());
					}
					
					
				}
		}
		
		// save
		
		
	
//		Map parametermap = request.getParameterMap();
		
		// 对map进行解析
//		Map resmap = new HashMap();
//		Iterator entries = parametermap.entrySet().iterator();
//		Map.Entry entry;
//		String key = "";
//		String value = "";
//		Integer comment_id = 0;
//		while (entries.hasNext()) {
//			entry = (Map.Entry) entries.next();
//			key = (String) entry.getKey();
//			System.out.println("key" + key);
//			Object valObject = entry.getValue();
//			if (null == valObject) {
//				value = "";
//			}else if(valObject instanceof String[]) {
//				//获取comment_id
//				String [] values = (String[]) valObject;
//				for(int i=0; i<values.length; i++){
//				
//				}
//				//System.out.println("value" + value);
//				
//			}else {
//				value = valObject.toString();
//				System.out.println("value" + value);
//			}
//		}
	
		// 获取component
		// 依据commponent的type来进行过滤
		// 进行数据库插入操作
		// TODO 判断插入失败的情况
			
 		//Component component = logMgrService.getCommentById(component_id);
 		
 		
//		
//		logMgrService.saveComponentcomments(Integer component_id, Integer comment_id);
		
		
		
		


		// TODO 返回值需要修改
		//printWriter.write("{\"result\":0}");
		printWriter.write("{\"result\":0,\"data\":" + null + "}");
		printWriter.flush();
		printWriter.close();
	}

//	// TODO 传参的方式需要改变下，这里只需要用到component_id, 后续传参考虑用表单实现
//	@RequestMapping(value = "/delete")
//	public void delete(HttpServletRequest request, PrintWriter printWriter) {
//		
//	    // 擦数只有一个component_id
//		int result = 1;
//		
//		Integer component_id = Integer.parseInt(request.getParameter("component_id"));
//		
//				
//		try {
//			logMgrService.deleteComponentInfos(component_id);
//			result = 0;
//		}catch(Exception e){
//			logger.error("mysql err" + e.getMessage());
//			
//		}
//		
//		// TODO 返回值需要修改
//		printWriter.write("{\"result\":" + result + "}");
//		printWriter.flush();
//		printWriter.close();
//	}

	@RequestMapping(value = "/getComments")
	public void getComments(HttpServletRequest request, HttpServletResponse response, PrintWriter printWriter) {
		//System.out.println("ComponentCommentController getComments CommentQueryForm :" + JSON.toJSONString(form));
		System.out.println("component_id is" + request.getParameter("component_id"));
		System.out.println("fetch_type is " + request.getParameter("fetch_type"));
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String fetch_type = request.getParameter("fetch_type");
		String component_id = request.getParameter("component_id");
		
		System.out.println(component_id);
		
		int result = 1;
		List<Comment> list = new ArrayList<Comment>();
		List<CommentByComponentId> list2 = new ArrayList<CommentByComponentId>();
		
		switch(fetch_type) {
		case "1":
			System.out.println("fetch_type == 1");
			//list = logMgrService.getCommentByComponentId(component_id);
			//先获取comment_id
			List<Component> components = logMgrService.getComponentInfoByComponentId(Integer.valueOf(component_id));
			if (components.size() == 0) {
				printWriter.write("{\"result\":" + result + "}");
				break;
			}
			result = 0;
			String properties = components.get(0).getProperties();
			System.out.println(properties);
			JSONObject jsonObj = JSON.parseObject(properties);
			JSONArray jsonArray = jsonObj.getJSONArray("process_list");
			//System.out.println(jsonArray);
			// 遍历jsonarray 取data_name
			for (int i = 0; i < jsonArray.size(); ++i) {
				JSONObject dataObject = jsonArray.getJSONObject(i);
				String data_name = dataObject.getString("data_name");
				List<Comment> list_tmp = logMgrService.getCommentByKeyword(data_name);
				list.addAll(list_tmp);
			}
			
			logger.info("ComponentCommentController getComment " + JSON.toJSONString(list));
			printWriter.write("{\"result\":" + result + ",\"data\":" + JSON.toJSONString(list) + "}");
			break;
		case "2":
			System.out.println("fecth_type == 2");
			list2 = logMgrService.getCommentsByComponentId(Integer.parseInt(component_id));
			if (list2.size() == 0) {
				result = 1;
				printWriter.write("{\"result\":" + result + "}");
				break;
			}
			result = 0;
			System.out.println(list2);
			logger.info("ComponentCommentController getComment " + JSON.toJSONString(list2));
			printWriter.write("{\"result\":" + result + ",\"data\":" + JSON.toJSONString(list2) + "}");
			break;
		}
		
//		result = 1;
//		
//		logger.info("ComponentCommentController getComment " + JSON.toJSONString(list2));
//		printWriter.write("{\"result\":1,\"data\":" + JSON.toJSONString(list2) + "}");
				
		//printWriter.flush();
		//printWriter.close();
	}

}
