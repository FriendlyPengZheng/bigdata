//is_leaf : 0:非叶子节点 1：叶子节点 2： 未知0
(function(window, undefined) {
    var TIPS = null, RANGE = null,
        LOOKDIALOG, SETDIALOG, DELDIALOG, DIYDIALOG, DIYUPD;
    $(function() {
        _gTree();
        _gTools();
    });
    function _gTools() {
        //event/node menu
        $("#J_rMenu").click(function(e) {
            e.stopPropagation();
            var t = $(this).hide(), cur = t.data("cur");
            switch($(e.target).attr("type")){
            case "rename" :
                _rename(cur); break;
            case "back" :
                _back(cur); break;
            case "look" :
                _look(cur); break;
            case "set" :
                _set(cur); break;
            case "delete" :
                _delete(cur); break;
                default : break;
            }
        });
        //set
        SETDIALOG = $.Dlg.Util.popup({
            id: "J_set",
            title: lang.t("设置"),
            width: '540px',
            saveCallback: function(d) {
                _saveSet();
            }
        });
        var diyHtml = '<ul class="ui-dlg-ul">' + _getDiyHtml() + '</ul>';
        DIYDIALOG = $.Dlg.Util.popup({
            id: "J_diy",
            title: lang.t("添加自定义加工项"),
            contentHtml: diyHtml,
            width: '540px',
            callback: function(obj) {
                window.setTimeout(function() {
                    _showCustomList($("#J_editBox").find(".edit-toolbar .sel-con"));
                    var _setRange = function() {
                        if(window.getSelection) {
                            var sel = window.getSelection();
                            if(sel.rangeCount) {
                                RANGE = sel.getRangeAt(0);
                                RANGE.isCollapsed = true;
                            }
                        } else {
                            RANGE = document.selection.createRange();
                            RANGE.isCollapsed = true;
                        }
                    };
                    $("#J_editContent").keydown(function(e){
                        if(!_checkString(e, e.keyCode)) return false;
                    }).on("mousedown, mouseup, keydown, keyup", function() {
                        _setRange();
                    });
                }, 500);
            },
            saveCallback: function(d) {
                var param = _handleSaveDiy(true);
                if(param) {
                    ajaxData(getUrl("gamecustom", "manage", "saveDiy"), param, function(data) {
                        location.reload();
                    });
                }
            }
        });
        $("#J_contentHead").find(".a-link-diy").click(function() {
            DIYDIALOG.show();
        });
        DIYUPD = $.Dlg.Util.popup({
            id: "J_updDiy",
            title: lang.t("修改自定义加工项"),
            width: '500px',
            saveCallback: function(d) {
                var param = _handleSaveDiy(false);
                if(param) {
                    param.diy_id = $("#J_updDiy").attr("data-id");
                    ajaxData(getUrl("gamecustom", "manage", "saveDiy"), param, function() {
                        location.reload();
                    });
                }
            }
        });

        //look
        var width = Math.max(300, Math.min(900, Math.round(.8 * $(window).width())));
        LOOKDIALOG = $.Dlg.Util.popup({
            id: "J_listData",
            title: lang.t("子项排序及删除"),
            width: width,
            saveCallback: function(d) {
                var $table = $("#J_listData").find(".list-data-table"),
                    param = {
                        r_id: $table.attr("data-id"),
                        type: $table.attr("data-type"),
                        id: []
                    };
                $table.find(".div-tbody .div-tr").each(function() {
                    param.id.push($(this).attr("data-id"));
                });
                ajaxData(getUrl("gamecustom", "manage", "adjustDataList"), param, function() {
                    d.hide();
                });
            }
        });
        $(document).click(function(e) {
            e.stopPropagation();
            $("#J_rMenu").hide();
        });
    }
    /**
     * @brief _handleSaveDiy
     * 判断是否可以保存自定义加工项
     * 获得自定义加工项的保存参数
     * @param isAdd : true 添加 false:修改
     * @return
     */
    function _handleSaveDiy(isAdd) {
        var diy = isAdd ? $("#J_diy") : $("#J_updDiy"),
            editContent = isAdd ? $("#J_editContent") : $("#J_updBox"),
            contentText = editContent.text(),
            diyRType = isAdd ? _getDiyType() : diy.attr("r-type"),
            dataName = isAdd ? $("#J_dataName") : $("#J_updDataName"),
            param = {
                diy_name: diy.find('input[name="r_name"]').val(),
                data_rule: '',
                node_id: $("#J_tree").jstree("get_selected").attr("node_id"),
                game_id: $("#J_paramGameId").val(),
                diy_type: diyRType
            },
            ok = true;

        diy.find(".necessary").each(function(){
            if(!$(this).val()) {
                $(this).hint();
                ok = false;
            }
        });
        if(!contentText) {
            editContent.hint();
            ok = false;
        } else {
            param.data_rule = isAdd ? _getDiyRule() : editContent.attr("data-rule");
        }
        if(!ok) return ok;
        //name
        var preName = dataName.find('input[name="pre_name"]'),
            sufName = dataName.find('input[name="suf_name"]');
        if(diyRType == 3) {
            param.pre_name = preName.val();
            param.suf_name = sufName.val();
            if(!(param.pre_name || param.suf_name)) {
                preName.add(sufName).hint();
                ok = false;
            }
        } else if(diyRType == 2) {
            param.pre_name = preName.val();
            param.suf_name = sufName.val();
            param.low_flag = dataName.find('input[name="low_flag"]').is(":checked");
            param.high_flag = dataName.find('input[name="high_flag"]').is(":checked");
            if(!(preName.val() || sufName.val())) {
                preName.add(sufName).hint();
                ok = false;
            }
        }
        return ok ? param : ok;
    }
    /**
     * @brief _getDiyRule
     * 获得自定义加工项的表达式
     * @return
     */
    function _getDiyRule(){
        var editContent = $("#J_editContent"),
            selA = editContent.find(".sel-a"),
            rule = editContent.text();
        selA.each(function(){
            rule = rule.replace($(this).text(), '[' + $(this).attr("data-id") + ']');
        });
        return rule ;
    }
    /**
     * @brief _checkString
     *
     * @param keyCode
     * backspace:8, F5: 116, 减:189, 点:190, 除:191, space: 32
     * 16: shift
     * 乘: shift+56, 加: shift+187 (: shift+57, ): shift+48
     * @return
     */
    function _checkString(e, keyCode) {
        var codeArr = [8, 32, 37, 38, 39, 40, 116, 189, 190, 191 ],
            shiftArr = [48, 56, 57, 187],
            flag = false;
        if(e.shiftKey && inArray(keyCode, shiftArr)) {
            flag = true;
        }
        if(!e.shiftKey && inArray(keyCode, codeArr)) {
            flag = true;
        }
        if(!e.shiftKey && keyCode >= 48 && keyCode <= 57) {
            flag = true;
        }
        return flag;
    }
    /**
     * @brief _showCustomList
     * container
     * @return
     */
    function _showCustomList(container) {
        var option = {
            title: lang.t("插入指标:"),
            obj: container.empty(),
            page: 2,
            type: 2,
            mulRadio: 3,
            search: true,
            data: [],
            clickChildFn: function(cur, p) {
                _boxChildFn(p, cur);
            },
            callback: function(cur, title) {
                if(title.attr("data-child") != "true") {
                    _boxChildFn(cur)
                }
            },
            getData: function(id, fn) {
                id = id.split("_");
                ajaxData(getUrl("common", "stat", "getSettings"),{
                    type: id[0],
                    r_id: id[1]
                }, function(data){
                    data = _handleSettingChoose(data);
                    if(fn) fn(data);
                });
            }
        };
        ajaxData(getUrl("common", "stat", "getIndicators"), {
            game_id: $("#J_paramGameId").val(),
            type: ["report", "set"]
        }, function(data) {
            data = _handleCustomChoose(data);
            option.data = data;
            $.choose.core(option);
        });
    }

    /**
     * @brief _boxChildFn
     * 点击child在编辑框内生成
     * @param cur
     * @param p: parent
     * pType 2: distr 3: item
     */
    function _boxChildFn(p, child) {
        var editContent = $("#J_editContent"),
            title = p.find(".title-m .t-name"),
            pType = title.attr("data-common").split("_"),
            diyCustomType = _getDiyType();

        if(pType[0] == "report") {
            //do not allow distribute and item show together
            if((pType[1] == 2 && diyCustomType == 3) || pType[1] == 3 && diyCustomType == 2) {
                $("#J_boxTips").find(".text-tips").hint();
                return;
            }
        }
        var customName = child ? child.text() : title.text(),
            customId = title.attr("data-id") + (child ? "_" + child.attr("data-id") : ""),
            customHtml = '<button class="sel-a" onclick="return false;" data-id="' + customId + '" '
                + 'data-type="' + (pType[1] ? pType[1] : pType[0]) + '" contenteditable = false>' + customName + '</button>';

        if(RANGE) {
            if($.browser.msie) {
                RANGE.pasteHTML('&nbsp;'+ customHtml);
            } else {
                var fragment = RANGE.createContextualFragment('&nbsp;' + customHtml);
                RANGE.insertNode(fragment);
            }
        } else {
            editContent.html(editContent.html() + '&nbsp;' + customHtml);
        }
        editContent.find(".sel-a").click(function(e){
            e.stopPropagation();
            RANGE = null;
        });
        $("#J_dataName").html(_getSetNameConfig(_getDiyType()));
    }
    /**
     * @brief _getDiyType
     *  获取整个表达式的custom type
     * @return
     */
    function _getDiyType() {
        var editContent = $("#J_editContent"),
            selA = editContent.find(".sel-a"),
            customType = 1;
        selA.each(function(){
            var type = $(this).attr("data-type");
            if(type == 2) {
                customType = 2;
                return;
            } else if(type == 3){
                customType = 3;
                return;
            }
        });
        return customType;
    }
    function _handleCustomChoose(data) {
        var report = [], set = [];
        if(data && data.length){
            $.each(data, function(){
                if(this.type == "report") {
                    report.push({
                        title: this.r_name,
                        attr: {
                            common: this.type + "_" + (this.is_multi == "1"
                                                       ? (inArray(this.op_type, ["distr_sum", "distr_max", "distr_set"]) ? 2 : 3)
                                                       : 1),
                            id: this.type + "_" + this.r_id,
                            cid: this.type + "_" + this.r_id,
                            child: false
                        }
                    });
                } else if(this.type == "set") {
                    set.push({
                        title: this.r_name,
                        attr: {
                            common: this.type,
                            id: this.type + "_" + this.r_id,
                            cid: this.type + "_" + this.r_id,
                            child: this.is_multi == "1" ? true : false
                        }
                    });
                }
            });
        }
        return [{
            title: lang.t("游戏分析数据"),
            children: set
        },{
            title: lang.t("游戏自定义数据"),
            children: report
        }];
    }
    function _handleSettingChoose(data) {
        var rlt = [];
        $.each(data, function(){
            rlt.push({
                title: this.data_name,
                attr: { id : this.id },
                selected: false
            });
        });
        return rlt;
    }
    function _saveSet() {
        var set = $("#J_set"), ok = true, rlt = {},
            cur = $("#J_rMenu").data("cur");

        rlt.r_id = cur.attr("data-id");
        var preName = set.find('input[name="pre_name"]'),
            sufName = set.find('input[name="suf_name"]');
        if(preName.val() == '' && sufName.val() == '') {
            ok = false;
            preName.hint();
            sufName.hint();
        } else {
            rlt.pre_name = preName.val();
            rlt.suf_name = sufName.val();
        }
        if(cur.attr("r-type") == "2") {//range
            var lowFlag = set.find('input[name="low_flag"]'),
                highFlag = set.find('input[name="high_flag"]');
            if( lowFlag.is(":checked") == false && highFlag.is(":checked") == false ){
                lowFlag.parent().parent().hint();
                ok = false;
            } else {
                rlt.low_flag = lowFlag.is(":checked") ? 1 : 0;
                rlt.high_flag = highFlag.is(":checked") ? 1 : 0;
            }

            var range = $("#J_range"), n = 0;
            range.find(".dis-sel").each(function(){
                var t = $(this),
                    low = t.find("input[name='range_low[]']"),
                    high = t.find("input[name='range_high[]']");
                if(low.val() && high.val()){
                    rlt['range_low[' + n + ']'] = low.val();
                    rlt['range_high[' + n + ']'] = high.val();
                    n++;
                }
            });
            if(!n) {
                ok = false;
                range.find(".distri-ul").hint();
            }
        }
        if(ok) {
            ajaxData(getUrl("gamecustom", "manage", "setStatItem"), rlt, function(data){
                SETDIALOG.hide();
            }, true);
        }
    }
    function _gTree() {
        _gSearch();
        var gameId = $("#J_paramGameId").val(),
            tree = $("#J_tree");
        tree.jstree({
            plugins: ["html", "json_data", "themes", "cookies", "ui", "core", "crrm", "dnd", "search"],
            themes: { "theme": "orange" },
            core: { "initially_open": ["custom0"] },
            cookies: {
                save_opened: "jstree-tongji-open" + $("#J_paramGameId").val(),
                save_selected: "stree-tongji-selected" + $("#J_paramGameId").val()
            },
            search: _gTreeSearch(),
            json_data: _gTreeJsonData(),
            ui: {
                select_limit: 1
            },
            crrm: {
                move: {
                    default_position: "inside",
                    check_move: function(m) {
                        return _checkMove(m);
                    }
                }
            },
            dnd: {
                drag_check: function(ui) {
                    var dragId = $(ui.o).closest(".list-file").attr("data-id"),
                        dragLeaf = parseInt($(ui.o).closest(".list-file").attr("is-leaf"), 10),
                        type = $(ui.o).closest(".list-file").attr("data-type"),
                        hoverId = ui.r.attr("node_id");
                    if(type == "node") { //移动节点
                        //节点不能移动到自己 || 节点不能移动到叶子
                        if(hoverId == dragId) return { inside: false };
                        //移向叶子节点，不允许
                        if (parseInt(ui.r.attr("is_leaf"), 10) == 1) return { inside: false };
                        //移向根节点，允许
                        if(hoverId == 0) return { inside: true };
                        var r_p = ui.r.parentsUntil(".jstree", "li"); // 移向节点的父亲
                        if (parseInt(r_p.attr("node_id"), 10) != 0) { // 已至少2级
                            //超出3级，不允许
                            if (dragLeaf == 0) return { inside: false };
                        } else { // 只有1级
                            //o => is_leaf = 1||2 =>return true
                            //o => is_leaf = 0 && child_id !=0 (all) 所有孩子不能有非叶子
                            if (dragLeaf == 0) return { inside: _checkChildrenIsLeaf(dragId) };
                        }
                        return { inside: true };
                    } else { //移动事件
                        //事件不能移动到非叶子节点
                        if(parseInt(ui.r.attr("is_leaf") ,10 ) == 0) return false;
                    }
                    return {
                        inside: true,
                        after: false,
                        before: false
                    };
                },
                drag_finish: function(ui){
                    var file = $(ui.o).closest(".list-file");
                    move({
                        id: file.attr("data-id"),
                        parent_id: ui.r.attr("node_id")
                    }, file.attr("data-type") != "node" ? 2 : 1);
                }
            }
        }).bind("select_node.jstree", function(event, node) {
            if(node.rslt.obj.attr("id")!="custom0" && node.args[1]) node.inst.toggle_node();
            _showContent(node.rslt.obj.attr("is_leaf"), node.rslt.obj.attr("node_id"));
        }).bind("move_node.jstree", function(event, node){
            var param = {
                id: node.rslt.o.attr("node_id"),
                parent_id: node.rslt.op.attr("node_id"),
                after_id: 0
            };
            if(node.rslt.p == "inside") {
                param.parent_id = node.rslt.r.attr("node_id");
            } else {
                param.after_id = node.rslt.or.attr("node_id") ? node.rslt.or.attr("node_id") : 0;
                if(node.rslt.op.attr("node_id") != node.rslt.cr.attr("node_id")) {
                    param.parent_id = node.rslt.cr.attr("node_id");
                }
            }
            move(param);
        }).bind("loaded.jstree", function(event, node){
            _headTools();
        }).bind("search.jstree", function(evt, node) {
            if(node.rslt.nodes.length == 0) {
                _showSearchTips();
            }
        });
    }
    function _gTreeJsonData() {
        return {
            data: [{
                "data": {
                    "title": lang.t("配置管理"),
                    "icon": "root"
                },
                "state": "closed",
                "attr": { id: "custom0" , node_id: 0, is_leaf: 0 }
            }],
            ajax: {
                url: "../../../gamecustom/tree/getManageTree",
                data: function(n) {
                    return {
                        game_id: $("#J_paramGameId").val(),
                        parent_id: n.attr ? n.attr("node_id") : 0
                    };
                },
                success: function( res ) {
                    if (res.result == 0 && res.data) {
                        var nodes = [];
                        $.each(res.data, function(){
                            nodes.push({
                                data : this.node_name,
                                state : (this.is_leaf == "1" ? "leaf" : "closed"),
                                attr : {
                                    title: this.node_name,
                                    id :"custom" + this.node_id,
                                    node_id : this.node_id,
                                    node_name : this.node_name,
                                    is_leaf : this.is_leaf
                                }
                            });
                        });
                        return nodes;
                    } else {
                        return "";
                    }
                }
            }
        };
    }
    /**
     * @brief _treeSearch
     * 左树搜索功能
     * @return
     */
    function _gTreeSearch() {
        return {
            case_insensitive: true,
            ajax: {
                url: "../../../gamecustom/tree/search",
                data: function(n) {
                    return {
                        keyword: $("#J_search").find(".srh-txt").val(),
                        game_id: $("#J_paramGameId").val()
                    };
                },
                success: function(res) {
                    var arr = [];
                    $(res.data).each(function() {
                        arr.push("#custom" + this);
                    });
                    return arr;
                }
            }
        };
    }
    /**
     * @brief _showContent
     * 显示节点列表（事件列表）
     * @param isLeaf
     * @param nodeId
     * @return
     */
    function _showContent(isLeaf, nodeId) {
        var content = $("#J_content").empty().addClass("big-loading");
        var contentHead = $("#J_contentHead");
        if (isLeaf === "1") {//show report list
            contentHead.find(".links.link-node").hide();
            contentHead.find(".links.link-event").show();
            ajaxData("../../../gamecustom/manage/getStatItemByNodeId", {
                node_id : nodeId
            }, function(data){
                content.removeClass("big-loading");
                fac(configure(data));
            });
        } else {
            contentHead.find(".links.link-node").show();
            contentHead.find(".links.link-event").hide();
            ajaxData("../../../gamecustom/tree/getManageTree", {
                parent_id : nodeId }, function(nodelist) {
                    content.removeClass("big-loading");
                    if(nodelist && nodelist.length) {
                        fac(configure(nodelist, true));
                    }
                });
        }
    }

    function _headTools() {
        var contentHead = $("#J_contentHead"),
            addBtn = contentHead.find(".a-link-node"),
            mergeBtn =  contentHead.find(".a-link-merge"),
            deleteBatchBtn = contentHead.find(".a-link-delete"),
            diyBtn = contentHead.find(".a-link-diy");

        mergeBtn.click(function(e){
            e.stopPropagation();
            var curNode = _getCurNode();
            if(parseInt((_getCurNode()).attr("is_leaf"), 10) == 0) {
                ajaxData("../../../gamecustom/tree/mergeNode" ,{ id : curNode.attr("node_id") }, function() {
                    $(_getCurNode()).attr("is_leaf", 1);
                    _refreshTree();
                }, true);
            }
        });
        addBtn.click(function(e) {
            e.stopPropagation();
            var curNode = _getCurNode();
            if(curNode.attr("is_leaf") == 1) {
                say(lang.t("叶子节点不能添加节点。"));
            } else {
                _addNode();
            }
        });
        // TODO
        deleteBatchBtn.click(function() {
            var curNode = _getCurNode(),
                list = $("#J_content .list-file").filter(".selected"),
                param = {
                    //id: []
            		id: ""
                };
            list.each(function() {
                //param.id.push((curNode.attr("is_leaf") == 1 ? ($(this).attr("data-type") + "_") : "") + $(this).attr("data-id"));
            	node_id = (curNode.attr("is_leaf") == 1 ? ($(this).attr("data-type") + "_") : "") + $(this).attr("data-id");
            	console.log("node_id:" + node_id);
            	param.id += node_id;
            	param.id += "_";
            });

            if (curNode.attr("node_id") == -1) {// delete report
                param.game_id = $("#J_paramGameId").val();
                param.status = 1;
                ajaxData(getUrl("common", "report", "setStatus"), param, function(data) {
                    $("#J_tree").jstree("refresh", -1);
                }, true);
            } else if (curNode.attr("is_leaf") == 1) { // move to trash
                param.parent_id = -1;
                ajaxData("../../../gamecustom/tree/moveStatItem", param, function(data) {
                    $("#J_tree").jstree("refresh", -1);
                }, true);
            } else { // delete node
            	console.log("param.id:" + param.id);
                ajaxData("../../../gamecustom/tree/delNode", param, function(data) {
                    $("#J_tree").jstree("refresh", -1);
                }, true);
            }
            return false;
        });
    }
    /**
     * @brief _addNode
     * 添加节点
     * @return
     */
    function _addNode() {
        var content = $("#J_content");
        var _fn = function(t) {
            var file = t.closest(".list-file");
            if(t.val()) {
                var selected = _getCurNode();
                ajaxData("../../../gamecustom/tree/addNode", {
                    name: t.val(),
                    parent_id: (selected.attr("node_id") ? selected.attr("node_id") : 0),
                    game_id: $("#J_paramGameId").val()
                }, function(data) {
                    _refreshTree();
                }, true);
            } else {
                file.remove();
            }
        };
        if(content.find(".list-rename").length == 0) {
            var li = $(document.createElement("div")).addClass("list-file")
                    .css({ "border-color" : "#61CC49" })
                    .html('<div class="list-img node"></div>'
                          + '<div class="list-title"><span><input type="text" class="list-rename"></span></div>'
                          + '<span class="list-move jstree-draggable" style="display:none; "></span>'
                          + '<span class="list-move" style="display:none; "><input type="checkbox"/></span>').prependTo(content);
            li.find(".list-rename").focus().blur(function(){
                _fn( $(this) );
            }).keydown(function(e){
                if( e.keyCode == 13 ){
                    _fn( $(this) );
                }
            });
        }
    }

    /**
     * @brief configure
     * @param configure
     * @param isNode
     * r_type : 1 basic 2 range 3 item
     * @return
     */
    function configure(configure, isNode) {
        var prepared = [], lists = [], SELECTED = 0,
            contentHeadBatch = $("#J_contentHead").find(".batch").hide(),
            callback;

        if (!isNode) {
            _initStatList(lists);
        }
        if (isNode || _getCurNode().attr("node_id") == -1) {
            contentHeadBatch = contentHeadBatch.not(".report-only");
        } else {
            contentHeadBatch = contentHeadBatch.filter(".report-only");
        }

        $.each(configure, function() {
            $(this).each(function() {
                var tmp = {
                    title: (isNode ? this.node_name : this.r_name),
                    isNotice: this.is_empty ? true : false,
                    className: "node", // default "node"
                    attr: {
                        "title": (isNode ? this.node_name : this.r_name),
                        "data-id": (isNode ? this.node_id : this.r_id),
                        "data-type": (isNode ? "node" : this.type),
                        "r-type": (isNode ? 0 : this.r_type),
                        "is-leaf": this.is_leaf
                    }
                };

                if (isNode) {
                    lists.push(tmp);
                    return;
                }

                if(this.type == "report") {
                    tmp.className = this.r_type == 2 ? "range" : (this.r_type == 3 ? "item" : "basic");
                    tmp.attr["sstid"] =  this.sstid;
                    tmp.attr["op-type"] = this.op_type;
                    tmp.attr["op-fields"] = this.op_fields;
                    lists[0].lists[this.r_type == 1 ? 0 : 1].lists.push(tmp);
                } else if(this.type == "diy"){
                    tmp.className = "diy";
                    lists[1].lists[this.r_type == 1 ? 0 : 1].lists.push(tmp);
                }
            });
        });

        callback = function(list) {
            if ($("#J_tree").jstree("get_selected").attr("node_id") !== "-1") {
                list.contents.sortable({
                    items: "> li",
                    handle: ".jstree-draggable",
                    containment: "parent",
                    placeholder: "list-file-placeholder",
                    helper: function() {
                        return "<li></li>"; // dummy
                    },
                    start: function() {
                        $(this).removeClass("sort-cancel");
                    },
                    update: function(e, ui) {
                        if (!$(this).hasClass("sort-cancel")) {
                            var param = {
                                id: ui.item.attr("data-id"),
                                parent_id: $("#J_tree").jstree("get_selected").attr("node_id"),
                                after_id: ui.item.next(".list-file").attr("data-id") || 0
                            },
                            dataType = ui.item.attr("data-type");
                            if (dataType !== "node") {
                                param.type = dataType;
                            }
                            move(param, dataType !== "node" ? 3/* Adjust stat display order */ : 1);
                        }
                    }
                }).mouseleave(function() {
                    if ($(".list-file-placeholder").length > 0) {
                        $(this).addClass("sort-cancel").sortable("cancel");
                    }
                });
            }

            list.contents.find(".list-file").mouseover(function(){
                $(this).find(".list-move").show();
                $(this).find(".list-multi").show();
            }).mouseout(function(){
                $(this).find(".list-move").hide();
                if (!$(this).hasClass("selected")) {
                    $(this).find(".list-multi").hide();
                }
            }).bind("contextmenu", function(e){
                if (SELECTED == 1 && $(this).hasClass("selected") || SELECTED == 0) {
                    _showMenu(e,$(this));
                }
                return false;
            });
            list.contents.find(".list-multi").click(function() {
                var node = $(this).parents(".list-file");
                if ($(this).find("input").attr("checked")) {
                    SELECTED ++;
                    node.addClass("selected");
                } else {
                    SELECTED --;
                    node.removeClass("selected");
                }
                if (SELECTED) contentHeadBatch.show();
                else contentHeadBatch.hide();
            });
        };

        if (!isNode) {
            prepared.push({
                type: "tabs",
                container: $("#J_content").addClass('mt10'),
                child: []
            });
            $.each(lists, function() {
                var tab = {
                    type: "tab",
                    title: this.title,
                    child: []
                };
                $(this.lists).each(function() {
                    if (this.lists.length === 0) {
                        return;
                    }

                    tab.child.push({
                        type: "wrap",
                        title: this.title,
                        headEnabled: false,
                        bottomEnabled: false,
                        child: [{
                            type: "list",
                            configure: this.lists,
                            moveBtnClass : "jstree-draggable",
                            afterCreate: callback
                        }]
                    });
                });
                prepared[0].child.push(tab);
            });
        } else {
            prepared.push({
                type: "list",
                container: $("#J_content"),
                configure: lists,
                moveBtnClass : "jstree-draggable",
                afterCreate: callback
            });
        }

        return prepared;
    }
    /**
     * 初始化统计项列表
     *
     * @param  array     lists
     * @return undefined
     */
    function _initStatList(lists) {
        $.each([lang.t("常规统计项")], function(typeIdx, typeTitle) {
            lists[typeIdx] = {
                title: typeTitle,
                lists: []
            };
            $.each([lang.t("基本类型"), lang.t("分布及item类型")], function(rTypeIdx, rTypeTitle) {
                lists[typeIdx].lists[rTypeIdx] = {
                    title: rTypeTitle,
                    lists: []
                };
            });
        });
    }
    /**
     * @brief _showMenu
     * 右键菜单
     * node: rename del
     * report:
     * r_type == 1 rename back,
     * r_type != 1 rename, look, set, back
     * diy: rename, look, set, del
     * @param e
     * @param obj
     *
     * @return
     */
    function _showMenu(e, obj) {
        var rMenu = $("#J_rMenu"),
            type = obj.attr("data-type"),
            rType = obj.attr("r-type");
        var look = rMenu.find('li[type="look"]'),
            set = rMenu.find('li[type="set"]'),
            back = rMenu.find('li[type="back"]'),
            del = rMenu.find('li[type="delete"]');

        rMenu.css({
            left : e.clientX,
            top : e.clientY + $("body").scrollTop()
        }).data("cur", obj);
        look.add(set).add(back).add(del).hide();
        if(type == "node") {
            del.show();
        } else if(type == "diy") {
            look.add(set).add(del).show();
        } else if(type == "report") {
            if(rType == "1") {
                back.show();
            } else {
                look.add(set).add(back).show();
            }
        }
        rMenu.show();
    }
    /**
     * @brief _showSearchTips
     * 显示搜索tips
     * @return
     */
    function _showSearchTips() {
        if(TIPS) clearTimeout(TIPS);
        var tips = $("#J_search").find(".search-tips");
        tips.slideDown(200, function(){
            TIPS = setTimeout(function(){
                tips.fadeOut(300);
            }, 500);
        });
    }
    /**
     * @brief _gSearch
     * 树的搜索功能
     * @return
     */
    function _gSearch() {
        var search = $("#J_search"),
            searchIpt = search.find(".srh-txt"),
            searchTag = search.find(".search-tag"),
            searchFn = function() {
                $("#J_tree").jstree("search", searchIpt.val());
            };
        searchIpt.focus(function() {
            search.css({ border: "2px solid #FE9D1F" });
        }).blur(function() {
            search.css({ border: "2px solid #CCC" });
        }).keydown(function(e) {
            if(e.keyCode == 13) searchFn();
        });
        searchTag.click(function() {
            searchFn();
        });
    }

    /**
     * @brief  _checkMove
     * 左树移动-检测
     * @param m
     * m.r: 目标节点
     * m.o: 移动节点
     * m.cr: 移动节点未来的父节点
     * m.op：移动节点曾经的父节点
     * @return
     */
    function _checkMove(m){
        if(m.p == "inside"){
            return _checkInside(m.o, m.r);
        } else { //"before\after"
            if(m.op.attr("node_id") == m.cr.attr("node_id")) {
                return true;
            } else {
                if(_checkInside(m.o, m.cr)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * @brief _checkInside
     *
     * @param m
     * @param o: 移动节点
     * @param r: 目标节点
     * @param rp: 目标节点的父节点
     *
     * @return
     */
    function _checkInside(o, r) {
        //移向叶子节点，不允许
        if (parseInt(r.attr("is_leaf"), 10) == 1) return false;
        //移向根节点，允许
        if(parseInt(r.attr("node_id"), 10) == 0) return true;
        var rp = r.parentsUntil(".jstree", "li"); // 移向节点的父亲
        if (parseInt(rp.attr("node_id"), 10) != 0) { // 已至少2级
            // 超出3级，不允许
            if (parseInt(o.attr("is_leaf"), 10) == 0) return false;
        } else { // 只有1级
            //o => is_leaf = 1||2 =>return true
            //o => is_leaf = 0 && child_id !=0 (all) 所有孩子不能有非叶子
            if (parseInt(o.attr("is_leaf"), 10) == 0) return _checkChildrenIsLeaf(o.attr("node_id"));
        }
        return true;
    }
    /**
     * @brief _checkChildrenIsLeaf
     * 判断所有孩子是否都是叶子节点
     * id : 移动节点的node id
     * @return
     */
    function _checkChildrenIsLeaf(id) {
        var check = true;
        ajax("../../../gamecustom/tree/getManageTree",
             $.extend(getPageParameters(), { parent_id: id }), function(res) {
                 if (res.result == 0) {
                     if (res.data) {
                         for (var i = 0; i < res.data.length; i++) {
                             if (parseInt(res.data[i].is_leaf, 10) == 0) {
                                 return check = false;
                             }
                         }
                     }
                 } else {
                     say(lang.t("操作失败:") + res.err_desc);
                 }
             }, "GET", false);
        return check;
    }
    /**
     * @brief move
     * 移动节点
     * @param id : 当前节点id
     * @param parent_id ：父节点
     * @param type: 1: node 2:report 3:adjust stat display order
     */
    function move(param, type) {
        var url,
            callback = function(data) {
                $("#J_tree").jstree("refresh", -1);
            },
            layer = false;

        switch (type) {
            case 2:
                url = "../../../gamecustom/tree/moveStatItem";
                break;

            case 3:
                url = getUrl("gamecustom", "manage", "adjustStatOrder");
                callback = undefined;
                layer = true;
                break;

            default:
                url = "../../../gamecustom/tree/moveNode";
                break;
        }

        ajaxData(url, param, callback, layer);
    }


    /**
     * @brief _delete
     * 删除
     * @param obj
     *
     * @return
     */
    function _delete(obj) {
        var type = obj.attr("data-type"),
            id = obj.attr("data-id"),
            url,
            param = {};

        if (type != "node") {
            url = getUrl("gamecustom", "manage", "removeStatItem");
            param.r_list = [{type: type, r_id: id}];
        } else {
            url = "../../../gamecustom/tree/delNode";
            param.id = id;
        }

        if(id) {
            if(DELDIALOG) {
                $(DELDIALOG.getMask()).remove();
                $(DELDIALOG.getContainer()).remove();
            }
            DELDIALOG = $.Dlg.Util.confirm(lang.t("请确认？"), lang.t("确定要删除吗？"), function() {
                ajaxData(url, param, function() {
                    if(type == 1) {
                        _refreshTree();
                    } else {
                        obj.remove();
                        _refreshTree();
                        // var delId = obj.attr('data-id');
                        // console.log(delId);
                        // $('#J_tree li').each(function () {
                        //     console.log($(this).attr());
                        //     if ($(this).attr('node-id') == delId) {
                        //         console.log($(this).text());
                        //     }
                        // });
                    }
                }, true);
            });
        } else {
            say(lang.t("此项出现异常，不能删除~"));
        }
    }

    /**
     * @brief _rename
     * 重命名
     * @param obj
     *
     * @return
     */
    function _rename(obj) {
        var titleSpan = obj.find(".list-title span"),
            rename = $(document.createElement("input")).attr("type", "text")
                .addClass("list-rename").val(titleSpan.text()),
            type = obj.attr("data-type"),
            id = obj.attr("data-id"),
            url = "../../../gamecustom/tree/setName";
        url = type == "node" ? url : "../../../gamecustom/manage/setStatItemName";
        var _fn = function(t) {
            var newName = t.val(),
                oldName = obj.attr("title");
            if(newName && newName != oldName) {
                ajaxData(url, {
                    id: id,
                    name: newName,
                    type: type
                }, function() {
                    titleSpan.empty().text(newName);
                    obj.attr("title", newName);
                    if(type == "node") {
                        $(_getCurNode()).find('li[node_id="' + id + '"] >a')
                            .html('<ins class="jstree-icon">&nbsp;</ins>' + newName);
                    }
                }, true);
            } else {
                rename.remove();
                titleSpan.text(oldName);
            }
        };
        titleSpan.empty().append(rename);
        rename.focus().blur(function() {
            _fn($(this));
        }).keydown(function(e) {
            if(e.keyCode == 13){//Enter
                _fn($(this));
            } else if(e.keyCode == 27) { //ESC
                titleSpan.text($(this).closest(".list-file").attr("title"));
            }
        });
    }
    /**
     * @brief _back
     * 找回名称功能
     * name = sstid+ op_fields + op_type
     */
    function _back(obj) {
        var name = obj.attr("sstid") + obj.attr("op-fields") + _getOpType(obj.attr("op-type"));
        ajaxData(getUrl("common", "report", "setName"), {
            type: "report",
            id: obj.attr("data-id"),
            name: name
        }, function() {
            obj.find(".list-title span").empty().text(name);
            obj.attr("title", name);
        }, true);
    }

    function _getOpType(opType){
        switch(opType){
        case "ucount": return lang.t("人数");
        case "count": return lang.t("人次");
        case "sum": return lang.t("求和");
        case "max": return lang.t("最大");
        case "set":
        case "distr_sum":
        case "distr_max":
        case "distr_set":
            return "";
        case "ip_distr": return lang.t("地区分布");
            default : return lang.t("未知类型");
        }
    }

    /**
     * @brief _look
     * 查看数据列表功能
     * @param obj
     *
     * @return
     */
    function _look(obj) {
        LOOKDIALOG.setTitle(lang.t("子项排序及删除") + " - " + obj.attr("title"));
        var content = $(LOOKDIALOG.getContent()).empty();
        ajaxData(getUrl("gamecustom", "content", "getDataList"), {
            r_id: obj.attr("data-id"),
            type: obj.attr("data-type")
        }, function(data) {
            var maxHeight = Math.round(.9 * $(window).height()) - 150,
                height = 41 * data.length,
                html = '<div class="div-table list-data-table" data-id="' + obj.attr("data-id") + '" data-type="' + obj.attr("data-type") + '">'
                     +   '<div class="div-thead">'
                     +     '<div class="div-tr clearfix">'
                     +       '<div class="div-th wp30">' + lang.t("数据ID") + '</div>'
                     +       '<div class="div-th wp50">' + lang.t("数据名称") + '</div>'
                     +       '<div class="div-th wp20">' + lang.t("操作") + '</div>'
                     +     '</div>'
                     +   '</div>'
                     +   '<div class="div-tbody" style="max-height:' + maxHeight + 'px">',
                $tbody;

            $.each(data, function(){
                html +=    '<div class="div-tr clearfix" data-id="' + (this.diy_data_id || this.data_id) + '">'
                     +       '<div class="div-td wp30">' + (parseInt(this.data_id) !== 0 ? this.data_id : this.data_expr) + '</div>'
                     +       '<div class="div-td wp50">' + this.data_name + '</div>'
                     +       '<div class="div-td wp20"><a href="javascript:void(0);" class="del-btn btn-green">' + lang.t("删除") + '</a></div>'
                     +     '</div>';
            });
            html += '</div></div>';

            $tbody = $(html).appendTo(content).find(".div-tbody");
            $tbody.sortable({
                items: "> .div-tr",
                cancel: "a",
                cursor: "move",
                placeholder: "list-data-placeholder",
                start: function(e, ui) {
                    ui.placeholder.height(ui.item.height()).width(ui.item.width());
                }
            }).find(".div-tr").css("cursor", "move").find(".del-btn").click(function() {
                $(this).parent().parent().remove();
            });

            if (height > maxHeight) {
                $tbody.css("overflow-y", "scroll");
            }

            LOOKDIALOG.show();
        });
    }
    /**
     * @brief _set
     * 设置功能
     * @param obj
     *
     * @return
     */
    function _set(obj) {
        var html = '',
            type = obj.attr("data-type"),
            dlgUl = $(document.createElement("ul")).addClass("ui-dlg-ul"),
            callback;

        if (type === "diy") {
            callback = function(data) {
                $("#J_updDiy").attr({
                    "r-type": data.r_type,
                    "data-id": data.r_id
                });
                DIYUPD.setConfirmHtml(dlgUl.html(_getDiyHtml(data, true)));
                DIYUPD.show();
            };
        } else {
            callback = function(data) {
                dlgUl.append($(document.createElement("li")).addClass("ui-dlg-list").html(_getSetNameConfig(obj.attr("r-type"), data)));
                if(obj.attr("r-type") == "2") {
                    _rangeFac((data.range ? data.range : []), dlgUl);
                }
                $(SETDIALOG.getContent()).empty().append(dlgUl);
                SETDIALOG.show();
            };
        }

        ajaxData(getUrl("gamecustom", "manage", "getStatItem"), {
            r_id: obj.attr("data-id"),
            type: type
        }, callback);
    }
    function _getDiyHtml(data, isUpd) {
        data = data ? data : {};
        var html = ''
        html += '<li class="ui-dlg-list">'
            +   '<span class="list-title">' + lang.t("加工项名称：") + '</span>'
            +   '<input type="text" name="r_name" class="txt-ipt necessary" value="' + (data.r_name ? data.r_name : "") + '">'
            + '</li>'
            + '<li class="ui-dlg-list">'
            +   '<h3 class="title">' + lang.t("编辑指标表达式：") + '</h3>';
        if(isUpd) {
            html += '<div class="upd-box" id="J_updBox" data-rule="' + data.data_rule + '" >' + data.data_rule_name + '</div>'
                + '</li>'
                + '<li class="ui-dlg-list" id="J_updDataName">'
                + _getSetNameConfig(data.r_type, data)
                + '</li>';
        } else {
            html += '<div class="edit-box" id="J_editBox">'
                +      '<div class="edit-toolbar">'
                +          '<div class="sel-con"></div>'
                +      '</div>'
                +      '<div class="edit-content textarea" id="J_editContent" contenteditable="true">&nbsp;'
                +      '</div>'
                +      '<div class="edit-footer">'
                +          '<span class="edit-notice">' + lang.t("注：") + lang.t("编辑区只能输入{1}和{2}，还可插入事件。", "+,-,*,/,.,(,)", "0~9") + '</span>'
                +      '</div>'
                +  '</div>'
                + '</li>'
                + '<li class="ui-dlg-list">'
                +   '<span class="text-tips">' + lang.t("注：") + lang.t("item类型和等级分布类型只能选择一项。") + '</span>'
                + '</li>'
                + '<li class="ui-dlg-list" id="J_dataName">'
                + _getSetNameConfig()
                + '</li>';
        }
        return html;
    }

    function _getSetNameConfig(type, data) {
        data  = data ? data : {};
        if(type == "3") {
            return ''
                +   '<span class="list-title">' + lang.t("数据名称：") + '</span>'
                +   '<input type="text" name="pre_name" class="txt-ipt txt-pre" value="'
                + (data.pre_name ? data.pre_name : "") + '"> <span>-</span> '
                +   '<span class="txt-dis">item-name</span>'
                +   '<span>-</span>'
                +   '<input type="text" name="suf_name" class="txt-ipt txt-suf" value="'
                + (data.suf_name ? data.suf_name : "") + '">';
        } else if(type == "2") {
            return ''
                +   '<span class="list-title">' + lang.t("数据名称：") + '</span>'
                +   '<input type="text" name="pre_name" class="txt-ipt txt-pre" value="'
                + (data.pre_name ? data.pre_name : "") + '"> <span>-</span> '
                +   '<span class="txt-range">'
                +   ' <label><input type="checkbox" name="low_flag" class="mr2" ' + (data.low_flag ? 'checked' : '')+ '>[low]</label>~'
                +   ' <label><input type="checkbox" name="high_flag" class="mr2" '+ (data.high_flag ? 'checked' : '') + '>[high]</label> '
                +  '</span>'
                +   ' <span>-</span> '
                +   '<input type="text" name="suf_name" class="txt-ipt txt-suf" value="'
                + (data.suf_name ? data.suf_name : "") + '">';
        }
        return "";
    }
    function _refreshTree() {
        var tree = $("#J_tree"),
            selected = tree.jstree("get_selected");
        if(selected.length) {
            tree.jstree("refresh", selected);
            _showContent(selected.attr("is_leaf"), selected.attr("node_id"));
        } else {
            tree.jstree("refresh", -1);
        }
        //tree.jstree( "refresh", selected.length == 1 ? selected : -1 );
    }
    function _getCurNode() {
        return $("#J_tree").jstree("get_selected");
    }
    /**
     * @brief ajaxData
     * 获取数据接口PHP
     * @param url
     * @param fn : 回调函数
     * @param timeDimension ：时间维度
     *
     */
    function ajaxData(url, param, fn, layer) {
        if(layer)overlayer({ text : lang.t("操作中...")});
        ajax(url, $.extend(getPageParameters(), param), function(res){
            if (res.result == 0) {
                if(layer)hidelayer(lang.t("操作成功"));
                if(fn) fn(res.data);
            } else {
                if(layer)hidelayer();
                say(lang.t("操作失败:") + res.err_desc);
            }
        }, "POST");
    }
    /**
     * @brief getPageParameters
     * 获取页面公共参数
     */
    function getPageParameters() {
        return {
            game_id: $("#J_paramGameId").val()
        };
    }

    function _rangeFac(data, con) {
        var maxLen = 4,
            moreLi = '<li class="dis-sel sel-more"><a class="more" href="javascript:void(0);">more</a></li>',
            $selLi = $(document.createElement('li')).addClass('ui-dlg-list').attr("id", "J_range"),
            html =  '<span class="list-title">' + lang.t("分布区间：") + '</span>'+ '<ul class="distri-ul">';

        if(data && data.length) {
            $.each(data,function(i) {
                html += '<li class="dis-sel ';
                if( i > 3 ) html += 'hide-li';
                html +=    '">'
                    + '[ <input type="text" name="range_low[]" class="dis-range" placeholder="$low" value="' + this.low + '"/>'
                    + lang.t("，") + '<input type="text" name="range_high[]" class="dis-range" placeholder="$high" value="' + this.high + '"/>';
                if(data.length > 1) html += '<span class="del-m">&nbsp;</span>';
                html += ' )</li>';
            });
            if(data.length > 4) html += moreLi;
        } else {
            for(var i = 0; i < maxLen; i++) {
                html += '<li class="dis-sel">'
                    + '[ <input type="text" name="range_low[]" class="dis-range" placeholder="$low"/>'
                    + lang.t("，") + '<input type="text" name="range_high[]" class="dis-range" placeholder="$high"/>';
                if(maxLen > 1) html += '<span class="del-m">&nbsp;</span>';
                html += ' )</li>';
            };
        }
        html += '</ul>' + ' <a href="javascript:void(0);" class="add-m">Add</a>';
        $selLi.html(html).appendTo(con);
        //delete
        $selLi.find('.del-m').live('click', function(e){
            e.stopPropagation();
            var $target = $(e.target),
                disLen = $selLi.find("ul .dis-sel").length;

            if(disLen == 1) {
                return;
            } else if(disLen == 2) {
                $target.parent().remove();
                $selLi.find("ul .dis-sel").find(".del-m").remove();
            } else {
                $target.parent().remove();
            }
        });
        var $moreLi = $selLi.find('.sel-more'),
            $more = $moreLi.find('.more'),
            $add = $selLi.find(".add-m");

        //add
        $add.click(function(e, low, high) {
            e.stopPropagation();
            var $target = $(e.target),
                $disUl = $selLi.find("ul"),
                $disSels =  $disUl.find(".dis-sel").not(".sel-more"),
                disLen = $disSels.length;

            if(disLen == 1 && $disSels.find(".del-m").length == 0) {
                $(document.createElement('span')).addClass('del-m').appendTo( $disSels );
            }
            low = typeof low == "undefined" ? "" : low;
            high = high || "";
            var $li = $(document.createElement('li')).addClass('dis-sel')
                    .html(' [ <input type="text" name="range_low[]" class="dis-range" value="' + low + '" placeholder="$low"/>'
                          + lang.t("，") + '<input type="text" name="range_high[]" class="dis-range" value="' + high + '" placeholder="$high"/>'
                          + ' ) <span class="del-m">&nbsp;</span>');

            if($moreLi.length != 0) {
                if(!$more.hasClass('pack')) $more.click();
                $moreLi.before( $li );
            } else {
                $li.appendTo( $disUl );
            }
        });
        //more
        $more.click(function(e) {
            e.stopPropagation();
            var $self = $(this);
            if($self.hasClass('pack')) {
                $self.removeClass('pack').text('more');
                $selLi.find('.dis-sel:gt(3)').not('.sel-more').addClass('hide-li');
            } else {
                $self.addClass('pack').text('pack');
                $selLi.find('.hide-li').removeClass('hide-li');
            }
        });
        // 自动生成区间
        var $auto = $(document.createElement("div")).addClass("mt10 pb10"),
            $rangeText = $(document.createElement("span")).addClass("dp-ib w40").text(lang.t("间隔：")),
            $topText = $(document.createElement("span")).addClass("dp-ib w50 ml10").text(lang.t("截止值：")),
            $autoBtn = $("<a href='javascript:void(0);' class='ml10 ui-sel-btn'>" + lang.t("开始生成区间") + "</a>"),
            $rangeInput = $("<input type='text' name='range' class='dis-range' placeholder='" + lang.t("间隔") + "' />"),
            $topInput = $("<input type='text' name='top' class='dis-range' placeholder='" + lang.t("截止值") + "' />");
        $auto.append($rangeText).append($rangeInput).append($topText).append($topInput).append($autoBtn);
        $autoBtn.click(function() {
            var topV = $topInput.val(),
                rangeV = $rangeInput.val();
            if (!rangeV || isNaN(rangeV)) {
                $rangeInput.hint(); return;
            }
            if (!topV || isNaN(topV)) {
                $topInput.hint(); return;
            }
            if ($(this).hasClass("disabled")) return;
            // 生成区间数组
            var start = 0, end = 0, ranges = [];
            rangeV = parseInt(rangeV);
            topV = parseInt(topV);
            while (start <= topV) {
                end = start + rangeV;
                if (end <= topV)  ranges.push([start, end]);
                start = end
            }
            var len = ranges.length;
            if (len) {
                $selLi.find(".distri-ul").find(">li").not(".sel-more").remove();
                $autoBtn.addClass("disabled");
                $.each(ranges, function(i, range) {
                    (function(i, range) {
                        setTimeout(function() {
                            $add.trigger("click", range);
                            len --;
                            if (!len) $autoBtn.removeClass("disabled");
                        }, i*20);
                    })(i, range);
                });
            }
        });
        $selLi.find(".distri-ul").before($auto);
    }
})(window);
