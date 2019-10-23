$(function(){
$.Select = {
    CONST : {
        CSS_CON : "select-con",
        CSS_TITLE : "select-title",
        CSS_OPTION_CON : "select-option-con",
        CSS_SELECT_ITEM : "select-item",
        CSS_SELECTED_ITEM : "selected-item"
    },
    LANGUAGE: {
        chinese: {
            defaultTitle: "请选择："
        },
        english: {
            defaultTitle: "select:"
        }
    },
    create : function( options ){
        var opts = {
            data : [],
            language : "chinese",
            container : $("body")
        };
        $.extend(opts, options);
        if (!$.Select.LANGUAGE[opts.language]) {
            opts.language = "chinese";
        }
        opts = $.extend({}, $.Select.LANGUAGE[opts.language], opts);
        return new _selectFactory( opts )._getInstance();
    },
    _selectFactory : function( opts ){
        this.selectContainer = $(document.createElement("div")).addClass( $.Select.CONST.CSS_CON );
        $.Select.setOptionContent( $optionCon, opts.data );
        opts.container.append( this.selectContainer );
        $.Select.bindEvents( this.selectContainer, false );
    },
    setOptionContent :function( selectContainer, data ){
        var $titleCon = $(document.createElement("div")).addClass( $.Select.CONST.CSS_TITLE ),
            $optionCon = $(document.createElement("div")).addClass( $.Select.CONST.CSS_OPTION_CON ),
            html = '<ul>';

        selectContainer.empty().append( $titleCon.add( $optionCon ) );
        if( data && data.length ){
            $.each(data, function(i){
                html += '<li class="' + $.Select.CONST.CSS_SELECT_ITEM;
                if( this.selected ){
                    $titleCon.attr( "data-id", this.id ).text( this.name );
                    html += ' ' + $.Select.CONST.CSS_SELECTED_ITEM;
                }
                html += '" data-id="' + this.id + '" >' + this.name + '</li>';
            });
        }
        html += '</ul>';
        $optionCon.html( html );
    },
  /**
   * @brief function
   *
   * @param selectContainer
   * @param firstClick boolean true false
   * @param clickEvent
   *
   * @return
   */
    bindEvents : function( selectContainer, firstClick, clickEvent ){
        var $titleCon = selectContainer.find( "." + $.Select.CONST.CSS_TITLE ),
            $optionCon = selectContainer.find( "." + $.Select.CONST.CSS_OPTION_CON );

        /*
         * 给title绑定click事件
         */
        $titleCon.click(function(e){
            e.stopPropagation();
            if( $optionCon.is(":visible") ){
                $optionCon.hide();
            }else{
                $optionCon.show();
            }
        });
        /*
         * 选中当前的select,改变title的选中状态
         * 改变原始的select
         */
        var _selectedHandler = function( $cur, $title, curId, curText ){
            $cur.siblings().removeClass( $.Select.CONST.CSS_SELECTED_ITEM );
            $cur.addClass( $.Select.CONST.CSS_SELECTED_ITEM );
            $title.attr( "data-id",curId ).text( curText );
            if( clickEvent )clickEvent();
        };
        /**
         * 给option绑定click事件
         */
        $optionCon.find("ul li").click(function(e){
            e.stopPropagation();
            var $target = $(e.target),
                titleId = $titleCon.attr("data-id"),
                titleText = $titleCon.text(),
                curId = $target.attr("data-id"),
                curText = $target.text();

            if( titleId != curId ){
                _selectedHandler( $target, $titleCon, curId, curText );
            }
            $optionCon.hide();
        });
        if( firstClick ) {
            var $target = $optionCon.find("ul li").first();
            _selectedHandler( $target, $titleCon, $target.attr("data-id"), $target.text() );
        }
        /**
         * 绑定键盘事件
         */
        selectContainer.attr( "tabindex", "0" ).bind("keydown",function(e){
            switch( e.keyCode ){
                case 13:
                    $optionCon.hide();
                    break;
                case 38:
                    e.preventDefault();
                    var $selected = selectContainer.find( "." + $.Select.CONST.CSS_SELECTED_ITEM ),
                        $curSelected = $selected.prev();

                    if( $curSelected && $curSelected.hasClass( $.Select.CONST.CSS_SELECT_ITEM ) ){
                        _selectedHandler( $curSelected, $titleCon, $curSelected.attr("data-id"), $curSelected.text() );
                    }
                    break;
                case 40:
                    e.preventDefault();
                    var $selected = selectContainer.find( "." + $.Select.CONST.CSS_SELECTED_ITEM ),
                        $curSelected = $selected.next();

                    if( $curSelected && $curSelected.hasClass( $.Select.CONST.CSS_SELECT_ITEM ) ){
                        _selectedHandler( $curSelected, $titleCon, $curSelected.attr("data-id"), $curSelected.text() );
                    }
                    break;
            }
        });
        $("body").click(function(e){
            $optionCon.hide();
        });
    }
};
$.widget( "can.tmselect", {
    selectContainer : null,
    CONST : {
        common : {
            CSS_CON : "select-con",
            CSS_TITLE : "select-title",
            CSS_OPTION_CON : "select-option-con",
            CSS_SELECT_ITEM : "select-item",
            CSS_SELECTED_ITEM : "selected-item"
        },
        white : {
            CSS_CON : "select-con-white",
            CSS_TITLE : "select-title-white",
            CSS_OPTION_CON : "select-option-con-white",
            CSS_SELECT_ITEM : "select-item-white",
            CSS_SELECTED_ITEM : "selected-item-white"
        }
    },
    options : {
        colorTheme : "common",
        href : false
    },
    _create:function(){
        var that = this;

        this._selectFactory();
        $("body").click(function(e){
            that.selectContainer.find( "." + that.CONST[that.options.colorTheme].CSS_OPTION_CON ).hide();
        });
    },
    reset: function(){
        this.selectContainer.empty();
        this._selectFactory();
    },
    _selectFactory : function(){
        if(!this.selectContainer) {
            this.selectContainer = $(document.createElement("div")).addClass( this.CONST[this.options.colorTheme].CSS_CON );
        }
        var that = this,
            $titleCon = $(document.createElement("div")).addClass( that.CONST[that.options.colorTheme].CSS_TITLE ).text('请选择：'),
            $optionCon = $(document.createElement("div")).addClass( that.CONST[that.options.colorTheme].CSS_OPTION_CON ),
            html = '<ul>';

        this.element.find("option").each(function(){
            var $self  = $(this),
                id = $self.attr("data-id"),
                text = $self.text();

            html += '<li class="' + that.CONST[that.options.colorTheme].CSS_SELECT_ITEM;
            if( $self.attr("selected") ){
                $titleCon.attr({
                    "data-id": id,
                    "title": text
                }).text( text );
                html += ' ' + that.CONST[that.options.colorTheme].CSS_SELECTED_ITEM;
            }
            html += '" data-id="' + id + '" title="' + text + '" >'
                + ( that.options.href ? '<a href="' + $self.attr("data-href") + '">' + text + '</a>' : text )
                + '</li>';
        });
        html += '</ul>';
        $optionCon.html( html );
        this.selectContainer.append( $titleCon.add( $optionCon ) );
        that.element.hide().after( this.selectContainer );
        /*
         * 给title绑定click事件
         */
        $titleCon.click(function(e){
            e.stopPropagation();
            if( $optionCon.is(":visible") ){
                $optionCon.hide();
            }else{
                $optionCon.show();
            }
        });
        /*
         * 选中当前的select,改变title的选中状态
         * 改变原始的select
         */
        var _selectedHandler = function( $cur, $title, curId, curText ){
            $cur.siblings().removeClass( that.CONST[that.options.colorTheme].CSS_SELECTED_ITEM );
            $cur.addClass( that.CONST[that.options.colorTheme].CSS_SELECTED_ITEM );
            $title.attr({
                "data-id": curId,
                "title": curText
            }).text( curText );

            //改变原始select
            that.element.find("option:selected").attr( "selected", false );
            that.element.find('option[data-id="' + curId + '"]').attr( "selected", true );
            that.element.change();//激发原始select的change事件
        };
        /**
         * 给option绑定click事件
         */
        $optionCon.find("ul li").click(function(e){
            e.stopPropagation();
            var $target = $(e.target),
                titleId = $titleCon.attr("data-id"),
                titleText = $titleCon.text(),
                curId = $target.attr("data-id"),
                curText = $target.text();

            if( titleId != curId ){
                _selectedHandler( $target, $titleCon, curId, curText );
            }
            $optionCon.hide();
        });
        /**
         * 绑定键盘事件
         */
        that.selectContainer.attr( "tabindex", "0" ).bind("keydown",function(e){
            switch( e.keyCode ){
                case 13:
                    $optionCon.hide();
                    break;
                case 38:
                    e.preventDefault();
                    var $selected = that.selectContainer.find( "." + that.CONST[that.options.colorTheme].CSS_SELECTED_ITEM ),
                        $curSelected = $selected.prev();

                    if( $curSelected && $curSelected.hasClass( that.CONST[that.options.colorTheme].CSS_SELECT_ITEM ) ){
                        _selectedHandler( $curSelected, $titleCon, $curSelected.attr("data-id"), $curSelected.text() );
                    }
                    break;
                case 40:
                    e.preventDefault();
                    var $selected = that.selectContainer.find( "." + that.CONST[that.options.colorTheme].CSS_SELECTED_ITEM ),
                        $curSelected = $selected.next();

                    if( $curSelected && $curSelected.hasClass( that.CONST[that.options.colorTheme].CSS_SELECT_ITEM ) ){
                        _selectedHandler( $curSelected, $titleCon, $curSelected.attr("data-id"), $curSelected.text() );
                    }
                    break;
            }
        });
    },
    getSelectContainer : function(){
        return this.selectContainer;
    },
    getOptionContainer: function() {
        return this.selectContainer.find("." + this.CONST[this.options.colorTheme].CSS_OPTION_CON);
    }
});
});
