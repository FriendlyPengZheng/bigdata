<?php
class data_configuration_DataInfo extends data_configuration_SortedUnion
{
    public function getInfo($confs)
    {
        if ($this->getCache() !== null) {
            return $this->getCache();
        }
        foreach ($confs as $conf) {
            if (isset($conf['data_id'])) {
                $this->addDataId($conf);
            } elseif (isset($conf['range'])) {
                $this->addRidWithRange($conf);
            } else {
                $this->addRid($conf);
            }
        }
        $info = $this->union();
        $this->setCache($info);
        return $info;
    }

    protected function addDataId($conf)
    {
        $this->addUnion(
            "SELECT data_id AS _uuid, " .
                "data_id, data_name, (sthash MOD 10000) AS sthash, range, " .
                "IF(range_name = '', range, range_name) AS range_name " .
            "FROM t_data_info " .
            "WHERE hide = 0 AND data_id = ?",
            $conf['data_id']);
        $this->addSortedConf($conf['data_id'], $conf);
    }

    protected function addRid($conf)
    {
        $this->addUnion(
            "SELECT CONCAT_WS(':', type, r_id) AS _uuid, " .
                "data_id, data_name, (sthash MOD 10000) AS sthash, range, " .
                "IF(range_name = '', range, range_name) AS range_name " .
            "FROM t_data_info " .
            "WHERE hide = 0 AND type = ? AND r_id = ?",
            $conf['type'], $conf['r_id']);
        $this->addSortedConf(sprintf('%s:%s', $conf['type'], $conf['r_id']), $conf);
    }

    protected function addRidWithRange($conf)
    {
        $this->addUnion(
            "SELECT CONCAT_WS(':', type, r_id, range) AS _uuid, " .
                "data_id, data_name, (sthash MOD 10000) AS sthash, range, " .
                "IF(range_name = '', range, range_name) AS range_name " .
            "FROM t_data_info " .
            "WHERE hide = 0 AND type = ? AND r_id = ? AND range = ?",
            $conf['type'], $conf['r_id'], $conf['range']);
        $this->addSortedConf(sprintf('%s:%s:%s', $conf['type'], $conf['r_id'], $conf['range']), $conf);
    }
}
