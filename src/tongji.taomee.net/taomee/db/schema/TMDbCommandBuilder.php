<?php
/*
 * TMDbCommandBuilder class file.
 */

/*
 * TMDbCommandBuilder provides basic methods to create query commands.
 */
class TMDbCommandBuilder extends TMComponent
{
    const PARAM_PREFIX=':dbparam';

    /**
     * @var TMDbSchema The schema for this command builder.
     */
    private $_schema;

    /**
     * @var TMDbConnection Database connection.
     */
    private $_connection;

    /**
     * @param TMDbSchema $schema the schema for this command builder
     */
    public function __construct($schema)
    {
        $this->_schema = $schema;
        $this->_connection = $schema->getDbConnection();
    }

    /**
     * Get schema.
     * @return TMDbSchema.
     */
    public function getSchema()
    {
        return $this->_schema;
    }

    /**
     * Alters the SQL to apply LIMIT and OFFSET.
     * Default implementation is applicable for PostgreSQL, MySQL and SQLite.
     * @param string $sql SQL query string without LIMIT and OFFSET.
     * @param integer $limit maximum number of rows, -1 to ignore limit.
     * @param integer $offset row offset, -1 to ignore offset.
     * @return string SQL with LIMIT and OFFSET
     */
    public function applyLimit($sql, $limit, $offset)
    {
        if ($limit >= 0) {
            $sql .= ' LIMIT ' . (int)$limit;
        }
        if ($offset > 0) {
            $sql .= ' OFFSET ' . (int)$offset;
        }
        return $sql;
    }

    /**
     * 连接SQL的条件语句
     * @param string $sql
     * @param string $condition
     * @return string
     */
    public function applyCondition($sql, $condition)
    {
        if ($condition !== '') {
            return $sql . ' WHERE ' . $condition;
        } else {
            return $condition;
        }
    }

    /**
     * @brief ensureTable
     * 确定数据表存在性
     * @param {string} $table
     */
    protected function ensureTable(&$table)
    {
        if (is_string($table) && ($table=$this->_schema->getTable($table)) === null) {
            throw new TMException(TM::t('taomee','"{class}类设置的"数据表"{table}"不存在！',
                array('{class}' => $this->_modelClassName, '{table}' => $tableName)));
        }
    }

    /**
     * Creates an INSERT command.
     * @param TMDbTableSchema|string $table the table schema or the name of table.
     * @param array $data data to be inserted (column name=>column value). If a key is not a valid column name, the corresponding value will be ignored.
     * @return TMDbCommand insert command
     */
    public function createInsertCommand($table, $data)
    {
        $this->ensureTable($table);
        $fields = array();
        $placeholders = array();
        $values = array();
        $i = 0;
        foreach ($data as $name => $value) {
            if (($column = $table->getColumn($name)) !== null && ($value !== null || $column->allowNull)) {
                $fields[] = $column->rawName;
                $placeholders[] = self::PARAM_PREFIX.$i;
                $values[self::PARAM_PREFIX.$i] = $column->typecast($value);
                $i ++;
            }
        }
        if ($fields === array()) {
            $pks = is_array($table->primaryKey) ? $table->primaryKey : array($table->primaryKey);
            foreach ($pks as $pk) {
                $fields[] = $table->getColumn($pk)->rawName;
                $placeholders[] = 'null';
            }
        }
        $sql = "INSERT INTO {$table->name} (" . implode(',', $fields) . ') VALUES(' . implode(',', $placeholders).')';
        $command = $this->_connection->createCommand($sql);
        foreach ($values as $name => $value) {
            $command->bindValue($name, $value);
        }
        return $command;
    }

    /**
     * 获取上次插入的主键值
     * @param TMDbTableSchema $table
     * @return mixed
     */
    public function getLastInsertID($table)
    {
        if ($table->sequenceName !== null) {
            return $this->_connection->getLastInsertID($table->sequenceName);
        } else {
            return null;
        }
    }

    /**
     * @brief createPkCondition
     * 构造主键条件
     * @param TMDbTableSchema|string $table the table schema or the name of table.
     * @param {mixed} $values 主键值
     * @return {string} SQL
     */
    public function createPkCondition($table, $values)
    {
        $this->ensureTable($table);
        return $this->createInCondition($table, $table->primaryKey, $values);
    }

    /**
     * 创建含IN的SQL语句
     * @param TMDbTableSchema $table
     * @param {array} $values
     * @return {string}
     */
    public function createInCondition($table, $columnName, $values)
    {
        if (($n = count($values)) < 1) return '0=1';
        $this->ensureTable($table);
        if (is_array($columnName) && count($columnName) === 1) $columnName = reset($columnName);

        if (is_string($columnName)) {
            if (!isset($table->columns[$columnName])) {
                throw new TMDbException(TM::t('taomee', '{table}不存在列{column}！', array(
                    '{table}'  => $table->name,
                    '{column}' => $columnName
                )));
            }
            $column = $table->columns[$columnName];

            $values = array_values($values);
            foreach ($values as &$value) {
                $value = $column->typecast($value);
            }
            if ($n == 1) {
                return $column->rawName .
                    ($values[0] === null
                    ? ' IS NULL'
                    : (' = ' . $this->_connection->quoteValue($values[0])));
            } else {
                return $column->rawName . ' IN (' . implode(',', $values) . ')';
            }
        }

        if (is_array($columnName)) {
            foreach ($columnName as $name) {
                if (!isset($table->columns[$name])) {
                    throw new TMDbException(TM::t('taomee', '{table}不存在列{column}！', array(
                        '{table}'  => $table->name,
                        '{column}' => $name
                    )));
                }
                for ($i = 0; $i < $n; $i ++) {
                    if (isset($values[$i][$name])) {
                        $values[$i][$name] = $table->columns[$name]->typecast($values[$i][$name]);
                    } else {
                        throw new TMDbException(TM::t('taomee', '{table}列{column}的值没有设置！', array(
                            '{table}'  => $table->name,
                            '{column}' => $name
                        )));
                    }
                }
            }
            if (count($values) === 1) {
                $entries = array();
                foreach ($values[0] as $name => $value) {
                    $entries[] = $table->columns[$name]->rawName .
                        ($value === null
                        ? ' IS NULL'
                        : (' = ' . $this->_connection->quoteValue($value)));
                }
                return implode(' AND ', $entries);
            } else {
                return $this->createCompositeInCondition($table, $values);
            }
        }

        throw new TMDbException(TM::t('taomee', '列名只能为字符串或数组！'));
    }

    /**
     * 创建复杂IN的SQL语句
     * @param TMDbTableSchema $table
     * @param {array} $values
     * @return {string}
     */
    protected function createCompositeInCondition($table, $values)
    {
        $keyNames = array();
        foreach ($values[0] as $name => $value) {
            $keyNames[] = $table->columns[$name]->rawName;
        }
        $vs = array();
        foreach ($values as $value) {
            $vs[] = '(' . implode(',', $value) . ')';
        }
        return '(' . implode(', ', $keyNames) . ') IN (' . implode(',', $vs) . ')';
    }

    /**
     * 根据指定的列创建SQL语句
     * @param {TMDbTableSchema|string} $table
     * @param {array} $columns
     * @return {string}
     */
    public function createColumnCondition($table, $columns)
    {
        $this->ensureTable($table);
        $conditions = array();
        $values = array();
        $i = 0;
        foreach ($columns as $name => $value) {
            if (null === ($column = $table->getColumn($name))) {
                throw new TMDbException(TM::t('taomee', '{table}不存在列{column}！', array('{table}' => $table->name, '{column}' => $name)));
            }
            if (is_array($value)) {
                $conditions[] = $this->createInCondition($table, $name, $value);
            } elseif (null !== $value) {
                $conditions[] = $column->rawName . '=' . self::PARAM_PREFIX.$i;
                $values[self::PARAM_PREFIX.$i] = $column->typecast($value);
                $i ++;
            } else {
                $conditions[] = $column->rawName . ' IS NULL';
            }
        }
        return array(
            'condition' => implode(' AND ', $conditions),
            'param' => $values
        );
    }

    /**
     * 创建UPDATE语句
     * @param {TMDbTableSchema|string} $table
     * @param {array} $data
     * @param {string} $condition
     *
     * @return
     */
    public function createUpdateCommand($table, $data, $condition)
    {
        $this->ensureTable($table);
        $fields = array();
        $values = array();
        $i = 0;
        foreach ($data as $name => $value) {
            if (($column = $table->getColumn($name)) !== null && ($value !== null || $column->allowNull)) {
                $fields[] = $column->rawName . '=' . self::PARAM_PREFIX.$i;
                $values[self::PARAM_PREFIX.$i] = $column->typecast($value);
                $i ++;
            }
        }
        if ($fields === array()) {
            throw new TMDbException(TM::t('taomee','更新"{table}"失败！', array('{table}'=>$table->name)));
        }
        $sql = "UPDATE {$table->name} SET " . implode(', ', $fields);
        $sql = $this->applyCondition($sql, $condition);
        $command = $this->_connection->createCommand($sql);
        foreach ($values as $name => $value) {
            $command->bindValue($name, $value);
        }
        return $command;
    }

    /**
     * 创建DELETE语句
     * @param {TMDbTableSchema|string} $table
     * @param {string} $condition
     * @param {array} $param
     *
     * @return
     */
    public function createDeleteCommand($table, $condition, $param=array())
    {
        $this->ensureTable($table);
        $sql = "DELETE FROM {$table->name} ";
        $sql = $this->applyCondition($sql, $condition);
        $command = $this->_connection->createCommand($sql);
        foreach ($param as $name => $value) {
            $command->bindValue($name, $value);
        }
        return $command;
    }
}
