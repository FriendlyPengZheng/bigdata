(function($, undefined) {
var ADDDIALOG, UPDDIALOG, DELDIALOG, MOVEDIALOG, FIELDSET;
$(function(){
    gModule();
    $("#J_addBtn").on("click", function() {
        if (!ADDDIALOG) {
            var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
            ADDDIALOG = $.Dlg.Util.popup({
                id : "J_addCategory",
                title: lang.t("添加类别"),
                contentHtml: $("<form>").append(fieldSet.getElement()),
                save: function(con) {
                    ajaxData(getUrl("gameanalysis/mobilegame", "economy", "addCategory"),
                        _getParam($("#J_addCategory").find("form").formToArray()), function(){
                        location.reload();
                    }, "POST");
                }
            });
        }
        ADDDIALOG.show();
    });
//$("#J_tableContainer").datatable({
    //searchEnabled: true,
    //searchContainer: $("#J_tableSearchContainer")
//});
});
/**
 * @brief childEvent
 * 展示二级类别功能（by一级类别）
 * @param option
 * is_leaf: 1: 叶子（只能添道具） 0：非叶子（只能添类别） 2：不知道
 * @return
 */
window.childEvent = function(option) {
    if(option.data.is_leaf != 1) {
        return $(document.createElement("i")).addClass("expansion").attr({
            "data-id": option.data.category_id,
            "data-name": option.data.category_name
        }).click(function() {
            var t = $(this),
                pTr = t.closest('tr');
            if (t.hasClass("clicked")) {
                t.removeClass("clicked");
                pTr.removeClass("cur");
                pTr.next().filter(".mod-tr-con").remove();
            } else {
                t.addClass("clicked");
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
                fac(_getChildConfig(newTd, t.closest("tr").attr("data-id")));
            }
        });
    }
}
/**
 * @brief tableEvent
 * 一级类别-添加子类别、删除功能
 * @param data
 *
 * @return
 */
window.tableEvent = function(data) {
    return $(_addChildEvent(data)).add(_moveChildEvent(data)).add(_delEvent(data));
}
/**
 * @brief childTableEvent
 * 二级类别-删除功能
 * @param data
 * @return
 */
window.childTableEvent = function(data) {
    return $(_moveChildEvent(data)).add(_delEvent(data));
}
/**
 * @brief
 * 添加子类别功能
 * @param option
 * @return
 */
function _addChildEvent(option) {
    if(option.data.is_leaf != 1) {
        return $(document.createElement("a")).addClass("mod-add btn-green").attr({
            "data-id": option.data.category_id,
            "data-name": option.data.category_name
        }).text(lang.t("添加子类别")).click(function(){
            _addEventFn($(this).attr("data-id"));
        });
    }
}

/**
 * @brief
 * 移动子类别功能
 * @param option
 * @return
 */
function _moveChildEvent(option) {
    if(option.data.is_leaf == 1) {
        return $(document.createElement("a")).addClass("mod-move btn-green").attr({
            "data-id": option.data.category_id,
            "data-name": option.data.category_name
        }).text(lang.t("移动类别")).click(function(){
            _moveEventFn($(this).attr("data-id"));
        });
    }
}
/**
 * @brief
 * 删除类别功能
 * @param option
 * @return
 */
function _delEvent(option) {
    return $(document.createElement("a")).addClass("mod-add btn-green").attr({
        "data-id": option.data.category_id,
        "data-name": option.data.category_name
    }).text(lang.t("删除")).click(function() {
        var t = $(this);
        if(DELDIALOG) {
            $(DELDIALOG.getMask()).remove();
            $(DELDIALOG.getContainer()).remove();
        }
        DELDIALOG = $.Dlg.Util.popup({
            id : "J_delCategory",
            title: lang.t("删除类别"),
            contentHtml: lang.t("确定要永久删除吗？"),
            save: function(con) {
                ajaxData(getUrl("gameanalysis/mobilegame", "economy", "delCategory"), {
                    game_id: $("#J_paramGameId").val(),
                    sstid: "_coinsbuyitem_",
                    category_id: t.attr("data-id")
                }, function(){
                    say(lang.t("删除成功~"), true);
                    var tr = t.closest("tr");
                    if(tr.hasClass("cur")) {
                        tr.next().filter(".mod-tr-con").remove();
                    }
                    tr.remove();
                }, "POST");
            }
        });
        DELDIALOG.show();
    });
}
/**
 * @brief _moveEventFn
 * 移动类别功能
 * @param id: 当前类别id
 * @return
 */
function _moveEventFn(id) {
    if(MOVEDIALOG) {
        $(MOVEDIALOG.getMask()).remove();
        $(MOVEDIALOG.getContainer()).remove();
        MOVEDIALOG = null;
    }
    MOVEDIALOG = $.Dlg.Util.popup({
        id : "J_moveCategory",
        title: lang.t("移动类别"),
        contentHtml: '<div><h4 class="title">' + lang.t("选择一级类别：") + '</h4><div id="J_settings"></div></div>',
        callback: function(con) {
            con.find(".ui-cont .ui-content").css({ "overflow-y": "initial"});
            _createSettings($("#J_settings"));
        },
        saveCallback: function(con) {
            var categoryId = $("#J_moveCategory").find(".selected-m .title-m .t-name").attr("data-id");
            ajaxData(getUrl("gameanalysis/mobilegame", "economy", "moveCategory"), {
                parent_id: categoryId,
                game_id: $("#J_paramGameId").val(),
                sstid: "_coinsbuyitem_",
                category_id: id
            }, function(){
                location.reload();
            }, "POST");
        }
    });
    MOVEDIALOG.show();
}
/**
 * @brief _createSettings
 */
function _createSettings(con) {
    var selCon = $(document.createElement("div")).addClass("sel-con"),
        selP = $(document.createElement("div")).addClass("sel-p");
    var opts = {
        search : true,
        type : 2,
        page : 2,
        selected : [],
        obj : selP,
        mulRadio : 1
    };
    ajaxData(getUrl("gameanalysis/mobilegame", "economy", "getCategoryList"), {
        game_id: $("#J_paramGameId").val(),
        sstid: "_coinsbuyitem_",
        parent_id: 0,
        pagination: 0
    }, function(data) {
        opts.data = _handleChoose(data);
        $.choose.core(opts);
    });
    selP.appendTo(selCon.appendTo(con));
}
/**
 * @brief _handleChoose
 * data : [{
 *      title: '',
 *      attr : { id : 1, otherAttr : ''},
 *      child : [{
 *          title : '',
 *          attr : { id : 1, child : true/false, cid : 1 }
 *      }]
 * }]
 * @return
 */
function _handleChoose(data) {
    var rlt = [{
        title: lang.t("一级类别"),
        children: []
    }];
    $(data).each(function(){
        if(this.is_leaf != 1) {
            rlt[0].children.push({
                title: this.category_name,
                attr: {
                    id: this.category_id,
                    child: false
                }
            });
        }
    });
    return rlt;
}
/**
 * @brief _addEventFn
 * 添加类别功能
 * @param id: 父类别id
 * @return
 */
function _addEventFn(id) {
    if(ADDDIALOG) {
        $(ADDDIALOG.getMask()).remove();
        $(ADDDIALOG.getContainer()).remove();
        ADDDIALOG = null;
    }
    var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
    ADDDIALOG = $.Dlg.Util.popup({
        id : "J_addCategory",
        title: lang.t("添加类别"),
        contentHtml: $("<form>").append(fieldSet.getElement()),
        save: function(con) {
            ajaxData(getUrl("gameanalysis/mobilegame", "economy", "addCategory"),
                _getParam($("#J_addCategory").find("form").formToArray(), id),  function(){
                location.reload();
            }, "POST");
        }
    });
    ADDDIALOG.show();
}
/**
 * @brief
 * listtable -子类别列表
 * @param container
 * @param id
 * @return
 */
function _getChildConfig(container, id) {
    return [{
        type: "listtable",
        container: container,
        isAjax: true,
        renameUrl: "index.php?r=gameanalysis/mobilegame/economy/setCategory",
        url: {
            page: "getPageParam",
            extend: "index.php?r=gameanalysis/mobilegame/economy/getCategoryList&parent_id=" + id
        },
        thead: [{
            type: "string", title: lang.t("二级类别ID") }, {
            type: "string", title: lang.t("二级类别名称") } , {
            type: "string", title: lang.t("操作")
        }],
        appendColumns: [{
            type: "data", key: "category_id", isID: 1 }, {
            type: "rename", key: "category_name", isID : 0 }, "childTableEvent"]
    }];
}
/**
 * @brief _getOptions
 * 添加类别-获取类别列表配置
 *
 * @return
 */
function _getOptions() {
    return {
        items: [{
            label: {
                title: lang.t("类别名称"),
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "category_name",
                className: "ipttxt"
            }]
        }]
    };
}
function _getParam(param, pId) {
    return param && $.isArray(param) ? param.concat({
        name: "game_id",
        value: $("#J_paramGameId").val()
    }, {
        name: "sstid",
        value: "_coinsbuyitem_"
    }, {
        name: "parent_id",
        value: (pId ? pId : 0)
    }) : [];
}

function ajaxData(url, param, fn, type){
    overlayer({ text: lang.t("加载中...")});
    ajax(url, param, function (res) {
        if (res.result == 0) {
            hidelayer(lang.t("加载成功~.~"));
            if(fn) fn(res.data);
        } else {
            hidelayer(lang.t("出错了"));
            say(lang.t("获取数据错误：") + res.err_desc);
        }
    }, type);
}
window.getPageParam = function(pId) {
    return {
        game_id: $("#J_paramGameId").val(),
        sstid: "_coinsbuyitem_"
    }
};
})(jQuery);
