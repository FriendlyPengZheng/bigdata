package com.taomee.tms.client;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * tms hdfs流文件类说明
 * @author looper
 */
public class TmsStreamer {

	private String file; // comleted path;
	private InputStream inputStream; // file、path流描述符
	private String artifactId;   //default(基础加工项) 的  artifactId为-1

	// prams
	private byte data_type;

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		if (artifactId.equals("-1")) {
			this.artifactId = "-1";
		} else {
			this.artifactId = artifactId;
		}
	}

	private int time;
	private int hour;

	private int taskid = -1; // default no taskid

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public TmsStreamer get_params() {
		return this;
	}

	public void set_params(TmsStreamer otherStreamer) {
		this.data_type = otherStreamer.data_type;
		this.time = otherStreamer.time;
		this.taskid = otherStreamer.taskid;
		this.hour = otherStreamer.hour;
	}

	public void setData_type(String d) {
		data_type = (byte) Integer.parseInt(d, 16);
	}

	public void setTime(String t) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date d = df.parse(t);
		time = (int) (d.getTime() / 1000);
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
