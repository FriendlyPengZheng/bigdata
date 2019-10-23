/**
 * @fileOverview 我的收藏窗口部件基礎部分
 * @name widget.js
 * @author Maverick youngleemails@gamil.com
 */

/**
 * 生成每个小窗口，包括内容区
 * @param {} t
 * @param {} con
 * @param {} shared 是否属于分享的模块
 * @returns {} 
 */
var _widget_fac = function(t, con, shared) {
	var configure = [], tmp;
	if (t.draw_type == 3) {    // 是表格
        tmp = {
			type: "wrap",
			container: con,
			draggable: true,
			title: t.collect_name,
			headEnabled: false,
			bottomEnabled: t.metadata_cnt > 10 ? false : true,    // 大于十条数据则不显示 graph
            afterCreate: function (container) {
				var conInfo = container.closest('.mod-box').attr('calc-type');
				if (conInfo) {
					conInfo = conInfo.split("|");
					for (var i = 0; i < conInfo.length; i++) {
						container.closest('.mod-box')
							.find('input')
							.filter("." + conInfo[i])
							.attr({ checked: "checked" });
					}					
				}
				container.find('.calc-box input').change(function () {
					var	sendInfo = "", url;
					container.find('.calc-box input').each(function () {
						if ($(this).attr('checked') == "checked")
							sendInfo += $(this).attr('title') + "|";
					});
					t.calc_option = sendInfo.substr(0, sendInfo.length-1);
					container.attr({'calc-type': sendInfo.substr(0, sendInfo.length-1)});
                    if (window.pageConfigure.shared) {
                        url = getUrl('home', 'collect', 'setCalcOptionForSharedFavor');
                    } else {
                        url = getUrl('home', 'collect', 'setCalcOption');
                    }
					ajaxData(url, {
						collect_id: container.attr("data-id"),
						calc_option: t.calc_option
					}, function (data) {
						_widget_content_fac(t, container.find('.mod-text-con').empty());
						sendInfo = "";
					});
				});
				$('.calc-opt').click(function () {
					msglog($(this).attr('stid'), $(this).attr('sstid'), $(this).attr('item'));
				});
            },
			attr: { "data-id": t.collect_id,
					"data-type" : t.draw_type,
					"calc-type": t.calc_option,
                    "calc-row-option": t.calculateRow_option,
					"data-count": t.metadata_cnt },
			calc: function (r) {
				r.container.find('.calc-opt').toggleClass('chosen');
				var conInfo = r.container.closest('.mod-box').attr('calc-type');
				if (conInfo) {
					conInfo = conInfo.split("|");
					for (var i = 0; i < conInfo.length; i++) {
						r.container.closest('.mod-box')
							.find('input')
							.filter("." + conInfo[i])
							.attr({ checked: "checked" });
					}					
				}
			},
			calcAttr: function (r) {
				r.calc.addClass("stat-listener").attr({
					'stid': '我的收藏',
					'sstid': '部件操作按钮',
					'item': '部件数据计算'
				});
			},
			calcChkBoxAttr: function (r) {
				r.head.find('.calc-opt').each(function () {
					$(this).addClass("stat-listener").attr({
						'stid': '我的收藏',
						'sstid': '部件计算复选框'
					});
					switch ($(this).find('input').attr('title')) {
					case 'min':
						$(this).attr({ "item": "最小值" });
						break;
					case 'max':
						$(this).attr({ "item": "最大值" });
						break;
					case 'sum':
						$(this).attr({ "item": "合计" });
						break;
					case 'avg':
						$(this).attr({ "item": "平均值" });
						break;
					};
				});
			},
            grandAll: function (r) {
                var url;
                t.calculateRow_option = r.grandAll.is(":checked") ? 1 : 0;
                if (window.pageConfigure.shared) {
                    url = getUrl('home', 'collect', 'setCalcRowOptionForSharedFavor');
                } else {
                    url = getUrl('home', 'collect', 'setCalcRowOption');
                }
                ajaxData(url, {
                    collect_id: r.container.attr("data-id"),
                    calc_option: t.calculateRow_option
                }, function (data) {
                    _widget_content_fac(t, r.container.find('.mod-text-con').empty());
					r.container.closest('.mod-box').attr({'calc-row-option': t.calculateRow_option});
                });
            },
            grandAllAttr: {
                checked: t.calculateRow_option ? true : false
            },
			editAttr: function (r) {
				r.edit.addClass("stat-listener").attr({
					'stid': '我的收藏',
					'sstid': '部件操作按钮',
					'item': '编辑部件'
				});
			},
            delete: function (r) {
				var param = { collect_id: r.container.attr("data-id"), metadata: [] },
					chked = r.container.find('.mod-text-con').find('.fixed-column').find("input:checked"),
					chkall = r.container.find('.mod-text-con').find('.fixed-column').find("input");
				if (chked.length === 0) {
					say(lang.t("批量删除前请选中具体的数据项"));
				} else if (chked.length == chkall.length) {
					ajaxData(getUrl("home", "collect", "delete"), {
						collect_id:  r.container.attr("data-id")
					}, function() {
						r.container.remove();
					}, true);
				} else {
					chked.each(function () {
						var info = $(this).parents('td').next().attr("dataid").split("^");
						param.metadata.push({
							data_id: info[0],
							gpzs_id: info[1],
							data_expr: info[2]
						});
					});
					ajaxData(getUrl("home", "collect", "deleteMetadata"), param, function (data) {
						_widget_content_fac({
							collect_id : r.container.attr("data-id"),
							draw_type:  r.container.attr("data-type"),
							calc_option: r.container.attr("calc-type"),
							calculateRow_option: t.attr("calc-row-option")
						}, r.container.find(".mod-text-con").empty());
					}, true);					
				}
			},
			remvAttr: function (r) {
				r.remove.addClass("stat-listener").attr({
					'stid': '我的收藏',
					'sstid': '部件操作按钮',
					'item': '删除部件'
				});
			},
			downAttr: function (r) {
				r.download.addClass("stat-listener").attr({
					'stid': '我的收藏',
					'sstid': '部件操作按钮',
					'item': '部件数据下载'
				});
			},
			child: [{
				type: "data",
				// show_table: true,
				// show_graph: true,
				url: {
					extend: getUrl("home", "collect", "getMetadataWithDate", "collect_id=" + t.collect_id),
					page: function() {
						return getPageParameters();
					}
				},
				afterLoad: function(data, container) {
					fac([$.extend({
						container: container
					}, hugeTableConfigure(data.data,
										  getTheadByDate(data.date.reverse()),    // thead是时间
										  getUrl("common", "data", "getTimeSeries"),
										  t.calc_option,
                                          t.calculateRow_option))]);
					$('.icon-mod-table').addClass("cur");
					$('.icon-mod-graph').removeClass("cur");
					$(this).next().find('.icon-mod-graph').click(function () {
						_widget_content_fac({
								collect_id : t.collect_id,
								draw_type: 1,
								calc_option: t.calc_option,
                                calculateRow_option: t.calculateRow_option
							}, container.empty());
							$(this).parent().parent().prev().find('.mod-delete-btn').hide();
							$(this).parent().parent().prev().find('.icon-mod-calc').hide();
							$(this).parent().parent().prev().find('.calc-opt').addClass('chosen');
							$(this).parent().parent().prev().find('.mod-calc-row').hide();
					});
					$(this).next().find('.icon-mod-table').click(function () {
						_widget_content_fac({
							collect_id : t.collect_id,
							draw_type: 3,
							calc_option: t.calc_option,
                            calculateRow_option: t.calculateRow_option
						}, container.empty());
						$(this).parent().parent().prev().find('.mod-delete-btn').show();
						$(this).parent().parent().prev().find('.icon-mod-calc').show();
                        $(this).parent().parent().prev().find('.mod-calc-row').show();
					});
				}
			}]
		};
        tmp.download = function(r) {
            $.download(getUrl("home", "collect", "getDataById"), $.extend(getDownloadParameters(), {
                collect_id : r.container.attr("data-id"),
                export: 1,
                file_name: r.head.find("span.mod-title").text()
            }));
            msglog(r.download.attr('stid'), r.download.attr('sstid'), r.download.attr('item'));
        };
        if (shared) {
            tmp.edit = function(r) {
                _upd_widget(r);
                msglog(r.edit.attr('stid'), r.edit.attr('sstid'), r.edit.attr('item'));
            };
        }
        // TODO 暂时只对管理员开放分享功能
        if (window.responseData.isAdmin) {
            tmp.shared = function(r) {
                _shared_widget(r);
            };
        }

        if (!window.pageConfigure.shared) {
			tmp.remove = function(r) {
                $.Dlg.Util.confirm(lang.t("确定删除窗口（{1}）？", t.collect_name),
                                   lang.t("删除此窗口后将从系统中永久消失。"),
                                   function() {
                                       ajaxData(getUrl("home", "collect", "delete"), {
                                           collect_id:  r.container.attr("data-id")
                                       }, function() {
                                           r.container.remove();
                                       }, true);
                                   });
                msglog(r.remove.attr('stid'), r.remove.attr('sstid'), r.remove.attr('item'));
            };
        }
		configure.push(tmp);
	} else {    // 不是表格
		configure.push({
			type: "wrap",
			container: con,
			title: t.collect_name,
			headEnabled: false,
			bottomEnabled: t.metadata_cnt > 10 ? false : true,
			draggable: true,
			attr: { "data-id": t.collect_id, "data-type": t.draw_type },
			edit: function(r) { _upd_widget(r); },
			remove: function(r){
				$.Dlg.Util.confirm(lang.t("确定删除窗口（{1}）？", t.collect_name),
								   lang.t("删除此窗口后将从系统中永久消失。"),
								   function() {
									   ajaxData(getUrl("home", "collect", "delete"), {
										   collect_id:  r.container.attr("data-id")
									   }, function(){
										   r.container.remove();
									   }, true);
								   });
			},
			download: function(r) {
				$.download(getUrl("home", "collect", "getDataById"), $.extend(getDownloadParameters(), {
					collect_id : r.container.attr("data-id"),
					export: 1,
					file_name: r.head.find("span.mod-title").text()
				}));
			},
			child: [{
				type: "data",
				url: {
					extend: getUrl("home", "collect", "getDataById", "collect_id=" + t.collect_id),
					page: function() {
						return getPageParameters();
					}
				},
				afterLoad: function(data, container) {
					var columnStack = t.draw_type == 2 ? 'normal' : ( t.draw_type == 4 ? 'percent' : '');
					fac([{
						type: "graph",
						container: container,
						columnStack: columnStack,
						chartStock: true,
						chartType: t.draw_type == 1 ? "line" : "column",
						data: data
					}]);
				}
			}]
		});
	}
	fac(configure);
};

/**
 * 根据 reportlist 显示页面的 widget
 * @returns {} 
 */
var _show_widget = function() {
    ajaxData(getUrl("home", "Collect", "getListByFavorId"), {
        favor_id: $("#J_gFavorId").val()    // J_gFavorId 在頁面上是 hidden 的
    }, function(data) {
        if (data.self && data.self.length) {
			// 循环绘制每个 chart
            $.each(data.self, function(i) {
                var t = this, con = _get_widget_con();
				t.draw_type = 3;    // 把 draw_type 改为 3
                t.calculateRow_option = parseInt(t.calculateRow_option);
                window.setTimeout((function(con, t) {
                    return function() {
                        _widget_fac(t, con, window.pageConfigure.shared ? false : true);
                    };
                })(con, t), i * 10 );
            });
        }
        if (data.shared && data.shared.length) {
            $.each(data.shared, function(i) {
                var t = this, con = _get_widget_con();
				t.draw_type = 3;    // 把 draw_type 改为 3
                t.calculateRow_option = parseInt(t.calculateRow_option);
                window.setTimeout((function(con, t) {
                    return function() {
                        _widget_fac(t, con, false);
                    };
                })(con, t), i * 10 );
            });
        }
    });
};

/**
 * 生成每个小窗口内容区
 * @param {} t 窗口信息
 * @param {} con 窗口 container
 */
var _widget_content_fac = function(t, con) {
    con.addClass("flash-loading");
    if( t.draw_type == 3 ){
        ajaxData(getUrl("home", "collect", "getMetadataWithDate"), $.extend(getPageParameters(), {
            collect_id: t.collect_id
        }), function(c){
            con.removeClass("flash-loading");
            var configure = hugeTableConfigure(
				c.data,
				getTheadByDate(c.date.reverse()),
				getUrl("common", "data", "getTimeSeries"),
				t.calc_option,
                t.calculateRow_option);
            configure.container = con;
            fac([configure]);
        });
    } else {
        ajaxData(getUrl("home","collect","getDataById"), $.extend(getPageParameters(), {
            collect_id: t.collect_id
        }), function(c){
            con.removeClass("flash-loading");
            var columnStack = t.draw_type == 2 ? 'normal' : ( t.draw_type == 4 ? 'percent' : '');
            fac([{
                type: "graph",
                container: con,
                chartType: t.draw_type == 1 ? "line" : "column",
                chartStock: true,
                columnStack: columnStack,
                data: c
            }]);
        });
    }
};

/**
 * 获取 z-index 层级
 * @returns { Integer } z-index 层级
 */
var _get_index = function() {
    if (FAVOR.index == 0)
		FAVOR.index = 100;
    return FAVOR.index--;
};

/**
 * TODO
 * @param {} data
 * @returns {} 
 */
var _handle_indicator_choose = function(data) {
    var report = [], set = [], diy = [];
    if (data && data.length) {
        $.each(data, function() {
            if(this.type == "report") {
                report.push({
                    title: this.r_name,
                    attr: {
                        id: this.type + "|" + this.r_id,
                        cid: this.type + "|" + this.r_id,
                        child: this.is_multi == "1" ? true : false
                    }
                });
            } else if(this.type == "set") {
                set.push({
                    title: this.r_name,
                    attr: {
                        id: this.type + "|" + this.r_id,
                        cid: this.type + "|" + this.r_id,
                        child: this.is_multi == "1" ? true : false
                    }
                });
            } else if (this.type == "diy") {
                diy.push({
                    title: this.r_name,
                    attr: {
                        id: this.type + "|" + this.r_id,
                        cid: this.type + "|" + this.r_id,
                        child: this.is_multi == "1" ? true : false
                    }
                });
            }
        });
    }
    return [{ title: lang.t("游戏分析数据"), children: set },
			{ title: lang.t("游戏自定义数据"), children: report },
			{ title: lang.t("游戏自定义加工数据"), children: diy }];
};

/**
 * TODO
 * @param {} data
 * @param {} sel
 * @returns {} 
 */
var _handle_setting_choose = function(data, sel) {
    var rlt = [];

    $.each( data, function(){
        rlt.push({
            title : this.data_name,
            attr : { id : this.id },
            selected : $.tools._in_array( sel, this.id ) ? true : false
        });
    });

    return rlt;
};


var hugeTableConfigure = function(list, thead, url, calculate, calculateRow) {
	calculate = calculate ? calculate : "";
    var maxHeight = $(".aside").height() - $("#J_contentBody").position().top - 110 - 17;
    var favorType = $("#J_gFavorType").val();
    var options = {
        type: "hugeTable",
		checkbox: false,
		calculate: calculate.split("|"),
        hide: false,
        dataDelay: true,
        height: maxHeight,
        thead: thead,
        data: [],
        isAndAllCalacuteRow: calculateRow ? calculateRow : false,
        prepareData: function(option) {
            var prepare = [];
            $.each(option.data, function() {
                prepare.push([{
                    title: this[0].title,
                    dataId: this[0].dataId
                }]);
            });
            return prepare;
        },
        child: [{
            type: "progressiveTable",
			checkbox: false,
			calculate: calculate.split("|"),
            url: {
                url: url,
                page: function(ids) {
                    var parameters = getPageParameters();
                    for( var i = 0; i < ids.length; i++ ){
                        var tmpId = ids[i].split("^");
                        parameters["data_info[" + i + "][data_id]"]   = tmpId[0];
                        parameters["data_info[" + i + "][gpzs_id]"]   = tmpId[1];
                        parameters["data_info[" + i + "][data_expr]"] = tmpId[2];
                        parameters["data_info[" + i + "][data_name]"] = tmpId[3];
                        parameters["data_info[" + i + "][factor]"]    = tmpId[4];
                        parameters["data_info[" + i + "][precision]"] = tmpId[5];
                        parameters["data_info[" + i + "][unit]"]      = tmpId[6];
                    }

                    parameters["by_data_expr"] = 1;
                    return parameters;
                }
            },
            afterAllLoaded: function (data) {
                //$.each(data[0].data, function () {
                    //console.log(this);
                //});
            }
        }]
    };

    $(list).each(function() {
        options.data.push([{
            title: this.data_name,
            dataId: [
                this.data_id,
                this.gpzs_id,
                this.data_expr,
                this.data_name,
                this.factor,
                this.precision,
                this.unit
            ].join("^")
        }]);
    });
    return options;
};

/**
 * 根据日期获取表格头，用于插件
 * @param {} dateSeries
 * @returns {} 
 */
var getTheadByDate = function(dateSeries) {
    var thead = [{
        title: lang.t("日期"),
        css: {width: "200px"}
    }];
    $(dateSeries).each(function() {
        thead.push({
            title: this.toString(),
            className: isWeekend(this) ? "gr" : "",
            css: {width: "80px"}
        });
    });
    return thead;
};

/**
 * 未用
 * @param {} data
 * @returns {} 
 */
var _handle_graph_data = function(data) {
    var rlt = [],
        len = data.data ? data.data.length : 0;
    for (var i = 0; i < len; i++ ) {
        var tmp = {
            data : [],
            pointInterval: data.pointInterval * 1000,
            pointStart: data.pointStart * 1000,
            name : data.data[i].name
        };
        for(var j = 0; j < data.data[0].data.length; j++ ){
            tmp.data.push(parseFloat(data.data[i].data[j]));
        }
        rlt.push(tmp);
    }
    return rlt;
};
