<?php
class TMExistValidator extends TMValidator
{
    /**
     * @var string the class of the attribute to be checked
     */
    public $className = null;

    /**
     * @var string the attribute to be checked
     */
    public $attributeName = null;

    /**
     * @var array the condition to check exists
     */
    public $condition = null;

    /**
     * array
     */
    public $exclude = null;

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    protected function validateAttribute($object, $attribute)
    {
        $attributeName = $this->attributeName === null ? $attribute: $this->attributeName;
        $queryParams = array();
        $condition = " $attributeName = ?";
        $queryParams[] = $object->$attribute;

        if ($this->className !== null) {
            $className = TM::import($this->className);
            $object = new $className();
        }
        $table = $object->getTableSchema();
        if (($column = $table->getColumn($attributeName)) === null) {
            $this->throwError($object, $attribute, '{object}没有{attribute}属性！', array('{object}' => $className));
        }
        if (is_array($this->condition)) {
            foreach ($this->condition as $attr => $val) {
                if ($val === null) {
                    $condition .= " AND $attr IS NULL";
                } else {
                    $condition .= " AND $attr = ?";
                    $queryParams[] = $val;
                }
            }
        }
        if (is_array($this->exclude)) {
            foreach ($this->exclude as $attr => $val) {
                if ($val === null) {
                    $condition .= " AND $attr IS NOT NULL";
                } else {
                    $condition .= " AND $attr != ?";
                    $queryParams[] = $val;
                }
            }
        }
        if (!$object->getDb()->createCommand(
            'SELECT COUNT(1) FROM ' . $object->tableName()
            . ' WHERE ' . $condition
        )->queryScalar($queryParams)) {
            $this->throwError($object, $attribute, '{attribute}不存在！');
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
            '{class}' => 'TMExistValidator',
            '{method}' => 'validateValue'
        )));
    }
}
