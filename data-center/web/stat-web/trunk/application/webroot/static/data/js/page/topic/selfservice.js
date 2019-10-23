/**
 * @fileOverview 数据自主查询功能页面
 * @name selfservice.js
 * @author Maverick
 */
$(document).ready(function () {
	
});


// 查询用 ajax 请求参数
var param = {
	"game_id": 1,
	"platform_id": -1,
	"zone_id": -1,
	"server_id": -1,
	"gpzs_id": 105,
	"operation": "setdiff",    // intersect, union
	"operands": [
		{
			"report_name": "life eatting item ucount",
			"stid": "life",
			"sstid": "eatting",
			"op_type": "ucount",
			"op_fields": "item",
			"range": "rice",
			"data_name": "rice ucount",
			"data_id": 100,
			"periods": [
				{
					"from": "2014-10-01",
					"to": "2014-10-07"
				}, {
					"from": "2014-10-08",
					"to": "2014-10-14"
				}
			]
		}, {
			"report_name": "life eatting item ucount",
			"stid": "life",
			"sstid": "eatting",
			"op_type": "ucount",
			"op_fields": "item",
			"range": "wheat",
			"data_name": "wheat ucount",
			"data_id": 101,
			"periods": [
				{
					"from": "2014-10-01",
					"to": "2014-10-07"
				}, {
					"from": "2014-10-08",
					"to": "2014-10-14"
				}
			]
		}
	]
}
