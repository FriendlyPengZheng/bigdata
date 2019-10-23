<?php
class common_WeixinData
{

    public function getWeitoken()
    {
        $url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wx254b68671eaffeec&corpsecret=ruiRtUQ2-IK0BExiK1sSzesH2AHymKRXgiw7ED1ZamKI-tbnkMBoN7YxEsqybqRL";
        $ch = curl_init();
        curl_setopt($ch,CURLOPT_SSL_VERIFYPEER,FALSE);
        curl_setopt($ch,CURLOPT_SSL_VERIFYHOST,FALSE);
        curl_setopt($ch,CURLOPT_RETURNTRANSFER,1);
        curl_setopt($ch,CURLOPT_URL,$url);
        $output = curl_exec($ch);
        $rr = json_decode($output,true);
        return json_decode($output,true)['access_token'];
    }

    public function sendWeixin($email,$shtml,$node_str,$is_test)
    {   
        $weitoken = $this->getWeitoken();
        $old_media = json_decode($email['weixin_media_id'],true);
        if((!$email['weixin_media_id']) || (!$old_media['id']) || (($old_media['createtime'] + 259200) <= time())){
            $mres = $this->getMediaId($email['email_id'],$weitoken);
            $t_mid = $mres['media_id'];
            $email['weixin_media_id'] = json_encode(array("id" => $t_mid, "createtime" => $mres['created_at']));
            (new common_EmailConfig())->updateMediaId($email['email_id'],$email['weixin_media_id']);
        }else{
            $t_mid = $old_media['id'];
        }
        $wei_rec = explode('-',$email['weixin_recev']);
        $wrec = $is_test ? $wei_rec[1] : $wei_rec[0];
        $url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=$weitoken";   
        $data = array(
            "totag"  => $wrec,
            "msgtype"=>"mpnews",
            "agentid"=>"0",
            "mpnews" =>array(
                "articles"=>array(
                    array(
                        "title"             => urlencode($email['subject'].'日报 '.date('Y-m-d', strtotime('yesterday'))),
                        "thumb_media_id"    => urlencode($t_mid),
                        "author"            => urlencode("淘米游戏数据平台部"),
                        "content_source_url"=> "v.61.com",
                        "content"           => urlencode($shtml),
                        "digest"            => urlencode($node_str),
                        "show_cover_pic"    => "0"
                    )
                )
            ),
            "safe"=>"1"
        );
        $headerArray = array(
            'Accept:application/json,text/javascript,*/*',
            'Content-Type:application/x-www-form-urlencoded',
            'charset=utf-8',
            'Referer:https://mp.weixin.qq.com'
        );
        $tts = urldecode(stripslashes(json_encode($data)));
        $ch =curl_init();
        curl_setopt($ch,CURLOPT_HTTPHEADER,$headerArray);
        curl_setopt($ch,CURLOPT_SSL_VERIFYPEER,FALSE);
        curl_setopt($ch,CURLOPT_SSL_VERIFYHOST,FALSE);
        curl_setopt($ch,CURLOPT_RETURNTRANSFER,1);
        curl_setopt($ch,CURLOPT_TIMEOUT,300);
        curl_setopt($ch,CURLOPT_POST,1);
        curl_setopt($ch,CURLOPT_URL,$url);
        curl_setopt($ch,CURLOPT_POSTFIELDS,$tts);
        $output = curl_exec($ch);
        return json_decode($output,true);
    }
    public function getMediaId($email_id,$access_token)
    {
        $path = $this->getPicPath($email_id);
        if(false != ($handle = opendir($path))){
            while(false != ($file = readdir($handle))){
                if($file != '.' && $file != '..'){
                    $dirArray[] = '@'.$path.$file;
                }
            }
            closedir($handle);
        }
        $url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=$access_token&type=image";
        $pic_f = array('media' => $dirArray[0]);
        $ch =curl_init();
        curl_setopt($ch,CURLOPT_SSL_VERIFYPEER,FALSE);
        curl_setopt($ch,CURLOPT_SSL_VERIFYHOST,FALSE);
        curl_setopt($ch,CURLOPT_RETURNTRANSFER,1);
        curl_setopt($ch,CURLOPT_TIMEOUT,300);
        curl_setopt($ch,CURLOPT_POST,1);
        curl_setopt($ch,CURLOPT_URL,$url);
        curl_setopt($ch,CURLOPT_POSTFIELDS,$pic_f);
        $output = curl_exec($ch);
        $res = json_decode($output,true);
        TMValidator::ensure($res['media_id'],TM::t('tongji','微信mediaID获取失败！请重试：）'));
        return $res;
    }

    public function getPicPath($email_id)
    {
        return TM::app()->getBasePath() . DIRECTORY_SEPARATOR . 'config' . DIRECTORY_SEPARATOR . 'weixin' . DIRECTORY_SEPARATOR . $email_id . DIRECTORY_SEPARATOR;
    }

}	
