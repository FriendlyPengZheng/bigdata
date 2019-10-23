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
     * @var array super admin name list
     */
    public $admin = array();

    /**
     * @var boolean whether to show the site maintain page
     */
    public $maintain = false;

    /**
     * @var array additional authority id list
     */
    public $additionalAuthority = array();

    /**
     * @var array additional mask id list
     */
    public $mask = array();

    /**
     * @var string The local ip address.
     */
    public $localIp = null;

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

        if (is_array($this->admin) && $this->admin) {
            $this->admin = array_fill_keys($this->admin, true);
        } else {
            $this->admin = array();
        }
        if (isset($this->localIp)) {
            $this->localIp = array_fill_keys((array)$this->localIp, true);
        }
    }

    /**
     * Page or ajax request actions.
     */
    public function actions()
    {
        return array(
            'index' => array(),
            'login' => array('user_name' => null, 'user_pwd' => null, 'vericode' => null),
            'getVericode' => array(),
            'logout' => array(),
            'changePassword' => array('old_pwd' => null, 'new_pwd' => null)
        );
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
        return strpos($sRoute, 'user/') !== 0 && strpos($sRoute, 'site/') !== 0;
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
     *
     * @param  integer $userId
     * @return User
     */
    public function setUserId($userId)
    {
        TM::app()->session->add('user_id', (int)$userId);
        TM::app()->session->add('is_admin', isset($this->admin[$userId]));

        return $this;
    }

    /**
     * Get is admin
     * @return boolean
     */
    public function isAdmin()
    {
        return (boolean)TM::app()->session->get('is_admin');
    }

    /**
     * Whether the current user is a guest.
     * return boolean
     */
    public function isGuest()
    {
        $sUserName = $this->getUserName();
        return isset($sUserName) && strpos($sUserName, 'guest_') === 0;
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
     *
     * @param  string $userName
     * @return User
     */
    public function setUserName($userName)
    {
        TM::app()->session->add('user_name', $userName);

        return $this;
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
            'is_super_admin' => $this->isAdmin(),
            'is_guest' => $this->isGuest()
        );
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
     *
     * @param  array $authority
     * @return User
     */
    public function setAuthority($authority = null)
    {
        if (!isset($authority)) {
            if (false === ($authority = $this->fetchAuthority())) {
                $this->toLogin();
            }
        }
        // Everyone has -1 and the admins have -2
        $authority['-1'] = true;
        if ($this->isAdmin()) {
            $authority['-2'] = true;
        }
        $authority = $authority + $this->fetchAdditionalAuthority();
        TM::app()->session->add('user_auth_list', $authority);

        return $this;
    }

    /**
     * 获取额外设置的权限值
     *
     * @return array
     */
    public function fetchAdditionalAuthority()
    {
        if (is_array($this->additionalAuthority) && !empty($this->additionalAuthority)) {
            return array_fill_keys($this->additionalAuthority, true);
        } else {
            return array();
        }
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
     * Check authority.
     */
    public function checkAuthority()
    {
        $sRoute = TM::app()->getCompleteRoute();
        if (!$this->_loginRequired($sRoute)) return;

        TM::app()->getHttp()->recordRequest();
        TM::app()->cookie->set('lastUrl', TM::app()->getHttp()->getRecordCurrentUrl(), '+30days');
        if (!$this->isLogin()) {
            $this->toLogin();
        }

        // redirect to maintain page.
        if ($this->maintain && !$this->isAdmin()) {
            TM::app()->getHttp()->redirect(TM::app()->getUrlManager()->rebuildUrl('site/error/maintain'));
        }

        // check ajax request authority.
        if (TM::app()->getHttp()->isAjaxRequest()) {
            $aNavi = TM::app()->navigator->getAuthRequiredNavi();
            $aAuth = $this->getAuthority();
            if (isset($aNavi[$sRoute]) && !isset($aAuth[$aNavi[$sRoute]])) {
                $this->setAuthorized(false)->forbidden();
            }
        // check for navigator authority
        } else {
            TM::app()->navigator->getNavigator();
        }
    }

    /**
     * Whether logged in, just according to session.
     *
     * @return boolean
     */
    public function isLogin()
    {
        return (boolean)$this->getUserId();
    }

    /**
     * Whether logged in, just according to session.
     *
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
            $sCurrentUrl = TM::app()->cookie->get('lastUrl');
            if (!$sCurrentUrl) {
                $sCurrentUrl = TM::app()->getHttp()->getBaseUrl();
            }
        }
        $this->getSSOClient()->toLogin($sCurrentUrl);
    }

    /**
     * Request power system for ...
     *
     * @param integer $iCommandId command id.
     * @param array $aExtra extra parameters.
     * @return mixed
     */
    private function _request($iCommandId, $aExtra = array())
    {
        $aData = array_merge(array('cmd' => $iCommandId, 'client_ip' => TM::app()->getHttp()->getIp()), $aExtra);
        if ($this->trace) {
            TM::app()->log->log('User requests ' . $this->powerSystem . '?' . http_build_query($aData), TMLog::TYPE_INFO);
        }
        return TMCurlHelper::fetch($this->powerSystem, $aData, 'post', 'json');
    }

    /**
     * Set authorized flag.
     *
     * @param  boolean $authorized
     * @return User    for chaining
     */
    public function setAuthorized($authorized)
    {
        $this->_authorized = $authorized;

        return $this;
    }

    /**
     * Whether the user is authorized.
     *
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
     * Has no authority.
     */
    public function forbidden()
    {
        if (TM::app()->getHttp()->isAjaxRequest()) {
            $this->ajax(1, TM::t('tongji', '亲，你还没有权限哦！'));
        } else {
            $this->display('user/forbidden.html');
        }
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

    // ------ The following methods are for user page ------

    /**
     * Local login page.
     */
    public function index($aParams)
    {
        $this->assign('need_vericode', $this->isForeigner());
        $this->display('user/login.html');
    }

    /**
     * Login action.
     */
    public function login($aParams)
    {
        if ($this->isForeigner()) {
            $this->_checkVericode($aParams['vericode']);
        }
        $this->_doLogin($aParams['user_name'], $aParams['user_pwd']);
        $sCurrentUrl = TM::app()->getHttp()->getRecordCurrentUrl();
        if (empty($sCurrentUrl)) {
            $sCurrentUrl = TM::app()->getHttp()->getBaseUrl();
        }
        $this->ajax(0, $sCurrentUrl);
    }

    /**
     * Log in to power system.
     *
     * @param  string  $sUserName
     * @param  string  $sPassword
     * @return boolean
     * @throw TMException
     */
    private function _doLogin($sUserName, $sPassword)
    {
        $sUserName = trim($sUserName);
        TMValidator::ensure($sUserName !== '', TM::t('tongji', '请输入用户名！'));
        $sPassword = trim($sPassword);
        TMValidator::ensure($sPassword !== '', TM::t('tongji', '请输入密码！'));

        $aExtra = array('user_name' => $sUserName, 'passwd' => $sPassword, 'system_id' => TM::app()->getSystemId());
        $aData = $this->_request(1002, $aExtra);
        if (isset($aData['result']) && 0 == $aData['result']) {
            $this->setUserId($aData['data']['user_info']['user_id']);
            $this->setUserName($aData['data']['user_info']['user_name']);
            $this->setAuthority(array_fill_keys($aData['data']['authority_list'], true));
            if ($this->trace) {
                TM::app()->log->log('User login success ' . print_r($aData, true), TMLog::TYPE_INFO);
            }
            return;
        }
        if ($this->trace) {
            TM::app()->log->log('User login failed ' . print_r($aData, true), TMLog::TYPE_ERROR);
        }

        TMValidator::ensure(false, TM::t('tongji', isset($aData['err_desc']) ? $aData['err_desc'] : '登录错误！'));
    }

    /**
     * Get vericode.
     */
    public function getVericode()
    {
        $vericode = $this->_generateVericode();
        TM::app()->session->add('user_vericode', md5($vericode));

        $width = 100;
        $height = 37;
        $type = 'png';

        // 生成图形资源
        if(function_exists('imagecreatetruecolor')){
            $im = imagecreatetruecolor($width, $height);
        }else{
            $im = imagecreate($width, $height);
        }

        $r = array(225, 255, 255, 223);
        $g = array(225, 236, 237, 255);
        $b = array(225, 236, 166, 125);
        $key = mt_rand(0, 3);

        $backColor = imagecolorallocate($im, $r[$key], $g[$key], $b[$key]); //背景色（随机）
        $borderColor = imagecolorallocate($im, 100, 100, 100); //边框色
        $pointColor = imagecolorallocate($im, mt_rand(0, 255), mt_rand(0, 255), mt_rand(0, 255)); //点颜色

        // 边框上色
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

        // 写入字符串
        $stringColor = imagecolorallocate($im, mt_rand(0, 200), mt_rand(0, 120), mt_rand(0, 120));
        @imagestring($im, 5, 30, 10, $vericode, $stringColor);

        // 输出至浏览器
        $image_function = 'image' . $type;
        header('Content-Type: image/' . $type);
        $image_function($im);
        imagedestroy($im);
    }

    /**
     * Generate vericode.
     */
    private function _generateVericode()
    {
        $num = '';
        $n = 4;
        for ($i = 0; $i < $n; $i++) {
            $num .= (string)dechex(mt_rand(0, 15));
        }
        return $num;
    }

    /**
     * Check vericode.
     */
    private function _checkVericode($sVericode)
    {
        $sVericode = trim($sVericode);
        TMValidator::ensure($sVericode !== '', TM::t('tongji', '请输入验证码！'));
        TMValidator::ensure(TM::app()->session->remove('user_vericode') === md5($sVericode),
            TM::t('tongji', '验证码错误！'));
    }

    /**
     * Logout action.
     */
    public function logout($aParams)
    {
        $this->toLogout()->toLogin();
    }

    /**
     * Logout, destroy session.
     *
     * @return User
     */
    public function toLogout()
    {
        if ($this->useSSO) {
            $this->getSSOClient()->toLogout();
        } else {
            TM::app()->session->destroy();
        }

        return $this;
    }

    /**
     * Change password by the current password, only for guest_* users.
     */
    public function changePassword($aParams)
    {
        if (!$this->isLogin()) $this->toLogin();

        TMValidator::ensure($this->isGuest(), TM::t('tongji', '您不是Guest用户，暂时不能通过此方式修改密码！'));
        $this->_doChangePassword($aParams['old_pwd'], $aParams['new_pwd']);

        $this->toLogout();
        $this->ajax(0);
    }

    /**
     * Change password by password.
     * @param string $sOldPwd
     * @param string $sNewPwd
     */
    private function _doChangePassword($sOldPwd, $sNewPwd)
    {
        $sOldPwd = trim($sOldPwd);
        TMValidator::ensure($sOldPwd !== '', TM::t('tongji', '请输入原密码！'));
        $sNewPwd = trim($sNewPwd);
        TMValidator::ensure($sNewPwd !== '', TM::t('tongji', '请输入新密码！'));

        $aExtra = array('user_id' => $this->getUserId(), 'old_passwd' => md5($sOldPwd), 'passwd' => md5($sNewPwd), 'passwd_sec' => md5($sNewPwd));
        $aData = $this->_request(1101, $aExtra);
        if (isset($aData['result']) && 0 == $aData['result']) {
            if ($this->trace) {
                TM::app()->log->log('User change password success ' . print_r($aData, true), TMLog::TYPE_INFO);
            }
            return;
        }
        if ($this->trace) {
            TM::app()->log->log('User change password failed ' . print_r($aData, true), TMLog::TYPE_ERROR);
        }
        TMValidator::ensure(false, TM::t('tongji', isset($aData['err_desc']) ? $aData['err_desc'] : '修改密码错误！'));
    }

    /**
     * @brief fetchAllUsers 
     * 获取系统有权限的用户列表
     *
     * @return {array}
     */
    public function fetchAllUsers()
    {
        $aExtra = array('system_id' => TM::app()->getSystemId());
        return $this->_request(1006, $aExtra);
    }

    /**
     * @brief getSpecialMask 
     * 获取特殊的功能标志位
     *
     * @return {array}
     */
    public function getSpecialMask()
    {
        return $this->mask;
    }

    /**
     * 返回用户是否是登录状态
     */
    public function logined()
    {
        if ($this->isLogin()) {
            $this->ajax(0, $this->getUserName());
        } else {
            $this->ajax(-1);
        }
    }
}
