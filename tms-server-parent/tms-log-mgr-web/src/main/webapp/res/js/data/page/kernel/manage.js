(function(window, undefined) {
var index = 100;
var DIALOG = {
    addEmail : null
};
$(function(){
    _formatEmail();
    DIALOG.addEmail = $.Dlg.Util.popup({
        id: "J_addEmail",
        title: lang.t("添加Email"),
        contentHtml: _getEmailContent(),
        saveCallback: function(){
            _add_email();
        },
        callback: function(con){
        }
    });
    $("#J_addEmailBtn").click(function(e){
        e.stopPropagation();
        _clear();
        _create_indicator();
        DIALOG.addEmail.show();
    });
});
function _formatEmail(){
    $("#J_datalist").find("tbody tr").each(function(){
        var t = $(this),
            receivers = t.find(".td:eq(2)"), receiversArr = receivers.text().split(";"), receiversHtml = '',
            cc = t.find(".td:eq(3)"), ccArr = cc.text().split(";"), ccHtml = '',
            test_receivers = t.find(".td:eq(4)"), testReceiversArr = test_receivers.text().split(";"), testReceiversHtml = '';
        for(var i = 0; i < receiversArr.length; i++){
            receiversHtml += receiversArr[i] + ( receiversArr[i] ? ";<br/>" : "" );
        }
        receivers.html(receiversHtml);
        for(var j = 0; j < ccArr.length; j++){
            ccHtml += ccArr[j] + ( ccArr[j] ? ";<br/>" : "" );
        }
        cc.html(ccHtml);
        for(var i = 0; i < testReceiversArr.length; i++){
            testReceiversHtml += testReceiversArr[i] + ( testReceiversArr[i] ? ";<br/>" : "" );
        }
        test_receivers.html(testReceiversHtml);
        //operate
        t.find(".email-edit").click(function(e){
            e.stopPropagation();
            _upd_email(t);
        });
        t.find(".email-del").click(function(e){
            $.Dlg.Util.confirm(lang.t("确定删除？"), lang.t("删除此Email后将从系统中永久消失。"), function(){
                ajaxData("../../../admin/email/delete", {
                    email_id:  t.attr("data-id")
                }, function(){
                    t.remove();
                }, true);
            });
        });
        t.find(".email-auto").click(function(e){
            var _this = $(this),
                status = t.attr("data-status"),
                title, content;
            if (status === "1") {
                title = lang.t("确定取消自动？");
                content = lang.t("取消后此Email将不再自动发送。");
                status = 0;
            } else {
                title = lang.t("确定设为自动？");
                content = lang.t("设为自动后此Email将自动发送。");
                status = 1;
            }
            $.Dlg.Util.confirm(title, content, function(){
                ajax("../../../admin/email/setStatus", {
                    email_id:  t.attr("data-id"),
                    status: status
                }, function(res){
                    if (res.result === 0) {
                        t.attr("data-status", res.data);
                        var text = res.data == 1 ? lang.t("取消自动") : lang.t("设为自动");
                        _this.attr("title", text).text(text);
                        say("设置成功！", true);
                    } else {
                        say("设置错误：" + res.err_desc);
                    }
                }, "POST");
            });
        });
    });
}
function _upd_email(tr){
    _clear();
    var email = $("#J_addEmail");
    ajaxData("../../../admin/email/getEmailConfig", {
        email_id : tr.attr("data-id")
    }, function(data){
        email.data("tr", tr);
        email.find(".ui-title").text(lang.t("修改Email"));
        email.find('input[name="subject"]').val(data.subject);
        email.find('input[name="receviers"]').val(data.receviers);
        email.find('input[name="cc"]').val(data.cc);
        email.find('input[name="test_receiver"]').val(data.test_receiver);
		email.find('input[name="weixin_recev"]').val(data.weixin_recev);
		email.find('input[name="weixin_media_id"]').val(data.weixin_media_id);
        email.find('textarea[name="remarks"]').text(data.remarks);
        email.find('textarea[name="dependencies"]').text(data.dependencies);
        $("#J_indicator").hide();
        DIALOG.addEmail.show();
    }, true);
}
function _clear(){
    $("#J_addEmail").data("tr", null).find(".ui-title").text(lang.t("添加Email"));
    $("#J_indicator").find(".sel-wrapper").empty();
}
function _getEmailContent(){
    return '' + '<ul>'
        +   _getWidgetSel([{
                title: lang.t("主题："), name: "subject" },{
                title: lang.t("收件人："), name: "receviers" }, {
                title: lang.t("抄送："), name: "cc" }, {
                title: lang.t("测试收件人："), name: "test_receiver"},{
				title: lang.t("微信收件人："), name: "weixin_recev"},{
				title: lang.t("Media_Id："), name: "weixin_media_id"
            }])
        + '<li class="widget-sel">'
        +   '<span class="title-inline">' + lang.t("发送频率：") + '</span>'
        +   '<span class="freq">' + lang.t('每日发送') + '</span>' + '<input type="radio" name="setfreq" class="WEEKLY freq-type freq" title="DAILY"  checked="checked">'
		+   '<span class="freq">' + lang.t('每月发送') + '</span>' + '<input type="radio" name="setfreq" class="MONTHLY freq-type freq" title="MONTHLY">'
        + '</li>'
        + '<li class="widget-sel">'
        +   '<h4 class="title">' + lang.t("说明：") + '</h4>'
        +   '<textarea name="remarks" class="ipttxtarea" cols="86" rows="4"/></textarea>'
        + '</li>'
        + '<li class="widget-sel">'
        +   '<h4 class="title">' + lang.t("依赖：") + '</h4>'
        +   '<textarea name="dependencies" class="ipttxtarea" cols="86" rows="4"/></textarea>'
        + '</li>'
        + '<li class="widget-sel" id="J_indicator">'
        +   '<h4 class="title">' + lang.t("选择游戏：") + '</h4>'
        +   '<div class="sel-wrapper"></div>'
        + '</li>'
        + '</ul>';
}
function _getWidgetSel(config){
    var html = '';
    $.each(config, function(){
        html += '<li class="widget-sel">'
        +   '<span class="title-inline">' + this.title + '</span>'
        +   '<input name="' + this.name + '" type="text"  class="ipttxt"/>'
        + '</li>'
    });
    return html;
}
/**
 * @brief _add_email
 */
function _add_email(){
    var email = $("#J_addEmail"),
        indicator = $("#J_indicator"),
        tr = email.data("tr"),
        param = _check_email();

		if(param){
			if(tr){ //update email
				param.email_id = tr.attr("data-id");
				ajaxData("../../../admin/email/updateEmailConfig", param, function(data){
					DIALOG.addEmail.hide();
					go(_getUrl(param.email_id));
				}, true);
			} else { //add new email
				ajaxData("../../../admin/email/apply", param, function(data){
					DIALOG.addEmail.hide();
					go(_getUrl(data.email_id));
				}, true);
			}
		}
	}

/**
 * @brief _check_email
 * @return
 */
function _check_email(){
    var email = $("#J_addEmail"),
        param = {
            subject: email.find('input[name="subject"]').val(),
            receviers: email.find('input[name="receviers"]').val(),
            cc: email.find('input[name="cc"]').val(),
            test_receiver: email.find('input[name="test_receiver"]').val(),
			weixin_recev: email.find('input[name="weixin_recev"]').val(),
			weixin_media_id: email.find('input[name="weixin_media_id"]').val(),
			frequency_type: email.find('.freq-type:checked').attr('title'),
            remarks: email.find('textarea[name="remarks"]').val(),
            dependencies: email.find('textarea[name="dependencies"]').val(),
        },
        indicator = $("#J_indicator");

		/* check param*/
		if( !param.subject ) {
			email.find('input[name="subject"]').hint();
			return false;
		}
		if( !param.receviers ) {
			email.find('input[name="receviers"]').hint();
			return false;
		}
		if( !email.data("tr") ) {
			if( indicator.find(".sel-con").find(".zone-server .selected-m").length < 1 ){
				indicator.find(".title").hint();
				return false;
			} else {
				param.game_id = indicator.find(".sel-con").find(".item .title-m .t-name").attr("data-id");
				param.platform_id = indicator.find(".sel-con").find(".platform .title-m .t-name").attr("data-id");
				param.gpzs_id = indicator.find(".sel-con").find(".zone-server .title-m .t-name").attr("data-id");
			}
		}
		return param;
	}
	function _create_indicator(item, platform, zoneServer){
		var indicator = $("#J_indicator"),
			selWrap = indicator.find(".sel-wrapper"),
			selCon = $(document.createElement("div")).addClass("sel-con")
				.css({ 'position' : 'relative', 'z-index' : _get_index() }),
			selP = $(document.createElement("div")).addClass("sel-p"),
			html =  '<ul>'
				+   '<li class="sel-p-li item"></li>'
				+   '<li class="sel-p-li platform"></li>'
				+   '<li class="sel-p-li zone-server"></li>'
				+ '</ul>';

		selCon.append(selP.html(html)).appendTo(selWrap);
		item = item ? item : [];
		platform = platform ? platform : [];
		zoneServer = zoneServer ? zoneServer : [];
		_create_item( selP, item, platform, zoneServer );
	}

	function _create_item(con, sel, platform, zoneServer) {
		var indicator = $("#J_indicator"),
			data = indicator.data("item"),
			prev = con.parent().prev().find(".sel-p ul .item").find(".selected-m");
		sel = sel.length ? sel : ( prev.length ? [prev.find(".title-m .t-name").attr("data-id")] : [] );
		if( !sel.length ){
			_create_platform(con);
		}
		var opts = {
			search : true,
			title: lang.t("选择游戏："),
			callback : function( curObj, title ){
				_create_platform( con, title.attr("data-id"), platform, zoneServer );
			},
			type : 1,
			selected : sel ? sel : [],
			obj : con.find(".item")
		};
		
		if( data ){
			opts.data = data;
			$.choose.core( opts );
		} else {
			ajaxData("../../../common/game/getGameList", null, function(data){
				var tmp = {game_id: -2, game_name: "汇总"};
				data.push(tmp);
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
			title: lang.t("选择平台："),
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
				ajaxData("../../../common/gpzs/getPlatform", {
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
			title: lang.t("选择区服："),
			type : 1,
			selected : sel ? sel : [],
			obj : con.find(".zone-server").empty()
		};

		if( id ){
			ajaxData("../../../common/gpzs/getZoneServer2", {
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
	 * @brief ajaxData
	 *
	 * @param url
	 * @param param
	 * @param fn:回调函数
	 * @param hide:是否显示overlayer
	 * @param empt:发生请求错误时，是否say
	 */
	function ajaxData( url, param, fn, hide, empt ){
		if( hide ) overlayer({ text: lang.t("加载中...")});
		ajax( url, param, function(res){
			if(res.result == 0){
				if(hide) hidelayer(lang.t("加载成功~.~"));
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
	/**
	 * @brief _get_index
	 * 获取z-index层级
	 * @return
	 */
	function _get_index(){
		if(index == 0){
			index = 100;
		}
		return index--;
	}
	function _getUrl(id){
		var href = (location.href.split("?"))[0],
			search = _getNaviArgs(),
			searchArr = [];
		if(id) search.email_id = id;
		if(!$.isEmptyObject(search)){
			$.each(search, function(index, val){
				searchArr.push(index + "=" + val);
			});
		}
		return href + "?" + searchArr.join("&")
	}
	/**
	 * @brief _getNaviArgs
	 * 获取url中的参数
	 * @return
	 */
	function _getNaviArgs(){
		var qs = location.search.length > 0 ? location.search.substring(1) : "",
			args = {},
			items = qs.length ? qs.split("&") : [],
			item = null;
		for(var i = 0; i < items.length; i++){
			item = items[i].split("=");
			if(item[0].length){ args[item[0]] = item[1]; }
		}
		return args;
	}
})(window);
