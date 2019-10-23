/**
 * data : [{
 * 		title : "",
 * 		selected : true/ false,
 * 		attr : { id : "", child : true/ false, cid : (get children's id), otherattr : ""},
 * 		children : [] //if length = 0 and child = true then getChild()
 * }]
 * obj : which is the container of $choose
 * title : if you don't set the title ,default is '请选择'
 * page : 1-> data[{}] 2-> data[{ children : [{}] }] 3->data[{ children : [{ children : [{}] }] }]
 * type : 1 : common 2: page 3 : radio 4 : multi
 * selected : selected info []
 * mulRadio : 1 : radio 2 : mul
 * setNum : the num of children which you can selected at one time
 * dftNum: the default selected number
 * isRecord :(true/false) whether record the number of selected
 * recordClass : label the select which need to be recorded
 */
var cnt = 0;
(function () { if( jQuery && jQuery.choose ) { return; }
(function ($) {
$.choose = {
	_type : {
		COMMONSELECT : 1,
		PAGESELECT : 2,
		RADIOSELECT : 3,
		MULTISELECT : 4
	},
	core : function( options ) {
        var defaults = {
            type : 1,
            mulRadio : 1,
            data : [],
            page : 2,
            search : false,
            obj : $("body"),
            title : lang.t("请选择："),
            selected : [],
            dftNum : 5,
            setNum : 15,
            isRecord : false,
            showBottom: false,
            recordClass : "record",
            recordNum : 1000
        };
        options = $.extend( defaults, options );
        options.type = parseInt( options.type, 10 );

        switch( options.type ) {
            case 1:
                this._common_factory(options);
                break;
            case 2:
                this._page_factory(options);
                break;
            case 3:
                this._multi_factory(options);
                break;
            case 5:
                this._checkboxFactory(options);
                break;
            default :
                break;
        }
	},
	_common_factory : function( options ) {
		var _this = this,
            curClass = 'common-choose';
        if( options.isRecord ){ curClass += ' ' + options.recordClass; }
	    var $selCon = _this._basic_fac( options.title, curClass ),
			$optCon = $selCon.find('.options-m'),
			$titleCon = $selCon.find('.title-m'),
			$title = $titleCon.find('.t-name'),
			html = '';

    	if( options.search ){ this._common_search( $optCon ); }
		html = this._common_option_fac( options.data, options.selected );
    	$optCon.append( $(html) );
    	$selCon.appendTo( options.obj );
    	/* select click*/
    	$optCon.find(".opt-m").click(function(e){
    		e.stopPropagation();
			var $self = $(this),
			    titleId = $title.attr("data-id"),
			    curId = $self.attr("data-id");

            // record
            if( $selCon.hasClass( options.recordClass )){
                var recordNum = $("body").data(options.recordClass);

                recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
                if( $selCon.hasClass('recorded') ){
                    //清除之前的次级指标的个数
                    var $selUl = options.obj.closest('.sel-con').find(".sel-ul");

                    if( $selUl.length ){
                        recordNum -= ($selUl.find(".act-li").length - 1);
                    }
                } else {
                    if( options.recordNum <= recordNum ){
                        say(lang.t("指标选项个数不能超过{1}个~。~", options.recordNum));
                        return;
                    }
                    $selCon.addClass('recorded');
                    recordNum = recordNum ? ( parseInt( recordNum, 10 ) + 1 ) : 1;
                }
                $("body").data(options.recordClass, recordNum);
            }
			_this._handle_selected_event( $title, $self );
			/* show title */
			if( options.showTitleFn )options.showTitleFn();
			/* show child */
			if( $self.attr('data-child') == 'true' ){
				_this._get_child( $self, options );
			}else{
				$self.closest('.select-m').parent().parent().find('.sel-ul').remove();
			}
			if( options.callback )options.callback( options.obj, $title );
			$optCon.hide();
    	});
    	$optCon.find('.opt-m.selected-opt').click();

    	/* select keyEvent*/
    	$selCon.attr( "tabindex", "0" ).bind("keydown",function(e){
    		var keyCode = e.keyCode,
				$self = $(this),
				$title = $self.find(".title-m").find(".t-name");

			switch( keyCode ){
				case 13 : //enter
			        e.preventDefault();
			        var $selLi = $self.find('.options-m').find('.selected-opt');

			        if( $selLi.length != 0 ){ $selLi.click(); }
			    	break;
                case 38 : //up
                	e.preventDefault();
                	var $selected = $self.find(".selected-opt"),
                		$curSel = $();

                	if( $selected.is(":visible") ){
                		if( $selected.prev().is(":visible") ){
                			$curSel = $selected.prev();
                		}else{
                			$curSel = $selected.prevAll(".opt-m:visible").first();
                		}
                	}
                	if( $curSel.length == 0 ){ return; }
			    	//scroll
			    	var curTop = $curSel.length > 0 ? $curSel.position().top : 0,
			    		$parent = $curSel.parent();

			    	if( $curSel &&  curTop < $curSel.height() ){
			    		var scrollTop = $parent.scrollTop();

			    		scrollTop += curTop - $selected.position().top;
			    		$parent.scrollTop( scrollTop );
			    	}

			    	//selected
			    	if( $curSel && $curSel.hasClass("opt-m") ){
			    		$.choose._handle_selected_event( $title, $curSel );
			    	}
                	break;
            	case 40 : //down
                	e.preventDefault();
                	var $selected = $self.find(".selected-opt"),
                		$curSel = $();

                	if( $selected.is(":visible") ){
                		if( $selected.next().is(":visible") ){
                			$curSel = $selected.next();
                		}else{
                			$curSel = $selected.nextAll(".opt-m:visible").first();
                		}
                	}else{
                		$curSel = $self.find(".opt-m:visible").first();
                	}
                	if( $curSel.length == 0 ){ return; }
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
                		$.choose._handle_selected_event( $title, $curSel );
                	}
                	break;
                default :
                	break;
			}
    	});
	},
	_get_child : function( $obj, options ){ //obj -> selected-opt
		var children = $obj.data('children');
		if( children ){
			$.choose._show_child( $obj, children, options );
		}else{
            if($obj.data("data")){
                var data = $obj.data("data");
                $obj.data('children', data);
                $.choose._show_child( $obj, data, options );
            } else {
                var fn = function( data ){
                    $obj.data( 'children', data );
                    $.choose._show_child( $obj, data, options );
                };
                if( options.getData ) options.getData( $obj.attr("data-cid"), fn );
            }
		}
	},
	_show_child : function( $obj, data, opts ){
		switch( parseInt( opts.mulRadio, 10 ) ){
			case 1 :
				$.choose._mul_child( $obj, data, opts );
				break;
			case 2 :
				$.choose._radio_child( $obj, data, opts );
				break;
            case 3 :
                $.choose._click_child( $obj.closest('.selected-m'), data, opts );
			default :
				break;
		}
	},
    //$selectedM当前选中的父元素
    _click_child : function( $selectedM, data, opts ) {
        var $con = $selectedM.parent(),
            $ul = $(document.createElement("ul")).addClass('sel-ul'),
            html = '';

		$con.find('.sel-ul').remove();
		$.each( data, function(i){
			html += '<li class="sel-li ';
			if( this.selected ){
                html += 'act-li';
            }
			html += '" ';
			$.each( this.attr, function( k, v ){
				html += 'data-' + k + '="' + v + '"';
			});
			html +='><span class="child-m" ' + '>'
				+ this.title
				+ '</span></li>';
		});

		$con.append( $ul.html( html ) );
        $ul.find(".sel-li").click(function(e){
            e.stopPropagation();
            if( opts.clickChildFn )opts.clickChildFn( $(this), $selectedM );
        });
    },
	_mul_child : function( $obj, data, opts ){
		var $body = $("body"),
            $ul = $(document.createElement("ul")).addClass('sel-ul'),
			html = '',
            $selectedM = $obj.closest('.selected-m'),
			$con = $selectedM.closest('.sel-p').parent();

		$con.find('.sel-ul').remove();
		$.each( data, function(i){
			html += '<li class="sel-li ';
			if( this.selected ){
                html += 'act-li';    // act-li 是控制选中的绿色
            }
			html += '" ';
			$.each( this.attr, function( k, v ){
				html += 'data-' + k + '="' + v + '"';
			});
			html +='><span class="child-m" ' + '>'
				+ this.title
				+ '</span></li>';
		});

		$ul.html( html );
		//default selected 'act-li'
        var dftNum = 1;

        if( opts.isRecord && $selectedM.hasClass( opts.recordClass ) ){
            var recordNum = $body.data(opts.recordClass);
            recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
            var diff = opts.recordNum - ( recordNum - 1 ),
                dftNum = diff - opts.dftNum > 0 ? opts.dftNum : diff;
        }
        if( $ul.find('.act-li').length == 0 ){
			$ul.find('.sel-li:lt(' + dftNum + ')').addClass('act-li');
		}

        /* init record */
        if( opts.isRecord && $selectedM.hasClass( opts.recordClass ) ){
           var actLiLen = $ul.find('.act-li').length;

            actLiLen = actLiLen ? actLiLen : 1;
            recordNum += actLiLen - 1;
            $body.data(opts.recordClass, recordNum);
        }
		//show setNum
		var $sAct = $ul.find(".act-li"),
			sActLen = $sAct.length,
            $sSel = $ul.find(".sel-li").not(".sel-tools"),
            sSelLen = $sSel.length;

        if( sSelLen > opts.setNum ){
			if( sActLen > opts.setNum ){
                $sAct.filter(':gt(' + ( opts.setNum - 1 ) + ')').addClass("hide-li");
            } else {
                $sSel.not(".act-li").filter(":gt(" + ( opts.setNum - sActLen - 1 ) + ")").addClass("hide-li");
            }
            //more
            $ul.append( $(document.createElement('li'))
				.addClass('sel-li sel-tools')
				.html('<a class="more" href="javascript:void(0);">more</a>'));
        }
		//select all
        $ul.append( $(document.createElement('li'))
            .addClass('sel-li sel-tools')
            .html('<a class="sel-all" href="javascript:void(0);">all</a>'));

		$ul.appendTo( $con );
		$.choose._more_event( $ul, opts );
        $.choose._all_event( $ul, opts );
        /* show title */
        if( opts.showTitleFn )opts.showTitleFn();
		$ul.find(".sel-li").click(function(e){
            e.stopPropagation();
			var $self = $(this),
				len = $ul.find(".act-li").length;

            /* change record */
            if( opts.isRecord && $selectedM.hasClass( opts.recordClass ) ){
                var recordNum = $body.data(opts.recordClass);

                recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
                if( !$self.hasClass("act-li") ){
                    if( opts.recordNum - recordNum <= 0 ){
                        say(lang.t("指标选项不能超过{1}个~.~", opts.recordNum));
                        return;
                    }
                    recordNum += 1;
                } else if( len > 1 ) {
                    recordNum -= 1;
                }
                $body.data( opts.recordClass, recordNum );
            }
            if( !$self.hasClass("act-li") ){
                $self.addClass("act-li");
            }else if( len > 1 ){
                $self.removeClass("act-li");
            }
            /* show title*/
			if( opts.showTitleFn )opts.showTitleFn();
            if(opts.childFn) opts.childFn($(this));
		});
	},
	_multi_factory : function( opts ){
		var $ul = $(document.createElement("ul")).addClass('sel-ul'),
			html = '';

		$.each( opts.data, function(i){
			html += '<li class="sel-li ';
			if( this.selected || $.tools._in_array( opts.sel, this.attr.id ) ){
                html += 'act-li';
            }
			html += '" ';
			$.each( this.attr, function( k, v ){
				html += 'data-' + k + '="' + v + '"';
			});
			html +='><span class="child-m" ' + '>'
				+ this.title
				+ '</span></li>';
		});

		$ul.html( html );
		//default selected 'act-li'
        var dftNum = opts.dftNum;
        if( $ul.find('.act-li').length == 0 ){
			$ul.find('.sel-li:lt(' + dftNum + ')').addClass('act-li');
		}
		//show setNum
		var $sAct = $ul.find(".act-li"),
			sActLen = $sAct.length,
            $sSel = $ul.find(".sel-li").not(".sel-tools"),
            sSelLen = $sSel.length;

        if( sSelLen > opts.setNum ){
			if( sActLen > opts.setNum ){
                $sAct.filter(':gt(' + ( opts.setNum - 1 ) + ')').addClass("hide-li");
            } else {
                $sSel.not(".act-li").filter(":gt(" + ( opts.setNum - sActLen - 1 ) + ")").addClass("hide-li");
            }
            //more
            $ul.append( $(document.createElement('li'))
				.addClass('sel-li sel-tools')
				.html('<a class="more" href="javascript:void(0);">more</a>'));
        }
		//select all
        $ul.append( $(document.createElement('li'))
            .addClass('sel-li sel-tools')
            .html('<a class="sel-all" href="javascript:void(0);">all</a>'));

		$ul.appendTo( opts.obj );
		$.choose._more_event( $ul, opts );
        $.choose._all_event( $ul, opts );
		$ul.find(".sel-li").click(function(e){
            e.stopPropagation();
			var $self = $(this),
				len = $ul.find(".act-li").length;

            if( !$self.hasClass("act-li") ){
                $self.addClass("act-li");
            }else if( len > 1 ){
                $self.removeClass("act-li");
            }
            //callback
            if( opts.callback ) opts.callback( $self );
		});

	},
	_radio_child : function( $obj, data, opts ){
		var $ul = $(document.createElement("ul")).addClass('sel-ul'),
			html = '',
            $selectedM = $obj.closest('.selected-m'),
			$con = $selectedM.closest('.sel-p').parent();

		$con.find('.sel-ul').remove();
		$.each( data, function(i){
			html += '<li class="sel-li ';
			if( this.selected ){
				html += 'act-li';
			}
			html += '" ';
			$.each( this.attr, function( k, v ){
				html += 'data-' + k + '="' + v + '"';
			});
			html +='><span class="child-m" ' + '>'
				+ this.title
				+ '</span></li>';
		});

		$ul.html( html );

		//default selected 'act-li'
		//if( $ul.find('.act-li').length == 0 ){
			//$ul.find('.sel-li:eq(0)').addClass('act-li');
		//}
		//show setNum
		var $sAct = $ul.find(".act-li"),
			sActLen = $sAct.length,
            $sSel = $ul.find(".sel-li").not(".sel-tools"),
            sSelLen = $sSel.length;

        if( sSelLen > opts.setNum ){
			if( sActLen > opts.setNum ){
                $sAct.filter(':gt(' + ( opts.setNum - 1 ) + ')').addClass("hide-li");
            } else {
                $sSel.not(".act-li").filter(":gt(" + ( opts.setNum - sActLen - 1 ) + ")").addClass("hide-li");
            }
            //more
            $ul.append( $(document.createElement('li'))
				.addClass('sel-li sel-tools')
				.html('<a class="more" href="javascript:void(0);">more</a>'));
        }

		$ul.appendTo( $con );
        /* show title */
        if( opts.showTitleFn )opts.showTitleFn();
		$ul.find(".sel-li").click(function(e){
            e.stopPropagation();
			var $self = $(this);

			if( !$self.hasClass("act-li") ){
				$ul.find(".act-li").removeClass("act-li");
				$self.addClass("act-li");
			}
			if( opts.showTitleFn ) opts.showTitleFn();
            if(opts.childFn) opts.childFn($(this));
		});
		$.choose._more_event( $ul, opts );
	},
	_more_event : function( $con, opts ){
		$con.find(".more").toggle(
			function(e){
				e.stopPropagation();
				$con.find(".hide-li").removeClass("hide-li");
				$(this).text("pack");
			},
			function(e){
				e.stopPropagation();
				$con.find(".sel-li").not(".act-li:lt(" + opts.setNum + ")").not(".sel-tools").addClass("hide-li");
				$(this).text("more");
			}
		);
	},
    _all_event : function( $con, opts ){
        var $body = $("body");

        $con.find(".sel-all").toggle(
            function(e){
				e.stopPropagation();
                var $self = $(this),
                    $sels = $con.find(".sel-li").not('.sel-tools');

                //record
                if( opts.isRecord && opts.obj.find(".selected-m").hasClass( opts.recordClass ) ){
                    var recordNum = $body.data( opts.recordClass ),
                        actNum = $con.find(".act-li").length,
                        moreNum = $sels.length - actNum;

                    recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
                    var canNum = opts.recordNum - recordNum;

                    if( canNum <= 0 ){
                        say(lang.t("指标选项不能超过{1}个~.~", opts.recordNum));
                        return;
                    } else if( canNum >= moreNum ){
                        $sels.not(".act-li").addClass("act-li");
                        recordNum += moreNum;
                    } else {
                        $sels.not(".act-li").filter(":lt(" + canNum + ")").addClass("act-li");
                        recordNum += canNum;
                    }
                    $body.data( opts.recordClass, recordNum );
                } else {
                    $sels.not(".act-li").addClass("act-li");
                }

                $self.addClass("seled-all");
            },
            function(e){
				e.stopPropagation();
                var $self = $(this);

                //record
                if( opts.isRecord && opts.obj.find(".selected-m").hasClass( opts.recordClass ) ){
                    var recordNum = $body.data( opts.recordClass ),
                        actNum = $con.find(".act-li").length;

                    recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
                    recordNum -= ( actNum - 1 );
                    $body.data( opts.recordClass, recordNum );
                }
                $con.find(".act-li:gt(0)").removeClass("act-li");
                $self.removeClass("seled-all");
            }
        );
    },
	_handle_selected_event : function( $title, $curSel ){
		var curText = $curSel.find(".opt-v").text();

		$curSel.parent().find(".selected-opt").removeClass("selected-opt");
		$curSel.addClass('selected-opt');
		$title.attr( {
				'data-id' : $curSel.attr('data-id'),
				'data-child' : $curSel.attr('data-child'),
                'data-stat' : $curSel.attr('data-stat'),
                'data-common' : $curSel.attr('data-common'),
				'title' : curText
			})
			.text( curText )
			.parent().parent().addClass('selected-m');
	},
	_common_option_fac : function( data, selected ){
		var html = '<ul>';

		$.each( data, function(){
			html += '<li class="opt-m '
			if( this.selected || $.tools._in_array( selected, this.attr.id ) ){
				html += 'selected-opt';
			}
			html +=	'" ';
			$.each( this.attr, function( k, v ){
				html += 'data-' + k + '="' + v + '" ';
			});
			html += '>'
    			+ '<span class="opt-v" title="'+ this.title +'">' + this.title + '</span>';
			if( this.attr.child ){
    			html += '<span class="dir"></span>';
    		}
    		html += '</li>';
		});
		html += '</ul>';

		return html;
	},
	_common_search : function( $obj ){
		var $searchCon = $(document.createElement("div"))
						.addClass("sel-search")
						.html('<span class="search-con">'
								+'<input type="text" class="srh-txt" />'
								+'<i class="search-tag">&nbsp;</i></span>');

		$obj.css({ 'border-top' : '1px solid #bbb' })
			.append( $searchCon );
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
				} else {
					$self.parent().addClass("hide-li");
				}
			});
		}).keydown(function(e){
			var keyCode = e.keyCode;
			//$obj.parent().parent().keydown();
		});
	},
    _checkboxFactory: function(options) {
        var that = this,
            curClass = 'page-choose';
        var selCon = that._basic_fac(options.title , curClass, options.showBottom),
			optCon = selCon.find('.options-m'),
			titleCon = selCon.find('.title-m'),
			title = titleCon.find('.t-name'),
			html = '';
		var selAllHTML = '</span> <a class="sel-all" href="javascript:void(0);">' + lang.t("全选") + '</a>',
			selReverseHTML = '<a class="sel-reverse" href="javascript:void(0);">' + lang.t("反选") + '</a>';
        
        optCon.append(that._checkboxOptionFac(options.data, options.selected));
		if(options.search) that._page_search(optCon);
		selCon.appendTo(options.obj);
		$(selReverseHTML).insertAfter($('.search-con'));
        $(selAllHTML).insertAfter($('.search-con'));
		optCon.click(function(e){ e.stopPropagation(); });
		optCon.find('.subchk-li input[name="chk-choose"]').click(function(e){
            var t = $(this),
                dataIds = '',
                titleText = '',
                checkeds = optCon.find('input[name="chk-choose"]:checked');
            if(checkeds.length) {
                selCon.addClass('selected-m');
            } else {
                selCon.removeClass('selected-m');
            }
            $(checkeds).each(function(i) {
                var text = $(this).closest(".subchk-li").find(".sub-li-a").text();
                titleText += i == 0 ? text : (i == 1 ? (lang.t("、") + text) : (i == 2 ? "..." : ""));
                dataIds += $(this).closest(".subchk-li").attr("data-id") + ",";
            });
            title.attr({
                "data-id": dataIds.slice(0, -1)
            }).text(titleText ? titleText : lang.t("请选择："));
        });
		optCon.find('.sel-all').click(function () {
			optCon.find('li').each(function () {
				$(this).find('input').not(':checked').attr({ "checked": true });
			});
            var dataIds = '',
                titleText = '',
                checkeds = optCon.find('input[name="chk-choose"]:checked');
            selCon.addClass('selected-m');
            $(checkeds).each(function (i) {
                var text = $(this).closest(".subchk-li").find(".sub-li-a").text();
                titleText += i == 0 ? text : (i == 1 ? (lang.t("、") + text) : (i == 2 ? "..." : ""));
                dataIds += $(this).closest(".subchk-li").attr("data-id") + ",";
            });
            title.attr({
                "data-id": dataIds.slice(0, -1)
            }).text(titleText);
		});
		optCon.find('.sel-reverse').click(function () {
			optCon.find('li').find('input').each(function () {
                if ($(this).attr("checked") == "checked") { $(this).attr({ "checked": false }); }
                else { $(this).attr({ "checked": true }); }
			});
            var dataIds = '',
                titleText = '',
                checkeds = optCon.find('input[name="chk-choose"]:checked');
            if (checkeds.length) { selCon.addClass('selected-m'); }
            else { selCon.removeClass('selected-m'); }
            $(checkeds).each(function (i) {
                var text = $(this).closest(".subchk-li").find(".sub-li-a").text();
                titleText += i == 0 ? text : (i == 1 ? (lang.t("、") + text) : (i == 2 ? "..." : ""));
                dataIds += $(this).closest(".subchk-li").attr("data-id") + ",";
            });
            title.attr({
                "data-id": dataIds.slice(0, -1)
            }).text(titleText ? titleText : lang.t("请选择："));
		});
    },
	_page_factory : function( options ) {
		var _this = this,
            curClass = 'page-choose';
        if ( options.isRecord ) { curClass += ' '+ options.recordClass; }
        var $selCon = _this._basic_fac( options.title , curClass, options.showBottom ),
			$optCon = $selCon.find('.options-m'),
			$titleCon = $selCon.find('.title-m'),
			$title = $titleCon.find('.t-name'),
			html = '';

		if(options.page == 2){
			$optCon.append(_this._page_option_fac( options.data, options.selected ));
		}
		if( options.search ){ _this._page_search( $optCon ); }
		$optCon.click(function(e){ e.stopPropagation(); });
		$optCon.find(".sub-cat-li").click(function(e){
			e.stopPropagation();
			var $self = $(this),
				$a = $self.find('.sub-li-a');
            // record
            if( $selCon.hasClass( options.recordClass )){
                var recordNum = $("body").data(options.recordClass);
                recordNum = recordNum ? parseInt( recordNum, 10 ) : 0;
                if( $selCon.hasClass('recorded') ){
                    //清除之前的次级指标的个数
                    var $selUl = options.obj.closest('.sel-con').find(".sel-ul");
                    if($selUl.length) recordNum -= ($selUl.find(".act-li").length - 1);
                } else {
                    if( options.recordNum <= recordNum ){
                        say(lang.t("指标选项个数不能超过{1}个~。~", options.recordNum));
                        return;
                    }
                    $selCon.addClass('recorded');
                    recordNum = recordNum ? ( parseInt( recordNum, 10 ) + 1 ) : 1;
                }
                $("body").data(options.recordClass, recordNum);
            }
			$optCon.find(".sub-cat-li .sub-li-a.cur").removeClass('cur');
			$selCon.addClass('selected-m');
			$a.addClass('cur');
			$title.attr({
                    "data-id": $self.attr("data-id"),
                    "data-child" : $self.attr("data-child"),
                    "data-stat" : $self.attr("data-stat"),
                    "data-common" : $self.attr("data-common")
                }).text( $a.text() );
			if( options.callback )options.callback( options.obj, $title );
			/* show child */
			if($self.attr('data-child') == 'true'){
				_this._get_child( $self, options );
			}else{
				$self.closest('.select-m').parent().parent().find('.sel-ul').remove();
			}
			/* show title */
			if( options.showTitleFn )options.showTitleFn();
			$optCon.hide();
		});
        $optCon.find(".sub-cat-li .dir").click(function(e){ e.stopPropagation(); });
		$selCon.appendTo( options.obj );
        //if has selected ,then show child
        var $selectedLiA =  $optCon.find(".sub-li-a.cur");

        if ( $selectedLiA.length ) {
            $selectedLiA.parent().click();
        }
	},
	_page_search : function( $obj ){
		var $search = $(document.createElement("div")).addClass("subview-search")
				.html('<span class="search-con">'
						+ '<input type="text" class="srh-txt">'
						+ '<i class="search-tag">&nbsp;</i>'
						+ '</span>');

		$search.click(function(e){ e.stopPropagation(); })
			.prependTo( $obj )
			.find('.srh-txt').keyup(function(){
	    		var val = $(this).val(),
					reg = new RegExp(val);

				$obj.find('.sub-li-a').each(function(){
					var $self = $(this),
						text = $self.text();

					if( reg.test(text) ){
	    				var newText = text.replace( reg, '<font style="color: #58a601; font-weight: bold; ">'+ val +'</font>' );

						$self.html( newText );
						$self.parent().removeClass("hide-li");
					}else{
						$self.parent().addClass("hide-li");
					}
				});
    		});
	},
	_checkboxOptionFac: function(data , sel) {
		var subviewUl = $(document.createElement("ul")).addClass("subchk-con clearfix");
            subviewLis = $();
		$.each(data, function() {
            var subviewLi = $(document.createElement("li")).addClass("subchk-li"),
                attr = {};
            if(this.attr){
                $.each(this.attr, function(key, val){
                    attr["data-" + key] = val;
                });
            }
            subviewLi.attr(attr).append(
                $('<label><input type="checkbox" class="mr2" name="chk-choose"><span class="sub-li-a">' + this.title + '</span></label>')
            );
            subviewLis = subviewLis.add(subviewLi);
		});
        subviewUl.append(subviewLis);
        return subviewUl;
	},
	_page_option_fac : function( data , sel){
		var subviewUl = $(document.createElement("ul")).addClass("subview-con clearfix");
            subviewLis = $();
		$.each( data, function(){
            var subviewLi = $(document.createElement("li")).addClass("subview-li"),
                attr = {};
            if(this.attr){
                $.each(this.attr, function(key, val){
                    attr["data-" + key] = val;
                });
            }
            subviewLi.attr(attr).append($(document.createElement("h4")).addClass("subview-hd").text(this.title));
            if(this.children && this.children.length != 0){
                var subviewCatUl = $(document.createElement("ul")).addClass("subview-cat"),
                    subviewCatLis = $();
                $.each(this.children, function(){
                    var subviewCatLi = $(document.createElement("li")).addClass("sub-cat-li"),
                        catAttr = {},
                        subviewCatA = $(document.createElement("a"))
                            .addClass("sub-li-a" + (sel && $.tools._in_array( sel, this.attr.id) ? ' cur' : ''))
                            .text(this.title);
                    if(this.attr){
                        $.each(this.attr, function(key, val){
                            catAttr["data-" + key] = val;
                            if(key == "id") catAttr.title = val;
                        });
                    }
                    subviewCatLi.attr(catAttr).append(subviewCatA);
                    if(this.attr.child) subviewCatLi.append($(document.createElement("span")).addClass("dir"));
                    if(this.data) subviewCatLi.data("data", this.data);
                    subviewCatLis = subviewCatLis.add(subviewCatLi);
                });
                subviewCatUl.append(subviewCatLis).appendTo(subviewLi);
            }
            subviewLis = subviewLis.add(subviewLi);
		});
        subviewUl.append(subviewLis);
        return subviewUl;
	},
	_basic_fac : function( title , chooseClass, showBottom ){
        var optConStyle = { 'display' : 'none' };
        if(showBottom) {
            optConStyle.bottom = "26px";
        } else {
            optConStyle.top = "26px";
        }
		var $selCon = $(document.createElement("div")).addClass('select-m ' + chooseClass ),
		$titleCon = $(document.createElement("div")).addClass("title-m"),
		$optCon = $(document.createElement("div")).addClass("options-m").css(optConStyle),
		$title = $(document.createElement("span")).addClass("t-name").text( title ).appendTo( $titleCon );

    	$titleCon.add( $optCon ).appendTo( $selCon );
    	/* show options */
    	$titleCon.click(function(e){
    		e.stopPropagation();
    		if($optCon.is(":visible")){
				$optCon.hide();
			}else{
				$("body").find(".select-m .options-m").hide();
				$optCon.find(".subview-search .search-con .srh-txt").val('').keyup();
				$optCon.show();
			}
    	});

    	$("body").click(function(){
			if($optCon.is(":visible")){
				$optCon.hide();
			}
		});
    	return $selCon;
	}
};
$.tools = {
	_in_array : function( arr, val ){
    	for( var i = 0; i < arr.length; i++ ){
    		if( arr[i] == val ){
    			return true;
    		}

    	}
    	return false;
	},
	_get_left : function( $obj, $con ){
		$con = $con ? $con : $(window);
		var windowW = $(window).width(),
			w = $obj.width() ? $obj.width() : 130;

		if(windowW - w >0){
			return (windowW - w)/2;
		}else{
			return 0;
		}
	},
	_overlayer : function( options ){
		cnt++;
		var opts = {
			text : lang.t("加载中...")
		};
		$.extend(opts, options);
		var $layer = $("#J_layer"),
			$notice = $("#J_notice"),
			$body = $("body");

		if( $layer.length == 0 ){
			$layer = $(document.createElement("div")).addClass("layer")
					.attr("id", "J_layer")
					.appendTo($body);
		}else{
			$layer.show();
		}
		if( $notice.length == 0 ){
			$notice = $(document.createElement("div")).addClass("notice")
					.attr("id", "J_notice")
					.css({
						"left" : $.tools._get_left($notice) + 'px',
						"top" : 0 //$("body").scrollTop() + 'px'
					})
					.appendTo($body);
			$notice.text( opts.text );
		}else{
			$notice.text( opts.text );
            $notice.css({
				"left" : $.tools._get_left($notice) + 'px',
				"top" : 0 //$("body").scrollTop() + 'px'
			}).show();
		}
	},
	_hidelayer : function( text ){
		cnt--;
		if( cnt == 0 ){
			var text = text ? text : '',
				$notice = $("#J_notice");
			$notice.text(text);
			$("#J_layer").add( $notice ).hide();
		}
	},
    _set_center : function( obj ){
        var windowW = $(window).width(),
            windowH = $(window).height(),
            w = parseInt( obj.css("width"), 10 ),
            h = parseInt( obj.css("height"), 10 ),
            left = 0,
            top = $("body").scrollTop();

        if( windowW - w > 0 ){
          left = ( windowW - w ) / 2;
        }
        if( windowH - h > 0 ){
          top += (windowH - h) / 2;
        }else{
          //notice
          top += 40;
        }

        obj.css({
          'left': left + "px",
          'top' : top + "px"
        });
    },
    show : function( $obj ){
        var $over = $("#J_popOver");

        if( $over.length ){
            $over.show();
        }else{
            $over = $(document.createElement('div')).addClass('overlayer').attr({ id : 'J_popOver'});
            $over.appendTo( $("body") ).show();
        }
        $.tools._set_center( $obj );
        $obj.show();
    },
    hide : function( $obj ){
        var $over = $("#J_popOver");

        if( $over.length ){
            $over.remove();
        }
        $obj.hide();
    }
};
/**
 * popup
 * type -> 1: confirm
 * success : exec the function when save button is clicked
 * callback : when the popup appendTo $obj(body)
 * title : the title of the popup
 * content : the html of the content(popup-c-wrap)
 */
$.popup = {
	_type : {
		CONFIRMPOP : 1
	},
    core : function( options ){
		var defaults = {
			type : 1,
			obj : $("body"),
			title : lang.t("弹出框"),
            content : '',
            okButton : lang.t("保存"),
            cancelButton : lang.t("取消"),
            close : function(){ return; },
            ok : function(){ return; },
            cancel : function(){ return; },
            callback : function(){ return; }
		};
		options = $.extend( defaults, options ),
		type = parseInt( options.type, 10 );

		switch( type ){
			case this._type.CONFIRMPOP :
				this._confirm_factory( options );
				break;
			default :
				break;
		}

    },
    _confirm_factory : function( options ){
        var _self = this,
            $wrapper = $(document.createElement('div')).addClass('popup-wrapper')
                        .attr({ id : options.id })
                        .css({ 'display' : 'none' }),
            html = '<div class="popup-t-wrap">'
                + '<div class="popup-title">' + options.title + '</div>'
                + '<div class="popup-tools">'
                + '<a href="javascript:void(0);" class="t-close">&nbsp;</a></div>'
                + '</div>'
                + '<div class="popup-c-wrap">' + options.content + '</div>'
                + '<div class="popup-b-wrap">'
                + '<a href="javascript:void(0)" class="btn-s-dft mr10 save"><span>' + options.okButton + '</span></a>'
                + '<a href="javascript:void(0)" class="btn-s-dft cancel" ><span>' + options.cancelButton + '</span></a>'
                + '<div>';

        $wrapper.html( html ).appendTo( options.obj );
        $wrapper.dragIt({
            "holder" : ".popup-title",
            "limit" : false
        });
        if( options.callback ) options.callback( $wrapper );
        //close
        $wrapper.find('.t-close').click(function(e){
            e.stopPropagation();
            $.tools.hide( $wrapper );
            if( options.close ) options.close( $wrapper );
        });
        //cancel
        $wrapper.find('.cancel').click(function(e){
            e.stopPropagation();
            $.tools.hide( $wrapper );
            if( options.cancel ) options.cancel( $wrapper );
        });
        //save
        $wrapper.find('.save').click(function(e){
            e.stopPropagation();

            if( options.ok ) options.ok( $wrapper );
        });
    }
};
})(jQuery);
})();
