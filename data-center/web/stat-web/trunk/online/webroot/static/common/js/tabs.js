(function($, undefined) {

var tabId = 0;

function getNextTabId() {
    return ++tabId;
}

$.widget("data.tabs", {
    eventNamespace: "",
    active: null,
    options: {
        idPrefix: "J_uiTab",
        active: null,
        event: "click",
        // callbacks
        activate: null,
        load: null,
        beforeLoad: null
    },
    _create: function() {
        var _self = this,
            active = this.options.active;

        //this.element.addClass("tabs-wrapper").css("height", "400px");
        this._generateTabs();

        this.options.active = this._initActive();

        if (this.options.active !== false && this.anchors.length) {
            this.active = this.tabs.eq(this.options.active);
        } else {
            this.active = $();
        }

        this._refresh();

        if (this.active.length) {
            //this.load(this.options.active);
            this.load(this.active);
        }
    },
    _initActive: function() {
        var active = this.options.active,
            locationHash = location.hash.substring( 1 );
        if (active === null) {
            // check active tab if declared by url
            if (locationHash) {
                this.tabs.each(function(i) {
                    if ($(this).attr("data-panel") == locationHash) {
                        active = i;
                        return false;
                    }
                });
            }
            // check active tab if declared by class
            if (active === null) {
                active = this.tabs.index(this.tabs.filter(".tabs-active"));
            }
            // no active tab, set to false
            if (active === null || active === -1) {
                active = this.tabs.length ? 0 : false;
            }
        }
        // handle numbers
        if (active !== false) {
            active = this.tabs.index(this.tabs.eq(active));

            if ( active === -1 ) {
                active = 0;
            }
        }
        if (active === false && this.anchors.length) {
            active = 0;
        }
        return active;
    },
    _getTabId: function(tab) {
        return tab.attr("area-control") || this.options.idPrefix + getNextTabId();
    },
    _generateTabs: function() {
        var _self = this;

        this.tabList = this.element.find("ul").eq(0)
            .addClass("tabs-list");

        this.tabs = this.tabList.find(">li:has(a)")
            .addClass("tabs-control");

        this.anchors = this.tabs.map(function() {
            return $("a", this)[0];
        });

        this.panels = $();

        this.anchors.each(function(i, anchor) {
            var tab = $(anchor).closest("li"), panel, panelId;

            if (tab.attr("data-for")) {
                panelId = tab.attr("data-for");
                panel = $("#" + panelId);
            } else {
                panel = $(_self.element.find(">div").get(i));
                if (!panel.length) {
                    panel = $("<div>").appendTo(_self.element);
                }
                panelId = _self._getTabId(tab);
            }
            //anchor.href = "#" + panelId;
            panel.attr("id", panelId);
            tab.attr({
                "data-panel": panelId,
                title: tab.text()
            });
            if (panel.hasClass("tabs-ajax")) {
                panel.addClass("loading");
            }
            _self.panels = _self.panels.add(panel);
        });

        this.panels.addClass("tabs-panel");
        this._afterGenerate();
    },
    _afterGenerate: function() {},
    _beforeSetupEvents: function() {},
    _refresh: function() {
        this._beforeSetupEvents(this.options.event);
        this._setupEvents(this.options.event);
        if (this.active.length) {
            this.active.addClass("tabs-active");
            this._getPanelForTab(this.active).show();
        }
    },
    _setupEvents: function(event) {
        var events = {
            click: function(event) {
                event.preventDefault();
            }
        };
        if (event) {
            $.each(event.split(" "), function(index, eventName) {
                events[ eventName ] = "_eventHandler";
            });
        }
        this._on(this.anchors, events);
    },
    _getPanelForTab: function(tab) {
        var id = $(tab).attr("data-panel");
        return this.element.find("#" + id);
    },
    _eventHandler: function(event) {
        var options = this.options,
            anchor = $(event.currentTarget),
            tab = anchor.closest("li"),
            toShow = this._getPanelForTab(tab),
            toHide = this.active.length ? this._getPanelForTab(this.active) : $(),
            eventData = {
                oldTab: this.active,
                oldPanel: toHide,
                newTab: tab,
                newPanel: toShow
            };

        if (tab.hasClass("tabs-loading") || this.running) {
            return;
        }

        this.active = tab;
        if (toShow.length) {
            //this.load(this.tabs.index(tab), event);
            this.load(tab, event);
        }

        this._toggle(event, eventData);
    },
    _toggle: function(event, eventData) {
        var _self = this,
            toShow = eventData.newPanel,
            toHide = eventData.oldPanel;

        this.running = true;
        eventData.scrollTop = $(window).scrollTop();

        function show() {
            eventData.newTab.addClass("tabs-active");
            if (toShow.length) {
                toShow.show();
            }
            complete();
        }

        function complete() {
            _self.running = false;
            _self._trigger("activate", event, eventData);
        }

        if (toHide.length) {
            eventData.oldTab.removeClass("tabs-active");
            toHide.hide();
        }

        show();
    },
    load: function(tab, event) {
        var //tab = this.tabs.eq(index),
            panel = this._getPanelForTab(tab),
            eventData = {
                tab: tab,
                panel: panel
            },
            _self = this;
        if (!tab.hasClass("tabs-ajax") || tab.hasClass("tabs-loaded")) {
            return;
        }
        tab.addClass("tabs-loading");
        if (this._trigger("beforeLoad", event, eventData)) {
            window.setTimeout(function() {
                if (_self._trigger("load", event, eventData) != false) {
                    tab.removeClass("tabs-loading");
                    tab.addClass("tabs-loaded");
                    if ($(_self.element).height() < eventData.panel.height()) {
                        $(_self.element).css("height", eventData.panel.height());
                    }
                }
                window.setTimeout(function() {
                    tab.removeClass("tabs-loading");
                }, 30000);
            }, 100);
        }
    },
    reload: function() {
        var tab = this.active,
            //index = this.tabs.index(this.tabs.filter(".tabs-active")),
            panel = this._getPanelForTab(tab),
            eventData = {
                tab: tab,
                panel: panel
            };
        if (!tab.hasClass("tabs-ajax") || !tab.hasClass("tabs-loaded") || tab.hasClass("tabs-loading")) {
            return;
        }
        tab.removeClass("tabs-loaded");
        this.load(tab);
    },
    getActivePanel: function() {
    	return this._getPanelForTab(this.active);
    },
    getPanels: function() {
    	return this.panels;
    },
    getActive: function() {
    	return this.active;
    },
    setReload : function(){
        this.tabs.not(".tabs-active").removeClass("tabs-loaded");
    },
    getTabs : function(){
        return this.tabs;
    }
});

$.widget("data.tabsExtendMore", $.data.tabs, {
    options: {
        minTabWidth: 100
    },
    showTabs: null,
    moreTabThumb: null,
    moreList: null,
    moreTabs: null,
    moreTabsAnchors: null,
    maxWidth: null,
    spaceWidth: 0,
    more: true,
    _afterGenerate: function() {
        var that = this, tempWidth = 0, width,
            moreThumbText;
        this.spaceWidth = this.maxWidth = this.tabList.width();
        this.moreTabs = $();
        this.showTabs = $();
        $(this.tabs).each(function() {
            width = $(this).outerWidth();
            tempWidth += width + 10 + 5 + 2;
            if (tempWidth >= that.maxWidth) {
                that.moreTabs = that.moreTabs.add($(this).attr("attr-width", width));
            } else {
                that.spaceWidth -= width + 10 + 5 + 2;
                that.showTabs = that.showTabs.add($(this).attr("attr-width", width));
            }
        });

        this.moreTabsAnchors = this.moreTabs.map(function() {
            return $("a", this)[0];
        });
        if (!this.moreTabsAnchors.length) {
            this.more = false;
            return;
        }
        moreThumbText = $(document.createElement("span")).text("more...");
        this.moreList = $(document.createElement("ul")).addClass("tabs-more").hide()
            .append(this.moreTabs);

        this.moreTabThumb = $(document.createElement("li")).addClass("tabs-more-thumb").append(moreThumbText)
            .append(this.moreList).appendTo(this.tabList);

        this.spaceWidth -= (this.moreTabThumb.outerWidth() + 5);
        this.spaceWidth > 0 ? '' : (this.spaceWidth = 0);
    },
    _beforeSetupEvents: function() {
        var that = this,
            events = {
                click: "_moreTabsEventHandler"
            };
        if (!this.more) {
            return;
        }
        this.moreTabThumb.on("click", function() {
            that.moreList.toggle();
        });
        this._on(this.moreTabsAnchors, events);
    },
    _moreTabsEventHandler: function(event) {
        var anchor = $(event.target).off(event),
            li = anchor.closest("li"),
            toSpaceWidth = li.attr("attr-width");

        if (this.spaceWidth < toSpaceWidth) {
            this._adjustTabWidth(toSpaceWidth - this.spaceWidth);
        }
        this.showTabs = this.showTabs.add(li);
        this.moreTabThumb.before(li);
    },
    _adjustTabWidth: function(toSpaceWidth) {
        var that = this;
        this.spaceWidth = 0;
        this.showTabs.each(function() {
            if (toSpaceWidth > 0 && !$(this).hasClass("more-adjusted")) {
                $(this).width(that.options.minTabWidth).addClass("more-adjusted");
                toSpaceWidth -= ($(this).attr("attr-width") - that.options.minTabWidth);
            }
        });

        // you can only put the less active to more
        if (toSpaceWidth > 0) {
            this.showTabs.each(function() {
                if (toSpaceWidth > 0) {
                    that._on($(this).width("auto").removeClass("more-adjusted").appendTo(that.moreList), {
                        click: "_moreTabsEventHandler"
                    });
                    toSpaceWidth -= that.options.minTabWidth;
                }
            });
            this.spaceWidth -= toSpaceWidth;
            this.showTabs = this.showTabs.filter(".more-adjusted");
        } else {
            this.spaceWidth -= toSpaceWidth;
        }
    }
});
})(jQuery);
