<?php
class TMException extends \Exception
{
    public function __construct($message = '', $code = 500, $previous = NULL) 
    {
        parent::__construct($message, $code);
    }
}

class TMFatalException extends \TMException
{
}

class TMWebException extends \TMException
{
}

class TMWebNotFoundException extends \TMWebException
{
}

class TMBadMethodCallException extends \TMException
{
}
