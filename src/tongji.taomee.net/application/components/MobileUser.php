<?php
class MobileUser extends User
{
    /**
     * @see parent
     */
    public function isForeigner()
    {
        return false;
    }

    /**
     * Has no authority.
     */
    public function forbidden()
    {
        if (TM::app()->getHttp()->isAjaxRequest()) {
            $this->ajax(1, TM::t('tongji', '亲，你还没有权限哦！'));
        } else {
            $this->display('mobile/forbidden.html');
        }
    }

    /**
     * Local login page.
     */
    public function index($aParams)
    {
        $this->assign('need_vericode', $this->isForeigner());
        $this->display('mobile/login.html');
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
    }
}
