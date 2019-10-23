/**
 * @fileOverview
 * @name jquery.cartitem.js
 * @author Young Lee   youngleemails@gmail.com
 * @license GNU GENERAL PUBLIC LICENSE Version 3 
 */
(function($) {
	$.widget('uicart.item', {
		options: {
            zIndexBasic: 100,
			timeDft: "0000-00-00 ~ 0000-00-00",
			timeMax: 5,
			timeCount: 0,
			timeInit: 1,
			timeInfo: [],
            ckboxSelected: function () {
                //TODO: 
            },
            gameNameTxt: function () {
                $(this).text("More");
            },
            ItemNameTxt: function () {
                $(this).text("Game Custom Item");
            },
            setItemTime: function () {
                $(this).click(function () {
					console.log('set time');
                });
            },
            setItemAdd: function () {
                $(this).click(function () {
                    var $addArea = $(this).parent(),
						$timeArea = $(this).parent().parent(),
						$tagArea = $timeArea.find('.tag-area'),
                        $widget = $timeArea.parent().parent();
					var foldIcon = $(this).parent().parent().parent().next().find('.item-fold'),
						unfoldHgt = 145;
					if ($addArea.height() < unfoldHgt) {
						foldIcon.toggleClass('foldarea');
						foldIcon.find('.icon').children().eq(0).toggleClass('icon-itemfold');
						foldIcon.find('.icon').children().eq(0).toggleClass('icon-itemunfold');
					}
					$addArea.animate({ height: '145px' }, 'fast');
					$tagArea.animate({ height: '145px' }, 'fast');
                    $timeArea.animate({ height: '145px' }, 'fast');
                    $widget.animate({ height: '215px' }, 'fast');
                    $widget.find('.item-fold').animate({ marginTop: '178px' }, 'fast', function () {
                        $(this).show();
						$tagArea.find('.time-tag').not('.dft').show();
                    });
                });
            },
            setItemClose: function () {
                $(this).click(function () {
                    var $widget = $(this).parent().parent();
                    $widget.fadeOut('slow', function () {
                        $(this).remove();
                    });
                });
            },
            setItemFold: function () {
                $(this).click(function () {
					$(this).toggleClass('foldarea');
					$(this).find('.icon').children().eq(0).toggleClass('icon-itemfold');
					$(this).find('.icon').children().eq(0).toggleClass('icon-itemunfold');
                    var $fold = $(this),
                        $widget = $(this).parent().parent(),
                        $timeArea = $widget.find('.time-area'),
						$addArea = $timeArea.find('.add-area'),
						$tagArea = $timeArea.find('.tag-area');

					if ($(this).hasClass('foldarea')) {
						$tagArea.find('.time-tag').not('.dft').fadeOut('fast');
						$fold.animate({ marginTop: '62px' }, 'fast');
						$widget.animate({ height: '100px' }, 'fast');
						$timeArea.animate({ height: '30px' }, 'fast');
						$addArea.animate({ height: '30px' }, 'fast');
						$tagArea.animate({ height: '30px' }, 'fast');
					} else {
						$addArea.animate({ height: '145px' }, 'fast');
						$tagArea.animate({ height: '145px' }, 'fast');
						$timeArea.animate({ height: '145px' }, 'fast');
						$widget.animate({ height: '215px' }, 'fast');
						$widget.find('.item-fold').animate({ marginTop: '178px' }, 'fast', function () {
							$(this).show();
							$tagArea.find('.time-tag').not('.dft').show();
						});
					}
                });
            }
		},
		_create: function () {
            var ckbox = this._setCheckBox();
			this.options.ckboxSelected.call(ckbox);
			
			var itemInfo = this._setItemInfo();
            var itemOpArea = this._setItemOpArea();
			
            var gameName = this._setGameName();
			this.options.gameNameTxt.call(gameName);
			
            var itemName = this._setItemName();
			this.options.ItemNameTxt.call(itemName);
			
			var itemTime = this._setItemTime();
			this.options.setItemTime.call(itemTime);
			
			var timeDel = this._setTimeDel();
			this._delTime(timeDel, "click");

			var itemAdd = this._setItemAdd();
			this.options.setItemAdd.call(itemAdd);
			this._alterTime(itemAdd, "click");

			this._timeCache(itemInfo.find('.tag-area'));
			
            var itemClose = this._setItemClose();
			this.options.setItemClose.call(itemClose);

            var itemFold = this._setItemFold();
            this.options.setItemFold.call(itemFold);
		},
        _setCheckBox: function (con) {
			var context = this;
            this.item = $(document.createElement("div")).addClass('item-ins').appendTo(this.element);
			this.checkboxCon = $(document.createElement("div")).addClass('checkbox-con').appendTo(this.item);
			this.checkbox = $(document.createElement("input")).attr({ type: 'checkbox' }).addClass('check-item').appendTo(this.checkboxCon);
			this.checkbox.trigger('click');
            return this.checkbox;
        },
		_setItemInfo: function () {
			this.iteminfo = $(document.createElement("div")).addClass('item-info').appendTo(this.item);
			return this.iteminfo;
		},
        _setItemOpArea: function () {
            this.opCon = $(document.createElement("div")).addClass('op-area').appendTo(this.item);
            return this.opCon;
        },
        _setGameName: function () {
			this.itemNameArea = $(document.createElement("div")).addClass('itemname-area').appendTo(this.iteminfo);
			this.itemGameCon = $(document.createElement("div")).addClass('itemgame-con').appendTo(this.itemNameArea);
			this.itemGame = $(document.createElement("span")).addClass('item-game').appendTo(this.itemGameCon);
            return this.itemGame;
        },
        _setItemName: function () {
			this.itemNameCon = $(document.createElement("div")).addClass('itemname-con').appendTo(this.itemNameArea);
			this.itemName = $(document.createElement("span")).addClass('item-name').appendTo(this.itemNameCon);
            return this.itemName;
        },
        _setItemTime: function () {
            this.itemTimeArea = $(document.createElement("div")).addClass('time-area').appendTo(this.iteminfo);
			this.itemTimeTagArea= $(document.createElement("div")).addClass('tag-area').appendTo(this.itemTimeArea);
			return this._addTimeHead(this);
        },
		_setTimeDel: function () {
			return this._addTimeDel(this);
		},
        _setItemAdd: function () {
			this.itemTimeAddArea = $(document.createElement("div")).addClass("add-area").appendTo(this.itemTimeArea);
			this.itemTAdd = $(document.createElement("div")).addClass('tag-con').appendTo(this.itemTimeAddArea);
			this.iconAdd = $(document.createElement("div")).addClass('icon').appendTo(this.itemTAdd);
			this.iconTimeAdd = $(document.createElement("div")).addClass('icon-timeadd').appendTo(this.iconAdd);
            
            return this.itemTAdd;
        },
        _setItemClose: function () {
			this.itemClose = $(document.createElement("div")).addClass('item-close').appendTo(this.opCon);
			this.iconClose = $(document.createElement("div")).addClass('icon').appendTo(this.itemClose);
			this.iconItemClose = $(document.createElement("div")).addClass('icon-itemclose').appendTo(this.iconClose);
            return this.itemClose;
        },
        _setItemFold: function () {
            // this.itemFold = $(document.createElement("div")).addClass("item-fold").appendTo(this.opCon).hide();
			this.itemFold = $(document.createElement("div")).addClass("item-fold foldarea").appendTo(this.opCon);
			this.iconFold = $(document.createElement("div")).addClass('icon').appendTo(this.itemFold);
			this.iconItemFold = $(document.createElement("div")).addClass('icon-itemunfold').appendTo(this.iconFold);
            return this.itemFold;
        },
		_addTimeHead: function (context) {
			context.itemTimeHead = $(document.createElement('div')).addClass('time-tag').appendTo(context.itemTimeTagArea);
			if (context.options.timeCount == 0)
				context.itemTimeHead.addClass('dft');
			context.itemTimeDft = $(document.createElement('input')).attr({ type: 'text' }).val(context.options.timeDft).addClass('datepick').appendTo(context.itemTimeHead);
			context.options.timeCount += 1;
			return context.itemTimeDft;
		},
		_addTimeDel: function (context) {
			context.itemClose = $(document.createElement("div")).addClass('time-close').appendTo(context.itemTimeHead);
			if (context.itemTimeHead.hasClass('dft'))
				context.itemClose.hide();
			context.iconClose = $(document.createElement("div")).addClass('icon').appendTo(context.itemClose);
			context.iconItemClose = $(document.createElement("div")).addClass('icon-timeclose').appendTo(context.iconClose);
			return context.itemClose;
		},
		_alterTime: function (eventSource, eventType) {
			var context = this;
			eventSource.on(eventType, function () {
				context._trigger('addtime');
				if (context.options.timeMax > context.options.timeCount)
					context._addTime();
			});
		},
		_addTime: function () {
			var context = this,
				nTimeSel = context._addTimeHead(context),
				nTimeClose = context._addTimeDel(context);
			context.options.setItemTime.call(nTimeSel);
			context._delTime(nTimeClose, "click");
			return nTimeSel;
		},
		_delTime: function (eventSource, eventType) {
			var context = this;
			eventSource.on(eventType, function () {
				context._trigger('deltime');
				if (!$(this).parent().hasClass("dft")) {
					$(this).parent().fadeOut('fast', function () {
						$(this).remove();
						context.options.timeCount -= 1;
					});
				}
			});
		},
		_timeCache: function (con) {
			for(var i = 0; i < this.options.timeInit - 1; i++) {
				var extraTime = this._addTime();
				console.log(extraTime);
				extraTime.parent().hide();
			}
			for(var j = 0; j < this.options.timeInfo.length; j++) {
				var fixedTime = this.options.timeInfo[j].from + '~' + this.options.timeInfo[j].to;
				con.children().eq(j).find('input').val(fixedTime);
			}
		},
		destroy: function () {
            this.item.remove();
            $.Widget.prototype.destroy.apply(this, arguments);
		}
	});
})(jQuery);
