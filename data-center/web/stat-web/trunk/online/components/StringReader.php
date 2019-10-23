<?php
class StringReader
{
    private $_buffer;

    private $_length;

    private $_pos;

    public function __construct($string)
    {
        $this->_buffer = $string;
        $this->_length = strlen($string);
        $this->_pos = 0;
    }

    public function read($length)
    {
        if ($this->_pos + $length > $this->_length) {
            throw new StringReaderException(TM::t('tongji', '缓冲区内容长度不足，需要{need}，只有{rest}！', array(
                '{need}' => $length,
                '{rest}' => $this->_length - $this->_pos
            )));
        }

        $read = substr($this->_buffer, $this->_pos, $length);
        $this->_pos += $length;
        return $read;
    }
}

class StringReaderException extends TMException
{
}
