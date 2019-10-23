//package com.taomee.tms.bigdata.MRold;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.hadoop.conf.Configured;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.RawComparator;
//import org.apache.hadoop.io.Writable;
//import org.apache.hadoop.io.WritableComparable;
//import org.apache.hadoop.mapred.FileOutputFormat;
//import org.apache.hadoop.mapred.InputFormat;
//import org.apache.hadoop.mapred.JobClient;
//import org.apache.hadoop.mapred.JobConf;
//import org.apache.hadoop.mapred.Mapper;
//import org.apache.hadoop.mapred.OutputFormat;
//import org.apache.hadoop.mapred.Partitioner;
//import org.apache.hadoop.mapred.Reducer;
//import org.apache.hadoop.mapred.lib.MultipleInputs;
//import org.apache.hadoop.mapred.lib.MultipleOutputs;
//import org.apache.hadoop.util.Tool;
//import org.apache.hadoop.util.ToolRunner;
//
//import com.taomee.bigdata.lib.SetExpressionAnalyzer;
//import com.taomee.bigdata.lib.Utils;
//
//public class DriverOld extends Configured implements Tool {
//
//	private static int printUsage() {
//		System.out.println("Driver " 
//				+ " <-gameInfo <game info>>" 
//				+ " <[-taskID <taskID>]|[-op <op>]>"
//				+ " <-date <date>>" 
//				+ " [-gameID <gameID>]"
//				+ " [-output <outputPath>]" 
//				+ " [-fix]");
//		ToolRunner.printGenericCommandUsage(System.out);
//		return -1;
//	}
//
//	public int run(String[] args) throws Exception {
//		JobConf jobConf = new JobConf(this.getConf(), this.getClass());
//		
//		//创建各种参数变量
//		String taskID = "";
//		String date = "";
//		List<String> gameInfo = new ArrayList<String>();
//		List<String> otherArgs = new ArrayList<String>();
//		
//		String op = "";
//		String gameID = "";
//		String jobName = "";
//		String reducer = "";
//		String output = "";
//		String outputKeyClass = "";
//		String outputValueClass = "";
//		String combinerClass = "";
//		String keyComparatorClass = "";
//		String partitionerClass = "";
//		String groupComparatorClass = "";
//		boolean isFix = false;
//		List<String[]> inputParams = new LinkedList<String[]>();
//		
//		//显示args参数
//		for (int i = 0; i < args.length; i++) {
//			System.out.println("args " + i + " = " + args[i]);
//		}
//
//		//从args读取参数分配给参数变量
//		for (int i = 0; i < args.length; i++) {
//			if (args[i].equals("-gameInfo")) {
//				String gameinfo_temp = args[++i];
//				jobConf.set("GameInfo", gameinfo_temp);
//				String[] gameinfo = gameinfo_temp.split(",");
//				if (gameinfo.length < 1) {
//					throw new IllegalArgumentException("invalid add gameInfo");
//				}
//				for (int j = 0; j < gameinfo.length; j++) {
//					gameInfo.add(gameinfo[j]);
//				}
//			} else if (args[i].equals("-taskID")) {
//				taskID = args[++i];
//			} else if (args[i].equals("-date")) {
//				date = args[++i];
//			} else if (args[i].equals("-op")) {
//				op = args[++i];
//			} else if (args[i].equals("-gameID")) {
//				gameID = args[++i];
//			} else if (args[i].equals("-output")) {
//				output = args[++i];
//			} else if (args[i].equals("-fix")) {
//				isFix = true;
//				i++;
//			} else if (args[i].equals("-combinerClass")) {
//                combinerClass = args[++i];
//            } else if (args[i].equals("-setKeyComparator")) {
//                keyComparatorClass = args[++i];
//            } else if (args[i].equals("-setPartitioner")) {
//                partitionerClass = args[++i];
//            } else if (args[i].equals("-setGroupComparator")) {
//                groupComparatorClass = args[++i];
//            }  else {
//				otherArgs.add(args[i]);
//			}
//		}
//		
//		//检查参数变量是否赋值
//		if(date.equals("")){
//			throw new Exception("date can not be null");
//		}
//		if(gameID.equals("")){
//			gameID = "*";
//		}
//		if(taskID.equals("") && op.equals("")){
//			throw new Exception("taskID or op can not be null");
//		}
//		if((!op.equals("")) && output.equals("")){
//			throw new Exception("output can not be null");
//		}
//		
//		//解析taskID，获得相关运行参数并赋给参数变量，并输出到控制台
//		if(!taskID.equals("")){
//			jobName = Utils.getOPByTaskID(taskID);
//		}else{
//			jobName = op;
//		}
//		System.out.println("Job name: "+jobName+" "+date);
//		
//		if(op.equals("")){
//			op = Utils.getOPByTaskID(taskID);
//		}
//		String opType = op.substring(0,op.indexOf("("));
//		String exp = op.substring(op.indexOf("(")+1,op.lastIndexOf(")"));
//		System.out.println("op type: "+opType+"\n"+"expression: "+exp);
//		if (opType.toLowerCase().equals("distinct_count")) {//按key求每个key的value去重的个数
//			String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(exp, date, gameID);
//			for (String inputPath : inputPaths) {
//				String inputFormat = "org.apache.hadoop.mapred.TextInputFormat";
//				String mapper = "com.taomee.tms.bigdata.MR.UcountMapper";
//				System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
//				String[] inputParam = new String[]{inputPath,inputFormat,mapper};
//				inputParams.add(inputParam);
//			}
//			reducer = "com.taomee.tms.bigdata.MR.UcountReducer";
//			outputKeyClass = "org.apache.hadoop.io.Text";
//			outputValueClass = "org.apache.hadoop.io.NullWritable";
//			MultipleOutputs.addNamedOutput(
//                    jobConf, "partG",
//                    Class.forName("org.apache.hadoop.mapred.TextOutputFormat").asSubclass(OutputFormat.class),
//                    Class.forName("org.apache.hadoop.io.Text").asSubclass(WritableComparable.class),
//                    Class.forName("org.apache.hadoop.io.IntWritable").asSubclass(Writable.class));
//		}
//		if (opType.toLowerCase().equals("set")) {
//			SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer().analysis(exp);
//			jobConf.set("setExpression", exp);
//			Map<String, Integer> setMap = analyzer.getSetMap();
//			for (String setExp : setMap.keySet()) {
//				int flagNum = Integer.valueOf(setMap.get(setExp));
//				String[] inputPaths = SetExpressionAnalyzer.getInputPathsBySetExpression(setExp, date , gameID);
//				for (String inputPath : inputPaths) {
//					String inputFormat = "org.apache.hadoop.mapred.TextInputFormat";
//					String mapper = "com.taomee.tms.bigdata.MR.SetMapper"+ flagNum;
//					System.out.println("AddInput: "+inputPath+","+inputFormat+","+mapper);
//					String[] inputParam = new String[]{inputPath,inputFormat,mapper};
//					inputParams.add(inputParam);
//				}
//			}
//			reducer = "com.taomee.tms.bigdata.MR.SetReducer";
//			outputKeyClass = "org.apache.hadoop.io.Text";
//			outputValueClass = "org.apache.hadoop.io.IntWritable";
//		}
//		
//		if(output.equals("")){
//			output = Utils.getOutputByTaskID(taskID,date);
//		}
//		if(isFix){
//			output += "-fix";
//		}
//		Path outputPath = new Path(output);
//		System.out.println("Output: "+output);
//		
//		//为job设置参数
//		jobConf.setJobName(jobName);
//
//		for(String[] inputParam:inputParams){
//			MultipleInputs.addInputPath(jobConf, new Path(inputParam[0]), Class.forName(inputParam[1]).asSubclass(InputFormat.class), Class.forName(inputParam[2]).asSubclass(Mapper.class));
//		}
//		
//		jobConf.setReducerClass(Class.forName(reducer).asSubclass(Reducer.class));
//		
//		jobConf.setOutputKeyClass(Class.forName(outputKeyClass).asSubclass(WritableComparable.class));
//		jobConf.setOutputValueClass(Class.forName(outputValueClass).asSubclass(Writable.class));
//		
//		FileSystem fs = FileSystem.get(this.getConf());
//		if(fs.exists(outputPath)){
//			fs.delete(outputPath,true);
//		}
//		FileOutputFormat.setOutputPath(jobConf, outputPath);
//		
//		if (!combinerClass.equals("")) {
//		    System.err.println("set combiner class " + combinerClass);
//		    jobConf.setCombinerClass(Class.forName(combinerClass).asSubclass(Reducer.class));
//		}
//
//		if (!keyComparatorClass.equals("")) {
//		    System.err.println("set key comparator " + keyComparatorClass);
//		    jobConf.setOutputKeyComparatorClass(Class.forName(keyComparatorClass).asSubclass(RawComparator.class));
//		}
//
//		if (!partitionerClass.equals("")) {
//		    System.err.println("set partitioner " + partitionerClass);
//		    jobConf.setPartitionerClass(Class.forName(partitionerClass).asSubclass(Partitioner.class));
//		}
//
//		if (!groupComparatorClass.equals("")) {
//		    System.err.println("set group comparator " + groupComparatorClass);
//		    jobConf.setOutputValueGroupingComparator(Class.forName(groupComparatorClass).asSubclass(RawComparator.class));
//		}
//		
//		JobClient.runJob(jobConf);
//		return 0;
//	}
//	
//	public static void main(String[] args) throws Exception
//    {
//		System.setProperty("log4j.configuration", "file:///C:/Users/mukade/javaworkspace/sparktest2/src/log4j.properties");
//        int ret = ToolRunner.run(new DriverOld(), args);
//        System.exit(ret);
//    }
//}
