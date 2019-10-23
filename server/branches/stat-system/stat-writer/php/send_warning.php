<?php
	require_once('./Rmail/Rmail.php');

	// 获取昨天的日期
	$yesterday = date('Y-m-d', time() - 24 * 60 * 60); 
	$mail_host = "mail.shidc.taomee.com";
	$mail_port = 25;
	$mail_user = "";
	$mail_passwd = "";
	$mail_from = "andy@taomee.com";
	$mail_cc   = "";
	$mail_title  = "未定义的EventType（".$yesterday."）";
	$mail_body = "";
	$mail_to   = "andy@taomee.com, henry@taomee.com, ianguo@taomee.com, ping@taomee.com";

	$file_handle = fopen("./undefined_event_type", "r");
	while (!feof($file_handle)) {
		$mail_body = $mail_body.fgets($file_handle)."<br/>";
	}
	fclose($file_handle);

	$mail = new Rmail();
	$mail->setTextCharset('UTF-8');
	$mail->setHTMLCharset('UTF-8');
	$mail->setHeadCharset('UTF-8');
	$mail->setSMTPParams($mail_host, $mail_port, 'helo', false, $mail_user, $mail_passwd);
	$mail->setFrom($mail_from);
	$mail->setCc($mail_cc);
	$mail->setSubject($mail_title);
	$mail->setHTML($mail_body);

	$mail->send(array($mail_to), 'smtp');
?>
