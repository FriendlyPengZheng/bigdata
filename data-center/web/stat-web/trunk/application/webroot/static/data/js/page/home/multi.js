$(function(){
    _g_widget();
    _show_widget();
});
function _g_widget(){
    var addWidgetBtn = $("#J_addWidgetBtn"),
        widgetHtml = $.widgetfac({
            id: "J_drawType",
            widget: [{ value : 1, classname : "p-line", name : lang.t("线图") },{
                value : 2, classname : "p-column-stack", name : lang.t("堆积柱形图")}, {
                value : 3, classname : "p-table", name : lang.t("表格")}, {
                value : 4, classname : "p-column-percent", name : lang.t("百分比堆积柱形图")}, {
                value : 5, classname : "p-column-standard", name : lang.t("簇状柱形图")}]
        }),
        noticeHtml = $.favorfac({ type: 4 }),
        indicatorHtml = $.favorfac({
            type: 2,
            title: lang.t("选择指标："),
            attr: { id: "J_indicator" },
            add: true
        }),
        nameHtml = $.favorfac({
            type: 1,
            title: lang.t("设置窗口小部件标题："),
            input: {
                id: "J_inputTitle",
                dftName: lang.t("新窗口小部件")
            }
        });

    DIALOG.widget = $.Dlg.Util.popup({
        id: "J_widget",
        title: lang.t("添加窗口小部件"),
        contentHtml: widgetHtml + noticeHtml + indicatorHtml + nameHtml,
        saveCallback: function(){
            _add_widget();
        },
        callback: function(con){
            con.find(".widget-type-con").click(function(e){ e.stopPropagation();
                _e_widget_type($(this));
            });
            con.find(".add-m").click(function(e){ e.stopPropagation();
                _create_indicator();
            });
            $("#J_inputTitle").click(function(e){ e.stopPropagation();
                $(this).select();
            });
        }
    });
    addWidgetBtn.click(function(e){ e.stopPropagation();
        _clear();
        _create_indicator();
        DIALOG.widget.show();
    });

}

/**
 * @brief _add_widget
 */
function _add_widget(){
    var widget = $("#J_widget"),
        indicator = $("#J_indicator"),
        r = widget.data("r");

    if(_check_widget()){
        var param = {
            favor_id : $("#J_gFavorId").val(),
            draw_type : $("#J_drawType").find(".active").attr("data-type") ,
            indicator : [],
            collect_name : $("#J_inputTitle").val()
        };

        indicator.find(".sel-con").each(function(){
            var t = $(this),
                gpzsTitle = t.find(".zone-server").find(".title-m .t-name"),
                reportTitle = t.find(".report").find(".title-m .t-name"),
                settings = [],
                id = reportTitle.attr("data-id").split("|");

            if( gpzsTitle.attr("data-id") && reportTitle.attr("data-id") ){
                if( reportTitle.attr("data-child") ){
                    var ulSet = t.closest(".sel-con").find(".sel-ul");
                    ulSet.find(".act-li").each(function(){
                        settings.push( $(this).attr("data-id") );
                    });
                }
                param.indicator.push({
                    type : id[0],
                    id : id[1],
                    settings : settings,
                    gpzs_id: gpzsTitle.attr("data-id")
                });
            }
        });

        if(r){ //update widget
            param.collect_id = r.container.attr("data-id");
            ajaxData(getUrl("home", "collect", "set"), param, function(data){
                DIALOG.widget.fadeOut();
                r.renameWrap(param.collect_name);
                _widget_content_fac(param, r.getContent().empty());
            }, true);
        } else { //add new widget
            ajaxData(getUrl("home", "collect", "add"), param, function(data){
                param.collect_id = data;
                DIALOG.widget.fadeOut();
                _widget_fac(param, _get_widget_con());
            }, true);
        }
    }
}

/**
 * @brief _upd_widget
 * 修改小窗口信息
 * @param r：每个模块对象
 * @return
 */
function _upd_widget(r){
    _clear();
    var widget = $("#J_widget");
    ajaxData(getUrl("home", "collect", "getUpdatingInfo"), {
        collect_id : r.container.attr("data-id")
    }, function(data){
        widget.data("r", r);
        widget.find(".ui-title").text(lang.t("修改窗口小部件"));
        $("#J_drawType").find('.widget-type-con[data-type="' + data.draw_type + '"]').addClass("active").siblings().removeClass("active");
        $("#J_inputTitle").val(data.collect_name);
        if(data.indicator.length){
            $.each(data.indicator, function(){
                _create_indicator([this.game_id], [this.platform_id], [this.gpzs_id], [this.type+"|"+this.id], this.settings);
            });
        } else {
            _create_indicator();
        }
        DIALOG.widget.show();
    }, true);
}
/**
 * @brief _check_widget
 * record-> table : 1000 ,other : 100
 * @return
 */
function _check_widget(){
    var ok = true,
        body = $("body"),
        indicator = $("#J_indicator"),
        inputTitle = $("#J_inputTitle"),
        drawType = $("#J_drawType").find(".widget-type-con.active").attr("data-type"),
        record = parseInt( body.data("multi-record"), 10 );

    /* record */
    if( drawType == 3 ){
        if( record > 1000 ){
            say(lang.t("表格显示形式只能选择{1}条数据~.~", "1000"));
            ok = false; return ok;
        }
    } else{
        if( record > 100 ) {
            say(lang.t("非表格显示形式只能选择{1}条数据~.~", "100"));
            ok = false; return ok;
        }
    }
    /* check param*/
    if( indicator.find(".sel-con").find(".report .selected-m").length < 1 ){
        indicator.find(".title").hint();
        ok = false; return ok;
    }
    if( !inputTitle.val() ){
        inputTitle.hint();
        ok = false; return ok;
    }
    return ok;
}
function _clear(){
    $("body").data("multi-record", 0);
    $("#J_widget").data("r", null);
    $("#J_widget").find(".ui-title").text(lang.t("添加窗口小部件"));
    $("#J_drawType").find('.widget-type-con:first').addClass("active").siblings().removeClass("active");
    $("#J_indicator").find(".sel-wrapper").empty();
    $("#J_inputTitle").val(lang.t("新窗口小部件"));
}
/**
 * @brief _e_widget_type
 * widget type绑定click事件
 * @param t
 */
function _e_widget_type(t){
    if(!t.hasClass('active')){
        t.siblings().removeClass('active');
        t.addClass('active');
    }
}
function _create_indicator( item, platform, zoneServer, report, settings ){
    var indicator = $("#J_indicator"),
        selWrap = indicator.find(".sel-wrapper"),
        selCon = $(document.createElement("div")).addClass("sel-con")
            .css({ 'position' : 'relative', 'z-index' : _get_index() }),
        selP = $(document.createElement("div")).addClass("sel-p"),
        html =  '<ul>'
            +   '<li class="sel-p-li item"></li>'
            +   '<li class="sel-p-li platform"></li>'
            +   '<li class="sel-p-li zone-server"></li>'
            +   '<li class="sel-p-li report"></li>'
            + '</ul>',
        addIndicator = indicator.find(".add-m");

    selCon.append(selP.html(html)).appendTo(selWrap);
    item = item ? item : [];
    platform = platform ? platform : [];
    zoneServer = zoneServer ? zoneServer : [];
    report = report ? report : [];
    settings = settings? settings : [];
    _create_item( selP, item, platform, zoneServer, report, settings );
}

function _create_item( con, sel, platform, zoneServer, report, settings ){
    var indicator = $("#J_indicator"),
        data = indicator.data("item"),
        prev = con.parent().prev().find(".sel-p ul .item").find(".selected-m");
    sel = sel.length ? sel : ( prev.length ? [prev.find(".title-m .t-name").attr("data-id")] : [] );
    if( !sel.length ){
        _create_platform(con);
        _create_report(con);
    }
    var opts = {
        search : true,
        callback : function( curObj, title ){
            _create_platform( con, title.attr("data-id"), platform, zoneServer );
            _create_report( con, title.attr("data-id"), report, settings );
        },
        type : 1,
        selected : sel ? sel : [],
        obj : con.find(".item")
    };
    if( data ){
        opts.data = data;
        $.choose.core( opts );
    } else {
        ajaxData(getUrl("common", "game", "getGameList"), null, function(data){
            data = _handle_choose( data, "game" );
            indicator.data( "item", data );
            opts.data = data;
            $.choose.core( opts );
        });
    }
}

function _create_platform( con, id, sel, zoneServer ){
    var indicator = $("#J_indicator"),
        data = indicator.data("platform" + id),
        prevCon = con.closest(".sel-con").prev(),
        prevItem = prevCon.find(".item .selected-m").find(".title-m .t-name"),
        prev = prevCon.find(".sel-p ul .platform").find(".selected-m");
    sel = sel && sel.length ? sel
            : ( prevItem.length && prevItem.attr("data-id") == id && prev.length
                    ? [prev.find(".title-m .t-name").attr("data-id")] : [] );
    if( !sel.length ){
        _create_zone(con);
    }
    var opts = {
        search : true,
        callback : function( curObj, title ){
            _create_zone( con, title.attr("data-id"), id, zoneServer );
        },
        type : 1,
        selected : sel ? sel : [],
        obj : con.find(".platform").empty()
    };

    if( id ){
        if(data){
            opts.data = data;
            $.choose.core(opts);
        } else {
            ajaxData(getUrl("common", "gpzs", "getPlatform"), {
                game_id: id
            }, function(data){
                data = _handle_choose( data, "platform" );
                indicator.data("platform" + id, data );
                opts.data = data;
                $.choose.core( opts );
            });
        }
    } else {
        opts.data = [];
        $.choose.core( opts );
    }
}
function _create_zone( con, id, gameId, sel ){
    var indicator = $("#J_indicator"),
        prevCon = con.closest(".sel-con").prev(),
        prevItem = prevCon.find(".item .selected-m").find(".title-m .t-name"),
        prev = prevCon.find(".sel-p ul .zone-server").find(".selected-m");
    sel = sel && sel.length ? sel
            : ( prevItem.length && prevItem.attr("data-id") == gameId && prev.length
                    ? [prev.find(".title-m .t-name").attr("data-id")] : [] );
    var opts = {
        search : true,
        type : 1,
        selected : sel ? sel : [],
        obj : con.find(".zone-server").empty()
    };

    if( id ){
        ajaxData(getUrl("common", "gpzs", "getZoneServer"), {
            game_id : gameId,
            platform_id: id
        }, function(data){
            data = _handle_choose( data, "gpzs" );
            opts.data = data;
            $.choose.core( opts );
        });
    } else {
        opts.data = [];
        $.choose.core( opts );
    }
}
/**
 * @brief _create_report
 *
 */
function _create_report( con, id, sel, settings ){
    var selWrap = con.closest('.sel-wrapper'),
        selUl = con.closest('.sel-con').find('.sel-ul');

    if( selUl.length ){ selUl.remove(); }
    var opts = {
        search : true,
        type : 2,
        page : 2,
        selected : sel ? sel : [],
        obj : con.find(".report").empty(),
        mulRadio : 1,
        isRecord : true,
        recordClass : "multi-record",
        getData : function( id, fn ){
            id = id.split("|");
            ajaxData(getUrl("common", "stat", "getSettings"),{
                type: id[0],
                r_id: id[1]
            }, function(data){
                data = _handle_setting_choose(data, (settings ? settings : []))
                if(fn) fn(data);
            });
        }
    };
    var delRFn = function(){
        //delete every indicator(including item + zone-server + event )
        var delR = con.find(".del-r");
        if( delR.length == 0 ){
            $(document.createElement("span")).addClass("del-r")
                .click(function(e){ e.stopPropagation();
                    if( selWrap.find(".sel-con").length > 1 ){
                        var selCon = $(this).closest(".sel-con");
                        _handle_record( selCon, "multi-record");
                        selCon.remove();
                    }
                }).appendTo( con );
        }
    };

    if( id ){
        ajaxData(getUrl("common", "stat", "getIndicators"),{
            game_id : id,
            type: ["set", "report", "diy"]
        }, function(data){
            data = _handle_indicator_choose( data );
            opts.data = data;
            $.choose.core( opts );
            delRFn();
        });
    } else {
        opts.data = [];
        $.choose.core( opts );
        delRFn();
    }
}
function _handle_choose(data, pre){
    var rlt = [],
        preName = pre;
    if(preName == "platform") preName = "gpzs";
    if(data && data.length){
        $.each(data,function(){
            var title = (pre == "gpzs" && this.zone_id == -1 && this.server_id == -1) ? lang.t("全区全服") : this[preName + "_name"];
            rlt.push({
                title : title,
                attr : { id : this[pre + "_id"] }
            });
        });
    }
    return rlt;
}
/**
 * @brief getPageParameters
 * 获取页面公共参数
 */
function getPageParameters() {
    return {
        from: $("#J_showFrom").val(),
        to: $("#J_showTo").val()
    };
}
/**
 * @brief getPageParameters
 * 获取页面下载公共参数
 */
function getDownloadParameters() {
    return {
        from: $("#J_from").val(),
        to: $("#J_to").val()
    };
}
