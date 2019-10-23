/**
 * @fileOverview 事务控件
 * @name trsnwidget.js
 * @author Maverikc
 */
$(document).ready(function () {
	var tp = $('.transaction-panel');
	_addTrsn();
	_delTrsn(tp);
});

var _checkTrsn = function () {
	var tlen = $('.transaction-panel').length,
		rules = parseInt($('.rules-config').attr('data-id')),
		submit = $('.transaction-submit');
	if (tlen > 1) {
		if (tlen === rules) { $('.transaction-add').hide(); }
		else if (tlen < rules) {
			$('.transaction-add').show();
		} else if (tlen > rules) {
			$('.transaction-add').hide();
			if (rules === 3)
				$('.transaction-panel:gt(2)').remove();
			if (rules == 5)
				$('.transaction-panel:gt(9)').remove();
		}
		$('.panel-close').show();
		submit.show();

	} else {
		$('.panel-close').hide();
		(rules === 3 && tlen === 1 ) ? submit.hide() : submit.show();
	}
};

var _delTrsn = function (t) {
	t.find('.panel-close').click(function () {
		var tp = $(this).parent();
		tp.remove();
		_checkTrsn();
	});
};

var _addTrsn = function () {
	var newTrsn = "<div class='transaction-panel'>"
	        + "<div class='report-conf' data-id='report'></div>"
			+ "<div class='rId-conf' data-id='nil'></div>"
			+ "<div class='dataId-conf' data-id='nil'></div>"
			+ "<input type='hidden' class='time-from' value='" + $("#J_from").val() + "' />"
			+ "<input type='hidden' class='time-to' value='" + $("#J_to").val() + "' />"
			+ "<div class='panel-close'>"
			+ "<div class='icon'>"
			+ "<div class='icon-pclose'></div>"
			+ "</div>"
			+ "</div>"
			+ "<div class='report-ins'>"
			+ "<div class='node-level'>"
			+ "<div class='widget-sel' id='J_selNode'>"
			+ "<span class='sel-stid'>" + lang.t("选择事件：") + "</span>"
			+ "<div class='sel-wrapper'></div>"
			+ "</div>"
		    + "</div>"
			+ "</div>"
			+ "<div class='time-ins'>"
			+ "<div class='tag-panel'>"
			+ "<div class='time-txt'>"
			+ "<span class='txt-ins'>" + lang.t("时间：") + "</span>"
			+ "</div>"
			+ "<div class='time-tag cur' title='最多 5 个时间段'>"
			+ "<div class='tag-icon'>"
			+ "<div class='icon'>"
			+ "<div class='icon-timetag'></div>"
			+ "</div>"
			+ "</div>"
			+ "<div class='tag-txt'>"
			+ "<span>" + $("#J_from").val() + "~" + $("#J_to").val() + "</span>"
			+ "</div>"
			+ "</div>"
			+ "<div class='tag-con' title='最多 5 个时间段'>"
			+ "<div class='tag-add'>"
			+ "<div class='icon'>"
			+ "<div class='icon-timeadd'></div>"
			+ "</div>"
		    + "</div>"
		    + "</div>"
			+ "</div>"
			+ "<div class='calendar' title='最多 5 个时间段'>"
			+ "<div class='widget-sel'>"
			+ "<div class='sel-wrapper'>"
			+ "<div class='datepicker-trigger radius5-all fr calendar-widget'>"
			+ "<i class='datepicker-icon'></i>"
			+ "<input class='title' type='text' value=" + $("#J_from").val() + "~" + $("#J_to").val() + " />"
			+ "<i class='datepicker-arrow'></i>"
			+ "</div>"
			+ "</div>"
			+ "</div>"
			+ "</div>"
			+ "</div>"
			+ "</div>";
	$('.transaction-add').click(function () {
		if ($('.transaction-panel').length < parseInt($('.rules-config').attr('data-id'))) {
			$(newTrsn).insertAfter($('.transaction-panel').last());
			var newt = $('.transaction-panel').last();
			_createByNodeId(newt, {
				'game_id': parseInt($('.gameid-config').attr('data-id')),
				'parent_id': 0
			}, [ 'node_id', 'node_name', 'is_leaf' ]);									
			_createTime(newt);
			_delTrsn(newt);
			_checkTrsn();
			_iconHover();
		}
	});
};
