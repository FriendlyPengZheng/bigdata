(function( $, undefined ){
$.draw = {
    /**
     * @brief function
     *
     * @param options
     * showNum    int     每页显示的总条数
     * page       boolen  分页功能
     * chartData  array   图数据
     * chartStock boolen  是否显示为大量数据形式，即显示x轴导航
     * series     array   图坐标数据
     * xAxisCategories array 当为bar时显示x双轴
     * container  object  父容器
     * width      int     图宽度
     * @return
     */
    DrawFactory : function( options ){
        var _self = this,
            opts = {
                showNum : 10,
                page : true,
                chartData : [],
                columnStack: '',
                lineColumn : false,
                lineAreaColumn : false,
                yUnit : [],
                series : [],
                xAxisCategories : [],
                isSetYAxisMin: false,
                chartType : "line",
                width : 400,
                container : null
            };
        $.extend( opts, options );
        //TODO 为了保证原始的yUnit:'' yAxisUnit: ['', ''] 更改为ok后，需要删除
        if(!$.isArray(opts.yUnit)) opts.yUnit = [opts.yUnit];
        if(opts.yAxisUnit) {
            opts.yUnit = [];
            if(opts.yAxisUnit[0]) opts.yUnit.push(opts.yAxisUnit[0]);
            if(opts.yAxisUnit[1]) opts.yUnit.push(opts.yAxisUnit[1]);
        }
        opts.xAxisCategories = opts.chartType == "bar" ? opts.xAxisCategories : [];
        //110:图两边y轴距离 48：columnWidth * 3
        opts.showNum = options.showNum
                ? options.showNum
                : ( opts.chartType == "bar" ? 10 : Math.ceil((opts.width-110)/48));
        this.chartData = opts.chartData;
        this.series = opts.series;
        this.xAxisCategories = opts.xAxisCategories;

        if( !opts.chartData.length ){
            opts.container.text("no data...");
            return;
        }
        if (opts.chartStock) {
            _draw_stock({
                columnStack: opts.columnStack,
                chartData : opts.chartData,
                container : opts.container.get(0),
                chartType : opts.chartType,
                width : opts.width,
                timeDimension : opts.timeDimension,
                colorTheme : opts.colorTheme
            });
            return;
        }
        //分页功能
        if( opts.page ){
            /**
             * @brief function
             * 显示图功能
             * @param start : 显示开始下标
             * @param showNum ： 总显示条数
             *
             */
            this.showGraph = function( start, showNum ){
                var length = _self.series.length;
                start = ( start >= 0 && start < length ) ? start : 0;
                var end = start + showNum <= length ? start + showNum : length;

                var chartData = [],
                    xAxisCategories = _self.xAxisCategories.slice( start, end ),
                    series = _self.series.slice( start, end );

                $.each( _self.chartData, function(i){
                    this.data = this.data ? this.data : [];
                    var tmp = {
                        data : this.data.slice( start, end ),
                        name : this.name
                    };
                    if( this.type ) tmp.type = this.type;
                    if( opts.lineColumn && i == 1 ) tmp.yAxis = 1;
                    chartData.push( tmp );
                });
                if( opts.chartType == "pie" ){
                    _draw_pie({
                        chartData : chartData,
                        yUnit : opts.yUnit,
                        container : opts.container.get(0),
                        width : opts.width
                    });
                } else {
                    _draw({
                        chartData : chartData,
                        series : series,
                        lineAreaColumn : opts.lineAreaColumn,
                        lineColumn : opts.lineColumn,
                        xAxisCategories : xAxisCategories,
                        isSetYAxisMin: opts.isSetYAxisMin,
                        yUnit : opts.yUnit,
                        chartType : opts.chartType,
                        container : opts.container.get(0),
                        width : opts.width
                    });
                }
            };
            _self.showGraph( 0, opts.showNum );
            var pageBar = new $.page.PageFactory({
                showNum : opts.showNum,
                container : opts.container.parent(),
                total : opts.series.length,
                preEvent : function( pageIndex, showNum ){
                    _self.showGraph( ( pageIndex - 1 ) * showNum, showNum );
                },
                nextEvent : function( pageIndex, showNum ){
                    _self.showGraph( ( pageIndex - 1 ) * showNum, showNum );
                }
            });
        } else {
            if( opts.chartType == "pie" ){
                _draw_pie({
                    chartData : opts.chartData,
                    yUnit : opts.yUnit,
                    container : opts.container.get(0),
                    width : opts.width
                });
            } else {
                _draw({
                    chartData : opts.chartData,
                    series : opts.series,
                    xAxisCategories : opts.xAxisCategories,
                    lineAreaColumn : opts.lineAreaColumn,
                    chartType : opts.chartType,
                    lineColumn : opts.lineColumn,
                    isSetYAxisMin: opts.isSetYAxisMin,
                    yUnit : opts.yUnit,
                    container : opts.container.get(0),
                    width : opts.width
                });
            }
        }
    }
};

function chartPool()
{
    this.chart = [];
    this.color = {
        'navy_blue' : {
            rangeSelectorColor : '#8294B8',
            rangeSelectorHover : '#4F638C',
            colors : ['#4572A7','#AA4643','#89A54E','#80699B','#3D96AE','#DB843D','#92A8CD','#A47D7C','#B5CA92']
        },
        'green' : {
            rangeSelectorColor : '#8EC657',
            rangeSelectorHover : '#B3EC7C',
            colors : ['#89A54E','#4572A7','#AA4643','#80699B','#3D96AE','#DB843D','#92A8CD','#A47D7C','#B5CA92']
        },
        'orange' : {
            rangeSelectorColor : '#8E7E6E',
            rangeSelectorHover : '#BEA280',
            colors : ["#6CC4ED", "#E7A944", "#9D8C7A", "#72CB68", "#9B65FA", "#BADC36", "#FC71A6", "#E2E204", "#F8FF01"]
        }
    };
}

chartPool.prototype = {
    add: function(chart) {
        this.chart.push(chart);
    },
    remove: function(index) {
        this.chart[index] = null;
    },
    getAll: function() {
        return this.chart;
    },
    clear: function() {
        this.chart = [];
    },
    redraw: function() {
        $(this.chart).each(function() {
            var container = $(this.container).parent();
            this.showLoading();
            this.setSize(container.width(), container.height());
            this.hideLoading();
        });
    }
};

Highcharts.setOptions({
    global: {
        useUTC: false
    },
    lang: {
        rangeSelectorZoom: "显示时间段:"
    }
});

var CHART_POOL = new chartPool();

function Stock(){};
Stock.prototype = {
    getDateFormat: function(timeDimension, chartData){
        var xItv = 3600 * 1000, //一小时
            xNItv = 3600 * 1000, //1hour
            dateFormat = "%Y-%m-%d"; // 时间显示的格式
        switch(timeDimension){
            case "day" : //set the tickInterval
                var itv = 2;
                if( chartData && chartData.length != 0 ){
                    itv = Math.ceil( chartData[0].data.length/7 );
                    if( itv > 3 && itv < 14 ){
                        itv = 7;
                    }else if( itv >= 14 && itv < 27){
                        itv = 14;
                    }else if( itv >= 27 ){
                        itv = 21;
                    }
                }
                xItv = xItv * 24 * itv;
                xNItv = xItv * 24 * itv;
                dateFormat = "%Y-%m-%d";
                break;
            case "min" :
                xItv = xItv * 6;
                xNItv = xNItv * 24 * 1;
                dateFormat = "%Y-%m-%d %H:%M";
                break;
            case "onlymin" :
                xItv = xItv * 6;
                xNItv = xNItv * 24 * 1;
                dateFormat = "%H:%M";
                break;
            default:
                break;
        }
        return dateFormat;
    },
    //type 1: x轴时间 2: x轴数字
    getNavigatorXAxis: function(type){
        var xAxis = {};
        switch( type ){
            case 1:
                xAxis = {
                    dateTimeLabelFormats: {
                        second: '%H:%M:%S',
                        minute: '%H:%M',
                        hour: '%H:%M',
                        day: "%Y-%m-%d",
                        week: "%Y-%m-%d",
                        month: "%Y-%m"
                    }
                };
                break;
            case 2:
                xAxis = {
                    tickInterval: 3600 * 1000 * 24 * 365 * 6
                };
                break;
            default:
                break;
        }
        return xAxis;
    },
    //type 1: x轴时间 2: x轴数字
    getTooltip: function(type, dateFormat){
        var tooltip = {};
        switch( type ){
            case 1:
                tooltip = {
                    shared: true,
                    useHTML: true,
                    formatter: function(){
                        var dateStr = Highcharts.dateFormat(dateFormat, this.x),
                            s = '<table>'
                            + '<tr>' + dateStr + _get_tooltip_series(dateStr) + '</tr>';
                        $.each(this.points, function(i, point) {
                            s += '<tr>'
                                + '<td style="color: ' + point.series.color + '" >' +　point.series.name + '：' + '</td>'
                                + '<td>' + (Math.round ( point.y * 100 ) / 100).toString().addCommas() + '</td>'
                                + '</tr>';
                        });
                        return s;
                    }
                };
                break;
            case 2:
                tooltip = {
                    shared: true,
                    useHTML: true,
                    formatter: function(){
                        var s = '<table>';
                        $.each(this.points, function(i, point) {
                            s += '<tr>'
                                + '<td style="color: ' + point.series.color + '" >' +　point.series.name + '：' + '</td>'
                                + '<td>' + (Math.round( point.y * 100 ) / 100).toString().addCommas()  + '</td>'
                                + '</tr>';
                        });
                        return s;
                    }
                };
                break;
            default:
                break;
        }
        return tooltip;
    },
    //type 1: x轴时间 2: x轴数字
    getXAxis: function(type, timeDimension){
        var xAxis = {};
        switch(type){
            case 1:
                xAxis = {
                    dateTimeLabelFormats: {
                        second: '%H:%M:%S',
                        minute: '%H:%M',
                        hour: '%H:%M',
                        day: ( timeDimension && timeDimension == "onlymin" ? "-" : "%m-%d" ),
                        week: "%Y-%m-%d",
                        month: "%Y-%m"
                    }
                };
                break;
            case 2:
                xAxis = {
                    tickInterval: 3600 * 1000 * 24 * 365 * 6
                };
                break;
            default: break;
        }
        return xAxis;
    },
    getSeries: function(chartData, timeDimension){
        if(timeDimension == "day"){
            var series = [];
            if(chartData && $.isArray(chartData)){
                for(var i = 0; i < chartData.length; i++){
                    series.push({
                        pointStart: chartData[i].pointStart,
                        pointInterval: chartData[i].pointInterval,
                        name: chartData[i].name,
                        data: stock.setMarker(chartData[i].data, chartData[i].pointStart, chartData[i].pointInterval)
                    });
                }
            }
            return series;
        } else {
            return chartData;
        }
    },
    getNaviSeries: function(chartData){
        if(chartData && $.isArray(chartData) && chartData[0]){
            return {
                pointStart: chartData[0].pointStart,
                pointInterval: chartData[0].pointInterval,
                name: chartData[0].name,
                data: stock.setMarker(chartData[0].data, chartData[0].pointStart, chartData[0].pointInterval, true)
            };
        } else {
            return {};
        }
    },
    setMarker: function(data, start, interval, navi){
        var rlt = [];
        for(var i = 0; i < data.length; i++){
            var d = new Date();
            d.setTime(start + i * interval);
            rlt.push({
                y: data[i],
                marker: {
                    enabled: navi ? false: (d.getDay() == 0 ? true: false),
                    symbol: navi ? null :  (d.getDay() == 0 ? 'url(../../../../image/common/sun.png)': null)
                }
            })
        }
        return rlt;
    }
};
var stock = new Stock();
/**
 * create stock or general timeline
 * including sophisticated navigation options
 * like a small navigator series, preset date ranges, date picker, scrolling and panning
 * timeDimension : 天数据或分钟数据; day, minute; default(day)
 * xAxisType : x轴显示时间数据还是非时间 1:时间 2：数字 ;default(1)
 */
function _draw_stock( options ){
    var opts = {
        timeDimension : "day",
        chartType : "line",
        container : null,
        xAxisType : 1,
        xArr : [],
        chartData :[],
        columnStack: '',
        colorTheme : 'orange'
    };
    $.extend( opts, options );
	var stockChart = new Highcharts.StockChart({
		chart: {
            renderTo: opts.container,
            width : opts.width,
            height : 300,
            type: opts.chartType,
            zoomType: "x"
        },
        colors : CHART_POOL.color[opts.colorTheme].colors,
		series: stock.getSeries(opts.chartData, opts.timeDimension),
        title: {
            text: null
        },
        credits: {
            enabled: false
        },
        legend: {
        	enabled: true,
        	align: 'bottom',
        	borderWidth: 0,
        	floating: true,
        	x: 0,
        	y: 13
        },
        rangeSelector: {
            enabled: false
        },
        navigator: {
            series: stock.getNaviSeries(opts.chartData),
            xAxis : stock.getNavigatorXAxis(opts.xAxisType)
        },
        plotOptions : {
            column : {
                stacking: opts.columnStack,
                pointPadding : 0.2,
                borderWidth : 0,
                cropThreshold : 10,
                pointPadding : 0,
                dataLabels : {
                    formatter:function(){
                        return this.point.y;
                    },
                    enabled : true
                },
                pointWidth : 16
            },
            series : {
                dataGrouping : {
                    approximation : "close"
                }
            }
        },
        tooltip: stock.getTooltip(opts.xAxisType, stock.getDateFormat(opts.timeDimension, opts.chartData)),
        xAxis: stock.getXAxis(opts.xAxisType, opts.timeDimension),
        yAxis: { min: 0 }
	});
    CHART_POOL.add(stockChart);
}
function Chart(){};
Chart.prototype = {
    //获取一般画highcharts的z轴, 当画bar时，用双x轴显示百分数
    getXAxis: function(opts){
        var xAxis = [];
        xAxis.push({
            categories : opts.series,
            tickInterval : chart._getXAxisTickInterval(opts.series),
            labels : {
                style : {
                    fontSize : "12px"
                }
            },
            title : {
                text : opts.xUnit,
                align : 'high' ,
                style : { color : '#999'}
            }
        });
        if( opts.xAxisCategories.length ){
            xAxis.push({
                linkedTo : 0,
                categories : opts.xAxisCategories,
                labels : {
                    style : { fontSize : "12px", color : '#6CC4ED' },
                    format : '{value}'
                },
                opposite : true
            });
        }
        return xAxis;
    },
    _getXAxisTickInterval: function(series){
        return Math.ceil( series.length / 15 );
    },
    //标注星期天
    _getAxisPlotLines: function(series){
        series = $.isArray(series) ? series : [];
        var len = series.length,
            plotLines = [];
        if(len && $.date.isDate(series[0])) {
            for(var i = 0; i < series.length; i++){
                if($.date.getWeekNum(series[i]) == 0){
                    plotLines.push({
                        color: '#6CC4ED',
                        dashStyle: 'DashDot',
                        width: 1,
                        value: i
                    });
                }
            }
        }
        return plotLines;
    },
    //获取y轴（双y轴）
    getYAxis: function(opts){
        if( opts.lineColumn ){
            return [{
                min : 0,
                title : { text : '', align : 'high', style : { color : '#999'} },
                labels : {
                    style : { fontSize : "12px", color : "#61B1D6" },
                    formatter: function(){
                        return ((typeof this.value == "number") ? _get_number(this.value) : this.value)
                            + (opts.yUnit[0] ? opts.yUnit[0] : '');
                    }
                }
            },{
                min : 0,
                title : { text : '', align : 'high', style : { color : '#999'} },
                labels : {
                    style : { fontSize : "12px", color : '#CA943B' },
                    formatter: function(){
                        return ((typeof this.value == "number") ? _get_number(this.value) : this.value)
                            + (opts.yUnit[1] ? opts.yUnit[1] : '');
                    }
                },
                opposite : true
            }];
        } else {
            return [{
                min : (opts.isSetYAxisMin ? _getYAxisMin(opts.chartData) : 0),
                title : { text : '', align : 'high', style : { color : '#999'} },
                labels : {
                    style : { fontSize : "12px" },
                    formatter: function(){
                        return ((typeof this.value == "number") ? _get_number(this.value) : this.value)
                            + (opts.yUnit[0] ? opts.yUnit[0] : '');
                    }
                }
            }];
        }
    }
};
var chart = new Chart();
/**
 * @brief _draw
 * 画图功能
 * @param options
 * type  string  图的样式( Can be one of line, spline, area, areaspline, column, bar, pie and scatter )
 *
 * @return
 */
function _draw( options ){
    var opts = {
        chartData : [],
        series : [],
        width : 400,
        chartType : "line",
        lineColumn : false,
        xUnit : '',
        yUnit : [],
        isSetYAxisMin: false,
        container : null
    };
    $.extend( opts, options );
    Highcharts.setOptions({
        lang: {
            numericSymbols: null,
            thousandsSep: ","
        }
    });
    if( opts.lineAreaColumn ){
        _draw_lineAreaColumn( opts );
        return;
    }
    $(opts.container).highcharts({
        chart : {
            zoomType : 'x',
            renderTo : opts.container,
            type : opts.chartType,
            width : opts.width,
            height: 300
        },
        colors : _get_colors(),
        credits : {
            enabled : false
        },
        title : {
            text : null
        },
        xAxis : chart.getXAxis(opts),
        yAxis : chart.getYAxis(opts),
        tooltip : {
            shared: true,
            useHTML: true,
            percentageDecimals : 1,
            formatter: function(){
                var s = '<table>' + '<tr><td colspan="2">' + this.x + _get_tooltip_series(this.x) + '</td></tr>';
                $.each(this.points, function(i, point) {
                    s += '<tr>'
                        + '<td style="color: ' + point.series.color + '" >'
                        + ( point.point.name ? point.point.name : point.series.name ) + '：' + '</td>'
                        + '<td>' + (Math.round ( point.y * 100 ) / 100).toString().addCommas()
                        + (opts.yUnit[i] ? opts.yUnit[i] : (opts.yUnit[0] ? opts.yUnit[0] : '')) + '</td>'
                        + ( point.point.per ? ( '<td>(' + point.point.per + ')</td>' ) : '')
                        + '</tr>';
                });
                s += '</table>';
                return s;
            }
        },
        plotOptions : {
            column : {
                pointPadding : 0.2,
                borderWidth : 0,
                cropThreshold : 10,
                pointPadding : 0,
                dataLabels : {
                    formatter:function(){
                        return this.point.y;
                    },
                    enabled : true
                },
                pointWidth : 16
            },
            bar : {
                dataLabels : {
                    enabled : true
                },
                pointWidth: 16
            }
        },
        //legend : {
            //enabled : false
        //},
        series : opts.chartData
    });
}

function _get_tooltip_series(str){
    return $.date.getChWeek(str)
            ? "（" + $.date.getChWeek(str) + "）"
            : "";
}
/**
 * @brief _draw_lineAreaColumn
 * 画折线图，柱状图，面积图在一张图中
 * @param opts
 *
 * @return
 */
function _draw_lineAreaColumn( opts ){
    $(opts.container).highcharts({
        chart : {
            zoomType : 'x',
            renderTo : opts.container,
            type : opts.chartType,
            width : opts.width,
            height: 300
        },
        colors : [ "#6CC4ED", "#E7A944", "#9D8C7A" ],
        credits : {
            enabled : false
        },
        title : {
            text : null
        },
        xAxis : chart.getXAxis(opts),
        yAxis : chart.getYAxis(opts),
        tooltip : {
            shared: true,
            useHTML: true,
            percentageDecimals : 1,
            formatter: function(){
                var s = '<table>';

                $.each(this.points, function(i, point) {
                    s += '<tr>'
                        + '<td style="color: ' + point.series.color + '" >' +　point.series.name + '：' + '</td>';
                    s += ( point.point.per
                            ? '<td>' + point.point.per + '</td>'
                            : '<td>' + (Math.round ( point.y * 100 ) / 100).toString().addCommas() + '</td>'
                         )
                        + '</tr>';
                });
                s += '</table>';
                return s;
            }
        },
        plotOptions : {
            //series : {
                //stacking : 'normal'
            //},
            column : {
                pointPadding : 0.2,
                borderWidth : 0,
                cropThreshold : 10,
                pointPadding : 0,
                dataLabels : {
                    x : 0,
                    y : 60,
                    enabled : true
                },
                pointWidth : 16
            },
            area : {
                marker:{
                    color : "#9D8C7A",
                    enabled:!0,
                    states:{
                        hover:{
                            enabled:!0
                        }
                    }
                },
                fillOpacity : 0,
                lineWidth:0,
                dataLabels:{
                    color : "#9D8C7A",
                    enabled:!0,
                    formatter:function(){
                        return this.point.per;
                    }
                }
            },
            line : {
                dataLabels:{
                    color : "#E7A944",
                    enabled:!0,
                    x:-56,
                    y:20,
                    formatter:function(){
                        return this.point.x == 0 ? "" : this.point.per;
                    }
                }
            }
        },
        legend : {
            enabled : false
        },
        series : opts.chartData
    });

}

function _get_number(data){
    var yi = 100000000, wan = 10000;
    return data > yi
        ? Math.round(data / yi * 100) / 100 + '亿'
        : data > wan
            ? Math.round(data / wan * 100) / 100 + '万'
            : data;
}
/**
 * @brief _draw_pie
 *
 * @param chartData
 * @param id
 *
 * @return
 */
function _draw_pie( options ){
    var opts = {
        chartData : [],
        width : 400,
        type : "pie",
        yUnit : [],
        container : null
    };

    $.extend( opts, options );
    var floating = true, x = -20, y = -10, verticalAlign = "bottom",
        itemStyle = {
            height: "14px",
            lineHeight: "14px",
            fontSize: "12px",
            color: "#333"
        };
        center = ["40%", "50%"];

    //var chart = new Highcharts.Chart({
    $(opts.container).highcharts({
        chart : {
            renderTo : opts.container,
            plotBackgroundColor : null,
            plotBorderWidth : null,
            plotShadow : false,
            marginBottom : 0,
            marginTop : 0,
            width : opts.width,
            height : 300,
            spacingBottom : 0,
            spacingTop : 0
        },
        colors : _get_colors(),
        credits : {
            enabled: false
        },
        legend : {
            align : "right",
            verticalAlign : verticalAlign,
            layout : "vertical",
            labelFormatter : function() {
                return this.name + "：<b>" + this.y + (opts.yUnit[0] ? opts.yUnit[0] : '') + "</b>(" + Math.round( this.per * 10 ) / 10 + "%)";
            },
            borderWidth : 0,
            x : x,
            y : y,
            floating : floating,
            itemStyle : itemStyle
        },
        title : {
            text : null
        },
        tooltip : {
            useHTML : true,
            headerFormat : '<h4>{point.key}</h4><table style="margin-top: 5px;">',
            pointFormat : '<tr><td style="word-break: keep-all;" nowrap>{series.name}：</td>'
                + '<td style="text-align: right"><b>{point.y}</b></td><td>({point.per:.1f}%)</td></tr>',
            footerFormat : "</table>",
            percentageDecimals : 1
        },
        plotOptions : {
            pie : {
                allowPointSelect : true,
                center : center,
                cursor : "pointer",
                ignoreHiddenPoint : true,
                dataLabels : {
                    enabled : false
                },
                showInLegend : true
            }
        },
        series : [{
            type : "pie",
            name : "比例",
            data : opts.chartData
        }]
    });
}
/**
 * @brief _get_colors
 * 获得color的颜色数组
 * @return
 */
function _get_colors(){
    return [ "#6CC4ED", "#E7A944", "#9D8C7A", "#72CB68", "#9B65FA", "#BADC36", "#FC71A6", "#E2E204", "#F8FF01" ];
}
function _getYAxisMin(data){
    var min = 0, max = 0;
    min = data[0] && data[0].data[0] && data[0].data[0].y ? data[0].data[0].y : min;
    for(var i = 0; i < data.length; i++){
        $.each(data[i].data, function(){
            min = this.y < min ? this.y : min;
            max = this.y > max ? this.y : max;
        });
    }
    min = Math.floor(min - (max - min) / 2)
    return min > 0 ? min : 0;
}
})(jQuery);
