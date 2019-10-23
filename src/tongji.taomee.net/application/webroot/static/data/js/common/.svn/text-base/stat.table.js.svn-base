/**
 * fixed table
 * row & column
 *
 */
(function( $, undefined ) {
$.widget("stat.table", {
    scrollSize : 17,
    wrapper : null, //the wrapper of all table plugin
    rowed : false, //has been rowed
    columned : false, //has been columned
    cornered : false, //has been cornered
    defaultClass : {
        tableWrapper : 'fixed-table-wrapper',
        cornerCon : 'fixed-corner',
        rowCon : 'fixed-row',
        columnCon : 'fixed-column',
        tableCon : 'fixed-body',
        stripedTable : 'striped-table',
        table : 'fixed-table',
        tbody : 'fixed-tbody',
        tr : 'tr',
        th : 'th',
        strip : 'strip' //show different color of <tr> ,need addClass strip to <tr:odd> when IE
    },
    options : {
        striped : false, //odd & even different color
        wrapWidth : 800,
        wrapHeight : 400,
        rowNum : 1,
        colNum : 1,
        highlight : true //change color when mouseover
    },
    _create : function(){
        var o = this.options;

        this.element.addClass(this.defaultClass.table);
        this._fixedTable();
        if( o.highlight ) { this._highlight(); }
        if( $.browser.msie ){ this._strip(); }
    },
    tableWidth: 0,
    realTableWrap: null,
    wrapHeight: 0,
    _fixedTable : function(){
        var o = this.options,
            con = this.element.parent(),
            tW = this.element.outerWidth(),
            tH = this.element.outerHeight();

        var wrapper = this._getWrapper();
        var divTable =  $(document.createElement("div"))
            .addClass( this.defaultClass.tableCon )
            .css({
                overflow : "auto",
                width : o.wrapWidth,
                height : o.wrapHeight
            })
           .append( this.element.addClass( this.defaultClass.table + ( o.striped ?  ' ' + this.defaultClass.stripedTable : '' ) ))
           .appendTo( wrapper );

        this.tableWidth = tW;
        this.wrapHeight = o.wrapHeight;
        this.realTableWrap = divTable;

        if( tW > o.wrapWidth  && tH > this.wrapHeight ){ //fixedRow && fixedColumn
            this._fixedCorner();
            this._fixedRow();
            this.columned = true;
            this._fixedColumn();
        } else if( tW > o.wrapWidth && tH <= this.wrapHeight ){ //fixedColumn
            this.wrapHeight = tH + this.scrollSize;
            divTable.height( this.wrapHeight );
            this.columned = true;
            this._fixedColumn();
        } else if( tW <= o.wrapWidth && tH > this.wrapHeight ){ //fixedRow && tableWidth
            this.element.width( o.wrapWidth - this.scrollSize );
            this._fixedRow();
        } else { //tableWidth
            this.wrapHeight = tH;
            divTable.height( this.wrapHeight );
            this.element.width( o.wrapWidth );
        }
    },
    rejustWrapWidth: function() {
        if (this.realTableWrap.is(":hidden")) return;
        var containerWidth = this.realTableWrap.parent().get(0).clientWidth,
            normalWidth = this.options.wrapWidth;

        this._setOption("wrapWidth", containerWidth);

        if (containerWidth > normalWidth) {
            // no need to other operation if the width is larger than old wrapWidth
            this.element.width(this.options.wrapWidth - this.scrollSize);
            this.realTableWrap.width(containerWidth);
        } else {
            if (this.columned) {
                // already fixed the row width, rejust the container width only
                this.realTableWrap.width(containerWidth);
            } else {
                if (this.tableWidth > containerWidth) {
                    this._fixedColumn();
                }
            }
        }
        // if fixed the row
        if (this.rowed) {
            this._getCon(this.defaultClass.rowCon, 1).width(containerWidth - this.scrollSize);
        }
    },
    rejustWrapHeight: function(){
        if (this.realTableWrap.is(":hidden")) {
            return;
        }
        var rejustHeight = this._maxHeight();
        if(this.columned){
            var that = this;
            this.columnCon.height(rejustHeight - this.scrollSize + 2);
            window.setTimeout(function() {
                that._addColumn();
            }, 100);
        } else if(this.element.outerWidth() > this.options.wrapWidth){
            this.columned = true;
            this._fixedColumn();
        }
        if(!this.rowed && this.element.outerHeight() > this.options.wrapHeight){
            this.element.width(this.element.width()-17);
            this._fixedRow();
        }
        if(!this.cornered && this.rowed && this.columned){
            this._fixedCorner();
        }
        this.realTableWrap.height(rejustHeight);
        this.element.parent().height(rejustHeight);
    },
    _maxHeight: function() {
        var tableHeight = this.element.height() + this.scrollSize,
            wrapHeight = this.options.wrapHeight;
        return tableHeight > wrapHeight ? wrapHeight : tableHeight;
    },
    _fixedCorner : function(){
        var cornerCon = this._getCon( this.defaultClass.cornerCon, 2 ).empty(),
            cornerTbl = $(document.createElement('table'))
             .addClass( this.defaultClass.table + ' ' + this.element.attr('class')),
            cornerThead = $(document.createElement('thead')),
            cornerTbody = $(document.createElement('tbody')),
            orgThs = this.element.find('thead tr th'),
            orgTbodyTrs = this.element.find('tbody tr'),
            cornerTblWidth = 0 ,
            cornerTblHeight = 0;

        //thead th
        var theadTr = $(document.createElement('tr'));

        for( var i = 0; i < this.options.colNum; i++ ){
            $( orgThs[i] ).clone().appendTo( theadTr );
            cornerTblWidth += $( orgThs[i] ).outerWidth();
        }
        cornerTblHeight += $( orgThs[i] ).outerHeight();
        theadTr.appendTo( cornerThead );

        //tbody td
        for( var i = 0; i < this.options.rowNum - 1; i++ ){
            var tr = $(document.createElement('tr')),
                tds = $( orgTbodyTrs[i] ).find('td');

            for( var j = 0; j < this.options.colNum; j++ ){
                $(tds[j]).clone().appendTo( tr );
                if( this.options.striped && i % 2 == 1 ){
                    tr.addClass("odd");
                }
            }
            cornerTblHeight += $(tds[j]).outerHeight();
            tr.appendTo( cornerTbody );
        }

        cornerTbl.css({
            'width' : cornerTblWidth + 1 + 'px',
            'height' : cornerTblHeight + 'px'
        });
        cornerCon.css({
            'width' : cornerTblWidth + 1 + 'px',
            'height' : cornerTblHeight + 1 + 'px',
            'overflow' : 'hidden'
        });
        cornerTbl.append( cornerThead.add( cornerTbody ) ).appendTo( cornerCon );
        this.cornered = true;
    },
    _fixedRow : function(){
        var orgThead = this.element.find('thead'),
            orgTbody = this.element.find('tbody'),
            rowCon = this._getCon( this.defaultClass.rowCon, 1 ).empty(),
            rowTbl = $(document.createElement('table'))
                        .addClass( this.defaultClass.table + ' ' + this.element.attr('class') ),
            rowThead = orgThead.clone(),
            rowTbody = $(document.createElement('tbody')),
            tbodyTrs = orgTbody.find('tr'),
            rowTblHeight = orgThead.outerHeight(),  //thead
            tbodyTrHeight = orgTbody.find('tr:eq(0)').outerHeight(),
            tmpTrs = $();

        for( var i = 0; i < this.options.rowNum - 1; i++ ){
            var tr = $(tbodyTrs[i]).clone();
            if( this.options.striped && i % 2 == 1 ){
                tr.addClass("odd");
            }
            tmpTrs = tmpTrs.add( tr );
            rowTblHeight += tbodyTrHeight;
        }
        rowTbody.append( tmpTrs );
        rowTbl.css({
            'width' : this.element.outerWidth() + 1 + 'px',
            'height' : rowTblHeight + 'px '
        });
        rowCon.css({
            'width' : this.options.wrapWidth + 1 - this.scrollSize + 'px',
            'height' : rowTblHeight + 1 + 'px',
            'overflow' : 'hidden'
        });
        rowTbl.append( rowThead.add( rowTbody ) ).appendTo( rowCon );
        this.wrapper.find( '.' + this.defaultClass.tableCon ).scroll(function(){
            rowTbl.css({
                 'margin-left' : '-' + $(this).scrollLeft() + 'px'
            });
        });
        this.rowed = true;
    },
    _fixedColumn : function(){
        this.columning = true;
        var columnCon = this._getCon( this.defaultClass.columnCon, 1 ).empty(),
            columnTbl = $(document.createElement('table'))
             .addClass( this.defaultClass.table + ' ' + this.element.attr('class')),
            colThead = $(document.createElement('thead')),
            colTbody = $(document.createElement('tbody')),
            colTblWidth = 0 ,
            colTblHeight = 0;
        this.columnCon = columnCon;
        //thead th
        var theadTr = $(document.createElement("tr")).addClass( this.defaultClass.tr ),
            ths = this.element.find("thead tr th"),
            tmpThs = $();

        for( var i = 0; i < this.options.colNum; i++ ){
            colTblWidth += $(ths[i]).outerWidth();
            tmpThs = tmpThs.add( $(ths[i]).clone() );
        }
        colTblHeight += $(ths[i]).outerHeight();
        theadTr.append( tmpThs ).appendTo( colThead );

        //tbody td
        var tbodyTrs = this.element.find("tbody tr"),
            tmpTrs = $();

        for( var i = 0; i < tbodyTrs.length; i++ ){
            var tr = $(document.createElement("tr")),
                tds = $(tbodyTrs[i]).find("td"),
                tmpTds = $();

            $(tbodyTrs[i]).addClass("columned");
            for( var j = 0; j < this.options.colNum; j++ ){
                tmpTds = tmpTds.add( $(tds[j]).clone() );
            }
            tmpTds.appendTo( tr );
            colTblHeight += $(tds[j]).outerHeight();
            if( this.options.striped && i % 2 == 1 ){
                tr.addClass("odd");
            }
            tmpTrs = tmpTrs.add( tr.append( tmpTds ) );
        }
        colTbody.append( tmpTrs );

        columnTbl.css({
            'width' : colTblWidth + 1 + 'px',
            'height' : colTblHeight + 1 + 'px'
        });
        columnCon.css({
            'width' : colTblWidth + 1 + 'px' ,
            'height' : ( this.columned ? this.wrapHeight - this.scrollSize : this.wrapHeight ) + 'px',
            'overflow' : 'hidden'
        });
        columnTbl.append( colThead ).append( colTbody ).appendTo( columnCon );
        this.wrapper.find( '.' + this.defaultClass.tableCon ).scroll(function(){
            columnTbl.css({
                'margin-top': '-' + $(this).scrollTop() + 'px'
            });
        });
        this.columning = false;
    },
    columning: true,
    _addColumn: function(){
        if (this.columning) return;
        this.columning = true;
        var columnTbl = this.columnCon.find("table"),
            colTbody = columnTbl.find("tbody"),
            tbody = this.element.find("tbody"),
            colTblHeight = columnTbl.outerHeight();

        //tbody td
        var tbodyTrs = tbody.find("tr").not(".columned"),
            tmpTrs = $();
        for( var i = 0; i < tbodyTrs.length; i++ ){
            var tr = $(document.createElement("tr")),
                tds = $(tbodyTrs[i]).find("td"),
                tmpTds = $();

            for( var j = 0; j < this.options.colNum; j++ ){
                tmpTds = tmpTds.add( $(tds[j]).clone() );
            }
            $(tbodyTrs[i]).addClass("columned");
            tmpTds.appendTo( tr );
            colTblHeight += $(tds[j]).outerHeight();
            if( this.options.striped && i % 2 == 1 ){
                tr.addClass("odd");
            }
            tmpTrs = tmpTrs.add( tr.append( tmpTds ) );
        }
        colTbody.append( tmpTrs );
        columnTbl.css({ 'height' : colTblHeight + 1 + 'px' });
        this.columning = false;
    },
    _getCon : function( className, zIndex ){
        var wrapper =  this.wrapper ? this.wrapper : this._getWrapper(),
            con = wrapper.find( '.' + className );

        return con.length
            ? con
            : $(document.createElement('div'))
                .addClass( className )
                .css({
                    'position':'absolute',
                    'z-index': zIndex,
                    'top' : 0,
                    'left' : 0
                }).appendTo( wrapper );
    },
    _getWrapper : function(){
       this.wrapper =  this.wrapper
                        ? this.wrapper
                        : this.wrapper = $(document.createElement('div'))
                            .addClass( this.defaultClass.tableWrapper )
                            .css({
                                 'position' : 'relative'
                            }).appendTo( this.element.parent() );
       return this.wrapper;
    },
    _highlight : function(){
        var o = this.options,
            _this = this,
            columnTbody = $();

        if( _this.columned ){
            columnTbody = _this.wrapper.find( '.' + _this.defaultClass.columnCon + ' .' + _this.defaultClass.table + ' tbody' );
            columnTbody.find('tr').each(function(i){
                var _self = $(this);

                _self.mouseover(function(){
                    _self.addClass("highlight");
                    _this.element.find('tbody tr:eq(' + i + ')').addClass("highlight");
                }).mouseout(function(){
                    _self.removeClass("highlight");
                    _this.element.find('tbody tr:eq(' + i + ')').removeClass("highlight");
                });
            });
        }
        _this.element.find('tbody tr').each(function(i){
            var _self = $(this);

            _self.mouseover(function(){
                _self.addClass("highlight");
                if( _this.columned ){
                    columnTbody.find('tr:eq(' + i + ')').addClass("highlight");
                }
            }).mouseout(function(){
                _self.removeClass("highlight");
                if( _this.columned ){
                    columnTbody.find('tr:eq(' + i + ')').removeClass("highlight");
                }
            });
        });
    },
    _strip : function(){
        var _this = this;

        _this.element.find("tbody tr:even").addClass("strip");
        if( _this.columned ){
            var columnCon = _this._getCon( _this.defaultClass.columnCon );

            columnCon.find("tbody tr:even").addClass("strip");
        }
        if( _this.rowed ){
            var rowCon = _this._getCon( _this.defaultClass.rowCon );

            rowCon.find("tbody tr:even").addClass("strip");
        }
    }
});
})(jQuery);
