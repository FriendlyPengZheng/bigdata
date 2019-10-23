package com.taomee.bigdata.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import com.taomee.common.schedule.task.AbstractTask;

/**
 * 数据发送task
 * @author cheney
 * @date 2013-11-07
 */
public class HdfsDataSendTask extends AbstractTask<Streamer> {
	
	private Streamer streamer;
	
	private Socket socket;
	private InputStream is;
	private InputStreamReader isr;
	private BufferedReader br;
	
	private OutputStream os;
	private InputStream bk;
	
	public HdfsDataSendTask(int state, CountDownLatch cdl){
		setState(state);
		this.countDownLatch = cdl;
	}
		
	@Override
	public void process() {
		try {
			
			boolean c = connect();
			if(!c){
				HdfsDataFetchJob.getInstance().feedBack(streamer);
				return;
			}
			os = socket.getOutputStream();
            bk = socket.getInputStream();
			
			streamer = get();
			
			is = streamer.getInputStream();
			
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			String line;
			byte[] buf = null;
			TranProtocol pt = null;
			while ( (line = br.readLine()) != null){
                for(int i=0; i<10; i++) {
                    pt = DataParser.parser(line, streamer);
                    try {
                        buf = pt.pack();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        break;
                    }
                    if(buf != null) {
                        os.write(buf, 0, buf.length);
                        bk.read(buf, 0, 9);
                        if(buf[8] == 0) {
                            //System.out.println(String.format("insert [%s] return %d, OK", line, buf[8]));
                            break;
                        }
                        System.err.println(String.format("insert [%s] return %d, try again ...", line, buf[8]));
                    }
                }
                Thread.sleep(1);
			}
			
			System.out.println(streamer.getFile() + " have finished over.");
            Thread.sleep(1000);
			
			//IOUtils.copyBytes(is, os, 10);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(os != null) os.close();
				if(socket != null) socket.close();
				if(br != null) br.close();
				if(isr != null) isr.close();
				if(is != null) is.close();
				if(bk != null) bk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(countDownLatch != null)
				countDownLatch.countDown();
			HdfsDataFetchJob.getInstance().feedBack(this);
		}
	}
	
	boolean connect() {
		int index = PropertiesConf.get();
		try {
			socket = new Socket(
					PropertiesConf.SOCKET_SERVER[index],
					PropertiesConf.SOCKTE_PORT[index]);
		} catch (UnknownHostException e) {
			System.out.println("UnknownHost: " 
						+ PropertiesConf.SOCKET_SERVER[index] + ":"
						+ PropertiesConf.SOCKTE_PORT[index] + "\n"
						+ e.getMessage());
			return false;
		} catch (IOException e) {
			System.out.println("Connnect to Server Faild: " 
					+ PropertiesConf.SOCKET_SERVER[index] + ":"
					+ PropertiesConf.SOCKTE_PORT[index] + "\n"
					+ e.getMessage());
			return false;
		}
        System.out.println("Connect to Server "
                + PropertiesConf.SOCKET_SERVER[index] + ":"
                + PropertiesConf.SOCKTE_PORT[index]);
		return true;
			
	}
	
	public static void main(String[] args) {
		HdfsDataSendTask task = new HdfsDataSendTask(1, null);
		task.process();
	}

}
