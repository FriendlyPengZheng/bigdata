<?php
abstract class TMProtoMessage
{
    /**
     * @var TMProtoDescriptor
     */
    protected $descriptor;

    /**
     * @return TMProtoDescriptor
     */
    public static function descriptor()
    {
        throw new TMProtoException(TM::t('taomee', '子类必须实现此方法！'));
    }

    /**
     * @param string $data
     */
    public function __construct($data = null)
    {
        // Cache the descriptor instance
        $this->descriptor = TMProto::getRegistry()->getDescriptor($this);

        if (null !== $data) {
            $this->parse($data);
        }
    }

    /**
     * Parse the given data to hydrate the object
     *
     * @param string                     $data
     * @param TMProtoCodecInterface|null $codec
     */
    public function parse($data, TMProtoCodecInterface $codec = null)
    {
        if (is_array($data)) $codec = 'array';
        $codec = TMProto::getCodec($codec);
        $codec->decode($this, $data);
    }

    /**
     * Parse the given data to hydrate the object
     */
    public static function parseFromString($data)
    {
        return new static($data);
    }

    /**
     * Serialize the current object data
     *
     * @param  TMProtoCodecInterface|null $codec
     * @return string
     */
    public function serialize(TMProtoCodecInterface $codec = null)
    {
        $codec = TMProto::getCodec($codec);
        return $codec->encode($this);
    }

    /**
     * Serialize the current object data
     *
     * @param  TMProtoCodecInterface|null $codec
     * @return string
     */
    public function serializeToString(TMProtoCodecInterface $codec = null)
    {
        return $this->serialize($codec);
    }

    /**
     * Checks if the given tag number is set
     *
     * @param  int  $tag
     * @return bool
     */
    public function has($tag)
    {
        if ($this->descriptor->hasField($tag)) {
            $f = $this->descriptor->getField($tag);
            $name = $f->getName();

            return $f->isRepeated() ? count($this->$name) > 0 : $this->$name !== null;
        }

        return false;
    }

    /**
     * Get the value by tag number
     *
     * @param  int      $tag
     * @param  int|null $idx
     * @return mixed
     */
    public function get($tag, $idx = null)
    {
        $f = $this->descriptor->getField($tag);

        if (!$f) {
            return null;
        }

        $name = $f->getName();

        return $idx !== null ? $this->{$name}[$idx] : $this->$name;
    }

    /**
     * Sets the value by tag number
     *
     * @param  int              $tag
     * @param  mixed            $value
     * @param  int|null         $idx
     * @return TMProtoMessage          Fluent interface
     * @throws TMProtoException        If trying to set an unknown field
     */
    public function set($tag, $value, $idx = null)
    {
        $f = $this->descriptor->getField($tag);

        if (!$f) {
            throw new TMProtoException(TM::t('taomee', '域{tag}不存在！', array('{tag}' => $tag)));
        }

        $name = $f->getName();

        if ($idx === null) {
            $this->$name = $value;
        } else {
            $this->{$name}[$idx] = $value;
        }

        return $this;
    }

    /**
     * Adds a new value to a repeated field by tag number
     *
     * @param  int              $tag
     * @param  mixed            $value
     * @return TMProtoMessage
     * @throws TMProtoException        If trying to modify an unknown field
     */
    public function add($tag, $value)
    {
        $f = $this->descriptor->getField($tag);

        if (!$f) {
            throw new TMProtoException(TM::t('taomee', '域{tag}不存在！', array('{tag}' => $tag)));
        }

        $name = $f->getName();

        $this->{$name}[] = $value;

        return $this;
    }

    /**
     * Clears/Resets a field by tag number
     *
     * @param  int             $tag
     * @return TMProtoMessage       Fluent interface
     * @throws TMProtoException     If trying to modify an unknown field
     */
    public function clear($tag)
    {
        $f = $this->descriptor->getField($tag);

        if (!$f) {
            throw new TMProtoException(TM::t('taomee', '域{tag}不存在！', array('{tag}' => $tag)));
        }

        $name = $f->getName();

        $this->$name = $f->isRepeated() ? array() : null;

        return $this;
    }

    /**
     * @brief toArray 
     * Convert object to array
     *
     * @return {array}
     */
    public function toArray()
    {
        $fields = $this->descriptor->getFields();
        $values = array();

        foreach ($fields as $field) {
            $name = $field->getName();
            $values[$name] = $this->{$name};
        }

        return $values;
    }
}
