$(function(){
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
                $from.val($.trim(userDate[0]));
                $to.val($.trim(userDate[1]));
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
    var transGame = $("#J_transGame");
    ajaxData(getUrl("topic", "cdntraffic", "getCdnGame"), null, function(data){
        _selectFac(_handleSelect(data), transGame);
        gModule();
    });
});

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
    var i = 0;
    $.each(data, function(index, val){
        rlt.push({
            id: index,
            name: val,
            selected: (i == 0 ? true : false)
        })
        i++;
    });
    return rlt;
}
/**
 * @brief ajaxData
 *
 * @param url
 * @param param
 * @param fn:回调函数
 * @param hide:是否显示overlayer
 * @param empt:发生请求错误时，是否say
 */
function ajaxData( url, param, fn, hide, empt ){
    if( hide ) overlayer({ text: lang.t("加载中...")});
    ajax( url, param, function(res){
        if(res.result == 0){
            if(hide) hidelayer(lang.t("加载成功~.~"));
            if(fn) fn(res.data);
        } else {
            if(hide) hidelayer();
            if(empt){
                if(fn)fn([]);
            } else {
                say(lang.t("获取数据错误：") + res.err_desc);
            }
        }
    }, "POST");
}
window.getPageParam = function(){
    var param = {
        "from[0]" : $("#J_from").val(),
        "to[0]" : $("#J_to").val(),
        "data_info[0][range]": $("#J_transGame").find(".selected-item").attr("data-id"),
        "contrast": $("#J_contrast").attr("checked") ? 1 : 0
    };
    if(param.contrast == 1) param["from[1]"] = $("#J_single_from").val();
    return param;
}

/**
 * @brief function prepareTableData
 * for tm.module.js
 * 处理table显示数据格式
 * @param option
 *
 * @return
 */
window.prepareTableData = function(option){
    var data = option.data;
    if(option.hugeTable){
        var config = {};
        if(option.theadAvg) config.avg = true;
        if(option.average) config.average = option.average;
        return tmtool.handleHugeTableData(data[0].data, config, option.average ? data[0].average.data : []);
    } else {
        var rlt = [], average = [lang.t("均值")];
        $.each(data, function(m){
            var key = this.key ? this.key : [];
            if(key.length){
                if(option.average) average = average.concat(this.average.data);
                $(this.data).each(function(k){
                    var that = this;
                    $(this.data).each(function(i, value){
                        rlt[i] = $.isArray(rlt[i]) ? rlt[i] : [];
                        if(k == 0) rlt[i].push(key[i]);
                        rlt[i].push(value);
                        if(option.contrast) {
                            if(m != 0)rlt[i].push(that.contrast_rate && that.contrast_rate[i] ? that.contrast_rate[i] : '-');
                        } else {
                            if(option.percentage) {
                                rlt[i].push(that.percentage && that.percentage[i] ? that.percentage[i] : '-');
                            }
                            if(option.specialper){
                                rlt[i].push(that.specialper && that.specialper[i] ? that.specialper[i] : '-');
                            }
                            if(option.yoy) {
                                rlt[i].push(that.yoy && that.yoy[i] ? that.yoy[i] : '-');
                            }
                            if(option.qoq) {
                                rlt[i].push(that.qoq && that.qoq[i] ? that.qoq[i] : '-');
                            }
                        }
                    });
                });
            }
        });
        if(option.average) rlt.unshift(average);
        return rlt;
    }
}
window.getTheadByName = function(data, o){
    var arr = [];
    if(data){
        $(data).each(function(i){
            arr.push({ type: "date", title: lang.t("日期") });
            $(this.data).each(function(){
                arr.push({
                    type: "number",
                    title: this.name ? this.name : ""
                });
            });
            if(i == 1) arr.push({ type: "percentage", title: lang.t("变化率") });
        });
    }
    return arr;
}
