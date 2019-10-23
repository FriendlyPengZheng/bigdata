package com.taomee.bigdata.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.taomee.bigdata.lib.ReturnCode;

public class LogAnalyser
{
    //                                            [0]       [1]       [2]       [3]      [4]      [5]      [6]     [7]      [8]
    private final static String[] basicKeys = { "_hip_", "_stid_", "_sstid_", "_gid_", "_pid_", "_zid_", "_sid_", "_ts_", "_acid_" };//所有统计项都要有的key
    public final static String HOST_IP   = basicKeys[0];    //落统计log的服务器IP
    public final static String STAT_ID   = basicKeys[1];    //统计项ID
    public final static String STID      = basicKeys[1];    //统计项ID
    public final static String SSTAT_ID  = basicKeys[2];    //子统计项ID
    public final static String SSTID     = basicKeys[2];    //子统计项ID
    public final static String GAME      = basicKeys[3];    //游戏名
    public final static String PLATFORM  = basicKeys[4];    //运营平台
    public final static String ZONE      = basicKeys[5];    //大区
    public final static String SERVER    = basicKeys[6];    //服务器
    public final static String TIME      = basicKeys[7];    //时间戳
    public final static String ACCOUNT   = basicKeys[8];    //账号
    public final static String ROLE      = "_plid_";        //角色ID
    public final static String CLIENT_IP = "_cip_";         //客户端IP
    public final static String OP        = "_op_";          //操作符

    public final static String KEY_AMT = "_amt_";			//付费额
    public final static String KEY_LV = "_lv_";				//等级
    
    private HashMap<String, String> keyValueSet = new HashMap<String, String>();
    private ReturnCode code = ReturnCode.get();
    private String log = null;
    private OperatorAnalyser opAnalyser = new OperatorAnalyser();
    private ArrayList<String[]> output = new ArrayList<String[]>(64 * 1024);

    public int analysis(String log) {
        keyValueSet.clear();
        output.clear();
        this.log = new String(log);

        String[] keyValue = log.trim().split("\t");
        if(keyValue == null || keyValue.length == 0) {
            return code.setCode("W_LOG_EMPTY");
        }

        //获取每个字段
        for(int i=0; i<keyValue.length; i++) {
            String[] kv = keyValue[i].split("=");
            if(kv == null || kv.length != 2) {
                code.setCode("E_KV_FORMAT", String.format("[%s][%s]", log, keyValue[i]));
                keyValueSet.put(kv[0].trim(), "-1");
                continue;
            }

            //if(keyValueSet.containsKey(kv[0])) {SDK已经判断，不会出现次情况
            //    return code.setCode("E_KEY_EXIST", String.format("[%s][%s]", log, keyValue[i]));
            //}

            keyValueSet.put(kv[0].trim(), kv[1].trim());
        }

        //检查是否包含了必须的字段
        if(!checkBasicKeys()) {
            return code.getLastCode();
        }

        try {
            Integer.valueOf(keyValueSet.get(GAME));
        } catch (NumberFormatException e) {
            return code.setCode("E_LOG_GAMEID_NOTNUM");
        }
        try {
            Integer.valueOf(keyValueSet.get(PLATFORM));
        } catch (NumberFormatException e) {
            return code.setCode("E_LOG_PLATFORMID_NOTNUM");
        }
        try {
            Integer.valueOf(keyValueSet.get(ZONE));
        } catch (NumberFormatException e) {
            return code.setCode("E_LOG_ZONEID_NOTNUM");
        }
        try {
            Integer.valueOf(keyValueSet.get(SERVER));
        } catch (NumberFormatException e) {
            return code.setCode("E_LOG_SERVERID_NOTNUM");
        }

        return code.setOkCode();
    }


    public int analysisAndGet(String log) {
        int ret = analysis(log);
        if(!code.isOK(ret)) {
            return ret;
        }
        opAnalyser.analysis(keyValueSet.get(OP));

        while(opAnalyser.hasNext()) {
            //输出格式  op time [value] account[-player] game platform zone server stid sstid [op-field] [key]
            String[] nextOp = opAnalyser.next();
            String[] zs = combine(getValue(ZONE), getValue(SERVER), getValue(PLATFORM));
            String ap = getAPid();
            for(int i=0; i<zs.length; i++) {
                if(nextOp == null) {
                    break;
                }
                if(nextOp.length == 1) {//UCOUNT,COUNT
                    output.add(
                            new String[] {
                                nextOp[0].toUpperCase(),
                                getValue(TIME),
                                null,
                                String.format("%s\t%s\t%s\t%s\t%s", ap, getValue(GAME), zs[i], getValue(STAT_ID), getValue(SSTAT_ID))
                            });
                } else if(nextOp.length == 2) {//SUM,MAX,SET,IP_DISTR,DISTR*
                    if(nextOp[0].compareToIgnoreCase("item") == 0) {
                        output.add(
                                new String[] {
                                    "UCOUNT",
                                    getValue(TIME),
                                    null,
                                    String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s", ap, getValue(GAME), zs[i], getValue(STAT_ID), getValue(SSTAT_ID), nextOp[1], getValue(nextOp[1]))
                                });
                        output.add(
                                new String[] {
                                    "COUNT",
                                    getValue(TIME),
                                    null,
                                    String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s", ap, getValue(GAME), zs[i], getValue(STAT_ID), getValue(SSTAT_ID), nextOp[1], getValue(nextOp[1]))
                                });
                    } else {
                        output.add(
                                new String[] {
                                    nextOp[0].toUpperCase(),
                                    getValue(TIME),
                                    getValue(nextOp[1]),
                                    String.format("%s\t%s\t%s\t%s\t%s\t%s", ap, getValue(GAME), zs[i], getValue(STAT_ID), getValue(SSTAT_ID), nextOp[1])
                                });
                    }
                } else {//ITEM_SUM,ITEM_MAX,ITEM_SET,ITEM_DISTR
                    output.add(
                            new String[] {
                                nextOp[0].toUpperCase(),
                                getValue(TIME),
                                getValue(nextOp[2]),
                                String.format("%s\t%s\t%s\t%s\t%s\t%s,%s\t%s", ap, getValue(GAME), zs[i], getValue(STAT_ID), getValue(SSTAT_ID), nextOp[1], nextOp[2], getValue(nextOp[1]))
                            });
                }
            }
        }

        return code.setOkCode();
    }
    
    //原始日志解析为:<k,v>
    public int parse(String log) {
        keyValueSet.clear();
        output.clear();
        this.log = log;

        String[] keyValue = log.trim().split("\t");
        if(keyValue == null || keyValue.length == 0) {
            return code.setCode("W_LOG_EMPTY");
        }

        //获取每个字段
        for(int i=0; i<keyValue.length; i++) {
            if(keyValue[i].trim().length() == 0)    continue;
            String[] kv = keyValue[i].split("=");
            if(kv == null || kv.length != 2) {
                return code.setCode("E_KV_FORMAT", String.format("[%s][%s]", log, keyValue[i]));
            }

            if(keyValueSet.containsKey(kv[0].trim())) {
                return code.setCode("E_KEY_EXIST", String.format("[%s][%s]", log, keyValue[i]));
            }

            keyValueSet.put(kv[0].trim(), kv[1].trim());
        }

        //检查是否包含了必须的字段
        if(!checkBasicKeys()) {
            return code.getLastCode();
        }

        return code.setOkCode();
    }
    
    /**return gid zid sid pid apid*/
    public String getKey() {        
        return String.format("%s\t%s\t%s\t%s\t%s", 
        					getValue(GAME)
        				   ,getValue(ZONE)
        				   ,getValue(SERVER)
        				   ,getValue(PLATFORM)
        				   ,getAPid());
    }
    
    /**return gid zid sid pid [ext_key] apid*/
    public String getExtKey(String extKey) {        
        return String.format("%s\t%s\t%s\t%s\t%s\t%s", 
        					getValue(GAME)
        				   ,getValue(ZONE)
        				   ,getValue(SERVER)
        				   ,getValue(PLATFORM)
        				   ,getValue(extKey)
        				   ,getAPid());
    }

    public String getValue(String key) {
        if(key == null) {
            code.setCode("W_KEY_EMPTY", String.format("[%s][%s]", log, key));
            return null;
        }
        String ret = keyValueSet.get(key);
        if(ret == null) {
            code.setCode("E_KEY_NOT_EXIST", String.format("[%s][%s]", log, key));
        } else {
            code.setOkCode();
        }
        return ret;
    }

    public boolean containsKey(String key) {
        return keyValueSet.containsKey(key);
    }

    public ArrayList<String[]> getOutput() {
        return output;
    }

    private boolean checkBasicKeys() {
        for(int i=0; i<basicKeys.length; i++) {
            if(!keyValueSet.containsKey(basicKeys[i])) {
                code.setCode("E_BASIC_KEY_NOT_EXIST", String.format("[%s][%s]", log, basicKeys[i]));
                return false;
            }
        }
        return true;
    }

    public String[] combine() {
        return combine(getValue(ZONE), getValue(SERVER), getValue(PLATFORM));
    }

    private String[] combine(String zone, String server, String platform) {
        if(zone.compareTo("-1") == 0 &&
                server.compareTo("-1") == 0) {
            if(platform.compareTo("-1") == 0) {
                return new String[]{"-1\t-1\t-1"};
            } else {
                return new String[]{ "-1\t-1\t" + platform,
                                "-1\t-1\t-1"};
            }
        }
        if(platform.compareTo("-1") == 0) {
            return new String[]{ zone + "\t" + server + "\t-1",
                                "-1\t-1\t-1" };
        }
        return new String[]{ zone + "\t" + server + "\t" + platform,
                         zone + "\t" + server + "\t-1",
                         "-1\t-1\t" + platform,
                         "-1\t-1\t-1" };
    }

    public String getAPid() {
        String account = getValue(ACCOUNT);
        String role = keyValueSet.get(ROLE);
        if(role != null) {
            return account + "-" + role;
        }
        return account + "--1";
    }

    public static void main(String args[]) {
        String[] log = new String[] {
//            "_hip=10.1.1.63\t_stid_=active\t_sstid_=active\t_gid_=seer\t_pid_=taomee\t_zid_=0\t_sid_=1\t_ts_=1383117270\t_acid_=185908545",
//            "_hip=10.1.1.63\t_stid_=active\t_sstid_=active\t_gid_=seer\t_pid_=taomee\t_zid_=0\t_sid_=1\t_ts_=1383117270\t_acid_=185908545\t_plid_=1383117270",
            "_hip=10.1.1.63\t_stid_=active\t_sstid_=active\t_gid_=seer\t_pid_=taomee\t_zid_=0\t_sid_=1\t_ts_=1383117270\t_acid_=185908545\tcoins=10\t_op_=sum:coins",
            "_hip=10.1.1.63\t_stid_=active\t_sstid_=active\t_gid_=seer\t_pid_=taomee\t_zid_=0\t_sid_=1\t_ts_=1383117270\t_acid_=185908545\tproduct=1\tcoins=10\t_plid_=1383117270\t_op_=sum:coins|item:product|item_sum:product,coins",
        };
        LogAnalyser l = new LogAnalyser();
        ReturnCode rCode = ReturnCode.get();
        for(int i=0; i<log.length; i++) {
            l.analysis(log[i]);
            System.out.println("error:" + rCode.getErrorList());
            System.out.println("warn:" + rCode.getWarnList());
            ArrayList<String[]> o = l.getOutput();
            System.out.println(log[i]);
            for(int j=0; j<o.size(); j++) {
                System.out.println(o.get(j)[0] + "\t" + o.get(j)[1] + "\t" + o.get(j)[2] + "\t" + o.get(j)[3]);
            }
        }
    }

}
