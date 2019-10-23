package com.taomee.tms.mgr.module;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class CheckAdminAuthority {
	
	@Autowired
	private UserService userService;
	@Autowired
	private UrlService urlService;
	
	@Pointcut("execution(* com.taomee.tms.mgr.ctl.common.GameController.*(..))"
			+ "execution(* com.taomee.tms.mgr.ctl.common.PageController.*(..)) || "
			+ "execution(* com.taomee.tms.mgr.ctl.common.MetadataController.*(..)) || "
			+ "execution(* com.taomee.tms.mgr.ctl.common.CommentController.*(..)) || "
			+ "execution(* com.taomee.tms.mgr.ctl.common.LogController.*(..)) || "
			+ "execution(* com.taomee.tms.mgr.ctl.common.ServerController.*(..)) || "
			+ "execution(* com.taomee.tms.mgr.ctl.common.SchemaController.*(..)) || "
			+ "execution(* com.taomee.tms.mgr.ctl.common.TaskController.*(..))")
	public void adminAuthorityCheck(){}
	
	
	@SuppressWarnings("unchecked")
	@Before("adminAuthorityCheck()")
	public void checkAdminAuthority() {
		if(userService.checkSession()) {
			String isAdmin = userService.getIsAdmin();
			if(!isAdmin.equals("1")) {
				userService.logout();
				urlService.deleteLastUrl();
			} 
		}
	}

}
