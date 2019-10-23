<?php
class TMInlineValidator extends TMValidator
{
    /**
     * @var string method name to call.
     */
    public $method;

    /**
     * @var mixed
     */
    public $params;

    /**
     * @var string
     */
    public $message = '{attribute}的设置不正确！';

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
        $method = $this->method;
        if (!$object->$method($value, $attribute, $this->params)) {
            $this->throwError($object, $attribute, $this->message);
        }
    }
}
