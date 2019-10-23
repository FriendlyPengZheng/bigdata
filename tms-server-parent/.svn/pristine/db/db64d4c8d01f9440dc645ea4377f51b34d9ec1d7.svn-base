package com.taomee.tms.mgr.ctl;

public class ConfigService {
	
	private static final Boolean RELEASE = false;

	public static boolean useSSO = false;
	public static String  baseicUrl = "gameanalysis/mobilegame/overview/index/01";
	
	private String powerSystem;
	private String[] admin;
	private String appId;
	private String expir;
	private String systemUrl;
	private String authKey;
	private String cookieKey;
	private String loginUrl;
	private String powerUrl;

	public ConfigService() {
		if(RELEASE) {
			this.setPowerSystem("http://am-server.taomee.net/index.php");
			String[] tmp = {"3081"};
			this.setAdmin(tmp);
			this.setAppId("24");
			this.setExpir("86400");
			this.setSystemUrl("");
			this.setAuthKey("TM_SSO");
			this.setCookieKey("_TM_SSO");
			this.setLoginUrl("http://home.taomee.net/index.php");
			this.setPowerUrl("http://home.taomee.net/api.php");
		} else {
			this.setPowerSystem("http://am-server.taomee.net/index.php");
			String[] tmp = {"3081"};
			this.setAdmin(tmp);
			this.setAppId("24");
			this.setExpir("86400");
			this.setSystemUrl("");
			this.setAuthKey("TM_SSO");
			this.setCookieKey("_TM_SSO");
			this.setLoginUrl("http://home.taomee.net/index.php");
			this.setPowerUrl("http://home.taomee.net/api.php");
		}
		
	}

	public String getPowerSystem() {
		return powerSystem;
	}

	public void setPowerSystem(String powerSystem) {
		this.powerSystem = powerSystem;
	}

	public String[] getAdmin() {
		return admin;
	}

	public void setAdmin(String[] admin) {
		this.admin = admin;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getExpir() {
		return expir;
	}

	public void setExpir(String expir) {
		this.expir = expir;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getSystemUrl() {
		return systemUrl;
	}

	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}

	public String getCookieKey() {
		return cookieKey;
	}

	public void setCookieKey(String cookieKey) {
		this.cookieKey = cookieKey;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getPowerUrl() {
		return powerUrl;
	}

	public void setPowerUrl(String powerUrl) {
		this.powerUrl = powerUrl;
	}


}
