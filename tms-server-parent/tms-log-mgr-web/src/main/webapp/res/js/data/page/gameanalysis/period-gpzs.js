var MODULE = [];
var pageTabs = $();
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
        defaultDate: $date.val(),
        selectDefaultDate: true,
        rangeSeparator: "~",
        onClose: function(userDate) {
            //判断是否是同一时间
            if ( userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != $from.val()
                || $.datepick.formatDate("yyyy-mm-dd", userDate[1]) != $to.val()) ){
                var userDate = $date.val().split("~");
                userDate[0] = $.trim(userDate[0]);
                userDate[1] = $.trim(userDate[1]);
                $from.val(userDate[0]);
                $to.val(userDate[1]);
                _handle_show_time( userDate[0], userDate[1] );
                _refreshData();
            }
        }
    });
    $("#J_from_to").click(function(e){
        $date.focus();
        e.stopPropagation();
    });

    gModule();
    var $platform = $("#J_platform");
    getZoneServer( $platform.find(":selected").attr("data-id"), function( data ) {
        _zoneServerFac( data );
    },"全区全服");
    
    $platform.tmselect().change(function(e){
        getZoneServer( $(this).find(":selected").attr("data-id"), function( data ) {
            _zoneServerFac(data, true);
        },"全区全服");
    });

}


function _getPlatformId(){
    return $("#J_platform").find(":selected").attr("data-id");
}

/*function _getZoneId(){
    return $("#J_zone").find(".selected-item").attr("data-id");
}*/

function _getZSId(){
    return $("#J_zoneServer").find(".selected-item").attr("data-id");
}
/**
 * @brief _zoneFac
 * 生成区服列表并绑定事件
 * @param data array 区服列表
 * @return
 */
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

window.getPageParam = function(flag){
    /*var gpzs = $("#J_zoneServer").find(".selected-item").attr("data-id"),
        gpzs = gpzs.split("_");*/
	if (flag === "download") {
		return {
			"from[0]" : $("#J_from").val(),
			"to[0]" : $("#J_to").val(),
			platform_id : _getPlatformId(),
			/*zone_id : _getZoneId(),
			server_id : _getServerId(),
			gpzs_id : -1,*/
			ZS_id : _getZSId(),
			game_id : $("#J_paramGameId").val()
		};
	} 
    return {
        "from[0]" : $("#J_showFrom").val(),
        "to[0]" : $("#J_showTo").val(),
        platform_id : _getPlatformId(),
/*        zone_id : _getZoneId(),
        server_id : _getServerId(),*/
        ZS_id : _getZSId(),
        game_id : $("#J_paramGameId").val()
    };
};
/*function tableEvent(data) {
    return $(_trendEvent(data)).add(_distributeEvent(data));
}
function trendEvent(data){
    return _trendEvent(data);
}

function _trendEvent(data) {
    return $(document.createElement("a")).addClass("mod-trend mr5").attr({
        "data-id": data.id,
        "data-name": data.name
    }).text(lang.t("查看趋势")).click(function(){
        var t = $(this),
            pTr = t.closest('tr'),
            modDistr = pTr.find(".mod-distr");
        if(modDistr && modDistr.hasClass("clicked")) {
            modDistr.removeClass("clicked").text(lang.t("等级分布"));
        }
        if (t.hasClass("clicked")) {
            t.removeClass("clicked").text(lang.t("查看趋势"));
            pTr.removeClass("cur");
            pTr.next().filter(".mod-tr-con").remove();
        } else {
            t.addClass("clicked").text(lang.t("收起"));
            pTr.addClass("cur");
            var newTr = $(), newTd = $();
            if(pTr.next().hasClass("mod-tr-con")){
                newTr = pTr.next();
                newTd = newTr.find("td:eq(0)").empty();
            } else {
                newTr = $(document.createElement("tr")).addClass("mod-tr-con");
                newTd = $(document.createElement("td")).addClass("td").attr("colspan", pTr.find("td").length).appendTo(newTr);
                pTr.after(newTr);
            }
            fac(_getTrendConfig(newTd, t.attr("data-name"), t.closest("tr").attr("data-id")));
        }
    });
}
function _distributeEvent(data) {
    return $(document.createElement("a")).addClass("mod-distr mr5").attr({
        "data-id": data.id,
        "data-name": data.name
    }).text(lang.t("等级分布")).click(function(){
        var t = $(this),
            pTr = t.closest('tr'),
            modTrend = pTr.find(".mod-trend");
        if(modTrend && modTrend.hasClass("clicked")) {
            modTrend.removeClass("clicked").text(lang.t("查看趋势"));
        }
        if (t.hasClass("clicked")) {
            t.removeClass("clicked").text(lang.t("等级分布"));
            pTr.removeClass("cur");
            pTr.next().filter(".mod-tr-con").remove();
        } else {
            t.addClass("clicked").text(lang.t("收起"));
            pTr.addClass("cur");
            var newTr = $(), newTd = $();
            if(pTr.next().hasClass("mod-tr-con")){
                newTr = pTr.next();
                newTd = newTr.find("td:eq(0)").empty();
            } else {
                newTr = $(document.createElement("tr")).addClass("mod-tr-con");
                newTd = $(document.createElement("td")).addClass("td").attr("colspan", pTr.find("td").length).appendTo(newTr);
                pTr.after(newTr);
            }
            fac(_getDistributeConfig(newTd, t.attr("data-name"), t.closest("tr").attr("data-id")));
        }
    });
}
function _getDistributeConfig(container, title, id) {
    return [{
        type: "wrap",
        container: container,
        title: title,
        headEnabled: false,
        bottomEnabled: true,
        child: [{
            type: "data",
            url: {
                extend: ["", "index.php?r=common/data/getDistribution&by_item=1"
                            + "&data_info[0][type]=1&data_info[0][stid]=_getmaintsk_"
                            + "&data_info[0][op_type]=ucount&data_info[0][op_fields]=_lv_&data_info[0][sstid]=" + id
                            + "&data_info[0][sort_type]=1&data_info[0][distr_name]=" + lang.t("接任务人数") + "&data_info[0][dimen_name]=" + lang.t("等级")
                            + "&data_info[0][distr_by]=4&data_info[0][precision]=0"
                            + "&data_info[1][type]=1&data_info[1][stid]=_getmaintsk_"
                            + "&data_info[1][op_type]=ucount&data_info[1][op_fields]=_lv_&data_info[1][sstid]=" + id
                            + "&data_info[1][sort_type]=1&data_info[1][distr_name]=" + lang.t("接任务人数") + "&data_info[1][dimen_name]=" + lang.t("等级")
                            + "&data_info[1][distr_by]=4&data_info[1][precision]=0&data_info[1][percentage]=1"
                            ],
                page: function() {
                    return getPageParam();
                }
            },
            child: [{
                type: "graph",
                lineColumn: true,
                chartPage: true,
                isSetYAxisMin: false,
                chartConfig: [{ name: lang.t("接任务人数"), round: "0", type: "line", unit: lang.t("人"), visible: "1" }, {
                    name: lang.t("接任务人数占比"), round: "0", type: "column", unit: "%", visible: "1" }]
            }, {
                type: "table",
                thead: [{ type: "date", title: lang.t("日期") }, {
                    type: "number", title: lang.t("接任务人数") }, {
                    type: "percentage", title: lang.t("占比") }],
                prepareData: "prepareTableData"
            }]
        }]
    }];
}
function _getTrendConfig(container, title, id) {
    if(_getAside() == "mbsales") {
        return [{
            type: "wrap",
            container: container,
            title: title,
            headEnabled: false,
            bottomEnabled: true,
            child: [{
                type: "data",
                url: {
                    extend: ["", "index.php?r=gameanalysis/mobilegame/economy/getItemSaleDetail&sstid=_coinsbuyitem_&item_id=" + id],
                    page: function() {
                        return getPageParam();
                    }
                },
                child: [{
                    type: "graph",
                    chartConfig: [{ name: "", round: "0", type: "line", unit: "", visible: "1" }]
                }, {
                    type: "table",
                    thead: [{ type: "date", title: lang.t("日期") }, {
                        type: "number", title: lang.t("购买人数") }, {
                        type: "number", title: lang.t("销售数量") }, {
                        type: "number", title: lang.t("消耗游戏币数") }],
                    prepareData: "prepareTableData"
                }]
            }]
        }];
    } else {
        return [{
            type: "wrap",
            container: container,
            title: title,
            headEnabled: false,
            bottomEnabled: true,
            child: [{
                type: "data",
                url: {
                    extend: ["", "index.php?r=gameanalysis/mobilegame/mission/getMissionDetail&mission_type=main&sstid=" + id],
                    page: function() {
                        return getPageParam();
                    }
                },
                child: [{
                    type: "graph",
                    chartConfig: [{ name: lang.t("接取人数"), round: "0", type: "line", unit: "", visible: "1" }]
                }, {
                    type: "table",
                    thead: [{ type: "date", title: lang.t("日期") }, {
                        type: "number", title: lang.t("接取人数") }, {
                        type: "number", title: lang.t("完成人数") }, {
                        type: "number", title: lang.t("放弃人数") }],
                    prepareData: "prepareTableData"
                }]
            }]
        }];
    }
}
function _getAside() {
    var aside = (_getModuleKey()).split("-");
    return aside[aside.length-1];
}
*/