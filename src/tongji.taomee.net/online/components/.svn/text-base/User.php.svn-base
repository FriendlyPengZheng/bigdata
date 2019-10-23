<?php
class User extends TMController implements TMUserInterface
{
    /**
     * @var string system id.
     */
    public $systemId = null;

    /**
     * @var string power system domain name.
     */
    public $powerSystem = null;

    /**
     * @var boolean whether use sso for logging in and out.
     */
    public $useSSO = true;

    /**
     * @var boolean whether to trace the process.
     */
    public $trace = false;

    /**
     * @var array Administrators' user_ids.
     */
    public $admin = array();

    /**
     * @var boolean whether to show the site maintain page
     */
    public $maintain = false;

    /**
     * @var string The local ip address.
     */
    public $localIp = null;

    /**
     * @var string Foreigner must have this authority for login.
     */
    public $foreignerAuthority = null;

    /**
     * @var array The games' configuration.
     */
    public $game = null;

    /**
     * @var TMSSOClient sso client.
     */
    private $_ssoClient = null;

    /**
     * @var boolean whether the user is authorized.
     */
    private $_authorized = true;

    /**
     * Init this component.
     */
    public function init()
    {
        $this->_check();
        $this->admin = array_fill_keys($this->admin, true);

        if (isset($this->localIp)) {
            $this->localIp = array_fill_keys((array)$this->localIp, true);
        }
    }

    /**
     * Initialize the games' configuration.
     */
    public function initGame()
    {
        if (isset($this->game) && is_string($this->game) && is_readable($this->game)) {
            $this->game = include($this->game);
        }
        if (!is_array($this->game)) {
            $this->game = array();
        }
    }

    /**
     * Check required property.
     * @throw TMException
     */
    private function _check()
    {
        if (!isset($this->systemId)) {
            $this->systemId = TM::app()->getSystemId();
        }
        if (!isset($this->powerSystem)) {
            throw new TMException('User must be initialized with power system url.');
        }
    }

    /**
     * Whether login is required.
     * @param string $sRoute
     * @return boolean
     */
    private function _loginRequired($sRoute)
    {
        return strpos($sRoute, 'user/') !== 0 && strpos($sRoute, 'error/') !== 0;
    }

    /**
     * Get SSO client.
     * @return TMSSOClient
     */
    public function getSSOClient()
    {
        if (!isset($this->_ssoClient)) {
            $this->_ssoClient = TM::app()->sso->setUser($this);
        }
        return $this->_ssoClient;
    }

    /**
     * Get user id.
     * @return integer
     */
    public function getUserId()
    {
        return (int)TM::app()->session->get('user_id');
    }

    /**
     * Set user id.
     * @param integer $userId
     */
    public function setUserId($userId)
    {
        TM::app()->session->add('user_id', (int)$userId);
        TM::app()->session->add('is_admin', isset($this->admin[$userId]));
    }

    /**
     * Whether the current user is an administrator.
     * @return boolean
     */
    public function isAdmin()
    {
        return (boolean)TM::app()->session->get('is_admin');
    }

    /**
     * Get user name.
     * @return string
     */
    public function getUserName()
    {
        return TM::app()->session->get('user_name');
    }

    /**
     * Set user name.
     * @param string $userName
     */
    public function setUserName($userName)
    {
        TM::app()->session->add('user_name', $userName);
    }

    /**
     * Get authority array.
     * @return array
     */
    public function getAuthority()
    {
        return TM::app()->session->get('user_auth_list');
    }

    /**
     * Set authority.
     * @param array $authority
     */
    public function setAuthority($authority = null)
    {
        if (!isset($authority)) {
            if (false === ($authority = $this->fetchAuthority())) {
                $this->toLogin();
            }
        }
        TM::app()->session->add('user_auth_list', $authority);
    }

    /**
     * Fetch authority from power system.
     * @return mixed
     */
    public function fetchAuthority()
    {
        $aExtra = array('user_id' => $this->getUserId(), 'system_id' => TM::app()->getSystemId());
        $aData = $this->_request(1003, $aExtra);
        if (isset($aData['result']) && 0 == $aData['result']) {
            if ($this->trace) {
                TM::app()->log->log('User fetch authority success ' . print_r($aData, true), TMLog::TYPE_INFO);
            }
            $aAuthIds = array();
            foreach ($aData['data'] as $value) {
                $aAuthIds[$value['id']] = true;
            }
            return $aAuthIds;
        }
        if ($this->trace) {
            TM::app()->log->log('User fetch authority failed ' . print_r($aData, true), TMLog::TYPE_ERROR);
        }
        return false;
    }

    /**
     * Get authority-filtered games.
     */
    public function getGameByAuth()
    {
        $aGame = TM::app()->session->get('authorized_games');
        if (isset($aGame)) return $aGame;

        $this->initGame();
        $authority = $this->getAuthority();
        $aGame = array();
        foreach ($this->game as $gameInfo) {
            $gameId = $gameInfo['gameId'];
            if (isset($gameInfo['auth_id']) && !isset($authority[$gameInfo['auth_id']])) continue;
            $rmKey = $keyMap = array();
            foreach ($gameInfo['data_list'] as $dataKey => $dataInfo) {
                if (is_array($dataInfo['data_id'])) {
                    $keyMap = array_merge($keyMap, array_fill_keys($dataInfo['data_id'], $dataKey));
                }
                if (!isset($dataInfo['auth_id']) || isset($authority[$dataInfo['auth_id']])) continue;
                unset($gameInfo['data_list'][$dataKey]);
                $rmKey[] = $dataKey;
            }
            foreach ($rmKey as $key) {
                if (!isset($keyMap[$key])) continue;
                unset($gameInfo['data_list'][$keyMap[$key]]);
            }
            if (!$gameInfo['data_list']) continue;
            $aGame[$gameId] = $gameInfo;
        }
        TM::app()->session->add('authorized_games', $aGame);
        return $aGame;
    }

    /**
     * Check authority.
     */
    public function checkAuthority()
    {
        $sRoute = TM::app()->getCompleteRoute();
        if (!$this->_loginRequired($sRoute)) {
            return;
        }
        if (!$this->isLogin()) {
            TM::app()->getHttp()->recordRequest();
            $this->toLogin();
        }
        // redirect to maintain page
        if ($this->maintain && !$this->isAdmin()) {
            TM::app()->getHttp()->redirect(TM::app()->getUrlManager()->rebuildUrl('error/maintain'));
        }
        if(!$this->getGameByAuth()) {
            $this->setAuthorized(false)->forbidden();
        }
    }

    /**
     * Whether logged in, just according to session.
     * @return boolean
     */
    public function isLogin()
    {
        return (boolean)$this->getUserId();
    }

    /**
     * Whether logged in, just according to session.
     * @return boolean true if logged in sso
     */
    public function toLogin()
    {
        if (!$this->useSSO) {
            TM::app()->getHttp()->redirect(TM::app()->getUrlManager()->rebuildUrl('user/index'));
            // exit when redirect
        }

        if ($this->getSSOClient()->isLogin()) {
            $this->setAuthority();
            return true;
        }
        $sCurrentUrl = TM::app()->getHttp()->getRecordCurrentUrl();
        if (!$sCurrentUrl) {
            $sCurrentUrl = TM::app()->getHttp()->getBaseUrl();
        }
        $this->getSSOClient()->toLogin($sCurrentUrl);
    }

    /**
     * Log in to power system.
     * @param string $sUserName
     * @param string $sPassword
     * @return boolean
     * @throw TMException
     */
    public function doLogin($sUserName, $sPassword)
    {
        if (empty($sUserName)) {
            throw new TMException(TM::t('taomee', '请输入用户名！'));
        }
        if (empty($sPassword)) {
            throw new TMException(TM::t('taomee', '请输入密码！'));
        }
        $aExtra = array('user_name' => $sUserName, 'passwd' => $sPassword, 'system_id' => TM::app()->getSystemId());
        $aData = $this->_request(1002, $aExtra);
        if (isset($aData['result']) && 0 == $aData['result']) {
            $authority = array_fill_keys($aData['data']['authority_list'], true);
            if ($this->isForeigner() &&
                    isset($this->foreignerAuthority) &&
                    !isset($authority[$this->foreignerAuthority])) {
                throw new TMException(TM::t('taomee', '您没有权限登录本系统！'));
            }
            $this->setAuthority($authority);
            $this->setUserId($aData['data']['user_info']['user_id']);
            $this->setUserName($aData['data']['user_info']['user_name']);
            if ($this->trace) {
                TM::app()->log->log('User login success ' . print_r($aData, true), TMLog::TYPE_INFO);
            }
        } else {
            if ($this->trace) {
                TM::app()->log->log('User login failed ' . print_r($aData, true), TMLog::TYPE_ERROR);
            }
            throw new TMException(TM::t('taomee', $aData['err_desc']));
        }
    }

    /**
     * Request power system for ...
     * @param integer $iCommandId command id.
     * @param array $aExtra extra parameters.
     * @return mixed
     */
    private function _request($iCommandId, $aExtra = array())
    {
        $aData = array_merge(array(
            'cmd'       => $iCommandId,
            'client_ip' => TM::app()->getHttp()->getIp(),
        ), $aExtra);
        if ($this->trace) {
            TM::app()->log->log('User requests ' . $this->powerSystem . '?' . http_build_query($aData), TMLog::TYPE_INFO);
        }
        return TMCurlHelper::fetch($this->powerSystem, $aData, 'post', 'json');
    }

    /**
     * Set authorized flag.
     * @param boolean $authorized
     * @return this for chaining
     */
    public function setAuthorized($authorized)
    {
        $this->_authorized = $authorized;
        return $this;
    }

    /**
     * Whether the user is authorized.
     * @return boolean
     */
    public function isAuthorized()
    {
        return $this->_authorized;
    }

    /**
     * Write a message with user_name and user_id.
     * @param string $sMsg
     * @param array $aExtra
     * @return boolean
     */
    public function info($sMsg, $aExtra)
    {
        return TM::app()->getLog()->log(
            sprintf('%s[%s]%s, %s', $this->getUserName(), $this->getUserId(), $sMsg, print_r($aExtra, true)),
            TMLog::TYPE_INFO
        );
    }

    /**
     * Whether the current user is a foreigner.
     * @return boolean
     */
    public function isForeigner()
    {
        if (!isset($this->localIp) ||
                !isset($this->localIp[TM::app()->getHttp()->getIp()])) {
            return true;
        }

        return false;
    }

    /**
     * Get user information.
     *
     * @return array
     */
    public function getUserInfo()
    {
        return array(
            'current_admin_name' => $this->getUserName(),
            'current_admin_id' => $this->getUserId(),
            'is_super_admin' => $this->isAdmin()
        );
    }

    // ------ The following methods are for user page ------

    /**
     * Local login page.
     */
    public function index()
    {
        $this->assign('need_vericode', $this->isForeigner());
        $this->display('user/login.html');
    }

    /**
     * Login action.
     */
    public function login()
    {
        $aParams = TM::app()->getHttp()->getParameters('user_name', 'user_pwd');
        try {
            if ($this->isForeigner()) {
                $this->checkVericode();
            }
            $this->doLogin($aParams['user_name'], $aParams['user_pwd']);
            $sCurrentUrl = TM::app()->getHttp()->getRecordCurrentUrl();
            if (empty($sCurrentUrl)) {
                $sCurrentUrl = TM::app()->getHttp()->getBaseUrl();
            }
            $this->ajax(0, $sCurrentUrl);
        } catch (Exception $e) {
            $this->ajax(1, $e->getMessage());
        }
    }

    /**
     * Get vericode.
     */
    public function getVericode()
    {
        $vericode = $this->generateVericode();
        TM::app()->session->add('user_vericode', md5($vericode));

        $width = 100;
        $height = 37;
        $type = 'png';

        //生成图形资源
        if(function_exists('imagecreatetruecolor')){
            $im = imagecreatetruecolor($width, $height);
        }else{
            $im = imagecreate($width, $height);
        }

        $r = Array(
            225, 255, 255, 223
        );
        $g = Array(
            225, 236, 237, 255
        );
        $b = Array(
            225, 236, 166, 125
        );
        $key = mt_rand(0, 3);

        $backColor = imagecolorallocate($im, $r[$key], $g[$key], $b[$key]); //背景色（随机）
        $borderColor = imagecolorallocate($im, 100, 100, 100); //边框色
        $pointColor = imagecolorallocate($im, mt_rand(0, 255), mt_rand(0, 255), mt_rand(0, 255)); //点颜色

        //边框上色
        @imagefilledrectangle($im, 0, 0, $width - 1, $height - 1, $backColor);
        @imagerectangle($im, 0, 0, $width - 1, $height - 1, $borderColor);

        // 干扰色
        for($i = 0; $i < 10; $i ++) {
            $fontcolor = imagecolorallocate($im, mt_rand(0, 255), mt_rand(0, 255), mt_rand(0, 255));
            imagearc($im, mt_rand(- 10, $width), mt_rand(- 10, $height), mt_rand(30, 300), mt_rand(20, 200), 55, 44, $fontcolor);
        }
        for($i = 0; $i < 25; $i ++) {
            $fontcolor = imagecolorallocate($im, mt_rand(0, 255), mt_rand(0, 255), mt_rand(0, 255));
            imagesetpixel($im, mt_rand(0, $width), mt_rand(0, $height), $pointColor);
        }

        //写入字符串
        $stringColor = imagecolorallocate($im, mt_rand(0, 200), mt_rand(0, 120), mt_rand(0, 120));
        @imagestring($im, 5, 30, 10, $vericode, $stringColor);

        //输出至浏览器
        $image_function = 'image'.$type;
        header('Content-Type: image/' . $type);
        $image_function($im);
        imagedestroy($im);
    }

    /**
     * Generate vericode.
     */
    protected function generateVericode()
    {
        $num = '';
        $n = 4;
        for ($i = 0; $i < $n; $i++) {
            $num .= (string)dechex(mt_rand(0, 15));
        }
        return $num;
    }

    /**
     * check vericode
     */
    public function checkVericode()
    {
        $aParams = TM::app()->getHttp()->getParameters('vericode');
        if (empty($aParams['vericode'])) {
            throw new TMException(TM::t('taomee', '请输入验证码！'));
        }
        if (TM::app()->session->remove('user_vericode') != md5($aParams['vericode'])) {
            throw new TMException(TM::t('taomee', '验证码错误！'));
        }
    }

    /**
     * Logout action.
     */
    public function logout()
    {
        if ($this->useSSO) {
            $this->getSSOClient()->toLogout();
        } else {
            TM::app()->session->destroy();
        }
        $this->toLogin();
    }

    /**
     * Has no authority.
     */
    public function forbidden()
    {
        if (TM::app()->getHttp()->isAjaxRequest()) {
            $this->ajax(1, TM::t('taomee', '亲，你还没有权限哦！'));
        } else {
            $this->display('user/forbidden.html');
        }
    }
}
