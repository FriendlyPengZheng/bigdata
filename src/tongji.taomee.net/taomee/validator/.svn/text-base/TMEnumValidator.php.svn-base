<?php
class TMEnumValidator extends TMValidator
{
    /**
     * @var mixed default value to be set while empty. Defaults to null, meaning no default value
     */
    public $defaultValue;
    
    /**
     * @var {boolean} whether to reverse the result of pattern
     */
    public $reverse = false;
    
    /**
     * @var boolean whether the attribute value can be null or empty. Defaults to true,
     * meaning that if the attribute is empty, it is considered valid.
     */
    public $allowEmpty = false;

    /**
     * @var array.
     */
    public $range;

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    protected function validateAttribute($object, $attribute)
    {
        if ($this->allowEmpty && $this->isEmpty($object->$attribute)) {
            if (null !== $this->defaultValue) {
                $object->$attribute = $this->defaultValue;
            }
            return;
        }
        $this->valid($object, $attribute, $object->$attribute);
    }

    /**
     * @brief valid 
     * 公共验证
     *
     * @param {object} $object 需要验证的类
     * @param {string} $attribute the attribute being validated
     * @param {mixed} $value the value being validated
     */
    protected function valid($object, $attribute, $value)
    {
        if (!is_array($this->range)) {
            $this->throwError($object, $attribute, '{attribute}的range属性必需为数组！');
        }
        if ($this->isEmpty($value)) {
            $this->throwError($object, $attribute, '{attribute}不能为空！');
        }
        if (!$this->reverse && !in_array($value, $this->range) || $this->reverse && in_array($value, $this->range)) {
            $this->throwError($object, $attribute, '{attribute}的设置不正确！');
        }
    }

    /**
     * Validates the given value
     * @param {object} $object 需要验证的类
     * @param {string} $attribute the attribute being validated
     * @param {mixed} $value the value being validated
     */
    public function validateValue(TMValidatorInterface $object, $attribute, $value)
    {
        if ($this->allowEmpty && $this->isEmpty($value)) {
            return;
        }
        $this->valid($object, $attribute, $value);
    }
}
