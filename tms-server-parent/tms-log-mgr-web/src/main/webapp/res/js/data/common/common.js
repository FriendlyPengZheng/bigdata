/**
 * @brief gModule
 * 根据配置生成页面
 * need json file
 * @return
 */
function gModule() {
    // for debug
	// TODO 路径需要修改
    console.log("/tms-log-mgr-web/json/" + _getTopBarKey() + "/" + _getModuleKey() + ".json");
    //$.getJSON("json/" + window.responseData.locale + "/" + _getTopBarKey() + "/" + _getModuleKey() + ".json", null, function(data) {
    // 通过controler返回json文件，这是一个比较合理的做法
    // TODO 路径需要更改
    
    //$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-overview-board.json", null, _setCompoment);
    //$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-realtime.json", null, _setCompoment);
    //$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-overview-keymetrics.json", null, _setCompoment);
    //$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-overview-keymetrics-2.json", null, _setCompoment);
    //$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-players-new.json", null, _setCompoment);
    //$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-players-keepers.json", null, _setCompoment);
    //$.getJSON("/tms-log-mgr-web/json/" + "gameanalysis/" + "gameanalysis-whale.json", null, _setCompoment);
    $.getJSON("/tms-log-mgr-web/json/" + _getTopBarKey() + "/" + _getModuleKey() + ".json", null, _setCompoment);
    
    //获取gameId的数据
    var gameId =  $("#J_paramGameId").val();
    console.log("gameID: " + gameId + "\n");
    $.getJSON("/tms-log-mgr-web/json/" + _getTopBarKey() + "/" + _getModuleKey() + "-" + gameId + ".json", null, _setCompoment);
}

/**
 * @brief _setCompoment
 * set component
 * @return
 */
function _setCompoment(data){
	 console.log("window.responseData is " + JSON.stringify(window.responseData, null, 4));
     var prepared = [];
     $(data).each(function(i) {
         if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore)) {
             // for debug
             console.log("ignore id is " + this.ignoreId);
             this.container = $("#J_contentBody");
             if(i == 0 && this.type == "tabs" ) {
                 this.tabsSkin = this.child[0] && this.child[0].tabsSkin
                     ? this.child[0].tabsSkin : this.tabsSkin;
                 if(this.tabsSkin)$("#J_contentHeader").css({'position' : 'absolute', 'top' : '10px', 'right' : '10px'});
             }
             prepared.push(this);
         }
     });
     console.log("prepared size is " + prepared.length);
     //$("#J_contentBody").data("content-data", fac(prepared));
     var tmp = $("#J_contentBody").data("content-data");
     if(tmp == null){
     	$("#J_contentBody").data("content-data", fac(prepared));
     } else {
     	$("#J_contentBody").data("content-data", tmp.concat(fac(prepared)));
     }
}

/**
 * @brief _getModuleKey
 * get module key
 * game_type - aside_parent - aside_child
 * @return
 */
function _getModuleKey(){
    var parentKey = '',
        childKey = '';
    $("#J_aside").find(".cur").each(function(){
        var t = $(this);
        if(t.hasClass("parent")){
            parentKey = t.attr("data-key");
        } else if(t.hasClass("child")) {
            childKey = t.attr("data-key");
        }
    });
    return _getTopBarKey() + '-' + parentKey + (childKey ? '-' + childKey : '');
}

/**
 * @brief _getTopBarKey
 * 获得一级导航key
 * @return
 */
function _getTopBarKey(){
    return topKey = $("#J_header").find(".main-nav li.cur").attr("data-key");
}

/**
 * @brief function prepareLineColumnAreaTableData
 * 处理线、柱、面积图共体数据
 * @param data
 *
 * @return
 */
window.prepareLineColumnAreaTableData = function(data){
    var rlt = [];
    $.each(data.data, function(m){
        var key = this.key ? this.key : [];
        if(key.length){
            $(this.data).each(function(k){
                var that = this;
                $(this.data).each(function(i, value){
                    rlt[i] = $.isArray(rlt[i]) ? rlt[i] : [];
                    rlt[i].push(that.index && that.index[i] ? that.index[i] : '-');
                    if(k == 0) rlt[i].push(key[i]);
                    rlt[i].push(value);
                    rlt[i].push(that.percentage && that.percentage[i] ? that.percentage[i] : '-');
                    rlt[i].push(that.specialper && that.specialper[i] ? that.specialper[i] : '-');
                });
            });
        }
    });
    return rlt;
};

/**
 * @brief function prepareTableData
 * for tm.module.js
 * 处理table显示数据格式
 * @param option
 *
 * @return
 */
window.prepareTableData = function(option){
    var data = option.data;
    if(option.hugeTable){
        var config = {}, averageData = [], sumData = [];
        if(option.theadAvg) config.avg = true;
        if(option.average) {
            config.average = option.average;
            averageData = data[0].average.data;
        }
        if(option.sum) {
            config.sum = option.sum;
            sumData = data[0].sum.data;
        }
        return tmtool.handleHugeTableData(data[0].data, config, averageData, sumData);
    } else {
        var rlt = [], average = [], sum = [], align = 0;
        $.each(data, function(m){
            var key = this.key ? this.key : [];
            if (m == 0) align = key.length;
            if(key.length){
                if(option.average) {
                    average.push(lang.t("均值"));
					if (this.average === undefined)
						average = average.concat(this.data);
					else
						average = average.concat(this.average.data);						
                }
                if(option.sum) {
                    sum.push(lang.t("合计"));
                    sum = sum.concat(this.sum.data);
                }
                $(this.data).each(function(k){
                    var that = this;
                    $(this.data).each(function(i, value){
                        rlt[i] = $.isArray(rlt[i]) ? rlt[i] : [];
                        if(k == 0) rlt[i].push(key[i]);
                        rlt[i].push(value);
                        if(option.contrast) {
                            if(m != 0)rlt[i].push(that.contrast_rate && that.contrast_rate[i] ? that.contrast_rate[i] : '-');
                        } else {
                            if(option.percentage) {
                                rlt[i].push(that.percentage && that.percentage[i] ? that.percentage[i] : '-');
                            }
                            if(option.specialper){
                                rlt[i].push(that.specialper && that.specialper[i] ? that.specialper[i] : '-');
                            }
                            if(option.qoq) {
                                rlt[i].push(that.qoq && that.qoq[i] ? that.qoq[i] : '-');
                            }
                            if(option.yoy) {
                                rlt[i].push(that.yoy && that.yoy[i] ? that.yoy[i] : '-');
                            }
                        }
                    });
                    if (m != 0 && option.contrast) {
                        var len = this.data.length, i;
                        for (i = len; i < align; i++) {
                            rlt[i] = $.isArray(rlt[i]) ? rlt[i] : [];
                            if(k == 0) rlt[i].push('-'); // date
                            rlt[i].push('-'); // value
                            rlt[i].push('-'); // rate
                        }
                    }
                });
            }
        });
        if(option.average) rlt.unshift(average);
        if(option.sum) rlt.unshift(sum);
        return rlt;
    }
};

/**
 * @brief function wrapDownload
 * for tm.module.js
 * 模块下载
 * @param d:模块参数
 *
 * @return
 */
window.wrapDownload = function(d) {
    var param = getPageParam("download"),
        toolsOption = d.getToolsOption();

    if(toolsOption.from && toolsOption.to) {
        param["from[1]"] = toolsOption.from;
        param["to[1]"] = toolsOption.to;
    }
    if(toolsOption["from[1]"] && toolsOption["to[1]"]) {
        param["from[1]"] = toolsOption["from[1]"];
        param["to[1]"] = toolsOption["to[1]"];
    }
    $.download(tmtool.getDownloadUrl(d.options.urlExtend, d.options.title), param);
};

/**
 * @brief showComment
 * 指标注释功能
 * @param d
 *
 * @return
 */
window.showComment = function(d) {
    var container = $(d.comment).find(".help-container");
    if(container.length) {
        container.fadeIn("normal");
    } else {
        container = $("<div>").addClass("help-container");
        var header = $("<div>").addClass("help-header").html('<span class="help-title">' + lang.t("数据指标说明") + '</span>'
															 // + '<a href="javascript:void(0);" class="help-close" title="' + lang.t("关闭") + '">x</a>'
															),
            lis = '<li class="help-li clearfix"><span class="help-li-title loading">&nbsp;</span></li>';
        container.append(header.add($("<div>").addClass("help-body").append($("<ul>").html(lis))));
        container.appendTo($(d.comment)).hide();
		container.fadeIn("fast");
        // header.find(".help-close").click(function(e){
        //     e.stopPropagation();
        //     $(this).closest(".help-container").hide();
        // });
        //ajax(getUrl("common", "comment", "getComponentComments"), {
		ajax("../../../../common/componentcomment/getComments?", {
            component_id: d.options.id,
            fetch_type: 1
        }, function(res) {
            if(res.result == 0) {
                lis = '';
                if(res.data && res.data.length) {
                    for(var i = 0; i < res.data.length; i++) {
                        lis += '<li class="help-li clearfix" comment_id=' + res.data[i].comment_id + '>'
                            + '<span class="help-li-title">' + res.data[i].keyword + '</span>'
                            + '<span class="help-li-content">' + res.data[i].comment + '</span></li>';
                    }
                } else {
                    lis += '<li class="help-li clearfix"><span class="help-li-title">' + lang.t("还没有指标注释哦") + '~</span></li>';
                }
                container.find(".help-body ul").html(lis);
            } else {
                say(lang.t("获取错误：") + res.err_desc);
            }
        });
    }
};

/**
 * @brief function : getTheadByName
 * 根据数据名称拼写table thead的值
 * for tm.module.js
 * @param data：Thead数据
 * @param o:Thead显示选项
 *
 * @return
 */
window.getTheadByName = function(data, o){
    var arr = [];
    if(data){
        $(data).each(function(){
            arr.push({ type: "date", title: lang.t("日期") });
            $(this.data).each(function(){
                arr.push({
                    type: "number",
                    title: this.name ? this.name : ""
                });
            });
        });
    }
    return arr;
};

window.getTheadByConfig = function (data, o) {
	var arr = [];
	if (data) {
        $(data).each(function(){
			var theadType = "string",
				theadTitle = "游戏";
            arr.push({ type: theadType, title: lang.t(theadTitle) });
            $(this.data).each(function(){
                arr.push({
                    type: "number",
                    title: this.name ? this.name : ""
                });
            });
        });
    }
	return arr;
};

/**
 * @brief function : getTheadByName
 * 根据数据名称拼写table thead的名称
 * for tm.module.js
 * @param data：Thead数据
 * @param o:Thead显示选项
 *
 * @return
 */
window.getTheadNameByData = function (data, o) {
    var arr = [], thead = o.theadConfig, type = ""; 
    if(data){
        $(data).each(function(index){
            if (thead[0]) {
                arr.push(thead[0]);
            } else {
                arr.push({ type: "date", title: lang.t("日期") }); 
            }   
            $(this.data).each(function(i){
                type = "number";
                if (thead[i+1]) {
                    type = thead[i+1].type;
                }   
                arr.push({
                    type: type,
                    title: this.name ? this.name : ""
                }); 
            }); 
        }); 
    }   
    return arr;
};

/**
 * @brief function getTheadByDate
 * 根据时间序列自动生成table thead
 * for tm.module.js
 * @param data
 * @param o
 *
 * @return
 */
window.getTheadByDate = function(data, o){
    if (data && data.length) {
        var config = {}, key = [];
        for(var i = data[0].key.length-1; i > -1; i--){
            key.push(data[0].key[i]);
        }
        if(o.theadAvg) config.avg = { title: lang.t("平均转化率") };
        if(o.average)  config.average = { title: lang.t("均值") };
        if(o.sum)  config.sum = { title: lang.t("合计") };
        return tmtool.getTheadByDate(key, config);
    } else {
        return [];
    }
};

/**
 * @brief _handle_show_time
 *
 * @param from "2014-05-20"
 * @param to "2014-05-21"
 *
 * @return
 */
function _handle_show_time( from, to ){
    var $timeTips = $("#J_timeTips"),
        $period = $timeTips.find('.period'),
        aFrom = from.split('-'),
        aTo = to.split('-'),
        showFrom = '',
        showTo = '',
        dateFrom = new Date( aFrom[0], (aFrom[1] - 1), aFrom[2]),
        dateTo = new Date( aTo[0],(aTo[1] - 1), aTo[2]),
        interval = (dateTo.getTime() - dateFrom.getTime()) / (24 * 60 * 60 * 1000);

    if ( interval < 0 ) {
        var tmp = '';
        aFrom = to.split('-');
        aTo = from.split('-');
        tmp = to;
        to = from;
        from = tmp;
    }

    if ( interval > 365 * 2 ) {
        var tmpD = new Date();

        tmpD.setTime(dateTo.getTime() - 90 * 24 * 60 * 60 * 1000);
        showFrom = tmpD.getFullYear() + '-' + (tmpD.getMonth() + 1) + '-' + tmpD.getDate() ;
        showTo = to;
        $period.text( showFrom + '~' + showTo );
        $timeTips.show();
    } else if (interval >= 0) {
        $timeTips.hide();
        showFrom = from;
        showTo = to;
        $period.text( showFrom + '~' + showTo );
    } else {
        $timeTips.hide();
    }

    $("#J_showFrom").val( showFrom );
    $("#J_showTo").val( showTo );
}
