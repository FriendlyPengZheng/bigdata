package com.taomee.tms.mgr.module;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.ctl.ConfigService;


@Service
public class SSOClient {
	private static final Logger logger = LoggerFactory
			.getLogger(SSOClient.class);
	
	private String appId;
	private String expir;
	private String systemUrl;
	private String authKey;
	private String cookieKey;
	private String loginUrl;
	private String powerUrl;
	
	@Autowired
	private UrlService urlService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UserService userService;
	
	SSOClient() {
		ConfigService config = new ConfigService();
		this.appId = config.getAppId();
		this.expir = config.getExpir();
		this.systemUrl = config.getSystemUrl();
		this.authKey = config.getAuthKey();
		this.cookieKey = config.getCookieKey();
		this.loginUrl = config.getLoginUrl();
		this.powerUrl = config.getPowerUrl();
	}
	
	
	public String getLoginUrl() {
		String queryString = "app_id=" + appId + "&expire=" + expir + "&app_key=" + _getAppKey() + "&refer=";
		String lastUrl = urlService.getLastUrl();
		System.out.println("lastUrl:" + lastUrl);
		String refer = null;
		if(lastUrl == null || lastUrl.isEmpty() || lastUrl.equals(ConfigService.baseicUrl)) {
			System.out.println("=========1===========");
			refer = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath()+"/" + ConfigService.baseicUrl;
		} else {
			System.out.println("=========2===========");
			refer = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + urlService.getLastUrl();
		}
		System.out.println("refer:"+refer);
		String url = null;
		try {
			url = loginUrl + "?" + queryString + URLEncoder.encode(refer,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("$$$$$$$$$:"+url);
		return url;
	}
	
	public Boolean isLogin() {
		String data = _request("1001");
		System.out.println("data:" + data);
		if(data == null || data.isEmpty() || data.equals("false")) {
			return false;
		}
		
		JSONObject dataObject = null;
		try{
			dataObject = JSONObject.parseObject(data); 
		}catch(Exception e){
		    return false;
		};
		
		if(dataObject == null || dataObject.isEmpty()){
			return false;
		}
		System.out.println("dataObject:" + dataObject);
		
		if(!dataObject.getInteger("status_code").equals(0)) {
			return false;
		}
		
		JSONObject userInfo = dataObject.getJSONObject("user_info");
		if(userInfo == null || userInfo.isEmpty()) {
			return false;
		}
		
		userService.setUserName(userInfo.getString("user_name"));
		userService.setUserId(userInfo.getString("user_id"));		
		return true;
	}
	
	public Boolean logout() {
		String data = this._request("1003");
		if(data == null || data.isEmpty() || data.equals("false")) {
			return false;
		}
		
		JSONObject dataObject = JSONObject.parseObject(data);
		System.out.println("dataObject:" + dataObject);
		if(!dataObject.getInteger("status_code").equals(0)) {
			return false;
		}
		
		return true;
	}
	
	private String _request(String cmd) {
		String queryString = "cmd=" + cmd + "&sso_key=" + this._getSSOKey() +
						"&app_id=" + appId + "&app_key=" + this._getAppKey();
		
		System.out.println("queryString:" + queryString);
		return HttpRequest.sendGet(powerUrl, queryString);
	}

	private String _getSSOKey() {
		String key = null;
		key = CookieUtils.getCookieValue(request, cookieKey);
		if(key == null || key.isEmpty()) {
			key = "";
		}
		return key;
	}
	
	private String _getAppKey() {
		return getMD5(appId + authKey);
	}
	
	public static String getMD5(String str) {
		// 生成一个MD5加密计算摘要
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // 计算md5函数
	    md.update(str.getBytes());
	    // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
	    // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
	    return new BigInteger(1, md.digest()).toString(16);
	}
	
}
