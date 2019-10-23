/**
 *  StatInfo 自定义模块
 * @type {StatInfo}
 */
module.exports = StatInfo;
//global.statTM.OpCode = require("./opCode.js");
var statCommon = require("./statCommon.js");
/**
 * 私有变量
 * @type {number}
 */
var sc_key_maxsz = 64;
var sc_value_maxsz = 64;

/**
 *
 *将数据绑定到this上，避免因为闭包问题导致数据重写
 *
 */
function StatInfo() {
    this.m_has_op = false;
    this.m_info = new Array();
    this.m_ops=new Array();
    var i;
    for (i = 0; i < statTM.OpCode.OP_END; i++) {
        this.m_ops[i]= new Array();
    }
    this.clear();
}

/**
 * 添加info信息
 */
StatInfo.prototype.add_info=function(key, value)
{
    statCommon.stat_trim_underscore(key+"");//避免因为传进来整形，导致程序崩溃
    if (typeof value == "string") {
        if (!((this.m_info.length <= 30))) {
            console.warn(value + "值不合法,or 数组长度超过30");
            return;
        }
    } else {
        if (!(value > 0 && this.m_info.length <= 30)) {
            console.warn(value + "小于0,or 数组长度超过30");
            return;
        }
    }
    this.m_info[key] = value;
}

StatInfo.prototype.add_op = function (op, key1, key2) {
    statCommon.stat_trim_underscore(key1+"");
    statCommon.stat_trim_underscore(key2+"");
    /**
     * hasOwnProperty 判断数组当中的key是否存在
     */
    if (!(this.is_valid_op(op) && this.m_info.hasOwnProperty(key1))) {
        return;
    }
    switch (op) {
        case statTM.OpCode.OP_ITEM_SUM:
        case statTM.OpCode.OP_ITEM_MAX:
        case statTM.OpCode.OP_ITEM_SET:
            if (!(this.m_info.hasOwnProperty(key2))) {
                return;
            }
            key1 = key1 + "," + key2;
            break;
        default:
            break;
    }
    /**
     * 不能直接赋值，需要push
     */
    this.m_ops[op].push(key1);

    this.m_has_op=true;

}

StatInfo.prototype.clear=function () {
    this.m_info=new Array();
    if(this.m_has_op == true) {
        var i;
        for (i = statTM.OpCode.OP_BEGIN + 1; i != statTM.OpCode.OP_END; i++)
        {
            this.m_ops[i]=new Array();
        }
        this.m_has_op=false;
    }
}

StatInfo.prototype.serialize=function () {
    var out="";
    for(var a in this.m_info)
    {
        out+="\t";
        out +=(a+"="+this.m_info[a]);
    }

    if(this.m_has_op){

        var op = new Array(
            "",
                "sum:", "max:", "set:", "ucount:",
                "item:", "item_sum:", "item_max:", "item_set:",
                "sum_distr:", "max_distr:", "min_distr:",
                "set_distr:",
                "ip_distr:");
        var vline = "";

        out += "\t_op_=";


        for(var i = statTM.OpCode.OP_BEGIN;i != statTM.OpCode.OP_END;i++){

            if(this.m_ops[i].length != 0){
                out += vline;
                out += this.serialize_op(op[i],this.m_ops[i]);

                vline = "|";
            }
        }
    }

    return out;

}


StatInfo.prototype.serialize_op=function(op,keys)
{
    var vline="";
    var i=0;
    var oss="";
    for(i=0;i<keys.length;i++)
    {
        oss+=vline+op+keys[i];
        vline="|";
    }
    return oss;
}

StatInfo.prototype.is_valid_value = function (value) {
    return statCommon.size_between(value, 1, this.sc_value_maxsz) && !statCommon.key_no_invalid_chars(value);
}

/**
 * 判断是否是合法操作符Op
 * @param op
 * @returns {boolean}
 */
StatInfo.prototype.is_valid_op = function (op) {
    return (op > statTM.OpCode.OP_BEGIN && op < statTM.OpCode.OP_END);
}
