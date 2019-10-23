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
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.form.SchemaForm;

@Controller
@RequestMapping("/common/schema")

public class SchemaController {
	private static final Logger logger = LoggerFactory
			.getLogger(CommentController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	@RequestMapping(value = "/index/{id}")
	public String index(Model model) {
		List<SchemaInfo> schemas = new ArrayList<SchemaInfo>();
		schemas = logMgrService.getSchemaInfos();
		//System.out.print(schemas.toString());
		model.addAttribute("schemas", schemas);
		return "conf/schema";
	}
	
	@RequestMapping(value = "/save")
	public void save(SchemaForm form, PrintWriter printWriter) {
		System.out.println("SchemaController save :" + JSON.toJSONString(form));
		
		SchemaInfo schemaInfo = new SchemaInfo();
		schemaInfo.setLogId(form.getLogId());
		schemaInfo.setOp(form.getOp());
		schemaInfo.setCascadeFields(form.getCascadeFields());
		schemaInfo.setMaterialId(form.getMaterialId());
		
		if(form.getMaterialName() != null){
			schemaInfo.setMaterialName(form.getMaterialName());
		}
		if(form.getSchemaId() == null){
			schemaInfo.setSchemaId(0);
		} else {
			schemaInfo.setSchemaId(Integer.parseInt(form.getSchemaId()));
		}
		
		Integer schemaId = schemaInfo.getSchemaId();
		//Integer materialId = schemaInfo.getMaterialId();
		if (schemaId == 0) {
			try {
				//System.out.print("schema info insert\n");
				schemaId = logMgrService.insertSchemaInfo(schemaInfo);
			} catch(Exception e) {
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"reuslt\":1}");
				printWriter.flush();
				printWriter.close();
			}
		} else {
			try {
				System.out.print("schema info update, schemaInfo: "+schemaInfo.toString()+"\n");
				logMgrService.updateSchemaInfo(schemaInfo);
			} catch(Exception e) {
				logger.error("MySQL error" + e.getMessage());
				printWriter.write("{\"reuslt\":1}");
				printWriter.flush();
				printWriter.close();
			}
		}
		
		printWriter.write("{\"result\":0,\"data\":" + "{\"schemaId\":" + schemaId.toString() + "}" + "}");
		//printWriter.write("{\"result\":0,\"data\":" + "{\"schemaId\":" + "1" + "}" + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/delete")
	public void delete(SchemaForm form, PrintWriter printWriter) {
		System.out.println("SchemaController delete :"
				+ JSON.toJSONString(form));
		
		int result = 0;
		SchemaInfo schemaInfo = new SchemaInfo();
		String strSchemaId = form.getSchemaId();
		schemaInfo.setLogId(form.getLogId());
		schemaInfo.setOp(form.getOp());
		schemaInfo.setCascadeFields(form.getCascadeFields());
		schemaInfo.setMaterialId(form.getMaterialId());
		if(form.getMaterialName() != null){
			schemaInfo.setMaterialName(form.getMaterialName());
		}
		
		if(strSchemaId == null || Integer.parseInt(strSchemaId) == 0){
			// 非0,参数错误
			result = 1;
		}else{
			schemaInfo.setSchemaId(Integer.parseInt(strSchemaId));
			logMgrService.updateSchemaInfoByStatus(schemaInfo);
			result = 0;
		}
		
		// TODO 返回值需要修改
		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}
}
