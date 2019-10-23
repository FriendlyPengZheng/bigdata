package com.taomee.bigdata.monitor.util;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.StringBuilder;

public class HttpClient
{
    public static String readContentFromGet(String url) throws IOException { 
        URL getUrl = new URL(url); 
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection(); 

        connection.connect(); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
        StringBuilder buffer = new StringBuilder();
        String lines; 
        while ((lines = reader.readLine()) != null) { 
            buffer.append(lines);
            buffer.append("\n");
        } 
        reader.close(); 
        connection.disconnect(); 
        return buffer.toString();
    } 
}
