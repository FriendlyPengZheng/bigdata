<?php
class TMErrorHandler extends TMComponent
{
    private $_error;

    private $_hold = true;

    private $_notFound = false;

    private $_exception;

    public $errorAction;

    public $outputClean = true;

    /**
     * @brief handle
     * 错误处理
     *
     * @param {object} $oEvent
     */
    public function handle($oEvent)
    {
        $oEvent->handled = true;
        // 清除其他输出
        if ($this->outputClean) {
            for ($level = ob_get_level(); $level > 0; $level --) {
                if (!@ob_end_clean()) {
                    ob_clean();
                }
            }
        }
        if ($oEvent instanceof TMExceptionEvent) {
            $this->handleException($oEvent->getException());
        } else {
            $this->handleError($oEvent);
        }
    }

    /**
     * @brief handleException
     * 处理异常
     *
     * @param {object} $exception
     */
    protected function handleException($exception)
    {
        if ($exception instanceof TMValidatorException) {
            $this->_hold = false;
        } else if ($exception instanceof TMWebNotFoundException) {
            $this->_notFound = true;
        }
        $isHttpException = ($exception instanceof TMHttpException) ? true : false;
        $this->_error = $data = array(
            'code' => $isHttpException ? $exception->statusCode : $exception->getCode(),
            'originalCode' => $isHttpException ? $exception->getCode() : 'exception',
            'type' => get_class($exception),
            'message' => $this->_hold && $isHttpException ? $this->getHttpHeader($exception->statusCode) : $exception->getMessage(),
            'file' => $exception->getFile(),
            'line' => $exception->getLine()
        );
        $this->_exception = $exception;

        if ($exception instanceof TMHttpException) {
            $this->display($data);
        } else {
            if (TAOMEE_DEBUG) {
                TM::app()->displayException($exception);
            } else {
                $this->display($data);
            }
        }
    }

    public function getException()
    {
        return $this->_exception;
    }

    /**
     * @brief getHttpHeader 
     * get http header message
     *
     * @param {integer} $httpCode http code
     * @param {string} $replacement
     *
     * @return {string}
     */
    protected function getHttpHeader($httpCode, $replacement='')
    {
        $httpCodes = array(
            100 => 'Continue',
            101 => 'Switching Protocols',
            102 => 'Processing',
            118 => 'Connection timed out',
            200 => 'OK',
            201 => 'Created',
            202 => 'Accepted',
            203 => 'Non-Authoritative',
            204 => 'No Content',
            205 => 'Reset Content',
            206 => 'Partial Content',
            207 => 'Multi-Status',
            210 => 'Content Different',
            300 => 'Multiple Choices',
            301 => 'Moved Permanently',
            302 => 'Found',
            303 => 'See Other',
            304 => 'Not Modified',
            305 => 'Use Proxy',
            307 => 'Temporary Redirect',
            310 => 'Too many Redirect',
            400 => 'Bad Request',
            401 => 'Unauthorized',
            402 => 'Payment Required',
            403 => 'Forbidden',
            404 => 'Not Found',
            405 => 'Method Not Allowed',
            406 => 'Not Acceptable',
            407 => 'Proxy Authentication Required',
            408 => 'Request Time-out',
            409 => 'Conflict',
            410 => 'Gone',
            411 => 'Length Required',
            412 => 'Precondition Failed',
            413 => 'Request Entity Too Large',
            414 => 'Request-URI Too Long',
            415 => 'Unsupported Media Type',
            416 => 'Requested range unsatisfiable',
            417 => 'Expectation failed',
            418 => 'I’m a teapot',
            422 => 'Unprocessable entity',
            423 => 'Locked',
            424 => 'Method failure',
            425 => 'Unordered Collection',
            426 => 'Upgrade Required',
            449 => 'Retry With',
            450 => 'Blocked by Windows Parental Controls',
            500 => 'Internal Server Error',
            501 => 'Not Implemented',
            502 => 'Bad Gateway ou Proxy Error',
            503 => 'Service Unavailable',
            504 => 'Gateway Time-out',
            505 => 'HTTP Version not supported',
            507 => 'Insufficient storage',
            509 => 'Bandwidth Limit Exceeded',
        );
        return isset($httpCodes[$httpCode]) ? $httpCodes[$httpCode] : $replacement;
    }

    /**
     * @brief handleError
     * 处理用于显示的错误，将错误码转为可读
     *
     * @param {object} $oEvent
     */
    protected function handleError($oEvent)
    {
        switch ($oEvent->code) {
            case E_WARNING:
                $sErrorType = 'PHP warning';
                break;
            case E_NOTICE:
                $sErrorType = 'PHP notice';
                break;
            case E_USER_ERROR:
                $sErrorType = 'User error';
                break;
            case E_USER_WARNING:
                $sErrorType = 'User warning';
                break;
            case E_USER_NOTICE:
                $sErrorType = 'User notice';
                break;
            default:
                $sErrorType = 'PHP error';
        }
        $this->_error = $data = array(
            'code' => 500,
            'originalCode' => $oEvent->code,
            'type' => $sErrorType,
            'message' => $oEvent->message,
            'file' => $oEvent->file,
            'line' => $oEvent->line
        );
        if (TAOMEE_DEBUG) {
            TM::app()->displayError($data['originalCode'], $data['message'], $data['file'], $data['line']);
        } else {
            $this->display($data);
        }
    }

    /**
     * @brief getError
     * 获取当前错误
     *
     * @return {array|null}
     */
    public function getError()
    {
        return $this->_error;
    }

    /**
     * hold
     * @return boolean
     */
    public function hold()
    {
        return $this->_hold;
    }

    /**
     * not found
     * @return boolean
     */
    public function notFound()
    {
        return $this->_notFound;
    }

    /**
     * @brief display
     * 显示错误信息
     *
     * @param {array} $data
     */
    protected function display($data)
    {
        $oApp = TM::app();
        if ($this->errorAction) {
            $oApp->runController($this->errorAction, true);
        } else {
            if (!headers_sent()) {
                header("HTTP/1.0 {$data['code']} ".$this->getHttpHeader($data['code'], $data['type']));
            }
            $oApp->displayError($data['code'], $data['message'], $data['file'], $data['line']);
        }
    }
}
