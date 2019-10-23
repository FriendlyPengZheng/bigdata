<?php
class AutoDisplay implements TMDisplayInterface
{
    public function assignListener(TMController $controller)
    {
        $controller->assign('current_skin', TM::app()->defaultSkin);
        $controller->assign('skins', ['navy_blue' => '1']);
        $controller->assign('application_info',
            ['localeSwitchable' => isset(TM::app()->localeSwitchable) ? TM::app()->localeSwitchable : false],
            true);

        $oUser = TM::app()->getUser();
        if ($oUser->isLogin()) {
            $controller->assign('user', TM::app()->getUser()->getUserInfo());
            if ($oUser->isAuthorized()) {
				$navigator = TM::app()->navigator->getNavigator();
				if(isset($navigator['second_bar']['171'])){
					$gamelist = (new common_Game())->getGamePermitList(array(2,5,6,10,16));
					if(empty($gamelist)){
					   unset($navigator['second_bar']['171']);
					}
				}
                $controller->assign($navigator, null, true);
            }
        }

        // 检查是否有超级管理员权限        
        if ($oUser->isAdmin()) {
            $controller->assign('admin_auth', true);
        }
    }
}
