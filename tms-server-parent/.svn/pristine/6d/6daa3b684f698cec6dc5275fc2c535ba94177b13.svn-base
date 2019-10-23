package com.taomee.tms.mgr.tools.DataCaculator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.taomee.tms.mgr.api.LogMgrService;



// import java.util.Iterator;
// 配置类，不实现业务逻辑，只是生产bean


/**
 * @author window
 * @brief　configration
 */
@Configuration
@Component
public class CaculateConfigration {
	
	@Bean
	public static AbstactCaculateDataTools getCaculateDataTools(String expr, 
																Integer index,
																String fromDateString,
																String toDateString,
																HttpServletRequest request, 
																LogMgrService logMgrService,
																Integer period) {
		// period = 2;
		switch (period) {
		case 1:
			return new DayCaculateDataTools(expr, index, fromDateString, toDateString, request, logMgrService);
		case 2:
			return new WeekCaculateDataTools(expr, index, fromDateString, toDateString, request, logMgrService);
		case 3:
			return new MonthCaculateDataTools(expr, index, fromDateString, toDateString, request, logMgrService);
		case 4:
			return new MinCaculateDataTools(expr, index, fromDateString, toDateString, request, logMgrService);
		case 5:
			// 小时数据
			return new HourCaculateDataTools(expr, index, fromDateString, toDateString, request, logMgrService);
		case 6:
			return new VersionWeekCaculateDataTools(expr, index, fromDateString, toDateString, request, logMgrService);
		case 7:
			return new ExprCaculateDataTools(expr, index, fromDateString, toDateString, request, logMgrService);
		default:
			// TODO 统一错误处理
			return null;
		}
	}									
}