"use strict";
(function(window, undefined) {
	var index = 100;
	var Tips = null;
	var VIEW = {
		widget: null
	};
	$(function() {
		_g_tools();
		_g_home();
	});

	//---------------add to favor start--------------
	function _g_home(){
		var html = '';
		html += '<div class="widget-sel">'
			+   '<span class="note"><span class="c-red">'
			+ lang.t("注：")
			+ '</span>'
			+ lang.t("只能添加到{1}类型的小部件中，且最多可添加{2}条数据哦。", '<span class="c-red">' + lang.t("表格") + '</span>', '<span class="c-red">1000</span>')
			+ '</span>'
			+ '</div>'
			+ '<div class="widget-sel" id="J_type">'
			+   '<span class="sel-title">' + lang.t("我的收藏类型：") + '</span>'
			+   '<label class="mr10"><input type="radio" name="type" value="1" checked>' + lang.t("单项目") + '</label>'
			+   '<label class="mr10"><input type="radio" name="type" value="2">' + lang.t("多项目") + '</label>'
			+ '</div>'
			+ '<div class="widget-sel" id="J_homePlat" style="display:none;">'
			+   '<span class="sel-title">' + lang.t("选择平台区服：") + '</span><div class="sel-wrapper"></div><a href="javascript:void(0);" class="add-m">Add</a>'
			+ '</div>'
			+ '<div class="widget-sel" id="J_favor">'
			+   '<span class="sel-title">' + lang.t("我的收藏：") + '</span> <a class="add-m" href="javascript:void(0);">' + lang.t("新建") + '</a>'
			+   '<div class="sel-wrapper"></div>'
			+ '</div>'
			+ '<div class="widget-sel" id="J_collect">'
			+   '<span class="sel-title">' + lang.t("窗口小部件：") + '</span> <a class="add-m" href="javascript:void(0);">' + lang.t("新建") + '</a>'
			+   '<div class="sel-wrapper"></div>';
		VIEW.widget = $.Dlg.Util.popup({
			id: "J_widget",
			title: lang.t("添加到我的收藏"),
			width: '600px',
			contentHtml: html,
			saveCallback: function(){
				_add_widget();
			},
			callback: function(con){
			}
		});
		var plat = $("#J_homePlat"),
			favor = $("#J_favor"),
			collect = $("#J_collect");
		$("#J_addFavorBtn").click(function(e){
			e.stopPropagation();
			var checked = $("#J_content").find("table").find(".tbl-ckb:checked"),
				len = checked.length;
             if(len == 0){
                 say(lang.t("请选择需要添加项~。~"));
             } else if(len > 1000){
                 say(lang.t("最多只能添加{1}条指标~", "1000"));
             } else {
                 $("#J_widget").data("len", len);
                 _create_platform();
                 _create_favor();
                 _create_collect(-1); //unknown the favor_id at first time
                 VIEW.widget.show();
             }
			//$("#J_widget").data("len", len);
			//_create_platform();
			//_create_favor();
			//_create_collect(-1); //unknown the favor_id at first time
			//VIEW.widget.show();
		});

		// XML 上传
		$('#J_xmlUpload').change(function () {
			var options = {
				url: "index.php?r=gamecustom/item/upload&ajax=1&game_id=" + $('#J_paramGameId').val(),
				type: "post",
				dataType: "json",
				success: function(res) {
					if (res.result == 0) {
                        if (res.data.warn_info) {
                            var dlg = $.Dlg.Util.message("温馨提示", "正在处理，请稍候...", "确定"),
                                msg = "<table class='table'><tr><p>以下统计项名称重复</p>" +
                                      "<th class='th'>ID</th>" +
                                      "<th class='th'>first</th>" +
                                      "<th class='th'>second</th>" +
                                      "<th class='th'>third</th>" +
                                      "<th class='th'>fourth</th>" +
                                      "</tr>"
                            $.each(res.data.warn_info, function() {
                                msg += "<tr><td class='td'>" + this.id + "</td>" +
                                    "<td class='td'>" + this.first + "</td>" +
                                    "<td class='td'>" + this.second + "</td>" +
                                    "<td class='td'>" + this.third + "</td>" +
                                    "<td class='td'>" + this.fourth + "</td></tr>";
                            });
                            msg += "</table>";
                            dlg.setConfirmHtml(msg);
                            dlg.setOkHandler(function() {
                                location.reload();
                            });
                            dlg.show();
                        } else {
                            say(lang.t("上传成功~"), true, function() {
                                location.reload();
                            });
                        }
					}
				    else {
                        say(lang.t("上传失败~。~") + res.err_desc);
                        location.reload();
                    }
				}
			};
			$("form:first").ajaxSubmit(options);
			$(this).blur();
		});

		plat.find(".add-m").click(function(e) {
			e.stopPropagation();
			_create_platform();
		});
		//new favor
		favor.find(".add-m").click(function(e) {
			e.stopPropagation();
			var t = $(e.target),
				wrap = favor.find(".sel-wrapper"),
				selCon = favor.find(".sel-con"),
				selLen = selCon.length,
				newTxt = wrap.find(".popup-txt");
			if(t.hasClass('clicked')){
				t.removeClass('clicked');
				newTxt.hide();
				selCon.show();
			} else {
				t.addClass('clicked');
				selCon.hide();
				if(newTxt.length == 0){
					$(document.createElement('input')).attr({"type": "text"}).val(lang.t("未命名我的收藏"))
						.addClass('popup-txt').appendTo(wrap);
				} else {
					newTxt.show();
				}
				collect.find(".add-m").click();
			}
		});
		//new collect
		collect.find(".add-m").click(function(e) {
			e.stopPropagation();
			var t = $(e.target),
				wrap = collect.find('.sel-wrapper'),
				selCon = collect.find(".sel-con"),
				selLen = selCon.length,
				newTxt = wrap.find(".popup-txt");

			if(t.hasClass('clicked') && !favor.find('.sel-wrapper .popup-txt').is(':visible')){
				t.removeClass('clicked');
				newTxt.hide();
				selCon.show();
			}else{
				t.addClass('clicked');
				selCon.hide();
				if( newTxt.length != 0){
					newTxt.show();
				}else{
					$(document.createElement('input')).attr({"type": "text"}).val(lang.t("未命名窗口小部件"))
						.addClass('popup-txt').appendTo(wrap);
				}
			}
		});
		//type
		$("#J_type").find("input[name='type']").click(function(e) {
			e.stopPropagation();
			if( $(this).val() == 2 ){
				plat.show();
			} else {
				plat.hide();
			}
			_create_favor();
			_create_collect();
		});
	}

	function _add_widget(){    // 在保存点击后调用
		var opts = _handle_add_to_favor(),
			addCollectFn = function(param){
				ajaxData(getUrl("home", "collect", opts.a), opts, function(){
					say( lang.t("保存成功！"), true );
					VIEW.widget.fadeOut();
				});
			};

		if(opts){
			if( opts.favor_id ){
				addCollectFn(opts);
			}else{
				ajaxData(getUrl("home", "favor", "add"), {
					layout: 1,
					favor_name: opts.favor_name,
					favor_type: opts.favor_type,
					game_id: opts.game_id ? opts.game_id : 0
				}, function(data){
					opts.favor_id = data;
					addCollectFn(opts);
				});
			}
		}
	}
	/**
	 * add the report info to favor
	 * check favor or collect selected
	 */
	function _handle_add_to_favor(){
		//var checked = $("#J_content").find("table").find(".favor-checked"),
        var checked = $("#J_content").find("table").find(".tbl-ckb:checked"),
			widget = $("#J_widget"),
			favor = $("#J_favor"),
			collect = $("#J_collect"),
			favorNew = favor.find('.add-m'),
			collectNew = collect.find('.add-m'),
			favorType = $("#J_type").find("input[name='type']:checked").val(),
			opts = {
				draw_type :3,
				favor_type : favorType,
				indicator : []
			};
		if( favorType == 1 ){
			opts.game_id = $("#J_paramGameId").val();
			//total number is 1000
			if(  checked.length > 1000 ){
				widget.find(".note").hint(); return null;
			}
		} else {
			//zone server
			var plat = $("#J_homePlat"),
				actLi = plat.find(".act-li"),
				gpzs = [];
			if( actLi.length ){
				actLi.each(function(){
					gpzs.push($(this).attr("data-id"));
				});
			} else {
				plat.hint(); return null;
			}
			//total number is 1000
			if(  gpzs.length * checked.length > 1000 ){
				widget.find(".note").hint(); return null;
			}
		}
		if( favorNew.hasClass('clicked') ){//favor_name
			var favorName = favor.find('.sel-wrapper .popup-txt');

			if( favorName.val() ){
				opts.favor_name = favorName.val();
			}else{
				favorName.hint(); return null;
			}
		}else{//favor_id
			var favorSel = favor.find('.sel-wrapper').find('.select-m');
			if( favorSel.hasClass('selected-m') ){
				opts.favor_id = favorSel.find('.title-m .t-name').attr("data-id");
			}else{
				favorSel.hint(); return null;
			}
		}

		if( collectNew.hasClass('clicked') ){//collect_name
			var collectName = collect.find('.sel-wrapper .popup-txt');

			if( collectName.val() ){
				opts.collect_name = collectName.val();
				opts.a = 'add';
			}else{
				collectName.hint(); return null;
			}
		}else{//collect_id
			var collectSel = collect.find('.sel-wrapper').find('.select-m');

			if( collectSel.hasClass('selected-m') ){
				opts.collect_id = collectSel.find('.title-m .t-name').attr("data-id");
				opts.a = 'append';
			}else{
				collectSel.hint(); return null;
			}
		}
		//handle checked => indicator
        $("#J_singleReport").find("table").find(".tbl-ckb:checked").each(function(){
		//$("#J_singleReport").find("table").find(".favor-checked").each(function(){
			var parts = $(this).attr('value').split("_");
			opts.indicator.push({
				type: parts[0],
				id: parts[1],
				settings: []
			});
		});
        var $tabs = $("#J_multiReport").find(".tabs-active");
        if ($tabs.length) {
            var parts = $tabs.attr("data-id").split("_"),
                multi = {
                    type: parts[0],
                    id : parts[1],
                    settings: []
                };
            $("#J_multiReport").find("table").find(".tbl-ckb:checked").each(function(){
                multi.settings.push($(this).val());
            });
            opts.indicator.push(multi);
            $("#J_multiReport").find("table").find(".tbl-ckb:checked").each(function(){
            //$("#J_multiReport").find("table").find(".favor-checked").each(function(){
                var parts = $("#J_multiReport").find(".tabs-active").attr("data-id").split("_"),
                    multi = {
                        type: parts[0],
                        id : parts[1],
                        settings: []
                    };
                multi.settings.push($(this).attr('value'));
                opts.indicator.push(multi);
            });
        }
		//多项目指标需要添加区服
		if( favorType == 2 ){
			var indicatorTmp = [];
			actLi.each(function(){
				var gpzsId = $(this).attr("data-id");
				$.each( opts.indicator, function(){
					indicatorTmp.push({
						type: this.type,
						id : this.id,
						settings : this.settings,
						gpzs_id : gpzsId
					});
				});
			});
			opts.indicator = indicatorTmp;
		}

		return opts;
	}
	/**
	 * @brief _create_platform
	 *
	 * @return
	 */
	function _create_platform() {
		var plat = $("#J_homePlat"),
			selWrap = plat.find(".sel-wrapper"),
			selCon = $(document.createElement("div")).addClass("sel-con").css({ 'position' : 'relative', 'z-index' : _get_index() }),
			selP = $(document.createElement("div")).addClass("sel-p"),
			data = plat.data("platform"),
			addPlat = plat.find(".add-m");
		var opts = {
			search : true,
			type : 2,
			page : 2,
			obj : selP,
			mulRadio : 1,
			callback : function( curObj, title ){
				_handle_exist( selWrap, curObj, title );
			},
			getData : function( id, fn ){
				ajaxData(getUrl("common", "gpzs", "getZoneServer"),{
					game_id: $("#J_paramGameId").val(),
					platform_id: id
				}, function(data){
					data = _handle_setting_choose(data);
					if(fn) fn(data);
				});
			}
		};
		if(data){
			opts.data = data;
			$.choose.core(opts);
		} else {
			ajaxData(getUrl("common", "gpzs", "getPlatform"), {
				game_id: $("#J_paramGameId").val()
			}, function(data){
				data = _handle_indicator_choose(data);
				plat.data("platform",data);
				opts.data = data;
				$.choose.core(opts);
			}, false, true);
		}
		var delM = $(document.createElement("span")).addClass("del-m");
		delM.click(function(e) {
			e.stopPropagation();
			if( selWrap.find(".sel-con").length > 1 ){
				var selCon = $(this).closest(".sel-con");
				selCon.remove();
			}
		});
		selP.add(delM).appendTo(selCon.appendTo(selWrap));
	}
	function _handle_indicator_choose(data){
		var rlt = [{
			title: lang.t("选择平台"),
			children: []
		}];
		if(data && data.length){
			$.each(data, function(){
				rlt[0].children.push({
					title: this.gpzs_name,
					attr: {
						id: this.platform_id,
						cid: this.platform_id,
						child: true
					}
				});
			});
		}
		return rlt;
	}
	function _handle_setting_choose( data ){
		var rlt = [];

		if(data && data.length){
			$.each( data, function(){
				rlt.push({
					title : (( this.zone_id == -1 && this.server_id == -1 ) ? lang.t("全区全服") : this.gpzs_name),
					attr : { id : this.gpzs_id },
					selected : false
				});
			});
		}

		return rlt;
	}
	/**
	 * @brief _handle_exist
	 *
	 * @param selWrap
	 * @param curObj: the current selected title
	 * @param title
	 */
	function _handle_exist( selWrap, curObj, title ){
		var exist = [],
			id = title.attr("data-id"),
			existWraps = curObj.find(".exist-wrap");

		selWrap.find(".sel-con .sel-p").find(".title-m .t-name").not(title).each(function(){
			var id = $(this).attr("data-id");
			if( id ) exist.push( id );
		});
		if( inArray( id, exist ) ){
			if( existWraps.length == 0 ){
				var delExist = $(document.createElement("span")) .addClass("exist-del");
				delExist.click(function(e) {
					e.stopPropagation();
					$(this).parent().remove();
				});
				$(document.createElement("div")).addClass("exist-wrap").text(lang.t("该项目已存在。"))
					.append(delExist).appendTo(curObj);
			}
		}else if( existWraps.length > 0){
			existWraps.remove();
		}
	}
	/**
	 * create the select of favor list
	 */
	function _create_favor() {
		var favor = $("#J_favor"),
			selWrap = favor.find(".sel-wrapper").empty(),
			selcon = $(document.createElement("div")).addClass("sel-con")
                .css({ 'position' : 'relative', 'z-index' : 2 }),
			selP = $(document.createElement("div")).addClass("sel-p");
		favor.find(".add-m").removeClass("clicked");
		selWrap.find(".popup-txt").hide();
		var opts = {
			search : true,
			type : 1,
			callback : function( curObj, title){
				var id = title.attr("data-id");
				_create_collect( id ? id : -1 );
			},
			obj : selP
		};
		ajaxData(getUrl("home", "favor", "getList"), null, function(favorlist){
			opts.data = _handle_for_choose(favorlist, 'favor');
			$.choose.core( opts );
		});
		selP.appendTo(selcon.appendTo(selWrap));
	}
	/**
	 * create the select of collect list by choose
	 */
	function _create_collect( id ){
		var collect = $("#J_collect"),
			selWrap = collect.find(".sel-wrapper").empty(),
			selCon = $(document.createElement("div")).addClass("sel-con")
                .css({ 'position' : 'relative', 'z-index' : 1}),
			selP = $(document.createElement("div")).addClass("sel-p");

		collect.find(".add-m").removeClass("clicked");
		selWrap.find(".popup-txt").hide();
		var opts = { search : true, type : 1, obj : selP };
		var fn = function(data){
			opts.data = _handle_for_choose(data, 'collect');
			$.choose.core( opts );
		};
		if( id == -1 ){
			fn([]);
		} else {
			ajaxData(getUrl("home", "collect", "getListByFavorId"), {
				favor_id : id
			}, fn);
		}
		selP.appendTo(selCon.appendTo(selWrap));
	}

	/**
	 * we should give the data like [{ title : "", attr : { id : "" }}] when use choose
	 */
	function _handle_for_choose( data, pre ){
		var rlt = [];
        if( pre == 'favor' ){
		    if(data && data.length){
				var gameId = $("#J_paramGameId").val(),
					type = $("#J_type").find("input[name='type']:checked").val();
				$.each( data, function(){
					if( type == 1 && this.favor_type == type && this.game_id == gameId ){
						rlt.push({
							title : this[pre + "_name"],
							attr : { id : this[pre + "_id"] }
						});
					} else if( type == 2 && this.favor_type == type ){
						rlt.push({
							title : this[pre + "_name"],
							attr : { id : this[pre + "_id"] }
						});
					}
				});
            }
        } else {
            if (data.self && data.self.length) {
				$.each( data.self, function(){
                    rlt.push({
                        title : this[pre + "_name"],
                    attr : { id : this[pre + "_id"] }
                    });
				});
            }
            if (data.shared && data.shared.length) {
				$.each( data.shared, function(){
                    rlt.push({
                        title : this[pre + "_name"],
                    attr : { id : this[pre + "_id"] }
                    });
				});
            }
        }
		return rlt;
	}

	function _get_index(){
		if( index == 0 ) index = 100;
		return index--;
	}
	//---------------add to favor end---------------
	function _g_tools(){
		//time
		var $game = $("#J_game"),
			$from = $("#J_from"),
			$to = $("#J_to"),
			$date = $("#J_date");

		$game.change(function(){
			var gameId = $(this).find(":selected").attr("data-id");
			go(getUrl($("#J_r").val(), undefined, undefined, "game_id=" + gameId));
		});

		// change content width
		if ($('#J_authchk').val() == "1")
			$('.main').find('.content').css({ marginRight: 45 });
		else
			$('.main').find('.content').css({ marginRight: 10 });
		
		// select time
		$date.datepick({
			rangeSelect: true,
			monthsToShow: 3,
			monthsToStep: 3,
			monthsOffset: 2,
			shortCut : true,
			onClose: function(userDate, e) {
				if (userDate.length) {
					var userDate = $date.val().split("~");
					userDate[0] = $.trim(userDate[0]);
					userDate[1] = $.trim(userDate[1]);
					$from.val(userDate[0]);
					$to.val(userDate[1]);
				} else {
					$date.val($from.val() + " ~ " + $to.val());
				}
				refresh();
			}
		});
		$("#J_from_to").click(function(e){
			$date.focus();
			e.stopPropagation();
		});

		var $platform = $("#J_platform");

		getZoneServer( $platform.find(":selected").attr("data-id"), function( data ){
			_zoneServer_fac( data );
			_g_tree();
		});
		$platform.tmselect().change(function(e){
			getZoneServer( $(this).find(":selected").attr("data-id"), function( data ){
				_zoneServer_fac( data, true );
			});
		});

		_gCart();
	}

	function _gCart() {
		var $from = $("#J_from"),
			$to = $("#J_to"),
			$date = $("#J_date");
		
		$('#J_cart').slidecart({
			top: 87,
			panelWidth: 307,
			height: $(window).height() - 87,
			mask: true,
			tags: 1,
			setSingleTag: [ function () {
				$(this).css({ height: 60 }).attr({ 'title': '自助查询' }).addClass('selfhelp-tag').data({ operands: [] });
				var selfhelpIcon = $(document.createElement('div')).addClass('icon').css({
					height: 29,
					width: 29,
					position: 'relative',
					marginTop: 15,
					marginLeft: 6,
					overflow: 'hidden',
					display: 'inline-block'
				}).appendTo($(this)),
					iconSearch = $(document.createElement('div')).addClass('search').appendTo(selfhelpIcon);
			} ],
			setItemsArea: function () {
			},
			setFilter: function () {
				var filter_info = [
					'米米号',    // 1
					'首次登陆日期',    // 2
					'最后登陆日期',    // 3
					'等级',    // 4
					'当前是否为VIP',    // 5
					'当月付费额',    // 6
					'当月付费次数',    // 7
					'首次按条付费时间',    // 8
					'最后按条付费时间',    // 9
					'累计按条付费总额',    // 10
					'累计按条付费次数',    // 11
					'首次包月时间',    // 12
					'最后包月时间',    // 13
					'累计包月总额',    // 14
					'累计包月次数',    // 15
					'游戏币消耗量',    // 16
					'游戏币存量',    // 17
				];
				var filterCtrl = $(document.createElement('div')).css({ float: 'left', width: '90%', marginLeft: 10, marginBottom: 8, fontSize: 14 }).addClass('filter-ctrl').appendTo($(this));

				var filterTitle = $(document.createElement('span')).css({ float: 'left', fontSize: 14}).text('过滤查询信息').addClass('filter-title').appendTo(filterCtrl);
				var selReverse = $(document.createElement('a')).css({ float: 'right', fontSize: 14, cursor: 'pointer' }).text(' 反选').addClass('filter-reverse').appendTo(filterCtrl).click(function () {
					var inputs = $(this).parent().parent().find('input');
					inputs.trigger('click');
				});
				var selAll = $(document.createElement('a')).css({ float: 'right', fontSize: 14, cursor: 'pointer' }).text('全选|').addClass('filter-all').appendTo(filterCtrl).click(function () {
					var inputs = $(this).parent().parent().find('input');
					inputs.each(function () {
						$(this).attr({ checked: true });
					});
				});

				for(var i = 0; i < 17; i++) {
					var divcls = 'filter-' + (i + 1).toString(),
						iptcls = 'input-' + (i + 1).toString(),
						spancls = 'span-' + (i + 1).toString();
					var filter = $(document.createElement('label')).addClass(divcls).attr({ 'filter_id': i + 1 }).css({ marginLeft: 10, marginRight: 10, marginTop: 2, width: 130, float: 'left' }).appendTo($(this));
					var filterChk = $(document.createElement('input')).addClass(iptcls).attr({ type: 'checkbox', 'filter_id': i + 1 }).appendTo(filter);
					if (i == 0) { filterChk.click(); }
					var filterTxt = $(document.createElement('span')).addClass(spancls).css({ marginLeft: 3, color: '#383838' }).text(filter_info[i]).appendTo(filter);
				}
			},
			setUnfold: function () {
				var foldTxtArea = $(document.createElement('div')).css({ marginTop: 7, marginLeft: 12, height: 18, width: 50 }).addClass('fold-area').appendTo($(this)),
					foldTxt = $(document.createElement('span')).css({ color: '#F5F6FA', marginLeft: 3, fontSize: 15, fontWeight: 'bold' }).text('更 多').addClass('fold-btn').appendTo(foldTxtArea),
					icon = $(document.createElement('div')).addClass('icon').appendTo($(this)),
					ptTop = $(document.createElement('div')).addClass('pointer-top').appendTo(icon);
				$(this).attr({
					title: '更多选项'
				});
				$(this).click(function () {
					var hook = $(this).find('.icon').children();
					
					if (hook.hasClass('pointer-top')) {
						hook.removeClass('pointer-top').addClass('pointer-down');
						// $('.filter-info').show();
					} else if (hook.hasClass('pointer-down')) {
						hook.removeClass('pointer-down').addClass('pointer-top');
						// $('.filter-info').fadeOut('fast');
					}
				});
			},
			setBtn: function () {
				var btnTxtArea = $(document.createElement('div')).css({ marginTop: 8, marginLeft: 88, height: 26, width: 112 }).addClass('txt-area').appendTo($(this));
				var btnTxt = $(document.createElement('span')).css({ marginLeft: 35 }).text('查 询').addClass('btn-txt').appendTo(btnTxtArea);
			}
		}).on('slidecartrefresh', function () {
			var context = this;
			ajaxData(getUrl("common", "basket", "getBasketInfo"), {}, function(data) {
				// 清空操作
				$('.item-con').children().remove();
				$('.game-selector').children().remove();
				if (data.length) { $('.game-selector').show(); }
				else { $('.game-selector').hide(); }
				for(var i = 0; i < data.length; i++) {    // 每个游戏有一个外层的 for
					var tmp_game = data[i]['game_name'],
						tmp_gameid = data[i]['game_id'],
						tmp_items = data[i]['items'],
						tmp_dfttime = data[i]['dft_time'],
						tmp_filter = data[i]['filter_info'];
					// 修改基准游戏选项
					$(document.createElement('option')).text(tmp_game).attr({
						'game_id': tmp_gameid
					}).appendTo($('.game-selector'));
					for (var j = 0; j < tmp_items.length; j++) {
						var itemins = $(document.createElement("div")).addClass('game-item').appendTo($('.item-con'));
						itemins.item({
							timeDft: tmp_dfttime,    // 默认日期
							timeInit: tmp_items[j]['periods'].length,
							timeInfo: tmp_items[j]['periods'],
							gameNameTxt: function () {    // 游戏名称回调
								var ifDataId = tmp_items[j]['data_id'] == undefined ? {} : { 'data_id': tmp_items[j]['data_id'] };
								var param = $.extend({
									'type': tmp_items[j]['type'],
									'r_id': tmp_items[j]['r_id'],
									'gpzs_id': tmp_items[j]['gpzs_id'],
									'game_id': tmp_gameid
								}, ifDataId);
								$(this).text(tmp_game).attr(param);
							},
							setItemClose: function () {
								var context = this;
								$(this).click(function () {
									var source = $(this).parent().prev().find('.itemname-area'),
										gameName = source.find('.itemgame-con').find('.item-game').text(),
										itemName = source.find('.itemname-con').find('.item-name').text(),
										rId = source.find('.itemgame-con').find('.item-game').attr('r_id'),
										dataId = source.find('.itemgame-con').find('.item-game').attr('data_id'),
										param = {
											'game_name': gameName,
											'items': [{
												'item_name': itemName,
												'r_id': rId,
												'data_id': dataId == undefined ? null : dataId
											}]
										};
									ajaxData(getUrl("common", "basket", "deleteBasketInfo"),
											 param,
											 function (data) {
												 // if (data) {
												 // 	 var $widget = $(context).parent().parent().parent();
												 // 	 $widget.remove();
												 // }
											 }, false, true);
									var $widget = $(context).parent().parent().parent();
									$widget.remove();
								});
							},
							ItemNameTxt: function () {
								$(this).text(tmp_items[j]['item_name']);
							},
							setItemTime: function () {
								var context = this;
								$(this).datepick({
									rangeSelect: true,
									monthsToShow: 1,
									monthsToStep: 1,
									monthsOffset: 1,
									shortCut : false,
									maxDate: new Date(),
									onClose: function(userDate, e) {
										var itemIns = $('#J_cart').find('.item-ins'),
											filterInfo = $('#J_cart').find('.filter-info');

										if (userDate.length) {
											var userDate = $date.val().split("~");
											userDate[0] = $.trim(userDate[0]);
											userDate[1] = $.trim(userDate[1]);
											$from.val(userDate[0]);
											$to.val(userDate[1]);
										} else {
											$date.val($from.val() + "~" + $to.val());
										}
									}
								});
							}
						});
					}
				}
			}, false, true);
		}).on('slidecartsubmit', function () {
			var basicInfo = gatherInfo($(this), 'submit');
			formSubmit(basicInfo);
		}).on('slidecartsave', function () {
			var basicInfo = gatherInfo($(this), 'save');
			formSave(basicInfo);
		});

		$(window).one('beforeunload', function() {
			var root = $('#J_cart');
			if (root.find('.game-item').length) {
				var basicInfo = gatherInfo(root, 'save');
				formSave(basicInfo);
			}
		});

		var stat = function () {
			var divisor = $('.item-con').find('.game-item').length,
				dividend = $('.item-con').find('.game-item').find('.check-item:checked').length,
				dinfo = '已选' + dividend + '条，共' + divisor + '条';
			
			if (dinfo != $('.notify-txt').find('span').text())
				$('.notify-txt').find('span').text(dinfo);
			if (dividend < 2)
				$('.calc-selector').children().eq(2).hide();    // no setdiff
			else
				$('.calc-selector').children().eq(2).show();    // with setdiff
			
			setTimeout(stat, 250);
		};
		stat();
	}

	function gatherInfo(widget, type) {
		var items = widget.find('.item-ins'),
			selItems = items.find('.checkbox-con').find('input').filter(':checked').parent().parent(),
			baseGameId = parseInt(widget.find('.game-selector').children().filter(':selected').attr('game_id')),
			calcType = widget.find('.calc-selector').children().filter(':selected').attr('type'),
			filterInfo = widget.find('.filter-info').children().find('input:checked'),
			filterSet = new Array(),
			opMeta = new Array(),
			pro = new Object();

		filterInfo.each(function () {
			filterSet.push(parseInt($(this).attr('filter_id')));
		});
		
		selItems.each(function () {
			var meta = new Object(),
				source = $(this).find('.item-game'),
				item = $(this).find('.item-name');
			if (type == 'submit') {
				meta.game_id = source.attr('game_id');
			} else if (type == 'save') {
				meta.game_name = source.text();
				meta.item_name = item.text();
			}
			meta.type = source.attr('type');
			meta.gpzs_id = source.attr('gpzs_id');
			meta.r_id = source.attr('r_id');
			if (source.attr('data_id') != undefined)
				meta.data_id = source.attr('data_id');
			meta.periods = new Array();
			$(this).find('.time-tag').each(function () {
				var timeRange = $(this).find('input').val().split('~'),
					timeFrom = timeRange[0],
					timeTo = timeRange[1];
				meta.periods.push({
					from: timeFrom,
					to: timeTo
				});
			});
			opMeta.push(meta);
		});

		var headInfo = type == "submit" ? {
			'game_id': baseGameId,
			'operation': calcType
		} : {};
		pro = $.extend(headInfo, { 'operands': opMeta, 'filter_info': filterSet });
		
		return pro;
	}

	/**
	 * @brief: 弹出对话框的信息
	 * @param: NULL
	 * @return: NULL
	 */
	function _getOptions() {
		var date = new Date(),
			usrName = $('#J_header').find('.wrapper').find('.links').find('li').eq(0).text().split("，")[1],
            dateInfo = date.toISOString().substring(0, 10).replace(/\-/g, '') + '_' + date.getHours() + '_' + date.getMinutes(),
			dftTxt = usrName + '_' + dateInfo;
		return {
			items: [{
				label: {
					title: lang.t("本次查询名称为："),
					className: "title-inline"
				},
				items: [{
					type: "text",
					name: "set-name",
					className: "ipttxt",
					value: dftTxt
				}]
			}]
		};
	}
	
	// 确认本次查询
	function formSubmit(pro) {
		var SUBMITDIALOG, FIELDSET, param;
		FIELDSET = new tm.form.fieldSet($.extend({}, _getOptions()));
		if (!SUBMITDIALOG && pro.operands.length != 0) {
			if (pro.operands.length > 5) {
				say("目前对差集支持最多3个事件的计算,对交集和并集支持最多5个事件的计算");
			} else if (pro.operands.length > 3 && pro.operation == 'setdiff') {
				say("目前对差集支持最多3个事件的计算,对交集和并集支持最多5个事件的计算");
			} else {
				SUBMITDIALOG = $.Dlg.Util.popup({
					id: "J_setSubmitName",
					title: lang.t("自助查询（请到我的下载中查看本次查询）"),
					contentHtml: $("<form>").append(FIELDSET.getElement())
				});
				SUBMITDIALOG.setSaveHandler(function () {
					ajaxData(getUrl("tool", "selfhelp", "add"),
							 $.extend(pro, {
								 'file_name': $("#J_setSubmitName").find("form").find('input').val()
							 }),
							 function(res) {
								 $('.item-con').find('.check-item:checked').parent().parent().parent().fadeOut(function () {
									 $(this).remove();
								 });
								 var dlg = $.Dlg.Util.message("温馨提示", "正在处理，请稍候...", "确定"),
							 	 	 msg = "";
								 dlg.show();
								 if (res.code === 0) {
							 	 	 go(res.data.url);
							 	 	 return;
								 } else if (res.code === 1) {
							 	 	 msg = "由于您选择的时间间隔较长，已自动为您异步下载，"
							 	 		 + "您可以到页面右上角【我的下载】查看和下载！"
							 	 		 + "<a href='" + getUrl("tool/file/index")
							 	 		 + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
								 } else if (res.code === 2) {
							 	 	 msg = "文件正在处理，请耐心等待，您也可以到页面右上角【我的下载】查看！"
							 	 		 + "<a href='" + getUrl("tool/file/index")
							 	 		 + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
								 }
								 if (msg !== "") {
							 	 	 dlg.setConfirmHtml(msg);
							 	 	 dlg.getManager().setPosition(dlg);
							 	 	 dlg.show();
								 }
							 }, "POST");
				});
				SUBMITDIALOG.show();				
			}
		} else {
			say('查询前请先选中一组事件');
		}
		
		return false;
	}

	function formSave(pro) {
		ajaxData(getUrl('common', 'basket', 'reviseInfo'), pro, function (res) {
			//TODO: 
			// say('Save Info Failure.');
		}, false);
	}
	
	function _gSearch() {
		var search = $("#J_search"),
			searchIpt = search.find(".srh-txt"),
			searchTag = search.find(".search-tag"),
			searchFn = function() {
				$("#J_tree").jstree("search", searchIpt.val());
			};
		searchIpt.focus(function() {
			search.css({ border: "2px solid #FE9D1F" });
		}).blur(function() {
			search.css({ border: "2px solid #CCC" });
		}).keydown(function(e) {
			if(e.keyCode == 13) searchFn();
		});
		searchTag.click(function() {
			searchFn();
		});
	}

	function _showSearchTips() {
		if(Tips) clearTimeout(Tips);
		var tips = $("#J_search").find(".search-tips");
		tips.slideDown(200, function(){
			Tips = setTimeout(function(){
				tips.fadeOut(300);
			}, 500);
		});
	}
	function _g_tree() {
		_gSearch();
		// 左树
		var gameId = $("#J_game").find(":selected").attr("data-id");
		$("#J_tree").jstree({
			plugins : ["html", "json_data", "themes", "cookies", "ui", "core", "search"],
			themes : { "theme" : "orange" },
			search : {
				case_insensitive: true,
				ajax: {
					url : getUrl("gamecustom", "tree", "search"),
					data : function(n) {
						return {
							keyword: $("#J_search").find(".srh-txt").val(),
							game_id: $("#J_paramGameId").val()
						};
					},
					success : function(res) {
						var arr = [];
						$(res.data).each(function() {
							arr.push("#custom" + this);
						});
						return arr;
					}
				}
			},
			json_data : {
                ajax : {
                    url : getUrl("gamecustom", "tree", "getTree"),
                    data : function(n) {    // 请求参数
                        return {
                            game_id : gameId,
                            parent_id : n.attr ? n.attr("node_id") : 0
                        };
                    },
                    success : function( res ) {
                        if (res.result == 0 && res.data) {
                            var nodes = [];
                            $.each(res.data, function() {
                                nodes.push({
                                    data : this.node_name,
                                    state : (this.is_leaf == "1" ? "leaf" : "closed"),
                                    attr : {
                                        title: this.node_name,
                                        id: "custom" + this.node_id,
                                        node_id : this.node_id,
                                        is_leaf : this.is_leaf
                                    }
                                });
                            });
                            return nodes;
                        } else {
                            return "";
                        }
                    }
                }
            },
			ui: {
				select_limit: 1
			}
		}).bind("select_node.jstree", function(event, node) {    // 页面选择节点后的事件绑定
			if (node.rslt.obj.attr("is_leaf") === "1") {
				var container = $("#J_content").empty().addClass("loading"),
                    modTitle = node.rslt.obj.text();

                // 两个表格的配置
				ajax(getUrl("gamecustom", "content", "getContentList"), $.extend({
					node_id : node.rslt.obj.attr("node_id"),
					tags: "all"
				}, getPageParameters()), function(res) {
					if (res.result == 0) {
                        container.removeClass("loading has-no-data")
                            .data("content-data", fac(configure(res.data, $.extend({
					            node_id : node.rslt.obj.attr("node_id")
				            }, getPageParameters()), modTitle)));
					}
				});
                
			} else {
				if( node.args[1] ) { node.inst.toggle_node(); }
			}
		}).bind("search.jstree", function(evt, node) {
			if(node.rslt.nodes.length == 0) {
				_showSearchTips();
			}
		});
	}

	// 跟据返回生成内容配置
	function configure(configure, param, title) {
		if((configure.data instanceof Object && $.isEmptyObject(configure.data))
           || (Array.isArray(configure.data) && !configure.data.length)) {
			$("#J_content").addClass("has-no-data").text(lang.t("没数据"));
			return;
		}
		var prepared = [],
            child = null,
            rlen = configure.data[0].length,
			theadConfigure = getTheadByDate(configure.date),
            judgeRange = function (rlen) {
                var range = {
					'all': 101,
					'month': 201,
					'week': 99999999
				};
                for (var prop in range) {
				    if (range.hasOwnProperty(prop)) {
					    if (rlen < range[prop]) {
						    return prop;
					    }
				    }
			    }
                return "all";
            },
            dataconfig = function (data) {
                var conf = [];
                $(data).each(function () {
                    conf.push([{
						title: this.r_name,    // left tree node name
						dataId: this.type + "_" + this.r_id,
						iffavor: false,//this.iffavor,
						ifselfhelp: this.ifselfhelp,
						infavor: this.infavor,
						inselfhelp: this.inselfhelp
                    }]);
                });
                return conf;
            },            
			hugeTableConfigure = function(option, thead, prepared, page) {
                return {
                    type: "hugeProgressiveTable",    // Draw type
					checkbox: true,
				    cartEntrance: true,
					authChk: $('#J_authchk').val() == "1" ? 1 : 0,
				    cartCallback: 'tableEvent',
                    hide: false,
                    dataDelay: true,
                    height: maxHeight,
                    thead: thead,
                    data: dataconfig(option),    // [],
                    prepareData: prepared,
                    url: {
                        url: getUrl("common", "data", "getTimeSeries"),
                        page: page
                    }
                };
			},
			tabConfigure = function(option, thead, prepared) {
				var options = {
                    type: "tabsExtendMore",
                    child: []
                },
					dataUrl = getUrl("gamecustom", "content", "getDataList", "r_id="),
					tabConfig = {
						title: "",
						child: [ {
                            type: "wrap",
                            theme: "no-frame",
                            headEnabled: false,
                            bottomEnabled: false,
                            condition: ['tagFilter'],
                            conditionOptions: {
                                tagFilter: {
                                    default: judgeRange(rlen)
                                },
								ignore: []
                            },
                            child: [ {
							    type: "data",
							    url: {
								    timeDimension: 1,
								    page: getPageParameters
							    },
							    child: []
						    } ]
                        } ]
					}, tempTabConfig;
				$(option).each(function() {
					tempTabConfig = $.extend(true, {}, tabConfig);
                    
					tempTabConfig.title = this.r_name;
                    tempTabConfig.child[0].conditionOptions.tagFilter.default = judgeRange(this.r_length);
					tempTabConfig.attr = {
						"data-id" : this.type + "_" + this.r_id,
						"rlength" : this.r_length
					};
					tempTabConfig.child[0].child[0].url.extend = dataUrl + this.r_id + "&type=" + this.type;
					tempTabConfig.child[0].child[0].rlen = this.r_length;
					tempTabConfig.child[0].child[0].child.push(hugeTableConfigure([], thead, prepared, function(ids) {
						var parameters = getPageParameters(),
							i = 0, length = ids.length, parts;
						for (; i < length; i++) {
							parts = ids[i].split(":");
							parameters["data_info[" + i + "][data_id]"] = parts[0];
							parameters["data_info[" + i + "][data_expr]"] = parts[1] ? parts[1] : "";
						}

						parameters["by_data_expr"] = 1;
						return parameters;
					}));
					options.child.push($.extend(true, {}, tempTabConfig));
				});
				return options;
			},
			maxHeight = $(".aside").height() - $("#J_content").position().top - 110 - 20;

		if (configure.data[0] && configure.data[1]) {
            maxHeight = 400;    // both
        }
        if (configure.data[0] && configure.data[0].length) {
            prepared.push({
			    type: "wrap",
			    container: $("#J_content"),
			    attr: { id: "J_singleReport" },
			    title: title,
			    headEnabled: false,
			    bottomEnabled: false,
			    condition: ['tagFilter'],
			    conditionOptions: {
                    tagFilter: {
                        default: judgeRange(rlen)
                    },
					ignore: ["modify_week"]
                },
                search: true,
			    download: function(wrap) {
				    var param = getDownloadParameters(),
                        options = wrap.getOption();
				    param.is_multi = 0;
                    param.searchValue = options.searchValue;
				    if (param) {
					    $.download(getUrl("gamecustom", "tree", "export"), param);
				    }
			    },
			    child: [{
                    type: "data",
                    url: {
                        extend: getUrl("gamecustom", "content", "getContentList"),
                        page: function () {
                            return param;
                        }
                    },
                    refresh: function (option) {
                        option.dataChange && (option.data = dataconfig(option.data.data[0]));
                        $(child).each(function() {
                            this.refresh(option);
                        });
                    },
                    afterLoad: function (data, container) {
                        var conf = hugeTableConfigure(data.data[0], getTheadByDate(data.date), null, function(ids) {
					        var parameters = getPageParameters(),
						        i = 0, length = ids.length, parts;
					        for (; i < length; i++) {
						        parts = ids[i].split("_");
						        parameters["data_info[" + i + "][type]"] = parts[0];
						        parameters["data_info[" + i + "][r_id]"] = parts[1];
						        if (parts[0] === "report") {
							        parameters["data_info[" + i + "][range]"] = "";
						        }
					        }
					        parameters["by_data_expr"] = 1;
					        return parameters;
				        });
                        conf["container"] = container;
                        child = fac([ conf ]);
                    }
                }]
		    });
        }
		if (configure.data[1] && configure.data[1].length) {
			prepared.push({
				type: "wrap",
				container: $("#J_content"),
				title: title,
				attr: { id: "J_multiReport" },
				headEnabled: false,
				bottomEnabled: false,
                search: true,
				download: function(wrap) {
					var param = getDownloadParameters(),
                        options = wrap.getOption();
					param.is_multi = 1;
                    param.searchValue = options.searchValue;
					if (param) {
						$.download(getUrl("gamecustom", "tree", "export"), param);
					}
				},
				child: [
					tabConfigure(configure.data[1], theadConfigure, function(data) {
						var prepare = [];
						$.each(data, function() {
							prepare.push([{
								title: this.data_name,
								dataId: parseInt(this.data_id) !== 0 ? this.data_id : ("0:" + this.data_expr),
								iffavor: false,//this.iffavor,
								ifselfhelp: this.ifselfhelp,
								infavor: this.infavor,
								inselfhelp: this.inselfhelp
							}]);
						});
						return prepare;
					})
				]
			});
		}
		return prepared;
	}

	/**
	 * @brief getTheadByDate
	 * 根据日期获取表格头，用于插件
	 *
	 * @param dateSeries
	 */
	function getTheadByDate(dateSeries) {
		var thead = [{
            title: lang.t("日期"),
            css: { width: "200px" }
        }];
		for(var i = dateSeries.length - 1; i > -1; i--) {
			thead.push({
				title: (dateSeries[i]).toString(),
				className: isWeekend(dateSeries[i]) ? "gr" : "",
				css: { width: "80px" }
			});
		}
		return thead;
	}
    
	/**
	 * @brief getPageParameters
	 * 获取页面公共参数
	 */
	function getPageParameters() {
		return {
			from: $("#J_from").val(),
			to: $("#J_to").val(),
			gpzs_id: $("#J_zoneServer").find(".selected-item").attr("data-id").split("_")[0],
			game_id: $("#J_paramGameId").val()
		};
	}

	function refresh() {
		ajax(getUrl("gamecustom", "content", "getTimePoints"), getPageParameters(), function(res) {
			if (res.result == 0) {
				var modules = $("#J_content").data("content-data");
				if (modules && modules.length) {
					$(modules).each(function() {
						this.refresh({
							theadChange: true,
							thead: getTheadByDate(res.data)
						});
					});
				}
			}
		}, "POST");
	}

	function getDownloadParameters() {
		var select = $("#J_tree").jstree("get_selected");
		if (!select.length) {
			return ;
		}
		return {
			game_id: $("#J_paramGameId").val(),
			gpzs_id: $("#J_zoneServer").find(".selected-item").attr("data-id").split("_")[0],
			node_id: select.attr("node_id"),
			file_name: select.text(),
			from: $("#J_from").val(),
			to: $("#J_to").val()
		};
	}

	/**
	 * @brief _zoneServer_fac
	 * 生成区服列表并绑定事件
	 * @param data array 区服列表
	 * @return
	 */
	function _zoneServer_fac( data, firstClick ){
		firstClick = firstClick ? firstClick : false;
		var $zoneServer = $("#J_zoneServer");

		$.Select.setOptionContent( $zoneServer, data );
		$.Select.bindEvents( $zoneServer, firstClick, function(){
			refresh();
		});
	}
	/**
	 * @brief getZoneServer
	 * 获取区服列表
	 * @param id ：平台id
	 * @return
	 */
	function getZoneServer( id, fn ){
		ajax(getUrl("common", "gpzs", "getZoneServer"), {
			game_id : $("#J_paramGameId").val(),
			platform_id : id
		}, function(res){
			if (res.result == 0) {
				var data = [];
				$.each( res.data, function( i ){
					data.push({
						id : this.gpzs_id + "_" + this.zone_id,
						name : ( this.zone_id == -1 && this.server_id == -1 ) ? lang.t("全区全服") : this.gpzs_name,
						selected : i == 0 ? true : false
					});
				});
				if( fn )fn( data );
			} else {
				say(lang.t("获取数据错误：") + res.err_desc);
			}
		});
	}

})(window);

var getGameId = function () {
	return { 'game_id': $("#J_paramGameId").val() };
};

var getGpzsId = function () {
	return { 'gpzs_id': $("#J_zoneServer").find(".selected-item").attr("data-id").split("_")[0] };
};

var getType = function () {
	return { 'type': 'report' };
};

var getReportData = function (root, td) {
	var judge = root.parent().parent().parent().parent().parent().parent().parent();
	
	if (judge.hasClass('tabs-wrapper')) {
		var rid = judge.find('ul').find('.tabs-active').attr('data-id').split('_')[1];
		return {
			'r_id': rid,
			'data_id': td.attr('dataid')
		};
	} else {
		return { 'r_id': td.attr('dataid').split('_')[1] };
	}
};

var getPeriods = function () {
	$date = $("#J_date");
	return { periods: [{
		from: $date.val().split("~")[0],
		to: $date.val().split("~")[1]
	}] };
};

var getFilename = function () {
	var date = new Date();
	return { 'file_name': date.toLocaleString() + '的查询' };
};

var getFilterInfo = function () {
	return { 'filter_info': [1] };    // default is 1
};

var blinkTag = function () {
	$('.selfhelp-tag').hint(1, '#FFA500');
};

/**
 * @brief ajaxData
 * ajax请求数据
 * @param url：请求url链接
 * @param param：参数
 * @param fn：回调函数
 * @param hide：是否显示overlay提醒
 */
function ajaxData(url, param, fn, hide) {
	if(hide) overlayer({ text: lang.t("操作中...")});
	ajax(url, param, function(res){
		if(res.result == 0){
			if(hide) hidelayer(lang.t("操作成功~.~"));
			if(fn) fn(res.data);
		} else {
			if(hide) hidelayer();
			say(lang.t("获取数据错误：") + res.err_desc);
		}
	}, "POST");
}

/**
 * @brief tableEvent
 * 用于操作表格
 * 在生成表格过程中通过 _trigger 触发
 */
window.tableEvent = function () {
	$('.fixed-table').find('.selfhelp').die().live('click', function () {
		var idx = $(this).parent().parent().index();
		var root = $(this).parent().parent().parent().parent().parent().parent();
		var tdWanted = root.find('.fixed-body').find('tbody').find('tr').eq(idx).children().eq(2);

		var param = $.extend({
				'game_name': $('li').find('.selected-item-white').attr('title')
			}, getGameId(), {
				'items': [ $.extend({
					'item_name': tdWanted.attr('title')
				}, getType(), getGpzsId(), getReportData(root, tdWanted)) ]
			});

		// 发送请求后台刷新 session
		ajaxData(getUrl("common", "basket", "addBasketInfo"), param, function(data) {
			if (data) {
				blinkTag();
			}
        }, false, true);
	});

	$('.fixed-table').find('.myfavor').die().live('click', function () {
		$('.fixed-table').find('.myfavor').removeClass('favor-checked');
		$(this).addClass('favor-checked');
		$('#J_addFavorBtn').trigger('click');
	});
};

window.fetchTags = function (tagName) {
	//TODO: 
};
