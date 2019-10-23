<?php
class TMNumberValidator extends TMValidator
{
    /**
     * @var boolean whether the attribute value can only be an integer. Defaults to false.
     */
    public $integerOnly = false;

    /**
     * @var boolean whether the attribute value can be null or empty. Defaults to true,
     * meaning that if the attribute is empty, it is considered valid.
     */
    public $allowEmpty = false;

    /**
     * @var integer|float the value set when the attribute is empty. Defaults to null.
     */
    public $defaultValue;

    /**
     * @var integer|float upper limit of the number. Defaults to null, meaning no upper limit.
     */
    public $max;

    /**
     * @var integer|float lower limit of the number. Defaults to null, meaning no lower limit.
     */
    public $min;

    /**
     * @var string user-defined error message used when the value is too big.
     */
    public $tooBig;

    /**
     * @var string user-defined error message used when the value is too small.
     */
    public $tooSmall;

    /**
     * @var string the regular expression for matching integers.
     */
    public $integerPattern = '/^\s*[+-]?\d+\s*$/';

    /**
     * @var string the regular expression for matching numbers.
     */
    public $numberPattern = '/^\s*[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?\s*$/';

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
        if (!is_numeric($value)) {
            $message = $this->message !== null ? $this->message : '{attribute}必须为数字！';
            $this->throwError($object, $attribute, $message);
        }
        if ($this->integerOnly) {
            if (!preg_match($this->integerPattern, "{$value}")) {
                $message = $this->message !== null ? $this->message : '{attribute}必须为整数！';
                $this->throwError($object, $attribute, $message);
            }
        } else {
            if (!preg_match($this->numberPattern, "{$value}")) {
                $message = $this->message !== null ? $this->message : '{attribute}必须为数字！';
                $this->throwError($object, $attribute, $message);
            }
        }
        if ($this->min !== null && $value < $this->min) {
            $message = $this->tooSmall !== null ? $this->tooSmall : '{attribute}不能小于{min}！';
            $this->throwError($object, $attribute, $message, array('{min}' => $this->min));
        }
        if ($this->max !== null && $value > $this->max) {
            $message = $this->tooBig !== null ? $this->tooBig : '{attribute}不能大于{max}！';
            $this->throwError($object, $attribute, $message, array('{max}' => $this->max));
        }
    }
}
