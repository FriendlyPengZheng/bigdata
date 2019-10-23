package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.lf5.LogLevel;
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
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.Comment;
import com.taomee.tms.mgr.form.CommentForm;
import com.taomee.tms.mgr.form.CommentQueryForm;
import com.taomee.tms.mgr.form.Outer;


@Controller
@RequestMapping("/common/comment")
public class CommentController {
	private static final Logger logger = LoggerFactory
			.getLogger(CommentController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;

	// 获取所有的评论
	@RequestMapping(value = "/index/{id}")
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
		// comments.add(c2);`
		
		//////////////////////////////////////////

		String jsonString = "{\"outerAttrInteger\":1,\"outerAttrString\":\"hello\",\"outerAttrInner\":{\"innerAttrInteger\":2,\"innerAttrString\":\"world\"},\"outerAttrInnerList\":[{\"innerAttrInteger\":3,\"innerAttrString\":\"thierry\"}, {\"innerAttrInteger\":4,\"innerAttrString\":\"sevin\"}]}";
		Outer outer = JSON.parseObject(jsonString, Outer.class);
		System.out.println("outer is " + JSON.toJSONString(outer));
		//////////////////////////////////////////
		
		// TODO
		comments = logMgrService.getAllCommentInfos();
		model.addAttribute("comments", comments);
		return "conf/comment";
	}

	@RequestMapping(value = "/save")
	public void save(CommentForm form, PrintWriter printWriter) {
		System.out.println("CommentController save :" + JSON.toJSONString(form));
		
		Integer comment_id = 0;

		Comment comment = new Comment();

		comment.setKeyword(form.getKeyword());
		comment.setComment(form.getComment());

		if (form.getCommentId() == null) {
			comment.setCommentId(0);
		} else {
			comment.setCommentId(Integer.parseInt(form.getCommentId()));
		}
		
		// TODO 异常处理机制
		if (comment.getCommentId() == 0) {
			try {
				comment_id = logMgrService.insertCommentInfo(comment);
			} catch(Exception e) {
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"reuslt\":1}");
				printWriter.flush();
				printWriter.close();
			}
		} else {
			logMgrService.updateCommentInfo(comment);
		}

		// TODO 返回值需要修改
		printWriter.write("{\"result\":0,\"data\":" + "{\"commentId\":" + comment_id.toString() + "}" + "}");
		printWriter.flush();
		printWriter.close();
	}

	@RequestMapping(value = "/delete")
	public void delete(CommentForm form, PrintWriter printWriter) {
		System.out.println("CommentController delete :"
				+ JSON.toJSONString(form));

		int result = 0;

		String strCommentId = form.getCommentId();
		if (strCommentId == null || Integer.parseInt(strCommentId) == 0) {
			// 非0,参数错误
			result = 1;
		}

		int commentId = Integer.parseInt(strCommentId);
		if (commentId == 0) {
			result = 1;
		} else {
			Comment comment = new Comment();
			comment.setCommentId(commentId);
			logMgrService.deleteCommentInfo(comment);
			// TODO
			result = 0;
		}

		// TODO 返回值需要修改
		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}

	@RequestMapping(value = "/getComments")
	public void getComments(CommentQueryForm form, PrintWriter printWriter) {
		System.out.println("CommentController getComments CommentQueryForm :"
				+ JSON.toJSONString(form));

		int result = 0;
		List<Comment> list = new ArrayList<Comment>();
		
		// ajax可能传一个null,需要做下判断
//		if (null == form) {
//			System.out.println("null");
//			result = 1;
//			list = logMgrService.getAllCommentInfos();
//			// TODO 返回值需要修改
//			logger.info("CommentController getComment " + JSON.toJSONString(list));
//			printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(list) + "}");
//			
//			printWriter.flush();
//			printWriter.close();
//			return;
//			
//		}
	
		switch (form.getFetchType()) {
		case "1": // 关键词精确匹配
			list = logMgrService.getCommentByKeyword(form.getKeyword());
			break;
		case "2": // 关键词模糊匹配
			list = logMgrService.getCommentByKeywordFuzzy(form.getKeyword());
			break;
		case "3": // 所有
		
		default:
			list = logMgrService.getAllCommentInfos();
		}
		
		
		result = 1;
		// TODO 返回值需要修改
		logger.info("CommentController getComment " + JSON.toJSONString(list));
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(list) + "}");
		
		printWriter.flush();
		printWriter.close();
	}
	
	

	
	
	
	// @RequestMapping(value = "/save")
	// @ResponseBody
	// public Map<String, String> save(CommentForm form){
	// Comment comment = new Comment();
	// comment.setKeyword(form.getKeyword());
	// comment.setComment(form.getComment());
	// // TODO 写入数据
	//
	// // printWriter.write("{\"result\":0,\"data\":\"user\"}");
	// // printWriter.flush();
	// // printWriter.close();
	// Map<String, String> map = new HashMap<String, String>(1);
	// map.put("result", "0");
	// return map;
	// }

}
