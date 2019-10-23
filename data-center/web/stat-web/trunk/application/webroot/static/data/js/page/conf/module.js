(function(window, undefined) {
var DIALOG = {
    addModule : null,
    copyModule: null,
    deleteModule: null,
    setComments: null
};
$(function(){
    //显示页面的module list
    getModuleList(0, function(data){
        _recursionShow(data, $("#J_contentBody"));
    });
    var html = '<form method="post" id="J_form">';
    html += mfac.create({ type: "select", config: [{
            type: "select",
            id: "J_addType",
            title: "模块类型",
            name: "component_type",
            config: [{ name: "Wrap", id: "wrap" }, {
                name: "Tabs", id: "tabs" }, {
                name: "Tab", id: "tab" }, {
                name: "Data", id: "data" }, {
                name: "listtable", id: "listtable"
            }]
        }]
    });
    html += '<div id="J_moduleCon"></div></form>';
    DIALOG.addModule = $.Dlg.Util.popup({
        id: "J_addModule",
        title: "添加模块",
        contentHtml : html,
        saveCallback: function(){ _addModule(); },
        close: function(){ _clear(); },
        cancel: function(){ _clear(); },
        callback: function(con){
            $("#J_addType").change(function(e){
                var val = $(this).val();
                con.find("#J_moduleCon").empty().html(mfac.create({
                    type: val
                }));
                if(val == "wrap") {
                    con.find("#J_moduleCon").append((mfac.create({ type: "attribute" })).add(
                            mfac.create({ type: "metadata" }).add(
                                mfac.create({ type: "processdata" }))));
                    con.find("#J_moduleCon").append(mfac.create({
                        type: "checkbox",
                        title: "selControl：",
                        name: "isSelControl",
                        config: [{ title:  "控制", value: 1 }],
                        checkedFn: function(t){
                            var selCon = $("#J_moduleCon").find(".selctl-con");
                            if(t.attr("checked")){
                                if(selCon.length){
                                    selCon.show();
                                } else {
                                    selCon = $(document.createElement("div")).addClass("selctl-con");
                                    t.closest("ul").after(selCon.append(mfac.create({ type: "selcontrol" })));
                                    selCon.append(mfac.create({ type: "selconfig" }));
                                }
                            } else {
                                if(selCon.length) selCon.hide();
                            }
                        }
                    }));
                } else if(val == "tab"){
                    con.find("#J_moduleCon").append(mfac.create({ type: "attribute" }));
                } else if(val == "listtable") {
                    con.find("#J_moduleCon").append(mfac.create({ type: "thead" }))
                        .append(mfac.create({ type: "listtableconfig" }));
                } else if(val == "data") {
                    con.find("#J_moduleCon").append(mfac.create({ type: "urlextend" }));
                    con.find("#J_moduleCon").append(mfac.create({
                        type: "checkbox",
                        title: "isSelControl：",
                        name: "isSelControl",
                        config: [{ title:  "控制URL", value: 1 }],
                        checkedFn: function(t){
                            var selCon = $("#J_moduleCon").find(".selctl-con");
                            if(t.attr("checked")){
                                if(selCon.length){
                                    selCon.show();
                                } else {
                                    selCon = $(document.createElement("div")).addClass("selctl-con");
                                    t.closest("ul").after(selCon.append(mfac.create({ type: "urlmatch" })));
                                }
                            } else {
                                if(selCon.length) selCon.hide();
                            }
                        }
                    }));
                    con.find("#J_moduleCon").append(mfac.create({
                        type: "checkbox",
                        title: "表格呈现：",
                        name: "show_table",
                        config: [{ title:  "显示", value: 1 }],
                        checkedFn: function(t){
                            var tableCon = $("#J_moduleCon").find(".table-con");
                            if(t.attr("checked")){
                                if(tableCon.length){
                                    tableCon.show();
                                } else {
                                    tableCon = $(document.createElement("div")).addClass("table-con");
                                    t.closest("ul").after(tableCon.append(mfac.create({ type: "table" })));
                                    tableCon.append(mfac.create({ type: "thead" }));
                                }
                            } else {
                                if(tableCon.length) tableCon.hide();
                            }
                        }
                    }));
                    con.find("#J_moduleCon").append(mfac.create({
                        type: "checkbox",
                        title: "图呈现：",
                        name: "show_graph",
                        config: [{ title:  "显示", value: 1 }],
                        checkedFn: function(t){
                            var graphCon = $("#J_moduleCon").find(".graph-con");
                            if(t.is(":checked")){
                                if(graphCon.length){
                                    graphCon.show();
                                } else {
                                    graphCon = $(document.createElement("div")).addClass("graph-con");
                                    t.closest("ul").after(graphCon.append(mfac.create({ type: "graph" })));
                                    graphCon.append(mfac.create({ type: "graphconfig" }));
                                }
                            } else {
                                if(graphCon.length) graphCon.hide();
                            }
                        }
                    }));
                }
            }).first().change();
        }
    });
    DIALOG.deleteModule = $.Dlg.Util.popup({
        id : "J_delModule",
        title: "删除模块",
        contentHtml: "确定要永久删除吗？",
        saveCallback: function(){
            ajaxData(getUrl("admin", "manage", "deleteComponent"), {
                component_id : DIALOG.deleteModule.id
            }, function(){
                DIALOG.deleteModule.hide();
                window.location.reload();
            });
        }
    });
    $("#J_addModuleBtn").click(function(e){
        e.stopPropagation();
        DIALOG.addModule.show();
    });
    $("#J_json").click(function(e){
        e.stopPropagation();
        overlayer({ text: "加载中..."});
        ajax(getUrl("admin", "manage", "build"), {
            module_key: _getModuleKey()
        }, function(res){
            if(res.result == 0){
                hidelayer("加载成功~.~");
                say("恭喜你，成功啦~跪安吧~", true);
            } else {
                hidelayer();
                $.Dlg.Util.message("生成失败", "生成失败：" + res.err_desc, "关闭").show();
            }
        }, "POST");
    });
    _gCopy();
});
function _gCopy(){
    DIALOG.copyModule = $.Dlg.Util.popup({
        id : "J_copyModule",
        title: "复制模块",
        contentHtml: '<div><h4 class="title">选择导航：</h4><div id="J_naviSettings"></div></div>',
        callback: function(con){
            con.find(".ui-cont .ui-content").css({ "overflow-y": "initial"});
            _createNaviSettings($("#J_naviSettings"));
        },
        close: function(){
            DIALOG.copyModule.component_id = null;
        },
        cancel: function(){
            DIALOG.copyModule.component_id = null;
        },
        saveCallback: function(){
            //get module key and component_id
            var key = '', selected = $("#J_naviSettings").find(".selected-m .title-m .t-name");
            if(DIALOG.copyModule.component_id && selected.length){
                if(selected.attr("data-child") == "true"){
                    var actLi = $("#J_naviSettings").find(".sel-ul .act-li");
                    if(actLi.length){
                        $(actLi).each(function(i){
                            key += 'module_key[' + i + ']=' + selected.attr("data-id") + '-' + $(this).attr("data-id") + '&';
                        });
                    }
                } else {
                    key += 'module_key[0]=' + selected.attr("data-id") + '&';
                }
                key += 'component_id=' + DIALOG.copyModule.component_id;
                ajaxData(getUrl("admin", "manage", "copyComponent"), key, function(){
                    DIALOG.copyModule.hide();
                    DIALOG.copyModule.component_id = null;
                    say("复制成功~", true);
                });
            } else {
                say("请正确选择导航~");
            }
        }
    });
}
/**
 * @brief _createNaviSettings
 */
function _createNaviSettings(con){
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
    ajaxData(getUrl("admin/manage/getModules"), null, function(data){
        opts.data = _handleNaviChoose(data);
        $.choose.core(opts);
    });
    selP.appendTo(selCon.appendTo(con));
}
/**
 * @brief _handleSettings
 *
 * @param data
 *
 * @return
 */
function _handleSettings(data){
    var rlt = [];
    $.each( data, function(i){
        if ($.isArray(this.children) && this.children.length > 0) {
            var that = this,
                set = _handleSettings(this.children);
            $.each(set, function(j) {
                rlt.push({
                    title : that.name + " - " + this.title,
                    attr : { id : that.key + "-" + this.attr.id },
                    selected : i == 0 && j == 0 ? true : false
                });
            });
        } else {
            rlt.push({
                title : this.name,
                attr : { id : this.key },
                selected : i == 0 ? true : false
            });
        }
    });
    return rlt;
}
/**
 * @brief _handleNaviChoose
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
function _handleNaviChoose(data){
    var rlt = [];
    $(data).each(function(){
        var that = this;
        var tmp = {
            title: this.name,
            attr: { id: this.key },
            children: []
        };
        $(this.children).each(function(){
            tmp.children.push({
                title: this.name,
                attr: {
                    id: that.key + "-" + this.key,
                    child: this.children && this.children.length ? true : false
                },
                data: this.children ? _handleSettings(this.children) : []
            });
        });
        rlt.push(tmp);
    });
    return rlt;
}
var i = 0;
//------------------copy end ------------------
function _recursionShow(data, container){
    if(data && $.isArray(data)){
        $.each(data, function(){
            var that = this,
                module = _showModule(this, container, this.component_type),
                id = module.getId();
            if(id){
                getModuleList(id, function(data){
                    if(that.component_type == "tabs"){
                        _showModule(data, module.getContent(), "tab");
                    } else {
                        _recursionShow(data, module.getContent());
                    }
                });
            }
        });
    }
}
function _clear(){
    DIALOG.addModule.container = null;
    DIALOG.addModule.parent_id = 0;
    DIALOG.addModule.cur = null;
    DIALOG.addModule.curid = null;
    $("#J_addModule").find(".ui-title").text("添加模块");
}
function _updModule(container, id) {
    ajaxData(getUrl("admin", "manage", "getComponentById"), {
        component_id: id
    }, function(data){
        // 目前 getComponentById 请求中没有 theadThermodynamic 的值
        // console.log(data.theadThermodynamic);
        $("#J_addModule").find(".ui-title").text("修改模块");
        $('#J_addType option[value="' + data.component_type + '"]').attr("selected", true);
        $("#J_addType").change();
        switch(data.component_type){
            case "wrap":
                mfac.setValue($("#J_moduleCon"), data, "wrap");
                break;
            case "tab":
                mfac.setValue($("#J_moduleCon"), data, "tab");
                break;
            case "data":
                mfac.setValue($("#J_moduleCon"), data, "data");
                break;
            case "listtable":
                mfac.setValue($("#J_moduleCon"), data, "listtable");
                break;
            default:
                break;
        }
        DIALOG.addModule.cur = container;
        DIALOG.addModule.curid = id;
        DIALOG.addModule.parent_id = data.parent_id;
        DIALOG.addModule.show();
    }, true);
}
function _addModule() {
    if (_checkForm()) {
        var param = {
            module_key : _getModuleKey()
        };
        param = $.extend(param, mfac.getValue($("#J_form"), $("#J_addType").val()));
        param.parent_id = DIALOG.addModule.parent_id ? DIALOG.addModule.parent_id : 0;
        if (DIALOG.addModule.cur && DIALOG.addModule.curid) { //upd
            param.component_id = DIALOG.addModule.curid;
            ajaxData(getUrl("admin", "manage", "updateComponent"), param, function(data) {
                //success => show
                _clear();
                DIALOG.addModule.hide();
                window.location.reload();
            }, true);
        } else { //add
            var container = DIALOG.addModule.container ? DIALOG.addModule.container : $("#J_contentBody");
            ajaxData(getUrl("admin/manage/addComponent"), param, function(data) {
                //success => show
                param.component_id = data.component_id;
                _clear();
                DIALOG.addModule.hide();
                window.location.reload();
            }, true);
        }
    }
}
function _showModule(param, container, type){
    switch(type){
        case "wrap" :
            return fac(_handleWrapData(param, container))[0];
            break;
        case "tabs":
            return  fac(_handleTabsData(param, container))[0];
            break;
        case "tab":
            return fac(_handleTabData(param, container))[0];
            break;
        case "data":
            return  fac(_handleDataData(param, container))[0];
            break;
        case "listtable":
            return  fac(_handleListtableData(param, container))[0];
            break;
        default: break;
    }
}

function _handleTabsData(param, container){
    var prepared = [];
    var tmp = {
        type: "tabwrap",
        title: "Tabs",
        container: container,
        attr: { "data-id": param.component_id },
        toolsConfig: [{
            title: "添加Tab",
            callback: function(container, id, content){
                $('#J_addType option[value="tab"]').attr("selected", true);
                $("#J_addType").change();
                DIALOG.addModule.parent_id = id;
                DIALOG.addModule.container = content;
                $("#J_addModuleBtn").click();
            }
        },{
            title: "删除",
            callback: function(container, id){
                DIALOG.deleteModule.show();
                DIALOG.deleteModule.container = container;
                DIALOG.deleteModule.id = id;
            }
        }]
    };
    prepared.push($.extend({}, param, tmp));
    return prepared;
}
/**
 * @brief _handleTabData
 *
 * @param param : 整个Tab的data array
 * @param container
 */
function _handleTabData(param, container){
    var prepared = [];
    var tmp = {
        type: "tabs",
        tabsSkin: '',
        container: container,
        beforeLoad: function(event, eventData){
            var tab = eventData.tab,
                panel = eventData.panel;
            var container = $(document.createElement("div")).addClass("data-wrap clearfix").appendTo(panel);

            getModuleList(tab.attr("data-id"), function(data){
                _recursionShow(data, container);
            });
        },
        child: []
    };
    $(param).each(function(i){
        if(i == 0 && this.tabsSkin) tmp.tabsSkin = this.tabsSkin;
        var tab = {
            title: this.title,
            attr: { "data-id": this.component_id },
            toolsConfig: [{
                title: "添加child",
                callback: function(container, id, content){
                    $('#J_addType option[value="data"]').attr("selected", true);
                    $("#J_addType").change();
                    DIALOG.addModule.parent_id = id;
                    DIALOG.addModule.container = content;
                    $("#J_addModuleBtn").click();
                }
            },{
                title: "修改",
                callback: function(container, id){
                    _updModule(container, id);
                }
            },{
                title: "删除",
                callback: function(container, id){
                    DIALOG.deleteModule.container = container;
                    DIALOG.deleteModule.id = id;
                    DIALOG.deleteModule.show();
                }
            }]
        }
        for(var i = 0; i < this.attr_key.length; i++ ){
           tab.attr[this.attr_key[i]] = this.attr_value[i]
        }
        tmp.child.push(tab);
    });
    prepared.push(tmp);
    return prepared;
}
function _handleDataData(data, container){
    var prepared = [];
    var tmp = {
        type: data.component_type,
        attr: {
            "data-id" : data.component_id
        },
        container: container,
        url: {
            page: data.urlPage,
            timeDimendion: data.urlTimeDimension,
            extend: []
        },
        toolsConfig: [{
            title: "修改",
            callback: function(container, id){
                _updModule(container, id);
            }
        },{
            title: "删除",
            callback: function(container, id){
                DIALOG.deleteModule.container = container;
                DIALOG.deleteModule.id = id;
                DIALOG.deleteModule.show();
            }
        }],
        child: []
    };
    tmp.url.extend.push("");
    for( var i = 0; i < data.urlExtend.length; i++ ){
        tmp.url.extend.push(data.urlExtend[i]);
    }
    if(data.show_table == 1){
        tmp.child.push(_handleTableData(data));
    }
    if(data.show_graph == 1){
        tmp.child.push(_handleGraphData(data));
    }
    prepared.push(tmp);
    return prepared;
}
function _handleListtableData(data, container){
    var prepared = [];
    var tmp = {
        type: data.component_type,
        attr: {
            "data-id" : data.component_id
        },
        container: container,
        isAjax: data.isAjax,
        url: {
            page: data.urlPage,
            paginationUrl: data.urlPagination,
            extend: data.urlExtend
        },
        pagination: data.enablePagination,
        pagesize: data.pagesize,
        thead: [],
        appendColumns: data.appendColumns,
        toolsConfig: [{
            title: "修改",
            callback: function(container, id){
                _updModule(container, id);
            }
        },{
            title: "删除",
            callback: function(container, id){
                DIALOG.deleteModule.container = container;
                DIALOG.deleteModule.id = id;
                DIALOG.deleteModule.show();
            }
        }]
    };
    if(data.thead_type){
        for(var i = 0; i < data.thead_type.length; i++ ){
            tmp.thead.push({
                type: data.thead_type[i],
                title: data.thead_title[i]
            });
        }
    }
    prepared.push(tmp);
    return prepared;
}
function _handleTableData(data, container){
    var rlt = {
        type: "table",
        thead: [],
        qoq: (data.qoq == 1 ? true : false),
        yoy: (data.yoy == 1 ? true : false),
        hide: (data.hide == 1 ? true : false),
        prepareData: data.prepareData
    };
    if(data.thead_type){
        for(var i = 0; i < data.thead_type.length; i++ ){
            rlt.thead.push({
                type: data.thead_type[i],
                title: data.thead_title[i]
            });
        }
    }
    if(container) rlt.container = container;
    return rlt;
}
function _handleGraphData(data, container){
    var rlt = {
        type: "graph",
        chartType: data.chartType,
        page : (data.chartPage == 1 ? true : false),
        yUnit: data.yUnit,
        lineAreaColumn: (data.lineAreaColumn == 1 ? true : false),
        columnStack: data.columnStack,
        chartStock: (data.chartStock == 1 ? true : false),
        chartConfig: data.chartConfig
    };
    if(container) rlt.container = container;
    return rlt;
}
/**
 * @brief _handleWrapData
 * 处理页面显示wrap模块
 * @param param
 * @return
 */
function _handleWrapData(param, container){
    var prepared = [];
    var tmp = {
        attr: {
            "data-id": param.component_id
        },
        container: container,
        type: param.component_type,
        width: param.width/100,
        contrast: parseInt(param.contrast) ? true : false,
        copy: function(o){
            DIALOG.copyModule.component_id = o.options.attr["data-id"];
            DIALOG.copyModule.show();
        },
        toolsConfig: [{
            title: "添加child",
            callback: function(container, id, content){
                $('#J_addType option[value="tabs"]').attr("selected", true);
                $("#J_addType").change();
                DIALOG.addModule.parent_id = id;
                DIALOG.addModule.container = content;
                $("#J_addModuleBtn").click();
            }
        },{
            title: "修改",
            callback: function(container, id){
                _updModule(container, id);
            }
        },{
            title: "删除",
            callback: function(container, id){
                DIALOG.deleteModule.show();
                DIALOG.deleteModule.container = container;
                DIALOG.deleteModule.id = id;
            }
        }, {
            title: "配置注释",
            callback: function(container, id) {
                _showSetComments(id);
            }
        }]
    };
    for(var i = 0; i < param.attr_key.length; i++ ){
       tmp.attr[param.attr_key[i]] = param.attr_value[i];
    }
    tmp.title = param.title + (param.component_desc ? "（"  + param.component_desc + "）" : "");
    tmp = $.extend(param, tmp);
    prepared.push(tmp);
    return prepared;
}
function getModuleList(id, fn){
    id = id ? id : 0;
    ajaxData(getUrl("admin", "manage", "getComponents"), {
        module_key: _getModuleKey(),
        parent_id: id
    }, function(data){
        if(fn) fn(data);
    }, true, true);
}
/**
 * @brief _getModuleKey
 * get module key
 * game_type - aside_parent - aside_child
 * @return
 */
function _getModuleKey(){
    return $("#J_gNaviKey").val().replace(/\./g, "-");
}
function _checkForm(){
    var ok = true;
    $("#J_form").find(".necessary").each(function(){
        if(!$(this).val()){
            $(this).hint();
            ok = false;
        }
    });
    return ok;
}
//--------------------set comments start ------------
function _showSetComments(id) {
    if(!DIALOG.setComments) {
        DIALOG.setComments = $.Dlg.Util.popup({
            id : "J_setComments",
            title: "配置注释",
            contentHtml: _getCommentHtml(),
            callback: function(con) {
                _createAllCommmentList();
                _bindNewCommentEvent();
            },
            save: function(con) {
                var commentIds = [];
                $("#J_itemCommentList").find(".item-li").each(function(){
                    commentIds.push($(this).attr("comment_id"));
                });
                ajaxData(getUrl("admin", "manage", "saveComments"), {
                    component_id: $("#J_setComments").data("box-id"),
                    comment_id: commentIds
                }, function(data){
                    say("保存成功~", true);
                });
            }
        });
        $(DIALOG.setComments.getContent()).css("overflow-y", "inherit");
    }
    $("#J_setComments").data("box-id", id);
    _getItemCommentList(id);
    _getFetchCommentList(id);
    DIALOG.setComments.show();
}
function _getCommentHtml(){
    return ''
        + '<h4 class="title">注释列表：</h4>'
        + '<ul class="item-list" id="J_itemCommentList">'
        //+     '<li class="item-li">活跃用户数<a href="javascript: void(0);" class="item-li-del" title="删除">&nbsp;</a></li>'
        + '</ul>'
        + '<div class="item-tools">添加注释选择池</div>'
        + '<ul class="item-pool">'
        +    '<li class="widget-sel" id="J_fetchCommentList">'
        +       '<span class="title-block">系统已帮您匹配到<strong class="fetch-note">0</strong>个</span>'
        +       '<ul class="fetch-list">'
        //+           '<li><a href="javascript: void(0);" class="btn-green">新增角色</a></li>'
        +       '</ul>'
        +    '</li>'
        +    '<li class="widget-sel" id="J_commentList">'
        +       '<span class="title-inline">请选择注释:</span>'
        +       '<div class="choose-con-inline"></div>'
        +    '</li>'
        +    '<li class="widget-sel" id="J_newCommentList">'
        +       '<span class="title-inline">新建注释:</span>'
        +       '<div class="sel-wrapper">'
        +           '<span>注释名称：</span><input type="text" class="ipt-txt mr20" name="keyword">'
        +           '<span>注释详情：</span><input type="text" class="ipt-txt w420" name="comment">'
        +           '<a href="javascript: void(0);" title="保存" class="add-comment ml10" id="J_addCommentBtn">&nbsp;</a>'
        +       '</div>'
        +    '</li>'
        + '</ul>';
}

function _createAllCommmentList() {
    var commentData = $("body").data("comment_list");
    var options = { search: true, type: 2, page: 2,
        obj: $("#J_commentList").find(".choose-con-inline"),
        callback: function(cur, title){
            $("#J_itemCommentList").append(_createItemList([{
                comment_id: title.attr("data-id"),
                keyword: title.text()
            }]));
        }
    };
    if(commentData) {
        options.data = commentData;
        $.choose.core(options);
    } else {
        ajaxData(getUrl("common", "comment", "getComments"), null, function(data){
            data = _handleDataForChoose(data);
            $("body").data("comment_list", data);
            options.data = data;
            $.choose.core(options);
        });
    }
}

function _handleDataForChoose(data){
    var rlt = {
        title: "注释",
        children: []
    };
    $.each(data, function(key, value){
        $(value).each(function(){
            rlt.children.push({
                title: this.keyword,
                attr: { id: this.comment_id }
            });
        });
    });
    return [rlt];
}
/**
 * @brief _getItemCommentList
 *
 * @param componentId
 * fetch_type: 1: 已绑定注释，2：系统匹配到的注释
 * @return
 */
function _getItemCommentList(componentId) {
    var itemList = $("#J_itemCommentList");
    itemList.append($("<li>").addClass("loading"));
    ajaxData(getUrl("admin", "manage", "getComments"), {
        component_id: componentId,
        fetch_type: 1
    }, function(data){
        itemList.empty().append(_createItemList(data));
    });
}
/**
 * @brief
 *
 * @param componentId
 * fetch_type: 1: 已绑定注释，2：系统匹配到的注释
 * @return
 */
function _getFetchCommentList(componentId) {
    var fetchList = $("#J_fetchCommentList").find(".fetch-list");
    fetchList.append($("<li>").addClass("loading"));
    ajaxData(getUrl("admin", "manage", "getComments"), {
        component_id: componentId,
        fetch_type: 2
    }, function(data){
        $("#J_fetchCommentList").find(".fetch-note").text(data && data.length ? data.length: 0);
        fetchList.empty().append(_createFetchList(data));
    });
}
/**
 * @brief _bindNewCommentEvent
 * 新增注释并加入注释列表
 * @return
 */
function _bindNewCommentEvent() {
    $("#J_addCommentBtn").click(function() {
        var newCommentList = $("#J_newCommentList"),
            keyword = newCommentList.find("input[name=keyword]"),
            comment = newCommentList.find("input[name=comment]"),
            param = {
                keyword: keyword.val(),
                comment: comment.val()
            };
        if(param.keyword && param.comment) {
            ajaxData(getUrl("common", "comment", "save"), param, function(data){
                param.comment_id = data.comment_id;
                $("#J_itemCommentList").append(_createItemList([param]));
            });
        } else {
            if(!param.keyword) keyword.hint();
            if(!param.comment) comment.hint();
        }
    });
}
/**
 * @brief
 * <li class="item-li">新增用户数<a href="javascript: void(0);" class="item-li-del" title="删除">&nbsp;</a></li>
 * [{
 *      keyword: "",
 *      comment_id: ""
 * }]
 * @return
 */
function _createItemList(list) {
    var items = $();
    for(var i = 0; i < list.length; i++) {
        items = items.add($('<li>').addClass("item-li").attr({
            comment_id: list[i].comment_id
        }).text(list[i].keyword).append($('<a>').addClass("item-li-del").attr({
                href: "javascript: void(0);",
                title: "删除"
            }).click(function(){
                $(this).closest(".item-li").remove();
            })
        ));
    }
    return items;
}
/**
 * @brief _createFetchList
 * <li><a href="javascript: void(0);" class="btn-green">新增用户数</a></li>
 * [{
 *      keyword: "",
 *      comment_id: ""
 * }]
 * @return
 */
function _createFetchList(list) {
    list = $.isArray(list) ? list : [];
    var fetchs = $();
    for(var i = 0; i < list.length; i++) {
        fetchs = fetchs.add($('<li>').attr({
            comment_id: list[i].comment_id
        }).append($('<a>').addClass("btn-green").attr({
                href: "javascript: void(0);",
                comment_id: list[i].comment_id
            }).text(list[i].keyword).click(function(){
                var t = $(this);
                $("#J_itemCommentList").append(_createItemList([{
                    comment_id: t.attr("comment_id"),
                    keyword: t.text()
                }]));
            })
        ));
    }
    return fetchs;
}
//--------------------set comments end   ------------
window.getPageParam = function(){
    return {
        from : "2013-11-01",
        to : "2013-11-30",
        platform_id : -1,
        zone_id : -1,
        server_id : -1,
        gpzs_id : 25,
        game_id : 13
    };
}
window.prepareTableData = function(data){
    var returnValue = [];
    if (!$.isArray(data.key)) return;
    $(data.data).each(function() {
        var that = this;
        if (this.data) {
            $(this.data).each(function(i, value) {
                returnValue[i] = [data.key[i], value, that.qoq[i], that.yoy[i]];
            });
        }
    });
    return returnValue;
}
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
    if(hide) overlayer({ text: "加载中..."});
    ajax( url, param, function(res){
        if(res.result == 0){
            if(hide) hidelayer("加载成功~.~");
            if(fn) fn(res.data);
        } else {
            if(hide) hidelayer();
            if(empt){
                if(fn)fn([]);
            } else {
                say("获取数据错误：" + res.err_desc);
            }
        }
    }, "POST");
}
})(window);
