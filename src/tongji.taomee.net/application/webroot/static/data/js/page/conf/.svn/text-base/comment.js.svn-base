(function($, undefined) {
var ADDDIALOG, UPDDIALOG, DELDIALOG, FIELDSET;
$(function(){
$("#J_addCommentBtn").on("click", function() {
    if (!ADDDIALOG) {
        var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
        ADDDIALOG = $.Dlg.Util.popup({
            id : "J_addComment",
            title: "添加注释",
            contentHtml: $("<form>").append(fieldSet.getElement()),
            save: function(con) {
                ajaxData(getUrl("common", "comment", "save"),
						 $("#J_addComment").find("form").formToArray(),
						 function(){
							 location.reload();
						 }, "POST");
            }
        });
    }
    ADDDIALOG.show();
});
$(".upd-btn").on("click", function() {
    var values = $.parseJSON($(this).attr("data"));
    if (!UPDDIALOG) {
        FIELDSET = new tm.form.fieldSet($.extend({}, _getOptions()));
        UPDDIALOG = $.Dlg.Util.popup({
            id : "J_updComment",
            title: "修改注释",
            contentHtml: $("<form>").append(FIELDSET.getElement()),
            save: function(con) {
                var param = $("#J_updComment").find("form").formToArray();
                param.push({
                    name: "comment_id",
                    value: values.comment_id
                });
                ajaxData(getUrl("common", "comment", "save"), param, function(){
                    location.reload();
                }, "POST");
            }
        });
    }
    FIELDSET.setValues(values);
    UPDDIALOG.show();
    return false;
});
$(".del-btn").on("click", function() {
    var t = $(this),
        values = $.parseJSON($(this).attr("data"));
    if (!DELDIALOG) {
        DELDIALOG = $.Dlg.Util.popup({
            id : "J_delComment",
            title: "删除注释",
            contentHtml: "确定要永久删除吗？",
            save: function(con) {
                ajaxData(getUrl("common", "comment", "delete"), {
                    comment_id: values.comment_id
                }, function(){
                    t.closest("tr").remove();
                }, "POST");
            }
        });
    }
    DELDIALOG.show();
    return false;
});
$("#J_tableContainer").datatable({
    searchEnabled: true,
    searchContainer: $("#J_tableSearchContainer")
});
});
function _getOptions() {
    return {
        items: [{
            label: {
                title: "注释名称",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "keyword",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "注释详情",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "comment",
                className: "ipttxt"
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
