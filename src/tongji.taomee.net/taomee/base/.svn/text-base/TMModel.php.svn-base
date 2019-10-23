<?php
class TMModel extends TMComponent implements TMValidatorInterface
{
    private $_db = null;

    private $_validators;

    /**
     * @brief tableName
     * 获取数据表的名称
     *
     * @return {string}
     */
    public function tableName()
    {
        return '';
    }

    /**
     * 获取数据表的字段
     *
     * @return {Array}
     */
    public function attributes()
    {
        return array();
    }

    /**
     * 获取属性名称
     * @return {array}
     */
    public function attributeNames()
    {
        return array();
    }

    /**
     * 获取属性前端显示名称
     * @return {array}
     */
    public function attributeLabels()
    {
        return array();
    }

    /**
     * 获取指定属性的显示名称
     * @param {string} $attribute 属性名
     * @return {string} 属性显示名称
     * @see generateAttributeLabel
     * @see attributeLabels
     */
    public function getAttributeLabel($attribute)
    {   
        $labels = $this->attributeLabels();
        if (isset($labels[$attribute])) {
            return $labels[$attribute];
        } else {
            return $this->generateAttributeLabel($attribute);
        }
    }

    /**
     * 动态生成用户友好的显示名称
     * @param {string} $name 列名
     * @return {string} 动态生成的显示名称
     */
     public function generateAttributeLabel($name)
     {   
         return ucwords(trim(strtolower(str_replace(array('-','_','.'),' ',preg_replace('/(?<![A-Z])[A-Z]/', ' \0', $name)))));
     } 

    /**
     * 批量设置属性
     * @param {array} $values
     */
    public function setAttributes($values)
    {   
        if (!is_array($values)) return;
        foreach ($values as $name => $value) {   
            $this->$name = $value;
        }   
    }

    /**
     * @brief getDb
     * 获取数据库操作实例
     *
     * @return {TMDbConnection}
     */
    public function getDb()
    {
        if (!isset($this->_db)) {
            $this->_db = TM::app()->getDb();
        }
        $this->_db->modelClass = $this;
        return $this->_db;
    }

    /**
     * 返回类属性的配置
     *
     * @return {array}
     */
    public function rules()
    {
        return array();
    }

    /**
     * 验证
     *
     * @param {string|array} $attributeName
     * @param {null|mixed} $attributeValue
     */
    protected function validate($attributeName, $attributeValue = null)
    {
        foreach ($this->getValidators() as $validator) {
            if ($attributeValue) {
                $this->$attributeName = $attributeValue;
            }
            $validator->validate($this, $attributeName);
        }
    }

    /**
     * 获取需要加载的验证类
     *
     * @param {string|null} $attribute
     *
     * @return {array}
     */
    protected function getValidators($attribute = null)
    {
        if ($this->_validators === null) {
            $this->_validators = $this->createValidators();
        }
        $validators = array();
        foreach ($this->_validators as $validator) {
            if ($attribute === null || in_array($attribute, $validator->attributes, true)) {
                $validators[] = $validator;
            }
        }
        return $validators;
    }

    /**
     * @brief resetValidators 
     * 重置验证类
     */
    protected function resetValidators()
    {
        $this->_validators = null;
    }

    /**
     * 创建配置的验证类
     *
     * @return {array}
     */
    protected function createValidators()
    {
        $validators = array();
        foreach ($this->rules() as $rule) {
            if (isset($rule[0], $rule[1])) {
                $validators[] = TMValidator::createValidator($rule[1], $this, $rule[0], array_slice($rule, 2));
            } else {
                throw new TMException(TM::t('taomee', '{class}的属性验证配置不正确！', array('{class}' => get_class($this))));
            }
        }
        return $validators;
    }

    /**
     * 错误处理
     * 当返回false时，不进行共用的错误处理
     *
     * @param {string} $attribute
     * @param {string} $message
     * @param {array} $params
     *
     * @return {string}
     */
    public function addError($attribute, $message, $params)
    {
        $params['{attribute}'] = $this->getAttributeLabel($attribute);
        return TM::t('taomee', $message, $params);
    }
}
