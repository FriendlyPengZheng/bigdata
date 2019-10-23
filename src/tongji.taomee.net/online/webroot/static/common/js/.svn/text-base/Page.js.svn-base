/**
 * 分页功能
 */
$.page = {
    CONST : {
        CSS_PAGE_BAR : "paging-bar",
        CSS_PAGE_BTN : "paginate-btn",
        CSS_PAGE_PRE : "paginate-pre",
        CSS_PAGE_NEXT : "paginate-next",
        CSS_PAGE_INDEX : "page-index",
        CSS_PAGE_TOTAL : "page-total",
        CSS_PAGE_LABEL : "paginate-label"
    },
    PageFactory : function( options ){
        var opts = {
            showNum : 5, //一个页面显示的条数
            total : 1,   //数据总条数
            container : $("body"),
            preEvent : null,
            nextEvent : null
        };
        $.extend( opts, options );
        //清空已有的pageBar
        opts.container.find( "." + $.page.CONST.CSS_PAGE_BAR ).remove();
        var _self = this,
            pageTotal = Math.ceil( opts.total / opts.showNum ) ;
        pageTotal = pageTotal ? pageTotal : 1;

        var $pageBar = $(document.createElement("div"))
                    .addClass( $.page.CONST.CSS_PAGE_BAR ),
            $pagePre = $(document.createElement("a"))
                    .addClass( $.page.CONST.CSS_PAGE_PRE + " " + $.page.CONST.CSS_PAGE_BTN + " dis" ),
            $pageNext = $(document.createElement("a"))
                    .addClass( $.page.CONST.CSS_PAGE_NEXT + " " + $.page.CONST.CSS_PAGE_BTN + ( pageTotal <= 1 ? " dis" : " ") ),
            $pageLabel = $(document.createElement("span"))
                    .addClass( $.page.CONST.CSS_PAGE_LABEL )
                    .html(' <font class="' + $.page.CONST.CSS_PAGE_INDEX + '">1</font> / <font class="' + $.page.CONST.CSS_PAGE_TOTAL + '">' + pageTotal + '</font> ');

        $pagePre.add( $pageLabel ).add( $pageNext ).appendTo( $pageBar );
        $pageBar.appendTo( opts.container );

        $pagePre.click(function(e){
            e.stopPropagation();
            var $_self = $(this);

            if( !$_self.hasClass("dis") ){
                var $pageIndex = _self.getPageIndex(),
                    index = parseInt( $pageIndex.text(), 10 );

                index--;
                $pageIndex.text( index );
                if( index < pageTotal && $pageNext.hasClass("dis") ){
                    $pageNext.removeClass("dis");
                }
                if( index <= 1 && !$_self.hasClass("dis") ){
                    $_self.addClass("dis");
                }
                //用户自定义事件
                if( opts.preEvent ) opts.preEvent( index, opts.showNum );
            }
        });

        $pageNext.click(function(e){
            e.stopPropagation();
            var $_self = $(this);

            if( !$_self.hasClass("dis") ){
                var $pageIndex = _self.getPageIndex(),
                    index = parseInt( $pageIndex.text(), 10 );

                index++;
                $pageIndex.text( index );
                if( index > 1 && $pagePre.hasClass("dis") ){
                    $pagePre.removeClass("dis");
                }
                if( index >= pageTotal && !$_self.hasClass("dis") ){
                    $_self.addClass("dis");
                }
                //用户自定义事件
                if( opts.nextEvent ) opts.nextEvent( index, opts.showNum  );
            }
        });
        /**
         * 获得PageBar的数字部分对象
         */
        this.getPageIndex = function(){
            return $pageLabel.find( "." + $.page.CONST.CSS_PAGE_INDEX );
        };

        /**
         * 获得PageBar实例
         */
        this.getInstance = function(){
            return $pageBar;
        };
        /*
         * 页码重置
         */
        this.resetPageBar = function(){
            this.getPageIndex().text(1);
            $pagePre.addClass("dis");
            if( pageTotal > 1 ){
                $pageNext.removeClass("dis");
            } else {
                $pageNext.addClass("dis");
            }
        };
    }
};
