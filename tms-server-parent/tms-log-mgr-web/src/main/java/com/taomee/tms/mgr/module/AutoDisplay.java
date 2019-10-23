package com.taomee.tms.mgr.module;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.taomee.tms.mgr.entity.GameInfo;

@Aspect
public class AutoDisplay {
	@Autowired
	private UserService userService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private NavigatorService navigatorService;
	@Autowired
	private GameService gameService;
	@Autowired
	private UrlService urlService;
	
	@Pointcut("execution(* org.springframework.web.servlet.View.render(java.util.Map,..))")
	public void renderModel(){}
	
	@Around("renderModel()")
	public void assignNavi(ProceedingJoinPoint jp) throws Throwable{
		System.out.println("url:" + request.getRequestURL() + "?" +request.getQueryString());
		String uri = request.getRequestURI();
		String query = request.getQueryString();
		HttpServletResponse response = (HttpServletResponse) jp.getArgs()[2];
		
		if(request.getRequestURI().equals(request.getContextPath() + "/")
				|| request.getRequestURI().equals(request.getContextPath() + "/to_login")) {
			jp.proceed(jp.getArgs());
			return;
		}
		
		//if(query != null && query.equals("r=f/c/a") || query.equals("r=common/stat/getIndicators")) {
		if(uri.contains("/index.php")) {
			response.sendRedirect(request.getContextPath() + "/empty");
			Object[] args = {jp.getArgs()[0],jp.getArgs()[1],response};
			jp.proceed(args);
			return;
		}
		
		if(!userService.checkSession()) {
			response.sendRedirect(request.getContextPath());
			Object[] args = {jp.getArgs()[0],jp.getArgs()[1],response};
			jp.proceed(args);
			return;
		}
		
		String pageUrl = request.getRequestURL().toString();
		//设置最后一次url连接
		if(query != null) {
			String lastUrl = uri+"?"+request.getQueryString();
			urlService.SetLastUrl(response, lastUrl);
		}
		
		int gameId = 0;
		String gameIdReq = request.getParameter("game_id");
		Object gameobj = httpSession.getAttribute("game_id");
		String gameIdSes = (gameobj == null)? null : gameobj.toString();
		if (gameIdReq != null && !("".equals(gameIdReq))) {
			gameId = Integer.parseInt(gameIdReq);			
		} else if (gameIdSes != null && !("".equals(gameIdSes))) {
			gameId = Integer.parseInt(gameIdSes);
		} else {
			ArrayList<Integer> tempFunc = new ArrayList<Integer>();
			tempFunc.add(5);//这里写死是游戏分析
			Map<Integer, GameInfo> gameInfo = gameService.getIdGroupedGameByAuth(tempFunc);
			for(int id : gameInfo.keySet()){
				if (gameInfo.get(id) != null) {
					gameId = id;
					break;
				}
			}
		}		
		httpSession.setAttribute("game_id", gameId);
		
		@SuppressWarnings("unchecked")
		Map<String,Object> tempModel = (Map<String, Object>) jp.getArgs()[0];
		Map<String, Object> navigatorInfo = navigatorService.getNavigator();
		tempModel.put("navigator", navigatorInfo);
		//Map<String,	Object> testmanageInfo = navigatorService.getNavigatorForManage();
		Map<String, String> userInfo = userService.getUserInfo();
		tempModel.put("user", userInfo);
		tempModel.put("game_id", gameId);
		tempModel.put("page_url", pageUrl);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()+"/";
		tempModel.put("base_url", basePath);
		tempModel.put("locale", "zh_CN");
		String logOutPath = basePath + "user/logout";
		tempModel.put("logOutPath", logOutPath);
		tempModel.put("isAdmin", userService.getIsAdmin());
		
		Object[] args = {tempModel,jp.getArgs()[1],jp.getArgs()[2]};
		jp.proceed(args);
	}
}
