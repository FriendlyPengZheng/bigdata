<?php
class Error extends TMController
{
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
                if ($handler->notFound()) {
                    $this->display('site/notfound.html');
                } else {
                    $this->display('site/error.html');
                }
            }
        }
    }

    public function maintain($aUserParam)
    {
        $this->display('site/maintain.html');
    }
}
