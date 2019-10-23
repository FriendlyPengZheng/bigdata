/* 全游戏数组init */
var games = new Array();
var gpzsArr = new Array();

/* 用户登录信息 */
var account = localStorage.MTJ_USER;
var userKey = localStorage.MTJ_KEY;

/*Check Login*/
if(!isLogin()){
	if(account == "" || account == undefined){
		window.location.href = '../login/login.html';	
	}else{
		console.log("localStorage account:"+account+" will login");
		userLogin();
	}
}else{
    console.log("welcome:"+account);
	initGames();
}	   			

/* 使用localStorage信息进行用户登录 */
function userLogin()
{	
	$.ajax({
		url:reqUrl,
		type:'POST',
		async: false,
		dataType:'json',
		data:{"r": 'user/login', "user_name":account,"user_pwd":userKey},
		success:function(data){
		    if(data.result != 0){
		    	alert("登录状态失效，返回重新登录");
				window.location.href = '../login/login.html';
		    }else{
		    	initGames();
		    }
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			//alert(XMLHttpRequest.status+"|"+XMLHttpRequest.readyState+"|"+textStatus);
			//alert("登录ajaxErr，返回重新登录:"+account+"|"+userKey);
			window.location.href = '../login/login.html';
		}
   });
}
/* --------------通用方法定义开始-------------- */
/* 根据服务端session判断用户是否已登录 */
function isLogin()
{
	var ret = false;
    $.ajax({
        url  : reqUrl,
        type : "GET",
        async: false,
        dataType : "json",
        data : {r:"user/logined"},
        success:function(data){
            if(data.result == 0){
                ret = true;
            }
        }
    });
    return ret;
}

function getPlatform(gameId,stat=0)
{
    var ret = "";
    $.ajax({
        url  : reqUrl,
        type : "GET",
        async: false,
        dataType : "json",
        data : {r:"common/gpzs/getPlatform",game_id:gameId,status:stat},
        success:function(data){
            ret = data["data"];
        }
    });
    return ret;
}

function getZoneServer(gameId,platformId,stat=0)
{
    var ret = "";
    $.ajax({
        url  : reqUrl,
        type : "GET",
        async: false,
        dataType : "json",
        data :{r:"common/gpzs/getZoneServer",game_id:gameId,platform_id:platformId,status:stat},
        success:function(data){
            ret = data["data"];
        }
    });
    return ret;
}

function initGames()
{
	var showlist = [2,5,632,657,664,673,688];
    if(games.length == 0){
        $.ajax({
            url  : reqUrl,
            type : "GET",
            async: false,
            dataType : "json",
            data : {r:"common/game/getGameList"},
            success:function(data){
                var menu = '';
                for(var i=0;i<data["data"].length;i++){
                    if((data["data"][i]["func_slot"] & (1<<9)) && showlist.inArray(data["data"][i]["game_id"]) ){
                        menu += '<li class="pro-list-li"><a href="product.html?gameId='+data["data"][i]["game_id"]+'&gpzsId='+data["data"][i]["gpzs_id"]+'" class="pro-list-link">';
                        menu += data["data"][i]["game_name"]+'</a></li>';
                        gpzsArr.push({game_id:data["data"][i]["game_id"],gpzs_id:data["data"][i]["gpzs_id"]});
                    }
                    games[data["data"][i]["game_id"]] = data["data"][i]["game_name"];
                }
                $('.header-bar .pro-list-ul').html(menu);
            }
        });
    }    
}
/* --------------通用方法定义结束-------------- */

/* --------------按钮绑定事件开始-------------- */
/* 退出注销 */
$('.admin-info a').click(function(e){
	e.preventDefault();
	localStorage.MTJ_USER = '';
	localStorage.MTJ_KEY  = '';
	window.location.href = '../login/login.html';
	//setCookie('MTJ-USER','');
    //window.location.href = '/?r=user/logout';
});

/* 刷新页面 */
$('.refresh').click(function(){
	window.location.reload();
});

/* product list menu */
$('#J-menu').click(function(){
	$('.pro-list-ul').toggle()
});

/* tab menu */
$('.tab-menu li').click(function(){
	$(this).siblings().removeClass('pure-menu-selected');
	$(this).addClass('pure-menu-selected');
});
/* --------------按钮绑定事件结束-------------- */
	