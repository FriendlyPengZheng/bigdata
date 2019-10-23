<?php
class TMHttpException extends TMException
{
    /**
     * @var {integer} http status code, such as 403, 500, etc..
     */
    public $statusCode;

    public function __construct($status, $message = null, $code = 0)
    {
        $this->statusCode = $status;
        parent::__construct($message, $code);
    }
}
