package com.taomee.bigdata.hbase.bean;

import java.io.Serializable;

/**
 * Created by looper on 2017/3/31.
 */
//| server_id | data_id | time       | value |
// data_id  建议用long 类型
public class ResultBean  implements Serializable{
	private static final long serialVersionUID = -642959769720678251L;
	private Integer serverId ;
    private Integer dataId ;
    private Long time ;
    private Double value ;

    public ResultBean(Integer serverId, Integer dataId, Long time, Double value) {
        this.serverId = serverId;
        this.dataId = dataId;
        this.time = time;
        this.value = value;
    }

    public ResultBean() {
        //this.serverId = serverId;
    }

    @Override

    public String toString() {
        return "ResultBean{" +
                "serverId=" + serverId +
                ", dataId=" + dataId +
                ", time=" + time +
                ", value=" + value +
                '}';
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
