<?php
/**
* @file NavigatorForExt.php
* @brief 对外服务只提供我的收藏、游戏分析、游戏自定义数据
* @author violet violet@taomee.com
* @version 1.0
* @date 2015-09-11
*/
class NavigatorForExt extends TMComponent
{
    /**
     * @var array game types.
     */
    private $_aGameTypes = array();

    /**
     * @var array navigator cache.
     */
    private $_aNavigator;

    public function init()
    {
        $this->_aGameTypes = array_fill_keys((new common_Game())->getGameType(), '');
    }

    /**
     * Get authoritiy-filtered navigator.
     * @throw TMUserException
     * @return array
     */
    public function getNavigator()
    {
        if ($this->_aNavigator) return $this->_aNavigator;
        list($aNavigator, $aUrls) = $this->getCompleteNavigatorInfo();
        // no authority
        if (empty($aNavigator)) {
            TM::app()->getUser()->setAuthorized(false)->forbidden();
        }
        // check the current route in navigator, if not exist, jump to the first enabled
        $sRoute = TM::app()->getCompleteRoute();
        $this->checkRoute($aUrls, $sRoute, $aNavigator[key($aNavigator)]['url']);
        // get the current navigator tree
        $aCurrentMap = $this->getCurrentMap($aNavigator, $aUrls, $sRoute);

        $aTopBar = $aNavigator;
        $aSecondBar = $aAside = array();
        $sGameFunction = false;
        if (isset($aCurrentMap[0], $aTopBar[$aCurrentMap[0]])) {
            if (isset($aTopBar[$aCurrentMap[0]]['children'])) {
                $aSecondBar = $aTopBar[$aCurrentMap[0]]['children'];
            }
            // Current url for game list.
            if ($aTopBar[$aCurrentMap[0]]['game_related']) {
                $sGameFunction = $aTopBar[$aCurrentMap[0]]['func_slot'];
                $this->setGameRoute($aSecondBar);
            }
        }
        if (isset($aCurrentMap[1])) {
            if (isset($aSecondBar[$aCurrentMap[1]]['children'])) {
                $aAside = $aSecondBar[$aCurrentMap[1]]['children'];
            }
        }
        $aCurrentPage = array('parent' => '', 'child' => '');

        $iChildKey  = array_pop($aCurrentMap);
        $iParentKey = array_pop($aCurrentMap);
        $aParentAside = array();
        if (isset($aAside[$iParentKey])) {
            $aParentAside = $aAside[$iParentKey];
        } else if (isset($aSecondBar[$iParentKey])) {
            $aParentAside = $aSecondBar[$iParentKey];
        }
        if ($aParentAside) {
            $aCurrentPage['parent'] = str_ireplace(array_keys($this->_aGameTypes), '', $aParentAside['key']);
            $aCurrentPage['parent_name'] = $aParentAside['name'];

            if (isset($aParentAside['children'][$iChildKey])) {
                $aCurrentPage['child'] = $aParentAside['children'][$iChildKey]['key'];
                $aCurrentPage['child_name'] = $aParentAside['children'][$iChildKey]['name'];
            }
        }
        $status = true;
        foreach ($aAside as $aside) {
            $status = $status && !$aside['is_parent'];
        }   
        if ($status) {
            $aAside = $aSecondBar;
            $aSecondBar = array();
        }  
        return $this->_aNavigator = array(
            'top_bar'      => $aTopBar,
            'second_bar'   => $aSecondBar,
            'game'         => false !== $sGameFunction ? $this->getGameNavigator($sGameFunction) : null,
            'aside'        => $aAside,
            'current_page' => $aCurrentPage
        );
    }

    /**
     * @brief setGameRoute 
     * 设置游戏链接
     *
     * @param {array} $navigator
     */
    protected function setGameRoute($navigator)
    {
        foreach ($navigator as $navi) {
            if (isset($this->_aGameTypes[$navi['key']])) {
                $this->_aGameTypes[$navi['key']] = $navi['url'];
            }
        }
    }

    /**
     * Check the route in navigator.
     * @param array $aUrls
     * @param string $sDefault
     */
    protected function checkRoute($aUrls, $sRoute, $sDefault)
    {
        if (!isset($aUrls[$sRoute])) {
            TM::app()->getHttp()->redirect($sDefault);
        }
    }

    /**
     * Get game type from the route given.
     * @param string $sRoute
     * @return string|null
     */
    protected function getGameTypeFromRoute($sRoute)
    {
        $sGameType = null;
        foreach ($this->_aGameTypes as $gameType => $info) {
            if (strpos($sRoute, $gameType) !== false) {
                $sGameType = $gameType;
                break;
            }
        }
        return $sGameType;
    }

    /**
     * Get game navigator if necessary.
     * @return array
     */
    protected function getGameNavigator($sGameFunction)
    {
        $sGameFunction || ($sGameFunction = null);
        $aAuthGame = (new common_Game())->getIdGroupedGameByAuth($sGameFunction);
        $sRoute = TM::app()->getCompleteRoute();
        $sGameType = $this->getGameTypeFromRoute($sRoute);
        list(, $aUrls) = $this->getCompleteNavigatorInfo();
        foreach ($aAuthGame as $gameId => &$gameInfo) {
            $sGameTypeRoute = str_replace($sGameType, $gameInfo['game_type'], $sRoute);
            if (!isset($aUrls[$sGameTypeRoute])) {
                $gameInfo['url'] = $this->_aGameTypes[$gameInfo['game_type']] .
                    '&game_id=' . $gameInfo['game_id'];
            } else {
                $gameInfo['url'] = TM::app()->getUrlManager()->rebuildUrl($sGameTypeRoute,
                    array('game_id' => $gameInfo['game_id']));
            }
        }
        return $aAuthGame;
    }

    /**
     * Get current map
     * @param array $aNavigator
     * @param array $aUrls
     * @param string $sRoute
     * @return array
     */
    protected function getCurrentMap(&$aNavigator, &$aUrls, $sRoute = null)
    {
        $aCurrentMap = $aUrls[$sRoute];
        foreach ($aUrls[$sRoute] as $naviId) {
            $aNavigator[$naviId]['current'] = 1;
            if (!isset($aNavigator[$naviId]['children']) || empty($aNavigator[$naviId]['children'])) {
                break;
            }
            $aNavigator = &$aNavigator[$naviId]['children'];
        }
        return $aCurrentMap;
    }

    /**
     * Get complete navigator info
     * @return array
     */
    protected function getCompleteNavigatorInfo()
    {
        $sessKey = TM::app()->getLocale() . '.navigator_info';
        $aNavigatorInfo = TM::app()->session->get($sessKey);
        if (!isset($aNavigatorInfo)) {
            $aRawNavigator = $this->getRawNavigator();
            $aAuthIds = TM::app()->getUser()->getAuthority();
            $aNavigatorInfo = $this->parseRawNavigator($aRawNavigator, $aAuthIds);
            TM::app()->session->add($sessKey, $aNavigatorInfo);
        }
        return $aNavigatorInfo;
    }

    /**
     * Get navigator rows from database.
     *
     * @return array navigator rows.
     */
    protected function getRawNavigator()
    {
        $debug = defined('IS_DEBUG') && IS_DEBUG;
        $command = TM::app()->db->createCommand()
            ->setFetchMode(PDO::FETCH_GROUP|PDO::FETCH_ASSOC)
            ->select('level,navi_id,navi_name AS name,navi_key AS key,navi_url AS url,navi_url AS route,parent_id,auth_id,' .
                'is_page,is_main,game_related,is_parent,func_slot')
            ->from('t_web_navi')
            ->where('level != 0')
            ->order('display_order ASC');
        if (!$debug) {
            $command->andWhere('status = 1');
        } else {
            $command->andWhere('status <> 0');
        }
        return $command->queryAll();
    }

    /**
     * Parse navigator rows to a tree with authorities allowed.
     *
     * @param  array $aRawNavigator the raw navigator rows.
     * @param  array $aAuthIds      authorities allowed.
     * @return array                navigator tree.
     */
    protected function parseRawNavigator(&$aRawNavigator, &$aAuthIds)
    {
        $aNavigatorInfo = array();
        $aNavigatorUrls = array();
        $iNaviLevel = 1;
        while (isset($aRawNavigator[$iNaviLevel])) {
            // tmp level
            $aLevelNavis = array();
            foreach ($aRawNavigator[$iNaviLevel] as $navi) {
                // authorization
                if (!isset($aAuthIds[$navi['auth_id']])) {
                    continue;
                }
                if ($iNaviLevel == 1) {
                    if (!in_array($navi['name'], array('我的收藏', '游戏分析', '游戏自定义数据'))) {
                        continue;
                    }
                }
                // navigator depends on all games.
                if ($navi['game_related']) {
                    $aAuthGame = (new common_Game())->getTypeGroupedGameByAuth($navi['func_slot']);
                    if (!$aAuthGame) {
                        continue;
                    }
                }
                if ($navi['is_page']) {
                    if ($navi['parent_id']) {
                        // store url with level
                        $aNavigatorUrls[$iNaviLevel-1][$navi['parent_id']][] = array(
                            'ids' => array($navi['navi_id']),
                            'url' => $navi['url']
                        );
                    } else {
                        // first level store with url directly
                        $aNavigatorUrls[$navi['url']] = array($navi['navi_id']);
                    }
                }
                // rebuild url
                $navi['url'] = $navi['url'] ? TM::app()->getUrlManager()->rebuildUrl($navi['url']) : '';
                $navi['name'] = TM::t('tongji', $navi['name']);
                $aLevelNavis[$navi['parent_id']][$navi['navi_id']] = $navi;
            }
            if (empty($aLevelNavis)) break;
            $aNavigatorInfo[$iNaviLevel] = $aLevelNavis;
            $iNaviLevel ++;
        }
        unset($aLevelNavis);
        if (!isset($aNavigatorInfo[1])) {
            return array(null, null);
        }
        $this->stripUrl($aNavigatorInfo, $aNavigatorUrls);
        return array($aNavigatorInfo[1][0], $aNavigatorUrls);
    }

    /**
     * Strip empty navigator parent.
     *
     * @param array $aNavigator
     * @param array $aUrls
     */
    protected function stripUrl(&$aNavigator, &$aUrls)
    {
        end($aNavigator);
        $iNaviLevel = key($aNavigator);
        $iLastLevel = $iNaviLevel;
        while ($iNaviLevel > 0) {
            $iNextLevel = $iNaviLevel - 1;
            if (isset($aNavigator[$iNaviLevel])) {
                foreach ($aNavigator[$iNaviLevel] as $parentId => $navis) {
                    foreach ($navis as $naviId => $navi) {
                        $aChildrenNavi = null;
                        // parent check child
                        if ($navi['is_parent']) {
                            if (isset($aNavigator[$iLastLevel]) && isset($aNavigator[$iLastLevel][$naviId]) && !empty($aNavigator[$iLastLevel][$naviId])) {
                                $aChildrenNavi = $aNavigator[$iLastLevel][$naviId];
                            }
                        }
                        // rewrite parent url
                        if (!$aNavigator[$iNaviLevel][$parentId][$naviId]['url'] && !empty($aChildrenNavi)) {
                            $aNavigator[$iNaviLevel][$parentId][$naviId]['url'] = $aChildrenNavi[key($aChildrenNavi)]['url'];
                        }
                        $aNavigator[$iNaviLevel][$parentId][$naviId]['children'] = $aChildrenNavi;
                        // get url mapping
                        if (isset($aUrls[$iNaviLevel]) && isset($aUrls[$iNaviLevel][$naviId])) {
                            foreach ($aUrls[$iNaviLevel][$naviId] as $value) {
                                array_unshift($value['ids'], $naviId);
                                $aUrls[$iNextLevel][$parentId][] = $value;
                                if (!$iNextLevel) {
                                    $aUrls[$value['url']] = $value['ids'];
                                }
                            }
                        }
                    }
                }
                unset($aUrls[$iNaviLevel]);
            }
            $iLastLevel = $iNaviLevel;
            $iNaviLevel = $iNextLevel;
        }
        unset($aUrls[0]);
    }

    /**
     * Get authoritiy-required navi.
     * @return array
     */
    public function getAuthRequiredNavi()
    {
        $aAuthRequiredNavi = TM::app()->getCache()->get('auth_required_navi');
        if (!isset($aAuthRequiredNavi)) {
            $aAuthRequiredNavi = TM::app()->db->createCommand()
                ->setFetchMode(PDO::FETCH_KEY_PAIR)
                ->select('navi_url as url, auth_id')
                ->from('t_web_navi')
                ->where('status = 1 AND auth_id != "0"')
                ->queryAll();
            TM::app()->getCache()->set('auth_required_navi', $aAuthRequiredNavi, 0);
        }
        return $aAuthRequiredNavi;
    }

    /**
     * Get auth_id by navi_keys
     *
     * @param  array  $aNaviKeys eg. array(gameanalysis, players, new)
     * @param  int    $iGameId
     * @return string
     */
    public function getAuthIdByNaviKey($aNaviKeys, $iGameId = null)
    {
        $sAuthId = '0';
        $aGame = null;
        $iParentId = 0;
        while ($sNaviKey = array_shift($aNaviKeys)) {
            $aNaviInfo = TM::app()->db->createCommand()
                ->select('navi_id,navi_key,auth_id,game_related,func_slot')
                ->from('t_web_navi')
                ->where('parent_id = ? AND navi_key = ?')
                ->queryRow(array($iParentId, $sNaviKey));
            if (!$aNaviInfo) break;
            if ($aNaviInfo['auth_id']) $sAuthId = $aNaviInfo['auth_id'];
            if ($aNaviInfo['game_related']) {
                if (!isset($aGame)) {
                    $aGame = (new common_Game())->findAll(array('condition' => array('game_id' => $iGameId)));
                    if (!$aGame) break;
                    $aGame = $aGame[0];
                }
                array_unshift($aNaviKeys, $aGame['game_type']);
            }
            $iParentId = $aNaviInfo['navi_id'];
        }
        return $sAuthId;
    }

    /**
     * 获取导航用于模板管理
     * TODO url管理
     * @return array
     */
    public function getNavigatorForManage()
    {
        list($aNavigator, $aUrls) = $this->getCompleteNavigatorInfo();
        $aPages = array();
        $aChildrenKey = array();
        foreach ($aUrls as $url => $keys) {
            $aTmpNavigator = $aNavigator;
            $aPages[$url] = array('url' => $url, 'name' => array(), 'key' => array());
            foreach ($keys as $key) {
                if (isset($aTmpNavigator[$key])) {
                    if ($aTmpNavigator[$key]['key']) {
                        if (!isset($this->_aGameTypes[$aTmpNavigator[$key]['key']])) {
                            $aPages[$url]['key'][] = $aTmpNavigator[$key]['key'];
                        }
                        $aPages[$url]['name'][] = $aTmpNavigator[$key]['name'];
                    }
                    if (isset($aTmpNavigator[$key]['children'])) $aTmpNavigator = $aTmpNavigator[$key]['children'];
                } else break;
            }
            if (empty($aPages[$url]['key'])) {
                unset($aPages[$url]);
            } else {
                $key = implode('.', $aPages[$url]['key']);
                if (isset($aChildrenKey[$key])) {
                    unset($aPages[$url]);
                } else {
                    $aPages[$url]['title'] = implode('-', $aPages[$url]['name']);
                    $aPages[$url]['key'] = $key;
                    $aChildrenKey[$key] = 1;
                }
            }
        }
        // remove all item without key
        foreach ($aNavigator as $key => $navigator) {
            if (isset($navigator['children'])) {
                $aChildrenKey = array();
                foreach ($navigator['children'] as $k => $child) {
                    if (isset($this->_aGameTypes[$child['key']])) {
                        foreach ($child['children'] as $j => $c) {
                            // remove the key repeated
                            if (isset($aChildrenKey[$c['key']])) continue;
                            $aNavigator[$key]['children'][$j] = $c;
                            $aChildrenKey[$c['key']] = 1;
                        }
                        unset($aNavigator[$key]['children'][$k]);
                    } elseif (!isset($child['key']) || !$child['key']) {
                        unset($aNavigator[$key]['children'][$k]);
                    }
                }
            }
            if (!isset($navigator['key']) || !$navigator['key'] && empty($aNavigator[$key]['children'])) {
                unset($aNavigator[$key]);
            }
        }
        return array('navigator' => $aNavigator, 'pages' => $aPages);
    }
}
