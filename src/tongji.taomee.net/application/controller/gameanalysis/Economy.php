<?php
abstract class gameanalysis_Economy extends gameanalysis_Analysis
{
    public function actions()
    {
        return array_merge(parent::actions(), array(
            // 道具数据
            'getItemSaleTop' => array('top' => 10, 'factor' => 1), // Has export function
            'getItemSaleListTotal' => array('category_id' => null, 'factor' => 1),
            'getItemSaleList' => array('category_id' => null, 'factor' => 1, 'start' => 0, 'end' => 100),
            'getItemSaleDetail' => array('item_id' => null, 'factor' => 1),
            // 道具管理
            'item' => array(),
            'getItemListTotal' => array(),
            'getItemList' => array('start' => 0, 'end' => 100, 'pagination' => false),
            'setName' => array('id' => null, 'name' => ''),
            'setHide' => array('id' => null, 'hide' => 0),
            'importItem' => array(),
            // 道具类别管理
            'category' => array(),
            'addCategory' => array('category_name' => '', 'parent_id' => 0),
            'delCategory' => array('category_id' => array()),
            'setCategory' => array('id' => null, 'name' => ''),
            'moveCategory' => array('category_id' => null, 'parent_id' => null),
            'getCategoryListTotal' => array('parent_id' => 0),
            'getCategoryList' => array('parent_id' => 0, 'start' => 0, 'end' => 100, 'pagination' => true),
            'getItemCategory' => array('item_id' => null),
            'setItemCategory' => array('item_id' => null, 'category_id' => null, 'append' => 0),
            'getCategoryItem' => array('category_id' => null),
            'setCategoryItem' => array('item_id' => null, 'category_id' => null, 'append' => 0),
            // 道具类别数据
            'getCategorySaleListTotal' => array('parent_id' => 0, 'factor' => 1),
            'getCategorySaleList' => array('parent_id' => 0, 'factor' => 1, 'start' => 0, 'end' => 100),
            'getCategorySaleDetail' => array('category_id' => null, 'factor' => 1),
            // 道具及类别数据下载
            'export' => array('factor' => 1)
        ));
    }

    public function commonParameters()
    {
        return array_merge(parent::commonParameters(), array('sstid' => null, 'gpzs_id' => null));
    }

    public function getItemSaleTop($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $model = new gameanalysis_ItemSale();
        $period = $model->getPeriod($aUserParameters);
        if ($aUserParameters['export']) {
            $this->exportSaleData($aUserParameters, $period);
        }

        if (!$period) $this->ajax(0);
        return $this->ajax(0, array($model->getSaleData($aUserParameters, $period)));
    }

    protected function exportSaleData($aUserParameters, $period)
    {
        $this->initExporter($aUserParameters);
        $this->oExporter->add($this->sFilename);
        $model = new gameanalysis_ItemSale();
        $aSaleData = $model->getSaleData($aUserParameters, $period);
        if (!$aSaleData) {
            $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
        }

        $aSaleDetail = $model->getSaleDetailData(
            $aUserParameters,
            $model->getItemIds(),
            $period,
            gameanalysis_ItemSale::MASK_MONEY
        );

        $aTitle = array(TM::t('tongji', '道具ID'), TM::t('tongji', '道具名称'));
        foreach ($aSaleData['data'] as $data) {
            $aTitle[] = $data['name'];
        }
        $aTitle = array_merge($aTitle, array_reverse($aSaleDetail[key($aSaleDetail)]['key']));
        $this->oExporter->put($aTitle, CsvExporter::ENCODE_PREV, count($aSaleData['data']) + 2);

        $iIdx = 0;
        foreach ($aSaleDetail as $item => $value) {
            $aLine = array($item, $aSaleData['key'][$iIdx]);
            foreach ($aSaleData['data'] as $data) {
                $aLine[] = $data['data'][$iIdx];
            }
            $aLine = array_merge($aLine, array_reverse($value['data'][0]['data']));
            $this->oExporter->put($aLine, CsvExporter::ENCODE_PREV, 2);
            ++$iIdx;
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    public function getItemSaleListTotal($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $model = new gameanalysis_ItemSale();
        $period = $model->getPeriod($aUserParameters);

        if (!$period) $this->ajax(0);
        $this->ajax(0, $model->getSaleListTotal($aUserParameters, $period));
    }

    public function getItemSaleList($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $model = new gameanalysis_ItemSale();
        $period = $model->getPeriod($aUserParameters);

        if (!$period) $this->ajax(0);
        $this->ajax(0, $model->getSaleList($aUserParameters, $period));
    }

    public function getItemSaleDetail($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $model = new gameanalysis_ItemSale();
        $period = $model->getPeriod($aUserParameters);
        if (!$period) $this->ajax(0);
        $aDetail = $model->getSaleDetailData($aUserParameters, array($aUserParameters['item_id']), $period);
        if (!$aDetail) return $this->ajax(0);
        return $this->ajax(0, array_values($aDetail));
    }

    public function getCategorySaleListTotal($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $model = new gameanalysis_ItemCategorySale();
        $period = $model->getPeriod($aUserParameters);

        if (!$period) $this->ajax(0);
        $this->ajax(0, $model->getSaleListTotal($aUserParameters, $period));
    }

    public function getCategorySaleList($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $model = new gameanalysis_ItemCategorySale();
        $period = $model->getPeriod($aUserParameters);

        if (!$period) $this->ajax(0);
        $this->ajax(0, $model->getSaleList($aUserParameters, $period));
    }

    public function getCategorySaleDetail($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $model = new gameanalysis_ItemCategorySale();
        $period = $model->getPeriod($aUserParameters);
        if (!$period) $this->ajax(0);
        $aDetail = $model->getSaleDetailData($aUserParameters, array($aUserParameters['category_id']), $period);
        if (!$aDetail) return $this->ajax(0);
        return $this->ajax(0, array_values($aDetail));
    }

    public function export($aUserParameters)
    {
        $aUserParameters = $this->adjustFactor($aUserParameters);

        $this->initExporter($aUserParameters);
        $model = new gameanalysis_ItemSale();
        $period = $model->getPeriod($aUserParameters);
        TMValidator::ensure($period, TM::t('tongji', '时间段选择不正确！'));
        $aSaleData = $model->getSaleData($aUserParameters, $period, false);
        TMValidator::ensure($aSaleData, TM::t('tongji', '数据为空！'));

        $aPoints = $period->getPoints();
        $this->gotoExportFile(
            [
                'rows' => count($aSaleData['key']) * 4, // （销售数量 + 销售金额） × （类别 + 道具）,
                'cols' => count($aPoints['key']),
                'pageCellLimit' => 0 // always go to my download
            ],
            $aUserParameters,
            ['economy', 'itemsale', $aUserParameters['sstid']]
        );
    }

    protected function adjustFactor($aUserParameters)
    {
        // 页游游戏币销售金额乘0.01
        if ($aUserParameters['sstid'] === '_coinsbuyitem_' &&
                $this->sGameType === 'webgame') {
            $aUserParameters['factor'] = 0.01;
        }

        return $aUserParameters;
    }

    // ------ for management ------

    /**
     * 道具管理
     */
    public function item($aUserParameters)
    {
        $this->assignIgnore($aUserParameters);
        $this->display('gameanalysis/mbmanage.html');
    }

    /**
     * 道具类别管理
     */
    public function category($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategory();
        if (TM::app()->getUser()->isAdmin()) {
            $this->assign('admin_auth', true);
		}
        $this->assign('category', $oModel->findAll(array(
            'condition' => array(
                'game_id' => $aUserParameters['game_id'],
                'sstid' => $aUserParameters['sstid'],
                'parent_id' => 0
            )
        )));
        $this->display('gameanalysis/category.html');
    }

    /**
     * 道具销售
     */
    public function index($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/mbsale.html');
    }

    /**
     * 取道具列表数量
     */
    public function getItemListTotal($aUserParameters)
    {
        $model = new gameanalysis_Item();
        $this->ajax(0, $model->getListTotal($aUserParameters));
    }

    /**
     * 取道具列表
     */
    public function getItemList($aUserParameters)
    {
        $model = new gameanalysis_Item();
        $this->ajax(0, $model->getList($aUserParameters));
    }

    /**
     * 设置道具名称
     */
    public function setName($aUserParameters)
    {
        $model = new gameanalysis_Item();
        $this->ajax(0, $model->setName($aUserParameters));
    }

    /**
     * 设置道具是否隐藏
     */
    public function setHide($aUserParameters)
    {
        $model = new gameanalysis_Item();
        $this->ajax(0, $model->setHide($aUserParameters));
    }

    /**
     * Import item names.
     *
     * @param  array  $aUserParameters
     */
    public function importItem($aUserParameters)
    {
        TMValidator::ensure($aUserParameters['game_id'], TM::t('tongji', '上传道具名称必须指定游戏！'));
        TMValidator::ensure(in_array($aUserParameters['sstid'], array('_coinsbuyitem_', '_mibiitem_')),
            TM::t('tongji', '道具类型不合法！'));
        $file = $this->uploadItem($aUserParameters);
        $reader = new XmlReader();
        TMValidator::ensure($reader->open($file), TM::t('tongji', '文件不能正常打开！'));
        $oModel = new gameanalysis_Item();
        $itemIdAttrs = array('product_id', 'commodityID', 'productID');
        $itemNameAttrs = array('Name', 'name');
        while ($reader->read()) {
            if ($reader->nodeType !== XMLReader::ELEMENT || strtolower($reader->name) !== 'item') {
                continue;
            }
            $itemId = $itemName = null;
            foreach ($itemIdAttrs as $attr) {
                if (($itemId = $reader->getAttribute($attr)) !== null) {
                    break;
                }
            }
            foreach ($itemNameAttrs as $attr) {
                if (($itemName = $reader->getAttribute($attr)) !== null) {
                    break;
                }
            }
            if ($itemId === null || $itemName === null) {
                continue;
            }
            $oModel->replace(array(
                'game_id' => $aUserParameters['game_id'],
                'sstid' => $aUserParameters['sstid'],
                'item_id' => $itemId,
                'item_name' => $itemName,
                'hide' => 0
            ));
        }
        $this->ajax(0);
    }

    /**
     * Upload item file.
     *
     * @param  array  $aUserParameters
     * @return string The item path
     */
    protected function uploadItem($aUserParameters)
    {
        $path = $this->getItemPath();
        TMUploadFile::register();
        $file = new TMUploadFile('files', new TMUploadFileSystem($path, true));
        $file->setName($aUserParameters['game_id'] . $aUserParameters['sstid'] . time());
        $file->addValidators(array(
            new TMUploadMimetypeValidator(array('application/xml', 'text/plain')),
            new TMUploadExtensionValidator('xml')))
        ->upload();

        return $path . $file->getNameWithExtension();
    }

    /**
     * Get path for item files, create it if not exists.
     *
     * @return string      The item path.
     * @throws TMException If fails to get the path.
     */
    protected function getItemPath()
    {
        $path = rtrim(TM::app()->getRuntimePath(), DS) . DS . 'item' . DS;
        if (TMFileHelper::mkdir($path) === false) {
            throw new TMException(TM::t('tongji', '道具目录{dir}不可写！', array('{dir}' => $path)));
        }

        return $path;
    }

    /**
     * 增加道具类别
     */
    public function addCategory($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategory();
        $oModel->attributes = $aUserParameters;
        $oModel->insert();
        $this->ajax(0, $oModel->category_id);
    }

    /**
     * 删除道具类别
     */
    public function delCategory($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategory();
        $oModel->game_id = $aUserParameters['game_id'];
        $oModel->sstid = $aUserParameters['sstid'];
        $categoryIds = (array)$aUserParameters['category_id'];

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            foreach ($categoryIds as $categoryId) {
                $oModel->category_id = $categoryId;
                $oModel->delete();
            }
            $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }

        $this->ajax(0);
    }

    /**
     * 设置道具类别信息
     */
    public function setCategory($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategory();
        $oModel->category_id = $aUserParameters['id'];
        $oModel->category_name = $aUserParameters['name'];
        $this->ajax(0, $oModel->update(array('category_name')));
    }

    /**
     * 移动道具类别
     */
    public function moveCategory($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategory();
        $oModel->attributes = $aUserParameters;

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            $oModel->move();
            $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
        $this->ajax(0);
    }

    /**
     * 获取道具分类条数
     */
    public function getCategoryListTotal($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategory();
        $this->ajax(0, count($oModel->findAll(array(
            'condition' => array(
                'game_id' => $aUserParameters['game_id'],
                'sstid' => $aUserParameters['sstid'],
                'parent_id' => $aUserParameters['parent_id']
            )
        ))));
    }

    /**
     * 获取道具分类
     */
    public function getCategoryList($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategory();
        $params = array(
            'condition' => array(
                'game_id' => $aUserParameters['game_id'],
                'sstid' => $aUserParameters['sstid'],
                'parent_id' => $aUserParameters['parent_id']
            )
        );
        if ($aUserParameters['pagination']) {
            $params = array_merge($params, array(
                'limit' => (int)$aUserParameters['end'],
                'offset' => (int)$aUserParameters['start']
            ));
        }
        $this->ajax(0, $oModel->findAll($params));
    }

    /**
     * 获取道具的所有类别
     */
    public function getItemCategory($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategoryRel();
        $oModel->game_id = $aUserParameters['game_id'];
        $oModel->sstid = $aUserParameters['sstid'];
        $oModel->item_id = $aUserParameters['item_id'];
        $this->ajax(0, $oModel->getItemCategory());
    }

    /**
     * 设置道具类别
     */
    public function setItemCategory($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategoryRel();
        $oModel->game_id = $aUserParameters['game_id'];
        $oModel->sstid = $aUserParameters['sstid'];
        $oModel->item_id = $aUserParameters['item_id'];

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            $oModel->setItemCategory((array)$aUserParameters['category_id'], $aUserParameters['append']);
            $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }

        $this->ajax(0);
    }

    /**
     * 获取类别的所有道具
     */
    public function getCategoryItem($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategoryRel();
        $oModel->game_id = $aUserParameters['game_id'];
        $oModel->sstid = $aUserParameters['sstid'];
        $oModel->category_id = $aUserParameters['category_id'];
        $this->ajax(0, $oModel->getCategoryItem());
    }

    /**
     * 设置类别道具
     */
    public function setCategoryItem($aUserParameters)
    {
        $oModel = new gameanalysis_ItemCategoryRel();
        $oModel->game_id = $aUserParameters['game_id'];
        $oModel->sstid = $aUserParameters['sstid'];
        $categoryIds = (array)$aUserParameters['category_id'];

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            foreach ($categoryIds as $categoryId) {
                $oModel->category_id = $categoryId;
                $oModel->setCategoryItem((array)$aUserParameters['item_id'], $aUserParameters['append']);
            }
            $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }

        $this->ajax(0);
    }
}
