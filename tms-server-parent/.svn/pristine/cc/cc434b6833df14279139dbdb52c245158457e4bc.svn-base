package com.taomee.tms.client;

import java.io.IOException;
import java.net.URI;
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

import com.taomee.tms.common.schedule.job.AbstractJob;
import com.taomee.tms.common.schedule.task.ITask;
import com.taomee.tms.common.util.DateUtils;
import com.taomee.tms.common.util.InitConfUtils;
import com.taomee.tms.mgr.api.LogMgrService;

/**
 * 从Hdfs中结果文件目录中抓取path， 对path进行遍历获取所有结果文件，将其中数据传输至Server端
 * 
 * @author cheney
 * @date 2013-11-06 17:20:00
 */
public class HdfsDataFetchJob extends AbstractJob<TmsStreamer> {

	private static HdfsDataFetchJob job = null;

	// 要扫描的目录队列
	private Queue<String> pathq = new LinkedBlockingQueue<String>();

	private FileSystem fs = null;
	private String[] args = null;
	private Configuration conf = new Configuration();
	private int cdlNum = 0;
	private String data_type;
	private String task_id = "-1";
	private String date;
	private String hour = "0";
	private String artifactId="-1";
	private LogMgrService logMgrService = UseAPILoadMgrService.getInstance().getLogMgrService();

	public HdfsDataFetchJob() {
	}

	public HdfsDataFetchJob(String[] args) {
		this.args = args;
	}

	public static HdfsDataFetchJob getInstance() {
		return job;
	}

	// public static

	public void before() {
		super.before();
		int i = 0;
		String hdfsURI = null;
		// String path
		List<String> otherArgs = new ArrayList<String>();
		try {
			for (i = 0; i < args.length; i++) {
				if (args[i].equals("-uri")) {
					hdfsURI = args[++i];
				} else if (args[i].equals("-path")) {
					String tmp = args[++i];
					pathq.add(tmp);
					System.out.println("path:"+tmp);
					// System.out.println(tmp);
				} else if (args[i].equals("-type")) {
					data_type = args[++i];
				} else if (args[i].equals("-date")) {
					date = args[++i];
					System.out.println("date:"+date);
				} else if (args[i].equals("-task")) {
					task_id = args[++i];
					System.out.println("task:"+task_id);
				} else if (args[i].equals("-hour")) {
					hour = args[++i];
				} else {
					otherArgs.add(args[i]);
				}
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
					System.out.println("");
					task = new HdfsTmsDataSendTask(getState(), countDownLatch,logMgrService);
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
					//当taskid大于0，说明是加工项，需要解析文件路径，解析文件路径当中的artifactId作为参数传入
					if (Integer.valueOf(task_id) > 0) {
						String filePath = s.getPath().toString();
						int index = filePath.toString().lastIndexOf("/");
						int index2 = filePath.substring(0, index).lastIndexOf(
								"/");
						artifactId = filePath.substring(0, index)
								.substring(index2 + 1).split("_")[0];
						//System.out.println("artifactId:" + artifactId);
					}
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
		st.setArtifactId(artifactId);//设置新系统的artifactId信息。
		try {
			st.setTime(date);
		} catch (java.text.ParseException e) {
		}
		st.setTaskid(task_id);
		System.out.println("find file: " + st.getFile());
		return st;
	}

	public static void main(String[] args) throws IOException {
		job = new HdfsDataFetchJob(args);
		//System.out.println("**"+InitConfUtils.getParamValue("client.run.model.debug"));
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
