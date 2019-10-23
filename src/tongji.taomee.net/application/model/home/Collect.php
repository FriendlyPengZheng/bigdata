<?php
class home_Collect extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_collect';
    }

    /**
     * @brief rules
     * 获取参数检测的配置
     *
     * @return {array}
     */
    public function rules()
    {
        return array(
            array('collect_name', 'string', 'min' => 1, 'max' => 255),
            array('draw_type', 'number', 'min' => 1, 'max' => 5, 'integerOnly' => true)
        );
    }

    /**
     * Get collect list by favor_id.
     *
     * @param  integer $iFavorId
     * @param  integer $iUserId
     * @param  boolean $bCountMetadata
     * @return array
     */
    public function getListByFavorId($iFavorId, $iUserId = null, $bCountMetadata = false)
    {
        $sCondition = 'favor_id = ?';
        $aParam = array($iFavorId);
        if (isset($iUserId)) {
            $sCondition .= ' AND user_id = ?';
            $aParam[] = $iUserId;
        }

        $oCommand = $this->getDb()->createCommand()
            ->select('c.collect_id,c.collect_name,c.favor_id,c.draw_type,c.user_id,c.calc_option,c.calculateRow_option')
            ->from($this->tableName() . ' c')
            ->where($sCondition);

        if ($bCountMetadata) {
            $oCommand->select($oCommand->getSelect() . ',COUNT(1) AS metadata_cnt')
                ->leftJoin((new home_Metadata())->tableName() . ' m', 'c.collect_id=m.collect_id')
                ->group('c.collect_id');
        }

        return $oCommand->order('c.collect_id')->queryAll($aParam);
    }

    /**
     * Delete the collect, also its metadatas.
     * @param array $aUserParam
     * @return boolean
     */
    public function deleteCollect($aUserParam)
    {
        $collect = $this->checkCollectExists($aUserParam);
        if ($collect['user_id'] != $aUserParam['user_id']) return false;
        $transaction = $this->getDb()->beginTransaction();
        try {
            $this->collect_id = $aUserParam['collect_id'];
            $this->delete();
            $oMetadata = new home_Metadata();
            $oMetadata->deleteMetadataByCollectId($aUserParam['collect_id']);
            $transaction->commit();
            return true;
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * Check if the collect exists.
     * @param array $aUserParam
     * @return array the collect info if it exists
     */
    public function checkCollectExists($aUserParam)
    {
        $aCollect = $this->getCollectById($aUserParam['collect_id']);
        TMValidator::ensure($aCollect, TM::t('tongji', '小部件不存在！'));
        return $aCollect;
    }

    /**
     * Get collect info by collect_id.
     * @param integer $iCollectId
     * @return array
     */
    protected function getCollectById($iCollectId)
    {
        return $this->getDb()->createCommand()
            ->select('collect_id, collect_name, favor_id, draw_type, user_id')
            ->from($this->tableName())
            ->where('collect_id = ?')
            ->queryRow(array($iCollectId));
    }

    /**
     * Move this collect to other favor.
     * @param array $aUserParam
     * @return boolean
     */
    public function move($aUserParam)
    {
        $aCollect = $this->checkCollectExists($aUserParam);
        if ($aUserParam['favor_id'] == $aCollect['favor_id']) {
            return;
        }
        if ($aUserParam['user_id'] != $aCollect['user_id']) {
            return;
        }
        $oFavor = new home_Favor();
        $aCurrent = $oFavor->checkFavorExists(array_merge($aUserParam, array('favor_id' => $aCollect['favor_id'])));
        $aForward = $oFavor->checkFavorExists($aUserParam);
        TMValidator::ensure(
            $aCurrent['favor_type'] === $aForward['favor_type'],
            TM::t('tongji', '移向收藏与小部件所在收藏类型不一致！')
        );
        if ($aCurrent['favor_type'] == home_Favor::TYPE_SINGLE_GAME) {
            TMValidator::ensure(
                $aCurrent['game_id'] === $aForward['game_id'],
                TM::t('tongji', '移向收藏与小部件所在收藏游戏类型不一致！')
            );
        }
        $this->collect_id = $aUserParam['collect_id'];
        $this->favor_id = $aUserParam['favor_id'];
        return $this->update(array('favor_id'));
    }

    /**
     * Add a collect.
     * @param array $aUserParam
     * @return integer
     */
    public function add($aUserParam)
    {
        $aMetadata = array_values($this->handleCollectParams($aUserParam));
        $transaction = $this->getDb()->beginTransaction();
        try {
            $this->attributes = $aUserParam;
            $this->insert();
            foreach ($aMetadata as $key => $metadata) {
                $metadata['collect_id'] = $this->collect_id;
                $metadata['display_order'] = $key;
                $model = new home_Metadata();
                $model->attributes = $metadata;
                $model->insert();
            }
            $transaction->commit();
            return $this->collect_id;
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * Append indicators to an existed collect.
     * @param array $aUserParam
     * @return integer
     */
    public function append($aUserParam)
    {
        $aCollect = $this->checkCollectExists($aUserParam);
        $aUserParam = array_merge($aUserParam, $aCollect);
        $aMetadata = $this->handleCollectParams($aUserParam);
        $model = new home_Metadata();
        $aCurrent = $model->getListByCollectId($aUserParam['collect_id']);
        $iMaxOrder = 0;
        $aTemp = array_fill_keys(array_keys($aMetadata), 1);
        foreach ($aCurrent as $metadata) {
            $key = $metadata['data_id'] . ':' . $metadata['gpzs_id'] . ':' . $metadata['data_expr'];
            $aTemp[$key] = 1;
            if (isset($aMetadata[$key])) $aMetadata[$key]['display_order'] = $metadata['display_order'];
            if ($metadata['display_order'] > $iMaxOrder) $iMaxOrder = $metadata['display_order'];
        }
        $this->checkMetadataCount($aTemp, $aUserParam['draw_type']);
        $transaction = $this->getDb()->beginTransaction();
        try {
            foreach ($aMetadata as $metadata) {
                $metadata['collect_id'] = $aUserParam['collect_id'];
                if (!isset($metadata['display_order'])) $metadata['display_order'] = ++$iMaxOrder;
                $model->replace($metadata);
            }
            $transaction->commit();
            return $aUserParam['collect_id'];
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * Update the collect.
     * @param array $aUserParam
     * @return integer the collect id
     */
    public function set($aUserParam)
    {
        $aCollect = $this->checkCollectExists($aUserParam);
        $aUserParam['favor_id'] = $aCollect['favor_id'];
        $aMetadata = array_values($this->handleCollectParams($aUserParam));
        $transaction = $this->getDb()->beginTransaction();
        try {
            $this->collect_id   = $aUserParam['collect_id'];
            $this->collect_name = $aUserParam['collect_name'];
            $this->draw_type    = $aUserParam['draw_type'];
            $this->update(array('collect_name', 'draw_type'));
            $model = new home_Metadata();
            $model->deleteMetadataByCollectId($aUserParam['collect_id']);
            foreach ($aMetadata as $key => $metadata) {
                $metadata['collect_id'] = $aUserParam['collect_id'];
                $metadata['display_order'] = $key;
                $model = new home_Metadata();
                $model->attributes = $metadata;
                $model->insert();
            }
            $transaction->commit();
            return $aUserParam['collect_id'];
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * Handle collect params for adding or updating.
     * @param array $aUserParam
     * @return array
     */
    protected function handleCollectParams($aUserParam)
    {
        $this->attributes = $aUserParam;
        $this->validate(array('collect_name', 'draw_type'));
        $oFavor = new home_Favor();
        $aFavor = $oFavor->checkFavorExists($aUserParam);
        TMValidator::ensure(
            $aUserParam['indicator'] && is_array($aUserParam['indicator']),
            TM::t('tongji', '指标不能为空！')
        );
        $aMetadata = $this->parseIndicator($aUserParam['indicator'], $aFavor['favor_type'], $aFavor['game_id']);
        $this->checkMetadataCount($aMetadata, $aUserParam['draw_type']);
        return $aMetadata;
    }

    /**
     * Check metadata count.
     * @param array   $aMetadata
     * @param integer $iDrawType
     * @return
     */
    protected function checkMetadataCount($aMetadata, $iDrawType)
    {
        TMValidator::ensure($aMetadata, TM::t('tongji', '元数据不能为空！'));
        $iCount = count($aMetadata);
        TMValidator::ensure(
            $iDrawType == 3 ? $iCount <= 1000 : $iCount <= 100,
            TM::t('tongji', '表格类型元数据不能超过{1}条，非表格不能超过{2}条！', ['{1}' => 1000, '{2}' => 100])
        );
    }

    /**
     * Parse indicators into metadatas.
     * @param  array   $aIndicator
     * @param  integer $iFavorType
     * @param  integer $iGameId
     * @return array
     */
    protected function parseIndicator($aIndicator, $iFavorType, $iGameId)
    {
        $aMetadata = $aHandler = array();
        $gpzs = new common_GpzsInfo();
        $gameList = (new common_Game())->getIdGroupedGameByAuth();
        foreach ($aIndicator as $ind) {
            if (!isset($ind['id'])) continue;
            if (!isset($ind['gpzs_id'])) {
                if ($iFavorType == home_Favor::TYPE_MIXED_GAME) continue;
                $ind['gpzs_id'] = 0;
            }
            $ind['game_id'] = $iGameId;
            $ind['platform_id'] = 0;
            $ind['game_name'] = $ind['gpzs_name'] = '';
            if ($ind['gpzs_id']) {
                $gpzsInfo = $gpzs->getGpzsInfoById($ind['gpzs_id']);
                if (!$gpzsInfo) continue;
                $ind['game_id'] = $gpzsInfo[0]['game_id'];
                $ind['platform_id'] = $gpzsInfo[0]['platform_id'];
                $ind['gpzs_name'] = $gpzsInfo[0]['gpzs_name'];
                if (isset($gameList[$ind['game_id']])) {
                    $ind['game_name'] = $gameList[$ind['game_id']]['game_name'];
                }
            }
            if (!isset($ind['type'])) $ind['type'] = 'report';
            if (!isset($aHandler[$ind['type']])) {
                $aHandler[$ind['type']] = $this->initIndicatorHandler($ind['type']);
            }
            if (!$aHandler[$ind['type']]) continue;
            list($common, $dataList, $settings) = $this->{$aHandler[$ind['type']]['method']}(
                $aHandler[$ind['type']]['handler'], $ind);
            $common = array_merge($common, array(
                'data_name'   => $ind['game_name'] . $ind['gpzs_name'],
                'gpzs_id'     => $ind['gpzs_id'],
                'game_id'     => $ind['game_id'],
                'platform_id' => $ind['platform_id']
            ));
            $aMetadata = array_merge($aMetadata, $this->parseSettings($common, $dataList, $settings));
        }
        return $aMetadata;
    }

    /**
     * Init indicator handlers by type.
     * @param  string     $type
     * @return array|null
     */
    protected function initIndicatorHandler($type)
    {
        switch ($type) {
            case 'report':
                return array(
                    'method'  => 'parseReport',
                    'handler' => array(new common_Report(), new common_DataInfo())
                );
                break;

            case 'set':
                return array(
                    'method'  => 'parseSet',
                    'handler' => array(new module_Set(), new module_SetData())
                );
                break;

            case 'diy':
                return array(
                    'method'  => 'parseDiy',
                    'handler' => array(new gamecustom_Diy(), new gamecustom_DiyData())
                );
                break;
        }
    }

    /**
     * Parse report.
     * @param  array $handler
     * @param  array $ind
     * @return array
     */
    protected function parseReport($handler, $ind)
    {
        $nothing = array_fill(0, 3, array());

        $aAuth = TM::app()->getUser()->getAuthority();
        $customAuthId = TM::app()->navigator->getAuthIdByNaviKey(array('gamecustom'));
        if ($customAuthId && !isset($aAuth[$customAuthId])) return $nothing;

        $statInfo = $handler[0]->getStatItemById($ind['id']);
        if (!$statInfo || $statInfo['game_id'] != $ind['game_id']) return $nothing;
        $dataInfo = $handler[1]->getRangeByRid($statInfo);
        if (!$dataInfo) return $nothing;
        $common = array('r_id' => $statInfo['r_id'], 'type' => $statInfo['type']);
        $dataList = TMArrayHelper::column($dataInfo, null, 'data_id');
        $settings = TMArrayHelper::assoc('settings', $ind, array());
        if (!$statInfo['is_multi']) {
            $id = key($dataList);
            $dataList[$id]['data_name'] = $statInfo['r_name'];
            $settings = array($id);
        }
        return array($common, $dataList, $settings);
    }

    /**
     * Parse set.
     * @param  array $handler
     * @param  array $ind
     * @return array
     */
    protected function parseSet($handler, $ind)
    {
        $nothing = array_fill(0, 3, array());

        $handler[0]->set_id = $handler[1]->set_id = $ind['id'];
        $setInfo = $handler[0]->findById();
        if (!$setInfo || $setInfo[0]['game_id'] != $ind['game_id']) return $nothing;
        $aAuth = TM::app()->getUser()->getAuthority();
        if ($setInfo[0]['auth_id'] && !isset($aAuth[$setInfo[0]['auth_id']])) return $nothing;

        $dataInfo = $handler[1]->formatList($handler[1]->getList());
        if (!$dataInfo) return $nothing;
        $common = array('r_id' => $setInfo[0]['set_id'], 'type' => 'set');
        $dataList = TMArrayHelper::column($dataInfo, null, 'id');
        $settings = TMArrayHelper::assoc('settings', $ind, array());
        return array($common, $dataList, $settings);
    }

    /**
     * Parse diy.
     * @param  array $handler
     * @param  array $ind
     * @return array
     */
    protected function parseDiy($handler, $ind)
    {
        $nothing = array_fill(0, 3, array());

        $aAuth = TM::app()->getUser()->getAuthority();
        $customAuthId = TM::app()->navigator->getAuthIdByNaviKey(array('gamecustom'));
        if ($customAuthId && !isset($aAuth[$customAuthId])) return $nothing;

        $handler[0]->diy_id = $handler[1]->diy_id = $ind['id'];
        $diyInfo = $handler[0]->findById();
        if (!$diyInfo || $diyInfo[0]['game_id'] != $ind['game_id']) return $nothing;

        $dataInfo = $handler[1]->findByDiyId();
        if (!$dataInfo) return $nothing;
        $common = array('r_id' => $diyInfo[0]['diy_id'], 'type' => 'diy');
        $dataList = TMArrayHelper::column($dataInfo, null, 'id');
        $settings = TMArrayHelper::assoc('settings', $ind, array());
        if ((int)$diyInfo[0]['r_type'] === gamecustom_diy_Base::CATEGORY_BASIC) {
            $settings = array(key($dataList));
        }
        return array($common, $dataList, $settings);
    }

    /**
     * Parse settings.
     * @param  array $common
     * @param  array $dataList
     * @param  array $settings
     * @return array
     */
    protected function parseSettings($common, $dataList, $settings)
    {
        $aMetadata = array();
        foreach ($settings as $dataId) {
            if (!isset($dataList[$dataId])) continue;
            $aMetadata[
                $dataList[$dataId]['data_id'] . ':' .
                $common['gpzs_id'] . ':' .
                TMArrayHelper::assoc('data_expr', $dataList[$dataId], '')
            ] = array_merge($common, array(
                'data_name' => $common['data_name'] . $dataList[$dataId]['data_name'],
                'data_id'   => $dataList[$dataId]['data_id'],
                'data_expr' => TMArrayHelper::assoc('data_expr', $dataList[$dataId], ''),
                'factor'    => TMArrayHelper::assoc('factor', $dataList[$dataId], 1),
                'precision' => TMArrayHelper::assoc('precision', $dataList[$dataId], 2),
                'unit'      => TMArrayHelper::assoc('unit', $dataList[$dataId], '')
            ));
        }
        return $aMetadata;
    }

    /**
     * Adjust the collect.
     *
     * @param  array $aUserParam
     * @return int               The adjusted collect id
     */
    public function adjust($aUserParam)
    {
        $aCollect = $this->checkCollectExists($aUserParam);
        $this->attributes = $aUserParam;
        $this->validate(array('collect_name'));

        $transaction = $this->getDb()->beginTransaction();
        try {
            $this->collect_id   = $aUserParam['collect_id'];
            $this->collect_name = $aUserParam['collect_name'];
            $this->update(array('collect_name'));

            $aKeys = $this->updateExistMetadata($aUserParam);
            $this->deleteRestMetadata($aKeys);

            $transaction->commit();
            return $aUserParam['collect_id'];
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    protected function updateExistMetadata($aUserParam)
    {
        $aKeys = $this->getAssocMetadata($aUserParam);

        $model = new home_Metadata();
        $aUserParam['indicator'] = (array)$aUserParam['indicator'];
        $order = 1;
        foreach ($aUserParam['indicator'] as $metadata) {
            if (!isset($metadata['data_key']) || !$metadata['data_key']
                    || !isset($aKeys[$metadata['data_key']])
                    || !isset($metadata['data_name']) || !$metadata['data_name']) {
                continue;
            }
            $model->updateByAttr([
                'data_name' => $metadata['data_name'],
                'display_order' => $order++
            ], [
                'collect_id' => $aKeys[$metadata['data_key']]['collect_id'],
                'data_id' => $aKeys[$metadata['data_key']]['data_id'],
                'data_expr' => $aKeys[$metadata['data_key']]['data_expr'],
                'gpzs_id' => $aKeys[$metadata['data_key']]['gpzs_id']
            ]);
            unset($aKeys[$metadata['data_key']]);
        }

        return $aKeys;
    }

    protected function getAssocMetadata($aUserParam)
    {
        $model = new home_Metadata();
        $aMetadata = $model->getListByCollectId($aUserParam['collect_id']);
        $aKeys = [];
        foreach ($aMetadata as $metadata) {
            $aKeys[implode(':', [
                $metadata['collect_id'],
                $metadata['data_id'],
                $metadata['data_expr'],
                $metadata['gpzs_id']
            ])] = $metadata;
        }
        return $aKeys;
    }

    protected function deleteRestMetadata($aKeys)
    {
        $model = new home_Metadata();
        foreach ($aKeys as $metadata) {
            $model->deleteAllByAttributes([
                'collect_id' => $metadata['collect_id'],
                'data_id' => $metadata['data_id'],
                'data_expr' => $metadata['data_expr'],
                'gpzs_id' => $metadata['gpzs_id']
            ]);
        }
    }
}
