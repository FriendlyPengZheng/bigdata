(function(){
$(function(){
	var $body = $("body"), 
		set = null, 
		clear = null,
		$xml = $body.find("input[name='xml-file']"),
		xmlFile = 'xml/'+ ($xml.val() ? $xml.val() : 'game.xml');
  	
  	$(".help").bind({
  		mouseover : function(e){
  			e.stopPropagation();
  			var data = $body.data("data");
	  		var type = $(this).attr("type"),
	  			$target = $(e.target),
	  			$parent = $target.parent().parent();
  			var show = function(){
		  		if(data){
			  		var $d = $($body.data("data"));
			  		var help = $d.find('tab[type="'+type+'"]').find("description").text();
			  		if(!$parent.find("div").hasClass("help-container")){
		  				mkHelp($target,help);
		  			}
		  		}else{
		  			$.get(xmlFile,function(d){
			  		    $body.data("data",d);
			  			var help = $(d).find('tab[type="'+type+'"]').find("description").text();
			  			if(!$parent.find("div").hasClass("help-container")){
			  				mkHelp($target,help);
			  			}
			  		});
		  		}
  			};
  			
  			clear = setTimeout(show,500);
  			if(set != null)clearTimeout(set);
  			set = null;
  		},
  		mouseout : function(e){
  			e.stopPropagation();
			var dispear = function(){
				var $parent = $(e.target).parent().parent();
  					$parent.find(".help-container").remove();
  			};
      		set = setTimeout(dispear,500); 
      		if(clear != null)clearTimeout(clear);
  			clear = null;
  		} 
  	});
});

function mkHelp($obj,help){
	var $parent = $obj.parent().parent(),
		left = getLeft($obj),
		leftDiv = left.leftDiv,
		leftTri = left.leftTri;
		
	$("body").find(".help-container").remove();
	var $spanHelp = $(document.createElement("span"))
				.addClass("help-wrapper")
				.text(help)
				.css({
					"left": "-20000px"
				})
				.appendTo("body"),
		top = getTop($obj,$spanHelp) - 66;
		$div = $(document.createElement("div"))
				.addClass("help-container clearfix")
				.css({
					"left" : leftDiv,
					"top": top
				}),
	    $spanDer = $(document.createElement("span"))
				.addClass("help-der")
				.css({
					"left": leftTri
				});
		
		if(isUp($obj,$spanHelp)){
			$spanDer.addClass("help-der-up");
		}else{
			$spanDer.addClass("help-der-down")
				.css({
					"top" : getHelpH($spanHelp) - 12 + "px"
				});
		}
	$("body .help-wrapper").remove();
    $div.append($spanHelp.css({"left" : "0px"})).append($spanDer);
	$parent.append($div);
	
}

function getLeft($obj){
	var left = {};
		leftDiv = $obj.offset().left,
		widthAll = $("body").width(),
		minus = widthAll - leftDiv,
		leftTri = 25;
	if(minus < 220){
		leftDiv -= (220-minus);
		leftTri += (220-minus);
	}
	if(leftTri > 205){
		leftTri = 200;
	}
	
	left.leftDiv = leftDiv - 25;
	left.leftTri = leftTri;
	
	return left;
}

function getHelpH($spanHelp){
	return  $spanHelp.height() + 20 + 14;
}

function getTop($obj,$spanHelp){
	var top = $obj.offset().top,
		conH = $obj.parent().height();

	if(!isUp($obj,$spanHelp)){
		top = top - getHelpH($spanHelp) - conH;
	}
	return top;
}

/**
 * 判断是上箭头还是下箭头
 */
function isUp($obj,$spanHelp){
	var top = $obj.offset().top,
		pageH = document.documentElement.clientHeight;
		
	if(getHelpH($spanHelp) + top > pageH){
		return false;
	}else{
		return true;
	}
}

})();
