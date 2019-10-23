<?php
class gamecustom_report_Base extends TMModel
{
    const CATEGORY_BASIC = 1;
    const CATEGORY_DISTR = 2;
    const CATEGORY_ITEM = 3;

    /**
     * @var array names for op_type
     */
    protected $aOpTypeNames = array(
        'ucount' => '人数',
        'count'  => '人次',
        'sum'    => '求和',
        'max'    => '最大值',
        'set'    => '人次'
    );

    /**
     * 实例化统计项配置
     * @param integer $iType
     * @return gamecustom_report_Base
     */
    public static function instance($iType = self::CATEGORY_BASIC)
    {
        switch ($iType) {
            case self::CATEGORY_DISTR:
                return new gamecustom_report_Distr();
                break;
            case self::CATEGORY_ITEM:
                return new gamecustom_report_Item();
                break;
            default:
                return new self();
                break;
        }
    }

    /**
     * Get configuration for report or result.
     * @param array $aInfo
     * @return array
     */
    public function getConfiguration($aInfo)
    {
        return array();
    }

    /**
     * Set configuration for report/result.
     * @param array $aInfo
     * @param array $aUserParam
     * @return boolean
     */
    public function setConfiguration($aInfo, $aUserParam)
    {
    }
}
