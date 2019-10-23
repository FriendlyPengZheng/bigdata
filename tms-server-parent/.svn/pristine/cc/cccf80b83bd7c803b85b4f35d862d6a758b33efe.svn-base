package com.taomee.tms.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import com.taomee.tms.common.schedule.task.AbstractTask;
import com.taomee.tms.mgr.entity.DataToObject;

/**
 * 测试新接口的协议,后面该类废弃
 * @author looper
 * @date 2017年5月18日 下午3:39:20
 * @project tms-hdfsFetchData NewHdfsTmsDataSendTask
 */
public class NewHdfsTmsDataSendTask extends AbstractTask<TmsStreamer> {

	private TmsStreamer streamer;

	private InputStream is;
	private InputStreamReader isr;
	private BufferedReader br;

	private OutputStream os;
	private InputStream bk;

	

	public NewHdfsTmsDataSendTask(int state, CountDownLatch cdl) {
		setState(state);
		System.out.println(state);
		this.countDownLatch = cdl;
	}

	
	public void process() {
		try {

			streamer = get();
		
			is = streamer.getInputStream();		
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				String lines[] = line.split("\t");
				System.out.println("lines的长度:" + lines.length);
				
				// lines=4,基础统计项信息拼接
				if (lines.length == 4) {
					DataToObject datatoObject = new DataToObject(
							streamer.getTaskid(), 
							streamer.getTime(),
							Integer.valueOf(lines[0]), 
							Integer.valueOf(streamer.getArtifactId()),
							Integer.valueOf(lines[1]), 
							lines[2],
							Double.valueOf(lines[3]));
				
					System.out.println("jichu:" + datatoObject);
					//logMgrService.insertUpdateDataResultInfo(datatoObject);

				} else if (lines.length == 3) {// lines.length=3,加工项信息拼接
					DataToObject datatoObject = new DataToObject(
							streamer.getTaskid(), streamer.getTime(),
							Integer.valueOf(streamer.getArtifactId()),
							Integer.valueOf(lines[0]), lines[1],
							Double.valueOf(lines[2]));
					System.out.println("jiagong:" + datatoObject);
					//logMgrService.insertUpdateDataResultInfo(datatoObject);
				}
			}

			
			Thread.sleep(1000);


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {			
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (countDownLatch != null)
				countDownLatch.countDown();
			NewHdfsDataFetchJob.getInstance().feedBack(this);
		}
	}

	public static void main(String[] args) {
		
	}

}
