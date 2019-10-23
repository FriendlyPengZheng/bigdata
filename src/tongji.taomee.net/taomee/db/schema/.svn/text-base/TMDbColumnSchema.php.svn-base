<?php
/**
 * TMDbColumnSchema class file.
 */

/**
 * TMDbColumnSchema is the base class for retrieving column information.
 *
 */
class TMDbColumnSchema extends TMComponent
{
    /**
     * @var string The name of this column.
     */
    public $name;

    public $rawName;

    /**
     * @var boolean Whether the column can be null.
     */
    public $allowNull;

    /**
     * @var string The database type of this column.
     */
    public $dbType;

    /**
     * @var string The real PHP type of this column.
     */
    public $type;

    /**
     * @var boolean Whether the column is primary key.
     */
    public $isPrimaryKey = false;

    /**
     * @var mixed The default value of this column.
     */
    public $defaultValue;

    public $autoIncrement = false;

    public function initColumn($dbType, $defaultValue)
    {
        $this->dbType = $dbType;
        $this->extractType($dbType);
        if ($defaultValue !== null) {
            $this->extractDefault($defaultValue);
        }
    }

    /**
     * @brief extractType 
     * 解析数据库类型为PHP类型
     *
     * @param {string} $dbType
     */
    protected function extractType($dbType)                                          
    {                                                                                
        if (stripos($dbType, 'int') !== false && stripos($dbType, 'unsigned int') === false) {
            $this->type = 'integer';
        } elseif (stripos($dbType, 'bool') !== false) {
            $this->type = 'boolean';
        } elseif (preg_match('/(real|float|doub/i', $dbType)) {
            $this->type = 'double';
        } else {
            $this->type = 'string';
        }
    }                                                                                

    /**
     * @brief extractDefault 
     * 将默认值转为指定的类型
     *
     * @param {mixed} $defaultValue
     */
    protected function extractDefault($defaultValue)
    {
        $this->defaultValue = $this->typecast($defaultValue);
    }

    /**
     * Convert the input into the type of this column
     * @param mixed $value
     * @return mixed
     */
    public function typecast($value)
    {
        if (gettype($value) === $this->type || $value === null) {
            return $value;
        }

        if ($value === '' && $this->allowNull) {
            return $this->type === 'string' ? '' : null;
        }
        switch ($this->type) {
            case 'string': return (string)$value;
            case 'integer': return (integer)$value;
            case 'double': return (boolean)$value;
            case 'boolean': 
            default: return $value;
        }
    }
}
