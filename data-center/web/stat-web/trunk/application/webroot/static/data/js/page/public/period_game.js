var MODULE = [];
var pageTabs = $();
$(function(){
    _gTools();
});
/**
 * @brief _gTools
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
        defaultDate: $date.val(),
        selectDefaultDate: true,
        rangeSeparator: "~",
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
        $date.focus();
        e.stopPropagation();
    });

    var selGame = $("#J_selGame");
    // 新 ajax 请求
    ajaxData(getUrl($('#J_gameListRoute').val()),
             {},
             function(data){
                 _selectFac(_handleSelect(data), selGame);
                 gModule();
             });
}
/**
 * @brief _selectFac
 * 生成列表并绑定事件
 * @param data array 列表
 * @return
 */
function _selectFac(data, container){
    $.Select.setOptionContent(container, data);
    $.Select.bindEvents(container, false, function(){
        _refreshData();
    });
}
function _handleSelect(data){
    var rlt = [];
    $.each(data, function(index, val){
        rlt.push({
            id: val.game_id + "-" + val.gpzs_id,
            name: val.game_name,
            selected: (index == 0 ? true : false)
        });
    });
    return rlt;
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
};

window.getPageParam = function(){
    var gpzs = $("#J_selGame").find(".selected-item").attr("data-id").split("-");
    return {
        "from[0]" : $("#J_from").val(),
        "to[0]" : $("#J_to").val(),
        "game_id": gpzs[0],
        "gpzs_id": gpzs[1]
    };
};

/**
 * @brief _getModuleKey
 * get module key
 * second_bar - aside_parent - aside_child
 * @return
 */
function _getModuleKey(){
    var parentKey = '',
        childKey = '';
    $("#J_aside").find(".cur").each(function(){
        var t = $(this);
        if(t.hasClass("parent")){
            parentKey = t.attr("data-key");
        } else if(t.hasClass("child")) {
            childKey = t.attr("data-key");
        }
    });
	var dataKeySegment = $("#J_header .main-sub-nav .cur").attr("data-key");
	if (dataKeySegment === undefined) {
		return _getTopBarKey() + "-" + parentKey + (childKey ? "-" + childKey : "");
	} else {
		return _getTopBarKey() + "-" + dataKeySegment + "-" + parentKey + (childKey ? "-" + childKey : "");		
	}
}
