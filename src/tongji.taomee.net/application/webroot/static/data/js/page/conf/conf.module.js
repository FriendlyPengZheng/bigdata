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

	var Module = function() {};
	Module.prototype = {
		// options for init
		options: {},
		uuid: null,
		// real start, set the options
		_createModule: function(options) {
			this.uuid = this.getUuid();
			this.options = ModuleManage.extend({}, ModuleLanguage.get(this.moduleName), this.options, options);
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
			return $(document.createElement("a")).addClass("iconfont mod-btn " + className);
		},
		_createBtn: function(title, className){
			className = className ? className : '';
			return $(document.createElement("a")).addClass("mod-s-btn " + className).text(title);
		},
		// create radio button document
		_createRadioButtion: function(listType) {
			var container = this._createDoc("ul", "clearfix change-btn-con"),
				list = $(),
				that = this;
			$(listType).each(function() {
				list = list.add(that._createDoc("li", "change-btn")
								.append(that._createDoc("span").text(this.title))
								.attr({
									dataId: this.dataId
								}));
			});
			list.first().addClass("first");
			list.last().addClass("last");
			return container.append(list);
		},
		//datepicker
		_createTime: function(){
			var container = this._createDoc("div", "datepicker-trigger radius5-all fr");
			var html = ' <i class="datepicker-icon"></i>'
					+ ' <input class="title cts-date" type="text" value="'
					+ ($("#J_from").val() ? $("#J_from").val() : '')
					+ '~'
					+ ($("#J_to").val() ? $("#J_to").val() : '')
					+ '" />'
					+ ' <i class="datepicker-arrow"></i>';
			return container.html(html);
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
	};
	ModuleLanguageManage.prototype = {
		language: {},
		regional: {
			chinese: {
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
					}, {
						title: "版本周",
						dataId: 6
					}],
					copyTitle: "复制",
					removeTitle: "删除",
					editTitle: "修改",
					downloadTitle: "下载"
				},
				table: {
					yoyTitle: "同比(%)",
					qoqTitle: "环比(%)"
				},
				data: {
					ajaxErrorHit: "获取数据错误，请稍后再试。",
					ajaxEmptyHit: "没有数据。"
				},
				list: {
					noticeTitle: "分布区间为空"
				}
			},
			english: {
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
					}, {
						title: "version week",
						dataId: 6
					}],
					copyTitle: "copy",
					removeTitle: "delete",
					editTitle: "edit",
					downloadTitle: "download"
				},
				table: {
					yoyTitle: "yoy(%)",
					qoqTitle: "qoq(%)"
				},
				data: {
					ajaxErrorHit: "There is an error, please try again later.",
					ajaxEmptyHit: "No data."
				},
				list: {
					noticeTitle: "Distribution is empty."
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
	window.ModuleLanguage = new ModuleLanguageManage("chinese");

	// wrap include table/graph and timeDimension、download
	ModuleManage("wrap", {
		options: {
			//wrap属性
			attr: {},
			// 容器
			container: $("body"),
			// 头部日期列表
			headTime: undefined,
			downloadTitle: undefined,
			removeTitle: undefined,
			editTitle: undefined,
			favorTitle: undefined,
			renameTitle: undefined,
			commentTitle: undefined,
			copyTitle: undefined,
			// 是否提供头部功能
			headEnabled: true,
			// 是否提供对比时间功能
			contrast: true,
			// 是否提供底部功能
			bottomEnabled: true,
			// 显示宽度（百分比）
			width: 1,
			// 数据或显示更新时需要调用的函数
			refresh: null,
			// 下载
			download: null,
			// 删除
			remove: null,
			// 编辑
			edit: null,
			// 添加到我的收藏
			favor: null,
			// 重命名
			rename: null,
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
		download: null,
		remove: null,
		edit: null,
		favor: null,
		rename: null,
		comment: null,
		content: $(),
		timeDimension: null,
		headTime: $(),
		isTableShow: true,
		isGraphShow: true,
		create: function() {
			this._createHeader();
			this._createBody();
			this.container = this._createDoc("div", "mod-box").attr(this.options.attr)
				.append(this.head).append(this.body);
			if(this.options.draggable) this._createDrag();
			$(this.options.container).append(this._getBoxContainer().append(this.container));
			this._initEvents();
		},
		_getBoxContainer: function(){
			return this._createDoc("div", "mod-box-container").css({
				float: "left",
				width: this.options.width * 100 + "%"
			});
		},
		_initEvents: function() {
			var that = this;
			this.copy.on("click", function(){
				that._trigger(that.options.copy, that);
			});
			if(this.download){
				this.download.on("click", function() {
					that._trigger(that.options.download, that);
				});
			}
			if(this.remove){
				this.remove.on("click", function() {
					that._trigger(that.options.remove, that);
				});
			}
			if(this.edit){
				this.edit.on("click", function() {
					that._trigger(that.options.edit, that);
				});
			}
			if (this.options.headEnabled) {
				this.headTime.find("li").on("click", function() {
					if (!$(this).hasClass("cur")) {
						that.headTime.find("li").removeClass("cur");
						$(this).addClass("cur");
						that.timeDimension = $(this).attr("dataId");
						that.refresh();
					}
				}).first().click();
			}
			if(this.options.contrast){
				var contrast = this.contrast,
					time = contrast.find(".cts-date");
				time.datepick({
					rangeSelect: true,
					monthsToShow: 3,
					monthsToStep: 3,
					monthsOffset: 2,
					shortCut : true,
					maxDate: new Date(),
					onClose: function(userDate) {
						//判断是否是同一时间
						if(userDate.length && ($.datepick.formatDate("yyyy-mm-dd", userDate[0]) != that.from
											   || $.datepick.formatDate("yyyy-mm-dd", userDate[1]) != that.to ) ){
												   var userDate = time.val().split("~");
												   userDate[0] = $.trim(userDate[0]);
												   userDate[1] = $.trim(userDate[1]);
												   that.from = userDate[0];
												   that.to = userDate[1];
												   that.refresh({
													   from: that.from,
													   to: that.to,
													   dataChange: true
												   });
											   }
					}
				});
				this.contrast.click(function(e){ e.stopPropagation();
												 time.focus();
											   });
			}
			if (!empty(this.options.bottomEnabled)) {
				this.table.on("click", function() {
					if (!$(this).hasClass("cur")) {
						that.table.addClass("cur");
						that.graph.removeClass("cur");
						that.isTableShow = true;
						that.isGraphShow = false;
						that.refresh();
					}
				});
				this.graph.on("click", function() {
					if (!$(this).hasClass("cur")) {
						that.graph.addClass("cur");
						that.table.removeClass("cur");
						that.isTableShow = false;
						that.isGraphShow = true;
						that.refresh();
					}
				}).click();
			}
			if(this.options.draggable){
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
		refresh: function(options) {
			this._trigger(this.options.refresh, $.extend({}, {
				timeDimension: this.timeDimension,
				table: this.isTableShow,
				graph: this.isGraphShow
			}, options));
		},
		_createHeader: function() {
			var o = this.options,
				head = this._createDoc("div", "mod-header"),
				title = this._createDoc("span", "mod-title");
			this.copy = this._createButton("icon-mod-copy fr ml5").attr("title", o.copyTitle).appendTo(head);
			if(o.remove){
				this.remove = this._createButton("icon-mod-remove fr ml5").attr("title", o.removeTitle).appendTo(head);
			}
			if(o.download){
				this.download = this._createButton("icon-mod-download fr ml5").attr("title", this.options.downloadTitle)
					.appendTo(head);
			}
			if(o.edit){
				this.edit = this._createButton("icon-mod-edit fr ml5").attr("title", o.editTitle).appendTo(head);
			}
			if(o.rename){
				this.rename = this._createButton("icon-mod-rename fr ml5").attr("title", o.renameTitle).appendTo(head);
			}
			if(o.favor) {
				this.favor = this._createButton("icon-mod-favor fr ml5").attr("title", o.favorTitle).appendTo(head);
			}
			if(o.comment) {
				this.comment = this._createButton("icon-mod-comment fr ml5").attr("title", o.commentTitle).appendTo(head);
			}
			this.head = head.append(title.text(this.options.title));
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
				tools = this._createDoc("div", "mod-text-tools"),
				bottom = this._createDoc("div", "mod-text-bottom");
			this.content = this._createDoc("div", "mod-text-con");
			if (!empty(this.options.headEnabled)) {
				this.headTime = this._createRadioButtion(this.options.headTime);
				head.append(this.headTime);
			}
			if(this.options.contrast){
				this.contrast = this._createTime();
				head.append(this.contrast);
			}
			if(this.options.toolsConfig, $.isArray(this.options.toolsConfig)){
				$.each(this.options.toolsConfig, function(){
					var t = this,
						btn = that._createBtn(t.title, "mr5");
					btn.appendTo(tools).click(function(){
						if(t.callback) t.callback(that.getContainer(), that.getId(), that.getContent());
					});
				});
			}
			if (!empty(this.options.bottomEnabled)) {
				this.table = this._createButton("icon-mod-table");
				this.graph = this._createButton("icon-mod-graph mr5");
				bottom.append(this.graph).append(this.table);
			}
			this.body = container.append(head).append(tools).append(this.content).append(bottom);
		},
		getContainer: function(){
			return this.container;
		},
		getContent: function() {
			return this.content;
		},
		getId: function(){
			return this.container.attr("data-id");
		},
		renameWrap: function(title){
			this.head.find(".mod-title").text(title);
		}
	});

	// create tab document
	ModuleManage("tab", {
		options: {
			container: $("body"),
			tabsSkin: "",
			child: [],
			refresh: null
		},
		head: $(),
		body: $(),
		content: $(),
		container: $(),
		panels: $(),
		create: function() {
			this._createHeader();
			this.container = this._createDoc("div", "tabs-wrapper" + (this.options.tabsSkin == "orange" ? " radio-tabs-wrapper" : ""))
				.append(this.head)
				.append(this.panels);
			$(this.options.container).append(this.container);
		},
		_createHeader: function() {
			var that = this,
				head = this._createDoc("ul", "tabs-list clearfix" + (this.options.tabsSkin == "orange" ? " radio-tabs" : "")),
				len = this.options.child.length,
				list = $(), panel;
			$(this.options.child).each(function(i) {
				list = list.add($(that._createDoc("li", "tabs-ajax tabs-control"
												  + (i == 0 ? " first" : ( i == len - 1 ? " last" : ""))).attr($.extend({}, this.attr))).append(
													  that._createDoc("a").text(this.title)
												  ).data("data", this));
				if (this.toolsConfig) {
					that.panels = that.panels.add($("<div>").append(that._createTools(this.toolsConfig)));
				}
			});
			this.head = head.append(list);
		},
		_createTools: function(toolsConfig) {
			var tools = this._createDoc("div", "tab-wrap-tools"),
				that = this;
			$.each(toolsConfig, function(){
				var t = this,
					btn = that._createBtn(t.title, "mr5");
				btn.appendTo(tools).click(function(){
					if(t.callback) t.callback(that.getContainer(), that.getId(), that.getContent());
				});
			});
			return tools;
		},
		getId: function(){
			return this.container.tabs("getActive").attr("data-id");;
		},
		getContent: function(){
			return this.container.tabs("getActivePanel");
		},
		getContainer: function() {
			return this.container;
		},
		refresh: function(option) {
			this._trigger(this.options.refresh, option);
		}
	});
	// create tab document
	ModuleManage("tabwrap", {
		options: {
			container: $("body"),
			title: "tabwrap",
			refresh: null
		},
		container: $(),
		create: function() {
			this.container = this._createDoc("div", "tab-wrap").append(
				this._createDoc("h4").text(this.options.title));
			this.content = this._createDoc("div", "tab-wrap-con");
			this._createTools();
			if(this.options.attr) this.container.attr(this.options.attr);
			$(this.options.container).append(this.container.append(this.content));
		},
		_createTools: function(){
			var that = this;
			if(this.options.toolsConfig, $.isArray(this.options.toolsConfig)){
				var tools = this._createDoc("div", "tab-wrap-tools");
				$.each(this.options.toolsConfig, function(){
					var t = this,
						btn = that._createBtn(t.title, "mr5");
					btn.appendTo(tools).click(function(){
						if(t.callback) t.callback(that.getContainer(), that.getId(), that.getContent());
					});
				});
				tools.appendTo(this.container);
			}
		},
		getContainer: function() {
			return this.container;
		},
		getContent: function() {
			return this.content;
		},
		getId: function(){
			return this.container.attr("data-id");
		},
		refresh: function(option) {
			this._trigger(this.options.refresh, option);
		}
	});

	// create table document
	ModuleManage("table", {
		options: {
			container: $("body"),
			// 表格头部title
			thead: [],
			// 是否有同比数据
			yoy: false,
			yoyTitle: undefined,
			// 是否有环比数据
			qoq: false,
			checkbox: false,
			qoqTitle: undefined,
			// 是否初始隐藏
			hide: true,
			// 数据是否延后
			dataDelay: false,
			// 重组数据
			prepareData: null,
			// 生成表格后加载
			afterCreate: null,
			// 刷新时调用
			refresh: null
		},
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
			this._createHeader();
			this._createBody();
			this.container = this._createDoc("div", "table-wrapper")
				.append(this._createDoc("table", "table")
						.append(this.head).append(this.body));
			$(this.options.container).append(this.container);
			this._trigger(this.options.afterCreate, this.getContainer());
		},
		_createHeader: function() {
			var that = this, o = this.options,
				head = this._createDoc("thead"),
				list = $();
			if(o.checkbox){
				list = list.add($(that._createDoc("th", "th w80").attr({}).html('<a class="table-btn sel-total">全选</a>|<a class="table-btn sel-other">反选</a>')));
			}
			$(o.thead).each(function(i) {
				list = list.add($(that._createDoc("th", "th " + (this.className ? this.className : "") )).attr({
					"data-type": this.type
				}).css(this.css || {}).text(this.title));
			});
			if (o.yoy) {
				list = list.add($(this._createDoc("th", "th")).attr({
					"data-type": "float"
				}).text(this.options.yoyTitle));
			}
			if (o.qoq) {
				list = list.add($(this._createDoc("th", "th")).attr({
					"data-type": "float"
				}).text(this.options.qoqTitle));
			}
			this.head = head.append(this._createDoc("tr", "tr").append(list));
		},
		_createBody: function() {
			var that = this,
				colspan = that.options.thead.length - 1,
				list = $(),
				body = this._createDoc("tbody");
			$(this._prepareData()).each(function() {
				var row = $();
				if (that.options.dataDelay) {
					$(this).each(function() {
						if(that.options.checkbox){
							row = row.add(that._createDoc("td", "td hd").html('<input type="checkbox" class="tbl-ckb" value="' + this.dataId + '"/>'))
						}
						row = row.add(that._createDoc("td", "td hd").text(this.title).attr(this));
					});
					row = row.add(that._createDoc("td", "td hd").attr({
						colspan: colspan
					}).append(that._createDoc("span", "row-loading").text("loading...")));
				} else {
					if(this && $.isArray(this)){
						for(var k = 0; k < this.length; k++ ){
							row = row.add(that._createDoc("td", "td hd").text(this[k]));
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
				this.options.data = this._trigger(this.options.prepareData, this.options.data);
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
			option = $.extend({
				table: this.table
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
		}
	});

	// get ajax data
	ModuleManage("data", {
		container: null,
		options: {
			url: {
				extend: null,
				page: null
			},
			timeDimension: null,
			afterLoad: null,
			refresh: null,
			data: null,
			isAjax: true,
			ajaxErrorHit: undefined,
			ajaxEmptyHit: undefined
		},
		timeDimension: null,
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
			if(this.options.container.hasClass("data-wrap")){
				this.container = this.options.container.addClass("clearfix").attr(this.options.attr);
			} else {
				this.container = this._createDoc("div", "data-wrap clearfix").attr(this.options.attr).appendTo(this.options.container);
			}
			this.content = this._createDoc("div", "data-con");
			this.data = this.options.data || [];
			this.container.append(this._createDoc("h4").text("Data")).append(this._createTools()).append(this.content);
			//this.init();
		},
		getContainer: function(){
			return this.container;
		},
		getContent: function(){
			return this.content;
		},
		getId: function(){
			return this.container.attr("data-id");
		},
		_createTools: function(){
			var that = this;
			if(this.options.toolsConfig, $.isArray(this.options.toolsConfig)){
				var tools = this._createDoc("div", "data-tools");
				$.each(this.options.toolsConfig, function(){
					var t = this,
						btn = that._createBtn(t.title, "mr5");
					btn.appendTo(tools).click(function(){
						if(t.callback) t.callback(that.getContainer(), that.getId(), that.getContent());
					});
				});
				tools.appendTo(this.container);
			}
		},
		url: [],
		child: [],
		init: function() {
			var that = this,
				callback = function(data) {
					that._trigger(that.options.afterLoad, data, that.container);
				};
			this.timeDimension = this.options.url.timeDimension ? this.options.url.timeDimension : 1;
			this.dataChange = true;
			$.isArray(this.options.url.extend) ? (this.url = this.options.url.extend) : (this.url = ["", this.options.url.extend]);
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
				this._getAjaxData(callback);
			} else {
				this._trigger(callback, this.data[this.timeDimension]);
			}
		},
		_getAjaxData: function(callback) {
			var that = this;
			this.loading();
			if (!that.dataChange && this.data[this.timeDimension]) {
				that.loaded();
				that._trigger(callback, this.data[this.timeDimension]);
				return;
			}
			if(this.url[this.timeDimension]){
				ajax(this.url[this.timeDimension], $.extend({
					period: this.timeDimension
				}, this.getPageParameters()), function(res) {
					if (res.result == 0) {
						that.loaded();
						that.data[that.timeDimension] = res.data;
						if (!$.isEmptyObject(that.data[that.timeDimension])) {
							that._trigger(callback, that.data[that.timeDimension]);
						} else {
							that.content.text(that.options.ajaxEmptyHit);
						}
						that.dataChange = false;
						that.timeDimensionChange = false;
					} else {
						that.content.text(that.options.ajaxErrorHit);
					}
				});
			}
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
			if (option.timeDimension && this.timeDimension != option.timeDimension) {
				this.timeDimension = option.timeDimension;
				this.timeDimensionChange = true;
			} else {
				if (option.dataChange) {
					this._reloadData();
				}
			}
			callback = function(data) {
				if (that.dataChange || that.timeDimensionChange) {
					option.data = data;
				}
				option.dataChange = that.dataChange || that.timeDimensionChange;
				that._trigger(that.options.refresh, option);
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
			data: {}
		},
		head: $(),
		body: $(),
		content: $(),
		container: $(),
		graph: true,
		chart: [],
		data: null,
		create: function() {
			this.data = this.options.data;
			this.container = this._createDoc("div", "graph-wrapper flash-loading");
			$(this.options.container).append(this.container);
			this._draw();
		},
		_draw: function() {
			var width = this.options.container.width();
			new $.draw.DrawFactory({
				container : this.container.removeClass("flash-loading"),
				colorTheme : "orange",
				timeDimension : "day",
				columnStack: this.options.columnStack ? this.options.columnStack : "",
				chartData : this.data.data,
				chartType : this.options.chartType,
				series: this.data.key,
				yUnit: this.options.yUnit ? this.options.yUnit : '',
				chartStock: this.options.chartStock,
				page : this.options.page ? true : false,
				width : width
			});
			//this.chart.push(chart);
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
			// 表格头部title
			thead: [],
			checkbox: false,
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
			if(o.checkbox){
				list = list.add($(that._createDoc("th", "th w80").attr({}).html('<a class="table-btn sel-total">全选</a>|<a class="table-btn sel-other">反选</a>')));
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
				window.setTimeout((function(that, tmpData, body){
					return function(){
						var list = $(),
							ids = [],
							colspan = that.options.thead.length - 1;

						$(tmpData).each(function(){
							var row = $();
							if (that.options.dataDelay) {
								$(this).each(function() {
									if(that.options.checkbox){
										row = row.add(that._createDoc("td", "td hd").html('<input type="checkbox" class="tbl-ckb" value="' + this.dataId + '"/>'))
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
									that._createProgressiveRow(rows, res.data);
								}
							};
						})(list));
						that._fixedTable(that.getContainer(), that.tbl);
					};
				})(that, tmpData, body), i * 500 );
				i++;
			}
		},
		_createRows: function(tmpData) {
			var list = $(),
				ids = [],
				that = this,
				colspan = that.options.thead.length - 1;

			$(tmpData).each(function(){
				var row = $();
				if (that.options.dataDelay) {
					$(this).each(function() {
						if(that.options.checkbox){
							row = row.add(that._createDoc("td", "td hd").html('<input type="checkbox" class="tbl-ckb" value="' + this.dataId + '"/>'))
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
			this.body = this.body.append(list);
			ajax(this.options.url.url, this.getCommonParameters(ids), (function(rows) {
				return function(res) {
					if (res.result == 0) {
						that._createProgressiveRow(rows, res.data);
					}
				};
			})(list));
			this._fixedTable(this.getContainer(), this.table);
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
					colNum: this.options.checkbox ? 2 : 1,
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
							$(data.data.reverse()).each(function() {
								row.append(that._createDoc("td", "td").text(this.toString()));
							});
						};
					})(row, this), index * 60);
				});
			} else {
				$.each(rows, function(){
					$(this).find(".td:last").empty().css({'text-align': 'left'}).text("没数据...");
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
			container: $("body"),
			url: {
				url: "",
				page: null
			},
			dataCountEachRequest: 200
		},
		rows: $(),
		create: function() {
			this.getData();
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
					ids.push(that.options.checkbox ? row.find("td:eq(1)").attr("dataId") : row.find("td:first").attr("dataId"));
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
						})(rows));
					};
				})(ids, rows), j * 10);
				if (length < eachCount) {
					eachCount = length;
				}
			}
		},
		_create: function(rows, data) {
			var that = this, row;
			if( data && data.data && data.data.length ){
				$(data.data).each(function(index) {
					row = rows[index];
					window.setTimeout((function(row, data) {
						return function() {
							row.find("td:last").remove();
							$(data.data.reverse()).each(function() {
								row.append(that._createDoc("td", "td").text(this.toString()));
							});
						};
					})(row, this), index * 60);
				});
			} else {
				$.each(rows, function(){
					$(this).find(".td:last").empty().css({'text-align': 'left'}).text("没数据...");
				});
			}
			rows = null;
		},
		refresh: function() {
			this.getData();
		}
	});

	// List
	// table类型list
	ModuleManage("listtable", {
		options: {
			container: $("body"),
			appendColumns: [],
			isAjax: true,
			url: {},
                        enablePagination: false,
			afterCreate: null
		},
		create: function() {
			if(this.options.container.hasClass("data-wrap")){
				this.container = this.options.container.addClass("clearfix").attr(this.options.attr);
			} else {
				this.container = this._createDoc("div", "data-wrap clearfix").attr(this.options.attr).appendTo(this.options.container);
			}
			this.content = this._createDoc("div", "data-con");
			this.container.append(this._createDoc("h4").text("listtable")).append(this._createTools()).append(this.content);
		},
		_createTools: function(){
			var that = this;
			if(this.options.toolsConfig, $.isArray(this.options.toolsConfig)){
				var tools = this._createDoc("div", "data-tools");
				$.each(this.options.toolsConfig, function(){
					var t = this,
						btn = that._createBtn(t.title, "mr5");
					btn.appendTo(tools).click(function(){
						if(t.callback) t.callback(that.getContainer(), that.getId(), that.getContent());
					});
				});
				tools.appendTo(this.container);
			}
		},
		getContainer: function(){
			return this.container;
		},
		getContent: function(){
			return this.content;
		},
		getId: function(){
			return this.container.attr("data-id");
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
			//sortHelper: null,
			moveBtnClass: "",
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
				li;

			this.lists = $();
			this.moveHandler = $();
			for (i; i < maxLis; i++) {
				configure = $.extend(true, {}, o.configure[i]);
				li = this._createDoc("li", "list-file").attr(configure.attr);
				this._createDoc("div", "list-img " + configure.className)
					.add(this._createDoc("div", "list-title") .html('<span>' + configure.title + '</span>'))
					.appendTo(li);
				if (o.isDroppable || o.isSortable) {
					moveHandler = that._createDoc("span", o.moveBtnClass + " list-move");
					moveHandler.attr({ style : "display:none;", title : "移动" }).text(configure.title);
					this.moveHandler = this.moveHandler.add(moveHandler);
					li.append(moveHandler);
				}
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
})(jQuery);
// for 游戏分析的prepareData
//var data = [], that = this;
//$.each(this.options.data, function() {
//if (!$.isArray(this)) return;
//$(this).each(function(index) {
//if (!data[index]) {
//data[index] = [];
//}
//if (this.data) {
//$(this.data).each(function(i) {
//data[i].push(this.toString());
//});
//} else {
//data[index].push(this);
//}
//if (that.options.yoy) {
//if (this.yoy) {
//$(this.yoy).each(function(i) {
//data[i].push(this.toString());
//});
//}
//}
//if (that.options.qoq) {
//if (this.qoq) {
//$(this.qoq).each(function(i) {
//data[i].push(this.toString());
//});
//}
//}
//});
//});
//return data;
