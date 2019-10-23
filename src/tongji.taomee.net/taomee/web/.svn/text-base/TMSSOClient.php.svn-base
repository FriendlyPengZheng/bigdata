<?php
/*
 * Single Sign On
 */
class TMSSOClient extends TMComponent
{
    /**
     * @var integer special system id, required.
     */
    public $systemId = null;

    /**
     * @var string system domain name, required.
     */
    public $systemUrl = null;

    /**
     * @var string url for sso login, required.
     */
    public $loginUrl = null;

    /**
     * @var string url for interacting with sso system, required.
     */
    public $powerUrl = null;

    /**
     * @var integer seconds to be expired.
     */
    public $expire = 86400;

    /**
     * @var string cookie key for sso.
     */
    public $cookieKey = '_TM_SSO';

    /**
     * @var string auth key for sso.
     */
    public $authKey = 'TM_SSO';

    /**
     * @var boolean whether to trace the sso process.
     */
    public $trace = false;

    /**
     * @var array required properties.
     */
    private $_aRequiredProperties = array('systemId', 'systemUrl', 'loginUrl', 'powerUrl');

    /**
     * @var User user.
     */
    private $_user = null;

    /**
     * Check the required properties.
     * @throw TMException if not given.
     */
    private function _check()
    {
        foreach ($this->_aRequiredProperties as $property) {
            if (!isset($this->$property)) {
                throw new TMException(TM::t('taomee', 'TMSSOClient\'s {property} must be setted.',
                            array('{property}' => $property)));
            }
        }
    }

    /**
     * Set user.
     *
     * @param  User        $user
     * @return TMSSOClient
     */
    public function setUser(TMUserInterface $user)
    {
        $this->_user = $user;

        return $this;
    }

    /**
     * Init this component.
     */
    public function init()
    {
        if (!isset($this->systemId)) {
            $this->systemId = TM::app()->getSystemId();
        }
        if (!isset($this->systemUrl)) {
            $this->systemUrl = TM::app()->getSystemUrl();
        }
        $this->_check();
    }

    /**
     * Whether logged in or not.
     * @return boolean
     * @throw TMException
     */
    public function isLogin()
    {
        $aData = $this->_request(1001);
        if (is_array($aData) && 0 == $aData['status_code'] && is_array($aData['user_info'])) {
            if ($this->trace) {
                TM::app()->log->log('Has logged in ' . print_r($aData, true), TMLog::TYPE_INFO);
            }
            if (!isset($this->_user)) {
                throw new TMException(TM::t('taomee', 'TMSSOClient isLogin must be called after setUser.'));
            }
            $this->_user->setUserId($aData['user_info']['user_id']);
            $this->_user->setUserName($aData['user_info']['user_name']);
            return true;
        }
        if ($this->trace) {
            TM::app()->log->log('Not login ' . print_r($aData, true), TMLog::TYPE_INFO);
        }
        return false;
    }

    /**
     * Go to sso login page.
     */
    public function toLogin($sUrl = null)
    {
        $aData = array(
            'app_id'  => $this->systemId,
            'expire'  => $this->expire,
            'app_key' => $this->_getAppKey(),
            'refer'   => isset($sUrl) ? urlencode($sUrl) : urlencode($this->systemUrl)
        );
        $sLoginUrl = $this->loginUrl . '?' . http_build_query($aData);
        if ($this->trace) {
            TM::app()->log->log('To login ' . $sLoginUrl, TMLog::TYPE_INFO);
        }
        TM::app()->getHttp()->redirect($sLoginUrl);
    }

    /**
     * Logout from sso.
     */
    public function toLogout()
    {
        $aData = $this->_request(1003);
        if (is_array($aData) && 0 == $aData['status_code']) {
            TM::app()->session->destroy();
        } else {
            if ($this->trace) {
                TM::app()->log->log('Logout failed ' . print_r($aData, true), TMLog::TYPE_ERROR);
            }
        }
    }

    /**
     * Request sso system for ...
     * @param integer $iCommandId command id.
     * @param array $aExtra extra parameters.
     * @return mixed
     */
    private function _request($iCommandId, $aExtra = array())
    {
        $aData = array_merge(array(
            'cmd'     => $iCommandId,
            'sso_key' => $this->_getSSOKey(),
            'app_id'  => $this->systemId,
            'app_key' => $this->_getAppKey()
        ), $aExtra);
        if ($this->trace) {
            TM::app()->log->log('TMSSOClient requests ' . $this->powerUrl . '?' . http_build_query($aData), TMLog::TYPE_INFO);
        }
        return TMCurlHelper::fetch($this->powerUrl, $aData, 'get', 'json');
    }

    /**
     * Get sso key.
     * @return string
     */
    private function _getSSOKey()
    {
        $sKey = '';
        if (isset($_COOKIE[$this->cookieKey]) && !empty($_COOKIE[$this->cookieKey])) {
            $sKey = $_COOKIE[$this->cookieKey];
        }
        return $sKey;
    }

    /**
     * Get application key.
     * @return string
     */
    private function _getAppKey()
    {
        return md5($this->systemId . $this->authKey);
    }

}
