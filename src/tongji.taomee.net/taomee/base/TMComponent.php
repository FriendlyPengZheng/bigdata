<?php
class TMComponent
{
    private $_id;

    private $_attributes = array();

    public function __construct($id=null)
    {
        if ($id !== null) $this->_id = $id;
    }

    /**
     * 获取属性值
     *
     * @param  string      $sAttributeName
     * @throw  TMException If attribute doesn't exist
     * @return mixed
     */
    public function __get($sAttributeName)
    {
        $sGetter = 'get' . $sAttributeName;
        if (method_exists($this, $sGetter)) {
            return $this->$sGetter();
        } elseif (property_exists($this, $sAttributeName)) {
            return $this->$sAttributeName;
        } elseif (array_key_exists($sAttributeName, $this->_attributes)) {
            return $this->_attributes[$sAttributeName];
        }
        throw new TMException(TM::t('taomee', '{class}的属性{attribute}不存在！',
            array('{class}' => get_class($this), '{attribute}' => $sAttributeName)));
    }

    /**
     * 设置属性值
     *
     * @param  string $sAttributeName
     * @param  mixed  $mAttributeVal
     * @return bool
     */
    public function __set($sAttributeName, $mAttributeVal)
    {
        $sSetter = 'set' . $sAttributeName;
        if (method_exists($this, $sSetter)) {
            return $this->$sSetter($mAttributeVal);
        } elseif (property_exists($this, $sAttributeName)){
            return $this->$sAttributeName = $mAttributeVal;
        } else {
            return $this->_attributes[$sAttributeName] = $mAttributeVal;
        }
    }

    /**
     * 查看属性是否已设置
     *
     * @param  string $sAttributeName
     * @return bool
     */
    public function __isset($sAttributeName)
    {
        $sGetter = 'get' . $sAttributeName;
        if (method_exists($this, $sGetter)) {
            return $this->$sGetter() !== null;
        } elseif (isset($this->_attributes[$sAttributeName])) {
            return true;
        }
        return false;
    }

    /**
     * 释放属性
     *
     * @param  string $sAttributeName
     * @return null
     */
    public function __unset($sAttributeName)
    {
        $sSetter = 'set' . $sAttributeName;
        if (method_exists($this, $sSetter)) {
            $this->$sSetter(null);
        } elseif (isset($this->_attributes[$sAttributeName])) {
            unset($this->_attributes[$sAttributeName]);
        }
    }
    
    /**
     * 批量设置属性
     * @param {array} $values
     */
    public function setAttributes($values)
    {
        if (!is_array($values)) return;
        foreach ($values as $name => $value) {
            $this->$name = $value;
        }
    }

    /**
     * construct时调用，可重写
     */
    public function init()
    {
    }
}
