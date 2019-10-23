<?php
class common_OnlineConfig extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_online_config';
    }

    public function getGameIdGroupedList()
    {
        return $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('game.game_id,game.game_name,game.online_auth_id,online.data_id,online.gpzs_id,' .
                'online.sthash,online.show_name,online.auth_id,online.in_summary,online.is_all, online.order')
            ->from($this->tableName() . ' online')
            ->join((new common_Game())->tableName() . ' game', 'online.game_id=game.game_id')
            ->where('game.status = ' . common_Game::USING)
            ->andWhere('game.func_slot & (1 << ' . common_Game::ONLINE_MASK . ') <> 0')
            ->order('online.position, online.order')
            ->queryAll();
    }
}
