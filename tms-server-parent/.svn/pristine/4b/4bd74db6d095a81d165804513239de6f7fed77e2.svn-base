/**
 * @fileOverview 隐藏功能
 * @name without_tools.js
 * @changelog Maverick 10.10.2014 增加了隐藏/显示全部任务功能
 */
$(document).ready(function () {
    _hideAll();
    _showAll();
});

/**
 * 隐藏全部任务
 */
var _hideAll = function () {
    $('#J_hideAll').on("click", function () {
        /*var r = "gameanalysis/" + ($("#J_paramGameType").val() ? $("#J_paramGameType").val() : "webgame"),*/
            ajaxURL = "../../mission/setHideAll";
        ajaxData(ajaxURL, $.extend({
            type : $('.tabs-active').attr('data-type'),
            hide : 1
        }, getPageParam()), function (data) {
            var _dataPanel = '#' + $('.tabs-active').attr('data-panel');
            $(_dataPanel).find('.mod-hide-chk').attr('checked', true);
        }, true);
    });
};

/**
 * 显示全部任务
 */
var _showAll = function () {
    $('#J_showAll').on("click", function () {
        /*var r = "gameanalysis/" + ($("#J_paramGameType").val() ? $("#J_paramGameType").val() : "webgame"),*/
            ajaxURL = "../../mission/setHideAll";
        ajaxData(ajaxURL, $.extend({
            type : $('.tabs-active').attr('data-type'),
            hide : 0
        }, getPageParam()), function (data) {
            var _dataPanel = '#' + $('.tabs-active').attr('data-panel');
            $(_dataPanel).find('.mod-hide-chk').attr('checked', false);
        }, true);
    });
};

/**
 * 发送 ajax 请求
 * @param { String } url
 * @param { Object } param
 * @param { Function } fn 回调函数
 * @param { Boolean } hide 是否显示 overlayer
 * @param { Boolean } empt 发生请求错误时是否 say
 */
function ajaxData(url, param, fn, hide, empt) {
    if (hide) { overlayer({ text: lang.t("操作中") }); }

    ajax(url, param, function(res) {
        if (0 == res.result) {
            if (hide) { hidelayer(lang.t("操作成功~.~")); }
            if (fn) { fn(res.data); }
        } else {
            if (hide) { hidelayer(); }
            if (empt) {
                if (fn) { fn([]); }
            } else { say(lang.t("获取数据错误：") + res.err_desc); }
        }
    }, "POST");
}
