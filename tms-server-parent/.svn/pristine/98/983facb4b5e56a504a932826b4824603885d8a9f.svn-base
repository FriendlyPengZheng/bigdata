package com.taomee.tms.mgr.beans;

import java.io.Serializable;

/**
 * Author looper.
 * Company  TaoMee.Inc, ShangHai.
 * Date  2017/7/5.
 * stid+sstid 到Logid的信息映射bean
 */
public class StidSStidRefLogDaily implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1044487187273654820L;
	
	private Long id; //stid映射日志修改表自增id
	private Integer curd;//1.表示add,2.表示修改(remove)状态，但是对于内存那边来说就是删除状态。
    private String stid;
    private String sstid;
    private Integer gid;
    private Integer logid;//stid+sstid映射到新统计的logid信息


    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getSstid() {
        return sstid;
    }

    public StidSStidRefLogDaily() {
		//super();
	}

	public StidSStidRefLogDaily(Long id, Integer curd, String stid,
			String sstid, Integer gid, Integer logid) {
		super();
		this.id = id;
		this.curd = curd;
		this.stid = stid;
		this.sstid = sstid;
		this.gid = gid;
		this.logid = logid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCurd() {
		return curd;
	}

	public void setCurd(Integer curd) {
		this.curd = curd;
	}

	public void setSstid(String sstid) {
        this.sstid = sstid;
    }

    public Integer getLogid() {
        return logid;
    }

    public void setLogid(Integer logid) {
        this.logid = logid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    

}
