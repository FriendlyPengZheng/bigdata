(function () {
var CONFIGURE;

$(function(){
    CONFIGURE = new Data();
    (function () {
        ajax(getUrl("online", "getGameInfo"),{},function(res){
            if (res.result == 0) {
                CONFIGURE.setData(res.data);
            }
        },'GET');
    })();
});
function _gModule() {
    var prepared = [];
    prepared.push({
        container: $("#J_content").empty(),
        type: "data",
        isTimeDimensionInherit: false,
        url: {
            timeDimension: 4,
            extend: ["", "", "", "", getUrl("online", "getData")],
            page: function () {
                return CONFIGURE.getPageParam();
            }
        },
        child: [{
            type: "graph",
            chartStock: true,
            navigator: false,
            title: CONFIGURE.getTitle() + "-最高在线",
            showMax: true,
            height: 500,
            loadUrl: getUrl("online", "getData"),
            timeDimension: "onlymin"
        }]
    });
    $("#J_content").data("content-data", fac(prepared));
    CONFIGURE.next();
}

var Data = function () {
    var that = this;
    $('#J_dateTools').find(".change-btn").click(function(){
        var t = $(this);
        if(t.hasClass("cur")){
            t.removeClass("cur");
        }else{
            t.siblings().removeClass("cur");
            t.addClass("cur");
        }    
        that.data_id = t.attr("data-id");
        that.refresh();
    });
    this.data_id = 1;
    this.i = 0;
};
Data.prototype = {
    setData: function (data) {
        this.data = data;
        this.length = data.length - 1;
        this.init();
    },
    init: function () {
        _gModule();
        this.interval = setInterval(_gModule, 10000);
    },
    refresh: function () {
        _gModule();
    },
    getDate: function() {
        var now = $.date.getNow(), d = '';
        switch(parseInt(this.data_id, 10)) {
            case 1:
                d += now + ',' + $.date.getDate(now, -1) ;
                break;
            case 2:
                d += now + ',' + $.date.getDate(now, -7) ;
                break;
            default: break;
        }
        return d;
    },
    getCurrent: function () {
        return this.data[this.i];
    },
    next: function () {
        if (this.i >= this.length) {
            this.i = 0;
        } else {
            this.i ++;
        }
    },
    getTitle: function () {
        return this.getCurrent()["gameName"] + this.getCurrent()["dataName"];
    },
    getPageParam: function () {
        var param = {
            game_id: this.getCurrent()["game_id"],
            zs_id: this.getCurrent()["zs_id"],
            time: this.getDate()
        };
        return param;
    }
};
})();
