package com.taomee.tms.mgr.tools.weixin;

import java.io.BufferedReader;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;  
import java.net.HttpURLConnection;  
import java.net.URL;  

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
   
  
public class WXUpload {  
    private static final String upload_wechat_url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";  
  
    public static JSONObject upload(String accessToken, String type, File file) {  
        JSONObject jsonObject = null;  
        String last_wechat_url = upload_wechat_url.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);  
        // 定义数据分割符  
        String boundary = "----------sunlight";  
        try {  
            URL uploadUrl = new URL(last_wechat_url);  
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();  
            uploadConn.setDoOutput(true);  
            uploadConn.setDoInput(true);  
            uploadConn.setRequestMethod("POST");  
            // 设置请求头Content-Type  
            uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);  
            // 获取媒体文件上传的输出流（往微信服务器写数据）  
            OutputStream outputStream = uploadConn.getOutputStream();  
  
            // 从请求头中获取内容类型  
            String contentType = "Content-Type: " + getContentType();  
            // 请求体开始  
            outputStream.write(("--" + boundary + "\r\n").getBytes());  
            outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", file.getName()).getBytes());  
            outputStream.write(String.format("Content-Type: %s\r\n\r\n", contentType).getBytes());  
  
            // 获取媒体文件的输入流（读取文件）  
            DataInputStream in = new DataInputStream(new FileInputStream(file));  
            byte[] buf = new byte[1024 * 8];  
            int size = 0;  
            while ((size = in.read(buf)) != -1) {  
                // 将媒体文件写到输出流（往微信服务器写数据）  
                outputStream.write(buf, 0, size);  
            }  
            // 请求体结束  
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());  
            outputStream.close();  
            in.close();  
  
            // 获取媒体文件上传的输入流（从微信服务器读数据）  
            InputStream inputStream = uploadConn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
            StringBuffer buffer = new StringBuffer();  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            bufferedReader.close();  
            inputStreamReader.close();  
            // 释放资源  
            inputStream.close();  
            inputStream = null;  
            uploadConn.disconnect();  
            // 使用json解析  
            jsonObject = JSON.parseObject(buffer.toString());  
            System.out.println(jsonObject);  
        } catch (Exception e) {  
            System.out.println("上传文件失败！");  
            e.printStackTrace();  
        }  
        return jsonObject;  
    }  
  
    // 获取文件的上传类型，图片格式为image/png,image/jpeg等。非图片为application /octet-stream  
    private static String getContentType() throws Exception {   
    	return "image/jpeg";
    } 
    
    public static void main( String[ ] args ) {
    	String token = "D8zuslctYZEPs3sYWBDY4Wdgo0cslXJD5K8srK7BsQXdnR8AnBd-sfBxdg85QoV3xnYKJ9xYm2NF-UZbktmtXdVtxaA5lBwG9wP40tzszqDSDHO7Hc6iLoCnjgA3bQgfDlmxWVEajJY-3Vo35aCKodReHm5jxYBa6zU7UZgvsjQ9h9JRaBY-l9z1CiOIPQR2-LLmzZMKdFPeYcH5lrnuQLA_WeUOEQh_bsk7Curmu6g";
    	JSONObject result = WXUpload.upload(token, "image", new File("weixin/105"));
    	System.out.println(result.toJSONString());
    }
      
  
}  
