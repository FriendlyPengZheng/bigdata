package com.taomee.tms.mgr.tools.excle;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelTools {
	private WritableWorkbook book = null;
	private Set<Integer> sheetList = new HashSet<Integer>();
	private Boolean openFlag = false;
	
	public void open(String filename) throws IOException {
		this.book = Workbook.createWorkbook( new File(filename));
		this.openFlag = true;
	}
	
	public void close() throws WriteException, IOException {
		this.book.close();
	}
	
	public void write() throws IOException {
		book.write();
	}
	
	public Boolean setSheet(Integer sheetId, String sheetName, List<CellInfo> cells) throws WriteException, IOException {
		if(!openFlag) {
			return false;
		}
		
		WritableSheet sheet = getWriteSheet(sheetId, sheetName);	
		for(CellInfo cell:cells) {
			if(cell == null || cell.getLocation() == null || cell.getType() == null) {
				continue;
			}
			
			WritableCellFormat format = cell.getFormat();
			
			if(cell.getMergerPath()!=null) {
				Path path = cell.getMergerPath();
				System.out.println(cell.getValue() +" mergerPath:" +path.getSart().x+ " "+ path.getSart().y+ " "+ 
						path.getEnd().x+ " "+ path.getEnd().y);
				sheet.mergeCells( path.getSart().x, path.getSart().y, 
						path.getEnd().x, path.getEnd().y);
			}
			
			sheet = addCell(sheet, cell.getType(), cell.getLocation(), cell.getValue(), format);
			
		}
		return true;
	}
	
	public  WritableSheet getWriteSheet(Integer sheetId) {
		return getWriteSheet(sheetId, null);
	}
	
	public WritableSheet getWriteSheet(Integer sheetId, String sheetName) {
		WritableSheet sheet = null;
		if(sheetList.contains(sheetId)) {
			sheet = book.getSheet(sheetId);
		} else  {
			if(sheetName == null){
				sheet = book.createSheet("Sheet"+sheetId, sheetId);
			} else {
				sheet = book.createSheet( sheetName ,sheetId);
			}
			sheetList.add(sheetId);
		}
		
		return sheet;
	}
	
	/*public Boolean setSheet(Integer sheetId, String sheetName, CellInfo cell) throws WriteException, IOException {
		List<CellInfo> list = new ArrayList<CellInfo>();
		list.add(cell);
		return setSheet(sheetId, sheetName, list);
	}*/
	
	public void setColour(Colour type, String colorCode) {
		Color color = Color.decode(colorCode);
		book.setColourRGB(type, color.getRed(),
                color.getGreen(), color.getBlue());
	}
	
	private WritableSheet addCell(WritableSheet sheet, CellType type, Point location, String value, WritableCellFormat format) throws RowsExceededException, WriteException {
		if(value == null || value.isEmpty()) {
			if(type.equals(CellType.LABLE)) {
				value = "";
			} else {
				value = "0.0";
			}
		}
		
		if(format == null) {
			format = new WritableCellFormat();
		}
		
		switch(type) {
		case LABLE:
			Label label = new Label(location.x , location.y , value ,format);
			sheet.addCell(label);
			break;
		case NUMBER:
			//System.out.println("value:" + value);
			jxl.write.Number number = new jxl.write.Number( location.x , location.y , Double.parseDouble(value),format);
			sheet.addCell(number);
			break;
		case PERCENT_FLOAT:
			Double tmp = Double.parseDouble(value.replaceAll("%",""));
			jxl.write.Number number1 = new jxl.write.Number( location.x , location.y , tmp/100,format);
			sheet.addCell(number1);
			break;
		default:
		}
		return sheet;
	}
	
	public static void main(String args[]) {
		ExcelTools tool = new ExcelTools();
		try {
			tool.open("maggie_test.xls");
			System.out.print("open success!");
			List<CellInfo> cellList = new ArrayList<CellInfo>();
			CellInfo title = new CellInfo(CellType.LABLE,"title",new Point(0,0));
			title.setMergerPath(new Path(new Point(0,0), new Point(2,1)));
			WritableCellFormat format = new WritableCellFormat();
			format.setAlignment(jxl.format.Alignment.CENTRE);
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		
			tool.setColour(Colour.YELLOW2, "#fffca8");
			format.setBackground(Colour.YELLOW2);
			
			format.setBorder(Border.TOP, BorderLineStyle.THIN, Colour.BLACK);
			title.setFormat(format);
			cellList.add(title);
			
			CellInfo cell1 = new CellInfo(CellType.NUMBER,"13.12",new Point(0,2));
			CellInfo cell2 = new CellInfo(CellType.NUMBER,"135.12",new Point(1,2));
			CellInfo cell3 = new CellInfo(CellType.NUMBER,"1323.12",new Point(2,2));
			cellList.add(cell1);
			cellList.add(cell2);
			cellList.add(cell3);
			
			tool.setSheet(0, "test", cellList);
			tool.getWriteSheet(0).setColumnView(0, 37);
			tool.write();
			tool.close();
			
			System.out.print("write success!");
		} catch (IOException | WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

