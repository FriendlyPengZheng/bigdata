$(function(){
    //time
    var $from = $("#J_from"),
        $date = $("#J_date");

	// select time
    $date.datepick({
        rangeSelect: false,
        multiSelect: 10,
        autoWidth: true,
        monthsToShow: 1,
        monthsToStep: 1,
        monthsOffset: 0,
        maxDate: new Date(),
        onClose: function(userDate) {
            if(!userDate.length){ $date.val($from.val()); }
            _setDateWidth();
            $from.val($date.val().split(",").join(","));
            _refreshData();
        }
    });
    $("#J_fromTime").click(function(e){
        e.stopPropagation();
        $date.focus();
    });
    $("#J_rlcontrast").click(function(){
        var curDate = $.date.getNow(),
            dateStr = curDate;
        if($(this).is(":checked")){
            curDate = ($("#J_from").val().split(","))[0];
            for(var i = 1; i < 8; i++){
                dateStr += ',' + $.date.getDate(curDate, -i);
            }
        }
        $from.val(dateStr);
        $date.val(dateStr);
        _setDateWidth();
        _refreshData();
    });

    ajaxData(getUrl("topic", "signtransext", "getSigntransGame"), null, function(data){
        _selectFac(_handleSelect(data), $("#J_transGame"));
        _gModule();
    });
});
//set target's width
function _setDateWidth(){
    var $date = $("#J_date"),
        width = $date.val().length * 7;
    $date.width(width > 300 ? 300 : (width < 70 ? 70 : width));
}
/**
 * @brief _selectFac
 * 生成列表并绑定事件
 * @param data array 列表
 * @return
 */
function _selectFac(data, container){
    $.Select.setOptionContent(container, data);
    $.Select.bindEvents(container, false, function(){
        if ($("#J_aside").find(".cur.parent").attr("data-key") === "signtrans") _changeStep();
        _refreshData();
    });
}
function _changeStep(){
    var stepArr = [lang.t("验证密码"), lang.t("创建角色"), lang.t("登陆online")];
    if(_getGameInfo().game_id == 1) stepArr = [lang.t("验证密码"), lang.t("登陆online"), lang.t("创建角色")];
    $("#J_realtimeWrap").find(".tabs-list .tabs-control").each(function(i){
        $(this).attr("title", stepArr[i]).find("a").text(stepArr[i]);
    });
}
function _handleSelect(data){
    var rlt = [];
    $.each(data, function(index, val){
        rlt.push({
            id: (val.game_id + "|" + val.gpzs_id),
            name: val.game_name,
            selected: (index === 1 ? true : false)
        })
    });
    // if(rlt[0]['id'].split('|')[0] == 1) {
    //     remove = rlt.splice(0,1);
    //     rlt.push(remove[0]);
    // }
    return rlt;
}
/**
 * @brief _g_module
 * 根据配置生成页面
 * @return
 */
function _gModule(){
    $.getJSON("json/" + window.responseData.locale + "/topic/" + _getModuleKey() + ".json" , null, function(data) {
        var prepared = [];
        $(data).each(function(i) {
            if(i == 0) this.attr.id = "J_realtimeWrap";
            this.container = $("#J_contentBody");
            this.tabsSkin = "orange";
            if(i == 0 && this.type == "tabs" && this.tabsSkin != ""){
                $("#J_contentHeader").css({ 'position' : 'absolute', 'top' : '10px', 'right' : '10px' });
            }
            prepared.push(this);
        });
        $("#J_contentBody").data("content-data", fac(prepared));
        if ($("#J_aside").find(".cur.parent").attr("data-key") === "signtrans") _changeStep();
    });
}

/**
 * @brief _getModuleKey
 * get module key from aside
 * game_type - aside_parent - aside_child
 * @return
 */
function _getModuleKey(){
    var parentKey = '',
        childKey = '';
    $("#J_aside").find(".cur").each(function(){
        var t = $(this);
        if(t.hasClass("parent")){
            parentKey = t.attr("data-key");
        } else if(t.hasClass("child")) {
            childKey = t.attr("data-key");
        }
    });
    return 'topic' + '-' + parentKey + (childKey ? '-' + childKey : '');
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
function _getTimePeriod(){
    var from = ($("#J_from").val()).split(","),
        rlt = {};
    for(var i = 0; i < from.length; i++){
        rlt["from[" + i + "]"] = from[i];
        rlt["to[" + i + "]"] = from[i];
    }
    return rlt;
}
function _getGameInfo(){
    // $("#J_transGame").find(".select-item").eq(0).toggleClass('selected-item');
    // $("#J_transGame").find(".select-item").eq(1).toggleClass('selected-item');
    ids = $("#J_transGame").find(".selected-item").attr("data-id").split("|");
    return { game_id: ids[0], gpzs_id: ids[1] };
}
window.getPageParam = function(){
    return $.extend(_getGameInfo(), _getTimePeriod());
}
