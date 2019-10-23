(function($, undefined) {
$(function() {
$(".J-btn-build").on("click", function() {
    var key = $(this).attr('data-key');
    $.Dlg.Util.confirm('生成JSON', '确定生成JSON？', function () {
        _build({
            module_key: key.replace(/\./g, '-')
        });
    });
    return false;
});
});

/**
 * @brief _build
 * 生成JSON
 *
 * @param param
 */
function _build(param) {
    overlayer({ text: "加载中..."});
    ajax(getUrl("admin", "manage", "build"), param, function (res) {
        if (res.result == 0) {
            hidelayer("生成成功~.~");
        } else {
            hidelayer("出错了");
            say("获取数据错误：" + res.err_desc);
        }
    }, "POST");
}

})(jQuery);
