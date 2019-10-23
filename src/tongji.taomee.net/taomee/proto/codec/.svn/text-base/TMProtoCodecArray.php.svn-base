<?php
/**
 * This codec serializes and unserializes data from/to PHP associative
 * arrays, allowing it to be used as a base for an arbitrary number
 * of different serializations (json, yaml, ini, xml ...).
 */
class TMProtoCodecArray implements TMProtoCodecInterface
{
    /**
     * @var bool
     */
    protected $useTagNumber = false;

    /**
     * Tells the codec to expect the array keys to contain the
     * field's tag number instead of the name.
     *
     * @param bool $useIt
     */
    public function useTagNumberAsKey($useIt = true)
    {
        $this->useTagNumber = $useIt;
    }

    /**
     * @param TMProtoMessage $message
     * @return array
     */
    public function encode(TMProtoMessage $message)
    {
        return $this->encodeMessage($message);
    }

    /**
     * @param  TMProtoMessage    $message
     * @param  array             $data
     * @return TMProtoMessage
     */
    public function decode(TMProtoMessage $message, $data)
    {
        return $this->decodeMessage($message, $data);
    }

    protected function encodeMessage(TMProtoMessage $message)
    {
        $descriptor = TMProto::getRegistry()->getDescriptor($message);

        $data = array();
        foreach ($descriptor->getFields() as $tag => $field) {
            if ($field->isRequired() && !$message->has($tag)) {
                throw new TMProtoException(TM::T('taomee', 'Message({msg})的{name}域没有值！',
                    array('{msg}' => get_class($message), '{name}' => $field->getName())));
            }

            $key = $this->useTagNumber ? $field->getNumber() : $field->getName();
            $value = $message->get($tag);
            if ($field->isRepeated()) {
                // Make sure the value is an array of values
                $value = (array)$value;
                foreach ($value as $k => $v) {
                    $value[$k] = $this->filterValue($v, $field);
                }
            } else {
                $value = $this->filterValue($value, $field);
            }

            $data[$key] = $value;
        }

        return $data;
    }

    protected function decodeMessage(TMProtoMessage $message, $data)
    {
        // Get message descriptor
        $descriptor = TMProto::getRegistry()->getDescriptor($message);

        foreach ($data as $key => $value) {
            // Get the field by tag number or name
            $field = $this->useTagNumber ? $descriptor->getField($key) : $descriptor->getFieldByName($key);
            // Unknown field found
            if (!$field) {
                continue;
            }

            if ($field->isRepeated()) {
                $value = array_values((array)$value);
                foreach ($value as $k => $v) {
                    $value[$k] = $this->filterValue($v, $field);
                }
            } else {
                $value = $this->filterValue($value, $field);
            }

            $message->set($field->getNumber(), $value);
        }

        return $message;
    }

    protected function filterValue($value, TMProtoField $field)
    {
        switch ($field->getType()) {
            case TMProto::TYPE_MESSAGE:
                // Tell apart encoding and decoding
                if ($value instanceof TMProtoMessage) {
                    return $this->encodeMessage($value);
                } else {
                    $nested = $field->getReference();
                    return $this->decodeMessage(new $nested(), $value);
                }
                break;

            case TMProto::TYPE_STRING:
                return (string)$value;
                break;

            case TMProto::TYPE_FLOAT:
            case TMProto::TYPE_DOUBLE:
                return filter_var($value, FILTER_VALIDATE_FLOAT, FILTER_NULL_ON_FAILURE);
                break;

            // Assume the rest are ints
            default:
                return filter_var($value, FILTER_VALIDATE_INT, FILTER_NULL_ON_FAILURE|FILTER_FLAG_ALLOW_HEX);
                break;
        }
    }
}
