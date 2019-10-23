<?php
class gameanalysis_WhaleUserData extends TMFormModel
{
    public function tableName()
    {
        return 'v_whale_user_month';
    }

    public function getList($aUserParam)
    {
        $select = 'game_id,platform_id,zone_id,server_id,account_id,platform_name,ctime,current_level,time,total_payments,total_count';
        $command = $this->getDb()->createCommand()
            ->from($this->tableName())
            ->where('game_id = ? AND time = ?')
            ->order('total_payments DESC')
            ->limit((int)$aUserParam['top']);
        $params = array($aUserParam['game_id'], $aUserParam['time']);

        if ($aUserParam['platform_id'] === '-1') {
            $gpzs = new common_GpzsInfo();
            $gpzs->game_id = $aUserParam['game_id'];
            if (!$gpzs->hasSinglePlatform()) {
                return $command->select($select . ',total_ratio_by_platform AS total_ratio')->andWhere('platform_id != -1')->queryAll($params);
            }
        }
        $params[] = $aUserParam['platform_id'];
        return $command->select($select . ',total_ratio')->andWhere('platform_id = ?')->queryAll($params);
    }
}
