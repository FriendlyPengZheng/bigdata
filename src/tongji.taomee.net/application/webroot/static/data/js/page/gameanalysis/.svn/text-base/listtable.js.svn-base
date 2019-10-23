/**
 * @fileOverview 获取任务数据
 * @name listtable.js
 * @changelog Maverick 10.10.2014 TODO
 */
var TABSTYPE = "coinsbuyitem";

$(document).ready(function () {
	_gTools();
});

function _gModule() {
    $.getJSON("json/" + window.responseData.locale + "/gameanalysis/" + _getModuleKey() + ".json", null, function(data) {
        var prepared = [];
        $(data).each(function(i) {
            if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore)){
                this.container = $("#J_contentBody");
                if(i == 0 && this.type == "tabs" ){
                    this.tabsSkin = this.child[0] && this.child[0].tabsSkin
                        ? this.child[0].tabsSkin : this.tabsSkin;
                    if(this.tabsSkin)$("#J_contentHeader").css({'position' : 'absolute', 'top' : '10px', 'right' : '10px'});
                }
                prepared.push(this);
            }
        });
        $("#J_contentBody").data("content-data", fac(prepared));
    });
}

/**
 * 有关页面首部工具区功能：时间功能 平台选择功能 区服功能 
 */
function _gTools() {
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
        maxDate: new Date(),
        defaultDate: $date.val(),
        selectDefaultDate: true,
        rangeSeparator: "~",
        onClose: function(userDate) {
            //判断是否是同一时间
            if( userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != $from.val()
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

    var $platform = $("#J_platform");
    getZoneServer( $platform.find(":selected").attr("data-id"), function( data ) {
        _zoneServerFac( data );
        _gModule();
    });
    $platform.tmselect().change(function(e){
        getZoneServer( $(this).find(":selected").attr("data-id"), function( data ) {
            _zoneServerFac(data, true);
        });
    });
}

/**
 * @brief _zoneServerFac
 * 生成区服列表并绑定事件
 * @param data array 区服列表
 * @return
 */
function _zoneServerFac(data, firstClick) {
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
function _refreshData() {
    var modules = $("#J_contentBody").data("content-data");
    if (modules && modules.length) {
        $(modules).each(function() {
            this.refresh({
                dataChange: true
            });
        });
    }
}

/**
 * @brief getZoneServer
 * 获取区服列表
 * @param id ：平台id
 * @return
 */
function getZoneServer( id, fn ) {
    ajax(getUrl("common", "gpzs", "getZoneServer"), {
        game_id : $("#J_paramGameId").val(),
        platform_id : id
    }, function(res){
        if (res.result == 0) {
            var data = [];
            $.each( res.data, function( i ){
                data.push({
                    id : this.gpzs_id + "_" + this.zone_id + "_" + this.server_id,
                    name : ( this.zone_id == -1 && this.server_id == -1 ) ? lang.t("全区全服") : this.gpzs_name,
                    selected : i == 0 ? true : false
                });
            });
            if( fn )fn( data );
        } else {
            say(lang.t("获取数据错误：") + res.err_desc);
        }
    });
}

window.getPageParam = function() {
    var gpzs = $("#J_zoneServer").find(".selected-item").attr("data-id"),
        gpzs = gpzs.split("_");
    return {
        "from[0]" : $("#J_showFrom").val(),
        "to[0]" : $("#J_showTo").val(),
        platform_id : $("#J_platform").find(":selected").attr("data-id"),
        zone_id : gpzs[1],
        server_id : gpzs[2],
        gpzs_id : gpzs[0],
        game_id : $("#J_paramGameId").val()
    };
};

function tableEvent(data) {
	// 在 trend 后增加 rate 和 distribute
    return $(_trendEvent(data)).add(_rateEvent(data)).add(_distributeEvent(data));
}

function _rateEvent (option) {
    var r = "gameanalysis/" + ($("#J_paramGameType").val() ? $("#J_paramGameType").val() : "webgame"),
	    _getRateConfig = function (container, title, id, type) {
            return [{
                type: "wrap",
                container: container,
                title: title,
                headEnabled: false,
                bottomEnabled: true,
                child: [{
                    type: "data",
                    url: {
                        extend: ["", "index.php?r=" + r + "/mission/getMissionDetail&mission_type=" + (type ? type : "main")
                                 + "&sstid=" + id
                                 + "&rate=" + 1],
                        page: function () {
                            return getPageParam();
                        }
                    },
                    child: [{
                        type: "graph",
                        isSetYAxisMin: false,
                        chartConfig: [{name: "", round: "0", type: "line", unit: "%", visible: "1"}]
                    }, {
                        type: "table",
                        minHeight: 300,
                        thead: [{ type: "date", title: lang.t("日期") },
                                { type: "percentage", title: lang.t("接取率") },
                                { type: "percentage", title: lang.t("完成率") }],
                        prepareData: "prepareTableData"
                    }]
                }]
            }];
        };

	return $(document.createElement("a")).addClass("mod-rate mr5").attr({
        "data-id": option.data.id,
        "data-name": option.data.name
    }).text(lang.t("比率趋势")).click(function(){
        var t = $(this),
			pTr = t.closest('tr'),
			modTrend = pTr.find(".mod-trend"),
			modDistr = pTr.find(".mod-distr");
		if (modTrend && modTrend.hasClass("clicked")) {
            modTrend.removeClass("clicked").text(lang.t("人数趋势"));
        }
        if (modDistr && modDistr.hasClass("clicked")) {
            modDistr.removeClass("clicked").text(lang.t("等级分布"));
        }
		if (t.hasClass("clicked")) {
			t.removeClass("clicked").text(lang.t("比率趋势"));
			pTr.removeClass("cur");
			pTr.next().filter(".mod-tr-con").remove();
		} else {
			t.addClass("clicked").text(lang.t("收起"));
			pTr.addClass("cur");
			var newTr = $(), newTd = $();
			if (pTr.next().hasClass("mod-tr-con")) {
				newTr = pTr.next();
				newTd = newTr.find("td:eq(0)").empty();
			} else {
                newTr = $(document.createElement("tr")).addClass("mod-tr-con");
                newTd = $(document.createElement("td")).addClass("td")
					.attr("colspan", pTr.find("td").length)
					.appendTo(newTr);
				pTr.after(newTr);
			}
			fac(_getRateConfig(newTd,
							   t.attr("data-name"),
							   t.closest("tr").attr("data-id"),
							   _getTabsTypeByPanel(option.container)));
		}
    });
}

/**
 * @brief
 * 人数趋势
 * @param data
 *
 * @return
 */
function trendEvent(data) {
    return _trendEvent(data);
}

function _trendEvent(option) {
    return $(document.createElement("a")).addClass("mod-trend mr5").attr({
        "data-id": option.data.id,
        "data-name": option.data.name
    }).text(lang.t("人数趋势")).click(function() {
        var t = $(this),
            pTr = t.closest('tr'),
            modDistr = pTr.find(".mod-distr"),
			modRate = pTr.find(".mod-rate");
        if (modDistr && modDistr.hasClass("clicked")) {
            modDistr.removeClass("clicked").text(lang.t("等级分布"));
        }
		if (modRate && modRate.hasClass("clicked")) {
			modRate.removeClass("clicked").text(lang.t("比率趋势"));
		}
        if (t.hasClass("clicked")) {
            t.removeClass("clicked").text(lang.t("人数趋势"));
            pTr.removeClass("cur");
            pTr.next().filter(".mod-tr-con").remove();
        } else {
            t.addClass("clicked").text(lang.t("收起"));
            pTr.addClass("cur");
            var newTr = $(), newTd = $();
            if (pTr.next().hasClass("mod-tr-con")) {
                newTr = pTr.next();
                newTd = newTr.find("td:eq(0)").empty();
            } else {
                newTr = $(document.createElement("tr")).addClass("mod-tr-con");
                newTd = $(document.createElement("td")).addClass("td")
					.attr("colspan", pTr.find("td").length)
					.appendTo(newTr);
                pTr.after(newTr);
            }
            fac(_getTrendConfig(newTd,
								t.attr("data-name"),
								t.closest("tr").attr("data-id"),
								_getTabsTypeByPanel(option.container)));
        }
    });
}

/**
 * @brief  人数趋势配置
 *
 * @return
 */
function _getTrendConfig(container, title, id, type) {
    if(_getAside() == "data") { //任务数据
        return _getTrendConfigByMission(container, title, id, type);
    }
    return [];
}

/**
 * @brief
 * 任务数据-人数趋势配置
 * @param type
 *
 * @return
 */
function _getTrendConfigByMission(container, title, id, type) {
    return [{
        type: "wrap",
        container: container,
        title: title,
        headEnabled: false,
        bottomEnabled: true,
        child: [{
            type: "data",
            url: {
                extend: ["", "index.php?r=gameanalysis/mobilegame/mission/getMissionDetail&mission_type="
                    + (type ? type : "main") + "&sstid=" + id],
                page: function() {
                    return getPageParam();
                }
            },
            child: [{
                type: "graph",
                isSetYAxisMin: false,
                chartConfig: [{ name: "", round: "0", type: "line", unit: "", visible: "1" }]
            }, {
                type: "table",
				minHeight: 300,
                thead: [{ type: "date", title: lang.t("日期") }, {
                    type: "number", title: lang.t("接取人数") }, {
                    type: "number", title: lang.t("完成人数") }, {
                    type: "number", title: lang.t("放弃人数") }],
                prepareData: "prepareTableData"
            }]
        }]
    }];
}

function _distributeEvent(option) {
    return $(document.createElement("a")).addClass("mod-distr mr5").attr({
        "data-id": option.data.id,
        "data-name": option.data.name
    }).text(lang.t("等级分布")).click(function() {
        var t = $(this),
            pTr = t.closest('tr'),
            modTrend = pTr.find(".mod-trend"),
			modRate = pTr.find(".mod-rate");
        if (modTrend && modTrend.hasClass("clicked")) {
            modTrend.removeClass("clicked").text(lang.t("人数趋势"));
        }
		if (modRate && modRate.hasClass("clicked")) {
			modRate.removeClass("clicked").text(lang.t("比率趋势"));
		}
        if (t.hasClass("clicked")) {
            t.removeClass("clicked").text(lang.t("等级分布"));
            pTr.removeClass("cur");
            pTr.next().filter(".mod-tr-con").remove();
        } else {
            t.addClass("clicked").text(lang.t("收起"));
            pTr.addClass("cur");
            var newTr = $(), newTd = $();
            if (pTr.next().hasClass("mod-tr-con")) {
                newTr = pTr.next();
                newTd = newTr.find("td:eq(0)").empty();
            } else {
                newTr = $(document.createElement("tr")).addClass("mod-tr-con");
                newTd = $(document.createElement("td")).addClass("td")
					.attr("colspan", pTr.find("td").length)
					.appendTo(newTr);
                pTr.after(newTr);
            }
            fac(_getDistributeConfig(newTd,
									 t.attr("data-name"),
									 t.closest("tr").attr("data-id"),
									 _getTabsTypeByPanel(option.container)));
        }
    });
}

function _getDistributeConfig(container, title, id, type) {
    var stid = "_getmaintsk_";
    stid = type == "new"
        ? "_getnewtsk_"
        : (type == "aux" ? "_getauxtsk_" : stid);
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
                            + "&data_info[0][type]=1&data_info[0][stid]=" + stid
                            + "&data_info[0][op_type]=ucount&data_info[0][op_fields]=_lv_&data_info[0][sstid]=" + id
                            + "&data_info[0][sort_type]=1&data_info[0][distr_name]=" + lang.t("接任务人数") + "&data_info[0][dimen_name]=" + lang.t("等级")
                            + "&data_info[0][distr_by]=4&data_info[0][precision]=0"
                            + "&data_info[1][type]=1&data_info[1][stid]=" + stid
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
                page: true,
                isSetYAxisMin: false,
                chartConfig: [{ name: lang.t("接任务人数"), round: "0", type: "line", unit: "人", visible: "1" }, {
                    name: lang.t("接任务人数占比"), round: "0", type: "column", unit: "%", visible: "1" }]
            }, {
                type: "table",
				minHeight: 300,
                thead: [{ type: "range", title: lang.t("等级") }, {
                    type: "number", title: lang.t("接任务人数") }, {
                    type: "percentage", title: lang.t("占比") }],
                prepareData: "prepareTableData"
            }]
        }]
    }];
}

function _getTabsTypeByPanel(container) {
    if(container) {
        var tab = container.closest(".tabs-wrapper").find('li[data-panel="'
                + container.attr("id") + '"]');
        return tab.attr("data-type");
    }
    return "";
}

function _getAside() {
    var aside = (_getModuleKey()).split("-");
    return aside[aside.length-1];
}

window.listtableDownload = function(d){
    var url = "";
    if(_getAside() == "data") {
        if(d.options.title == lang.t("新手引导转化率")) {
            url = "index.php?r=gameanalysis/mobilegame/mission/getNewMissionTop&export=1&mission_type=new&file_name=" + d.options.title;
        } else if(d.options.title == lang.t("任务数据")) {
            url = "index.php?r=gameanalysis/mobilegame/mission/getMissionList&export=1&file_name=" + d.options.title;
        }
    }
    if(url) {
        $.download(url, getPageParam());
    } else {
        say(lang.t("下载链接不正确~"));
    }
};
