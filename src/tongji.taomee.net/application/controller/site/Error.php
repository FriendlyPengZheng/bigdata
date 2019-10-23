<?php
class Error extends TMController
{
    /**
     * @brief showError
     * 错误处理
     */
    public function showError()
    {
        $handler = TM::app()->getErrorHandler();
        if ($error = $handler->getError()) {
            if (TM::app()->getHttp()->isAjaxRequest()) {
                if ($handler->hold()) {
                    $this->ajax(-1, TM::t('tongji', '系统错误，请联系管理员！'));
                } else {
                    $this->ajax(-1, $error['message']);
                }
            } else {
                $user = TM::app()->getUser();
                if ($user->isLogin()) {
                    if ($handler->notFound()) {
                        $this->display('site/notfound.html');
                    } else {
                        $this->display('site/error.html');
                    }
                } else {
                    $user->toLogin();
                }
            }
        }
    }

    public function maintain($aUserParam)
    {
        $this->display('site/maintain.html');
    }
}
