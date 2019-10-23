var cnt = 0;
(function($){
$.extend({
Tools : {
    Language: {
        chinese: {
            title: lang.t("请选择：")
        },
        english: {
            title: "select:"
        }
    },
    SelectType : {
    	COMMON : 1,
    	PAGE : 2
    },
    /**
     * type : 1全选  2单选
     * setNum :可选择的数量
     * search : 搜索功能
     * seleted : 显示已选选项
     */
    sel : function(arr, $obj, opts){
    	var options = {
        	title : "",
            language: "chinese",
        	selectType : $.Tools.SelectType.COMMON,
        	arr : arr ? arr : [],
        	obj : $obj ? $obj : $("body"),
        	setNum : 5,
        	type : 1,
        	search : false,
        	selected : {}
        };
        $.extend(options, opts);
        options = $.extend($.Tools.Language[options["language"]], options);

        switch (options.selectType){
    		case $.Tools.SelectType.COMMON :
                new $.Tools.CommonSelectFactory(options);
                break;
            case $.Tools.SelectType.PAGE :
                new $.Tools.PageSelectFactory(options);
                break;
            default :
            	new $.Tools.CommonSelectFactory(options);
            	break;
        }
    },
    PageSelectFactory : function(options){
    	var $selCon = $(document.createElement("div")).addClass("select-m"),
    		$titleCon = $(document.createElement("div"))
    					.addClass("title-m"),
    		$optCon = $(document.createElement("div"))
    					.addClass("options-m"),
    					//.css({'display' : 'none'}),
    		$optUl = $(document.createElement("ul")).addClass("subview-con clearfix"),
    		$title = $(document.createElement("span"))
    				.addClass("t-name")
    				.text(options.title)
    				.attr("data-id",'')
    				.appendTo($titleCon);
    	var $search = $(document.createElement("div")).addClass("subview-search")
    				.html('<span class="search-con">'
    						+ '<input type="text" class="srh-txt">'
    						+ '<i class="search-tag">&nbsp;</i>'
    						+ '</span>');
    	var html = '';

    	$.each(options.arr, function(){
    		html += '<li class="subview-li">'
    			+ '<h3 class="subview-hd">' + this.hd + '</h3>';

    		$.each( this.data, function(i){
    			if(i == 0){
    				html += '<h4 class="subview-title first">';
    			}else{
    				html += '<h4 class="subview-title">';
    			}
    			html += this.title + '</h4>'
    				+ '<ul class="subview-cat clearfix">';
    			$.each( this.data, function(){
    				html += '<li class="sub-cat-li">'
    					+ '<a class="sub-li-a" data-id="' + this.id + '">' + this.name
    					+ '</a></li>';
    			});
    			html += '</ul>';
    		});
    		html +=
    			 '</li>';
    	});
    	$optCon.append($search);
    	$optUl.html(html).appendTo($optCon);
    	$titleCon.add($optCon).appendTo($selCon);

    	$selCon.appendTo(options.obj);
    },
    CommonSelectFactory : function(options){
    	var $selCon = $(document.createElement("div")).addClass("select-m"),
    		$titleCon = $(document.createElement("div"))
    					.addClass("title-m"),
    		$optCon = $(document.createElement("div"))
    					.addClass("options-m")
    					.css({'display' : 'none'}),
    		$optUl = $(document.createElement("ul")),
    		$title = $(document.createElement("span"))
    				.addClass("t-name")
    				.text(options.title)
    				.attr("data-id",'')
    				.appendTo($titleCon);

    	var html = '';

    	$.each(options.arr, function(){
    		html += '<li class="opt-m ';
    		if($.Tools._in_ids(this.id, options.selected.id)){
    			$title.attr('data-id',this.id)
					.attr("data-child", this.child)
					.attr("title", this.value)
					.text(this.value);
				$selCon.addClass('selected-m');
    			html += ' selected-opt';
    		}
    		html += '" data-id="' + this.id
    				+'" data-child="' + this.child
    				+'" dimension="' + this.dimension
    				+'">'
    				+ '<span class="opt-v" title="'+ this.value +'">' + this.value + '</span>';
    		if(this.child){
    			html += '<span class="dir"></span>';
    		}
    		html += '</li>';
    	});
    	$optUl.html(html).appendTo($optCon);
    	$titleCon.add($optCon).appendTo($selCon);
    	/*show children if selected opt*/
    	if(options.selected.child){
			var $selectedOpt = $optUl.find(".selected-opt");
			var fn = function(data){
				options.setSeled = true;
				$selectedOpt.data("setting", data);
				$.Tools._show_child(options.obj.parent(), data, options);
			}

			if(options.getSettingFn)options.getSettingFn($selectedOpt.attr("dimension"), fn);
		}

    	/* search */
    	if(options.search){
    		$.Tools._search($optUl);
    	}

    	/* show options */
    	$titleCon.click(function(e){
    		e.stopPropagation();
    		if($optCon.is(":visible")){
				$optCon.hide();
			}else{
				$("body").find(".select-m .options-m").hide();
				$optCon.find(".srh-txt").val('').keyup();
				$optCon.show();
			}
    	});
    	/* select click*/
    	$optUl.find("li").click(function(e){
    		e.stopPropagation();
			var $self = $(this),
			    titleId = $title.attr("data-id"),
			    curId = $self.attr("data-id"),
			    curText = $self.find(".opt-v").text();

			if(titleId != curId){
				var $parent = options.obj.parent();

				$optUl.find(".selected-opt").removeClass("selected-opt");
				$title.attr('data-id',curId)
					.attr("data-child", $self.attr("data-child"))
					.attr("title", curText)
					.text(curText)
					.parent().parent().addClass('selected-m');
				$self.addClass("selected-opt");
				/* show title*/
				if(options.showTitleFn)options.showTitleFn();
				/* callback*/
				if(options.callback)options.callback(curId);
				/* show child*/
				$parent.find(".sel-ul").remove();
				if(options.childShow && $self.attr("data-child") == "true"){
					var setting = $self.data("setting");

					if(!setting){
						var fn = function(data){
							$self.data("setting", data);
							options.setSeled = false;
							$.Tools._show_child($parent, data, options);
							if(options.showTitleFn)options.showTitleFn();
						}

						if(options.getSettingFn)options.getSettingFn($self.attr("dimension"), fn);
					}else{
						$.Tools._show_child($parent, setting, options);
					}
				}
			}
			if(options.existFn)options.existFn(options.obj, $title, curId);
			$optCon.hide();
    	});

    	/* select keyEvent*/
    	$selCon.attr("tabindex","0").bind("keydown",function(e){
			var keyCode = e.keyCode,
				$self = $(this),
				$title = $self.find(".title-m").find(".t-name");

    		switch(e.keyCode){
				case 13:
			        e.preventDefault();
			        /* show child*/
			        var $parent = $self.parent().parent(),
			        	$curSel = $self.find(".selected-opt");

					$parent.find(".sel-ul").remove();
					if(options.childShow && $curSel.attr("data-child") == "true"){
						var setting = $curSel.data("setting");

						if(!setting){
							var fn = function(data){
								$curSel.data("setting", data);
								$.Tools._show_child($parent, data, options);
							}

							if(options.getSettingFn)options.getSettingFn($curSel.attr("dimension"), fn);
						}else{
							$.Tools._show_child($parent, setting, options);
						}
					}

                    $optCon.hide();
                    break;
                case 38:
                	e.preventDefault();
                	var $selected = $self.find(".selected-opt"),
                		$curSel = $();

                	if($selected.is(":visible")){
                		if($selected.prev().is(":visible")){
                			$curSel = $selected.prev();
                		}else{
                			$curSel = $selected.prevAll(".opt-m:visible").first();
                		}
                	}
                	if($curSel.length == 0){
                		return;
                	}
                	//scroll
                	var curTop = $curSel.length > 0 ? $curSel.position().top : 0,
                		$parent = $curSel.parent();

                	if($curSel &&  curTop < $curSel.height() ){
                		var scrollTop = $parent.scrollTop();

                		scrollTop += curTop - $selected.position().top;
                		$parent.scrollTop( scrollTop );
                	}
                	//selected
                	if($curSel && $curSel.hasClass("opt-m")){
                		$title.attr('data-id',$curSel.attr("data-id"))
							.attr("data-child", $curSel.attr("data-child"))
							.text($curSel.text())
							.parent().parent().addClass('selected-m');

						$selected.removeClass("selected-opt");
						$curSel.addClass("selected-opt");
                	}
                	break;
            	case 40:
                	e.preventDefault();
                	var $selected = $self.find(".selected-opt"),
                		$curSel = $();

                	if($selected.is(":visible")){
                		if($selected.next().is(":visible")){
                			$curSel = $selected.next();
                		}else{
                			$curSel = $selected.nextAll(".opt-m:visible").first();
                		}
                	}else{
                		$curSel = $self.find(".opt-m:visible").first();
                	}
                	if($curSel.length == 0){
                		return;
                	}
                	//scroll
                	var curTop = $curSel.length > 0 ? $curSel.position().top : 0,
                		$parent = $curSel.parent();

                	if($curSel &&  curTop > $parent.height() - $curSel.height() ){
                		var scrollTop = $parent.scrollTop();

                		scrollTop += curTop - $selected.position().top;
                		$parent.scrollTop( scrollTop );
                	}

                	//selected
                	if($curSel && $curSel.hasClass("opt-m")){
                		$title.attr('data-id',$curSel.attr("data-id"))
							.attr("data-child", $curSel.attr("data-child"))
							.text($curSel.text())
							.parent().parent().addClass('selected-m');

						$selected.removeClass("selected-opt");
						$curSel.addClass("selected-opt");
                	}
            		break;
			}
    	});

    	$selCon.appendTo(options.obj);

    	$("body").click(function(){
    		if($optCon.is(":visible")){
    			$optCon.hide();
    		}
    	});
    },
    _search : function($obj){
    	var $searchCon = $(document.createElement("div"))
						.addClass("sel-search")
						.html('<span class="search-con">'
								+'<input type="text" class="srh-txt" />'
								+'<i class="search-tag">&nbsp;</i></span>');

		$obj.css({'border-top':'1px solid #bbb'})
			.before($searchCon);
		$searchCon.find(".search-tag").click(function(e){
			e.stopPropagation();
			return;
		});
		$searchCon.find(".srh-txt").click(function(e){
			e.stopPropagation();
		}).keyup(function(){
			var val = $(this).val(),
				reg = new RegExp(val);

			$obj.find("li span.opt-v").each(function(){
				var $self = $(this),
					text = $self.text();

				if(reg.test(text)){
    				var newText = text.replace(reg, '<font style="color: #261CDC; font-weight: bold; ">'+ val +'</font>');

					$self.html(newText);
					$self.parent().removeClass("hide-li");
				}else{
					$self.parent().addClass("hide-li");
				}
			});
		}).keydown(function(e){
			var keyCode = e.keyCode;
			$obj.parent().parent().keydown();
		});
    },
    _show_child : function($obj, setting, opts){
		switch(parseInt(opts.type, 10)){
			case 1:
				$.Tools._mul_child($obj, setting, opts);
				break;
			case 2:
				$.Tools._radio_child($obj, setting, opts);
				break;
			default:
				break;
		}
   	},
	_mul_child : function($obj, setting, opts){
		var html = '<ul class="sel-ul">',
    		setLen = setting.length;

    	if(!opts.setSeled){
    		$.each(setting, function(i){
				if(i < opts.setNum){
					html += '<li class="sel-li act-li"><span class="child-m" data-id="'+ this.id +'">'
						+ this.value
						+ '</span></li>';
				}else{
					html += '<li class="hide-li dis-li"><span class="child-m" data-id="'+ this.id +'">'
						+ this.value
						+ '</span></li>';
				}
			});
			if(setLen > opts.setNum){
				html += '<li class="sel-li sel-more"><a class="more" href="javascript:void(0);">more</a></li>';
			}
    	}else{
    		$.each(setting, function(i){
    			html += '<li class="sel-li ';
    			if($.Tools._in_array(opts.selected.setting,this.id)){
    				html += 'act-li';
    			}else{
    				html += 'dis-li';
    			}
    			html += '"><span class="child-m" data-id="'+ this.id +'">'
						+ this.value
						+ '</span></li>';
    		});
    	}

		html += '</ul>';
		$(html).appendTo($obj);

		$obj.find(".child-m").click(function(){
			var $self = $(this),
				$pLi = $self.parent(),
				len = $obj.find(".act-li").length;

			if(len < opts.setNum){
				if($pLi.hasClass("dis-li")){
					$pLi.removeClass("dis-li").addClass("act-li");
				}else if(len > 1){
					$pLi.removeClass("act-li").addClass("dis-li");
				}
			}else{
				if($pLi.hasClass("dis-li")){
					$obj.find(".act-li:first").removeClass("act-li").addClass("dis-li");
					$pLi.removeClass("dis-li").addClass("act-li");
				}else{
					$pLi.removeClass("act-li").addClass("dis-li");
				}
			}
			if(opts.showTitleFn)opts.showTitleFn();
		});
		$obj.find(".more").toggle(
			function(e){
				e.stopPropagation();
				$obj.find(".hide-li").addClass("sel-li").removeClass("hide-li");
				$(this).text("pack");
			},
			function(e){
				e.stopPropagation();
				$obj.find(".sel-li").not(".act-li").not(".sel-more").removeClass("sel-li").addClass("hide-li");
				$(this).text("more");
			}
		);
   	},
   	_radio_child : function($obj, setting, opts){
   		var html = '<ul class="sel-ul">',
    		setLen = setting.length;

   		$.each(setting, function(i){
			if(opts.setSeled){
				html += '<li class="sel-li ';
				if($.Tools._in_array(opts.selected.setting, this.id)){
					html += 'act-li';
				}else{
					html += 'dis-li';
				}

				html += '"><span class="child-m" data-id="'+ this.id +'">'
						+ this.value
						+ '</span></li>';
			}else{
				if(i == 0){
					html += '<li class="sel-li act-li"><span class="child-m" data-id="'+ this.id +'">'
						+ this.value
						+ '</span></li>';
				}else if(i < opts.setNum){
					html += '<li class="sel-li dis-li"><span class="child-m" data-id="'+ this.id +'">'
						+ this.value
						+ '</span></li>';
				}else{
					html += '<li class="hide-li dis-li"><span class="child-m" data-id="'+ this.id +'">'
						+ this.value
						+ '</span></li>';
				}
			}
		});
		if(setLen > opts.setNum){
			html += '<li class="sel-li sel-more"><a class="more" href="javascript:void(0);">more</a></li>';
		}
		html += '</ul>';
		$(html).appendTo($obj);

		$obj.find(".child-m").click(function(){
			var $self = $(this),
				$pLi = $self.parent();

			if($pLi.hasClass("dis-li")){
				$obj.find(".act-li").removeClass("act-li").addClass("dis-li");;
				$pLi.removeClass("dis-li").addClass("act-li");
			}
			if(opts.showTitleFn)opts.showTitleFn();
		});

		$obj.find(".more").toggle(
			function(e){
				e.stopPropagation();
				$obj.find(".hide-li").addClass("sel-li").removeClass("hide-li");
				$(this).text("pack");
			},
			function(e){
				e.stopPropagation();
				$obj.find(".sel-li").not(".act-li").not(".sel-more").removeClass("sel-li").addClass("hide-li");
				$(this).text("more");
			}
		);
   	},
	_in_array : function(arr, val){
    	for(var i = 0; i < arr.length; i++){
    		if(arr[i] == val){
    			return true;
    		}

    	}
    	return false;
	},
   	_overlayer : function(options){
		cnt++;
		var opts = {
			text : lang.t("加载中...")
		};
		$.extend(opts, options);
		var $layer = $("#J_layer"),
			$notice = $("#J_notice"),
			$body = $("body");

		if($layer.length == 0){
			$layer = $(document.createElement("div")).addClass("layer")
					.attr("id", "J_layer")
					.appendTo($body);
		}else{
			$layer.show();
		}
		if($notice.length == 0){
			$notice = $(document.createElement("div")).addClass("notice")
					.attr("id", "J_notice")
					.text(opts.text)
					.css({
						"left" : $.Tools._get_left($notice) + 'px',
						"top" : 0 //$("body").scrollTop() + 'px'
					})
					.appendTo($body);
		}else{
			$notice.css({
				"left" : $.Tools._get_left($notice) + 'px',
				"top" : 0 //$("body").scrollTop() + 'px'
			}).show();
		}
	},
	_hidelayer : function(text){
		cnt--;
		if(cnt == 0){
			var text = text ? text : '',
				$notice = $("#J_notice");
			$notice.text(text);
			$("#J_layer").add($notice).hide();
		}
	},
	_get_left : function($obj, $con){
		$con = $con ? $con : $(window);
		var windowW = $(window).width(),
			w = $obj.width() ? $obj.width() : 130;

		if(windowW - w >0){
			return (windowW - w)/2;
		}else{
			return 0;
		}
	},
	_in_ids : function(ids, id){
		var aIds = ids.split(',');

		if($.Tools._in_array(aIds, id)){
			return true;
		}else{
			return false;
		}
	}
}

});
})(jQuery);


