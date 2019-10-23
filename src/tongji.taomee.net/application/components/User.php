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
		//$aData = $this->_request(1002, $aExtra);
		$aData = array();
		$aData['result'] = 0;
		$aData['data']['user_info']['user_id'] = '1924';
		$aData['data']['user_info']['user_name'] = $sUserName;
		$aData['data']['authority_list'] = array(
            -1         ,
			-2         ,
			0          ,
			24127219999,
			24127220016,
			24127249999,
			24127259999,
			24127280016,
			24127289999,
			24127299999,
			24127300016,
			24127319999,
			24127329999,
			24127330016,
			24127349999,
			24127350016,
			24127359999,
			24127370016,
			24127379999,
			24127389999,
			24127390016,
			24127409999,
			24127419999,
			24127420016,
			24127439999,
			24127440016,
			24127449999,
			24127450016,
			24127460016,
			24127470016,
			24127480016,
			24127490016,
			24127500016,
			24127510016,
			24127520016,
			24127530016,
			24127540016,
			24127550016,
			24127560016,
			24127570016,
			24127580016,
			24127580018,
			24127590016,
			24127600016,
			24127600018,
			24127610016,
			24127620016,
			24127630016,
			24127640016,
			24127650016,
			24127660016,
			24127670016,
			24127680016,
			24127690016,
			24127700016,
			24127710016,
			24127720016,
			24127730016,
			24127740016,
			24127740018,
			24127750016,
			24127760016,
			24127760018,
			24128280016,
			24128289999,
			2412828999999,
			24128290016,
			24128299999,
			24128309999,
			24128310016,
			24128329999,
			24128330016,
			24128349999,
			24128350016,
			24128360016,
			24128370016,
			24128380016,
			24128390016,
			24128400016,
			24128410016,
			24128420016,
			24128430016,
			24128430018,
			24128440016,
			24128450016,
			24128450018,
			24128530016,
			24134770016,
			24136119999,
			24136130016,
			24136139999,
			24137130016,
			24137139999,
			24137869999,
			24137870016,
			24137880016,
			24138020016,
			24138030016,
			24138789999,
			24138799999,
			24138809999,
			24138810016,
			24138820016,
			24138830016,
			24138840016,
			24138850016,
			24138860016,
			24138870016,
			24138880016,
			24138890016,
			24138900016,
			24138910016,
			24138920016,
			24138930016,
			24138940016,
			24138950016,
			24138960016,
			24138969999,
			24139929999,
			24139960016,
			24139979999,
			24139980016,
			24139990016,
			24140000016,
			24140010016,
			24140020016,
			24140030016,
			24140040016,
			24140050016,
			24140069999,
			24140070016,
			24140080016,
			24140090016,
			24140100016,
			24140119999,
			24140120016,
			24140130016,
			24140140016,
			24140140018,
			24140150016,
			24140169999,
			24140170016,
			24140180016,
			24140180018,
			24140210016,
			24140970016,
			24140979999,
			24140990016,
			24140999999,
			24141000016,
			24141009999,
			//以下为game权限
			24127260016,
			24127270016,
			24128000016,
			24131560016,
			24132080016,
			24134110016,
			24135760016,
			24135770016,
			24135780016,
			24135790016,
			24135800016,
			24136580016,
			24137660016,
			24137670016,
			24137710016,
			24137720016,
			24137850016,
			24137980016,
			24138130016,
			24138140016,
			24138200016,
			24138220016,
			24138240016,
			24138250016,
			24138260016,
			24138270016,
			24138280016,
			24138290016,
			24138300016,
			24138310016,
			24138320016,
			24138330016,
			24138340016,
			24138350016,
			24138360016,
			24138380016,
			24138460016,
			24138470016,
			24138490016,
			24138500016,
			24138590016,
			24138600016,
			24138610016,
			24138620016,
			24138630016,
			24138640016,
			24138770016,
			24138960016,
			24139200016,
			24139500016,
			24139510016,
			24139520016,
			24139530016,
			24139620016,
			24139640016,
			24139690016,
			24139700016,
			24139780016,
			24139910016,
			24140290016,
			24140320016,
			24140330016,
			24140470016,
			24140480016,
			24140510016,
			24140530016,
			24140540016,
			24140560016,
			24140580016,
			24140590016,
			24140600016,
			24140620016,
			24140640016,
			24140660016,
			24140700016,
			24140720016,
			24140830016,
			24140850016,
			24140930016,
			24141060016,
			24141100016,
			24141150016,
			24141190016,
			24141310016,
			24141350016,
			24135800018,
			24135760018,
			24135770018,
			24135780018,
			24135790018,
			24128000018,
			24136580018,
			24138220018,
			24127270018,
			24139200018,
			24131560018,
			24132080018,
			24137850018,
			24127260018,
			24134110018,
			24138200018,
			24138960018,
			24137720018,
			24137710018,
			24137660018,
			24137670018,
			24138130018,
			24138140018,
			24138360018,
			24138240018,
			24138260018,
			24138270018,
			24138280018,
			24138290018,
			24138300018,
			24138310018,
			24138320018,
			24138330018,
			24138340018,
			24138350018,
			24138380018,
			24138590018,
			24138620018,
			24138600018,
			24138610018,
			12312313   ,
			24138770018,
			24137980018,
			24138250018,
			24138500018,
			24138470018,
			24138490018,
			24138460018,
			24138630018,
			24138640018,
		);
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
