(function ($, undefined) {
    var ADDDIALOG, UPDDIALOG, DELDIALOG, FIELDSET, AUTOCOMMENT, CURRENTDIALOG;
    $(function () {
        $("#J_addMetadataBtn").on("click", function () {
            if (!ADDDIALOG) {
                var fieldSet = new tm.form.fieldSet($.extend({}, _getOptions()));
                ADDDIALOG = $.Dlg.Util.popup({
                    id: "J_addMetadata",
                    title: "添加元数据",
                    contentHtml: $("<form>").append(fieldSet.getElement()),
                    save: function (con) {
                    	console.log("formToArray value is " + JSON.stringify($("#J_addMetadata").find("form").formToArray(), true));
                        ajaxData("../save?", $("#J_addMetadata").find("form").formToArray(), function () {
                            location.reload();
                        }, "POST");
                    }
                });
            }
            ADDDIALOG.show();
            CURRENTDIALOG = ADDDIALOG;
        });
        $(".upd-btn").on("click", function () {
            if (!UPDDIALOG) {
                var option = $.extend({}, _getOptions());
                FIELDSET = new tm.form.fieldSet(option),
                    UPDDIALOG = $.Dlg.Util.popup({
                        id: "J_updMetadata",
                        title: "修改元数据",
                        contentHtml: $("<form>").append(FIELDSET.getElement()),
                        save: function (con) {
                            var param = $("#J_updMetadata").find("form").formToArray();
                            ajaxData("../save?", param, function () {
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
        $(".del-btn").on("click", function () {
            var t = $(this),
                values = $.parseJSON($(this).attr("data"));
            if (DELDIALOG) {
                $(DELDIALOG.getMask()).remove();
                $(DELDIALOG.getContainer()).remove();
            }
            DELDIALOG = $.Dlg.Util.popup({
                id: "J_delMetadata",
                title: "删除元数据",
                contentHtml: "确定要永久删除吗？",
                save: function (con) {
                    ajaxData("../delete?", {
                        "metadataId": values.metadataId
                    }, function () {
                        t.closest("tr").remove();
                        location.reload();
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
        var _createComment = function () {
            AUTOCOMMENT = $.Dlg.Util.popup({
                id: "J_autoComments",
                title: "自动获取元数据注释",
                contentHtml: "",
                save: function (con) {
                    var container, comment = $("#J_autoComments").find("form").find('[name=comment-list]:checked');
                    console.log("comment val is " + comment.val());
                    if (CURRENTDIALOG) {
                        container = $(CURRENTDIALOG.getContainer());
                        container.find("[name=commentId]").val(comment.val());
                        container.find("[name=comment]").val(comment.attr("data-comment"));
                    }
                }
            });
            $(AUTOCOMMENT.getContainer()).css({"z-index": 100});
        };
        ajaxData("../../comment/getComments?", {
            "keyword": $(o).closest("li").parent().find('input[name="metadataName"]').val(),
            "commentId": $(o).closest("li").find('input[name="commentId"]').val(),
            "fetchType": 2
        }, function (data) {
        	console.log("222");
            if (!AUTOCOMMENT) _createComment();
            console.log("data is " + JSON.stringify(data, true));
            var commentField = new tm.form.fieldSet(_handleCommentsOptions(data));
            console.log("commentField is " + JSON.stringify(commentField.getElement(), true));
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
        if (data && data.length) {
            items = [{
                label: {
                    title: "注释：",
                    className: "title-block"
                },
                items: []
            }];
            for (var i = 0; i < data.length; i++) {
                items[0].items.push({
                    label: {
                        title: data[i].keyword,
                        className: "mr10"
                    },
                    labelWrap: true,
                    type: "radio",
                    name: "comment-list",
                    value: data[i].commentId,
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
        return {items: items};
    }

    function _getOptions() {
        return {
            items: [{
                items: [{
                    type: "hidden",
                    name: "metadataId"
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
                    name: "metadataName",
                    className: "ipttxt"
                }]
            }, {
                label: {
                    title: "data_id",
                    className: "title-inline"
                },
                items: [{
                    type: "text",
                    name: "dataId",
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
                    name: "commentId"
                }, {
                    type: "button",
                    title: "自动获取",
                    className: "btn-green",
                    eventClick: function (o) {
                        _autoComments(o);
                    }
                }]
            }]
        };
    }

    function ajaxData(url, param, fn, type) {
        overlayer({text: "加载中..."});
        ajax(url, param, function (res) {
            if (res.result == 0) {
                hidelayer("加载成功~.~");
                if (fn) fn(res.data);
            } else {
                hidelayer("出错了");
                say("获取数据错误：" + res.err_desc);
            }
        }, type);
    }
})(jQuery);
