<?php
class TMStringValidator extends TMValidator
{
    /**
     * @var integer maximum length. Defaults to null, meaning no maximum limit.
     */
    public $max;

    /**
     * @var integer minimum length. Defaults to null, meaning no minimum limit.
     */
    public $min;

    /**
     * @var integer exact length. Defaults to null, meaning no exact length limit.
     */
    public $is;

    /**
     * @var string user-defined error message used when the value is too short.
     */
    public $tooShort;

    /**
     * @var string user-defined error message used when the value is too long.
     */
    public $tooLong;

    /**
     * @var boolean whether the attribute value can be null or empty. Defaults to true,
     * meaning that if the attribute is empty, it is considered valid.
     */
    public $allowEmpty = false;

    /**
     * @var string the value set when the attribute is empty. Defaults to null.
     */
    public $defaultValue;

    /**
     * @var string the encoding of the string value to be validated (e.g. 'UTF-8').
     * This property is used only when mbstring PHP extension is enabled.
     * The value of this property will be used as the 2nd parameter of the
     * mb_strlen() function.
     * If this property is set false, then strlen() will be used even if mbstring is enabled.
     */
    public $encoding = 'UTF-8';

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
            if ($this->defaultValue) $object->$attribute = $this->defaultValue;
            return;
        }
        if ($this->isEmpty($value)) {
            $this->throwError($object, $attribute, '{attribute}不能为空！');
        }

        if (is_array($value)) {
            $this->throwError($object, $attribute, '{attribute}配置错误！');
        }

        if (function_exists('mb_strlen') && $this->encoding !== false) {
            $length = mb_strlen($value, $this->encoding);
        } else {
            $length = strlen($value);
        }

        if ($this->min !== null && $length < $this->min) {
            $message = $this->tooShort !== null ? $this->tooShort : '{attribute}至少{min}个字符！';
            $this->throwError($object, $attribute, $message, array('{min}' => $this->min));
        }
        if ($this->max !== null && $length > $this->max) {
            $message = $this->tooLong !== null ? $this->tooLong : '{attribute}至多{max}个字符！';
            $this->throwError($object, $attribute, $message, array('{max}' => $this->max));
        }
        if ($this->is !== null && $length !== $this->is) {
            $message = $this->message !== null ? $this->message : '{attribute}必须{length}个字符！';
            $this->throwError($object, $attribute, $message, array('{length}' => $this->is));
        }
    }
}
