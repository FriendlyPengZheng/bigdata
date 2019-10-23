package com.taomee.tms.bigdata.MR;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.taomee.bigdata.lib.SetExpressionAnalyzer;
import com.taomee.bigdata.lib.Utils;

public class MRDriver extends Configured implements Tool {

	private static int printUsage() {
		System.out.println("Driver " 
				+ " <-gameInfo <game info>>" 
				+ " <[-taskID <taskID>]|[-op <op>]>"
				+ " <-date <date>>" 
				+ " [-gameID <gameID>]"
				+ " [-addInput <inputPath>]"
				+ " [-output <outputPath>]" 
				+ " [-fix]");
		ToolRunner.printGenericCommandUsage(System.out);
		return -1;
	}

	public int run(String[] args) throws Exception {
		Configuration conf = this.getConf();
		
		//创建各种参数变量
		String taskID = "";
		String date = "";
		List<String> gameInfo = new ArrayList<String>();
		List<String> otherArgs = new ArrayList<String>();
		
		String op = "";
		String gameID = "";
		String jobName = "";
		String reducer = "";
		String output = "";
		String outputKeyClass = "";
		String outputValueClass = "";
		String combinerClass = "";
		String keyComparatorClass = "";
		String partitionerClass = "";
		String groupComparatorClass = "";
		String additionalInputs = "";
		
		boolean isFix = false;
		List<String[]> inputParams = new LinkedList<String[]>();
		
		//显示args参数
		for (int i = 0; i < args.length; i++) {
			System.out.println("args " + i + " = " + args[i]);
		}

		//从args读取参数分配给参数变量
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-gameInfo")) {
				String gameinfo_temp = args[++i];
				conf.set("GameInfo", gameinfo_temp);
				String[] gameinfo = gameinfo_temp.split(",");
				if (gameinfo.length < 1) {
					throw new IllegalArgumentException("invalid add gameInfo");
				}
				for (int j = 0; j < gameinfo.length; j++) {
					gameInfo.add(gameinfo[j]);
				}
			} else if (args[i].equals("-taskID")) {
				taskID = args[++i];
			} else if (args[i].equals("-date")) {
				date = args[++i];
			} else if (args[i].equals("-op")) {
				op = args[++i];
			} else if (args[i].equals("-gameID")) {
				gameID = args[++i];
			} else if (args[i].equals("-output")) {
				output = args[++i];
			} else if (args[i].toLowerCase().equals("-addInputs")) {
				additionalInputs += "\t"+args[++i];
			} else if (args[i].equals("-fix")) {
				isFix = true;
				i++;
			} else if (args[i].equals("-combinerClass")) {
                combinerClass = args[++i];
            } else if (args[i].equals("-setKeyComparator")) {
                keyComparatorClass = args[++i];
            } else if (args[i].equals("-setPartitioner")) {
                partitionerClass = args[++i];
            } else if (args[i].equals("-setGroupComparator")) {
                groupComparatorClass = args[++i];
            }  else {
				otherArgs.add(args[i]);
			}
		}
		
		//检查参数变量是否赋值
		if(date.equals("")){
			printUsage();
			throw new Exception("date can not be null");
		}
		if(gameID.equals("")){
			gameID = "*";
		}
		if(taskID.equals("") && op.equals("")){
			printUsage();
			throw new Exception("taskID or op can not be null");
		}
		if((!op.equals("")) && output.equals("")){
			printUsage();
			throw new Exception("output can not be null");
		}
		
		//解析taskID，获得相关运行参数并赋给参数变量，并输出到控制台
		if(!taskID.equals("")){
			jobName = "task"+taskID+" "+Utils.getTaskInfo(Integer.valueOf(taskID)).getTaskName()+" "+Utils.getOPByTaskID(taskID)+" "+date;
		}else{
			jobName = op+" "+date;
		}
		System.out.println("Job name: "+jobName);
		
		if(op.equals("")){
			op = Utils.getOPByTaskID(taskID);
		}
		String opType = op.substring(0,op.indexOf("("));
		String exp = op.substring(op.indexOf("(")+1,op.lastIndexOf(")"));
		System.out.println("expression: "+exp+"\n"+"op type: "+opType);
		if (opType.toLowerCase().equals("distinct_count")) {//按key求每个key的value去重的个数
			String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(exp, date, gameID);
			for (String inputPath : inputPaths) {
				String inputFormat = "org.apache.hadoop.mapreduce.lib.input.TextInputFormat";
				String mapper = "com.taomee.tms.bigdata.MR.UcountMapper";
				System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
				String[] inputParam = new String[]{inputPath,inputFormat,mapper};
				inputParams.add(inputParam);
			}
			reducer = "com.taomee.tms.bigdata.MR.UcountReducer";
			outputKeyClass = "org.apache.hadoop.io.Text";
			outputValueClass = "org.apache.hadoop.io.NullWritable";
		}else if (opType.toLowerCase().equals("set")) {//集合运算
			SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer().analysis(exp);
			conf.set("setExpression", exp, "source1");
			for (String setExp : analyzer.getAllExp()) {
				int flagNum = Integer.valueOf(analyzer.getExpFlag(setExp));
				String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(setExp, date , gameID);
				for (String inputPath : inputPaths) {
					String inputFormat = "org.apache.hadoop.mapreduce.lib.input.TextInputFormat";
					String mapper = "com.taomee.tms.bigdata.MR.SetMapper"+ flagNum;
					System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
					String[] inputParam = new String[]{inputPath,inputFormat,mapper};
					inputParams.add(inputParam);
				}
			}
			reducer = "com.taomee.tms.bigdata.MR.SetReducer";
			outputKeyClass = "org.apache.hadoop.io.Text";
			outputValueClass = "org.apache.hadoop.io.IntWritable";
		}else if (opType.toLowerCase().equals("sum")){
			String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(exp,date,gameID);
			for(String inputPath:inputPaths){
				String inputFormat = "org.apache.hadoop.mapreduce.lib.input.TextInputFormat";
				String mapper = "com.taomee.tms.bigdata.MR.SumMapper";
				System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
				String[] inputParam = new String[]{inputPath,inputFormat,mapper};
				inputParams.add(inputParam);
			}
			reducer = "com.taomee.tms.bigdata.MR.SumReducer";
			outputKeyClass = "org.apache.hadoop.io.Text";
			outputValueClass = "org.apache.hadoop.io.FloatWritable";
		}else if (opType.toLowerCase().equals("count")){
			String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(exp,date,gameID);
			for(String inputPath:inputPaths){
				String inputFormat = "org.apache.hadoop.mapreduce.lib.input.TextInputFormat";
				String mapper = "com.taomee.tms.bigdata.MR.CountMapper";
				System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
				String[] inputParam = new String[]{inputPath,inputFormat,mapper};
				inputParams.add(inputParam);
			}
			reducer = "com.taomee.tms.bigdata.MR.CountReducer";
			outputKeyClass = "org.apache.hadoop.io.Text";
			outputValueClass = "org.apache.hadoop.io.IntWritable";
		}else if (opType.toLowerCase().equals("item_value_max")){
			String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(exp,date,gameID);
			for(String inputPath:inputPaths){
				String inputFormat = "org.apache.hadoop.mapreduce.lib.input.TextInputFormat";
				String mapper = "com.taomee.tms.bigdata.MR.ItemValueMaxMapper";
				System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
				String[] inputParam = new String[]{inputPath,inputFormat,mapper};
				inputParams.add(inputParam);
			}
			reducer = "com.taomee.tms.bigdata.MR.ItemValueMaxReducer";
			outputKeyClass = "org.apache.hadoop.io.Text";
			outputValueClass = "org.apache.hadoop.io.FloatWritable";
		}else if (opType.toLowerCase().equals("item_max")){
			String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(exp,date,gameID);
			for(String inputPath:inputPaths){
				String inputFormat = "org.apache.hadoop.mapreduce.lib.input.TextInputFormat";
				String mapper = "com.taomee.tms.bigdata.MR.ItemValueMapper";
				System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
				String[] inputParam = new String[]{inputPath,inputFormat,mapper};
				inputParams.add(inputParam);
			}
			reducer = "com.taomee.tms.bigdata.MR.ItemMaxReducer";
			outputKeyClass = "org.apache.hadoop.io.Text";
			outputValueClass = "org.apache.hadoop.io.FloatWritable";
		}else if (opType.toLowerCase().equals("item_avg")){
			String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(exp,date,gameID);
			for(String inputPath:inputPaths){
				String inputFormat = "org.apache.hadoop.mapreduce.lib.input.TextInputFormat";
				String mapper = "com.taomee.tms.bigdata.MR.ItemValueMapper";
				System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
				String[] inputParam = new String[]{inputPath,inputFormat,mapper};
				inputParams.add(inputParam);
			}
			reducer = "com.taomee.tms.bigdata.MR.ItemAvgReducer";
			outputKeyClass = "org.apache.hadoop.io.Text";
			outputValueClass = "org.apache.hadoop.io.FloatWritable";
		}
		
		if(!additionalInputs.equals("")){
			String[] extraInputs = additionalInputs.split("\t");
			for(int i = 1;i<=extraInputs.length-1;i++){
				inputParams.add(new String[]{extraInputs[i].split(",")[0],extraInputs[i].split(",")[1],"org.apache.hadoop.mapreduce.lib.input.TextInputFormat"});
			}
		}
		
		if(output.equals("")){
			output = Utils.getOutputDirPathByTaskID(taskID,date);
		}
		
		String nonfixOutput = output;
		if(isFix){
			output += "/fix";
		}
		Path outputPath = new Path(output);
		System.out.println("Output: "+output);
		
		//为job设置参数
		Job job = new Job(getConf());
		
		job.setJobName(jobName);
		
		
		for(String[] inputParam:inputParams){
			MultipleInputs.addInputPath(job, new Path(inputParam[0]), Class.forName(inputParam[1]).asSubclass(InputFormat.class), Class.forName(inputParam[2]).asSubclass(Mapper.class));
		}
		
		job.setReducerClass(Class.forName(reducer).asSubclass(Reducer.class));
		
		job.setOutputKeyClass(Class.forName(outputKeyClass).asSubclass(WritableComparable.class));
		job.setOutputValueClass(Class.forName(outputValueClass).asSubclass(Writable.class));
		
		FileSystem fs = FileSystem.get(this.getConf());
		if(fs.exists(outputPath)){
			fs.delete(outputPath,true);
		}
		FileOutputFormat.setOutputPath(job, outputPath);
		
		if (!combinerClass.equals("")) {
		    System.err.println("set combiner class " + combinerClass);
		    job.setCombinerClass(Class.forName(combinerClass).asSubclass(Reducer.class));
		}

		if (!partitionerClass.equals("")) {
		    System.err.println("set partitioner " + partitionerClass);
		    job.setPartitionerClass(Class.forName(partitionerClass).asSubclass(Partitioner.class));
		}
	
		job.setJarByClass(getClass());
		int ret = 1;
		try {
			ret = job.waitForCompletion(true) ? 0 : 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(isFix){
			System.out.println();
			String[] gameStrs = gameID.split(",");
			if(gameID.equals("")||gameID.equals(" ")||gameID.equals("*")){
				gameStrs[0]="*";
			}
			for(String gameStr:gameStrs){
				FileStatus[] status = fs.globStatus(new Path(nonfixOutput+"/partG"+gameStr+"-*"));
				for(FileStatus fileToDelete:status){
					System.out.println("delete "+fileToDelete.getPath());
					fs.delete(fileToDelete.getPath(),true);
				}
				
				Path fixedFileMoveToPath = new Path(nonfixOutput+"/");
				status = fs.globStatus(new Path(output+"/partG"+gameStr+"-*"));
				for(FileStatus fileToMove:status){
					System.out.println("move "+fileToMove.getPath()+" to "+fixedFileMoveToPath);
					fs.rename(fileToMove.getPath(), fixedFileMoveToPath);
				}
			}
			if(fs.exists(outputPath)){
				System.out.println("\n"+"delete tmp path:"+outputPath);
				fs.delete(outputPath,true);
			}
			
		}
		fs.close();
		
		return ret;
	}
	
	public static void main(String[] args) throws Exception{	
        int ret = ToolRunner.run(new MRDriver(), args);
        System.exit(ret);
    }
}
