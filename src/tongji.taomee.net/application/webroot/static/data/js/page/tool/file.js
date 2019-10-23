$(function() {
    // download
    $(".file-list").on("click", ".btn-down", function() {
        var $tr = $(this).closest("tr"),
            param = {
                file_source: $tr.attr("file-source"),
                file_id: $tr.attr("file-id"),
            };
        ajax(getUrl("tool/file/get"), param, function(res) {
            if (res.result == 0) {
                go(res.data.url);
            } else {
                say(res.err_desc);
            }
        });
    });
    // delete
    $(".btn-del").click(function() {
        var $tr = $(this).closest("tr"),
            param = {
                file_id: $tr.attr("file-id")
            };
        ajax(getUrl("tool/file/del"), param, function(res) {
            if (res.result == 0) {
                $tr.remove();
                if (!($(".file-list tbody tr").length)) {
                    go(getUrl("tool/file/index"));
                }
            } else {
                say(res.err_desc);
            }
        });
    });
	
    // refresh file status
    var refreshHandle = window.setInterval(function() {
        var fileIds = [];
        $(".refresh").each(function() {
            fileIds.push($(this).closest("tr").attr("file-id"));
        });

        if (fileIds.length < 1) {
            clearInterval(refreshHandle);
            return;
        }

        ajax(getUrl("tool/file/refresh"), { file_id: fileIds }, function(res) {
            if (res.result != 0) return;
            $.each(res.data, function(i, file) {
                var $file = $("#J_file" + file.file_id);
                $file.next().text(file.progress + "%");
                if (file.status == 2 || file.status == 3) {
                    $file.removeClass("refresh");
                    if (file.status == 2) {
                        $file.html('<span class="c-ok">' + lang.t("已完成") + '</span>');
                        $file.next().next().next().prepend('<span class="btn-dft btn-down">' + lang.t("下载") + '</span>');
                    } else {
                        $file.next().text("--");
                        $file.html('<span class="c-err">' + lang.t("出错了！") + file.message + '</span>');
                    }
                } else if (file.status == 1) {
                    $file.html('<span class="exporting">' + lang.t("处理中") + '</span>');
                }
            });
        });
    }, 5000);
});
