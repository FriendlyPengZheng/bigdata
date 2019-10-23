/* filter button */
$('.filter-button').click(function(){
	var top = $('.container').offset().top;
//alert(top);
	var w = $(document).width();
	var h = $(document).height();
	h = h-top;

    var channelUl = $('#filter-form-channel');
    var channelHtml = channelUl.html();
    console.log(channelHtml);
    if(channelHtml == "" || channelHtml == undefined){
        var platform = getPlatform(gameId);
        var liHtml = '';
        for(var i=0;i<platform.length;i++)
        {
            liHtml += '<li data="'+platform[i]["gpzs_id"]+'|'+platform[i]["platform_id"]+'|'+platform[i]["server_id"]+'|'+platform[i]["zone_id"]+'">'+platform[i]["gpzs_name"]+'</li>';
        }
        channelUl.html(liHtml);
    }

    $('body').css("overflow","hidden");

	$('#J_mask').css({
		"width"   : w+"px",
		"height"  : h+"px",
		"top"     : top+"px",
	}).show();
});

/* close button */
$('.close-btn').click(function(){
    $('body').css("overflow","auto");
	$('#J_mask').hide();
});

/* filter reset button */
$('.filter-reset').click(function(){
	$('.filter-container .filter-items .filter-ul li').removeClass('filtered');
	$("input[type='date']").val(0);
});

/* filter li selected */
$(document).on('click', '#filter-form-channel li', function() {
	$(this).siblings().removeClass('filtered');
	$(this).addClass('filtered');

    var platformId = $(this).attr("data").split("|")[1];
    var zoneServer = getZoneServer(gameId,platformId);
    var serverUl = $('#filter-form-server');
    var serverHtml = serverUl.html();
    var liHtml = '';
    for(var i=0;i<zoneServer.length;i++)
    {
        liHtml += '<li data="'+zoneServer[i]["gpzs_id"]+'|'+zoneServer[i]["platform_id"]+'|'+zoneServer[i]["server_id"]+'|'+zoneServer[i]["zone_id"]+'">'+zoneServer[i]["gpzs_name"]+'</li>';
    }
    serverUl.html(liHtml);
});

$(document).on('click','#filter-form-server li',function(){
    $(this).siblings().removeClass('filtered');
    $(this).addClass('filtered');
});

$('#filter-form-date li').click(function(){
    $(this).siblings().removeClass('filtered');
    $(this).addClass('filtered');
});

$('.filter-container .pure-button-active').click(function(){
	var fChannel = $('.filter-container .filter-items .filter-form-channel li.filtered').attr("data");
	var fServer  = $('.filter-container .filter-items .filter-form-server li.filtered').attr("data");
	var fDate    = $('.filter-container .filter-items .filter-form-date    li.filtered').attr("data");
	var sDate = '';
	var eDate = '';

	if(!fDate){//自定义或为了容错而默认一个时段（1个月）
		date = new Date();
		eDate = $('#filter-edate').val() ? $('#filter-edate').val() : date.toLocaleDateString();
		sDate = $('#filter-sdate').val() ? $('#filter-sdate').val() : new Date(date.setMonth((date.getMonth()-1))).toLocaleDateString();
	}else{
        date  = new Date();
        fDate = parseInt(fDate);
        fDate--;
        sDate = getDateStr(-fDate);
        eDate = date.toLocaleDateString();
    }

    if(dataKey == '总在线'){
        eDate = sDate;
    }

    var platformId = -1;
    var serverId   = -1;
    var zoneId     = -1;
    var gpzsId     = gpzsId;
    
    if(fChannel){
        var ids = fChannel.split("|");
        gpzsId     = ids[0];
        platformId = ids[1];
        serverId   = ids[2];
        zoneId     = ids[3];
    }

    if(fServer){
        var ids = fServer.split("|");
        gpzsId     = ids[0];
        platformId = ids[1];
        serverId   = ids[2];
        zoneId     = ids[3];
    }
    if(gpzsId == "" || gpzsId == undefined){
        alert("请选择渠道/平台或区服");
        return false;
    }

    console.log('sdate:'+sDate+',edate:'+eDate);
    console.log('gpzs:'+gpzsId+',platform:'+platformId+',server:'+serverId+',zone:'+zoneId);
    var reqData = {  r:'common/data/getTimeSeries',
				qoq:0,
				yoy:0,
				average:0,
				data_info:dataInfo,
                exprs:exprs,
				period:1,
				searchValue:'',
				from:{0:sDate},
				to:{0:eDate},
				gpzs_id:gpzsId,
				game_id:gameId,
                platform_id:platformId
			};
    if(dataKey != "新增留存" && dataKey != '活跃留存'){
        reqData["sum"] = 0;
        reqData["contrast"] = 0;
    }
    if(dataKey == '总在线'){
        var reqData = {  
            r:'common/data/getRealTimeSeries',
			qoq:0,
			yoy:0,
            average:0,
            check_all:1,
            all_name:"总在线",
            fill_null:1,
            sum:0,
			data_info:dataInfo,
            exprs:exprs,
			period:4,
            searchValue:'',
			from:{0:sDate},
			to:{0:eDate},
			gpzs_id:gpzsId,
			game_id:gameId
		};
    }
    if(dataKey == '新增分布' || dataKey == '活跃分布'){
        var reqData = {            
            r:'common/data/getDistribution',
            qoq:0,
            yoy:0,
            average:0,
            period:1,
            searchValue:'',
            data_info:dataInfo,
            from:{0:sDate},
            to:{0:eDate},
            platform_id:platformId,
            zone_id:zoneId,
            server_id:serverId,
            gpzs_id:gpzsId,
            game_id:gameId
        };
    }
	/* 请求过滤数据开始 */
	$.ajax({
		url:reqUrl,
		type:'POST',
		dataType:'json',
		data:reqData,
		success:function(data){
            if(dataKey == '新增留存' || dataKey == '活跃留存' ){
                var graphData = data['data'][0]['data'];
                var keyData   = data['data'][0]['key'];
                retentionFunc(graphData,keyData);
            }else if(dataKey == 'ARRPU' || dataKey == 'ARPU' || dataKey == '付费渗透'){
                var graphData = data['data'][0]['data'];
                var keyData   = data['data'][0]['key'];
                penetrationFunc(graphData,keyData);
            }else if(dataKey == '总在线'){
                var graphData = data['data'][0]['data'].pop();
                graphData     = graphData['data'];
                var keyData   = data['data'][0]['key'];
                onlineFunc(graphData,keyData);
            }else if(dataKey == '新增分布' || dataKey == '活跃分布'){
                var graphData = data["data"][0]["data"][0];
                var keyData = data["data"][0]["key"];
                distributionFunc(graphData,keyData,sDate,eDate);
            }else{
                var graphData = data["data"][0]["data"][0]["data"];
                var keyData = data["data"][0]["key"];
                keyFunc(graphData,keyData);
            }

            /* 隐藏遮罩 */
            $('#J_mask').hide();
		},
		error:function(){
			
		}
	});
	/* 请求过滤数据结束 */

});

function keyFunc(graphData,keyData)
{
	/* 创建曲线图 */ 
	var ctx = canvasChart.getContext("2d");
	var color = colors["orange"];//颜色
	var chartData = {
		labels : keyData,
		datasets : [
			{
				label : dataKey,
				backgroundColor : color,
				borderColor : color,
				fill : false,
				data : graphData
			},
		]
	};
	new Chart(ctx, {
		type:'line',
		data: chartData
	});

    /* 生成表格 */
    var tableHtml = '<thead><th>日期</th><th>'+dataKey+'</th></thead>';
    tableHtml += '<tbody>';
    for(var i=0;i<graphData.length;i++){
        if(i %2 == 0){
            tableHtml += '<tr class="pure-table-odd">';
        }else{
            tableHtml += '<tr>';
        }
        tableHtml += '<td>'+keyData[i]+'</td>';
        tableHtml += '<td>'+graphData[i]+'</td></tr>';
    }
    tableHtml += '</tbody>';
    $('#chartTable').html(tableHtml);
    $('.f-date').html(keyData[0]+'至'+keyData[keyData.length-1]);
}

function retentionFunc(graphData,keyData)
{
    /* 生成表格 */
    var tableHtml = '<thead><th>日期</th>';
    if(dataKey == '新增留存'){
        for(var i=0;i<graphData.length;i++)
        {
            tableHtml += '<th>'+(i+1)+'日</th>';
        } 
    }else{
        tableHtml += '<th>1日</th><th>7日</th><th>14日</th><th>30日</th>';
    }
    tableHtml +='</thead><tobdy>';
    for(var i=0;i<keyData.length;i++){
        if(i %2 == 0){
            tableHtml += '<tr class="pure-table-odd">';
        }else{
            tableHtml += '<tr>';
        }
        tableHtml += '<td>'+keyData[i].substr(5)+'</td>';
        for(var j=0;j<graphData.length;j++)
        {
            tableHtml += '<td>'+graphData[j]['data'][i]+'</td>';
        }
        tableHtml += '</tr>';
    }
    $('#chartTable').html(tableHtml);
    $('.f-date').html(keyData[0]+'至'+keyData[keyData.length-1]);
}

function penetrationFunc(graphData,keyData)
{
    var canvasChart = $("#keyChart").get(0);
    var datasets = new Array();
    for(var i=0;i<graphData.length;i++)
    {
        datasets[i] = {label:graphData[i]['name']+'%',backgroundColor:pieColors[i],borderColor:pieColors[i],fill:false,data:graphData[i]['data']};
    }
	/* 创建曲线图 */ 
	var ctx = canvasChart.getContext("2d");
	var chartData = {
		labels   : keyData,
		datasets : datasets 
	};
	new Chart(ctx, {
		type:'line',
		data: chartData
	});
    /* 生成表格 */
    var tableHtml = '<thead><th>日期</th>';
    for(var i=0;i<graphData.length;i++)
    {
        tableHtml += '<th>'+graphData[i]["name"]+'</th>';
    }
    tableHtml += '</thead><tbody>';
    for(var i=0;i<keyData.length;i++)
    {
        if(i%2 == 0){
            tableHtml += '<tr class="pure-table-odd">';
        }else{
            tableHtml += '<tr>';
        }
        tableHtml += '<td>'+keyData[i]+'</td>';
        for(var j=0;j<graphData.length;j++)
        {
            tableHtml += '<td>'+graphData[j]["data"][i]+'</td>';
        }
        tableHtml += '</tr>';
    }
    tableHtml += '</tbody>';
    $('#chartTable').html(tableHtml);
    $('.f-date').html(keyData[0]+'至'+keyData[keyData.length-1]);
}

function onlineFunc(graphData,keyData)
{
    /* 创建曲线图 */ 
    var ctx = canvasChart.getContext("2d");
    var color = colors["orange"];//颜色
    var chartData = {
    	labels : keyData,
    	datasets : [
    		{
    			label : dataKey,
    			backgroundColor : color,
    			borderColor : color,
    			fill : false,
    			data : graphData
    		},
    	]
    };
    new Chart(ctx, {
    	type:'line',
    	data: chartData
    });
    
    /* 生成表格 */
    var tableHtml = '<thead><th>日期</th><th>'+dataKey+'</th></thead>';
    tableHtml += '<tbody>';
    for(var i=0;i<graphData.length;i++){
        if(i %2 == 0){
            tableHtml += '<tr class="pure-table-odd">';
        }else{
            tableHtml += '<tr>';
        }
        tableHtml += '<td>'+keyData[i]+'</td>';
        tableHtml += '<td>'+graphData[i]+'</td></tr>';
    }
    tableHtml += '</tbody>';
    $('#chartTable').html(tableHtml);
    $('.f-date').html(keyData[0]+'至'+keyData[keyData.length-1]);
}

function distributionFunc(graphData,keyData,sDate,eDate)
{
    var tableHtml = '<thead><tr><th>渠道</th><th>数量</th><th>占比</th></tr></thead><tbody>';
    for(var i=0;i<keyData.length;i++)
    {
        if(i%2 == 0){
            tableHtml += '<tr>';
        }else{
            tableHtml += '<tr class="pure-table-odd">';
        }   
        tableHtml += '<td>'+keyData[i]+'</td><td>'+graphData["data"][i]+'</td><td>'+graphData["percentage"][i]+'</td></tr>';
    }
    tableHtml +=  '</tbody>';
    $('#chartTable').html(tableHtml);
    $('.f-date').html(sDate+'至'+eDate);
}
