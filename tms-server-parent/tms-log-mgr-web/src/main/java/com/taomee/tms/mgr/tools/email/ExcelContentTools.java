package com.taomee.tms.mgr.tools.email;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.tools.DateTools;
import com.taomee.tms.mgr.tools.GenericPair;

public class ExcelContentTools extends DefaultContentTools{
	private DateTools dateTool;
	private DataResultTools getDataTool;
	private List<EmailDataInfo> emailDataList;
	private Boolean messageFlag;
	private Integer mOffer = -120;
	private String title;
	private List<GenericPair<String, List<String>>> body;
	
	public ExcelContentTools(List<EmailDataInfo> emailDataList, DataResultTools getDataTool, String title) {
		this.emailDataList = emailDataList;
		this.messageFlag = false;
		
		this.dateTool = new DateTools();
		this.getDataTool= getDataTool;
		this.title = title;
		this.body = new LinkedList<GenericPair<String, List<String>>>();
	}

	@Override
	public String getMessageBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getMessagePic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GenericPair<String, List<String>>> getExcelData() {
		// TODO Auto-generated method stub
		setMessage();
		
		return this.body;
	}
	
	
	private void setMessage() {
		if(messageFlag) {
			System.out.println("MessageFlag is true!");
			return;
		}
		
		this.body.clear();
		
		for(EmailDataInfo emailInfo:emailDataList) {
			List<String> timeList = getYearTimeList(emailInfo.getdataDateType(),mOffer);
			if(timeList == null || timeList.isEmpty()) {
				break;
			}
			
			String expre = emailInfo.getdataExpr();
			if(expre == null || expre.isEmpty()) {
				continue;
			}
			List<DataResultInfo> tmpList = getDataTool.getContrastDataList(getDataList(expre), emailInfo.getServerId(), timeList, 2, getExpre(expre));
			if(tmpList == null || tmpList.isEmpty()) {
				continue;
			}
			List<String> values = new ArrayList<String>();
			List<String> contrasts = new ArrayList<String>();
			//System.out.println("!!!tmpList size:" + tmpList.size());
			for(int i = 0; i < tmpList.size()-1; i++) {
				Double value = tmpList.get(i).getDataValue();
				if(value == null) {
					values.add("");
					contrasts.add("");
					continue;
				}
				
				values.add(value.toString());
				Double nextValue = tmpList.get(i+1).getDataValue();
				if(nextValue == null) {
					contrasts.add("");
					continue;
				}
				String contrast = getDataTool.getContrast(value, nextValue);
				contrasts.add(contrast);
			}
			
			GenericPair<String, List<String>> pairValue = new GenericPair<String, List<String>>();
			pairValue.setFirst(emailInfo.getContentTitle()+"|"+emailInfo.getdataName());
			pairValue.setSecond(values);
			this.body.add(pairValue);
			
			GenericPair<String, List<String>> pairContrast = new GenericPair<String, List<String>>();
			pairContrast.setFirst(emailInfo.getContentTitle()+"|"+emailInfo.getdataName() + "增长率");
			pairContrast.setSecond(contrasts);
			this.body.add(pairContrast);
			
			this.messageFlag = true;
			
		}
		
	}

}
