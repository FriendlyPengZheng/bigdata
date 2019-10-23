<?php
class Holiday extends TMController
{
    public function actions()
    {
        return array(
            'index' => array(),
            'getCalendarEvent' => array(
                'start' => null, // from
                'end' => null    // to
            ),
            'saveCalendarEvent' => array(
                'event_id' => null, // empty for new
                'type'     => 1,    // 1: holidays
                'title'    => '',
                'start'    => null,
                'end'      => null,
                'all_day'  => 1
            ),
            'delCalendarEvent' => array(
                'event_id' => null
            ),
            'initHolidays' => array(
                'year' => date('Y')
            )
        );
    }

    public function index($aUserParameters)
    {
        $this->display('conf/holidays.html');
    }

    public function getCalendarEvent($aUserParameters)
    {
        $oEvent = new calendar_Event();
        $this->ajax(0, $oEvent->formatList($oEvent->getList($aUserParameters)));
    }

    public function saveCalendarEvent($aUserParameters)
    {
        $oEvent = new calendar_Event();
        $oEvent->event_type = $aUserParameters['type'];
        $oEvent->event_name = $aUserParameters['title'];
        $oEvent->from = $aUserParameters['start'];
        $oEvent->to   = $aUserParameters['end'];
        $oEvent->all_day = $aUserParameters['all_day'];
        if ($aUserParameters['event_id']) {
            $oEvent->event_id = $aUserParameters['event_id'];
            $this->ajax(0, $oEvent->update());
        }

        $oEvent->insert();
        $this->ajax(0, $oEvent->event_id);
    }

    public function delCalendarEvent($aUserParameters)
    {
        $aUserParameters['event_id'] = (array)$aUserParameters['event_id'];
        if (empty($aUserParameters['event_id'])) $this->ajax(0);

        $oEvent = new calendar_Event();
        foreach ($aUserParameters['event_id'] as $eventId) {
            $oEvent->event_id = $eventId;
            $oEvent->delete();
        }

        $this->ajax(0);
    }

    public function initHolidays($aUserParameters)
    {
        $year = (int)$aUserParameters['year'];
        TMValidator::ensure($year >= 2014 && $year <= 2999, TM::t('tongji', '年份不合要求！'));
        $oEvent = new calendar_Event();
        $oEvent->event_name = TM::t('tongji', '假期');
        $oEvent->event_type = 1; // 节假日事件
        $oEvent->all_day = 1;    // 全天事件

        $from = strtotime($year . '-01-01 00:00:00');
        $to   = strtotime(($year + 1) . '-01-01 00:00:00');
        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            while ($from < $to) {
                $w = (int)date('w', $from);
                if ($w === 0 || $w === 6) {
                    $oEvent->from = date('Y-m-d', $from);
                    $oEvent->to   = date('Y-m-d', $from + 86400);
                    $oEvent->event_id = null;
                    $oEvent->insert();
                    if ($w === 0) {
                        $from += 518400;
                        continue;
                    }
                }
                $from += 86400;
            }
            $transaction->commit();
            $this->ajax(0);
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }
}
