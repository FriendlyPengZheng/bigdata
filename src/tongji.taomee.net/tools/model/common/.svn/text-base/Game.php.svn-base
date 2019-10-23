<?php
class common_Game extends TMFormModel
{
    const WEB = 'webgame';
    const CLIENT = 'clientgame';
    const MOBILE = 'mobilegame';
    const TEST = 'test';
    const SITE = 'site';

    const NOT_USED = 0;
    const BE_USED = 1;
    const DELETED = 2;
    const BE_EXTERNAL = 3;

    const ONLINE_MASK = 0;
    const RECHARGE_MASK = 1;

    /**
     * Table name for this model.
     *
     * @return string
     */
    public function tableName()
    {
        return 't_game_info';
    }

    /**
     * 获取参数检测的配置
     *
     * @return array
     */
    public function rules()
    {
        return array(
            array('game_id', 'number', 'integerOnly' => true, 'min' => 1, 'max' => 2147483647),
            array('game_name', 'string', 'min' => 1, 'max' => 255),
            array('game_type', 'enum', 'range' => $this->getGameType()),
            array('auth_id,manage_auth_id', 'string', 'min' => 1, 'max' => 32),
            array('online_auth_id', 'string', 'min' => 1, 'max' => 32, 'allowEmpty' => true),
            array('ignore', 'checkIgnore'),
            array('status', 'enum', 'range' => array_keys($this->getStatus()), 'defaultValue' => self::BE_USED),
            array('func_plot', 'number', 'integerOnly' => true),
        );
    }

    /**
     * 获取类型
     *
     * @return array
     */
    public function getGameType()
    {
        return array(
            self::WEB,
            self::CLIENT,
            self::MOBILE,
            self::TEST,
            self::SITE
        );
    }

    /**
     * 获取状态
     *
     * @return array
     */
    public function getStatus()
    {
        return array(
            self::NOT_USED => TM::t('tongji', '未使用'),
            self::BE_USED => TM::t('tongji', '使用中'),
            self::DELETED => TM::t('tongji', '已废弃'),
            self::BE_EXTERNAL => TM::t('tongji', '对外')
        );
    }

    /**
     * 功能掩码列表
     *
     * @return array
     */
    public function getFuncMask()
    {
        return array(
            self::ONLINE_MASK => TM::t('tongji', '在线统计'),
            self::RECHARGE_MASK => TM::t('tongji', '充值系统')
        );
    }

    /**
     * 获取列表
     *
     * @param  array $params
     * @return array
     */
    public function getFormattedList($params = null)
    {
        return $this->formatList($this->findAll($params));
    }

    /**
     * 处理游戏列表
     *
     * @param  array $gameList
     * @return array
     */
    protected function formatList($gameList)
    {
        $funcMask = $this->getFuncMask();
        $status = $this->getStatus();
        foreach ($gameList as &$game) {
            $game['func_title'] = '';
            foreach ($funcMask as $mask => $title) {
                if ($mask & $game['func_slot'] > 0) {
                    $game['func_title'] .= $title . ',';
                }
            }
            $game['func_title'] = rtrim($game['func_title'], ',');
            $game['status_name'] = TMArrayHelper::assoc($game['status'], $status, $game['status']);
        }
        return $gameList;
    }

    /**
     * 通过功能掩码获取游戏
     *
     * @param  integer $iFuncMask
     * @return array
     */
    public function findByFuncMask($iFuncMask)
    {
        return TM::app()->getDb()->createCommand()
            ->select($this->attributes())
            ->from($this->tableName())
            ->where('func_slot & ? > 0 AND status IN (?, ?)')
            ->queryAll(array(1 << (int)$iFuncMask, self::BE_USED, self::BE_EXTERNAL));
    }

    /**
     * 检测ignore字段
     *
     * @param  string $value
     * @return boolean
     */
    public function checkIgnore($value)
    {
        $this->ignore = '';

        if ($value) {
            $this->ignore = implode('_', array_unique(array_filter(explode('_', $value))));
        }

        return true;
    }

    /**
     * 插入前检测game_id的唯一性
     *
     * @return boolean
     */
    public function beforeInsert()
    {
        $validator = TMValidator::createValidator('unique', $this, 'game_id');
        $validator->validate($this, 'game_id');
        return true;
    }

    /**
     * Get game list against authority.
     *
     * @return array
     */
    public function getTypeGroupedGameByAuth()
    {
        $aTypeGrouped = TM::app()->session->get('type_grouped_authorized_games');
        if (!isset($aTypeGrouped)) {
            list($aTypeGrouped,) = $this->getGameByAuth();
        }
        return $aTypeGrouped;
    }

    /**
     * Get authorized games' key-value list as game_id->game_info
     *
     * @return array
     */
    public function getIdGroupedGameByAuth($iFuncMask=0)
    {
        $aIdGrouped = TM::app()->session->get('id_grouped_authorized_games');
        if (!isset($aIdGrouped)) {
            list(,$aIdGrouped) = $this->getGameByAuth();
        }
        if ($iFuncMask) {
            foreach ($aIdGrouped as $key => $game) {
                if (!$game['func_slot'] & (1 << (int)$iFuncMask)) unset($aIdGrouped[$key]);
            }
        }
        return $aIdGrouped;
    }

    /**
     * Get games from database against authority.
     *
     * @return array(game_type->game_id->game_info, game_id->game_info)
     */
    protected function getGameByAuth()
    {
        $aAuth = TM::app()->getUser()->getAuthority();
        $aTypeGrouped = $aIdGrouped = array();
        $aGameList = TM::app()->getDb()->createCommand()
            ->select('game.game_id, game_name, game_type, auth_id, manage_auth_id, ignore, gpzs_id, game.status')
            ->from($this->tableName() . ' game')
            ->join((new common_GpzsInfo())->tableName() . ' gpzs', 'game.game_id = gpzs.game_id')
            ->where('game.status <> 2 AND platform_id = -1 AND zone_id = -1 AND server_id = -1')
            ->queryAll();
        foreach ($aGameList as $game) {
            if ($game['status'] == self::BE_USED && !isset($aAuth['-4'])) continue;
            if ($game['status'] == self::NOT_USED && !isset($aAuth['-3'])) continue;
            if (isset($aAuth[$game['auth_id']])) {
                $aTypeGrouped[$game['game_type']][$game['game_id']] = $game;
                $aIdGrouped[$game['game_id']] = $game;
            }
        }
        TM::app()->session->add('type_grouped_authorized_games', $aTypeGrouped);
        TM::app()->session->add('id_grouped_authorized_games', $aIdGrouped);
        return array($aTypeGrouped, $aIdGrouped);
    }

    /**
     * Check if it has the game authority.
     *
     * @param integer $iGameId
     */
    public function checkGameAuth($iGameId)
    {
        $iGameId = (int)$iGameId;
        $aAuthGame = $this->getIdGroupedGameByAuth();
        if (!isset($aAuthGame[$iGameId])) {
            TM::app()->getUser()->setAuthorized(false)->forbidden();
        }
    }

    /**
     * Check if it has the game management authority.
     *
     * @param integer $iGameId
     */
    public function checkGameManageAuth($iGameId)
    {
        if (!$this->hasGameManageAuth($iGameId)) {
            TM::app()->getUser()->setAuthorized(false)->forbidden();
        }
    }

    /**
     * Whether it has the game's management authority.
     *
     * @param  integer $iGameId
     * @return boolean
     */
    public function hasGameManageAuth($iGameId)
    {
        $iGameId = (int)$iGameId;
        $aAuthGame = $this->getIdGroupedGameByAuth();
        $iManageAuthId = $aAuthGame[$iGameId]['manage_auth_id'];
        $aAuth = TM::app()->getUser()->getAuthority();
        if ($iManageAuthId && isset($aAuth[$iManageAuthId])) {
            return true;
        }
        return false;
    }
}
