<?php
class backend_Data extends TMModel
{   
    //相应服务类型
    public $moduleType;

    public $ip;

    public $minutes;

    public function rules()
    {
        return array(
            array('moduleType', 'number', 'integerOnly' => true, 'min' => 0, 'max' => 8),
            array('ip', 'regex', 'pattern' => '/^((25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)\.){3}(25[0-5]|2[0-4]\d|1\d{2}|[1-9]?\d)$/', 'allowEmpty' => false, 'message' => '输入Ip格式错误'),
            array('minutes','number','integerOnly' =>true, 'min' => 1,'max'=> 1440,'regex','pattern' => '/^[0-9]*[1-9][0-9]*$/','allowEmpty' => false, 'message' => '输入时间格式错误,必须为正整数,且不超过一天')
        );
    }

    /**
     * @brief getTableList
     * 获取后台iModuleType类型数据列表
     * @param {interval} $iModuleType
     *
     * @return {array}
     */
    public function getTableList($iModuleType)
    {
        $this->moduleType = $iModuleType;
        $this->validate(array('moduleType'));

        TMProto::register();
        $socket = TM::app()->backendSocket->connect();
        $header = new backend_proto_GetHeader();
        $header->setProtoId(0xA010)->setModuleType($iModuleType)->setPrintFlag(0);
        $head = $header->serialize();

        $socket->write(pack('L', 4 + strlen($head)) . $head);
        list(, $length) = unpack('L', $socket->read(4));
        $responseHeader = new backend_proto_ResponseHeader();
        $responseHeader->parse($socket->read(5));
        if (($code = $responseHeader->getCode()) !== 0) {
            throw new TMException(TM::t('tongji', '获取数据错误，错误码{code}', array('{code}' => $code)));
        }

        $ClassName = backend_DataFactory::getDataClass($iModuleType);
        $sReceivedData = new $ClassName();
        $sReceivedData->parse($socket->read($length - 9));
        $aReceivedData = $sReceivedData->serialize(new TMProtoCodecArray());
        foreach ($aReceivedData['info'] as &$info) {
            foreach($info as  $key =>&$data){
                if(substr($key, -4) === 'Size')    $data = $this->transformSize2Mb($data);  
                if(substr($key, -7) === 'Percent') $data = round($data, 2).'%';
            }
            $info['ip'] = $this->transformLong2Ip($info['ip']);
            $info['lastHeartBeatTime'] = date('Y-m-d H:i:s', $info['lastHeartBeatTime']);
        }
        return $aReceivedData;
    }


    /**
     * @brief deleteMonitor
     * 删除监控
     * @param {interval} $iModuleType
     * @param {string} $sMonitorIp
     *
     * @return {boolean}
     */
    public function deleteMonitor($iModuleType, $sMonitorIp)
    {
        $this->moduleType = $iModuleType;
        $this->ip = $sMonitorIp;
        $this->validate(array('moduleType', 'ip'));

        TMProto::register();
        $socket = TM::app()->backendSocket->connect();

        $header = new backend_proto_DeleteMonitorHeader();
        $iMonitorIp = $this->transformIp2Long($sMonitorIp);
        $head = $header->setProtoId(0xA002)->setModuleType($iModuleType)->setIp($iMonitorIp)->serialize();
        $socket->write(pack('L', 4 + strlen($head)) . $head);

        $socket->read(4);

        $responseHeader = new backend_proto_ResponseHeader();
        $responseHeader->parse($socket->read(5));
        if (($code = $responseHeader->getCode()) !== 0) {
            throw new TMException(TM::t('tongji', '获取数据错误，错误码{code}', array('{code}' => $code)));
        }
        return true;
    }

    /**
     * @brief forbiddenAlarm 
     * 禁止告警
     * @param {interval} $iModuleType
     * @param {string}   $sExtraParamIp
     * @param {interval} $iExtraParamMin
     * @param {interval} $iForbidFlag
     *
     * @return {boolean}
     */
    public function forbiddenAlarm($iModuleType,$iForbidFlag,$sExtraParamIp=null,$iExtraParamMin=null)
    {   
        $this->moduleType = $iModuleType;
        $this->minutes = $iExtraParamMin;
        $this->ip = $sExtraParamIp;

        $this->validate(array('moduleType'));

        TMProto::register();
        $socket = TM::app()->backendSocket->connect();

        $header = new  backend_proto_forbidAlarmHeader();
        $header->setProtoId(0xA011)->setModuleType($iModuleType)->setFbdFlag($iForbidFlag);
        if($iForbidFlag & 0x80){   
            $this->validate(array('minutes'));
            $header->setMinutes($iExtraParamMin);
        }
        if(!($iForbidFlag & 0x40 )) {   
            $this->validate(array('ip'));
            $iExtraParamIp = $this->transformIp2Long($sExtraParamIp);
            $header->setIp($iExtraParamIp);
        }

        $head = $header->serialize();
        $socket->write(pack('L', 4 + strlen($head)) . $head);
        $socket->read(4);
        $responseHeader = new backend_proto_ResponseHeader();
        $responseHeader->parse($socket->read(5));
        if (($code = $responseHeader->getCode()) !== 0) {
            throw new TMException(TM::t('tongji', '获取数据错误，错误码{code}', array('{code}' => $code)));
        }
        return true;
    }

    /**
     * @brief transformIp2Long 
     * 将ip格式转化为long型(小端到大端)
     * @param {string} $ip
     *
     * @return {uint}
     */
    public function  transformIp2Long($ip)
    {
        $ip = ip2long($ip);
        list(, $ip) = unpack('N', pack('V', $ip));
        return $ip;
    }

    /**
     * @brief transformLong2Ip 
     * 将long型格式转化为ip格式(大端到小端)
     * @param {long} $ip
     *
     * @return {string}
     */
    public function transformLong2Ip($ip)
    {
        list(, $ip) = unpack('N', pack('V', $ip));
        $ip = long2ip($ip);
        return $ip;
    }

    /**
     * @brief  transformSize2Mb 
     * 将byte转化成相应单位字节
     * @param {interval} $size (后台数据大小:单位byte)
     * @param {interval} $digits (要保留的位数)
     *
     * @return {interval} (单位转化后的数据)
     */
    public function transformSize2Mb($size,$digits=2)      
    {   
        //单位数组，是必须1024进制依次的。
        $unit = array('','K','M','G','T','P');  
        //对数的基数
        $base = 1024;
        //字节数对1024取对数，值向下取整
        if($size){
            $i = floor(log($size, $base));
            if($i){
                return round($size/pow($base, $i), $digits).' '.$unit[$i] . 'B';
            }
        }
        return $size.'B';
    }

}
