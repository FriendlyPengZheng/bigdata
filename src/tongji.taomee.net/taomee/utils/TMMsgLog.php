<?php
/**
* @file TMMsgLog.php
* @brief 新版统计系统
* @author violet violet@taomee.com
* @version 1.0
* @date 2015-06-10
*/
class TMMsgLog extends TMComponent
{
    /**
     * @var {integer} 游戏ID
     */
    public $gameId;

    /**
     * @var {integer} 缓存的游戏ID
     */
    private $_restoreGameId;

    /**
     * @var {integer} 平台ID
     */
    private $_platformId = -1;

    /**
     * @var {integer} 区ID
     */
    private $_zoneId = -1;

    /**
     * @var {integer} 服ID
     */
    private $_serverId = -1;

    /**
     * @var {integer} 帐号
     */
    private $_accountId = -1;

    /**
     * @var {integer} 角色
     */
    private $_playerId = -1;

    /**
     * @var {string} 统计项一级目录
     */
    private $_stid;

    /**
     * @var {string} 统计项二级目录
     */
    private $_sstid;

    /**
     * @var {array} 统计项子目录
     */
    private $_items = array();

    /**
     * @var {array} 统计项操作
     */
    private $_ops = array();

    /**
     * @var {array} 统计项操作类型
     */
    private $_opKeys = array(
        '', /* op_begin */
        'sum', 'max', 'set', 'ucount',
        'item', 'item_sum', 'item_max', 'item_set',
        'sum_distr', 'max_distr', 'min_distr',
        'set_distr',
        'ip_distr'
        /* op_end */
    );

    /**
     * @var {string} 日志目录
     */
    private $_logPath = '/opt/taomee/stat/data/inbox';

    public function init()
    {
        $this->checkLogPath();
    }

    /**
     * @brief setLogPath 
     * 设置统计日志
     *
     * @param {string} $path
     *
     * @return {TMMsgLog}
     */
    public function setLogPath($path)
    {
        $this->_logPath = $path;
        $this->checkLogPath();
        return $this;
    }

    /**
     * 检测统计日志路径
     */
    protected function checkLogPath()
    {
        if (!file_exists($this->_logPath)) {
            if (false === TMFileHelper::mkdir($this->_logPath)) throw new TMException('统计日志路径不可写入！');
        }
        if (!is_writable($this->_logPath)) {
            throw new TMException('统计日志路径不可写入！');
        }
    }

    /**
     * @brief setGameId 
     * 设置游戏ID
     *
     * @param {ingeter} $gameId
     *
     * @return {TMMsgLog}
     */
    public function setGameId($gameId)
    {
        if ($this->gameId != $gameId) {
            $this->_restoreGameId = $this->gameId ? $this->gameId : (int)$gameId;
            $this->gameId = (int)$gameId;
        }
        return $this;
    }

    /**
     * 重置游戏ID
     *
     * @return {TMMsgLog}
     */
    public function resetGameId()
    {
        if ($this->_restoreGameId) $this->gameId = $this->_restoreGameId;
        return $this;
    }

    /**
     * @brief setPlatformId 
     * 设置平台ID
     *
     * @param {ingeter} $platformId
     *
     * @return {TMMsgLog}
     */
    public function setPlatformId($platformId)
    {
        $this->_platformId = (int)$platformId;
        return $this;
    }

    /**
     * @brief setZoneId 
     * 设置区ID
     *
     * @param {ingeter} $zoneId
     *
     * @return {TMMsgLog}
     */
    public function setZoneId($zoneId)
    {
        $this->_zoneId = (int)$zoneId;
        return $this;
    }

    /**
     * @brief setServerId 
     * 设置服ID
     *
     * @param {ingeter} $serverId
     *
     * @return {TMMsgLog}
     */
    public function setServerId($serverId)
    {
        $this->_serverId = (int)$serverId;
        return $this;
    }

    /**
     * @brief setAccountId 
     * 设置帐号ID
     *
     * @param {ingeter} $accountId
     *
     * @return {TMMsgLog}
     */
    public function setAccountId($accountId)
    {
        $this->_accountId = (int)$accountId;
        return $this;
    }

    /**
     * @brief setPlayerId 
     * 设置角色ID
     *
     * @param {ingeter} $playerId
     *
     * @return {TMMsgLog}
     */
    public function setPlayerId($playerId)
    {
        $this->_playerId = (int)$playerId;
        return $this;
    }

    /**
     * @brief setStid 
     * 设置stid
     *
     * @param {string} $stid
     *
     * @return {TMMsgLog}
     */
    public function setStid($stid)
    {
        $this->_stid = $stid;
        return $this;
    }

    /**
     * @brief setSstid 
     * 设置sstid
     *
     * @param {string} $sstid
     *
     * @return {TMMsgLog}
     */
    public function setSstid($sstid)
    {
        $this->_sstid = $sstid;
        return $this;
    }

    /**
     * @brief addItem 
     * 添加统计项
     *
     * @param {string} $key
     * @param {string} $value
     *
     * @return {TMMsgLog}
     */
    public function addItem($key, $value)
    {
        $this->_items[$key] = $value;
        return $this;
    }

    /**
     * @brief addOp 
     * 添加操作
     *
     * @param {integer} $op 操作符
     * @param {string} $key1 操作KEY
     * @param {string} $key2 操作KEY
     *
     * @return {TMMsgLog}
     */
    public function addOp($op, $key1, $key2 = '')
    {
        if (!($this->isValidOp($op) && array_key_exists($key1, $this->_items))) return;
        switch($op) {
            case TMMsgLog_OpCode::OP_ITEM_SUM:
            case TMMsgLog_OpCode::OP_ITEM_MAX:
            case TMMsgLog_OpCode::OP_ITEM_SET:
                if (!array_key_exists($key2, $this->_items))   return;
                $key1 = $key1.','.$key2;
                break;
            default:
                break;
        }
        $this->_ops[] = $this->_opKeys[$op] . ':' . $key1;
        return $this;
    }

    /**
     * @brief isValidOp 
     * 判断操作是否可用
     */
    protected function isValidOp($op)
    {
        return TMMsgLog_OpCode::OP_BEGIN < $op && TMMsgLog_OpCode::OP_END > $op;
    }

    /**
     * @brief _joinItems 
     * 序列化时回调
     */
    private function _joinItems($v, $k)
    {
        return $k . '=' . $v;
    }

    /**
     * @brief serialize 
     * 序列化日志内容
     */
    protected function serialize()
    {
        $message = implode("\t", array_map(array($this, '_joinItems'), $this->_items, array_keys($this->_items)));
        $items = array(
            '_hip_'   => $this->getLocalIp(),
            '_stid_'  => $this->_stid,
            '_sstid_' => $this->_sstid,
            '_gid_'   => $this->gameId,
            '_zid_'   => $this->_zoneId,
            '_sid_'   => $this->_serverId,
            '_pid_'   => $this->_platformId,
            '_ts_'    => time(),
            '_acid_'  => $this->_accountId,
            '_plid_'  => $this->_playerId
        );
        if (!empty($this->_ops)) $items['_op_'] = implode('|', $this->_ops);
        return implode("\t", array_map(array($this, '_joinItems'), $items, array_keys($items))) . "\t" . $message . "\n";
    }

    /**
     * @brief getLocalIp 
     * 获取当前服务器IP
     */
    protected function getLocalIp()
    {
        return isset($_SERVER['SERVER_ADDR']) ? $_SERVER['SERVER_ADDR'] : '127.0.0.1';
    }

    /**
     * @brief clear 
     * 清除内容
     */
    protected function clear()
    {
        $this->_items = array();
        $this->_ops = array();
    }

    /**
     * @brief error 
     * 写错误日志
     */
    protected function error($message)
    {
        $file = $this->_logPath . DS . 'error.log';
        $fd = fopen($file, 'a');
        if (false === $fd) return;
        $ts = date('Y-m-d H:i:s', time());
        fwrite($fd, '[' . $ts . ']' . $message . "\n");
        fclose($fd);
    }

    /**
     * @brief write 
     * 日志写入
     */
    public function write()
    {
        umask(0000);
        //每20秒一个文件
        $curhc = (int)(floor(time() / 20) * 20); 
        $file = $this->_logPath . DS . $this->gameId . '_game_custom_' . $curhc;
        $fd = fopen($file, 'ab');
        if (false === $fd) {
            $this->error('[write_custom_log]open(): ' . $file);
            return;
        }
        chmod($file, 0777);
        $message = $this->serialize();
        if (false === fwrite($fd, $message)) {
            $this->error('[write_custom_log]write(): ' . $message);
        }
        $this->clear();
        fclose($fd);
    }

    /**
     * @brief basic 
     * 日志写入
     */
    public function basic()
    {
        umask(0000);
        //每20秒一个文件
        $curhc = (int)(floor(time() / 20) * 20); 
        $file = $this->_logPath . DS . $this->gameId . '_game_basic_' . $curhc;
        $fd = fopen($file, 'ab');
        if (false === $fd) {
            $this->error('[write_basic_log]open(): ' . $file);
            return;
        }
        chmod($file, 0777);
        $message = $this->serialize();
        if (false === fwrite($fd, $message)) {
            $this->error('[write_basic_log]write(): ' . $message);
        }
        $this->clear();
        fclose($fd);
    }
}

class TMMsgLog_OpCode 
{
    const OP_BEGIN = 0;

    const OP_SUM = 1;    // 把某个字段某时间段内所有值相加
    const OP_MAX = 2;    // 求某字段某时间段内最大值
    const OP_SET = 3;    //直接取某字段最新的数值
    const OP_UCOUNT = 4; //对某个字段一段时间的值做去重处理

    const OP_ITEM = 5;      // 求某个大类下的各个ITEM求人数人次
    const OP_ITEM_SUM = 6;  // 对各个ITEM的产出数量/售价等等求和
    const OP_ITEM_MAX = 7;  // 求出各个ITEM的产出数量/售价等等的最大值
    const OP_ITEM_SET = 8;  //求出每个ITEM的最新数值

    const OP_SUM_DISTR = 9; // 对每个人的某字段求和，然后求出前面的“和”在各个区间下的人数
    const OP_MAX_DISTR = 10;// 对每个人的某字段求最大值，然后求出前面的“最大值”在各个区间下的人数
    const OP_MIN_DISTR = 11;//对每个人的某字段求最小值，然后根据前面的最小值在各个区间下做人数分布
    const OP_SET_DISTR = 12;//取某个字段的最新值，做分布

    const OP_IP_DISTR = 13; // 根据IP字段求地区分布的人数人次

    const OP_END = 14;
}
