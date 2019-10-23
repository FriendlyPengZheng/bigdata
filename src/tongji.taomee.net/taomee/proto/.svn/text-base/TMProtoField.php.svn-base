<?php
class TMProtoField
{
    public $number;
    public $name;
    public $type = TMProto::TYPE_STRING;
    public $length;
    public $lengthType;
    public $reference;
    public $rule = TMProto::RULE_REQUIRED;
    public $repeatType;
    public $default;

    public function __construct($opts = array())
    {
        if (!empty($opts)) {
            if (isset($opts['number'])) $this->number = (int)$opts['number'];
            if (isset($opts['name'])) $this->name = $opts['name'];
            if (isset($opts['type'])) $this->type = (int)$opts['type'];
            if (isset($opts['length'])) $this->length = $opts['length'];
            if (isset($opts['lengthType'])) $this->lengthType = $opts['lengthType'];
            if (isset($opts['reference'])) $this->reference = $opts['reference'];
            if (isset($opts['rule'])) $this->rule = (int)$opts['rule'];
            if (isset($opts['repeatType'])) $this->repeatType = $opts['repeatType'];
            if (isset($opts['default'])) $this->default = $opts['default'];
        }
    }

    public function getNumber()
    {
        return $this->number;
    }

    public function getName()
    {
        return $this->name;
    }

    public function getType()
    {
        return $this->type;
    }

    public function getLength()
    {
        return $this->length;
    }

    public function getLengthType()
    {
        return $this->lengthType;
    }

    public function getRepeatType()
    {
        return $this->repeatType;
    }

    public function getReference()
    {
        return $this->reference;
    }

    public function getDefault()
    {
        return $this->default;
    }

    public function isRequired()
    {
        return $this->rule === TMProto::RULE_REQUIRED;
    }

    public function isRepeated()
    {
        return $this->rule === TMProto::RULE_REPEATED;
    }

    public function isMessage()
    {
        return $this->type === TMProto::TYPE_MESSAGE;
    }
}
