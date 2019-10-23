<?php
class ExportCommand extends TMDaemonCommand
{
    /**
     * @var integer the interval in seconds to check if there is a new task.
     */
    public $scanInterval = 120;

    /**
     * @var integer the max count of cells that can be obtained each time.
     */
    public $cellLimit = 100000;

    /**
     * @var integer the max count of worker.
     */
    public $maxWorkerCount = 1;

    /**
     * @var CsvExporter
     */
    protected $oExporter = null;

    /**
     * @var array handlers for exporting.
     */
    protected $aHandlers = array(
        'gamecustom.0' => 'handleData', // is_multi = 0
        'gamecustom.1' => 'handleData', // is_multi = 1
        'home.collect' => 'handleCollect',
        'home.favor' => 'handleFavor',
        'economy.itemsale' => 'handleItemSale'
    );

    /**
     * @var string message.
     */
    protected $sMessage = '';

    /**
     * @var boolean whether to write head.
     */
    protected $bHead = true;

    /**
     * Init exporter.
     * @return CsvExporter
     */
    protected function initExporter()
    {
        if (isset($this->oExporter)) return $this->oExporter;

        return $this->oExporter = TM::createComponent(array(
            'class' => 'application.components.CsvExporter',
            'defaultDirname' => (new tool_file_Export())->getFilePath()
        ));
    }

    /**
     * Get a task.
     * @return array
     */
    protected function getTask()
    {
        return (new tool_file_Export())->findOne();
    }

    /**
     * Callback before work.
     * @param array $task
     */
    protected function beforeWork($task)
    {
        $file = new tool_file_Export();
        $file->file_id = $task['file_id'];
        $file->status = tool_File::ST_GEN;
        $file->begin_time = date('Y-m-d H:i:s');
        $file->update(['status', 'begin_time']);
    }

    /**
     * Work.
     * @param array $task
     */
    protected function work($task)
    {
        $aUserParam = $task['params'];
        unset($task['params']);

        $sType = $this->type($task);
        if (isset($this->aHandlers[$sType])) {
            return $this->{$this->aHandlers[$sType]}($task, $aUserParam);
        }

        $this->sMessage = TM::t('tongji', '未知的文件导出类型！');
        return false;
    }

    /**
     * Get the type of the task.
     * @param array $task
     * @return string
     */
    protected function type($task)
    {
        $aType = explode('.', $task['file_key'], 3);
        $sType = $aType[0] . '.' . $aType[1];
        unset($aType);
        return $sType;
    }

    /**
     * Callback when the work succeeds.
     * @param array $task
     */
    protected function afterSucceed($task)
    {
        $file = new tool_file_Export();
        $file->file_id = $task['file_id'];
        $file->status = tool_File::ST_DONE;
        $file->end_time = date('Y-m-d H:i:s');
        $file->progress = 100;
        $file->update(['status', 'end_time', 'progress']);
    }

    /**
     * Callback when the work fails.
     * @param array $task
     */
    protected function afterFail($task)
    {
        $file = new tool_file_Export();

        $f = $file->getFilePath() . DIRECTORY_SEPARATOR . $task['file_key'];
        if (file_exists($f)) @unlink($f);

        $file->file_id = $task['file_id'];
        $file->status = tool_File::ST_ERR;
        $file->end_time = date('Y-m-d H:i:s');
        $file->message = $this->sMessage;
        $file->progress = 0;
        $file->update(['status', 'end_time', 'message', 'progress']);
    }

    /**
     * Clean before and after each work.
     */
    protected function clean()
    {
        TM::app()->db->setActive(false);
    }

    /**
     * Write data into current file.
     * @param array $aData
     */
    protected function writeData($aData)
    {
        if ($this->bHead) {
            $this->oExporter->putWithTitle(TM::t('tongji', '日期'), $aData['key']);
            $this->bHead = false;
        }
        foreach ($aData['data'] as &$data) {
            $this->oExporter->putWithTitle($data['name'], $data['data']);
        }
    }

    /* ------ file exporting handlers ------ */

    protected function handleData($task, $aUserParam)
    {
        $this->initExporter()->add($task['file_key']);
        $step = count($aUserParam['group']);
        foreach ($aUserParam['group'] as $idx => $group) {
            if (isset($group['group_name'])) $this->oExporter->putWithTitle($group['group_name']);
            $group['data_info'] = array_chunk($group['data_info'], floor($this->cellLimit / $aUserParam['columns']));
            $this->bHead = true;
            foreach ($group['data_info'] as $dataInfo) {
                $aUserParam['data_info'] = $dataInfo;
                if ($aData = data_Data::model()->getTimeSeries($aUserParam)) {
                    $this->writeData($aData[0]);
                }
            }
            $this->oExporter->put();
            $this->setProgress($task['file_id'], (int)(($idx + 1) / $step * 100));
        }
        return true;
    }

    protected function handleCollect($task, $aUserParam)
    {
        $this->initExporter()->add($task['file_key']);
        $aDataInfo = array_chunk($aUserParam['data_info'], floor($this->cellLimit / $aUserParam['columns']));
        $step = count($aDataInfo);
        foreach ($aDataInfo as $idx => $dataInfo) {
            $aUserParam['data_info'] = $dataInfo;
            if ($aData = data_Data::model()->getTimeSeries($aUserParam)) {
                $this->writeData($aData[0]);
                $this->setProgress($task['file_id'], (int)(($idx + 1) / $step * 100));
            }
        }
        return true;
    }

    protected function handleFavor($task, $aUserParam)
    {
        $aCollects = $aUserParam['collects'];
        unset($aUserParam['collects']);
        $this->initExporter()->setTempDirname();
        $step = count($aCollects);
        foreach ($aCollects as $idx => $collect) {
            $this->oExporter->add($collect['collect_name'], true);
            $this->bHead = true;
            $collect['data_info'] = array_chunk($collect['data_info'], floor($this->cellLimit / $aUserParam['columns']));
            foreach ($collect['data_info'] as $dataInfo) {
                $aUserParam['data_info'] = $dataInfo;
                if ($aData = data_Data::model()->getTimeSeries($aUserParam)) {
                    $this->writeData($aData[0]);
                }
            }
            $this->setProgress($task['file_id'], (int)(($idx + 1) / $step * 100));
        }
        $this->oExporter->pack($task['file_key']);
        return true;
    }

    protected function handleItemSale($task, $aUserParam)
    {
        $this->initExporter()->setTempDirname();
        $cachePrefix = $this->oExporter->getTempDirname();
        $model = new gameanalysis_Data();
        $period = $model->getPeriod($aUserParam);
        $this->writeItemSaleList($aUserParam, $period, $cachePrefix);
        $this->setProgress($task['file_id'], 50);
        $this->writeCategorySaleList($aUserParam, $period, $cachePrefix);
        $this->oExporter->pack($task['file_key']);
        return true;
    }

    protected function writeItemSaleList($aUserParam, $period, $cachePrefix)
    {
        $model = new gameanalysis_ItemSale();
        $aSaleData = $model->getSaleData($aUserParam, $period);

        $aItems = $model->getItemIds();
        $aSaleDetail = $model->getSaleDetailData($aUserParam, $aItems, $period, gameanalysis_ItemSale::MASK_MONEY);
        $aTitle = $aSaleDetail[key($aSaleDetail)]['key'];
        array_push($aTitle, 'data_name', TM::t('tongji', '道具名称'), TM::t('tongji', '道具ID'));
        $aTitle = array_reverse($aTitle);
        $this->oExporter->add(TM::t('tongji', '按道具'), true);
        $cache = TM::app()->getCache();
        foreach ($aSaleData['data'] as $i => $value) {
            $aTitle[2] = $value['name'];
            $this->oExporter->put($aTitle, CsvExporter::ENCODE_PREV, 3);
            foreach ($aItems as $idx => $itemId) {
                $aLine = $aSaleDetail[$itemId]['data'][$i]['data'];
                array_push($aLine, $value['data'][$idx], $aSaleData['key'][$idx], $itemId);
                $aLine = array_reverse($aLine);
                $this->oExporter->put($aLine, CsvExporter::ENCODE_PREV, 2);
                $cache->set(sprintf('ia.%s.%s.%s', $cachePrefix, $i, $itemId), $aLine);
            }
            $this->oExporter->put();
        }
    }

    protected function writeCategorySaleList($aUserParam, $period, $cachePrefix)
    {
        $model = new gameanalysis_ItemCategorySale();
        $aUserParam['parent_id'] = 0;
        $aSaleList = $model->getSaleList($aUserParam, $period, false);
        if (!$aSaleList) return;

        $oRel = new gameanalysis_ItemCategoryRel();
        $oRel->game_id = $aUserParam['game_id'];
        $oRel->sstid = $aUserParam['sstid'];
        $cache = TM::app()->getCache();
        $aCategory = $aCategoryItem = $aCategoryName = array();
        foreach ($aSaleList as $cat1) {
            $cat1Name = array($cat1['category_name'], '');
            $cache->set(sprintf('cf.%s.%s.%s', $cachePrefix, 0, $cat1['category_id']),
                array_merge($cat1Name, array('', '', $cat1['_salemoney'])));
            $cache->set(sprintf('cf.%s.%s.%s', $cachePrefix, 1, $cat1['category_id']),
                array_merge($cat1Name, array('', '', $cat1['_salenum'])));
            $aCategory[] = $cat1['category_id'];
            if ((int)$cat1['is_leaf'] === gameanalysis_ItemCategory::TYPE_NONLEAF) {
                $aUserParam['parent_id'] = $cat1['category_id'];
                $aSaleList2 = $model->getSaleList($aUserParam, $period, false);
                if (!$aSaleList2) continue;
                foreach ($aSaleList2 as $cat2) {
                    $cat2Name = array($cat1['category_name'], $cat2['category_name']);
                    $cache->set(sprintf('cf.%s.%s.%s', $cachePrefix, 0, $cat2['category_id']),
                        array_merge($cat2Name, array('', '', $cat2['_salemoney'])));
                    $cache->set(sprintf('cf.%s.%s.%s', $cachePrefix, 1, $cat2['category_id']),
                        array_merge($cat2Name, array('', '', $cat2['_salenum'])));
                    $aCategory[] = $cat2['category_id'];
                    $oRel->category_id = $cat2['category_id'];
                    $aCategoryItem[$oRel->category_id] = TMArrayHelper::column($oRel->getCategoryItem(), 'item_id');
                    $aCategoryName[$oRel->category_id] = $cat2Name;
                }
            } else {
                $oRel->category_id = $cat1['category_id'];
                $aCategoryItem[$oRel->category_id] = TMArrayHelper::column($oRel->getCategoryItem(), 'item_id');
                $aCategoryName[$oRel->category_id] = $cat1Name;
            }
        }

        $aSaleDetail = $model->getSaleDetailData($aUserParam, $aCategory, $period);
        $aTitle = $aSaleDetail[key($aSaleDetail)]['key'];
        array_push($aTitle, 'data_name',
            TM::t('tongji', '道具名称'), TM::t('tongji', '道具ID'), TM::t('tongji', '二级类别'), TM::t('tongji', '一级类别'));
        $aTitle = array_reverse($aTitle);
        $aDataName = array(TM::t('tongji', '销售金额'), TM::t('tongji', '销售数量'));
        $this->oExporter->add(TM::t('tongji', '按类别'), true);
        foreach ($aDataName as $i => $dataName) {
            $aTitle[4] = $dataName;
            $this->oExporter->put($aTitle, CsvExporter::ENCODE_PREV, 5);
            foreach ($aCategory as $idx => $categoryId) {
                $aLine = array_merge($cache->get(sprintf('cf.%s.%s.%s', $cachePrefix, $i, $categoryId)),
                    array_reverse($aSaleDetail[$categoryId]['data'][$i]['data']));
                $this->oExporter->put($aLine, CsvExporter::ENCODE_PREV, 4);
                if (isset($aCategoryItem[$categoryId])) {
                    foreach ($aCategoryItem[$categoryId] as $itemId) {
                        $aLine = array_merge($aCategoryName[$categoryId],
                            (array)$cache->get(sprintf('ia.%s.%s.%s', $cachePrefix, $i, $itemId)));
                        $this->oExporter->put($aLine, CsvExporter::ENCODE_PREV, 4);
                    }
                }
            }
            $this->oExporter->put(); // new line
        }
    }

    protected function setProgress($fileId, $progress)
    {
        $file = new tool_file_Export();
        $file->file_id = $fileId;
        $file->progress = $progress;
        $file->update(['progress']);
    }
}
