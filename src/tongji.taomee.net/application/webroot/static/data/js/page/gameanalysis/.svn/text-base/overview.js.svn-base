var MODULE = [];
var pageTabs = $();
$(function(){
    _gProgressData();
    _gTools();
    _showViewData();
    gModule();
});
/**
 * @brief _gProgressData
 * 服务器数据运算进度
 *
 * @return
 */
function _gProgressData() {
    var r = "gameanalysis/" + ($("#J_paramGameType").val() ? $("#J_paramGameType").val() : "webgame");
    ajax(getUrl(r, "Overview", "isTaskComplete"), {
        game_id: $("#J_paramGameId").val()
    }, function(res) {
        var text = lang.t("已完成");
        if(!res.data){
            text = lang.t("正在加工中...");
            $("#J_progress").find(".progress-text").addClass("warning");
        }
        $("#J_progress").find(".progress-text").text(text);
    });
}
/**
 * @brief _g_tools
 * 有关页面首部工具区功能
 * 时间功能 平台选择功能 区服功能
 * @return
 */
function _gTools(){
    //time
    var $from = $("#J_from"),
        $to = $("#J_to"),
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
                _handle_show_time( userDate[0], userDate[1] );
                _refreshData();
            }
        }
    });
    $("#J_from_to").click(function(e){
        e.stopPropagation();
        $date.focus();
    });

    //single contrast date
    var $single = $("#J_single"),
        $singleFrom = $("#J_single_from"),
        $singleDate = $("#J_single_date");
    $singleDate.datepick({
        rangeSelect: false,
        monthsToShow: 1,
        monthsToStep: 1,
        monthsOffset: 0,
        onClose: function(userDate) {
            if( userDate.length && $.datepick.formatDate("yyyy-mm-dd", userDate[0]) != $singleFrom.val() ){
                $singleFrom.val( $singleDate.val() );
                _refreshData();
            }
        }
    });
    $single.click(function(e){
        e.stopPropagation();
        $singleDate.focus();
    });
    $("#J_contrast").click(function(){
        var $self = $(this);

        if ($self.attr("checked")) {
            $self.next().hide();
            $("#J_single").show();
        } else {
            $self.next().show();
            $("#J_single").hide();
        }
        _refreshData();
    });
}

/**
 * @brief _refreshData
 * 刷新数据
 */
function _refreshData(){
    var modules = $("#J_contentBody").data("content-data");
    if (modules && modules.length) {
        $(modules).each(function() {
            this.refresh({
                dataChange: true
            });
        });
    }
    _showViewData();
}

/**
 * @brief showViewData
 * 获取viewdata数据
 * @param fn
 *
 * @return
 */
function _showViewData(){
    var r = "gameanalysis/" + ($("#J_paramGameType").val() ? $("#J_paramGameType").val() : "webgame"),
        params = {
            "from[0]" : $("#J_showFrom").val(),
            "to[0]" : $("#J_showTo").val(),
            contrast : $("#J_contrast").attr("checked") ? 1 : 0,
            gpzs_id : $("#J_gpzsId").val(),
            game_id : $("#J_paramGameId").val()
        };
    if (params.contrast) params["from[1]"] = $("#J_single_from").val();
    ajax(getUrl(r, "overview", "data"), params, function(res){
        if (res.result == 0) {
            _viewDataFac(res.data);
        } else {
            say(lang.t("获取数据错误：") + res.err_desc);
        }
    });
}
/**
 * @brief _viewDataFac
 * 更新viewdata数据
 * @param data
 * @return
 */
function _viewDataFac(data){
    var $viewData = $("#J_viewData"),
        html = '',
        len = data ? data.length : 0;
    if(len){
        $.each( data, function(i){
            html += '<li class="view-data' + (i+1) + '">'
                + '<span class="view-txt">' + this.name + '</span>';
            var n = this.data.length;

            if( n ){
                if( n == 1 ){
                    html += '<strong class="imp" style="font-size: 24px;">'
                        + (i == 2 ? (this.data[0]).toString().addCommas(2) : (this.data[0]).toString().addCommas())
                        + '</strong>'
                } else {
                    for(var j = 0; j < len; j++ ){
                        if( j == 0 ){
                            html += '<strong class="imp">'
                                + (i == 2 ? (this.data[j]).toString().addCommas(2) : (this.data[j]).toString().addCommas())
                                + '</strong>';
                        } else if(j == 2) {
                            html += this.data[j] >= 0
                                ? '<strong class="up">+' + this.data[j] + '%</strong>'
                                : '<strong class="down">' + this.data[j] + '%</strong>';
                        } else {
                            html += '<strong>'
                                + (i==2 ? (this.data[j]).toString().addCommas(2) : (this.data[j]).toString().addCommas())
                                + '</strong>';
                        }
                    }
                }
            }
        });
    }
    $viewData.empty().html( html );
}

window.getPageParam = function(flag){
    var param = {
        "from[0]" : $("#J_showFrom").val(),
        "to[0]" : $("#J_showTo").val(),
        contrast : $("#J_contrast").attr("checked") ? 1 : 0,
        gpzs_id : $("#J_gpzsId").val(),
        game_id : $("#J_paramGameId").val()
    };
    if (param.contrast)
		param["from[1]"] = $("#J_single_from").val();
	if (flag === "download") {
		$.extend(param, {
			"from[0]" : $("#J_from").val(),
			"to[0]" : $("#J_to").val()
		});
	}
	
    return param;
};
