var TABSTYPE = "coinsbuyitem";
$(function(){
    _gTools();
});
function _gModule(){
    $.getJSON("json/" + window.responseData.locale + "/gameanalysis/" + _getModuleKey() + ".json?v=1", null, function(data) {
        var prepared = [];
        $(data).each(function(i) {
            if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore)){
                this.container = $("#J_contentBody");
                if(i == 0 && this.type == "tabs" ){
                    this.tabsSkin = this.child[0] && this.child[0].tabsSkin
                        ? this.child[0].tabsSkin : this.tabsSkin;
                    if(this.tabsSkin)$("#J_contentHeader").css({'position' : 'absolute', 'top' : '10px', 'right' : '10px'});
                    this.tabsClick = function(t) {
                        TABSTYPE = t.attr("data-type");
                    };
                }
                prepared.push(this);
            }
        });
        $("#J_contentBody").data("content-data", fac(prepared));
    });
}
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
    getZoneServer( $platform.find(":selected").attr("data-id"), function( data ){
        _zoneServerFac( data );
        _gModule();
    });
    $platform.tmselect().change(function(e){
        getZoneServer( $(this).find(":selected").attr("data-id"), function( data ){
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
function _zoneServerFac(data, firstClick){
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

window.getPageParam = function(){
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
    }
}
//-------------event for listtable-------------
/**
 * @brief _newTrEvent
 * create new tr as container
 * @param t
 * @param callback
 *
 * @return
 */
function _newTrEvent(t, callback) {
    var pTr = t.closest('tr');
    if(!t.hasClass("expansion")) {
        var expansion =  pTr.find(".expansion");
        if(expansion && expansion.hasClass("clicked")) {
            expansion.removeClass("clicked");
        }
    }
    pTr.find(".mod-list-btn").not(t).each(function() {
        if($(this).hasClass("clicked")) {
            $(this).removeClass("clicked").text($(this).attr("data-text"));
        }
    });
    if (t.hasClass("clicked")) {
        t.removeClass("clicked").text(t.attr("data-text"));
        pTr.removeClass("cur");
        pTr.next().filter(".mod-tr-con").remove();
    } else {
        if(!t.hasClass("expansion")) {
            t.text(lang.t("收起"));
        }
        t.addClass("clicked");
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
        if(callback) callback(newTd);
    }
}
/**
 * @brief childEvent
 * 展示二级类别列表（by一级类别）
 * @param option
 * is_leaf: 1: 叶子（只能添道具） 0：非叶子（只能添类别） 2：不知道
 * @return
 */
window.childEvent = function(option) {
    if(option.data.is_leaf != 1) {
        return $(document.createElement("i")).addClass("expansion").attr({
            "data-id": option.data.category_id,
            "data-name": option.data.category_name
        }).click(function() {
            var t = $(this);
            _newTrEvent($(this), function(container) {
                 fac(_getChildConfig(container, t.closest("tr").attr("data-id")));
            });
        });
    }
}

/**
 * @brief tableEvent
 * 一级类别功能
 * is_leaf:
 *  1：叶子（查看道具、查看趋势）
 *  0：非叶子（child、查看趋势）
 * @param data
 *
 * @return
 */
window.tableEvent = function(data) {
    if(data.data.is_leaf == 1) {
        return $(_trendEvent(data)).add(_mbEvent(data));
    } else {
        return _trendEvent(data);
    }
}

/**
 * @brief childTableEvent
 * 二级类别列表-（查看趋势，查看道具功能）
 * @return
 */
window.childTableEvent = function(data) {
    return $(_trendEvent(data)).add(_mbEvent(data));
};

/**
 * @brief
 * 按道具-查看趋势功能
 * @param data
 *
 * @return
 */
window.trendEvent = function(data) {
    return _trendEvent(data);
}

/**
 * @brief mbInCategoryTrendEvent
 * 按类别-道具查看趋势
 * @param data
 * @return
 */
window.mbInCategoryTrendEvent = function(data) {
    return _trendEvent(data, true);
};

/**
 * @brief _trendEvent
 * 查看趋势功能
 * @param option
 *
 * @return
 */
function _trendEvent(option, mbInCategory) {
    return $(document.createElement("a")).addClass("mod-trend btn-green mod-list-btn").attr({
        "data-id": option.data.id,
        "data-name": option.data.name,
        "data-text": lang.t("查看趋势")
    }).text(lang.t("查看趋势")).click(function(){
        var t = $(this);
        _newTrEvent(t, function(container) {
            fac(_getTrendConfigByItem(container, t.attr("data-name"), t.closest("tr").attr("data-id"), _getTabsTypeByPanel(option.container), mbInCategory));
        });
    });
}
/**
 * @brief _mbEvent
 * 查看道具列表功能
 * @param option
 *
 * @return
 */
function _mbEvent(option) {
    return $(document.createElement("a")).addClass("mod-mb btn-green mod-list-btn").attr({
        "data-id": option.data.id,
        "data-name": option.data.name,
        "data-text": lang.t("查看道具")
    }).text(lang.t("查看道具")).click(function(){
        var t = $(this);
        _newTrEvent($(this), function(container) {
            fac(_getMbInCategoryConfig(container, t.closest("tr").attr("data-id")));
        });
    });
}

/**
 * @brief
 * listtable -二级类别列表
 * @param container
 * @param id
 * @return
 */
function _getChildConfig(container, id) {
    id = id ? id : 0;
    return [{
        type: "listtable",
        container: container,
        isAjax: true,
        renameUrl: "index.php?r=gameanalysis/mobilegame/economy/setCategory",
        pagination: true,
        url: {
            page: "getPageParam",
            paginationUrl: "index.php?r=gameanalysis/mobilegame/economy/getCategorySaleListTotal&parent_id=" + id
                + "&sstid=" + (TABSTYPE == "coinsbuyitem" ? "_coinsbuyitem_" : "_mibiitem_"),
            extend: "index.php?r=gameanalysis/mobilegame/economy/getCategorySaleList&parent_id=" + id
                +"&sstid=" + (TABSTYPE == "coinsbuyitem" ? "_coinsbuyitem_" : "_mibiitem_")
        },
        thead: [{
            type: "string", title: lang.t("二级类别ID") }, {
            type: "string", title: lang.t("二级类别名称") } , {
            type: "number", title: lang.t("购买人次") } , {
            type: "number", title: lang.t("卖出数量") } , {
            type: "number", title: lang.t("销售总金额") } , {
            type: "string", title: lang.t("操作")
        }],
        appendColumns: [{
            type: "data", key: "category_id", isID: 1 }, {
            type: "data", key: "category_name", isID : 0 }, {
            type: "data", key: "_buycount", isID : 0 }, {
            type: "data", key: "_salenum", isID : 0 }, {
            type: "data", key: "_salemoney", isID : 0 },
            "childTableEvent"]
    }];
}

/**
 * @brief _getMbInCategoryConfig
 * listtable - 按类别-道具列表
 * @param container
 * @param id
 * @return
 */
function _getMbInCategoryConfig(container, id) {
    return [{
        type: "listtable",
        container: container,
        isAjax: true,
        url: {
            page: "getPageParam",
            extend: "index.php?r=gameanalysis/mobilegame/economy/getItemSaleList&category_id=" + id
            +"&sstid=" + (TABSTYPE == "coinsbuyitem" ? "_coinsbuyitem_" : "_mibiitem_")
        },
        thead: [{
            type: "string", title: lang.t("道具ID") }, {
            type: "string", title: lang.t("道具名称") } , {
            type: "number", title: lang.t("购买人次") } , {
            type: "number", title: lang.t("卖出数量") } , {
            type: "number", title: lang.t("销售总金额") } , {
            type: "string", title: lang.t("操作")
        }],
        appendColumns: [{
            type: "data", key: "item_id", isID: 1 }, {
            type: "data", key: "item_name", isID : 0 }, {
            type: "data", key: "_buycount", isID : 0 }, {
            type: "data", key: "_salenum", isID : 0 }, {
            type: "data", key: "_salemoney", isID : 0 },
            "mbInCategoryTrendEvent"]
    }];
}
/**
 * @brief
 * 道具销售-查看趋势配置
 * @param type {string} item: 按道具 category: 按类别
 *
 * @return
 */
function _getTrendConfigByItem(container, title, id, type, mbInCategory) {
    if(type == "category" && mbInCategory || type == "item") { //按道具|按类别-道具趋势
        return [{
            type: "wrap",
            container: container,
            title: title,
            headEnabled: false,
            bottomEnabled: true,
            child: [{
                type: "data",
                url: {
                    extend: ["", "index.php?r=gameanalysis/mobilegame/economy/getItemSaleDetail&sstid="
                                + (TABSTYPE == "coinsbuyitem" ? "_coinsbuyitem_" : "_mibiitem_&factor=0.01")
                                + "&item_id=" + id],
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
                    thead: [{ type: "date", title: lang.t("日期") }, {
                        type: "number", title: lang.t("购买人数") }, {
                        type: "number", title: lang.t("销售数量") }, {
                        type: "number", title: lang.t("消耗游戏币数") }],
                    prepareData: "prepareTableData"
                }]
            }]
        }];
    } else {//按类别
        return [{
            type: "wrap",
            container: container,
            title: title,
            headEnabled: false,
            bottomEnabled: true,
            child: [{
                type: "data",
                url: {
                    extend: ["", "index.php?r=gameanalysis/mobilegame/economy/getCategorySaleDetail&sstid="
                                + (TABSTYPE == "coinsbuyitem" ? "_coinsbuyitem_" : "_mibiitem_&factor=0.01")
                                + "&category_id=" + id],
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
                    thead: [{ type: "date", title: lang.t("日期") }, {
                        type: "number", title: lang.t("购买总人次") }, {
                        type: "number", title: lang.t("销售总数量") }, {
                        type: "number", title: lang.t("销售总金额") }],
                    prepareData: "prepareTableData"
                }]
            }]
        }];
    }
}

/**
 * @brief _getTabsTypeByPanel
 * 获取当前选中的模块中的tabs-active的type
 * @param container
 *
 * @return
 */
function _getTabsTypeByPanel(container) {
    if(container) {
        var tab = container.closest(".tabs-wrapper").find('li.tabs-active');
        return tab.attr("data-type");
    }
    return "";
}

window.listtableDownload = function(d){
    var url = "";
    if(TABSTYPE == "coinsbuyitem") {
        if(d.options.title == lang.t("道具销售Top10")) {
            url = "index.php?r=gameanalysis/mobilegame/economy/getItemSaleTop&export=1&sstid=_coinsbuyitem_&file_name=" + d.options.title;
        } else if(d.options.title == lang.t("每个道具销售数据")) {
            url = "index.php?r=gameanalysis/mobilegame/economy/export&sstid=_coinsbuyitem_&file_name=" + d.options.title;
        }
    } else if(TABSTYPE == "mibiitem") {
        if(d.options.title == lang.t("道具销售Top10")) {
            url = "index.php?r=gameanalysis/mobilegame/economy/getItemSaleTop&export=1&sstid=_mibiitem_&factor=0.01&file_name=" + d.options.title;
        } else if(d.options.title == lang.t("每个道具销售数据")) {
            url = "index.php?r=gameanalysis/mobilegame/economy/export&sstid=_mibiitem_&factor=0.01&file_name=" + d.options.title;
        }
    }
    if(url) {
        $.download(url, getPageParam());
    } else {
        say(lang.t("下载链接不正确~"));
    }
};
