package com.taomee.tms.mgr.tools.excle;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.tools.DateTools;

public class ExcelConvertTools {
	private ExcelTreeNode root;
	private Integer width;
	private Map<Colour, String> colourMap = new HashMap<Colour,String>();
	
	
	public ExcelConvertTools() {
		root = new ExcelTreeNode("root", 0);
		width = 0;
		colourMap.put(Colour.YELLOW, "#fffca8");
		colourMap.put(Colour.BLUE, "#538dd5");
		colourMap.put(Colour.BLUE2, "#c5d9f1");
	}
	
	public Boolean addInfo(String path, List<String> dataList){
		if(path == null || path.isEmpty()) {
			return false;
		}
		String[] splitPath = path.split("\\|");
		
		ExcelTreeNode base = this.root;
		for(int i = 0; i < splitPath.length; i++) {
			String nodeName = splitPath[i];
			ExcelTreeNode child = base.getChild(nodeName);
			if(child == null) {
				child = new ExcelTreeNode(nodeName, base.getLevel() + 1);
				base.addChild(child);
			}
			base = child;
		}
		
		if(base.getLevel() == 3 && dataList!= null) {
			base.addExtreInfos(dataList);
		}
		
		return true;
	}
	
	public void writeToExcle(String Path, String sheetName) throws IOException, WriteException {
		ExcelTools tool = new ExcelTools();
		tool.open(Path);
		for (Map.Entry<Colour, String> entry : colourMap.entrySet()) {
			tool.setColour(entry.getKey(), entry.getValue());
		}  
		tool.setSheet(0, sheetName, this.convertToCell());
		
		WritableSheet sheet = tool.getWriteSheet(0);
		sheet.setColumnView(0, 37);
		sheet.setColumnView(1, 37);
		for(int i = 2; i < this.width; i++) {
			sheet.setColumnView(i, 19);
		}
		
		tool.write();
		tool.close();
		System.out.println("write success! Path["+Path+"]");
	}
	
	public List<CellInfo> convertToCell() throws WriteException {
		List<CellInfo> list = new LinkedList<CellInfo>();
		list = convert(new Point(0,0), this.root, null,null,list);
		return list;
	}
	
	private List<CellInfo> convert(Point location, ExcelTreeNode baseNode,Integer childId, Integer childListNumber,List<CellInfo> list) throws WriteException {
		this.width = location.x + 1 > this.width ? location.x + 1 : this.width;
		List<ExcelTreeNode> children = null;
		switch(baseNode.getLevel()) {
		case 0:
			children = baseNode.getChildren();
			break;
		case 1:
			if(location.y != 0) {
				location.y++;
			}
			location.x = 0;
			list.addAll(convertLevel1(baseNode, location));
			location.y++;
			children = baseNode.getChildren();
			break;
		case 2:
			location.x = 0;
			list.addAll(convertLevel2(baseNode, location));
			children = baseNode.getChildren();
			break;
		case 3:
			location.x = 1;
			list.addAll(convertLevel3(baseNode, location, childId,childListNumber ));
			location.y++;
			return list;
		}
		
		for(int i =0; i < children.size();i++) {
			convert(location, children.get(i),i,children.size(),list);
		}
		
		return list;
	}
	
	public ExcelTreeNode getRoot() {
		return this.root;
	}
	
	private List<CellInfo> convertLevel1(ExcelTreeNode node, Point location) throws WriteException {	
		List<CellInfo> list = new LinkedList<CellInfo>();
		CellInfo info = new CellInfo(CellType.LABLE, node.getNodeName(), new Point(location.x, location.y));
		//设置合并部分
		Point start = new Point(location.x, location.y);
		Point end = new Point(location.x+1, location.y);
		info.setMergerPath(new Path(start, end));
		WritableCellFormat format = new WritableCellFormat();
		//设置背景色
		format.setBackground(Colour.BLUE);
		//设置平行居中
		format.setAlignment(jxl.format.Alignment.CENTRE);
		//设置边框
		format.setBorder(Border.TOP, BorderLineStyle.THIN, Colour.BLACK);
		info.setFormat(format);
		list.add(info);
		location.x = location.x +2;
		
		DateTools dateTool = new DateTools("yyyy-MM");
		for(int i = 0; i < 12; i++) {
			format = new WritableCellFormat();
			info = new CellInfo(CellType.LABLE, dateTool.getMonthDate(0-i), new Point(location.x, location.y));
			//设置背景色
			format.setBackground(Colour.BLUE);
			//设置平行居中
			format.setAlignment(jxl.format.Alignment.CENTRE);
			info.setFormat(format);
			list.add(info);
			location.x++;
		}
		return list;
	}
	
	private List<CellInfo> convertLevel2(ExcelTreeNode node, Point location) throws WriteException {
		List<CellInfo> list = new LinkedList<CellInfo>();
		CellInfo info =  new CellInfo(CellType.LABLE, node.getNodeName(), new Point(location.x, location.y));
		WritableCellFormat format = new WritableCellFormat();
		//设置居中
		format.setAlignment(jxl.format.Alignment.CENTRE);
		format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		//设置背景色
		format.setBackground(Colour.YELLOW);
		//设置边框
		format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
		info.setFormat(format);
		//设置合并
		if(node.getChildren().isEmpty()) {
			list.add(info);
			return list;
		}
		Point start = new Point(location.x, location.y);
		Point end = new Point(location.x, location.y+node.getChildren().size() - 1);
		info.setMergerPath(new Path(start, end));
		list.add(info);
		return list;
	}
	
	private List<CellInfo> convertLevel3(ExcelTreeNode node, Point location, Integer childId,Integer listNumber) throws WriteException {
		List<CellInfo> list = new LinkedList<CellInfo>();
		CellInfo info =  new CellInfo(CellType.LABLE, node.getNodeName(), new Point(location.x, location.y));
		WritableCellFormat format = new WritableCellFormat();
		//设置平行居中
		format.setAlignment(jxl.format.Alignment.CENTRE);
		//设置背景色
		if(childId != null && childId.equals(0)) {
			format.setBackground(Colour.BLUE);
			//设置边框
			format.setBorder(Border.TOP, BorderLineStyle.THIN, Colour.BLACK);
		}
		if(childId != null && listNumber != null && childId.equals(listNumber-1)) {
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.BLACK);
		}
		info.setFormat(format);
		list.add(info);
		location.x++;
		
		for(String value:node.getExtreInfo()) {
			if(value.contains("%")) {
				info =  new CellInfo(CellType.PERCENT_FLOAT, value, new Point(location.x, location.y));
				format = new WritableCellFormat(NumberFormats.PERCENT_FLOAT);
			} else {
				info =  new CellInfo(CellType.NUMBER, value, new Point(location.x, location.y));
				format = new WritableCellFormat();
			}
			
			//设置背景色
			if(childId != null && childId.equals(0)) {
				format.setBackground(Colour.BLUE2);
				format.setBorder(Border.TOP, BorderLineStyle.THIN, Colour.BLACK);
			}
			
			if(childId != null && listNumber != null && childId.equals(listNumber-1)) {
				format.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.BLACK);
			}
			
			//设置靠右
			format.setAlignment(jxl.format.Alignment.RIGHT);
			location.x++;
			info.setFormat(format);
			list.add(info);
		}
		return list;
	}

}
