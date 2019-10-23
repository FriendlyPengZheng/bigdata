<?php
class TMFormModel extends TMModel
{
    static private $_md = array();

    static private $_db;

    /**
     * 表单的属性
     * <array>
     */
    private $_attributes;

    /**
     * 是否处于修改状态
     * <boolean>
     */
    private $_updated;

    public function __construct()
    {
        $this->_attributes = $this->getMetaData()->attributeDefaults;
        $this->_updated = false;
        $this->init();
    }

    public function getMetaData()
    {
        $className = get_class($this);
        if (!array_key_exists($className, self::$_md)) {
            self::$_md[$className] = null;
            self::$_md[$className] = new TMFormMetaData($this);
        }
        return self::$_md[$className];
    }

    public function __get($name)
    {
        if (isset($this->_attributes[$name])) {
            return $this->_attributes[$name];
        } elseif (isset($this->getMetaData()->columns[$name])) {
            return null;
        } else {
            return parent::__get($name);
        }
    }

    public function __set($name, $value)
    {
        if ($this->setAttribute($name,$value) === false) {
            parent::__set($name, $value);
        }
    }

    public function __isset($name)
    {
        if (isset($this->_attributes[$name])) {
            return true;
        } elseif (isset($this->getMetaData()->columns[$name])) {
            return false;
        } else {
            return parent::__isset($name);
        }
    }

    public function __unset($name)
    {
        if (isset($this->getMetaData()->columns[$name])) {
            unset($this->_attributes);
        } else {
            parent::__unset($name);
        }
    }

    /**
     * 获取表名称
     * @return {string}
     */
    public function tableName()
    {
        return 't_' . strtolower(preg_replace('/([a-z])([A-Z])/', '$1_$2', get_class($this)));
    }

    /**
     * 获取属性名称
     * @return {array}
     */
    public function attributeNames()
    {
        return array_keys($this->getMetaData()->columns);
    }

    /**
     * 只有当表格数据库中没有设置主键时需要重写该方法
     * @return string|array
     */
    public function primaryKey()
    {
    }

    /**
     * @brief getTableSchema
     * 获取表结构
     * @return {object}
     */
    public function getTableSchema()
    {
        return $this->getMetaData()->tableSchema;
    }

    /**
     * @brief getCommandBuilder
     * 获取SQL拼接类
     * @return {object}
     */
    public function getCommandBuilder()
    {
        return $this->getDb()->getSchema()->getCommandBuilder();
    }

    /**
     * @brief hasAttribute
     * 检测是否存在属性
     * @param {string} $name
     * @return {boolean}
     */
    public function hasAttribute($name)
    {
        return isset($this->getMetaData()->columns[$name]);
    }

    /**
     * @brief getAttribute
     * 获取设置的属性
     * @param {string} $name
     * @return {mixed}
     */
    public function getAttribute($name)
    {
        if (property_exists($this, $name)) {
            return $this->$name;
        } elseif (isset($this->_attributes[$name])) {
            return $this->_attributes[$name];
        }
    }

    /**
     * @brief setAttribute
     * 设置属性
     * @param {string} $name
     * @param {mixed} $value
     * @return {boolean}
     */
    public function setAttribute($name, $value)
    {
        if (property_exists($this, $name)) {
            $this->$name = $value;
        } elseif (isset($this->getMetaData()->columns[$name])) {
            $this->_attributes[$name] = $value;
        } else {
            return false;
        }
        return true;
    }

    /**
     * @brief getAttributes
     * 批量获取属性及值
     * @param {array|true} $names 当为true时，返回所有设置的属性及值
     * @return {array}
     */
    public function getAttributes($names=true)
    {
        $attributes = $this->_attributes;
        foreach ($this->getMetaData()->columns as $name => $column) {
            if (property_exists($this, $name)) {
                $attributes[$name] = $this->$name;
            } elseif ($names === true && !isset($attributes[$name])) {
                $attributes[$name] = null;
            }
        }
        if (is_array($names)) {
            $attrs = array();
            foreach($names as $name) {
                if(property_exists($this, $name)) {
                    $attrs[$name] = $this->$name;
                } else {
                    $attrs[$name] = isset($attributes[$name]) ? $attributes[$name] : null;
                }
            }
            return $attrs;
        } else {
            return $attributes;
        }
    }

    /**
     * @brief attributes 
     * 返回所有的字段名
     *
     * @return {array}
     */
    public function attributes()
    {
        return array_keys($this->getMetaData()->columns);
    }

    /**
     * @brief save 
     * 保存
     *
     * @param {array|true} $attributes
     * @return {boolean}
     */
    public function save($attributes=true)
    {
        $primaryKey = $this->getPrimaryKey();
        $updated = true;
        if (is_array($primaryKey)) {
            foreach ($primaryKey as $value) {
                $updated = empty($value) ? false : true;
                if (!$updated) break;
            }
        } else {
            $updated = empty($primaryKey) ? false : true;
        }

        $this->_updated = $updated;
        if ($updated) {
            return $this->update($attributes);
        } else {
            return $this->insert($attributes);
        }
    }

    /**
     * @brief isUpdated 
     * 获取当前处于的状态
     *
     * @return {boolean}
     */
    public function isUpdated()
    {
        return $this->_updated;
    }

    /**
     * @brief insert
     * 插入新的记录
     * @param {array|true} $attributes
     * @return {boolean}
     */
    public function insert($attributes=true)
    {
        if (!is_array($attributes)) $attributes = $this->attributeNames();
        $this->validate($attributes);
        $builder = $this->getCommandBuilder();
        $table = $this->getMetaData()->tableSchema;
        if ($this->beforeInsert()) {
            $command = $builder->createInsertCommand($table, $this->getAttributes($attributes));
            if ($command->execute()) {
                $primaryKey = $table->primaryKey;
                if (is_string($primaryKey) && $this->$primaryKey === null) {
                    $this->$primaryKey = $builder->getLastInsertID($table);
                } elseif (is_array($primaryKey)) {
                    foreach($primaryKey as $pk) {
                        if($this->$pk === null) {
                            $this->$pk = $builder->getLastInsertID($table);
                            break;
                        }
                    }
                }
                $this->afterInsert();
                return true;
            }
        }
        return false;
    }

    /**
     * Doing something before insert a record.
     * @return boolean If false, the inserting action will be stopped.
     */
    public function beforeInsert()
    {
        return true;
    }

    /**
     * 插入后执行
     */
    public function afterInsert()
    {
    }

    /**
     * 更新记录（根据主键）
     * @param {array|true} $attributes
     * @return {boolean}
     */
    public function update($attributes = true)
    {
        if (!is_array($attributes)) $attributes = $this->attributeNames();
        $this->validate($attributes);
        $builder = $this->getCommandBuilder();
        $table = $this->getMetaData()->tableSchema;
        if ($this->beforeUpdate()) {
            $command = $builder->createUpdateCommand($table,
                $this->getAttributes($attributes),
                $builder->createPkCondition($table, (array)$this->getPrimaryKey()));
            $command->execute();
            $this->afterUpdate();
            return true;
        }
        return false;
    }

    /**
     * Doing something before update a record by primary key.
     * @return boolean If false, the updating action will be stopped.
     */
    public function beforeUpdate()
    {
        return true;
    }

    /**
     * Doing something after update a record by primary key.
     */
    public function afterUpdate()
    {
    }

    /**
     * @brief delete
     * 删除记录（根据主键）
     * @return {boolean}
     */
    public function delete()
    {
        $builder = $this->getCommandBuilder();
        $table   = $this->getMetaData()->tableSchema;
        if ($this->beforeDelete()) {
            $command = $builder->createDeleteCommand($table, $builder->createPkCondition($table, (array)$this->getPrimaryKey()));
            if ($command->execute() !== false) {
                $this->afterDelete();
                return true;
            }
        }
        return false;
    }

    /**
     * 删除前调用
     * @return {boolean} 当返回true时继续删除，否则不删除
     */
    public function beforeDelete()
    {
        return true;
    }

    /**
     * 删除后调用
     */
    public function afterDelete()
    {
    }

    /**
     * @brief deleteAllByAttributes
     * 根据属性值删除数据
     * @param {array} $attributes
     * @return {boolean}
     */
    public function deleteAllByAttributes($attributes)
    {
        $builder = $this->getCommandBuilder();
        $table = $this->getTableSchema();
        $condition = $builder->createColumnCondition($table, $attributes);
        $command = $builder->createDeleteCommand($table, $condition['condition'], $condition['param']);
        return $command->execute() !== false;
    }

    /**
     * @brief getPrimaryKey
     * 获取主键值
     */
    public function getPrimaryKey()
    {
        $table = $this->getMetaData()->tableSchema;
        if (is_string($table->primaryKey)) {
            return $this->{$table->primaryKey};
        } elseif (is_array($table->primaryKey)) {
            $values = array();
            foreach ($table->primaryKey as $name) {
                $values[$name] = $this->$name;
            } return array($values);
        } else {
            return null;
        }
    }

    /**
     * 设置主键值
     * @param {mixed} $value
     */
    public function setPrimaryKey($value)
    {
        $table = $this->getMetaData()->tableSchema;
        if (is_string($table->primaryKey)) {
            $this->{$table->primaryKey} = $value;
        } elseif (is_array($table->primaryKey)) {
            foreach ($table->primaryKey as $name) {
                $this->$name = $value[$name];
            }
        }
    }

    /**
     * @brief findByPk
     * 通过主键加载model
     * TODO 条件未添加
     *
     * @param {array|mixed} $pk 主键值
     * @param $condition
     * @param $param
     *
     * @return
     */
    public function findByPk($pk, $condition='', $param=array())
    {
        $builder = $this->getCommandBuilder();
        $table = $this->getTableSchema();
        $condition = $builder->createPkCondition($table, (array)$pk);
        $sql = "SELECT * FROM {$table->name} WHERE {$condition}";
        $command = $this->getDb()->createCommand($sql);
        $model = clone $this;
        $model->attributes = $command->queryRow();
        $model->afterFind();
        return $model;
    }

    /**
     * @brief afterFind
     * 查找后调用
     */
    public function afterFind() {}

    /**
     * @brief find
     * 返回数据库命令
     *
     * @return TMDbCommand
     */
    public function find()
    {
        return $this->getDb()->createCommand()
            ->select($this->attributes())
            ->from($this->tableName());
    }





    /**
     * 取数据库数据
     * @param  array $params
     * @return array
     */
    public function findAll($params = null)
    {
        $select = $this->attributes();
        $condition = $order = $limit = $offset = '';
        $queryParams = array();
        if (is_array($params)) {
            if (isset($params['select'])) $select = $params['select'];
            if (isset($params['condition']) && is_array($params['condition'])) {
                $condition = array('AND');
                foreach ($params['condition'] as $attr => $val) {
                    $condition[] = $attr . ' = ?';
                    $queryParams[] = $val;
                }
            }
            if (isset($params['order'])) $order = $params['order'];
            if (isset($params['limit'])) $limit = $params['limit'];
            if (isset($params['offset'])) $offset = $params['offset'];
        }
        $command = $this->getDb()->createCommand()->select($select)->from($this->tableName())->where($condition);
        if ($order) $command->order($order);
        if ($limit) $command->limit($limit);
        if ($offset) $command->offset($offset);
        return $command->queryAll($queryParams);
    }
}

class TMFormMetaData
{
    private $_modelClassName;

    public $tableSchema;

    public $columns;

    public $attributeDefaults = array();

    public function __construct($model)
    {
        $this->_modelClassName = get_class($model);

        $tableName = $model->tableName();
        if (($table = $model->getDb()->getSchema()->getTable($tableName)) === null) {
            throw new TMException(TM::t('taomee','"{class}类设置的"数据表"{table}"不存在！',
                array('{class}' => $this->_modelClassName, '{table}' => $tableName)));
        }
        if ($table->primaryKey === null) {
            $table->primaryKey = $model->primaryKey();
            if (is_string($table->primaryKey) && isset($table->columns[$table->primaryKey])) {
                $table->columns[$table->primaryKey]->isPrimaryKey = true;
            } elseif (is_array($table->primaryKey)) {
                foreach ($table->primaryKey as $name) {
                    if (isset($table->columns[$name])) {
                        $table->columns[$name]->isPrimaryKey = true;
                    }
                }
            }
        }
        $this->tableSchema = $table;
        $this->columns = $table->columns;

        foreach ($table->columns as $name => $column) {
            if (!$column->isPrimaryKey && $column->defaultValue !== null) {
                $this->attributeDefaults[$name] = $column->defaultValue;
            }
        }
    }
}
