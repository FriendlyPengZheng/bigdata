package com.taomee.tms.mgr.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taomee.tms.mgr.ctl.ConfigService;

@Service
public class UrlService {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	
	public void SetLastUrl(HttpServletResponse res, String lastUrl) {	
		if(lastUrl == null || lastUrl.isEmpty()) {
			CookieUtils.editCookie(request, res, "last_url", null, null);
			return;
		}
		CookieUtils.addCookie(res, "last_url", lastUrl, null);
	}
	
	public String getLastUrl() {
		String lastUrl = CookieUtils.getCookieValue(request, "last_url");
		if(lastUrl == null || lastUrl.isEmpty()){
			return ConfigService.baseicUrl;
		}
		return CookieUtils.getCookieValue(request, "last_url");
	}
	
	public void deleteLastUrl() {
		CookieUtils.delCookie(request, response, "last_url");
	}

}
                                                                    