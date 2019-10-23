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
   // $.getJSON("json/" + window.responseData.locale + "/" + _getTopBarKey() + "/" + _getModuleKey() + ".json", null, function(data) {
	$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-overview-board.json", null, function(data) {
        var prepared = [];
        $(data).each(function(i) {
        	console.log(this);
        	console.log(window.responseData.currentPage);
            if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore) && this.attr.container) {
                this.container = $(this.attr.container);
                if (this.container.length > 0) {
                    this.container.parent().show();
                    this.child[0].child[0].beforeDraw = "beforeDraw";
                    prepared.push(this);
                }
            }
        });
        //$("#J_contentBody").data("content-data", fac(prepared));
        var tmp = $("#J_contentBody").data("content-data");
        if(tmp == null){
        	$("#J_contentBody").data("content-data", fac(prepared));
        } else {
        	$("#J_contentBody").data("content-data", tmp.concat(fac(prepared)));
        }
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
function _gTools(){
   //time
   // var $from = $("#J_from"),
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
     
     /*$("#J_fromTime").click(function(e){
        e.stopPropagation();
        $date.focus();
    });*/
    
    gModule();
    getZoneServer(_getPlatformId(), function( data ){
    	_zoneServerFac( data );
    },"全区全服");
    
    $("#J_platform").tmselect().change(function(e){
    	getZoneServer($(this).find(":selected").attr("data-id"), function( data ){
    		_zoneServerFac( data, true );
        },"全区全服");
    });
     
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
    });*/
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
                /*id : -1,
                name : server_name,*/
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
	to = $("#J_to").val();
    return {
        /*server_id : _getServerId(),
        gpzs_id : -1,
        zone_id : _getZoneId(),*/
    	ZS_id : _getZSId(),
        platform_id : _getPlatformId(),
        game_id : $("#J_paramGameId").val(),
        from: $.date.getDate(to, -30),
        to: to,
        not_mark_date: 1
    };
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

function printObj(obj){
	var output = "";
	for(var i in obj){  
		var property=obj[i];  
		output+=i+" = "+property+"\n"; 
	}  
	return output;
}

