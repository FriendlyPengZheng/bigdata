<?php
class common_GpzsInfo extends TMFormModel
{
    /**
     * 获取数据表的名称
     * @return string
     */
    public function tableName()
    {
        return 't_gpzs_info';
    }

    /**
     * 获取参数检测的配置
     * @return array
     */
    public function rules()
    {
        return array(
            array('game_id, platform_id', 'exist')
        );
    }

    /**
     * 获取平台列表
     * @return array
     */
    public function getPlatform()
    {
        $this->validate(array('game_id'));
        $condition = array(
            'game_id' => $this->game_id,
            'zone_id' => -1,
            'server_id' => -1
        );
        // status = -1 取出所有记录
        if (!isset($this->status)) {
            $condition['status'] = 0;
        } elseif ($this->status != -1) {
            $condition['status'] = $this->status;
        }
        if ($this->platform_id) {
            $condition['platform_id'] = $this->platform_id;
        }
        return $this->findAll(array('condition' => $condition));
    }

    /**
     * 获取区服列表
     * @return array
     */
    public function getZoneServer()
    {
        $this->validate(array('game_id', 'platform_id'));
        $condition = array(
            'game_id' => $this->game_id,
            'platform_id' => $this->platform_id
        );
        // status = -1 取出所有记录
        if (!isset($this->status)) {
            $condition['status'] = 0;
        } elseif ($this->status != -1) {
            $condition['status'] = $this->status;
        }
        return $this->findAll(array('condition' => $condition));
    }

    /**
     * 获取Gpzs列表
     * @return array
     */
    public function getList()
    {
        $this->validate(array('game_id'));
        $condition = array(
            'status' => 0,
            'game_id' => $this->game_id
        );
        if (isset($this->platform_id)) {
            $condition['platform_id'] = $this->platform_id;
        }
        if (isset($this->server_id)) {
            $condition['server_id'] = $this->server_id;
        }
        if (isset($this->zone_id)) {
            $condition['zone_id'] = $this->zone_id;
        }
        return $this->findAll(array('condition' => $condition, 'order' => 'gpzs_id ASC'));
    }

    /**
     * Get gpzs info by gpzs_id.
     * @param  array|integer $gpzsId
     * @return array
     */
    public function getGpzsInfoById($gpzsId)
    {
        $aQueryPlaceHolder = array();
        $aQueryParameters = array();
        $gpzsId = (array)$gpzsId;
        foreach ($gpzsId as $id) {
            $aQueryPlaceHolder[] = '?';
            $aQueryParameters[]  = $id;
        }
        return $this->getDb()->createCommand()
            ->select($this->attributes())
            ->from($this->tableName())
            ->where('status = 0 AND gpzs_id IN(' . implode(',', $aQueryPlaceHolder) .')')
            ->queryAll($aQueryParameters);
    }

    /**
     * 单平台 or not
     * @return boolean
     */
    public function hasSinglePlatform()
    {
        return count($this->getPlatform()) === 1;
    }
}
