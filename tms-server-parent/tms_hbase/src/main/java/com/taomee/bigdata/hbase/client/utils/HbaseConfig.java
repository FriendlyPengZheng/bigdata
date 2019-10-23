package com.taomee.bigdata.hbase.client.utils;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 加载hbse的配置信息
 * Created by looper on 2017/3/30.
 */
public class HbaseConfig {
    private static Logger LOG = LoggerFactory.getLogger(HbaseConfig.class);

    private static org.apache.hadoop.conf.Configuration hConfig;
    private static final String resource = "hbase_config.xml";
    private static org.apache.commons.configuration.Configuration conf;

    /**
     * 静态方法块加载配置文件
     */
    static
    {
        try {
           conf = new XMLConfiguration(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回配置对象
     * @return
     */
    public static org.apache.hadoop.conf.Configuration getConf()
    {
        hConfig = HBaseConfiguration.create();
        hConfig.set("hbase.zookeeper.property.clientPort", conf.getString("zookeeper.property.clientPort"));
        hConfig.set("hbase.zookeeper.quorum", conf.getString("zookeeper.quorum"));
        //hConfig.set("hbase.master", conf.getString("master"));
        /*System.out.println("zookeeper.property.clientPort:" + conf.getString("zookeeper.property.clientPort"));
        System.out.println("zookeeper.quorum:" + conf.getString("zookeeper.quorum"));
        System.out.println("master:" + conf.getString("master"));*/
        LOG.debug("zookeeper.property.clientPort:" + conf.getString("zookeeper.property.clientPort"));
        LOG.debug("zookeeper.quorum:" + conf.getString("zookeeper.quorum"));
       // LOG.debug("master:" + conf.getString("master"));
        return hConfig;
    }
    /**
     * 测试配置文件能否正常加载
     *
     * @param args
     */
    public static void main(String[] args) {
        getConf();
    }
}
