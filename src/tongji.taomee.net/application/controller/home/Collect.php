<?php
class Collect extends home_Home
{
    public function actions()
    {
        return array(
            'getListByFavorId' => array('favor_id' => null),
            'getUpdatingInfo' => array('collect_id' => null),
            'getAdjustingInfo' => array('collect_id' => null),
            'getMetadataWithDate' => array(
                'collect_id' => null,
                'period' => 1,
                'from' => null,
                'to' => null
            ),
            'getDataById' => array(
                'collect_id' => null,
                'gpzs_id' => null,
                'period' => 1,
                'from' => null,
                'to' => null,
                'export' => null,
                'file_name' => null
            ),
            'add' => array(
                'collect_name' => null,
                'favor_id' => null,
                'draw_type' => null,
                'indicator' => array()
            ),
            'append' => array(
                'collect_id' => null,
                'indicator' => array()
            ),
            'set' => array(
                'collect_id' => null,
                'collect_name' => null,
                'draw_type' => null,
                'indicator' => array()
            ),
            'adjust' => array(
                'collect_id' => null,
                'collect_name' => null,
                'indicator' => array()
            ),
            'setCalcOption' => array(
                'collect_id' => null,
                'calc_option' => null
            ),
            'setCalcOptionForSharedFavor' => array(
                'collect_id' => null,
                'calc_option' => null
            ),
            'delete' => array('collect_id' => null),
            'move' => array('collect_id' => null, 'favor_id' => null),
            'deleteMetadata' => array('collect_id' => null, 'metadata' => array()),
            'share' => array( 'collect_id' => null, 'users' => null),
            'setCalcRowOption' => array(
                'collect_id' => null,
                'calc_option' => null
            ),
            'setCalcRowOptionForSharedFavor' => array(
                'collect_id' => null,
                'calc_option' => null
            ),
        );
    }

    public function getListByFavorId($aUserParameters)
    {
        $model = new home_Collect();
        $shared = new home_SharedCollect();
        $sharedFavor = new home_SharedFavorCollect();
        $this->ajax(0, array(
            'self' => array_merge($model->getListByFavorId(
                $aUserParameters['favor_id'],
                TM::app()->getUser()->getUserId(),
                true // Count metadata or not
            ), $sharedFavor->getListByFavorId(
                $aUserParameters['favor_id'],
                TM::app()->getUser()->getUserId(),
                true
            )),
            'shared' => $shared->getListByFavorId($aUserParameters['favor_id'])
        ));
    }

    /**
     * Deprecated, use getAdjustingInfo instead.
     */
    public function getUpdatingInfo($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $aCollect = $model->checkCollectExists($aUserParameters);
        $model = new home_Metadata();
        $aMetadata = $model->getListByCollectId($aUserParameters['collect_id']);
        $aCollect['indicator'] = array();
        foreach ($aMetadata as $metadata) {
            $key = $metadata['type'] . ':' . $metadata['r_id'] . ':' . $metadata['gpzs_id'];
            $setting = ($metadata['type'] === 'set' || $metadata['type'] === 'diy')
                ? ($metadata['data_id'] . ':' . $metadata['data_expr']) : $metadata['data_id'];
            if (isset($aCollect['indicator'][$key])) {
                $aCollect['indicator'][$key]['settings'][] = $setting;
                continue;
            }
            $metadata['id'] = $metadata['r_id'];
            $metadata['settings'] = array($setting);
            unset($metadata['r_id'], $metadata['data_id'], $metadata['data_expr'], $metadata['data_name']);
            $aCollect['indicator'][$key] = $metadata;
        }
        $aCollect['indicator'] = array_values($aCollect['indicator']);
        $this->ajax(0, $aCollect);
    }

    public function getAdjustingInfo($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $aCollect = $model->checkCollectExists($aUserParameters);
        $model = new home_Metadata();
        $aMetadata = $model->getListByCollectId($aUserParameters['collect_id']);
        $aCollect['indicator'] = array();
        foreach ($aMetadata as $metadata) {
            $aCollect['indicator'][] = [
                'data_name' => $metadata['data_name'],
                'data_key' => implode(':', [
                    $metadata['collect_id'],
                    $metadata['data_id'],
                    $metadata['data_expr'],
                    $metadata['gpzs_id']
                ])
            ];
        }
        $this->ajax(0, $aCollect);
    }

    public function getMetadataWithDate($aUserParameters)
    {
        $aUserParameters['period'] = data_time_PeriodFactory::TYPE_DAY;
        $aTimePoints = data_time_PeriodFactory::createPeriod(
            $aUserParameters['from'], $aUserParameters['to'], $aUserParameters['period'])->getPoints();
        $this->ajax(0, array(
            'date' => $aTimePoints['key'],
            'data' => $this->getMetadata($aUserParameters)
        ));
    }

    public function getDataById($aUserParameters)
    {
        // For data_Data getTimeSeries
        $aUserParameters['period'] = data_time_PeriodFactory::TYPE_DAY;
        $aUserParameters['game_id'] = 0;
        $aUserParameters['by_item'] = 0;
        $aUserParameters['yoy'] = 0;
        $aUserParameters['qoq'] = 0;
        $aUserParameters['contrast'] = 0;
        $aUserParameters['average'] = 0;
        $aUserParameters['rate2data'] = 0;
        $aUserParameters['by_data_expr'] = 1;

        $aUserParameters['data_info'] = $this->getMetadata($aUserParameters);
        if ($aUserParameters['export']) {
            $this->export($aUserParameters);
        }

        if (!$aUserParameters['data_info']) {
            $this->ajax(0);
        }
        $this->ajax(0, data_Data::model()->getTimeSeries($aUserParameters));
    }

    protected function export($aUserParameters)
    {
        $this->initExporter($aUserParameters);
        if (!$aUserParameters['data_info']) {
            $this->ajax(0, ['code' => 0, 'url' => $this->writeTimeSeries(null, $this->sFilename)]);
        }

        $aTimePoints = data_time_PeriodFactory::createPeriod(
            $aUserParameters['from'],
            $aUserParameters['to'],
            $aUserParameters['period']
        )->getPoints();

        $this->gotoExportFile(
            [
                'rows' => count($aUserParameters['data_info']),
                'cols' => count($aTimePoints['key'])
            ],
            $aUserParameters,
            ['home', 'collect', $aUserParameters['collect_id']]
        );

        $aData = data_Data::model()->getTimeSeries($aUserParameters);
        /* $this->ajax(0, $this->writeTimeSeries($aData, $this->sFilename)); */
        $this->ajax(0, ['code' => 0, 'url' => $this->writeTimeSeries($aData, $this->sFilename)]);
    }

    public function add($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $aUserParameters['calc_option'] = 'sum';
        $aUserParameters['calculateRow_option'] = 1;
        $this->ajax(0, $model->add($aUserParameters));
    }

    public function append($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $this->ajax(0, $model->append($aUserParameters));
    }

    /**
     * Deprecated, use adjust instead.
     */
    public function set($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $this->ajax(0, $model->set($aUserParameters));
    }

    public function adjust($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $this->ajax(0, $model->adjust($aUserParameters));
    }

    public function setCalcOption($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $collect = $model->checkCollectExists($aUserParameters);
        if ($collect['user_id'] == TM::app()->getUser()->getUserId()) {
            $model->collect_id = $aUserParameters['collect_id'];
            $model->calc_option = $aUserParameters['calc_option'];
            $model->update(array('calc_option'));
        } else {
            $model = new home_SharedCollect();
            $collect = $model->checkCollectExists($aUserParameters);
            if ($collect) {
                $model->collect_id = $aUserParameters['collect_id'];
                $model->user_id = $aUserParameters['user_id'];
                $model->favor_id = $collect['favor_id'];
                $model->calc_option = $aUserParameters['calc_option'];
                $model->update(array('calc_option'));
            }
        }
        $this->ajax(0);
    }

    public function setCalcOptionForSharedFavor($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $collect = $model->checkCollectExists($aUserParameters);
        $favorId = $collect['favor_id'];
        if ($collect['user_id'] != TM::app()->getUser()->getUserId()) {
            $model = new home_SharedFavorCollect();
            $collect = $model->checkCollectExists($aUserParameters);
            $model->collect_id = $aUserParameters['collect_id'];
            $model->user_id = $aUserParameters['user_id'];
            $model->favor_id = $favorId;
            $model->calc_option = $aUserParameters['calc_option'];
            if (!$collect) {
                $model->insert();
            } else {
                $model->update(array('calc_option'));
            }
        }
        $this->ajax(0);
    }

    public function delete($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $delete = $model->deleteCollect($aUserParameters);
        $model = new home_SharedCollect();
        if (true === $delete) {
            $model->deleteAllByAttributes(array(
                'collect_id' => $aUserParameters['collect_id']
            ));
        } else {
            $model->deleteAllByAttributes(array(
                'collect_id' => $aUserParameters['collect_id'],
                'user_id' => $aUserParameters['user_id']
            ));
        }
        $this->ajax(0);
    }

    public function move($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $model->move($aUserParameters);
        $model = new home_SharedCollect();
        $this->ajax(0, $model->move($aUserParameters));
    }

    public function deleteMetadata($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $model->checkCollectExists($aUserParameters);
        $aUserParameters['metadata'] = (array)$aUserParameters['metadata'];
        $model = new home_Metadata();
        foreach ($aUserParameters['metadata'] as $metadata) {
            if (!isset($metadata['data_id'], $metadata['data_expr'], $metadata['gpzs_id'])) continue;
            $model->deleteAllByAttributes(array(
                'collect_id' => $aUserParameters['collect_id'],
                'data_id' => $metadata['data_id'],
                'data_expr' => $metadata['data_expr'],
                'gpzs_id' => $metadata['gpzs_id']
            ));
        }
        $this->ajax(0);
    }

    public function fetchSharedUsers()
    {
        $this->ajax(0, TM::app()->getUser()->fetchAllUsers());
    }

    public function share($aUserParameters)
    {
        $model = new home_Collect();
        $collect = $model->checkCollectExists($aUserParameters);

        $favor = new home_Favor();

        $model = new home_SharedCollect();
        $model->collect_id = $collect['collect_id'];

        $users = explode(',', $aUserParameters['users']);
        foreach ($users as $userId) {
            $userId = (int)$userId;
            if (!$userId) continue;
            TMValidator::ensure(TM::app()->getUser()->getUserId() != $userId, TM::t('tongji', '不能分享给自己哦'));
            $favor = $favor->fetchSharedFavor($userId, $collect['favor_id']);
            $model->favor_id = $favor['favor_id'];
            $model->user_id = $userId;
            $model->calc_option = 'sum';
            $model->calculateRow_option = 1;
            $model->delete();
            $model->insert();
        }
        $this->ajax(0);
    }

    public function setCalcRowOption($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $collect = $model->checkCollectExists($aUserParameters);
        $favorId = $collect['favor_id'];
        if ($collect['user_id'] == TM::app()->getUser()->getUserId()) {
            $model->collect_id = $aUserParameters['collect_id'];
            $model->calculateRow_option = $aUserParameters['calc_option'];
            $model->update(array('calculateRow_option'));
        } else {
            $model = new home_SharedCollect();
            $collect = $model->checkCollectExists($aUserParameters);
            if ($collect) {
                $model->collect_id = $aUserParameters['collect_id']; 
                $model->user_id = $aUserParameters['user_id'];
                $model->favor_id = $collect['favor_id'];
                $model->calculateRow_option = $aUserParameters['calc_option'];
                $model->update(array('calculateRow_option'));
            }
        }
        $this->ajax(0);
    }

    public function setCalcRowOptionForSharedFavor($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $collect = $model->checkCollectExists($aUserParameters);
        $favorId = $collect['favor_id'];
        if ($collect['user_id'] != TM::app()->getUser()->getUserId()) {
            $model = new home_SharedFavorCollect();
            $collect = $model->checkCollectExists($aUserParameters);
            $model->collect_id = $aUserParameters['collect_id'];
            $model->user_id = $aUserParameters['user_id'];
            $model->favor_id = $favorId;
            $model->calculateRow_option = $aUserParameters['calc_option'];
            if (!$collect) {
                $model->insert();
            } else {
                $model->update(array('calculateRow_option'));
            }
        }
        $this->ajax(0);
    }
}
