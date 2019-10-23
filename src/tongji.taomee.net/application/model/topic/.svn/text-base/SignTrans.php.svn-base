<?php
class topic_SignTrans extends TMModel
{
    /* 转化率类型 */
    const RATE_TOTAL = 1;
    const RATE_STEP = 2;

    public function tableName()
    {
        if ($this->period == data_time_PeriodFactory::TYPE_DAY) {
            return 't_reg_trans';
        }
        return 't_reg_trans_hour';
    }

    public function rules()
    {
        return array(
            array('step', 'enum', 'range' => array('s2/s1', 's3/s1', 's4/s1', 's5/s1')),
            array('game_id', 'enum', 'range' => array_keys($this->getGameList()))
        );
    }

    public function attributeNames()
    {
        return array('game_id', 'step', 'average', 'period', 'rate_type');
    }

    public function getSummary($aUserParam)
    {
        $this->validate(array('step'));
        $aGameList = $this->getGameList();
        if ($this->game_id) {
            if (!isset($aGameList[$this->game_id])) return;
            $aGameList = array($this->game_id => TM::t('tongji', '转化率'));
        } else {
            unset($aGameList[1]); // 摩尔庄园步骤不一样，不加入汇总
        }
        if (!$this->checkTime($aUserParam)) return;
        $aData = array();
        foreach ($aUserParam['from'] as $key => $from) {
            $period = data_time_PeriodFactory::createPeriod($from, $aUserParam['to'][$key], $this->period);
            $aData[] = $this->populateByStep($this->getRateDataByStep($period, array_keys($aGameList)),
                                             $period, $aGameList);
        }
        return $aData;
    }

    /**
     * 获取各游戏转化率
     * @param data_time_Period $period
     * @param array $game
     * @return array
     */
    protected function getRateDataByStep($period, $game)
    {
        $timerange = 'time >= ? AND time <= ?';
        $oCommand = $this->getDb()->createCommand()->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select("gameid, time AS _key, CONCAT(ROUND({$this->step}*100, 2), '%') AS _value")
            ->from($this->tableName())
            ->where(array('IN', 'gameid', $game))
            ->andWhere($timerange);
        if ($this->period == data_time_PeriodFactory::TYPE_DAY) {
            return $oCommand->queryAll(array(date('Ymd', $period->getFrom()), date('Ymd', $period->getTo())));
        }
        return $oCommand->queryAll(array($period->getFrom(), $period->getTo()));
    }

    /**
     * 单步骤填充各游戏
     * @param array $data
     * @param data_time_Period $period
     * @param array $game
     * @return array
     */
    protected function populateByStep($data, $period, $game)
    {
        $aPoints = $this->getPeriodPoints($period, '0%');
        $aKeys = array_keys($aPoints['value']);
        if (!$aKeys) return;
        $aKeys[] = -1; // The end.
        $aData = array();
        foreach ($game as $key => $name) {
            $aEach = array('name' => $name, 'data' => $aPoints['value'], 'avg' => '0%');
            if (isset($data[$key])) {
                $i = 0;
                foreach ($data[$key] as $value) {
                    while ($value['_key'] >= $aKeys[$i+1] && $aKeys[$i+1] !== -1) {
                        $i++;
                    }
                    // $value['_value'] may be null, e.g. 0/0
                    $aEach['data'][$aKeys[$i]] = isset($value['_value']) ? $value['_value'] : '0%';
                }
                $aEach['avg'] = round(array_sum($aEach['data'])/($i+1), 2) . '%';
            }
            $aEach['data'] = array_values($aEach['data']);
            $aData[] = $aEach;
        }
        return array(
            'key' => $aPoints['key'],
            'data' => $aData,
            'pointStart' => $period->getFrom(),
            'pointInterval' => $period->getInterval()
        );
    }

    public function getTransRateByGame($aUserParam)
    {
        $this->validate(array('game_id'));
        if (!$this->checkTime($aUserParam)) return;
        $aData = array();
        foreach ($aUserParam['from'] as $key => $from) {
            $period = data_time_PeriodFactory::createPeriod($from, $aUserParam['to'][$key], $this->period);
            $aData[] = $this->populate($this->getRateDataByGame($period), $period, '0%', true);
        }
        return $aData;
    }

    /**
     * 检查时间参数
     * @param array $aUserParam
     * @return boolean
     */
    protected function checkTime(&$aUserParam)
    {
        $aUserParam['from'] = (array)$aUserParam['from'];
        $aUserParam['to'] = (array)$aUserParam['to'];
        if (count($aUserParam['from']) !== count($aUserParam['to'])) return false;
        return true;
    }

    /**
     * 根据类型获取不同转化率类型的 SQL
     * @return string
     */
    protected function getTransRateSQL()
    {
        switch ($this->rate_type) {
            case self::RATE_STEP:
                return
                    'CONCAT(ROUND(s2/s1*100, 2), "%") AS s2, ' .
                    'CONCAT(ROUND(s3/s2*100, 2), "%") AS s3, ' .
                    'CONCAT(ROUND(s4/s3*100, 2), "%") AS s4, ' .
                    'CONCAT(ROUND(s5/s4*100, 2), "%") AS s5';
                break;

            case self::RATE_TOTAL:
            default:
                return '"100%" AS s1, ' .
                    'CONCAT(ROUND(s2/s1*100, 2), "%") AS s2, ' .
                    'CONCAT(ROUND(s3/s1*100, 2), "%") AS s3, ' .
                    'CONCAT(ROUND(s4/s1*100, 2), "%") AS s4, ' .
                    'CONCAT(ROUND(s5/s1*100, 2), "%") AS s5';
                break;
        }
    }

    /**
     * 获取转化步骤
     * @return array
     */
    public function getTransStep()
    {
        $aStep = array('s1' => '注册米米号', 's2' => '验证密码');
        if ($this->game_id == 1) {
            $aStep += array('s3' => '登录online', 's4' => '落活跃统计', 's5' => '创建角色');
        } else {
            $aStep += array('s3' => '创建角色', 's4' => '登录online', 's5' => '落活跃统计');
        }
        // 步骤间转化率没有第一步
        if ($this->rate_type == self::RATE_STEP) unset($aStep['s1']);
        return $aStep;
    }

    /**
     * 获取转化率数据
     * @param data_time_Period $period
     * @return array
     */
    protected function getRateDataByGame($period)
    {
        $timerange = "time >= ? AND time <= ?";
        
        $oCommand = $this->getDb()->createCommand()
            ->select('time AS _key, ' . $this->getTransRateSQL())
            ->from($this->tableName())
            ->where('gameid = ? AND ' . $timerange);
        if ($this->period == data_time_PeriodFactory::TYPE_DAY) {
            return $oCommand->queryAll(array($this->game_id, date('Ymd', $period->getFrom()), date('Ymd', $period->getTo())));
        }
        return $oCommand->queryAll(array($this->game_id, $period->getFrom(), $period->getTo()));
    }

    /**
     * 填充数据
     * @param array $data
     * @param data_time_Period $period
     * @param $default 默认填充值
     * @param $average 是否求平均值
     * @return null|array
     */
    protected function populate($data, $period, $default, $average = false)
    {
        $aPoints = $this->getPeriodPoints($period, $default);
        $aKeys = array_keys($aPoints['value']);
        if (!$aKeys) return;
        $aKeys[] = -1; // The end.
        $aData = array();
        $aStep = $this->getTransStep();
        foreach ($aStep as $stepCode => $stepName) {
            $aData[$stepCode] = array('name' => $stepName, 'data' => $aPoints['value']);
        }
        $i = 0;
        foreach ($data as $value) {
            while ($value['_key'] >= $aKeys[$i+1] && $aKeys[$i+1] !== -1) {
                $i++;
            }
            foreach ($aStep as $stepCode => $stepName) {
                $aData[$stepCode]['data'][$aKeys[$i]] = isset($value[$stepCode]) ? $value[$stepCode] : '0%';
            }
        }
        foreach ($aStep as $stepCode => $stepName) {
            $aData[$stepCode]['data'] = array_values($aData[$stepCode]['data']);
            if ($average) $aData[$stepCode]['avg'] = round(array_sum($aData[$stepCode]['data'])/($i+1), 2) . '%';
        }
        return array(
            'key' => $aPoints['key'],
            'data' => array_values($aData),
            'pointStart' => $period->getFrom(),
            'pointInterval' => $period->getInterval()
        );
    }

    /**
     * 获取时间点
     * @param data_time_Period $period
     * @param string|integer $defalutValue
     * @return array
     */
    protected function getPeriodPoints($period, $defalutValue = 0)
    {
        if ($this->period == data_time_PeriodFactory::TYPE_DAY) {
            $aPoints = array('key' => array(), 'value' => array());
            $iTime = $period->getFrom();
            $iNow = time();
            while ($iTime <= $period->getTo()) {
                $aPoints['key'][] = date('Y-m-d', $iTime);
                $aPoints['value'][date('Ymd', $iTime)] = $defalutValue;
                $iTime += 86400;
            }
            return $aPoints;
        }
        return $period->getPoints(false, $defalutValue, -7200);    // set last 2 hours as offset
    }

    /**
     * 获取游戏列表
     * @return array
     */
    public function getGameList()
    {
        return TMArrayHelper::column((new common_Game())->findByFuncMaskWithGpzs(common_Game::SIGNTRANS_MASK), 'game_name', 'game_id');
    }
}
