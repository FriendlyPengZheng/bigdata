<?php
class gameanalysis_MissionData extends gameanalysis_Data
{
    /**
     * Masks for configuration.
     */
    const MASK_UCOUNT = 1;
    const MASK_DONE_UCOUNT = 2;
    const MASK_RATE = 4;

    /**
     * @var array mission types.
     */
    private $_types = array('new' => true, 'main' => true, 'aux' => true, 'etc' => true);

    /**
     * @var array Mission ids.
     */
    private $_missionIds = array();

    public function tableName()
    {
        return 'v_gametask_data';
    }

    public function filterType($sType)
    {
        if (isset($this->_types[$sType])) {
            return $sType;
        }
        return key($this->_types);
    }

    public function addMissionId($missionId)
    {
        $this->_missionIds[] = $missionId;
    }

    public function getMissionIds()
    {
        return $this->_missionIds;
    }

    public function getConfiguration($mask = null)
    {
        $aConf = array(
            array('name' => TM::t('tongji', '接取人数'), 'field' => 'getucount', 'slot' => 1),
            array('name' => TM::t('tongji', '完成人数'), 'field' => 'doneucount', 'slot' => 3),
            array('name' => TM::t('tongji', '放弃人数'), 'field' => 'abrtucount', 'slot' => 1),
            array('name' => TM::t('tongji', '接取率'), 'field' => 'acceptpercent', 'slot' => 4),
            array('name' => TM::t('tongji', '完成率'), 'field' => 'donepercent', 'slot' => 4)
        );
        if ($mask === null) return $aConf;

        foreach ($aConf as $idx => $conf) {
            if (($conf['slot'] & $mask) === 0) {
                unset($aConf[$idx]);
            }
        }
        return array_values($aConf);
    }

    public function getMissionTrans($aUserParam, data_time_Period $period)
    {
        $aUserParam['mission_type'] = $this->filterType($aUserParam['mission_type']);
        $aList = $this->getMissionList($aUserParam, array($aUserParam['mission_type']), $period);
        if (!$aList) return;
        $aList = reset($aList);

        $aData = array('key'  => array(), 'data' => array(array(
            'name' => TM::t('tongji', '新手任务转化率'),
            'index' => array(),
            'data' => array(),
            'percentage' => array(), // 累计转化率
            'specialper' => array()  // 步骤间转化率
        )));
        $first = 0;
        foreach ($aList as $idx => $value) {
            $this->addMissionId($value['sstid']);
            $aData['key'][] = $value['gametask_name'];
            $aData['data'][0]['index'][] = $idx + 1;
            $aData['data'][0]['data'][] = $value['_doneucount'];
            if ($idx === 0) {
                $first = $value['_doneucount'];
                $aData['data'][0]['specialper'][] = '0%';
            } else {
                $aData['data'][0]['specialper'][] = ($aList[$idx-1]['_doneucount'] ?
                    round(100*$value['_doneucount']/$aList[$idx-1]['_doneucount'], 2) : 0) . '%';
            }
            $aData['data'][0]['percentage'][] = ($first ? round(100*$value['_doneucount']/$first, 2) : 0) . '%';
        }
        return $aData;
    }

    public function getMissionList($aUserParam, $aType, data_time_Period $period)
    {
        $sql = 'SELECT type,sstid,gametask_name,SUM(getucount) AS _getucount,SUM(abrtucount) AS _abrtucount,' .
               'SUM(doneucount) AS _doneucount,CONCAT(ROUND(SUM(doneucount)/SUM(getucount)*100, 2), "%") AS _rate ' .
               'FROM ' . $this->tableName() . ' ' .
               'WHERE gpzs_id = ? AND time >= ? AND time <= ? AND type IN (';
        $param = array($aUserParam['gpzs_id'], $period->getFrom(), $period->getTo());
        foreach ($aType as $type) {
            $sql .= '?,';
            $param[] = $type;
        }
        $sql = rtrim($sql, ',') . ') GROUP BY type,sstid';
        if (isset($aUserParam['top'])) {
            $sql .= ' ORDER BY `order` DESC LIMIT ' . (int)$aUserParam['top'];
        }
        return $this->getDb()->createCommand($sql)
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->queryAll($param);
    }

    public function getMissionDetailData($aUserParam, $aMissions, data_time_Period $period, $mask = null)
    {
        $aMissionDetail = $this->getMissionDetail($aUserParam, $aMissions, $period);
        if (!$aMissionDetail) {
            return;
        }

        $aConf = $this->getConfiguration($mask);
        return $this->populate($aMissionDetail, $aMissions, $aConf, $period);
    }

    protected function getMissionDetail($aUserParam, $aMissions, data_time_Period $period)
    {
        if (!$aMissions) return;

        $sql = 'SELECT sstid,time AS _key,gametask_name,getucount,abrtucount,doneucount,CONCAT(ROUND(acceptpercent, 2), "%") AS acceptpercent,CONCAT(ROUND(donepercent, 2), "%") AS donepercent ' .
               'FROM ' . $this->tableName() . ' ' .
               'WHERE type = ? AND gpzs_id = ? AND time >= ? AND time <= ? AND sstid IN (';
        $param = array($aUserParam['mission_type'], $aUserParam['gpzs_id'], $period->getFrom(), $period->getTo());
        foreach ($aMissions as $missionId) {
            $sql .= '?,';
            $param[] = $missionId;
        }
        return $this->getDb()->createCommand(rtrim($sql, ',') . ')')
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->queryAll($param);
    }
}
