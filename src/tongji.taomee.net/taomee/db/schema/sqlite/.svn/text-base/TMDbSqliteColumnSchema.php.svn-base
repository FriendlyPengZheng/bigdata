<?php
/**
* @file TMDbSqliteColumnSchema.php
* @brief TMDbSqliteColumnSchema is the class for retrieving metadata information from a Sqlite database.
* @author violet violet@taomee.com
* @version 1.0
* @date 2015-02-12
*/
class TMDbSqliteColumnSchema extends TMDbColumnSchema
{
    /**
     * @see parent
     */
    protected function extractDefault($defaultValue)
    {
        $this->defaultValue = $this->typecast(strcasecmp($defaultValue, 'null') ? $defaultValue : null);
    }
}
