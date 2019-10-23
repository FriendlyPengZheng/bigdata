(function($, undefined) {
    window.tm = {};
    var moduleId = 0,
        slice = Array.prototype.slice;
    function getNextModuleId() {
        return ++moduleId;
    }

    function ModuleManage(name, base, prototype) {
        var constructor, basePrototype,
            proxiedPrototype = {};

        if (!prototype) {
            prototype = base;
            base = Module;
        }
        // this modules must be used with "new" keyword
        constructor = function (options) {
            if (!this._createModule) {
                return new constructor(options);
            }
            if (arguments.length) {
                this._createModule(options);
            }
        }
        $.extend(constructor, {
            _proto: $.extend({}, prototype)
        });

        basePrototype = new base();

        basePrototype.options = ModuleManage.extend({}, basePrototype.options);
        $.each(prototype, function(prop, value) {
            if (!$.isFunction(value)) {
                proxiedPrototype[prop] = value;
                return;
            }
            proxiedPrototype[prop] = (function() {
                var _super = function () {
                        return base.prototype[prop].apply(this, arguments);
                    },
                    _superApply = function (args) {
                        return base.prototype[prop].apply(this, args);
                    };
                return function() {
                    var __super = this._super,
                        __superApply = this._superApply,
                        returnValue;
                    this._super = _super;
                    this._superApply = _superApply;

                    returnValue = value.apply(this, arguments);

                    this._super = __super;
                    this._superApply = __superApply;

                    return returnValue;
                };
            })();
        });

        constructor.prototype = ModuleManage.extend(basePrototype, proxiedPrototype, {
            constructor: constructor,
            moduleName: name
        });

        tm[name] = function(options) {
            return new constructor(options);
        };
    }
    ModuleManage.extend = function(target) {
        var input = slice.call(arguments, 1),
            inputIndex = 0,
            inputLength = input.length,
            key,
            value;
        for (; inputIndex < inputLength; inputIndex++) {
            for (key in input[inputIndex]) {
                value = input[inputIndex][key];
                if (input[inputIndex].hasOwnProperty(key) && value !== undefined) {
                    // clone object
                    if ($.isPlainObject(value)) {
                        target[key] = $.isPlainObject(target[key]) ?
                            ModuleManage.extend({}, target[key], value) :
                        ModuleManage.extend({}, value);
                    } else {
                        target[key] = value;
                    }
                }
            }
        }
        return target;
    };
    /**
     * @function getOption:  when you want to deliver parent's option to his children
     * @function refresh:  refresh children when refresh parent
     */
    var Module = function() {};
    Module.prototype = {
        // options for init
        options: {},
        uuid: null,
        // real start, set the options
        _createModule: function(options) {
            this.uuid = this.getUuid();
            this.options = ModuleManage.extend({}, ModuleLanguage.get(this.moduleName), this.options, options);
            console.log("options", options);
            this.create();
        },
        // can be rewrite
        create: function() {
        },
        // create document
        _createDoc: function(type, className) {
            return $(document.createElement(type)).addClass(className);
        },
        // create button document
        _createButton: function(className) {
            return $(document.createElement("a")).addClass("mod-btn " + className)
                .append($(document.createElement("span")).html("&nbsp;"));
        },
        _createThermoSwitch: function (className, options) {
            var heatmapSel = $(document.createElement("select")).addClass("mod-heatmap " + className);

            for(var i = 0; i < options.length; i++) {
                heatmapSel.append($(document.createElement("option")).attr({dataId: i}).html(options[i]));
            }

            return heatmapSel;
            // return $(document.createElement("select")).addClass("mod-heatmap " + className)
            //     .append($(document.createElement("option")).html("data"));
        },
        // create radio button document
        _createRadioButton: function(listType) {
            var container = this._createDoc("ul", "clearfix change-btn-con"),
                list = $(),
                that = this;
            $(listType).each(function() {
                list = list.add(that._createDoc("li", "change-btn stat-button")
                                .append(that._createDoc("span").text(this.title))
                                .attr({
                                    dataId: this.dataId,
									title: this.title
                                }));
            });
            list.first().addClass("first");
            list.last().addClass("last");
            return container.append(list);
        },
        // trigger other callback
        _trigger: function(callback, option) {
            if ($.isFunction(callback)) {
                return callback.apply(this.container, [option, this.container]);
            } else if ($.isFunction(window[callback])) {
                return window[callback].apply(this.container, [option, this.container]);
            }
        },
        getUuid: function() {
            return getNextModuleId();
        }
    };

    var ModuleLanguageManage = function(language) {
        this.set(language);
        //this.set("zh_CN");
    };
    ModuleLanguageManage.prototype = {
        language: {},
        regional: {
            "zh_CN": {
                wrap: {
                    headTime: [{
                        title: "日",
                        dataId: 1
                    }, {
                        title: "周",
                        dataId: 2
                    }, {
                        title: "月",
                        dataId: 3
                    }],
                    removeTitle: "删除",
                    editTitle: "修改",
                    downloadTitle: "下载",
                    renameTitle: "重命名",
                    favorTitle: "添加到我的收藏",
                    thermoTitle: "热力图",
                    deletetTitle: "数据批量删除",
                    calcTitle: "对现有数据进行计算",
                    renameMessageTitle: "重命名（注：保存新名称请按Enter键。）",
                    renameMessageContent: "关闭并刷新",
                    sharedTitle: "分享"
                },
                tools: {
                    contrastTitle: "对比时间："
                },
                sel: {
                    ajaxEmptyHit: "没有数据。",
                    ajaxErrorHit: "获取数据错误，请稍后再试。"
                },
                multictrl: {
                    groupChooseTitle: '提示：最多可同时选择<em class="c-red"> 5 </em>项。',
                    ajaxEmptyHit: "没有数据。",
                    ajaxErrorHit: "获取数据错误，请稍后再试。"
                },
                table: {
                    ajaxEmptyHit: "没有数据。",
                    ajaxErrorHit: "获取数据错误，请稍后再试。",
                    percentageTitle: "百分比",
                    contrastRateTitle: "变化率",
                    yoyTitle: "同比",
                    qoqTitle: "环比",
                    allTitle: "全选",
                    inverseTitle: "反选"
                },
                hugeProgressiveTable: {
                    ajaxEmptyHit: "没有数据。",
                    ajaxErrorHit: "获取数据错误，请稍后再试。",
                    allTitle: "全选",
                    inverseTitle: "反选"
                },
                progressiveTable: {
                    ajaxEmptyHit: "没有数据。",
                    ajaxErrorHit: "获取数据错误，请稍后再试。"
                },
                graph: {
                    ajaxErrorHit: "获取数据错误，请稍后再试。",
                    ajaxEmptyHit: "没有数据。"
                },
                data: {
                    ajaxErrorHit: "获取数据错误，请稍后再试。",
                    ajaxEmptyHit: "没有数据。"
                },
                list: {
                    noticeTitle: "分布区间为空",
                    moveTitle: "移动",
                    multiTitle: "复选"
                },
                listtable: {
                    ajaxEmptyHit: "没有数据。",
                    ajaxErrorHit: "获取数据错误，请稍后再试。"
                }
            },
            "en_US": {
                wrap: {
                    headTime: [{
                        title: "day",
                        dataId: 1
                    }, {
                        title: "week",
                        dataId: 2
                    }, {
                        title: "month",
                        dataId: 3
                    }],
                    removeTitle: "delete",
                    editTitle: "edit",
                    downloadTitle: "download",
                    renameTitle: "rename",
                    favorTitle: "add to favor",
                    thermoTitle: "thermodynamic diagram",
                    deletetTitle: "delete data from table",
                    calcTitle: "calculation upon data",
                    renameMessageTitle: "Rename (note：Please click Enter when saving new name.)",
                    renameMessageContent: "closed and refresh",
                    sharedTitle: "Shared"
                },
                tools: {
                    contrastTitle: "contrast:"
                },
                sel: {
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data."
                },
                multictrl: {
                    groupChooseTitle: 'Note: Most alternative <em class="c-red"> 5 </em> at the same time.',
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data."
                },
                table: {
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data.",
                    percentageTitle: "percentage",
                    contrastRateTitle: "contrast rate",
                    yoyTitle: "yoy",
                    qoqTitle: "qoq",
                    allTitle: "All",
                    inverseTitle: "Inverse"
                },
                hugeProgressiveTable: {
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data.",
                    allTitle: "All",
                    inverseTitle: "Inverse"
                },
                progressiveTable: {
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data."
                },
                graph: {
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data."
                },
                data: {
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data."
                },
                list: {
                    noticeTitle: "Distribution is empty.",
                    moveTitle: "move",
                    multiTitle: "multi"
                },
                listtable: {
                    ajaxErrorHit: "There is an error, please try again later.",
                    ajaxEmptyHit: "No data."
                }
            }
        },
        set: function(language) {
            if (this.regional[language]) {
                this.language = this.regional[language];
            }
        },
        get: function(moduleType) {
            if (this.language[moduleType]) {
                return this.language[moduleType];
            } else {
                return {};
            }
        }
    };
    window.ModuleLanguage = new ModuleLanguageManage(window.responseData.locale);

    /**
     * @brief tools
     * tools for wrap
     * @param condition {array}: values in ("contrast", "month-to-month", "month");
     */
    ModuleManage("tools", {
        options: {
            condition: [],
			conditionOptions: null,
            container: $("body"),
            headTime: [],
            selControl: {},
            contrastTitle: undefined,
			wrapname: undefined
        },
        contrast: $(),
        from: undefined,
        to: undefined,
		tags: undefined,
        headTime: $(),
        timeDimension: 1,
        selControl: $(),
        multiControl: null,
        urlExtend: {},
        create: function() {
        	console.log("Create function in tool");
            var that = this;
            //console.log("tm.tool options is " + JSON.stringify(this.options, null, 4));
            //selControl可能异步加载选项
            if(inArray("selControl", this.options.condition)) {
                if(this.options.selControl.isMultiple) {
                    this.multiControl = tm.multictrl($.extend(this.options.selControl, {
                        container: that.options.container,
                        refresh: function(option){
                            that.urlExtend = option;
                            that.refresh();
                        },
                        afterLoad: function(option){
                            that.urlExtend = option;
                            that._create();
                        }
                    }));
                } else {
                    this.selControl = tm.sel($.extend(this.options.selControl, {
                        container: that.options.container,
                        refresh: function(option){
                            that.urlExtend = option.urlExtend;
                            that.refresh();
                        },
                        afterLoad: function(option) {
                            that.urlExtend = option.urlExtend;
                            that._create();
                        }
                    }));
                }
            } else {
                this._create();
            }
        },
        _create: function() {
        	console.log("_create function in tool");
            var that = this;
            if(inArray("contrast", this.options.condition)) {
                this._contrast();
            }
            if(inArray("month-to-month", this.options.condition)) {
                this._timeTools(2);
            }
            if(inArray("month", this.options.condition)) {
                this._timeTools(4);
            }
            if(inArray("timeDimension", this.options.condition)) {
                this._timeDimensionTools();
            }
			if (inArray("tagFilter", this.options.condition)) {
				this._tagFilter(this.options.conditionOptions.tagFilter, this.options.conditionOptions.ignore);
			}
            console.log("ModuleManage tool this.options.afterLoad start to trigger");
            this._trigger(this.options.afterLoad, {
                getOption: function() {
                    return that.getOption();
                }
            });
        },
        //时间维度控制功能
        _timeDimensionTools: function(){
            var that = this;
            this.headTime = this._createRadioButton(this.options.headTime).appendTo(this.options.container);
            //init head timeDimension buttons
            this.headTime.find("li:eq(0)").addClass("cur");
            that.timeDimension = this.headTime.find("li:eq(0)").attr("dataId");
            this.headTime.find("li").on("click", function() {
                if (!$(this).hasClass("cur")) {
                    that.headTime.find("li").removeClass("cur");
                    $(this).addClass("cur");
                    that.timeDimension = $(this).attr("dataId");
                    that.refresh();
                }
            }).attr({
				'sstid': that.options.wrapname
			});
        },
        //时间对比功能
        _contrast: function() {
            var that = this, datepicker;
            this.contrast = this._createDoc("span", "cts-title fr")
                .html('<label><input type="checkbox" name="cts-chk" class="cts-chk" >'
                      + this.options.contrastTitle + '</label>');
            datepicker = tm.datepicker({
                container: this.options.container,
                contrast: this.contrast.find(".cts-chk"),
                refresh: function(option){
                    that.from = option.from;
                    that.to = option.to;
                    that.refresh();
                }
            });
            this.contrast.appendTo(this.options.container);
            this.contrast.find(".cts-chk").click(function(){
                if($(this).is(":checked")){
                    datepicker.getContainer().find(".cts-date").focus();
                } else {
                    that.refresh();
                }
            });
        },
        //时间控制功能
        _timeTools: function(type){
            var that = this;
            var datepicker = tm.datepicker({
                container: this.options.container,
                type: type,
                refresh: function(option) {
                    that.from = option.from;
                    that.to = option.to;
                    that.refresh();
                },
                afterLoad: function(option) {
                    that.from = option.from;
                    that.to = option.to;
                }
            });
        },
		// 标签过滤功能
		_tagFilter: function (option, ignore) {
			var context = this,
				dft = option["default"] === undefined ? "all" : option["default"];

			// Add basic tags
			this.tags = dft;
			var dftHtml = '<a class="filter-opt stat-button week" sstid="' + $.trim(this.options.wrapname) + '" tag-type="week" title= ' + lang.t('"近一周落的自定义数据项"') + '>' + lang.t('近一周新加统计项') + '</a>'
					+ '<a class="filter-opt stat-button modify_week" sstid="' + $.trim(this.options.wrapname) + '" tag-type="modify_week" title=' + lang.t('"该模块下近一周有数据的统计项"') + '>' + lang.t('近一周有数据的统计项') + '</a>'
					  + '<a class="filter-opt stat-button month" sstid="' + $.trim(this.options.wrapname) + '" tag-type="month" title=' + lang.t('"近一月落的自定义数据项"') + '>' + lang.t('近一月新加统计项') + '</a>'
					  + '<a class="filter-opt stat-button all" sstid="' + $.trim(this.options.wrapname) + '" tag-type="all" title=' + lang.t('"该模块下全部自定义数据项"') + '>' + lang.t('全部') + '</a>';
			this.filterOptsBasic = this._createDoc("div", "filter-opts")
				.html(dftHtml).each(function () {
					for (var i = 0; i < ignore.length; i++) {
						$(this).find("." + ignore[i]).remove();
					}
				});

			// TODO: Add external tags
			// this._trigger(this.options.externalTags);

			this.filterOptsBasic.find("[tag-type=" + dft + "]").addClass("filter-selected");
			this.filterOptsBasic.appendTo(this.options.container[0])
				.find('a')
				.click(function () {
					context.filterOptsBasic.find('a').removeClass('filter-selected');
					context.tags = $(this).attr("tag-type");
					$(this).toggleClass('filter-selected');
			        if ($.isFunction(context.options.tagCallback)) {
						context._trigger(context.options.tagCallback);
					} else if ($.isFunction(window[context.options.tagCallback], $(this).attr('tag-type'))) {
						context._trigger(window[context.options.tagCallback], $(this).attr('tag-type'));
					}
					context.tagRefresh($(this).attr("tag-type"));
				});
		},
        getOption: function(){
            var option = {
				tags: this.tags,
                from: this.from,
                to: this.to,
                urlExtend: this.urlExtend,
                timeDimension: this.timeDimension
            };
            if(inArray("contrast", this.options.condition)){
                delete(option.from);
                delete(option.to);
                if(this.contrast.find(".cts-chk").is(":checked")) {
                    option['from[1]'] = this.from;
                    option['to[1]'] = this.to;
                }
            }
            return option;
        },
        // Do not need to deliver getOption, because delivered getOption when afterLoad
        refresh: function() {
            this._trigger(this.options.refresh);
        },
		// Click tag to refresh
		tagRefresh: function(tag) {
			this._trigger(this.options.refresh, { tags: tag });
		}
    });

    //datepicker
    ModuleManage("datepicker", {
        options: {
            // 容器
            container: $("body"),
            contrast: null,
            //type: 1: day~day 2: month~month 3: day 4: month
            type: 1
        },
        container: $(),
        from: undefined,
        to: undefined,
        create: function(){
            var that = this;
            switch(this.options.type){
            case 1:
                this._createDayToDay();
                break;
            case 2:
                this._createMonthToMonth();
                break;
            case 4:
                this._createMonth();
                break;
            default:
                break;
            }
            this.container.appendTo(this.options.container);
            this._trigger(this.options.afterLoad, this.getOption());
        },
        _createDayToDay: function(){
            this.from = $("#J_paramFrom").val() ? $("#J_paramFrom").val() : $.date.getNow();
            this.to = $("#J_paramTo").val() ? $("#J_paramTo").val() : $.date.getDate($.date.getNow(), -30);
            var that = this;
            this.container = this._createDoc("div", "datepicker-trigger radius5-all fr");
            this.container.html(' <i class="datepicker-icon"></i>'
                                + ' <input class="title cts-date" type="text" value="'
								+ this.from
								+ '~'
								+ this.to
								+ '" />'
                                + ' <i class="datepicker-arrow"></i>');
            var time = this.container.find(".cts-date");
            time.datepick({
                rangeSelect: true,
                monthsToShow: 3,
                monthsToStep: 3,
                monthsOffset: 2,
                shortCut : true,
                maxDate: new Date(),
                onShow: function(){
                    if(that.options.contrast){
                        that.options.contrast.attr("checked", true);
                    }
                },
                onClose: function(userDate) {
                    //判断是否是同一时间
                    if(userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != that.from
                                           || $.datepick.formatDate("yyyy-mm-dd", userDate[1]) != that.to ) ){
                                               var userDate = time.val().split("~");
                                               that.from = $.trim(userDate[0]);
                                               that.to = $.trim(userDate[1]);
                                               that.refresh();
                                           }
                }
            });
            this.container.click(function(e){ e.stopPropagation();
                                              time.focus();
                                            });
        },
        _createMonth: function(){
            this.from = $("#J_paramTo").val() ? $("#J_paramTo").val() : $.date.getCurMonth();
            this.to = this.from;
            var that = this;
            this.container = this._createDoc("div", "datepicker-trigger radius5-all fr single");
            this.container.html(' <i class="datepicker-icon"></i>'
                                + ' <input class="title cts-date" type="text" value="' + this.from + '" />'
                                + ' <i class="datepicker-arrow"></i>');
            var time = this.container.find(".cts-date");
            time.datepick({
                changeYear: true,
                changeMonth: true,
                hideCalendar: true,
                dateFormat: 'yyyy-mm',
                maxDate: new Date(),
                onChangeMonthYear: function(year, month){
                    if(year && month){
                        time.val(year + '-' + $.date.parseFullMonth(month));
                    }
                },
                onClose: function(userDate) {
                    if(userDate.length && $.datepick.formatDate("yyyy-mm", userDate[0]) != that.from) {
                        that.from = time.val();
                        that.to = that.from;
                        that.refresh();
                    }
                }
            });
            this.container.click(function(e){ e.stopPropagation();
                                              time.focus();
                                            });

        },
        _createMonthToMonth: function(){
            this.from = $("#J_paramFrom").val() ? $("#J_paramFrom").val() : $.date.getCurMonth();
            this.to = $("#J_paramTo").val() ? $("#J_paramTo").val() : $.date.getCurMonth();
            var that = this;
            this.container = this._createDoc("div", "datepicker-trigger radius5-all fr month-to-month");
            this.container.html(' <i class="datepicker-icon"></i>'
                                + ' <input class="title date-from" type="text" value="' + this.from + '" />'
                                + '<span class="datepicker-separator">~</span>'
                                + ' <input class="title date-to" type="text" value="' + this.to + '" />'
                                + ' <i class="datepicker-search"></i>');
            var from = this.container.find(".date-from"),
                to = this.container.find(".date-to");
            from.datepick({
                changeYear: true,
                changeMonth: true,
                hideCalendar: true,
                dateFormat: 'yyyy-mm',
                maxDate: new Date(),
                onChangeMonthYear: function(year, month){
                    if(year && month){
                        from.val(year + '-' + $.date.parseFullMonth(month));
                    }
                },
                onOK: function(userDate) {
                    if(userDate.length && $.datepick.formatDate("yyyy-mm", userDate[0]) != that.from) {
                        that.from = from.val();
                    }
                    to.focus();
                },
                onClose: function(userDate) {
                    if(userDate.length && $.datepick.formatDate("yyyy-mm", userDate[0]) != that.from) {
                        that.from = from.val();
                    }
                }
            });
            to.datepick({
                changeYear: true,
                changeMonth: true,
                hideCalendar: true,
                dateFormat: 'yyyy-mm',
                maxDate: new Date(),
                onChangeMonthYear: function(year, month){
                    if(year && month){
                        to.val(year + '-' + $.date.parseFullMonth(month));
                    }
                },
                onClose: function(userDate) {
                    if(userDate.length && $.datepick.formatDate("yyyy-mm", userDate[0]) != that.to) {
                        that.to = to.val();
                    }
                }
            });
            this.container.find(".datepicker-search").click(function(){
                that.refresh();
            });
        },
        getContainer: function() {
            return this.container;
        },
        getOption: function() {
            return {
                from: this.from,
                to: this.to
            };
        },
        refresh: function() {
            this._trigger(this.options.refresh, this.getOption());
        }
    });

    // wrap include table/graph and timeDimension、download
    ModuleManage("wrap", {
        options: {
            //wrap属性
            attr: {},
			theme: undefined,
            // 容器
            container: $("body"),
			// 功能区配置
            condition: [],
			conditionOptions: {},
            data_index: [],
            // 头部日期列表
            headTime: undefined,
            removeTitle: undefined,
            editTitle: undefined,
            downloadTitle: undefined,
            renameTitle: undefined,
            favorTitle: undefined,
            thermoTitle: undefined,
            calcTitle: undefined,
            commentTitle: undefined,
            renameMessageTitle: undefined,
            renameMessageContent: undefined,
            sharedTitle: undefined,
            // 是否提供头部功能
            headEnabled: true,
            // 是否提供底部功能
            bottomEnabled: true,
            // 是否有计算功能
            calc: null,
            // 计算按钮属性
            calcAttr: null,
            // 计算checkbox属性
            calcChkBoxAttr: null,
            // 显示宽度（百分比）
            width: 1,
            // 数据或显示更新时需要调用的函数
            refresh: null,
            // 下载
            download: null,
            // 下载按钮属性
            downAttr: null,
            // 删除
            remove: null,
            // 删除按钮属性
            remvAttr: null,
            // 编辑
            edit: null,
            // 编辑按钮属性
            editAttr: null,
            // 重命名
            rename: null,
            // 添加到我的收藏
            favor: null,
            // 热力图
            heatmap: null,
            // 显示注释
            comment: null,
            // 移动
            draggable: false
        },
        head: $(),
        body: $(),
        drag: $(),
        table: null,
        graph: null,
        ifData: null,
        ifHeatmap: null,
        download: null,
        remove: null,
        edit: null,
        favor: null,
        heatmap: null,
        rename: null,
        comment: null,
        calc: null,
        shared: null,
        content: $(),
        isTableShow: true,
        isGraphShow: true,
        dataDisplay: true,    // status flag for select-data
        heatmapDisplay: false,    // status flag for select-heatmap
        heatmapFn: null,
        tools: null,
        create: function() {
        	console.log("Create function in wrap");
            var that = this, condition = [];
            this._createHeader();    // create mod-header in this.head
            this._createBody();    // create mod-text in this.body
            this.container = this._createDoc("div", "mod-box").attr(this.options.attr)
				.addClass(this.options.theme)
                .append(this.head).append(this.body);    // now put this.head and this.body into mod-box
            if(this.options.draggable) this._createDrag();
            $(this.options.container).append(this._getBoxContainer().append(this.container));
            //wrap 内容区的条件控制部件
            if(!$.isEmptyObject(this.options.selControl)) condition.push("selControl");
            if(this.options.headEnabled) condition.push("timeDimension");

            //console.log("condition is " + condition.concat(that.options.condition));    // 一般来说是空，若wrap右侧有“日周月”输出"timeDimension"
            //console.log("conditionOptions is " + JSON.stringify(that.options.conditionOptions, null, 4));
            //console.log("that.options.headTime is " + JSON.stringify(that.options.headTime, null, 4));
            //console.log("that.options.selControl is " + JSON.stringify(that.options.selControl, null, 4));
            console.log("start Create function in tool");
            this.tools = tm.tools({
                condition: condition.concat(that.options.condition),
				conditionOptions: that.options.conditionOptions,
                headTime: that.options.headTime,
                selControl: that.options.selControl,
                container: that.content.parent().find(".mod-text-head"),
				wrapname: that.options.title,
                refresh: function(tags) {
                    that.refresh($.extend({
                        dataChange: true
                    }, tags));
                },
                afterLoad: function(option) {
                    console.log("ModuleManage tool in afterLoad");
                    that.getToolsOption = option.getOption;
                    that._initEvents();
                    that._trigger(that.options.afterLoad, {
                        container: that.getContent(),
                        superior: that
                    });
                }
            });
            console.log("end Create function in tool");
        },
        getOption: function(){
            var option = {};
            if (this.getToolsOption) {
                $.extend(option, this.getToolsOption());
            }
            return $.extend(option, {
                table: this.isTableShow,
                graph: this.isGraphShow,
                ifData: this.dataDisplay,
                ifHeatmap: this.heatmapDisplay,
                heatmapCallback: this.heatmapFn
            });
        },
        refresh: function(option) {
            //需要改变模块控制条件列表
            if (option && option.multiReload && this.tools && this.tools.multiControl) {
                this.getContent().empty();
                this.tools.multiControl.reload();
            } else {
                this._trigger(this.options.refresh, $.extend({}, this.getOption(), option));
            }
        },
        _getBoxContainer: function() {
            return this._createDoc("div", "mod-box-container").css({
                float: "left",
                width: this.options.width * 100 + "%"
            });
        },
        _initEvents: function() {
        	console.log('calc calc11111')
        	console.log(this.calc)
            var that = this;
            if (this.calc) {
                if (null !== that.options.calcAttr) that._trigger(that.options.calcAttr, that);
                if (null !== that.options.calcChkBoxAttr) that._trigger(that.options.calcChkBoxAttr, that);
                this.calc.on("click", function () {
                    that._trigger(that.options.calc, that);
                });
            }
            if(this.download) {
                if (null !== that.options.downAttr) that._trigger(that.options.downAttr, that);
                this.download.on("click", function() {
                    that._trigger(that.options.download, that);
                });
            }
            if(this.remove) {
                if (null !== that.options.remvAttr) that._trigger(that.options.remvAttr, that);
                this.remove.on("click", function() {
                    that._trigger(that.options.remove, that);
                });
            }
            if(this.edit) {
                if (null !== that.options.editAttr) that._trigger(that.options.editAttr, that);
                this.edit.on("click", function() {
                    that._trigger(that.options.edit, that);
                });
            }
            if(this.rename) {
                this.rename.on("click", function(){
                    that._renameEvent();
                });
            }
            if(this.favor) {
                this.favor.on("click", function() {
                	console.log("that.options.favor", that.options.favor);
                	console.log("that", that)
                    that._trigger(that.options.favor, that);
                });
                
            }
            if (this.heatmap) {
                var heatmapCallback = function (body) {
                    if (body != undefined) {
                        that._trigger(that.options.heatmap, body);
                    }
                };
                that.heatmapFn = heatmapCallback;
                that.dataDisplay = false;
                that.heatmapDisplay = true;

                this.heatmap.on("change", function () {
                    var heatmapOption = $(this).find(':selected').val();
                    switch (heatmapOption) {
                        case "数据":
                        that.dataDisplay = true;
                        that.heatmapDisplay = false;
                        that.refresh();    // mark
                        break;
                        case "热力图":
                        that.dataDisplay = false;
                        that.heatmapDisplay = true;
                        that.refresh();    // mark
                        break;
                    };
                });
            }
            if(this.comment) {
                this.comment.on("click", function() {
                    that._trigger(that.options.comment, that);
                });
            }
            if (this.shared) {
                this.shared.on("click", function() {
                    that._trigger(that.options.shared, that);
                });
            }
            if (this.options.bottomEnabled) {
                // this.isTableShow and this.isGraphShow is the status flag
                this.table.on("click", function() {
                    if (!$(this).hasClass("cur")) {
                        that.table.addClass("cur");
                        that.graph.removeClass("cur");
                        if (that.heatmap) {
                            that.heatmap.show();
                        }
                        that.isTableShow = true;
                        that.isGraphShow = false;
                        that.refresh();
                    }
                });
                this.graph.on("click", function() {
                    if (!$(this).hasClass("cur")) {
                        that.graph.addClass("cur");
                        that.table.removeClass("cur");
                        if (that.heatmap) {
                            that.heatmap.hide();
                        }
                        that.isTableShow = false;
                        that.isGraphShow = true;
                        that.refresh();
                    }
                }).click();
            }
            if (this.options.draggable){
                that.container.draggable({
                    handle : that.drag,
                    helper : "clone",
                    zIndex : 10,
                    start : function( event, ui ){
                        var helper = $(ui.helper);
                        $(event.target).css({
                            'border' : "1px dashed #CCC"
                        });
                        helper.find(".mod-header").css({ "padding-right" : "5px" }).find(".mod-btn").remove();
                        helper.find(".mod-text").remove();
                        helper.animate({
                            "border-width" : "2px",
                            "border-color" : "#61cc49",
                            width : '300px',
                            height: '40px'
                        }, 500);
                    },
                    stop : function(event, ui){
                        $(event.target).css({
                            'border' : '1px solid #B6BDC5'
                        });
                    }
                });
            }
        },
        _renameEvent: function() {
            var that = this;
            if(that.renameDialog) {
                that.renameDialog.show();
            } else {
                that.renameDialog = $.Dlg.Util.message(that.options.renameMessageTitle, "", that.options.renameMessageContent);
                if(that.options.rename.renameUrl && that.options.rename.nameListUrl) {
                    tm.listtable({
                        container: $(that.renameDialog.getTextContainer()),
                        thead: [{ title: "id" }, { title: "name" }],
                        url: {
                            extend: that.options.rename.nameListUrl
                        },
                        renameUrl: that.options.rename.renameUrl,
                        isAjax: true,
                        appendColumns: [{ type: "data", key: "id" }, { type: "rename", key: "name" }]
                    });
                    that.renameDialog.show();
                    //关闭并刷新功能(刷新模块中的数据)
                    that.renameDialog.setOkHandler(function() {
                        that.refresh({ dataChange: true });
                    });
                }
            }
        },
        _createSelect: function (className) {
            var that = this,
                opt = this.options;
            // this.container = this._createDoc().prependTo(opt.container);
            // $(opt.config).each(function (item) {
            //     var selcon = that._createDoc("span", "mod-sel").appendTo(that.container);
            // });
        },
        _createHeader: function() {
            var o = this.options,
                head = this._createDoc("div", "mod-header"),
                title = this._createDoc("span", "mod-title");
            if(o.remove){
                this.remove = this._createButton("mod-remove fr ml5")
                    .attr({ "sstid": o.removeTitle }).appendTo(head);
            }
            if(o.download){
                this.download = this._createButton("mod-download fr ml5 stat-button")
                    .attr({
						"title": this.options.downloadTitle,
						"sstid": $.trim(this.options.title)
					}).appendTo(head);
            }
            if(o.edit){
                this.edit = this._createButton("mod-edit fr ml5")
                    .attr({ "title": o.editTitle }).appendTo(head);
            }
            if(o.rename){
                this.rename = this._createButton("mod-rename-btn fr ml5")
                    .attr("title", o.renameTitle).appendTo(head);
            }
            if(o.favor) {
                this.favor = this._createButton("mod-favor-btn fr ml5 stat-button")
                    .attr({
						"title": o.favorTitle,
						"sstid": $.trim(this.options.title)
					}).appendTo(head);
            }
            if(o.comment) {
                this.comment = this._createButton("mod-comment-btn fr ml5")
                    .attr("title", o.commentTitle).appendTo(head);
            }
            if(o.shared) {
                this.shared = this._createButton("mod-shared-btn fr ml5")
                    .attr("title", o.sharedTitle).appendTo(head);
            }
            this.head = head.append(title.text(this.options.title));
            if (o.calc) {
                var calcContainer = '<div class="calc-utils"></div>';
                var min = '<label class="calc-opt"><input title="min" class="min" type="checkbox">&nbsp' + lang.t("最小值") + '&nbsp&nbsp&nbsp</label>',
                    max = '<label class="calc-opt"><input title="max" class="max" type="checkbox">&nbsp' + lang.t("最大值") + '&nbsp&nbsp&nbsp</label>',
                    sum = '<label class="calc-opt"><input title="sum" class="sum" type="checkbox">&nbsp' + lang.t("合计") + '&nbsp&nbsp&nbsp</label>',
                    avg = '<label class="calc-opt"><input title="avg" class="avg" type="checkbox">&nbsp' + lang.t("平均值") + '&nbsp&nbsp&nbsp</label>';
                this.calc = this._createButton("mod-calc-btn fr ml5")
                    .attr({ "title": o.calcTitle }).appendTo(head);

                $(min).appendTo(head).addClass('calc-box').addClass('chosen');
                $(max).appendTo(head).addClass('calc-box').addClass('chosen');
                $(sum).appendTo(head).addClass('calc-box').addClass('chosen');
                $(avg).appendTo(head).addClass('calc-box').addClass('chosen');
            }
            if (o.heatmap) {
                this.heatmap = true;
                this.heatmap = this._createThermoSwitch("origin-sel", ["热力图", "数据"])
                    .attr("title", o.thermoTitle).appendTo(head);
            }
        },
        _createDrag: function(){
            var that = this;
            this.drag = this._createDoc("span", "drop").css({"display": "none"}).appendTo(this.container);
            this.container.mouseover(function(){
                that.drag.show();
            }).mouseout(function(){
                that.drag.hide();
            });
        },
        _createBody: function() {
            var that = this,
                container = this._createDoc("div", "mod-text"),
                head = this._createDoc("div", "mod-text-head"),
                bottom = this._createDoc("div", "mod-text-bottom");
            this.content = this._createDoc("div", "mod-text-con");
            if (this.options.bottomEnabled) {
                this.table = this._createButton("mod-table").addClass("stat-button").attr({
					'title': "表切换",
					'sstid': this.options.title
				});
                this.graph = this._createButton("mod-graph mr5").addClass("stat-button").attr({
					'title': "图切换",
					'sstid': this.options.title
				});
                bottom.append(this.graph).append(this.table);
            }
			if (this.options.condition.length) {
				head.css({ minHeight: '31px' });
			}
            this.body = container.append(head).append(this.content).append(bottom);
        },
        getContent: function() {
            return this.content;
        },
        renameWrap: function(title){
            this.head.find(".mod-title").text(title);
        }
    });

    /**
     * @brief ModuleManage
     * 生成select控制器
     * @param "sel"
     * @param {
     */
    ModuleManage("sel", {
        options: {
            isMatch: false,
            urlKey: "",
            config: [{
                key: "sstid", //default request key eg: key=""
                urlKey: "sstid",
                container: $("body"),
                titlePre : "",
                titleSuf: "",
                isAjax: false,
                data: []
            }],
            match: []
        },
        container: $(),
        create: function(){
            this._createSelect();
        },
        _createSelect: function(){
            var that = this, o = this.options;
            this.container = this._createDoc("div", "mod-sel-con").prependTo(o.container);
            $(o.config).each(function(i){
                var selcon = that._createDoc("span", "mod-sel").appendTo(that.container);
                window.setTimeout((function(config, selcon, that, i){
                    return function(){
                        that.getData(config, function(data){
                            if(!data) return;
                            var html = '';
                            html += (config.titlePre ? '<span>' + config.titlePre + '</span>' : '')
                                + '<select class="origin-sel">';
                            $.each(data, function(){
                                html += '<option data-id="' + this[config.key] + '">' + this.name + '</option>';
                            });
                            html += '</select>' + (config.titleSuf ?
                                                   '<span>' + config.titleSuf + '</span>' : '');
                            selcon.html(html);
                            selcon.find('.origin-sel').change(function(e){
                                that.refresh();
                            });
                            if(i == that.options.config.length - 1) {
                                that._trigger(that.options.afterLoad, { urlExtend: that.getCurIdOption()});
                            }
                        });
                    };
                })(this, selcon, that, i), i * 10);
            });
        },
        getCurIdOption: function(){
            var that = this, option = {}, tmp = {}, ids = '',
                seled = that.container.find("select option:selected");
            $(seled).each(function(i){
                var id = $(this).attr("data-id");
                tmp[that.options.config[i]["urlKey"]] = id;
                ids += id + (i != seled.length - 1 ? "|" : "");
            });
            if(this.options.isMatch) {
                option["match_id"] = ids;
                option["is_match"] = true;
            } else {
                option = tmp;
            }
            return option;
        },
        getData: function(option, callback) {
        	console.log("tm.module.ok.option", option);
            var that = this;
            if (option.isAjax) {
                var param = option.url.page ? this._trigger(option.url.page) : "";
                ajax(option.url.extend, param, function(res) {
                    if (res.result == 0) {
                        if (!$.isEmptyObject(res.data)) {
                            that._trigger(callback, res.data);
                        } else {
                            that.options.container.text(that.options.ajaxEmptyHit);
                        }
                    } else {
                        that.options.container.text(that.options.ajaxErrorHit);
                    }
                }, "POST", false);
            } else {
                this._trigger(callback, option.data);
            }
        },
        refresh: function(){
            var that = this;
            this._trigger(this.options.refresh, {
                urlExtend: that.getCurIdOption()
            });
        }
    });
    /**
     * @brief ModuleManage
     * 生成multiple控制器
     * @param "sel"
     * @param {
     */
    ModuleManage("multictrl", {
        options: {
            urlKey: "",
            groupChooseTitle: undefined,
            config: [{
                key: "sstid", //default request key eg: key=""
                urlKey: "sstid",
                container: $("body"),
                titlePre : "",
                titleSuf: "",
                isAjax: false,
                data: []
            }]
        },
        container: $(),
        create: function(){
            this.container = this._createDoc("div", "mod-chk-con indicator-sel").prependTo(this.options.container);
            this._createTitle();
            this._createGroup();
            this._checkInitEvent();
        },
        _createTitle: function() {
            this.title = this._createDoc("div", "indicator-title")
                .html('<a href="javascript:void(0)" class="indicator-a">' + this.options.config[0].titlePre + '</a>'
                      + '<span class="c-blue context"></span>')
                .appendTo(this.container);
        },
        _createGroup: function() {
            var that = this, config = this.options.config[0];
            this.group = this._createDoc("div", "indicator-con")
                .css({ 'display' : 'none' })
                .html('<div class="indicator-top">'
                      + '<em class="indicator-notice">' + that.options.groupChooseTitle + '</em>'
                      + '</div><ul class="group loading"></ul>')
                .appendTo(this.container);
            this.getData(config, function(data) {
                var group = that.group.find(".group");
                group.removeClass("loading");
                that.title.find(".context").text('');
                if(!data || !data.length) {
                    group.html('<li class="sel">no data~.~</li>');
                    return;
                }
                var html = '';
                $.each(data, function(i) {
                    if(i == 0) that.title.find(".context").text(this[config.key + "name"]);
                    html += '<li class="sel" title="' + this[config.key + "name"] + '"><label>'
                        + '<input type="checkbox" class="mr2" data-id="' + this[config.key + "id"]
                        + '" data-name="' + this[config.key + "name"] + '"';
                    html += (i == 0 ? 'checked' : '');
                    html += '>' + this[config.key + "name"] +  '</label></li>';
                });
                group.html(html);
                that._trigger(that.options.afterLoad, that.getOption());
            });
        },
        _checkInitEvent: function(){
            var that = this;
            this.group.find(".sel input[type='checkbox']").click(function(e){
                e.stopPropagation();
                var t = $(this),
                    checked = that.group.find(".sel input[type='checkbox']:checked"),
                    len = checked.length;
                //至少选中一个，最多选中5个
                if(t.is(":checked")){
                    if(len > 5) checked.not(t).eq(0).attr("checked", false);
                } else {
                    if(len < 1) t.attr( "checked", true );
                }
                var chks = that.group.find(".sel input[type='checkbox']:checked");
                that.title.find(".context").text(that._getCheckedSetName(chks));
                that.refresh();
            });
            this.title.click(function(e) { e.stopPropagation();
                                           var t= $(this);
                                           if(t.hasClass("clicked")) {
                                               t.removeClass("clicked");
                                               that.group.hide();
                                           } else {
                                               t.addClass("clicked");
                                               that.group.show();
                                           }
                                         });
            that.group.click(function(e){ e.stopPropagation(); });
            $("body").click(function(e){
                e.stopPropagation();
                if(that.title.hasClass("clicked")){
                    that.title.removeClass("clicked");
                    that.group.hide();
                }
            });
        },
        _getCheckedSetName: function(checked) {
            var name = '';
            checked.each(function(i) {
                if(i > 2) return;
                var t = $(this);
                name += i == 0 ? t.attr("data-name") : (i == 1 ? '、' + t.attr("data-name") :  '...');
            });
            return name;
        },
        getData: function(option, callback) {
            var that = this;
            if (option.isAjax) {
                var param = option.url.page ? this._trigger(option.url.page) : "";
                ajax(option.url.extend, param, function(res) {
                    if (res.result == 0) {
                        if (!$.isEmptyObject(res.data)) {
                            that._trigger(callback, res.data);
                        } else {
                            that.options.container.text(that.options.ajaxEmptyHit);
                        }
                    } else {
                        that.options.container.text(that.options.ajaxErrorHit);
                    }
                }, "POST", false);
            } else {
                this._trigger(callback, option.data);
            }
        },
        _getChecked: function(){
            return this.group.find(".sel input[type='checkbox']:checked");
        },
        getOption: function(){
            var that = this, option = {};
            option[that.options.config[0].urlKey] = [];
            this._getChecked().each(function(i) {
                option[that.options.config[0].urlKey].push($(this).attr("data-id"));
            });
            return option;
        },
        reload: function(){
            this.container.empty();
            this._createTitle();
            this._createGroup();
            this._checkInitEvent();
        },
        refresh: function(loaded){
            var that = this;
            this._trigger(this.options.refresh,  that.getOption());
        }
    });

    // create tab document
    ModuleManage("tab", {
        options: {
            container: $("body"),
            tabsSkin: "", //orange
            child: [],
            getOption: $.loop,
            refresh: null
        },
        head: $(),
        body: $(),
        content: $(),
        container: $(),
        create: function() {
            this._createHeader();
            this.container = this._createDoc("div", "tabs-wrapper"
                                             + (this.options.tabsSkin == "orange" ? " radio-tabs-wrapper" : "")).append(this.head);
            $(this.options.container).append(this.container);
        },
        _createHeader: function() {
            var that = this,
                head = this._createDoc("ul", "tabs-list clearfix" + (this.options.tabsSkin == "orange" ? " radio-tabs" : "")),
                len = this.options.child.length,
                list = $();
            $(this.options.child).each(function(i) {
                list = list.add($(that._createDoc("li", "tabs-ajax tabs-control"
												  + (i == 0 ? " first" : ( i == len - 1 ? " last" : "")))
								  .attr($.extend({}, this.attr)))
								.append(that._createDoc("a").text(this.title))
								.data("data", this));
            });
            this.head = head.append(list);
        },
        getContainer: function() {
            return this.container;
        },
        refresh: function(option) {
            this._trigger(this.options.refresh, option);
        }
    });

    // create table document
    ModuleManage("table", {
        options: {
            calculate: [],
            container: $("body"),
            // 表格头部title
            thead: [],
            // 是否有同比数据
            yoy: false,
            yoyTitle: undefined,
            // 是否有环比数据
            qoq: false,
            qoqTitle: undefined,
            checkbox: false,
            // 是否有百分比数据
            percentage: false,
            percentageTitle: undefined,
            contrastRateTitle: undefined,
            allTitle: undefined,
            inverseTitle: undefined,
            // 是否初始隐藏
            hide: true,
            // 数据是否延后
            dataDelay: false,
            // 重组数据
            prepareData: null,
            // 生成表格后加载
            afterCreate: null,
            // 刷新时调用
            refresh: null,
            // 纯数据显示
            ifData: null,
            // 热力图显示
            ifHeatmap: null,
            // 热力图回调
            heatmapCallback: null
        },
        head: $(),
        body: $(),
        content: $(),
        container: null,
        table: true,
        create: function() {
            this._create();
            if (this.options.hide) {
                this.hide();
            }
        },
        _create: function() {
            this._init();
            if($.isEmptyObject(this.options.data)) {
                this.container.addClass("mod-no-data").text(this.options.ajaxEmptyHit);
                return;
            }
            this._createHeader();
            this._createBody();
            this.container.append(this._createDoc("table", "table")
                                  .append(this.head).append(this.body));
            this._trigger('setThermodynamic', this.body);
            this._adjustWidth(this.head, this.body, 0, 20);    // first column(0 is the index) by default
            this._trigger(this.options.afterCreate, this.getContainer());
        },
        _adjustWidth: function (thead, tbody, columnNum, space) {
            var adjustColumn = [],
                newWidth = 200;
            tbody.find('tr').each(function () {
                var eleText = $(this).find('td').eq(columnNum).text();
                $('#J_test').text(eleText);
                $('#J_test').css("font-size", $(this).find('td').css("font-size"));
                adjustColumn.push($('#J_test').width());
            });
            adjustColumn.sort(function (x, y) { return (x > y ? 1 : -1); });
            newWidth = adjustColumn[adjustColumn.length - 1] + space;
            thead.find('th').eq(columnNum).css({
                "width": String(newWidth > 500 ? 500 : newWidth) + 'px'
            });
        },
        _init: function(){
            if(!this.container) {
                this.container = this._createDoc("div", "table-wrapper");
            }
            this.container.empty();
            if(this.options.minHeight){
                $(this.options.container).css({
                    "height": this.options.minHeight + "px"
                }).append(this.container);
            } else {
                $(this.options.container).append(this.container);
            }
        },
        _createHeader: function() {
            var that = this, o = this.options,
                head = this._createDoc("thead"),
                theadData = $.isFunction(o.thead)
                    ? o.thead(this.options.data)
                    : ($.isFunction(window[o.thead]) ? window[o.thead](this.options.data, o) : o.thead),
                argument = [];
            list = $();
            if (o.checkbox) {
                list = list.add($(that._createDoc("th", "th w80")
                                  .attr({})
                                  .html('<a class="table-btn sel-total">'
                                        + o.allTitle
                                        + '</a>|<a class="table-btn sel-other">'
                                        + o.inverseTitle + '</a>')));
            }
            for (var i = 0; i < o.calculate.length; i++) {
                switch (o.calculate[i]) {
                case 'min':
                    theadData.splice(1, 0, {
                        title: lang.t("最小值"),
                        attr: { "data-key": "min" },
                        css: { width: "80px" }
                    });
                    break;
                case 'max':
                    theadData.splice(1, 0, {
                        title: lang.t("最大值"),
                        attr: { "data-key": "max" },
                        css: { width: "80px" }
                    });
                    break;
                case 'sum':
                    theadData.splice(1, 0, {
                        title: lang.t("合计"),
                        attr: { "data-key": "sum" },
                        css: { width: "80px" }
                    });
                    break;
                case 'avg':
                    theadData.splice(1, 0, {
                        title: lang.t("平均值"),
                        attr: { "data-key": "avg" },
                        css: { width: "80px"}
                    });
                    break;
                }
            }
            if(_isContrast()) {
                if(!$.isFunction(o.thead) && !$.isFunction(window[o.thead])) {
                    theadData = theadData.concat(theadData);
                    argument.push(this.options.contrastRateTitle);
                }
            } else {
                if(o.qoq) argument.push(this.options.qoqTitle);
                if(o.yoy) argument.push(this.options.yoyTitle);
                if(o.percentage) argument.push(this.options.percentageTitle);
            }
            $(theadData).each(function(i) {
                var attr = {
                    "data-type": this.type
                };
                $.extend(attr, this.attr ? this.attr : {});
                if (this.disortable) {
                    attr["data-disabled"] = 1;
                }
                list = list.add($(that._createDoc("th", "th " + (this.className ? this.className : "") ))
                                .attr(attr).css(this.css || {}).text(this.title));
            });
            for(var i = 0; i < argument.length; i++) {
                list = list.add($(this._createDoc("th", "th")).attr({
                    "data-type": "percentage"
                }).text(argument[i]));
            }
            this.head = head.append(this._createDoc("tr", "tr").append(list));
        },
        _createBody: function() {
            var that = this,
                colspan = that.options.thead.length - 1,
                list = $(),
                body = this._createDoc("tbody");

            $(this._prepareData()).each(function() {    // All the rows
                var row = $();
                if (that.options.dataDelay) {
                    $(this).each(function() {
                        if(that.options.checkbox){
                            row = row.add(that._createDoc("td", "td hd")
                                          .html('<input type="checkbox" class="tbl-ckb" value="'
                                                + this.dataId + '"/>'));
                        }
                        row = row.add(that._createDoc("td", "td hd").text(this.title).attr(this));
                    });
                    if (that.options.calculate[0]) {
                        for(var j = 0; j < that.options.calculate.length; j++) {
                            row = row.add(that._createDoc("td", "td hd")
                                          .append(that._createDoc("span").text("--"))
                                          .css({"text-align": "right"}));
                        }
                    }
                    row = row.add(that._createDoc("td", "td hd").attr({
                        colspan: colspan
                    }).append(that._createDoc("span", "row-loading").text("loading...")));
                } else {
                    if (this && $.isArray(this)) {
                        for(var k = 0; k < this.length; k++ ){
                            var isweek = k == 0 && ( $.date.getWeekNum(this[k]) == 0 ||
                                                     $.date.getWeekNum(this[k]) == 6 )
                                    ? true : false;
                            row = row.add(that._createDoc("td", "td hd" + (isweek ? ' gr' : ''))
                                          .text(this[k] === null ? "-" : this[k]));
                        }
                    }
                }
                list = list.add(that._createDoc("tr").append(row));
            });
            this.body = body.append(list);
        },
        _prepared: false,
        _prepareData: function() {
            if (!this._prepared) {
                this.options.data = this._trigger(this.options.prepareData, {
                    data: this.options.data,
                    hugeTable: this.options.type == "hugeTable" ? true : false,
                    qoq: this.options.qoq,
                    yoy: this.options.yoy,
                    percentage: this.options.percentage,
                    contrast: _isContrast(),
                    average: this.options.average,
                    sum: this.options.sum,
                    theadAvg: this.options.theadAvg
                });
            }
            this._prepared = true;
            return this.options.data;
        },
        getContainer: function() {
            return this.container;
        },
        show: function() {
            this.getContainer().show();
            this.table = true;
            if (!this._prepared) {
                this._create();
            }
        },
        hide: function() {
            this.getContainer().hide();
            this.table = false;
        },
        refresh: function(option) {
            this.contrast = option.from && option.to ? true : false;
            option = $.extend({
                table: this.table,
                ifData: this.ifData,
                ifHeatmap: this.ifHeatmap,
                heatmapCallback: this.heatmapCallback
            }, option);
            if (option.theadChange) {
                this.options.thead = option.thead;
                this.container.remove();
            }
            if (option.dataChange || option.thead) {
                this.options.data = option.data;
                this.container.remove();
                this._prepared = false;
            }
            if (option.table) {
                this.show();
            } else {
                this.hide();
            }

            if (this.options.ifData === null && this.options.ifHeatmap === null) {
                this.options.ifData = option.ifData;
                this.options.ifHeatmap = option.ifHeatmap;
                this.options.heatmapCallback = option.heatmapCallback;
            } else if (this.options.ifData != option.ifData && this.options.ifHeatmap != option.ifHeatmap) {
                this.options.ifData = option.ifData;
                this.options.ifHeatmap = option.ifHeatmap;
                this.options.heatmapCallback = option.heatmapCallback;
                this.container.remove();
                this._create();
            }
        }
    });

    // get ajax data
    ModuleManage("data", {
        container: null,
        options: {
            url: {
                timeDimension: null,
                extend: null,
                page: null
            },
			// rlen: null,
            afterLoad: null,
            refresh: null,
            data: null,
            isAjax: true,
            isSelControl: false,//true, //TODO
            urlExtend: {},
            from: null,
            to: null,
            isTimeDimensionInherit: true,
            ajaxErrorHit: undefined,
            ajaxEmptyHit: undefined,
			getOption: $.loop
        },
        timeDimension: null,
		tags: null,
        urlExtend: {},
        from: null,
        to: null,
        dataChange: true, //
        data: [],
        getPageParameters: function() {
            if (this.options.url.page) {
                return this._trigger(this.options.url.page);
            } else {
                return "";
            }
        },
        create: function() {
            this.container = this.options.container;
            this.data = this.options.data || [];
            this.init();
        },
        url: [],
        child: [],
        init: function() {
            var that = this,
                option = this.getOption(),
				// tagrange = {
				// 	'all': 101,
				// 	'month': 201,
				// 	'week': 99999999
				// },
                callback = function(data) {
                    that._trigger(that.options.afterLoad, data);
                };
            this.timeDimension = this.options.isTimeDimensionInherit
                ? (option.timeDimension ? option.timeDimension : 1)
            : (this.options.url.timeDimension ? this.options.url.timeDimension : 1);
			// for (var prop in tagrange) {
			// 	if (tagrange.hasOwnProperty(prop)) {
			// 		if (this.options.rlen < tagrange[prop]) {
			// 			this.tags = prop;
			// 			break;
			// 		}
			// 	}
			// }
            // this.timeDimension = option.timeDimension
            //     ? option.timeDimension
            //     : (this.options.url.timeDimension ? this.options.url.timeDimension : 1);
            this.dataChange = true;
            this.getData(callback);
        },
        loadingAdded: false,
        load: null,
        loading: function() {
            if (this.loadingAdded) {
                return;
            }
            this.load = $(document.createElement("div")).addClass("flash-loading");
            this.options.container.find(">div").hide();
            this.options.container.append(this.load);
            this.loadingAdded = true;
        },
        loaded: function() {
            if (!this.loadingAdded) {
                return;
            }
            this.loadingAdded = false;
            this.load.remove();
            this.options.container.find(">div").show();
        },
        getData: function(callback) {
            if (this.options.isAjax) {
                console.log("ModuleManage data getData isAjax");
                this.url = this.options.isSelControl
                    ? this.options.url.extend
                    : ( $.isArray(this.options.url.extend)
                        ? this.options.url.extend
                        : ["", this.options.url.extend ]);
                this._getAjaxData(callback);
            } else {
                console.log("ModuleManage data getData not isAjax");
                this._trigger(callback, this.data[this.timeDimension]);
            }
        },
        getOption: function() {
            var that = this,
                opt = this.options.getOption(),
                option = {
					// tags: that.tags,
                    period: that.timeDimension
                };
            $.extend(option, opt, opt.urlExtend);
            delete(option.urlExtend);
            return option;
        },
        _getAjaxData: function(callback) {
            var that = this, url = this.url, option = this.getOption(), ajaxOpt;
            this.loading();
            if (that.options.isSelControl && !that.dataChange && this.data[option.match_id]) {
                that.loaded();
                that._trigger(callback, this.data[option.match_id]);
                return;
            }
            if (!that.dataChange && this.data[this.timeDimension]) {
                that.loaded();
                that._trigger(callback, this.data[this.timeDimension]);
                return;
            }
            url = this.options.isSelControl
                ? (url[option.match_id] ? url[option.match_id] : "")
            : (url[this.timeDimension] ? url[this.timeDimension] : "");
            if(!url) {
                that.loaded();
                that.options.container.addClass("mod-no-data").text(that.options.ajaxErrorHit);
                return;
            }
            console.log("ModuleManage data url is " + url);
            url = url.split("?");

            ajaxOpt = $.extend({}, option);
            delete ajaxOpt.timeDimension;
            delete ajaxOpt.table;
            delete ajaxOpt.graph;
            delete ajaxOpt.ifData;
            delete ajaxOpt.ifHeatmap;
            delete ajaxOpt.heatmapCallback;

            console.log("ModuleManage data url is " + url[0]);
            console.log("ModuleManage data params is " + JSON.stringify($.extend({}, parseArgs(url[1]), ajaxOpt, this.getPageParameters(), null, 4)));
            ajax(url[0],
				 $.extend({}, parseArgs(url[1]), ajaxOpt, this.getPageParameters()),
				 function(res) {
					 that.loaded();
					 if (res.result == 0) {
						 if(that.options.isSelControl){
							 that.data[option.match_id] = res.data;
							 that._trigger(callback, that.data[option.match_id]);
						 } else {
							 that.data[that.timeDimension] = res.data;
							 that._trigger(callback, that.data[that.timeDimension]);
						 }
					 } else {
						 that.options.container.addClass("mod-no-data").text(that.options.ajaxErrorHit);
					 }
				 }, "POST");
        },
        timeDimensionChange: false,
        _reloadData: function() {
            this.data = [];
            this.dataChange = true;
        },
        refresh: function(option) {
            var that = this,
                option = option || {},
                callback = null;
            if (this.options.isTimeDimensionInherit
                && option.timeDimension
                && this.timeDimension != option.timeDimension) {
                this.timeDimension = option.timeDimension;
                this.timeDimensionChange = true;
            } else {
                if (option.dataChange) {
                    this._reloadData();
                }
            }
			option.tags === undefined || (this.tags = option.tags);
            callback = function(data) {
                if (that.dataChange || that.timeDimensionChange) {
                    option.data = data;
                }
                option.dataChange = that.dataChange || that.timeDimensionChange;
                that.dataChange = false;
                that.timeDimensionChange = false;
                that._trigger(that.options.refresh, option);    // 触发自定义的 refresh
            };
			this.getData(callback);
        }
    });

    // graph
    ModuleManage("graph", {
        options: {
            container: null,
            columnStack: '',
            chartType: "line",
            chartStock: false,
            page: false,
            // 是否初始隐藏
            hide: false,
            timeDimension: "day",
            data: {}
        },
        head: $(),
        body: $(),
        content: $(),
        container: $(),
        graph: true,
        chart: [],
        data: null,
        width: 0,
        create: function() {
            this.data = this.options.data;
            this.container = this._createDoc("div", "graph-wrapper");
            $(this.options.container).append(this.container.append(this._createDoc("div", "graph-inner flash-loading")));
            if (!this.options.hide) {
                this._draw();
            } else {
                this.hide();
            }
        },
        _draw: function() {
            var that = this, o = this.options,
                container = this.container.find(".graph-inner").removeClass("flash-loading");
            this._trigger(this.options.beforeDraw, {
                container: this.container,
                data: this.data
            });
            if($.isEmptyObject(this.data)){
                console.log("ModuleManage graph _draw no data");
                container.addClass("mod-no-data").text(that.options.ajaxEmptyHit);
                container.get(0).removeAttribute("data-highcharts-chart");
            } else {
                var width = o.container.width();
                this.width = width ? width : this.width;
                o.chartType = o.chartConfig && o.chartConfig[0] && o.chartConfig[0].type ? o.chartConfig[0].type : o.chartType;
                new $.draw.DrawFactory({
                    page : o.page ? true : false,
                    container: container,
                    chartType: o.chartType,
                    chartData : tmtool.getDrawData(this.data, o),
                    series: tmtool.getSeries(this.data, o.keyUnit, o.average),
                    yUnit: tmtool.getYUnit(o),
                    xAxisCategories : tmtool.getXAxisCategories(this.data, o),
                    isSetYAxisMin: !(o.isSetYAxisMin  == false ? false : true),
                    chartStock: o.chartStock,
                    timeDimension : o.timeDimension,
                    columnStack: o.columnStack ? o.columnStack : "",
                    lineAreaColumn: o.lineAreaColumn ? o.lineAreaColumn : false,
                    doubleYAxis: o.lineColumn ? o.lineColumn : false,
                    width : that.width
                });
                //new $.draw.DrawFactory({
                //lineColumn: o.lineColumn ? o.lineColumn : false,
                //isSetYAxisMin: o.isSetYAxisMin ? true : false,
                //});
                //this.chart.push(chart);
            }
        },
        show: function() {
            this.getContainer().show();
            this.graph = true;
        },
        hide: function() {
            this.getContainer().hide();
            this.graph = false;
        },
        getContainer: function() {
            return this.container;
        },
        refresh: function(option) {
            option = $.extend({
                graph: this.graph
            }, option);
            if (option.dataChange) {
                this.data = option.data;
            }
            if (option.graph) {
                this.show();
                this._draw();
            } else {
                this.hide();
            }
        }
    });

    /*
     * create huge and progressive table
     * 1、ajax data (show data row by row, including the first column)
     * 2、fixed row and column
     */
    ModuleManage("hugeProgressiveTable", {
        options: {
            container: $("body"),
            allTitle: undefined,
            inverseTitle: undefined,
            ajaxErrorHit: undefined,
            ajaxEmptyHit: undefined, // 表格头部title
            thead: [],
			appendEvent: [],
            checkbox: false,
			cartEntrance: false,
			authChk: false,
			cartCallback: '',
            // 是否初始隐藏
            hide: true,
            // 数据是否延后
            dataDelay: false,
            // 重组数据
            prepareData: null,
            // 生成表格后加载
            afterCreate: null,
            // 刷新时调用
            refresh: null,
            // 请求数据的链接
            url: {
                url: "",
                page: null
            },
            cntRequest: 100
        },
        tbl: $(),
        head: $(),
        body: $(),
        content: $(),
        container: $(),
        table: true,
        create: function() {
            this._create();
            if (this.options.hide) {
                this.hide();
            }
        },
        _create: function() {
			this.options.appendEvent = [];
            this.container = this._createDoc("div", "table-wrapper");
            this.tbl = this._createDoc("table", "table");
            $(this.options.container).append(this.container.append(this.tbl));
            this._createHeader();
            this._createBody();
        },
        _createHeader: function() {
            var that = this, o = this.options,
                head = this._createDoc("thead"),
                list = $();
			if (o.cartEntrance) {
				console.log("==================test")
                list = list.add($(that._createDoc("th", "th w80").html('<span>' + '操作' + '</span>')));
			}
            if(o.checkbox){
                list = list.add($(that._createDoc("th", "th w80").html('<a class="table-btn sel-total">' + o.allTitle + '</a>|<a class="table-btn sel-other">' + o.inverseTitle + '</a>')));
            }
            $(o.thead).each(function(i) {
                list = list.add($(that._createDoc("th", "th " + (this.className ? this.className : "") )).attr({
                    "data-type": this.type
                }).css(this.css || {}).text(this.title));
            });
            this.head = head.append(this._createDoc("tr", "tr").append(list));
            this.tbl.append(this.head);
        },
        _createBody: function() {
            var that = this,
                body = this._createDoc("tbody"),
                data = this._prepareData(),
                i = 0,
                cnt = 1 + Math.ceil((data.length-10)/that.options.cntRequest);

            //每total条数据append
            while(i < cnt){
                var cntRequest = i == 0 ? 10 : that.options.cntRequest,
                    start = i == 0 ? 0 : ((i-1) * cntRequest + 10),
                    tmpData = data.slice(start, start + cntRequest);
                var appendId = window.setTimeout((function(that, tmpData, body) {
                    return function(){
                        var list = $(),
                            ids = [],
                            colspan = that.options.thead.length - 1;

                        $(tmpData).each(function(){
                            var row = $();
                            if (that.options.dataDelay) {
                                $(this).each(function() {
                                    if(that.options.cartEntrance){
										var favorText = this.iffavor == 1 ? '<a class="table-btn myfavor stat-button" title="添加收藏" value="' + this.dataId + '">' + '收藏' + '</a>' : '',
											selfhelpText = this.ifselfhelp & that.options.authChk ? '<a class="table-btn selfhelp stat-button" title="自助查询" value="' + this.dataId + '">'  + '查询' + '</a>' : '',
											separatorText = (this.iffavor == 1 && this.ifselfhelp) & that.options.authChk ? '|' : '',
											htmlText = selfhelpText + separatorText + favorText == '' ? '-' : selfhelpText + separatorText + favorText;
										row = row.add(that._createDoc("td", "td hd").html(htmlText));

                                    }
                                    if(that.options.checkbox){
                                        row = row.add(that._createDoc("td", "td hd").html('<input type="checkbox" class="tbl-ckb" value="' + this.dataId + '"/>'));
                                    }
                                    row = row.add(that._createDoc("td", "td hd").text(this.title).attr(this));
                                    ids.push(this.dataId);
                                });
                                row = row.add(that._createDoc("td", "td hd").attr({
                                    colspan: colspan
                                }).append(that._createDoc("span", "row-loading").text("loading...")));
                            } else {
                                $(this).each(function() {
                                    row = row.add(that._createDoc("td", "td hd").text(this.toString()));
                                    ids.push(this.dataId);
                                });
                            }
                            list = list.add(that._createDoc("tr").append(row));
                        });
                        that.body = body.append(list);
                        that.tbl.append(that.body);
                        ajax(that.options.url.url, that.getCommonParameters(ids), (function(rows) {
                            return function(res) {
                                if (res.result == 0) {
                                    that._createProgressiveRow(rows, res.data && res.data[0] ? res.data[0] : []);
			                        if ($.isFunction(that.options.cartCallback)) {
				                        that._trigger(that.options.cartCallback);
			                        } else if ($.isFunction(window[that.options.cartCallback], that.container)) {
				                        that._trigger(window[that.options.cartCallback], that.container);
			                        }
                                }
                            };
                        })(list), "POST");
                        that._fixedTable(that.getContainer(), that.tbl);
                        $(".fixed-body table").table("rejustWrapWidth");
                    };
                })(that, tmpData, body), i * 500 );
				this.options.appendEvent.push(appendId);
                i++;
            }
        },
        _prepared: false,
        _prepareData: function() {
            if (!this._prepared && $.isFunction(this.options.prepareData)) {
                this.options.data = this._trigger(this.options.prepareData, this.options.data);
            }
            this._prepared = true;
            return this.options.data;
        },
        _fixedTable : function(container, table){
            if (table.data("statTable")){
                table.table("rejustWrapHeight");
            } else {
                table.table({
                    colNum: this.options.checkbox || this.options.cartEntrance ? 2 : 1,
                    wrapWidth: container.width(),
                    wrapHeight: this.options.height ? this.options.height : container.parent().height()
                });
            }
            _tableChoose(container);
        },
        getCommonParameters: function(ids) {
            if (this.options.url.page) {
                return this._trigger(this.options.url.page, ids);
            } else {
                return {
                    ids: ids.join(";")
                };
            }
        },
        _createProgressiveRow: function(rows, data) {
            var that = this, row;
            if( data && data.data && data.data.length ){
                $(data.data).each(function(index) {
                    row = $(rows[index]);
                    window.setTimeout((function(row, data) {
                        return function() {
                            row.find("td:last").remove();
                            $(data.data.reverse()).each(function(i, item) {
                                row.append(that._createDoc("td", "td").text(item !== null ? item.toString() : "--"));
                            });
                        };
                    })(row, this), index * 60);
                });
            } else {
                $.each(rows, function(){
                    $(this).find(".td:last").empty().css({'text-align': 'left'}).text(that.options.ajaxEmptyHit);
                });
            }
            rows = null;
        },
        getContainer: function() {
            return this.container;
        },
        show: function() {
            this.getContainer().show();
            this.table = true;
        },
        hide: function() {
            this.getContainer().hide();
            this.table = false;
        },
        refresh: function(option) {
			for (var i = 0; i < this.options.appendEvent.length; i++) {
				clearTimeout(this.options.appendEvent[i]);
			}
            option = $.extend({
                table: this.table
            }, option);
            if (option.theadChange) {
                this.options.thead = option.thead;
                this.container.remove();
            }
            if (option.dataChange) {
                this.options.data = option.data;
                this.container.remove();
                this._prepared = false;
            }
            if (option.table) {
                this.show();
                if (option.dataChange || option.thead) {
                    this._create();
                }
            } else {
                this.hide();
            }
        }
    });
    // table按条请求数据
    ModuleManage("progressiveTable", {
        options: {
            checkbox: false,
            calculate: [],
            ajaxEmptyHit: undefined,
            container: $("body"),
            url: {
                url: "",
                page: null
            },
            dataCountEachRequest: 200,
            afterLoad: null
        },
        rows: $(),
        create: function() {
            var that = this;
            this.getData();
            that._trigger(that.options.afterLoad);
        },
        getCommonParameters: function(ids) {
            if (this.options.url.page) {
                return this._trigger(this.options.url.page, ids);
            } else {
                return {
                    ids: ids.join(";")
                };
            }
        },
        getData: function() {
            var that = this,
                ids, total, eachCount = this.options.dataCountEachRequest,
                i, j = 0, row, rows,
                length;
            this.rows = this.options.container.find("table tbody tr");
            length = this.rows.length;
            eachCount = eachCount > length ? length : eachCount;
            while (length > 0) {
                rows = [];
                ids = [];
                for (i=0; i<eachCount; i++) {
                    row = $(this.rows[j]);
                    ids.push(that.options.checkbox ? row.find("td:eq(1)").attr("dataId")
                             : row.find("td:first").attr("dataId"));
                    rows.push(row);
                    length --;
                    j++;
                }
                window.setTimeout((function(ids, rows) {
                    return function() {
                        ajax(that.options.url.url, that.getCommonParameters(ids), (function(rows) {
                            return function(res) {
                                if (res.result == 0) {
                                    that._create(rows, res.data);
                                }
                            };
                        })(rows), "POST");
                    };
                })(ids, rows), j * 10);
                if (length < eachCount) {
                    eachCount = length;
                }
            }
        },
        _create: function(rows, data) {
            var formatFloat = function(src, pos) {
                return Math.round(src * Math.pow(10, pos)) / Math.pow(10, pos);
            };
            var _getMin = function (seq) {
                var min = seq[0];
                for(var i = 0; i < seq.length; i++)
                    if (seq[i] < min)
                        min = seq[i];
                return min;
            };
            var _getMax = function (seq) {
                var max = seq[0];
                for(var i = 0; i < seq.length; i++)
                    if (seq[i] > max)
                        max = seq[i];
                return max;
            };
            var _getSum = function (seq) {
                var total = 0;
                for(var i = 0; i < seq.length; i++)
                    total += seq[i];
                return formatFloat(total, 2);
            };
            var _getAvg = function (seq) {
                return formatFloat((_getSum(seq) / seq.length), 2);
            };
            var that = this, row, o = this.options;
            if (data && data[0] && data[0].data && data[0].data.length) {
                $(data[0].data).each(function(index) {
                    row = rows[index];
                    window.setTimeout((function(row, data) {
                        return function() {
                            row.find("td:last").remove();
                            var seq = [],
                                percent = false;
                            var template = [];
                            $(data.data.reverse()).each(function(i, item) {
                                row.append(that._createDoc("td", "td").text(item !== null ? item : "--"));
                                if (item !== null) seq.push(parseFloat(item));
                                if ((typeof item == "string") && (item[item.length - 1] == '%')) {
									percent = true;
								}
                            });
                            row.parent().parent().find('thead tr').each(function () {
                                $(this).find('th').each(function () {
                                    var ele = $(this).attr('data-key');
                                    if (ele) template.push(ele);
                                });
                            });
                            o.calculate.reverse();
                            if (seq.length) {
                                for (var i = 0; i < o.calculate.length; i++) {
                                    switch(o.calculate[i]) {
                                    case 'avg':
                                        var avg = _getAvg(seq);
                                        row.find("td").eq(template.indexOf("avg") + 1)
                                            .text(percent == false ? avg : avg + '%');
                                        break;
                                    case 'sum':
                                        var sum = _getSum(seq);
                                        row.find("td").eq(template.indexOf("sum") + 1)
                                            .text(percent == false ? sum : sum + '%');
                                        break;
                                    case 'max':
                                        var max = _getMax(seq);
                                        row.find("td").eq(template.indexOf("max") + 1)
                                            .text(percent == false ? max : max + '%');
                                        break;
                                    case 'min':
                                        var min = _getMin(seq);
                                        row.find("td").eq(template.indexOf("min") + 1)
                                            .text(percent == false ? min : min + '%');
                                        break;
                                    }
                                }
                            }
                            template = [];
                            percent = false;
                        };
                    })(row, this), index * 60);
                });
            } else {
                $.each(rows, function(){
                    $(this).find(".td:last")
                        .empty()
                        .css({'text-align': 'left'})
                        .text(that.options.ajaxEmptyHit);
                });
            }
            rows = null;
        },
        refresh: function() {
            this.getData();
        }
    });
    /*
     * Fn listtable
     * table类型list
     * @param isAjax {boolean}
     * @param data {array}
     * @param thead {array}
     * @param renameUrl {string} : request url when rename
     * @param tools {object}
     */
    ModuleManage("listtable", {
        options: {
            cntRequest: 150,
            container: $("body"),
            isAjax: false,
            data: [],
            url: {
                extend: "",
                paginationUrl: "",
                page: "getPageParam"
            },
            thead: [],
            appendColumns: [],
            renameUrl: "",
            afterLoad: null,
            pagination: false,
            pagesize: 100
        },
        data: [],
        container: null,
        thead: null,
        tbody: null,
        rows: null,
        create: function() {
            this.container = this._createDoc("div", "mb20 flash-loading").appendTo(this.options.container);
            //this._create();
            console.log("friendly++++++++pagination"+this.options.pagination);
            if (this.options.pagination) {
                this._createPagination();
            } else {
                this._create();
            }
        },
        _create: function(data){
            var that = this;
            this.getData(function(data){
                that.container.removeClass("flash-loading");
                that.table = that._createDoc("table", "table module-table").appendTo(that.container);
                that._createThead();
                that.data = data;
                that._createTbody();
                that._trigger(that.options.afterLoad);
            });
        },
    	_createPagination: function () {
                var that = this;
                function createPagination(total, currentPage) {
                    var totalPage = Math.ceil(total / that.options.pagesize),
                        $pageContainer = that._createDoc("ul", "pagination"),
                        minRange = 4, maxRange = totalPage - 3,
                        firstPoint = 0, secondPoint = 0,
                        currentPage = currentPage || 1, loading = false;
                    for (var i = 1; i <= totalPage; i ++) {
                        if (i < minRange || i > maxRange || (i >= currentPage - 1 && i <= currentPage + 1)) {
                            $pageContainer.append(that._createDoc("li", "pagination-number " + (currentPage == i ? ' active' : '')).attr({
                                number: i
                            }).append(
                                that._createDoc("a").text(i)
                            ));
                        } else if (i < currentPage - 1 && firstPoint ++ < 3 || i > currentPage + 1 && secondPoint ++ < 3) {
                            $pageContainer.append(that._createDoc("li").append(
                                that._createDoc("a").text('·')
                            ));
                        }
                    }
                    $pageContainer.on("click", ".pagination-number", function () {
                        if (loading) return;
                        loading = true;
                        var page = $(this).attr('number') - 0;
                        that.params = {
                            start: (page - 1) * that.options.pagesize,
                            end: page * that.options.pagesize
                        };
                        that.container.empty().addClass("flash-loading");
                        that.afterLoad = that.options.afterLoad;
                        that.options.afterLoad = function () {
                            that.options.afterLoad = that.afterLoad;
                            loading = false;
                            that._trigger(that.options.afterLoad);
                        };
                        that.outerContainer.find(".pagination").remove();
                        that.outerContainer.find('.pagination-container').append(createPagination(total, page));
                        that._create();
                    });
                    return $pageContainer;
                }
                this._getPaginationData(function (count) {
                    if (count > that.options.pagesize) {
                        that.container.removeClass("flash-loading");
                        var $container = that._createDoc("div", "pagination-container").appendTo(that.container),
    		        $dataContainer = that._createDoc("div", "data-container flash-loading"),
                            $pagination = createPagination(count);
                        $container.append($dataContainer).append($pagination);
                        that.outerContainer = that.container;
                        that.container = $dataContainer;
                        $pagination.find(".pagination-number:first").click();
                    } else {
                        that._create();
                    }
                });
            },
            params: null,
            _getPaginationData: function (callback) {
                if(this.options.isAjax) {
                    var that = this;
                    ajax(this.options.url.paginationUrl, this.getPageParameters(), function(res) {
                    	console.log("friendly2------------------------");
                        if (res.result == 0) {
                            that._trigger(callback, res.data);
                        } else {
                            that.options.container.empty().addClass("mod-no-data").text(that.options.ajaxErrorHit);
                        }
                    }, "POST");
                } else {
                    return this.options.data.length;
                }
            },
        _createThead: function() {
            var that = this;
            if(this.options.thead.length) {
                var tr = this._createDoc("tr");
                $.each(this.options.thead, function(){
                    tr.append(that._createDoc("th", "th"
                                              + (inArray(this.type, ["checkbox", "child"]) ? " w40" : ""))
                              .text(this.title));
                });
                this.thead = this._createDoc("thead").append(tr).appendTo(this.table);
            }
        },
        getData: function(callback){
            if(this.options.isAjax) {
                this._getAjaxData(callback);
            } else {
                return this.options.data;
            }
        },
        _getAjaxData: function(callback) {
            var that = this;
            ajax(this.options.url.extend, this.getPageParameters(), function(res) {
            	console.log("friendly1-----------------------------");
                if (res.result == 0) {
                    that._trigger(callback, res.data);
                } else {
                    that.options.container.empty().addClass("mod-no-data").text(that.options.ajaxErrorHit);
                }
            }, "POST");
        },
        getPageParameters: function() {
            if (this.options.url.page) {
                return this._trigger(this.options.url.page);
            } else {
                return "";
            }
        },
        _createTbody: function() {
            this.rows = $();
            this.data = this.data ? this.data : [];
            this.contents = this._createDoc("tbody").appendTo(this.table);
            var that = this, o = this.options, i = 0,
                cnt = this.data.length ? Math.ceil(this.data.length/o.cntRequest) : 0;

            //每total条数据append
            while(i < cnt) {
                var start = i * o.cntRequest,
                    tmpData = that.data.slice(start, start + o.cntRequest);
                window.setTimeout((function(that, tmpData, container) {
                    return function() {
                        var rows = $(), data, row;
                        for(var j = 0; j < tmpData.length; j++) {
                            data = tmpData[j];
                            row = that._createDoc("tr").attr({
                                "data-id": (data["id"] ? data["id"] : "")
                            });
                            $(that.options.appendColumns).each(function() {
                                if(this.type) {
                                    switch(this.type) {
                                    case "checkbox":
                                        row.append(that._createDoc("td", "td hd").append(that._createDoc("input").attr({
                                            "type": "checkbox"
                                        }).addClass("chk")));
                                        break;
                                    case "data":
                                        if(this.isID == "1") {
                                            row.attr("data-id", data[this.key])
                                        }
                                        row.append(that._createDoc("td", "td hd").attr({
                                            "data-key": this.key
                                        }).text(data[this.key] ? data[this.key] : '-'));
                                        break;
                                    case "rename":
                                        row.append(
                                            that._createDoc("td", "td hd").attr({
                                                "data-key": this.key
                                            }).append(
                                                that._createDoc("input", "mod-rename").attr({
                                                    "type": "input",
                                                    "data-name": data[this.key]
                                                }).val(data[this.key]).blur(function() {
                                                    that._renameEvent($(this));
                                                }).keydown(function(e) {
                                                    if(e.keyCode == 13) that._renameEvent($(this));
                                                })
                                            )
                                        );
                                        break;
                                    }
                                } else if($.isFunction(this)) {
                                    row.append(that._createDoc("td", "td hd").append(that._trigger(this, data)));
                                } else if ($.isFunction(window[this])) {
                                    row.append(that._createDoc("td", "td hd").append(that._trigger(window[this], {
                                        data: data,
                                        container: that.options.container
                                    })));
                                }
                            });
                            that.rows = that.rows.add(row);
                            rows = rows.add(row);
                        }
                        container.append(that.rows);
                    };
                })(this, tmpData, that.contents), i * 10);
                i++;
            }
        },
        refresh: function(option) {
            this.container.empty().addClass("flash-loading");
            this._create();
        },
        _renameEvent: function(t) {
            var that = this, o = this.options,
                oldName = t.attr("data-name"),
                newName = t.val();
            if(newName) {
                if(oldName != newName && o.renameUrl) {
                    overlayer();
                    ajax(o.renameUrl, $.extend({
                        id : t.closest("tr").attr("data-id"),
                        name : newName
                    }, that.getPageParameters()), function(res) {
                        hidelayer();
                        if(res.result == 0) {
                            t.attr("data-name", newName);
                        } else {
                            say(that.options.ajaxErrorHit + res.err_desc);
                            t.val(oldName);
                        }
                        t.blur();
                    });
                }
            } else {
                t.val(oldName);
            }
        }
    });
    /**
     * List
     * <li>类型的list
     */
    ModuleManage("list", {
        options: {
            container: $("body"),
            configure: [],
            appendColumns: [],
            listItemClass: "",
            // 是否支持排序功能
            isSortable: true,
            sortConfigure: {},
            noticeTitle: undefined,
            moveTitle: undefined,
            multiTitle: undefined,
            //sortHelper: null,
            moveBtnClass: "",
            multiBtnClass: "",
            afterCreate: null
        },
        create: function() {
            this._create();
            //this._initEvents();
            this._trigger(this.options.afterCreate, this);
        },
        rows: null,
        _create: function() {
            var that = this, o = this.options,
                maxLis = this.options.configure.length,
                i = 0, configure, moveHandler,
                multiHandler, li;

            this.lists = $();
            this.moveHandler = $();
            this.multiHandler = $();
            for (i; i < maxLis; i++) {
                configure = $.extend(true, {}, o.configure[i]);
                li = this._createDoc("li", "list-file").attr(configure.attr);
                this._createDoc("div", "list-img " + configure.className)
                    .add(this._createDoc("div", "list-title") .html('<span>' + configure.title + '</span>'))
                    .appendTo(li);
                if (o.isDroppable || o.isSortable) {
                    moveHandler = that._createDoc("span", o.moveBtnClass + " list-move");
                    moveHandler.attr({ style: "display:none;", title: that.options.moveTitle }).text(configure.title);
                    this.moveHandler = this.moveHandler.add(moveHandler);
                    li.append(moveHandler);
                }
                multiHandler = that._createDoc("label", o.multiBtnClass + " list-multi")
                    .append(that._createDoc("input", " list-multi-chk").attr({type: "checkbox"}))
                    .attr({ style: "display:none;", title: that.options.multiTitle });
                this.multiHandler = this.multiHandler.add(multiHandler);
                li.append(multiHandler);

                if(configure.isNotice){
                    li.append(that._createDoc("span", "empty").html("&nbsp").attr("title", o.noticeTitle));
                }
                this.lists = this.lists.add(li);
            }

            this.contents = this._createDoc("ul", "clearfix").append(this.lists);
            o.container.append(this.contents);
        },
        _initEvents: function() {
            var that = this, o = this.options;
            if (this.options.isSortable) {
                //o.sortConfigure.handle = this.moveHandler;
                //this.contents.sortable(o.sortConfigure).disableSelection();
            }
        }
    });
    function _isContrast(){
        var $contrast = $("#J_contrast");
        return ($contrast && $contrast.attr("checked")) ? true : false;
    }

    function ModuleTools(){}
    ModuleTools.prototype = {
        /**
         * @brief _handleLineAreaColumn
         * 同一图中显示线图柱状图面积图
         * percentage: 累计转化率
         * specialper：步骤间转化率
         * @return
         */
        _handleLineAreaColumn: function(data) {
            var config = [{
                type: "column",
                data: [],
                name: data.name
            },{
                type: "area",
                data: [],
                key: "percentage",
                name: lang.t("累积转化率（%）")
            }, {
                type: "line",
                data: [],
                key: "specialper",
                name: lang.t("步骤间转化率（%）")
            }];
            $.each(config, function(){
                var len = data.data.length;
                for( var j = 0; j < len; j++ ){
                    var tmp = {
                        y : (data.data[j] == null ? null : parseFloat(data.data[j]))
                    };
                    if( this.key && data[this.key] ){
                        var perArr = data[this.key];
                        tmp.per = perArr[j];
                    }
                    this.data.push(tmp);
                }
            });
            return config;
        },
        getDrawData: function(data, opts){
            if(opts.lineAreaColumn){
                return data && data[0] && data[0].data && data[0].data.length ? this._handleLineAreaColumn(data[0].data[0]) : [];
            } else if(opts.chartStock) {
                return this._handleStockData(data);
            } else {
                if (opts.average) {
                    data = this._handleAvgData(data);
                }
                if(opts.chartType == "pie"){
                    return this._handlePieData(data && data[0] ? data[0] : []);
                } else {
                    return this._handleChartData(data, opts);
                }
            }
        },
        _handleAvgData: function(data) {
            var rlt = [];
            $(data).each(function() {
				var key = [], data = [], length;
				if (this.average === undefined)
					length = this.key.length;
				else
					length = this.average.key.length;
                for (var i = 1; i < length; i++) {
					if (this.average === undefined) {
						key.push(this.key[i]);
						data.push(this.data[0].data[i]);
					} else {
						key.push(this.average.key[i]);
						data.push(this.average.data[i]);
					}
                }
                rlt.push({
                    key: key,
                    data: [{
                        name: lang.t("平均值"),
                        data: data
                    }]
                });
            });
            return rlt;
        },
        handleTableLineAreaColumn: function(data){
            var rlt  = [],
                len = data.key.length;
            //percentage specialper
            for(var i = 0; i < len; i++){
                var arr = [];
                arr.push(data.key[i]);
                arr.push((data.data[0].data && data.data[0].data[i] != null ? data.data[0].data[i] : '-' ));
                arr.push((data.data[0].percentage && data.data[0].percentage[i] != null ? data.data[0].percentage[i] : '-' ));
                arr.push((data.data[0].specialper && data.data[0].specialper[i] != null ? data.data[0].specialper[i] : '-' ));
                rlt.push(arr);
            }
            return rlt;
        },
        _handleStockData: function(data){
            var rlt = [], that = this;
            if(data.length) {
                var pointInterval = data[0].pointInterval * 1000,
                    pointStart = data[0].pointStart * 1000;
                $(data).each(function(){
                    $(this.data).each(function(){
                        rlt.push({
                            name: this.name,
                            pointInterval: pointInterval,
                            pointStart: pointStart,
                            turboThreshold: 10000,
                            data: that._handleData(this.data)
                        })
                    });
                });
            }
            return rlt;
        },
        _handlePieData: function(data){
            var rlt = [],
                len = data.data[0].data.length,
                percentage = data.data[0].percentage ? true : false;
            for( var i = 0; i < len; i++ ){
                var tmp = {
                    type: "pie",
                    y : data.data[0].data[i] == null ? null : parseFloat( data.data[0].data[i] ),
                    name : data.key[i]
                };
                if(percentage) tmp.per = parseFloat(data.data[0].percentage[i]);
                rlt.push(tmp);
            }
            return rlt;
        },
        _handleChartData: function(data, o){
            var config = o.chartConfig ? o.chartConfig : [];
            var rlt = [], that = this;
            $(data).each(function(){
                var t = this;
                $(this.data).each(function(i){
                    var tmp = {
                        data: [],
                        name: (config[i] && config[i].name ? config[i].name : this.name),
                        visible: o.lineColumn ? true : (config[i] && config[i].visible == false ? false : true)
                    };
                    if(config[i] && config[i].type) tmp.type = config[i].type;
                    var len = this.data.length,
                        percentage = this.percentage ? true : false;
                    for(var j = 0; j < len; j++){
                        var dataTmp = {
                            marker : {
                                enabled: len <= 50 ? true : ($.date.getWeekNum(t.key[j]) == 0 ? true: false ),
                                symbol: ($.date.getWeekNum(t.key[j]) == 0 ? "url(../../../../image/common/sun.png)" : null)
                            },
                            y: that._handleY(this.data[j], config[i])
                        };
                        if(percentage) dataTmp.per = this.percentage[j];
                        //名称：1、对比，2、config中配置name
                        if(_isContrast() && t.key && t.key[j]) dataTmp.name = t.key[j];
                        tmp.data.push(dataTmp);
                    }
                    if(o.lineColumn && i >= 1) tmp.yAxis = 1;
                    rlt.push(tmp);
                });
            });
            return rlt;
        },
        _handleY : function(data, config){
            return data == null
                ? null
                : (config && parseInt(config.round) ? Math.round(data) : parseFloat(data));
        },
        /**
         * @brief _handleData
         * handle every data, guarantee number ,not string
         * @param data
         */
        _handleData: function(data){
            var rlt = [];
            for(var i = 0; i < data.length; i++){
                var tmpData = data[i] == null ? null : parseFloat(data[i]);
                rlt.push(tmpData);
            }
            return rlt;
        },
        /**
         * @brief getTheadByDate
         * 根据日期获取表格头，用于插件
         */
        getTheadByDate: function(dateSeries, arg) {
            var thead = [{ title: lang.t("日期"), css: {width: "200px"} }];
            if(arg && arg.avg){
                thead.push({ title: arg.avg.title, css: { width: "80px" }});
            }
            if(arg && arg.average){
                thead.push({ title: arg.average.title, css: { width: "80px" }});
            }
            $(dateSeries).each(function() {
                thead.push({
                    title: this.toString(),
                    className: isWeekend(this) ? "gr" : "",
                    css: { width: "80px" }
                });
            });
            return thead;
        },
        /**
         * @brief function
         *
         * @param data
         * @param arg: { avg: true }
         *
         * @return
         */
        handleHugeTableData: function(data, arg, average){
            var rlt = [];
            $(data).each(function(j){
                var tmp = [];
                tmp.push(this.name);
                if(arg && arg.average) tmp.push(average[j]);
                if(arg && arg.avg) tmp.push(this.avg);
                if(this.data){
                    for(var i = this.data.length - 1; i >= 0; i--){
                        tmp.push(this.data[i]);
                    }
                }
                rlt.push(tmp);
            });
            return rlt;
        },
        getSeries: function(data, keyUnit, average){
            if (average) {
                data = this._handleAvgData(data);
            }
            return data[0] && data[0].key
                ? ( keyUnit ? this.handleKeyUnit(data[0].key, keyUnit) : data[0].key )
                : [];
        },
        handleKeyUnit: function(data, unit){
            var key = [];
            for(var i = 0; i < data.length; i++ ){
                key.push(data[i] + unit);
            }
            return key;
        },
        getYUnit: function(o){
            var yUnit = [];
            if(o.chartConfig){
                $(o.chartConfig).each(function(i){
                    yUnit.push(this.unit);
                });
            }
            return yUnit;
        },
        getXAxisCategories: function(data, o){
            return o.chartType == "bar" && o.chartConfig && o.chartConfig[0].type == "bar" && data[0] && data[0].data[0].percentage
                ? data[0].data[0].percentage
                : [];
        },

        /**
         * @brief function
         * add period to the download url if period is not exist
         * add file_name to the download url
         * add export = 1 to the download url
         * @param extend ["", "day", "week", "month", "minute", "hour" ]
         * @param timeDimension
         * @param title : title of module "wrap"
         */
        getDownloadUrl: function(extend, title){
            return extend + '?export=1&file_name=' + title;
        }
    };
    window.tmtool = new ModuleTools();

    function _tableChoose(con) {
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
})(jQuery);
