(function($, undefined) {
var UPDDIALOG;
$(function() {
$("#J_buildBtn").click(function() {
    overlayer({ text: "生成中..."});
    ajax(getUrl("admin", "online", "build"), null, function(res) {
        hidelayer("操作结束~.~");
        if(res.result == 0) {
            say("生成成功~.~", true);
        } else {
            say("生成错误：" + res.err_desc);
        }
    });
});
$("#J_tableContainer").datatable({
    searchEnabled: true,
    searchContainer: $("#J_tableSearchContainer")
});
$(".upd-btn").on("click", function() {
    var t = $(this);
    if(UPDDIALOG) {
        $(UPDDIALOG.getMask()).remove();
        $(UPDDIALOG.getContainer()).remove();
    }
    UPDDIALOG = $.Dlg.Util.popup({
        id : "J_updGame",
        title: "修改数据配置",
        contentHtml: '<div class="loading"></div>',
        callback: function(con){
            _getDataConfigList(t.attr("data-id"), function(data){
                con.find(".ui-hgt16").html(_createForm(data));
            });
        },
        save: function(con) {
            _save(t.attr("data-id"));
        }
    });
    UPDDIALOG.show();
    return false;
});
});
/**
 * @brief _save
 * 保存数据配置信息
 * @param id: gameID
 */
function _save(id) {
    overlayer({ text: "加载中..."});
    var config = ["show", "data_name", "data_id", "gpzs_id", "sthash", "show_name",
        "auth_id", "position", "in_summary", "is_all"];
        param = [];
    $("#J_updGame").find(".online-table tbody").find("tr").each(function(){
        if($(this).find('input[name="show"]').is(":checked")) {
            var values = {};
            for(var i = 0; i < config.length; i++) {
                var field = $(this).find('input[name="' + config[i] + '"]');
                values[config[i]] = (field.attr("type") == "checkbox" || field.attr("type") == "radio")
                    ? (field.is(":checked") ? 1 : 0)
                    : field.val();
            }
            param.push(values);
        }
    });
    ajax(getUrl("admin", "online", "save"), {
        game_id: id,
        data: param
    }, function (res) {
        if (res.result == 0) {
            hidelayer("加载成功~.~");
            location.reload();
        } else {
            hidelayer("出错了");
            say("获取数据错误：" + res.err_desc);
        }
    }, "POST");
}
function _createForm(data) {
    var html = '<table class="table module-table online-table">'
            + '<thead><tr>'
                + '<th class="th w40">显示</th>'
                + '<th class="th">数据名称</th>'
                + '<th class="th w250">显示名称</th>'
                + '<th class="th">权限ID</th>'
                + '<th class="th w40">位置</th>'
                + '<th class="th w60">添加到通常</th>'
                + '<th class="th">是否是总在线</th>'
            + '</tr></thead>'
            + '<tbody>';
    for(var i = 0; i < data.length; i++) {
        html += '<tr>'
                + '<td class="td hd"><input type="checkbox" name="show" value="'
                    + data[i].show + '" ' + (data[i].show == 1 ? 'checked="checked"' : '') + '"></td>'
                + '<td class="td hd" title="数据ID：' + data[i].data_id
                    + '，gpzsID：' + data[i].gpzs_id + '，sthash：' + data[i].sthash + '">'
                    + data[i].data_name
                    + '<input type="hidden" name="data_name" value="' + data[i].data_name + '">'
                    + '<input type="hidden" name="data_id" value="' + data[i].data_id + '">'
                    + '<input type="hidden" name="gpzs_id" value="' + data[i].gpzs_id + '">'
                    + '<input type="hidden" name="sthash" value="' + data[i].sthash + '">'
                + '</td>'
                + '<td class="td hd"><input type="input" class="mod-rename w200" name="show_name" value="'
                    + data[i].show_name + '"></td>'
                + '<td class="td hd"><input type="input" class="mod-rename" name="auth_id" value="'
                    + data[i].auth_id + '"></td>'
                + '<td class="td hd"><input type="input" class="mod-rename w20" name="position" value="'
                    + data[i].position + '"></td>'
                + '<td class="td hd"><input type="checkbox" name="in_summary" value="'
                    + data[i].in_summary + '" ' + (data[i].in_summary == 1 ? 'checked="checked"' : '') + '"></td>'
                + '<td class="td hd"><input type="radio" name="is_all" value="'
                    + data[i].is_all + '" ' + (data[i].is_all == 1 ? 'checked="checked"' : '') + '></td>'
                + '</tr>';
    }
        html += '</tbody></table>';
    return html;
}

/**
 * @brief _getDataConfigList
 * 获取数据配置
 * @param callback
 *
 * @return
 */
function _getDataConfigList(id, callback) {
    ajax(getUrl("admin", "online", "getOnlineList"), {
        game_id: id
    }, function(res) {
        if (res.result == 0) {
            callback(res.data ? res.data : []);
        } else {
            say("获取错误：" + res.err_desc)
        }
    });
}
})(jQuery);
