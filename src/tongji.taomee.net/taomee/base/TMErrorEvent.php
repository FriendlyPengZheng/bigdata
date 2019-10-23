<?php
class TMErrorEvent extends TMComponent
{
    public $handled = false;

    public $code;

    public $message;

    public $file;

    public $line;

    public function __construct($code, $message, $file, $line)
    {
        $this->code = $code;
        $this->message = $message;
        $this->file = $file;
        $this->line = $line;
    }
}
