<?php
class AutoDisplay implements TMDisplayInterface
{
    public function assignListener(TMController $controller)
    {
        $controller->assign('current_skin', TM::app()->defaultSkin);
        $controller->assign('skins', ['navy_blue' => '1']);

        $oUser = TM::app()->getUser();
        if ($oUser->isLogin()) {
            $controller->assign('user', TM::app()->getUser()->getUserInfo());
        }
    }
}
