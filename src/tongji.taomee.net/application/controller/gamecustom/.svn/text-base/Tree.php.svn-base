<?php
class Tree extends gamecustom_Custom
{
    /**
     * 搜索过滤的字符
     */
    private $_searchValue;

    public function actions()
    {
        $marker = TM::createComponent(array('class' => 'application.components.DateMarker'));
        $aDate = $marker->getDateMark();
        return array(
            'index' => array(
                'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
                'to'   => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d'),
                'game_id' => $this->getDefaultGameId()
            ),
            'export' => array(
                'from' => null,
                'to'   => null,
                'period' => 1,
                'yoy'    => 0,
                'qoq'    => 0,
                'contrast'  => 0,
                'average'   => 0,
                'rate2data' => 0,
                'game_id'   => null,
                'gpzs_id'   => null,
                'node_id'   => null,
                'is_multi'  => 0,
                'by_item'   => 0,
                'file_name' => null,
                'searchValue' => null
            ),
            'getTree' => array('parent_id' => 0, 'game_id' => $this->getDefaultGameId()),
            'setName' => array('id' => null, 'name' => null),
            'addNode' => array('parent_id' => null, 'name' => null, 'game_id' => null),
            'delNode' => array('id' => null),
            'moveNode'  => array('id' => null, 'parent_id' => null, 'after_id' => null),
            'mergeNode' => array('id' => null),
            'moveStatItem' => array('id' => null, 'parent_id' => null),
            'getManageTree' => array('parent_id' => 0, 'game_id' => $this->getDefaultGameId()),
            'search' => array('game_id' => null, 'keyword' => null)
        );
    }

    public function index($aUserParameters)
    {
        $game = new common_Game();
        // 检查是否有游戏的管理权限
        if ($game->hasGameManageAuth($aUserParameters['game_id'])) {
            $this->assign('manage_auth', true);
		}
		//检查是否有自定义查询权限
        $games = $game->findByFuncMask(common_Game::SELF_SEARCH_MASK);
        if (in_array($aUserParameters['game_id'], TMArrayHelper::column($games, 'game_id'))) {
            $this->assign('cartflag', 1);
        }
        $model = new common_GpzsInfo();
        $model->attributes = $aUserParameters;
        $aPlatform = $model->getPlatform();
        $this->assign('platform', $aPlatform);
        if (($key = key($aPlatform)) !== false) {
            $model->platform_id = $aPlatform[$key]['platform_id'];
        }
        $this->assign('zone_server', $model->getZoneServer());
        $this->display('gamecustom/view.html');
    }

    public function getTree($aUserParameters)
    {
        $model = new gamecustom_Tree();
        $this->ajax(0, $model->getTree($aUserParameters));
    }

    public function setName($aUserParameters)
    {
        $model = new gamecustom_Tree();
        $this->ajax(0, $model->setName($aUserParameters));
    }

    public function addNode($aUserParameters)
    {
        (new common_Game())->checkGameManageAuth($aUserParameters['game_id']);
        $model = new gamecustom_Tree();
        $this->ajax(0, $model->addNode($aUserParameters));
    }

    public function delNode($aUserParameters)
    {
        $model = new gamecustom_Tree();
        $this->ajax(0, $model->delNode((array)$aUserParameters['id']));
    }

    public function moveNode($aUserParameters)
    {
        $model = new gamecustom_Tree();
        $this->ajax(0, $model->moveNode($aUserParameters));
    }

    public function mergeNode($aUserParameters)
    {
        $model = new gamecustom_Tree();
        $this->ajax(0, $model->mergeNode($aUserParameters));
    }

    public function moveStatItem($aUserParameters)
    {
        $model = new gamecustom_Tree();
        $aRIds = (array)$aUserParameters['id'];
        foreach ($aRIds as $rId) {
            $parts = explode('_', $rId, 2);
            if (!isset($parts[1])) {
                $aUserParameters['type'] = 'report';
                $aUserParameters['id'] = $parts[0];
            } else {
                $aUserParameters['type'] = $parts[0];
                $aUserParameters['id'] = $parts[1];
            }
            $model->moveStatItem($aUserParameters);
        }
        $this->ajax(0);
    }

    public function getManageTree($aUserParameters)
    {
        (new common_Game())->checkGameManageAuth($aUserParameters['game_id']);
        $model = new gamecustom_Tree();
        $this->ajax(0, $model->getManageTree($aUserParameters));
    }

    private function _filterBySearch($v)
    {
        return false !== strstr($v['data_name'], $this->_searchValue);
    }

    public function export($aUserParameters)
    {
        $this->initExporter($aUserParameters);
        $period = data_time_PeriodFactory::createPeriod($aUserParameters['from'], $aUserParameters['to']);
        $aTimePoints = $period->getPoints();

        $model = new data_Data();
        $aUserParameters['by_data_expr'] = 1;
        $aUserParameters['group'] = array();
        // 搜索
        $searchValue = null;
        if ($aUserParameters['searchValue']) {
            $searchValue = $this->_searchValue = $aUserParameters['searchValue'];
        }
        $aContentList = parent::getContentList($aUserParameters);
        $isMulti = (int)$aUserParameters['is_multi'];
        if (isset($aContentList[$isMulti])) {
            $iCount = 0;
            foreach ($aContentList[$isMulti] as $idx => $r) {
                $info = array('type' => $r['type'], 'r_id' => $r['r_id']);
                if ($isMulti) {
                    $exprInfo = $model->r2dataExpr($info);
                    // 搜索
                    $searchValue && ($exprInfo = array_filter($exprInfo, array($this, '_filterBySearch')));
                    if (!$exprInfo) continue;
                    $iCount += count($exprInfo);
                    $aUserParameters['group'][$idx] = array(
                        'group_name' => $r['r_name'],
                        'data_info'  => $exprInfo
                    );
                    continue;
                }
                // 搜索
                if (isset($searchValue) && false === strstr($r['r_name'], $searchValue)) {
                    continue;
                }
                $iCount += 1;
                $aUserParameters['group'][0]['data_info'][] = array_merge($info, array(
                    'range'     => '',
                    'data_name' => $r['r_name']
                ));
            }
            $this->gotoExportFile(
                [
                    'rows' => $iCount,
                    'cols' => count($aTimePoints['key'])
                ],
                $aUserParameters,
                ['gamecustom', $isMulti, $aUserParameters['node_id']]
            );
        }

        $this->oExporter->add($this->sFilename);
        foreach ($aUserParameters['group'] as $group) {
            $aUserParameters['data_info'] = $group['data_info'];
            if ($aData = data_Data::model()->getTimeSeries($aUserParameters)) {
                if (isset($group['group_name'])) $this->oExporter->putWithTitle($group['group_name']);
                $this->writeTimeSeries($aData);
                $this->oExporter->put();
            }
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    public function search($aUserParameters)
    {
        $this->ajax(0, (new gamecustom_Tree())->search($aUserParameters));
    }
}
