package com.taomee.tms.mgr.form;

public class GameForm {
	
	@Override
	public String toString() {
		return "GameInfo [gameId=" + game_id + ", gameName=" + game_name
				+ ", gameType=" + game_type + ", authId=" + auth_id
				+ ", mangeAuthId=" + manage_auth_id + ", status=" + status
				+ ", funcSlot=" + func_slot + ", onlineAuthId=" + online_auth_id 
				+ ", ignore=" + ignore + ", game_email=" + game_email
				+ "]";
	}

	private String game_id;
	private String game_name;
	private String game_type;
	private String auth_id;  //查看权限ID
	private String manage_auth_id; //管理权限ID
	private Integer status;   //0未使用 1使用 2 删除
	private String game_email;
	private Integer func_slot;  //功能槽
	private String online_auth_id; //在线统计权限ID
	private String ignore;
	
	public String getGame_id() {
		return game_id;
	}
	public void setGame_id(String game_id) {
		this.game_id = game_id;
	}
	public String getGame_name() {
		return game_name;
	}
	public void setGame_name(String game_name) {
		this.game_name = game_name;
	}
	public String getGame_type() {
		return game_type;
	}
	public void setGame_type(String game_type) {
		this.game_type = game_type;
	}
	public String getAuth_id() {
		return auth_id;
	}
	public void setAuth_id(String auth_id) {
		this.auth_id = auth_id;
	}
	public String getManage_auth_id() {
		return manage_auth_id;
	}
	public void setManage_auth_id(String manage_auth_id) {
		this.manage_auth_id = manage_auth_id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getGame_email() {
		return game_email;
	}
	public void setGame_email(String game_email) {
		this.game_email = game_email;
	}
	public Integer getFunc_slot() {
		return func_slot;
	}
	public void setFunc_slot(Integer func_slot) {
		this.func_slot = func_slot;
	}
	public String getOnline_auth_id() {
		return online_auth_id;
	}
	public void setOnline_auth_id(String online_auth_id) {
		this.online_auth_id = online_auth_id;
	}
	public String getIgnore() {
		return ignore;
	}
	public void setIgnore(String ignore) {
		this.ignore = ignore;
	}
	
}
 
