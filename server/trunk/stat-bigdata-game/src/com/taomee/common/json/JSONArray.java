package com.taomee.common.json;

import java.util.LinkedHashMap;
import java.util.Iterator;

public class JSONArray
{
    private static final int JSON_EMPTY  = 0;
    private static final int JSON_STRING = 1;
    private static final int JSON_NUMBER = 2;
    private static final int JSON_ARRAY  = 3;

    private String key = null;
    private String value = null;
    private LinkedHashMap<String, JSONArray> values = new LinkedHashMap<String, JSONArray>();
    private int type = JSON_EMPTY;
    private int level = -1;

    public JSONArray() { }

    public JSONArray(String k, String v) {
        key = k;
        value = v;
        type = JSON_STRING;
    }

    public JSONArray(String k, LinkedHashMap<String, JSONArray> v) {
        setKey(k);
        setValue(v);
    }

    private void setKey(String k) {
        key = k;
    }

    public void setValue(String v) {
        value = v;
        type = JSON_STRING;
    }

    public void setValue(Long v) {
        value = String.valueOf(v);
        type = JSON_NUMBER;
    }

    public void setValue(Integer v) {
        value = String.valueOf(v);
        type = JSON_NUMBER;
    }

    public void setValue(Double v) {
        value = String.valueOf(v);
        type = JSON_NUMBER;
    }

    public void setValue(Float v) {
        value = String.valueOf(v);
        type = JSON_NUMBER;
    }

    public void setValue(LinkedHashMap<String, JSONArray> v) {
        values = v;
        type = JSON_ARRAY;
    }

    private void setLevel(int l) {
        level = l;
    }

    public JSONArray get(String k) {
        if(type == JSON_ARRAY) {
            if(values.containsKey(k)) {
                return values.get(k);
            }
        }
        return null;
    }

    public JSONArray get(int i) {
        return get(String.valueOf(i));
    }

    public String get() {
        if(type == JSON_STRING || type == JSON_NUMBER) {
            return value;
        }
        return null;
    }

    public long getLong() throws NumberFormatException {
        if(type == JSON_STRING || type == JSON_NUMBER) {
            return Long.valueOf(value);
        }
        throw new NumberFormatException("Number format error from array");
    }

    public int getInt() throws NumberFormatException {
        if(type == JSON_STRING || type == JSON_NUMBER) {
            return Integer.valueOf(value);
        }
        throw new NumberFormatException("Number format error from array");
    }

    public double getDouble() throws NumberFormatException {
        if(type == JSON_STRING || type == JSON_NUMBER) {
            return Double.valueOf(value);
        }
        throw new NumberFormatException("Number format error from array");
    }

    public float getFloat() throws NumberFormatException {
        if(type == JSON_STRING || type == JSON_NUMBER) {
            return Float.valueOf(value);
        }
        throw new NumberFormatException("Number format error from array");
    }

    public String toString() {
        if(type == JSON_EMPTY)  return "";
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<level; i++)  buffer.append('\t');
        if(type == JSON_ARRAY) {
            if(key != null) buffer.append(key + "\t=>\n");
            Iterator<String>it = values.keySet().iterator();
            JSONArray v;
            while(it.hasNext()) {
                v = values.get(it.next());
                v.setLevel(level+1);
                buffer.append(v);
                v.setLevel(0);
            }
        } else {
            buffer.append(key + "\t=>\t" + value + '\n');
        }
        return buffer.toString();
    }

}
