<?php
class data_configuration_Data extends TMModel
{
    protected $statList;

    protected $dataList;

    protected $gameId;

    protected $dataConfigure;

    protected $bySstid;

    public function getDataList()
    {
        // Hit the cache.
        if (isset($this->dataList)) return $this->dataList;

        // get data configure by r_id/type or data_id
        $dataInfo = new common_DataInfo();
        $this->dataList = $dataInfo->getDataList($this->statList);
        if (!$this->bySstid) $this->dataConfigure = $dataInfo->aUniqueKey;
        return $this->dataList;
    }

    public function getDataConfigure()
    {
        return $this->dataConfigure;
    }

    /**
     * @brief statList 
     * get r_id/type by stid/sstid/op_type/op_fields
     * @return this
     */
    public function statList()
    {
        if ($this->bySstid) {
            $statList = array();
            $this->dataConfigure = array();
            $model = new common_Report();
            foreach ($this->statList as $info) {
                $info['game_id'] = $this->gameId;
                foreach ($model->getSstid($info) as $stat) {
                    $statList[] = $stat;
                    $key = $stat['r_id'] . ':' . $stat['type'];
                    !isset($this->dataConfigure[$key]) && ($this->dataConfigure[$key] = array());
                    $stat['data_name'] = $stat['name'];
                    $this->dataConfigure[$key] = array_merge($stat, $info);
                }
            }
            $this->statList = $statList;
        } else {
            $this->statList = common_Stat::getStatByUk($this->gameId, $this->statList);
        }
        return $this;
    }

    public function setGameId($gameId)
    {
        $this->gameId = $gameId;
        return $this;
    }

    /**
     * @brief getGameId 
     *
     * @return {interval}
     */
    public function getGameId()
    {
        return $this->gameId;
    }

    public function setStatList($statList)
    {
        $this->statList = $statList;
        return $this;
    }

    public function getStatList()
    {
        return $this->statList;
    }

    public function setBySstid($bySstid)
    {
        $this->bySstid = $bySstid;
        return $this;
    }

    public function getBySstid()
    {
        return $this->bySstid;
    }
}
