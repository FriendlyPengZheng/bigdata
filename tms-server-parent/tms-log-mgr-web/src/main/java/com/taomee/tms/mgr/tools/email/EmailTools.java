package com.taomee.tms.mgr.tools.email;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
//import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class EmailTools {
	public static final String DEFAULT_FROM = "TM-datacenter@taomee.com";
	public static final String DEFAULT_HOST = "mail.shidc.taomee.com";
	
	private List<String> toList;
	private List<String> ccList;
	private String subject;
	
	private String from;
	private String host;
	
	private Map<String, String> messagePicMap;
	private String messageBody;
	private File attachment;
	
	public EmailTools(){
		setFrom(DEFAULT_FROM);
		host = DEFAULT_HOST;
		toList = new ArrayList<String>();
		ccList = new ArrayList<String>();
		messagePicMap = new HashMap<String, String>();
	}
	
	public Boolean checkEmailAddress(String emailAddress) {
		if(emailAddress == null || emailAddress.isEmpty()) {
			return false;
		}
		
		String[] apart = emailAddress.split("@");
		if(apart.length != 2) {
			return false;
		}
		
		return true;
	}
	
	public void sendEmail() {
		if(!checkALL()) {
			return;
		}
		
		// 获取系统属性
		Properties properties = System.getProperties();
		// 设置邮件服务器
		properties.setProperty("mail.smtp.host", this.host);
		// 获取默认session对象
		Session session = Session.getDefaultInstance(properties);
		//session.setDebug(true);
        //Transport transport = session.getTransport();
		// 创建默认的 MimeMessage 对象
		MimeMessage message = new MimeMessage(session);
        try {
        	// Set From: 头部头字段
			message.setFrom(new InternetAddress(from));
			// Set To: 头部头字段
			InternetAddress[] sendTo = new InternetAddress[toList.size()];
			for (int i = 0; i < sendTo.length; i++) {  
	             System.out.println("发送到:" + toList.get(i));  
	             sendTo[i] = new InternetAddress(toList.get(i));  
	         }
			
			message.setRecipients(Message.RecipientType.TO, sendTo);
			
			//设置抄送
			if(!ccList.isEmpty()) {
				InternetAddress[] ccAdresses = new InternetAddress[ccList.size()];
				for (int i=0; i<ccAdresses.length; i++){
	            	ccAdresses[i] = new InternetAddress(ccList.get(i));
	            }
	        	message.setRecipients(Message.RecipientType.CC, ccAdresses);
			}
			
			//设置主题
			message.setSubject(subject);
		    
		    message.setContent(this.getMessage());
		    
		    //transport.connect();
		    Transport.send(message);
		    System.out.println("Send email success!");
		    
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private MimeMultipart getMessage() {
		MimeMultipart multipart = new MimeMultipart("related");
		try {
			//头部信息
			if(this.messageBody != null && !this.messageBody.isEmpty()) {
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(this.messageBody, "text/html;charset=\"utf8\"");
				multipart.addBodyPart(messageBodyPart);
			}
			
			//加载图片
			if(this.messagePicMap != null && this.messagePicMap.size() >0) {
				Iterator<Map.Entry<String, String>> iter = this.messagePicMap.entrySet().iterator();
				while(iter.hasNext()) {
					BodyPart messagePicPart = new MimeBodyPart();
					Map.Entry<String, String> entry = iter.next();
					System.out.println("key:" + entry.getKey() + " path:" + entry.getValue());
					DataSource fds = new FileDataSource(entry.getValue());
					messagePicPart.setDataHandler(new DataHandler(fds));
					messagePicPart.setHeader("Content-ID","<" +entry.getKey()+">");
					multipart.addBodyPart(messagePicPart);
				}
			}
			
			//添加附件
			if(this.attachment != null) {
				BodyPart attachmentBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachment);
				attachmentBodyPart.setDataHandler(new DataHandler(source));
				//MimeUtility.encodeWord可以避免文件名乱码
                try {
					attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                multipart.addBodyPart(attachmentBodyPart);
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return multipart;
	}
	
	private Boolean checkALL() {
		if(subject==null || subject.isEmpty()) {
			System.out.println("Subject is empty!");
			return false;
		}
		
		if(toList.isEmpty()) {
			System.out.println("To Address is empty!");
			return false;
		}
		
		return true;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public List<String> getToList() {
		return toList;
	}

	public void setToList(List<String> toList) {
		List<String> result = new ArrayList<String>();
		for(String to:toList) {
			if(this.checkEmailAddress(to)) {
				result.add(to);
			}
		}
		this.toList = result;
	}

	public List<String> getCcList() {
		return ccList;
	}

	public void setCcList(List<String> ccList) {
		List<String> result = new ArrayList<String>();
		for(String to:ccList) {
			if(this.checkEmailAddress(to)) {
				result.add(to);
			}
		}
		this.ccList = result;
	}

	public Map<String, String> getMessagePicMap() {
		return messagePicMap;
	}

	public void setMessagePicMap(Map<String, String> messagePicMap) {
		this.messagePicMap = messagePicMap;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	public File  getAttachment() {
		return attachment;
	}

	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}
	
	
	public static void main(String [] args) {
		EmailTools emailTool = new EmailTools();
		String to = "maggie@taomee.com";
		String cc = "";
		
		List<String> tos =  Arrays.asList(to.split(";"));
		
		emailTool.setToList(tos);
		//emailTool.createEmail();
		emailTool.setSubject("This is the Subject Line!");
		emailTool.setMessageBody("<H1>Hello111</H1><img src=\"cid:image1\"><img src=\"cid:image2\">");
		//emailTool.setMessageBody("<H1>Hello111</H1><img src=\"cid:image1\">");
		Map<String, String> map = new HashMap<String, String>();
		map.put("image1", "C:\\Users\\maggie\\Pictures\\test\\8751_pic.jpeg");
		map.put("image2", "C:\\Users\\maggie\\Pictures\\test\\8752_pic.jpeg");
		emailTool.setMessagePicMap(map);
		emailTool.sendEmail();
	}

}
