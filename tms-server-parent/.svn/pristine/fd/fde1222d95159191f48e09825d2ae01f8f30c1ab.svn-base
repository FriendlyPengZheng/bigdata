package com.taomee.tms.mgr.tools.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.taomee.tms.mgr.tools.GenericPair;

public class LineChartTools {
	private LineChartDefaultForm form;
	
	public LineChartTools() {
		form = new LineChartDefaultForm();
	}
	
	public JFreeChart createChart(DefaultCategoryDataset databaset, String xName, String yName, String chartTitle) {
		//设置title字体
		StandardChartTheme mChartTheme = new StandardChartTheme("CN");
		/*mChartTheme.setExtraLargeFont(form.getTitleFont());
		ChartFactory.setChartTheme(mChartTheme);*/
		//设置标题字体  
        mChartTheme.setExtraLargeFont(form.getTitleFont());  
        //设置轴向字体  
        //mChartTheme.setLargeFont(new Font("宋体", Font.CENTER_BASELINE, 10));  
        //设置图例字体  
        mChartTheme.setRegularFont(form.getRegularFont());  
        //应用主题样式  
        ChartFactory.setChartTheme(mChartTheme);
		
		//创建chart
		JFreeChart chart = ChartFactory.createLineChart(
		         chartTitle,
		         xName,
		         yName,
		         databaset,
		         PlotOrientation.VERTICAL,
		         true,true,true);
		
		//修改默认格式
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
	    // 取得横轴
	    CategoryAxis categoryAxis = plot.getDomainAxis();
	    //设置横轴字体
	    categoryAxis.setLabelFont(form.getNumberFont());
	    categoryAxis.setTickLabelFont(form.getTickLabelFont());
	    //设置字体斜角45度
	    categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	    
	    //获取纵轴
	    NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
	    //设置纵轴字体颜色
	    numberAxis.setLabelFont(form.getNumberFont());
	    numberAxis.setTickLabelFont(form.getTickLabelFont());
	    
	    //设置背景色
	    plot.setBackgroundPaint(form.getBackgroundColor());
	    //设置网格线颜色
	    plot.setDomainGridlinePaint(form.getGridlineColor());
	    plot.setDomainGridlinesVisible(true);
	    plot.setRangeGridlinePaint(form.getGridlineColor());
	    
	    //设置线条颜色
	    LineAndShapeRenderer lasp = (LineAndShapeRenderer) plot.getRenderer();
	    for(int i = 0; i < databaset.getRowCount(); i++) {
	    	lasp.setSeriesPaint(i , form.getLineColor());
	    	lasp.setSeriesStroke(i, new BasicStroke(form.getLineWith()));
	    }
	    
		return chart;
	}
	
	public JFreeChart setRang(JFreeChart chart, Double lower, Double upper) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		ValueAxis rangAxis = plot.getRangeAxis();
		rangAxis.setRange(lower, upper);
		return chart;
	}
	
	public void saveJPEG(JFreeChart chart, String path, int width, int height) {
		//int width = 560; /* Width of the image */          
	    //int height = 370; /* Height of the image */                          
	    File pieChart = new File(path);                        
	    try {
			ChartUtilities.saveChartAsJPEG( pieChart, chart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void showChart(JFreeChart chart) {
		ApplicationFrame fram  = new ApplicationFrame("default");
		ChartPanel chartPanel = new ChartPanel( chart );
	    chartPanel.setPreferredSize( new java.awt.Dimension( 1000 , 400 ) );
	    fram.setContentPane( chartPanel );
	    
	    fram.pack( );
	    RefineryUtilities.centerFrameOnScreen( fram );
	    fram.setVisible( true );
	}
	
	public JFreeChart setLineInfo(JFreeChart chart, String lineName, Color lineColor, Float lineWith) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		LineAndShapeRenderer lasp = (LineAndShapeRenderer) plot.getRenderer();
		DefaultCategoryDataset databaset = (DefaultCategoryDataset) plot.getDataset();
		Integer index = databaset.getColumnIndex(lineName);
		
		if(lineColor != null) {
			lasp.setSeriesPaint(index , lineColor);
		}
		
		if(lineWith != null) {
			lasp.setSeriesStroke(index, new BasicStroke(lineWith));
		}
		
		return chart;
	}
	
	public DefaultCategoryDataset getDataset(Map<String, List<GenericPair<String, Number>>> map) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		
		Iterator<Map.Entry<String, List<GenericPair<String, Number>>>> entries = map.entrySet().iterator();  
		while(entries.hasNext()) {
			Map.Entry<String, List<GenericPair<String, Number>>> entry = entries.next();
			String lineName = entry.getKey();
			//System.out.println("linename:" + lineName);
			List<GenericPair<String, Number>> dataList = entry.getValue();
			for(GenericPair<String, Number> data:dataList) {
				//System.out.println("key:" + data.getFirst() + " value:" + data.getSecond());
				dataset.addValue( data.getSecond() , lineName , data.getFirst() );
			}
		}
		return dataset;
	}

	public LineChartDefaultForm getForm() {
		return form;
	}

	public void setForm(LineChartDefaultForm form) {
		this.form = form;
	}
	
	public static void main( String[ ] args ) {
		LineChartTools tool = new LineChartTools();
		
		//创建dataset
		/*Map<String, Map<String, Number>> datasetMap = new HashMap<String, Map<String, Number>>();
		Map<String, Number> lineMap = new HashMap<String, Number>();
		
		lineMap.put("1970", 15);
		lineMap.put("1980", 30);
		lineMap.put("1990", 60);
		lineMap.put("2000", 120);
		lineMap.put("2010", 240);
		lineMap.put("2014", 300);
		
		datasetMap.put("school", lineMap);*/
		//DefaultCategoryDataset dataset = tool.creatLineDataset(datasetMap);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		dataset.addValue( 50 , "学校" , "11-12" );
	    dataset.addValue( 60 , "学校" , "11-13" );
	    dataset.addValue( 70 , "学校" ,  "11-14" );
	    dataset.addValue( 120 , "学校" , "11-15" );
	    dataset.addValue( 240 , "学校" , "11-16" );
	    dataset.addValue( 300 , "学校" , "11-17" );
	    dataset.addValue( 50 , "学校" , "11-18" );
	    dataset.addValue( 60 , "学校" , "11-19" );
	    dataset.addValue( 70 , "学校" ,  "11-20" );
	    dataset.addValue( 120 , "学校" , "11-21" );
	    dataset.addValue( 240 , "学校" , "11-22" );
	    dataset.addValue( 300 , "学校" , "11-23" );
	    dataset.addValue( 50 , "学校" , "11-24" );
	    dataset.addValue( 60 , "学校" , "11-25" );
	    dataset.addValue( 70 , "学校" ,  "11-26" );
	    dataset.addValue( 120 , "学校" , "11-27" );
	    dataset.addValue( 240 , "学校" , "11-28" );
	    dataset.addValue( 300 , "学校" , "11-29" );
	    dataset.addValue( 50 , "学校" , "11-30" );
	    dataset.addValue( 60 , "学校" , "11-31" );
	    dataset.addValue( 70 , "学校" ,  "11-32" );
	    dataset.addValue( 120 , "学校" , "11-33" );
	    dataset.addValue( 240 , "学校" , "11-34" );
	    dataset.addValue( 300 , "学校" , "11-35" );
		
		JFreeChart chart = tool.createChart(dataset, "年份", "学校数量", "学校数量-年份");
		
		tool.showChart(tool.setRang(chart, 50D, 300D));
		//tool.saveJPEG(chart, "C:\\Users\\maggie\\Pictures\\test\\test2.jpeg", 1000, 400);
	}
}
