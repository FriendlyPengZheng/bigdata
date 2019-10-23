(function($) {
$.fn.Table = function( opts ) {
    /**
     * @brief
     * page       boolean  是否需要分页功能
     * showNum    int      每页显示多少条数据
     * order      boolean  是否排序
     * orderBegin int      开始排序列的下标
     */
    var option = {
        page : true,
        showNum : 10,
        order : true,
        orderBegin : 0,
        columns : {}
    }

    if( !this.length ) {
        return this;
    }
    if( this.length > 1 ) {
        return this.each(function(){
            $(this).Table( opts );
        })
    }

    if( !this[0].tagName || this[0].tagName.toUpperCase() != "TABLE" ) {
        return this;
    }
    $.extend( option, opts );

    return new $(this).TableFactory( option );
};

$.fn.TableFactory = function( opts ) {
    var _self = this,
        rows = this.find("tbody tr"),
        ths = this.find("th"),
        i = 0;

    this.aTrs = [];
    while( rows[i] ) {
        this.aTrs.push(rows[i]);
        i ++;
    }

    /**
     * @brief function
     * 显示tr功能
     * @param start : 显示tr的开始下标
     * @param showNum ： 总显示条数
     *
     */
    this.showTr = function( start, showNum ){
        var length = _self.aTrs.length;

        if( start >= 0 && start < length ){
            var tmpFragment = document.createDocumentFragment(),
                end = start + showNum <= length ? start + showNum : length;

            for( var j = start; j < end; j++ ){
                tmpFragment.appendChild( _self.aTrs[j] );
            }
        }
        _self.find("tbody").empty().append( tmpFragment );
    };
    /**
     * @brief function
     *
     * @param iCol : 排序的列标
     * @param dataType : 该列数据类型
     *
     */
    this.sortTable = function( iCol, dataType ) {
        if( _self.data("sortCol.table.sort") == iCol ) {
            $( ths.get(iCol) ).toggleClass("up down");
            this.aTrs.reverse();
        } else {
            this.aTrs.sort( sort(iCol, dataType) );
            $(ths.get( _self.data("sortCol.table.sort") )).removeClass("down up");
            $(ths.get(iCol)).addClass("sorted down");
        }

        _self.showTr( 0, opts.showNum );
        if( opts.page && pageBar ){
            pageBar.resetPageBar();
        }
        _self.data( "sortCol.table.sort", iCol );
    }

    //分页功能
    if( opts.page ){
        var pageBar = new $.page.PageFactory({
            showNum : opts.showNum,
            container : _self.parent(),
            total : _self.aTrs.length,
            preEvent : function( pageIndex, showNum ){
                _self.showTr( ( pageIndex - 1 ) * showNum, showNum );
            },
            nextEvent : function( pageIndex, showNum ){
                _self.showTr( ( pageIndex - 1 ) * showNum, showNum );
            }
        });
        _self.showTr( 0, opts.showNum );
    }
    //排序功能
    if( opts.order ){
        _self.addClass("sortable");
        ths.each(function(j){
            if( j > opts.orderBegin ){
                var $this = $(this);

                if( !$this.attr("data-type") ) {
                    $this.attr( "data-type", "string" );
                }
                $this.attr( "data-ind", $this.index() );
                $this.addClass("sorted");
                $this.click(function(e){
                    e.stopPropagation();
                    _self.sortTable( $(this).attr("data-ind"), $(this).attr("data-type") );
                });
           }
        });
        ths[0].click();
    }

    return this;
};

/**
 * @brief sort
 * 排序规则方法
 * 倒叙
 * @param iCol : 排序列下标
 * @param dataType ：排序列数据类型
 *
 * @return
 */
function sort( iCol, dataType ) {
    return function(oTR1, oTR2) {
        var value1 = convert($($(oTR1).find("td").get(iCol)).text(), dataType),
            value2 = convert($($(oTR2).find("td").get(iCol)).text(), dataType);

        if(value1 < value2) {
            return 1;
        } else if(value1 > value2) {
            return -1;
        } else {
            return 0;
        }
    }
}
/**
 * @brief convert
 * 排序数据处理功能
 * @param value
 * @param dataType
 *
 * @return
 */
function convert(value, dataType) {
    switch(dataType) {
        case "int":
            return parseInt(value);
        case "float":
            return parseFloat(value);
        case "date":
            return new Date(Date.parse(value));
        default:
            return value.toString();
    }
}
})(jQuery);
