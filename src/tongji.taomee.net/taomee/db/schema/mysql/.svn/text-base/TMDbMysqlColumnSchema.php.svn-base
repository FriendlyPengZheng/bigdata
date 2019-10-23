<?php
/**
 * TMDbMysqlColumnSchema class file.
 */

/**
 * TMDbMysqlColumnSchema is the class for retrieving metadata information from a MySQL database (version 4.1.x and 5.x).
 *
 */
class TMDbMysqlColumnSchema extends TMDbColumnSchema
{
    protected function extractType($dbType)                                          
    {                                                                                
        if (strncmp($dbType, 'enum', 4) === 0) {
            $this->type = 'string';
        } elseif (strpos($dbType, 'float') !== false && strpos($dbType, 'double') === false) {
            $this->type = 'double';
        } elseif (stripos($dbType, 'bool') !== false) {
            $this->type = 'boolean';
        } elseif (strpos($dbType, 'int') === 0 && strpos($dbType, 'unsigned') === false || preg_match('/(bit|tinyint|smallint|mediumint)/', $dbType)) {
            $this->type = 'integer';
        } else {
            $this->type = 'string';
        }
    }                                                                                

    protected function extractDefault($defaultValue)
    {
        if (strncmp($this->dbType, 'bit', 3) === 0) {
            $this->defaultValue = bindec(trim($defaultValue, 'b\''));
        } elseif ($this->dbType === 'timestamp' && $defaultValue === 'CURRENT_TIMESTAMP') {
            $this->defaultValue = null;
        } else {
            parent::extractDefault($defaultValue);
        }
    }
}
