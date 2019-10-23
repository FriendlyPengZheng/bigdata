/* 初始化默认时间段 */
var dateArr15 = new Array();
for(var d=0;d<=14;d++)
{
	dateArr15.push(getDateStr(d-14));
}

const $table = $(".pdt-list tbody").empty();
$.each(gpzsArr, function(key, item) {
    var tR = '<tr>';
    if(key%2 == 0){
        tR = '<tr class="pure-table-odd">';    
    }
    var $tr = $(tR+'<td><a href="../product/product.html?gameId='+item["game_id"]+'&gpzsId='+item["gpzs_id"]+'">'+games[item["game_id"]]+'</a></td><td>Loading...</td><td>Loading...</td><td>Loading...</td></tr>');
    generateAllData(item["game_id"],item["gpzs_id"], $tr);
    $table.append($tr);
});

function generateAllData(gameId,gpzsId, $tr)
{
    var ret = new Array();
    var newusersDataInfo = {'data_name' :'新增人次','type':1,'stid':'_newac_','sstid':'_newac_','op_fields':'','op_type':'count','range':'','period':4,'factor':1,'precision':0,'unit':''};
    var newusersExprs = {'data_name' : '新增人次','period':5,'precision':0,'unit':'','expr':"{0}"};
      
    var incomeDataInfo = {"data_name" :"收入",'type':1,'stid':'_acpay_','sstid':'_acpay_','op_fields':'_amt_','op_type':'sum','range':'','period':4,'factor':0.01,'precision':2,'unit':''};
    var incomeExprs = {"data_name" : "收入",'period':5,'precision':2,'unit':'','expr':"{0}"};

    var todayNew = 0;
    var todayIn = 0
    var yestIn  = 0;
    var percent = 0;
    //var percent = yestIn == 0 ? 0 : ((todayIn-yestIn)/yestIn*100).toFixed(2)+"%";
	
	$.ajax({
		url:reqUrl,
		type:'POST',
		dataType:'json',
		data:{  r:'common/data/getRealTimeSeries',
				qoq:0,
				yoy:0,
				average:0,
				calc_type:'sum',
				fill_null:0,
				sum:1,
				data_info:{0:newusersDataInfo,1:incomeDataInfo},
                exprs:{0:newusersExprs,1:incomeExprs},
				period:5,
				searchValue:'',
				from:{0:dateArr15[13],1:dateArr15[14]},
				to:{0:dateArr15[13],1:dateArr15[14]},
				contrast:0,
				gpzs_id:gpzsId,
				game_id:gameId
			},
		success:function(data){
            if(data.data == null || data.data == ""){
            	console.log("data null gameId:"+gameId);
                ret = ["N/A","N/A","N/A"]; 
            }else{
                todayNew = data["data"][1]["sum"]["data"][0];
				todayIn  = data["data"][1]["sum"]["data"][1];
				yestIn   = data["data"][0]["sum"]["data"][1];
				
				if(todayNew == "" || todayNew == undefined || todayNew == null){
				    todayNew = "N/A"; 
				}
				if(todayIn == "" || todayIn == undefined || todayIn == null){
				    todayIn = "N/A"; 
				    percent = "N/A";
				}else{
				    todayIn = parseInt(todayIn);
				}
				if(yestIn == "" || yestIn == undefined || yestIn == null){
				    yestIn = "N/A"; 
				    percent = "N/A";
				}else{
				    yestIn = parseInt(yestIn);
				}
				if(todayIn != 'N/A' && yestIn !='N/A'){
			        percent = yestIn == 0 ? 0 : ((todayIn-yestIn)/yestIn*100).toFixed(2)+"%";
			    }
				$tr.find("td").eq(1).html(todayNew);
			    $tr.find("td").eq(2).html(todayIn);
			    $tr.find("td").eq(3).html(percent);
            }
		},
		error:function(){
            $tr.find("td").eq(1).html('ajax err');
		    $tr.find("td").eq(2).html('ajax err');
		    $tr.find("td").eq(3).html('ajax err');
		}
    });
    
    
    
    
}
