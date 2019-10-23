/**
 * Suggested ComboBox (jQuery extension)
 * require jQuery
 *
 * @author nemo@2010-10-19
 */
(function($) {
$.fn.extend({
suggested : function(data, name, error, cssExtend, userable, fn) {
	if (data == undefined) return this;
	if ($(this).length > 1) {
		$(this).each(function() {
			$(this).suggested(data, name);
		});
		return;
	}
	// like const
	var hoverStyle = {
		color:'#000',
		backgroundColor:'#ddd'
	};
	var defaultStyle = {
		color:'#000',
		backgroundColor:'#FFF',
        lineHeight: "26px",
        paddingLeft: "5px",
        whiteSpace: "nowrap"
	};
	var defaultText = '请输入您要查看的域名';

	error = Boolean(error);

	$(this).cancelSuggest();

	var objThis = $(this);

	objThis.attr('autocomplete', 'off');

	if (typeof data == 'string') {
		data = eval(data);
	}
	
	// add ComboBox
	var comboBox = document.createElement('div');
	comboBox.className = 'suggested';
    $(this).get(0).comboBox = $(comboBox);

    // add hidden value
    var hidValue = document.createElement("input");
    hidValue.type = "hidden";
    hidValue.name = name;
    $(this).after(hidValue);

    // add if can use undefined value
    $(this).attr("userable", userable || false);

	if ($(this).css('width') == 'auto'
			|| $(this).css('width') == undefined) {
		$(this).css('width', '150px');
	}
	
    var cssOptions = {
			border:'1px solid #c0c2c6',
			display: 'none',
			position:'absolute',
	        //top: objThis.offset().top + objThis.height() + 6,
	        //bottom: objThis.offset().top,
	        left: objThis.offset().left,
			zIndex:'500',
			width: objThis.width() - 4,
			padding: '0px',
			backgroundColor: '#FFF',
			cursor: 'default',
	        maxHeight: "250px",
	        overflowX: "hidden",
	        overflowY: "auto"
		};
    var winHeight = $(window).height();
    if ( 250 + objThis.offset().top > winHeight) {
        cssOptions = $.extend(cssOptions, {
                bottom: winHeight - objThis.offset().top
        });
    } else {
        cssOptions = $.extend(cssOptions, { 
                top: objThis.offset().top + objThis.height() + 6
        });
    }
	//cssOptions = $.extend(cssOptions, cssExtend || {} );
	$(comboBox).css(cssOptions);

	$(comboBox).append('<input type="hidden" class="hidSuggested" name="' + name + '" />');
	
	var err;
	if (error == true) {
		err = document.createElement('span');
		$(err).css({
			marginLeft: '2px'
		});
		$(this).after(err);
		$(err).html('&nbsp;&nbsp;&nbsp;');
	}

	$(document.body).append(comboBox);

	if (data.length > 0) {
		$(this).val(defaultText);
	}

	$.each(data, function(i, item) {
		var di = document.createElement('div');
		$(di).css(defaultStyle);
		if (typeof item == 'object') {
			$(di).attr('value', item['value']);
			$(di).attr('title',item['name']);
			$(di).text(item['name']);
            if(item['selected']) {
                $(di).addClass("data-selected-dft");
            }
		} else {
			var v = item + '';
			$(di).attr('value', v);
			$(di).attr('title',v);
			$(di).text(v);
		}
		$(comboBox).append(di);

		// add event
		$(di).mouseover(function() {
			$(comboBox).find('.selected').removeClass('selected').css(defaultStyle);
			$(di).addClass('selected').css(hoverStyle);
		});
		$(di).mouseout(function() {
			$(di).removeClass('selected').css(defaultStyle);
		});
		$(di).mousedown(function(e) {
			objThis.val($(this).text())
			       .attr("title",$(this).text());
            $(comboBox).find(".hidSuggested").val($(this).attr('value'));
			$(hidValue).val($(this).attr('value'));
            comboBox.style.display = "none";
		});
	});

    // hide comboBox
    $(document.body).click(function() {
        comboBox.style.display = "none";
    });

	// bind event
	$(this).click(function() {
		$(this).keypress();
		return false;
	});
	$(this).focus(function() {
		$(this).val('');
	});
	$(this).blur(function() {
		var text = $(this).val();
		if (text != '') {
            if ($(this).isValid()==false) {
                $(hidValue).val(text);
            } else {
                $(comboBox).find('div:visible:first').trigger("mousedown");
            }
		}
		//comboBox.style.display = 'none';
		if (error) {
			if ($(this).isValid() == false) {
				$(err).text('No.').css({
					backgroundColor: '#F00',
					color: '#FFF'
				});
			} else {
				$(err).text('Ok!').css({
					backgroundColor: '#0F0',
					color: '#000'
				});
			}
		}
	});

	$(this).focus(function(o) {
		var _this = $(this);
		var itv = window.setInterval(function() {

			var combo = $(comboBox); // objThis.next('.suggested:first');
			combo.find('div').find('span.suggested-highlight').each(function() {
				$(this).after($(this).text()).remove();});

			$(comboBox).find('.selected:not(:visible)').removeClass('selected').css(defaultStyle);
			comboBox.style.display = 'inline';
			var text = _this.val();

			if (error) {
				if (_this.isValid() == false) {
					$(err).text('No.').css({
						backgroundColor: '#F00',
						color: '#FFF'
					});
				} else {
					$(err).text('Ok!').css({
						backgroundColor: '#0F0',
						color: '#000'
					});
				}
			}

			if (text == '') {
				combo.find('div').show();
				return;
			}

			combo.find('div').each(function() {
				if ($(this).text().indexOf(text) == -1) {
					$(this).hide();
				} else {
					$(this).show().html($(this).text().
							replace(text, '<span style="color: #00F; font-weight: bold;" class="suggested-highlight" >' + text + '</span>'));
				}
			});
		}, 300);
		$(this).blur(function() {
			window.clearInterval(itv);
		});
	});

	$(this).keydown(function(evt) {
		var code = evt.keyCode ? evt.keyCode : evt.which;
		if (code == 13 || code == 9) { // Enter
            if(!fn) $(this).blur();
            window.setTimeout(function() {
                comboBox.style.display = "none";
            }, 1);
			if ($(comboBox).find('.selected').length == 0) {
				$(comboBox).find('div:visible:first').mousedown();
			} else {
				$(comboBox).find('.selected').mousedown();
			}
			return false;
		} else if (code == 38) { // up
			$(comboBox).find('.selected:not(:visible)').removeClass('selected');
			if ($(comboBox).find('.selected:visible').length != 1) {
				$(comboBox).find('div:visible:last').addClass('selected');
			} else {
				if ($(comboBox).find('.selected').siblings(':visible').not($(comboBox).find('.selected ~ div')).last().length == 0) {
					$(comboBox).find('.selected').removeClass('selected');
					$(comboBox).find('div:visible:last').addClass('selected');
				} else {
					$(comboBox).find('.selected').siblings(':visible').not($(comboBox).find('.selected ~ div')).last().addClass('selected');
					$(comboBox).find('.selected:last').removeClass('selected');
				}
			}
			$(comboBox).find('div').css(defaultStyle);
			$(comboBox).find('.selected').css(hoverStyle);
		} else if (code == 40) { // down
			$(comboBox).find('.selected:not(:visible)').removeClass('selected');
			if ($(comboBox).find('.selected:visible').length != 1) {
				$(comboBox).find('div:visible:first').addClass('selected');
			} else {
				if ($(comboBox).find('.selected ~ :visible:first').length == 0) {
					$(comboBox).find('.selected').removeClass('selected');
					$(comboBox).find('div:visible:first').addClass('selected');
				} else {
					$(comboBox).find('.selected ~ :visible:first').addClass('selected');
					$(comboBox).find('.selected:first').removeClass('selected');
				}
			}
			$(comboBox).find('div').css(defaultStyle);
			$(comboBox).find('.selected').css(hoverStyle);
		}
	});

    $(this).keyup(function(evt){
		var code = evt.keyCode ? evt.keyCode : evt.which;
        if(fn) {
            fn(evt);
            if (code == 13 || code == 9) { // Enter
                $(this).blur();
            }
        }
    });

    $(comboBox).find(".data-selected-dft").mousedown();
	return this;
},
isValid : function() {

	if ($(this).length > 1) {
		var right = false;
		var wrong = false;
		$(this).each(function() {
			var text = $(this).val();
			var valid = false;
			$(this).get(0).comboBox.find('div').each(function() {
				if ($(this).text() == text) {
					valid = true;
				}
			});

			if (valid) {
				right = true;
			} else {
				wrong = true;
			}
		});
		return !wrong;
	} else {
		var text = $(this).val();
		var valid = false;
		$(this).get(0).comboBox.find('div').each(function() {
			if ($(this).text() == text) {
				valid = true;
			}
		});
		return valid;
	}
},
getValue : function() {
	if (!$(this).isValid()) {
		return undefined;
	}
	return $(this).get(0).comboBox.find('.hidSuggested').val();
},
cancelSuggest : function() {
	$(this).val('');
	var sug = $(this).next('.suggested:first');
	sug.next('span').remove();
	sug.remove();
},
loadingSuggest : function() {
	$(this).suggested(new Array('loading...'));
}
})
})(jQuery);
