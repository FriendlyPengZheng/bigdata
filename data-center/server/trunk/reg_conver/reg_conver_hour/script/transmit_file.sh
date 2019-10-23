SSHPASS="./sshpass"
PASSWORD="STat(room)712"
PORT="56000"
HOST_WEIXIN="stat@192.168.71.59"
FILE1="/opt/taomee/data-analysis/system/Model-reg-funnel/reg_hour_warn_weixin_bigdata.html"
FILE2="/opt/taomee/data-analysis/system/Model-reg-funnel/reg_hour_warn_weixin_release.html"

#拷贝html文件到微信服务器
if [ -s $FILE1 ];then
    ${SSHPASS} -p${PASSWORD} scp -P ${PORT} ${FILE1} ${HOST_WEIXIN}:/opt/taomee/stat/weixin/
    ${SSHPASS} -p${PASSWORD} ssh -p ${PORT} ${HOST_WEIXIN} "php /opt/taomee/stat/weixin/weixinSend_Reg_Hour_Warn.php"
fi
if [ -s $FILE2 ];then
    ${SSHPASS} -p${PASSWORD} scp -P ${PORT} ${FILE2} ${HOST_WEIXIN}:/opt/taomee/stat/weixin/
    ${SSHPASS} -p${PASSWORD} ssh -p ${PORT} ${HOST_WEIXIN} "php /opt/taomee/stat/weixin/weixinSend_Reg_Hour_Warn_Release.php"
fi
