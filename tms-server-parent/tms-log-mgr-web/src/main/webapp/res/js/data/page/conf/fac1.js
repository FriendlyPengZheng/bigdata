function ModuleFac(){}
ModuleFac.prototype = {
    options: {
        type: "wrap"
    },
    create: function(opts) {
        this.options = $.extend({},this.options, opts);
        switch (opts.type) {
        case "wrap": return this._wrapFac(); break;
        case "tabs": return ''; break;
        case "tab": return this._tabFac(); break;
        case "data": return this._dataFac(); break;
        case "listtable": return this._listtableFac(); break;
        case "table": return this._tableFac(); break;
        case "graph": return this._graphFac(); break;
        case "selcontrol": return this._selControlFac(); break;
        case "selconfig" : return this._selConfigFac(); break;
        case "select": return this._widgetSelFac(this.options.config); break;
        case "attribute": return this._attributeFac(this.options.attrTitle, this.options.attrClass); break;
        case "urlextend": return this._urlExtendFac(); break;
        case "urlmatch": return this._urlMatchFac(); break;
        case "metadata": return this._metadataFac(); break;
        case "processdata": return this._processdataFac(); break;
        case "thead": return this._theadFac(this.options.theadDataconfig); break;
        case "checkbox": return this._checkboxFac(this.options); break;
        case "graphconfig": return this._graphConfigFac(); break;
        case "listtableconfig": return this._listtableConfigFac(); break;
        default: break;
        }
    },
    _createDoc: function(doc, className) {
        return $(document.createElement(doc)).addClass(className);
    },
    _checkboxFac: function(opts) {
        var con = this._createDoc("ul");
        con.append(this._createCheckbox(opts));
        con.find('input[type="checkbox"]').click(function(){
            if(opts.checkedFn) opts.checkedFn($(this));
        });
        return con;
    },
    //---process start---
    _processdataFac: function() {
        var that = this,
            sel = this._createDoc("li", "widget-sel process-con"),
            titleCon = this._createDoc("div", "process-title-con"),
            title = this._createDoc("span", "title-inline").text("操作数据元："),
            checkProcess = this._createDoc('span').html(
                '<label class="mr20"><input type="radio" name="process_type" value="expres" checked/> Expres</label>'
                    + '<label class="mr20"><input type="radio" name="process_type" value="distr"/> Distr</label>'),
            generate = this._createDoc("a", "btn-green fr mr10").attr("href", "javascript: void(0);").text('自动生成'),
            addM = this._createDoc("a", "add-m fr").attr("href", "javascript: void(0);").text("新建"),
            processUl = this._createDoc("ul", "attr-ul process-ul");
        titleCon.append(title.add(checkProcess).add(addM).add(generate));
        sel.append(titleCon.add(processUl.append(this._createProcessDataLi("expres"))));
        //自动生成
        generate.click(function(e) {
            e.stopPropagation();
            that._generateProcessData(sel.closest("#J_moduleCon").find(".metadata-ul"), processUl,
                                      checkProcess.find('input[name="process_type"]:checked').val());
        });
        //新建操作数
        addM.click(function(e) {
            e.stopPropagation();
            processUl.append(that._createProcessDataLi(
                checkProcess.find('input[name="process_type"]:checked').val()));
        });
        checkProcess.find('input[name="process_type"]').click(function(){
            processUl.empty().append(that._createProcessDataLi($(this).val()));
        });
        return this._createDoc("ul").append(sel.append(titleCon).append(processUl));
    },
    _generateProcessData: function(metadataUl, processUl, processType) {
        var that = this, metalis = metadataUl.find(".attr-sel");
        if(metalis.length) {
            processUl.empty();
            metalis.find(".url-ipt").each(function(i) {
                var val = $(this).val().split("&");
                var opts = {};
                for (var j = 0; j < val.length; j++) {
                    var arr = val[j].split("=");
                    opts[arr[0]] = arr[1];
                    if (arr[0] == 'data_name') opts.distr_name = arr[1];
                }
                opts.data = i;
                processUl.append(that._createProcessDataLi(processType, opts));
            });
        }
    },
    _createProcessDataLi: function(type, opts){
        type = type ? type : "expres";
        var attrSel = this._createDoc("li", "attr-sel process-sel"),
            html = '';
        var config = type == "expres" ? this._getExpresConfig(opts) : this._getDistrConfig(opts);
        $(config).each(function(i){
            html += '<strong title="' +  (this.hover ? this.hover : '') + '">' + this.title + '：</strong>'
                + '<input type="text" name="' + this.name + '" class="attr-ipt process-ipt ' + this.width + '" '
                + 'value="' + this.value + '" />';
        });
        html +=  '<span class="del-m">&nbsp;</span>';
        attrSel.html(html).find(".del-m").click(function(e){
            e.stopPropagation();
            $(this).closest("li.attr-sel").remove();
        });
        return attrSel;
    },
    _getExpresConfig: function(opts){
        return [{ title: "expre",
                  name: "expre",
                  value: (opts && opts.expre ? opts.expre : "{0}"),
                  width: 'w60'
                },
                { title: "data_name",
                  name: "data_name",
                  value: (opts && opts.data_name ? opts.data_name: ""),
                  width: 'w140'
                },
                { title: "unit",
                  name: "unit",
                  value: (opts && opts.unit ? opts.unit: ""),
                  width: 'w30'
                },
                { title: "precision",
                  name: "precision",
                  value: (opts && opts.precision ? opts.precision: 2),
                  width: 'w30'
                },
                { title: "data",
                  name: "data",
                  value: (opts && (opts.data || opts.data == 0)? opts.data: ""),
                  width: 'w100'
                }];
    },
    _getDistrConfig: function(opts){
        return [{ title: "sort_type",
                  name: "sort_type",
                  value: (opts && opts.sort_type ? opts.sort_type : 3),
                  width: 'w20'
                  , hover: "1:default; 2: 递增; 3: 递减" },
                { title: "distr_name",
                  name: "distr_name",
                  value: (opts && opts.distr_name ? opts.distr_name: ""),
                  width: 'w100'
                },
                { title: "dimen_name",
                  name: "dimen_name",
                  value: (opts && opts.dimen_name ? opts.dimen_name: ""),
                  width: 'w80'
                },
                { title: "distr_by",
                  name: "distr_by",
                  value: (opts && opts.distr_by ? opts.distr_by: 4),
                  width: 'w20'
                  , hover: "1:平台 2:区服 3:stid 4: range 5: data" },
                { title: "distr_type",
                  name: "distr_type",
                  value: (opts && opts.distr_type ? opts.distr_type: 1),
                  width: 'w20'
                  , hover: "eg: 1:sum 2: avg 3: absavg 4: max" },
                { title: "data",
                  name: "data",
                  value: (opts && (opts.data || opts.data == 0)? opts.data: ""),
                  width: 'w60'
                }];
    },
    setProcessDataValue: function(con, type, list){
        if(type && list && $.isArray(list)){
            var that = this, processUl = con.find(".process-ul");
            con.find('input[value="' + type + '"]').attr("checked", true);
            processUl.empty();
            $(list).each(function(){
                processUl.append(that._createProcessDataLi(type, this));
            });
        }
    },
    _getProcessDataValue: function(con){
        var processType = con.find('input[name="process_type"]:checked').val(),
            rlt= [];
        var config = processType == "expres" ? this._getExpresConfig() : this._getDistrConfig();
        con.find(".process-sel").each(function(i){
            var t = $(this), tmp = {};
            $(config).each(function(){
                tmp[this.name] = t.find('input[name="' + this.name + '"]').val();
            });
            rlt.push(tmp);
        });
        return { process_list: rlt, process_type: processType  };
    },
    //--processdata end---
    //--- sel control config start---
    _selConfigFac: function() {
        var that = this,
            sel = this._createDoc("li", "widget-sel"),
            attrUl = this._createDoc("ul", "attr-ul selctl-ul"),
            addM = this._createDoc("a", "add-m")
                .attr("href", "javascript: void(0);")
                .text("Add")
                .css({ "margin-left" : "80px" });
        sel.append(this._createDoc("h4", "title").text("selConfig：").append(addM)
                   .add(attrUl.append(this._createSelConfigLi())));
        addM.click(function(e) {
            e.stopPropagation();
            attrUl.append(that._createSelConfigLi());
        });
        return this._createDoc("ul").append(sel);
    },
    _createSelConfigLi: function(options) {
        var opts = {
            key: '',
            urlKey: "",
            titlePre: "",
            titleSuf: "",
            isAjax: 0,
            urlPage: "",
            urlExtend: "",
            data: []
        };
        $.extend(opts, options);
        var attrSel = this._createDoc("li", "attr-sel config-sel");
        var html = '';
        html +=  '<div>{ <span class="del-m ctl-del">&nbsp;</span></div>'
            + '<div class="mb5">'
            + '<strong>key：</strong>'
            + '<input type="text" name="key" class="attr-ipt w60" value="' + opts.key + '"/>'
            + '<strong>urlKey：</strong>'
            + '<input type="text" name="urlKey" class="attr-ipt w80" value="' + opts.urlKey + '"/>'
            + '<strong>titlePre：</strong>'
            + '<input type="text" name="titlePre" class="attr-ipt w80" value="' + opts.titlePre + '"/>'
            + '<strong>titleSuf：</strong>'
            + '<input type="text" name="titleSuf" class="attr-ipt w60" value="' + opts.titleSuf + '"/>'
            + '，<label class="mr2"><input type="checkbox" name="isAjax" class="mr2" '
            + (parseInt(opts.isAjax) == 1 ? ' checked' : '') + '/><strong>isAjax</strong></label>'
            + '，<strong>urlPage：</strong>'
            + '<input type="text" name="urlPage" class="attr-ipt w100" value="' + opts.urlPage + '"/>'
            + '</div><div class="mb5">'
            + '<strong>urlExtend：</strong>'
            + '<input type="text" name="urlExtend" class="attr-ipt w640" value="' + opts.urlExtend + '"/>'
            + '</div><div class="selctldata"></div>'
            + '<div> }</div>';
        attrSel.html(html);
        attrSel.find(".selctldata").append(mfac.create({
            type: "thead",
            theadDataconfig:  this._getSelctlDataConfig()
        }));
        if (opts.control_id && opts.control_id.length) {
            var val = [];
            for (var i = 0; i < opts.control_id.length; i++) {
                val.push({ id: opts.control_id[i], name: opts.control_name[i] });
            }
            this.setTheadValue((this._getSelctlDataConfig()).config, attrSel.find(".thead-ul"), val);
        }
        attrSel.find(".del-m").click(function(e){
            e.stopPropagation();
            if($(this).closest("ul").find("li.attr-sel").length > 1){
                $(this).closest("li.attr-sel").remove();
            }
        });
        return attrSel;
    },
    setSelConfigValue: function(con, opts) {
        if (opts && $.isArray(opts)) {
            var that = this;
            con.empty();
            $(opts).each(function() {
                con.append(that._createSelConfigLi(this));
            });
        }
    },
    _getSelConfigValue: function(con) {
        var that = this, rlt= [];
        con.find(".config-sel").each(function(i) {
            rlt[i] = {};
            rlt[i]["key"] = $(this).find('input[name="key"]').val();
            rlt[i]["urlKey"] = $(this).find('input[name="urlKey"]').val();
            rlt[i]["titlePre"] = $(this).find('input[name="titlePre"').val();
            rlt[i]["titleSuf"] = $(this).find('input[name="titleSuf"]').val();
            rlt[i]["isAjax"] = $(this).find('input[name="isAjax"]').is(":checked") ? 1 : 0;
            rlt[i]["urlPage"] = $(this).find('input[name="urlPage"]').val();
            rlt[i]["urlExtend"] = $(this).find('input[name="urlExtend"]').val();
            $.extend(rlt[i], that._getTheadValue($(this).find(".selctldata"), that._getSelctlDataConfig().config));
        });
        return { selConfig: rlt };
    },
    //--- appendColumns config start---
    _listtableConfigFac: function() {
        var that = this,
            sel = this._createDoc("li", "widget-sel"),
            attrUl = this._createDoc("ul", "attr-ul config-ul"),
            addM = this._createDoc("a", "add-m").attr("href", "javascript: void(0);").text("Add");
        sel.append(this._createDoc("h4", "title")
                   .text("AppendColumns：")
                   .append(addM)
                   .add(attrUl.append(this._createListtableConfigLi())));
        addM.click(function(e){
            e.stopPropagation();
            attrUl.append(that._createListtableConfigLi());
        });
        return this._createDoc("ul").append(sel);
    },
    //isFn : isID : type : key : fn
    _createListtableConfigLi: function(val) {
        var opts = { isFn: 0, isID: 0, type: "data", key: "", fn: "" };
        opts = $.extend(opts, val);
        var attrSel = this._createDoc("li", "attr-sel config-sel");
        attrSel.html('{' + ' <label class="mr2"><input type="checkbox" name="isFn" class="mr2" '
                     + (parseInt(opts.isFn) == 1 ? ' checked' : '') + '/><strong>isFn</strong></label>'
                     + '， <label class="mr2"><input type="radio" name="isID" class="mr2" '
                     + (parseInt(opts.isID) == 1 ? ' checked' : '') + '/><strong>isID</strong></label>'
                     + '，' + '<strong>type：</strong>' + '<input type="text" name="type" class="attr-ipt" '
                     + 'value="' + opts.type + '"/>'
                     + '，' + '<strong>key：</strong>' + '<input type="text" name="key" class="attr-ipt" '
                     + 'value="' + opts.key + '"/>'
                     + '，' + '<strong>fn：</strong>' + '<input type="text" name="fn" class="attr-ipt" '
                     + 'value="' + opts.fn + '"/>'
                     + '} <span class="del-m">&nbsp;</span>');
        attrSel.find(".del-m").click(function(e){
            e.stopPropagation();
            if ($(this).closest("ul").find("li.attr-sel").length > 1) {
                $(this).closest("li.attr-sel").remove();
            }
        });
        return attrSel;
    },
    setListtableConfigValue: function(con, opts) {
        if (opts && $.isArray(opts)) {
            var that = this;
            con.empty();
            $(opts).each(function() {
                con.append(that._createListtableConfigLi(this));
            });
        }
    },
    _getListtableConfigValue: function(con) {
        var rlt= [];
        con.find(".config-sel").each(function(i) {
            rlt[i] = {};
            rlt[i]["isFn"] = $(this).find('input[name="isFn"]').is(":checked") ? 1 : 0;
            rlt[i]["isID"] = $(this).find('input[name="isID"]').is(":checked") ? 1 : 0;
            rlt[i]["type"] = $(this).find('input[name="type"]').val();
            rlt[i]["key"] = $(this).find('input[name="key"]').val();
            rlt[i]["fn"] = $(this).find('input[name="fn"]').val();
        });
        return { appendColumns: rlt };
    },
    //--- graph config start---
    _graphConfigFac: function() {
        var that = this,
            sel = this._createDoc("li", "widget-sel"),
            attrUl = this._createDoc("ul", "attr-ul config-ul"),
            addM = this._createDoc("a", "add-m").attr("href", "javascript: void(0);").text("Add");
        sel.append(this._createDoc("h4", "title")
                   .text("GraphConfig：")
                   .add(attrUl.append(this._createGraphConfigLi())).add(addM));
        addM.click(function(e){
            e.stopPropagation();
            attrUl.append(that._createGraphConfigLi());
        });
        return this._createDoc("ul").append(sel);
    },
    //name : type : visible : round : yUnit
    _createGraphConfigLi: function(val){
        var opts = { graph_name: '', graph_type: "line", graph_visible: 1, graph_round: 0, graph_unit: "" };
        if(val){
            opts.graph_name = val.name;
            opts.graph_type = val.type;
            opts.graph_visible = val.visible;
            opts.graph_round = val.round;
            opts.graph_unit = val.unit;
        }
        var attrSel = this._createDoc("li", "attr-sel config-sel");
        attrSel.html('{' + '<strong>name：</strong>' + '<input type="text" name="graph_name" class="attr-ipt" '
                     + 'value="' + opts.graph_name + '"/>'
                     + '，' + '<strong>type：</strong>' + '<input type="text" name="graph_type" class="attr-ipt" '
                     + 'value="' + opts.graph_type + '"/>'
                     + '，' + '<label class="mr2"><input type="checkbox" name="graph_visible" class="mr2" '
                     + (parseInt(opts.graph_visible) == 1 ? ' checked' : '') + '/>visible</label>'
                     + '，' + '<label class="mr2"><input type="checkbox" name="graph_round" class="mr2" '
                     + (parseInt(opts.graph_round) == 1 ? ' checked' : '') + '/>round</label>'
                     + '，' + '<strong>unit：</strong>' + '<input type="text" name="graph_unit" class="attr-ipt" '
                     + 'value="' + opts.graph_unit + '"/>'
                     + '} <span class="del-m">&nbsp;</span>');
        attrSel.find(".del-m").click(function(e){
            e.stopPropagation();
            if ($(this).closest("ul").find("li.attr-sel").length > 1) {
                $(this).closest("li.attr-sel").remove();
            }
        });
        return attrSel;
    },
    setGraphConfigValue: function(con, opts){
        if(opts && $.isArray(opts)){
            var that = this;
            con.empty();
            $(opts).each(function(){
                con.append(that._createGraphConfigLi(this));
            });
        }
    },
    _getGraphConfigValue: function(con){
        var rlt= [];
        con.find(".config-sel").each(function(i){
            rlt[i] = {};
            rlt[i]["name"] = $(this).find('input[name="graph_name"]').val();
            rlt[i]["type"] = $(this).find('input[name="graph_type"]').val();
            rlt[i]["visible"] = $(this).find('input[name="graph_visible"]').is(":checked") ? 1 : 0;
            rlt[i]["round"] = $(this).find('input[name="graph_round"]').is(":checked") ? 1 : 0;
            rlt[i]["unit"] = $(this).find('input[name="graph_unit"]').val();
        });
        return { chartConfig: rlt };
    },
    //---table thead start---
    _theadFac: function(config){
        config = config ? config : this._getTheadConfig();
        var that = this,
            sel = this._createDoc("li", "widget-sel"),
            attrUl = this._createDoc("ul", "attr-ul thead-ul"),
            addM = this._createDoc("a", "add-m").attr("href", "javascript: void(0);").text("Add").css({ "margin-left" : "70px" });
        sel.append(this._createDoc("h4", "title").text(config.title).append(addM)
                   .add(attrUl.append(this._createTheadLi(config.config))));
        addM.click(function(e){
            e.stopPropagation();
            attrUl.append(that._createTheadLi(config.config));
        });
        return this._createDoc("ul").append(sel);
    },
    setTheadValue: function(config, con, val){
        con.empty();
        var that = this;
        $.each(val, function(i){
            con.append(that._createTheadLi(config, val[i]));
        });
    },
    _getTheadValue: function(con, config){
        var rlt= {};
        config = config ? config : (this._getTheadConfig()).config;
        con.find(".attr-sel").each(function(i){
            var t = $(this);
            $.each(config, function(){
                if (typeof rlt[this.name] === "undefined") {
                    rlt[this.name] = [];
                }
                rlt[this.name].push(t.find('input[name="' + this.name + '[]"]').val());
            });
        });
        return rlt;
    },
    _createTheadLi: function(config, val) {
        var attrSel = this._createDoc("li", "attr-sel thead-sel");
        var html = '{';
        $.each(config, function() {
            html += this.title + '：<input type="text" name="' + this.name
                + '[]" class="attr-ipt" value="'
                + (val && val[this.title] ? val[this.title] : '') + '" > ';
        });
        html += '} <span class="del-m">&nbsp;</span>';
        attrSel.html(html);
        attrSel.find(".del-m").click(function(e){
            e.stopPropagation();
            if ($(this).closest("ul").find("li.attr-sel").length > 1) {
                $(this).closest("li.attr-sel").remove();
            }
        });
        return attrSel;
    },
    _getTheadConfig: function(){
        return {
            title: "Thead：",
            config: [{ name: "thead_type", title: "type" },{
                name: "thead_title", title: "title" }]
        };
    },
    _getSelctlDataConfig: function(){
        return {
            title: "Data:",
            config: [{ name: "control_id", title: "id" },{
                name: "control_name", title: "name" }]
        };
    },
    //---attribute start---
    _attributeFac: function(title, className){
        title = title ? title : "Attr：";
        className = className ? className : "dft-ul";
        var that = this,
            sel = this._createDoc("li", "widget-sel"),
            attrUl = this._createDoc("ul", "attr-ul " + className),
            addM = this._createDoc("a", "add-m").attr("href", "javascript: void(0);").text("Add").css({"margin-left" : "84px"});
        sel.append(this._createDoc("h4", "title").text(title).append(addM).add(attrUl.append(this._createAttrLi())));
        addM.click(function(e) {
            e.stopPropagation();
            attrUl.append(that._createAttrLi());
        });
        return this._createDoc("ul").append(sel);
    },
    setAttrValue: function(con, aKey, aValue){
        if(aKey.length){
            con.empty();
            for(var i = 0; i < aKey.length; i++){
                con.append(this._createAttrLi(aKey[i], aValue[i]));
            }
        }
    },
    _getAttrValue: function(con){
        var rlt= {
            attr_key: [],
            attr_value: []
        };
        con.find(".attr-sel").each(function(){
            var key = $(this).find('input[name="attr_key[]"]').val(),
                value = $(this).find('input[name="attr_value[]"]').val();
            if(key && value){
                rlt["attr_key"].push(key);
                rlt["attr_value"].push(value);
            }
        });
        return rlt;
    },
    _createAttrLi: function(key, value){
        var attrSel = this._createDoc("li", "attr-sel");
        attrSel.html( '<input type="text" name="attr_key[]" class="attr-ipt" value="' + (key ? key : '') + '">  =>  '
                      + ' <input type="text" name="attr_value[]" class="attr-ipt" value="' + (value ? value : '') + '">'
                      + '<span class="del-m">&nbsp;</span>');
        attrSel.find(".del-m").click(function(e){
            e.stopPropagation();
            if ($(this).closest("ul").find("li.attr-sel").length > 1) {
                $(this).closest("li.attr-sel").remove();
            }
        });
        return attrSel;
    },
    //---metadata start---
    _metadataFac: function(){
        var that = this,
            con = this._createDoc("div", "widget-sel"),
            titleCon =  this._createDoc("div", "meta-title-con"),
            metaCon = this._createDoc("div", "choose-con-inline"),
            ul = this._createDoc("ul", "attr-ul metadata-ul"),
            addM = this._createDoc("a", "add-m fr").attr("href", "javascript: void(0);").text("新建");
        titleCon.append(this._createDoc("span", "title-inline").text("添加数据元:").add(metaCon).add(addM));
        _createMetadata(metaCon, function(curObj, title){
            ul.append(that._createMetaLi(ul, title.attr("data-id")));
        });
        addM.click(function(e){
            e.stopPropagation();
            ul.append(that._createMetaLi(ul));
        });
        this._setMetaIndex(ul);
        return con.append(titleCon.add(ul));
    },
    _setMetaIndex: function(ul) {
        ul.find(".attr-sel").each(function(i) {
            $(this).find(".data-index").attr("data-index", i).text('[' + i + ']：');
        });
    },
    _createMetaLi: function(ul, url) {
        var that = this, attrSel = this._createDoc("li", "attr-sel");
        var metaIndex = parseInt(ul.find(".attr-sel:last .data-index").attr("data-index"), 10);
        metaIndex = metaIndex || metaIndex == 0 ? metaIndex + 1 : 0;
        attrSel.html('<i class="data-index" data-index=' + metaIndex + '>['
                     + metaIndex + ']：</i>'
                     + '<input type="text" class="url-ipt" name="data_info[]" value="'+ (url ? url : '') + '">'
                     + '<span class="del-m">&nbsp;</span>');
        attrSel.find(".del-m").click(function(e){
            e.stopPropagation();
            $(this).closest("li.attr-sel").remove();
            that._setMetaIndex(ul);
        });
        return attrSel;
    },
    setMetaValue: function(con, aValue) {
        if(aValue && aValue.length){
            con.empty();
            for(var i = 0; i < aValue.length; i++){
                con.append(this._createMetaLi(con, aValue[i]));
            }
        }
    },
    _getMetaValue: function(con) {
        var i = 0, rlt= {};
        con.find(".attr-sel").each(function(){
            rlt['data_info['+ i + ']'] = $(this).find('input[name="data_info[]"]').val();
            i++;
        });
        return rlt;
    },
    //---url select match start---
    //用于"data"中请求数据url
    _urlMatchFac: function() {
        var that = this,
            sel = this._createDoc("li", "widget-sel"),
            addM = this._createDoc("a", "add-m fr").attr("href", "javascript: void(0);").text("新建");
        attrUl = this._createDoc("ul", "attr-ul match-ul");
        sel.append((this._createDoc("h4", "title").text("UrlMatch：").append(addM)).add(attrUl));
        addM.click(function(e){
            e.stopPropagation();
            attrUl.append(that._createMatchLi());
        });
        return this._createDoc("ul").append(sel);
    },
    setMatchValue: function(con, aUrlMatch) {
        var that = this;
        con.empty();
        for(var i = 0; i < aUrlMatch.length; i++){
            con.append(this._createMatchLi(aUrlMatch[i].id, aUrlMatch[i].common, aUrlMatch[i].data));
        }
    },
    _getMatchValue: function(con, argument){
        argument = argument && $.isArray(argument) ? argument : [];
        var i = 0, rlt= {};
        con.find(".attr-sel").each(function(){
            var url = '', common = $(this).find('input[name="common[]"]').val();
            if(common){
                var arr = common.split("?"),
                    param = parseArgs(arr[1]);
                param.qoq = inArray("qoq", argument) ? 1 : 0;
                param.yoy = inArray("yoy", argument) ? 1 : 0;
                param.average = inArray("average", argument) ? 1: 0;
                param.sum = inArray("sum", argument) ? 1 : 0;
                url = arr[0] + "?";
                $.each(param, function(key, val){
                    url += key + '=' + val + '&';
                });
            }
            rlt['urlMatch[' + i + ']'] = {
                id: $(this).find('input[name="id[]"]').val(),
                common: url,
                data: $(this).find('input[name="data[]"]').val()
            };
            i++;
        });
        return rlt;
    },
    _createMatchLi: function(id, common, data){
        var attrSel = this._createDoc("li", "attr-sel");
        attrSel.html( '' + 'id: <input type="text" name="id[]" class="attr-ipt w60" value="' + (id ? id : '') + '">，'
                      + 'common: <input type="text" name="common[]" class="attr-ipt w420" value="' + (common ? common : '') + '">，'
                      + 'data: <input type="text" name="data[]" class="attr-ipt w60" value="' + ( data ? data : '') + '">'
                      + '<span class="del-m">&nbsp;</span>');
        attrSel.find(".del-m").click(function(e){
            e.stopPropagation();
            $(this).closest("li.attr-sel").remove();
        });
        return attrSel;
    },
    //----------
    //---url extend start---
    //用于"data"中请求数据url
    _urlExtendFac: function(){
        var that = this,
            sel = this._createDoc("li", "widget-sel"),
            attrUl = this._createDoc("ul", "attr-ul url-ul");
        sel.append(this._createDoc("h4", "title").text("UrlExtend：").add(attrUl));
        $(that._getDimensionConfig()).each(function(){
            attrUl.append(that._createUrlLi(this));
        });
        return this._createDoc("ul").append(sel);
    },
    setUrlValue: function(con, aUrlExtend){
        if(aUrlExtend && aUrlExtend.length){
            var that = this;
            con.empty();
            $(this._getDimensionConfig()).each(function(){
                if(aUrlExtend[this.value-1]){
                    var urlExtend = aUrlExtend[this.value-1];
                    con.append(that._createUrlLi(this, urlExtend.common, urlExtend.data));
                } else {
                    con.append(that._createUrlLi(this));
                }
            });
        }
    },
    _getUrlValue: function(con, argument){
        argument = argument && $.isArray(argument) ? argument : [];
        var i = 0, rlt= {};
        con.find(".attr-sel").each(function(){
            var url = '', common = $(this).find('input[name="common[]"]').val();
            if(common){
                var arr = common.split("?"),
                    param = parseArgs(arr[1]);
                param.qoq = inArray("qoq", argument) ? 1 : 0;
                param.yoy = inArray("yoy", argument) ? 1 : 0;
                param.average = inArray("average", argument) ? 1 : 0;
                param.sum= inArray("sum", argument) ? 1 : 0;
                url = arr[0] + "?";
                $.each(param, function(key, val){
                    url += key + '=' + val + '&';
                });
            }
            rlt['urlExtend[' + i + ']'] = {
                common: url,
                data: $(this).find('input[name="data[]"]').val()
            };
            i++;
        });
        return rlt;
    },
    _createUrlLi: function(config, common, data){
        var attrSel = this._createDoc("li", "attr-sel");
        attrSel.html( '<span class="dp-ib w45">[' + config.title + ']</span>'
                      + 'common: <input type="text" name="common[]" class="attr-ipt w420" value="' + (common ? common : '') + '">，'
                      + 'data: <input type="text" name="data[]" class="attr-ipt w160" value="' + ( data ? data : '') + '">');
        return attrSel;
    },
    //----------
    _wrapFac: function() {
        var o = this.options;
        return this._widgetSelFac(this.getConfig("wrap" , o.data ? o.data : null));
    },
    _tabFac: function() {
        var o = this.options;
        return this._widgetSelFac(this.getConfig("tab", o.data ? o.data : null));
    },
    _dataFac: function() {
        var o = this.options;
        return this._widgetSelFac(this.getConfig("data", o.data ? o.data : null));
    },
    _listtableFac: function() {
        var o = this.options;
        return this._widgetSelFac(this.getConfig("listtable", o.data ? o.data : null));
    },
    _tableFac: function() {
        var o = this.options;
        return this._widgetSelFac(this.getConfig("table", o.data ? o.data : null));
    },
    _graphFac: function() {
        var o = this.options;
        return this._widgetSelFac(this.getConfig("graph", o.data ? o.data : null));
    },
    _selControlFac: function() {
        var o = this.options;
        return this._widgetSelFac(this.getConfig("selcontrol", o.data ? o.data : null));
    },
    _widgetSelFac: function(config) {
        if (config && $.isArray(config)) {
            var that = this,
                html = '<ul>';
            $.each(config, function() {
                switch(this.type) {
                case "input" :
                    html += that._createInput(this); break;
                case "textarea" :
                    html += that._createTextarea(this); break;
                case "select" :
                    html += that._createSelect(this); break;
                case "radio" :
                    html += that._createRadio(this); break;
                case "checkbox" :
                    html += that._createCheckbox(this); break;
                default: break;
                }
            });
            html += '</ul>';
            return html;
        }
    },
    _createCheckbox: function(opts){
        var html = '';
        html += '<li class="widget-sel">'
            + '<span class="title-inline">' + opts.title + '</span>';
        $.each(opts.config, function(){
            html += '<label class="mr20">'
                + '<input type="checkbox" name="' + opts.name + '" value="' + this.value + '" '
                + ( this.checked ? ' checked' : '' )
                + '/> ' + this.title + '</label>';
        });
        html += '</li>';
        return html;
    },
    _createInput: function(opts){
        return '<li class="widget-sel">'
            + '<span class="title-inline">' + opts.title + '</span>'
            + '<input class="ipttxt ' + (opts.necessary ? ' necessary' : '')
            + '" type="text" name="' + opts.name + '" value="' + (opts.value ? opts.value : '') + '">'
            + '</li>';
    },
    _createTextarea: function(opts){
        return '<li class="widget-sel">'
            + '<h4 class="title">' + opts.title + '</h4>'
            + '<textarea class="textarea ' + (opts.necessary ? ' necessary' : '')
            + '" name="' + opts.name + '">'
            + (opts.value ? opts.value : '') + '</textarea>'
            + '</li>';
    },
    _createSelect: function(opts){
        var html = '<li class="widget-sel">'
                + '<span class="title-inline">' + opts.title + '</span>'
                + '<select class="sel" id="' + opts.id + '" name="' + opts.name + '">';
        $.each(opts.config, function(){
            html += '<option value="' + this.id+ '">' + this.name + '</option>';
        });
        html += '</select></li>';
        return html;
    },
    _createRadio: function(opts){
        var html = '';
        html += '<li class="widget-sel">'
            + '<span class="title-inline">' + opts.title + '</span>';
        $.each(opts.config, function(){
            html += '<label class="mr20">'
                + '<input type="radio" name="' + opts.name + '" value="' + this.value + '" '
                + ( this.checked ? ' checked' : '' )
                + '/> ' + this.title + '</label>';
        });
        html += '</li>';
        return html;
    },
    getValue: function(form, type){
        switch(type) {
        case "wrap":
            return this._getWrapValue(form);
        case "tabs":
            return { component_type: "tabs"};
        case "tab":
            return this._getTabValue(form);
        case "data":
            return this._getDataValue(form);
        case "listtable":
            return this._getListtableValue(form);
        default:
            return {};
        }
    },
    _getWrapValue: function(form){
        var config = this._getWrapConfig(),
            rlt = { component_type: "wrap" };
        $.each(config, function(){
            var t = this;
            if(this.type == "input"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]').val();
            } else if(this.type == "radio"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]:checked').val();
            } else if(this.type == "textarea"){
                rlt[this.name] = form.find('textarea[name="' + this.name + '"]').val();
            } else if(this.type == "checkbox") {
                if(!rlt[this.name]) {
                    rlt[this.name] = [];
                }
                form.find('input[name="' + this.name + '"]:checked').each(function(){
                    rlt[this.name].push($(this).val());
                });
            }
        });
        $.extend(rlt,
                 this._getAttrValue(form.find(".dft-ul")),
                 this._getMetaValue(form.find(".metadata-ul")),
                 this._getProcessDataValue(form.find(".process-con")));
        if(form.find('input[name="isSelControl"]').is(":checked")){
            rlt = $.extend(rlt, this._getSelControlValue(form.find(".selctl-con")));
            rlt.isSelControl = 1;
        } else {
            rlt.isSelControl = 0;
        }
        return rlt;
    },
    _getSelControlValue: function(form){
        var config = this._getSelControlConfig(), rlt = {};
        $.each(config, function(){
            var t = this;
            if(this.type == "input"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]').val();
            } else if(this.type == "radio"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]:checked').val();
            } else if(this.type == "textarea"){
                rlt[this.name] = form.find('textarea[name="' + this.name + '"]').text();
            }
        });
        $.extend(rlt, this._getSelConfigValue(form.find(".selctl-ul")));
        return { selControl: rlt };
    },
    _getTabValue: function(form){
        var config = this._getTabConfig(),
            rlt = { component_type: "tab" };
        $.each(config, function(){
            var t = this;
            if(this.type == "input"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]').val();
            } else if(this.type == "radio"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]:checked').val();
            }
        });
        $.extend(rlt, this._getAttrValue(form.find(".dft-ul")));
        return rlt;
    },
    _getDataValue: function(form) {
        var config = this._getDataConfig(),
            rlt = { component_type: "data" };
        $.each(config, function(){
            var t = this;
            if (this.type == "input") {
                rlt[this.name] = form.find('input[name="' + this.name + '"]').val();
            } else if (this.type == "radio") {
                rlt[this.name] = form.find('input[name="' + this.name + '"]:checked').val();
            } else if (this.type == "checkbox") {
                if(!rlt[this.name]) {
                    rlt[this.name] = [];
                }
                form.find('input[name="' + this.name + '"]:checked').each(function(){
                    rlt[this.name].push($(this).val());
                });
            }
        });
        rlt = $.extend(rlt, this._getUrlValue(form.find(".url-ul"), rlt.argument));
        if(form.find('input[name="isSelControl"]').is(":checked")){
            rlt = $.extend(rlt, this._getMatchValue(form.find(".match-ul"), rlt.argument));
            rlt.isSelControl = 1;
        } else {
            rlt.isSelControl = 0;
        }
        if(form.find('input[name="show_table"]').is(":checked")){
            rlt = $.extend(rlt, this._getTableValue(form));
            rlt.show_table = 1;
        } else {
            rlt.show_table = 0;
        }
        if(form.find('input[name="show_graph"]').is(":checked")){
            rlt = $.extend(rlt, this._getGraphValue(form));
            rlt.show_graph = 1;
        } else {
            rlt.show_graph = 0;
        }
        rlt.component_type = "data";
        return rlt;
    },
    _getListtableValue: function(form){
        var config = this._getListtableConfig(),
            rlt = { component_type: "listtable" };
        $.each(config, function(){
            var t = this;
            if(this.type == "input"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]').val();
            } else if(this.type == "radio"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]:checked').val();
            } else if(this.type == "checkbox") {
                if(!rlt[this.name]) {
                    rlt[this.name] = [];
                }
                form.find('input[name="' + this.name + '"]:checked').each(function(){
                    rlt[this.name].push($(this).val());
                });
            }
        });
        rlt = $.extend(rlt, this._getTheadValue(form.find(".attr-ul.thead-ul")),
                       this._getListtableConfigValue(form.find(".attr-ul.config-ul")));
        return rlt;
    },
    _getTableValue: function(form) {
        var config = this._getTableConfig(),
            rlt = { component_type: "table" };
        $.each(config, function(){
            var t = this;
            if(this.type == "input"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]').val();
            } else if(this.type == "radio"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]:checked').val();
            } else if(this.type == "checkbox"){
                var checkbox = form.find('input[name="' + this.name + '"]');
                rlt[t.name] = [];
                $(checkbox).each(function(){
                    rlt[t.name]['"' + $(this).val() + '"'] = $(this).is(":checked") ? 1 : 0;
                });
            }
        });
        rlt = $.extend(rlt, this._getTheadValue(form.find(".attr-ul.thead-ul")));
        return rlt;
    },
    _getGraphValue: function(form){
        var config = this._getGraphConfig(),
            rlt = { component_type: "graph" };
        $.each(config, function(){
            var t = this;
            if(this.type == "input"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]').val();
            } else if(this.type == "radio"){
                rlt[this.name] = form.find('input[name="' + this.name + '"]:checked').val();
            }
        });
        rlt = $.extend(rlt, this._getGraphConfigValue(form.find(".attr-ul.config-ul")));
        return rlt;
    },
    setValue: function(con, opts, type){
        var data = {}, config = [];
        switch(type){
        case "wrap":
            config = this._getWrapConfig();
            this.setMetaValue(con.find(".metadata-ul"), opts.data_info);
            this.setProcessDataValue(con.find(".process-con"), opts.process_type, opts.process_list);
            if(opts.isSelControl == 1){
                con.find('input[name="isSelControl"]').attr("checked", true).click().attr("checked", true);
                this.setValue(con.find(".selctl-con"), opts.selControl, "selcontrol");
                this.setSelConfigValue(con.find(".selctl-ul"), opts.selControl.selConfig);
            }
            this.setAttrValue(con.find(".dft-ul"), opts.attr_key, opts.attr_value);
            break;
        case "selcontrol" :
            config = this._getSelControlConfig();
            break;
        case "tab":
            config = this._getTabConfig();
            this.setAttrValue(con.find(".dft-ul"), opts.attr_key, opts.attr_value);
            break;
        case "data":
            config = this._getDataConfig();
            this.setUrlValue(con.find(".url-ul"), opts.urlExtend);
            if(opts.show_table == 1){
                con.find('input[name="show_table"]').attr("checked", true).click().attr("checked", true);
                this.setValue(con.find(".table-con"), opts, "table");
            }
            if(opts.show_graph == 1){
                con.find('input[name="show_graph"]').attr("checked", true).click().attr("checked", true);
                this.setValue(con.find(".graph-con"), opts, "graph");
            }
            if(opts.isSelControl == 1){
                con.find('input[name="isSelControl"]').attr("checked", true).click().attr("checked", true);
                this.setMatchValue(con.find(".match-ul"), opts.urlMatch);
            }
            break;
        case "listtable":
            config = this._getListtableConfig();
            var val = [];
            for(var i = 0; i < opts.thead_type.length; i++){
                val.push({
                    type: opts.thead_type[i],
                    title: opts.thead_title[i]
                });
            }
            this.setTheadValue((this._getTheadConfig()).config, con.find(".thead-ul"), val);
            this.setListtableConfigValue(con.find(".config-ul"), opts.appendColumns);
            break;
        case "table":
            config = this._getTableConfig();
            var val = [];
            for(var i = 0; i < opts.thead_type.length; i++){
                val.push({
                    type: opts.thead_type[i],
                    title: opts.thead_title[i]
                });
            }
            this.setTheadValue((this._getTheadConfig()).config, con.find(".thead-ul"), val);
            break;
        case "graph":
            config = this._getGraphConfig();
            this.setGraphConfigValue(con.find(".config-ul"), opts.chartConfig);
            break;
        default:
            break;
        }
        $.each(config, function() {
            var t = this;
            if(this.type == "input") {
                con.find('input[name="' + this.name + '"]').val(opts[this.name]);
            } else if(this.type == "radio") {
                con.find('input[name="' + this.name + '"][value="' + opts[this.name] + '"]')
                    .attr("checked", true);
            } else if(this.type == "textarea") {
                con.find('textarea[name="' + this.name + '"]').val(opts[this.name]);
            } else if(this.type == "checkbox") {
                con.find('input[name="' + this.name + '"]').each(function() {
                    if(inArray($(this).val(), opts[this.name])) {
                        $(this).attr("checked", true);
                    }
                });
            }
        });
    },
    getConfig: function(type, opts) {
        var data = {}, config = [];
        switch (type) {
        case "wrap":
            data = {
                title: "",
                ignore: 0,
                ignoreId: '',
                headEnabled: 1,
                bottomEnabled: 1,
                condition: [],
                width: 100,
                comment: "", //"showComment"
                download: "wrapDownload",
                favor: "addToFavor",
                urlExtend: [],
                edit: "",
                remove: "",
                heatmap: "",    // setThermodynamic
                renameUrl: "",
                nameListUrl: ""
            };
            config = this._getWrapConfig();
            break;
        case "tab":
            data = {
                title: "",
                tabsSkin: '',
                ignore: 0,
                ignoreId: ''
            };
            config = this._getTabConfig();
            break;
        case "data":
            data = {
                urlPage: "getPageParam",
                urlTimeDimension: 1,
                argument: [],
                isTimeDimensionInherit: 1
            };
            config = this._getDataConfig();
            break;
        case "listtable":
            data = {
                urlPage: "getPageParam",
                urlExtend: "",
                urlPagination: "",
                renameUrl: "",
                isAjax: 1,
                enablePagination: 0
            };
            config = this._getListtableConfig();
            break;
        case "table":
            data = {
                hugeTable: 0,
                hide: 1,
                minHeight: null,
                prepareData: "prepareTableData",
                theadAvg: 0
            };
            config = this._getTableConfig();
            break;
        case "graph":
            data = {
                chartStock: 0,
                chartPage: 0,
                timeDimension: "day",
                lineColumn: 0,
                lineAreaColumn: 0,
                isSetYAxisMin: 0,
                columnStack: '',
                keyUnit: ''
            };
            config = this._getGraphConfig();
            break;
        case "selcontrol":
            data = {
                isMatch: 0,
                isMultiple: 0
            };
            config = this._getSelControlConfig();
            break;
        default:
            break;
        }
        $.each(config, function() {
            var t = this;
            if (this.type == "input") {
                this.value = data[this.name];
            } else if (this.type == "radio") {
                $.each(this.config, function() {
                    if (this.value == data[t.name]) {
                        this.checked = true;
                    }
                });
            } else if (this.type == "checkbox") {
                $.each(this.config, function() {
                    if (inArray(this.value, data[t.name])) {
                        this.checked = true;
                    }
                });

            }
        });
        return config;
    },
    //Wrap
    _getWrapConfig: function(){
        return [{
            name: "title",
            title: "模块名称：",
            necessary: true,
            type: "input"
        }, {
            type: "radio",
            title: "Ignore：",
            name: "ignore",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            name: "ignoreId",
            title: "Ignore-id：",
            type: "input"
        }, {
            name: "component_desc",
            title: "模块说明：",
            necessary: false,
            type: "input"
        }, {
            type: "radio",
            title: "HeadEnabled：",
            name: "headEnabled",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "checkbox",
            title: "HeadTime：",
            name: "headTime",
            config: [{ title: "日", value: "1" },
                     { title: "周", value: "2" },
                     { title: "月", value: "3" },
                     { title: "小时", value: "5" },
                     { title: "分", value: "4" },
                     { title: "版本周", value: "6" }]
        }, {
            type: "checkbox",
            title: "Condition：",
            name: "condition",
            config: [{ title:  "contrast", value: "contrast" }, {
                title: "month-to-month", value: "month-to-month" }, {
                    title:  "month", value: "month" }]
        }, {
            type: "radio",
            title: "BottomEnabled：",
            name: "bottomEnabled",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "radio",
            title: "显示宽度比例：",
            name: "width",
            config: [{ title:  "100%", value: 100 }, { title:  "50%", value: 50 }, { title:  "25%", value: 25 }]
        }, {
            name: "ignoreTools",
            title: "忽略方法名称：",
            type: "input",
            value: "ignoreWrap"
        }, {
            name: "download",
            title: "下载方法名称：",
            type: "input"
        }, {
            name: "edit",
            title: "编辑方法名称：",
            type: "input"
        }, {
            name: "remove",
            title: "删除方法名称：",
            type: "input"
        }, {
            name: "favor",
            title: "添加到我的收藏：",
            type: "input"
        }, {
            name: "heatmap",
            title: "热力图开关：",
            type: "input"
        }, {
            name: "comment",
            title: "显示注释：",
            type: "input"
        },{
            name: "renameUrl",
            title: "renameUrl：",
            type: "input"
        },{
            name: "nameListUrl",
            title: "nameListUrl：",
            type: "input"
        }];
    },
    _getTabConfig: function(){
        return [{
            name: "title",
            title: "Tab名称：",
            necessary: true,
            type: "input"
        }, {
            type: "radio",
            title: "TabsSkin：",
            name: "tabsSkin",
            config: [{ title:  "default", value: '' }, { title:  "orange", value: "orange" }]
        }, {
            type: "radio",
            title: "Ignore：",
            name: "ignore",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            name: "ignoreId",
            title: "Ignore-id：",
            type: "input"
        }, {
            name: "component_desc",
            title: "模块说明：",
            type: "input"
        }];
    },
    _getDataConfig: function(){
        return [{
            name: "urlPage",
            title: "UrlPage：",
            necessary: true,
            type: "input"
        }, {
            type: "checkbox",
            title: "参数选项：",
            name: "argument",
            config: [{ title:  "qoq", value: "qoq" }, {
                title: "yoy", value: "yoy" }, {
                    title: "average", value: "average" }, {
                        title: "sum", value: "sum" }, {
                            title:  "percentage", value: "percentage" }]
        }, {
            type: "radio",
            title: "DimensionInherit：",
            name: "isTimeDimensionInherit",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "radio",
            title: "UrlTimeDimension：",
            name: "urlTimeDimension",
            config: this._getDimensionConfig()
        }];
    },
    _getDataParamConfig: function(){
        return ["qoq", "yoy", "average", "sum", "percentage"];
    },
    _getDimensionConfig: function(){
        return [{ value: 1, title: "天" },
                { value: 2, title: "周" },
                { value: 3, title: "月" },
                { value: 4, title: "分"},
                { value: 5, title: "时" },
                { value: 6, title: "版本周"}];
    },
    _getListtableConfig: function(){
        return [{
            name: "urlPage",
            title: "UrlPage：",
            necessary: true,
            type: "input"
        }, {
            name: "urlExtend",
            title: "UrlExtend：",
            necessary: true,
            type: "input"
        }, {
            type: "radio",
            title: "isAjax：",
            name: "isAjax",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            name: "renameUrl",
            title: "renameUrl：",
            type: "input"
        }, {
            name: "urlPagination",
            title: "UrlPagination：",
            type: "input"
        }, {
            type: "radio",
            title: "enablePagination：",
            name: "enablePagination",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }];
    },
    _getTableConfig: function(){
        return [{
            type: "radio",
            name: "hugeTable",
            title: "HugeTable：",
            config: [{ title: "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            name: "prepareData",
            title: "PrepareData：",
            type: "input"
        }, {
            type: "radio",
            title: "hide：",
            name: "hide",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            name: "theadFn",
            title: "TheadFn：",
            type: "input"
        }, {
            name: "minHeight",
            title: "minHeight：",
            type: "input"
        }, {
            type: "radio",
            title: "平均转化率：",
            name: "theadAvg",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }];
    },
    _getGraphConfig: function(){
        return [{
            type: "radio",
            title: "chartStock：",
            name: "chartStock",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "radio",
            title: "chartPage：",
            name: "chartPage",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "radio",
            title: "timeDimension：",
            name: "timeDimension",
            config: [{ title:  "min", value: "min" }, { title:  "day", value: "day" },
                     { title:  "onlymin", value: "onlymin" }]
        }, {
            type: "radio",
            title: "columnStack：",
            name: "columnStack",
            config: [{ title:  "default", value: "" }, { title:  "percent", value: "percent" }]
        }, {
            type: "radio",
            title: "lineAreaColumn：",
            name: "lineAreaColumn",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "radio",
            title: "lineColumn：",
            name: "lineColumn",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "radio",
            title: "isSetYAxisMin：",
            name: "isSetYAxisMin",
            config: [{ title:  "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            name: "keyUnit",
            title: "keyUnit：",
            type: "input"
        }];
    },
    _getSelControlConfig: function(){
        return [{
            type: "radio",
            name: "isMatch",
            title: "isMatch：",
            config: [{ title: "true", value: 1 }, { title:  "false", value: 0 }]
        }, {
            type: "radio",
            name: "isMultiple",
            title: "isMultiple：",
            config: [{ title: "true", value: 1 }, { title:  "false", value: 0 }]
        }];
    }
};

var mfac = new ModuleFac();

function _createMetadata(con, fn) {
    var selCon = $(document.createElement("div")).addClass("sel-con"),
        selP = $(document.createElement("div")).addClass("sel-p");
    var opts = {
        search : true,
        type : 2,
        page : 2,
        selected : [],
        obj : selP,
        mulRadio : 1,
        showBottom: true,
        callback: function(curObj, title) {
            if (fn) { fn(curObj, title); }
        }
    };
    //ajaxData(getUrl("common/Basicdata/getListGroupByPeriod"), null, function(data){
    ajaxData("../../common/basicdata/getMetadataListGroupByPeriod?", null, function(data){
        opts.data = _handleMetadataChoose(data);
        $.choose.core(opts);
    });
    selP.appendTo(selCon.appendTo(con));
}

/**
 * @brief _handleMetadataChoose
 * data : [{
 *      title: '',
 *      attr : { id : 1, otherAttr : ''},
 *      child : [{
 *          title : '',
 *          attr : { id : 1, child : true/false, cid : 1 }
 *      }]
 * }]
 * @return
 */
function _handleMetadataChoose(data) {
    var rlt = [];
    $.each(data, function(key, value) {
        var tmp = {
            title: ( key == 1 ? "天"
                     : key == 2 ? "周"
                     : key == 3 ? "月"
                     : key == 4 ? "分"
                     : key == 5 ? "时" : "版本周" ),
            attr: { id: key },
            children: []
        };
        $(value).each(function() {
            tmp.children.push({
                title: this.data_name,
                attr: { id: this.url }
            });
        });
        rlt.push(tmp);
    });
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
    if (hide) { overlayer({ text: "加载中..."}); }
    ajax(url, param, function(res) {
        if (res.result == 0) {
            if (hide) { hidelayer("加载成功~.~"); }
            if (fn) { fn(res.data); }
        } else {
            if (hide) { hidelayer(); }
            if (empt){
                if (fn) { fn([]); }
            } else {
                say("获取数据错误：" + res.err_desc);
            }
        }
    }, "POST");
}
