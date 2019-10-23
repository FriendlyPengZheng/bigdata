<?php
class TMDefaultValidator extends TMValidator
{
    /**
     * @var mixed default value off one attribute
     */
    public $value;

    /**
     * @var boolean
     */
    public $setOnEmpty = true;

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    protected function validateAttribute($object, $attribute)
    {
        if ($this->setOnEmpty) {
            if ($object->$attribute === null || $object->$attribute === '') {
                $object->$attribute = $this->value;
            }
        } else {
            $object->$attribute = $this->value;
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
        throw new TMException(TM::t('taomee', '非属性不能调用{class}的{method}进行检测！', array(
            '{class}' => 'TMDefaultValidator',
            '{method}' => 'validateValue'
        )));
    }
}
