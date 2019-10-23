<?php
/**
 * Keeps instances of the different message descriptors used.
 *
 */
class TMProtoRegistry
{
    /**
     * @var array
     */
    protected $descriptors = array();

    /**
     * @param string|TMProtoMessage $message
     * @param TMProtoDescriptor     $descriptor
     */
    public function setDescriptor($message, TMProtoDescriptor $descriptor)
    {
        $message = is_object($message) ? get_class($message) : $message;
        $this->descriptors[$message] = $descriptor;
    }

    /**
     * Obtains the descriptor for the given message class, obtaining
     * it if not yet loaded.
     *
     * @param  string|TMProtoMessage $message
     * @return TMProtoDescriptor
     */
    public function getDescriptor($message)
    {
        $message = is_object($message) ? get_class($message) : $message;

        // Build a descriptor for the message
        if (!isset($this->descriptors[$message])) {
            if (!class_exists($message)) {
                throw TMProtoException(TM::t('taomee', 'Message类{class}不存在！', array('{class}' => $message)));
            }

            $this->descriptors[$message] = call_user_func(array($message, 'descriptor'));
        }

        return $this->descriptors[$message];
    }

    /**
     * @param  string|TMProtoMessage $message
     * @return bool
     */
    public function hasDescriptor($message)
    {
        $message = is_object($message) ? get_class($message) : $message;

        return isset($this->descriptors[$message]);
    }

    /**
     * @param  string|TMProtoMessage $message
     */
    public function unsetDescriptor($message)
    {
        $message = is_object($message) ? get_class($message) : $message;

        unset($this->descriptors[$message]);
    }
}
