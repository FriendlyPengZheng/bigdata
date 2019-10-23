/**
 * Dlg Dialog ( jQuery extension )
 * <pre>A new dialog</pre>
 *
 * = require jQuery =
 *
 * @author nemo
 * @date   2011-05-20
 * @version beta 2
 *
 * update history:
 * 2011-04-21 迁移入itl做相应改变
 * 2011-04-25 遮罩大小调整
 * 2011-05-20 去掉alert框的header和bottom
 * 2013-09-18 warningDialog增加warningType, 增加用于操作的弹出框popup
 */
/**
 * 类中用到的class
 * ui-dlg
 * ui-header-common
 * ui-header-simple
 * ui-title
 * ui-cont
 * ui-hgt16
 * ui-bottom
 * ui-content
 * btn-s-dft btn-s btn-l-dft:按钮
 * err-l ok-l warning-l:提示图标
 *
 */
(function($){
var DLG_MASK_COUNT = 0;
$.extend({
Dlg : {
    /**
     * 常量定义
     * <pre>其中有些项可以配置</pre>
     * @access private
     */
    CONST : {
        VOID_LINK : "javascript:void(0)",
        CLOSE_BUTTON_TITLE : "关闭",
        DEFAULT_TITLE : "消息",
        DEFAULT_CONFIRM_TEXT : "您确定要这么做吗？",
        DEFAULT_WANING_TEXT : "请等待！",
        MESSAGE_DIALOG_BUTTON : "好的",
        OK_BUTTON_TEXT : "确定",
        SAVE_BUTTON_TEXT : "保存",
        YES_BUTTON_TEXT : "是",
        NO_BUTTON_TEXT : "否",
        CANCEL_BUTTON_TEXT : "取消",
        MASK_COLOR : "#999",
        MASK_OPACITY : 0.7,
        // ---------------- css style class -----------------
        CSS_DLG : "ui-dlg",
        CSS_HEADER_COMMON : "ui-header-common",
        CSS_HEADER_SIMPLE : "ui-header-simple",
        CSS_TITLE : "ui-title",
        CSS_CONT : "ui-cont",
        CSS_BOTTOM : "ui-bottom",
        CSS_CONTENT : "ui-content",
        CSS_TEXT : "ui-hgt16",
        CSS_BTNLARGE : "btn-l-dft",
        CSS_BTNSMALL : "btn-s-dft",
        CSS_WARNING : "warning-l",
        CSS_OK : "ok-l",
        CSS_ERROR : "err-l",
        //------normally will not change these bellow--------
        BLANK_STRING : "",
        HTML_DIV : "div",
        HTML_SPAN : "span",
        HTML_A : "a",
        HTML_IFRAME : "iframe",
        HTML_H3 : "h3",
        UNDEFINED : "undefined",
        AJAX_ERROR_MSG : "网络错误，请稍候再试..."
    },

    /**
     * Dialog工具，简化使用 ( 简单入口 )
     */
    Util : {
        /**
         * alert框
         * @access public
         *
         * @params obj {Object} 任意想输出的内容
         * @params right {boolean} 是否是正确的信息
         * @return {AlertDialog} 返回这个Dialog
         */
        alert : function( obj, right, callback ){
            var dialog = new $.Dlg.DialogFactory({
                    dialogType : ( right ? $.Dlg.DialogType.ALERT.RIGHT : $.Dlg.DialogType.ALERT.ERROR ),
                    alertText : obj.toString()
                }).getInstance();

            dialog.show(callback);
            return dialog;
        },

        /**
         * confirm框
         * @access public
         *
         * @params title {string} 标题
         * @params text {string} 提示文字
         * @params ok {Function} 按下确认触发
         * @params cancel {Function} 按下取消触发
         * @return {ConfirmDialog} 返回这个Dialog
         */
        confirm : function( title, text, ok, cancel, close ) {
            var dialog = new $.Dlg.DialogFactory({
                dialogType : $.Dlg.DialogType.CONFIRM,
                title : title,
                confirmText : text,
                closeButtonEvent : close
            }).getInstance();

            dialog.setOkHandler(ok);
            dialog.setCancelHandler(cancel);
            dialog.show();
            return dialog;
        },

        /**
         * popup框
         * @access public
         *
         * @params id {string} id
         * @params title {string} 标题
         * @params contentHtml {string} 内容区
         * @params save {Function} 按下保存触发
         * @params cancel {Function} 按下取消触发
         * @params close {Function} 按下关闭触发
         * @params callback {Function} 生成好弹出框后触发
         * @return {ConfirmDialog} 返回这个Dialog
         */
        popup : function( param ) {
            var dialog = new $.Dlg.DialogFactory({
                dialogType : $.Dlg.DialogType.POPUP,
                title : param.title ? param.title : $.Dlg.CONST.DEFAULT_TITLE,
                closeButtonEvent : param.close,
                saveCallback : param.saveCallback ? param.saveCallback : null
            }).getInstance();
            var container = dialog.getContainer();

            $(container).attr("id" , param.id ? param.id : "J_popup" );
            if( param.contentHtml ){ dialog.setConfirmHtml( param.contentHtml); }
            if( param.save ) { dialog.setSaveHandler( param.save ); }
            if( param.cancel ) { dialog.setCancelHandler(param.cancel); }
            if( param.callback ) param.callback( $(container) );
            if( param.width ) $(container).width( param.width );
            return dialog;
        },
        /**
         * message框
         * @access public
         *
         * @params title {string} 标题
         * @params text {string} 提示文字
         * @params contentHtml {string} 提示html内容
         * @params buttonText {string} 按钮文字
         * @return {MessageDialog} 返回这个Dialog
         */
        message : function(title, text, buttonText ) {
            var dialog = new $.Dlg.DialogFactory({
                    dialogType : $.Dlg.DialogType.MESSAGE,
                    title : title,
                    confirmText : text,
                    okButtonText : ( buttonText == undefined ? $.Dlg.CONST.MESSAGE_DIALOG_BUTTON : buttonText)
                }).getInstance();

            //dialog.show();
            return dialog;
        },

        /**
         * remote框
         * @access public
         *
         * @params url {string} 要加载的远程URL
         * @return {RemoteDialog} 返回这个Dialog
         */
        remote : function(url) {
            var dialog = new $.Dlg.DialogFactory({
                    dialogType : $.Dlg.DialogType.COMMON,
                    url : url
                }).getInstance();

            dialog.show();
            return dialog;
        },
        /**
         * warning框
         * @access public
         *
         * @params title {string} 标题
         * @params text {string} 提示文字
         * @params type {int} 警告框类型
         * @return {WarningDialog} 返回这个Dialog
         */
        warning : function(title, text, type) {
            var dialog = new $.Dlg.DialogFactory({
                    dialogType : $.Dlg.DialogType.WARNING,
                    title : title,
                    warningText : text,
                    warningType : type ? type : 0
                }).getInstance();

            dialog.show();
            return dialog;
        }
    },

    /**
     * Dialog类型
     * 当作枚举
     * @access public
     */
    DialogType : {
        /**
         * 普通类型，包含关闭按钮，不会自动关闭
         */
        COMMON : 1,
        /**
         * 提示信息，没有关闭按钮，自动延迟关闭
         */
        ALERT : {
            RIGHT : 2, // 正确信息
            ERROR : 3  // 错误信息
        },
        /**
         * 确认信息。有OK/CANCEL,YES/NO按钮
         */
        CONFIRM : 4,
        /**
         * 单纯信息。只有OK按钮
         */
        MESSAGE : 5,
        /**
         * 警告信息，不含关闭按钮，不会自动关闭，可以用于loading
         */
        WARNING : 6,
        /**
         * 一般弹出框，用于操作事件，内容需要HTML填充
         * OK/CANCEL按钮
         */
        POPUP : 7
    },

    /**
     * 按钮类型
     * 当作枚举
     * @access public
     */
    ButtonType : {
        /**
         * 没有按钮
         */
        NULL : 0,
        /**
         * "确定"，"我知道了"等按钮
         */
        OK : 1,
        /**
         * "是"、"否"按钮
         */
        YESNO : 2,
        /**
         * "确定"，"取消"按钮
         */
        OKCANCEL : 3,
        /**
         * "保存"，"取消"按钮
         */
        SAVECANCEL : 4
    },

    /**
     * 警告类型
     * 当作枚举
     * @access public
     */
    WarningType : {
        /**
         * smile
         */
        WARNING : 0,
        /**
         * right
         */
        OK : 1,
        /**
         * error
         */
        ERROR : 2
    },
    /**V
     * @brief function
     *
     * @return
     */
    _get_warning_class : function( type ){
        var iconClass = '';

        switch( type ){
            case $.Dlg.WarningType.WARNING :
                iconClass = $.Dlg.CONST.CSS_WARNING;
                break;
            case $.Dlg.WarningType.OK :
                iconClass = $.Dlg.CONST.CSS_OK;
                break;
            case $.Dlg.WarningType.ERROR :
                iconClass = $.Dlg.CONST.CSS_ERROR;
                break;
            default :
                break;
        }

        return iconClass;
    },
    /**
     * Class 获得dialog的工厂，需要传入参数
     * @access public
     *
     * @params options {json} 所有支持的options列表
     * --------------------------------------------
     * dialogType        弹框类型。默认为$.Dlg.DialogType.MESSAGE
     * buttonType        按钮类型。默认为$.Dlg.ButtonType.NULL
     * warningType       警告类型。默认为$.Dlg.WarningType.WARNING
     * maskZIndex        遮罩层级。默认为80
     * dialogZIndex      弹框层级。默认为90
     * closeDelay        自动关闭延迟，用于ALERT类型框。默认为1500ms
     * fadeSpeed         对话框淡出动画执行时间，默认500ms
     * contentHtml       内容区域HTML，用于自定义体。 默认为空
     * url               COMMON弹框加载内容的URL。默认不定义
     * title             COMMON类型框的title。 默认为$.Dlg.CONST.DEFAULT_TITLE
     * message           MessageDialog的提示信息。默认为空
     * confirmText       Confirm弹框的确认文字信息。 默认为$.Dlg.CONST.DEFAULT_CONFIRM_TEXT
     * alertText         AlertDialog的提示文字。默认为空
     * hideTarget        弹框时要隐藏的对象。默认为null
     * width             弹框的宽度。默认为css样式表中定义的宽度
     * -----------------------
     * okButtonText      ok按钮的文字。默认"确定"
     * yesButtonText     yes按钮的文字。默认"是"
     * noButtonText      no按钮的文字。默认"否"
     * cancelButtonText  cancel按钮的文字。默认"取消"
     * closeButtonEvent  close按钮触发事件。默认为null
     * okButtonEvent     ok按钮触发事件。或见Dialog.setOkHandler
     * yesButtonEvent    yes按钮触发事件。或见Dialog.setYesHandler
     * noButtonEvent     no按钮触发事件。或见Dialog.setNoHandler
     * cancelButtonEvent cancel按钮触发事件。或见Dialog.setCancelHandler
     */
    DialogFactory : function( options ){
        var opts = {
            dialogType :  $.Dlg.DialogType.MESSAGE,
            buttonType : $.Dlg.ButtonType.NULL,
            warningType : $.Dlg.WarningType.WARNING,
            maskZIndex : 80,
            dialogZIndex : 90,
            closeDelay : 1500,
            fadeSpeed  : 500,
            contentHtml : $.Dlg.CONST.BLANK_STRING,
            title : $.Dlg.CONST.DEFAULT_TITLE,
            message : $.Dlg.CONST.BLANK_STRING,
            confirmText : $.Dlg.CONST.DEFAULT_CONFIRM_TEXT,
            alertText : $.Dlg.CONST.BLANK_STRING,
            warningText : $.Dlg.CONST.DEFAULT_WARNING_TEXT,
            hideTarget : null,
            //-------------------
            okButtonText : $.Dlg.CONST.OK_BUTTON_TEXT,
            saveButtonText : $.Dlg.CONST.SAVE_BUTTON_TEXT,
            yesButtonText : $.Dlg.CONST.YES_BUTTON_TEXT,
            noButtonText : $.Dlg.CONST.NO_BUTTON_TEXT,
            cancelButtonText : $.Dlg.CONST.CANCEL_BUTTON_TEXT,
            closeButtonEvent : null,
            okButtonEvent : null,
            yesButtonEvent : null,
            noButtonEvent : null,
            cancelButtonEvent : null
        };
        $.extend( opts, options );

        this.getInstance = function(){
            var dialog = null;

            switch ( opts.dialogType ){
                case $.Dlg.DialogType.COMMON :
                    dialog = new $.Dlg.CommonDialog(opts);
                    break;
                case $.Dlg.DialogType.ALERT.RIGHT :
                case $.Dlg.DialogType.ALERT.ERROR :
                    dialog = new $.Dlg.AlertDialog( opts );
                    break;
                case $.Dlg.DialogType.CONFIRM :
                    // 按钮类型为YESNO或OKCANCEL两种
                    if( opts.buttonType != $.Dlg.ButtonType.YESNO && opts.buttonType != $.Dlg.ButtonType.OKCANCEL ){
                        opts.buttonType = $.Dlg.ButtonType.OKCANCEL;
                    }
                    dialog = new $.Dlg.ConfirmDialog( opts );
                    break;
                case $.Dlg.DialogType.POPUP :
                    // 按钮类型为OKCANCEL
                    opts.buttonType = $.Dlg.ButtonType.SAVECANCEL;
                    dialog = new $.Dlg.ConfirmDialog( opts );
                    break;
                case $.Dlg.DialogType.WARNING :
                    dialog = new $.Dlg.WarningDialog(opts);
                    break;
                default :// (MESSAGE)
                    // 按钮类型为OK
                    opts.buttonType = $.Dlg.ButtonType.OK;
                    dialog = new $.Dlg.ConfirmDialog(opts);
                    break;
            }
            dialog.options = opts;
            var container = dialog.getContainer();

            $(container).dragIt({
                holder: "." + $.Dlg.CONST.CSS_TITLE,
                limit: false
            });
            return dialog;
        };
    },

    /**
     * Class 自定义通用类型弹框 $.Dlg.DialogType.COMMON
     * @access public
     *
     * @params options {json} 由Dialog工厂传入的参数
     */
    CommonDialog : function(options) {
        // private fields
        var self = this,
            manager = new $.Dlg.DialogManager(options),
            beforeCloseHandler = null;

        var EVENT = {
            SHOW : "show",
            HIDE : "hide",
            BEFORE_CLOSE : "beforeClose",
            SUCCESS : "success",
            ERROR : "error"
        };

        var container = document.createElement($.Dlg.CONST.HTML_DIV),    // 框架对象
            header  = document.createElement($.Dlg.CONST.HTML_DIV),
            btnClose = document.createElement($.Dlg.CONST.HTML_A),
            title = document.createElement( $.Dlg.CONST.HTML_H3 ),
            content = document.createElement($.Dlg.CONST.HTML_DIV),
            bottom  = document.createElement($.Dlg.CONST.HTML_DIV);

        $(container).addClass($.Dlg.CONST.CSS_DLG).css({
            position: "absolute",
            zIndex: options.dialogZIndex
        });
        if (options.width) {
            $(container).css("width", options.width);
        }
        $(header).addClass($.Dlg.CONST.CSS_HEADER_COMMON);
        $(content).addClass($.Dlg.CONST.CSS_CONT);
        $(bottom).addClass($.Dlg.CONST.CSS_BOTTOM);

        // 填充header
        btnClose.href = $.Dlg.CONST.VOID_LINK;
        btnClose.title = $.Dlg.CONST.CLOSE_BUTTON_TITLE;
        $(btnClose).text("x");
        $(title).addClass( $.Dlg.CONST.CSS_TITLE ).html(options.title);
        $(header).append( $(title).add( $(btnClose) ) );
        // 填充content
        var contentContainer = null;

        if( typeof options.url == $.Dlg.CONST.UNDEFINED ){
            contentContainer = document.createElement($.Dlg.CONST.HTML_DIV );
            $(contentContainer).addClass($.Dlg.CONST.CSS_CONTENT).html(options.contentHtml).appendTo($(content));
        } else {
            contentContainer = content;
            $.ajax({
                url : options.url,
                success : function(html) {
                    $(container).trigger(EVENT.SUCCESS);
                    $(content).html(html);
                },
                error : function() {
                    $(container).trigger(EVENT.ERROR);
                    $(content).append($.Dlg.CONST.AJAX_ERROR_MSG);
                }
            });
        }

        /**
         * 组装按钮区域
         * @access private
         *
         * @params self {Dialog} 自己
         */
        var setupBottom = function() {
            new $.Dlg.ButtonFactory(options).applyTo(self);
        };

        // 组装框架并附加到body中去
        $(container).hide()
                .append(header).append(content).append(bottom)
                .appendTo(document.body);

        // 绑定事件
        $(btnClose).click(function() {
            if ($.isFunction(options.closeButtonEvent)) {
                options.closeButtonEvent();
            }
            self.fadeOut(false);
        });

        /**
         * 附加按钮事件 ok
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setOkHandler = function(handler) {
            options.okButtonEvent = handler;
            setupBottom();
        };

        /**
         * 附加按钮事件 yes
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setYesHandler = function(handler) {
            options.yesButtonEvent = handler;
            setupBottom();
        };

        /**
         * 附加按钮事件 no
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setNoHandler = function(handler) {
            options.noButtonEvent = handler;
            setupBottom();
        };

        /**
         * 附加按钮事件 cancel
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setCancelHandler = function(handler) {
            options.cancelButtonEvent = handler;
            setupBottom();
        };

        /**
         * 当加载URL成功后触发
         * @access public
         *
         * @params fn {Function}
         */
        this.success = function(fn) {
            if ($.isFunction(fn)) {
                $(container).bind(EVENT.SUCCESS, fn);
            } else if (fn == undefined) {
                $(container).trigger(EVENT.SUCCESS);
            } else {
                throw new $.Dlg.paramTypeException(0, (typeof fn));
            }
        };

        /**
         * 当加载URL失败后触发
         * @access public
         *
         * @params fn {Function}
         */
        this.error = function(fn) {
            if ($.isFunction(fn)) {
                $(container).bind(EVENT.ERROR, fn);
            } else if (fn == undefined) {
                $(container).trigger(EVENT.ERROR);
            } else {
                throw new $.Dlg.paramTypeException(0, (typeof fn));
            }
        };

        /**
         * 当试图关闭对话框前
         * @access public
         *
         * @params fn {Function} 若fn返回false则阻止close事件
         */
        this.beforeClose = function(fn) {
            beforeCloseHandler =  fn;
        };

        /**
         * 检查是否能够关闭
         * @access private
         *
         * @return {boolean} 能否被关闭 通过了beforeCloseHandler的约束
         */
        var canBeClosed = function() {
            return (!$.isFunction(beforeCloseHandler) || beforeCloseHandler());
        };

        /**
         * 显示该对话框 show，或绑定事件。 触发事件show
         * @access public
         *
         * @params param {mixed} how to show or bind event
         */
        this.show = function(param) {
            if ($.isFunction(param)) {
                // bind action to event
                $(container).bind(EVENT.SHOW, param);
            } else {
                manager.showMask();
                // exec action and trigger event
                // when use show's callback, only one parameter works
                param = param == undefined ? 0 : param;
                $(container).show(param, function() {
                    $(this).trigger(EVENT.SHOW);
                });
                $(bottom).find("." + $.Dlg.CONST.CSS_BTNSMALL + ", ." + $.Dlg.CONST.CSS_BTNLARGE ).filter(":first").focus();
            }
        };

        /**
         * 显示该对话框 fadeIn，或绑定事件。触发事件show
         * @access public
         *
         * @params param {mixed} how to fade or bind event
         */
        this.fadeIn = function(param) {
            if ($.isFunction(param)) {
                // bind action to event
                $(container).bind(EVENT.SHOW, param);
            } else {
                manager.showMask();
                // exec action and trigger event
                param = param == undefined ? 0 : param;
                $(container).fadeIn(param, function() {
                    $(this).trigger(EVENT.SHOW);
                });
            }
        };

        /**
         * 关闭该对话框 hide，或绑定事件。 触发事件hide
         * @access public
         *
         * @params param {mixed} how to hide or bind event
         */
        this.hide = function(param) {
            if ($.isFunction(param)) {
                // bind action to event
                $(container).bind(EVENT.HIDE, param);
            } else if (param == false || canBeClosed()) {
                manager.hideMask();
                // exec action and trigger event
                // when use show's callback, only one parameter works
                param = param == undefined ? 0 : param;
                $(container).hide(param, function() {
                    $(this).trigger(EVENT.HIDE);
                });
            }
        };

        /**
         * 关闭该对话框 fadeOut，或绑定事件。触发事件hide
         * @access public
         *
         * @params param {mixed} how to fade or bind event
         */
        this.fadeOut = function(param) {
            if ($.isFunction(param)) {
                // bind action to event
                $(container).bind(EVENT.HIDE, param);
            } else if (param === false || canBeClosed()) {
                manager.hideMask();
                // exec action and trigger event
                param = param == undefined ? options.fadeSpeed : param;
                $(container).fadeOut(param, function() {
                    $(this).trigger(EVENT.HIDE);
                });
            }
        };

        /**
         * 获得弹框容器对象
         * @access public
         *
         * @return {HTMLDiv} 弹框最大的容器
         */
        this.getContainer = function() {
            return container;
        };

        /**
         * 获得弹框主自定义内容区
         * @access public
         *
         * @return {HTMLDiv} 弹框自定义的内容区
         */
        this.getContent = function() {
            return contentContainer;
        };

        /**
         * 获得弹框容器底部
         * @access public
         *
         * @return {HTMLDiv} 弹框底部容器
         */
        this.getBottom = function() {
            return bottom;
        };

        /**
         * 得到弹框主体内容的HTML
         * @access public
         *
         * @return {string} HTML格式内容
         */
        this.getContentHtml = function() {
            return $(contentContainer).html();
        };

        /**
         * 设置弹框主体内容的HTML
         * @access public
         *
         * @params html {string} HTML格式内容
         */
        this.setContentHtml = function(html) {
            $(contentContainer).html(html);
        };

        // 填充bottom
        if (typeof options.url == $.Dlg.CONST.UNDEFINED) {
            setupBottom();
        }

        /**
         * @brief function
         * 得到控制弹出窗口的类
         *
         * @return {object}
         */
        this.getManager = function() {
            return manager;
        };

        manager.setPosition(this);
    },

    /**
     * Class Alert形式的Dialog
     * @access public
     *
     * @params options {json} 由Dialog工厂传入的参数
     */
    AlertDialog : function( options ){
        // private fields
        var self = this,
            manager = new $.Dlg.DialogManager( options ),
            timer = null;
        var container = document.createElement($.Dlg.CONST.HTML_DIV), // 框架对象
            content = document.createElement($.Dlg.CONST.HTML_DIV), //内容区的wrapper
            contentContainer = document.createElement($.Dlg.CONST.HTML_DIV), //填充content
            icon = document.createElement($.Dlg.CONST.HTML_SPAN); //图标

        $(container).addClass( $.Dlg.CONST.CSS_DLG ).css({
            position: "absolute",
            zIndex: options.dialogZIndex
        });
        $(content).addClass( $.Dlg.CONST.CSS_CONT );
        $(contentContainer).addClass( $.Dlg.CONST.CSS_CONTENT );
        if( options.dialogType == $.Dlg.DialogType.ALERT.ERROR ){
            $(icon).addClass("err-l");
        } else {
            $(icon).addClass("ok-l");
        }

        $(content).append($(contentContainer).append($(icon).text( options.alertText )));
        // 组装框架并附加到body中去
        $(container).hide()
                .append(content)
                .appendTo(document.body);

        /**
         * 显示该对话框 show
         * @access public
         *
         * @params hideHandler {Function} 自动消失后的回调函数
         */
        this.show = function(hideHandler) {
            window.clearTimeout( timer );
            manager.showMask();
            $(container).show();
            timer = window.setTimeout( function(){
                self.fadeOut( options.fadeSpeed, hideHandler );
            }, options. closeDelay );
        };

        /**
         * 关闭该对话框 hide
         * @access public
         *
         * @params param {mixed} how to fade
         */
        this.hide = function(param) {
            manager.hideMask();
            $(container).hide(0, param);
        };

        /**
         * 关闭该对话框 fadeOut
         * @access public
         *
         * @params speed {mixed} fade speed
         * @params callBack {Function} callback
         */
        this.fadeOut = function(speed, callBack) {
            manager.hideMask();
            if (speed == undefined) {
                speed = options.fadeSpeed;
            }
            $(container).fadeOut(speed, callBack);
        };

        /**
         * 获得弹框容器对象
         * @access public
         *
         * @return {HTMLDiv} 弹框最大的容器
         */
        this.getContainer = function() {
            return container;
        };
        /**
         * 获得弹框主自定义内容区
         * @access public
         *
         * @return {HTMLDiv} 弹框自定义的内容区
         */
        this.getContent = function() {
            return contentContainer;
        };

        manager.setPosition(this);
    },

    /**
     * Class 弹框管理器
     * 遮罩层
     * @access public
     */
    DialogManager : function( options ){
        this.options = options;
        var mask = document.createElement( $.Dlg.CONST.HTML_DIV );

        $(mask).css({
            position : "absolute",
            backgroundColor : $.Dlg.CONST.MASK_COLOR,
            opacity : $.Dlg.CONST.MASK_OPACITY,
            top : 0,
            left : 0,
            width : $(document.body).width(),
            zIndex : options.maskZIndex
        }).hide().appendTo( document.body );

        var resetSize = function() {
            $(mask).css({
                width: $(document.body).width(),
                height: $(document).height()
            });
        };

        $(window).scroll(resetSize);
        $(window).resize(resetSize);

        /**
         * 显示遮罩
         * @access public
         */
        this.showMask = function() {
            // TODO 禁止最外层滚动
            //$("body").css("overflow", "hidden");
            DLG_MASK_COUNT ++;
            if( options.hideTarget != null && $.isFunction( options.hideTarget.css ) ){
                options.hideTarget.css("visibility", "hidden");
            }
            $(mask).css({
                height : $(document).height()
            }).show();
        };

        /**
         * 关闭遮罩
         * @access public
         */
        this.hideMask = function() {
            $(mask).fadeOut("fast", function(){
                if (options.hideTarget != null && $.isFunction( options.hideTarget.css ) ){
                    options.hideTarget.css("visibility", "visible");
                }
            });
            // TODO 恢复最外层滚动
            DLG_MASK_COUNT --;
            //if (!DLG_MASK_COUNT) $("body").css("overflow", "auto");
        };
        this.getElement = function() {
            return mask;
        };

        /**
         * 设定弹框的位置
         * @access public
         *
         * @params dialog {Dialog} 要设定位置的弹框
         */
        this.setPosition = function( dialog ){
            var container = dialog.getContainer(),
                scrollTop = $(document).scrollTop(),
                scrollLeft = $(document).scrollLeft(),
                viewportWidth = $(window).width(),
                viewportHeight = $(window).height(),
                containerWidth = $(container).width(),
                containerHeight = $(container).height();

            var top = scrollTop + parseInt( ( viewportHeight - containerHeight ) / 2, 10 ),
                left = scrollLeft + parseInt( ( viewportWidth - containerWidth ) / 2, 10 );

            $(container).css({
                top: Math.max(50, top),
                left: left
            });
        };
    },

    /**
     * Class 消息类型弹框 $.Dlg.DialogType.CONFIRM
     *     包含有MessageDialog功能，有方法别名
     * @access public
     *
     * @params options {json} 由Dialog工厂传入的参数
     */
    ConfirmDialog : function( options ){
        // private fields
        var self = this;
        var manager = new $.Dlg.DialogManager(options);
        var beforeCloseHandler = null;

        var EVENT = {
            SHOW : "show",
            HIDE : "hide",
            BEFORE_CLOSE : "beforeClose"
        };

        var container = document.createElement( $.Dlg.CONST.HTML_DIV ), //框架对象
            header = document.createElement( $.Dlg.CONST.HTML_DIV ),
            title = document.createElement( $.Dlg.CONST.HTML_H3 ), //title
            btnClose = document.createElement( $.Dlg.CONST.HTML_A ),
            content = document.createElement( $.Dlg.CONST.HTML_DIV ),
            contentContainer = document.createElement( $.Dlg.CONST.HTML_DIV ),
            textContainer = document.createElement( $.Dlg.CONST.HTML_DIV ),
            bottom = document.createElement( $.Dlg.CONST.HTML_DIV );

        $(container).addClass($.Dlg.CONST.CSS_DLG).css({
            position: "absolute",
            zIndex: options.dialogZIndex
        });
        $(header).addClass($.Dlg.CONST.CSS_HEADER_COMMON);
        $(content).addClass($.Dlg.CONST.CSS_CONT);
        $(bottom).addClass($.Dlg.CONST.CSS_BOTTOM);

        // 填充header
        btnClose.href = $.Dlg.CONST.VOID_LINK;
        btnClose.title = $.Dlg.CONST.CLOSE_BUTTON_TITLE;
        $(btnClose).text("x");
        $(title).addClass( $.Dlg.CONST.CSS_TITLE ).text( options.title );
        $(btnClose).add( $(title) ).appendTo( $(header) );

        //填充content
        $(contentContainer).addClass( $.Dlg.CONST.CSS_CONTENT );
        $(textContainer).addClass( $.Dlg.CONST.CSS_TEXT ).text( options.confirmText );

        $(content).append($(contentContainer).append(textContainer));

        // 组装框架并附加到body中去
        $(container).hide()
                .append(header).append(content).append(bottom)
                .appendTo(document.body);

        // 绑定关闭窗口事件
        $(btnClose).click(function() {
            if ($.isFunction(options.closeButtonEvent)) {
                options.closeButtonEvent( container );
            }
            self.fadeOut(false);
        });

        /**
         * 组装按钮区域
         * @access private
         *
         * @params self {Dialog} 自己
         */
        var setupBottom = function() {
            new $.Dlg.ButtonFactory(options).applyTo(self);
        };

        /**
         * 附加按钮事件 ok
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setOkHandler = function(handler) {
            options.okButtonEvent = handler;
            setupBottom();
        };

        /**
         * 附加按钮事件 save
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setSaveHandler = function(handler) {
            options.saveButtonEvent = handler;
            setupBottom();
        };
        /**
         * 附加按钮事件 yes
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setYesHandler = function(handler) {
            options.yesButtonEvent = handler;
            setupBottom();
        };

        /**
         * 附加按钮事件 no
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setNoHandler = function(handler) {
            options.noButtonEvent = handler;
            setupBottom();
        };

        /**
         * 附加按钮事件 cancel
         * @access public
         *
         * @params handler {Function} 事件处理句柄
         */
        this.setCancelHandler = function(handler) {
            options.cancelButtonEvent = handler;
            setupBottom();
        };

        /**
         * 显示该对话框 show，或绑定事件。 触发事件show
         * @access public
         *
         * @params param {mixed} how to show or bind event
         */
        this.show = function(param){
            if ($.isFunction(param)) {
                // bind action to event
                $(container).bind(EVENT.SHOW, param);
            } else {
                manager.showMask();
                manager.setPosition(this);
                // exec action and trigger event
                // when use show's callback, only one parameter works
                param = param == undefined ? 0 : param;
                $(container).show(param, function() {
                    $(this).trigger(EVENT.SHOW);
                });
                $(bottom).find( "." + $.Dlg.CONST.CSS_BTNLARGE + ", ." + $.Dlg.CONST.CSS_BTNSMALL ).filter(":first").focus();
            }
        };

        /**
         * 显示该对话框 fadeIn，或绑定事件。触发事件show
         * @access public
         *
         * @params param {mixed} how to fade or bind event
         */
        this.fadeIn = function(param) {
            if ($.isFunction(param)) {
                // bind action to event
                $(container).bind(EVENT.SHOW, param);
            } else {
                manager.showMask();
                manager.setPosition(this);
                param = param == undefined ? 0 : param;
                // exec action and trigger event
                $(container).fadeIn(param, function() {
                    $(this).trigger(EVENT.SHOW);
                });
            }
        };

        /**
         * 当试图关闭对话框前
         * @access public
         *
         * @params fn {Function} 若fn返回false则阻止close事件
         */
        this.beforeClose = function(fn) {
            beforeCloseHandler =  fn;
        };
        /**
         * 检查是否能够关闭
         * @access private
         *
         * @return {boolean} 能否被关闭 通过了beforeCloseHandler的约束
         */
        var canBeClosed = function() {
            return (!$.isFunction(beforeCloseHandler) || beforeCloseHandler());
        };

        /**
         * 关闭该对话框 hide，或绑定事件。 触发事件hide
         * @access public
         *
         * @params param {mixed} how to hide or bind event
         */
        this.hide = function(param) {
            if ($.isFunction(param)) {
                // bind action to event
                $(container).bind(EVENT.HIDE, param);
            } else if (canBeClosed()) {
                manager.hideMask();
                // exec action and trigger event
                // when use show's callback, only one parameter works
                param = param == undefined ? 0 : param;
                $(container).hide(param, function() {
                    $(this).trigger(EVENT.HIDE);
                });
            }
        };

        /**
         * 关闭该对话框 fadeOut，或绑定事件。触发事件hide
         * @access public
         *
         * @params param {mixed} how to fade or bind event
         */
        this.fadeOut = function(param) {
            if($.isFunction(param)){
                //bind action to event
                $(container).bind(EVENT.HIDE, param);
            } else if(canBeClosed()){
                manager.hideMask();
                // exec action and trigger event
                param = param == undefined ? options.fadeSpeed : param;
                $(container).fadeOut(param, function() {
                    $(this).trigger(EVENT.HIDE);
                });
            }
        };
        /**
         * 设置title
         *
         * @return
         */
        this.setTitle = function(t) {
            $(title).text(t);
        };
        /**
         * 获得弹框容器对象
         * @access public
         *
         * @return {HTMLDiv} 弹框最大的容器
         */
        this.getContainer = function() {
            return container;
        };

        this.getMask = function() {
            return manager.getElement();
        };
        /**
         * 获得弹框主自定义内容区
         * @access public
         *
         * @return {HTMLDiv} 弹框自定义的内容区
         */
        this.getContent = function() {
            return contentContainer;
        };

        this.getTextContainer = function() {
            return textContainer;
        };
        /**
         * 获得弹框底部
         * @access public
         *
         * @return {HTMLDiv} 弹框底部
         */
        this.getBottom = function() {
            return bottom;
        };

        /**
         * 设置确认提示语
         * @access public
         *
         * @params text {string} 提示语
         */
        this.setConfirmText = function(text) {
            $(textContainer).text(text);
        };

        /**
         * 设置确认提示语 HTML格式
         * @access public
         *
         * @params html {string} 提示语
         */
        this.setConfirmHtml = function(html) {
            $(textContainer).html(html);
        };

        /**
         * 设置确认提示语
         * @access public
         *
         * @params text {string} 提示语
         * @params useHtml {boolean} 是否使用HTML
         */
        this.setMessageText = function(text) {
            self.setConfirmText(text);
        };

        /**
         * 设置确认提示语 HTML格式
         * @access public
         *
         * @params html {string} 提示语
         */
        this.setMessageHtml = function(html) {
            self.setConfirmHtml(html);
        };

        // 填充bottom
        setupBottom();

        /**
         * @brief function
         * 得到控制弹出窗口的类
         *
         * @return {object}
         */
        this.getManager = function() {
            return manager;
        };
        manager.setPosition(this);
    },

    /**
     * Class 按钮工厂，用于装配按钮
     * @access private
     *
     * @params options {json} 选项
     */
    ButtonFactory : function(options) {
        //准备按钮
        var btnYes = document.createElement( $.Dlg.CONST.HTML_A ),
            btnNo = document.createElement( $.Dlg.CONST.HTML_A ),
            btnOk = document.createElement( $.Dlg.CONST.HTML_A ),
            btnSave = document.createElement( $.Dlg.CONST.HTML_A ),
            btnCancel = document.createElement( $.Dlg.CONST.HTML_A );

        // 绑定事件
        btnYes.href = $.Dlg.CONST.VOID_LINK;
        $(btnYes).addClass( $.Dlg.CONST.CSS_BTNSMALL ).attr("title", options.yesButtonText).append($(document.createElement($.Dlg.CONST.HTML_SPAN)).text(options.yesButtonText)).click(options.yesButtonEvent);

        btnNo.href = $.Dlg.CONST.VOID_LINK;
        $(btnNo).addClass( $.Dlg.CONST.CSS_BTNSMALL ).attr("title", options.noButtonText).append($(document.createElement($.Dlg.CONST.HTML_SPAN)).text(options.noButtonText)).click(options.noButtonEvent);

        btnOk.href = $.Dlg.CONST.VOID_LINK;
        $(btnOk).addClass( $.Dlg.CONST.CSS_BTNSMALL + " mr10" ).attr("title", options.okButtonText).append($(document.createElement($.Dlg.CONST.HTML_SPAN)).text(options.okButtonText)).click(options.okButtonEvent);

        btnSave.href = $.Dlg.CONST.VOID_LINK;
        $(btnSave).addClass( $.Dlg.CONST.CSS_BTNSMALL + " mr10" ).attr("title", options.saveButtonText).append($(document.createElement($.Dlg.CONST.HTML_SPAN)).text(options.saveButtonText)).click(options.saveButtonEvent);

        btnCancel.href = $.Dlg.CONST.VOID_LINK;
        $(btnCancel).addClass( $.Dlg.CONST.CSS_BTNSMALL ).attr("title", options.cancelButtonText).append($(document.createElement($.Dlg.CONST.HTML_SPAN)).text(options.cancelButtonText)).click(options.cancelButtonEvent);

        /**
         * 应用于Dialog对象
         * @access private
         *
         * @params {Dialog} 应用于的Dialog对象
         */
        this.applyTo = function(dialog) {
            var bottom = dialog.getBottom();
            var closeDialog = function() {
                dialog.fadeOut();
            };
            // without check before closing
            var closeDialog0 = function() {
                dialog.fadeOut(false);
            };

            $(bottom).empty();
            switch (options.buttonType) {
                case $.Dlg.ButtonType.OK :
                    $(bottom).append(btnOk);
                    $(btnOk).click(closeDialog);
                    break;
                case $.Dlg.ButtonType.YESNO :
                    $(bottom).append(btnYes).append(btnNo);
                    $(btnYes).click(closeDialog);
                    $(btnNo).click(closeDialog);
                    break;
                case $.Dlg.ButtonType.OKCANCEL :
                    $(bottom).append(btnOk).append(btnCancel);
                    $(btnOk).click(closeDialog);
                    $(btnCancel).click(closeDialog0);
                    break;
                case $.Dlg.ButtonType.SAVECANCEL :
                    $(bottom).append(btnSave).append(btnCancel);
                    $(btnSave).click(function(){
                        if( options.saveCallback && $.isFunction( options.saveCallback ) ) {
                            var saveCanClose = options.saveCallback( dialog );

                            if( saveCanClose ) closeDialog();
                        } else {
                            closeDialog();
                        }
                    });
                    $(btnCancel).click(closeDialog0);
                    break;
                default :
                    break;
            }
        };
    },

    /**
     * Class Warning形式的Dialog
     * @access public
     *
     * @params options {json} 由Dialog工厂传入的参数
     */
    WarningDialog : function(options) {
        // private fields
        var self = this,
            manager = new $.Dlg.DialogManager(options),
            timer = null;

        var container = document.createElement($.Dlg.CONST.HTML_DIV), // 框架对象
            header  = document.createElement($.Dlg.CONST.HTML_DIV),
            content = document.createElement($.Dlg.CONST.HTML_DIV),
            contentContainer = document.createElement($.Dlg.CONST.HTML_DIV),
            icon = document.createElement($.Dlg.CONST.HTML_SPAN);

        $(container).addClass( $.Dlg.CONST.CSS_DLG ).css({
            position: "absolute",
            zIndex: options.dialogZIndex
        });
        $(header).addClass($.Dlg.CONST.CSS_HEADER_SIMPLE);
        // 填充content
        $(content).addClass($.Dlg.CONST.CSS_CONT);
        $(contentContainer).addClass($.Dlg.CONST.CSS_CONTENT);
        $(icon).addClass( $.Dlg._get_warning_class(options.warningType) ).text(options.warningText);
        $(contentContainer).append(icon).appendTo( $(content) );

        // 组装框架并附加到body中去
        $(container).hide()
                .append(content)
                .appendTo(document.body);

        /**
         * 显示该对话框 show
         * @access public
         *
         * @params hideHandler {Function} 自动消失后的回调函数
         */
        this.show = function(hideHandler) {
            manager.showMask();
            $(container).show();
        };

        /**
         * 关闭该对话框 hide
         * @access public
         *
         * @params param {mixed} how to fade
         */
        this.hide = function(param) {
            manager.hideMask();
            $(container).hide(0, param);
        };

        /**
         * 关闭该对话框 fadeOut
         * @access public
         *
         * @params speed {mixed} fade speed
         * @params callBack {Function} callback
         */
        this.fadeOut = function(speed, callBack) {
            manager.hideMask();
            speed = speed == undefined ? options.fadeSpeed : speed;
            $(container).fadeOut(speed, callBack);
        };

        /**
         * 获得弹框容器对象
         * @access public
         *
         * @return {HTMLDiv} 弹框最大的容器
         */
        this.getContainer = function() {
            return container;
        };

        /**
         * 获得弹框主自定义内容区
         * @access public
         *
         * @return {HTMLDiv} 弹框自定义的内容区
         */
        this.getContent = function() {
            return contentContainer;
        };

        manager.setPosition(this);
    },

    /**
     * 自定义异常：参数异常
     * @access private
     *
     * @params index     {int}    第几个参数（从0开始）
     * @params wrongType {string} 错误类型
     * @params desc      {string} 异常描述
     */
    paramTypeException : function(index, wrongType, desc) {
        this.desc = desc == undefined ? "" : desc;

        /**
         * @Override
         * 重写toString
         */
        this.toString = function() {
            if (this.desc) {
                return this.desc;
            } else {
                return "Type[" + wrongType + "] is not supported on parameters[" + index + "]";
            }
        };
    }
}
});
})(jQuery);
