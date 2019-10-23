(function($, undefined) {
var ADDDIALOG, UPDDIALOG, DELDIALOG, FIELDSET;
$(function(){
$("#J_addTaskBtn").on("click", function() {
    if (!ADDDIALOG) {
        var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
        ADDDIALOG = $.Dlg.Util.popup({
            id : "J_addTask",
            title: "添加计算模式",
            contentHtml: $("<form>").append(fieldSet.getElement()),
            save: function(con) {
                //ajaxData(getUrl("common", "comment", "save"),
            	//ajaxData("save?",
            	ajaxData("../task/save?",
						 $("#J_addTask").find("form").formToArray(),
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
            id : "J_updTask",
            title: "修改任务",
            contentHtml: $("<form>").append(FIELDSET.getElement()),
            save: function(con) {
                var param = $("#J_updTask").find("form").formToArray();
                param.push({
                    name: "taskId",
                    value: values.taskId
                });
                ajaxData("../task/save?", param, function(){
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
            id : "J_delTask",
            title: "删除任务",
            contentHtml: "确定要永久删除吗？",
            save: function(con) {
                ajaxData("../task/delete?", {
                    "taskId": values.taskId
                }, function(){
                    t.closest("tr").remove();
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
    return {
        items: [{
            label: {
                title: "任务名称",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "taskName",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "算法",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "op",
                className: "ipttxt"
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
                value: 0,
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
                value: 1,
                className: ""
            }, {
                label: {
                    title: "月",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "period",
                value: 2,
                className: ""
            }, {
                label: {
                    title: "版本周",
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
            }]
        },{
            label: {
                title: "结果是否入库",
                className: "title-inline"
            },
            items: [{
                label: {
                    title: "不入库",
                    className: ""
                },
                labelWrap: true,
                type: "radio",
                name: "result",
                value: 0,
                attr: {
                    checked: true
                },
                className: ""
            }, {
                label: {
                    title: "入库",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "result",
                value: 1,
                className: ""
            }]
        },{
            label: {
                title: "执行时间",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "executeTime",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "优先级",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "priority",
                className: "ipttxt"
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
