package com.taomee.tms.mgr.tools.chart;

import java.awt.Color;
import java.awt.Font;

public class LineChartDefaultForm {
	 public static final Font DEFAULT_TITLE_FONT = new Font("黑体", Font.PLAIN, 20);
	 public static final Font DEFAULT_XY_FONT = new Font("黑体", Font.PLAIN, 18);
	 public static final Font DEFAULT_TICK_LABLE_FONT = new Font("宋体", Font.PLAIN, 13);
	 
	 public static final Color DEFAULT_BACKGROUND_COL = new Color(229, 255, 255);
	 public static final Color DEFAULT_GRIDLINE_COL = Color.BLACK;
	 public static final Color DEFAULT_LINE_COL = new Color(0, 102, 255);
	 
	 public static final Float DEFAULT_LINE_WITH = 2.0f;
	
	//默认字体
	//标题字体
	private Font titleFont;
	//纵轴字体
	private Font numberFont;
	//横向字体
	private Font labelFont;
	//分类标签字体
	private Font tickLabelFont;
	//设置图例字体  
	private Font regularFont;
	
	//默认颜色
	//背景颜色
	private Color backgroundColor;
	//网格线颜色
	private Color GridlineColor;
	//默认线条颜色
	private Color lineColor;
	
	//默认线条粗细
	private Float lineWith;
	
	LineChartDefaultForm() {
		titleFont = DEFAULT_TITLE_FONT;
		numberFont = DEFAULT_XY_FONT;
		labelFont = DEFAULT_XY_FONT;
		tickLabelFont = DEFAULT_TICK_LABLE_FONT;
		regularFont = DEFAULT_TICK_LABLE_FONT;
		
		backgroundColor = DEFAULT_BACKGROUND_COL;
		GridlineColor = DEFAULT_GRIDLINE_COL;
		lineColor = DEFAULT_LINE_COL;
		
		lineWith = DEFAULT_LINE_WITH;
		
	}
	
	LineChartDefaultForm(String type) {
		if(type.equals(new String("default")) || type == null) {
			titleFont = DEFAULT_TITLE_FONT;
			numberFont = DEFAULT_XY_FONT;
			labelFont = DEFAULT_XY_FONT;
			tickLabelFont = DEFAULT_TICK_LABLE_FONT;
			
			backgroundColor = DEFAULT_BACKGROUND_COL;
			GridlineColor = DEFAULT_GRIDLINE_COL;
			lineColor = DEFAULT_LINE_COL;
			
			lineWith = DEFAULT_LINE_WITH;
		} else {
			//TODO可以加入别的样式设计
		}
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	public Font getNumberFont() {
		return numberFont;
	}

	public void setNumberFont(Font numberFont) {
		this.numberFont = numberFont;
	}

	public Font getLabelFont() {
		return labelFont;
	}

	public void setLabelFont(Font labelFont) {
		this.labelFont = labelFont;
	}

	public Font getTickLabelFont() {
		return tickLabelFont;
	}

	public void setTickLabelFont(Font tickLabelFont) {
		this.tickLabelFont = tickLabelFont;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getGridlineColor() {
		return GridlineColor;
	}

	public void setGridlineColor(Color gridlineColor) {
		GridlineColor = gridlineColor;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Float getLineWith() {
		return lineWith;
	}

	public void setLineWith(Float lineWith) {
		this.lineWith = lineWith;
	}

	public Font getRegularFont() {
		return regularFont;
	}

	public void setRegularFont(Font regularFont) {
		this.regularFont = regularFont;
	}
}
