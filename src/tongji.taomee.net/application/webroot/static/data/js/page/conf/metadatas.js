(function($, undefined) {
var ADDDIALOG, UPDDIALOG, DELDIALOG, FIELDSET, AUTOCOMMENT, CURRENTDIALOG;
$(function(){
$("#J_addMetadataBtn").on("click", function() {
    if (!ADDDIALOG) {
        var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
        ADDDIALOG = $.Dlg.Util.popup({
            id : "J_addMetadata",
            title: "添加元数据",
            contentHtml: $("<form>").append(fieldSet.getElement()),
            save: function(con) {
                ajaxData(getUrl("common", "basicdata", "save"), $("#J_addMetadata").find("form").formToArray(), function(){
                    location.reload();
                }, "POST");
            }
        });
    }
    ADDDIALOG.show();
    CURRENTDIALOG = ADDDIALOG;
});
$(".upd-btn").on("click", function() {
    if (!UPDDIALOG) {
        var option = $.extend({}, _getOptions());
        FIELDSET = new tm.form.fieldSet(option),
        UPDDIALOG = $.Dlg.Util.popup({
            id : "J_updMetadata",
            title: "修改元数据",
            contentHtml: $("<form>").append(FIELDSET.getElement()),
            save: function(con) {
                var param = $("#J_updMetadata").find("form").formToArray();
                ajaxData(getUrl("common", "basicdata", "save"), param, function(){
                    location.reload();
                }, "POST");
            }
        });
    }
    var values = $.parseJSON($(this).attr("data")),
        container = $(UPDDIALOG.getContainer());
    FIELDSET.setValues(values).getElement().find("input[name=type]:checked").click();
    container.find(":radio").attr("disabled", true);
    container.find(":radio").filter(":checked").attr("disabled", false);
    container.find("[name=task_id],[name=sstid],[name=stid],[name=op_type],[name=op_fields],[name=range]")
        .attr("readonly", true);
    UPDDIALOG.show();
    CURRENTDIALOG = UPDDIALOG;
    return false;
});
$(".del-btn").on("click", function() {
    var t = $(this),
        values = $.parseJSON($(this).attr("data"));
    if(DELDIALOG) {
        $(DELDIALOG.getMask()).remove();
        $(DELDIALOG.getContainer()).remove();
    }
    DELDIALOG = $.Dlg.Util.popup({
        id : "J_delMetadata",
        title: "删除元数据",
        contentHtml: "确定要永久删除吗？",
        save: function(con) {
            ajaxData(getUrl("common", "basicdata", "delete"), {
                basic_id: values.basic_id
            }, function(){
                t.closest("tr").remove();
            }, "POST");
        }
    });
    DELDIALOG.show();
    return false;
});
$("#J_tableContainer").datatable({
    searchEnabled: true,
    searchContainer: $("#J_tableSearchContainer")
});
});
/**
 * @brief _autoComments
 * 自动获取注释选择框
 * @param o: button
 *
 * @return
 */
function _autoComments(o) {
    var _createComment = function(){
        AUTOCOMMENT = $.Dlg.Util.popup({
            id : "J_autoComments",
            title: "自动获取元数据注释",
            contentHtml: "",
            save: function(con) {
                var container, comment = $("#J_autoComments").find("form").find('[name=comment-list]:checked');
                if (CURRENTDIALOG) {
                    container = $(CURRENTDIALOG.getContainer());
                    container.find("[name=comment_id]").val(comment.val());
                    container.find("[name=comment]").val(comment.attr("data-comment"));
                }
            }
        });
        $(AUTOCOMMENT.getContainer()).css({ "z-index": 100 });
    };
    ajaxData(getUrl("common", "comment", "getComments"), {
        "keyword": $(o).closest("li").parent().find('input[name="data_name"]').val(),
        "comment_id": $(o).closest("li").find('input[name="comment_id"]').val(),
        "fetch_type": 2
    }, function(data){
        if(!AUTOCOMMENT) _createComment();
        var commentField = new tm.form.fieldSet(_handleCommentsOptions(data));
        AUTOCOMMENT.setConfirmHtml($("<form>").append(commentField.getElement()));
        AUTOCOMMENT.show();
    });
}
/**
 * @brief _handleCommentsOptions
 * handle comments options for fieldSet
 * @param data {array}
 * @return
 */
function _handleCommentsOptions(data) {
    var items = [];
    if(data && data.length) {
        items = [{
            label: {
                title: "注释：",
                className: "title-block"
            },
            items: []
        }];
        for(var i = 0; i < data.length; i++) {
            items[0].items.push({
                label: {
                    title: data[i].keyword,
                    className: "mr10"
                },
                labelWrap: true,
                type: "radio",
                name: "comment-list",
                value: data[i].comment_id,
                attr: {
                    checked: !i,
                    "data-comment": data[i].comment
                },
                className: ""
            });
        }
    } else {
        items.push({
            label: {
                title: "没有找到匹配项~"
            }
        });
    }
    return { items: items };
}
function _getOptions() {
    return {
        items: [{
            items: [{
                type: "hidden",
                name: "basic_id"
            }]
        }, {
            label: {
                title: "时间维度",
                className: "title-inline"
            },
            items: [{
                label: {
                    title: "日",
                    className: ""
                },
                labelWrap: true,
                type: "radio",
                name: "period",
                value: 1,
                attr: {
                    checked: true
                },
                className: ""
            }, {
                label: {
                    title: "周",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "period",
                value: 2,
                className: ""
            }, {
                label: {
                    title: "月",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "period",
                value: 3,
                className: ""
            }, {
                label: {
                    title: "分钟",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "period",
                value: 4,
                className: ""
            }, {
                label: {
                    title: "小时",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "period",
                value: 5,
                className: ""
            }, {
                label: {
                    title: "版本周",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "period",
                value: 6,
                className: ""
            }]
        }, {
            label: {
                title: "元数据名称",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "data_name",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "类型",
                className: "title-inline"
            },
            items: [{
                label: {
                    title: "report",
                    className: ""
                },
                labelWrap: true,
                type: "radio",
                name: "type",
                value: 1,
                attr: {
                    checked: true
                },
                eventCallback: function() {
                    $("#J_reportConfigure").show();
                    $("#J_resultConfigure").hide();
                },
                className: ""
            }, {
                label: {
                    title: "result",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "type",
                value: 2,
                eventCallback: function() {
                    $("#J_reportConfigure").hide();
                    $("#J_resultConfigure").show();
                },
                className: ""
            }]
        }, {
            line: {
                attr: {
                    id: "J_reportConfigure"
                }
            },
            items: [{
                label: {
                    title: "stid:",
                    className: "fb"
                },
                type: "text",
                name: "stid",
                className: "txt w100"
            }, {
                label: {
                    title: "sstid:",
                    className: "ml10 fb"
                },
                type: "text",
                name: "sstid",
                className: "txt w100"
            }, {
                label: {
                    title: "op_type:",
                    className: "ml10 fb"
                },
                type: "text",
                name: "op_type",
                className: "txt w100"
            }, {
                label: {
                    title: "op_fields:",
                    className: "ml10 fb"
                },
                type: "text",
                name: "op_fields",
                className: "txt w100"
            }]
        }, {
            line: {
                attr: {
                    id: "J_resultConfigure"
                },
                cssStyle: {
                    display: "none"
                }
            },
            label: {
                title: "task_id",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "task_id",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "range",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "range",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "因子",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "factor",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "精度",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "precision",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "单位",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "unit",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "注释",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "comment",
                className: "ipttxt"
            }, {
                type: "hidden",
                name: "comment_id"
            }, {
                type: "button",
                title: "自动获取",
                className: "btn-green",
                eventClick: function(o){
                    _autoComments(o);
                }
            }]
        }]
    };
}
function ajaxData(url, param, fn, type){
    overlayer({ text: "加载中..."});
    ajax(url, param, function (res) {
        if (res.result == 0) {
            hidelayer("加载成功~.~");
            if(fn) fn(res.data);
        } else {
            hidelayer("出错了");
            say("获取数据错误：" + res.err_desc);
        }
    }, type);
}
})(jQuery);
