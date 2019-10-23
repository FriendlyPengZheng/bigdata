var index = 100;
var VIEW  = { widget: null };

$(document).ready(function () {
	_gHome();
});
//---------------add to favor start--------------
function addToFavor(option) {
    //clear
    $("#J_homePlat").find(".sel-wrapper").empty();

    _createPlatform();
    _createFavor();
    _createCollect(-1); //unknown the favor_id at first time
    _createMatedata(option.options.data_index, option.options.id);
    VIEW.widget.show();
	var fixedTop = parseInt($('body').scrollTop()) + 40;
	$('#J_widget').css('top', fixedTop);
}

function _gHome() {
    var html = '';
    html += '<div class="widget-sel">'
        +       '<span class="note"><span class="c-red">' + lang.t("注：") + '</span>' + lang.t("只能添加到{1}类型的小部件中。", '<span class="c-red">' + lang.t("表格") + '</span>') + '</span>'
        +   '</div>'
        +   '<div class="widget-sel" id="J_type">'
        +       '<span class="sel-title">' + lang.t("我的收藏类型：") + '</span>'
        +       '<label class="mr10"><input type="radio" name="type" value="1" checked>' + lang.t("单项目") + '</label>'
        +       '<label class="mr10"><input type="radio" name="type" value="2">' + lang.t("多项目") + '</label>'
        +   '</div>'
        +   '<div class="widget-sel" id="J_homePlat" style="display:none;">'
        +       '<span class="sel-title">' + lang.t("选择平台区服：") + '<a href="javascript:void(0);" class="add-m">Add</a></span><div class="sel-wrapper"></div>'
        +   '</div>'
        +   '<div class="widget-sel" id="J_favor">'
        +       '<span class="sel-title">' + lang.t("我的收藏：") + '</span> <a class="add-m" href="javascript:void(0);">' + lang.t("新建") + '</a>'
        +       '<div class="sel-wrapper"></div>'
        +   '</div>'
        +   '<div class="widget-sel" id="J_collect">'
        +       '<span class="sel-title">' + lang.t("窗口小部件：") + '</span> <a class="add-m" href="javascript:void(0);">' + lang.t("新建") + '</a>'
        +       '<div class="sel-wrapper"></div>'
        +   '</div>'
        +   '<div class="widget-sel" id="J_metadata">'
        +       '<span class="sel-title">' + lang.t("选择指标：") + '</span>'
		+   '<div class="sel-wrapper"></div>'    // 这里会在后面的逻辑添加选择列表
        +   '</div>';
    VIEW.widget = $.Dlg.Util.popup({
        id: "J_widget",
        title: lang.t("添加到我的收藏"),
        width: '600px',
        contentHtml: html,
        saveCallback: function() {
            _addWidget();
        }
    });
    var plat     = $("#J_homePlat"),    // 选择平台区服
        favor    = $("#J_favor"),    // 我的收藏
        collect  = $("#J_collect"),    // 窗口小部件
		metadata = $("#J_metadata");    // 指标
	
    plat.find(".add-m").click(function(e) {    // +Add 按钮
		e.stopPropagation();
		_createPlatform();
	});
	metadata.find(".sel-all").click(function () {    // 全选功能
		metadata.find('li').each(function () {
			$(this).find('input').not(':checked').trigger("click");
		});
	});
	metadata.find(".sel-reverse").click(function () {    // 反选功能
		metadata.find('li').find('input').each(function () {
			$(this).trigger("click");
		});
	});
    //type
    $("#J_type").find("input[name='type']").click(function(e){
		e.stopPropagation();
		if ($(this).val() == 2) { plat.show(); }    // 如果是多项目的话显示平台
		else { plat.hide(); }    // 单项目就不用了
		_createFavor();    // 生成我的收藏
		_createCollect();    // 生成窗口小部件
	});
    //new favor
    favor.find(".add-m").click(function(e){
		e.stopPropagation();
		var t = $(e.target),
			wrap = favor.find(".sel-wrapper"),
			selCon = favor.find(".sel-con"),
			selLen = selCon.length,
			newTxt = wrap.find(".popup-txt");
		if(t.hasClass('clicked')) {
			t.removeClass('clicked');
			newTxt.hide();
			selCon.show();
		} else {
			t.addClass('clicked');
			selCon.hide();
			if(newTxt.length == 0) {
				$(document.createElement('input')).attr({ "type": "text" }).val(lang.t("未命名我的收藏"))
					.addClass('popup-txt').appendTo(wrap);
			} else {
				newTxt.show();
			}
			collect.find(".add-m").click();
		}
	});
    //new collect
    collect.find(".add-m").click(function(e){
		e.stopPropagation();
		var t = $(e.target),
			wrap = collect.find('.sel-wrapper'),
			selCon = collect.find(".sel-con"),
			selLen = selCon.length,
			newTxt = wrap.find(".popup-txt");
		if(t.hasClass('clicked') && !favor.find('.sel-wrapper .popup-txt').is(':visible')){
			t.removeClass('clicked');
			newTxt.hide();
			selCon.show();
		}else{
			t.addClass('clicked');
			selCon.hide();
			if(newTxt.length != 0) {
				newTxt.show();
			} else {
				$(document.createElement('input')).attr({"type": "text"}).val(lang.t("未命名窗口小部件"))
					.addClass('popup-txt').appendTo(wrap);
			}
		}
	});
}

/**
 * 保存后动作
 */
function _addWidget() {
    var opts = _handleAddToFavor(),
        addCollectFn = function(param) {
            ajaxData(getUrl("home", "collect", opts.addCollectFn), opts, function(){
                say( lang.t("保存成功！"), true );
                VIEW.widget.fadeOut();
            });
        };
    if(opts) {
        if(opts.favor_id) {
            addCollectFn(opts);
        } else {
            ajaxData(getUrl("home", "favor", "add"), {
                layout: 1,
                favor_name: opts.favor_name,
                favor_type: opts.favor_type,
                game_id: opts.game_id ? opts.game_id : 0
            }, function(data) {
                opts.favor_id = data;
                addCollectFn(opts);
            });
        }
    }
}

/**
 * add the gameanalysis info to favor
 * check favor or collect selected
 */
function _handleAddToFavor() {
    var widget = $("#J_widget"),
        metadata = $("#J_metadata"),
        plat = $("#J_homePlat"),
	    favor = $("#J_favor"),
		collect = $("#J_collect"),
		favorNew = favor.find('.add-m'),
		collectNew = collect.find('.add-m'),
        favorType = $("#J_type").find("input[name='type']:checked").val(),
		opts = {
			draw_type :3,
            favor_type : favorType,
			indicator : []
		};
    //指标
    var metadataSeled = metadata.find('.sel-wrapper').find('.selected-m');
    if (metadataSeled.length) {
        opts.indicator.push({
            id: widget.data("indicator_id"),
            type: "set",
            settings: metadataSeled.find(".title-m .t-name").attr("data-id").split(",")
        });
    } else {
        metadata.hint(); return null;
    }
    //平台区服
    if(favorType == 1) {
        opts.game_id = $("#J_paramGameId").val();
    } else {
        //platform & zoneserver
        var actLi = plat.find(".act-li"),
            indicatorTmp = [];
        if(actLi.length) {
            actLi.each(function(){
                var t = $(this);
                $.each(opts.indicator, function(){
                    indicatorTmp.push({
                        id: this.id,
                        type: this.type,
                        settings: this.settings,
                        gpzs_id: t.attr("data-id")
                    });
                });
                opts.indicator = indicatorTmp;
            });
        } else {
            plat.hint(); return null;
        }
    }
    //我的收藏
	if (favorNew.hasClass('clicked')) {//favor_name
		var favorName = favor.find('.sel-wrapper .popup-txt');
		if(favorName.val()) {
			opts.favor_name = favorName.val();
		} else {
			favorName.hint(); return null;
		}
	} else {//favor_id
		var favorSel = favor.find('.sel-wrapper').find('.select-m');
		if(favorSel.hasClass('selected-m')) {
			opts.favor_id = favorSel.find('.title-m .t-name').attr("data-id");
		} else {
			favorSel.hint(); return null;
		}
	}

    // 窗口小部件
	if (collectNew.hasClass('clicked')) {//collect_name
		var collectName = collect.find('.sel-wrapper .popup-txt');
		if(collectName.val()) {
			opts.collect_name = collectName.val();
			opts.addCollectFn = 'add';
		} else {
			collectName.hint(); return null;
		}
	} else {//collect_id
		var collectSel = collect.find('.sel-wrapper').find('.select-m');
		if(collectSel.hasClass('selected-m')) {
			opts.collect_id = collectSel.find('.title-m .t-name').attr("data-id");
			opts.addCollectFn = 'append';
		} else {
			collectSel.hint(); return null;
		}
	}

    return opts;
}

/**
 * @brief _createMatedata
 * 生成指标数据选择框
 */
function _createMatedata(dataIndex, componentId) {
    var metadata = $("#J_metadata"),
        selWrap = metadata.find(".sel-wrapper").empty(),
        selCon = $(document.createElement("div")).addClass("sel-con")
			.css({ 'position': 'relative', 'z-index': 1 }),
        selP = $(document.createElement("div")).addClass("sel-p");
    var option = {
        type: 5,
        search: true,
        data: [],
        obj: selP
    };
    if (dataIndex) {
        ajaxData(getUrl("admin", "manage", "parse"), {
            game_id: $("#J_paramGameId").val(),
            component_id: componentId,
            data_index: dataIndex
        }, function(data) {
            $("#J_widget").data({
                "indicator_id": data.id
            });
            data = _handleMetadataForChoose(data.data_list);
            option.data = data;
            $.choose.core(option);
        });
    } else {
        $.choose.core(option);
    }
    selP.appendTo(selCon.appendTo(selWrap));

	// var selAllHTML = '</span> <a class="sel-all" href="javascript:void(0);">' + lang.t("全选") + '</a>',
	// 	selReverseHTML = '<a class="sel-reverse" href="javascript:void(0);">' + lang.t("反选") + '</a>',
	// 	selOpt = $(document.createElement("div")).addClass("sel-opt");    // .css({ 'position': 'relative', 'z-index': 1 });
	// $(selAllHTML).prependTo($('.search-con'));
}

/**
 * @brief _createPlatform
 * 生成平台区服选择框
 */
function _createPlatform() {
    var plat = $("#J_homePlat"),
        selWrap = plat.find(".sel-wrapper"),
        selCon = $(document.createElement("div")).addClass("sel-con")
			.css({ 'position' : 'relative', 'z-index' : _getIndex() }),
        selP = $(document.createElement("div")).addClass("sel-p"),
        data = plat.data("platform"),    // 之前保存的 data
        addPlat = plat.find(".add-m");
    var opts = {
        search: true,
        type: 2,
        page: 2,
        obj: selP,
        mulRadio: 1,
        callback: function(curObj, title) {
            _handleExist(selWrap, curObj, title);
        },
        getData: function(id, fn) {
            ajaxData(getUrl("common", "gpzs", "getZoneServer"), {
                game_id: $("#J_paramGameId").val(),
                platform_id: id
            }, function(data) {
                data = _handleZoneServerForChoose(data);
                if(fn) fn(data);
            });
        }
    };
    if (data) {
        opts.data = data;
        $.choose.core(opts);
    } else {
    	// TODO 修改ajax数据上传
        ajaxData(getUrl("common", "gpzs", "getPlatform"), {
    	//ajaxData("../common/getPlatform",{
            game_id: $("#J_paramGameId").val()
        }, function(data){
            data = _handlePlatformForChoose(data);
            plat.data("platform", data);    // 保存
            opts.data = data;
            $.choose.core(opts);
        }, false, true);
    }
    var delM = $(document.createElement("span")).addClass("del-m");    // 删除按钮
    delM.click(function(e) {
		e.stopPropagation();
		if( selWrap.find(".sel-con").length > 1 ){
			var selCon = $(this).closest(".sel-con");
			selCon.remove();
		}
	});
    selP.add(delM).appendTo(selCon.appendTo(selWrap));
}

function _handleMetadataForChoose(data) {
    var rlt = [];
    if(data && data.length) {
        $.each(data, function(){
            rlt.push({
                title: this.name,
                attr: { id: this.id }
            });
        });
    }
    return rlt;
}

function _handlePlatformForChoose(data) {
    var rlt = [{
        title: lang.t("选择平台"),
        children: []
    }];
    if(data && data.length) {
        $.each(data, function(){
            rlt[0].children.push({
                title: this.gpzs_name,
                attr: {
                    id: this.platform_id,
                    cid: this.platform_id,
                    child: true
                }
            });
        });
    }
    return rlt;
}

function _handleZoneServerForChoose(data) {
    var rlt = [];
    if(data && data.length){
        $.each( data, function(){
            rlt.push({
                title : (( this.zone_id == -1 && this.server_id == -1 ) ? lang.t("全区全服") : this.gpzs_name),
                attr : { id : this.gpzs_id },
                selected : false
            });
        });
    }
    return rlt;
}

/**
 * @brief _handleExist
 *
 * @param selWrap
 * @param curObj: the current selected title
 * @param title
 */
function _handleExist(selWrap, curObj, title) {
    var exist = [],
        id = title.attr("data-id"),
        existWraps = curObj.find(".exist-wrap");

    selWrap.find(".sel-con .sel-p").find(".title-m .t-name").not(title).each(function(){
        var id = $(this).attr("data-id");
        if ( id ) exist.push( id );
    });
    if ( inArray( id, exist ) ){
        if( existWraps.length == 0 ){
            var delExist = $(document.createElement("span")) .addClass("exist-del");
            delExist.click(function(e){ e.stopPropagation();
										$(this).parent().remove();
									  });
            $(document.createElement("div")).addClass("exist-wrap").text(lang.t("该项目已存在。"))
				.append(delExist).appendTo(curObj);
        }
    } else if ( existWraps.length > 0) {
        existWraps.remove();
    }
}

/**
 * create the select of favor list
 */
function _createFavor() {
	var favor = $("#J_favor"),
		selWrap = favor.find(".sel-wrapper").empty(),
		selcon = $(document.createElement("div")).addClass("sel-con")
            .css({ 'position' : 'relative', 'z-index': 3 }),
		selP = $(document.createElement("div")).addClass("sel-p");
    favor.find(".add-m").removeClass("clicked");
    selWrap.find(".popup-txt").hide();
    var opts = {
        search: true,
        type: 1,
        callback: function(curObj, title) {
			var id = title.attr("data-id");
			_createCollect(id ? id : -1);
        },
        obj: selP
    };
    ajaxData(getUrl("home", "favor", "getList"), null, function(favorlist){
        opts.data = _handleForChoose(favorlist, 'favor');
        $.choose.core(opts);
    });
	selP.appendTo(selcon.appendTo(selWrap));
}

/**
 * Create the select of collect list by choose
 */
function _createCollect(id){
	var collect = $("#J_collect"),
		selWrap = collect.find(".sel-wrapper").empty(),
		selCon = $(document.createElement("div")).addClass("sel-con")
            .css({ 'position' : 'relative', 'z-index': 2 }),
		selP = $(document.createElement("div")).addClass("sel-p");
    collect.find(".add-m").removeClass("clicked");
    selWrap.find(".popup-txt").hide();
    var opts = { search : true, type : 1, obj : selP };
	var fn = function(data){
		opts.data = _handleForChoose(data, 'collect');
        $.choose.core( opts );
	};
    if( id == -1 ){
        fn([]);
    } else {
        ajaxData(getUrl("home", "collect", "getListByFavorId"), {
            favor_id : id
        }, fn);
    }
	selP.appendTo(selCon.appendTo(selWrap));
}

/**
 * We should give the data like [{ title : "", attr : { id : "" }}] when use choose
 */
function _handleForChoose( data, pre ) {
	var rlt = [];
    if( pre == 'favor' ){
        if(data && data.length){
            var gameId = $("#J_paramGameId").val(),
                type = $("#J_type").find("input[name='type']:checked").val();
            $.each( data, function(){
                if( type == 1 && this.favor_type == type && this.game_id == gameId ){
                    rlt.push({
                        title : this[pre + "_name"],
                        attr : { id : this[pre + "_id"] }
                    });
                } else if( type == 2 && this.favor_type == type ){
                    rlt.push({
                        title : this[pre + "_name"],
                        attr : { id : this[pre + "_id"] }
                    });
                }
            });
        }
    } else {
        if (data.self && data.self.length) {
            $.each( data.self, function(){
                rlt.push({
                    title : this[pre + "_name"],
                attr : { id : this[pre + "_id"] }
                });
            });
        }
        if (data.shared && data.shared.length) {
            $.each( data.shared, function(){
                rlt.push({
                    title : this[pre + "_name"],
                attr : { id : this[pre + "_id"] }
                });
            });
        }
    }
	return rlt;
}

/**
 * @brief ajaxData
 * ajax请求数据
 * @param url：请求url链接
 * @param param：参数
 * @param fn：回调函数
 * @param hide：是否显示overlay体醒
 */
function ajaxData(url, param, fn, hide){
    if(hide) overlayer({ text: lang.t("操作中...")});
    ajax(url, param, function(res){
        if(res.result == 0){
            if(hide) hidelayer(lang.t("操作成功~.~"));
            if(fn) fn(res.data);
        } else {
            if(hide) hidelayer();
            say(lang.t("获取数据错误：") + res.err_desc);
        }
    }, "POST");
}

function _getIndex(){
    if( index == 0 ) index = 100;
    return index--;
}
