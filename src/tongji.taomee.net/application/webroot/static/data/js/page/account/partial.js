/**
 * @fileOverview 分游戏账号数据
 * @name partial.js
 * @author Maverick youngleemails@gmail.com 
 */
$(document).ready(function () {
	_gTools();
});

/**
 * 工具区
 */
var _gTools = function () {
    //time
    var $from = $("#J_from"),
        $to   = $("#J_to"),
        $date = $("#J_date");
	
	// select time
    $date.datepick({
        rangeSelect: true,
        monthsToShow: 3,
        monthsToStep: 3,
        monthsOffset: 2,
        shortCut : true,
        maxDate: new Date(),
        onClose: function(userDate) {
            //判断是否是同一时间
            if( userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != $from.val()
									|| $.datepick.formatDate("yyyy-mm-dd", userDate[1]) != $to.val()) ){
                var userDate = $date.val().split("~");
                userDate[0] = $.trim(userDate[0]);
                userDate[1] = $.trim(userDate[1]);
                $from.val(userDate[0]);
                $to.val(userDate[1]);
                _refreshData();
            }
        }
    });
    $("#J_from_to").click(function(e){
        $date.focus();
        e.stopPropagation();
    });

	// TODO: 此处需要通过请求 Ajax 数据来获得所选择的游戏
	ajaxData(getUrl("account", "partial", "getAccountGame"),
			 null,
			 function(data) {
				 _selectFac(_handleSelect(data), $("#J_transGame"));
				 gModule();
			 });
};

/**
 * 生成列表并绑定事件
 * @param {array} data 列表
 * @param {} container
 */
var _selectFac = function(data, container) {
    $.Select.setOptionContent(container, data);
    $.Select.bindEvents(container, false, function() {
        _refreshData();
    });
};

/**
 * 
 * @param {} data
 * @returns {} 
 */
var _handleSelect = function(data) {
    var rlt = [];
    $.each(data, function(index, val){
        rlt.push({
            id: (val.game_id + "|" + val.gpzs_id),
            name: val.game_name,
            selected: (index === 0 ? true : false)
        });
    });
    return rlt;
};


/**
 * 刷新数据
 */
var _refreshData = function() {
    var modules = $("#J_contentBody").data("content-data");
    if (modules && modules.length) {
        $(modules).each(function() {
            this.refresh({
                dataChange: true
            });
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
    if( hide ) {
		overlayer({ text: "加载中..."});
	}
    ajax(url, param, function(res) {
        if(res.result == 0) {
            if(hide) {
				hidelayer("加载成功~.~");
			}
            if(fn) {
				fn(res.data);
			}
        } else {
            if(hide) {
				hidelayer();
			}
            if(empt) {
                if(fn) {
					fn([]);
				}
            } else {
                say("获取数据错误：" + res.err_desc);
            }
        }
    }, "POST");
};

window.getPageParam = function() {
	var ids = $("#J_transGame").find(".selected-item").attr("data-id").split("|");
	var param = {
		"from": $('#J_from').val(),
		"to": $('#J_to').val(),
		"game_id": ids[0],
		"gpzs_id": ids[1]
    };
    return param;
};
