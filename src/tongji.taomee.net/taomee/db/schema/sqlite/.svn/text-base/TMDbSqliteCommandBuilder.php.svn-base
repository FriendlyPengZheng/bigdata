<?php
/**
* @file TMDbSqliteCommandBuilder.php
* @brief provides sqlite-special methods to create query commands.
* @author violet violet@taomee.com
* @version 1.0
* @date 2015-02-12
*/
class TMDbSqliteCommandBuilder extends TMDbCommandBuilder
{
    /**
     * @see parent
     */
    protected function createCompositeInCondition($table, $values)
    {
        $keyNames = array();
        foreach ($values[0] as $name => $value) {
            $keyNames[] = $table->columns[$name]->rawName;
        }
        $vs = array();
        foreach ($values as $value) {
            $vs[] = '(' . implode('||\',\'||', $value) . ')';
        }
        return '(' . implode('||\',\'||', $keyNames) . ') IN (' . implode(',', $vs) . ')';
    }
}
