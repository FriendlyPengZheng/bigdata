$(function(){
    $("#J_secondBar").tmselect({
        colorTheme : "white",
        href : true
    });
    if($("#J_game").length){
        $("#J_game").tmselect({
            colorTheme : "white",
            href : true
        });
    }
    if($("#J_moduleType").length){
        $("#J_moduleType").tmselect({
            colorTheme : "white",
            href : true
        });
    }
    //左树
    var $aside = $("#J_aside");

    $aside.find(".parent").click(function(e){
        e.stopPropagation();
        var t = $(this);

        if( !t.hasClass("more-icon") ){
            if( !t.hasClass("cur") ){
                $aside.find(".child").removeClass("cur");
                $aside.find(".parent").removeClass("cur");
                t.addClass("cur");
            }

        } else {
            if( t.hasClass("clicked") ){
                SessionStorage.set( "tongji" + t.attr("data-key"), 0 );
                t.removeClass("clicked");
            } else {
                SessionStorage.set( "tongji" + t.attr("data-key"), 1 );
                t.addClass("clicked");
            }
        }
    });

    $aside.find(".child").click(function(e){
        e.stopPropagation();
        var $self = $(this);
        if( !$self.hasClass("cur") ){
            $aside.find(".child").removeClass("cur");
            $aside.find(".parent").removeClass("cur");
            $self.addClass("cur");
            $self.closest(".parent").addClass("cur");
        }
    });
    //SessionStorage
    $aside.find(".parent.more-icon").each(function(){
        var t = $(this);
        if( SessionStorage.get("tongji" + t.attr("data-key")) == 1 ){
            t.addClass("clicked");
        }
    });
});
