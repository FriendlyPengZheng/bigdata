<?php
class garbled_DataOnceCheck extends TMModel
{
    /**
     * @brief common_DataGarbled
     */
    protected $dataGarbled;

    /**
     * @brief common_DataInfo
     */
    protected $dataInfo;

    /**
     * @brief array
     * 废弃的列表
     */
    protected $garbledItems = array();

    /**
     * @brief array
     * 游戏ID=>Gpzs ID
     */
    protected $gameGpzs = array();

    /**
     * @brief array
     * 排除的report ID列表
     */
    protected $excludeReports = array();

    /**
     * @brief getGarbled
     * 获取本次检测处的乱码
     *
     * @return {array}
     */
    public function getGarbled()
    {
        return $this->garbledItems;
    }

    /**
     * @brief check
     * 检测未检测的树节点名称，将乱码标志hide改为self::HIDE_AUTO_HIDE，并记入log
     *
     * @return {TreeEncodeCheck}
     */
    public function check($arguments)
    {
        // 检测时候有对应的全区全服的Gpzs ID，没有的话返回
        if (!($gameGpzs = TMArrayHelper::assoc($arguments['game_id'], $this->getGameGpzs(), ''))) {
            return $this;
        }

        $arguments['fill_null'] = 1;
        $arguments['gpzs_id'] = $gameGpzs;

        $endTime = date('Y-m-d H:i:s', (strtotime($arguments['to']) - 172800));

        $excludeReportsList = $this->getExcludeReport();
        $excludeReports = implode(',', $excludeReportsList);

        $oData = new data_Data();
        $iPageIndex = 0;

        // 按report分批
        while (($aReportList = $this->getReportList($arguments['game_id'], $excludeReports, $iPageIndex))) {
            $aReportIds = array_keys($aReportList);
            if (($aDataList = $this->getDataList($aReportIds, $endTime))) {
                $aChunkData = array_chunk($aDataList, 30);
                foreach ($aChunkData as $data) {
                    $arguments['data_info'] = $data;
                    $data = $oData->getTimeSeries($arguments);
                    foreach ($data[0]['data'] as $valueIndex => $values) {
                        $dataHide = common_DataInfo::HIDE_NOT_HIDE;
                        $this->checkEmptyValue($values['data']) &&
                            ($dataHide = $this->doWithEmpty($arguments['data_info'][$valueIndex], $aReportList[$arguments['data_info'][$valueIndex]['r_id']][0]));

                        // 修改data状态
                        $this->amendData($arguments['data_info'][$valueIndex]['data_id'], $dataHide);
                    }
                }
                // 检测到的乱码数据超过1000条，退出，下次再检测
                if (count($this->garbledItems) > 1000)  break;
            }
            $iPageIndex ++;
        }
        return $this;
    }

    /**
     * @brief amendData
     * 修改data状态
     *
     * @param {interval} $dataId
     * @param {interval} $dataHide
     */
    protected function amendData($dataId, $dataHide)
    {
        $oDataInfo = $this->getDataInfo();
        $oDataInfo->data_id = $dataId;
        $oDataInfo->hide = $dataHide;
        $oDataInfo->status = common_DataInfo::STATUS_CHECKED;
        $oDataInfo->update(array(
            'hide', 'status'
        ));
    }

    /**
     * @brief getDataGarbled
     * 缓存common_DataGarbled
     *
     * @return {common_DataGarbled}
     */
    protected function getDataGarbled()
    {
        if (!$this->dataGarbled) {
            $this->dataGarbled = new common_DataGarbled();
        }
        return $this->dataGarbled;
    }

    /**
     * @brief getDataInfo
     * 缓存common_DataInfo
     *
     * @return {common_DataInfo}
     */
    protected function getDataInfo()
    {
        if (!$this->dataInfo) {
            $this->dataInfo = new common_DataInfo();
        }
        return $this->dataInfo;
    }

    /**
     * @brief doWithEmpty
     * 处理空数据
     *
     * @param {array} $dataInfo
     * @param {array} $reportInfo
     *
     * @return {interval}
     */
    protected function doWithEmpty($dataInfo, $reportInfo)
    {
        // 记入数据库
        $oDataGarbled = $this->getDataGarbled();
        $oDataGarbled->attributes = array(
            'data_id' => $dataInfo['data_id'],
            'data_name' => $dataInfo['data_name'],
            'report_id' => $dataInfo['r_id'],
            'add_time' => $dataInfo['add_time'],
            'game_id' => $reportInfo['game_id'],
            'stid' => $reportInfo['stid'],
            'sstid' => $reportInfo['sstid'],
            'node_id' => $reportInfo['node_id']
        );
        // node-id为0，自动屏蔽
        $oDataGarbled->hide = common_DataInfo::HIDE_NOT_HIDE;
        if (!$reportInfo['node_id']) {
            $oDataGarbled->hide = common_DataInfo::HIDE_AUTO_HIDE;
        } else{
            try {
                // data-name为乱码，自动屏蔽
                TMEncode::isUTF8String($dataInfo['data_name']);
            } catch (TMEncodeException $e) {
                $oDataGarbled->hide = common_DataInfo::HIDE_AUTO_HIDE;
            }
            // 自动屏蔽的乱码项存入内存中，可用于其他操作
            if ($oDataGarbled->hide === common_DataInfo::HIDE_AUTO_HIDE) {
                $this->garbledItems[] = array(
                    $dataInfo['data_id'],
                    $dataInfo['data_name'],
                    $oDataGarbled->hide,
                    $reportInfo['stid'],
                    $reportInfo['sstid'],
                    $reportInfo['node_id']
                );
            }
        }
        $oDataGarbled->insert();

        return $oDataGarbled->hide;
    }

    /**
     * @brief checkEmptyValue
     * 检测空值
     *
     * @param array $values
     *
     * @return boolean
     */
    protected function checkEmptyValue($values)
    {
        return 1 === count(array_filter($values, 'strlen'));
    }

    /**
     * @brief getReportList
     * 获取指定游戏下指定段的report列表
     *
     * @param {interval} $iGameId
     * @param {array} $excludeIds
     * @param {interval} $page
     *
     * @return {array}
     */
    protected function getReportList($iGameId, $excludeIds, $page)
    {
        $report = new common_ReportInfo();
        return TM::app()->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select(implode(',', $report->attributes()))
            ->from($report->tableName())
            ->where('status = ?')
            ->andWhere('flag = ?')
            ->andWhere('game_id = ?')
            ->andWhere('stid NOT LIKE ?')
            ->andWhere('report_id NOT IN ( ' . $excludeIds . ' )')
            ->offset($page * 500)
            ->limit(500)
            ->queryAll(array(
                common_ReportInfo::STATUS_NOT_HIDE,
                common_ReportInfo::FLAG_NOT_CHECKED,
                $iGameId,
                '\_%'
            ));
    }

    /**
     * @brief getExcludeReport
     * 获取排除的report ID列表
     *
     * @return {array}
     */
    public function getExcludeReport()
    {
        return $this->excludeReports;
    }

    /**
     * @brief setExcludeReport
     * 设置排除的report ID列表
     *
     * @param {array} $reportIds
     *
     * @return {garbled_DataOnceCheck}
     */
    public function setExcludeReport($reportIds)
    {
        $this->excludeReports = $reportIds;
        return $this;
    }

    /**
     * @brief getDataList
     * 获取指定report id列表，指定时间前的data配置
     *
     * @param array $aReportIds
     * @param interval $endTime
     *
     * @return array
     */
    protected function getDataList($aReportIds, $endTime)
    {
        $oData = new common_DataInfo();
        return TM::app()->getDb()->createCommand()
            ->select(implode(',', $oData->attributes()))
            ->from($oData->tableName())
            ->where('status = ? AND hide = ? AND type = "report"')
            ->andWhere('r_id IN ( ' . implode(',', $aReportIds) . ')')
            ->andWhere('add_time <= ?')
            ->queryAll(array(
                common_DataInfo::STATUS_NOT_CHECKED,
                common_DataInfo::HIDE_NOT_HIDE,
                $endTime
            ));
    }

    /**
     * @brief getGameGpzs
     * 获取所有游戏ID=>Gpzs ID对应关系
     *
     * @return array
     */
    protected function getGameGpzs()
    {
        if ($this->gameGpzs) return $this->gameGpzs;
        $oGpzs = new common_GpzsInfo();
        return $this->gameGpzs = TM::app()->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_KEY_PAIR)
            ->select('game_id, gpzs_id')
            ->from($oGpzs->tableName())
            ->where('status = 0')
            ->queryAll();
    }
}
