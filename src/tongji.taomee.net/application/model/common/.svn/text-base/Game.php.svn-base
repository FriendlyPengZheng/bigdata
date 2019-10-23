<?php
class common_Game extends TMFormModel
{
    const WEB = 'webgame';       // 页游
    const CLIENT = 'clientgame'; // 端游
    const MOBILE = 'mobilegame'; // 手游
    const TEST = 'test';         // 测试
    const SITE = 'site';         // 网站

    const UNUSED = 0;     // 未使用
    const USING = 1;      // 使用中
    const DEPRECATE = 2;  // 已废弃

    const ONLINE_MASK = 0;    // 在线统计
    const FOREIGN_MASK = 1;   // 对外
    const SIGNTRANS_MASK = 2; // 新用户注册转化
    const ACCOUNT_MASK = 3;   // 帐号系统
    const CHARGE_MASK = 4;    // 充值系统
    const GAMEANALYSIS_MASK = 5;    // 游戏分析
    const MUYOU_ONLY_MASK = 6;    // 合作公司-木游only
    const SELF_SEARCH_MASK = 7; // 自定义查询
    const STEP_MASK = 8; // 步骤转化
    const MOBILE_MASK = 9; // 手机端

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
            array('status', 'enum', 'range' => array_keys($this->getStatus()), 'defaultValue' => self::USING),
            array('func_plot', 'number', 'integerOnly' => true),
            array('game_email', 'checkEmails')
        );
    }

    /**
     * 获取类型
     *
     * @return array
     */
    public function getGameType()
    {
        return array(self::WEB, self::CLIENT, self::MOBILE, self::TEST, self::SITE);
    }

    /**
     * 获取类型
     *
     * @return array
     */
    public function getGameTypeName()
    {
        return array(
            self::WEB => '页游', 
            self::CLIENT => '端游', 
            self::MOBILE => '手游', 
            self::TEST => '测试', 
            self::SITE => '网站'
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
            self::UNUSED => TM::t('tongji', '未使用'),
            self::USING => TM::t('tongji', '使用中'),
            self::DEPRECATE => TM::t('tongji', '已废弃')
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
            self::FOREIGN_MASK => TM::t('tongji', '对外'),
            self::SIGNTRANS_MASK => TM::t('tongji', '新用户注册转化'),
            self::ACCOUNT_MASK => TM::t('tongji', '帐号系统'),
            self::CHARGE_MASK => TM::t('tongji', '充值系统'),
            self::GAMEANALYSIS_MASK => TM::t('tongji', '游戏分析'),
            self::MUYOU_ONLY_MASK => TM::t('tongji', '合作公司-木游Only'),
            self::SELF_SEARCH_MASK => TM::t('tongji', '自定义查询'),
            self::STEP_MASK => TM::t('tongji', '步骤转化'),
            self::MOBILE_MASK => TM::t('tongji', '手机')
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
                if ($game['func_slot'] & (1 << (int)$mask)) {
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
     * @param  int[] $funcMask
     * @return array
     */
    public function findByFuncMask($funcMask)
    {
        $funcMask = (array)$funcMask;

        $condition = $param = array();
        foreach ($funcMask as $mask) {
            $condition[] = 'func_slot & ? > 0';
            $param[] = 1 << (int)$mask;
        }
        $condition[] = 'status = ?';
        $param[] = self::USING;

        return $this->getDb()->createCommand()
            ->select($this->attributes())
            ->from($this->tableName())
            ->where(implode(' AND ', $condition))
            ->queryAll($param);
    }

    /**
     * 通过功能掩码获取游戏信息，包括全平台全区全服gpzs_id
     *
     * @param  int[] $funcMask
     * @return array
     */
    public function findByFuncMaskWithGpzs($funcMask)
    {
        $game = $this->findByFuncMask($funcMask);
        $gameIds = array();
        foreach ($game as $gameInfo) {
            $gameIds[] = $gameInfo['game_id'];
        }
        $gpzsInfo = TMArrayHelper::column((new common_GpzsInfo())->getWholeGpzsInfo($gameIds), null, 'game_id');
        foreach ($game as $idx => &$gameInfo) {
            if (!isset($gpzsInfo[$gameInfo['game_id']])) {
                unset($game[$idx]);
                continue;
            }
            $gameInfo = array_merge($gameInfo, $gpzsInfo[$gameInfo['game_id']]);
        }

        return $game;
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
     * @var {string} 检测时使用的临时邮件
     */
    public $email;

    /**
     * @brief checkEmails
     * 检测game_email字段
     *
     * @param {string} $value
     *
     * @return {boolean}
     */
    public function checkEmails($value)
    {
        $validator = TMValidator::createValidator('email', $this, 'email', array());

        foreach (explode(';', $value) as $email) {
            $this->email = $email;
            $validator->validateValue($this, 'email', $email);

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
     * @param  int[] $funcMask
     * @return array
     */
    public function getTypeGroupedGameByAuth($funcMask = null)
    {
        $aAuth = TM::app()->getUser()->getAuthority();
        if ($funcMask) $funcMask = (array)$funcMask;
        else $funcMask = array();
        if (!isset($aAuth['-3'])) { // does not have auth for internal games
            $funcMask[] = self::FOREIGN_MASK;
        }
        $funcMask = array_merge($funcMask, TM::app()->getUser()->getSpecialMask());

        $aTypeGrouped = TM::app()->session->get('type_grouped_authorized_games');
        if (!isset($aTypeGrouped)) {
            list($aTypeGrouped,) = $this->getGameByAuth();
        }

        if (empty($funcMask)) return $aTypeGrouped;
        $funcMask = (array)$funcMask;
        foreach ($aTypeGrouped as $type => $group) {
            foreach ($group as $gameId => $gameInfo) {
                foreach ($funcMask as $mask) {
                    if (!($gameInfo['func_slot'] & (1 << (int)$mask))) {
                        unset($aTypeGrouped[$type][$gameId]);
                        if (empty($aTypeGrouped[$type])) {
                            unset($aTypeGrouped[$type]);
                        }
                        break;
                    }
                }
            }
        }
        return $aTypeGrouped;
    }

    /**
     * Get authorized games' key-value list as game_id->game_info
     *
     * @param  int[] $funcMask
     * @return array
     */
    public function getIdGroupedGameByAuth($funcMask = null)
    {
		$aAuth = TM::app()->getUser()->getAuthority();
        if ($funcMask) $funcMask = (array)$funcMask;
        else $funcMask = array();
        if (!isset($aAuth['-3'])) { // does not have auth for internal games
            $funcMask[] = self::FOREIGN_MASK;
        }
        $funcMask = array_merge($funcMask, TM::app()->getUser()->getSpecialMask());

        $aIdGrouped = TM::app()->session->get('id_grouped_authorized_games');
        if (!isset($aIdGrouped)) {
            list(,$aIdGrouped) = $this->getGameByAuth();
        }

        if (empty($funcMask)) return $aIdGrouped;
        $funcMask = (array)$funcMask;
        foreach ($aIdGrouped as $gameId => $gameInfo) {
            foreach ($funcMask as $mask) {
                if (!($gameInfo['func_slot'] & (1 << (int)$mask))) {
                    unset($aIdGrouped[$gameId]);
                    break;
                }
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
        $aGameList = $this->getDb()->createCommand()
            ->select('game.game_id,game_name,game_type,auth_id,manage_auth_id,ignore,func_slot,gpzs_id,game.status')
            ->from($this->tableName() . ' game')
            ->join((new common_GpzsInfo())->tableName() . ' gpzs', 'game.game_id=gpzs.game_id')
            ->where('game.status = ? AND platform_id = -1 AND zone_id = -1 AND server_id = -1')
            ->order('game_name')
            ->queryAll(array(self::USING));
        foreach ($aGameList as $game) {
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
        if (!isset($aAuthGame[$iGameId])) return false;

        $iManageAuthId = $aAuthGame[$iGameId]['manage_auth_id'];
        $aAuth = TM::app()->getUser()->getAuthority();
        if ($iManageAuthId && isset($aAuth[$iManageAuthId])) return true;

        return false;
    }

    /**
     * @brief getInfoByGameId 
     * 根据游戏ID获取游戏信息
     *
     * @param {integer} $gameId
     *
     * @return {array}
     */
    public function getInfoByGameId($gameId)
    {
        return $this->getDb()->createCommand()
            ->select('game_id,game_name,game_type,auth_id')
            ->from($this->tableName())
            ->where('game_id=?')
            ->queryRow([$gameId]);
    }

    /**
     * 获取白名单内的权限列表
     * @param array $permitList
     * @return array
     */
    public function getGamePermitList($permitList)
    {
        $resGame = array();
        $gameUserAuthList = $this->getIdGroupedGameByAuth();
        foreach($gameUserAuthList as $key => $agame){
            if(in_array($agame["game_id"],$permitList)){
                $resGame[] = $agame;
            }
        }
        return $resGame;
    }

    /**
     * 添加隐藏的模块
     *
     * @param integer $ignoreId
     * @param integer $gameId
     *
     * @return $this
     */
    public function addIgnore($ignoreId, $gameId)
    {
        $game = $this->find()
            ->where('game_id = ?')
            ->queryRow(array($gameId));
        if (!$game) return;
        $ignores = explode('_', $game['ignore']);
        if (in_array($ignoreId, $ignores)) return;

        $ignores[] = $ignoreId;
        $this->game_id = $gameId;
        $this->ignore = implode('_', $ignores);
        $this->update(array('ignore'));
        return $this;
    }
}
