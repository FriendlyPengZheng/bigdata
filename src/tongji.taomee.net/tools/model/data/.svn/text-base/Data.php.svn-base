<?php
class data_Data extends TMModel
{
    /**
     * 分布相关属性
     */
    private $_distrConfiguration = array();
    private $_distrTimeSeries = array();

    /**
     * @var data_configuration_ConfigurationSet
     */
    protected $configurationSet;

    /**
     * 用户参数，时间序列中用于存放处理后的用户参数
     */
    protected $userParam = array();
    protected $gameId = 0;
    protected $gpzsId = null;

    /**
     * 静态方法，返回实例
     */
    public static function model()
    {
        return new self();
    }

    protected function addUserParam($userParam)
    {
        $return = false;
        if ($this->gameId && $this->gameId != $userParam['game_id'] ||
                $this->gpzsId && $this->gpzsId != $userParam['gpzs_id']) {
            $return = array(
                'data_info' => $this->userParam,
                'game_id' => $this->gameId,
                'gpzs_id' => $this->gpzsId
            );
            $this->userParam = array();
        }
        $this->userParam[] = $userParam;
        $this->gameId = $userParam['game_id'];
        $this->gpzsId = $userParam['gpzs_id'];
        return $return;
    }

    /**
     * 将用户参数依次加入configuration
     *
     * @param $userParam
     *          data_info: 用户data_info参数列表
     *          game_id:   游戏ID
     *          gpzs_id:   gpzs ID
     * @param $byItem
     */
    protected function addUserParamToConfiguration($userParam, $byItem)
    {
        $gpzsModel = new common_GpzsInfo();
        $gpzsConf = $gpzsModel->getGpzsInfoById($userParam['gpzs_id']);
        $dataConfModel = new data_configuration_Data();
        $dataConfModel->setGameId($userParam['game_id'])->setStatList($userParam['data_info']);
        $byItem && $dataConfModel->statList();
        $this->addConfiguration($dataConfModel, $gpzsConf);
    }

    /**
     * 获取时间数据
     *
     * @param array $aUserParam
     */
    public function getTimeSeries($aUserParam)
    {
        if (isset($aUserParam['by_data_expr']) && $aUserParam['by_data_expr']) {
            unset($aUserParam['by_data_expr']);
            $aUserParam['by_item'] = 0;
            return $this->getDataExprTimeSeries($aUserParam);
        }

        $gpzsCount = 1;
        if (is_array($aUserParam['gpzs_id'])) $gpzsCount = count($aUserParam['gpzs_id']);

        $this->configurationSet = new data_configuration_ConfigurationSet();
        $this->userParam = array();
        $this->gameId = 0;
        foreach ($aUserParam['data_info'] as $dataIdx => $dataParam) {
            $dataParam['game_id'] = TMArrayHelper::assoc('game_id', $dataParam, $aUserParam['game_id']);
            $dataParam['gpzs_id'] = TMArrayHelper::assoc('gpzs_id', $dataParam, $aUserParam['gpzs_id']);
            $dataParam['range'] = TMArrayHelper::assoc('range', $dataParam, $aUserParam, null);
            if (($userParam = $this->addUserParam($dataParam))) {
                $this->addUserParamToConfiguration($userParam, $aUserParam['by_item']);
            }
        }
        if (($userParam = $this->addUserParam(array('game_id' => 0, 'gpzs_id' => 0)))) {
            $this->addUserParamToConfiguration($userParam, $aUserParam['by_item']);
        }

        $periodSet = $this->getPeriodSet($aUserParam);
        $aUserParam['gpzs_name'] = $gpzsCount > 1 ? true : false;
        $data = $this->timeSeries($this->configurationSet, $periodSet, $aUserParam);

        if (TMArrayHelper::assoc('by_exprs', $aUserParam, 1) && !empty($aUserParam['exprs'])) {
            $data = $this->calculate($aUserParam['exprs'], $data, $gpzsCount);
        }
        if ($aUserParam['contrast']) {
            $data = $this->contrast($aUserParam, $data);
        } elseif ($aUserParam['yoy'] || $aUserParam['qoq']) {
            $aUserParam['datecnt'] = is_array($aUserParam['from']) ? count($aUserParam['from']) : 1;
            $data = $this->rate($aUserParam, $data);
        }
        if ($aUserParam['average']) {
            $data = $this->average($data);
        }
        if (isset($aUserParam['sum']) && $aUserParam['sum']) {
            $data = $this->sum($data);
        }
        if ($aUserParam['rate2data'] && !$aUserParam['export']) {
            $data = $this->rate2data($aUserParam, $data);
        }
        return $data;
    }

    /**
     * 保存取数据所需的配置信息
     *
     * @param data_configuration_Data $dataConfiguration
     * @param array                   $gpzsConfigure
     */
    protected function addConfiguration($dataConfiguration, $gpzsConfigure)
    {
        if (!($this->configurationSet instanceof data_configuration_ConfigurationSet)) {
            $this->configurationSet = new data_configuration_ConfigurationSet();
        }
        $dataList = $dataConfiguration->getDataList();
        foreach ($gpzsConfigure as $gpzsInfo) {
            foreach ($dataConfiguration->getDataConfigure() as $key => $item) {
                if (isset($dataList[$key])) {
                    foreach ($dataList[$key] as $index => $dataInfo) {
                        $gpzsId = TMArrayHelper::assoc('gpzs_id', $dataInfo, $gpzsInfo['gpzs_id']);
                        $configuration =
                            new data_configuration_Configuration($dataInfo['data_id'], $dataInfo['sthash'], $gpzsId);
                        $configuration->setDataName(isset($item['data_name']) ?
                                (is_array($item['data_name']) && isset($item['data_name'][$index]) ?
                                    $item['data_name'][$index] : $item['data_name']) :
                                $dataInfo['data_name'])
                            ->setGpzsName($gpzsInfo['gpzs_name'])
                            ->setFactor(TMArrayHelper::assoc('factor', $item, 1))
                            ->setUnit(TMArrayHelper::assoc('unit', $item, null))
                            ->setPrecision(TMArrayHelper::assoc('precision', $item, 2));
                        $this->configurationSet->add($configuration);
                    }
                } else {
                    $configuration = new data_configuration_UndefinedConfiguration();
                    if (isset($item['data_name'])) $configuration->setDataName($item['data_name']);
                    $configuration->setGpzsName($gpzsInfo['gpzs_name']);
                    $this->configurationSet->add($configuration);
                }
            }
        }
        return $this->configurationSet;
    }

    /**
     * 返回取数据所需的时间配置信息
     *
     * @param  array                               $aUserParam
     * @return data_configuration_ConfigurationSet
     */
    protected function getPeriodSet($aUserParam)
    {
        $periodSet = new data_time_PeriodSet();
        $aUserParam['from'] = (array)$aUserParam['from'];
        $aUserParam['to']   = (array)$aUserParam['to'];
        if (count($aUserParam['from']) === count($aUserParam['to'])) {
            foreach ($aUserParam['from'] as $k => $from) {
                $period = data_time_PeriodFactory::createPeriod($from, $aUserParam['to'][$k], $aUserParam['period']);
                $periodSet->add($period);
                if ($aUserParam['yoy']) $periodSet->add($period->getYoy());
                if ($aUserParam['qoq']) $periodSet->add($period->getQoq());
            }
        }
        return $periodSet;
    }

    /**
     * 从数据库中获取时间序列的数据，并进行填充
     *
     * @param  data_configuration_ConfigurationSet $configurationSet
     * @param  data_time_PeriodSet                 $periodSet
     * @param  array                               $aUserParam
     * @return array
     */
    protected function timeSeries($configurationSet, $periodSet, $aUserParam)
    {
        $source = new data_source_DatabaseSource();
        $sourceData = $source->setConfigurationSet($configurationSet)->setPeriodSet($periodSet)->get($aUserParam);
        $populator = new data_populator_Populator();
        $data = array();
        foreach ($periodSet as $period) {
            $data[] = $populator->setConfigurationSet($configurationSet)
                ->setPeriod($period)->setSourceData($sourceData)->populate($aUserParam);
        }
        return $data;
    }

    /**
     * 计算
     *
     * @param  array $expressions
     * @param  array $multiDateData
     * @return array
     */
    protected function calculate($expressions, $multiDateData, $chunkCount = 1)
    {
        $calc = new data_calculator_Calculator();
        $calcData = array();
        foreach ($multiDateData as $key => $multiData) {
            $calcData[$key] = $multiData;
            $calcData[$key]['data'] = array();
            foreach (array_chunk($multiData['data'], count($multiData['data']) / $chunkCount) as $data) {
                $calcTmpData = $calc->setExpressions($expressions)->setOperandsData(array(
                    'key' => $multiData['key'],
                    'data' => $data
                ))->calculate();
                $calcData[$key]['data'] = array_merge($calcData[$key]['data'], $calcTmpData['data']);
            }
        }
        return $calcData;
    }

    /**
     * 对比
     *
     * @param  array $aUserParam
     * @param  array $calcData
     * @return array
     */
    public function contrast($aUserParam, $calcData)
    {
        $contrast = new data_calculator_Contrast();
        foreach ($calcData as $key => $data) {
            if ($key == 0) continue;
            foreach ($data['data'] as $i => $value) {
                // 基数据名称只改一次
                if ($key == 1) {
                    $calcData[0]['data'][$i]['name'] = sprintf('[%s~%s]%s',
                        $calcData[0]['key'][0], end($calcData[0]['key']), $calcData[0]['data'][$i]['name']);
                }
                $calcData[$key]['data'][$i]['name'] = sprintf('[%s~%s]%s',
                    $data['key'][0], end($data['key']), $calcData[$key]['data'][$i]['name']);
                $calcData[$key]['data'][$i]['contrast_rate'] = $contrast->setBaseData(
                    $calcData[0]['data'][$i]['data'])->contrast($value['data']);
            }
        }
        return $calcData;
    }

    /**
     * 同比环比
     *
     * @param  array $aUserParam
     * @param  array $calcData
     * @return array
     */
    protected function rate($aUserParam, $calcData)
    {
        $chunkData = array_chunk($calcData, count($calcData)/$aUserParam['datecnt']);
        $contrast = new data_calculator_Contrast();
        $data = array();
        foreach ($chunkData as $calcData) {
            $baseData = array_shift($calcData);
            $count = count($baseData['data']);
            for ($k=0; $k<$count; $k++) {
                $contrast->setBaseData($baseData['data'][$k]['data']);
                $calcTmp = array();
                foreach ($calcData as $contrastData) {
                    $calcTmp[] = $contrast->contrast($contrastData['data'][$k]['data']);
                }
                if ($aUserParam['qoq']) $baseData['data'][$k]['qoq'] = array_pop($calcTmp);
                if ($aUserParam['yoy']) $baseData['data'][$k]['yoy'] = array_pop($calcTmp);
            }
            $data[] = $baseData;
        }
        return $data;
    }

    /**
     * 求平均值
     *
     * @param  array $data
     * @return array
     */
    protected function average($data)
    {
        $average = new data_calculator_Average();
        foreach ($data as $key => $timeData) {
            $data[$key]['average'] = array(
                'key' => array(),
                'data' => array()
            );
            foreach ($timeData['data'] as $value) {
                $data[$key]['average']['key'][] = $value['name'];
                $data[$key]['average']['data'][] = $average->average($value['data']);
            }
        }
        return $data;
    }

    /**
     * 将yoy、qoq作为data返回
     *
     * @param  array $aUserParam
     * @param  array $data
     * @return array
     */
    protected function rate2data($aUserParam, $data)
    {
        if (!$aUserParam['qoq'] && !$aUserParam['yoy']) return $data;
        $formatted = array();
        foreach ($data as $key => $timeData) {
            $temp = array('key' => array(), 'data' => array());
            $cnt = count($timeData['key']);
            $i = 0;
            while ($i < $cnt) {
                $sub = array('data' => array(
                    'name' => TMArrayHelper::assoc('group_name', $aUserParam, TM::t('tongji', '指标'))));
                if ($aUserParam['qoq']) $sub['qoq'] = array('name' => TM::t('tongji', '环比增长率'));
                if ($aUserParam['yoy']) $sub['yoy'] = array('name' => TM::t('tongji', '同比增长率'));
                foreach ($timeData['data'] as $value) {
                    if (!$i) $temp['key'][] = $value['name'];
                    $sub['data']['data'][] = $value['data'][$i];
                    if (isset($sub['qoq'])) $sub['qoq']['data'][] = $value['qoq'][$i];
                    if (isset($sub['yoy'])) $sub['yoy']['data'][] = $value['yoy'][$i];
                }
                $temp['data'] = array_merge($temp['data'], array_values($sub));
                $i++;
            }
            $formatted[] = $temp;
        }
        return $formatted;
    }

    /**
     * 获取分布数据
     *
     * @param  array $aUserParam
     * @return array
     */
    public function getDistribution($aUserParam)
    {
        $oPeriodSet = $this->getPeriodSet($aUserParam);
        $this->createDistrConfiguration($aUserParam);
        $distributor = new data_calculator_Distributor();
        $aDistrData = array();
        foreach ($this->_distrConfiguration as $key => $distr) {
            if ($distr === null || $aUserParam['export'] && !$distr->isExport()) {
                $aDistrData[$key] = null;
                continue;
            }
            $source = $this->timeSeries($distr->getConfigurationSet(), $oPeriodSet, $aUserParam);
            if ($exprs = $distr->getExpressions()) {
                $source = $this->calculate($exprs, $source);
            }
            $data = array();
            $distributor->setRuler(); // 多段时间时，按第1段时间排序
            foreach ($source as $idx => $src) {
                $data[] = $distributor->setDistrConfiguration($distr)->setSourceData($src)->distribute();
                if (!$idx) $this->_distrTimeSeries[$key] = $distributor->getSourceData();
            }
            $aUserParam['datecnt'] = is_array($aUserParam['from']) ? count($aUserParam['from']) : 1;
            if ($aUserParam['contrast']) {
                $data = $this->contrast($aUserParam, $data);
            } elseif ($aUserParam['yoy'] || $aUserParam['qoq']) {
                $data = $this->rate($aUserParam, $data);
            }
            $distrData = array();
            foreach ($data as $idx => $each) {
                if ($aUserParam['datecnt'] > 1 && isset($aUserParam['from'][$idx], $aUserParam['to'][$idx])) {
                    $each['data'][0]['name'] =
                        '[' . $aUserParam['from'][$idx] . '~' . $aUserParam['to'][$idx] . ']' .
                        $each['data'][0]['name'];
                }
                if (!$idx) {
                    $distrData = $each;
                    continue;
                }
                $distrData['data'] = array_merge($distrData['data'], $each['data']);
            }
            if (!$aUserParam['export'] && $extend = $distr->getExtend()) {
                $distrData['data'] = array_merge($extend, $distrData['data']);
            }
            $aDistrData[$key] = $distrData;
        }
        return $aDistrData;
    }

    /**
     * 获取分布配置
     *
     * @return array
     */
    public function getDistrConfiguration()
    {
        return $this->_distrConfiguration;
    }

    /**
     * 获取分布时间序列
     *
     * @return array
     */
    public function getDistrTimeSeries()
    {
        return $this->_distrTimeSeries;
    }

    /**
     * 清空数组，重新填充
     */
    public function initDistribution()
    {
        $this->_distrConfiguration = $this->_distrTimeSeries = array();

        return $this;
    }

    /**
     * 生成分布配置
     *
     * @param array $aUserParam
     */
    protected function createDistrConfiguration($aUserParam)
    {
        foreach ($aUserParam['data_info'] as $distrInfo) {
            $distrInfo['game_id'] = TMArrayHelper::assoc('game_id', $distrInfo, $aUserParam['game_id']);
            $distrInfo['gpzs_id'] = TMArrayHelper::assoc('gpzs_id', $distrInfo, $aUserParam['gpzs_id']);
            $this->_distrConfiguration[] = data_configuration_DistrFactoryManager
                ::createDistrFactory($distrInfo['distr_by'])
                ->setProperties($distrInfo)->createDistr($aUserParam);
        }
    }

    /**
     * 时间维度的分布
     *
     * @param  array      $aUserParam
     * @return array|null
     */
    public function getTimeDistr($aUserParam)
    {
        $gpzsConfiguration = new common_GpzsInfo();
        $gpzsConfigure = $gpzsConfiguration->getGpzsInfoById($aUserParam['gpzs_id']);

        $dataConfiguration = new data_configuration_Data();
        $dataConfiguration->setGameId($aUserParam['game_id'])->setStatList($aUserParam['data_info']);
        $aUserParam['by_item'] && $dataConfiguration->statList();
        $this->addConfiguration($dataConfiguration, $gpzsConfigure);

        $aUserParam['from'] = (array)$aUserParam['from'];
        $aUserParam['to']   = (array)$aUserParam['to'];
        if (count($aUserParam['from']) !== count($aUserParam['to'])) return;
        $aPoints = $this->getTimeDistrPoints();
        $data = array();
        foreach ($aUserParam['from'] as $k => $from) {
            $periodSet = new data_time_PeriodSet();
            $period = data_time_PeriodFactory::createPeriod($from, $aUserParam['to'][$k], $aUserParam['period'])
                ->setInterval(60)->setPoints($aPoints);
            $periodSet->add($period);
            $source = new data_source_DatabaseSource(new data_source_TimeDistrSQLHelper());
            $sourceData = $source->setConfigurationSet($this->configurationSet)->setPeriodSet($periodSet)->get($aUserParam);
            $populator = new data_populator_Populator();
            foreach ($periodSet as $period) {
                $data[] = $populator->setConfigurationSet($this->configurationSet)
                    ->setPeriod($period)->setSourceData($sourceData)->populate($aUserParam);
            }
        }
        return $data;
    }

    /**
     * 获取时间分布的填充点
     */
    protected function getTimeDistrPoints()
    {
        $aPoints = array_fill_keys(array('key', 'value'), array());
        $iMin = 0;
        while ($iMin < 1440) {
            $aPoints['key'][] = sprintf('%02d:%02d', floor($iMin/60), $iMin%60);
            $aPoints['value'][$iMin] = 0;
            $iMin++;
        }
        return $aPoints;
    }

    /**
     * 实时数据
     *
     * @param  array      $aUserParam
     * @return array|null
     */
    public function getRealTimeSeries($aUserParam)
    {
        $gpzsConfiguration = new common_GpzsInfo();
        $gpzsConfigure = $gpzsConfiguration->getGpzsInfoById($aUserParam['gpzs_id']);

        $dataConfiguration = new data_configuration_Data();
        $dataConfiguration->setGameId($aUserParam['game_id'])->setStatList($aUserParam['data_info']);
        $aUserParam['by_item'] && $dataConfiguration->statList();
        $this->addConfiguration($dataConfiguration, $gpzsConfigure);

        $periodSet = $this->getPeriodSet($aUserParam);
        $data = $this->timeSeries($this->configurationSet, $periodSet, $aUserParam);
        if (!$data) return;

        $aDataInfo = $dataConfiguration->getDataList();
        if (!$aDataInfo) return;

        if ($aUserParam['check_all'] && !$this->hasRangeAll($aDataInfo)) {
            $data = $this->appendRangeAll($data, $aUserParam['all_name']);
        }
        if (isset($aUserParam['sum']) && $aUserParam['sum']) {
            $data = $this->sum($data);
        }

        return $data;
    }

    /**
     * Whether the data info has a range _all_.
     *
     * @param  array   $aDataInfo
     * @return boolean
     */
    protected function hasRangeAll($aDataInfo)
    {
        // Is there a range _all_?
        $hasAll = false;
        foreach ($aDataInfo as $key => $info) {
            foreach ($info as $inf) {
                if ($inf['range'] === '_all_') {
                    $hasAll = true;
                    break;
                }
            }
        }
        return $hasAll;
    }

    /**
     * Append a sum value to each data, it will take sName as its name.
     *
     * @param  array  $aData
     * @param  string $sName
     * @return array
     */
    protected function appendRangeAll($aData, $sName = '')
    {
        foreach ($aData as &$data) {
            $temp = array('name' => $sName, 'data' => array());
            foreach ($data['key'] as $idx => $key) {
                $temp['data'][$idx] = null;
                foreach ($data['data'] as $value) {
                    if (!isset($value['data'][$idx])) continue;
                    if (isset($temp['data'][$idx])) {
                        $temp['data'][$idx] += $value['data'][$idx];
                        continue;
                    }
                    $temp['data'][$idx] = $value['data'][$idx];
                }
            }
            $data['data'][] = $temp;
        }
        return $aData;
    }

    /**
     * Sum it!
     *
     * @param  array $data
     * @return array
     */
    protected function sum($data)
    {
        foreach ($data as $key => $timeData) {
            $data[$key]['sum'] = array(
                'key' => array(),
                'data' => array()
            );
            foreach ($timeData['data'] as $value) {
                $data[$key]['sum']['key'][] = $value['name'];
                $data[$key]['sum']['data'][] = array_sum($value['data']);
            }
        }
        return $data;
    }

    /**
     * Get time series by data_ids and data_exprs.
     *
     * @param  array $aUserParam
     * @return array
     */
    public function getDataExprTimeSeries($aUserParam)
    {
        list($dataInfo, $exprs, $dataInfo4Exprs, $map) =
            $this->packDataInfo($aUserParam['data_info'], $aUserParam['gpzs_id']);
        unset($aUserParam['data_info']);

        $data4DataInfo = $data4Exprs = $data = array();
        if ($dataInfo) {
            $aUserParam['data_info'] = $dataInfo;
            $data4DataInfo = $data = $this->getTimeSeries($aUserParam);
        }
        if ($exprs && $dataInfo4Exprs) {
            $aUserParam['data_info'] = $dataInfo4Exprs;
            $aUserParam['exprs'] = $exprs;
            $data4Exprs = $this->getTimeSeries($aUserParam);
            if (!$data) $data = $data4Exprs;
        }
        $aData = array();
        foreach ($data as $idx => $sub) {
            $sub['data'] = array();
            foreach ($map as $type) {
                if ($type) {
                    $sub['data'][] = array_shift($data4Exprs[$idx]['data']);
                    continue;
                }
                $sub['data'][] = array_shift($data4DataInfo[$idx]['data']);
            }
            $aData[] = $sub;
        }
        return $aData;
    }

    /**
     * Pack raw data info
     *
     * @param  array $rawDataInfo
     * @param  array $defaultGpzsId
     * @return array
     */
    protected function packDataInfo($rawDataInfo, $defaultGpzsId)
    {
        $dataInfo = $exprs = $dataInfo4Exprs = $map = array();
        $count = 0;
        foreach ($rawDataInfo as $raw) {
            $trans = isset($raw['data_id'], $raw['data_expr']) ? array($raw) : $this->r2dataExpr($raw);
            foreach ($trans as $tran) {
                $common = array(
                    'data_name' => TMArrayHelper::assoc('data_name', $raw, $tran, ''),
                    'precision' => TMArrayHelper::assoc('precision', $raw, $tran, 2),
                    'unit' => TMArrayHelper::assoc('unit', $raw, $tran, '')
                );
                $gpzsId = (isset($tran['gpzs_id']) && $tran['gpzs_id']) ? $tran['gpzs_id'] : $defaultGpzsId;
                if (!$tran['data_expr']) {
                    $dataInfo[] = array_merge($common, array(
                        'data_id' => $tran['data_id'],
                        'factor' => TMArrayHelper::assoc('factor', $raw, $tran, 1),
                        'gpzs_id' => $gpzsId
                    ));
                    $map[] = 0; // dataInfo
                    continue;
                }
                $temp = explode('|', $tran['data_expr']); // 1337;1343|({0}*0.01)*{1} 1337_1234;1343_123|({0}*0.01)*{1}
                if (count($temp) !== 2) continue;
                list($dataIdStr, $expr) = $temp;
                $dataId = explode(';', $dataIdStr);
                foreach ($dataId as $idx => $id) {
                    $tmpDataId = explode('_', $id);
                    if (count($tmpDataId) == 2) {
                        list($id, $tmpGpzsId) = $tmpDataId;
                    } else {
                        $tmpGpzsId = $gpzsId;
                    }
                    $ukey = $id . ':' . $tmpGpzsId;
                    if (!isset($dataInfo4Exprs[$ukey])) {
                        $dataInfo4Exprs[$ukey] = array('data_id' => $id, 'gpzs_id' => $tmpGpzsId, 'index' => $count++);
                    }
                    if ($idx != $dataInfo4Exprs[$ukey]['index']) {
                        $expr = str_replace('{' . $idx . '}', "{\t" . $dataInfo4Exprs[$ukey]['index'] . '}', $expr);
                    }
                }
                $exprs[] = array_merge($common, array('expr' => str_replace("\t", '', $expr)));
                $map[] = 1; // exprs
            }
        }
        return array($dataInfo, $exprs, $dataInfo4Exprs, $map);
    }

    /**
     * Translate r_info(type, r_id) to data_expr(data_id, data_expr)
     *
     * @param  array $rInfo
     * @return array
     */
    public function r2dataExpr($rInfo)
    {
        $dataInfoModel = new common_DataInfo();
        $diyDataModel = new gamecustom_DiyData();
        switch($rInfo['type']) {
            case 'report':
                $dataExpr = $dataInfoModel->getRangeByRid($rInfo);
                break;

            case 'diy':
                $diyDataModel->diy_id = $rInfo['r_id'];
                $dataExpr = $diyDataModel->findByDiyId();
                break;

            default:
                $dataExpr = array();
                break;
        }

        foreach ($dataExpr as &$expr) {
            if (!isset($expr['data_id'])) $expr['data_id'] = 0;
            if (!isset($expr['data_expr'])) $expr['data_expr'] = '';
        }

        return $dataExpr;
    }
}
