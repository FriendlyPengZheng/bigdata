"use strict";
(function(window, undefined) {
	var index = 100;
	var Tips = null;
	var VIEW = {
		widget: null
	};
	$(function() {
		_g_tools();
	});

	function _g_tools(){
		//time
		var $game = $("#J_game"),
			$from = $("#J_from"),
			$to = $("#J_to"),
			$date = $("#J_date");

		$game.change(function(){
			var gameId = $(this).find(":selected").attr("data-id");
			go(getUrl($("#J_r").val(), undefined, undefined, "game_id=" + gameId));
		});

		// change content width
		if ($('#J_authchk').val() == "1")
			$('.main').find('.content').css({ marginRight: 45 });
		else
			$('.main').find('.content').css({ marginRight: 10 });
		
		// select time
		$date.datepick({
			rangeSelect: true,
			monthsToShow: 3,
			monthsToStep: 3,
			monthsOffset: 2,
			shortCut : true,
			onClose: function(userDate, e) {
				if (userDate.length) {
					var userDate = $date.val().split("~");
					userDate[0] = $.trim(userDate[0]);
					userDate[1] = $.trim(userDate[1]);
					$from.val(userDate[0]);
					$to.val(userDate[1]);
				} else {
					$date.val($from.val() + " ~ " + $to.val());
				}
				refresh();
			}
		});
		$("#J_from_to").click(function(e){
			$date.focus();
			e.stopPropagation();
		});

		var $platform = $("#J_platform");

		/*getZoneServer( $platform.find(":selected").attr("data-id"), function( data ){
			_zoneServer_fac( data );
			_g_tree();
		});
		$platform.tmselect().change(function(e){
			getZoneServer( $(this).find(":selected").attr("data-id"), function( data ){
				_zoneServer_fac( data, true );
			});
		});*/

		getZoneServer(_getPlatformId(), function( data ){
	    	_zoneFac( data );
	    	_g_tree();
	    },"全区");
	    
	    getZoneServer(_getZoneId(), function( data ){
	    	_serverFac( data );
	    },"全服");
	    
	    $("#J_platform").tmselect().change(function(e){
	    	getZoneServer($(this).find(":selected").attr("data-id"), function( data ){
	        	_zoneFac( data, true );
	        },"全区");
	    });
	}

	function gatherInfo(widget, type) {
		var items = widget.find('.item-ins'),
			selItems = items.find('.checkbox-con').find('input').filter(':checked').parent().parent(),
			baseGameId = parseInt(widget.find('.game-selector').children().filter(':selected').attr('game_id')),
			calcType = widget.find('.calc-selector').children().filter(':selected').attr('type'),
			filterInfo = widget.find('.filter-info').children().find('input:checked'),
			filterSet = new Array(),
			opMeta = new Array(),
			pro = new Object();

		filterInfo.each(function () {
			filterSet.push(parseInt($(this).attr('filter_id')));
		});
		
		selItems.each(function () {
			var meta = new Object(),
				source = $(this).find('.item-game'),
				item = $(this).find('.item-name');
			if (type == 'submit') {
				meta.game_id = source.attr('game_id');
			} else if (type == 'save') {
				meta.game_name = source.text();
				meta.item_name = item.text();
			}
			meta.type = source.attr('type');
			meta.gpzs_id = source.attr('gpzs_id');
			meta.r_id = source.attr('r_id');
			if (source.attr('data_id') != undefined)
				meta.data_id = source.attr('data_id');
			meta.periods = new Array();
			$(this).find('.time-tag').each(function () {
				var timeRange = $(this).find('input').val().split('~'),
					timeFrom = timeRange[0],
					timeTo = timeRange[1];
				meta.periods.push({
					from: timeFrom,
					to: timeTo
				});
			});
			opMeta.push(meta);
		});

		var headInfo = type == "submit" ? {
			'game_id': baseGameId,
			'operation': calcType
		} : {};
		pro = $.extend(headInfo, { 'operands': opMeta, 'filter_info': filterSet });
		
		return pro;
	}

	/**
	 * @brief: 弹出对话框的信息
	 * @param: NULL
	 * @return: NULL
	 */
	function _getOptions() {
		var date = new Date(),
			usrName = $('#J_header').find('.wrapper').find('.links').find('li').eq(0).text().split("，")[1],
            dateInfo = date.toISOString().substring(0, 10).replace(/\-/g, '') + '_' + date.getHours() + '_' + date.getMinutes(),
			dftTxt = usrName + '_' + dateInfo;
		return {
			items: [{
				label: {
					title: lang.t("本次查询名称为："),
					className: "title-inline"
				},
				items: [{
					type: "text",
					name: "set-name",
					className: "ipttxt",
					value: dftTxt
				}]
			}]
		};
	}
	
	// 确认本次查询
	function formSubmit(pro) {
		var SUBMITDIALOG, FIELDSET, param;
		FIELDSET = new tm.form.fieldSet($.extend({}, _getOptions()));
		if (!SUBMITDIALOG && pro.operands.length != 0) {
			if (pro.operands.length > 5) {
				say("目前对差集支持最多3个事件的计算,对交集和并集支持最多5个事件的计算");
			} else if (pro.operands.length > 3 && pro.operation == 'setdiff') {
				say("目前对差集支持最多3个事件的计算,对交集和并集支持最多5个事件的计算");
			} else {
				SUBMITDIALOG = $.Dlg.Util.popup({
					id: "J_setSubmitName",
					title: lang.t("自助查询（请到我的下载中查看本次查询）"),
					contentHtml: $("<form>").append(FIELDSET.getElement())
				});
				SUBMITDIALOG.setSaveHandler(function () {
					ajaxData(getUrl("tool", "selfhelp", "add"),
							 $.extend(pro, {
								 'file_name': $("#J_setSubmitName").find("form").find('input').val()
							 }),
							 function(res) {
								 $('.item-con').find('.check-item:checked').parent().parent().parent().fadeOut(function () {
									 $(this).remove();
								 });
								 var dlg = $.Dlg.Util.message("温馨提示", "正在处理，请稍候...", "确定"),
							 	 	 msg = "";
								 dlg.show();
								 if (res.code === 0) {
							 	 	 go(res.data.url);
							 	 	 return;
								 } else if (res.code === 1) {
							 	 	 msg = "由于您选择的时间间隔较长，已自动为您异步下载，"
							 	 		 + "您可以到页面右上角【我的下载】查看和下载！"
							 	 		 + "<a href='" + getUrl("tool/file/index")
							 	 		 + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
								 } else if (res.code === 2) {
							 	 	 msg = "文件正在处理，请耐心等待，您也可以到页面右上角【我的下载】查看！"
							 	 		 + "<a href='" + getUrl("tool/file/index")
							 	 		 + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
								 }
								 if (msg !== "") {
							 	 	 dlg.setConfirmHtml(msg);
							 	 	 dlg.getManager().setPosition(dlg);
							 	 	 dlg.show();
								 }
							 }, "POST");
				});
				SUBMITDIALOG.show();				
			}
		} else {
			say('查询前请先选中一组事件');
		}
		
		return false;
	}

	function formSave(pro) {
		ajaxData(getUrl('common', 'basket', 'reviseInfo'), pro, function (res) {
			//TODO: 
			// say('Save Info Failure.');
		}, false);
	}
	
	function _gSearch() {
		var search = $("#J_search"),
			searchIpt = search.find(".srh-txt"),
			searchTag = search.find(".search-tag"),
			searchFn = function() {
				$("#J_tree").jstree("search", searchIpt.val());
			};
		searchIpt.focus(function() {
			search.css({ border: "2px solid #FE9D1F" });
		}).blur(function() {
			search.css({ border: "2px solid #CCC" });
		}).keydown(function(e) {
			if(e.keyCode == 13) searchFn();
		});
		searchTag.click(function() {
			searchFn();
		});
	}

	function _showSearchTips() {
		if(Tips) clearTimeout(Tips);
		var tips = $("#J_search").find(".search-tips");
		tips.slideDown(200, function(){
			Tips = setTimeout(function(){
				tips.fadeOut(300);
			}, 500);
		});
	}
	function _g_tree() {
		_gSearch();
		// 左树
		var gameId = $("#J_game").find(":selected").attr("data-id");
		$("#J_tree").jstree({
			plugins : ["html", "json_data", "themes", "cookies", "ui", "core", "search"],
			themes : { "theme" : "orange" },
			search : {
				case_insensitive: true,
				ajax: {
					url : "../../../gamecustom/tree/search",
					data : function(n) {
						return {
							keyword: $("#J_search").find(".srh-txt").val(),
							game_id: $("#J_paramGameId").val()
						};
					},
					success : function(res) {
						var arr = [];
						$(res.data).each(function() {
							arr.push("#custom" + this);
						});
						return arr;
					}
				}
			},
			json_data : {
                ajax : {
                	url : "../../../gamecustom/tree/getTree",
                    data : function(n) {    // 请求参数
                        return {
                            game_id : gameId,
                            parent_id : n.attr ? n.attr("node_id") : 0
                        };
                    },
                    success : function( res ) {
                        if (res.result == 0 && res.data) {
                            var nodes = [];
                            $.each(res.data, function() {
                                nodes.push({
                                    data : this.node_name,
                                    state : (this.is_leaf == "1" ? "leaf" : "closed"),
                                    attr : {
                                        title: this.node_name,
                                        id: "custom" + this.node_id,
                                        node_id : this.node_id,
                                        is_leaf : this.is_leaf
                                    }
                                });
                            });
                            return nodes;
                        } else {
                            return "";
                        }
                    }
                }
            },
			ui: {
				select_limit: 1
			}
		}).bind("select_node.jstree", function(event, node) {    // 页面选择节点后的事件绑定
			if (node.rslt.obj.attr("is_leaf") === "1") {
				var container = $("#J_content").empty().addClass("loading"),
                    modTitle = node.rslt.obj.text();

                // 两个表格的配置
				ajax("../../../gamecustom/content/getContentList", $.extend({
				//ajax(getUrl("gamecustom", "content", "getContentList"), $.extend({
					node_id : node.rslt.obj.attr("node_id"),
					tags: "all"
				}, getPageParameters()), function(res) {
					if (res.result == 0) {
                        container.removeClass("loading has-no-data")
                            .data("content-data", fac(configure(res.data, $.extend({
					            node_id : node.rslt.obj.attr("node_id")
				            }, getPageParameters()), modTitle)));
					}
				});
                
			} else {
				if( node.args[1] ) { node.inst.toggle_node(); }
			}
		}).bind("search.jstree", function(evt, node) {
			if(node.rslt.nodes.length == 0) {
				_showSearchTips();
			}
		});
	}

	// 跟据返回生成内容配置
	function configure(configure, param, title) {
		if((configure.data instanceof Object && $.isEmptyObject(configure.data))
           || (Array.isArray(configure.data) && !configure.data.length)) {
			$("#J_content").addClass("has-no-data").text(lang.t("没数据"));
			return;
		}
		var prepared = [],
            child = null,
            rlen = configure.data[0].length,
			theadConfigure = getTheadByDate(configure.date),
            judgeRange = function (rlen) {
                var range = {
					'all': 101,
					'month': 201,
					'week': 99999999
				};
                for (var prop in range) {
				    if (range.hasOwnProperty(prop)) {
					    if (rlen < range[prop]) {
						    return prop;
					    }
				    }
			    }
                return "all";
            },
            dataconfig = function (data) {
                var conf = [];
                $(data).each(function () {
                    conf.push([{
						title: this.r_name,    // left tree node name
						dataId: this.type + "_" + this.r_id,
						iffavor: this.iffavor,
						ifselfhelp: this.ifselfhelp,
						infavor: this.infavor,
						inselfhelp: this.inselfhelp
                    }]);
                });
                return conf;
            },            
			hugeTableConfigure = function(option, thead, prepared, page) {
                return {
                    type: "hugeProgressiveTable",    // Draw type
					checkbox: false,
				    cartEntrance: true,
					authChk: $('#J_authchk').val() == "1" ? 1 : 0,
				    cartCallback: 'tableEvent',
                    hide: false,
                    dataDelay: true,
                    height: maxHeight,
                    thead: thead,
                    data: dataconfig(option),    // [],
                    prepareData: prepared,
                    url: {
                        url: "../../../common/data/getTimeSeries",
                        page: page
                    }
                };
			},
			tabConfigure = function(option, thead, prepared) {
				var options = {
                    type: "tabsExtendMore",
                    child: []
                },
					dataUrl = "../../../gamecustom/content/getDataList?r_id=",
					tabConfig = {
						title: "",
						child: [ {
                            type: "wrap",
                            theme: "no-frame",
                            headEnabled: false,
                            bottomEnabled: false,
                            condition: ['tagFilter'],
                            conditionOptions: {
                                tagFilter: {
                                    default: judgeRange(rlen)
                                },
								ignore: []
                            },
                            child: [ {
							    type: "data",
							    url: {
								    timeDimension: 1,
								    page: getPageParameters
							    },
							    child: []
						    } ]
                        } ]
					}, tempTabConfig;
				$(option).each(function() {
					tempTabConfig = $.extend(true, {}, tabConfig);
                    
					tempTabConfig.title = this.r_name;
                    tempTabConfig.child[0].conditionOptions.tagFilter.default = judgeRange(this.r_length);
					tempTabConfig.attr = {
						"data-id" : this.type + "_" + this.r_id,
						"rlength" : this.r_length
					};
					tempTabConfig.child[0].child[0].url.extend = dataUrl + this.r_id + "&type=" + this.type;
					tempTabConfig.child[0].child[0].rlen = this.r_length;
					tempTabConfig.child[0].child[0].child.push(hugeTableConfigure([], thead, prepared, function(ids) {
						var parameters = getPageParameters(),
							i = 0, length = ids.length, parts;
						for (; i < length; i++) {
							parts = ids[i].split(":");
							parameters["data_info[" + i + "][data_id]"] = parts[0];
							parameters["data_info[" + i + "][data_expr]"] = parts[1] ? parts[1] : "";
						}

						parameters["by_data_expr"] = 1;
						return parameters;
					}));
					options.child.push($.extend(true, {}, tempTabConfig));
				});
				return options;
			},
			maxHeight = $(".aside").height() - $("#J_content").position().top - 110 - 20;

		if (configure.data[0] && configure.data[1]) {
            maxHeight = 400;    // both
        }
        if (configure.data[0] && configure.data[0].length) {
            prepared.push({
			    type: "wrap",
			    container: $("#J_content"),
			    attr: { id: "J_singleReport" },
			    title: title,
			    headEnabled: false,
			    bottomEnabled: false,
			    condition: ['tagFilter'],
			    conditionOptions: {
                    tagFilter: {
                        default: judgeRange(rlen)
                    },
					ignore: ["modify_week"]
                },
			    download: function() {
				    var param = getDownloadParameters();
				    param.is_multi = 0;
				    if (param) {
					    $.download(getUrl("gamecustom", "tree", "export"), param);
				    }
			    },
			    child: [{
                    type: "data",
                    url: {
                        extend: "../../../gamecustom/content/getContentList?",
                        page: function () {
                            return param;
                        }
                    },
                    refresh: function (option) {
                        option.dataChange && (option.data = dataconfig(option.data.data[0]));
                        $(child).each(function() {
                            this.refresh(option);
                        });
                    },
                    afterLoad: function (data, container) {
                        var conf = hugeTableConfigure(data.data[0], getTheadByDate(data.date), null, function(ids) {
					        var parameters = getPageParameters(),
						        i = 0, length = ids.length, parts;
					        for (; i < length; i++) {
						        parts = ids[i].split("_");
						        parameters["data_info[" + i + "][type]"] = parts[0];
						        parameters["data_info[" + i + "][r_id]"] = parts[1];
						        if (parts[0] === "report") {
							        parameters["data_info[" + i + "][range]"] = "";
						        }
					        }
					        parameters["by_data_expr"] = 1;
					        return parameters;
				        });
                        conf["container"] = container;
                        child = fac([ conf ]);
                    }
                }]
		    });
        }
		if (configure.data[1] && configure.data[1].length) {
			prepared.push({
				type: "wrap",
				container: $("#J_content"),
				title: title,
				attr: { id: "J_multiReport" },
				headEnabled: false,
				bottomEnabled: false,
				download: function() {
					var param = getDownloadParameters();
					param.is_multi = 1;
					if (param) {
						$.download(getUrl("gamecustom", "tree", "export"), param);
					}
				},
				child: [
					tabConfigure(configure.data[1], theadConfigure, function(data) {
						var prepare = [];
						$.each(data, function() {
							prepare.push([{
								title: this.data_name,
								dataId: parseInt(this.data_id) !== 0 ? this.data_id : ("0:" + this.data_expr),
								iffavor: this.iffavor,
								ifselfhelp: this.ifselfhelp,
								infavor: this.infavor,
								inselfhelp: this.inselfhelp
							}]);
						});
						return prepare;
					})
				]
			});
		}
		return prepared;
	}

	/**
	 * @brief getTheadByDate
	 * 根据日期获取表格头，用于插件
	 *
	 * @param dateSeries
	 */
	function getTheadByDate(dateSeries) {
		var thead = [{
            title: lang.t("日期"),
            css: { width: "200px" }
        }];
		for(var i = dateSeries.length - 1; i > -1; i--) {
			thead.push({
				title: (dateSeries[i]).toString(),
				className: isWeekend(dateSeries[i]) ? "gr" : "",
				css: { width: "80px" }
			});
		}
		return thead;
	}
    
	/**
	 * @brief getPageParameters
	 * 获取页面公共参数
	 */
	function getPageParameters() {
		return {
			from: $("#J_from").val(),
			to: $("#J_to").val(),
			
			server_id : _getServerId(),
			gpzs_id : -1,
			zone_id : _getZoneId(),
			platform_id : _getPlatformId(),
			game_id : $("#J_paramGameId").val()
		};
	}

	function refresh() {
		ajax("../../../gamecustom/content/getTimePoints", getPageParameters(), function(res) {
		//ajax(getUrl("gamecustom", "content", "getTimePoints"), getPageParameters(), function(res) {
			if (res.result == 0) {
				var modules = $("#J_content").data("content-data");
				if (modules && modules.length) {
					$(modules).each(function() {
						this.refresh({
							theadChange: true,
							thead: getTheadByDate(res.data)
						});
					});
				}
			}
		}, "POST");
	}

	function getDownloadParameters() {
		var select = $("#J_tree").jstree("get_selected");
		if (!select.length) {
			return ;
		}
		return {
			game_id: $("#J_paramGameId").val(),
			gpzs_id: $("#J_zoneServer").find(".selected-item").attr("data-id").split("_")[0],
			node_id: select.attr("node_id"),
			file_name: select.text(),
			from: $("#J_from").val(),
			to: $("#J_to").val()
		};
	}

	/**
	 * @brief _zoneFac
	 * 生成区服列表并绑定事件
	 * @param data array 区服列表
	 * @return
	 */
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

	/**
	 * @brief _serverFac
	 * 生成区服列表并绑定事件
	 * @param data array 区服列表
	 * @return
	 */
	function _serverFac( data, firstClick ){
	    firstClick = firstClick ? firstClick : false;
	    var $zoneServer = $("#J_server");
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
	/*    var modules = $("#J_contentBody").data("content-data");
	    if (modules && modules.length) {
	        $(modules).each(function() {
	            this.refresh({
	                dataChange: true
	            });
	        });
	    }*/
		ajax("../../../gamecustom/content/getTimePoints?", getPageParameters(), function(res) {
			if (res.result == 0) {
				var modules = $("#J_content").data("content-data");
				if (modules && modules.length) {
					$(modules).each(function() {
						this.refresh({
							theadChange: true,
							thead: getTheadByDate(res.data)
						});
					});
				}
			}
		}, "POST");
	};
	/**
	 * @brief getZoneServer
	 * 获取区服列表
	 * @param id ：平台id
	 * @return
	 */
	function getZoneServer( id, fn , server_name){
		    ajax("../../../common/gpzs/getZoneServer", {
	        gameId : $("#J_paramGameId").val(),
	        serverId : id
	    }, function(res){
	        if (res.result == 0) {
	            var data = [],
	                zoneId = $("#J_paramZoneId").val(),
	                serverId = $("#J_paramServerId").val(),
	                selected = false;
	            data.push({
	                id : -1,
	                name : server_name,
	                selected : true
	            });
	            $.each( res.data, function( i ){
	                data.push({
	                    id : this.serverId,
	                    name : this.serverName,
	                    selected : false
	                });
	            });
	            if (data) {
	                data[0].selected = true;
	            }
	            if( fn )fn( data );
	        } else {
	            say(lang.t("获取数据错误：") + res.err_desc);
	        }		
	    });
	}

})(window);

function _getPlatformId(){
    return $("#J_platform").find(":selected").attr("data-id");
}

function _getZoneId(){
    return $("#J_zone").find(".selected-item").attr("data-id");
}

function _getServerId(){
    return $("#J_server").find(".selected-item").attr("data-id");
}

var getType = function () {
	return { 'type': 'report' };
};

var getReportData = function (root, td) {
	var judge = root.parent().parent().parent().parent().parent().parent().parent();
	
	if (judge.hasClass('tabs-wrapper')) {
		var rid = judge.find('ul').find('.tabs-active').attr('data-id').split('_')[1];
		return {
			'r_id': rid,
			'data_id': td.attr('dataid')
		};
	} else {
		return { 'r_id': td.attr('dataid').split('_')[1] };
	}
};

var getPeriods = function () {
	$date = $("#J_date");
	return { periods: [{
		from: $date.val().split("~")[0],
		to: $date.val().split("~")[1]
	}] };
};

var getFilename = function () {
	var date = new Date();
	return { 'file_name': date.toLocaleString() + '的查询' };
};

var getFilterInfo = function () {
	return { 'filter_info': [1] };    // default is 1
};

var blinkTag = function () {
	$('.selfhelp-tag').hint(1, '#FFA500');
};

/**
 * @brief ajaxData
 * ajax请求数据
 * @param url：请求url链接
 * @param param：参数
 * @param fn：回调函数
 * @param hide：是否显示overlay提醒
 */
function ajaxData(url, param, fn, hide) {
	if(hide) overlayer({ text: lang.t("操作中...")});
	ajax(url, param, function(res){
		if(res.result == 0){
			if(hide) hidelayer(lang.t("操作成功~.~"));
			if(fn) fn(res.data);
		} else {
			if(hide) hidelayer();
			say(lang.t("获取数据错误：") + res.err_desc);
		}
	}, "POST");
}

/**
 * @brief tableEvent
 * 用于操作表格
 * 在生成表格过程中通过 _trigger 触发
 */
window.tableEvent = function () {
	$('.fixed-table').find('.selfhelp').die().live('click', function () {
		var idx = $(this).parent().parent().index();
		var root = $(this).parent().parent().parent().parent().parent().parent();
		var tdWanted = root.find('.fixed-body').find('tbody').find('tr').eq(idx).children().eq(1);

		var param = $.extend({
				'game_name': $('li').find('.selected-item-white').attr('title')
			}, getGameId(), {
				'items': [ $.extend({
					'item_name': tdWanted.attr('title')
				}, getType(), getGpzsId(), getReportData(root, tdWanted)) ]
			});

		// 发送请求后台刷新 session
		ajaxData(getUrl("common", "basket", "addBasketInfo"), param, function(data) {
			if (data) {
				blinkTag();
			}
        }, false, true);
	});

	$('.fixed-table').find('.myfavor').die().live('click', function () {
		$('.fixed-table').find('.myfavor').removeClass('favor-checked');
		$(this).addClass('favor-checked');
		$('#J_addFavorBtn').trigger('click');
	});
};

window.fetchTags = function (tagName) {
	//TODO: 
};
