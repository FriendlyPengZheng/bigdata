package com.taomee.bigdata;

import com.taomee.bigdata.hbase.bean.*;
import com.taomee.bigdata.hbase.bean.dao.impl.ResultDaoImpl;
import com.taomee.tms.mgr.entity.ResultInfo;

/**
 * Created by looper on 2017/4/6.
 */

//ResultBean  | server_id | data_id | time       | value |
//data_id  建议用long 类型
public class TestHbaseRB {

    public static void main(String[] args) {
        ResultDaoImpl resultDaoImp = new ResultDaoImpl();
        ResultInfo start_rb = new ResultInfo(4, 102, 1486569600L, 0d);
        ResultInfo stop_rb = new ResultInfo(4, 102, 1486742400L, 0d);
        ResultInfo dataRow = new ResultInfo(4, 103, 1486569600L, 5823d);

        //resultDaoImp.findListData(start_rb,stop_rb,0);
        resultDaoImp.putData(dataRow,0);

       /* //测试周数据
        com.taomee.bigdata.hbase.bean.ResultBean start_week_rb = new com.taomee.bigdata.hbase.bean.ResultBean(10, 169, 1486915200L, 0d);
        com.taomee.bigdata.hbase.bean.ResultBean stop_week_rb = new com.taomee.bigdata.hbase.bean.ResultBean(10, 169, 1488124800L, 0d);
        com.taomee.bigdata.hbase.bean.ResultBean data_week_Row = new com.taomee.bigdata.hbase.bean.ResultBean(10, 169, 1488729600L, 288289d);

        resultDaoImp.findListData(start_week_rb,stop_week_rb,1);
        resultDaoImp.putData(data_week_Row,1);

        //往月表当中插入记录
        com.taomee.bigdata.hbase.bean.ResultBean data_month_Row = new com.taomee.bigdata.hbase.bean.ResultBean(10, 161, 1488729600L, 288289d);
        resultDaoImp.putData(data_month_Row,2);

        //测试查询单条记录的文件
        com.taomee.bigdata.hbase.bean.ResultBean data_month_query_Row = new com.taomee.bigdata.hbase.bean.ResultBean(10, 161, 1488729600L,0d);
        com.taomee.bigdata.hbase.bean.ResultBean resultBean = resultDaoImp.getOneData(data_month_query_Row,2);
        System.out.println(resultBean);
*/

    }
}
