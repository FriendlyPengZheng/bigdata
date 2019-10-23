$(function(){
    var $platform = $("#J_platform");
    getZoneServer( $platform.find(":selected").attr("data-id"), function( data ){
        _zoneServer_fac( data );
        _show_widget();
    });
    $platform.tmselect().change(function(e){
        getZoneServer( $(this).find(":selected").attr("data-id"), function( data ){
            _zoneServer_fac( data, true );
        });
    });
    _g_widget();
});

/**
 * @brief _zoneServer_fac
 * 生成区服列表并绑定事件
 * @param data array 区服列表
 * @return
 */
function _zoneServer_fac( data, firstClick ) {
    firstClick = firstClick ? firstClick : false;
    var $zoneServer = $("#J_zoneServer");
    $.Select.setOptionContent( $zoneServer, data );
    $.Select.bindEvents( $zoneServer, firstClick, function(){
        _refresh_data();
    });
}
/**
 * @brief getZoneServer
 * 获取区服列表
 * @param id ：平台id
 * @return
 */
function getZoneServer( id, fn ) {
    ajax(getUrl("common", "gpzs", "getZoneServer"), {
        game_id : $("#J_gGameId").val(),
        platform_id : id
    }, function(res){
        if (res.result == 0) {
            var data = [];
            $.each( res.data, function( i ){
                data.push({
                    id : this.gpzs_id,
                    name : ( this.zone_id == -1 && this.server_id == -1 ) ? lang.t("全区全服") : this.gpzs_name,
                    selected : i == 0 ? true : false
                });
            });
            if( fn )fn( data );
        } else {
            say(lang.t("获取数据错误：") + res.err_desc);
        }
    });
}

function _g_widget() {
    var addWidgetBtn = $("#J_addWidgetBtn"),
        itemHtml = $.favorfac({ title: lang.t("项目：") + $("#J_gGameName").val() }),
        indicatorTableHtml = $.favorfac({
            type: 5,
            title: lang.t("数据调整："),
            attr: { id: "J_adjustTable" },
            add: false
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
        contentHtml: itemHtml  + nameHtml + indicatorTableHtml,
        saveCallback: function(){
            _add_widget();
        },
        callback: function(con){
            con.find(".widget-type-con").click(function(e){
                e.stopPropagation();
                _e_widget_type($(this));
            });
            con.find(".add-m").click(function(e){
                e.stopPropagation();
                _create_indicator();
            });
            $("#J_inputTitle").click(function(e){
                e.stopPropagation();
                $(this).select();
            });
        }
    });
    addWidgetBtn.click(function(e) {
        e.stopPropagation();
        _clear();
        _create_indicator();
        DIALOG.widget.show();
        $("#J_widget").css({
            'min-width': '900px'
        });
        $("#J_widget").css({
            'left': String(($(document).width() - $("#J_widget").width()) / 2) + "px",
            'top': String(($(window).height() - $("#J_widget").height()) / 2) + "px"
        });
    });
}
function _clear() {
    $("body").data("report-record", 0);
    $("#J_widget").data("r", null);
    $("#J_widget").find(".ui-title").text(lang.t("添加窗口小部件"));
    $("#J_drawType").find('.widget-type-con:first').addClass("active").siblings().removeClass("active");
    $("#J_adjustTable").find("#J_sortable").empty();
    $("#J_indicator").find(".sel-wrapper").empty();
    $("#J_inputTitle").val(lang.t("新窗口小部件"));
}
/**
 * @brief _e_widget_type
 * widget type绑定click事件
 * @param t
 */
function _e_widget_type(t) {
    if(!t.hasClass('active')){
        t.siblings().removeClass('active');
        t.addClass('active');
    }
}
/**
 * @brief _upd_widget
 * 修改小窗口信息
 * @param r：每个模块对象
 * @return
 */
function _upd_widget(r) {
    _clear();
    var widget = $("#J_widget"),
        adjInd = $("#J_sortable"),
        contWidth = 0;
    ajaxData(getUrl("home", "collect", "getAdjustingInfo"), {
        collect_id : r.container.attr("data-id")
    }, function(data) {
        widget.data("r", r);
        widget.attr({
            "calc-option": r.container.attr("calc-type"),
            "calc-row-option": r.container.attr("calc-row-option")
        });
        widget.find(".ui-title").text(lang.t("修改窗口小部件"));
        $("#J_inputTitle").val(data.collect_name);
        // generate indicator 
        if (data.indicator.length) {
            $.each(data.indicator, function() {
                _createAdjustTable(this.data_name, this.data_key, data.indicator.length);
            });
        }
        // set ul height, 26px = li height(22px) + margin-bottom(4px)
        var ulHeight = (parseInt($(window).height() / (26 * 2))) * 26,
            preHeight = adjInd.children().length * 26;
        if (preHeight > ulHeight) {
            adjInd.height(ulHeight > 26 * 3 ? ulHeight : 26 * 3);
            adjInd.css({ 'overflow-y': 'auto' });
        }
        // set ul sortable
        adjInd.sortable({
            scroll: true,
            scrollSensitivity: 10,
            cancel: "input",
            placeholder: "sortable-placeholder",
            containment: widget,
            stop: function (event, ui) {
                msglog(ui.item.attr('stid'), ui.item.attr('sstid'), ui.item.attr('item'));
            }
        });
        _setAdjTable();
        DIALOG.widget.show();
        // fixed widget width
        adjInd.children().each(function () {
            var tmpWidth = parseFloat($(this).children(':first').width());
            if (contWidth < tmpWidth) contWidth = tmpWidth;
        });
        var newWidth = parseInt(contWidth / 2) * 3;
        if (newWidth > 300) $('.ui-dlg').css('min-width', String(newWidth) + 'px');
        else $('.ui-dlg').css('min-width', '300px');
    }, true);
}
/**
 * @brief _add_widget
 */
function _add_widget() {
    var widget = $("#J_widget"),
        adjData = $("#J_adjustTable"),
        r = widget.data("r"),
		calcInfo = "";

    var param = {
        collect_id : r.container.attr("data-id"),
        collect_name : $("#J_inputTitle").val(),
        draw_type: 3,
        indicator: [],
		calc_option: widget.attr("calc-option"),
		calculateRow_option: widget.attr("calc-row-option")
    };

    adjData.find("li").each(function() {
        if ($(this).find('input').val() !== $(this).find('.edit-field').text())
            $(this).find('.edit-field').text($(this).find('input').val());
        
        $(this).find('input').blur();
        var t = $(this),
            name = t.find("span").text(),
            key  = t.find("span").attr("data-key");
        param.indicator.push({
            data_name: name,
            data_key: key
        });
        t.addClass("stat-listener").attr({
            'stid': '我的收藏',
            'sstid': '编辑收藏项操作',
            'item': '收藏指标拖动'
        });        
    });

    ajaxData(getUrl("home", "collect", "adjust"), param, function (data) {
        DIALOG.widget.fadeOut();
        r.renameWrap(param.collect_name);
        _widget_content_fac(param, r.getContent().empty());
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
        record = parseInt( body.data("report-record"), 10 );

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
    if( indicator.find(".sel-con .selected-m").length < 1 ){
        indicator.find(".title").hint();
        ok = false; return ok;
    }
    if( !inputTitle.val() ){
        inputTitle.hint();
        ok = false; return ok;
    }
    return ok;
}

/**
 * @brief _create_indicator
 * 生成指标
 * @param argIndicator：已选中reportlist数组
 * @param argSettings：已选中datalist数组
 * @param upd：当前是否修改状态
 * @return
 */
function _create_indicator(argIndicator, argSettings, upd) {
    var indicator = $("#J_indicator"),
        selWrap = indicator.find(".sel-wrapper"),
        selCon = $(document.createElement("div")).addClass("sel-con")
            .css({ 'position' : 'relative', 'z-index' : _get_index() }),
        selP = $(document.createElement("div")).addClass("sel-p"),
        data = indicator.data("indicator"),
        addIndicator = indicator.find(".add-m");
    var opts = {
        search : true,
        type : 2,
        page : 2,
        obj : selP,
        mulRadio : 1,
        isRecord : true,
        recordClass: "report-record",
        callback : function(curObj, title) {
            _handle_exist( selWrap, curObj, title );
        },
        selected : argIndicator ? argIndicator : [],
        getData : function(id, fn) {
            id = id.split("|");
            ajaxData(getUrl("common", "stat", "getSettings"), {
                type: id[0],
                r_id: id[1]
            }, function(data) {
                data = _handle_setting_choose(data, (argSettings ? argSettings : []));
                if(fn) fn(data);
            });
        },
        showTitleFn : function () {
            _show_title();
        }
    };
    if (data) {
        opts.data = data;
        $.choose.core(opts);
    } else {
        ajaxData(getUrl("common", "stat", "getIndicators"), {
            game_id: $("#J_gGameId").val(),
            type: ["set", "report", "diy"]
        }, function(data){
            data = _handle_indicator_choose(data);
            indicator.data("indicator",data);
            opts.data = data;
            $.choose.core(opts);
        }, false, true);
    }
    var delM = $(document.createElement("span")).addClass("del-m");
    delM.click(function(e) {
        e.stopPropagation();
        if( selWrap.find(".sel-con").length > 1 ){
            var selCon = $(this).closest(".sel-con");
            _handle_record( selCon, "report-record");
            selCon.remove();
            if(!upd)_show_title();
        }
    });
    selP.add(delM).appendTo(selCon.appendTo(selWrap));
}
/**
 * @brief _handle_exist
 *
 * @param selWrap
 * @param curObj: the current selected title
 * @param title
 */
function _handle_exist( selWrap, curObj, title ) {
    var exist = [],
        id = title.attr("data-id"),
        existWraps = curObj.find(".exist-wrap");

    selWrap.find(".sel-con .sel-p").find(".title-m .t-name").not(title).each(function(){
        var id = $(this).attr("data-id");
        if( id ) exist.push( id );
    });
    if( inArray( id, exist ) ){
        if( existWraps.length == 0 ){
            var delExist = $(document.createElement("span")) .addClass("exist-del");
            delExist.click(function(e){
                e.stopPropagation();
                $(this).parent().remove();
            });
            $(document.createElement("div")).addClass("exist-wrap").text(lang.t("该项目已存在。"))
                .append(delExist).appendTo(curObj);
        }
    }else if( existWraps.length > 0){
        existWraps.remove();
    }
}
/**
 * @brief _show_title
 * 添加小窗口部件-动态匹配title功能
 * @return
 */
function _show_title(){
    var titleCon = $("#J_addWidget").find(".title-txt"),
        addIndicator = $("#J_addIndicator"),
        item = $("#J_gGameName").val(),
        indicator = [];

    addIndicator.find(".sel-con .title-m .t-name").each(function(){
        var t = $(this);
        if( t.attr("data-id") ){
            if( t.attr("data-child") == "true" ){
                var selUl = t.closest(".sel-con").find(".sel-ul"), childTitle = '';
                selUl.find(".act-li").each(function(i){
                    childTitle += $(this).find(".child-m").text()
                        + (i == selUl.find(".act-li").length - 1 ? "" : lang.t("、"));
                });
                indicator.push( t.text() + "-" + childTitle );
            }else{
                indicator.push( t.text() );
            }
        }
    });
    titleCon.val( item + (indicator.length > 0 ? "(" + indicator.join(",") + ")" : '') );
}
/**
 * @brief getPageParameters
 * 获取页面公共参数
 */
function getPageParameters() {
    return {
        from: $("#J_showFrom").val(),
        to: $("#J_showTo").val(),
        gpzs_id: $("#J_zoneServer").find(".selected-item").attr("data-id"),
        game_id: $("#J_gGameId").val()
    };
}
/**
 * @brief getDownloadParameters
 * 获取页面下载公共参数
 */
function getDownloadParameters() {
    return {
        from: $("#J_from").val(),
        to: $("#J_to").val(),
        gpzs_id: $("#J_zoneServer").find(".selected-item").attr("data-id"),
        game_id: $("#J_gGameId").val()
    };
}
