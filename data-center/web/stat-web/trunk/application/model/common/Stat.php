<?php
abstract class common_Stat extends TMFormModel
{
    /* 统计项类型 */
    const TYPE_REPORT = 1;
    const TYPE_RESULT = 2;

    /**
     * @var integer 计数
     */
    private static $_counter = 0;

    /**
     * 实例化统计项
     * @param integer $iType
     * @return common_Stat
     */
    public static function instance($iType = self::TYPE_REPORT)
    {
        switch ($iType) {
            case self::TYPE_RESULT:
                return new common_Result();
                break;
            default:
                return new common_Report();
                break;
        }
    }

    /**
     * 转换统计项类型
     * @param integer $iType
     * @return string
     */
    public static function type2string($iType = self::TYPE_REPORT)
    {
        switch ($iType) {
            case self::TYPE_RESULT:
                return 'result';
                break;

            default:
                return 'report';
                break;
        }
    }

    /**
     * 通过统计项配置获取信息，保证顺序
     * @param  integer $iGameId
     * @param  array  $aUniqueKey
     * @return array
     */
    public static function getStatByUk($iGameId, $aUniqueKey)
    {
        // 按类型分组
        $aChunks = array();
        foreach ($aUniqueKey as $key) {
            $aChunks[$key['type']][] = $key;
        }
        // 取出所有统计项
        $aStat = $aHandlers = array();
        foreach ($aChunks as $type => $chunk) {
            if (!isset($aHandlers[$type])) {
                $aHandlers[$type] = self::instance($type);
            }
            $aStat += $aHandlers[$type]->findByUk($iGameId, $chunk);
        }
        // 按传入顺序排序
        $aSorted = array();
        foreach ($aUniqueKey as $key) {
            $uniqueKey = $aHandlers[$key['type']]->getUk($key);
            if (isset($aStat[$uniqueKey])) {
                $aSorted[] = array_merge($key, $aStat[$uniqueKey][0]);
                continue;
            }
            self::$_counter += 1;
            $aSorted[] = array_merge($key, $aHandlers[$key['type']]->undefined(self::$_counter));
        }
        return $aSorted;
    }

    abstract public function findByUk($iGameId, $aUniqueKey);

    abstract public function getUk($aInfo);

    abstract public function undefined($suffix);
}
