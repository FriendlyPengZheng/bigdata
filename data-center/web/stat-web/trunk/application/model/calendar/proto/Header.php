<?php
/**
 * DO NOT EDIT! Generated by TMProtoCompiler!
 * Date: 2014-09-02 16:06:11
 */

class calendar_proto_Header extends TMProtoMessage
{
    public $protoId = null;

    public $statusCode = 0;

    public static function descriptor()
    {
        $descriptor = new TMProtoDescriptor(__CLASS__);

        $f = new TMProtoField();
        $f->number     = 1;
        $f->name       = 'protoId';
        $f->type       = TMProto::TYPE_UINT32;
        $f->rule       = TMProto::RULE_REQUIRED;
        $descriptor->addField($f);

        $f = new TMProtoField();
        $f->number     = 2;
        $f->name       = 'statusCode';
        $f->type       = TMProto::TYPE_INT8;
        $f->rule       = TMProto::RULE_REQUIRED;
        $f->default    = 0;
        $descriptor->addField($f);

        return $descriptor;
    }

    public function hasProtoId()
    {
        return $this->has(1);
    }
    
    public function clearProtoId()
    {
        return $this->clear(1);
    }
    
    public function getProtoId()
    {
        return $this->get(1);
    }
    
    public function setProtoId($value)
    {
        return $this->set(1, $value);
    }
    
    public function hasStatusCode()
    {
        return $this->has(2);
    }
    
    public function clearStatusCode()
    {
        return $this->clear(2);
    }
    
    public function getStatusCode()
    {
        return $this->get(2);
    }
    
    public function setStatusCode($value)
    {
        return $this->set(2, $value);
    }
    
}
