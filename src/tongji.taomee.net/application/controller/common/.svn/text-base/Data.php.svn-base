<?php
class Data extends common_Base
{
    public function actions()
    {
        return array(
            'getTimeSeries' => array(
                'by_item' => 1,
                'contrast' => 0,
                'average' => 0,
                'rate2data' => 0,
                'group_name' => null,
                'sum' => 0,
                'by_data_expr' => 0,
                'by_exprs' => 1,
                'range' => null
            ),
            'getDistribution' => array(
                'contrast' => 0,
                'distr_by' => data_configuration_DistrFactoryManager::TYPE_PLATFORM
            ),
            'getTimeDistr' => array(
                'by_item' => 1,
                'calc_type' => 'sum'
            ),
            'getRealTimeSeries' => array(
                'by_item' => 1,
                'check_all' => null,
                'all_name' => null,
                'calc_type' => 'avg',
                'sum' => 0
            )
        );
    }

    /**
     * 接收公共参数
     */
    public function commonParameters()
    {
        return array(
            'game_id'    => null,
            'gpzs_id'    => null,
            'from'       => null,
            'to'         => null,
            'not_mark_date' => null,
            'offset'     => null, /* 日期偏移（天） */
            'fill_null'  => 1,    /* 填充null or 0 */
            'qoq'        => null, /* 环比 */
            'yoy'        => null, /* 同比 */
            'period'     => data_time_PeriodFactory::TYPE_DAY,
            /* 统计项列表 */
            'data_info'  => array(),
            /* 计算表达式列表 */
            'exprs'      => null,
            'export'     => null, /* 下载 */
            'file_name'  => null, /* 下载文件名称 */
            'extend'     => null, /* 扩展 */
            /* 记录参数 */
            'platform_id' => -1, /* 默认显示全平台数据 */
            'zone_id'     => null,
            'server_id'   => null,
            /* 分组 */
            'no_group'    => 0,
            'group'       => array()
        );
    }

    /**
     * 获取时间段的数据
     */
    public function getTimeSeries($aUserParameters)
    {
        if ($aUserParameters['rate2data']) $aUserParameters['qoq'] = $aUserParameters['yoy'] = 1;
        if ($aUserParameters['no_group']) $aUserParameters['group'] = array();
        $aUserParameters = $this->keySort($aUserParameters, ['from', 'to', 'exprs']);

        if ($aUserParameters['contrast']) {
            if (($dates = data_time_Time::align($aUserParameters)) === false) {
                $this->ajax(0);
            }
            $aUserParameters = array_merge($aUserParameters, $dates);
            $aUserParameters['yoy'] = $aUserParameters['qoq'] = 0;
        }
        if ($aUserParameters['offset']) {
            if (($dates = $this->offsetDate($aUserParameters, $aUserParameters['offset'])) === false) {
                $this->ajax(0);
            }
            $aUserParameters = array_merge($aUserParameters, $dates);
        }

        if ($aUserParameters['export']) $this->exportTimeSeries($aUserParameters);
        $aData = data_Data::model()->getTimeSeries($aUserParameters);
        if (is_array($aUserParameters['from']) && count($aUserParameters['from']) > 1) {
            $aData = $this->formatNameByTime($aData, $aUserParameters);
        }
        $this->ajax(0, $aData);
    }

    /**
     * 偏移日期
     *
     * @param  array $dates
     * @param  int   $offset
     * @return array
     */
    protected function offsetDate($dates, $offset)
    {
        $dates['from'] = array_values((array)$dates['from']);
        $dates['to'] = array_values((array)$dates['to']);

        $cntFrom = count($dates['from']);
        $cntTo = count($dates['to']);
        if (!$cntFrom || !$cntTo || $cntFrom !== $cntTo) return false;

        foreach ($dates['from'] as $idx => &$from) {
            if (($iFrom = strtotime("{$from} {$offset}day")) === false) return false;
            $from = date('Y-m-d', $iFrom);
            if (($iTo = strtotime("{$dates['to'][$idx]} {$offset}day")) === false) return false;
            $dates['to'][$idx] = date('Y-m-d', $iTo);
        }
        return $dates;
    }

    /**
     * 排序数组中某些键
     *
     * @param  array $aParameters
     * @param  array $aKeys
     * @return array              The formatted array
     */
    protected function keySort($aParameters, $aKeys)
    {
        foreach ($aKeys as $key) {
            if (isset($aParameters[$key]) && is_array($aParameters[$key])) {
                ksort($aParameters[$key]);
            }
        }

        return $aParameters;
    }

    /**
     * 根据时间格式化数据名称
     */
    protected function formatNameByTime($aData, $aParameters)
    {
        if (!is_array($aData)) return $aData;
        if ($aParameters['period'] == data_time_PeriodFactory::TYPE_MINUTE ||
                $aParameters['period'] == data_time_PeriodFactory::TYPE_HOUR) {
            foreach ($aData as $i => $data) {
                if (!isset($data['data']) || !is_array($data['data'])) continue;
                foreach ($data['data'] as $j => $value) {
                    $aData[$i]['data'][$j]['name'] =
                        '[' .  substr($data['key'][0], 0, strpos($data['key'][0], ' ')) . ']' . $value['name'];
                }
            }
        }
        return $aData;
    }

    /**
     * 时间段数据的导出
     */
    protected function exportTimeSeries($aUserParameters)
    {
        $this->dealTimeSeriesMultiPeriod($aUserParameters);
        $this->initExporter($aUserParameters);
        $this->oExporter->add($this->sFilename);
        $model = new data_Data();
        $aMultiPeriodData = $aUserParameters['exprs'];
        $bHasGroup = $aUserParameters['group'] && count($aMultiPeriodData) === 1;
        foreach ($aMultiPeriodData as $period => $expr) {
            $aUserParameters['exprs'] = $expr;
            $aUserParameters['period'] = $period;
            if (!$aUserParameters['extend']) {
                $this->writeTimeSeriesExt($aUserParameters, $model, $bHasGroup);
                continue;
            }
            if (!isset($aUserParameters['extend']['game_id'])) {
                $aUserParameters['extend']['game_id'] = $aUserParameters['game_id'];
            }
            if (!isset($aUserParameters['extend']['platform_id'])) {
                $aUserParameters['extend']['platform_id'] = $aUserParameters['platform_id'];
            }
            foreach ($this->getExtendParam($aUserParameters['extend']) as $param) {
                $aUserParameters = TMArrayHelper::recursiveMerge($aUserParameters, $param);
                if (isset($aUserParameters['extend_name'])) {
                    $this->oExporter->putWithTitle($aUserParameters['extend_name']);
                }
                $this->writeTimeSeriesExt($aUserParameters, $model, $bHasGroup);
            }
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    /**
     * 处理时间数据的多个时间维度
     */
    protected function dealTimeSeriesMultiPeriod(&$aUserParameters)
    {
        $aMultiPeriod = array();
        foreach ($aUserParameters['exprs'] as $expr) {
            if (!isset($expr['period'])) $expr['period'] = $aUserParameters['period'];
            if (!isset($aMultiPeriod[$expr['period']])) $aMultiPeriod[$expr['period']] = array();
            $aMultiPeriod[$expr['period']][] = $expr;
        }
        $aUserParameters['exprs'] = $aMultiPeriod;
    }

    
    /** 
     * 处理时间数据不使用操作数据元分组情况
     */
    protected function dealTimeSeriesWithoutExprs(&$aUserParameters, $model)
    {   
        if ($aUserParameters['by_exprs'] || $aUserParameters['no_group']) return;
        $lastTotal = 0;
        foreach ($model->getDataConfigurations() as $dataConfiguration) {
            $dataList = array_values($dataConfiguration->getDataList());
            foreach ($dataList as $index => $datas) {
                $aUserParameters['group'][$index]['start'] = $lastTotal;
                $lastTotal += count($datas);
            }   
        }   
    }   

    /**
     * 将时间序列写入文件
     *
     * @param array      $aUserParameters
     * @param data_Data  $model
     * @param boolean    $bHasGroup
     * @return
     */
    protected function writeTimeSeriesExt($aUserParameters, $model, $bHasGroup = false)
    {
        $aData = $model->getTimeSeries($aUserParameters);
        $this->dealTimeSeriesWithoutExprs($aUserParameters, $model);
        foreach ($aData as $data) {
            if ($aUserParameters['average']) $data['key'][] = TM::t('tongji', '均值');
            $data['key'][] = TM::t('tongji', '日期');
            $head = array_reverse($data['key']);
            if ($aUserParameters['rate2data']) {
                if ($aUserParameters['yoy']) $head[] = TM::t('tongji', '同比');
                if ($aUserParameters['qoq']) $head[] = TM::t('tongji', '环比');
            }
            if ($bHasGroup) $iGroupIdx = 0;
            else $this->oExporter->put($head, CsvExporter::ENCODE_ALL);
            foreach ($data['data'] as $k => $item) {
                if ($bHasGroup && isset($aUserParameters['group'][$iGroupIdx]) &&
                        $k == $aUserParameters['group'][$iGroupIdx]['start']) {
                    if ($iGroupIdx) $this->oExporter->put(); // blank line
                    if (isset($aUserParameters['group'][$iGroupIdx]['name'])) {
                        $this->oExporter->putWithTitle($aUserParameters['group'][$iGroupIdx]['name']);
                    }
                    $this->oExporter->put($head, CsvExporter::ENCODE_ALL);
                    $iGroupIdx++;
                }
                if ($aUserParameters['average']) $item['data'][] = $data['average']['data'][$k];
                if ($aUserParameters['rate2data']) {
                    if ($aUserParameters['yoy']) array_unshift($item['data'], $item['yoy'][0]);
                    if ($aUserParameters['qoq']) array_unshift($item['data'], $item['qoq'][0]);
                }
                $this->oExporter->putWithTitle($item['name'], $item['data']);
                if (!$aUserParameters['rate2data']) {
                    if ($aUserParameters['yoy']) $this->oExporter->putWithTitle(TM::t('tongji', '同比'), $item['yoy']);
                    if ($aUserParameters['qoq']) $this->oExporter->putWithTitle(TM::t('tongji', '环比'), $item['qoq']);
                }
            }
            $this->oExporter->put();
        }
    }

    /**
     * 响应分布数据及下载请求
     */
    public function getDistribution($aUserParameters)
    {
        $aUserParameters = $this->keySort($aUserParameters, ['from', 'to']);
        if ($aUserParameters['export']) $this->exportDistribution($aUserParameters);
        // use the first period if it exists.
        if (isset($aUserParameters['data_info'][0], $aUserParameters['data_info'][0]['period'])) {
            $aUserParameters['period'] = $aUserParameters['data_info'][0]['period'];
        }
        $aData = data_Data::model()->getDistribution($aUserParameters);
        if ($aData = array_values(array_filter($aData))) {
            foreach ($aData as $k => $data) {
                if ($k) {
                    $aData[0]['data'][] = $data['data'][0];
                    unset($aData[$k]);
                }
            }
        }
        $this->ajax(0, $aData);
    }

    /**
     * 下载分布数据
     */
    protected function exportDistribution($aUserParameters)
    {
        $this->initExporter($aUserParameters);
        $this->oExporter->add($this->sFilename);
        $model = new data_Data();
        $this->dealDistributionMultiPeriod($aUserParameters);
        $aMultiPeriodData = $aUserParameters['data_info'];
        foreach ($aMultiPeriodData as $period => $dataInfo) {
            $aUserParameters['period'] = $period;
            $aUserParameters['data_info'] = $dataInfo;
            if (!$aUserParameters['extend']) {
                $this->writeDistribution($aUserParameters, $model);
                continue;
            }
            if (!isset($aUserParameters['extend']['game_id'])) {
                $aUserParameters['extend']['game_id'] = $aUserParameters['game_id'];
            }
            foreach ($this->getExtendParam($aUserParameters['extend']) as $param) {
                $aUserParameters = TMArrayHelper::recursiveMerge($aUserParameters, $param);
                $this->oExporter->putWithTitle($aUserParameters['extend_name']);
                $this->writeDistribution($aUserParameters, $model);
            }
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    /**
     * 处理分布数据的多个时间维度
     */
    protected function dealDistributionMultiPeriod(&$aUserParameters)
    {
        $aMultiPeriod = array();
        foreach ($aUserParameters['data_info'] as $dataInfo) {
            if (!isset($dataInfo['period'])) $dataInfo['period'] = $aUserParameters['period'];
            if (!isset($aMultiPeriod[$dataInfo['period']])) $aMultiPeriod[$dataInfo['period']] = array();
            $aMultiPeriod[$dataInfo['period']][] = $dataInfo;
        }
        $aUserParameters['data_info'] = $aMultiPeriod;
    }

    /**
     * 获取分布数据并写入文件
     * @param array      $aUserParameters
     * @param data_Data $model
     * @return
     */
    protected function writeDistribution($aUserParameters, $model)
    {
        $aDistrData = $model->initDistribution()->getDistribution($aUserParameters);
        if (!$aDistrData) return;
        $aDistrConfiguration = $model->getDistrConfiguration();
        $aTimeSeries = $model->getDistrTimeSeries();
        foreach ($aDistrData as $key => $distrData) {
            if (!$distrData) continue;
            $distr = $aDistrConfiguration[$key];
            $head = $aTimeSeries[$key]['key'];
            if ($aUserParameters['yoy']) $head[] = TM::t('tongji', '同比');
            if ($aUserParameters['qoq']) $head[] = TM::t('tongji', '环比');
            if (isset($distrData['data'][0]['percentage'])) $head[] = TM::t('tongji', '比例');
            array_push($head, $distr->getDistrName(), $distr->getDimenName());
            $this->oExporter->put(array_reverse($head), CsvExporter::ENCODE_ALL);
            foreach ($aTimeSeries[$key]['data'] as $idx => $data) {
                $line = $data['data'];
                if ($aUserParameters['yoy']) $line[] = $distrData['data'][0]['yoy'][$idx];
                if ($aUserParameters['qoq']) $line[] = $distrData['data'][0]['qoq'][$idx];
                if (isset($distrData['data'][0]['percentage'])) $line[] = $distrData['data'][0]['percentage'][$idx];
                array_push($line, $distrData['data'][0]['data'][$idx], $data['name']);
                $this->oExporter->put(array_reverse($line), CsvExporter::ENCODE_PREV);
            }
            $this->oExporter->put(); // 空行
        }
    }

    /**
     * 时间维度的分布，如游戏时段
     */
    public function getTimeDistr($aUserParameters)
    {
        // use the first period if it exists.
        if (isset($aUserParameters['exprs'][0], $aUserParameters['exprs'][0]['period'])) {
            $aUserParameters['period'] = $aUserParameters['exprs'][0]['period'];
        }
        $aData = data_Data::model()->getTimeDistr($aUserParameters);
        if ($aUserParameters['export']) {
            $this->initExporter($aUserParameters);
            $this->oExporter->add($this->sFilename);
            foreach ($aData as $data) {
                $title = array(TM::t('tongji', '时间'), $data['data'][0]['name']);
                $content = array($data['key'], $data['data'][0]['data']);
                $this->oExporter->putColumns($title, $content);
                $this->oExporter->put();
            }
            $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
        }
        $this->ajax(0, $aData);
    }

    /**
     * 取实时数据，处理range=_all_的情况，如在线人数
     */
    public function getRealTimeSeries($aUserParameters)
    {
        $aUserParameters['to'] = $aUserParameters['from'];
        unset($aUserParameters['exprs']); // 不走计算
        $aData = data_Data::model()->getRealTimeSeries($aUserParameters);
        if (is_array($aUserParameters['from']) && count($aUserParameters['from']) > 1) {
            $aData = $this->formatNameByTime($aData, $aUserParameters);
        }
        $this->ajax(0, $aData);
    }

    /**
     * 获取扩展参数
     *
     * @param  array $info
     * @return array
     */
    protected function getExtendParam($info)
    {
        $params = array();
        switch ($info['r']) {
            // 多个sstid
            case 'common/report/getList':
                $model = new common_Report();
                $value = $model->getSstid($info);
                foreach ($value as $v) {
                    if (!isset($v[$info['key']])) continue;
                    parse_str($info['urlKey'] . '=' . $v[$info['key']] .
                        '&extend_name=' . $v['name'], $params[]);
                }
                break;
            // 多个平台（渠道）
            case 'common/gpzs/getPlatform':
                $model = new common_GpzsInfo();
                $model->attributes = $info;
                $value = $model->getPlatform();
                if ($info['isMultiple']) {
                    $url = '';
                    foreach ($value as $v) {
                        if (!isset($v[$info['key'] . 'id'])) continue;
                        $url .= $info['urlKey'] . '[]=' . $v[$info['key'] . 'id'] . '&';
                    }
                    parse_str(rtrim($url, '&'), $params[]);
                }
                break;
            // 多个区服
            case 'common/gpzs/getZoneServer':
                $model = new common_GpzsInfo();
                $model->attributes = $info;
                $value = $model->getZoneServer();
                if ($info['isMultiple']) {
                    $url = '';
                    foreach ($value as $v) {
                        if (!isset($v[$info['key'] . 'id'])) continue;
                        $url .= $info['urlKey'] . '[]=' . $v[$info['key'] . 'id'] . '&';
                    }
                    parse_str(rtrim($url, '&'), $params[]);
                }
                break;
            // 多个range交集
            case 'common/datainfo/getCommonRange':
                $model = new common_DataInfo();
                $value = $model->getCommonRange($info);
                foreach ($value as $v) {
                    if (!isset($v[$info['key']])) continue;
                    parse_str($info['urlKey'] . '=' . $v[$info['key']] .
                        '&extend_name=' . $v['range_name'], $params[]);
                }
                break;
        }
        return $params;
    }
}
