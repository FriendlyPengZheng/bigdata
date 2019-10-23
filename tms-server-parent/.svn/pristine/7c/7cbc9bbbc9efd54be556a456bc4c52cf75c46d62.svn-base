(function ($, undefined) {
//	var gameId = 0;
//	var changed = 0;
//	
//    $(function () {
//    	$("#J_games").change(function(e){
//    		changed = 0;
//    		/*gameId = _getGameId();
//    		 console.log("game id = " + gameId);
//    		 $(".item2").each(function(){
//    			 var url = $(this).attr("href");
//    			 
//    			 var resUrl;
//    			 if(url.indexOf("gameId=") == -1){
//    				 resUrl = url+"&gameId="+gameId;
//    				 console.log("11 resUrl:" + resUrl);
//    			 } else {
//    				 var tmpUrl = url.split("gameId=");
//    				 resUrl = tmpUrl[0] + "gameId=" + gameId;
//    				 console.log("22 resUrl:" + resUrl);
//    			 }
//    			 $(this).attr("href",resUrl);
//    		 });*/
//        });
//    	
//    	$(".item2").mouseover(function() {
//    		if(changed == 0){
//    			gameId = _getGameId();
//				console.log("game id = " + gameId);
//				$(".item2").each(function() {
//					var url = $(this).attr("href");
//
//					var resUrl;
//					if (url.indexOf("gameId=") == -1) {
//						resUrl = url + "&gameId=" + gameId;
//						console.log("11 resUrl:" + resUrl);
//					} else {
//						var tmpUrl = url.split("gameId=");
//						resUrl = tmpUrl[0] + "gameId=" + gameId;
//						console.log("22 resUrl:" + resUrl);
//					}
//					$(this).attr("href", resUrl);
//				});
//				changed = 1;
//    		}
//    	});
//    });
//    
//    function _getGameId(){
//        /*return $("#J_games").find(":selected").attr("data-id");*/
//    	return $("#J_games").val();
//    }

	var selGame = $("#J_selGame");
	ajax("../../../common/page/getGameList?",{},function(res){
			if (res.result == 0) {
				_selectFac(_handleSelect(res.data),selGame);
			}
	});
	
	/**                                                   
 	* @brief _selectFac                                  
 	* 生成列表并绑定事件                                 
	* @param data array 列表                             
 	* @return                                            
 	*/                                                   
	function _selectFac(data, container){                 
    	$.Select.setOptionContent(container, data);       
    	$.Select.bindEvents(container, false, function(){ 
        	_refreshData();                               
    	});                                               
	}
	
	function _handleSelect(data){                      
    	var rlt = [];                                  
    	$.each(data, function(index, val){             
        	rlt.push({                                 
            	id: val.gameId,   
            	name: val.gameName,                   
            	selected: (val.gameId == 0 ? true : false)  
        	});                                        
    	});                                            
    	return rlt;                                    
	}   
	
	function _refreshData(){
		//TODO
	}
	
	$(".J-btn-build").click(function(e){
		var key = $(this).attr('data-key');
		var gameid = $("#J_selGame").find(".selected-item").attr("data-id");
        e.stopPropagation();
        overlayer({ text: "加载中..."});
        ajax("../../component/build?", {
            moduleKey: key.replace(/\./g, '-'),
            gameId: gameid
        }, function(res){
            if(res.result == 0){
                hidelayer("加载成功~.~");
                say("恭喜你，成功啦：）", true);
            } else {
                hidelayer();
                $.Dlg.Util.message("生成失败", "生成失败：" + res.err_desc, "关闭").show();
            }
        }, "POST");
    });
    
    function ajaxData(url, param, fn, type) {
        overlayer({text: "加载中..."});
        ajax(url, param, function (res) {
            if (res.result == 0) {
                hidelayer("加载成功~.~");
                if (fn) fn(res.data);
            } else {
                hidelayer("出错了");
                say("获取数据错误：" + res.err_desc);
            }
        }, type);
    }
})(jQuery);
