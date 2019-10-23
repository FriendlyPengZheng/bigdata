package com.taomee.common.json;

import com.taomee.common.json.JSONArray;
import java.util.LinkedHashMap;

public class JSONDecode
{
    public static JSONArray decode(String s) {
        s = s.trim().replaceAll("\n", "");
        int type;
        char c = s.charAt(0);
        if(c != '{' && c != '[') {
            return null;
        }
        if(c == '[') {
            type = 0;
        } else {
            type = -1;
        }
        JSONArray ret = new JSONArray();
        ret.setValue(decode(s.substring(1, s.length() - 1), type));
        return ret;
    }

    private static LinkedHashMap<String, JSONArray> decode(String s, int type) {
        String key, value;
        LinkedHashMap<String, JSONArray> ret = new LinkedHashMap<String, JSONArray>();
        s = s.trim();
        while(s.trim().length() != 0) {
            if(type < 0) {//key : value
                key = getKey(s);
                //s = s.substring(key.length(), s.length()).trim();
                s = s.substring(s.split(":")[0].length()+1, s.length()).trim();
            } else {
                key = String.valueOf(type ++);
            }
            value = getValue(s);
            char c = value.charAt(0);
            if(c == '[') {
                ret.put(key, new JSONArray(key, decode(value.substring(1, value.length()-1), 0)));
            } else if(c == '{') {
                ret.put(key, new JSONArray(key, decode(value.substring(1, value.length()-1), -1)));
            } else if(c == '"') {
                ret.put(key, new JSONArray(key, decodeString(value)));
            } else if(value.compareTo("true") == 0) {
                ret.put(key, new JSONArray(key, "true"));
            } else if(value.compareTo("false") == 0) {
                ret.put(key, new JSONArray(key, "false"));
            } else if(value.compareTo("null") == 0) {
                ret.put(key, new JSONArray(key, "null"));
            } else {
                ret.put(key, new JSONArray(key, decodeNumber(value)));
            }
            if(value.length() >= s.length())    break;
            s = s.substring(value.length()+1, s.length()).trim();
            if(s.startsWith(",")) {
                s = s.substring(1, s.length()).trim();
            }
        }
        return ret;
    }

    private static String getKey(String s) {
        return decodeString(s);
    }

    private static String getValue(String s) {
        int index = 1;
        char c = s.charAt(0);
        int p = 1;
        StringBuffer buffer = new StringBuffer();
        buffer.append(c);
        if(c == '"') {
            while(index < s.length()) {
                c = s.charAt(index);
                buffer.append(c);
                if(c != '"' || s.charAt(index - 1) == '\\') {
                    index ++;
                } else {
                    break;
                }
            }
        } else if(c == '[') {
            while(index < s.length()) {
                c = s.charAt(index);
                buffer.append(c);
                if(c == '[' && s.charAt(index - 1) != '\\') {
                    p ++;
                } else if(c == ']' && s.charAt(index - 1) != '\\') {
                    if(p != 1) {
                        p --;
                    } else {
                        break;
                    }
                }
                index ++;
            }
        } else if(c == '{') {
            while(index < s.length()) {
                c = s.charAt(index);
                buffer.append(c);
                if(c == '{' && s.charAt(index - 1) != '\\') {
                    p ++;
                } else if(c == '}' && s.charAt(index - 1) != '\\') {
                    if(p != 1) {
                        p --;
                    } else {
                        break;
                    }
                }
                index ++;
            }
        } else if('0' <= c && c <= '9') {
            while(index < s.length()) {
                c = s.charAt(index);
                index++;
                if('0' <= c && c <= '9' || c == '.') {
                    buffer.append(c);
                } else {
                    break;
                }
            }
        } else if(s.startsWith("true")) {
            return "true";
        } else if(s.startsWith("false")) {
            return "false";
        } else if(s.startsWith("null")) {
            return "null";
        } else {
            System.out.println("get value error : " + s);
            return null;
        }
        return buffer.toString();
    }

    private static String decodeString(String s) {
        StringBuffer buffer = new StringBuffer();
        int index = 1;
        char c;
        while(index < s.length()) {
            c = s.charAt(index);
            if(c != '"' || s.charAt(index - 1) == '\\') {
                buffer.append(c);
                index ++;
            } else {
                break;
            }
        }
        return buffer.toString();
    }

    private static String decodeNumber(String s) {
        return s;
        //StringBuffer buffer = new StringBuffer();
        //int index = 0;
        //char c;
        //do {
        //    c = s.charAt(index++);
        //    buffer.append(c);
        //} while('0' <= c && c <= '9' && index < s.length());
        //return buffer.toString();
    }

    //"{ \"beans\" : [ { \"name\" : \"Hadoop:service=DataNode,name=FSDatasetState-DS-838490701-192.168.11.74-50010-1381909922548\", \"modelerType\" : \"org.apache.hadoop.hdfs.server.datanode.FSDataset\", \"Remaining\" : 4788458246144, \"Capacity\" : 7796419584000, \"DfsUsed\" : 2646695970594, \"StorageInfo\" : \"FSDataset{dirpath='/opt/taomee/hadoop/hadoop-hdfs/data4/current,/opt/taomee/hadoop/hadoop-hdfs/data3/current,/opt/taomee/hadoop/hadoop-hdfs/data2/current,/opt/taomee/hadoop/hadoop-hdfs/data1/current'}\" } ] }"
    public static void main(String[] argv) {
        //JSONArray ret = JSONDecode.decode("{ \"beans\" : [ { \"name\" : \"Hadoop:service=DataNode,name=FSDatasetState-DS-838490701-192.168.11.74-50010-1381909922548\", \"modelerType\" : \"org.apache.hadoop.hdfs.server.datanode.FSDataset\", \"Remaining\" : 4788458246144, \"Capacity\" : 7796419584000, \"DfsUsed\" : 2646695970594, \"StorageInfo\" : \"FSDataset{dirpath='/opt/taomee/hadoop/hadoop-hdfs/data4/current,/opt/taomee/hadoop/hadoop-hdfs/data3/current,/opt/taomee/hadoop/hadoop-hdfs/data2/current,/opt/taomee/hadoop/hadoop-hdfs/data1/current'}\" } ] }");
        JSONArray ret = JSONDecode.decode("[ { \"name\" : \"Hadoop:service=DataNode,name=FSDatasetState-DS-838490701-192.168.11.74-50010-1381909922548\", \"modelerType\" : \"org.apache.hadoop.hdfs.server.datanode.FSDataset\", \"Remaining\" : 4788458246144, \"Capacity\" : 7796419584000, \"DfsUsed\" : 2646695970594, \"StorageInfo\" : \"FSDataset{dirpath='/opt/taomee/hadoop/hadoop-hdfs/data4/current,/opt/taomee/hadoop/hadoop-hdfs/data3/current,/opt/taomee/hadoop/hadoop-hdfs/data2/current,/opt/taomee/hadoop/hadoop-hdfs/data1/current'}\" }  , 339871, \"d3298rf\"]");
        System.out.println(ret);
        //System.out.println(ret.get("beans").get("0").get("Capacity").getLong());
    }
}
