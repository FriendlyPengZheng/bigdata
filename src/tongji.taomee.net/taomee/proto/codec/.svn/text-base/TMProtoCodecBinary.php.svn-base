<?php
class TMProtoCodecBinary implements TMProtoCodecInterface
{
    /**
     * @param  TMProtoMessage $message
     * @return string
     */
    public function encode(TMProtoMessage $message)
    {
        return $this->encodeMessage($message);
    }

    /**
     * @param  string|TMProtoMessage $message
     * @param  string                $data
     * @return TMProtoMessage
     */
    public function decode(TMProtoMessage $message, $data)
    {
        static $reader;

        // Create a single reader for all messages to be parsed
        if (!$reader) {
            $reader = new TMProtoCodecBinaryReader();
        }

        // Initialize the reader with the current message data
        $reader->init($data);

        return $this->decodeMessage($reader, $message);
    }

    protected function encodeMessage(TMProtoMessage $message)
    {
        $writer = new TMProtoCodecBinaryWriter();

        // Get message descriptor
        $descriptor = TMProto::getRegistry()->getDescriptor($message);

        foreach ($descriptor->getFields() as $tag => $field) {
            if ($field->isRequired() && !$message->has($tag)) {
                throw new TMProtoException(TM::T('taomee', 'Message({msg})的{name}域没有值！',
                    array('{msg}' => get_class($message), '{name}' => $field->getName())));
            }

            $type = $field->getType();
            $value = $message->get($tag);
            if ($field->isRepeated()) {
                // Make sure the value is an array of values
                $value = (array)$value;
                $this->encodeSimpleType($writer, $field->getRepeatType(), count($value));
                foreach($value as $val) {
                    if ($type === TMProto::TYPE_MESSAGE) {
                        $writer->write($this->encodeMessage($val));
                    } elseif ($type === TMProto::TYPE_STRING) {
                        $writer->string($val, $field->getLength());
                    } else {
                        $this->encodeSimpleType($writer, $type, $val);
                    }
                }
            } elseif ($type === TMProto::TYPE_MESSAGE) {
                $writer->write($this->encodeMessage($value));
            } elseif ($type === TMProto::TYPE_STRING) {
                if (($lengthType = $field->getLengthType()) !== null) {
                    $length = strlen($value);
                    $this->encodeSimpleType($writer, $lengthType, $length);
                    $writer->string($value, $length);
                } else {
                    $writer->string($value, $field->getLength());
                }
            } else {
                $this->encodeSimpleType($writer, $type, $value);
            }
        }

        return $writer->getBytes();
    }

    protected function encodeSimpleType($writer, $type, $value)
    {
        switch ($type) {
            case TMProto::TYPE_INT8:
                $writer->int8($value);
                break;

            case TMProto::TYPE_UINT8:
                $writer->uint8($value);
                break;

            case TMProto::TYPE_INT16:
                $writer->int16($value);
                break;

            case TMProto::TYPE_UINT16:
                $writer->uint16($value);
                break;

            case TMProto::TYPE_INT32:
                $writer->int32($value);
                break;

            case TMProto::TYPE_UINT32:
                $writer->uint32($value);
                break;

            case TMProto::TYPE_INT64:
                $writer->int64($value);
                break;

            case TMProto::TYPE_UINT64:
                $writer->uint64($value);
                break;

            case TMProto::TYPE_FLOAT:
                $writer->float($value);
                break;

            case TMProto::TYPE_DOUBLE:
                $writer->double($value);
                break;

            default:
                throw new TMProtoException(TM::t('taomee', '未知域类型{type}！', array('{type}' => $type)));
                break;
        }
    }

    /**
     * @param  TMProtoCodecBinaryReader $reader
     * @param  TMProtoMessage           $message
     * @return TMProtoMessage
     */
    protected function decodeMessage($reader, TMProtoMessage $message)
    {
        // Get message descriptor
        $descriptor = TMProto::getRegistry()->getDescriptor($message);

        foreach ($descriptor->getFields() as $tag => $field) {
            $type = $field->getType();

            if ($field->isRepeated()) {
                $length = $this->decodeSimpleType($reader, $field->getRepeatType());
                while ($length-- > 0) {
                    if ($type === TMProto::TYPE_MESSAGE) {
                        $submessage = $field->getReference();
                        $submessage = new $submessage();
                        $value = $this->decodeMessage($reader, $submessage);
                    } elseif ($type === TMProto::TYPE_STRING) {
                        $value = $reader->string($field->getLength());
                    } else {
                        $value = $this->decodeSimpleType($reader, $type);
                    }
                    $message->add($tag, $value);
                }
            } elseif ($type === TMProto::TYPE_MESSAGE) {
                $submessage = $field->getReference();
                $submessage = new $submessage();
                $message->set($tag, $this->decodeMessage($reader, $submessage));
            } elseif ($type === TMProto::TYPE_STRING) {
                if (($lengthType = $field->getLengthType()) !== null) {
                    $message->set($tag, $reader->string($this->decodeSimpleType($reader, $lengthType)));
                } else {
                    $message->set($tag, $reader->string($field->getLength()));
                }
            } else {
                $message->set($tag, $this->decodeSimpleType($reader, $type));
            }
        }

        return $message;
    }

    protected function decodeSimpleType($reader, $type)
    {
        switch ($type) {
            case TMProto::TYPE_INT8:
                return $reader->int8();
                break;

            case TMProto::TYPE_UINT8:
                return $reader->uint8();
                break;

            case TMProto::TYPE_INT16:
                return $reader->int16();
                break;

            case TMProto::TYPE_UINT16:
                return $reader->uint16();
                break;

            case TMProto::TYPE_INT32:
                return $reader->int32();
                break;

            case TMProto::TYPE_UINT32:
                return $reader->uint32();
                break;

            case TMProto::TYPE_INT64:
                return $reader->int64();
                break;

            case TMProto::TYPE_UINT64:
                return $reader->uint64();
                break;

            case TMProto::TYPE_FLOAT:
                return $reader->float();
                break;

            case TMProto::TYPE_DOUBLE:
                return $reader->double();
                break;

            default:
                throw new TMProtoException(TM::t('taomee', '未知域类型{type}！', array('{type}' => $type)));
                break;
        }
    }
}
