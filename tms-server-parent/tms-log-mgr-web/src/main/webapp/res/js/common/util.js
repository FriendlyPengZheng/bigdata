/*********************************************************************************
 * 本文件包含运维平台工具js                                                      *
 * 本文件中的方法为大部分页面的常用方法，若仅小部分页面使用，请单独写作jQuery扩展*
 *********************************************************************************/

/**
 * @brief 记录是否正在进行ajax请求
 */
window.gLoadingCnt = 0;
window.gReloadCnt = 0;
window.layerCnt = 0;
var gLoading = gLoading || {};
gLoading = {
    pool: [],
    on: function() {
        window.gLoadingCnt ++;
        return gLoading.pool.length;
    },
    off: function(key) {
        window.gLoadingCnt --;
        window.gReloadCnt --;
        gLoading.pool[key] = null;
    },
    reload: function() {
        window.gReloadCnt ++;
    },
    isreload: function() {
        return gReloadCnt > 10 ? false : true;
    },
    disable: function() {
        return gLoadingCnt === 0 ? false : true;
    },
    add: function(key, jqHR) {
        gLoading.pool[key] = jqHR;
    },
    cancel: function() {
        var loading = gLoading.pool.length;
        while (loading --) {
            if (gLoading.pool[loading]) {
                gLoading.pool[loading].abort();
            }
        }
    }
};
window.gLoading = gLoading;


/**
 * 统一ajax处理(基于jQuery.ajax)
 *
 * 统一的success返回JSON数据格式:
 *     { result: {Number}, a: b, x: y, m: n... }
 *     总是有result，且通常0表示成功，其他为错误代码。
 *
 * 不接受的参数列表:
 *     dataType 一定为"json"
 *     error    统一的处理方式
 *     其他例如crossDomain等一切jQuery1.5新增的属性
 *
 * 接受的参数列表:
 * @params mixed   {JSON/String} 若为string，则认为是url参数，若为json，则忽略剩下所有参数
 * @params data    {String/JSON} 参数列表
 * @params success {Function}    成功处理函数
 * @params type    {String}      请求方式("GET"/"POST")。默认为"GET"
 * @params async   {Boolean}     是否为异步方式请求。默认为true
 * @params timeout {Number}      请求超时时间。单位:毫秒。默认为20000
 * @params preErr  {Function}    错误预处理函数
 *
 * @returns {Boolean} **仅在同步方式ajax请求下有意义** ajax是否执行成功
 */

function ajax(mixed, data, success, type, async, timeout, preErr) {

    var isAjaxSuccess = true; // 仅在同步方式ajax请求下有意义

    /**
     * 统一ajax错误处理
     */
    var errorHandler = function(config, xhr, textStatus, errorThrown, handler, key) {
        gLoading.off(key);
        if (preCheckError(config, xhr, textStatus, errorThrown, handler)) {
            isAjaxSuccess = false;
            // ajax出错处理
            if (window.responseData && window.responseData.isRelease) {
                alert("数据获取异常，若影响您的正常操作，请联系管理员。");
            } else {
                alert("xhr.status: " + xhr.status + "\ntextStatus: " + textStatus + "\nerrorThrown: " + errorThrown);
            }
        }
    };

    var preCheckError = function(config, xhr, textStatus, errorThrown, handler) {
        if ($.isFunction(handler)) {
            return handler(xhr, textStatus, errorThrown);
        } else {
            // rejected 重新请求
            // timeout 重新请求
            if ((xhr.state() == "rejected" && textStatus !== "parsererror") || (textStatus == "timeout" && gLoading.isreload())) {
                isAjaxSuccess = false;
                window.setTimeout((function(config){
                    return function(){
                        $.ajax(config);
                        gLoading.reload();
                    };
                })(config), 300);
                return false;
            // 页面刷新不用报错
            } else if (xhr.readyState == 0 || xhr.status == 0) {
                return false;
            } else {
                return true;
            }
        }
    };

    /**
     * 统一正确预处理
     */
    var preCheckSuccess = function(data) {
        if (data && !isNaN(data.result)) {
           return true;
        } else {
           // TODO 发送错误信息到服务端
           $.ajax({
               url: getUrl("f", "c", "a")
           });
           return false;
        }
    };

    /**
     * 统一正确处理
     */
    var successHandler = function(data, handler, key) {
        gLoading.off(key);
        if (preCheckSuccess(data)) {
            if ($.isFunction(handler)) {
                handler(data);
            }
        } else {
            isAjaxSuccess = false;
        }
    };

    if (typeof mixed == "string") {
        var key = gLoading.on();
        gLoading.add($.ajax({
            url:     mixed,
            data:    (data == undefined) ? "" : data,
            async:   (typeof async == "boolean") ? async : true,
            type:    type ? type : "GET" ,
            timeout: isNaN(timeout) ? 20000 : timeout,
            dataType: "json",
            success: (function(key) {
                return function (data) {
                    successHandler(data, success, key);
                };
            })(key),
            error: (function(key) {
                return function(xhr, textStatus, errorThrown) {
                	errorHandler(this, xhr, textStatus, errorThrown, preErr, key);
                };
            })(key)
        }), key);
    } else if (typeof mixed == "object") {
        var key = gLoading.on();
        // 设置不接受的参数
        mixed.dataType = "json";
        mixed.error = (function (key) {
            return function(xhr, textStatus, errorThrown) {
                errorHandler(this, xhr, textStatus, errorThrown, preErr, key);
            };
        })(key);
        mixed.url += "&ajax=ajax";
        // 修改success
        var copySuccess = mixed.success;
        mixed.success = (function(key) {
            return function (data) {
                successHandler(data, copySuccess, key);
            };
        })(key);
        gLoading.add($.ajax(mixed));
    } else {
        isAjaxSuccess = false;
        throw new TypeError("统一ajax方法第一个参数必须为JSON或String。");
    }

    return isAjaxSuccess;
}

/**
 * 统一得到请求URL
 * 以应对服务断URL请求方式的改变
 *
 * @author nemo@2010-04-07
 *
 * @params f {String} folder
 * @params c {String} controller
 * @params a {String} action
 * @params p {String} parameters
 *
 * @return {String} ajax url
 */
function getUrl(f, c, a, p) {
    /*var req = "index.php?r=";*/
	var req = "../../../../";
    if (f !== undefined) {
        req += f;
    }
    if (c !== undefined) {
        req += "/" + c;
    }
    if (a !== undefined) {
        req += "/" + a;
    }
    if (typeof p == "string") {
        req = req + "?" + p;
    }
    return req;
}

/**
 * 统一跳转URL
 * 采用getUrl方法
 *
 * @author nemo@2010-05-30
 *
 * @params f {String} folder or else only one url ( only one parameter )
 * @params c {String} controller
 * @params a {String} action
 * @params p {String} parameters
 */
function go(f, c, a, p) {
    if (arguments.length == 1) {
        window.open(f, "_self");
    } else {
        window.open(getUrl(f, c, a, p), "_self");
    }
}

/**
 * 设置cookie
 *
 * @author nemo@2010-10-24
 *
 * @params key     {String} key
 * @params val     {mixed}  val
 * @params expires {Number} expire time
 */
function setCookie(key, val, expires) {
	if (!expires || isNaN(expires)) {
		document.cookie = escape(key) + '=' + escape(val) + ';';
	} else {
	    var exp  = new Date();
		exp.setTime(exp.getTime() + expires);
		document.cookie = escape(key) + '=' + escape(val) + ';expires=' + exp.toGMTString();
	}
}

/**
 * 读取cookie
 *
 * @author nemo@2010-10-24
 *
 * @params key {String} key
 *
 * @return {String} 取得的value
 */
function getCookie(key) {
	var cookie = document.cookie;
	var header = escape(key) + '=';
	var beginPos = cookie.indexOf(header);
	if (beginPos == -1) {
		return '';
	} else {
		var pos = beginPos + header.length;
		return cookie.substring(pos).split(';')[0];
	}
}

/**
 * @brief listCookies 获取cookie列表
 *
 * @author violet@2012-09-17
 *
 * @return {Array} 获取的cookie名称列表
 */
function listCookies() {
    var cookies = document.cookie.split(";"),
        arrCookies = [],
        pair = "";
    for(var i=0, len=cookies.length; i<len; i++) {
        pair = cookies[i].split("=");
        arrCookies[pair[0]] = unescape(pair[1]);
    }
    return arrCookies;
}

/**
 * 本地存储
 *
 * @author nemo@2011-06-28
 */
var LocalStorage = {
    set: function(key, val, expires) {
        if (window.localStorage) {
            window.localStorage.setItem(key, val);
        } else {
            setCookie(key, val, expires);
        }
    },
    get: function(key) {
        if (window.localStorage) {
            return unescape( window.localStorage.getItem(key) );
        } else {
            return unescape( getCookie(key) );
        }
    }
};

/**
 * @brief 本地存储（单次页面）
 *
 * @author violet@2012-09-17
 */
var SessionStorage = {
    set: function(key, val) {
        if (window.sessionStorage) {
            window.sessionStorage.setItem(key, val);
        } else {
            setCookie("session." + key, val);
        }
    },
    get: function(key) {
        if (window.sessionStorage) {
            return unescape(window.sessionStorage.getItem(key));
        } else {
            return unescape(getCookie("session." + key));
        }
    },
    remove: function(key) {
        if (window.sessionStorage) {
            window.sessionStorage.removeItem("session." + key);
        } else {
            setCookie("session." + key, "", (new Date()).getTime() - 1);
        }
    },
    clear: function() {
        if (window.sessionStorage) {
            window.sessionStorage.clear();
        } else {
            var cookies = listCookies(),
                exp = (new Date()).getTime() - 1;
            if(cookies.length) {
                for(var i in cookies) {
                    if(i.indexOf("session.") == 0) {
                        setCookie(i, "", exp);
                    }
                }
            }
        }
    }
};

/**
 * 存储偏好
 * 传入的key为f.c.a.something
 *
 * @author nemo@2011-07-13
 */
var Prefrence = {
    set: function(key, val, expires) {
        LocalStorage.set("pref." + key, val, expires);
    },
    get: function(key) {
        return LocalStorage.get("pref." + key);
    }
};

/**
 * 过滤出没有重复值的数组
 *
 * @author nemo@2010-11-10
 *
 * @params arr {Array} 要被过滤的数组
 *
 * @return     {Array} 过滤后的数组
 */
function arrayUnique(arr) {
	var a = new Array();
	for (var i = 0; i < arr.length; i++) {
		var s = false;
		for (var j = 0; j < a.length; j++) {
			if (arr[i] == a[j]) {
				s = true;
				break ;
			}
		}
		if (!s) {
			a.push(arr[i]);
		}
	}
	return a;
}

/**
 * 看一个变量是否在这个数组里
 *
 * @author nemo@2011-05-25
 *
 * @params needle {mixed} 要查找的值
 * @params arr    {Array} 要查找的数组
 *
 * @return {Boolean} 是否在数组中
 */
function inArray(needle, arr) {
    if((needle == 0 || needle) && arr) {
        for (var i = 0; i < arr.length; i++)
            if (needle == arr[i])
                return true;
    }
	return false;
}

/**
 * @brief isWeekend
 * 判断date是否是周六或周日
 * @param date String "2013-07-02"
 *
 * @return
 */
function isWeekend( date ){
    var arr = date.split('-'),
        day = (new Date( arr[0], arr[1]-1, arr[2])).getDay();

    return ( day == 0 || day == 6 ) ? true : false;
}
$.date = {
    getWeekNum: function(d){
        if($.date.isDate(d)){
            var arr = d.split('-');
            return (new Date( arr[0], arr[1]-1, arr[2])).getDay();
        } else {
            return null;
        }
    },
    isDate: function(d){
        return (/^\d{4}-\d{1,2}-\d{1,2}$/).test(d);
    },
    getChWeek: function(d){
        switch($.date.getWeekNum(d)){
            case 1: return "周一"; break;
            case 2: return "周二"; break;
            case 3: return "周三"; break;
            case 4: return "周四"; break;
            case 5: return "周五"; break;
            case 6: return "周六"; break;
            case 0: return "周日"; break;
            default: return ""; break;
        }
    },
    //interval: 天数间隔
    getDate: function(d, interval){
        if($.date.isDate(d)){
            var arr = d.split('-'),
                date = new Date(arr[0], arr[1]-1, arr[2]);
            date.setTime(date.getTime() + 86400 * 1000 * interval);
            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
        } else {
            return d;
        }
    },
    parseFullMonth: function(month){
        if(month < 10){
            month = '0' + parseInt(month, 10);
        }
        return month;
    },
    //get today format: "2014-03-27"
    getNow: function(){
        var d = new Date();
        return d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
    },
    //get month, format: "2014-03"
    getCurMonth: function(){
        var d = new Date();
        return d.getFullYear() + "-" + (d.getMonth() + 1);
    }
};
/**
 * 定义的按键keyCode
 *
 * @author nemo@2011-04-18
 */
window.Keys = {
    CTRL: 17, ALT: 18, ENTER: 13, SEARCH: 191, ESC: 27,
    LEFT: 37, UP: 38, RIGHT: 39, DOWN: 40,
    DOT: 190,
    N0: 48, N1: 49, N2: 50, N3: 51, N4: 52, N5: 53, N6: 54, N7: 55, N8: 56, N9: 57,
    A: 65, B: 66, C: 67, D: 68, E: 69, F: 70, G: 71, H: 72, I: 73, J: 74, K: 75,
    L: 76, M: 77, N: 78, O: 79, P: 80, Q: 81, R: 82, S: 83, T: 84, U: 85, V: 86,
    W: 87, X: 88, Y: 89, Z: 90
};

/**
 * 快捷键实例
 *
 * @author nemo@2011-04-18
 */
window.ShortCut = {
    sequence: [],
    add: function(k) {
        return this.sequence[this.sequence.length] = k;
    },
    isCommand: function(cmd) {
        var len = cmd.length;

        if (this.sequence.length < cmd.length) return false;
        for (var j = 0, i = this.sequence.length - cmd.length; j < len; ++j) {
            eval("var tmp = Keys." + cmd[j].toUpperCase());
            if (tmp != this.sequence[i]) return false;
            i++;
        }
        return true;
    },
    clear: function() {
        this.sequence = [];
    }
};

/**
 * jQuery fn extensions
 */
(function($) {
$.fn.extend({

    /**
     * Disable selection
     *
     * @author nemo@2011-04-11
     *
     * @return {jQuery} this
     */
    disableSelection: function() {
        if ($(this).length > 1) {
            $(this).each(function() {
                $(this).disableSelection();
            });
            return this;
        } else if (this.length == 0) {
                return this;
        }
        this.get(0).onselectstart = function() {
            return false;
        };
        return this;
    },

    /**
     * enable selection
     *
     * @author nemo@2011-05-31
     *
     * @return {jQuery} this
     */
    enableSelection: function() {
        if ($(this).length > 1) {
            $(this).each(function() {
                $(this).disableSelection();
            });
            return this;
        } else if (this.length == 0) {
                return this;
        }
        this.get(0).onselectstart = function() {
            return true;
        };
        return this;
    },

    /**
     * 给对象加上提示效果
     *
     * @author nemo@2011-04-11
     *
     * @params time { Number } 闪烁次数
     * @params color { String } Blink color
     * @return {jQuery} this
     */
    hint: function(time, color) {
        var obj = this;
        var _time = time;
		var _color = color;
		var dftColor = obj.css('background-color');

    	if (time == undefined || !(time > 0)) {
    		_time = 2;
    	}
		if (color == undefined) {
			_color = '#FFF2B1';
		}
    	for (var i = 0; i < _time; i++) {
    		window.setTimeout(function() {
    			obj.css('background-color', _color);
    		}, (i * 2 + 1) * 300);
    		window.setTimeout(function() {
    			obj.css('background-color', dftColor);
    		}, (i + 1) * 2 * 300);
    	}
        $(this).focus();
        return this;
    },

    /**
     * 纵向显示表格荧光棒效果
     */
    verticalTable: function() {
        $(this).find(".detail").hover(function() {
            $(this).addClass("bg-table-highlight");
        }, function() {
            $(this).removeClass("bg-table-highlight");
        });
    },

    /**
     * 日期选择控件
     *   日历控件的代理，统一处理
     *   目前所用插件--jQuery-UI datepicker
     *
     * @author nemo@2011-04-22
     */
    date: function() {
        if ($(this).datepicker) {
            $(this).datepicker({
                dateFormat: "yy-mm-dd",
                showOn: "button",
                buttonImage: responseData.resServer + "/common/js/jqDatePicker/themes/base/images/calendar.png",
                buttonImageOnly: true
            });
        } else {
            throw new Error("Please load jQuery-UI's datepicker first.");
        }
    },

    /**
     * Drag it
     *
     * All supported events:
     * dragstart: on dragstart
     * dragmove : on move when dragging
     * dragend  : on dragend
     *
     * All supported options:
     * holder: drag by this holder ( if it has specified class )
     * limit: drag limit area ( jQuery Object, BE SURE it has only ONE element )
     * x: is allow horizontal dragging
     * y: is allow vertical dragging
     * opacity: the opacity while dragging
     */
    dragIt: function(opt) {
        if ($(this).length > 1) {
            $(this).each(function() {
                $(this).dragIt(opt);
            });
            return this;
        }
        var _this = this;
        var _opt = {
            limit: $(document.body),
            x: true,
            y: true
        };
        $.extend(_opt, opt);

        var dragState = {
            FREE: 0,
            START : 10,
            DRAGGING: 20
        };
        var dragEvent = {
            DRAG_START: "dragstart",
            DRAG_MOVE: "dragmove",
            DRAG_END: "dragend"
        };

        this.dragState = dragState.FREE;
        this.position = { x: 0, y: 0 };
        this.width = $(_this).outerWidth();
        this.height = $(_this).outerHeight();

        // START pre do
        // style
        if (_opt.holder != undefined) {
            $(_this).find(_opt.holder).css({
                cursor: "move"
            });
        } else {
            $(_this).css({
                cursor: "default"
            });
        }
        // END pre do

        // is dragging
        $.fn.isDragging = function() {
            return this.dragState && this.dragState == dragState.DRAGGING;
        };

        // bind event
        (function() {

            $(_this).mousedown(function(de) {
                if (de.which != 1) return;
                if (_opt.holder != undefined
                        && !$(de.target).is(_opt.holder)) {
                    return;
                }

                _this.dragState = dragState.START;
                _this.position.x = de.pageX;
                _this.position.y = de.pageY;

                // for IE setCapture
                if (_this.get(0).setCapture) {
                    _this.get(0).setCapture();
                }

                return false;
            });

            // bind move event
            $(document).mousemove(function(me) {
                if (me.which != 1) return;
                if (_this.dragState == dragState.FREE) return;

                if (_this.dragState == dragState.START) {
                    // first step to move
                    $(_this).trigger(dragEvent.DRAG_START);
                    _this.dragState = dragState.DRAGGING;

                    // change position
                    $(_this).css({
                        left: $(_this).position().left,
                        top: $(_this).position().top,
                        position: "absolute",
                        opacity: (_opt.opacity > 0 && _opt.opacity < 1) ? _opt.opacity : 1
                    });
                }

                var offX = me.pageX - _this.position.x,
                    offY = me.pageY - _this.position.y;

                var thisPos = $(_this).position();

                var newX = thisPos.left + offX,
                    newY = thisPos.top +  offY;

                if (_opt.limit && _opt.limit.length == 1) {
                    var pos = _opt.limit.position();
                    if (newX < pos.left) {
                        newX = pos.left;
                        _this.position.x -= (thisPos.left - pos.left);
                    } else if (newX + _this.width > pos.left + _opt.limit.width()) {
                        newX = pos.left + _opt.limit.width() - _this.width;
                        _this.position.x += (pos.left + _opt.limit.width() - thisPos.left - _this.width);
                    } else {
                        _this.position.x = me.pageX;
                    }
                    if (newY < pos.top) {
                        newY = pos.top;
                        _this.position.y -= (thisPos.top - pos.top);
                    } else if (newY + _this.height > pos.top +  _opt.limit.height()) {
                        newY = pos.top +  _opt.limit.height() - _this.height;
                        _this.position.y += (pos.top + _opt.limit.height() - thisPos.top - _this.height);
                    } else {
                        _this.position.y = me.pageY;
                    }
                } else {
                    _this.position.x = me.pageX;
                    _this.position.y = me.pageY;
                }

                if (_opt.x && _opt.y) {
                    $(_this).css({
                        left: newX,
                        top : newY
                    });
                } else if (!_opt.x) {
                    $(_this).css("top", newY);
                } else if (!_opt.y) {
                    $(_this).css("left", newX);
                }

                // trigger dragmove
                $(_this).trigger(dragEvent.DRAG_MOVE);
            });

            $(document).mouseup(function(ue) {
                if (ue.which != 1) return;

                $(_this).trigger(dragEvent.DRAG_END);
                _this.dragState = dragState.FREE;

                // for IE releaseCapture
                if (_this.get(0).releaseCapture) {
                    _this.get(0).releaseCapture();
                }

                // recover opacity
                $(_this).css({
                    opacity: 1
                });
            });
        })();

        return this;
    },

    /**
     * ajax提交表单处理json格式数据
     *
     * @params fn {Function} ajax handler
     */
    jsonForm: function(fn) {
        var options = {
            data: { ajax: 'ajax' },
            success: function(data){
                try {
                    data = eval("(" + data + ")");
                    fn(data);
                } catch (e) {
                    say("服务端错误，请稍后再试。(invalid json type)");
                }
            }
        };

        $(this).ajaxSubmit(options);
    },

    /**
     * 简易的onload tip
     * 在页面刚加载时给予提示，自动消失
     *
     * @author nemo@2011-06-16
     *
     * @params html {string} html
     * @params direction {string} 气泡指示方向["bottom"]
     */
    onloadTip: function(html, direction) {
        var self = this;
        var pos = $(this).position();
        if (direction == undefined) direction = "top";
        var trangle = document.createElement("div");
        $(trangle).addClass("tip-trangle tip-trangle-" + direction);
        var container = document.createElement("div");
        $(container).addClass("tip-container")
                .append(trangle)
                .append(html)
                .css({
                    left: (pos.left < 10 ? 10 : pos.left) - 10,
                    top: pos.top + $(self).height() + 20
                })
                .appendTo($("#J_main"))
                .fadeIn("slow");
        window.setTimeout(function() {
            $(container).fadeOut("slow", function() {
                $(this).remove();
            })
        }, 3000);
        return container;
    },

    /**
     * 简易的newly add tip
     * 在页面刚加载时给予提示，自动消失
     *
     * @author nemo@2011-07-12
     */
    newTip: function() {
        if ($(this).length > 1) {
            $(this).each(function() {
                $(this).newTip();
            });
            return this;
        } else if ($(this).length == 0) {
            return this;
        }

        var self = this;
        var pos = $(this).position();
        var container = document.createElement("center");
        $(container).addClass("new-tip-container")
                .text("New")
                .css({
                    position: "absolute",
                    left: (pos.left < 10 ? 10 : pos.left) - 10,
                    top: pos.top - 20
                })
                .mouseover(function() {
                    $(this).fadeOut(900, function() {
                        $(this).remove();
                    });
                });
        $(this).before(container)

        var animation = function () {
            $(container).animate({ fontSize: "16px" }, "slow", function() {
                $(this).animate({ fontSize: "12px" }, "slow", animation);
            });
        };

        window.setTimeout(function() {
            $(container).animate({ fontSize: "14px" }, "slow", animation);
        }, 1000);

        return container;
    },

    /**
     * 密码输入确认式密文（类似wp7 android ios的密码输入方式）
     *
     * @warning: bug in it...
     *
     * @author nemo@2011-07-06
     */
    password: function() {
        return this;
        if ($(this).length > 1) {
            $(this).each(function() {
                $(this).dragIt();
            });
            return this;
        } else if ($(this).length == 0) {
            return this;
        }

        var self = this;

        var thisPos = $(this).position();
        var facade = $("<input />").css({
            position: "absolute",
            border: "none",
            width: $(this).width() - 2,
            height: $(this).height() + 1,
            left: thisPos.left + 2,
            top: thisPos.top + 2,
            zIndex: 2,
            display: "none"
        });
        var strStar = "**************************************************" +
                "********************************************************" +
                "********************************************************";
        strStar = strStar.replace(/\*/g, "●");
        var starString = function(len) {
            return strStar.substring(0, len);
        };
        facade.val(starString($(this).val().length));
        $(this).after(facade);

        facade.focus(function() {
            $(self).focus();
        });

        var timer = null;
        $(this).mousedown(function() {
            $(this).focus();
            return false;
        });
        $(this).focus(function() {
            var tmp = $(this).val();
            $(this).val("");
            window.setTimeout(function() {
                $(self).val(tmp);
            }, 1);
            facade.show();
        }).keyup(function(e) {
            window.clearTimeout(timer);
            var str = $(this).val();
            if (e.keyCode == 8) {
                facade.val(starString(str.length));
            } else {
                var lastChar = str.substr(str.length - 1, 1);
                facade.val(starString(str.length - 1) + lastChar);
                timer = window.setTimeout(function() {
                    facade.val(starString(str.length));
                }, 1000);
            }
        }).blur(function() {
            facade.hide();
        });

    },
    /**
     * @brief function
     * 设置对象位置为固定
     * 当移动到非可见时，自动设置
     * 可见时，恢复原位置
     *
     * @author violet@2013-11-06
     */
    setFixed: function(options) {
        options = $.extend({
            "position": "top",
            "relative": 0
        }, options);
        var height = $(this).height(),
            width = $(this).width(),
            top = $(this).position().top,
            that = this,
            $wrapper = $(document.createElement("div"));
        // 包裹一层，防止塌陷
        $wrapper.css({
            height: height,
            position: "relative"
        });
        $(this).wrap($wrapper);

        // 计算当前位置
        $(this).css({
            top: top + options.relative,
            width: width
        });
        $(document).scroll(function() {
            if ($(document).scrollTop() > height) {
                that.addClass("fixed-container");
            } else {
                that.removeClass("fixed-container");
            }
        }).scroll();
    },
    /**
     * @brief function
     * 检测容器的滚动条是否显示
     * @author violet@2013-12-30
     */
    hasScrollBar : function() {
        return this.get(0).scrollHeight > this.innerHeight();
    }
});
})(jQuery);

/**
 * 统一得到url的方式
 */
window.Path = {
    /**
     * 根据相对资源URL根的路径得到完整资源路径
     *     在模板layout/bottom.html中定义了window.responseData。
     *     并立即设置了window.responseData.resServer = [$response_data.res_server]
     *     利用它，我们拼出了URL
     *
     * @params path {String} 相对路径
     *
     * @author nemo@2011-04-20
     */
    getResPath: function(path) {
        return responseData.resServer + path;
    },
    /**
     * 根据当前的URL，改变给定的key=>value，并返回
     *
     * @param key   {String}
     * @param value {String}
     *
     * @author violet@2013-09-13
     */
    modifyPath: function(key, value) {
        var currentUrl = location.href;
        if (-1 === currentUrl.search("&" + key + "\=") && -1 === currentUrl.search("/?" + key + "\=")) {
            if (-1 === currentUrl.indexOf("?")) {
                currentUrl += "?";
            } else {
                currentUrl += "&";
            }
            return currentUrl + key + "=" + value;
        } else {
            var replace = currentUrl.match(new RegExp("[&?]+" + key + "=([^&#]+)"));
            if (replace) {
                replace = replace[1];
            } else {
                replace = "";
            }
            return currentUrl.replace(key + "=" + replace, key + "=" + value);
        }
    }
}

/**
 * alert using Dlg
 *
 * @author nemo@2011-04-25
 */
window.say = function(p, right, callBack) {
    $.Dlg.Util.alert(p, right, callBack);
}

/**
 * 优化时间输入况体验
 *
 * @author nemo@2011-05-10
 */
function inputTimeOptimize() {
    $("input[type=time], .time-input").live("blur", function() {
        var text = $(this).val();
        text = $.trim(text.replace("：", ":"));
        if (text.indexOf(":") == -1) {
            if (text.length == 2) {
                text = "0" + text.charAt(0) + ":" + "0" + text.charAt(1);
            } else if (text.length == 3) {
                if (parseInt(text.charAt(1)) > 5) {
                    if (parseInt(text.charAt(0)) < 2) {
                        text = text.substring(0, 2) + ":0" + text.charAt(2);
                    }
                } else {
                    if (parseInt(text.charAt(0)) > 2) {
                        text = "0" + text.charAt(0) + ":" + text.substring(1, 3);
                    } else if (parseInt(text.charAt(0)) == 2 && parseInt(text.charAt(1)) > 3) {
                        text = "02:" + text.substring(1, 3);
                    }
                }
            } else if (text.length == 4) {
                text = text.substring(0, 2) + ":" + text.substring(2, 4);
            }
        } else {
            var arr = text.split(":");
            if (isNaN(arr[0]) || isNaN(arr[1])) return;
            var h = parseInt(arr[0]),
                m = parseInt(arr[1]);
            if (h < 10) {
                h = "0" + h;
            }
            if (m < 10) {
                m = "0" + m;
            }
            text = h + ":" + m;
        }
        $(this).val(text);
    });
}

/**
 * 将JSON对象转为字符串
 *
 * @author nemo@2011-06-21
 *
 * @params {object} obj
 * @return {string} decoded result
 */
function jsonEncode(obj) {
    if (window.JSON) {
        return JSON.stringify(obj);
    } else {
        // TODO not support HTML5
    }
}

/**
 * 将字符串转为JSON对象
 *
 * @author nemo@2011-06-21
 *
 * @params {string} string to decode
 * @return {object} obj
 */
function jsonDecode(str) {
    if (window.JSON) {
        return JSON.parse(str);
    } else {
        // TODO not support HTML5
    }
}

/**
 * 得到查询字符串对象
 *
 * @author violet@taomee.com
 *
 * @return {JSON} 查询字符串对象
 */
function getArgs() {
    return parseArgs(location.search.substring(1));
}

/**
 * @brief parseArgs
 * 将请求字符串链接解析为对象
 * @param str：请求的链接
 *
 * @return
 */
function parseArgs(query){
    var args = {}, pairs = query.split("&");
    for(var i = 0; i < pairs.length; i++) {
        var pos = pairs[i].indexOf('=');
        if (pos == -1) continue;
        var argname = decodeURIComponent(pairs[i].substring(0,pos));
        var value = decodeURIComponent(pairs[i].substring(pos+1));
        args[argname] = value;
    }
    return args;
}
/**
 * 类似PHP的empty函数
 *
 * @author violet@taomee.com
 *
 * @params {mixed} 要检查的对象
 *
 * @return {Boolean}
 */
function empty(obj) {
    if (obj == undefined || obj == null) {
        return true;
    }

    if (obj.constructor == "Array") {
        return (obj.length == 0);
    }

    switch (typeof obj) {
        case "string":
            return (obj == "0" || obj.length == 0);
        case "number":
            return (obj == 0);
        case "boolean":
            return !obj;
        case "object":
            return false;
        default:
            return true;
    }
}

/**
 * 调用function名为字符串的方法
 */
function executeFnByName(fnName, context) {
	var args = Array.prototype.slice.call(arguments, 2);
	namespaces = fnName.split(".");
	context = context || window;
	var func = namespaces.pop();
	for(var i = 0; i < namespaces.length; i++) {
		context = context[namespaces[i]];
	}
	return context[func].apply(this, args);
}

/**
 * @brief hasFlashPlayVersion
 *
 * 测试客户端浏览器flash版本
 *
 * @author violet@taomee.com
 *
 * @params {string} 要测试的版本号
 *
 * @return {Boolean}
 */
function hasFlashPlayVersion(Z) {
    var j = document,
        D = "undefined",
        t = navigator,
        S = "Shockwave Flash",
        r = "object",
        q = "application/x-shockwave-flash",
        O = window,
        W = "ShockwaveFlash.ShockwaveFlash",
        M = function() {
        var aa = typeof j.getElementById != D && typeof j.getElementsByTagName != D && typeof j.createElement != D,
            ah = t.userAgent.toLowerCase(),
            Y = t.platform.toLowerCase(),
            ae = Y ? /win/.test(Y) : /win/.test(ah),
            ac = Y ? /mac/.test(Y) : /mac/.test(ah),
            af = /webkit/.test(ah) ? parseFloat(ah.replace(/^.*webkit\/(\d+(\.\d+)?).*$/, "$1")) : false,
            X = !+"\v1",
            ag = [0, 0, 0],
            ab = null;
        if (typeof t.plugins != D && typeof t.plugins[S] == r) {
            ab = t.plugins[S].description;
            if (ab && !(typeof t.mimeTypes != D && t.mimeTypes[q] && !t.mimeTypes[q].enabledPlugin)) {
                T = true;
                X = false;
                ab = ab.replace(/^.*\s+(\S+\s+\S+$)/, "$1");
                ag[0] = parseInt(ab.replace(/^(.*)\..*$/, "$1"), 10);
                ag[1] = parseInt(ab.replace(/^.*\.(.*)\s.*$/, "$1"), 10);
                ag[2] = /[a-zA-Z]/.test(ab) ? parseInt(ab.replace(/^.*[a-zA-Z]+(.*)$/, "$1"), 10) : 0
            }
        } else {
            if (typeof O.ActiveXObject != D) {
                try {
                    var ad = new ActiveXObject(W);
                    if (ad) {
                        ab = ad.GetVariable("$version");
                        if (ab) {
                            X = true;
                            ab = ab.split(" ")[1].split(",");
                            ag = [parseInt(ab[0], 10), parseInt(ab[1], 10), parseInt(ab[2], 10)]
                        }
                    }
                } catch(Z) {}
            }
        }
        return {
            w3: aa,
            pv: ag,
            wk: af,
            ie: X,
            win: ae,
            mac: ac
        }
    } ();
    var Y=M.pv,X=Z.split(".");
    X[0]=parseInt(X[0],10);
    X[1]=parseInt(X[1],10)||0;
    X[2]=parseInt(X[2],10)||0;
    return(Y[0]>X[0]||(Y[0]==X[0]&&Y[1]>X[1])||(Y[0]==X[0]&&Y[1]==X[1]&&Y[2]>=X[2]))?true:false;
}

/**
 * @brief function 类似PHP的str_repeat函数
 *
 * @auth violet@taomee.com
 *
 * @param {intRegex} times
 *
 * @return {this}
 */
String.prototype.repeat = function(times) {
    var str = this.toString();
    while(--times) {
        str += str;
    }
    return str;
}

/**
 * 类似PHP的str_pad函数
 *
 * @author violet@taomee.com
 *
 * @params {intRegex} 长度
 * @params {string} 要加的字符
 *
 * @return {this}
 */
String.prototype.pad = function (pad_length, pad_string) {
    pad_string = String(pad_string) || " ";
    if(!pad_length || isNaN(pad_length) || pad_length < 0) {
        return this;
    }
    if(this.length >= pad_length) {
        return this;
    }
    var str = this.toString();
    var rpt = 0;
    rpt = Math.floor((pad_length - this.length)/pad_string.length);
    var left = pad_length - this.length - (pad_string.length) * rpt;
    if(rpt) {
        str = pad_string.repeat(rpt) + str;
    }
    if(left && pad_string.length > left) {
        str = pad_string.slice(0, pad_length - 1) + str;
    }
    return str;
}

/**
 * 为数字型字符串添加逗号分割符
 *
 * @author violet@taomee.com
 *
 * @return {this}
 */
String.prototype.addCommas = function (decimals) {
    nStr = this + "";
    var x = nStr.split(".");
    var x1 = x[0],
        x2 = x.length > 1 ? "." + x[1] : "",
        rgx = /(\d+)(\d{3})/;

    if(x.length == 1 && decimals && !isNaN(decimals)){
        x2 = ".";
        for(var i = 0; i < parseInt(decimals, 10); i++){
            x2 += "0";
        }
    }
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, "$1" + "," + "$2");
    }
    return x1 + x2;
}

/**
 * ajax请求链接池
 */
function PoolQueue(){
    this.poolLen = 2;
    this.poolQueue = [];

}
PoolQueue.prototype = {
    get : function(){
        var len = this.poolQueue.length,
            pool = {};

        if( len < this.poolLen ){
            pool = new Pool();
            pool.str = len;
            this.poolQueue.push( pool );
        } else {
            var i = 1;

            pool = this.poolQueue[0];
            while( i < this.poolLen ){
                var tmpPool = this.poolQueue[i];

                if( pool.waitRequest.length > tmpPool.waitRequest.length ){
                    pool = tmpPool;
                }
                i++;
            }
        }
        return pool;
    }
};
var Pool = function() {
    this.clear();
};
Pool.prototype = {
    init: function(option) {
        this.stat = true;
        if( this.interval ){
            return;
        }
        //500ms发送一次请求
        var that = this;
        this.interval = window.setInterval(function() {
            that.send();
        }, 500);
    },
    clear: function() {
        this.waitRequest = [];
        this.isRequest = 0;
        this.length = 3;
        this.request = [];
        this.interval = null;
    },
    stop: function(){
        if (this.interval) {
            var that = this;
            window.clearInterval(that.interval);
            this.interval = null;
        }
    },
    add: function(url, param, fn, type) {
        type = type ? type : "GET";
        this.waitRequest.push({
            url : url,
            param : param,
            fn : fn,
            type : type
        });
        this.push();
        if( !this.interval ){
            this.send();
        }
        this.init();
    },
    send: function() {
        var requestLength = this.request.length,
            that = this;

        if( !this.waitRequest.length && !this.request.length ){
            this.stop();
        }
        if( !requestLength ){
            return;
        }
        while (this.isRequest < this.length && this.request.length) {
            var request = this.request.shift();

            if (!request) {
                continue;
            }
            this.isRequest ++;
            window.setTimeout((function( request ){
                return function(){
                    ajax(request.url, request.param, (function(request, start ) {
                        return function(res) {
                            that.log((+new Date()) - start);
                            request.fn(res);
                            that.push();
                            that.isRequest --;
                            request = null;
                        };
                    })(request, (+new Date())), request.type);
                };
            })( request ), 2 );
        }
    },
    log: function(during) {
        if (this.stat) {
            msglog("0xfffffffb", "", during);
            msglog("0xfffffff6", "", during);
        }
    },
    push: function() {
        if( this.request.length < this.length && this.waitRequest.length ){
            this.request.push( this.waitRequest.shift() );
        }
    }
}

/**
 * @brief overlayer
 * 请求数据时提醒"加载中"功能
 * @param options
 *
 * @return
 */
function overlayer( options ){
    window.layerCnt++;
    var opts = {
        text : '加载中...'
    };
    $.extend(opts, options);
    var $layer = $("#J_layer"), $notice = $("#J_notice"), $body = $("body");

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
                .text(opts.text)
                .css({
                    "left" : _get_left( $notice ) + 'px',
                    "top" : 0 //$("body").scrollTop() + 'px'
                })
                .appendTo($body);
    }else{
        $notice.css({
            "left" : _get_left( $notice ) + 'px',
            "top" : 0 //$("body").scrollTop() + 'px'
        }).show();
    }
}
/**
 * @brief hidelayer
 * 隐藏“"加载中" 功能层
 * @param text
 *
 * @return
 */
function hidelayer( text ){
    window.layerCnt--;
    if( window.layerCnt == 0 ){
        var text = text ? text : '操作成功',
            $notice = $("#J_notice");
        $notice.text(text);
        $("#J_layer").add($notice).fadeOut(1000);
    }
}
function _get_left ( obj, con ){
    con = con ? con : $(window);
    var windowW = $(window).width(),
        w = obj.width() ? obj.width() : 130;

    if(windowW - w >0){
        return (windowW - w)/2;
    }else{
        return 0;
    }
}
function notify(content, autoHide) {
    var $container = $(document.createElement("div"));

    if (autoHide === undefined) {
        autoHide = true;
    }
    autoHide = autoHide && true;

    $container.append(content);

    $container.css({
        position: "fixed",
        top: "0px"
    }).appendTo("body");

    var w = $container.width();
    $container.addClass("notice").css({
        left: ($(window).width() - w) / 2
    });

    $container.slideDown("fast", function() {
        if (autoHide) {
            $container.get(0).timer = window.setTimeout(function() {
                $container.slideUp("fast");
                window.clearTimeout($container.get(0).timer);
                $container.remove();
            }, 3000);
        }
    });
    if (!autoHide) {
        var $mask = $(document.createElement("div"));
        $mask.addClass("mask").appendTo("body");
        return $container.add($mask);
    } else {
        return $container;
    }
}
$.extend({
download: function(url, param) {
    var dlg = $.Dlg.Util.message("温馨提示", "正在处理，请稍候...", "确定"),
        msg = "";
    url = url.split("?");
    param = param ? param : {};
    dlg.show();
    ajax(url[0], $.extend({}, parseArgs(url[1]), param), function(res) {
        if (res.result == 0) {
            dlg.hide();
            if (res.data.code === 0) {
                go(res.data.url);
                return;
            } else if (res.data.code === 1) {
                msg = "由于您选择的时间间隔较长，已自动为您异步下载，"
                + "您可以到页面右上角【我的下载】查看和下载！"
                + "<a href='" + getUrl("tool/file/index")
                + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
            } else if (res.data.code === 2) {
                msg = "文件正在处理，请耐心等待，您也可以到页面右上角【我的下载】查看！"
                + "<a href='" + getUrl("tool/file/index")
                + "' title='现在就去' target='_blank' class='a-go'>现在就去</a>";
            }
        } else {
            msg = "下载错误：" + res.err_desc;
        }
        if (msg !== "") {
            dlg.setConfirmHtml(msg);
            dlg.getManager().setPosition(dlg);
            dlg.show();
        }
    }, "POST");
}
});
var Lang = (function () {
    var Lang = function (defaultLang, currentLang) {
        this.defaultLang = defaultLang;
        this.currentLang = currentLang || "zh_CN";
    };

    /**
     * Object that holds the language packs.
     * @type {{}}
     */
    Lang.prototype.pack = {};

    /**
     * Special characters that need to be escaped.
     * @type {RegExp}
     */
    Lang.prototype.specialCharacters = /[-\/\\^$*+?.()|[\]{}]/g;

    /**
     * Translates text from the default language into the current language.
     * @param  {String} text The text to translate.
     * @param  {String} data The placeholders' key-value pairs.
     * @return {*}
     */
    Lang.prototype.t = function (text, data) {
        if (this.defaultLang !== this.currentLang && this.pack[this.currentLang]) {
            text = this.pack[this.currentLang][text] || text;
        }

        if (arguments.length === 1) return text;

        if ($.isPlainObject(data)) {
            for (var key in data) {
                text = text.replace(new RegExp(key.replace(this.specialCharacters, "\\$&"), "g"), data.key);
            }
        } else if (typeof data === "string") {
            for (var i = 1; i < arguments.length; i++) {
                text = text.replace(new RegExp(("{" + i + "}").replace(this.specialCharacters, "\\$&"), "g"), arguments[i]);
            }
        }
        return text;
    }

    return Lang;
})();
window.lang = new Lang("zh_CN", window.responseData.locale);
