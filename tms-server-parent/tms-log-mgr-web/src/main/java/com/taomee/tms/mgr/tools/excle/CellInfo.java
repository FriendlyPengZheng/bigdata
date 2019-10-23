package com.taomee.tms.mgr.tools.excle;

import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;

enum CellType {  
	  LABLE,NUMBER,PERCENT_FLOAT
} 

public class CellInfo {
	
	private CellType type = null;
	private String value = null;
	private Point location = null;
	private Path mergerPath = null;
	/*private Colour backColour = null;
	private Alignment alignment = null;
	private VerticalAlignment verticalAlignment = null;*/
	private WritableCellFormat format = null;
	
	public CellInfo() {}
	public CellInfo(CellType type, String value, Point location) {
		this.type = type;
		this.value = value;
		this.location = location;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}

	public Path getMergerPath() {
		return mergerPath;
	}

	public void setMergerPath(Path mergerPath) {
		this.mergerPath = mergerPath;
	}

	public CellType getType() {
		return type;
	}

	public void setType(CellType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public WritableCellFormat getFormat() {
		return format;
	}
	public void setFormat(WritableCellFormat format) {
		this.format = format;
	}
}

