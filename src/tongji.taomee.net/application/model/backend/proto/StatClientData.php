<?php
/**
 * DO NOT EDIT! Generated by TMProtoCompiler!
 * Date: 2014-09-03 11:36:34
 */

class backend_proto_StatClientData extends TMProtoMessage
{
    public $registerTotal = null;

    public $alarmTotal = null;

    public $info = array();

    public static function descriptor()
    {
        $descriptor = new TMProtoDescriptor(__CLASS__);

        $f = new TMProtoField();
        $f->number     = 1;
        $f->name       = 'registerTotal';
        $f->type       = TMProto::TYPE_UINT16;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 2;
        $f->name       = 'alarmTotal';
        $f->type       = TMProto::TYPE_UINT16;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 3;
        $f->name       = 'info';
        $f->type       = TMProto::TYPE_MESSAGE;
        $f->reference  = 'backend_proto_StatClientInfo';
        $f->rule       = TMProto::RULE_REPEATED;
        $f->repeatType = TMProto::TYPE_UINT16;
        $descriptor->addField($f);

        return $descriptor;
    }

    public function hasRegisterTotal()
    {
        return $this->has(1);
    }
    
    public function clearRegisterTotal()
    {
        return $this->clear(1);
    }
    
    public function getRegisterTotal()
    {
        return $this->get(1);
    }
    
    public function setRegisterTotal($value)
    {
        return $this->set(1, $value);
    }
    
    public function hasAlarmTotal()
    {
        return $this->has(2);
    }
    
    public function clearAlarmTotal()
    {
        return $this->clear(2);
    }
    
    public function getAlarmTotal()
    {
        return $this->get(2);
    }
    
    public function setAlarmTotal($value)
    {
        return $this->set(2, $value);
    }
    
    public function hasInfo()
    {
        return $this->has(3);
    }
    
    public function clearInfo()
    {
        return $this->clear(3);
    }
    
    public function getInfo($idx = null)
    {
        return $this->get(3, $idx);
    }
    
    public function setInfo($value, $idx = null)
    {
        return $this->set(3, $value, $idx);
    }
    
    public function getInfoList()
    {
        return $this->get(3);
    }
    
    public function addInfo($value)
    {
        return $this->add(3, $value);
    }
    
}


class backend_proto_StatClientInfo extends TMProtoMessage
{
    public $ip = null;

    public $port = null;

    public $redFlag = null;

    public $forbiddenFlag = null;

    public $lastHeartBeatTime = null;

    public $workplaceSize = null;

    public $inboxFileCount = null;

    public $inboxFileSize = null;

    public $outboxFileCount = null;

    public $outboxFileSize = null;

    public $sentFileCount = null;

    public $sentFileSize = null;

    public static function descriptor()
    {
        $descriptor = new TMProtoDescriptor(__CLASS__);

        $f = new TMProtoField();
        $f->number     = 1;
        $f->name       = 'ip';
        $f->type       = TMProto::TYPE_UINT32;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 2;
        $f->name       = 'port';
        $f->type       = TMProto::TYPE_UINT16;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 3;
        $f->name       = 'redFlag';
        $f->type       = TMProto::TYPE_UINT8;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 4;
        $f->name       = 'forbiddenFlag';
        $f->type       = TMProto::TYPE_UINT8;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 5;
        $f->name       = 'lastHeartBeatTime';
        $f->type       = TMProto::TYPE_UINT64;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 6;
        $f->name       = 'workplaceSize';
        $f->type       = TMProto::TYPE_UINT64;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 7;
        $f->name       = 'inboxFileCount';
        $f->type       = TMProto::TYPE_UINT32;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 8;
        $f->name       = 'inboxFileSize';
        $f->type       = TMProto::TYPE_UINT64;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 9;
        $f->name       = 'outboxFileCount';
        $f->type       = TMProto::TYPE_UINT32;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 10;
        $f->name       = 'outboxFileSize';
        $f->type       = TMProto::TYPE_UINT64;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 11;
        $f->name       = 'sentFileCount';
        $f->type       = TMProto::TYPE_UINT32;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 12;
        $f->name       = 'sentFileSize';
        $f->type       = TMProto::TYPE_UINT64;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        return $descriptor;
    }

    public function hasIp()
    {
        return $this->has(1);
    }
    
    public function clearIp()
    {
        return $this->clear(1);
    }
    
    public function getIp()
    {
        return $this->get(1);
    }
    
    public function setIp($value)
    {
        return $this->set(1, $value);
    }
    
    public function hasPort()
    {
        return $this->has(2);
    }
    
    public function clearPort()
    {
        return $this->clear(2);
    }
    
    public function getPort()
    {
        return $this->get(2);
    }
    
    public function setPort($value)
    {
        return $this->set(2, $value);
    }
    
    public function hasRedFlag()
    {
        return $this->has(3);
    }
    
    public function clearRedFlag()
    {
        return $this->clear(3);
    }
    
    public function getRedFlag()
    {
        return $this->get(3);
    }
    
    public function setRedFlag($value)
    {
        return $this->set(3, $value);
    }
    
    public function hasForbiddenFlag()
    {
        return $this->has(4);
    }
    
    public function clearForbiddenFlag()
    {
        return $this->clear(4);
    }
    
    public function getForbiddenFlag()
    {
        return $this->get(4);
    }
    
    public function setForbiddenFlag($value)
    {
        return $this->set(4, $value);
    }
    
    public function hasLastHeartBeatTime()
    {
        return $this->has(5);
    }
    
    public function clearLastHeartBeatTime()
    {
        return $this->clear(5);
    }
    
    public function getLastHeartBeatTime()
    {
        return $this->get(5);
    }
    
    public function setLastHeartBeatTime($value)
    {
        return $this->set(5, $value);
    }
    
    public function hasWorkplaceSize()
    {
        return $this->has(6);
    }
    
    public function clearWorkplaceSize()
    {
        return $this->clear(6);
    }
    
    public function getWorkplaceSize()
    {
        return $this->get(6);
    }
    
    public function setWorkplaceSize($value)
    {
        return $this->set(6, $value);
    }
    
    public function hasInboxFileCount()
    {
        return $this->has(7);
    }
    
    public function clearInboxFileCount()
    {
        return $this->clear(7);
    }
    
    public function getInboxFileCount()
    {
        return $this->get(7);
    }
    
    public function setInboxFileCount($value)
    {
        return $this->set(7, $value);
    }
    
    public function hasInboxFileSize()
    {
        return $this->has(8);
    }
    
    public function clearInboxFileSize()
    {
        return $this->clear(8);
    }
    
    public function getInboxFileSize()
    {
        return $this->get(8);
    }
    
    public function setInboxFileSize($value)
    {
        return $this->set(8, $value);
    }
    
    public function hasOutboxFileCount()
    {
        return $this->has(9);
    }
    
    public function clearOutboxFileCount()
    {
        return $this->clear(9);
    }
    
    public function getOutboxFileCount()
    {
        return $this->get(9);
    }
    
    public function setOutboxFileCount($value)
    {
        return $this->set(9, $value);
    }
    
    public function hasOutboxFileSize()
    {
        return $this->has(10);
    }
    
    public function clearOutboxFileSize()
    {
        return $this->clear(10);
    }
    
    public function getOutboxFileSize()
    {
        return $this->get(10);
    }
    
    public function setOutboxFileSize($value)
    {
        return $this->set(10, $value);
    }
    
    public function hasSentFileCount()
    {
        return $this->has(11);
    }
    
    public function clearSentFileCount()
    {
        return $this->clear(11);
    }
    
    public function getSentFileCount()
    {
        return $this->get(11);
    }
    
    public function setSentFileCount($value)
    {
        return $this->set(11, $value);
    }
    
    public function hasSentFileSize()
    {
        return $this->has(12);
    }
    
    public function clearSentFileSize()
    {
        return $this->clear(12);
    }
    
    public function getSentFileSize()
    {
        return $this->get(12);
    }
    
    public function setSentFileSize($value)
    {
        return $this->set(12, $value);
    }
    
}
