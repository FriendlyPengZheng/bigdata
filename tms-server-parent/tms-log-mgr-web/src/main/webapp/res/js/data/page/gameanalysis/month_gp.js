$(function(){
    $("#J_platform").tmselect().change(function(){
        _refreshData();
    });
    _gTools();
    gModule();
});
/**
 * @brief _gTools
 * 有关页面首部工具区功能
 * 时间功能 平台选择功能 区服功能
 * @return
 */
function _gTools(){
    var from = $("#J_from"),
        time = $("#J_date");
    time.datepick({
        changeYear: true,
        changeMonth: true,
        hideCalendar: true,
        dateFormat: 'yyyy-mm',
        maxDate: new Date(),
        onChangeMonthYear: function(year, month){
            if(year && month){
                time.val(year + '-' + $.date.parseFullMonth(month));
            }
        },
        onClose: function(userDate) {
            if(userDate.length && userDate[0].setMonth(userDate[0].getMonth() + 1) && $.datepick.formatDate("yyyy-mm", userDate[0]) != from.val()) {
                from.val(time.val());
                _refreshData();
            }
        }
    });
    $("#J_from_to").click(function(e){e.stopPropagation();
        time.focus();
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
};

window.getPageParam = function(){
    return {
        "from[0]" : $("#J_from").val(),
        "to[0]" : $("#J_from").val(),
        platform_id : $("#J_platform").find(":selected").attr("data-id"),
        game_id : $("#J_paramGameId").val()
    };
};
