<?php
class data_configuration_StatInfo extends data_configuration_SortedUnion
{
    const TYPE_REPORT = 1;
    const TYPE_RESULT = 2;

    public function getInfo($confs, $defaultGameId = null)
    {
        if ($this->getCache() !== null) {
            return $this->getCache();
        }
        foreach ($confs as $conf) {
            $conf['type'] = (int)$conf['type'];
            $conf['game_id'] = TMArrayHelper::assoc('game_id', $conf, $defaultGameId);
            if ($conf['type'] == self::TYPE_RESULT) {
                $this->addResult($conf);
            } elseif (isset($conf['sstid'])) {
                $this->addReport($conf);
            } else {
                $this->addReportWithoutSstid($conf);
            }
        }
        $info = $this->union();
        $this->setCache($info);
        return $info;
    }

    protected function addResult($conf)
    {
        $this->addUnion(
            "SELECT CONCAT_WS(':', {$conf['type']}, game_id, task_id) AS _uuid, " .
                "'result' AS type, result_id AS r_id, result_name AS r_name, '' AS sstid_name " .
            "FROM t_common_result " .
            "WHERE game_id = ? AND task_id = ?",
            $conf['game_id'], $conf['task_id']);
        $this->addSortedConf(sprintf('%s:%s:%s',
            $conf['type'], $conf['game_id'], $conf['task_id']), $conf);
    }

    protected function addReport($conf)
    {
        $this->addUnion(
            "SELECT CONCAT_WS(':', {$conf['type']}, game_id, stid, sstid, op_type, op_fields) AS _uuid, " .
                "'report' AS type, report_id AS r_id, report_name AS r_name, sstid_name " .
            "FROM t_report_info " .
            "WHERE game_id = ? AND stid = ? AND sstid = ? AND op_type = ? AND op_fields = ?",
            $conf['game_id'], $conf['stid'], $conf['sstid'], $conf['op_type'], $conf['op_fields']);
        $this->addSortedConf(sprintf('%s:%s:%s:%s:%s:%s',
            $conf['type'], $conf['game_id'], $conf['stid'], $conf['sstid'], $conf['op_type'], $conf['op_fields']), $conf);
    }

    protected function addReportWithoutSstid($conf)
    {
        $this->addUnion(
            "SELECT CONCAT_WS(':', {$conf['type']}, game_id, stid, op_type, op_fields) AS _uuid, " .
                "'report' AS type, report_id AS r_id, report_name AS r_name, sstid_name " .
            "FROM t_report_info " .
            "WHERE game_id = ? AND stid = ? AND op_type = ? AND op_fields = ?",
            $conf['game_id'], $conf['stid'], $conf['op_type'], $conf['op_fields']);
        $this->addSortedConf(sprintf('%s:%s:%s:%s:%s',
            $conf['type'], $conf['game_id'], $conf['stid'], $conf['op_type'], $conf['op_fields']), $conf);
    }
}
