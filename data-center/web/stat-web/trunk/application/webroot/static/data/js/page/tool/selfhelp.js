/**
 * @fileOverview 数据自助查询功能页面
 * @name selfservice.js
 * @author Maverick
 */
$(document).ready(function () {
    var tp = $('.transaction-panel');
    
    _gameInit();
	_createByNodeId(tp);
    _createTime(tp);
    _iconHover(tp);
	
    $('.transaction-add').hide();    // 默认隐藏添加按钮
	$('.transaction-submit').hide();    // 默认隐藏提交按钮
    $('.panel-close').hide();
});

var _iconHover = function () {    // css 外的一些 hover 效果
	$('.add-hov').hover(function () {    // Add transaction icon
		$(this).find('.icon').children().removeClass("icon-transadd").addClass("icon-transaddhov");
		$(this).find('.add-txt').css({ "color": "#FFFFFF" });
	}, function () {
		$(this).find('.icon').children().removeClass("icon-transaddhov").addClass("icon-transadd");
		$(this).find('.add-txt').css({ "color": "#7F7F7F" });	
	});
	
	$('.submit-hov').hover(function () {    // Submit transaction icon
		$(this).find('.icon').children().removeClass("icon-submit").addClass("icon-submithov");
		$(this).find('.submit-txt').css({ "color": "#FFFFFF" });
	}, function () {
		$(this).find('.icon').children().removeClass("icon-submithov").addClass("icon-submit");
		$(this).find('.submit-txt').css({ "color": "#7F7F7F" });
	});
	
	$('.time-add').hover(function () {    // Add time selector icon
		$(this).find('.icon').children().removeClass("icon-timeadd").addClass("icon-timeaddhov");
	}, function () {
        $(this).find('.icon').children().removeClass("icon-timeaddhov").addClass("icon-timeadd");
    });
};

var _gameInit = function () {
    var _gameInfo = function () {
	    var _createGameList = function () {
		    var games = $('#J_selGameList'),
			    selWrap = games.find('.sel-wrapper'),
			    selCon = $(document.createElement("div")).addClass("sel-con")
				    .css({ 'position' : 'relative' }),
			    selP = $(document.createElement("div")).addClass("sel-p"),
			    options = {
				    type: 1,
				    search: true,
				    data: [],
				    obj: selP,
				    callback: function () {    // 选择了游戏后的回调
						var nodeHtml = "<div class='node-level'>"
								+ "<div class='widget-sel' id='J_selNode'>"
								+ "<span class='sel-sstid'>" + lang.t("选择事件：") + "</span>"
								+ "<div class='sel-wrapper'></div>"
								+ "</div>"
								+ "</div>";
                        // 刷新游戏平台选择菜单（通过 ajax 请求确定菜单项目）
					    _createPlatform({
                            'game_id': $('#J_selGameList').find('.t-name').attr("data-id")
                        }, [ 'platform_id', 'gpzs_name', 'zone_id', 'server_id', 'gpzs_id' ]);
					    _createZoneServer();    // 刷新游戏区服选择菜单                    
                        // 把游戏 ID 写到页面上
					    $('.gameid-config').attr({
                            'data-id': $('#J_selGameList').find('.t-name').attr("data-id")
                        });
					    $('.transaction-add').show();    // 显示添加按钮
                        $('.transaction-panel').not(':first').remove();    // 移除多余的事务
						$('.transaction-panel').find('.node-level').remove();
						$('.transaction-panel').find('.report-level').remove();
						$('.transaction-panel').find('.data-level').remove();
						$(nodeHtml).appendTo($('.report-ins'));
						$('.transaction-submit').show();
                        // 刷新事件菜单
                        var tp = $('.transaction-panel');
						_createByNodeId(tp, {
							'game_id': parseInt($('.gameid-config').attr('data-id')),
							'parent_id': 0
						}, [ 'node_id', 'node_name', 'is_leaf' ]);
                        // _createTime(tp);
						$('#J_selPlatform').find('.opt-m').first().trigger("click");
				    }
			    };
            // 通过 ajax 请求确定菜单项目
		    var _getGameList = _selfhelpFactory("tool/selfhelp/getGamePermitList");
		    var gameList = _getGameList({}, [ 'game_id', 'game_name' ]);
		    for (var i = 0; i < gameList.length; i++)
			    options.data.push({ attr: { id: gameList[i].game_id }, title: gameList[i].game_name });
		    $.choose.core(options);
		    selP.appendTo(selCon.appendTo(selWrap));
	    };
	    var _createPlatform = function (param, get) {
		    $('#J_selPlatform').find('.sel-con').remove();
		    var platforms = $('#J_selPlatform'),
			    selWrap = platforms.find('.sel-wrapper'),
			    selCon = $(document.createElement("div")).addClass("sel-con")
				    .css({ 'position' : 'relative' }),
			    selP = $(document.createElement("div")).addClass("sel-p"),
			    options = {
				    type: 1,
				    search: true,
				    data: [],
				    obj: selP,
				    callback: function () {    // 选择了平台后的回调
                        // 刷新游戏区服选择菜单（通过 ajax 请求确定菜单项目）
					    _createZoneServer({
						    'game_id': $('#J_selGameList').find('.t-name').attr("data-id"),
						    'platform_id': $('#J_selPlatform').find('.t-name').attr("data-id")
					    }, [ 'gpzs_id', 'gpzs_name', 'zone_id', 'server_id', 'platform_id' ]);
						$('#J_selZoneServer').find('.opt-m').first().trigger("click");
				    }
			    };
            // 通过 ajax 请求确定菜单项目
		    var _getPlatform = _selfhelpFactory("common/gpzs/getPlatform");
		    if (param !== undefined && get !== undefined) {
			    var platformList = _getPlatform(param, get);
			    for(var i = 0; i < platformList.length; i++)
				    options.data.push({ attr: { id: platformList[i].platform_id },
									    title: platformList[i].gpzs_name });
			    $.choose.core(options);
		    } else {
			    $.choose.core(options);
		    }
		    selP.appendTo(selCon.appendTo(selWrap));
	    };
	    var _createZoneServer = function (param, get) {
		    $('#J_selZoneServer').find('.sel-con').remove();
		    var _getZoneServer = _selfhelpFactory("common/gpzs/getZoneServer");
		    var zoneservers = $('#J_selZoneServer'),
			    selWrap = zoneservers.find('.sel-wrapper'),
			    selCon = $(document.createElement("div")).addClass("sel-con")
				    .css({ 'position' : 'relative' }),
			    selP = $(document.createElement("div")).addClass("sel-p"),
			    options = {
				    type: 1,
				    search: true,
				    data: [],
				    obj: selP
			    };
		    var _handle_choose = function (data, pre) {
			    var rlt = [],
				    preName = pre;
			    if(preName == "platform") preName = "gpzs";
			    if(data && data.length){
				    $.each(data,function(){
					    var title = (pre == "gpzs" && this.zone_id == -1 && this.server_id == -1)
							    ? lang.t("全区全服") : this[preName + "_name"];
					    rlt.push({
						    title : title,
						    attr : { id : this[pre + "_id"] }
					    });
				    });
			    }
			    return rlt;
		    };
		    if (param !== undefined && get !== undefined) {
                // 通过 ajax 请求确定菜单项目
			    var zoneServList = _getZoneServer(param, get);
			    zoneServList = _handle_choose(zoneServList, "gpzs");
			    options.data = zoneServList;
			    $.choose.core(options);
		    } else {
			    $.choose.core(options);
		    }
		    selP.appendTo(selCon.appendTo(selWrap));
	    };
	    _createGameList();
	    _createPlatform();
	    _createZoneServer();
    };
    var _rulesInfo = function () {
	    var rules = $('#J_selRules'),
		    selWrap = rules.find('.sel-wrapper'),
		    selCon = $(document.createElement("div")).addClass("sel-con")
			    .css({ 'position' : 'relative', 'z-index': 2 }),
		    selP = $(document.createElement("div")).addClass("sel-p"),
		    opRules = {
			    type: 1,
			    search: true,
			    data: [ { attr: { id: 5 }, title: lang.t("并集运算") },
					    { attr: { id: 5 }, title: lang.t("交集运算") },
					    { attr: { id: 3 }, title: lang.t("差集运算") } ],
			    obj: selP,
			    callback: function () {    // 选择了预算后的回调
                    // 把运算规则允许的事务数量写到页面上
				    $('.rules-config').attr({'data-id': $('#J_selRules').find('.t-name').attr('data-id')});
                    _checkTrsn();
			    }
		    };
	    $.choose.core(opRules);
	    selP.appendTo(selCon.appendTo(selWrap));
    };
    
	_gameInfo();    // 游戏信息
	_rulesInfo();    // 运算信息
};
var _createStid = function (t, param, response) {
    t.find('.stid-level').find('.sel-con').remove();
    var stid = t.find('.stid-level').find('.widget-sel'),
        selWrap = stid.find('.sel-wrapper'),
        selCon = $(document.createElement("div")).addClass("sel-con")
			.css({ 'position' : 'relative' }),
        selP = $(document.createElement("div")).addClass("sel-p"),
        options = {
			type: 1,
			search: true,
			data: [],
			obj: selP,
            callback: function () {
                _createSstid(t, {
                    'game_id': parseInt($('.gameid-config').attr('data-id')),
                    'parent_id': t.find('.stid-level').find('.t-name').attr('data-id')
                }, [ 'node_id', 'node_name', 'is_leaf' ]);
                _createReportid(t);
                _createDataid(t);
            }
		};
    // var _getStidAll = _selfhelpFactory("common/report/getStidAll");
	var _getStidAll = _selfhelpFactory("gamecustom/tree/getTree");
    if (param !== undefined && response !== undefined) {
        var firList = _getStidAll(param, response);
        for(var i = 0; i < firList.length; i++)
            options.data.push({ attr: { id: firList[i].node_id },
                                title: firList[i].node_name });
        $.choose.core(options);
    } else {
        $.choose.core(options);
    }
    selP.appendTo(selCon.appendTo(selWrap));
};
var _createSstid = function (t, param, response) {
    t.find('.sstid-level').find('.sel-con').remove();
    var sstid = t.find('.sstid-level').find('.widget-sel'),
        selWrap = sstid.find('.sel-wrapper'),
        selCon = $(document.createElement("div")).addClass("sel-con")
			.css({ 'position' : 'relative' }),
        selP = $(document.createElement("div")).addClass("sel-p"),
        options = {
			type: 1,
			search: true,
			data: [],
			obj: selP,
            callback: function () {
                _createReportid(t, {
                    'game_id': parseInt($('.gameid-config').attr('data-id')),
                    'stid': t.find('.stid-level').find('.t-name').attr('data-id'),
                    'sstid': t.find('.sstid-level').find('.t-name').attr('data-id')
					// 'node_id': t.find('.sstid-level').find('.t-name').attr('data-id')
                }, [ 'r_id',
					 'r_name',
					 'is_multi',
					 'type' ]);
                _createDataid(t);
            }
		};
    var _getSstidByStid = _selfhelpFactory("gamecustom/tree/getTree");
    if (param !== undefined && response != undefined) {
        var secList = _getSstidByStid(param, response);
        for(var i = 0; i < secList.length; i++)
            options.data.push({ attr: { id: secList[i].node_id },
                                title: secList[i].node_name });
        $.choose.core(options);
    } else {
        $.choose.core(options);
    }
    selP.appendTo(selCon.appendTo(selWrap));
};
var _createByNodeId = function (t, param, response) {
	t.find('.node-level').last().find('.sel-con').remove();
	var node = t.find('.node-level').last().find('.widget-sel'),
		selWrap = node.find('.sel-wrapper'),
		selCon = $(document.createElement("div")).addClass("sel-con").css({ 'position' : 'relative' }),
		selP = $(document.createElement("div")).addClass("sel-p"),
		nodeHtml = "<div class='node-level'>"
			       + "<div class='widget-sel' id='J_selNode'>"
			         + "<span class='sel-sstid'>" + lang.t("选择事件：") + "</span>"
			         + "<div class='sel-wrapper'></div>"
			       + "</div>"
			     + "</div>",
		options = {
			type: 1,
			search: true,
			data: [],
			obj: selP,
			callback: function () {
				var clicked = $(this.obj),
					selCon = clicked.parent(),
					selWrapper = selCon.parent(),
					widgetSel = selWrapper.parent(),
					nodeLevel = widgetSel.parent();
				nodeLevel.next().remove();
				t.find('.report-level').remove();
				t.find('.data-level').remove();
				var datainfo = clicked.find('.t-name').attr('data-id').split(':'),
					isLeaf = datainfo[1];
				if ( isLeaf === "1" ) {
					_createReportid(t, {
						'game_id': parseInt($('.gameid-config').attr('data-id')),
						'node_id': t.find('.node-level').last().find('.t-name').attr('data-id').split(':')[0]
					}, [ 'r_id',
						 'r_name',
						 'is_multi',
						 'type' ]);
				} else {
					var hook = t.find('.node-level').last();
					$(nodeHtml).appendTo(t.find('.report-ins'));    // insert new node select
					_createByNodeId(t, {
						'game_id': parseInt($('.gameid-config').attr('data-id')),
						'parent_id': hook.find('.t-name').attr('data-id').split(':')[0]
					}, [ 'node_id', 'node_name', 'is_leaf' ]);
				}
			}
		};
	var _getByNodeId = _selfhelpFactory("gamecustom/tree/getTree");
	if (param !== undefined && response !== undefined) {
		try {
			var nodeList = _getByNodeId(param, response);
			for(var i = 0; i < nodeList.length; i++) {
				options.data.push({ attr: { id: nodeList[i].node_id + ":" + nodeList[i].is_leaf },
									title: nodeList[i].node_name});
			}
			$.choose.core(options);
		} catch (error) {    // void node
			t.find('.node-level').last().remove();
			t.find('.node-level').last().hint();
		}
	} else {
		$.choose.core(options);
	}
	selP.appendTo(selCon.appendTo(selWrap));
};
var _createReportid = function (t, param, response) {
    t.find('.report-level').find('.sel-con').remove();
	var reportHtml = "<div class='report-level'>"
			         + "<div class='widget-sel' id='J_selReportid'>"
			           + "<span class='sel-reportid'>" + lang.t("选择人数信息：") + "</span>"
			           + "<div class='sel-wrapper'></div>"
			         + "</div>"
			       + "</div>";
	$(reportHtml).appendTo(t.find('.report-ins'));
    var reportid = t.find('.report-level').find('.widget-sel'),
        selWrap = reportid.find('.sel-wrapper'),
        selCon = $(document.createElement("div")).addClass("sel-con").css({ 'position' : 'relative' }),
        selP = $(document.createElement("div")).addClass("sel-p"),
        options = {
			type: 1,
			search: true,
			data: [],
			obj: selP,
            callback: function () {
                var datainfo = t.find('.report-level').find('.t-name').attr('data-id').split(":");
				t.find('.data-level').remove();
                if (datainfo[2] === "1")
                    t.find('.report-conf').attr({ 'data-id': 'report' });
                else
                    t.find('.report-conf').attr({ 'data-id': 'result' });
                t.find('.rId-conf').attr({ 'data-id': parseInt(datainfo[0]) });
                if (parseInt(datainfo[1]) === 0) {
                    t.find('#J_selDataid').fadeOut();
                } else {
                    t.find('#J_selDataid').fadeIn();
                    _createDataid(t, {
                        'type': "report",
                        'r_id': parseInt(datainfo[0])
                    }, [ 'data_name',
                         'data_id' ]);
                }
            }
		};
	var _getListByNodeId = _selfhelpFactory("common/report/getListByNodeid");
	// $(reportHtml).appendTo(t.find('.report-ins'));
    if (param !== undefined && response !== undefined) {
        var thdList = _getListByNodeId(param, response);
        for(var i = 0; i < thdList.length; i++) {
            options.data.push({ attr: { id: thdList[i].r_id
                                        + ":" + thdList[i].is_multi
                                        + ":" + thdList[i].type },
                                title: thdList[i].r_name });
        }
		if (options.data.length === 0) {
			t.find('.report-level').remove();
			t.find('.node-level').last().hint();
		}
        $.choose.core(options);
    } else {
        $.choose.core(options);
    }
    selP.appendTo(selCon.appendTo(selWrap));
};
var _createDataid = function (t, param, response) {
    t.find('.data-level').find('.sel-con').remove();
	var dataHtml = "<div class='data-level'>"
			       + "<div class='widget-sel' id='J_selDataid'>"
			         + "<span class='sel-dataid'>" + lang.t("选择人数信息：") + "</span>"
			         + "<div class='sel-wrapper'></div>"
			       + "</div>"
			     + "</div>";
	$(dataHtml).appendTo(t.find('.report-ins'));
    var dataid = t.find('.data-level').find('.widget-sel'),
        selWrap = dataid.find('.sel-wrapper'),
        selCon = $(document.createElement("div")).addClass("sel-con").css({ 'position' : 'relative' }),
        selP = $(document.createElement("div")).addClass("sel-p"),
        options = {
			type: 1,
			search: true,
			data: [],
			obj: selP,
            callback: function () {
                t.find('.dataId-conf').attr({ 'data-id': t.find('.data-level')
                                              .find('.t-name')
                                              .attr("data-id") });
            }
		};
    var _getListByRid = _selfhelpFactory("common/datainfo/getListByRid");
    if (param !== undefined && response !== undefined) {
        var fouList = _getListByRid(param, response);
        for(var i = 0; i < fouList.length; i++)
            options.data.push({ attr: { id: fouList[i].data_id },
                                title: fouList[i].data_name });
		if (options.data.length === 0) {
			t.find('.data-level').remove();
			t.find('.report-level').hint();
		}
        $.choose.core(options);
    } else {
        $.choose.core(options);
    }
    selP.appendTo(selCon.appendTo(selWrap));
};
