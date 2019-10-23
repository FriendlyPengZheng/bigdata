<?php
class TMUniqueValidator extends TMValidator
{
    /**
     * string the class of the attribute to be checked
     */
    public $className = null;

    /**
     * string the attribute to be checked
     */
    public $attributeName = null;

    /**
     * array the condition to check exists
     */
    public $condition = null;

    /**
     * array
     */
    public $exclude = null;

    /**
     * string
     */
    public $message = '{attribute}是唯一值！';

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    protected function validateAttribute($object, $attribute)
    {
        if (is_array($object->$attribute)) {
            $this->throwError($object, $attribute, '{attribute}错误！');
        }
        $className = $this->className === null ? get_class($object) : TM::import($this->className);
        $attributeName = $this->attributeName === null ? $attribute: $this->attributeName;
        $finder = new $className();
        $table = $finder->getTableSchema();
        if (($column = $table->getColumn($attributeName)) === null) {
            $this->throwError($object, $attribute, '{object}没有{attribute}属性！', array('{object}' => $className));
        }
        $queryParams = array();
        $queryParams[] = $object->{$attribute};
        $condition = '';
        if (is_array($this->condition)) {
            foreach ($this->condition as $attr => $val) {
                if ($val === null) {
                    $condition .= " AND $attr IS NULL";
                } else {
                    $condition .= " AND $attr = ?";
                    $queryParams[] = $val;
                }
            }
        } elseif (is_string($this->condition)) {
            $condition = ' AND ' . $this->condition;
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
        if ($finder->getDb()->createCommand(
            'SELECT COUNT(1) FROM ' . $object->tableName()
            . ' WHERE ' . $attributeName . ' = ?'
            . $condition
        )->queryScalar($queryParams)) {
            $this->throwError($object, $attribute, $this->message);
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
            '{class}' => 'TMUniqueValidator',
            '{method}' => 'validateValue'
        )));
    }
}
