(function(window, undefined) {
var DELDIALOG, THRESHOLDDIALOG;
$(function(){
    _datalist();
	// Report tag and click to choose tag
	var weekHtml   = '<a href="javascript: void(0);" class="report-tag weekly-report report-selected" report-type="0">日报</a>',
		monthHtml  = '<a href="javascript: void(0);" class="report-tag weekly-report report-selected" report-type="1">月报</a>',
		wechatHtml = '<a href="javascript: void(0);" class="report-tag wechat-report report-selected" report-type="2">微信</a>';
	
	if ($('#J_gEmailFreqId').val() == "DAILY")
		$(weekHtml + wechatHtml).prependTo($('.main-sub-nav .fr'));
	else if ($('#J_gEmailFreqId').val() == "MONTHLY") 
		$(monthHtml).prependTo($('.main-sub-nav .fr'));
	
	$('.report-tag').click(function () {
		$(this).toggleClass('report-selected');
	});
	
    // Send
    $(".email-send").click(function() {
		var selected = $('.report-selected'),
			is_test = $(this).data("test"),
			emailId =  $("#J_gEmailId").val();

		if (selected.length) {
			$.Dlg.Util.confirm(lang.t("是否发送？"), lang.t("点击确定按钮开始发送..."), function() {
				var sendLog = {
					week: {
						stat: true,
						okMsg: lang.t("发送成功"),
						failMsg: lang.t("日报发送失败"),
						errDesc: ""
					},
					month: {
						stat: true,
						okMsg: lang.t("发送成功"),
						failMsg: lang.t("月报发送失败"),
						errDesc: ""
					},
					wechat: {
						stat: true,
						okMsg: lang.t("发送成功"),
						failMsg: lang.t("微信发送失败"),
						errDesc: ""
					}
				};
				
				overlayer({ text: lang.t("发送中...")});
				
				selected.each(function () {
					switch($(this).attr("report-type")) {
					case "0":
						ajax("../../../admin/email/send", {
							email_id : emailId,
							is_test : is_test
						}, function(res) {
							if(res.result == 0) {
								$("#J_emailTips").find(".last-send-time")
									.addClass("sent").text(res.data.last_send_time);
								$("#J_emailTips").find(".last-send-user")
									.addClass("sent").text(res.data.last_send_user);
								$("#J_emailTips").find(".receviers")
									.text(res.data.receviers);
								$("#J_emailTips").find(".test-receiver")
									.text(res.data.test_receiver);
							} else {
								sendLog.week.stat = false;
								sendLog.week.errDesc = res.err_desc;
							}
						}, "GET", true, 300 * 1000);						
						break;
					case "1":
						ajax("../../../admin/email/send", {
							email_id : emailId,
							is_test : is_test
						}, function(res) {
							if(res.result == 0) {
								$("#J_emailTips").find(".last-send-time").addClass("sent").text(res.data.last_send_time);
								$("#J_emailTips").find(".last-send-user").addClass("sent").text(res.data.last_send_user);
								$("#J_emailTips").find(".receviers").text(res.data.receviers);
								$("#J_emailTips").find(".test-receiver").text(res.data.test_receiver);
							} else {
								sendLog.week.stat = false;
								sendLog.week.errDesc = res.err_desc;
							}
						}, "GET", true, 300 * 1000);
						break;
					case "2":
						ajax("../../../admin/email/sendWeixin", {
							email_id : emailId,
							is_test : is_test
						}, function(res) {
							if (res.result != 0) {
								sendLog.wechat.stat = false;
								sendLog.wechat.errDesc = res.err_desc;
							}
						}, "GET", true, 300 * 1000);						
						break;
					};
				});
				
				if (sendLog.week.stat === false || sendLog.month.stat === false || sendLog.wechat.stat === false) {
					hidelayer(lang.t("发送失败"));
					
					var errMsg = "",
						newline = "</br>";
					for (var prop in sendLog) {
						var ins = sendLog[prop];
						if (ins.stat === false) {
							errMsg = errMsg + ins.failMsg + ": " + ins.errDesc + newline;
						}
					}

					$.Dlg.Util.message(lang.t("发送失败"),
									   errMsg,
									   lang.t("关闭")).show();
				} else {
					hidelayer(lang.t("发送成功！"));
					$.Dlg.Util.message(lang.t("发送成功"), lang.t("发送成功~.~"), lang.t("关闭")).show();
				}
			});			
		} else {
			say('请至少选中一个发送项');
		}
    });
});

/**
 * @brief
 * 加载页面中的指标列表
 * @return
 */
function _datalist(){
    var datalist = $("#J_datalist");
    datalist.find("tbody tr").each(function(){
        var t = $(this),
            period = _get_period(t.attr("data-period")),
            param = _get_param(t),
            offset = parseInt(t.attr("data-offset"));;
            //gameId = $("#J_gGameId").val();;
        if( period == 4 ){
            t.find("td:eq(1)").css({ "text-align" : "left" }).attr("colspan", 4)
            .find("span").removeClass("row-loading")
            .text(lang.t("点击【{1}】可查看今日，昨日，上周同期分钟数据详情。", t.attr("data-name")));
            t.append($('<td class="td hd w40"><a href="javascript: void(0);" class="del simple-btn">' + lang.t("删除") + '</a></td>'
                     + '<td class="td hd"><a href="javascript: void(0);" class="rename simple-btn mr10">' + lang.t("重命名") + '</a></td>'
                     + '<td class="td hd w70">&nbsp;</td>'));
            _bindRenameEvent(t);
            _bindDelete(t);
            _bindThreshold(t);
        } else if( period == 3) {
            var now = _get_time(-28),
                monthly = _get_time(-56),
                yearly = _get_time(-393);
            //ajaxData(getUrl("common", "data", "getTimeSeries"), $.extend({
            ajaxData("../../../common/data/getTimeSeries", $.extend({
            	//game_id: gameId,
                constrast: 0,
                by_item: 0,
                yoy: 1,
                qoq: 1,
                period: 3,
                "from[0]": monthly,
                "to[0]": now,
                "from[1]": yearly,
                "to[1]": yearly,
            }, param), function(data){
                _datalist_fac_month(data, t);
                _bindRenameEvent(t);
                _bindDelete(t);
                _bindThreshold(t);
            });
        } else{
            var now = _get_time(0, offset),
                yesterday = _get_time(-1, offset),
                weekly = _get_time(-7, offset),
                monthly = _get_time(-28, offset);
            //ajaxData(getUrl("common", "data", "getTimeSeries"), $.extend({
            ajaxData("../../../common/data/getTimeSeries", $.extend({
            	//game_id: gameId,
            	period: period,
                contrast: 1,
                by_item: 0,
                "from[0]": now,
                "to[0]": now,
                "from[1]": yesterday,
                "to[1]": yesterday,
                "from[2]": weekly,
                "to[2]": weekly,
                "from[3]": monthly,
                "to[3]": monthly
            }, param), function(data){
                _datalist_fac(data, t);
                _bindRenameEvent(t);
                _bindDelete(t);
                _bindThreshold(t);
            });
        }
        t.find("td:eq(0)").click(function(e){
            e.stopPropagation();
            if(t.hasClass("cur")){
                t.removeClass("cur").next().remove();
            } else {
                var tr = $(document.createElement("tr")),
                    td = $(document.createElement("td")).addClass("td hd").attr({ colspan : 8 }),
                    period = _get_period(t.attr("data-period"));
                t.addClass("cur").after(tr.append(td));
                if( period == 4 ){
                    param = $.extend(param, {
                        "from[0]": _get_time(0, offset),
                        "to[0]": _get_time(0, offset),
                        "from[1]": _get_time(-1, offset),
                        "to[1]": _get_time(-1, offset),
                        "from[2]": _get_time(-7, offset),
                        "to[2]": _get_time(-7, offset),
                        by_item: 0,
                        period: period,
                        //game_id:gameId
                    });
                } else {
                    param = $.extend(param, {
                        from: _get_time(-30, offset),
                        to: _get_time(0, offset),
                        by_item: 0,
                        period: period,
                        //game_id:gameId
                    });
                }
                configure(td, t.find("td:eq(0)").text(), param);
            }
        });
    });
}
function _bindDelete(t) {
    t.find(".del").click(function() {
        var _this = $(this);
        if(DELDIALOG) {
            $(DELDIALOG.getMask()).remove();
            $(DELDIALOG.getContainer()).remove();
        }
        DELDIALOG = $.Dlg.Util.popup({
            id : "J_delData",
            title: lang.t("删除数据"),
            contentHtml: lang.t("确定要永久删除吗？"),
            save: function(con) {
                ajaxData(getUrl("admin", "email", "deleteData"), {
                    email_data_id: _this.closest("tr").attr("data-id")
                }, function(){
                    t.closest("tr").remove();
                }, "POST");
            }
        });
        DELDIALOG.show();
    });
}
function _bindThreshold(t) {
    t.find(".threshold").click(function() {
        var _this = $(this),
            threshold = $.parseJSON(t.attr("data-threshold")),
            items = [{items: [{type: "hidden", name: "email_data_id", value: t.attr("data-id")}]}],
            ws = {
                Mon: "周一",
                Tue: "周二",
                Wed: "周三",
                Thu: "周四",
                Fri: "周五",
                Sat: "周六",
                Sun: "周日"
            }, w, v;
		// null 打钩
		// 没有属性 打钩
		// 有属性为0 不打钩
		// 有属性为1 不打钩
		var ifcheck = threshold === null ? 'checked="checked"' : threshold.hasOwnProperty('null_not_check') ? (threshold.null_not_check === "1" ? "" : 'checked="checked"') : 'checked="checked"';
        for (w in ws) {
            v = threshold && threshold["qoq"] && threshold["qoq"][w] || {};
            items.push({
                label: {
                    title: ws[w],
                    className: "title-inline"
                },
                items: [{
                    label: {
                        title: "min",
                        className: "mr5"
                    },
                    type: "text",
                    name: "threshold[qoq][" + w + "][min]",
                    value: typeof v.min !== "undefined" ? v.min : "",
                    className: "ipttxt w120"
                }, {
                    label: {
                        title: "max",
                        className: "ml10 mr5"
                    },
                    type: "text",
                    name: "threshold[qoq][" + w + "][max]",
                    value: typeof v.max !== "undefined" ? v.max : "",
                    className: "ipttxt w120"
                }]
            });
        }

        if (THRESHOLDDIALOG) {
            $(THRESHOLDDIALOG.getMask()).remove();
            $(THRESHOLDDIALOG.getContainer()).remove();
        }
		// Dialog Generate
        THRESHOLDDIALOG = $.Dlg.Util.popup({
            id: "J_setThresholdDlg",
            title: "设置环比阈值 - " + t.find(".item").text(),
            contentHtml: $("<form>").append($('<label class="lable-inline">'
											  + '<input type="checkbox" ' + ifcheck + ' class="null-not-check"></input>'
											  + '<span class="check-inline">阈值检查</span>'
											  + '</label>'))
				.append((new tm.form.fieldSet({items: 
s})).getElement()),
            save: function(con) {
				var param = $("#J_setThresholdDlg").find("form").serializeArray();
				param.push({ name: "threshold[null_not_check]", value: Math.abs($('.null-not-check:checked').length - 1) });
                ajax(getUrl("admin", "email", "setThreshold"),
					 param,
					 function(res) {
						 if (res.result === 0) {
							 var title = $("<div>").html(_build_threshold_title(res.data)).text();
							 _this.attr("title", title).closest("tr").attr("data-threshold", res.data);
							 say('设置成功！', true);
						 } else {
							 say("设置阈值错误：" + res.err_desc);
						 }
					 }, "POST");
            }
        });
        THRESHOLDDIALOG.show();
    });
}

function _bindRenameEvent(t) {
    var _renameEvent = function(o) {
        var oldName = o.attr("data-name"),
            newName = o.val();
        if(newName) {
            if(oldName != newName) {
                overlayer();
                ajax(getUrl("admin", "email", "renameData"), {
                    email_data_id : o.closest("tr").attr("data-id"),
                    data_name : newName
                }, function(res) {
                    hidelayer();
                    if(res.result == 0) {
                        location.reload();
                    } else {
                        say(lang.t("获取数据错误：") + res.err_desc);
                        t.val(oldName);
                    }
                    t.blur();
                });
            }
        } else {
            o.val(oldName);
        }
    };
    t.find(".rename").click(function() {
        var _this = $(this),
            tr = _this.closest("tr"),
            rename = _this.parent().find(".mod-rename");
        if(!rename.length) {
            rename = $('<input type="input" class="mod-rename" />').appendTo(_this.parent());
            rename.blur(function() {
                _renameEvent($(this));
            }).keydown(function(e) {
                if(e.keyCode  == 13) _renameEvent($(this));
            });
        }
        if(_this.hasClass("cur")) {
            _this.removeClass("cur");
            rename.hide();
        } else {
            _this.addClass("cur");
            rename.attr({
                "value": tr.attr("data-name"),
                "data-name": tr.attr("data-name")
            }).show();
        }
    });
}
/**
 * @brief configure
 *
 * @param con
 * @param title: the title of mod-box
 *
 * @return
 */
function configure(con, title, param){
	//console.log("?????" + param);
    var configure = [];
    configure.push({
        type: "wrap",
        container: con,
        title: param.period == 4 ? lang.t("【{1}】分钟数据详情", title) : lang.t("【{1}】近一月详情", title),
        headEnabled: false,
        bottomEnabled: false,
        child: [{
            type: "data",
            url: {
                //extend: getUrl("common", "data", "getTimeSeries"),
            	//extend: getUrl("../../../common/data/getTimeSeries"),
            	extend: "../../../common/data/getTimeSeries?",
                page: function() {
                    return param;
                }
            },
            afterLoad: function(data, container) {
                fac([{
                    type: "graph",
                    container: container,
                    columnStack: "",
                    chartType: "line",
                    chartStock: true,
                    timeDimension: param.period != 4 ? "day" : "min",
                    data: data
                }]);
            }
        }]
    });
    fac(configure);
};

/**
 * @brief _get_period
 * 根据date_type获取period
 * @param period : "MINUTE", "DAY", "WEEK", "MONTH"
 *
 * @return
 */
function _get_period(period){
    return period == "MINUTE" ? 4
        : (period == "WEEK" ? 2
                : (period == "MONTH" ? 3 : 1))
}
/**
 * @brief _get_param
 * 根据ids|exprs，生成ids
 * @param idExpr:"A,B|{0}/{1}"
 * @return
 * {
 *     ids : "Ａ,B",
 *     exprs[0][name] : name,
 *     exprs[0][expr] : {0}/{1},
 *     gpzs_id : gpzs
 * }
 */
function _get_param(t){
    var aIdExpr = (t.attr("data-expr")).split("|"),//("A,B|{0}/{1}").split("|");
        ids = aIdExpr[0].split(","),
        server_id= t.attr("data-server"),
        param = {
            "expres[0][expre]": aIdExpr[1],
            "expres[0][data_name]": t.attr("data-name")
        }, tmpId;
    for (var i = 0; i < ids.length; i ++) {
        tmpId = ids[i].split('_');
        if (tmpId.length == 2) {
            param["data_info[" + i + "][data_id]"] = tmpId[0];
            param["data_info[" + i + "][server_id]"] = tmpId[1];
        } else {
            param["data_info[" + i + "][data_id]"] = ids[i];
            param["data_info[" + i + "][server_id]"] = server_id;
        }
        if (aIdExpr[1] != "{0}") {
            param["data_info[" + i + "][precision]"] = 4;
        }
        param["data_info[" + i + "][period]"] = _get_period(t.attr("data-period"));
    }
    if (aIdExpr[1] != "{0}") {
        param["expres[0][precision]"] = 2;
    }
    return param;
}
/**
 * @brief _datalist_fac
 *
 * @param data: 每一指标的详细数据
 * @param tr: 行
 *
 * @return
 */
function _datalist_fac(data, tr){
    var html = '',
        offset = parseInt(tr.attr("data-offset"));
        rate = data[1].data[0].contrast_rate[0];
    html += _get_td(lang.t("今日："), data[0].data[0].data[0], null, true)
        + _get_td(lang.t("较昨日（{1}）：", _get_time(-1, offset)), data[1].data[0].data[0], data[1].data[0].contrast_rate[0], null, "w140")
        + _get_td(lang.t("较上周同期（{1}）：", _get_time(-7, offset)), data[2].data[0].data[0], data[2].data[0].contrast_rate[0], null, "w160")
        + _get_td(lang.t("较上月同期（{1}）：", _get_time(-28, offset)), data[3].data[0].data[0], data[3].data[0].contrast_rate[0], null, "w160")
        + '<td class="td hd w40"><a href="javascript: void(0);" class="del simple-btn">' + lang.t("删除") + '</a></td>'
        + '<td class="td hd"><a href="javascript: void(0);" class="rename simple-btn">' + lang.t("重命名") + '</a></td>'
        + '<td class="td hd w80"><a href="javascript: void(0);" class="threshold simple-btn mr10" title="' + _build_threshold_title(tr.attr("data-threshold")) + '">' + lang.t("设置阈值") + '</a></td>';
    tr.find("td:eq(1)").remove();
    tr.append($(html));
    rate && (rate = rate.split("%")[0]);
    if(rate > 20 || rate < -20 ){
        tr.find("td:eq(0) a").before(_get_empty('20%'));
    }
}

/**
 * @brief _datalist_fac_month
 *
 * @param $data: 每一指标的详细数据
 * @param tr: 行
 *
 * @return
 */
function _datalist_fac_month(data, tr){
    var html = '';
        qoqRate = data[0].data[0].qoq[1],
        yoyRate = data[0].data[0].yoy[1];
    html += _get_td(lang.t("上月({1}):", _get_time(-28, 0, false)), data[0].data[0].data[1], null, null, "w160")
         + _get_td(lang.t("较上月同期({1}):", _get_time(-56, 0, false)), data[0].data[0].data[0], qoqRate, null, "w160")
         + _get_td(lang.t("较去年同期({1}):", _get_time(-393, 0, false)), data[1].data[0].data[0], yoyRate, null, "w160")
         + '<td class="td hd w40"><a href="javascript: void(0);" class="del simple-btn">' + lang.t("删除") + '</a></td>'
         + '<td class="td hd"><a href="javascript: void(0);" class="rename simple-btn">' + lang.t("重命名") + '</a></td>'
         + '<td class="td hd w80"><a href="javascript: void(0);" class="threshold simple-btn mr10" title="' + _build_threshold_title(tr.attr("data-threshold")) + '">' + lang.t("设置阈值") + '</a></td>';
    tr.find("td:eq(1)").remove();
    tr.append($(html));
    qoqRate && (qoqRate = qoqRate.split("%")[0]);
    yoyRate && (yoyRate = yoyRate.split("%")[0]);
    if(qoqRate > 30 || qoqRate < -30 ){ 
        tr.find("td:eq(0) a").before(_get_empty('30%'));
    } else if(yoyRate > 30 || yoyRate < -30 ){
        tr.find("td:eq(0) a").before(_get_empty('30%'));
    }
}

function _build_threshold_title(data) {
    var threshold = $.parseJSON(data),
        title = "环比",
        ws = {
            Mon: "周一",
            Tue: "周二",
            Wed: "周三",
            Thu: "周四",
            Fri: "周五",
            Sat: "周六",
            Sun: "周日"
        }, w, v;
    for (w in ws) {
        v = threshold && threshold["qoq"] && threshold["qoq"][w] || {};
        if (typeof v.min !== "undefined" && typeof v.max !== "undefined") {
            title += "&#13;" + ws[w] + "：" + v.min + " ~ " + v.max;
        }
    }
    return title;
}
function _get_empty(num){
    return $(document.createElement("span"))
        .addClass("empty")
        .attr("title", lang.t("注意：") + lang.t("昨日数据超过{1}", num))
        .text("");
}
/**
 * @brief _get_td
 *
 * @param text: title
 * @param num: the number of title
 * @param rate: the rate of title(like qoq, yoy)
 * @param now: whether need addClass "now" or not
 *
 * @return
 */
function _get_td(text, num, rate, now, className){
    rate && (rate = rate.split("%")[0]);
    className = className ? className : "";
    return '<td class="td hd' + (now ? " now" : "") + '">'
        +   '<span class="item-title ' + className + '">' + text + '</span><br />'
        +   '<span class="item-txt">' + ( num ? num : '--' )
        +   (rate || rate == 0
            ? (rate >= 0
                ? ' <strong class="up">+' + rate + '%</span>'
                : ' <strong class="down">' + rate + '%</span>')
            : '')
        +   '</span>';
}

/**
 * @brief _get_time
 *
 * @return "2013-12-26" || "2013-12"
 */
function _get_time(ndays, offset, isDaily){
    ndays = !isNaN(ndays) ? ndays : 0;
    ndays = ndays - 1 - (!isNaN(offset) ? offset : 0);
    var now = new Date();
    /*now.setDate(now.getDate()+ndays);*/
    now.setMonth(now.getMonth()-1, now.getDate()+ndays)
    if(isDaily === undefined) {
        return now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
    } else {
        return now.getFullYear() + "-" + (now.getMonth() + 1);
    }
}
/**
 * @brief ajaxData
 *
 * @param url
 * @param param
 * @param fn:回调函数
 * @param hide:是否显示overlayer
 * @param empt:发生请求错误时，是否say
 */
function ajaxData( url, param, fn, hide, empt ){
    if( hide ) overlayer({ text: lang.t("加载中...")});
    ajax( url, param, function(res){
        if(res.result == 0){
            if(hide) hidelayer(lang.t("加载成功~.~"));
            if(fn) fn(res.data);
        } else {
            if(hide) hidelayer();
            if(empt){
                if(fn)fn([]);
            } else {
                say(lang.t("获取数据错误：") + res.err_desc);
            }
        }
    }, "POST");
}
})(window);
