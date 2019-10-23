/**
 * @fileOverview 隐藏功能
 * @name without_tools.js
 * @changelog Maverick 10.10.2014 增加了隐藏/显示全部任务功能
 */
var SETDIALOG;

$(document).ready(function () {
    gModule();
});

/**
 * 隐藏功能
 * @param { Object } option
 * @returns { Object }
 */
function hideEvent(option) {
    var hideConfig = _getHideCongfig(),
        hide = $('<label><input type="checkbox" '
                 + (option.data["hide"] && option.data["hide"] == 1
                    ? 'checked'
                    : (option.data["status"] && option.data["status"] == 1 ? 'checked' : ''))
                 + ' class="mod-hide-chk mr2"><a class="mod-hide btn-green">' + lang.t("隐藏") + '</a></label>');
    if (option && option.container) {
        var tab = option.container.closest(".tabs-wrapper").find('li[data-panel="'
                                                                 + option.container.attr("id") + '"]');
        hideConfig.hideUrl += '&' + hideConfig.key + '=' + tab.attr("data-type");
    }
    hide.find(".mod-hide-chk").click(function(){
        var t = $(this),
            checked = t.attr("checked") == "checked" ? true : false;
        overlayer({ text: lang.t("操作中...")});
        ajax(hideConfig.hideUrl, $.extend({
            id : t.closest("tr").attr('data-id'),
            hide : (checked ? 1 : 0)
        }, getPageParam()), function(res) {
            hidelayer();
            if (res.result != 0) {
                say(lang.t("修改失败！"));
                t.attr("checked", !checked);
            }
        });
    });
    return hide;
}

/**
 * 隐藏配置
 * @returns { Object }
 */
function _getHideCongfig() {
    var aside = (_getModuleKey()).split("-"),
        pAside = aside[aside.length-2],
        cAside = aside[aside.length-1];
    if (pAside == "economy" && cAside == "mbmanage") {
        return {
            hideUrl: "index.php?r=gameanalysis/mobilegame/Economy/setHide",
            key: "sstid"
        };
    }
    if (pAside == "mission" && cAside == "manage") {
        return {
            hideUrl: "index.php?r=gameanalysis/mobilegame/Mission/setHide",
            key: "type"
        };
    }
    if (pAside == "gpzsanalysis" && cAside == "manage") {
        return {
            hideUrl: "index.php?r=gameanalysis/mobilegame/Gpzsanalysis/setHide",
            key: "type"
        };
    }
    return {
        hideUrl: ""
    };
}

window.getPageParam = function() {
    return {
        game_id : $("#J_paramGameId").val()
    };
};
