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
    }
}

/**
 * @brief detailEvent
 * [鲸鱼用户] => 详情 功能
 * @param data
 *
 * @return
 */
function detailEvent(data) {
    return $(document.createElement("a")).addClass("mod-trend mr5").attr({
        "account_id": data.account_id,
    }).text(lang.t("详情")).click(function(){
        var t = $(this),
            pTr = t.closest('tr');
        if (t.hasClass("clicked")) {
            t.removeClass("clicked").text(lang.t("详情"));
            pTr.removeClass("cur");
            pTr.next().filter(".mod-tr-con").remove();
        } else {
            t.addClass("clicked").text(lang.t("收起"));
            pTr.addClass("cur");
            var newTr = $(), newTd = $();
            if(pTr.next().hasClass("mod-tr-con")){
                newTr = pTr.next();
                newTd = newTr.find("td:eq(0)").empty();
            } else {
                newTr = $(document.createElement("tr")).addClass("mod-tr-con");
                newTd = $(document.createElement("td")).addClass("td").attr("colspan", pTr.find("td").length).appendTo(newTr);
                pTr.after(newTr);
            }
            fac(_getDetailConfig(newTd, t.closest("tr").find("td:eq(2)").text()));
        }
    });
}

function _getDetailConfig(container, id) {
    return [{
        type: "wrap",
        container: container,
        title: id,
        headEnabled: false,
        bottomEnabled: false,
        child: [{
            type: "data",
            url: {
                extend: ["", "index.php?r=gameanalysis/mobilegame/whaleuser/getInfo&account_id=" + id],
                page: function() {
                    return getPageParam();
                }
            },
            child: [{
                type: "table",
                hide: false,
                sortable: false,
                thead: [{
                    type: "string", title: lang.t("按条付费信息")
                }, {
                    type: "string", title: lang.t("值")
                }, {
                    type: "string", title: lang.t("包月付费信息")
                }, {
                    type: "string", title: lang.t("值")
                }],
                prepareData: "prepareTableDetailData"
            }]
        }]
    }];
}
window.prepareTableDetailData = function(data) {
    var buyitem = [{ key: "first_buyitem_time", title: lang.t("首次按条付费日期") }, {
        key: "last_buyitem_time", title: lang.t("最后按条付费日期") }, {
        key: "buyitem_total_amount", title: lang.t("累计按条付费总额") }, {
        key: "buyitem_total_count", title: lang.t("累计按条付费次数") }, {
        key: "consume_golds", title: lang.t("游戏币消耗量") }, {
        key: "left_golds", title: lang.t("游戏币存量") }];
    var vip = [{ key: "vip", title: lang.t("当前是否VIP") }, {
        key: "first_vip_time", title: lang.t("首次包月日期") }, {
        key: "last_vip_time", title: lang.t("最后包月日期") }, {
        key: "vip_total_amount", title: lang.t("累计包月总额") }, {
        key: "vip_total_count", title: lang.t("累计包月次数") },{
        key: "last_login_time", title: lang.t("最后登录日期") }];
    var rlt = [];
    $.each(buyitem, function(i){
        var arr = [this.title, data.data[this.key]];
        arr.push(vip[i] ? vip[i].title : "");
        arr.push(vip[i] ? data.data[vip[i].key] : "");
        rlt.push(arr);
    });
    return rlt;
};
