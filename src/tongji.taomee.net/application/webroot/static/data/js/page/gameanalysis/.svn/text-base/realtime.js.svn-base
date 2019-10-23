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
    $("#J_fakeContrast").click(function(){
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
    getZoneServer(_getPlatformId(), function( data ){
        _zoneServerFac( data );
        gModule();
    });
    $("#J_platform").tmselect().change(function(e){
        getZoneServer($(this).find(":selected").attr("data-id"), function( data ){
            _zoneServerFac( data, true );
        });
    });
}
//set target's width
function _setDateWidth(){
    var $date = $("#J_date"),
        width = $date.val().length * 7;
    $date.width(width > 300 ? 300 : (width < 70 ? 70 : width));
}
function _getPlatformId(){
    return $("#J_platform").find(":selected").attr("data-id");
}
/**
 * @brief _zoneServerFac
 * 生成区服列表并绑定事件
 * @param data array 区服列表
 * @return
 */
function _zoneServerFac( data, firstClick ){
    firstClick = firstClick ? firstClick : false;
    var $zoneServer = $("#J_zoneServer");
    $.Select.setOptionContent( $zoneServer, data );
    $.Select.bindEvents( $zoneServer, firstClick, function(){
        _refreshData();
    });
}
/**
 * @brief _refreshData
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
 * @brief getZoneServer
 * 获取区服列表
 * @param id ：平台id
 * @return
 */
function getZoneServer( id, fn ){
    ajax(getUrl("common", "gpzs", "getZoneServer"), {
        game_id : $("#J_paramGameId").val(),
        platform_id : id
    }, function(res){
        if (res.result == 0) {
            var data = [],
                zoneId = $("#J_paramZoneId").val(),
                serverId = $("#J_paramServerId").val(),
                zsFound = selected = false;
            $.each( res.data, function( i ){
                selected = false;
                if (!zsFound && this.zone_id == zoneId && this.server_id == serverId) {
                    zsFound = true;
                    selected = true;
                }
                data.push({
                    id : this.gpzs_id + "_" + this.zone_id + "_" + this.server_id,
                    name : ( this.zone_id == -1 && this.server_id == -1 ) ? lang.t("全区全服") : this.gpzs_name,
                    selected : selected
                });
            });
            // 没有选中则默认第一个
            if (!zsFound && data) {
                data[0].selected = true;
            }
            if( fn )fn( data );
        } else {
            say(lang.t("获取数据错误：") + res.err_desc);
        }
    });
}
window.getPageParam = function() {
    var gpzs = $("#J_zoneServer").find(".selected-item").attr("data-id"),
        gpzs = gpzs.split("_");
    return $.extend({}, {
        server_id : gpzs[2],
        gpzs_id : gpzs[0],
        zone_id : gpzs[1],
        platform_id : _getPlatformId(),
        game_id : $("#J_paramGameId").val()
    }, _getTimePeriod());
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
