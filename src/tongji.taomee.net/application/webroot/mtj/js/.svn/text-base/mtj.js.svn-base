/* user login */
var account = getCookie('MTJ-USER');//登录用户
console.log("userName:"+account);
if(account){
	$('.admin-info span').html(account);
}else{
    window.location.href = '/login/login.html';
}

/* 全游戏数组 */
var games = new Array();
var gpzsArr = new Array();
initGames();

/* 退出注销 */
$('.admin-info a').click(function(e){
	e.preventDefault();
	setCookie('MTJ-USER','');
    window.location.href = '/?r=user/logout';
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
                    if(data["data"][i]["func_slot"] & (1<<9)){
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
