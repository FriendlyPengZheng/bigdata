(function(window, undefined) {
    if($.datatable && $.datatable.sort)
        $.extend($.datatable.sort, {
            "date-pre": function (a) {
                if (typeof a === "undefined") {
                    return Date.parse("01/01/1970 00:00:00");
                }
                a = a.split("~")[0];

                var x = Date.parse(a);
                if (isNaN(x) || x === "") {
                    x = Date.parse("01/01/1970 00:00:00");
                }
                return x;
            },
            "range-pre": function(a) {
                if (a == "-" || a === "") {
                    return 0;
                }
                var minus = a.replace("&lt;", "<").indexOf("<") !== -1;
                a = a.split(",")[0];
                return minus ? ((a.replace(/[^0-9]/ig, ""))*1)-1 : (a.replace(/[^0-9]/ig, ""))*1;
            },
            "range-asc": function(x, y) {
                return x - y;
            },
            "range-desc": function(x, y) {
                return y - x;
            }
        });
    function fac(options, extend) {
        var modules = [];
        $(options).each(function() {
            $.extend(this, extend);
            // for debug
            console.log("this.type is " + this.type);
            switch (this.type) {
            case "wrap":
                modules.push(new WindowFactory(this));
                //$("#J_content").find(".fixed-body table").table("rejustWrapWidth");
                break;
            case "tabs":
                modules.push(new TabFactory(this));
                break;
            case "tabsExtendMore":
                modules.push(new TabExtendMoreFactory(this));
                break;
            case "data":
                modules.push(new DataFactory(this));
                break;
            case "table":
                if (this.table === true) { this.hide = false; }
                modules.push(new TableFactory(this));
                break;
            case "graph":
                if (this.graph === false) { this.hide = true; }
                modules.push(tm.graph(this));
                break;
            case "hugeTable":
                if (this.table === true) { this.hide = false; }
                modules.push(new HugeTableFactory(this));
                $("#J_content").find(".fixed-body table").table("rejustWrapWidth");
                break;
            case "progressiveTable":    // My favor list is progressiveTable
                modules.push(tm.progressiveTable(this));
                break;
            case "hugeProgressiveTable":
                modules.push(tm.hugeProgressiveTable(this));
                break;
            case "listtable":
                modules.push(tm.listtable(this));
                break;
            case "list":
                modules.push(tm.list(this));
                break;
            }
        });
        return modules;
    }
    window.fac = fac;

    function HugeTableFactory(options)
    {
        $.extend(options, {
            afterCreate: function(container) {
                container.find("table").table({
                    colNum: options.checkbox ? 2 : 1,
                    wrapWidth: container.width(),
                    wrapHeight: options.height ? options.height : container.parent().height()
                });
                _tableChoose(container);
                fac(options.child, {
                    container: container.find(".fixed-body")
                });
            }
        });
        return tm.table(options);
    }
    function _tableChoose(con){
        var _curTable = function(tbl){
            var tblParent = tbl.parent();
            if( tblParent.hasClass('fixed-row') || tblParent.hasClass('fixed-corner') ) {
                var fhtColumn = tblParent.parent().find('.fixed-column');
                if( fhtColumn.length != 0 ){
                    tbl = fhtColumn.find(".fixed-table");
                }else{
                    tbl = tblParent.parent().find(".fixed-body .fixed-table");
                }
            }
            return tbl;
        };
        //choose all
        con.find(".fixed-table").find(".sel-total").click(function(e){
            e.stopPropagation();
            _curTable($(e.target).closest(".fixed-table")).find(".tbl-ckb").attr("checked", true);
        });
        //choose the other
        con.find(".fixed-table").find(".sel-other").click(function(e){
            e.stopPropagation();
            var curTable = _curTable( $(e.target).closest('.fixed-table') ),
                checked = curTable.find(".tbl-ckb:checked");
            checked.attr("checked", false);
            curTable.find(".tbl-ckb").not( checked ).attr("checked", true);
        });
    }
    function TableFactory(options)
    {
        options.sortable = options.sortable== false ? false : true;
        $.extend(options, {
            afterCreate: function(table) {
                //Table:table排序、分页插件
                if(options.sortable) {
                    var datatable = {
                        sortType: ["desc", "asc"],
                        pageSizeEnabled: true
                    };
                    if(options.average && options.sum){
                        datatable.sortFixed = [0, 1];
                    } else if(options.average || options.sum){
                        datatable.sortFixed = [0];
                    }
                    table.datatable(datatable);
                }
            }
        });
        return tm.table(options);
    }

    function DataFactory(options)
    {
        var child = [], extend = $.extend({}, options.extend);
        return tm.data($.extend({
            refresh: function(option) {
                $(child).each(function() {
                    this.refresh(option);
                });
            },
            afterLoad: function(data) {
                child = fac(options.child, $.extend(extend, {
                    container: options.container,
                    data: data
                }));
            }
        }, options));
    }

    function WindowFactory(options)
    {
        // for debug
        console.log("options.title is " + options.title + " ignore_id is " + options.ignoreId);
        var child = [], wrap, extend = $.extend({}, options.extend);
        $.extend(options, {
            refresh: function(option) {    // Option 是传递下来的消息
                $(child).each(function() {
                    this.refresh(option);
                });
            },
            afterLoad: function(option){
                console.log("ModuleManage wrap(WindowFactory) in afterLoad");
                child = fac(options.child, $.extend({}, option, {
                    container: option.container,
                    getOption: function(){
                        return option.superior.getOption();
                    },
					extend: extend
                }));
                var commentBtn = $(this).find('.mod-comment-btn');
                if (commentBtn.length) {
                    commentBtn.hover(function () {
                        $(this).trigger('click');
                    }, function () {
                        $(this).find('.help-container').fadeOut("fast");
                    });
                }
            }
        });
        wrap = tm.wrap(options);    // Draw wrapper
        return wrap;
    }

    function TabFactory(options)
    {
        var tab, opts = {}, optionsTabs = [];
        $.extend(options, {
            refresh: function(option) {
                var tabs = tab.getContainer().tabs("getTabs"),
                    activeTabs = tab.getContainer().tabs("getActive"),
                    opt = $.extend({}, opts, option),
                    child;
                $.extend(opts, option);
                if ((child = activeTabs.data("child"))) {
                    $(child).each(function() {
                        this.refresh(opt);
                    });
                }
                activeTabs.removeClass("need-refresh");
                if(!option.noRefresh){
                    tabs.not(activeTabs).filter(".tabs-loaded").addClass("need-refresh");
                }
            }
        });
        $(options.child).each(function(){
            if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore)) optionsTabs.push(this);
        });
        //console.log("TabFactory child is " + JSON.stringify(optionsTabs, null, 4));
        options.child = optionsTabs;
        tab = tm.tab(options),
        tab.getContainer().tabs({
            activate: function(event, eventData) {
                if(options.tabsClick) {
                    eventData.newTab.click();
                }
                if(eventData.newTab.hasClass("need-refresh")){
                    tab.refresh($.extend(opts, { "noRefresh" : true }));
                }
            },
            beforeLoad: function(event, eventData) {
                if(options.tabsClick) {
                    eventData.tab.click();
                }
                //处理wrap ignore
                var children = [];
                $(eventData.tab.data("data").child).each(function(){
                    if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore)) children.push(this);
                });
                //console.log("TabFactory children tab " + JSON.stringify(children, null, 4));
                eventData.tab.data("child", fac(children, {
                    container: eventData.panel,
                    getOption: function(){
                        return options.getOption();
                    },
                    extend: opts
                }));
            }
        });
        //用于外部添加tabs的点击事件
        if(options.tabsClick && $.isFunction(options.tabsClick)) {
            tab.getContainer().tabs("getTabs").click(function(){
                options.tabsClick($(this));
            });
        }
        return tab;
    }

    function TabExtendMoreFactory(options)
    {
        var tab, opts = {}, optionsTabs = [];
        $.extend(options, {
            refresh: function(option) {
                var tabs = tab.getContainer().tabsExtendMore("getTabs"),
                    activeTabs = tab.getContainer().tabsExtendMore("getActive"),
                    opt = $.extend({}, opts, option),
                    child;
                $.extend(opts, option);
                if ((child = activeTabs.data("child"))) {
                    $(child).each(function() {
                        this.refresh(opt);
                    });
                }
                activeTabs.removeClass("need-refresh");
                if(!option.noRefresh) {
                    tabs.not(activeTabs).filter(".tabs-loaded").addClass("need-refresh");
                }
            }
        });
        $(options.child).each(function(){
            if (!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore))
				optionsTabs.push(this);
        });
        options.child = optionsTabs;
        tab = tm.tab(options),
        tab.getContainer().tabsExtendMore({
            activate: function(event, eventData) {
                if(eventData.newTab.hasClass("need-refresh")){
                    tab.refresh($.extend(opts, { "noRefresh" : true }));
                }
            },
            beforeLoad: function(event, eventData) {
                //处理wrap ignore
                var children = [];
                $(eventData.tab.data("data").child).each(function(){
                    if(!this.ignore && !inArray(this.ignoreId, window.responseData.currentPage.ignore)) children.push(this);
                });
                eventData.tab.data("child", fac(children, {
                    container: eventData.panel,
                    getOption: function(){
                        return options.getOption();
                    },
                    extend: opts
                }));
            }
        });
        return tab;
    }

})(window);
