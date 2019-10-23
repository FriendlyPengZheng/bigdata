<?php
abstract class TMValidator extends TMComponent
{
    /**
     * @var array aliases for validators.
     */
    public static $validators = array(
        'exist'    => 'TMExistValidator',
        'unique'   => 'TMUniqueValidator',
        'string'   => 'TMStringValidator',
        'number'   => 'TMNumberValidator',
        'required' => 'TMRequiredValidator',
        'default'  => 'TMDefaultValidator',
        'enum'     => 'TMEnumValidator',
        'regex'    => 'TMRegexValidator',
        'time'     => 'TMTimeValidator',
        'email'    => 'TMEmailValidator'
    );

    /**
     * @var array attributes.
     */
    public $attributes = array();

    /**
     * @var string the user-defined error message. Different validators may define various
     * placeholders in the message that are to be replaced with actual values. All validators
     * recognize "{attribute}" placeholder, which will be replaced with the label of the attribute.
     */
    public $message;

    /**
     * @var string the user-defined exception class name. All exceptions will throw by only one
     * param message.
     */
    public $exception;

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    abstract protected function validateAttribute($object, $attribute);

    /**
     * Validates the given value
     * @param {object} $object 需要验证的类
     * @param {string} $attribute the attribute being validated
     * @param {mixed} $value the value being validated
     */
    abstract public function validateValue(TMValidatorInterface $object, $attribute, $value);

    /**
     * @brief createValidator
     * 创建验证的类
     *
     * @param {string} $name 验证类的名称
     * @param {object} $object 需要验证的类
     * @param {array|string} $attributes 验证属性的名称
     * @param {array|null} $params 验证的其他参数
     *
     * @return {TMValidator}
     */
    public static function createValidator($name, $object, $attributes, $params = array())
    {
        if (is_string($attributes)) {
            $attributes = preg_split('/[\s,]+/', $attributes, -1, PREG_SPLIT_NO_EMPTY);
        }
        if (!is_array($params)) {
            $params = array();
        }
        if (method_exists($object, $name)) {
            $validator = new TMInlineValidator();
            $validator->attributes = $attributes;
            $validator->method = $name;
            $validator->params = $params;
            if (isset($params['message'])) {
                $validator->message = $params['message'];
            }
            if (isset($params['exception'])) {
                $validator->exception = $params['exception'];
            }
        } else {
            $params['attributes'] = $attributes;
            if (isset(self::$validators[$name])) {
                $className = TM::import(self::$validators[$name], true);
            } else {
                $className = TM::import($name, true);
            }
            $validator = new $className();
            foreach ($params as $attr => $value) {
                $validator->$attr = $value;
            }
        }
        return $validator;
    }

    /**
     * 验证
     *
     * @param {object} $object
     * @param {array|string} $attributeName
     * @param {mixed} $attributes
     */
    public function validate(TMValidatorInterface $object, $attributes)
    {
        if (is_array($attributes)) {
            $attributes = array_intersect($this->attributes, $attributes);
        } else {
            $attributes = $this->attributes;
        }
        foreach ($attributes as $attribute) {
            $this->validateAttribute($object, $attribute);
        }
    }

    /**
     * Make sure the given expression is true, otherwise throw exception.
     *
     * @param mixed    $expression
     * @param string   $message
     * @param string   $exceptionType
     * @param callable $callback
     * @param array    $callback
     */
    public static function ensure($expression, $message, $exceptionType = null, $callback = null, $params = array())
    {
        if (!$expression) {
            if (!isset($exceptionType)) {
                $exceptionType = 'TMValidatorException';
            }
            if (isset($callback)) {
                call_user_func_array($callback, $params);
            }
            throw new $exceptionType($message);
        }
    }

    /**
     * Checks if the given value is empty.
     * A value is considered empty if it is null, an empty array, or the trimmed result is an empty string.
     * Note that this method is different from PHP empty(). It will return false when the value is 0.
     * @param mixed $value the value to be checked
     * @param boolean $trim whether to perform trimming before checking if the string is empty. Defaults to false.
     * @return boolean whether the value is empty
     */
    protected function isEmpty($value, $trim = false)
    {
        return $value === null || $value === array() || $value === '' || $trim && is_scalar($value) && trim($value) === '';
    }

    /**
     * 共用抛异常
     *
     * @param object $object
     * @param string $attribute
     * @param string $message
     * @param array|null $params
     */
    protected function throwError($object, $attribute, $message, $params = array())
    {
        $message = isset($this->message) ? $this->message : $message;
        if (($message = $object->addError($attribute, $message, $params))) {
            if ($this->exception) {
                throw new $this->exception($message);
            } else {
                throw new TMValidatorException($message);
            }
        }
    }
}

/**
 * Exception for validation.
 */
class TMValidatorException extends TMHttpException
{
    public function __construct($message, $code = -1, $status = 200)
    {
        parent::__construct($status, $message, $code);
    }
}

/**
 * validate的传入类必须实现该接口
 */
interface TMValidatorInterface
{
    /**
     * @brief addError 
     * 错误处理时会调用，返回false时，不会抛出验证异常
     *
     * @param {string} $attribute
     * @param {string} $message
     * @param {array} $params
     *
     * @return {string|false}
     */
    public function addError($attribute, $message, $params);
}
