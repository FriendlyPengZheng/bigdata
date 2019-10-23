(function($, undefined) {
var ITEMS = {
        GAME_TYPE: [],
        STATUS: [],
        IGNORED_COMPONENT: [],
        FUNC_SLOT: []
    },
    ADDDIALOG, UPDDIALOG, DELDIALOG, FIELDSET,
    IGNOREDIALOG, FIELDSET, IGNOREFIELDSET,
    FUNCDIALOG, FUNCFIELDSET;
$(function() {
$("#J_addGameBtn").on("click", function() {
    var options = _getOptionsConfig(),
        show = function() {
            $("#J_setIgnore").data("data", "");
            ADDDIALOG.show();
        };
    if (!ADDDIALOG) {
        var prepared = 1,
            setup = function() {
                var fieldSet = new tm.form.fieldSet($.extend({}, options));
                ADDDIALOG = $.Dlg.Util.popup({
                    id : "J_addGame",
                    title: "添加游戏",
                    contentHtml: $("<form>").append(fieldSet.getElement()),
                    save: function(con) {
                        _save($("#J_addGame").find("form").formToArray());
                    }
                });
                show();
            },
            callback = function(index, name, values) {
                return function() {
                    options.items[index].items = _setFormInlineItems(name, values, "radio");
                    if (!prepared--) setup();
                };
            };
        _getGameType(callback(2, "game_type", ITEMS.GAME_TYPE));
        _getStatus(callback(6, "status", ITEMS.STATUS));
    } else {
        show();
    }
});
$(".upd-btn").on("click", function() {
    var t = $(this),
        options = _getOptionsConfig(),
        show = function() {
            var values = $.parseJSON(t.attr("data")),
                container = $(UPDDIALOG.getContainer());
            $("#J_setIgnoreBtn").data("data", values.ignoreId);
            $("#J_setFuncBtn").data("data", values.funcSlot);
            _getIgnoredComponents(function() {
                _getFuncSlot(function() {
                	console.log(values);
                    var ignore = values.ignoreId.split("_"), ignoreTitle = [],
                        funcSlot = values.funcSlot, funcTitle = [];
                    for(var i = 0; i < ITEMS.IGNORED_COMPONENT.length; i++) {
                        if(inArray(ITEMS.IGNORED_COMPONENT[i].value, ignore)) {
                            ignoreTitle.push(ITEMS.IGNORED_COMPONENT[i].title);
                        }
                    }
                    var funcSlot = values.funcSlot, funcTitle = [];
                    for(var j = 0; j < ITEMS.FUNC_SLOT.length; j++) {
                        if((1<<ITEMS.FUNC_SLOT[j].value & parseInt($("#J_setFuncBtn").data("data"), 10)) > 0) {
                            funcTitle.push(ITEMS.FUNC_SLOT[j].title);
                        }
                    }
                    values.game_id = values.gameId;
                    values.game_name = values.gameName;
                    values.game_type = values.gameType;
                    values.auth_id = values.authId;
                    values.manage_auth_id = values.mangeAuthId;
                    values.online_auth_id = values.onlineAuthId;
   					values.game_email = values.gameEmail;
                    values.ignore_title = ignoreTitle.join("，");
                    values.ignore = values.ignoreId;
                    values.func_slot = values.funcSlot;
                    values.func_title = funcTitle.join("，");
                    FIELDSET.setValues(values);
                    container.find("[name=game_type]").attr("disabled", true);
                    container.find("[name=game_type]").filter(":checked").attr("disabled", false);
                    UPDDIALOG.show();
                });
            });
        };
    if (!UPDDIALOG) {
        var prepared = 1,
            setup = function() {
                FIELDSET = new tm.form.fieldSet($.extend({}, options));
                UPDDIALOG = $.Dlg.Util.popup({
                    id : "J_updGame",
                    title: "修改游戏",
                    contentHtml: $("<form>").append(FIELDSET.getElement()),
                    save: function(con) {
                        _save($("#J_updGame").find("form").formToArray());
                    }
                });
                show();
            },
            callback = function(index, name, values) {
                return function() {
                    options.items[index].items = _setFormInlineItems(name, values, "radio");
                    if (!prepared--) setup();
                };
            };
        _getGameType(callback(2, "game_type", ITEMS.GAME_TYPE));
        _getStatus(callback(6, "status", ITEMS.STATUS));
    } else {
        show();
    }
    return false;
});

$(".del-btn").on("click", function() {
    var t = $(this),
        values = $.parseJSON($(this).attr("data"));
    if(DELDIALOG) {
        $(DELDIALOG.getMask()).remove();
        $(DELDIALOG.getContainer()).remove();
    }
    var title = values.status == 1 ? "废弃" : "开始使用";
    values.status = values.status == 1 ? 2 : 1;
    DELDIALOG = $.Dlg.Util.popup({
        id : "J_delMetadata",
        title: (title + "游戏"),
        contentHtml: ("确定要" + title + "游戏吗？"),
        save: function(con) {
            ajax("../updateStatus?", {
            	"game_id": values.gameId,
                "status": values.status
            }, function(res){
                if(res.result == 0) {
                    location.reload();
                } else {
                    say("获取数据错误：" + res.err_desc);
                }
            }, "POST");
        }
    });
    DELDIALOG.show();
    return false;
});
$("#J_tableContainer").datatable({
    sortDefaultType: "asc",
    searchEnabled: true,
    sortDefaultColumn: 8,
    searchContainer: $("#J_tableSearchContainer")
});
});

/**
 * @brief _save
 * 保存游戏信息
 *
 * @param param
 *
 * @return
 */
function _save(param) {
    overlayer({ text: "加载中..."});
    ajax("../save?", param, function (res){	
        if (res.result == 0) {
        	hidelayer("加载成功：）");
            location.reload();
        } else {
            hidelayer("出错了：（");
            say("获取数据错误：" + res.err_desc);
        }
    }, "POST");
}

/**
 * @brief _setFormInlineItems
 * get radio config
 * @param name
 * @param values
 * @param type
 *
 * @return
 */
function _setFormInlineItems(name, values, type) {
    var items = [];
    $.each(values, function(i) {
        var opt = {
            label: {
                title: this.title,
                className: ("mr5" + (type == "checkbox" ? " chk-item" : ""))
            },
            labelWrap: true,
            type: type,
            name: name,
            value: this.value,
            className: ""
        };
        if(i == 0 && type == "radio") opt.attr = { checked: true };
        items.push(opt);
    });
    return items;
}

/**
 * @brief _getGameType
 * 获取游戏类型
z *
 * @param callback
 * @return
 */
function _getGameType(callback) {
    if(ITEMS.GAME_TYPE.length) {
        callback();
    } else {
      ajax("../getGameType?", null, function (res){
      	console.log(res);
            if (res.result == 0) {
                $.each(res.data, function() {
                    ITEMS.GAME_TYPE.push({
                        title: this.toString(),
                        value: this.toString()
                    });
                });
            }
            callback();
        });
    }
}

/**
 * @brief _getStatus
 * 获取游戏状态
 * @param callback
 *
 * @return
 */
function _getStatus(callback) {
    if(ITEMS.STATUS.length) {
        callback();
    } else {
    	ajax("../getStatus?", null, function (res){
            if (res.result == 0) {
                $.each(res.data, function(id) {
                    ITEMS.STATUS.push({
                        title: this.toString(),
                        value: id
                    });
                });
            }
            callback();
        });
    }
}

/**
 * @brief _getIgnoredComponents
 * 获取游戏包含的组件列表
 * @param callback
 *
 * @return
 */
function _getIgnoredComponents(callback) {
    if(ITEMS.IGNORED_COMPONENT.length) {
        callback();
    } else {
        ajax("../../../common/component/getIgnoredComponents?", null, function(res) {
            if (res.result == 0) {
                $.each(res.data, function() {
                    ITEMS.IGNORED_COMPONENT.push({
                        title: this.componentTitle + "（" + this.ignoreId + "）",
                        value: this.ignoreId
                    });
                });
            }
            callback();
        });
    }
}
/**
 * @brief _getFuncSlot
 * 获取功能列表
 * @param callback
 *
 * @return
 */
function _getFuncSlot(callback) {
    if(ITEMS.FUNC_SLOT.length) {
        callback();
    } else {
    	ajax("../getFuncMask?", null, function (res){
            if (res.result == 0) {
                $.each(res.data, function(index, val) {
                    ITEMS.FUNC_SLOT.push({
                        title: val,
                        value: index
                    });
                });
            }
            callback();
        });
    }
}
/**
 * @brief _showFuncSlot
 * 设置功能标志
 * @param o
 *
 */
function _showFuncSlot(o) {
    var show = function() {
        FUNCFIELDSET.getElement().find(":checkbox").attr("checked", false);
        var values = [];
        for(var i = 0; i < ITEMS.FUNC_SLOT.length; i++) {
            if((1<<ITEMS.FUNC_SLOT[i].value & parseInt($("#J_setFuncBtn").data("data"), 10)) > 0) {
                values.push(ITEMS.FUNC_SLOT[i].value);
            }
        }
        FUNCFIELDSET.setValues({ func_slot: values });
        FUNCDIALOG.show();

    };
    if (!FUNCDIALOG) {
        var options = {
                items: [{
                    label: {
                        title: "功能标志：",
                        className: "title-block"
                    }
                }]
            },
            setup = function() {
                FUNCFIELDSET= new tm.form.fieldSet(options);
                FUNCDIALOG = $.Dlg.Util.popup({
                    id : "J_setFunc",
                    title: "设置功能标志",
                    contentHtml: $("<form>").append(FUNCFIELDSET.getElement()),
                    save: function(con) {
                        var funcId = 0, funcTitle = [];
                        $("#J_setFunc input[name=func_slot]:checked").each(function() {
                            funcId += 1<<$(this).val();
                            funcTitle.push($(this).parent().text());
                        });
                        var objGame = $("#J_addGame").is(":visible") ? $("#J_addGame") : $("#J_updGame");
                        objGame.find("input[name=func_slot]").val(funcId);
                        objGame.find("input[name=func_title]").val(funcTitle);
                    }
                });
                show();
            },
            callback = function() {
                options.items[0].items = _setFormInlineItems("func_slot", ITEMS.FUNC_SLOT, "checkbox");
                setup();
            };
        _getFuncSlot(callback);
    } else {
        show();
    }
}
/**
 * @brief
 *
 * @param o
 *
 * @return
 */
function _showIgnoreComponent(o) {
    var show = function() {
        var values = $("#J_setIgnoreBtn").data("data");
        IGNOREFIELDSET.setValues({ ignore: (values ? values.split("_") : []) });
        IGNOREDIALOG.show();

    };
    if (!IGNOREDIALOG) {
        var options = {
                items: [{
                    label: {
                        title: "模块：",
                        className: "title-block"
                    }
                }]
            },
            setup = function() {
                IGNOREFIELDSET = new tm.form.fieldSet(options);
                IGNOREDIALOG = $.Dlg.Util.popup({
                    id : "J_setIgnore",
                    title: "设置",
                    contentHtml: $("<form>").append(IGNOREFIELDSET.getElement()),
                    save: function(con) {
                        var ignoreId = [], ignoreTitle = [];
                        $("#J_setIgnore input[name=ignore]:checked").each(function() {
                            ignoreId.push($(this).val());
                            ignoreTitle.push($(this).parent().text());
                        });
                        var objGame = $("#J_addGame").is(":visible") ? $("#J_addGame") : $("#J_updGame");
                        objGame.find("input[name=ignore]").val(ignoreId.join("_"));
                        objGame.find("input[name=ignore_title]").val(ignoreTitle);
                    }
                });
                show();
            },
            callback = function() {
                options.items[0].items = _setFormInlineItems("ignore", ITEMS.IGNORED_COMPONENT, "checkbox");
                setup();
            };
        _getIgnoredComponents(callback);
    } else {
        show();
    }
}
function _getOptionsConfig() {
    return {
        items: [{
            label: {
                title: "游戏ID",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "game_id",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "游戏名称",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "game_name",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "类型",
                className: "title-inline"
           }
        }, {
          	label: {
                title: "查看权限ID",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "auth_id",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "管理权限ID",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "manage_auth_id",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "online查看权限ID",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "online_auth_id",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "状态",
                className: "title-inline"
           }
        },{
            label: {
                title: "邮件地址",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "game_email",
                className: "ipttxt"
            }]
        }, {
            label: {
                title: "功能标志",
                className: "title-inline"
            },
            items: [{
                type: "text",
                name: "func_title",
                attr: {
                    readonly: "readonly"
                },
                className: "ipttxt"
            }, {
                type: "hidden",
                name: "func_slot"
            }, {
                type: "button",
                className: "btn-green",
                title: "设置",
                attr: { id: "J_setFuncBtn" },
                eventClick: function(o) {
                    _showFuncSlot(o);
                }
            }]
        }, {                                      
            label: {                              
                title: "忽略项",                  
                className: "title-inline"         
           },                                    
           items: [{                             
               type: "text",                     
               name: "ignore_title",             
               attr: {                           
                    readonly: "readonly"          
               },                                
               className: "ipttxt"               
           }, {                                  
               type: "hidden",                   
               name: "ignore"                    
           }, {                                  
               type: "button",                   
               className: "btn-green",           
               title: "设置",                    
               attr: { id: "J_setIgnoreBtn" },   
               eventClick: function(o) {         
                   _showIgnoreComponent(o);      
               }                                 
           }]
        }]
    };
}
function ajaxData(url, param, fn, type){
    overlayer({ text: "加载中..."});
    ajax(url, param, function (res) {
    	console.log(res);
        if (res.result == 0) {
        	console.log(2222222);
            hidelayer("加载成功~.~");
            if(fn) fn(res.data);
        } else {
            hidelayer("出错了");
            say("获取数据错误：" + res.err_desc);
        }
    }, type);
}
})(jQuery);