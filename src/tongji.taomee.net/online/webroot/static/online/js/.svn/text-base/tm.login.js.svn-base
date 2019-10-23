(function($, undefined) {
$.widget("tm.login", {
options: {
    loginUrl: "index.php?r=user/login",
    vericodeUrl: "index.php?r=user/getVericode&"
},
lang: {
    uid_no_empty: "用户名不能为空",
    passwd_no_empty: "密码不能为空",
    vericode_no_empty: "验证码不能为空",
    vericode_less_code: "验证码长度错误",
    vericode_error: "验证码错误",
    "100000" : "系统繁忙，请稍后再试"
},
isLogin: false,
dialog: null,
hint: null,
save_pwd: false,
form: {
    uid: "",
    passwd: "",
    vericode: ""
},
isLoadVc: false,
_createEle: function(type, attr, className) {
    return $(document.createElement(type)).attr(attr).addClass(className);
},
_ready: function() {
    var hint = $("<div>").addClass("dropdown-notice").hide();
    this.hint = hint.append($("<p>"));
    this.dialog.prepend(this.hint);
},
_init: function() {
    var that = this;
    this.dialog = $("#J_loginForm");
    this._ready();
    this.form.uid = $("#uid");
    this.form.passwd = $("#passwd");
    this.form.vericode = $("#vericode");
    this.loadVericode($.trim($("#J_isVericode").val()) == "1" ? true : false);
    this.form.submitBtn = $("#J_loginBtn");
    this.form.submitBtn.on("click", function() {
        if ($(this).attr("loading")) {
            return false;
        } else {
            that.setLoginStatus(true);
        }
        return that.checkValidate();
    }).hover(function() {
        $(this).addClass("login-btn-hover");
    }, function() {
        $(this).removeClass("login-btn-hover");
    });
    this.dialog.on("submit", this.checkValidate);
    $.each(["uid", "passwd", "vericode"], function() {
        (function(id) {
            var input = $("#" + id),
                label = $("#label_" + id),
                showLabel = function() {
                    label.css({
                        color: input.val() ? "" : "#ccc"
                    }).html(input.val() ? "&nbsp;" : label.attr("data-dft"));
                };
            if (!input.length || !label.length) return;
            input.on("focus", function() {
                $(this).attr("_focus", 1);
            }).on("blur", function() {
                $(this).removeAttr("_focus");
            }).on("keyup keydown input click", showLabel);
            showLabel();
        })(this.toString());
    });
},
setLoginStatus: function(isDuringLogin) {
    if (isDuringLogin) {
        this.form.submitBtn.attr("loading", 1).val("\u767B\u5F55\u4E2D");
    } else {
        this.form.submitBtn.removeAttr("loading").val("\u767B\u5F55");
    }
},
checkValidate: function() {
    if (this.form.uid.val() == "") {
        this.showError(this.lang.uid_no_empty);
        this.form.uid.focus();
        return false;
    }
    this.form.uid.val($.trim(this.form.uid.val()));

    if (this.form.passwd.val() == "") {
        this.showError(this.lang.passwd_no_empty);
        this.form.passwd.focus();
        return false;
    }

    if (this.isLoadVc) {
        if (this.form.vericode.val() == "") {
            this.showError(this.lang.vericode_no_empty);
            this.form.vericode.focus();
            return false;
        }
        if (this.form.vericode.val().length < 4) {
            this.showError(this.lang.vericode_less_code);
            this.form.vericode.focus();
            return false;
        }
        if (!(/^[a-zA-Z0-9]+$/).test(this.form.vericode.val())) {
            this.showError(this.lang.vericode_error);
            this.form.vericode.focus();
            return false;
        }
    }
    this.submit();
    return false;
},
submit: function() {
    var that = this;
    ajax(this.options.loginUrl, {
        user_name: this.form.uid.val(),
        user_pwd: this.form.passwd.val(),
        vericode: this.form.vericode.val()
    }, function(res) {
        if (res.result == 0) {
            location.href = res.data;
        } else {
            that.changeImg();
            that.showError(res.err_desc);
        }
    }, "POST");
},
loadVericode: function(load) {
    if (this.isLoadVc == load) {
        return;
    }
    var that = this;
    this.isLoadVc = load;
    if (load) {
        $("#imgvericode").click(function() {
            that.changeImg();
        }).attr("src", this.options.vericodeUrl + Math.random());
        $("#J_vericodeContainer").show();
    } else {
        $("#J_vericodeContainer").hide();
    }
},
changeImg: function() {
    $("#imgvericode").attr({
        src: this.options.vericodeUrl + Math.random()
    });
},
showError: function(msg) {
    this.setLoginStatus(false);
    if (!msg) {
        this.hint.hide();
        return;
    }
    var errorMsg = {
            errorSystem: "\u7cfb\u7edf\u9519\u8bef"
        };
    errorMsg[msg] && (msg = errorMsg[msg]);

    this.hint.html(msg).show();
}
});

$("#J_loginForm").login();
})(jQuery);
