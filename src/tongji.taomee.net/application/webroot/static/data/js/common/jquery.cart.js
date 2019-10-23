/**
 * @fileOverview slide cart plugin
 * @name cart.js
 * @author Young Lee   youngleemails@gmail.com
 * @license GNU GENERAL PUBLIC LICENSE Version 3
 */
// cart container
(function($) {
	$.fn.slideShow = function (dir, offset, speed, easing, fn) {
		var prop = new Object();
		prop[dir] = 0;
		return this.animate(prop, speed, easing, fn);
	};    // DONE
	$.fn.slideHide = function (dir, offset, speed, easing, fn) {
		var prop = new Object();
		prop[dir] = -offset;
		return this.animate(prop, speed, easing, fn);
	};    // DONE
	$.widget('uicart.slidecart', {
		options: {
			position: 'right',
			top: 0,
			bottom: 0,
			height: document.body.clientHeight,
			handlerWidth: 35,
			panelWidth: 250,
			notifyHgt: 40,
			BtnHgt: 100,
			containerPos: null,
			handlerPos: null,
			panelPos: null,
			mask: false,
			maskZIndex: 70,
			maskOpacity: 0.5,
			tags: 1,
			extraInfo: true,
			unfold: false,
			setCommonTag: function () {
				$(this).css({
					width: 35, height: 90, cursor: 'pointer'
				});
				return $(this);
			},
			commonTagEffect: function () {
				return $(this);
			},
			setSingleTag: [
				function () {
				} ],
			setItemsArea: function () {
				$(this).css({
					width: 240,
					height: $(window).height() - 50,
					overflowY: 'auto',
					zIndex: 73,
					marginLeft: 5,
					marginTop: 50,
					backgroundColor: '#5E5E5E'
				});
			},
			setSelOpt: function () {
				$(document.createElement('option')).text('并集运算').attr({
					type: 'union'
				}).appendTo($(this));
				$(document.createElement('option')).text('交集运算').attr({
					type: 'intersect'
				}).appendTo($(this));
				$(document.createElement('option')).text('差集运算').attr({
					type: 'setdiff'
				}).appendTo($(this));
			},
			setUnfold: function () {
				return $(this);
			},
			setFilter: function () {
				return $(this);
			},
			setBtn: function () {
				return $(this);
			}
		},
		_create: function () {
			// Set css positions
			this._setConPos(this.options.position);

			// Fixed element 
			this.element.css($.extend({
				position: "fixed",
				zIndex: this.options.maskZIndex + 1
			}, this.options.containerPos));

			this._setHandler();    // Handler
			this._setPanel();    // Panel
			
			// Add hook			
			var hooks = this._addTag(this._cartHandler);    // Add tag
			this._addHook(hooks, 'click', this.element);
		},
		_setConPos: function (pos) {
            this.options.containerPos = {
                top: this.options.top,
                bottom: this.options.bottom,
                width: this.options.panelWidth
            };
            this.options.handlerPos = {
                padding: 0,
                margin: 0,
                width: this.options.handlerWidth
            };
            this.options.panelPos = {
                height: "100%"
            };
			switch (pos) {
			case 'left':
                this.options.containerPos.left = -this.options.panelWidth;
                this.options.handlerPos.right = -this.options.handlerWidth;
				break;
			case "right":
			default:    // right
                this.options.containerPos.right = -this.options.panelWidth;
                this.options.handlerPos.left = -this.options.handlerWidth;
			}
		},    // DONE
		_setHandler: function () {
			this._cartHandler = $('<div></div>')
				.css($.extend({
					position: "absolute",
					zIndex: this.options.maskZIndex + 3
				}, this.options.handlerPos))
				.addClass("cart-handler")
				.appendTo(this.element);
			return this._cartHandler;
		},    // DONE
		_setPanel: function () {
			this._innerPanel = $('<div></div>')
				.css($.extend({
					zIndex: this.options.maskZIndex + 2
				}, this.options.panelPos))
				.addClass("inner-panel")
				.appendTo(this.element);
			this._setNotifyArea();
			this._setItemsArea();
			this._setBtnArea();
			return this._innerPanel;
		},    // DONE
		_setNotifyArea: function () {
			this._notifyArea = this._appendElement("div", "notify-area", this._innerPanel);
			this._notifyArea.css({
				height: this.options.notifyHgt,
				lineHeight: this.options.notifyHgt + "px"
			});
			this._setNotifyTxt();
			return this._notifyArea;
		},
		_setNotifyTxt: function () {
			this._notifyTxt = this._appendElement("div", "notify-txt", this._notifyArea);
			this._notify = this._appendElement('span', 'notify', this._notifyTxt);
			this._notify.attr({
				title: '差集支持最多3个事件的计算,交集和并集支持最多5个事件的计算'
			}).text('已选0条，共0条');
			return this._notifyTxt;
		},
		_setItemsArea: function () {
            var that = this,
                o = this.options;
			this.itemArea = this._appendElement("div", "item-area", this._innerPanel);
			this.itemArea.css({
				height: this.itemArea.parent().height() - o.notifyHgt - o.BtnHgt
			});
            $(window).resize(function () {
                that.itemArea.css({ 
                    height: that.itemArea.parent().height() - o.notifyHgt - o.BtnHgt 
                });
            });
			this._setItemCon();
			return this.itemArea;
		},
		_setItemCon: function () {
			this._itemCon = this._appendElement('div', 'item-con', this.itemArea);
			this._itemCon.css({
                height: "100%",
				overflowY: "auto"
			});
			this.options.setItemsArea.call(this._itemCon);
			return this._itemCon;
		},
		_setBtnArea: function () {
			this.btnArea = this._appendElement('div', 'btn-area', this._innerPanel);
			this.btnArea.css({
				height: this.options.BtnHgt
			});
			this._setSubmitCtrl();
			this._setFilter();
			this._setSubmitBtn();
			return this.btnArea;
		},
		_setSubmitCtrl: function () {
			this.submitCtrl = this._appendElement('div', 'submit-ctrl', this.btnArea);
			this.submitCtrl.css({
				height: this.options.BtnHgt / 2
			});
			if (this.options.extraInfo) this._setExtraInfo();
			return this.submitCtrl;
		},
		
		_setExtraInfo: function () {
			this.extraInfo = this._appendElement('div', 'extra-info', this.submitCtrl);
			this.extraInfo.css({
				paddingLeft: 10,
				paddingRight: 10,
                paddingTop: 10,
				height: this.options.BtnHgt / 2
			});
			this._setSelect();
			this._setUnfold();
			return this.extraInfo;
		},
		_setSelect: function () {
			this.selectArea = this._appendElement('div', 'select-area', this.extraInfo);
			this.gameSelector = this._appendElement('select', 'game-selector', this.selectArea);
			this.calcSelector = this._appendElement('select', 'calc-selector', this.selectArea);
            this.selectArea.css({
				float: 'left'
            });
			this.calcSelector.css({
				float: 'left',
				height: 30,
				width: 80
			});
			this.gameSelector.css({
				float: 'left',
				height: 30,
				width: 100
			}).hide();
			this.options.setSelOpt.call(this.calcSelector);
			return this.selectArea;
		},
		_setUnfold: function () {
			var context = this;
			this.unfold = this._appendElement('div', 'unfold', this.extraInfo);
			this.unfold.css({
				float: 'right',
				height: 30,
				width: 90
			}).click(function () {
				var $extra = $(this).parent(),
					$filter = $extra.parent().next();
					$submit = $extra.parent().next().next(),
					$btnArea = $extra.parent().parent(),
					$itemArea = $btnArea.prev();

				if (context.options.unfold) {
					$itemArea.animate({ height: '+=200px' }, 'fast');
					$itemArea.children().animate({ height: '+=200px' }, 'fast');
					$filter.animate({ height: '-=200px' }, 'fast', function () {
						$(this).hide();
					});
					$btnArea.animate({ height: '-=200px' }, 'fast');
					context.options.unfold = false;
				} else {
					$itemArea.animate({ height: '-=200px' }, 'fast');
					$itemArea.children().animate({ height: '-=200px' }, 'fast');
					$filter.animate({ height: '+=200px' }, 'fast').show();
					$btnArea.animate({ height: '+=200px' }, 'fast');
					context.options.unfold = true;
				}
			});
			this.options.setUnfold.call(this.unfold);
			return this.unfold;
		},
		_setFilter: function () {
			this.filter = this._appendElement('div', 'filter-info', this.btnArea);
			this.filter.css({
				float: 'left',
				width: '100%',
				height: 0
			}).hide();
			this.options.setFilter.call(this.filter);
			return this.filter;
		},
		_setSubmitBtn: function () {
			var margin_left = 10,
				bottom = 10,
				context = this;
			this.submitBtn = this._appendElement('div', 'submit-btn', this.btnArea);
			this.submitBtn.css({
				float: 'left',
				width: this.submitBtn.parent().width() - (margin_left * 2),
				marginLeft: margin_left,
				height: this.options.BtnHgt / 2 - bottom,
				borderRadius: 3,
				cursor: 'pointer'
			}).click(function () {
				context._trigger('submit');
			});
			this.options.setBtn.call(this.submitBtn);
			return this.submitBtn;
		},
		_addTag: function (handler) {    // Shall controll css from the outside
			var context = this;
			for(var i = 0; i < this.options.tags; i++) {
				var tagIns = $(document.createElement("div"))
						.addClass('tag-ins off')
						.appendTo(handler)
						.click(function () {
							$(this).toggleClass('off');
							$(this).toggleClass('on');
							if ($(this).hasClass('on'))
								context._trigger('refresh');
							else
								context._trigger('save');
						});
				this.options.setCommonTag.call(tagIns);
				this.options.commonTagEffect.call(tagIns);
				if (this.options.setSingleTag[i] != null)
					this.options.setSingleTag[i].call(tagIns);
			}
			return $('.tag-ins');
		},
		_addItemsArea: function (panel) {
			this._itemsArea = $('<div></div>')
				.addClass('items-area')
				.appendTo(panel);
			this.options.setItemsArea.call(this._itemsArea);
			return $('.items-area');
		},
		_addButton: function (panel) {
			var btnAreaWidth = 140;
			this._panelBtn = $('<div></div>')
				.css({
					height: btnAreaWidth,
					width: this.options.panelWidth,
					marginLeft: 0,
					marginTop: - btnAreaWidth,
					bottom: this.options.bottom,
					left: 0,
					zIndex: this.options.maskZIndex + 100,
					cursor: 'pointer'
				})
				.addClass('panel-btn')
				.appendTo(panel);
			return $('.panel-btn');
		},
		addButton: function (panel) {
			this._addButton(panel);
		},    // Call from the outside
		_addHook: function (anchor, event, folddiv) {
			var mask = document.createElement('div'),
				that = this,
				queue = [];
			
			$(mask).css({
				position : "fixed",
				backgroundColor: 'black',
				opacity : this.options.maskOpacity,
				top : this.options.top,
				bottom: this.options.bottom,
				nwidth : $(document).width(),
				left: 0,
				right: 0,
				zIndex: that.options.maskZIndex
			}).hide().appendTo(document.body);
			anchor.each(function () {
				$(this).on(event, function () {
					var queueSize = that.options.tags == 1 ? 2 : that.options.tags;    // queueSize >= 2 
					if (queue.length > queueSize - 1)
						queue.shift();
					queue.push(this);
					if (queue.length === 1) {
						if (that.options.mask) {
							$(mask).css({
 								height : $(document).height() - (that.options.top + that.options.bottom)
							}).fadeIn('normal');
						}				
						folddiv.slideShow(that.options.position, that.options.panelWidth, 'normal');
					} else if ((queue[queue.length - 1] == queue[queue.length - 2])) {
						if (that.options.mask) {
							$(mask).fadeOut("normal");
						}
						folddiv.slideHide(that.options.position, that.options.panelWidth, 'fast');
						queue = [];
					} else if ((queue[queue.length - 1] != queue[queue.length - 2])) {
						//TODO: refresh panel
					}
				});
			});
		},    // DONE
		addHook: function (anchor, event, folddiv) {
			this._addHook(anchor, event, folddiv);
		},    // Call from the outside
		_appendElement: function (type, cls, con) {
			return $(document.createElement(type)).addClass(cls).appendTo(con);
		},
		destroy: function () {
			this._cartHandler.remove();
			this._innerPanel.remove();
			$.Widget.prototype.destroy.apply(this, arguments);
		}
	});
})(jQuery);
