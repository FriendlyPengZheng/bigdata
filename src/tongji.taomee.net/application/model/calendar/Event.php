<?php
class calendar_Event extends TMFormModel
{
    public function tableName()
    {
        return 't_web_calendar_event';
    }

    public function rules()
    {
        return array(
            array('event_name', 'string', 'min' => 1, 'max' => 255, 'message' => TM::t('tongji', '事件名称不合要求！')),
            array('from', 'checkTime', 'message' => TM::t('tongji', '事件时间不合要求！')),
            array('all_day', 'enum', 'range' => array(0, 1)),
            array('event_type', 'enum', 'range' => array(1))
        );
    }

    public function getList($aUserParam)
    {
        $aUserParam['from'] = strtotime($aUserParam['start']);
        if ($aUserParam['from'] === false) return array();

        $aUserParam['to']   = strtotime($aUserParam['end']);
        if ($aUserParam['to'] === false) return array();

        return $this->getDb()->createCommand()
            ->select('event_id AS id,event_name AS title,event_type AS type,all_day,from,to')
            ->from($this->tableName())
            ->where('`from` BETWEEN ? AND ? OR `to` BETWEEN ? AND ?')
            ->queryAll(array($aUserParam['from'], $aUserParam['to'], $aUserParam['from'], $aUserParam['to']));
    }

    public function formatList($list)
    {
        foreach ($list as &$event) {
            $event['allDay'] = (boolean)$event['all_day'];
            $timeFormat = $event['allDay'] ? 'Y-m-d' : 'Y-m-d H:i:s';
            $event['start'] = date($timeFormat, $event['from']);
            $event['end']   = date($timeFormat, $event['to']);
        }

        return $list;
    }

    public function checkTime($value, $attribute, $params)
    {
        $this->from = strtotime($this->from);
        if ($this->from === false) return false;
        $this->to = strtotime($this->to);
        if ($this->to === false) return false;

        if ($this->from === $this->to) $this->to += 86400; // 1st day 00:00 to 2nd day 00:00
        if ($this->from > $this->to) return false;

        return true;
    }

    public function beforeInsert()
    {
        TMProto::register();
        $list = new calendar_proto_EventList();

        $this->addEvents($list, $this->from, $this->to, 1); // 1-节假日
        $this->sendEventList($list);

        return true;
    }

    public function beforeUpdate()
    {
        if (($aInfo = $this->findOne()) === false) return false;

        TMProto::register();
        $list = new calendar_proto_EventList();

        $this->addEvents($list, $aInfo['from'], $aInfo['to'], 2); // 2-工作日
        $this->addEvents($list, $this->from, $this->to, 1);       // 1-节假日
        $this->sendEventList($list);

        return true;
    }

    public function beforeDelete()
    {
        if (($aInfo = $this->findOne()) === false) return false;

        TMProto::register();
        $list = new calendar_proto_EventList();
        $this->addEvents($list, $aInfo['from'], $aInfo['to'], 2); // 2-工作日
        $this->sendEventList($list);

        return true;
    }

    protected function addEvents($list, $from, $to, $type)
    {
        $time = $from;
        while ($time < $to) {
            $alreadyEvents = $this->getDayEvents($time);
            foreach ($alreadyEvents as $idx => $info) {
                if ((int)$info['event_id'] === $this->event_id) {
                    unset($alreadyEvents[$idx]);
                    break;
                }
            }
            if (!$alreadyEvents) {
                $event = new calendar_proto_Event();
                $event->setType($type)->setTime($time);
                $list->addEvent($event);
            }
            $time += 86400;
        }
    }

    protected function findOne()
    {
        $this->event_id = (int)$this->event_id;
        $event = $this->findAll(array('condition' => array('event_id' => $this->event_id)));
        if (!$event) return false;
        return $event[0];
    }

    protected function getDayEvents($time)
    {
        return $this->getDb()->createCommand()
            ->select('event_id')
            ->from($this->tableName())
            ->where('`from` <= ? AND `to` > ?')
            ->queryAll(array($time, $time));
    }

    protected function sendEventList(calendar_proto_EventList $list)
    {
        $socket = TM::app()->backendSocket->connect();

        $header = new calendar_proto_Header();
        $head = $header->setProtoId(0xA012)->serialize();
        $body = $list->serialize();
        $socket->write(pack('L', 4 + strlen($head) + strlen($body)) . $head . $body);

        list(,$len) = unpack('L', $socket->read(4));
        $header->parse($socket->read($len - 4));
        $statusCode = $header->getStatusCode();
        if ($statusCode === -1) {
            TMValidator::ensure(false, TM::t('tongji', '日历事件设置错误！'));
        } elseif ($statusCode !== 0) {
            throw new TMException(TM::t('tongji', '设置日历事件接口错误，错误码{code}！', array('{code}' => $statusCode)));
        }
    }
}
