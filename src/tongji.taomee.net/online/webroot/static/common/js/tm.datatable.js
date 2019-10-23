(function($) {
"use strict";

$.datatable = {};
$.datatable.sort = {
    "string-pre": function (a) {
        if (typeof a != "string") {
            a = (a !== null && a.toString) ? a.toString() : "";
        }
        return a.toLowerCase();
    },
    "string-asc": function (x, y) {
        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    },
    "string-desc": function (x, y) {
        return ((x < y) ? 1 : ((x > y) ? -1 : 0));
    },
    "date-pre": function (a) {
        var x = Date.parse(a);

        if (isNaN(x) || x === "") {
            x = Date.parse("01/01/1970 00:00:00");
        }
        return x;
    },
    "date-asc": function (x, y) {
        return x - y;
    },
    "date-desc": function (x, y) {
        return y - x;
    },
    "number-pre": function (a) {
        return (a=="-" || a==="") ? 0 : a*1;
    },
    "number-asc": function (x, y) {
        return x - y;
    },
    "number-desc": function (x, y) {
        return y - x;
    }
};

$.widget("tm.datatable", {
options: {
    // sort
    sortType: ["asc", "desc"],
    // page
    pageSizeEnabled: false,
    pageSize: 10,
    pageSizeChangeEnabled: false,
    size: [5, 10, 20, 50, 100],
    pageSizeContainer: null,
    // search
    searchContainer: null,
    searchEnabled: false
},
table: null,
tableColumns: [],
tableData: [],
displayMaster: [],
display: [],
sort: [0, "desc", 0],
sortType: null,
sortFixed: [],
displayStart: 0,
displayEnd: 0,
pageSize: null,
pagination: null,
_create: function() {
    var o = this.options;
    this.element.addClass("datatable-wrapper");
    this.table = this.element.find("table").first().addClass("datatable");
    this.tableColumns = [];
    this.tableData = [];
    this.displayMaster = [];
    this.sortFixed = [];
    this.sortType = this.options.sortType;

    this._fetchColumns();
    this._fetchData();
    this.pageSize = this.displayMaster.length;
    // 排序
    this.table.addClass("sortable");
    this._prepareSort();
    // 分页
    if (o.pageSizeEnabled) this._initPage();
    // 每页个数
    if (o.pageSizeChangeEnabled) this._initSize();
    // 搜索
    if (o.searchEnabled) this._initSearch();
    this._initSortEvent();
},
_initSearch: function() {
    var wrap = $("<div>").addClass("datatable-search"),
        label = $("<label>"),
        search = $("<input/>"),
        that = this, timeout,
        length, value, i;
    search.on("input", function() {
        value = $(this).val();
        that.displayMaster = [];
        $(that.tableData).each(function() {
            i = 0;
            length = this.data.length;
            while (i < length) {
                if (this.data[i].toString().indexOf(value) > -1) {
                    that.displayMaster.push(this.index);
                    break;
                }
                i ++;
            }
        });
        if (timeout) window.clearTimeout(timeout);
        timeout = window.setTimeout(function() {
            that._sortable();
        }, 200);

    });
    label.append("搜索").append(search);
    if (this.options.searchContainer) {
        this.options.searchContainer.append(wrap.append(label));
    } else {
        this.element.prepend(wrap.append(label));
    }
},
_initSize: function() {
    var wrap = $("<div>").addClass("datatable-size"),
        label = $("<label>"),
        select = $("<select>"),
        that = this;
    $.each(this.options.size, function() {
        select.append("<option " + (this == that.pageSize ? "selected" : "") + ">" + this + "</option>");
    });
    select.on("change", function() {
        that.pageSize = $(this).val();
        that._resetPage();
        that._calculateEnd();
        that._drawRow();
    });
    label.append("每页显示").append(select).append("条数据");
    if (this.options.pageSizeContainer) {
        this.options.pageSizeContainer.append(wrap.append(label));
    } else {
        this.element.prepend(wrap.append(label));
    }
},
_initPage: function() {
    var that = this;
    this.pageSize = this.options.pageSize;
    this.pagination = new $.page.PageFactory({
        showNum: this.pageSize,
        container: this.element,
        total: this.tableData.length,
        preEvent: function(pageIndex, showNum) {
            that.displayStart = (pageIndex - 1) * showNum;
            that._calculateEnd();
            that._drawRow();
        },
        nextEvent : function( pageIndex, showNum ){
            that.displayStart = (pageIndex - 1) * showNum;
            that._calculateEnd();
            that._drawRow();
        }
    });
},
_resetPage: function() {
    this.displayStart = 0;
    this.pagination && this.pagination.resetPageBar();
},
_calculateEnd: function() {
    if ((this.displayEnd = this.displayStart + this.pageSize) > this.display.length) {
        this.displayEnd = this.display.length;
    }
},
_drawRow: function() {
    var start = this.displayStart,
        end = this.displayEnd,
        tbody = $(this.table.find("tbody").get(0)).empty();
    if (this.display.length) {
        for (start; start < end; start ++) {
            tbody.append(this.tableData[this.display[start]].tr.clone(true));
        }
    }
},
_initSortEvent: function() {
    var that = this;
    $.each(this.tableColumns, function() {
        var th = this;
        $(this.th).on("click", function() {
            $(that.tableColumns[that.sort[0]].th).removeClass("sorting-" + that.sort[1]);
            if (that.sort[0] != th.index) {
                that.sort = [th.index];
            }
            that.sort[2] = that.sortType[that.sort[2] + 1] ? that.sort[2] + 1 : 0;
            that.sort[1] = that.sortType[that.sort[2]];
            that._sortable();
        });
    });
    this._sortable();
},
_sortable: function() {
    var that = this,
        sortOption = this.sort,
        tableData = this.tableData;
    this.displayMaster.sort(function(a, b) {
        var dataType = that.tableColumns[sortOption[0]].dataType;
        return $.datatable.sort[(dataType ? dataType : "string") + "-" + sortOption[1]](
            tableData[a].dataSort[sortOption[0]],
            tableData[b].dataSort[sortOption[0]]
        );
    });

    $(this.tableColumns[sortOption[0]].th).addClass("sorting-" + sortOption[1]);
    this.display = this.sortFixed.concat(this.displayMaster);
    this._resetPage();
    this._calculateEnd();
    this._drawRow();
},
_prepareSort: function() {
    var tableColumns = this.tableColumns,
        columnLength = tableColumns.length,
        tableData = this.tableData,
        dataLength = tableData.length,
        dataSort,
        format, dataType, i, k;
    for (i = 0; i < columnLength; i ++) {
        $(tableColumns[i].th).addClass("sorting").append($("<i>"));
        dataType = tableColumns[i].dataType;
        tableColumns[i].sort.push("pre", 0);
        format = $.datatable.sort[(dataType ? dataType: "string") + "-pre"];
        for (k = 0; k < dataLength; k ++) {
            tableData[k].dataSort.push(format(tableData[k].data[i]));
        }
    }
},
_fetchData: function() {
    var tr = this.table.find("tbody").get(0).firstChild,
        nodeName, td, rowIndex = this.tableData.length,
        sortFormat;
    while (tr) {
        if (tr.nodeName.toUpperCase() === "TR") {
            this.tableData.push({
                index: rowIndex,
                tr: $(tr).clone(true),
                sortFixed: $.inArray(rowIndex, this.options.sortFixed) !== -1,
                dataSort: [],
                data: []
            });
            if ($.inArray(rowIndex, this.options.sortFixed) !== -1) {
                this.sortFixed.push(rowIndex);
            } else {
                this.displayMaster.push(rowIndex);
            }
            td = tr.firstChild;
            while (td) {
                nodeName = td.nodeName.toUpperCase();
                if (nodeName === "TD" || nodeName === "TH") {
                    this.tableData[rowIndex].data.push($.trim(td.innerHTML));
                }
                td = td.nextSibling;
            }
            rowIndex ++;
        }
        tr = tr.nextSibling;
    }
},
_fetchColumns: function() {
    var thead = this.table.get(0).getElementsByTagName("thead");
    if (thead.length !== 0) {
        this._detectHeader(thead);
    }
},
_detectHeader: function(thead) {
    var th = $(thead).children("tr").get(0).firstChild,
        nodeName,
        length = this.tableColumns.length;
    while (th) {
        nodeName = th.nodeName.toUpperCase();
        if (nodeName === "TD" || nodeName === "TH") {
            this._addColumn(th, length++);
        }
        th = th.nextSibling;
    }
},
_addColumn: function(th, col) {
    this.tableColumns.push({
        th: th,
        title: th.innerHTML,
        index: col,
        disabled: $(th).attr("data-disabled"),
        dataType: $(th).attr("data-type"),
        sort: [col]
    });
},
_init: function() {
},
refresh: function() {
    this.tableData = [];
    this.displayMaster = [];
    this.sortFixed = [];
    this._fetchData();
    this.pageSize = this.displayMaster.length;
    // 排序
    this._prepareSort();
    this._sortable();
},
show: function() {
}
});
})(jQuery);
