<?php
class TMTimeValidator extends TMValidator
{
    /**
     * @var integer maximum length. Defaults to null, meaning no maximum limit.
     */
    public $format;

    /**
     * @var boolean whether the attribute value can be null or empty. Defaults to true,
     * meaning that if the attribute is empty, it is considered valid.
     */
    public $allowEmpty = false;

    public $message;

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

        if (is_array($value)) {
            $this->throwError($object, $attribute, '{attribute}配置错误！');
        }

        $message = $this->message !== null ? $this->message : '{attribute}格式不正确！';

        $format = str_split(preg_replace('/\w/i', 'd', $this->format));
        $splitValue = str_split($value);
        count($format) == count($splitValue) || $this->throwError($object, $attribute, $message);

        foreach ($format as $index => $mark) {
            $v = $splitValue[$index];
            if ($mark == 'd') {
                preg_match('/\d/', $v) || $this->throwError($object, $attribute, $message);
            } else {
                $mark == $v || $this->throwError($object, $attribute, $message);
            }
        }

        strtotime($value) || $this->throwError($object, $attribute, $message);
    }
}
