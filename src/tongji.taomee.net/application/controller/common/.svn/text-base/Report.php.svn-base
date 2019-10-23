<?php
class Report extends TMController
{
    public function actions()
    {
        return [
            'getList' => ['game_id' => null, 'stid' => null, 'op_fields' => null, 'op_type' => null],
            'setName' => ['id' => null, 'name' => null],
            'setStatus' => ['id' => null, 'game_id' => null, 'status' => 0],
            'getSstid' => ['game_id' => null, 'stid' => null, 'op_fields' => null, 'op_type' => null],
            'setSstidName' => ['id' => null, 'name' => null],
            'getListByGameId' => ['game_id' => null, 'is_basic' => 0],
            'getStidAll' => ['game_id' => null, 'is_basic' => 0],
            'getSstidByStid' => ['game_id' => null, 'is_basic' => 0, 'stid' => null],
			'getListByStidAndSstid' => ['game_id' => null, 'is_basic' => 0, 'stid' => null, 'sstid' => null],
			'getListByNodeid' => ['node_id' => null, 'is_basic'=> 0]
        ];
    }

    /**
     * 获取report信息，用于展示
     * @param array $aUserParameters
     */
    public function getList($aUserParameters)
    {
        $model = new common_Report();
        $this->ajax(0, $model->getSstid($aUserParameters));
    }

    /**
     * 设置名称
     * @param array $aUserParameters
     */
    public function setName($aUserParameters)
    {
        $model = new common_Report();
        $this->ajax(0, $model->setName($aUserParameters));
    }

    /**
     * Set report status, 0 display and 1 not
     */
    public function setStatus($aUserParameters)
    {
        $model = new common_Report();
        $model->game_id = $aUserParameters['game_id'];
        $aRIds = (array)$aUserParameters['id'];
        foreach ($aRIds as $rId) {
            $model->report_id = $rId;
            $model->status = $aUserParameters['status'];
            $model->update(array('status'));
        }
        $this->ajax(0);
    }

    /**
     * 获取sstid信息，用于修改
     * @param array $aUserParameters
     */
    public function getSstid($aUserParameters)
    {
        $model = new common_Report();
        $this->ajax(0, $model->getCommonSstid($aUserParameters));
    }

    /**
     * 设置名称
     * @param array $aUserParameters
     */
    public function setSstidName($aUserParameters)
    {
        $aUserParameters['id'] = array_filter(explode(',', $aUserParameters['id']));
        $model = new common_Report();
        $this->ajax(0, $model->setSstidName($aUserParameters));
    }

    /**
     * 通过game_id获取report
     * @param array $aUserParameters
     */
    public function getListByGameId($aUserParameters)
    {
        $this->ajax(0, (new common_Report())->getStatItemList(
            ['game_id' => (int)$aUserParameters['game_id']],
            ['is_basic' => $aUserParameters['is_basic']]
        ));
    }

    /**
     * Get one game's all stids.
     *
     * @param  array $aUserParameters
     * @return null
     */
    public function getStidAll($aUserParameters)
    {
        $this->ajax(0, (new common_Report())->getStatItemList([
                'game_id' => (int)$aUserParameters['game_id'],
                'op_type' => 'ucount',
                'status' => 0 // show
            ], [
                'is_basic' => $aUserParameters['is_basic'],
                'hide' => 0 // show
            ], null, 'r.stid', 'r.stid'));
    }

    /**
     * Get all sstids under the given stid.
     *
     * @param  array $aUserParameters
     * @return null
     */
    public function getSstidByStid($aUserParameters)
    {
        $this->ajax(0, (new common_Report())->getStatItemList([
                'game_id' => (int)$aUserParameters['game_id'],
                'stid' => $aUserParameters['stid'],
                'op_type' => 'ucount',
                'status' => 0 // show
            ], [
                'is_basic' => $aUserParameters['is_basic'],
                'hide' => 0 // show
            ], null, 'r.sstid', 'r.sstid'));
    }

    /**
     * Get all reports with the given stid and sstid.
     *
     * @param  array $aUserParameters
     * @return null
     */
    public function getListByStidAndSstid($aUserParameters)
    {
        $this->ajax(0, (new common_Report())->getStatItemList([
                'game_id' => (int)$aUserParameters['game_id'],
                'stid' => $aUserParameters['stid'],
                'sstid' => $aUserParameters['sstid'],
                'op_type' => 'ucount',
                'status' => 0 // show
            ], [
                'is_basic' => $aUserParameters['is_basic'],
                'hide' => 0 // show
            ], null, 'r.report_id', 'r.report_name AS r_name,("1") AS type,r.report_id AS r_id,r.is_multi'));
    }
    /** 
	 * Get all reports with the given node_id.
	 *
	 * @param array $aUserParameters
	 * @return null
	 */
	public function getListByNodeid($aUserParameters)
	{
		$this->ajax(0,(new common_Report())->getStatItemList([
			    'node_id'=>(int)$aUserParameters['node_id'],
			    'op_type'=>'ucount',
			    'status'=>0
			],[
			    'is_basic'=>$aUserParameters['is_basic'],
			    'hide'=>0
			],null,'r.report_id','r.report_name AS r_name,("1") AS type,r.report_id AS r_id,r.is_multi'));
	}
    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters)
    {
        // 检查游戏权限
        if (isset($aParameters['game_id'])) {
            (new common_Game())->checkGameAuth($aParameters['game_id']);
        }
    }
}
