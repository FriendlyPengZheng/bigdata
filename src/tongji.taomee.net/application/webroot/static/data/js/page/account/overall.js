/**
 * @fileOverview 总体账号数据
 * @name overall.js
 * @author Maverick youngleemails@gmail.com
 */
$(document).ready(function () {
	_gTools();
});


/**
 * 工具区
 */
var _gTools = function () {
    // Time
    var $from = $("#J_from"),
        $to   = $("#J_to"),
        $date = $("#J_date");

	// Select time
	$date.datepick({
        rangeSelect: true,
        monthsToShow: 3,
        monthsToStep: 3,
        monthsOffset: 2,
        shortCut : true,
        maxDate: new Date(),
        onClose: function(userDate) {
            if(userDate.length
			   && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) !== $from.val()
				   || $.datepick.formatDate("yyyy-mm-dd", userDate[1]) !== $to.val())) {
                var userDate = $date.val().split("~");
                $from.val($.trim(userDate[0]));
                $to.val($.trim(userDate[1]));
                _refreshData();
            }
        }
    });

	$('#J_from_to').on("click", function (event) {
		$date.focus();
		event.stopPropagation();
	});

    gModule();
};


/**
 * 刷新数据
 */
var _refreshData = function () {
    var modules = $("#J_contentBody").data("content-data");
    if (modules && modules.length) {
        $(modules).each(function () {
            this.refresh({ dataChange: true });
        });
    }
};


/**
 * 带有错误处理的 ajax 请求处理方法
 * @param { Function } url ajax 请求 URL
 * @param { Object } param ajax 请求数据
 * @param { Function } fn 回调函数
 * @param { Boolean } hide 是否显示 overlayer
 * @param { Boolean } empt 发生请求错误时，是否 say
 */
var ajaxData = function (url, param, fn, hide, empt) {
    if( hide ) { overlayer({ text: "加载中"}); }
    ajax(url, param, function(res) {
        if(res.result == 0) {
            if(hide) { hidelayer("加载成功"); }
            if(fn) { fn(res.data); }
        } else {
            if(hide) { hidelayer(); }
            if(empt) {
                if(fn) { fn([]); }
            } else {
				say("获取数据错误：" + res.err_desc);
			}
        }
    }, "POST");
};

/**
 * 从页面上获取游戏 ID 和区服列表
 * @returns {} 
 */
window.getPageParam = function() {
	var param = {
		"from": $('#J_from').val(),
		"to": $('#J_to').val(),
        "game_id": $('#J_gGameId').val(),
		"gpzs_id": $('#J_gGpzsId').val()
    };
    return param;
};
