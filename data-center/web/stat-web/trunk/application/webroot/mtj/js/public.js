/* init start */
//var reqUrl = '/';
var reqUrl = 'https://mtj.taomee.com';

var colors = new Array();
colors['green']  = "rgb(28, 184, 65)";
colors['orange'] = "rgb(223, 117, 20)";
colors['red']    = "rgb(255,61,103)";
colors['blue']   = "rgb(0,120,231)";
colors['yellow'] = "rgb(255,194,51)";
colors['brown']  = "rgb(0,120,231)";

var pieColors = new Array();
pieColors = ["rgb(223, 117, 20)","rgb(28, 184, 65)","rgb(255,61,103)","rgb(0,120,231)","rgb(255,194,51)","rgb(0,120,231)"];

/* init end */

function getCookie(c_name)
{
	if (document.cookie.length>0)
	{
	    c_start=document.cookie.indexOf(c_name + "=");
	    if (c_start!=-1)
		{
		    c_start=c_start + c_name.length+1 ;
		    c_end=document.cookie.indexOf(";",c_start);
		    if (c_end==-1) c_end=document.cookie.length;
		    return unescape(document.cookie.substring(c_start,c_end));
		}
	}
	return "";
}

function setCookie(c_name,value,expiredays)
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate()+expiredays);
	document.cookie=c_name+ "=" +escape(value)+((expiredays==null) ? "" : ";expires="+exdate.toGMTString())+";path=/";
}

function getGameName(gameId)
{
	return games[gameId];
}

function getDateStr(day) { 
    var dd = new Date(); 
    dd.setDate(dd.getDate()+day);//获取AddDayCount天后的日期 
    var y = dd.getFullYear(); 
    var m = dd.getMonth()+1;//获取当前月份的日期 
    var d = dd.getDate(); 
    return y+"-"+m+"-"+d; 
}

Array.prototype.sum = function (){
    return this.reduce(function (partial, value){
        partial = partial == null ? 0 : partial;
        value = value == null ? 0 : value;
        return parseInt(partial) + parseInt(value);
    })
};
Array.prototype.maxValue = function(){ 
    return Math.max.apply({},this) 
} 
Array.prototype.min = function(){ 
    return Math.min.apply({},this) 
} 
Array.prototype.inArray = function (element) {
　　for (var i = 0; i < this.length; i++) {
　　    if (this[i] == element) {
　　        return true;
        }
    }
    return false;
}

