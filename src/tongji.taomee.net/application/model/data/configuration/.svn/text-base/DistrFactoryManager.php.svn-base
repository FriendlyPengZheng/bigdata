<?php
class data_configuration_DistrFactoryManager extends TMComponent
{
    /* 分布类型 */
    const TYPE_PLATFORM = 1;
    const TYPE_ZONESERVER = 2;
    const TYPE_SSTID = 3;
    const TYPE_RANGE = 4;
    const TYPE_DATA = 5;

    /**
     * 根据类型创建分布生成器
     * @param integer $type
     * @return data_configuration_DistrFactory
     */
    public static function createDistrFactory($type = self::TYPE_SSTID)
    {
        switch ($type) {
            case self::TYPE_PLATFORM:
            case self::TYPE_ZONESERVER:
                return new data_configuration_GpzsDistrFactory($type);
                break;

            case self::TYPE_SSTID:
                return new data_configuration_SstidDistrFactory();
                break;

            case self::TYPE_RANGE:
                return new data_configuration_RangeDistrFactory();
                break;

            case self::TYPE_DATA:
            default:
                return new data_configuration_DataDistrFactory();
                break;
        }
    }

    /**
     * 是否是gpzs_id类型的分布
     * @param integer $distrBy
     * @return boolean
     */
    public static function isGpzsDistr($distrBy)
    {
        return $distrBy == self::TYPE_PLATFORM || $distrBy == self::TYPE_ZONESERVER;
    }

    /**
     * 是否是平台类型的分布
     * @param integer $distrBy
     * @return boolean
     */
    public static function isPlatformDistr($distrBy)
    {
        return $distrBy == self::TYPE_PLATFORM;
    }

    /**
     * 是否是区服类型的分布
     * @param integer $distrBy
     * @return boolean
     */
    public static function isZoneServerDistr($distrBy)
    {
        return $distrBy == self::TYPE_ZONESERVER;
    }

    /**
     * 是否是DATA类型的分布
     * @param integer $distrBy
     * @return boolean
     */
    public static function isDataDistr($distrBy)
    {
        return $distrBy == self::TYPE_DATA;
    }

    /**
     * 是否是range类型的分布
     * @param integer $distrBy
     * @return boolean
     */
    public static function isRangeDistr($distrBy)
    {
        return $distrBy == self::TYPE_RANGE;
    }

    /**
     * 是否是sstid类型的分布
     * @param integer $distrBy
     * @return boolean
     */
    public static function isSstidDistr($distrBy)
    {
        return $distrBy == self::TYPE_SSTID;
    }
}
