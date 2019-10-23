<?php
class TMRequiredValidator extends TMValidator
{
    /**
     * @var boolean
     */
    public $trim = true;

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    protected function validateAttribute($object, $attribute)
    {
        $value = $object->$attribute;
        if ($this->isEmpty($value, $this->trim)) {
            $this->throwError($object, $attribute, '{attribute}不能为空！');
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
            '{class}' => 'TMRequiredValidator',
            '{method}' => 'validateValue'
        )));
    }
}
