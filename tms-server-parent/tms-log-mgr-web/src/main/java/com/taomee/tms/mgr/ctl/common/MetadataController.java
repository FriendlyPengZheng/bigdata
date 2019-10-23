package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.MetadataUrl;
import com.taomee.tms.mgr.entity.Metadata;
import com.taomee.tms.mgr.form.MetadataForm;
import com.taomee.tms.mgr.tools.IntegerTools;

@Controller
@RequestMapping("/common/basicdata")
public class MetadataController {
	private static final Logger logger = LoggerFactory
			.getLogger(MetadataController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;

	// 获取所有的元数据
	@RequestMapping(value = "/index/{id}")
	public String index(Model model) {
		List<Metadata> metadatas = null;

		try {
			metadatas = logMgrService.getAllMetadataInfos();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		System.out.println(JSON.toJSONString(metadatas));
		model.addAttribute("metadatas", metadatas);
		return "conf/metadata";
	}

	@RequestMapping(value = "/save")
	public void save(MetadataForm form, PrintWriter printWriter) {
		System.out.println("MetadataController save :"
				+ JSON.toJSONString(form));

		int result = 1;

		Metadata metadata = new Metadata();
		do {
			String metadataName = form.getMetadataName();
			if (metadataName == null || metadataName.equals("")) {
				System.out.println("empty metadataName");
				break;
			}

			metadata.setMetadataName(metadataName);

			metadata.setMetadataId(IntegerTools.safeStringToInt(form
					.getMetadataId()));
			metadata.setDataId(IntegerTools.safeStringToInt(form.getDataId()));
			metadata.setPeriod(IntegerTools.safeStringToInt(form.getPeriod()));
			// TODO check
			metadata.setFactor(Float.valueOf(form.getFactor()));
			metadata.setPrecision(IntegerTools.safeStringToInt(form
					.getPrecision()));
			metadata.setUnit(form.getUnit());
			metadata.setCommentId(IntegerTools.safeStringToInt(form
					.getCommentId()));
			metadata.setComment(form.getComment());

			// TODO validator 检验form
			//
			// if (form.getMetadataId() == null) {
			// metadata.setMetadataId(0);
			// } else {
			// metadata.setMetadataId(Integer.parseInt(form.getMetadataId()));
			// }

			logger.info("metadata is " + JSON.toJSONString(metadata));

			try {
				if (metadata.getMetadataId() == 0) {
					// 新建
					// TODO 异常控制
					logMgrService.insertMetadataInfo(metadata);
				} else {
					// 更新
					// TODO 异常控制
					logMgrService.updateMetadataInfo(metadata);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			result = 0;
		} while (false);

		// Metadata data = new Metadata();
		// data.setMetadataId();
		// data.setMetadataName(form.getMetadataName());
		// data.setDataId(1);
		// data.setPeriod(1);
		// data.setFactor(1);
		// data.setPrecision(2);
		// data.setUnit("%");
		// data.setComment("评论1");

		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}

	@RequestMapping(value = "/delete")
	public void delete(MetadataForm form, PrintWriter printWriter) {
		System.out.println("MetadataController delete :"
				+ JSON.toJSONString(form));

		int result = 1;

		Metadata metadata = new Metadata();
		int metadataId = Integer.parseInt(form.getMetadataId());
		metadata.setMetadataId(metadataId);

		if (metadataId > 0) {
			// TODO 处理异常
			logMgrService.deleteMetadataInfo(metadata);
			result = 0;
		}

		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}

	@RequestMapping(value = "/getMetadataListGroupByPeriod")
	public void getMetadataListGroupByPeriod(PrintWriter printWriter, HttpServletResponse response) {
		// printWriter.write("{\"result\":0,\"data\":{\"1\":[{\"data_name\":\"付费用户数\",\"url\":\"data_name=付费用户数&type=1&stid=_acpay_&sstid=_acpay_&op_fields=&op_type=ucount&range=&period=1&factor=1&precision=0&unit=\"}, {\"data_name\":\"付费次数\",\"url\":\"data_name=付费次数&type=1&stid=_acpay_&sstid=_acpay_&op_fields=&op_type=count&range=&period=1&factor=1&precision=0&unit=\"}],\"2\":[{data_name: \"新增设备数\",url: \"data_name=新增设备数&type=2&task_id=3&range=&period=2&factor=1&precision=0&unit=\"},{data_name: \"新建角色用户数\",url: \"data_name=新建角色用户数&type=2&task_id=5&range=&period=2&factor=1&precision=0&unit=\"}]}}");
		// printWriter.write("{\"result\":0,\"data\":{\"1\":[{\"data_name\":\"\u4ed8\u8d39\u7528\u6237\u6570\"},{\"data_name\":\"\u4ed8\u8d39\u7528\u6237\u6570\"}],\"2\":[{data_name: \"\u4ed8\u8d39\u7528\u6237\u6570\"},{data_name: \"\u4ed8\u8d39\u7528\u6237\u6570\"}]}}");

		// TODO 测试参数有误
		// printWriter.write("{\"result\":0,\"data\":[{\"commentId\":\"1\",\"keyword\":\"\u4ed8\u8d39\u7528\u6237\u6570\",\"comment\":\"\u6709\u4ed8\u8d39\u884c\u4e3a\u7684\u7c73\u7c73\u53f7\u6570\"},{\"commentId\":\"2\",\"keyword\":\"\u6536\u5165\u603b\u989d\",\"comment\":\"\u4ed8\u8d39\u603b\u91d1\u989d\uff0c\u4e3a\u6d41\u6c34\u6536\u5165\"}]}");

		// List<Metadata> metadatas = new ArrayList<Metadata>();
		// Metadata m1 = new Metadata();
		// m1.setMetadataId(1);
		// m1.setMetadataName("元数据1");
		// m1.setDataId(1);
		// m1.setPeriod(1);
		// m1.setFactor(1);
		// m1.setPrecision(2);
		// m1.setUnit("%");
		// m1.setComment("评论1");
		// metadatas.add(m1);
		// Metadata m2 = new Metadata();
		// m2.setMetadataId(2);
		// m2.setMetadataName("元数据2");
		// m2.setDataId(2);
		// m2.setPeriod(2);
		// m2.setFactor(10);
		// m2.setPrecision(0);
		// m2.setUnit("元");
		// m2.setComment("评论2");
		// metadatas.add(m2);
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式

		List<Metadata> metadatas = logMgrService.getAllMetadataInfos();

//		Map<Integer, List<Metadata>> map = new HashMap<Integer, List<Metadata>>();
//
//		Iterator<Metadata> iter = metadatas.iterator();
//		while (iter.hasNext()) {
//			Metadata metadata = iter.next();
//			Integer period = metadata.getPeriod();
//
//			if (!map.containsKey(period)) {
//				map.put(period, new ArrayList<Metadata>());
//			}
//
//			map.get(period).add(metadata);
//		}
		
		// 遍历所有元数据
		// 狗杂一个data_name 和 url
		
		
		Map<String, List<Metadata>> map = new HashMap<String, List<Metadata>>();
		Map<String, List<MetadataUrl>> urlmap = new HashMap<String, List<MetadataUrl>>();
		//aList = $model->getPeriodGroupedList();
		Iterator<Metadata> iter = metadatas.iterator();
		while (iter.hasNext()) {
			Metadata metadata = iter.next();
			
			MetadataUrl metadataUrl = new MetadataUrl();
			String data_name = metadata.getMetadataName();
			metadataUrl.setMetadataName(data_name);
			String url = "data_name=" + metadata.getMetadataName() + "&" + "dataid=" + metadata.getDataId() + "&" + "period=" + metadata.getPeriod() + "&" + "factor=" + metadata.getFactor() + "&" + "precision=" + metadata.getPrecision() + "&" + "unit=" + metadata.getUnit();  
			metadataUrl.setUrl(url);
			// JSON不允许Integer作为key，必须是String
			
			String period = metadata.getPeriod().toString();

			if (!map.containsKey(period)) {
				map.put(period, new ArrayList<Metadata>());
				urlmap.put(period,new ArrayList<MetadataUrl>());
			}
			map.get(period).add(metadata);
			urlmap.get(period).add(metadataUrl);
		}

		// printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(map) + "}");
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(urlmap) + "}");
//		printWriter.write("{\"result\":0}");

		printWriter.flush();
		printWriter.close();
		// TODO return 一个页面
		// model.addAttribute("metadatas", metadatas);
		// return "conf/metadata";
		
	}
}
