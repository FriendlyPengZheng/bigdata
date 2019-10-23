    var selplat = null ;
var MULTIRELOAD = false;
$(function(){
    _gTools();
    _gModule();
});
/**
 * @brief _gModule
 * 根据配置生成页面
 * need json file
 * @return
 */
function _gModule(){
    $.getJSON("json/" + window.responseData.locale + "/gameanalysis/" + _getModuleKey() + ".json", null, function(data) {
        var prepared = [];
        $(data).each(function(i) {
            if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore)){
                this.container = $("#J_contentBody");
                if(i == 0 && this.type == "tabs" ){
                    this.tabsSkin = this.child[0] && this.child[0].tabsSkin
                        ? this.child[0].tabsSkin : this.tabsSkin;
                    if(this.tabsSkin) $("#J_contentHeader").css({'position' : 'absolute', 'top' : '10px', 'right' : '10px'});
                    this.tabsClick = function(t) {
                        if(t.attr("data-type") == "platform") {
                            MULTIRELOAD = false;
                            (selplat.tmselect("getSelectContainer")).hide();
                        } else if(t.attr("data-type") == "zoneserver") {
                            (selplat.tmselect("getSelectContainer")).show();
                            MULTIRELOAD = true;
                        }
                    };
                }
                prepared.push(this);
            }
        });
        $("#J_contentBody").data("content-data", fac(prepared));
    });
}
/**
 * @brief _gTools
 * 有关页面首部工具区功能
 * 时间功能 平台选择功能
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
                _handle_show_time(userDate[0], userDate[1]);
                _refreshData();
            }
        }
    });
    $("#J_from_to").click(function(e){
        $date.focus();
        e.stopPropagation();
    });

    selplat = $("#J_platform").tmselect();
    (selplat.tmselect("getSelectContainer")).hide();
    $("#J_platform").change(function(){
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
                multiReload: MULTIRELOAD,
                dataChange: true
            });
        });
    }
};

window.getPageParam = function(){
    var param = {
        "from[0]" : $("#J_showFrom").val(),
        "to[0]" : $("#J_showTo").val(),
        game_id : $("#J_paramGameId").val()
    };
    if((selplat.tmselect("getSelectContainer")).is(":visible")) {
        param.platform_id = $("#J_platform").find(":selected").attr("data-id");
    }
    return param;
}
