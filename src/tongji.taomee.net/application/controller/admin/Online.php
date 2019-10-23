<?php
class Online extends TMController
{
    public function actions()
    {
        return array(
            'index' => array(),
            'getOnlineList' => array(
                'game_id' => null
            ),
            'save' => array(
                'game_id' => null,
                'data' => array()
            ),
            'build' => array()
        );
    }

    public function index($aUserParameters)
    {
        $this->assign('games', (new common_Game())->findByFuncMask(common_Game::ONLINE_MASK));
        $this->display('conf/online.html');
    }

    public function getOnlineList($aUserParameters)
    {
        $aUserParameters['game_id'] = (int)$aUserParameters['game_id'];
        $aDataInfo = $this->_getDataInfo($aUserParameters['game_id']);
        if (!$aDataInfo) $this->ajax(0);
        $aGpzsInfo = $this->_getGpzsInfo($aUserParameters['game_id']);
        if (!$aGpzsInfo) $this->ajax(0);

        $aOnlineConfig = $this->_getOnlineConfig($aUserParameters['game_id']);
        $aOnlineList = array();
        foreach ($aDataInfo as $dataInfo) {
            foreach ($aGpzsInfo as $gpzsInfo) {
                $key = $dataInfo['data_id'] . ':' . $gpzsInfo['gpzs_id'];
                $each = $this->_createOnline($gpzsInfo['gpzs_name'] . $dataInfo['data_name'],
                    $dataInfo['data_id'], $gpzsInfo['gpzs_id'], $dataInfo['sthash']);
                if (isset($aOnlineConfig[$key])) {
                    $each = $this->_setOnline($each, $aOnlineConfig[$key]);
                }
                $aOnlineList[] = $each;
            }
        }

        if (count($aOnlineList) > 1) {
            $sum = $this->_createOnline(TM::t('tongji', '累计值'), 0, 0, 0);
            if (isset($aOnlineConfig['0:0'])) {
                $sum = $this->_setOnline($sum, $aOnlineConfig['0:0']);
            }
            $aOnlineList[] = $sum;
        }

        $this->ajax(0, $aOnlineList);
    }

    public function save($aUserParameters)
    {
        $aGame = $this->_getOnlineGame((int)$aUserParameters['game_id']);
        $oOnlineConfig = new common_OnlineConfig();
        $oOnlineConfig->deleteAllByAttributes(array('game_id' => $aGame['game_id']));

        if (!is_array($aUserParameters['data']) || !$aUserParameters['data']) {
            $this->ajax(0);
        }

        $oOnlineConfig->game_id = $aGame['game_id'];
        foreach ($aUserParameters['data'] as $config) {
            $oOnlineConfig->attributes = $config;
            $oOnlineConfig->insert();
        }
        $this->ajax(0);
    }

    public function build($aUserParameters)
    {
        $aOnlineConfig = (new common_OnlineConfig())->getGameIdGroupedList();
        $aConfig = array();
        foreach ($aOnlineConfig as $gameId => $gameConfig) {
            if (!$gameConfig) continue;
            $each = array('game_name' => $gameConfig[0]['game_name'], 'summary' => array(), 'data_list' => array(), 'gameId' => $gameId);
            if ($gameConfig[0]['online_auth_id']) $each['auth_id'] = $gameConfig[0]['online_auth_id'];
            $keyOfSum = null;
            $sumKeys = array();
            $idx = 1;
            foreach ($gameConfig as $config) {
                $key = $config['is_all'] ? ($gameId << 8) : (($gameId << 8) + $idx++);
                $data = array('data_name' => $config['show_name']);
                if ($config['auth_id']) $data['auth_id'] = $config['auth_id'];
                if ($config['data_id'] === '0' && $config['gpzs_id'] === '0') {
                    $keyOfSum = $key;
                } else {
                    $data['data_id'] = $config['data_id'];
                    $data['gpzs_id'] = $config['gpzs_id'];
                    $data['sthash']  = $config['sthash'];
                    $sumKeys[] = $key;
                }
                $each['data_list'][$key] = $data;
                if ($config['in_summary']) $each['summary'][$key] = true;
                $each['order'] = $config['order'];
            }

            if (!$keyOfSum || !$sumKeys) {
                unset($each['data_list'][$keyOfSum]);
                unset($each['summary'][$keyOfSum]);
            } else {
                $each['data_list'][$keyOfSum]['data_id'] = $sumKeys;
            }

            if (!$each['data_list']) continue;
            if ($each['summary']) {
                $each['summary'] = array_keys($each['summary']);
            } else {
                unset($each['summary']);
            }
            if (!$each['order']) $each['order'] = $gameId;
            $aConfig[$gameId] = $each;
        }
        $this->ajax(0, $this->_writeOnlineConfig($aConfig));
    }

    private function _getDataInfo($iGameId)
    {
        $aReport = (new common_Report())->findByUk($iGameId, array(array(
            'stid'      => '_olcnt_',
            'sstid'     => '_olcnt_',
            'op_fields' => '_zone_,_olcnt_',
            'op_type'   => 'max'
        )));
        if (!$aReport) return;

        $aReport = array_pop($aReport);
        return (new common_DataInfo())->getRangeByRid($aReport[0]);
    }

    private function _getGpzsInfo($iGameId)
    {
        $oGpzs = new common_GpzsInfo();
        $oGpzs->game_id = $iGameId;

        // 分服游戏
        $aException = array_fill_keys(array(82, 606, 607), true);
        if (isset($aException[$iGameId])) {
            $oGpzs->platform_id = $oGpzs->zone_id = -1;
            $oGpzs->server_id = null;
        } else {
            $oGpzs->platform_id = $oGpzs->zone_id = $oGpzs->server_id = null;
        }

        $aGpzsInfo = null;
        try {
            $aGpzsInfo = $oGpzs->getList();
        } catch (TMException $e) {
            // do nothing
        }
        return $aGpzsInfo;
    }

    private function _getOnlineConfig($iGameId)
    {
        $aOnlineConfig = (new common_OnlineConfig())->findAll(array(
            'condition' => array(
                'game_id' => $iGameId
            ),
            'order' => 'position ASC'
        ));

        $aConfig = array();
        foreach ($aOnlineConfig as $conf) {
            $aConfig[$conf['data_id'] . ':' . $conf['gpzs_id']] = $conf;
        }
        return $aConfig;
    }

    private function _createOnline($sDataTitle, $iDataId, $iGpzsId, $iSthash)
    {
        return array(
            'show'      => 0,
            'data_name' => $sDataTitle,
            'show_name' => '',
            'data_id'   => $iDataId,
            'gpzs_id'   => $iGpzsId,
            'sthash'    => $iSthash,
            'auth_id'   => '',
            'position'  => '',
            'in_summary'   => 0,
            'is_all'       => 0
        );
    }

    private function _setOnline($aEach, $aConfig)
    {
        $aEach['show'] = 1;
        $aEach['show_name']  = $aConfig['show_name'];
        $aEach['auth_id']    = $aConfig['auth_id'];
        $aEach['position']   = $aConfig['position'];
        $aEach['in_summary'] = $aConfig['in_summary'];
        $aEach['is_all']     = $aConfig['is_all'];
        return $aEach;
    }

    private function _getOnlineGame($iGameId)
    {
        $aGame = (new common_Game())->findByFuncMask(common_Game::ONLINE_MASK);
        $aGame = TMArrayHelper::column($aGame, null, 'game_id');
        TMValidator::ensure(isset($aGame[$iGameId]), TM::t('tongji', '游戏不存在或者不能加入在线统计！'));
        return $aGame[$iGameId];
    }

    private function _sort($a, $b)
    {
        return $a['order'] > $b['order'];
    }

    private function _writeOnlineConfig($aConfig)
    {
        $aConfig += $this->_getStatOnlineConfig();
        if (!$aConfig) return;
        usort($aConfig, array($this, '_sort'));

        if (($content = var_export($aConfig, true)) === null) {
            TMValidator::ensure(false, TM::t('tongji', '导出配置时出错！'));
        }
        $content = '<?php return ' . $content . ';';
        $file = $this->_getOnlineConfigFile();
        if (file_put_contents($file, $content) === false) {
            throw new TMException(TM::t('tongji', '写入文件{file}错误！', array('{file}' => $file)));
        }
        if (($content = php_strip_whitespace($file)) === '') {
            throw new TMException(TM::t('tongji', '压缩文件{file}错误！', array('{file}' => $file)));
        }
        if (file_put_contents($file, $content) === false) {
            throw new TMException(TM::t('tongji', '写入文件{file}错误！', array('{file}' => $file)));
        }
        return $file;
    }

    private function _getStatOnlineConfig()
    {
        $file = dirname(TM::app()->getBasePath()) . DS . 'online' . DS . 'config' . DS . 'stat_game.php';
        if (is_readable($file)) {
            $config = include($file);
            if (is_array($config) && $config) {
                return $config;
            }
        }
        return array();
    }

    private function _getOnlineConfigFile()
    {
        return dirname(TM::app()->getBasePath()) . DS . 'online' . DS . 'config' . DS . 'game.php';
    }
}
