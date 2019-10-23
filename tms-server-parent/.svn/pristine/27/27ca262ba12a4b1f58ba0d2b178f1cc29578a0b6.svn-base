/**
 * 
 */
package com.taomee.tms.mgr.tools;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author sevin
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message implements Serializable{
	private int result ;
    private String err_desc ;
    private Object data = null;
    /**
     * 自定义返回
     * @param code
     * @param message
     * @return
     */
    public Message setMessage(int result, String err_desc){
        this.setResult(result);
        this.err_desc = err_desc;
        return this;
    }

    /**
     * 返回成功
     * @return
     */
    public Message setSuccessMessage(){
        this.setResult(MessageCode.SUCCESS);
        this.err_desc = "操作成功";
        return this;
    }

	/**
     * 返回成功
     * @param message
     * @return
     */
    public Message setSuccessMessage(String message){
        this.setResult(MessageCode.SUCCESS);
        this.err_desc = message;
        return this;
    }

    /**
     * 返回错误
     * @param message
     * @return
     */
    public Message setErrorMessage(String message){
        this.setResult(MessageCode.ERROR);
        this.err_desc = message;
        return this;
    }

    /**
     * 返回警告
     * @param message
     * @return
     */
    public Message setWarnMessage(String message){
        this.setResult(MessageCode.WARN) ;
        this.err_desc = message ;
        return this;
    }

    /**
     * 返回登录失败
     * @param message
     * @return
     */
    public Message setLoginFailMessage(String message){
        this.setResult(MessageCode.LOGIN_FAILED) ;
        this.err_desc = message ;
        return this;
    }

    /**
     * 返回没有权限
     * @param message
     * @return
     */
    public Message setPermissionDeniedMessage(String message){
        this.setResult(MessageCode.PERMISSION_DENIED) ;
        this.err_desc = message ;
        return this;
    }

	public Object getData() {
		return data;
	}

	public Message setData(Object data) {
		this.data = data;
		return this;
	}

	/**
	 * @return the result
	 */
	public int getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(int result) {
		this.result = result;
	}

	public String getErr_desc() {
		return err_desc;
	}

	public void setErr_desc(String err_desc) {
		this.err_desc = err_desc;
	}
}
