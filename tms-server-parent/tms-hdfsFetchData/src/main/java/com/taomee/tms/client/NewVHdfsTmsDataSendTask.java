package com.taomee.tms.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taomee.tms.common.schedule.task.AbstractTask;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.DataToObject;

/**
 * 
 * @author looper
 * 
 */
public class NewVHdfsTmsDataSendTask extends AbstractTask<TmsStreamer> {

	private TmsStreamer streamer;

	private InputStream is;
	private InputStreamReader isr;
	private BufferedReader br;

	private OutputStream os;
	private InputStream bk;
	private LogMgrService logMgrService;

	// static ClassPathXmlApplicationContext context;
	// static LogMgrService logMgrService;

	/*
	 * public static void onload() {
	 * 
	 * context = new ClassPathXmlApplicationContext( new String[] {
	 * "applicationContext.xml" }); // System.out.println(context);
	 * context.start(); logMgrService = (LogMgrService)
	 * context.getBean("LogMgrService"); // 获取bean
	 * 
	 * }
	 */

	public NewVHdfsTmsDataSendTask(int state, CountDownLatch cdl,LogMgrService logMgrService) {
		setState(state);
		System.out.println(state);
		this.countDownLatch = cdl;
		this.logMgrService = logMgrService;
		// onload();
	}

	/**
	 * 具体调用dubbo服务的地方
	 */
	public void process() {
		try {

			streamer = get();
			// System.out.println(streamer.toString());
			is = streamer.getInputStream();
			// byte buf[]=
			// boolean c = connect();
			/*
			 * if(!c){ HdfsDataFetchJob.getInstance().feedBack(streamer);
			 * return; }
			 */
			/**
			 * 在这里可以设置当链接不上dubbo的服务，需要重新将数据加入到队列中，重新发送
			 */
			/**
			 * if(dubbo.service is not connect) {
			 * HdfsDataFetchJob.getInstance().feedBack(streamer); return; }
			 */
			
			/*ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext2.xml" });

			context.start();
			LogMgrService logMgrService = (LogMgrService) context
					.getBean("LogMgrService"); // 获取bean
*/			System.out.println("bean:" + logMgrService);

			isr = new InputStreamReader(is,"utf8");
			br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				// System.out.println(streamer.getArtifactId()+"***"+line+"---"+streamer.getTaskid());//测试获取ArtifactId信息
				String lines[] = line.split("\t");
				// lines=4,基础统计项信息拼接
				if (lines.length == 4) {
					DataToObject datatoObject = new DataToObject(
							streamer.getTaskid(), streamer.getTime(),
							Integer.valueOf(lines[0]), Integer.valueOf(streamer
									.getArtifactId()),
							Integer.valueOf(lines[1]), lines[2],
							Double.valueOf(lines[3]));
					System.out.println("jichu:" + datatoObject);
					logMgrService.insertUpdateDataResultInfo(datatoObject);

				} else if (lines.length == 3) {// lines.length=3,加工项信息拼接
					DataToObject datatoObject = new DataToObject(
							streamer.getTaskid(), streamer.getTime(),
							Integer.valueOf(streamer.getArtifactId()),
							Integer.valueOf(lines[0]), lines[1],
							Double.valueOf(lines[2]));
					System.out.println("jiagong:" + datatoObject);
					logMgrService.insertUpdateDataResultInfo(datatoObject);
				}
				// 调用dubbo的服务
			}

			/*
			 * os = socket.getOutputStream(); bk = socket.getInputStream();
			 * 
			 * //strea = g streamer = get();
			 * 
			 * is = streamer.getInputStream();
			 * 
			 * isr = new InputStreamReader(is); br = new BufferedReader(isr);
			 * 
			 * String line; byte[] buf = null; TranProtocol pt = null; while (
			 * (line = br.readLine()) != null){ for(int i=0; i<10; i++) { pt =
			 * DataParser.parser(line, streamer); try { buf = pt.pack(); } catch
			 * (Exception e) { System.err.println(e.getMessage()); break; }
			 * if(buf != null) { os.write(buf, 0, buf.length); bk.read(buf, 0,
			 * 9); if(buf[8] == 0) {
			 * //System.out.println(String.format("insert [%s] return %d, OK",
			 * line, buf[8])); break; } System.err.println(String.format(
			 * "insert [%s] return %d, try again ...", line, buf[8])); } }
			 * Thread.sleep(1); }
			 */

			// System.out.println(streamer.getFile() + " have finished over.");
			Thread.sleep(1000);

			// IOUtils.copyBytes(is, os, 10);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// if(os != null) os.close();
				// if(socket != null) socket.close();
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				if (is != null)
					is.close();
				// if(bk != null) bk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (countDownLatch != null)
				countDownLatch.countDown();
			NewHdfsDataFetchJob.getInstance().feedBack(this);
		}
	}

	/*
	 * boolean connect() { int index = PropertiesConf.get(); try { socket = new
	 * Socket( PropertiesConf.SOCKET_SERVER[index],
	 * PropertiesConf.SOCKTE_PORT[index]); } catch (UnknownHostException e) {
	 * System.out.println("UnknownHost: " + PropertiesConf.SOCKET_SERVER[index]
	 * + ":" + PropertiesConf.SOCKTE_PORT[index] + "\n" + e.getMessage());
	 * return false; } catch (IOException e) {
	 * System.out.println("Connnect to Server Faild: " +
	 * PropertiesConf.SOCKET_SERVER[index] + ":" +
	 * PropertiesConf.SOCKTE_PORT[index] + "\n" + e.getMessage()); return false;
	 * } System.out.println("Connect to Server " +
	 * PropertiesConf.SOCKET_SERVER[index] + ":" +
	 * PropertiesConf.SOCKTE_PORT[index]); return true;
	 * 
	 * }
	 */

	public static void main(String[] args) {
		/*
		 * HdfsTmsDataSendTask task = new HdfsTmsDataSendTask(1, null);
		 * task.process();
		 */
	}

}
