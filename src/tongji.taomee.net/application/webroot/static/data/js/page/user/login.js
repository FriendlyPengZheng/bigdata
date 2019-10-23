(function() {
var error = function(element, msg) {
    $(element).prev("div").show().children("span").text(msg);
};
 // 点击登录按钮
$("#J_login_btn").click(function(){
     var username = $.trim($("#J_name").val()),
         password = $.trim($("#J_pwd").val());
     if (empty(username)) {
         error($("#J_name"), lang.t("请输入用户名！"));
         return;
     }
     if(empty(password)) {
         error($("#J_pwd"), lang.t("请输入密码！"));
         return;
     }
     $(this).hide();
     $(this).next().show();
     ajax(getUrl("user", "login"), {
         "user_name": username,
         "user_pwd": password
     }, function (res) {
        if (res.result == 0) {
            location.href = res.data;
        } else {
            error($("#J_name"), res.err_desc);
            $("#J_login_btn").show();
            $("#J_login_btn").next().hide();
        }
     }, "POST");
});
$("#J_name").focus(function(){
    $(this).prev().hide();
});
$("#J_pwd").focus(function(){
    $(this).prev().hide();
}).keyup(function(e){
    var code = e.keycode||e.which;
    if (code == 13) {
        $("#J_login_btn").click();
    }
});
})();
