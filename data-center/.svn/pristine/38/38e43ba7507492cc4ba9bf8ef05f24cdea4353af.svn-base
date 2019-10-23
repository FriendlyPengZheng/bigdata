<?php
class module_ComponentFactory extends TMComponent
{
    /**
     * All component types.
     */
    const TYPE_WRAP      = 'wrap';
    const TYPE_TABS      = 'tabs';
    const TYPE_TAB       = 'tab';
    const TYPE_DATA      = 'data';
    const TYPE_LISTTABLE = 'listtable';

    /**
     * @var array components cache.
     */
    private static $_components = array();

    /**
     * Create one component.
     * @param string $type
     * @param boolean $clone Whether to clone the cached components.
     * @return module_Component
     */
    public static function createComponent($type = self::TYPE_WRAP, $clone = false)
    {
        if (!isset(self::$_components[$type])) {
            switch ($type) {
                case self::TYPE_DATA:
                    self::$_components[$type] = new module_DataComponent();
                    break;

                case self::TYPE_TABS:
                    self::$_components[$type] = new module_TabsComponent();
                    break;

                case self::TYPE_TAB:
                    self::$_components[$type] = new module_TabComponent();
                    break;

                case self::TYPE_LISTTABLE:
                    self::$_components[$type] = new module_ListTableComponent();
                    break;

                case self::TYPE_WRAP:
                default:
                    self::$_components[$type] = new module_WrapComponent();
                    break;
            }
        }
        if ($clone) {
            return clone self::$_components[$type];
        } else {
            return self::$_components[$type];
        }
    }

    /**
     * Whether a component is type of wrap.
     * @param string $type
     * @return boolean
     */
    public static function isWrapComponent($type)
    {
        return $type === self::TYPE_WRAP;
    }

    /**
     * Whether a component is type of data.
     * @param string $type
     * @return boolean
     */
    public static function isDataComponent($type)
    {
        return $type === self::TYPE_DATA;
    }
}
