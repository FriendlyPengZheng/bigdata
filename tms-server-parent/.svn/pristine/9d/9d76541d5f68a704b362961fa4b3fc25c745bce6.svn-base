(function($, undefined) {
var ADDDIALOG, UPDDIALOG, DELDIALOG, FIELDSET;
$(function(){
$("#J_addLogBtn").on("click", function() {
    if (!ADDDIALOG) {
        var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
        ADDDIALOG = $.Dlg.Util.popup({
            id : "J_addLog",
            title: "添加日志",
            contentHtml: $("<form>").append(fieldSet.getElement()),
            save: function(con) {
                //ajaxData(getUrl("common", "comment", "save"),
            	//ajaxData("save?",
            	ajaxData("../save?",
						 $("#J_addLog").find("form").formToArray(),
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
            id : "J_updLog",
            title: "修改日志",
            contentHtml: $("<form>").append(FIELDSET.getElement()),
            save: function(con) {
                var param = $("#J_updLog").find("form").formToArray();
                param.push({
                    name: "logId",
                    value: values.logId
                });
                ajaxData("../save?", param, function(){
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
    container.find("[name=logId]").attr("readonly", true);
    UPDDIALOG.show();
    CURRENTDIALOG = UPDDIALOG;
    return false;
});
$(".del-btn").on("click", function() {
    var t = $(this),
        values = $.parseJSON($(this).attr("data"));
    if(DELDIALOG){
    	$(DELDIALOG.getContainer()).remove();
    }
    var title = values.status == 0 ? "废弃" : "开始使用";
    values.status = values.status == 0 ? 1 : 0;
    if (!DELDIALOG) {
        DELDIALOG = $.Dlg.Util.popup({
            id : "J_delLog",
            contentHtml: ("确定要" + title + "日志吗？"),
            save: function(con) {
                ajaxData("../delete?", {
                    "logId": values.logId,
                    "status": values.status
                }, function(){
                    //t.closest("tr").remove();
                    location.reload();
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
    return  {
    	 items: [{ 
            label: {
                title: "日志名称",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "logName",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "日志类型",
                className: "title-inline"
            },
            items: [{
                label: {
                    title: "注册类型",
                    className: ""
                },
                labelWrap: true,
                type: "radio",
                name: "type",
                value: 0,
                attr: {
                    checked: true
                },
                className: ""
            }, {
                label: {
                    title: "自定义类型",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "type",
                value: 1,
                className: ""
            }]
        }, {
            label: {
            title: "状态",
            className: "title-inline"
        },
        items: [{
            label: {
            title: "使用中",
            className: ""
        },
        labelWrap: true,
        type: "radio",
        name: "status",
        value: 0,
        attr: {
            checked: true
        },
        className: ""
        },{
        label: {
        title: "已废弃",
        className: "ml10"
    	},
    	labelWrap: true,
    	type: "radio",
    	name: "status",
    	value: 1,
    	className: ""
    }]
   }]
  };
}
function ajaxData(url, param, fn, type){
    overlayer({ text: "加载中..."});
    ajax(url, param, function (res) {
    	console.log("ajaxData result is " + res.result);
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
