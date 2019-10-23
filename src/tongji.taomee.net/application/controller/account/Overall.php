<?php
class Overall extends common_Overall
{
    protected function getPageTitle()
    {
        return TM::t('tongji', '帐号系统');
    }

    protected function getGameId()
    {
        return 169; // 帐号系统
    }
}
