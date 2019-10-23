<?php
class tool_TaskCompleteLog extends TMFormModel
{
    /**
     * Task type
     */
    const DATA_PROCESSING = 0;
    const DAILY_REPORT_CHECK = 1;

    /**
     * Task result
     */
    const SUCCESS = 0;
    const FAILURE = 1;

    /**
     * Return the table name.
     *
     * @return string
     */
    public function tableName()
    {
        return 't_task_complete_log';
    }

    /**
     * Whether data processing of the given game is complete
     *
     * @param  array $aUserParam
     * @return bool
     */
    public function isDataProcessingComplete($aUserParam)
    {
        return (bool)$this->findAll(array(
            'condition' => array(
                'game_id' => $aUserParam['game_id'],
                'task_type' => self::DATA_PROCESSING,
                'time' => data_time_Time::amend(strtotime('yesterday'), 'Y-m-d 00:00:00')
            )
        ));
    }
}
