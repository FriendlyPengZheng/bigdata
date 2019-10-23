$(function(){
    _gTools();
});
/**
 * @brief _gTools
 * 有关页面首部工具区功能
 * 时间功能 平台选择功能 区服功能
 * @return
 */
function _gTools(){
    //time
    var $from = $("#J_from"),
        $to = $("#J_to"),
        $date = $("#J_date");
	// select time
    $date.datepick({
        rangeSelect: true,
        monthsToShow: 3,
        monthsToStep: 3,
        monthsOffset: 2,
        shortCut : true,
        maxDate: new Date(),
        onClose: function(userDate) {
            //判断是否是同一时间
            if( userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != $from.val()
                || $.datepick.formatDate("yyyy-mm-dd", userDate[1]) != $to.val()) ){
                var userDate = $date.val().split("~");
                userDate[0] = $.trim(userDate[0]);
                userDate[1] = $.trim(userDate[1]);
                $from.val(userDate[0]);
                $to.val(userDate[1]);
                _refreshData();
            }
        }
    });
    $("#J_from_to").click(function(e){
        $date.focus();
        e.stopPropagation();
    });

    gModule();
}
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
    return {
        "from[0]" : $("#J_from").val(),
        "to[0]" : $("#J_to").val()
    }
}
/**
 * @brief function : getTheadByName
 * 根据数据名称拼写table thead的值
 * for tm.module.js
 * @param data：Thead数据
 * @param o:Thead显示选项
 *
 * @return
 */
window.getTheadByName = function(data, o){
    var arr = [];
    if(data){
        $(data).each(function(){
            if(this.key && this.key[0] && $.date.isDate(this.key[0])) {
                arr.push({ type: "date", title: lang.t("日期") });
            } else{
                arr.push({ type: "string", title: lang.t("注册步骤"), disortable: true });
            }
            $(this.data).each(function(){
                arr.push({
                    type: "percentage",
                    title: this.name ? this.name : ""
                });
            });
        });
    }
    return arr;
}
