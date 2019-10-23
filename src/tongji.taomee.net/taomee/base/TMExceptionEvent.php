<?php
class TMExceptionEvent extends TMComponent
{
    public $exception;

    public function __construct($exception)
    {
        $this->exception = $exception;
    }

    public function getException()
    {
        return $this->exception;
    }
}
