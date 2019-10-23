(function($, undefined) {
var ADDDIALOG, UPDDIALOG, DELDIALOG, FIELDSET;
$(function(){
$("#J_addServerBtn").on("click", function() {
    if (!ADDDIALOG) {
        var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
        ADDDIALOG = $.Dlg.Util.popup({
            id : "J_addServer",
            title: "添加区服",
            contentHtml: $("<form>").append(fieldSet.getElement()),
            save: function(con) {
                //ajaxData(getUrl("common", "comment", "save"),
            	//ajaxData("save?",
            	ajaxData("../save?",
						 $("#J_addServer").find("form").formToArray(),
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
            id : "J_updServer",
            title: "修改区服",
            contentHtml: $("<form>").append(FIELDSET.getElement()),
            save: function(con) {
                var param = $("#J_updServer").find("form").formToArray();
                param.push({
                    name: "serverId",
                    value: values.serverId
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
    container.find("[name=serverId],[name=gameId],[name=parentId],[name=isLeaf],[name=status]").attr("readonly", true);
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
            id : "J_delServer",
            contentHtml: ("确定要" + title + "区服吗？"),
            save: function(con) {
                ajaxData("../delete?", {
                    "serverId": values.serverId,
                    "serverName": values.serverName,
                    "parentId": values.parentId,
                    "gameId": values.gameId,
                    "isLeaf": values.isLeaf,
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
    return {
        items: [{
            label: {
                title: "serverName",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "serverName",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "gameId",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "gameId",
                className: "ipttxt"
            }]
        }, {
        	label:{
        	title:"parentId",
        	className: "title-inline"
        	},
        	items:[{
        		type: "text",
        		name: "parentId",
        	    className: "ipttxt"
        		}]
        },{
            label: {
                title: "是否是叶子节点",
                className: "title-inline"
            },
            items: [{
                label: {
                    title: "是",
                    className: ""
                },
                labelWrap: true,
                type: "radio",
                name: "isLeaf",
                value: 0,
                attr: {
                    checked: true
                },
                className: ""
            }, {
                label: {
                    title: "否",
                    className: "ml10"
                },
                labelWrap: true,
                type: "radio",
                name: "isLeaf",
                value: 1,
                className: ""
            }]
        },{
        	label :{
        	title: "状态",
        	className: "title-inline"
        },
        items: [{
        	label:{
        		title:"使用中",
        		className:""
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
