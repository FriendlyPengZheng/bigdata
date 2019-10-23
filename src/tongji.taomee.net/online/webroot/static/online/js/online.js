$(function(){
    msglog("stat-online登录用户详情", responseData.userName);
    msglog("stat-online登入", "用户登入");
    var game = $("#J_game"),
        zoneServer = $("#J_zoneServer"),
        changeBtns = $("#J_dateTools").find(".change-btn");
    game.tmselect({
        colorTheme : "white"
    }).change(function(){
        if($(this).val() == "-1") {
            changeBtns.removeClass("cur").addClass("disabled");
            _setDateValue($.date.getNow());
        } else {
            changeBtns.removeClass("disabled");
        }
        getZoneServer($(this).val(), function(data){
            var html = '', len = data.length;
            for(var i = 0; i < len; i++) {
                html += '<option value="' + data[i].id +'" data-id="' + data[i].id + '" '
                    + (len > 6
                        ? (i == 1 ? 'selected' : '')
                        : (i == 0 ? 'selected' : '')) + '>' + data[i].name + '</option>';
            }
            zoneServer.empty().append($(html)).tmselect("reset");
            zoneServer.first().change();
        });
    });
    zoneServer.tmselect({
        colorTheme : "white"
    }).change(function(){
        //"所有区服" 不能保持"快捷键"选中状态
        if($(this).find(":selected").index() == 0 && changeBtns.filter(".cur").length) {
            _resetDateTools();
            _setDateValue($.date.getNow());
        }
        _gModule();
    });
    _gModule();
    changeBtns.click(function(){
        var t = $(this);
        if(!t.hasClass("disabled")) {
            var d = $.date.getNow();
            if(t.hasClass("cur")) {
                t.removeClass("cur");
            } else {
                t.siblings().removeClass("cur");
                t.addClass("cur");
                d = _getDateByTools(t.attr("data-id"));
            }
            _setDateValue(d);
            //selected "总在线" if zone server is "所有区服"
            if(zoneServer.find(":selected").index() == 0
                && (/,/).test(zoneServer.find(":selected").attr("data-id"))) {
                zoneServer.tmselect("getOptionContainer").find("ul li:eq(1)").click();
            } else {
                _refreshData();
            }
        }
    });
    //time
    var time = $("#J_time"),
        date = $("#J_date");
    date.datepick({
        rangeSelect: false,
        multiSelect: 10,
        autoWidth: true,
        monthsToShow: 1,
        monthsToStep: 1,
        monthsOffset: 0,
        maxDate: new Date(),
        onClose: function(userDate) {
            var selectedDate = '';
            if(!userDate.length) {
                date.val(time.val());
            } else {
                for(var i = 0; i < userDate.length; i++) {
                    selectedDate += $.datepick.formatDate("yyyy-mm-dd", userDate[i])
                        + (i != userDate.length - 1 ? "," : "");
                }
            }
            if(selectedDate != time.val()) {
                time.val(date.val());
                _setDateWidth();
                _resetDateTools();
                _refreshData();
            }
        }
    });
    $("#J_dateCon").click(function(e){ e.stopPropagation();
        date.focus();
    });
});

function _gModule() {
    var prepared = [];
    prepared.push({
        container: $("#J_content").empty(),
        type: "data",
        isTimeDimensionInherit: false,
        url: {
            timeDimension: 4,
            extend: ["", "", "", "", getUrl("online", "getData")],
            page: "getPageParam"
        },
        child: [{
            type: "graph",
            chartStock: true,
            navigator: false,
            title: "最高在线",
            showMax: true,
            height: 500,
            loadUrl: getUrl("online", "getData"),
            timeDimension: "onlymin"
        }]
    });
    $("#J_content").data("content-data", fac(prepared));
}
/**
 * @brief _resetDateTools
 * reset all of date tools
 * @return
 */
function _resetDateTools() {
    $("#J_dateTools").find(".change-btn").removeClass("cur");
}
/**
 * @brief
 * 设置时间值和时间框显示宽度
 * @param d
 *
 * @return
 */
function _setDateValue(d) {
    $("#J_time").val(d);
    $("#J_date").val(d);
    _setDateWidth();
}
/**
 * @brief _setDateWidth
 * set target's width
 * @return
 */
function _setDateWidth(){
    var date = $("#J_date"),
        width = date.val().length * 7;
    date.width(width > 300 ? 300 : (width < 70 ? 70 : width));
}
/**
 * @brief _getDateByTools
 * set value of date and time
 * @param type {int} 1: 同比环比 2: 一周对比
 * @return
 */
function _getDateByTools(type) {
    type = type || 1;
    var now = $.date.getNow(), d = '';
    switch(parseInt(type, 10)) {
        case 1:
            d += now + ',' + $.date.getDate(now, -1) + ',' + $.date.getDate(now, -7);
            break;
        case 2:
            for(var i = 0; i < 7; i++) {
                d += ',' + $.date.getDate(now, -i);
            }
            break;
        default: break;
    }
    return d;
}

/**
 * @brief _refreshData
 * 刷新数据
 */
function _refreshData(){
    var modules = $("#J_content").data("content-data");
    if (modules && modules.length) {
        $(modules).each(function() {
            this.refresh({
                dataChange: true
            });
        });
    }
};

window.getPageParam = function() {
    var param = {
        game_id: $("#J_game").val(),
        zs_id: $("#J_zoneServer").val(),
        time: $("#J_time").val()
    };
    return param;
}

/**
 * @brief getZoneServer
 *
 * @param id: game id
 * @param fn: callback
 * @return
 */
function getZoneServer(id, fn) {
    ajax(getUrl("online", "getZoneServer"), {
        game_id: id
    }, function(res){
        var data = [];
        if (res.result == 0) {
            data = res.data;
        } else {
            say("获取数据错误：" + res.err_desc);
        }
        if(fn) fn(data);
    });
}
//stat
window.msglog = function(stid, sstid) {
    if (responseData.isRelease) {
        $.ajax({
            url: "//newmisc.taomee.com/misc.js?gameid=10000&uid=" + responseData.userId + "&stid=" + stid + "&sstid=" + sstid,
            dataType: "jsonp"
        });
    }
};

