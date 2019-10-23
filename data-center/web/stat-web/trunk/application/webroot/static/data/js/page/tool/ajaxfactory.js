/**
 * @fileOverview Ajax factory 
 * @name ajaxfactory.js
 * @author Maverick
 */

/**
 * 拉取游戏指标的工厂方法
 * @param { String } url 用于 getUrl 参数的 URL 字符串
 * @returns { Function } 返回实例方法
 */
var _selfhelpFactory = function (url) {
	var urlStr = url.split('/');

	return function (param, container) {
		var responseData = [];

		ajaxDataSync(getUrl(urlStr[0], urlStr[1], urlStr[2]),    // URL is binded with the instance
					 param,
					 function (data) {
						 for(var i = 0; i < data.length; i++) {
							 var ele = {};

							 for(var j = 0; j < container.length; j++) {
								 ele[container[j]] = data[i][container[j]];
							 }

							 responseData.push(ele);
						 }
						 return responseData;
					 });

		return responseData;
	};
};

/**
 * synchronous ajax request
 * @param { String } url 通过 getUrl 得到的 URL 字符串
 * @param { Object } param 用于 AJAX 请求的参数
 * @param { Function } fn 回调函数
 * @param { Boolean } hide 是否显示 overlayer
 * @param { Boolean } empt 发生请求错误时，是否 say
 */
var ajaxDataSync = function (url, param, fn, hide, empt) {
	if (hide) { overlayer({ text: lang.t("操作中") }); }
	
	ajax(url, param, function(res) {
		if(0 == res.result) {
			if (hide) { hidelayer(lang.t("操作成功~.~")); } 
			if (fn) { fn(res.data); } 
		} else {
			if (hide) { hidelayer(); } 
			if (empt) {
				if (fn) { fn([]); }
			} else { say(lang.t("获取数据错误：") + res.err_desc); }
		}
	}, "POST", false);    // here async is false
};

/**
 * asynchronous ajax request
 * @param { String } url 通过 getUrl 得到的 URL 字符串
 * @param { Object } param 用于 AJAX 请求的参数
 * @param { Function } fn 回调函数
 * @param { Boolean } hide 是否显示 overlayer
 * @param { Boolean } empt 发生请求错误时，是否 say
 */
var ajaxDataAsync = function (url, param, fn, hide, empt) {
	if (hide) { overlayer({ text: lang.t("操作中") }); }
	
	ajax(url, param, function(res) {
		if(0 == res.result) {
			if (hide) { hidelayer(lang.t("操作成功~.~")); } 
			if (fn) { fn(res.data); } 
		} else {
			if (hide) { hidelayer(); } 
			if (empt) {
				if (fn) { fn([]); }
			} else { say(lang.t("获取数据错误：") + res.err_desc); }
		}
	}, "POST");    // here async is false
};
