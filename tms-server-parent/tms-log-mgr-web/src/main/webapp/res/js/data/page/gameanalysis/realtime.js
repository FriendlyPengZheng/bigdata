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
    gModule();
    /*getZoneServer(_getPlatformId(), function( data ){
    	_zoneFac( data );
    },"全区");
    
    getZoneServer(_getZoneId(), function( data ){
    	_serverFac( data );
    },"全服");
    
    $("#J_platform").tmselect().change(function(e){
    	getZoneServer($(this).find(":selected").attr("data-id"), function( data ){
        	_zoneFac( data, true );
        },"全区");
    });   */
    getZoneServer(_getPlatformId(), function( data ){
    	_zoneServerFac( data );
    },"全区全服");
    
    $("#J_platform").tmselect().change(function(e){
    	getZoneServer($(this).find(":selected").attr("data-id"), function( data ){
    		_zoneServerFac( data, true );
        },"全区全服");
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

/*function _getZoneId(){
    return $("#J_zone").find(".selected-item").attr("data-id");
}

function _getServerId(){
    return $("#J_server").find(".selected-item").attr("data-id");
}*/

function _getZSId(){
    return $("#J_zoneServer").find(".selected-item").attr("data-id");
}

function _zoneServerFac( data, firstClick ){
    firstClick = firstClick ? firstClick : false;
    var $zoneServer = $("#J_zoneServer");
    $.Select.setOptionContent( $zoneServer, data );
    $.Select.bindEvents( $zoneServer, firstClick, function(){
/*    	getZoneServer(_getZSId(), function( data ){
        	_serverFac( data, true );
        },"全服全服");*/
        _refreshData();
    });
}
/**
 * @brief _zoneFac
 * 生成区服列表并绑定事件
 * @param data array 区服列表
 * @return
 *//*
function _zoneFac( data, firstClick ){
    firstClick = firstClick ? firstClick : false;
    var $zoneServer = $("#J_zone");
    $.Select.setOptionContent( $zoneServer, data );
    $.Select.bindEvents( $zoneServer, firstClick, function(){
    	getZoneServer(_getZoneId(), function( data ){
        	_serverFac( data, true );
        },"全服");
        _refreshData();
    });
}

*//**
 * @brief _serverFac
 * 生成区服列表并绑定事件
 * @param data array 区服列表
 * @return
 *//*
function _serverFac( data, firstClick ){
    firstClick = firstClick ? firstClick : false;
    var $zoneServer = $("#J_server");
    $.Select.setOptionContent( $zoneServer, data );
    $.Select.bindEvents( $zoneServer, firstClick, function(){
        _refreshData();
    });
}*/
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
function getZoneServer( id, fn , zs_name){
	    ajax("../../../../common/gpzs/getZoneServer", {
        gameId : $("#J_paramGameId").val(),
        /*serverId : id*/
        platformId : id
    }, function(res){
        if (res.result == 0) {
            var data = [],
                zoneId = $("#J_paramZoneId").val(),
                serverId = $("#J_paramServerId").val(),
                zsFound = selected = false;
            data.push({
                id : "-1_-1",
                name : zs_name,
                selected : true
            });
            $.each( res.data, function( i ){
                data.push({
                    /*id : this.serverId,
                    name : this.serverName,*/
                	id : this.zoneServerId,
                	name : this.zoneServerName,
                    selected : false
                });
            });
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
/*    var gpzs = $("#J_zone").find(".selected-item").attr("data-id"),
        gpzs = gpzs.split("_");*/
    return $.extend({}, {
        /*server_id : _getServerId(),
        gpzs_id : -1,
        zone_id : _getZoneId(),*/
    	ZS_id : _getZSId(),
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

