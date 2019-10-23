(function(window, undefined) {
function fac(options, extend) {
    console.log("conf.usage.js fac()");
    var modules = [];
    $(options).each(function() {
        $.extend(this, extend);
        switch (this.type) {
            case "wrap":
                modules.push(new WindowFactory(this));
                //$("#J_content").find(".fixed-body table").table("rejustWrapWidth");
                break;
            case "tabs":
                modules.push(new TabFactory(this));
                break;
            case "tabwrap":
                modules.push(tm.tabwrap(this));
                break;
            case "tabsExtendMore":
                modules.push(new TabExtendMoreFactory(this));
                break;
            case "data":
                modules.push(new DataFactory(this));
                break;
            case "table":
                modules.push(new TableFactory(this));
                break;
            case "graph":
                modules.push(tm.graph(this));
                break;
            case "hugeTable":
                modules.push(new HugeTableFactory(this));
                $("#J_content").find(".fixed-body table").table("rejustWrapWidth");
                break;
            case "progressiveTable":
                modules.push(tm.progressiveTable(this));
                break;
            case "hugeProgressiveTable":
                modules.push(tm.hugeProgressiveTable(this));
                break;
            case "list":
                modules.push(tm.list(this));
                break;
            case "listtable":
                modules.push(tm.listtable(this));
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
            var st = (new Date()).getTime()
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
        if( tblParent.hasClass('fixed-row') || tblParent.hasClass('fixed-corner') ){
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
    $.extend(options, {
        afterCreate: function(table) {
            //Table:table排序插件
            table.find("table").Table({
                orderBegin: -1
            });
        }
    });
    return tm.table(options);
}

function DataFactory(options)
{
    var child = [];
    return tm.data($.extend({
        refresh: function(option) {
            $(child).each(function() {
                this.refresh(option);
            });
        },
        afterLoad: function(data, container) {
            child = fac(options.child, {
                container: container,
                data: data
            });
        }
    }, options));
}

function WindowFactory(options)
{
    var child = [], wrap;
    $.extend(options, {
        refresh: function(option) {
            $(child).each(function() {
                this.refresh(option);
            });
        }
    });
    wrap = tm.wrap(options);
    child = fac(options.child, {
        container: wrap.getContent()
    });
    return wrap;
}

function TabFactory(options)
{
    var tab, opts={};
    $.extend(options, {
        refresh: function(option) {
            var activePanel = tab.getContainer().tabs("getActivePanel");
            $.extend(opts, option);
            $(activePanel.data("child")).each(function() {
                this.refresh(opts);
            });
        }
    });
    tab = tm.tab(options),
    tab.getContainer().tabs({
        activate: function(event, eventData) {
            tab.refresh(opts);
        },
        beforeLoad: function(event, eventData) {
            eventData.panel.data("child", fac(eventData.tab.data("data").child, {
                container: eventData.panel
            }));
            if(options.beforeLoad) options.beforeLoad(event, eventData);
        }
    });
    return tab;
}

function TabExtendMoreFactory(options)
{
    var tab, opts={};
    $.extend(options, {
        refresh: function(option) {
            var activePanel = tab.getContainer().tabsExtendMore("getActivePanel");
            $.extend(opts, option);
            $(activePanel.data("child")).each(function() {
                this.refresh(opts);
            });
        }
    });
    tab = tm.tab(options),
    tab.getContainer().tabsExtendMore({
        activate: function(event, eventData) {
            tab.refresh(opts);
        },
        beforeLoad: function(event, eventData) {
            eventData.panel.data("child", fac(eventData.tab.data("data").child, {
                container: eventData.panel
            }));
        }
    });
    return tab;
}
})(window);
