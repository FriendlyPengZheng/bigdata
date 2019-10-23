/**
 * @fileOverview 我的收藏的 View 部分
 * @name favor_view.js
 * @author Maverick youngleemails@gmail.com
 */

$.extend({
    favorfac : function(options) {
        var opts = {
            clsname: "widget-sel", title: '', type: 0, add: false,
            attr: {}, css: {}, input: {}, radio: []
        };
        $.extend( opts, options );
        var sel = $(document.createElement("div")).addClass(opts.clsname).attr(opts.attr).css(opts.css),
            s = opts.title ? ('<h4 class="title">' + opts.title + '</h4>') : '';
        switch( opts.type ) {
        case 1:    //input
            s += $._input(opts.input);
            break;
        case 2:    //select
            s += '<div class="sel-wrapper"></div>'
                + (opts.add ? '<a href="javascript:void(0);" class="add-m">Add</a>' : '');
            break;
        case 3:    //radio
            s += $._radio(opts.radio);
            break;
        case 4:    //notice
            s += '<span class="note">'
                + '<span class="c-red">'
                + lang.t("注：")
                + '</span>'
                + lang.t("表格显示形式可支持{1}条数据，其他显示形式可支持{2}条数据。",
                         '<span class="c-red">1000</span>',
                         '<span class="c-red">100</span>')
                + '</span>';
            break;
        case 5:
            s += '<ul id="J_sortable" class="sortable"></ul>'
                + (opts.add ? '<a href="javascript:void(0);" class="add-m">Add</a>' : '');
            break;
            break;
        default:
            break;
        }
        sel.html(s);
        return sel.get(0).outerHTML;
    },
    _input: function(data) {
        return '<input type="text" class="title-txt necessary" '
            + (data.name ? 'name="' + data.name + '"': '')
            + (data.id ? ' id="' + data.id + '"' : '')
            + (data.placeholder ? ' placeholder="' + data.placeholder + '"' : '')
            + ' value="' + (data.value ? data.value : ( data.dftName ? data.dftName : '')) + '"> ';
    },
    _radio: function(data) {
        var s = '';
        if(data && data.length){
            $.each(data, function(){
                s += '<label class="mr10">'
                    + '<input type="radio" name="'
                    + this.name
                    + '" value="'
                    + this.value
                    + '" '
                    + ( this.checked ? 'checked' : '')
                    + '/>'
                    + this.text
                    + '</label>';
            });
        }
        return s;
    },
    widgetfac: function(options) {
        var s = '<div class="widget-type-wrapper clearfix" id = "' + options.id + '">'
                + '<div class="widget-type-w clearfix" >';

        $.each(options.widget, function(i){
            s += '<div class="widget-type-con fl '
                + (i == 0 ? 'active' : '')
                + '" data-type="' + this.value + '">'
                + '<div class="type-tag ' + this.classname + '"></div>'
                + '<div class="type-name">' + this.name + '</div>'
                + '</div>';
        });
        s += '</div></div>';
        return s;
    }
});

function _createAdjustTable(data_name, data_key, data_num) {
    var adjTable = $("#J_adjustTable"),
        adjUl = adjTable.find(".sortable"),
        adjLi = $(document.createElement("li")).addClass('ui-state-default clearfix')
            .addClass("stat-listener")
            .attr({ 'stid': '我的收藏', 'sstid': '编辑收藏项操作', 'item': '收藏指标拖动' }),
        adjIpt = $(document.createElement("input")).val(data_name).addClass('editing-field').hide(),
        adjSpan  = $(document.createElement("span")).text(data_name)
            .attr({ 'data-key': data_key, 'border': 0, 'outline': 0 })
            .addClass("edit-field")
            .click(function (event) {
                event.stopPropagation();
            }),
        delM = $(document.createElement("span")).addClass('close icon-close stat-listener')
            .attr({ 'stid': '我的收藏', 'sstid': '编辑收藏项操作', 'item': '收藏指标删除' }),
        edtM = $(document.createElement("span")).addClass('edit icon-edit');

    if (adjUl.find('li').length < data_num) {
        adjSpan.add(delM).add(edtM).add(adjIpt).appendTo(adjLi.appendTo(adjUl));
    }

    // 点击删除按钮的动作
    delM.click(function (e) {
        e.stopPropagation();
        
        msglog($(this).attr('stid'), $(this).attr('sstid'), $(this).attr('item'));
        $(this).parent().remove();
    });
}

var _setAdjTable = function () {
    var adjUl = $('#J_sortable'),
        adjLi = adjUl.find('li'),
        adjSpan = adjUl.find('.edit-field'),
        adjIpt = adjUl.find('input'),
        edtM = adjUl.find('.edit'),
        delM = adjUl.find('.close'),
        blurFlag = 0;

    var _isSpan = function (t) {
        var curLi = t.parent(),
            curSpan = curLi.find('.edit-field'),
            curInput = curLi.find('.editing-field');
        if (curInput.css('display') === "none")
            return true;
        return false;
    };

    var _isInput = function (t) {
        var curLi = t.parent(),
            curSpan = curLi.find('.edit-field'),
            curInput = curLi.find('.editing-field');
        if (curSpan.css('display') === "none")
            return true;
        return false;        
    };

    var _fixedLi = function (t) {
        var curEdit = t,
            curClose = t.siblings('.close'),
            curLi = t.parent();
        edtM.each(function () {
            if (_isInput($(this)) && $(this) != curEdit) {
                _inputToSpan($(this));
            }
        });
        
        adjLi.each(function () {
            _unlockCurLi($(this));
        });
        _lockCurLi(curLi);
    };

    // 锁定当前 li
    var _lockCurLi = function (li) {
        var curEdit = li.find('.edit'),
            curClose = li.find('.close'),
            curUl = li.parent();
        curEdit.show();
        curClose.show();
        li.unbind('mouseenter mouseleave');
        adjLi.each(function () {
            $(this).css('cursor', 'default');
        });
        curUl.sortable("disable");
    };

    // 解锁当前 li
    var _unlockCurLi = function (li) {
        var curEdit = li.find('.edit'),
            curClose = li.find('.close'),
            curUl = li.parent();
        curEdit.hide();
        curClose.hide();
        li.hover(function () {
            $(this).find('.edit').show();
            $(this).find('.close').show();
        }, function () {
            $(this).find('.edit').hide();
            $(this).find('.close').hide();
        });
        adjLi.each(function () {
            $(this).css('cursor', 'move');
        });
        curUl.sortable("enable");
    };

    var _spanToInput = function (t) {
        var curLi = t.parent(),
            curSpan = curLi.find('.edit-field'),
            curInput = curLi.find('.editing-field');
        curInput.width(parseInt(curLi.width() * 0.8));    // 调整一下宽度
        curSpan.hide();
        curInput.show();
    };

    var _inputToSpan = function (t) {
        var curLi = t.parent(),
            curSpan = curLi.find('.edit-field'),
            curInput = curLi.find('.editing-field'),
            newVal = curInput.val();
        curSpan.text(newVal);
        curSpan.show();
        curInput.hide();
		_widthAdjust();
    };

	var _widthAdjust = function () {
		var contWidth = 0;
		adjLi.each(function () {
			var tmpWidth = parseFloat($(this).children(':first').width());
			if (contWidth < tmpWidth) contWidth = tmpWidth;
		});
        var newWidth = parseInt(contWidth / 2) * 3;
        if (newWidth > 300) $('.ui-dlg').css('min-width', String(newWidth) + 'px');
        else $('.ui-dlg').css('min-width', '300px');
	};

    var _focus = function (t) {
        var curLi = t.parent(),
            curInput = curLi.find('.editing-field');
        curInput.trigger("focus");
    };

    var _offFocus = function (t) {
        var curLi = t.parent(),
            curInput = curLi.find('.editing-field');
        curInput.trigger("blur", "inside");
    };

    var _backgroundChange = function (t, type) {
        var curLi = t.parent(),
            curEdit = curLi.find('.edit');
        switch(type) {
        case 'save':
            curEdit.removeClass('icon-edit').addClass('icon-edit-save');
            break;
        case 'edit':
            curEdit.addClass('icon-edit').removeClass('icon-edit-save');
            break;
        };
    };

    edtM.hide();
    delM.hide();

    adjLi.hover(function () {
        $(this).find('.edit').show();
        $(this).find('.close').show();
    }, function () {
        $(this).find('.edit').hide();
        $(this).find('.close').hide();
    });

    //FIXME: 这里到 _spanToInput 时,单项目的 input 不能点击进入 focus 状态
    edtM.on("click", function () {
        if ($(this).hasClass('icon-edit')) {
            blurFlag = 0;
        }
        
        if (_isSpan($(this)) && blurFlag === 0) {
            _fixedLi($(this));    // 锁定当前 li, 解锁其余 li
            _spanToInput($(this));    // 把 span 转换为 input
            _focus($(this));    // 点击编辑时就把 input 设置为 focus
            _backgroundChange($(this), "save");    // 更改为保存图标
        } else if (_isInput($(this))) {
            _inputToSpan($(this));    // 把 input 转换为 span
            // _offFocus($(this));    // 完成编辑把 input 从 focus 状态下解放出来(目前看不需要这个模块)
            _backgroundChange($(this), "edit");    // 更改回编辑图标
        }
    });

    // 窗口捕获点击一定要在编辑按钮之后
    $(window).click(function () {
        blurFlag = 0;
    });

    adjIpt.on("blur", function (event, location) {
        var curLi = $(this).parent(),
            curEdit = curLi.find('.edit'),
            curClose = curLi.find('.close');
        curEdit.addClass('icon-edit').removeClass('icon-edit-save');
        // 在点击保存时直接这么搞会先 blur, 然后触发点击,这时文本处于未编辑状态,这样会导致逻辑进入编辑状态
        // 解决了这个问题,在 blur 后标记一个状态,这个状态会影响文本处于 span 还是 input 的判断
        _inputToSpan(curEdit);
        _unlockCurLi(curLi);
        blurFlag = 1;
    });

    adjLi.click(function () {
        var curEdit = $(this).find('.edit'),
            curClose = $(this).find('.close'),
            boolEdit = $(this).find('.edit').css('display') === "none" ? true : false,
            boolClose = $(this).find('.close').css('display') === "none" ? true : false;
        if ((boolEdit === true) && (boolClose === true)) {
            curEdit.show();
            curClose.show();
        }
    });
};
