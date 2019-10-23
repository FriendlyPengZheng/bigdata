var MODULE = [];
var pageTabs = $();
$(function(){
    gModule();
});
/**
 * @brief _refresh_data
 * 刷新数据
 */
function _refreshData(){
    var modules = $("#J_contentBody").data("content-data");
    if (modules && modules.length) {
        $(modules).each(function() {
            this.refresh({
                dataChange: true
            });
        });
    }
};
/**
 * @brief ajaxData
 *
 * @param url
 * @param param
 * @param fn:回调函数
 * @param hide:是否显示overlayer
 * @param empt:发生请求错误时，是否say
 */
function ajaxData( url, param, fn, hide, empt ){
    if( hide ) overlayer({ text: lang.t("加载中...")});
    ajax( url, param, function(res){
        if(res.result == 0){
            if(hide) hidelayer(lang.t("加载成功~.~"));
            if(fn) fn(res.data);
        } else {
            if(hide) hidelayer();
            if(empt){
                if(fn)fn([]);
            } else {
                say(lang.t("获取数据错误：") + res.err_desc);
            }
        }
    }, "POST");
}

window.getPageParam = function(){
    return {};
}
