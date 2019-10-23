(function(){
var CHANGEPWD;
$(function(){
    //change passwd
    $("#J_passwd").click(function(){
        _showChangePwd();
    });
    //if you need notify the new function, addClass "need-new" to the element
    var $news = $("body").find(".need-new");

    if( $news.length ){
        $news.each(function(){
            $(this).wrap('<div class="tools-new"></div>')
        });
    }
    // 页面刷新时，取消ajax请求
    $(window).bind("beforeunload", function() {
        window.gLoading.cancel();
    });

    // 统计页面点击事件
	
    // $("[data-stat]").on("mousedown", function() {
    //     msglog($(this).attr("data-stat"), $(this).attr("data-stat-item"));
    // });
	
	// .stat-module for viewing click event
	$('.stat-module').on("mousedown", function () {
		msglog($(this).attr("stid"), $(this).attr("sstid"), responseData.userName);
	});
	// .stat-button for functional click event
	$('body').on("mousedown", ".stat-button", function () {
		if ($('.stat-module.cur').length) {
			msglog($('.stat-module.cur').attr('sstid'),
				   $(this).attr("title"),
				   $(this).attr("sstid"));
		} else {
			msglog($('.main-nav').find('.cur').find('a').text(),
				   $(this).attr("title"),
				   responseData.userName);
		}
	});
    //close tips
    $(".tips a.tips-close").click(function(){
    	$(this).parent().remove();
    });
    // skin change
    //$("#J_webSkin").click(function(e) {
    //    $("#J_webSkinSel").show();
    //    e.stopPropagation();
    //});
    //$("#J_webSkinSel li").click(function() {
    //    if ($(this).hasClass("current")) {
    //        return;
    //    }
    //    LocalStorage.set("skin", $(this).attr("data-value"));
    //    go(Path.modifyPath("skin", $(this).attr("data-value")));
    //});
    //$(window).click(function() {
    //    $("#J_webSkinSel").hide();
    //});
    //if (LocalStorage.get("skin") && LocalStorage.get("skin") != "null") {
    //    if (LocalStorage.get("skin") != responseData.skin) {
    //        go(Path.modifyPath("skin", LocalStorage.get("skin")));
    //    }
    //}


    // 敬请期待
    $(".J-coming-soon").add(".J-new-function").each(function() {
        var text = lang.t("敬请期待");
        if ($(this).hasClass("J-new-function")) {
            text = "";
        }
        (function(that, text) {
            var hint = $(document.createElement("d")).addClass("hint").text(text).appendTo(that);
            // 检测右侧是否有足够的距离
            if ($("body").width() - that.offset().left - that.width() > 75) {
                hint.addClass("hint-right").css({
                    top: Math.ceil((that.height() - 27)/2)
                });
            } else {
                hint.addClass("hint-left").css({
                    top: Math.ceil((that.height() - 27)/2)
                });
            }
        })($(this), text);
    });
});

function _showChangePwd() {
    var html = '<ul><li class="widget-sel">'
        + '<label class="title-inline">' + lang.t("旧密码：") + '</label>'
        + '<input type="password" name="old_pwd" class="ipttxt"><i class="icon">&nbsp;</i>'
        + '</li><li class="widget-sel">'
        + '<label class="title-inline">' + lang.t("新密码：") + '</label>'
        + '<input type="password" name="new_pwd" class="ipttxt"><i class="icon">&nbsp;</i>'
        + '</li><li class="widget-sel">'
        + '<label class="title-inline">' + lang.t("确认密码：") + '</label>'
        + '<input type="password" name="confirm_pwd" class="ipttxt"><i class="icon">&nbsp;</i>'
        + '</li></ul>';
    var _checkOk = function(t) {
        var icon = t.parent().find(".icon");
        if(!t.val()) {
            icon.removeClass("ok").addClass("error");
        } else {
            icon.removeClass("error").addClass("ok");
        }
    };
    if(CHANGEPWD) {
        $(CHANGEPWD.getMask()).remove();
        $(CHANGEPWD.getContainer()).remove();
    }
    CHANGEPWD = $.Dlg.Util.popup({
        id : "J_updPass",
        title: lang.t("修改密码"),
        contentHtml: html,
        callback: function(con) {
            var oldPwd = con.find('[name=old_pwd]'),
                newPwd = con.find('[name=new_pwd]'),
                confirmPwd = con.find('[name=confirm_pwd]');
            oldPwd.blur(function(){
                _checkOk($(this));
            });
            newPwd.blur(function(){
                _checkOk($(this));
            });
            confirmPwd.blur(function(){
                var icon = $(this).parent().find(".icon");
                if($(this).val() && $(this).val() == newPwd.val()) {
                    icon.removeClass("error").addClass("ok");
                } else {
                    icon.removeClass("ok").addClass("error");
                }
            });
        },
        saveCallback: function(con) {
            var updPass = $("#J_updPass"), ok = true;
            updPass.find(".icon").each(function(){
                $(this).parent().find("input").blur();
                if(!$(this).hasClass("ok")) {
                    ok = false;
                }
            });
            if(ok) {
                ajax(getUrl("user", "changePassword"), {
                    old_pwd: updPass.find('input[name=old_pwd]').val(),
                    new_pwd: updPass.find('input[name=new_pwd]').val()
                }, function(res) {
                    if(res.result == 0) {
                        location.reload();
                    } else {
                        say(lang.t("修改失败：") + res.err_desc);
                    }
                });
            }
            return ok;
        }
    });
    CHANGEPWD.show();
}
window.msglog = function(stid, sstid, item) {
    if (responseData.isRelease) {
        $.ajax({
            url: "//newmisc.taomee.com/misc.js?gameid=10000&uid=" + responseData.userId + "&stid=" + stid + "&sstid=" + sstid
				+ ((typeof item === "undefined") ? "" : ("&item=" + item)),
            dataType: "jsonp"
       });
    }
};
(function() {
    msglog("登入", "用户登入");
    msglog("页面数据", $("#J_header .main-nav li.cur").text());
    msglog("登录用户详情", responseData.userName);
})();
})();
