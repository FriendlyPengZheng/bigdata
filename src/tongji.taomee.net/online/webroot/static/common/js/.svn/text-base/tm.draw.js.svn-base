(function($, window, undefined){
HIGH_STOCK_INTERVAL = [];
HIGH_STOCK_KEY = 0;
Highcharts.setOptions({
    lang: {
        numericSymbols: null,
        thousandsSep: ",",
        rangeSelectorZoom: "显示时间段:"
    },
    global: {
        useUTC: false
    }
});

var orgHighchartsRangeSelectorPrototypeRender = Highcharts.RangeSelector.prototype.render;
Highcharts.RangeSelector.prototype.render = function (min, max) {
    orgHighchartsRangeSelectorPrototypeRender.apply(this, [min, max]);
    var leftPosition = this.chart.plotSizeX - 300,
        topPosition = this.chart.plotTop - 50,
        space = 2;
    this.zoomText.attr({
        x: leftPosition,
        y: topPosition + 20
    });
    leftPosition += this.zoomText.getBBox().width + space;
    for (var i = 0; i < this.buttons.length; i++) {
        this.buttons[i].attr({
            x: leftPosition,
            y: topPosition 
        });
        leftPosition += this.buttons[i].width + space;
    }
};

function Chart(){}
Chart.prototype = {
colors: {
    'blue': {
        colors: ["#1E9D98", "#E7A944", "#14D279", "#D197FF", "#20CFC8", "#2743FF", "#9D8C7A", "#FC71A6"]
    },
    'green': {
        rangeSelectorColor: '#8EC657',
        rangeSelectorHover: '#B3EC7C',
        colors: ['#89A54E', '#4572A7','#AA4643','#80699B','#3D96AE','#DB843D','#92A8CD','#A47D7C','#B5CA92']
    },
    'orange': {
        rangeSelectorColor: '#8E7E6E',
        rangeSelectorHover: '#BEA280',
        colors: ["#6CC4ED", "#E7A944", "#9D8C7A", "#72CB68", "#9B65FA", "#BADC36", "#FC71A6", "#20CFC8", "#E2E204"]
    }
},
getColors: function(theme){
    theme = theme ? theme : "orange";
    return this.colors[theme];
},
//should show two xAxes when xAxisCategories is not null, eg: need show percentage when chartType is "bar"
getXAxis: function(series, xUnit, xAxisCategories) {
    var that = this, xAxis = [];
    xAxis.push({
        categories: series,
        tickInterval: that._getXAxisTickInterval(series),
        labels: {
            style: {
                fontSize: "12px"
            }
        },
        title: {
            text: (xUnit[0] ? xUnit[0] : ""),
            align: 'high',
            style: { color: '#999'}
        }
    });
    if(xAxisCategories.length) {
        xAxis.push({
            linkedTo: 0,
            categories: xAxisCategories,
            labels: {
                style: {
                    fontSize: "12px",
                    color : '#6CC4ED'
                },
                format: '{value}'
            },
            opposite: true
        });
    }
    return xAxis;
},
//get the tick interval for xAxis
_getXAxisTickInterval: function(series) {
    return Math.ceil(series.length / 15);
},
getYAxis: function(yUnit, isSetYAxisMin, doubleYAxis) {
    var that = this, yAxis = [],
        config = {
            title: {
                text: '',
                align: 'high',
                style: { color: '#999' }
            },
            labels: {
                style: { fontSize: "12px" },
                formatter: function() {
                    return ((typeof this.value == "number") ? that._handleNumber(this.value) : this.value)
                        + (yUnit[0] ? yUnit[0] : '');
                }
            }
        };
    if(isSetYAxisMin) config.min = 0;
    yAxis.push(config);
    if(doubleYAxis) {
        yAxis.push({
            min: 0,
            title: {
                text: '',
                align: 'high',
                style: { color : '#999'}
            },
            labels: {
                style: {
                    fontSize : "12px",
                    color : '#CA943B'
                },
                formatter: function(){
                    return ((typeof this.value == "number") ? that._handleNumber(this.value) : this.value)
                        + (yUnit[1] ? yUnit[1] : '');
                }
            },
            opposite : true
        });
    }
    return yAxis;
},
_handleNumber: function(data){
    var yi = 100000000, wan = 10000;
    return data > yi
        ? Math.round(data / yi * 100) / 100 + '亿'
        : data > wan
            ? Math.round(data / wan * 100) / 100 + '万'
            : data;
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
getTooltip: function(yUnit){
    var that = this;
    return {
        shared: true,
        useHTML: true,
        percentageDecimals : 1,
        formatter: function(){
            var s = '<table>' + '<tr><td colspan="2">' + this.x + that._getTooltipSeries(this.x) + '</td></tr>';
            $.each(this.points, function(i, p) {
                s += '<tr>'
                + '<td style="color: ' + p.series.color + '" >'
                + ( p.point.name ? p.point.name : p.series.name ) + '：' + '</td>'
                + '<td>' + (Math.round (p.y * 100) / 100).toString().addCommas()
                + (yUnit[p.series.index] ? yUnit[p.series.index] : (yUnit[0] ? yUnit[0] : '')) + '</td>'
                + (p.point.per ? ( '<td>(' + p.point.per + ')</td>' ) : '')
                + '</tr>';
            });
            s += '</table>';
            return s;
        }
    };
},
_getTooltipSeries: function(str){
    return $.date.getChWeek(str)
            ? "（" + $.date.getChWeek(str) + "）"
            : "";
},
getLegend: function(lineAreaColumn) {
    if(lineAreaColumn) {
        return {
            enabled: false
        };
    } else {
        return {};
    }
},
getPlotOptions: function(lineAreaColumn, pointNumber, containerWidth){
    var pointWidth = Math.ceil(containerWidth / pointNumber / 2),
        columnDataLabelEnabled = false;
    pointWidth || (pointWidth = 1);
    pointWidth > 16 && (pointWidth = 16) && (columnDataLabelEnabled = true);
    if(lineAreaColumn) {
        return {
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
                pointWidth : pointWidth
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
        };
    } else {
        return {
            column: {
                pointPadding: 0.2,
                borderWidth: 0,
                cropThreshold: 10,
                pointPadding: 0,
                dataLabels: {
                    formatter:function(){
                        return this.point.y;
                    },
                    enabled: columnDataLabelEnabled
                },
                pointWidth: pointWidth
            },
            bar: {
                dataLabels: {
                    enabled: true
                },
                pointWidth: 16
            }
        };
    }
}
};
var chart = new Chart();

/**
 * @brief Stock
 * create large number
 * @return
 */
function Stock() {};
Stock.prototype = {
getMax: function(data) {
    var max = 0;
    for(var i = 0; i < data.length; i++) {
        for(var j = 0; j < data[i].data.length; j++) {
            max = data[i].data[j] > max ? data[i].data[j] : max;
        }
    }
    return max;
},
getLegend: function(o) {
    if($(window).outerHeight() < 500 && $(window).outerWidth() < 500) {
        return {};
    } else {
        return {
            enabled: true,
            align: 'left',
            layout: 'vertical',
            verticalAlign: 'top',
            floating: true,
            backgroundColor: "rgba(255, 255, 255, 0.8)",
            borderColor: "#206F86",
            borderWidth: 1,
            itemStyle: {
                color: "#144E5F",
                padding: "4px",
                fontSize: "12px"
            },
            labelFormatter: function() {
                return this.name;
            },
            x: 50
        };
    }
    //return {
        //enabled: true,
        //align: 'bottom',
        //backgroundColor: "rgba(255, 255, 255, 0.8)",
        //borderColor: "#206F86",
        //borderWidth: 0,
        //itemStyle: {
            //color: "#144E5F",
            //padding: "4px",
            //fontSize: "12px"
        //}
    //}
},
getDateFormat: function(timeDimension, chartData) {
    var xItv = 3600 * 1000, //一小时
        xNItv = 3600 * 1000, //1hour
        dateFormat = "%Y-%m-%d"; // 时间显示的格式
    switch(timeDimension) {
        case "day": //set the tickInterval
            var itv = 2;
            if(chartData && chartData.length != 0){
                itv = Math.ceil(chartData[0].data.length/7);
                if(itv > 3 && itv < 14) {
                    itv = 7;
                } else if(itv >= 14 && itv < 27) {
                    itv = 14;
                }else if(itv >= 27) {
                    itv = 21;
                }
            }
            xItv = xItv * 24 * itv;
            xNItv = xItv * 24 * itv;
            dateFormat = "%Y-%m-%d";
            break;
        case "min":
            xItv = xItv * 6;
            xNItv = xNItv * 24 * 1;
            dateFormat = "%Y-%m-%d %H:%M";
            break;
        case "onlymin":
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
getNavigatorXAxis: function(type) {
    var xAxis = {};
    switch(type) {
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
getTooltip: function(type, dateFormat) {
    var tooltip = {};
    switch(type) {
        case 1:
            tooltip = {
                shared: true,
                useHTML: true,
                formatter: function(){
                    var dateStr = Highcharts.dateFormat(dateFormat, this.x),
                        s = '<table>'
                        + '<tr>' + dateStr + chart._getTooltipSeries(dateStr) + '</tr>';
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
getXAxis: function(type, timeDimension) {
    var xAxis = {};
    switch(type){
        case 1:
            xAxis = {
                dateTimeLabelFormats: {
                    second: '%H:%M:%S',
                    minute: '%H:%M',
                    hour: '%H:%M',
                    day: (timeDimension && timeDimension == "onlymin" ? "-" : "%m-%d"),
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
getSeries: function(chartData, timeDimension) {
    if(timeDimension == "day") {
        var series = [];
        if(chartData && $.isArray(chartData)) {
            for(var i = 0; i < chartData.length; i++) {
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
getNaviSeries: function(chartData) {
    if(chartData && $.isArray(chartData) && chartData[0]) {
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
setMarker: function(data, start, interval, navi) {
    var rlt = [];
    for(var i = 0; i < data.length; i++){
        var d = new Date();
        d.setTime(start + i * interval);
        rlt.push({
            y: data[i],
            marker: {
                enabled: navi ? false: (d.getDay() == 0 ? true: false),
                symbol: navi ? null :  (d.getDay() == 0 ? 'url(./static/common/images/sun.png)': null)
            }
        })
    }
    return rlt;
},
getPlotOptions: function(pointNumber, containerWidth, columnStack){
    var pointWidth = Math.ceil(containerWidth / pointNumber / 2),
        columnDataLabelEnabled = false;
    pointWidth || (pointWidth = 1);
    pointWidth > 16 && (pointWidth = 16) && (columnDataLabelEnabled = true);
    return {
        column: {
            stacking: columnStack,
            pointPadding: 0.2,
            borderWidth: 0,
            cropThreshold: 10,
            pointPadding: 0,
            dataLabels: {
                formatter:function(){
                    return this.point.y;
                },
                enabled: columnDataLabelEnabled
            },
            pointWidth: pointWidth
        },
        line: {
            dataGrouping: {
                approximation: "open"
            }
        }
    };
},
getEvents: function(loadUrl, o) {
    var container = $(o.container.get(0));
    if(!container.attr("data-charts-key")) {
        container.attr("data-charts-key", HIGH_STOCK_KEY++);
    }
    for(var i = 0; i < HIGH_STOCK_INTERVAL.length; i++) {
        clearInterval(HIGH_STOCK_INTERVAL[i]);
    }
    HIGH_STOCK_INTERVAL = [];
    HIGH_STOCK_KEY = 0;
    if(loadUrl && _isTodayInTime()) {
        return {
            load: function(){
                var that = this;
                var notice = $(document.createElement("div")).addClass("notice-tips")
                    .text("刷新中...").appendTo($("body"));
                HIGH_STOCK_INTERVAL[container.attr("data-charts-key")] = setInterval(function(){
                    notice.show();
                    ajax(loadUrl, window.getPageParam(), function(res){
                        if(res.result == 0) {
                            notice.hide();
                            var data = [], max = 0;
                            $.each(res.data, function(){
                                $.each(this.data, function(){
                                    var tmp = [];
                                    for(var i = 0; i < this.data.length; i++){
                                        this.data[i] = this.data[i] == null ? null : parseFloat(this.data[i]);
                                        tmp.push(this.data[i]);
                                        max = this.data[i] > max ? this.data[i] : max;
                                    }
                                    data.push(tmp);
                                });
                            });
                            if(that.series) {
                                for(var i = 0; i < that.series.length; i++) {
                                   that.series[i].setData(data[i], true);
                                }
                            }
                            if(o.showMax && that.setTitle) {
                                that.setTitle({
                                    text: o.title + "【" + (max ? max.toString().addCommas(",") : 0)  + "】"
                                });
                            }
                        }
                    }, "POST");
                }, 5 * 60 * 1000);
            }
        };
    }
    return {};
}
};
var stock = new Stock();
/**
 * @params chartStock {boolean} can use highstock when large data
 * @params showNum {int} need show number when page
 * @params page {boolean} whether page or not
 * @params chartData {array} data
 * @params columnStack eg: "", "percentage"
 * @params doubleYAxis {boolean} should show two y axes when true
 * @params lineAreaColumn {boolean} whether show line, column, area together or not
 * @params xUnit {array} the unit of each xAxis
 * @params yUnit {array} the unit of each yAxis
 * @params series {array} used to create the axis
 * @params xAxisCategories {array} the array used to create doouble axes
 * @params isSetYAxisMin {boolean} whether need set yAxis min or not
 * @params chartType {string} type of chart,eg: "line", "column", "bar", "area", "areaspline", "scatter", "spline"
 * @params width
 * @params container {object} the container of chart
 * ---------for highstock------------
 * @params timeDimension {string} "day", "minute", "onlymin", default is "day"
 * @params xAxisType {int} the type of xAxis eg: 1: "时间",2:  "数字", default is 1
 */
$.draw = {
options: {
    showNum: 10,
    page: true,
    chartData: [],
    doubleYAxis: false,
    lineAreaColumn: false,
    loadUrl: "",
    xUnit: [],
    yUnit: [],
    series: [],
    xAxisCategories: [],
    isSetYAxisMin: true,
    chartType: "line",
    width: 400,
    height: 300,
    container: $("body"),
    //for highstock
    chartStock: false,
    navigator: true,
    title: "",
    showMax: false,
    columnStack: '',
    timeDimension: "day",
    xAxisType: 1
},
colors: chart.getColors("blue").colors,
DrawFactory: function(option) {
    this.options = $.extend({}, $.draw.options, option);
    this.options.showNum = option.showNum ? option.showNum
        : (this.options.chartType == "bar" ? 10 : Math.ceil(this.options.width - 110)/48);
    this.chartData = this.options.chartData;
    this.series = this.options.series;
    this.xAxisCategories = this.options.xAxisCategories;
    var that = this, o = this.options;
    //分页显示
    this._showChart =  function(start, showNum){
        var end = 0,
            chartData = [],
            series = [],
            xAxisCategories = [],
            length = that.series.length;
        start = (start >= 0 && start < length) ? start : 0;
        end = start + showNum <= length ? start + showNum : length;
        xAxisCategories = that.xAxisCategories.slice(start, end);
        series = that.series.slice(start, end);
        $.each(that.chartData, function(i){
            this.data = this.data ? this.data : [];
            var tmp = {
                data: this.data.slice(start, end),
                name: this.name
            };
            if(this.type) tmp.type = this.type;
            if(that.options.doubleYAxis && i == 1) tmp.yAxis = 1;
            chartData.push(tmp);
        });
        var options = $.extend({}, that.options, {
            chartData: chartData,
            series: series,
            xAxisCategories: xAxisCategories
        });
        if(that.options.chartType == "pie"){
            $.draw.drawPie(options);
        } else {
            $.draw.drawChart(options);
        }
    };
    if(this.chartData.length) {
        if(o.chartStock) {
            $.draw.drawStock(this.options);
        } else {
            if(this.options.page) {
                that._showChart(0, o.showNum);
                new $.page.PageFactory({
                    showNum: o.showNum,
                    container: o.container.parent(),
                    total: o.series.length,
                    preEvent: function(pageIndex, showNum) {
                        that._showChart((pageIndex - 1) * showNum, showNum);
                    },
                    nextEvent: function(pageIndex, showNum) {
                        that._showChart((pageIndex - 1) * showNum, showNum);
                    }
                });
            } else {
                if(o.chartType == "pie") {
                    $.draw.drawPie(this.options);
                } else {
                    $.draw.drawChart(this.options);
                }
            }
        }
    } else {
        this.options.container.text("no data...");
    }
},
drawChart: function(o){
    var container = o.container.get(0);
    $(container).highcharts({
        chart: {
            zoomType : 'x',
            renderTo : container,
            type : o.chartType,
            width : o.width,
            height: o.height
        },
        colors: $.draw.colors,
        credits: {
            enabled: false
        },
        title: {
            text: null
        },
        xAxis: chart.getXAxis(o.series, o.xUnit, o.xAxisCategories),
        yAxis: chart.getYAxis(o.yUnit, o.isSetYAxisMin, o.doubleYAxis),
        tooltip: chart.getTooltip(o.yUnit),
        plotOptions: chart.getPlotOptions(o.lineAreaColumn, o.series.length, o.width),
        legend: chart.getLegend(o.lineAreaColumn),
        series: o.chartData
    });
},
drawPie: function(o){
    var container = o.container.get(0);
    $(container).highcharts({
        chart: {
            renderTo: container,
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            marginBottom: 0,
            marginTop: 0,
            width: o.width,
            height: 300,
            spacingBottom: 0,
            spacingTop: 0
        },
        colors: $.draw.colors,
        credits: {
            enabled: false
        },
        title: {
            text: null
        },
        legend: {
            align: "right",
            verticalAlign: "bottom",
            layout: "vertical",
            labelFormatter: function() {
                return this.name + "：<b>" + this.y + (o.yUnit[0] ? o.yUnit[0] : '') + "</b>(" + Math.round( this.per * 10 ) / 10 + "%)";
            },
            borderWidth: 0,
            x: -20,
            y: -10,
            floating: true,
            itemStyle: {
                height: "14px",
                lineHeight: "14px",
                fontSize: "12px",
                color: "#333"
            }
        },
        tooltip: {
            useHTML: true,
            headerFormat: '<h4>{point.key}</h4><table style="margin-top: 5px;">',
            pointFormat: '<tr><td style="word-break: keep-all;" nowrap>{series.name}：</td>'
                + '<td style="text-align: right"><b>{point.y}</b></td><td>({point.per:.1f}%)</td></tr>',
            footerFormat: "</table>",
            percentageDecimals: 1
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                center: ["40%", "50%"],
                cursor: "pointer",
                ignoreHiddenPoint: true,
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series : [{
            type : "pie",
            name : "比例",
            data : o.chartData
        }]
    });
},
drawStock: function(o){
    var stockChart = new Highcharts.StockChart({
        chart: {
            zoomType: "",
            type: o.chartType,
            backgroundColor: "#FFF",
            renderTo: o.container.get(0),
            width: o.width,
            height: $(window).height() < 350 ? 300 : o.height,
            events: stock.getEvents(o.loadUrl, o),
        },
        colors: $.draw.colors,
        title: {
            text: (o.title ? o.title : ""),
            style: {
                fontWeight: "bold",
                color: "#304146",
                fontSize: "20px"
            }
        },
        credits: {
            enabled: false
        },
        series: stock.getSeries(o.chartData, o.timeDimension),
        legend: stock.getLegend(o),
        rangeSelector: {
            enabled: true,
            buttons: [{
                type: "minute",
                count: "1140",
                text: "5点后数据"
            }, {
                type: "all",
                text: "所有数据"
            }],
            buttonTheme: { // styles for the buttons
                fill: '#EEE',
                stroke: 'none',
                width: 100,
                'stroke-width': 0,
                padding: 5,
                style: {
                    color: '#206F86',
                    fontWeight: 'bold',
                    fontSize: "14px"
                },
                states: {
                    hover: {
                        fill: '#DFDFDF',
                        style: {
                            color: '#206F86',
                            cursor: "pointer"
                        }
                    },
                    select: {
                        fill: '#206F86',
                        style: {
                            color: 'white'
                        }
                    }
                }
            },
            selected: 0,
            inputEnabled: false
        },
        scrollbar: {
            enabled: (o.navigator ? true : false)
        },
        navigator: {
            enabled: (o.navigator ? true : false),
            series: stock.getNaviSeries(o.chartData),
            xAxis: stock.getNavigatorXAxis(o.xAxisType)
        },
        plotOptions: stock.getPlotOptions(o.series.length, o.width, o.columnStack),
        tooltip: stock.getTooltip(o.xAxisType, stock.getDateFormat(o.timeDimension, o.chartData)),
        xAxis: stock.getXAxis(o.xAxisType, o.timeDimension),
        yAxis: { min: 0 }
    });
    if(o.showMax && stockChart.setTitle) {
        var max = stock.getMax(o.chartData);
        stockChart.setTitle({
            text: o.title + "【" + (max ? max.toString().addCommas(",") : 0) + "】"
        });
    }
}
};
function _isTodayInTime() {
    var time = $("#J_time").val().split(","),
        now = ($.date.getNow()).split("-");
    for(var i = 0; i < time.length; i++) {
        var tmp = time[i].split("-");
        if(+new Date(now[0], now[1], now[2]) == +new Date(tmp[0], tmp[1], tmp[2])) {
            return true;
        }
    }
    return false;
}
})(jQuery, window, undefined);
