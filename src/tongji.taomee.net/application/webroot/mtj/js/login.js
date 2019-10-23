/* user login */
$('.pure-form input').focus(function(){
    $('.err-hint').css({"visibility":"hidden"});
});

$('#J_loginBtn').click(function(e){
	e.preventDefault();
    $(this).html("正在登录中...");
    var userName = $('#J_userName').val();
	var pwd      = $('#J_pwd').val();
//alert(userName+"-"+pwd);
	if(userName == ""  || pwd == ""){
		$('.err-hint .err-text').html('英文名和密码不能为空哟！');
		$('.err-hint').css({"visibility":"visible"});
	    return false;
	}else{
		/* AJAX请求登录开始 */
		$.ajax({
			url:reqUrl,
			type:'POST',
			//dataType:'jsonp',
			dataType:'json',
			data:{"r": 'user/login', "user_name":userName,"user_pwd":pwd},
			success:function(data){
			    if(data.result == 0){
                    setCookie("MTJ-USER",userName);
			    	window.location.href=data.data;
			    }else{
			    	$('.err-hint .err-text').html(data.err_desc);
			    	$('.err-hint').css({"visibility":"visible"});
			    }
			},
			error:function(){
				$('.err-hint .err-text').html('系统错误，请稍后再试!');
			    $('.err-hint').css({"visibility":"visible"});
			}
	    });
		/* AJAX请求登录结束 */
	}
});
