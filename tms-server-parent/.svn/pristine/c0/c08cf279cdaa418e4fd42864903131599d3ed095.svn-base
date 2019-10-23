package com.taomee.tms.mgr.tools.weixin;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.tools.HttpRequest;
//import com.taomee.tms.mgr.tools.HttpRequest2;

public class WeiXinTools {
	private static final String gettoken_wechat_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
	private static final String gettoken_wechat_param = "corpid=wx254b68671eaffeec&corpsecret=ruiRtUQ2-IK0BExiK1sSzesH2AHymKRXgiw7ED1ZamKI-tbnkMBoN7YxEsqybqRL";
	private static final String upload_wechat_url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	private static final String upload_wechat_content_type = "image/jpeg";
	private static final String send_wechat_url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
	
	public String getToken() {
		String result = HttpRequest.sendGet(gettoken_wechat_url, gettoken_wechat_param);
		JSONObject jRes = JSON.parseObject(result);
		if (!jRes.getString("errmsg").equals("ok")) {
			System.out.println("get token failed:" + result);
			return null;
		}
		return jRes.getString("access_token");
	}
	
	public String getMediaId() {
		return null;
	}
	
	public String sendWeixin(String title,String message, String mediaId, String totag) throws UnsupportedEncodingException {
		String token = getToken();
		if(token == null || token.isEmpty()) {
			return null;
		}
		System.out.println("token:" + token);
		
		String url = send_wechat_url.replace("ACCESS_TOKEN", token);
		String content = "<p>测试！测试！测试！test！test！</p>";
		JSONObject artical = new JSONObject();
		artical.put("title", title);
		artical.put("thumb_media_id", mediaId);
		//artical.put("thumb_media_id", mediaId);
		artical.put("author","淘米游戏数据平台部");
		artical.put("content_source_url", "v.61.com");
		artical.put("content",message);
		artical.put("show_cover_pic", "0");
		
		JSONArray articles = new JSONArray();
		articles.add(artical);
		
		JSONObject mpnews = new JSONObject();
		mpnews.put("articles", articles);
		
		JSONObject data = new JSONObject();
		//data.put("access_token", token);
		data.put("totag", totag);
		data.put("msgtype", "mpnews");
		data.put("agentid", "0");
		data.put("safe", "1");
		data.put("mpnews", mpnews);
		
		String result = HttpRequest.sendPost(url,data);
		
		return result;
	}
	
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
            String contentType = "Content-Type: " + upload_wechat_content_type;  
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
	
	
	public static void main( String[ ] args ) {
		try {
			WeiXinTools tool = new WeiXinTools();
			System.out.println(tool.sendWeixin("测试",
					"<p>测试！测试！测试！test！test！</p>",
					"3LQ1Upj8vZWC-djviPGICP3b-y0FDVYC6MsCmNRuZnOyUXh7Bj0mH90LM-kx3gjjw",
					"26"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
