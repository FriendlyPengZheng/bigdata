<?php
abstract class gameanalysis_Mission extends gameanalysis_Analysis
{
    public function actions()
    {
        return array_merge(parent::actions(), array(
            'getNewMissionTop' => array('top' => 10),
            'getMissionList' => array(),
            'getMissionDetail' => array('sstid' => null, 'rate' => 0),
            'gametask' => array(),
            'getGametaskList' => array('type' => null),
            'setName' => array('type' => null, 'id' => null, 'name' => ''),
            'setHide' => array('type' => null, 'id' => null, 'hide' => 0),
            'setHideAll' => array('type' => null, 'hide' => 0)
        ));
    }

    public function commonParameters()
    {
        return array_merge(parent::commonParameters(), array('mission_type' => null, 'gpzs_id' => null));
    }

    public function getNewMissionTop($aUserParameters)
    {
        $model = new gameanalysis_MissionData();
        $period = $model->getPeriod($aUserParameters);
        if ($aUserParameters['export']) {
            $this->exportMissionTrans($aUserParameters, $period);
        }

        if (!$period) $this->ajax(0);
        $aTransData = $model->getMissionTrans($aUserParameters, $period);
        if (!$aTransData) $this->ajax(0);
        $this->ajax(0, array($aTransData));
    }

    protected function exportMissionTrans($aUserParameters, $period)
    {
        $this->initExporter($aUserParameters);
        $this->oExporter->add($this->sFilename);
        $model = new gameanalysis_MissionData();
        $aTransData = $model->getMissionTrans($aUserParameters, $period);
        if (!$aTransData) {
            $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
        }

        $aMissions = $model->getMissionIds();
        $aDetailData = $model->getMissionDetailData(
            $aUserParameters, $aMissions, $period, gameanalysis_MissionData::MASK_DONE_UCOUNT);
        $aTitle = $aDetailData[key($aDetailData)]['key'];
        array_push($aTitle, TM::t('tongji', '步骤间转换率'), TM::t('tongji', '累计转换率'),
            TM::t('tongji', '完成人数'), TM::t('tongji', '任务名称'), TM::t('tongji', '任务ID'));
        $this->oExporter->put(array_reverse($aTitle), CsvExporter::ENCODE_PREV, 5);
        $iIdx = 0;
        foreach ($aDetailData as $missionId => $value) {
            $aLine = $value['data'][0]['data'];
            array_push($aLine, $aTransData['data'][0]['specialper'][$iIdx], $aTransData['data'][0]['percentage'][$iIdx],
                $aTransData['data'][0]['data'][$iIdx], $aTransData['key'][$iIdx], $missionId);
            $this->oExporter->put(array_reverse($aLine), CsvExporter::ENCODE_PREV, 2);
            $iIdx++;
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    public function getMissionList($aUserParameters)
    {
        $model = new gameanalysis_MissionData();
        $period = $model->getPeriod($aUserParameters);
        if ($aUserParameters['export']) {
            $this->exportMissionList($aUserParameters, $period);
        }

        if (!$period) $this->ajax(0);
        $aList = $model->getMissionList($aUserParameters, array($aUserParameters['mission_type']), $period);
        if (!$aList) $this->ajax(0);
        $this->ajax(0, reset($aList));
    }

    protected function exportMissionList($aUserParameters, $period)
    {
        $this->initExporter($aUserParameters);
        $this->oExporter->add($this->sFilename);
        $model = new gameanalysis_MissionData();
        $aType = array('main' => '主线任务', 'new' => '新手任务', 'aux' => '支线任务');
        $aData = $model->getMissionList($aUserParameters, array_keys($aType), $period);
        if (!$aData) {
            $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
        }

        $aTitle = array('任务ID', '任务名称');
        $aConf = array('_getucount' => '接取人数', '_doneucount' => '完成人数');
        foreach ($aType as $key => $title) {
            if (isset($aData[$key])) {
                $this->oExporter->putWithTitle($title);
                $missions = array();
                foreach ($aData[$key] as $data) {
                    $missions[] = $data['sstid'];
                }
                $aUserParameters['mission_type'] = $key;
                $aDetailData = $model->getMissionDetailData(
                    $aUserParameters,
                    $missions,
                    $period,
                    gameanalysis_MissionData::MASK_UCOUNT
                );
                $date = array_reverse($aDetailData[key($aDetailData)]['key']);
                $i = 0;
                foreach ($aConf as $dataKey => $conf) {
                    $head = array_merge($aTitle, array($conf), $date);
                    $this->oExporter->put($head, CsvExporter::ENCODE_PREV, 3);
                    foreach ($missions as $j => $mission) {
                        $line = $aDetailData[$mission]['data'][$i]['data'];
                        array_push($line, $aData[$key][$j][$dataKey], $aData[$key][$j]['gametask_name'], $mission);
                        $this->oExporter->put(array_reverse($line), CsvExporter::ENCODE_PREV, 2);
                    }
                    $this->oExporter->put(); // new line
                    ++$i;
                }
                $this->oExporter->put(); // new line
            }
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    public function getMissionDetail($aUserParameters)
    {
        $model = new gameanalysis_MissionData();
        $period = $model->getPeriod($aUserParameters);

        if (!$period) $this->ajax(0);
        $aDetail = $model->getMissionDetailData(
            $aUserParameters,
            array($aUserParameters['sstid']),
            $period,
            $aUserParameters['rate'] ? gameanalysis_MissionData::MASK_RATE : gameanalysis_MissionData::MASK_UCOUNT
        );
        if (!$aDetail) $this->ajax(0);
        $this->ajax(0, array_values($aDetail));
    }

    // ------ for management ----

    /**
     * 游戏任务管理
     */
    public function gametask($aUserParameters)
    {
        $this->assignIgnore($aUserParameters);
        $this->display('gameanalysis/gametaskmanage.html');
    }

    /**
     * 游戏任务数据
     */
    public function index($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/listtable.html');
    }

    /**
     * 游戏任务列表
     */
    public function getGametaskList($aUserParameters)
    {
        $model = new gameanalysis_Gametask();
        $this->ajax(0, $model->getList($aUserParameters));
    }

    /**
     * 设置游戏任务名称
     */
    public function setName($aUserParameters)
    {
        $model = new gameanalysis_Gametask();
        $this->ajax(0, $model->setName($aUserParameters));
    }

    /**
     * 设置游戏任务是否隐藏
     */
    public function setHide($aUserParameters)
    {
        $model = new gameanalysis_Gametask();
        $this->ajax(0, $model->setHide($aUserParameters));
    }

    /**
     * 隐藏或者显示某一类型的所有游戏任务
     */
    public function setHideAll($aUserParameters)
    {
        $model = new gameanalysis_Gametask();
        $this->ajax(0, $model->setHideAll($aUserParameters));
    }
}
