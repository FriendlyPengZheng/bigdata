package com.taomee.tms.mgr.ctl;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taomee.tms.mgr.ctl.common.LogController;
import com.taomee.tms.mgr.module.SSOClient;
import com.taomee.tms.mgr.module.UrlService;
import com.taomee.tms.mgr.module.UserService;

@Controller 
public class LoginController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(LoginController.class);
	
	private static final String loginUrl = "user/login";
	
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private SSOClient ssoClient;
	@Autowired
	private UserService userService;
	@Autowired
	private UrlService urlService;
	
	@RequestMapping(value = "/to_login") 
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		if(!ConfigService.useSSO) {
			return loginUrl;
		}

		if(ssoClient.isLogin()) {
			userService.setAuthority(null);
			String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + urlService.getLastUrl();
			System.out.println("isLogin url:" + url);
			return "redirect:" + url;
		}
		String url = ssoClient.getLoginUrl();
		//System.out.println("******:" + url);
		return "redirect:" + url;
	}
	
	@RequestMapping(value = "/empty") 
	public String empty(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		return null;
	}
}
