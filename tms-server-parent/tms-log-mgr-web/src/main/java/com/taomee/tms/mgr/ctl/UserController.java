package com.taomee.tms.mgr.ctl;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taomee.tms.mgr.module.SSOClient;
import com.taomee.tms.mgr.module.UrlService;
import com.taomee.tms.mgr.module.UserService;

@Controller  
@RequestMapping("/user") 
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UrlService urlService;
	@Autowired
	private SSOClient ssoClient;

	
	@RequestMapping(value = "/login")
	public void login(PrintWriter printWriter, String user_name, String user_pwd){
		userService.login(user_name, user_pwd);
		String lastUrl = urlService.getLastUrl();
		if(lastUrl == null || lastUrl.isEmpty()) {
			lastUrl = "gameanalysis/mobilegame/overview/index/01";
		}
		
		printWriter.write("{\"result\":0,\"data\":\""+lastUrl+"\"}"); 
		//printWriter.write("{\"result\":0,\"data\":\"gameanalysis/mobilegame/overview/index/01\"}");
        printWriter.flush(); 
        printWriter.close();
	}
	
	@RequestMapping(value= "/home")
	public String home(Model model){
		//model.addAttribute("testattr2","99999");
		return "home";
	}
	
	@RequestMapping(value = "/logout")
	public String logout(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		if(ConfigService.useSSO) {
			if(ssoClient.logout()){
				userService.logout();
			}
		} else {
			userService.logout();
		}
		return "home";
	}
	
	@RequestMapping(value = "setAuthority")
	public String setAuthority(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		
		return null;
	}
}
