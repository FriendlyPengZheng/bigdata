<?php
class common_DataInfo extends TMFormModel
{
    /**
     * 标志状态，已检测过
     */
    const STATUS_CHECKED = 1;

    /**
     * 标志状态，未检测
     */
    const STATUS_NOT_CHECKED = 0;

    /**
     * 隐藏标志，未隐藏
     */
    const HIDE_NOT_HIDE = 0;

    /**
     * 隐藏标志，已隐藏
     */
    const HIDE_TO_HIDE = 1;

    /**
     * 隐藏标志，自动隐藏
     */
    const HIDE_AUTO_HIDE = 2;

    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_data_info';
    }

    /**
     * @brief attributes 
     * 重写参数，用于SELECT时，重新组合sthash, range_name
     *
     * @return array
     */
    public function attributes()
    {
        return array(
            'data_id', 'data_name', '(sthash mod 10000) as sthash', 'range',
            'r_id', 'add_time', 'IF(range_name = "", range, range_name) as range_name'
        );
    }

    /**
     * Get data range info by rid, type.
     * @param  array $info
     * @return array
     */
    public function getRangeByRid($info)
    {
        return $this->getDb()->createCommand()
            ->select(implode(',', $this->attributes()) . ',data_id AS id,data_name AS name')
            ->from($this->tableName())
            ->where('hide = ? AND r_id = ? AND type = ?')
            ->order('display_order ASC')
            ->queryAll(array(self::HIDE_NOT_HIDE, $info['r_id'], $info['type']));
    }

    public $aUniqueKey = array();
    public $aSqlStatement = array();
    public $aSqlParameter = array();
    public $select;

    /**
     * @brief getDataList 
     * 根据参数获取配置列表
     *
     * @param array $aConfigureInfo
     * example:
     *  array(
     *      array('data_id' => 1212),
     *      array('r_id' => 12, 'type' => 'result')
     *      array('r_id' => 12, 'type' => 'result', 'range' => 7)
     *  )
     *
     * @return array
     */
    public function getDataList($aConfigureInfo)
    {
        $this->select =  implode(',', $this->attributes());
        foreach ($aConfigureInfo as $configure) {
            if (isset($configure['data_id'])) {
                $this->_dataId($configure);
            } elseif (isset($configure['range'])) {
                $this->_rIdTypeRange($configure);
            } else {
                $this->_rIdType($configure);
            }
        }
        if (empty($this->aSqlStatement)) return array();
        return $this->getDb()->createCommand('(' . implode(') UNION ALL (', $this->aSqlStatement) . ')')
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->queryAll($this->aSqlParameter);
    }

    /**
     * @brief _dataId 
     * 根据配置拼接data_id类型的SQL
     *
     * @param array $configure
     * example:
     *  array('data_id' => 1212)
     */
    private function _dataId($configure)
    {
        $this->aSqlStatement[] = 'SELECT data_id AS id,' . $this->select .
            ' FROM ' . $this->tableName() .
            ' WHERE hide = ? AND data_id = ?';
        $this->aSqlParameter[] = self::HIDE_NOT_HIDE;
        $this->aSqlParameter[] = $configure['data_id'];
        $this->aUniqueKey[$configure['data_id']] = $configure;
    }

    /**
     * @brief _rIdType 
     * 根据配置拼接r_id, type类型的SQL
     *
     * @param array $configure
     * example:
     *  array('r_id' => 12, 'type' => 'result')
     */
    private function _rIdType($configure)
    {
        $this->aSqlStatement[] = 'SELECT CONCAT_WS(":", r_id, type) AS id,' . $this->select .
            ' FROM ' . $this->tableName() .
            ' WHERE hide = ? AND r_id = ? AND type = ?' .
            ' ORDER BY display_order ASC';
        $this->aSqlParameter[] = self::HIDE_NOT_HIDE;
        $this->aSqlParameter[] = $configure['r_id'];
        $this->aSqlParameter[] = $configure['type'];
        $this->aUniqueKey[$configure['r_id'] . ':' . $configure['type']] = $configure;
    }

    /**
     * @brief _rIdTypeRange 
     * 根据配置拼接r_id, type, range类型的SQL
     *
     * @param array $configure
     * example:
     *  array('r_id' => 12, 'type' => 'result', 'range' => 7)
     */
    private function _rIdTypeRange($configure)
    {
        $this->aSqlStatement[] = 'SELECT CONCAT_WS(":", r_id, type, range) AS id,' . $this->select .
            ' FROM ' . $this->tableName() .
            ' WHERE hide = ? AND r_id = ? AND type = ? AND range = ?';
        $this->aSqlParameter[] = self::HIDE_NOT_HIDE;
        $this->aSqlParameter[] = $configure['r_id'];
        $this->aSqlParameter[] = $configure['type'];
        $this->aSqlParameter[] = $configure['range'];
        $this->aUniqueKey[$configure['r_id'] . ':' . $configure['type'] . ':' . $configure['range']] = $configure;
    }
}
