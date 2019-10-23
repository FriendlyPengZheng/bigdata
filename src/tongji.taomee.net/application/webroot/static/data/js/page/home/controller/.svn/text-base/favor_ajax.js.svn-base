/**
 * @fileOverview 我的收藏 Ajax 請求層
 * @name favor_ajax.js
 * @author Maverick youngleemails@gmail.com
 */

/**
 * @brief ajaxData
 *
 * @param url
 * @param param
 * @param fn:回调函数
 * @param hide:是否显示overlayer
 * @param empt:发生请求错误时，是否say
 */
function ajaxData(url, param, fn, hide, empt) {
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
    }, "POST");
}
