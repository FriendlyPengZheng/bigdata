/**
 * @fileOverview 我的收藏部分的基礎頁面
 * @name basic.js
 * @author Maverick youngleemails@163.com
 */
var FAVOR = { index : 100, conCnt : 1 };
var DIALOG = { addCenter : null, updCenter : null, widget : null };

$(document).ready(function () {
	_gTools();
    _gFavor();
    _addPageStatListener();
    _statListen();
});

/**
 * 统计选择器选中的 DOM 元素的点击次数
 */
var _statListen = function () {
    $('.stat-listener').bind("click", function () {
        msglog($(this).attr('stid'),
               $(this).attr('sstid'),
               $(this).attr('item'));
    });
};

var bottomAction = function () {
	$('.mod-table').addClass('cur');
};

var _refresh_data = function() {    // 時間選定後刷新頁面數據
  $("#J_contentBody").find(".mod-box").each(function(i) {
    window.setTimeout((function(t){
      return function(){
        _widget_content_fac({
          collect_id : t.attr("data-id"),
          draw_type:  t.attr("data-type"),
          calc_option: t.attr("calc-type"),
          calculateRow_option: t.attr("calc-row-option")
        }, t.find(".mod-text-con").empty());
      };
    })($(this)), i * 10);
  });
};

var _gTools = function () {
	var _pluginTime = function() {
		var $from = $("#J_from"),
			$to = $("#J_to"),
			$date = $("#J_date");

		$date.datepick({
			rangeSelect: true,    // 允许时间控件选择一段时间
			monthsToShow: 3,    // 时间控件点击后显示 3 个可选月份
			monthsToStep: 3,    // 点击上月和下月后移动的月分数
			monthsOffset: 2,    // 月份偏移量为 2
			shortCut : true,    // 时间选择快捷键
			maxDate: new Date(),    // 最大可选时间是当前时间
			// 当时间控件被关闭后的动作
			onClose: function(userDate) {
				//判断是否是同一时间
				if(userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != $from.val()
									   || $.datepick.formatDate("yyyy-mm-dd", userDate[1]) != $to.val()) ){
					var userDate = $date.val().split("~");
					userDate[0] = $.trim(userDate[0]);
					userDate[1] = $.trim(userDate[1]);
					$from.val(userDate[0]);
					$to.val(userDate[1]);
					_handle_show_time( userDate[0], userDate[1] );
					_refresh_data();
				}
			}
		});
		$("#J_from_to").click(function(e) {    // 在頁面上選擇時間
			e.stopPropagation();
			$date.focus();
		});
	};

	var _widgetExport = function() {
		$("#J_download").click(function(e) {
			e.stopPropagation();
			$.download(getUrl("home", "favor", "export"), $.extend(getDownloadParameters(), {
				favor_id : $("#J_gFavorId").val(),
				file_name: $("#J_gFavorName").val()
			}));
		});
	};
	
	var _leftTree = function() {
		$("#J_aside").find("ul:eq(0) li").droppable({    // 左樹列表
			// droppable 的選項
			tolerance : "pointer",    // 用鼠標指針去判斷是否進入了可放置區域
			// 事件
			over: function (event, ui) {    // 鼠標進入放置區域觸發
				var target = $(event.target);
				if ( _isAllow(target) ) {
					$(ui.helper).find(".mod-title").after(
						$(document.createElement("span"))
							.addClass("favor-helper")
							.text( "(" + lang.t("移动到：") + target.text() + ")" ));
					target.css( { "border-left" : "5px solid #61cc49" });    // 可以移動的標記爲綠色
				} else {
					$(ui.helper).find(".favor-helper").remove();
					target.css( { "border-left" : "5px solid red" });    // 不可以移動的標記爲紅色
				}
			},
			out: function(event, ui) {    // 鼠標離開放置區域觸發
				$(ui.helper).find(".favor-helper").remove();
				$(event.target).css({ "border-left" : "0px" });
			},
			drop: function(event, ui) {
				var target = $(event.target);
				target.css({ "border-left" : "0px" });
				if (_isAllow(target)) {
					ajaxData(getUrl("home", "collect", "move"), {
						collect_id: $(ui.draggable).attr("data-id"),
						favor_id: target.attr("data-id")
					}, function(){
						$(ui.draggable).fadeOut( 1500, function(){
							$(ui.draggable).remove();
						});
					}, true);
				}
			}
		});
	};

	var _isAllow = function(target) {
		var favorType = $("#J_gFavorType").val();
		if(target.hasClass("cur")) {
			return false;
		} else {
			return favorType == target.attr("data-type") ? ( favorType == 1 ? ($("#J_gGameId").val() == target.attr("data-game") ? true : false ) : true ) : false;
		}
	};

	_pluginTime();
	_widgetExport();
	_leftTree();
};

var _gFavor = function () {
	$('.favor-title .glyphName').click(function () {
		var oldVal = $(this).text();
		$(this).blur(function () {
			if ($(this).text() !== oldVal) {
				ajaxData(getUrl("home","favor","set"), {
					"favor_id": $("#J_gFavorId").val(),
					"layout": "1",
					"favor_type": $("#J_gFavorType").val(),
					"favor_name": $(this).text(),
					"game_id": $("#J_gGameId").val() ? $("#J_gGameId").val() : 0
				}, function (data) {
					if (!data) { say(lang.t("我的收藏名称修改失败")); }
					_refresh_page();
				}, true);
				oldVal = $(this).text();
			} else {
				
			}
		});
	});
	
    $("#J_delCenterBtn").click(function(e) {
		e.stopPropagation();
		$.Dlg.Util.confirm(lang.t("确定删除我的收藏？"), lang.t("删除此我的收藏后将从系统中永久消失。"), function() {
			ajaxData(getUrl("home", "Favor", "delete"), {
				"favor_id": $("#J_gFavorId").val()
			}, function(){
				_refresh_page();
			}, true);
		});
	});
    
    $("#J_dftCenterBtn").click(function(e) {
		e.stopPropagation();
		var t = $(this);
		if( t.attr("data-type") == 1 ) {    // 根據 data-type 確定是否爲默認收藏
			ajaxData(getUrl("home", "Favor", "cancelDefault"),{ "favor_id" : $("#J_gFavorId").val() }, function() {
				t.attr({
					"data-type": 0,
					"title" : lang.t("设置首次进入页面默认收藏")
				}).text(lang.t("设置为默认收藏"));
			});
		} else {
			ajaxData(getUrl("home", "Favor", "setDefault"),{ "favor_id" : $("#J_gFavorId").val() }, function() {
				t.attr({
					"data-type": 1 ,
					"title" : lang.t("取消设置首次进入页面默认收藏")
				}).text(lang.t("取消默认收藏"));
			});
		}
	});

    $("#J_sharedCenterBtn").on("click", function (e) {
		e.stopPropagation();
        _shared_favor($("#J_gFavorId").val());
    });
};

/**
 * @brief _refresh_page
 * 刷新页面
 * @return
 */
var _refresh_page = function() {
    go(getUrl("home", "Favor", "index")
       + "&favor_id=" + $("#J_gFavorId").val()
       + "&from=" + $("#J_showFrom").val()
       + "&to=" + $("#J_showTo").val());
};

/**
 * 檢查是否需要添加當前的收藏
 * @param $obj(newCenter,updCenter)
 * @return ok
 */
var _check_center  = function(center) {
    var ok = true;
    center.find('.necessary').each(function() {
        var t = $(this);
        if (!t.val()) {
			t.hint(); ok = false;
			return false;
		}
    });
    if (center.find('input[name="favor_type"]:checked').val()  == 1) {
        var selectM = $("#J_centerItem").find(".select-m");
        if( !selectM.hasClass("selected-m") ){
            selectM.parent().hint();
            return false;
        }
    }
    return ok;
};

/**
 * @brief _layout_style
 * 我的收藏布局 click 事件
 * @param t
 */
function _layout_style(t) {
    if ( !t.hasClass("layout-style-coned") ) {
        t.siblings().removeClass("layout-style-coned");
        t.addClass("layout-style-coned");
    }
}

/**
 * Sub the recordNum
 */
function _handle_record( selCon, className ) {
    var select = selCon.find(".select-m"),
        tName = select.find(".title-m .t-name"),
        recordNum = $("body").data(className);

    recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
    if( select.hasClass("selected-m") && select.hasClass(className) ){
        if( tName.attr("data-child") ){
            var childrenLen = selCon.find(".sel-ul .act-li").length;
            childrenLen = childrenLen ? childrenLen : 1;
            recordNum -= childrenLen;
        }else{
            recordNum -= 1;
        }
    }
    $("body").data( className, recordNum );
}
/**
 * 选择布局con
 */
function _get_widget_con() {
    var widgetCon = $("#J_contentBody").find(".window-layout-container");

    if (FAVOR.conCnt%2){ //1
        FAVOR.conCnt++;
        return widgetCon.first();
    } else { //0
        FAVOR.conCnt++;
        return widgetCon.last();
    }
}

/**
 * 增加的 stid, sstid, item 属性做内部统计
 */
function _addPageStatListener() {
    $('#J_dftCenterBtn').addClass("stat-listener").attr({
        'stid': '我的收藏',
        'sstid': '页面收藏选项',
        'item': '设为/取消默认收藏主页'
    });
    $('#J_download').addClass("stat-listener").attr({
        'stid': '我的收藏',
        'sstid': '页面收藏选项',
        'item': '数据批量下载'
    });
    $('#J_delCenterBtn').addClass("stat-listener").attr({
        'stid': '我的收藏',
        'sstid': '页面收藏选项',
        'item': '删除我的收藏'
    });
}

var _shared_favor = (function () {

    var favorId;

    var html = '<div class="widget-sel">'
        +   '<div class="widget-sel" id="J_sharedFavor">'
        +       '<span class="sel-title">' + lang.t("选择分享对象：") + '</span>'
		+   '<div class="sel-wrapper"></div>'    // 这里会在后面的逻辑添加选择列表
        +   '</div>';
    var widget = $.Dlg.Util.popup({
        title: lang.t("分享"),
        contentHtml: html,
        saveCallback: function(){
            var seled = $("#J_sharedFavor").find('.sel-wrapper').find('.selected-m'),
                users;
            if (seled.length) {
                users = seled.find(".title-m .t-name").attr("data-id");
            } else {
                seled.hint(); 
                return;
            }
            ajaxData(getUrl("home", "favor", "share"), {
                favor_id: favorId,
                users: users
            }, function(data) {
                say(lang.t("分享成功"), true);
                widget.hide();
            });
        }
    });

    var shared = $("#J_sharedFavor");
        selWrap = shared.find(".sel-wrapper").empty(),
        selCon = $(document.createElement("div")).addClass("sel-con")
			.css({ 'position': 'relative', 'z-index': 1 }),
        selP = $(document.createElement("div")).addClass("sel-p");
    var option = {
        type: 5,
        search: true,
        data: [],
        obj: selP
    };

    var _handleForChoose = function(data) {
        var rlt = [];
        if(data && data.length) {
            $.each(data, function(){
                rlt.push({
                    title: this.user_name,
                    attr: { id: this.user_id }
                });
            });
        }
        return rlt;
    };

    ajaxData(getUrl("home", "collect", "fetchSharedUsers"), {}, function(data){
        data = _handleForChoose(data.data);
        option.data = data;
        $.choose.core(option);
    }, true);

    selP.appendTo(selCon.appendTo(selWrap));

    return function(id) {
        favorId = id;
        widget.show();
    };

})();

/**
 * @brief _shared_widget
 * 分享小窗口信息
 * @param r：每个模块对象
 * @return
 */
var _shared_widget = (function () {

    var collectId;

    var html = '<div class="widget-sel">'
        +   '<div class="widget-sel" id="J_shared">'
        +       '<span class="sel-title">' + lang.t("选择分享对象：") + '</span>'
		+   '<div class="sel-wrapper"></div>'    // 这里会在后面的逻辑添加选择列表
        +   '</div>';
    var widget = $.Dlg.Util.popup({
        title: lang.t("分享"),
        contentHtml: html,
        saveCallback: function(){
            var seled = $("#J_shared").find('.sel-wrapper').find('.selected-m'),
                users;
            if (seled.length) {
                users = seled.find(".title-m .t-name").attr("data-id");
            } else {
                seled.hint(); 
                return;
            }
            ajaxData(getUrl("home", "collect", "share"), {
                collect_id: collectId,
                users: users
            }, function(data) {
                say(lang.t("分享成功"), true);
                widget.hide();
            });
        }
    });

    var shared = $("#J_shared");
        selWrap = shared.find(".sel-wrapper").empty(),
        selCon = $(document.createElement("div")).addClass("sel-con")
			.css({ 'position': 'relative', 'z-index': 1 }),
        selP = $(document.createElement("div")).addClass("sel-p");
    var option = {
        type: 5,
        search: true,
        data: [],
        obj: selP
    };

    var _handleForChoose = function(data) {
        var rlt = [];
        if(data && data.length) {
            $.each(data, function(){
                rlt.push({
                    title: this.user_name,
                    attr: { id: this.user_id }
                });
            });
        }
        return rlt;
    };

    ajaxData(getUrl("home", "collect", "fetchSharedUsers"), {}, function(data){
        data = _handleForChoose(data.data);
        option.data = data;
        $.choose.core(option);
    }, true);

    selP.appendTo(selCon.appendTo(selWrap));

    return function(r) {
        collectId = r.container.attr("data-id");
        widget.show();
    };

})();
