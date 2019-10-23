/**
 * @fileOverview 事件组件
 * @name timewidget.js
 * @author Maverick
 */
// 检查折行
var _checkAdd = function (t) {
    var tLen = t.width(),
        realTimeLen = t.find('.time-tag').length * (t.find('.time-tag').last().width() + 4),    // 4px extra
        minLen = t.find('.time-tag').last().width() + 4;    // 4px extra

    if (tLen - realTimeLen < t.find('.time-txt').width()) {
		t.find('.calendar').css({ 'margin': "20px 0px 0px 0px" });
		t.css({ 'padding': "0 0 40px 0" });
	} else {
		t.find('.calendar').css({ 'margin': "-10px 0px 0px 0px" });
		t.css({ 'padding': "0 0 10px 0" });
    }
};

var _addCur = function (t, tag) {
	t.find('.time-tag').each(function () {
		if ($(this).hasClass('cur'))
			$(this).removeClass('cur');
	});
	$(tag).addClass('cur');
};

// 添加新的标签
var _addTag = function (t, tseg) {
	var timeTag = "<div class='time-tag' title='最多 5 个时间段'>"
			+ "<div class='tag-icon'>"
			+ "<div class='icon'>"
			+ "<div class='icon-timetag'></div>"
			+ "</div>"
			+ "</div>"
			+ "<div class='tag-txt'>"
			+ "<span>" + tseg + "</span>"
			+ "</div>"
			+ "</div>";
	$(timeTag).insertBefore(t.find($('.tag-con')));
	t.find('.time-tag').last().click(function(e){
		// _dateBlur(t);
        e.stopPropagation();
		_addCur(t, this);
		_datepickTrigger(t);
    });	
	t.find('.tag-icon').click(function () {
		$(this).parent().remove();
		_checkCur(t);
		_checkAdd(t);
	});
};

// 触发时间控件选择时间
var _datepickTrigger = function (t) {
    var $from = t.find(".time-from"),
        $to = t.find(".time-to"),
        $date = t.find('input').last();
	
    $date.datepick({
        rangeSelect: true,
        monthsToShow: 2,
        monthsToStep: 2,
        monthsOffset: 2,
        shortCut : false,
        maxDate: new Date(),
		onShow: function () {
			var refreshTime = t.find('.time-ins').find('.cur').find('span').text();
			$date.val(refreshTime);
			// $('td').find('a').each(function () {
			// 	if ($(this).hasClass('.datepick-selected'))
			// 		$(this).removeClass('datepick-selected');
			// });
		},
        onClose: function(userDate) {
            if ( userDate.length )
			{
				var ifAdd = true;
				t.find('.time-tag').each(function () {
					var tStr = $(this).find('span').text(),
						nStr = $date.val(),
						fPre = $.trim(tStr.split("~")[0]),
						tPre = $.trim(tStr.split("~")[1]),
						fNow = $.trim(nStr.split("~")[0]),
						tNow = $.trim(nStr.split("~")[1]);
					if (fPre === fNow && tPre === tNow)
						ifAdd = false;
				});
				if (ifAdd && t.find('.time-tag').length < 10) {
					var userDate = $date.val().split("~");
					userDate[0] = $.trim(userDate[0]);
					userDate[1] = $.trim(userDate[1]);
					$from.val(userDate[0]);
					$to.val(userDate[1]);
					_handle_show_time( userDate[0], userDate[1] );
					t.find('.time-ins').find('.cur').find('span').text(userDate[0] + "~" + userDate[1]);
				}
            }
        }
    });
	
	$date.focus();
};

var _checkCur = function (t) {
	var curs = t.find('.cur').length;
	if (curs === 0) {
		t.find('.time-tag').last().addClass('cur');
	}
};

// var _dateBlur = function (t) {
// 	console.log("clicked");
// };

// 设置时间控件
var _createTime = function (t) {
	var $timeTag = t.find('.time-tag'),
		$timeAdd = t.find('.tag-con'),
		$date = t.find('input').last();

    $date.parent().click(function(e){
        e.stopPropagation();
		_datepickTrigger(t);
    });	
	$timeTag.click(function(e){
		// _dateBlur(t);
        e.stopPropagation();
		_addCur(t, this);
		_datepickTrigger(t);
    });
	$timeAdd.click(function () {    // 添加新的时间
		// _dateBlur(t);
		if (t.find('.time-tag').length < 5) {
			_addTag(t, $('#J_from').val() + "~" + $('#J_to').val());
			_checkAdd(t);
			t.find('.time-tag').each(function () {
				if ($(this).hasClass('cur'))
					$(this).removeClass('cur');
			});
			t.find('.time-tag').last().addClass('cur');
			_datepickTrigger(t);
		}
	});
	t.find('.tag-icon').click(function () {    // 删除标签
		$(this).parent().remove();
		_checkCur(t);
		_checkAdd(t);
	});
};
