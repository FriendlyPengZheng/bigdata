package com.taomee.bigdata;

import com.taomee.bigdata.hbase.bean.dao.impl.ResultDaoImpl;



/**
 * 多线程测试接口的稳定性
 * Created by looper on 2017/4/10.
 */
public class TestHbaseThread {

    static ResultDaoImpl resultDaoImp = new ResultDaoImpl();

    public static void main(String[] args) {

        System.out.println("测试开始");
        for (int i = 0; i < 200; i++) {
            // ResultDaoImp resultDaoImp = new ResultDaoImp();
            final int j = i;
            // System.out.println("测试开始");
        //    new Thread(new Runnable() {
          //      public void run() {
                    com.taomee.bigdata.hbase.bean.ResultBean data_month_Row = new com.taomee.bigdata.hbase.bean.ResultBean(j + 1, 100, 1488729600L, 288289d);
                    //System.out.println(data_month_Row);
                    //resultDaoImp.putData(data_month_Row, 0);
            //    }
            //}, "线程" + i).start();
            //  System.out.println("测试结束");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*for (int i = 0; i < 200; i++) {
            // ResultDaoImp resultDaoImp = new ResultDaoImp();
            final int j = i;
            // System.out.println("测试开始");
            new Thread(new Runnable() {
                public void run() {
                    com.taomee.bigdata.hbase.bean.ResultBean data_month_Row = new com.taomee.bigdata.hbase.bean.ResultBean(j+1, 100, 1488729600L, 288289d);
                    //System.out.println(data_month_Row);
                    resultDaoImp.putData(data_month_Row,0);
                }
            }, "线程" + i).start();
            //  System.out.println("测试结束");
        }
        System.out.println("测试结束");
    }*/

   /* public static int getRandom(long seed)
    {
        //Long random_seed =seed ;
        Random random = new Random(seed);
        return random.nextInt(1000);
    }*/
    }
}
