(function($, undefined) {
var component = function(options) {
        this.options = $.extend({}, this.options, options);
        this.element = null;
        this._create();
        this._init();
    },
    form = {},
    tm = {};
$.extend(tm, {
    apply: function(o, c, defaults) {
        if (defaults) {
            tm.apply(o, defaults);
        }
        if (o && c && typeof c == "object") {
            for (var p in c) {
                o[p] = c[p];
            }
        }
        return o;
    },
    extend: function() {
        var io = function (o) {
                for (var m in o) {
                    this[m] = o[m];
                }
            },
            oc = Object.prototype.constructor;
        return function (sb, sp, overrides) {
            if (typeof sp == "object") {
                overrides = sp;
                sp = sb;
                sb = overrides.constructor != oc ? overrides.constructor : function() {
                    sp.apply(this, arguments);
                };
            }
            var F = function() {},
                sbp,
                spp = sp.prototype;
            F.prototype = spp;
            sbp = sb.prototype = new F();
            sbp.constructor = sb;
            sb.superclass = spp;
            if (spp.constructor == oc) {
                spp.constructor = sp;
            }
            sb.override = function(o) {
                tm.override(sb, o);
            };
            sbp.superclass = sbp.supr = (function(){
                return spp;
            });
            sbp.override = io;
            tm.override(sb, overrides);
            sb.extend = function(o) {
                return tm.extend(sb, o);
            };
            return sb;
        };
    }(),
    override: function(orignalClass, overrides) {
        if (overrides) {
            var p = orignalClass.prototype;
            tm.apply(p, overrides);
        }

    }
});
form.field = tm.extend(component, {
options: {
    name: null,
    id: null,
    className: null,
    value: null,
    eventCallback: $.loop,
    attr: {}
},
getElement: function() {
    return this.element;
},
getName: function() {
    return this.options.name;
},
_createInputTag: function(type) {
    var html = "<input/>",
        o = this.options,
        attr = o.attr;
    attr.type = type;
    attr.name = o.name;
    attr.id = o.id;
    this.element = $(html).val(o.value).attr(attr).addClass(o.className);
},
_create: function() {},
_init: function() {
    this.element.on("blur keydown keyup click change", this.options.eventCallback);
}
});
/**
 * @brief text
 * <input type="text">
 */
form.textField = tm.extend(form.field, {
_create: function() {
    this._createInputTag("text");
},
setValue: function(value) {
    this.element.val(value);
    return this;
},
getValue: function() {
    return this.element.val();
},
validator: function() {
}
});

/**
 * @brief hidden
 * <input type="hidden">
 */
form.hiddenField = tm.extend(form.field, {
_create: function() {
    this._createInputTag("hidden");
},
setValue: function(value) {
    this.element.val(value);
    return this;
},
getValue: function() {
    return this.element.val();
},
validator: function() {
}
});

/**
 * @brief radio
 * <input type="radio">
 */
form.radioField = tm.extend(form.field, {
_create: function() {
    this._createInputTag("radio");
},
setValue: function(value) {
    if (value == this.options.value) {
        this.element.attr("checked", true);
    }
    return this;
}
});

/**
 * @brief checkbox
 * <input type="checkbox">
 */
form.checkboxField = tm.extend(form.field, {
_create: function() {
    this._createInputTag("checkbox");
},
setValue: function(value) {
    if (inArray(this.options.value, value)) {
        this.element.attr("checked", true);
    }
    return this;
}
});

/**
 * @brief label
 * <label class="className">title</label>
 * options: {
 *      title: "",
 *      className: ""
 *      content: null
 * }
 * @return
 */
form.label = tm.extend(component, {
options: {
    title: null,
    content: null,
    className: null
},
_create: function() {
    var o = this.options;
    this.element = $("<label>").text(o.title).addClass(o.className);
    if (o.content) this.element.prepend(o.content.getElement());
},
_init: function() {
},
getElement: function() {
    return this.element;
}
});

/**
 * @brief button
 * <a class="className"><span>title</span></a>
 * options: {
 *      title: "",
 *      className: ""
 * }
 * @return
 */
form.buttonField = tm.extend(component, {
options: {
    title: null,
    className: null,
    eventClick: $.loop
},
attr: {
    href: "javascript:void(0);"
},
_create: function() {
    var o = this.options;
    this.element = $("<a>")
        .append($("<span>").text(o.title))
        .addClass(o.className).attr($.extend(this.attr, o.attr));
},
_init: function() {
    var o = this.options;
    this.element.click(function(){
        if(o.eventClick) o.eventClick(this);
    });
},
getElement: function() {
    return this.element;
}
});
/**
 * @brief fieldSet
 * items: [{
 *      label: { title: "", className: "" },
 *      labelWrap: true/false,
 *      type: "radio"/"text"/"hidden",
 *      name: "",
 *      value: 1,
 *      attr: { checked: true },
 *      className: "",
 *      line: {//for <li>
 *          cssStyle: {},
 *          attr: {}
 *      },
 *      items: []
 * }]
 * @return
 */
form.fieldSet = tm.extend(component, {
options: function() {
    items: []
},
_create: function() {
    var that = this, line;
    this.formItems = [];
    this.items = this._initFields(this.options.items);
    this.element = $(document.createElement("ul"));
    this._appendLineFields();
},
_init: function() {},
getElement: function() {
    return this.element;
},
setValues: function(values) {
    $(this.formItems).each(function() {
        if (this.getName && this.getName()) {
            this.setValue(values[this.getName()] ? values[this.getName()] : null);
        }
    });
    return this;
},
_appendLineFields: function() {
    var that = this, lineOptions = {attr: {}, cssStyle: {}};
    $(this.items).each(function() {
        var line = $("<li class='widget-sel'></li>");
        if (this.line) {
            line.attr(this.line.attr);
            if (this.line.cssStyle) line.css(this.line.cssStyle);
        }
        if (this.label) line.append(this.label.getElement());
        line.append(that._appendInlineFields(this.items));
        that.element.append(line);
    });
},
_appendInlineFields: function(items) {
    var that = this, line = $();
    $(items).each(function() {
        if (this.label) line = line.add(this.label.getElement());
        if (this.item) line = line.add(this.item.getElement());
        if (this.items) line = line.add(that._appendInlineFields(this.items));
    });
    return line;
},
_initFields: function(itemsOptions) {
    var that = this, field, items = [];
    $(itemsOptions).each(function() {
        var item = {}, label = $.extend({}, this.label);
        if (this.type) {
            field = this.type + "Field";
            if (this.labelWrap) {
                label.content = new form[field](this);
                that.formItems.push(label.content);
            } else {
                item.item = new form[field](this);
                that.formItems.push(item.item);
            }
        }
        if (!$.isEmptyObject(label)) item.label = new form.label(label);
        if (this.items) item.items = that._initFields(this.items);
        item.line = this.line;
        items.push(item);
    });
    return items;
}
});
tm.form = form;
window.tm = $.extend(tm, window.tm);
})(jQuery);
