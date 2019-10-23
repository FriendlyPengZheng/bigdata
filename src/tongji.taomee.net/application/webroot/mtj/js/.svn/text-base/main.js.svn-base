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
    var $tr = $(tR+'<td><a href="/product/product.html?gameId='+item["game_id"]+'&gpzsId='+item["gpzs_id"]+'">'+games[item["game_id"]]+'</a></td><td>Loading...</td><td>Loading...</td><td>Loading...</td></tr>');
    generateAllData(item["game_id"],item["gpzs_id"], $tr);
    $table.append($tr);
});

function generateAllData(gameId,gpzsId, $tr)
{
    var ret = new Array();
    var newusersDataInfo = {'data_name' :'新增用户数','type':2,'task_id':14,'range':'','factor':1,'precision':0,'unit':''};
    var newusersExprs = {'data_name' : '新增用户数','period':1,'precision':0,'unit':'','expr':"{0}"};
    var incomeDataInfo = {"data_name" :"收入总额（元）",'type':1,'stid':'_acpay_','sstid':'_acpay_','op_fields':'_amt_','op_type':'sum','range':'','period':1,'factor':0.01,'precision':2,'unit':''};
    var incomeExprs = {"data_name" : "收入总额（元）",'period':1,'precision':2,'unit':'','expr':"{1}"};
	$.ajax({
		url:reqUrl,
		type:'POST',
		dataType:'json',
		data:{  r:'common/data/getTimeSeries',
				qoq:0,
				yoy:0,
				average:0,
				sum:0,
				data_info:{0:newusersDataInfo,1:incomeDataInfo},
                exprs:{0:newusersExprs,1:incomeExprs},
				period:1,
				searchValue:'',
				from:{0:dateArr15[0]},
				to:{0:dateArr15[14]},
				contrast:0,
				gpzs_id:gpzsId,
				game_id:gameId
			},
		success:function(data){
            if(data.data == null || data.data == ""){
                ret = ["N/A","N/A","N/A"]; 
            }else{
                var newusersData = data["data"][0]["data"][0]["data"];
                var incomeData   = data["data"][0]["data"][1]["data"];

                if(newusersData == ""){
                    ret[0] = "N/A"; 
                }else{
                    ret[0] = newusersData[14];
                }

                if(incomeData == ""){
                    ret[1] = "N/A";
                    ret[2] = "N/A";
                }else{
                    var todayIn = parseInt(incomeData[14]);
                    var yestIn  = parseInt(incomeData[13]);
                    var percent = yestIn == 0 ? 0 : ((todayIn-yestIn)/yestIn*100).toFixed(2)+"%";
                    ret[1] = todayIn;
                    ret[2] = percent;
                }
            }
            $tr.find("td").eq(1).html(ret[0]);
            $tr.find("td").eq(2).html(ret[1]);
            $tr.find("td").eq(3).html(ret[2]);
		},
		error:function(){
            $tr.find("td").eq(1).html("ajax err");
            $tr.find("td").eq(2).html("ajax err");
            $tr.find("td").eq(3).html("ajax err");
		}
    });
}
