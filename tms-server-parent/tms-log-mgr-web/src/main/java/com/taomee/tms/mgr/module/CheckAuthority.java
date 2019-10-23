package com.taomee.tms.mgr.module;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

public class CheckAuthority {
	@Autowired
	private UserService userService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private GameService gameService;
	@Autowired
	private UrlService urlService;
	
	
	public Boolean checkAuthority(){
		int gameId = 0;
		if ("".equals(request.getParameter("game_id")) || request.getParameter("game_id") == null) {
			return false;
		}
		gameId = Integer.parseInt(request.getParameter("game_id"));
		
		List<Integer> gameIds = gameService.getGameIdsByViewAuth();
		if(gameIds == null) {
			//session过期
			return false;
		}
		
		if(!gameIds.contains(gameId)) {
			return false;
		}
		
		return true;
	}

}
