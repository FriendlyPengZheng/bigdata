<?php
class TMRegexValidator extends TMValidator
{
    /**
     * @var string
     */
    public $pattern;
    
    /**
     * @var {boolean} whether to reverse the result of pattern
     */
    public $reverse = false;

    /**
     * @var boolean whether the attribute value can be null or empty. Defaults to true,
     * meaning that if the attribute is empty, it is considered valid.
     */
    public $allowEmpty = true;

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    protected function validateAttribute($object, $attribute)
    {
        $this->validateValue($object, $attribute, $object->$attribute);
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

        if ($this->pattern === null) {
            $this->throwError($object, $attribute, '{object}类的参数{attribute}配置错误！', array('{object}' => $object));
        }

        if (is_array($value)) {
            $this->throwError($object, $attribute, '{attribute}配置错误！');
        }
        
        $result = preg_match($this->pattern, $value);
        if ($this->reverse && $result || !$this->reverse && !$result) {
            $this->throwError($object, $attribute, '{attribute}配置错误！');
        }
    }
}
