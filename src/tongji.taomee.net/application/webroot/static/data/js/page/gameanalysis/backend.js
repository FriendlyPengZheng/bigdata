// @Author: Maverick
// @Time: 8-12-2014
// @email: youngleemails@163.com
// @Fileinfo: 后台服务状态列表的显示和相应的操作
$.extend($.datatable.sort, {
    "binary-pre": function(a) {
        var rlt = 0;
        if (a.search(/KB/)!= -1) {
            rlt = a.replace("KB", "") * Math.pow(1024, 1);
        } else if (a.search(/MB/)!= -1) {
            rlt =  a.replace("MB", "") * Math.pow(1024, 2);
        } else if (a.search(/GB/)!= -1) {
            rlt =  a.replace("GB", "") * Math.pow(1024, 3);
        } else if (a.search(/TB/)!= -1) {
            rlt =  a.replace("TB", "") * Math.pow(1024, 4);
        } else if (a.search(/PB/)!= -1) {
            rlt =  a.replace("PB", "") * Math.pow(1024, 5);
        }
        return rlt;
    },
    "binary-asc": function(x, y) {
        return x - y;
    },
    "binary-desc": function(x, y) {
        return y - x;
    }
});

(function($, undefined) {
	// 预备弹出的对话框
	var TYPEALTDIALOG, ALTSONDIALOG, ALTSOFFDIALOG, DELDIALOG, FBDDIALOG, FIELDSET;

	$(document).ready(function() {
		/**
         * @brief: 全部信息显示按钮
         * @param: NULL
         * @return: NULL
         */
		$('#J_allInfoBtn').on("click", function() {
            $('.alert-info').addClass('hidden');
            $('.all-info').removeClass('hidden');
            $('#J_alertInfoBtn').removeClass('add-btn').addClass('btn-dft');
            $(this).removeClass('btn-dft').addClass('add-btn');
		});


		/**
         * @brief: 告警信息显示按钮
         * @param: NULL
         * @return: NULL
         */
		$('#J_alertInfoBtn').on("click", function() {
            $('.alert-info').removeClass('hidden');
            $('.all-info').addClass('hidden');
            $('#J_allInfoBtn').removeClass('add-btn').addClass('btn-dft');
            $(this).removeClass('btn-dft').addClass('add-btn');
		});

		/**
		 * @brief: 对类型禁止告警
		 * @param: NULL
		 * @return: { boolean }
		 */
		$('#J_typeAlertOffBtn').on("click", function () {
            FIELDSET = new tm.form.fieldSet($.extend({}, _getOptions()));
            if (!TYPEALTDIALOG) {
                TYPEALTDIALOG = $.Dlg.Util.popup({
				    id: "J_typeAlert",
				    title: lang.t("禁止告警"),
				    contentHtml: $("<form>").append(FIELDSET.getElement())
			    });
            }
			TYPEALTDIALOG.setSaveHandler(function () {
				var param = $("#J_typeAlert").find("form").formToArray();
				if (isNumber(param[0].value)) {
					ajaxData(getUrl("gameanalysis/site", "backend", "banWarnForType"), {
						ban_type: _getCurAsideKey(),
						fbd_flag: 1,      // 代表禁止告警
						minutes: param[0].value
					}, function() {
						location.reload();
					}, "POST");
				} else {
					say(lang.t("请输入正确的告警时长"));
				}
			});
			TYPEALTDIALOG.show();
			return false;
		});

		/**
		 * @brief: 对 IP 批量禁止告警
		 * @note: fbd_flag 为 1 代表禁止告警 ，禁止告警时需要填写禁止时长
		 * @param: NULL
		 * @return: { boolean }
		 */
		$('#J_alertsOnBtn').on("click", function() {
            FIELDSET = new tm.form.fieldSet($.extend({}, _getOptions()));
			var ips   = [];
            $(".tbl-ckb").each(function () {
                if ($(this).attr('checked') == 'checked') {
                    ips.push(_getCurIp($(this)));
                }
            });
			if (ips.length) {
				if (!ALTSONDIALOG) {
					ALTSONDIALOG = $.Dlg.Util.popup({
						id: "J_alertsOn",
						title: lang.t("批量禁止告警"),
						contentHtml: $("<form>").append(FIELDSET.getElement())
					});
				}
				ALTSONDIALOG.setSaveHandler(function () {
					var param = $("#J_alertsOn").find("form").formToArray();
					if (isNumber(param[0].value)) {
						ajaxData(getUrl("gameanalysis/site", "backend", "banWarnForIp"), {
							ban_type: _getCurAsideKey(),
							fbd_flag: 1,    // 代表禁止 IP
							minutes: param[0].value,
							ip: ips
						}, function () {
							location.reload();
						}, "POST");
					} else {
						say(lang.t("请输入正确格式的告警时长"));
					}
				});
				ALTSONDIALOG.show();
			} else {
				say(lang.t("请选中IP"));
			}
			return false;
		});


        /**
		 * @brief: 对类型开启告警
		 * @param: NULL
		 * @return: NULL
		 */
		$('#J_typeAlertOnBtn').on("click", function () {
			ajaxData(getUrl("gameanalysis/site", "backend", "banWarnForType"), {
				ban_type: _getCurAsideKey(),
				fbd_flag: 0
			}, function () {
				location.reload();
			}, "POST");
		});


		/**
		 * @brief: 对 IP 批量开启告警
		 * @note: fbd_flag 为 0 代表开启告警 ，开启告警时不需要填写禁止时长
		 * @param: NULL
		 * @return: NULL
		 */
		$('#J_alertOffBtn').on("click", function() {
            var ips = [];
            $(".tbl-ckb").each(function () {
                if ($(this).attr('checked') == 'checked') {
                    ips.push(_getCurIp($(this)));
                }
            });
			if (ips.length) {
				ajaxData(getUrl("gameanalysis/site", "backend", "banWarnForIp"), {
					ban_type: _getCurAsideKey(),
					fbd_key: 0,      // 代表开启告警
					ip: ips
				}, function () {
					location.reload();
				}, "POST");
			} else {
				say(lang.t("请选中IP"));
			}
		});


		/**
		 * @brief: 单击全选
		 * @param: NULL
		 * @return: NULL
		 */
        $('.sel-total').click(function () {
			// 把所有单选框的 checked 属性设置为 true
            $(this).closest("table").find(".tbl-ckb").attr({
                checked: true
            });
        });


		/**
		 * @brief: 单击反选
		 * @param: NULL
		 * @return: NULL
		 */
        $('.sel-other').on("click", function () {
			// 反置单选框的 checked 属性
            $(this).closest("table").find(".tbl-ckb").each(function() {
                if ($(this).attr('checked') == 'checked') {
                    $(this).attr({
                        checked: false
                    });
                } else {
                    $(this).attr({
                        checked: true
                    });
                }
            });
        });
        _getBackendDataList();
	});


    /**
     * @brief: 根据选中的侧边栏的值显示对应的整个页面的列表信息
     * @param: { int }
     * @return: NULL
     */
	function _getBackendDataList() {
        ajaxData(getUrl("gameanalysis/site", "backend", "getTableList"), {
           // aside_key 对应相应侧边栏的选项
           // 向后端发送 aside_key 来获取相应的列表具体信息
            aside_key: _getCurAsideKey()
        }, function(data) {
            _createForm(data);
        }, "POST");
	}

	/**
	 * @brief: 根据列表信息创建列表
	 * @note: 通过 jQuery 隐式遍历 DOM 查看 data-key 的值是否在 data[0] 中，
	 *        如果在则在列表中添加为 tbody 的内容
	 * @param: { packages: PHP 传递过来的每个协议包的内容 }
     * @return: NULL
	 */
	function _createForm(packages) {
		_draw(packages);
        _addBtnWoff();
        _addBtnDel();
        _addBtnWon();
        _addSearch();
        // 每隔 5 分钟刷新页面
        setTimeout(function() {
            _getBackendDataList();
        }, 300000);
	}

    /**
     * @brief: 绘制 tbody
     * @param: { Object }
     * @return: NULL
     */
    function _draw(packages) {
        var registerTotal = packages.registerTotal,    // 注册 IP 数
            alarmTotal    = packages.alarmTotal,    // 告警 IP 数
            data          = packages.info;    // 列表具体信息
		var keyPool       = [];    // 页面表头项的属性池
		var tableAll      = $('.all-info table'),
            tableAlert    = $('.alert-info table');

		// 把上次的 tbody 设置为空
		tableAll.find("tbody").empty();
		tableAlert.find("tbody").empty();
        // 修改页面的文本，在页面上显示注册的 IP 和告警 IP
        $('#J_enrolled').text(lang.t("注册的IP个数：") + registerTotal);
        $('#J_warned').text(lang.t("告警的IP个数：") + alarmTotal);
        if (alarmTotal) {
            $('.alert-info').removeClass('hidden');
            $('.all-info').addClass('hidden');
            $('#J_alertInfoBtn').toggleClass('btn-dft').toggleClass('add-btn');
            $('#J_allInfoBtn').toggleClass('add-btn').toggleClass('btn-dft');
        }

		setDataType(tableAll);
		setDataType(tableAlert);

		tableAll.find('th').each(function() {
			// 池中存放表头每项的属性值或 "-"
			var istKey = $(this).attr("data-key");

			keyPool.push(istKey ? istKey : "-");
		});

		$(data).each(function() {
            // 对与表中每条数据项，都在左侧加一个选择框
			var tr = $('<tr class="tr"></tr>');
			var sel = '<td class="td hd">'
                    + '<input class="tbl-ckb" type="checkbox" value="">'
                    + '</td>';
            $(sel).appendTo(tr);

			// 因为表头第一项是全选/反选所以从 1 开始计数
			for (var i = 1; i < keyPool.length - 1; i++) {
				var newKey  = keyPool[i],    // 表中的 td 元素
                    redFlag = this.redFlag,
                    htmlAdd,    // 存放被添加的 tr 元素
					_htmlAdd_;

                if (1 == redFlag) {    // 红色标记用
                    htmlAdd = '<td class="td hd alerton" title="';
					_htmlAdd_ = '">';
                } else {
                    tr.addClass('alertoff');
                    htmlAdd = '<td class="td hd" title="';
					_htmlAdd_ = '">';
                }

				// 这里做一个从表头属性到传过来的 data 的映射
				// 每行只显示和表头属性一致的的传过来的数据的值
                htmlAdd = htmlAdd
					+ (this[newKey] || this[newKey] == 0 ? this[newKey] : "-")
					+ _htmlAdd_
                    + (this[newKey] || this[newKey] == 0 ? this[newKey] : "-")
                    + '</td>';
                $(htmlAdd).appendTo(tr);
			}

			// 操作
			var tdTop     = '<td class="td hd">',
			    delListen = '<a class="del-btn btn-green" title="' + lang.t("删除监控") + '" href="#">' + lang.t("删除监控") + '</a>',
			    offAlert  = '<a class="fbd-btn btn-green" title="' + lang.t("禁止告警") + '" href="#">' + lang.t("禁止告警") + '</a>',
			    onAlert   = '<a class="on-btn btn-green" title="' + lang.t("开启告警") + '" href="#">' + lang.t("开启告警") + '</a>',
			    tdBottom  = '</td>';

			var opAdd = tdTop
					+ delListen
					+ ($(this)[0]['forbiddenFlag'] == 1 ? onAlert: offAlert)
					+ tdBottom;
            $(opAdd).appendTo(tr);
			tr.appendTo(tableAll.find('tbody'));
		});

        // clone 上一张表的 tbody 中非红色标记的 tr
        $('.all-info tbody tr').each(function () {
            if (!$(this).hasClass('alertoff'))
                $(this).clone(true).appendTo(tableAlert.find('tbody'));
        });
    }


	/**
	 * @brief: 对 IP 禁止告警
	 * @note: fbd_flag 为 1 代表禁止告警 ，禁止告警时需要填写禁止时长
	 * @param: NULL
	 * @return: { boolean }
	 */
    function _addBtnWoff() {
		$(".fbd-btn").on("click", function() {
			var t = $(this);
			FIELDSET = new tm.form.fieldSet($.extend({}, _getOptions()));
			if (!FBDDIALOG) {
				FBDDIALOG = $.Dlg.Util.popup({
					id: "J_ipAltOff",
					title: lang.t("禁止告警"),
					contentHtml: $("<form>").append(FIELDSET.getElement())
				});
			}
			FBDDIALOG.setSaveHandler(function() {
				var param = $("#J_ipAltOff").find("form").formToArray();
				if (isNumber(param[0].value)) {
					ajaxData(getUrl("gameanalysis/site", "backend", "banWarnForIp"), {
						ban_type: _getCurAsideKey(),
						fbd_flag: 1,      // 代表禁止告警
						minutes: param[0].value,
						ip: [_getCurIp(t)]
					}, function() {
						location.reload();
					}, "POST");
				} else {
					say(lang.t("请输入正确格式的告警时长"));
				}
			});
			FBDDIALOG.show();
			return false;
		});
    }


	/**
	 * @brief: 对 IP 开启告警
	 * @note: fbd_flag 为 0 代表开启告警 ，开启告警时不需要填写禁止时长
	 * @param: NULL
	 * @return: NULL
	 */
    function _addBtnWon() {
		$(".on-btn").on("click", function() {
			var t = $(this);
			ajaxData(getUrl("gameanalysis/site", "backend", "banWarnForIp"), {
				ban_type: _getCurAsideKey(),
				fbd_flag: 0,      // 代表开启告警
				ip: [_getCurIp(t)]
			}, function() {
				location.reload();
			}, "POST");
		});
    }


    /**
     * @brief: 搜索
     * @param: NULL
     * @return: NULL
     */
    function _addSearch() {
		/**
		 * @brief: IP 搜索插件
		 * @param: NULL
		 * @return: NULL
		 */
		$("#J_tableContainer").find(".all-info").datatable({
			searchEnabled: true,
			searchContainer: $(".all-info .search-wrapper")
		});
		$("#J_tableContainer").find(".alert-info").datatable({
			searchEnabled: true,
			searchContainer: $(".alert-info .search-wrapper")
		});
    }


	/**
	 * @brief: 删除监控按钮
	 * @param: NULL
	 * @return: { boolean }
	 */
    function _addBtnDel() {
		$(".del-btn").on("click", function() {
			var t = $(this);
			if(!DELDIALOG) {
				DELDIALOG = $.Dlg.Util.popup({
					id: "J_delListen",
					title: lang.t("删除监控"),
					contentHtml: lang.t("确定删除监控？")
				});
			}
			DELDIALOG.setSaveHandler(function() {
				ajaxData(getUrl("gameanalysis/site",
								"backend",
								"deleteMonitor"), {
									delete_type: _getCurAsideKey(),
									ip: [_getCurIp(t)]
								}, function() {
									t.closest("tr").remove();
                                    location.reload();
								}, "POST");
			});
			DELDIALOG.show();
			return false;
		});
    }


	/**
	 * @brief: 得到侧边栏选中的选项值
	 * @note: 根据协议向后端传递数据，后端根据数据发送 Ajax 请求
	 * @param: NULL
	 * @return: { int }
	 */
	function _getCurAsideKey() {
		var key     = $('#J_aside').find(".child.cur:eq(0)").attr("data-key"),
			ptl_num = -1;    // return -1 by default

		switch (key) {    // TODO: switch 待优化
		case 'statclient': ptl_num = 0; break;
		case 'statserver': ptl_num = 1; break;
		case 'dbserver': ptl_num = 2; break;
		case 'configserver': ptl_num = 3; break;
		case 'statredis': ptl_num = 4; break;
		case 'statnamenode': ptl_num = 5; break;
		case 'statjobtracker': ptl_num = 6; break;
		case 'statdatanode': ptl_num = 7; break;
		case 'stattasktracker': ptl_num = 8; break;
		default: return ptl_num;
		}

		return ptl_num;
	}


	/**
	 * @brief: 得到目前鼠标选中选项对应的IP值
	 * @note: 根据协议向后端传递数据，后端根据数据发送 Ajax 请求
	 * @param: { String }
	 * @return: { String }
	 */
	function _getCurIp(cur) {
		return key = cur.closest("tr").find(".td.hd:eq(1)").text();
	}


	/**
	 * @brief: 弹出对话框的信息
	 * @param: NULL
	 * @return: NULL
	 */
	function _getOptions() {
		return {
			items: [{
				label: {
					title: lang.t("禁止告警时长（分钟）"),
					className: "title-inline"
				},
				items: [{
					type: "text",
					name: "fbd-time",
					className: "ipttxt"
				}]
			}]
		};
	}


	/**
	 * @brief: 对 ajax 方法的一次封装
	 * @note: 增加了对 ajax 方法中第三个函数参数返回值的检测
	 *        用于错误处理
	 * @param: { String, Object, Function, String }
	 * @return: NULL
	 */
	function ajaxData(url, param, fn, type) {
		overlayer({ text: lang.t("加载中")});
		ajax(url, param, function (res) {
			if (res.result == 0) {
				hidelayer(lang.t("加载成功"));
				if (fn)
					fn(res.data);
			} else {
				hidelayer(lang.t("出错了"));
				say(lang.t("获取数据错误：") + res.err_desc);
			}
		}, type);
	}


	/**
	 * @brief: 设置 data-type 属性的值
	 * @param: { DOM }
	 * @return: NULL
	 */
	function setDataType(table) {
		table.find('th').each(function () {
			var istKey = $(this).attr("data-key");

			if (inArray(istKey, [ "workplaceSize",
								  "inboxFileSize",
								  "outboxFileSize",
								  "sentFileSize",
								  "configuredSize",
								  "presentSize",
								  "dfsRemainingSize",
								  "dfsUsedSize" ])) {
                $(this).attr({ 'data-type': 'binary' });
            } else if (inArray(istKey, [ "port",
										 "inboxFileCount",
										 "outboxFileCount",
										 "sentFileCount",
										 "mapsRunning",
										 "reduceRunning",
										 "mapTaskSlots",
										 "reduceTaskSlots",
										 "TaskCompleted",
										 "underReplicatedBlocks",
										 "missingBlocks",
										 "totalDatanodes",
										 "liveNodes",
										 "deadNodes",
										 "activeTaskTrackers",
										 "blackListedTaskTrackers",
										 "runningMapTasks",
										 "maxMapTasks",
										 "runningReduceTasks",
										 "maxReduceTasks",
										 "failed",
										 "killed",
										 "prep",
										 "running" ])) {
                $(this).attr({ 'data-type': 'number' });
            } else if (inArray(istKey, ["dfsUsedPercent", "maxDfsUsedPercent"])) {
                $(this).attr({ 'data-type': 'percentage' });
            }
		});
	}


	/**
	 * @brief: 用户输入是否符合正整数形式
	 * @param: { String }
	 * @return: { boolean }
	 */
	function isNumber(s) {
		var regu = "^[0-9]+$";
		var re   = new RegExp(regu);

		if (s.search(re) != - 1)
			return true;
		else
			return false;
	};
})(jQuery);
