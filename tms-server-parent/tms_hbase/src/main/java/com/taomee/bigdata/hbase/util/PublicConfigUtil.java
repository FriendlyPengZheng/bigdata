package com.taomee.bigdata.hbase.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * 公共配置
 */
public class PublicConfigUtil {
    private static final Log LOG = LogFactory.getLog(PublicConfigUtil.class);

    private static Properties properties = new Properties();
    private PublicConfigUtil() {}

    static {
        try {
            properties.load(PublicConfigUtil.class.getClassLoader().getResourceAsStream("public_system.properties"));
        } catch (Exception e) {
            LOG.error("加载文件异常" + e);
        }
    }

    public static String readConfig(String key) {
        return (String) properties.get(key);
    }

}
