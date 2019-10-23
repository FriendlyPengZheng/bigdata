package com.taomee.tms.mgr.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class HttpRequest {
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
    public static String sendPost(String urlstr, JSONObject obj) {
    	HttpURLConnection connection=null;  
    	String result = new String();
    	//PrintWriter out = null;
        try {  
             //创建连接  
             URL url = new URL(urlstr);  
             connection = (HttpURLConnection) url.openConnection();  
               
  
             //设置http连接属性  
               
             connection.setDoOutput(true);  
             connection.setDoInput(true);  
             connection.setRequestMethod("POST"); // 可以根据需要 提交 GET、POST、DELETE、INPUT等http提供的功能  
             connection.setUseCaches(false);  
             connection.setInstanceFollowRedirects(true);  
               
             //设置http头 消息  
             connection.setRequestProperty("Content-Type","application/json");  //设定 请求格式 json，也可以设定xml格式的  
             connection.setRequestProperty("Accept","application/json");//设定响应的信息的格式为 json，也可以设定xml格式的  
             connection.connect();
             OutputStream out = connection.getOutputStream();
             //out.write(obj.toString().getBytes());  
             out.write(obj.toString().getBytes("utf-8"));
             
             /*OutputStreamWriter outWriter = new OutputStreamWriter(connection.getOutputStream());
             PrintWriter out = new PrintWriter(outWriter);
             String output = "{\"safe\":\"1\",\"msgtype\":\"mpnews\",\"mpnews\":{\"articles\":[{\"content\":\"<p>测试！测试！测试！test！test！</p>\",\"author\":\"淘米游戏数据平台部\",\"title\":\"测试\",\"thumb_media_id\":\"3LQ1Upj8vZWC-djviPGICP3b-y0FDVYC6MsCmNRuZnOyUXh7Bj0mH90LM-kx3gjjw\",\"show_cover_pic\":\"0\",\"content_source_url\":\"v.61.com\"}]},\"totag\":\"26\",\"agentid\":\"0\"}";
             out.print(output);*/
             out.flush();  
             out.close();  
   
//            读取响应  
             BufferedReader reader = new BufferedReader(new InputStreamReader(  
                     connection.getInputStream()));  
             String lines;    
             while ((lines = reader.readLine()) != null) {  
                 lines = new String(lines.getBytes(), "utf-8");  
                 result += lines; 
             }   
             reader.close();  
////              断开连接  
             connection.disconnect();  
         } catch (Exception e) {  
             // TODO Auto-generated catch block 
        	 System.out.println("发送 POST 请求出现异常！"+e);  
             e.printStackTrace();  
         }
        return result;
    }
    
    
    public static void main( String[ ] args ) {
    	String url = "http://maggie222.taomee.net/tms-log-mgr-web/common/data/getTimeSeries";
    	String param = "game_id=632&period=1&contrast=0&by_item=0&from[0]=2018-4-2&to[0]=2018-4-2&from[1]=2018-4-1&to[1]=2018-4-1&server_id=1&expres[0][expre]={0}&expres[0][data_name]=PCU&data_info[0][data_id]=11813&data_info[0][period]=1";
    	
    	/*JSONObject message = new JSONObject();
    	message.put("game_id", 632);
    	message.put("period", 1);
    	message.put("contrast", 1);
    	message.put("by_item", 0);*/
    	
    	String s=HttpRequest.sendGet(url, param);
        System.out.println(s);

    }
}
