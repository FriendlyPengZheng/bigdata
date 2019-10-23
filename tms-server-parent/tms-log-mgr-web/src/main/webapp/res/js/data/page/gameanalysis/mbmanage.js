var SETDIALOG, MORESETDIALOG;
$(function(){
    //gModule();
    //header功能区固定位置
    $(document).scroll(function() {
        if($(document).scrollTop() > $(".content-header").offset().top - $("#J_header").height()) {
            $("#J_headerTools").css({
                position: "fixed",
                top: "86px"
            });
        } else {
            $("#J_headerTools").css({
                position: "static"
            });
        }
    });
    $("#J_editMb").click(function() {
        var ids = [];
        $(".module-table:visible").find('tr td .chk:checked').each(function() {
            ids.push($(this).closest("tr").attr("data-id"));
        });
        if(ids.length) {
            _showSetCategory(ids, true);
        } else {
            say(lang.t("请选择道具~"));
        }
    });

    //上传图片功能
    $("#J_file").change(function(){
        $("form:first").ajaxSubmit(options);
        $(this).blur();
    });
    var options = {
        url: "../../../../common/economy/importItem&ajax=1",
        dataType: "json",
        success: function(res){
            if(res.result == 0) {
                say(lang.t("上传成功~"), true, function() {
                    location.reload();
                });
            } else {
                say(lang.t("上传失败~。~") + res.err_desc);
            }
        }
    };
});

function tableEvent(option) {
    return $(hideEvent(option)).add(categoryEvent(option));
}

/**
 * @brief
 * 一级货币-编辑类别功能
 * @return
 */
function categoryEvent(option) {
    var category = $('<a class="ctg-btn btn-green">' + lang.t("编辑类别") + '</a>');
    category.click(function(){
        _showSetCategory($(this).closest("tr").find('td[data-key="id"]').text());
    });
    return category;
}

/**
 * @brief hideEvent
 * 隐藏功能
 * @param data
 *
 * @return
 */
function hideEvent(option) {
    var hideConfig = _getHideCongfig(),
        hide = $('<label><input type="checkbox" '
				 + (option.data["hide"] && option.data["hide"] == 1
					? 'checked'
					: (option.data["status"] && option.data["status"] == 1 ? 'checked' : ''))
				 + ' class="mod-hide-chk mr2"><a class="mod-hide mr10">' + lang.t("隐藏") + '</a></label>');
    if(option && option.container) {
        var tab = option.container.closest(".tabs-wrapper").find('li[data-panel="'
																 + option.container.attr("id") + '"]');
        hideConfig.hideUrl += '?' + hideConfig.key + '=' + tab.attr("data-type");
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
            if(res.result != 0) {
                say(lang.t("修改失败！"));
                t.attr("checked", !checked);
            }
        });
    });
    return hide;
}

function _getHideCongfig() {
    var aside = (_getModuleKey()).split("-"),
        pAside = aside[aside.length-2],
        cAside = aside[aside.length-1];
    if(pAside == "economy" && cAside == "mbmanage") {
        return {
            hideUrl: "../../../../common/economy/setHide",
            key: "sstid"
        };
    }
    if(pAside == "mission" && cAside == "manage") {
        return {
            hideUrl: "../../../../common/Mission/setHide",
            key: "type"
        };
    }
    if(pAside == "gpzsanalysis" && cAside == "manage") {
        return {
            hideUrl: "../../../../common/Gpzsanalysis/setHide",
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

//--------edit category start ------------
function _showSetCategory(ids, more) {
    if(SETDIALOG) {
        $(SETDIALOG.getMask()).remove();
        $(SETDIALOG.getContainer()).remove();
    }
    SETDIALOG = $.Dlg.Util.popup({
        id : "J_setCategory",
        title: lang.t("编辑道具类别"),
        contentHtml: _getCategoryHtml(),
        callback: function(con) {
            _createAllCategoryList();
            //_bindNewCategoryEvent();
        },
        save: function(con) {
            var categoryIds = [], names = "";
            $("#J_itemCategoryList").find(".item-li").each(function(){
                categoryIds.push($(this).attr("category_id"));
                names += $(this).text() + ";";
            });
            var url = getUrl("common", "economy", "setItemCategory");
            url = more ? getUrl("common", "economy", "setItemCategory", "append=1") : url;
            ajaxData(url, {
                item_id: $("#J_setCategory").data("item-id"),
                category_id: categoryIds,
                game_id: $("#J_paramGameId").val(),
                sstid: "_coinsbuyitem_"
            }, function(data){
                if(more) {
                    location.reload();
                } else {
                    $(".module-table").find('tr[data-id="' + $("#J_setCategory").data("item-id")+ '"]:eq(0)')
                        .find('td[data-key="category_name"]').text(names.slice(0, -1));
                }
            });
        }
    });
    $(SETDIALOG.getContent()).css("overflow-y", "inherit");
    $("#J_setCategory").data("item-id", ids);
    if(!more) _getItemCategoryList(ids);
    SETDIALOG.show();
}

function _getCategoryHtml() {
    return ''
        + '<h4 class="title">' + lang.t("道具已属类别列表：") + '</h4>'
        + '<ul class="item-list" id="J_itemCategoryList">'
    //+     '<li class="item-li">活跃用户数<a href="javascript: void(0);" class="item-li-del" title="删除">&nbsp;</a></li>'
        + '</ul>'
        + '<div class="item-tools">' + lang.t("添加类别选择池") + '</div>'
        + '<ul class="item-pool">'
        +    '<li class="widget-sel sel-con" id="J_categoryList">'
        +    '<div class="sel-p">'
        +       '<span class="title-inline">' + lang.t("请选择类别:") + '</span>'
        +       '<div class="choose-con-inline"></div>'
        +    '</div>'
        +    '</li>'
    //+    '<li class="widget-sel" id="J_newCategoryList">'
    //+       '<span class="title-inline">新建道具类别:</span>'
    //+       '<div class="sel-wrapper">'
    //+           '<span>类别名称：</span><input type="text" class="ipt-txt mr20" name="category_name">'
    //+           '<a href="javascript: void(0);" title="保存" class="add-comment ml10" id="J_addCategoryBtn">添加</a>'
    //+       '</div>'
    //+    '</li>'
        + '</ul>';
}

function _createAllCategoryList() {
    var categoryData = $("body").data("category_list");
    var options = { search: true, type: 2, page: 2,
					mulRadio: 2,
					obj: $("#J_categoryList").find(".choose-con-inline"),
					callback: function(cur, title){
						if(title.attr("data-child") != "true") {
							$("#J_itemCategoryList").append(_createItemList([{
								category_id: title.attr("data-id"),
								category_name: title.text()
							}]));
						}
					},
					childFn: function(t) {
						var pSelected = t.closest(".sel-ul").prev(".sel-p").find(".selected-m").find(".t-name");
						var id = t.attr("data-id"),
							text = pSelected.text() + "-" + t.text();
						$("#J_itemCategoryList").append(_createItemList([{
							category_id: id,
							category_name: text
						}]));
					},
					getData: function(id, fn) {
						ajaxData(getUrl("common", "economy", "getCategoryList"), {
							sstid: "_coinsbuyitem_",
							game_id: $("#J_paramGameId").val(),
							parent_id: id,
                            pagination: 0
						}, function(data) {
							data = _handleSettiingChoose(data);
							if(fn) fn(data);
						});
					}
				  };
    if(categoryData) {
        options.data = categoryData;
        $.choose.core(options);
    } else {
        ajaxData(getUrl("common", "economy", "getCategoryList"), {
            parent_id: 0,
            sstid: "_coinsbuyitem_",
            game_id: $("#J_paramGameId").val(),
            pagination: 0
        }, function(data){
            data = _handleDataForChoose(data);
            $("body").data("category_list", data);
            options.data = data;
            $.choose.core(options);
        });
    }
}

function _handleSettiingChoose(data) {
    var rlt = [];
    $.each(data, function(){
        rlt.push({
            title : this.category_name,
            attr : { id : this.category_id }
        });
    });
    return rlt;
}

function _handleDataForChoose(data) {
    var rlt = {
        title: lang.t("道具类别"),
        children: []
    };
    $.each(data, function(key, value){
        $(value).each(function(){
            rlt.children.push({
                title: this.category_name,
                attr: {
                    id: this.category_id,
                    cid: this.category_id,
                    child: (this.is_leaf == 0 ? true : false)
                },
            });
        });
    });
    return [rlt];
}

/**
 * @brief _getItemCategoryList
 * 获取某个道具的道具类别列表
 * @param itemId
 * @return
 */
function _getItemCategoryList(itemId) {
    var itemList = $("#J_itemCategoryList");
    itemList.append($("<li>").addClass("loading"));
    ajaxData(getUrl("common", "economy", "getItemCategory"), {
        item_id: itemId,
        sstid: "_coinsbuyitem_",
        game_id: $("#J_paramGameId").val()
    }, function(data){
        itemList.empty().append(_createItemList(data));
    });
}

/**
 * @brief _bindNewCategoryEvent
 * 新增道具类别并加入类别列表
 * @return
 */
function _bindNewCategoryEvent() {
    $("#J_addCategoryBtn").click(function() {
        var newCategoryList = $("#J_newCategoryList"),
            name = newCategoryList.find("input[name=category_name]");
        if(name.val()) {
            ajaxData(getUrl("common", "economy", "addCategory"), {
                category_name: name.val(),
                game_id: $("#J_paramGameId").val(),
                sstid: "_coinsbuyitem_"
            }, function(data){
                $("#J_itemCategoryList").append(_createItemList([{
                    category_id: data,
                    category_name: name.val()
                }]));
            });
        } else {
            name.hint();
        }
    });
}

/**
 * @brief
 * <li class="item-li">消费类<a href="javascript: void(0);" class="item-li-del" title="删除">&nbsp;</a></li>
 * [{
 *      keyword: "",
 *      category_id: ""
 * }]
 * @return
 */
function _createItemList(list) {
    var items = $();
    for(var i = 0; i < list.length; i++) {
        items = items.add($('<li>').addClass("item-li").attr({
            category_id: list[i].category_id
        }).text(list[i].parent_name ? list[i].parent_name + "-" + list[i].category_name : list[i].category_name)
						  .append($('<a>').addClass("item-li-del").attr({
							  href: "javascript: void(0);",
							  title: lang.t("删除")
						  }).click(function(){
							  $(this).closest(".item-li").remove();
						  })
								 ));
    }
    return items;
}

//---------edit category end   ------------
/**
 * @brief ajaxData
 *
 * @param url
 * @param param
 * @param fn:回调函数
 * @param hide:是否显示overlayer
 * @param empt:发生请求错误时，是否say
 */
function ajaxData(url, param, fn, hide, empt) {
    if(hide) overlayer({ text: lang.t("加载中...")});
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
