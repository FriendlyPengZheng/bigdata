<?php
class common_Result extends common_Stat
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_common_result';
    }

    public function attributes()
    {
        return array('result_id as r_id', '("result") AS type', 'result_name AS r_name');
    }

    public function findByUk($iGameId, $aUniqueKey)
    {
        $aINParam = $aINPos = array();
        foreach ($aUniqueKey as $r) {
            $aINParam[] = $r['task_id'];
            $aINPos[] = '?';
        }
        $aINParam[] = $iGameId;
        return $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('task_id,' . implode(',', $this->attributes()))
            ->from($this->tableName())
            ->where('task_id IN (' . implode(',', $aINPos) . ')')
            ->andWhere('game_id = ?')
            ->queryAll($aINParam);
    }

    public function getUk($aInfo)
    {
        return $aInfo['task_id'];
    }

    public function undefined($suffix)
    {
        $undefined = array_fill_keys(array('r_id', 'r_name'), 'undefined' . $suffix);
        $undefined['type'] = 'undefined';
        return $undefined;
    }
}
