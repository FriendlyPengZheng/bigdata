/**
 * Overwrite $.draw.drawStock for board graph
 */
(function($, window, undefined) {
$.draw.drawStock = function(o){
    new Highcharts.StockChart({
        credits: {
            enabled: false
        },
        legend: {
            enabled: false
        },
        xAxis: {
            endOnTick: false,
            lineWidth: 0,
            tickWidth: 0,
            labels: {
                enabled: false
            }
        },
        yAxis: {
            endOnTick: false,
            gridLineWidth: 0,
            labels: {
                enabled: false
            }
        },
        tooltip: {
            shared: true,
            useHTML: true,
            formatter: function(){
                var dateStr = Highcharts.dateFormat("%Y-%m-%d", this.x),
                    chWeek = $.date.getChWeek(dateStr),
                    s = '<table><tr>' + dateStr + (chWeek ? '（' + chWeek + '）' : '') + '</tr>';
                $.each(this.points, function(i, point) {
                    s += '<tr><td style="color:' + point.series.color + '">' + point.series.name + '：</td><td>' +
                        (Math.round(point.y*100)/100).toString().addCommas() +
                        (o.yUnit[i] ? o.yUnit[i] : (o.yUnit[0] ? o.yUnit[0] : '')) + '</td></tr>';
                });
                return s;
            }
        },
        scrollbar: {
            enabled: false
        },
        rangeSelector: {
            enabled: false
        },
        navigator: {
            enabled: false
        },
        navigation: {
            buttonOptions: {
                enabled: false
            }
        },
        plotOptions: {
            area: {
                fillColor : {
                    linearGradient : {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops : [
                        [0, $.draw.colors[0]],
                        [1, Highcharts.Color($.draw.colors[0]).setOpacity(0).get('rgba')]
                    ]
                }
            }
        },
        chart: {
            type: o.chartType,
            renderTo: o.container.get(0),
            width: o.width,
            height: 120
        },
        colors: $.draw.colors,
        series: o.chartData
    });
};
})(jQuery, window, undefined);
/**
 * @brief gModule
 * 根据配置生成页面
 * need json file
 * @return
 */
function gModule() {
    $.getJSON("json/" + window.responseData.locale + "/" + _getTopBarKey() + "/" + _getModuleKey() + ".json", null, function(data) {
        var prepared = [];
        $(data).each(function(i) {
            if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore) && this.attr.container) {
                this.container = $(this.attr.container);
                if (this.container.length > 0) {
                    this.container.parent().show();
                    this.child[0].child[0].beforeDraw = "beforeDraw";
                    prepared.push(this);
                }
            }
        });
        $("#J_contentBody").data("content-data", fac(prepared));
    });
}


$(function(){
    _gTools();
});

/**
 * @brief _gTools
 * 有关页面首部工具区功能
 * 时间功能 平台选择功能 区服功能
 * @return
 */
function _gTools() {
    // time
    var $to = $("#J_to"),
        $date = $("#J_date");
    // select time
    $date.datepick({
        bottomDisabled: true,
        maxDate: new Date(),
        onClose: function(userDate) {
            if(!userDate.length){ $date.val($to.val()); }
            if ($date.val() !== $to.val()) {
                $to.val($date.val());
                _refreshData();
            }
        }
    });
    $("#J_toTime").click(function(e){
        e.stopPropagation();
        $date.focus();
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
        gpzs = gpzs.split("_"),
        to = $("#J_to").val();
    return {
        server_id: gpzs[2],
        gpzs_id: gpzs[0],
        zone_id: gpzs[1],
        platform_id: _getPlatformId(),
        game_id: $("#J_paramGameId").val(),
        from: $.date.getDate(to, -30),
        to: to,
        not_mark_date: 1
    };
}
window.beforeDraw = function(options) {
    var data = $.extend(true, {}, options.data[0]),
        one = data.data && data.data[0] || {},
        dat = $.isArray(one.data) && one.data.pop() || "-",
        qoq = $.isArray(one.qoq) && one.qoq.pop() || "-",
        yoy = $.isArray(one.yoy) && one.yoy.pop() || "-",
        key = $.isArray(data.key) && data.key.pop() || "-",
        board = options.container.prev(".board");
    if (board.length === 0) {
        board = $(
            '<ul class="board">' +
                '<li><span class="data">&nbsp;</span><span class="key">&nbsp</span></li>' +
                '<li>' + lang.t('环比') + '<i class="arrow up">&nbsp;</i><span class="rate qoq">&nbsp;</span></li>' +
                '<li>' + lang.t('同比') + '<i class="arrow down">&nbsp;</i><span class="rate yoy">&nbsp;</span></li>' +
            '</ul>'
        ).insertBefore(options.container);
    }

    var $data = board.find(".data"),
        $key = board.find(".key"),
        $qoq = board.find(".qoq"),
        $yoy = board.find(".yoy"),
        qoq_val, yoy_val;

    if (dat === "-") {
        qoq = yoy = "-";
    }
    $data.text(dat.addCommas());

    if (key !== $("#J_to").val()) {
        $key.text("(" + key + ")").show();
    } else {
        $key.hide();
    }

    qoq_val = parseFloat(qoq);
    if (isNaN(qoq_val) || qoq_val === 0) {
        $qoq.removeClass("up down").prev(".arrow").removeClass("up down");
    } else if (/^-[0-9]/.test(qoq)) {
        $qoq.removeClass("up down").addClass("down").prev(".arrow").removeClass("up down").addClass("down");
    } else {
        $qoq.removeClass("up down").addClass("up").prev(".arrow").removeClass("up down").addClass("up");
    }
    $qoq.text(qoq);

    yoy_val = parseFloat(yoy);
    if (isNaN(yoy_val) || yoy_val === 0) {
        $yoy.removeClass("up down").prev(".arrow").removeClass("up down");
    } else if (/^-[0-9]/.test(yoy)) {
        $yoy.removeClass("up down").addClass("down").prev(".arrow").removeClass("up down").addClass("down");
    } else {
        $yoy.removeClass("up down").addClass("up").prev(".arrow").removeClass("up down").addClass("up");
    }
    $yoy.text(yoy);
}
