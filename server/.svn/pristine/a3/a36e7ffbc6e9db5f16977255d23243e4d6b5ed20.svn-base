package com.taomee.bigdata.client;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 流描述符
 * @author cheney
 * @date 2013-11-14
 */
public class Streamer {

	private String file;					//comleted path;
	private InputStream inputStream;		//file、path流描述符
	
	//prams
	private byte data_type;
	private int time;
    private int hour;
	
	private int taskid = -1;	//default no taskid

	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public Streamer get_params() {
		return this;
	}
	public void set_params(Streamer otherStreamer) {
		this.data_type = otherStreamer.data_type;
        this.time = otherStreamer.time;
        this.taskid = otherStreamer.taskid;
        this.hour = otherStreamer.hour;
	}
    public void setData_type(String d) {
        data_type = (byte)Integer.parseInt(d,16);
    }
    public void setTime(String t) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date d = df.parse(t);
        time = (int) (d.getTime()/1000);
    }
    public void setHour(String h) {
        this.hour = Integer.parseInt(h);
    }
    public void setHour(int h) {
        this.hour = h;
    }
	public byte getData_type() {
		return data_type;
	}
	public int getTime() {
		return time;
	}
	public int getTaskid() {
		return taskid;
	}
    public int getHour() {
        return hour;
    }

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = Integer.parseInt(taskid);
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
}
