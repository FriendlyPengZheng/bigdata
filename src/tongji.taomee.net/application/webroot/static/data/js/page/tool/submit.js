/**
 * @fileOverview Submit a transaction
 * @name submit.js
 * @author Maverick
 */
$(document).ready(function () {
	var date = new Date();

	$('.transaction-submit').click(function () {
		var SUBMITDIALOG, FIELDSET;

		FIELDSET = new tm.form.fieldSet($.extend({}, _getOptions()));
		var param = {
			'game_id': $('#J_selGameList').find('.t-name').attr("data-id"),
			'gpzs_id': $('#J_selZoneServer').find('.t-name').attr("data-id"),
			'operation': undefined,    // setdiff  交集：intersect  并集：union
			'operands': [],
			'file_name': date.toLocaleString() + '的查询',    // default
			'filter_info': [1]
		};
		var rule = lang.t($('#J_selRules').find('.t-name').attr("title"));

		if (rule === lang.t("并集运算")) { param.operation = 'union'; }
		else if (rule === lang.t("交集运算")) { param.operation = 'intersect'; }
		else if (rule === lang.t("差集运算")) { param.operation = 'setdiff'; }
		
		$('.transaction-panel').each(function () {
			var oprand = {
				'type': $(this).find('.report-conf').attr("data-id"),
				'game_id': $('#J_selGameList').find('.t-name').attr("data-id"),
				'gpzs_id': $('#J_selZoneServer').find('.t-name').attr("data-id"),
				'r_id': parseInt($(this).find('.rId-conf').attr("data-id")),
				'periods': []
			};
			var tperiods = $(this).find('.time-tag');

			if ($(this).find('.dataId-conf').attr("data-id") !== 'nil'
				&& $(this).find('.report-level').find('.t-name').attr('data-id').split(':')[1] === "1")
				oprand.data_id = parseInt($(this).find('.dataId-conf').attr("data-id"));
			param.operands.push(oprand);
			
			// try {
			// 	if ($(this).find('.dataId-conf').attr("data-id") !== 'nil'
			// 		&& $(this).find('.report-level').find('.t-name').attr('data-id').split(':')[1] === "1")
			// 		oprand.data_id = parseInt($(this).find('.dataId-conf').attr("data-id"));
			// 	param.operands.push(oprand);
			// } catch (error) {
			// 	console.log('yes');
			// }
			
			tperiods.each(function () {
				var timep = $(this).find('span').text(),
					timeObject = {
						'from': timep.split("~")[0],
						'to': timep.split("~")[1]
					};
				oprand.periods.push(timeObject);
			});
		});

        if (!SUBMITDIALOG && infoChk()) {
			SUBMITDIALOG = $.Dlg.Util.popup({
				id: "J_setSubmitName",
				title: lang.t("自助查询（请到我的下载中下载本次查询）"),
				contentHtml: $("<form>").append(FIELDSET.getElement()),
				cancel: function () {
					param = {};
				}
			});
			SUBMITDIALOG.setSaveHandler(function () {
				if ($("#J_setSubmitName").find("form").find('input').val())
					param.file_name = $("#J_setSubmitName").find("form").find('input').val();
				ajaxData(getUrl("tool", "selfhelp", "add"),
						 param,
						 function(res) {
							 // go(getUrl("tool/file/index"));
							 // var dlg = $.Dlg.Util.message("温馨提示", "正在处理，请稍候...", "确定"),
							 // 	 msg = "";
							 // dlg.show();
							 // if (res.result === 0) {
							 // 	 if (res.data.code === 0) {
							 // 		 go(res.data.url);
							 // 		 return;
							 // 	 } else if (res.data.code === 1) {
							 // 		 msg = "由于您选择的时间间隔较长，已自动为您异步下载，"
							 // 			 + "您可以到页面右上角【我的下载】查看和下载！"
							 // 			 + "<a href='" + getUrl("tool/file/index")
							 // 			 + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
							 // 	 } else if (res.data.code === 2) {
							 // 		 msg = "文件正在处理，请耐心等待，您也可以到页面右上角【我的下载】查看！"
							 // 			 + "<a href='" + getUrl("tool/file/index")
							 // 			 + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
							 // 	 }
							 // } else {
							 // 	 msg = "下载错误：" + res.err_desc;
							 // }
							 // if (msg !== "") {
							 // 	 dlg.setConfirmHtml(msg);
							 // 	 dlg.getManager().setPosition(dlg);
							 // 	 dlg.show();
							 // }
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
        } else {
			var target = $('.select-m').not(':hidden').not('.selected-m'), 
				txt = target.parent().parent().parent().prev(),
				con = txt.parent();

			con.hint();
			$('.transaction-panel').each(function () {
				var ttag = $(this).find('.time-tag');
				if (ttag.length === 0) {
					$(this).find('.time-txt').hint();
					$(this).find('.calendar').hint();
				}
			});
		}
		
		return false;
	});

	var infoChk = function () {
		var undef = $('.select-m').not(':hidden').not('.selected-m').length,
			ifTag = true;

		$('.transaction-panel').each(function () {
			if ($(this).find('.time-tag').length === 0) {
				ifTag = false;
				return false;
			} else if ((!$('.report-level')) || (!$('.data-level'))) {
				ifTag = false;
				return false;
			}
			return true;
		});
		
		if (undef === 0 && ifTag === true) {
			return true;
		} else {
			return false;
		}
	};

	/**
	 * @brief: 弹出对话框的信息
	 * @param: NULL
	 * @return: NULL
	 */
	var _getOptions = function () {
		var date = new Date(),
			usrName = $('#J_header').find('.wrapper').find('.links').find('li').eq(0).text().split("，")[1],
			localString = date.toLocaleString(),
            dateInfo = date.toLocaleDateString().replace(/\//g, ''),
            timeInfo = date.toTimeString().replace(/\:/g, '_').split(' ')[0].slice(0, -3),
			dftTxt = usrName + '_' + dateInfo + '_' + timeInfo;
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
	};
	
	/**
	 * 对页面现有的信息进行自助查询
	 * @param { Object } param 自助数据查询 AJAX 请求所需参数
	 * @returns { Boolean } 自助数据查询结果
	 */
	var _selfhelpAdd = function (param) {
		var selfhelpResult = false;
		
		ajaxDataSync(getUrl("tool", "selfhelp", "add"),
					 param,
					 function (data) {
						 data === 0 ? selfhelpResult = true : selfhelpResult = false;
					 },
					 true);

		return selfhelpResult;
	};

	/**
	 * @brief ajaxData
	 *
	 * @param url
	 * @param param
	 * @param fn:回调函数
	 * @param hide:是否显示overlayer
	 * @param empt:发生请求错误时，是否say
	 */
	function ajaxData(url, param, fn, hide, empt) {
		if (hide) { overlayer({ text: lang.t("操作中") }); }
		
		ajax(url, param, function(res) {
			if(0 == res.result) {
				if (hide) { hidelayer(lang.t("操作成功~.~")); } 
				if (fn) { fn(res.data); } 
			} else {
				if (hide) { hidelayer(); } 
				if (empt) {
					if (fn) { fn([]); }
				} else { say(lang.t("获取数据错误：") + res.err_desc); }
			}
		}, "POST");
	}	
});
