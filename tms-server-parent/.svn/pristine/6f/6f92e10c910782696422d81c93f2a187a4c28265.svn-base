package com.taomee.tms.client;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taomee.bigdata.lib.SetExpressionAnalyzer;
import com.taomee.tms.client.simulatedubboserver.HdfsLoadPathConditions;
import com.taomee.tms.client.simulatedubboserver.SimulateDubboLogServiceImp;
import com.taomee.tms.common.schedule.job.AbstractJob;
import com.taomee.tms.common.schedule.task.ITask;
import com.taomee.tms.common.util.DateUtils;
import com.taomee.tms.common.util.InitConfUtils;
import com.taomee.tms.common.util.LoadConditionUtils;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.ArtifactInfo;

/**
 * 从Hdfs中结果文件目录中抓取path， 对path进行遍历获取所有结果文件，将其中数据传输至Server端
 * 
 * @author cheney
 * @date 2013-11-06 17:20:00
 */
public class NewHdfsDataFetchJob extends AbstractJob<TmsStreamer> {

	private static NewHdfsDataFetchJob job = null;

	// 要扫描的目录队列
	private Queue<String> pathq = new LinkedBlockingQueue<String>();

	private FileSystem fs = null;
	private String[] args = null;
	private Configuration conf = new Configuration();
	private int cdlNum = 0;
	private String data_type;
	private String task_id = "-1";
	private String artifact_id = "-1";
	private String date;
	private String hour = "0";
	private String artifactId = "-1";
	private String fileDate;
	private String gameId = null; //是否选择特定的入库条件
	private String calDate; //选择计算日期
	private LogMgrService logMgrService = UseAPILoadMgrService.getInstance().getLogMgrService();

	public NewHdfsDataFetchJob() {
	}

	public NewHdfsDataFetchJob(String[] args) {
		this.args = args;
	}

	public static NewHdfsDataFetchJob getInstance() {
		return job;
	}

	// public static

	public void before() {
		super.before();
		int i = 0;
		String hdfsURI = null;
		ArtifactInfo artifactInfo = new ArtifactInfo();// artifactInfo信息
		HdfsLoadPathConditions conditions = new HdfsLoadPathConditions();// 入库条件拼凑
		LoadConditionUtils loadUtils = new LoadConditionUtils();
		// String path
		List<String> otherArgs = new ArrayList<String>();
		try {
			for (i = 0; i < args.length; i++) {

				if (args[i].equals("-uri")) {
					hdfsURI = args[++i];
				} else if (args[i].equals("-type")) {
					data_type = args[++i];
				} else if (args[i].equals("-task")) {
					task_id = args[++i];
					System.out.println("task:" + task_id);
				} else if (args[i].equals("-artifact")) {
					artifact_id = args[++i];
					System.out.println("artifact:" + artifact_id);
				}  else if (args[i].equals("-hour")) {
					hour = args[++i];
				} else if (args[i].equals("-gameId")) {
					gameId = args[++i];
					System.out.println("gameId:"+gameId);
				} else if (args[i].equals("-calDate")) {
					calDate = args[++i];
					System.out.println("calDate:"+calDate);
				} else {
					otherArgs.add(args[i]);
				}
			}
			// 调用dubbo服务获取artifactInfo的信息,此处后面调取后台服务的服务信息
			//artifactInfo = new SimulateDubboLogServiceImp().getArtifactInfoByTaskId(task_id);
			/*ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext2.xml" });

			context.start();
			LogMgrService logMgrService = (LogMgrService) context.getBean("LogMgrService");*/ // 获取bean
			
			if(artifact_id.equals("-1") && task_id.equals("-1")){
				throw new IllegalArgumentException("you must set artifact_id or task_id");
			}
			if((!artifact_id.equals("-1")) && (!task_id.equals("-1"))){
				throw new IllegalArgumentException("both artifact_id and task_id could not be set at the same time!");
			}
			if(!artifact_id.equals("-1")){
				artifactInfo = logMgrService.getArtifactInfoByartifact(Integer.valueOf(artifact_id));
			}else if(!task_id.equals("-1")){
				artifactInfo = logMgrService.getArtifactInfoToLoadBytaskId(Integer.valueOf(task_id));
			}
			
			//防止对非结果入库
			if(artifactInfo.getResult() != 1){
				throw new IllegalArgumentException("artifact_"+artifactInfo.getArtifactId()+" is not a result,exit!");
			}
			System.out.println("artifactInfo:" + artifactInfo);
			try {
				// 根据传递进来的计算日期获取入库时间
				date = SetExpressionAnalyzer.getDateByOffset(
						artifactInfo.getOffset(), calDate).toString();
				
				fileDate = SetExpressionAnalyzer.getDateByOffset(
						LoadConditionUtils.periodReflectHdfsFileDate(artifactInfo.getPeriod()), 
						calDate);
				artifactId = artifactInfo.getArtifactId().toString();
				
				conditions.setTableName(artifactInfo.getHiveTableName());
				conditions.setFileDate(fileDate);
				conditions.setGameId(gameId);// 测试条件
				
				String tmp = loadUtils.loadPath(conditions);
				pathq.add(tmp);
				System.out.println("path:" + tmp);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fs = FileSystem.get(URI.create(hdfsURI), conf);

		} catch (ArrayIndexOutOfBoundsException e) {
			printUsage("missing param for " + args[i - 1]);
		} catch (IOException e) {
			printUsage("hdfsUri is not correct or connection refused: "
					+ e.getMessage());
		}

		if (hdfsURI == null) {
			printUsage("no hdfs uri");
		}
		if (pathq.isEmpty()) {
			printUsage("no input path");
		}

		try {

			while (!pathq.isEmpty()) {

				String spath = pathq.poll();

				Path path = new Path(spath);
				// System.out.println(path.toString()+"  ***");

				fetchHdfsFile(path);

			}
			// countDownLatch = new CountDownLatch(cdlNum);

			countDownLatch = new CountDownLatch(cdlNum);
			if (taskQueue.isEmpty()) {
				ITask<TmsStreamer> task;
				for (int j = 0; j < maxTaskNum; j++) {
					// 注释发送任务
					/*task = new NewHdfsTmsDataSendTask(getState(),
							countDownLatch);*/
					task = new NewVHdfsTmsDataSendTask(getState(),
							countDownLatch,logMgrService);
					taskQueue.add(task);

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void after() {
		try {
			long st = System.currentTimeMillis();
			System.out.println("Queue file count is: " + cdlNum);
			countDownLatch.await();
			System.out.println("Job has finished at: "
					+ DateUtils.getNowDateHms() + ", cost(s): "
					+ (System.currentTimeMillis() - st) / 1000);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fetchHdfsFile(Path path) throws IOException {
		FileStatus[] fstatus = fs.globStatus(path);
		System.out.println(path);

		if (fstatus != null && fstatus.length > 0) {
			for (FileStatus s : fstatus) {

				if (!fs.isFile(s.getPath())) {
					System.out.println(s.getPath());
				} else {
					// 当taskid大于0，说明是加工项，需要解析文件路径，解析文件路径当中的artifactId作为参数传入
					/*
					 * if (Integer.valueOf(task_id) > 0) { String filePath =
					 * s.getPath().toString(); int index =
					 * filePath.toString().lastIndexOf("/"); int index2 =
					 * filePath.substring(0, index).lastIndexOf( "/");
					 * artifactId = filePath.substring(0, index)
					 * .substring(index2 + 1).split("_")[0];
					 * //System.out.println("artifactId:" + artifactId); }
					 */
					TmsStreamer st = create(s.getPath());
					if (st != null) {
						feedBack(st);
						cdlNum++;
						// System.out.println(cdlNum);
					}
				}
			}
		}
	}

	private TmsStreamer create(Path path) throws IOException {
		TmsStreamer st = new TmsStreamer();
		st.setInputStream(fs.open(path));
		st.setFile(path.toUri().getPath());
		st.setData_type(data_type);
		st.setHour(hour);
		st.setArtifactId(artifactId);// 设置新系统的artifactId信息。
		try {
			st.setTime(date);
		} catch (java.text.ParseException e) {
		}
		st.setTaskid(task_id);
		System.out.println("find file: " + st.getFile());
		return st;
	}

	public static void main(String[] args) throws IOException {
		job = new NewHdfsDataFetchJob(args);
		// System.out.println("**"+InitConfUtils.getParamValue("client.run.model.debug"));
		if ("true"
				.equals(InitConfUtils.getParamValue("client.run.model.debug"))) {
			job.conf.set("mapred.job.tracker", "10.1.1.63:9001"); // for window
																	// debug
		}
		job.setState(1);
		job.start();
	}

	// usage
	private void printUsage(String msg) {

		// System.out.println(msg);

		StringBuffer usage = new StringBuffer();
		usage.append(
				"java -cp stat-bigdata.jar com.taomee.bigdata.client.HdfsDataFetchJob ")
				.append(" -uri <hdfs uri>")
				.append("[-path <input path> -type <data_type> -date <time> -task <taskid> -hour <hour>] ");

		System.out.println(usage.toString());

		ToolRunner.printGenericCommandUsage(System.out);

		System.exit(-1);
	}

}
