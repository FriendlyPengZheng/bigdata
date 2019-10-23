var FAVOR = {
    index : 100,
    conCnt : 1
};
var DIALOG = {
    addCenter : null,
    updCenter : null,
    widget : null
};
$(function(){
    _g_tools();
    _g_favor();
});
function _g_tools(){
    //time
    var $from = $("#J_from"),
        $to = $("#J_to"),
        $date = $("#J_date");

    $date.datepick({
        rangeSelect: true,
        monthsToShow: 3,
        monthsToStep: 3,
        monthsOffset: 2,
        shortCut : true,
        maxDate: new Date(),
        onClose: function(userDate) {
            //判断是否是同一时间
            if( userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != $from.val()
                || $.datepick.formatDate("yyyy-mm-dd", userDate[1]) != $to.val()) ){
                var userDate = $date.val().split("~");
                userDate[0] = $.trim(userDate[0]);
                userDate[1] = $.trim(userDate[1]);
                $from.val(userDate[0]);
                $to.val(userDate[1]);
                _handle_show_time( userDate[0], userDate[1] );
                _refresh_data();
            }
        }
    });
    $("#J_from_to").click(function(e){ e.stopPropagation();
        $date.focus();
    });
    $("#J_download").click(function(e){ e.stopPropagation();
        $.download(getUrl("home", "favor", "export"), $.extend(getDownloadParameters(), {
            favor_id : $("#J_gFavorId").val(),
            file_name: $("#J_gFavorName").val()
        }));
    });
    $( "#J_aside" ).find("ul:eq(0) li").droppable({
        tolerance : "pointer",
        over: function (event, ui) {
            var target = $(event.target);
            if( _is_allow(target) ){
                $(ui.helper).find(".mod-title").after(
                    $(document.createElement("span"))
                    .addClass("favor-helper")
                    .text( "(" + lang.t("移动到：") + target.text() + ")" ));
                target.css( { "border-left" : "5px solid #61cc49" });
            } else {
                $(ui.helper).find(".favor-helper").remove();
                target.css( { "border-left" : "5px solid red" });
            }
        },
        out : function(event, ui){
            $(ui.helper).find(".favor-helper").remove();
            $(event.target).css({ "border-left" : "0px" });
        },
        drop: function(event, ui) {
            var target = $(event.target);
            target.css({ "border-left" : "0px" });
            if(_is_allow(target)){
                ajaxData(getUrl("home", "collect", "move"), {
                    collect_id: $(ui.draggable).attr("data-id"),
                    favor_id: target.attr("data-id")
                }, function(){
                    $(ui.draggable).fadeOut( 1500, function(){
                        $(ui.draggable).remove();
                    });
                }, true);
            }
        }
    });
}
/**
 * @brief _is_allow
 * 判断是否是可移动到的收藏
 * @param target: 目标收藏
 *
 * @return
 */
function _is_allow(target){
    var favorType = $("#J_gFavorType").val();
    if(target.hasClass("cur")){
        return false;
    } else {
        return favorType == target.attr("data-type")
            ? ( favorType == 1
                    ? ($("#J_gGameId").val() == target.attr("data-game") ? true : false )
                    : true )
            : false;
    }
}
function _g_favor(){
    var addCenterBtn = $("#J_addCenterBtn"),
        updCenterBtn = $("#J_updCenterBtn"),
        styleHtml =  '<div class="style-wrapper clearfix">'
                +   '<div class="layout-style-con" data-type=1>'
                +       '<div class="layout-style style-100">100%</div>'
                +   '</div>'
                +   '<div class="layout-style-con layout-style-coned" data-type=2>'
                +       '<div class="layout-style fl style-50">50%</div>'
                +       '<div class="layout-style fl style-50">50%</div>'
                +   '</div></div>',
        radioHtml = $.favorfac({
            type: 3,//radio
            title: lang.t("我的收藏类型"),
            radio: [{ value: 1, text: lang.t("单项目"), name: "favor_type"
                },{ value: 2, text: lang.t("多项目"), checked: true, name: "favor_type"
            }]
        }),
        itemHtml = $.favorfac({
            type: 2, //select
            attr: { id: "J_centerItem" },
            css: {
                "position": "relative",
                "z-index": 2,
                "display": "none"
            },
            title: lang.t("选择项目：")
        }),
        nameHtml = $.favorfac({
            type: 1, //input
            title: lang.t("我的收藏名称："),
            input: {
                name: "favor_name",
                dftName: lang.t("我的收藏"),
                placeholder: lang.t("请输入我的收藏的名称：")
            }
        });

    DIALOG.addCenter = $.Dlg.Util.popup({
        id: "J_addCenter",
        title: lang.t("创建我的收藏"),
        contentHtml: styleHtml + radioHtml + itemHtml + nameHtml,
        saveCallback: function(){
            _add_center();
        },
        cancel: function(){
            addCenterBtn.removeClass("clicked");
        },
        close: function(){
            addCenterBtn.removeClass("clicked");
        },
        callback: function(con){
            _item_fac($("#J_centerItem"));
            con.find(".layout-style-con").click(function(e){ e.stopPropagation();
                _layout_style($(this));
            });
            con.find('input[name="favor_type"]').click(function(e){ e.stopPropagation();
                if( $(this).val() == 1 ){
                    $("#J_centerItem").show();
                } else {
                    $("#J_centerItem").hide();
                }
            }).filter(":checked").click();
            con.find('input[name="favor_name"]').click(function(){
                $(this).select();
            });
        }
    });
    addCenterBtn.click(function(e){ e.stopPropagation();
        $(this).addClass("clicked");
        DIALOG.addCenter.show();
    });
    //update center
    DIALOG.updCenter = $.Dlg.Util.popup({
        id: "J_updCenter",
        title: lang.t("自定义我的收藏"),
        contentHtml: styleHtml + nameHtml,
        saveCallback: function(){
            _upd_center();
        },
        callback: function(con){
            con.find(".layout-style-con").click(function(e){ e.stopPropagation();
                _layout_style($(this));
            });
            con.find('input[name="favor_name"]').click(function() {
                $(this).select();
            });
            updCenterBtn.click(function(e){ e.stopPropagation();
                con.find('.layout-style-con[data-type="' + $("#J_gFavorLayout").val() + '"]').click();
                con.find('.title-txt').val($("#J_gFavorName").val());
                DIALOG.updCenter.show();
            });
        }
    });
    //delete the center
    $("#J_delCenterBtn").click(function(e){ e.stopPropagation();
        $.Dlg.Util.confirm(lang.t("确定删除我的收藏？"), lang.t("删除此我的收藏后将从系统中永久消失。"), function(){
            ajaxData(getUrl("home", "Favor", "delete"), {
                "favor_id": $("#J_gFavorId").val()
            }, function(){
                _refresh_page();
            }, true);
        });
    });
    //default center
    $("#J_dftCenterBtn").click(function(e){ e.stopPropagation();
        var t = $(this);
        if( t.attr("data-type") == 1 ){
            ajaxData(getUrl("home", "Favor", "cancelDefault"),{ "favor_id" : $("#J_gFavorId").val() }, function(){
                t.attr({
                    "data-type": 0,
                    "title" : lang.t("设置首次进入页面默认收藏")
                }).text(lang.t("设置为默认收藏"));
            });
        } else {
            ajaxData(getUrl("home", "Favor", "setDefault"),{ "favor_id" : $("#J_gFavorId").val() }, function(){
                t.attr({
                    "data-type": 1 ,
                    "title" : lang.t("取消设置首次进入页面默认收藏")
                }).text(lang.t("取消默认收藏"));
            });
        }
    });
}
/**
 * @brief _add_center
 *
 */
function _add_center(){
    var addCenter = $(DIALOG.addCenter.getContainer());
    if(_check_center(addCenter)){
        var favorType = addCenter.find('input[name="favor_type"]:checked').val();
        ajaxData(getUrl("home", "Favor", "add"), {
            'layout' : addCenter.find(".layout-style-coned").attr("data-type"),
            'favor_name' : addCenter.find(".title-txt").val(),
            'favor_type' : addCenter.find('input[name="favor_type"]:checked').val(),
            'game_id' : favorType == 1 ? $("#J_centerItem").find(".selected-m .title-m .t-name").attr("data-id") : 0
        }, function(data){
            DIALOG.addCenter.fadeOut();
            go(getUrl($("#J_gR").val()) + "&favor_id=" + data );
        });
    }
}

function _upd_center(){
    var updCenter = $(DIALOG.updCenter.getContainer());
    if(_check_center(updCenter)){
        ajaxData(getUrl("home","favor","set"), {
            "favor_id": $("#J_gFavorId").val(),
            "layout": updCenter.find(".layout-style-coned").attr("data-type"),
            "favor_type": $("#J_gFavorType").val(),
            "favor_name": updCenter.find(".title-txt").val(),
            "game_id": $("#J_gGameId").val() ? $("#J_gGameId").val() : 0
        }, function(){
            _refresh_page();
        }, true);
    }
}
/**
 * @brief _refresh_page
 * 刷新页面
 * @return
 */
function _refresh_page(){
    go( getUrl("home", "Favor", "index")
        + "&favor_id=" + $("#J_gFavorId").val()
        + "&from=" + $("#J_showFrom").val()
        + "&to=" + $("#J_showTo").val() );
}
/**
 * @brief _refresh_data
 * 刷新页面数据
 * @return
 */
function _refresh_data(){
    $("#J_contentBody").find(".mod-box").each(function(i){
        window.setTimeout((function(t){
            return function(){
                _widget_content_fac({
                    collect_id : t.attr("data-id"),
                    draw_type:  t.attr("data-type")
                }, t.find(".mod-text-con").empty());
            };
        })($(this)), i * 10);
    });
}
/**
 * @brief _check_center
 *
 * @param $obj(newCenter,updCenter)
 * @return ok
 */
function _check_center( center ){
    var ok = true;
    center.find('.necessary').each(function(){
        var t = $(this);
        if( !t.val() ){ t.hint(); ok = false; return false; }
    });
    if( center.find('input[name="favor_type"]:checked').val()  == 1 ){
        var selectM = $("#J_centerItem").find(".select-m");
        if( !selectM.hasClass("selected-m") ){
            selectM.parent().hint();
            return false;
        }
    }
    return ok;
}
/**
 * @brief _layout_style
 * 我的收藏布局click事件
 * @param t
 */
function _layout_style(t){
    if( !t.hasClass("layout-style-coned") ){
        t.siblings().removeClass("layout-style-coned");
        t.addClass("layout-style-coned");
    }
}

$.extend({
favorfac : function(options){
    var opts = {
clsname: "widget-sel", title: '', type: 0, add: false,
        attr: {}, css: {}, input: {}, radio: []
    };
    $.extend( opts, options );
    var sel = $(document.createElement("div")).addClass(opts.clsname).attr(opts.attr).css(opts.css),
        s = opts.title ? ('<h4 class="title">' + opts.title + '</h4>') : '';
    switch( opts.type ){
        case 1://input
            s += $._input(opts.input);
            break;
        case 2: //select
            s += '<div class="sel-wrapper"></div>'
                + (opts.add ? '<a href="javascript:void(0);" class="add-m">Add</a>' : '');
            break;
        case 3://radio
            s += $._radio(opts.radio);
            break;
        case 4://notice
            s += '<span class="note">'
                + '<span class="c-red">' + lang.t("注：") + '</span>' + lang.t("表格显示形式可支持{1}条数据，其他显示形式可支持{2}条数据。", '<span class="c-red">1000</span>', '<span class="c-red">100</span>') + '</span>';
            break;
        default:
            break;
    }
    sel.html(s);
    return sel.get(0).outerHTML;
},
_input: function(data){
    return '<input type="text" class="title-txt necessary" '
        + (data.name ? 'name="' + data.name + '"': '')
        + (data.id ? ' id="' + data.id + '"' : '')
        + (data.placeholder ? ' placeholder="' + data.placeholder + '"' : '')
        + ' value="' + ( data.value ? data.value : ( data.dftName ? data.dftName : '') ) + '"> ';
},
_radio: function(data){
    var s = '';
    if(data && data.length){
        $.each(data, function(){
            s += '<label class="mr10">'
                + '<input type="radio" name="' + this.name + '" value="' + this.value + '" ' + ( this.checked ? 'checked' : '') + '/>' + this.text
                + '</label>';
        });
    }
    return s;
},
widgetfac: function(options){
    var s = '<div class="widget-type-wrapper clearfix" id = "' + options.id + '">'
        + '<div class="widget-type-w clearfix" >';

    $.each(options.widget, function(i){
        s += '<div class="widget-type-con fl '
            + (i == 0 ? 'active' : '')
            + '" data-type="' + this.value + '">'
            + '<div class="type-tag ' + this.classname + '"></div>'
            + '<div class="type-name">' + this.name + '</div>'
            + '</div>';
    });
    s += '</div></div>';
    return s;
}
});
function _item_fac(con){
    var wrap = con.find(".sel-wrapper"),
        selCon = $(document.createElement("div")).addClass("sel-con").css({ 'position' : 'relative', 'z-index' : 3 }),
        selP = $(document.createElement("div")).addClass("sel-p");
    selP.appendTo(selCon).appendTo(wrap);
    ajaxData(getUrl("common", "game", "getGameList"), null, function(data){
        $.choose.core({
            search: true,
            type: 1,
            selected: [],
            obj: selP,
            data: data && data.length ? _handle_item_choose(data): []
        });
    }, false, true);
}
/**
 * @brief _handle_item_choose
 *
 * @return
 */
function _handle_item_choose(data){
    var rlt = [];
    $.each( data, function(){
        rlt.push({
            title : this.game_name,
            attr : { id : this.game_id }
        });
    });
    return rlt;
}

/**
 * @brief _get_index
 * 获取z-index层级
 * @return
 */
function _get_index(){
    if(FAVOR.index == 0){
        FAVOR.index = 100;
    }
    return FAVOR.index--;
}

/**
 * sub the recordNum
 */
function _handle_record( selCon, className ){
    var select = selCon.find(".select-m"),
        tName = select.find(".title-m .t-name"),
        recordNum = $("body").data(className);

    recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
    if( select.hasClass("selected-m") && select.hasClass(className) ){
        if( tName.attr("data-child") ){
            var childrenLen = selCon.find(".sel-ul .act-li").length;
            childrenLen = childrenLen ? childrenLen : 1;
            recordNum -= childrenLen;
        }else{
            recordNum -= 1;
        }
    }
    $("body").data( className, recordNum );
}
/**
 *  * 选择布局con
 *   */
function _get_widget_con(){
    var widgetCon = $("#J_contentBody").find(".window-layout-container");

    if(FAVOR.conCnt%2){ //1
        FAVOR.conCnt++;
        return widgetCon.first();
    } else { //0
        FAVOR.conCnt++;
        return widgetCon.last();
    }
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
function ajaxData( url, param, fn, hide, empt ){
    if( hide ) overlayer({ text: lang.t("操作中...")});
    ajax( url, param, function(res){
        if(res.result == 0){
            if(hide) hidelayer(lang.t("操作成功~.~"));
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
//-------------------show widget start (used in single and multi)------
/**
 * @brief configure
 * 根据reportlist 显示页面的widget
 * @param data
 *
 */
function _show_widget(){
    ajaxData(getUrl("home", "Collect", "getListByFavorId"), {
        favor_id: $("#J_gFavorId").val()
    }, function(data){
        if(data && data.length){
            $.each(data, function(i){
                var t = this, con = _get_widget_con();
                window.setTimeout((function(con, t){
                    return function(){
                        _widget_fac( t, con );
                    };
                })(con, t), i* 10 );
            });
        }
    });
}

function hugeTableConfigure(list, thead, url) {
    var maxHeight = $(".aside").height() - $("#J_contentBody").position().top - 110 - 17;
    var favorType = $("#J_gFavorType").val();
    var options = {
        type: "hugeTable",
        hide: false,
        dataDelay: true,
        height: maxHeight,
        thead: thead,
        data: [],
        prepareData: function(option){
            var prepare = [];
            $.each(option.data, function() {
                prepare.push([{
                    title: this[0].title,
                    dataId: this[0].dataId
                }]);
            });
            return prepare;
        },
        child: [{
            type: "progressiveTable",
            url: {
                url: url,
                page: function(ids) {
                    var parameters = getPageParameters();
                    for( var i = 0; i < ids.length; i++ ){
                        var tmpId = ids[i].split("^");
                        parameters["data_info[" + i + "][data_id]"]   = tmpId[0];
                        parameters["data_info[" + i + "][gpzs_id]"]   = tmpId[1];
                        parameters["data_info[" + i + "][data_expr]"] = tmpId[2];
                        parameters["data_info[" + i + "][data_name]"] = tmpId[3];
                        parameters["data_info[" + i + "][factor]"]    = tmpId[4];
                        parameters["data_info[" + i + "][precision]"] = tmpId[5];
                        parameters["data_info[" + i + "][unit]"]      = tmpId[6];
                    }

                    parameters["by_data_expr"] = 1;
                    return parameters;
                }
            }
        }]
    };
    $(list).each(function() {
        options.data.push([{
            title: this.data_name,
            dataId: [this.data_id,this.gpzs_id,this.data_expr,this.data_name,this.factor,this.precision,this.unit].join("^")
        }]);
    });
    return options;
}

/**
 * @brief _widget_content_fac
 * 生成每个小窗口内容区
 * @param t： 窗口信息
 * @param con： 窗口container
 */
function _widget_content_fac( t, con ){
    con.addClass("flash-loading");
    if( t.draw_type == 3 ){
        ajaxData(getUrl("home", "collect", "getMetadataWithDate"), $.extend( getPageParameters(), {
            collect_id: t.collect_id
        }), function(c){
            con.removeClass("flash-loading");
            var configure = hugeTableConfigure(c.data, getTheadByDate(c.date.reverse()), getUrl("common", "data", "getTimeSeries"));
            configure.container = con;
            fac([configure]);
        });
    } else {
        ajaxData(getUrl("home","collect","getDataById"), $.extend(getPageParameters(), {
            collect_id: t.collect_id
        }), function(c){
            con.removeClass("flash-loading");
            var columnStack = t.draw_type == 2 ? 'normal' : ( t.draw_type == 4 ? 'percent' : '');
            fac([{
                type: "graph",
                container: con,
                chartType: t.draw_type == 1 ? "line" : "column",
                chartStock: true,
                columnStack: columnStack,
                data: c
            }]);
        });
    }
}
/**
 * @brief _widget_fac
 * 生成每个小窗口，包括内容区
 * @param t： 窗口信息
 * @param con： 窗口container
 */
function _widget_fac( t, con ){
    var configure = [];
    if( t.draw_type == 3 ){
        configure.push({
            type: "wrap",
            container: con,
            draggable: true,
            title: t.collect_name,
            headEnabled: false,
            bottomEnabled: false,
            attr: { "data-id": t.collect_id, "data-type" : t.draw_type },
            edit: function(r){
                _upd_widget(r);
            },
            remove: function(r){
                $.Dlg.Util.confirm(lang.t("确定删除窗口（{1}）？", t.collect_name), lang.t("删除此窗口后将从系统中永久消失。"), function(){
                    ajaxData(getUrl("home", "collect", "delete"), {
                        collect_id:  r.container.attr("data-id")
                    }, function(){
                        r.container.remove();
                    }, true);
                });
            },
            download: function(r) {
                $.download(getUrl("home", "collect", "getDataById"), $.extend(getDownloadParameters(), {
                    collect_id : r.container.attr("data-id"),
                    export: 1,
                    file_name: r.head.find("span.mod-title").text()
                }));
            },
            child: [{
                type: "data",
                url: {
                    extend: getUrl("home", "collect", "getMetadataWithDate", "collect_id=" + t.collect_id),
                    page: function() {
                        return getPageParameters();
                    }
                },
                afterLoad: function(data, container) {
                    fac([$.extend({
                        container: container
                    },
                        hugeTableConfigure(data.data, getTheadByDate(data.date.reverse()), getUrl("common", "data", "getTimeSeries"))
                    )]);
                }
            }]
        });
    } else {
        configure.push({
            type: "wrap",
            container: con,
            title: t.collect_name,
            headEnabled: false,
            bottomEnabled: false,
            draggable: true,
            attr: { "data-id": t.collect_id, "data-type": t.draw_type },
            edit: function(r){
                _upd_widget(r);
            },
            remove: function(r){
                $.Dlg.Util.confirm(lang.t("确定删除窗口（{1}）？", t.collect_name), lang.t("删除此窗口后将从系统中永久消失。"), function(){
                    ajaxData(getUrl("home", "collect", "delete"), {
                        collect_id:  r.container.attr("data-id")
                    }, function(){
                        r.container.remove();
                    }, true);
                });
            },
            download: function(r) {
                $.download(getUrl("home", "collect", "getDataById"), $.extend(getDownloadParameters(), {
                    collect_id : r.container.attr("data-id"),
                    export: 1,
                    file_name: r.head.find("span.mod-title").text()
                }));
            },
            child: [{
                type: "data",
                url: {
                    extend: getUrl("home", "collect", "getDataById", "collect_id=" + t.collect_id),
                    page: function() {
                        return getPageParameters();
                    }
                },
                afterLoad: function(data, container) {
                    var columnStack = t.draw_type == 2 ? 'normal' : ( t.draw_type == 4 ? 'percent' : '');
                    fac([{
                        type: "graph",
                        container: container,
                        columnStack: columnStack,
                        chartStock: true,
                        chartType: t.draw_type == 1 ? "line" : "column",
                        data: data
                    }]);
                }
            }]
        });
    }
    fac(configure);
}
/**
 * @brief getTheadByDate
 * 根据日期获取表格头，用于插件
 *
 * @param dateSeries
 */
function getTheadByDate(dateSeries) {
    var thead = [{
            title: lang.t("日期"),
            css: {width: "200px"}
        }];
    $(dateSeries).each(function() {
        thead.push({
            title: this.toString(),
            className: isWeekend(this) ? "gr" : "",
            css: {width: "80px"}
        });
    });
    return thead;
}
function _handle_graph_data(data){
    var rlt = [],
        len = data.data ? data.data.length : 0;
    for(var i = 0; i < len; i++ ){
        var tmp = {
            data : [],
            pointInterval: data.pointInterval * 1000,
            pointStart: data.pointStart * 1000,
            name : data.data[i].name
        };
        for(var j = 0; j < data.data[0].data.length; j++ ){
            tmp.data.push(parseFloat(data.data[i].data[j]));
        }
        rlt.push(tmp);
    }
    return rlt;
}
function _handle_indicator_choose(data){
    var report = [], set = [], diy = [];
    if(data && data.length){
        $.each(data, function(){
            if(this.type == "report") {
                report.push({
                    title: this.r_name,
                    attr: {
                        id: this.type + "|" + this.r_id,
                        cid: this.type + "|" + this.r_id,
                        child: this.is_multi == "1" ? true : false
                    }
                });
            } else if(this.type == "set") {
                set.push({
                    title: this.r_name,
                    attr: {
                        id: this.type + "|" + this.r_id,
                        cid: this.type + "|" + this.r_id,
                        child: this.is_multi == "1" ? true : false
                    }
                });
            } else if (this.type == "diy") {
                diy.push({
                    title: this.r_name,
                    attr: {
                        id: this.type + "|" + this.r_id,
                        cid: this.type + "|" + this.r_id,
                        child: this.is_multi == "1" ? true : false
                    }
                });
            }
        });
    }
    return [{
        title: lang.t("游戏分析数据"),
        children: set
    },{
        title: lang.t("游戏自定义数据"),
        children: report
    },{
        title: lang.t("游戏自定义加工数据"),
        children: diy
    }];
}
function _handle_setting_choose( data, sel ){
    var rlt = [];

    $.each( data, function(){
        rlt.push({
            title : this.data_name,
            attr : { id : this.id },
            selected : $.tools._in_array( sel, this.id ) ? true : false
        });
    });

    return rlt;
}
//-------------------show widget end (used in single and multi)------

