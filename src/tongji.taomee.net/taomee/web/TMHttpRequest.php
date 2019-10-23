<?php
/**
* @file TMHttpRequest.php
* @brief 用户请求
* @author violet violet@taomee.com
* @version 1.0
* @date 2017-08-29
*/

use taomee\web\request\ParameterBag;
use taomee\web\request\HeaderBag;
use taomee\web\request\ServerBag;

class TMHttpRequest extends TMComponent
{
    protected $query;

    protected $request;

    protected $server;

    protected $cookies;

    protected $headers;

    private $_hostInfo;
    private $_baseUrl;
    private $_scriptUrl;

    /**
     * Init this component.
     */
    public function init()
    {
        $this->normalizeRequest();
        $this->query = new ParameterBag($_GET);
        $this->request = new ParameterBag($_POST);
        $this->server = new ServerBag($_SERVER);
        $this->cookies = new ParameterBag($_COOKIE);
        $this->headers = new HeaderBag($this->server->getHeaders());
    }

    /**
     * @brief normalizeRequest 
     * 过滤用户参数
     */
    protected function normalizeRequest()
    {
        if (function_exists('get_magic_quotes_gpc') && get_magic_quotes_gpc()) {
            if (isset($_GET)) {
                $_GET = $this->stripSlashes($_GET);
            }
            if (isset($_POST)) {
                $_POST = $this->stripSlashes($_POST);
            }
            if (isset($_REQUEST)) {
                $_REQUEST = $this->stripSlashes($_REQUEST);
            }
            if (isset($_COOKIE)) {
                $_COOKIE = $this->stripSlashes($_COOKIE);
            }
        }
    }

    /**
     * @brief stripSlashes 
     * 过滤数组
     *
     * @param {array} $toFilters
     *
     * @return {array}
     */
    protected function stripSlashes(&$toFilters)
    {
        if (is_array($toFilters)) {
            if (0 === count($toFilters)) return $toFilters;

            $keys = array_map('stripslashes', array_keys($toFilters));
            $data = array_combine($keys, array_values($toFilters));

            return array_map(array($this, 'stripSlashes'), $toFilters);
        } else {
            return stripslashes($toFilters);
        }
    }

    /**
     * @brief getParameter
     * 返回请求的参数值，可设置默认值
     *
     * @param {string} $sQueryKey
     * @param {mixed} $mDefaultValue
     *
     * @return {mixed}
     */
    public function getParameter($sQueryKey, $mDefaultValue = null)
    {
        return isset($_GET[$sQueryKey]) ? $_GET[$sQueryKey] : (isset($_POST[$sQueryKey]) ? $_POST[$sQueryKey] : $mDefaultValue);
    }

    /**
     * 设置请求参数值
     * @param string $sQueryKey
     * @param mixed $mValue
     */
    public function setParameter($sQueryKey, $mValue)
    {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $_POST[$sQueryKey] = $mValue;
        } else {
            $_GET[$sQueryKey] = $mValue;
        }
    }

    /**
     * 返回请求的参数值
     * @param array $aQueryKey
     * @return array
     */
    public function getParameters($aQueryKey)
    {
        $aReturn = array();
        if (!is_array($aQueryKey)) {
            $aQueryKey = func_get_args();
        }
        foreach ($aQueryKey as $query) {
            $aReturn[$query] = $this->getParameter($query);
        }
        return $aReturn;
    }

    /**
     * @brief getFullParameters 
     * 获取所有用户传输的参数
     *
     * @return {array}
     */
    public function getFullParameters()
    {
        return $_GET + $_POST;
    }

    /**
     * @brief getPost
     * 获取POST请求的参数值
     *
     * @param {string} $sQueryKey
     * @param {mixed} $mDefaultValue
     *
     * @return {mixed}
     */
    public function getPost($sQueryKey, $mDefaultValue=null)
    {
        return isset($_POST[$sQueryKey]) ? $_POST[$sQueryKey] : $mDefaultValue;
    }

    /**
     * @brief isAjaxRequest
     * 返回请求是否为ajax请求
     *
     * @return {boolean}
     */
    public function isAjaxRequest()
    {
        if (isset($_SERVER['HTTP_X_REQUESTED_WITH']) && $_SERVER['HTTP_X_REQUESTED_WITH'] === 'XMLHttpRequest') {
            return true;
        }

        if ($this->getParameter('ajax')) {
            return true;
        }

        return false;
    }

    /**
     * 判断请求是否为POST请求
     * @return {boolean}
     */
    public function isPost()
    {
        return isset($_SERVER['REQUEST_METHOD']) && $_SERVER['REQUEST_METHOD'] === 'POST';
    }

    /**
     * @brief getQueryString
     * 获取请求链接的参数
     *
     * @return {string}
     */
    public function getQueryString()
    {
        return isset($_SERVER['QUERY_STRING']) ? $_SERVER['QUERY_STRING'] : '';
    }

    /**
     * @brief getHostInfo
     * 获取域名信息
     *
     * @return {string}
     */
    public function getHostInfo()
    {
        if (null === $this->_hostInfo) {
            if (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] == 'on') {
                $http = 'https';
            } else {
                $http = 'http';
            }
            if (isset($_SERVER['HTTP_HOST'])) {
                $this->_hostInfo = $http.'://'.$_SERVER['HTTP_HOST'];
            } else {
                $this->_hostInfo = $http.'://'.$_SERVER['SERVER_NAME'];
            }
        }
        return $this->_hostInfo;
    }

    /**
     * @brief getBaseUrl
     * 获取基本Url
     *
     * @return {string}
     */
    public function getBaseUrl()
    {
        if (null === $this->_baseUrl) {
            $this->_baseUrl = rtrim(dirname($this->getScriptUrl()), '\\/');
        }
        return $this->getHostInfo() . $this->_baseUrl;
    }

    /**
     * @brief getScriptUrl
     * 获取运行脚本的URL
     *
     * @return {string}
     */
    public function getScriptUrl()
    {
        if (null === $this->_scriptUrl) {
            $scriptName = basename($_SERVER['SCRIPT_FILENAME']);
            if (basename($_SERVER['SCRIPT_NAME']) === $scriptName) {
                $this->_scriptUrl = $_SERVER['SCRIPT_NAME'];
            } elseif (basename($_SERVER['PHP_SELF']) === $scriptName) {
                $this->_scriptUrl = $_SERVER['PHP_SELF'];
            } elseif (isset($_SERVER['ORIG_SCRIPT_NAME']) && basename($_SERVER['ORIG_SCRIPT_NAME']) === $scriptName) {
                $this->_scriptUrl = $_SERVER['ORIG_SCRIPT_NAME'];
            } elseif (($pos = strpos($_SERVER['PHP_SELF'],'/'.$scriptName)) !== false) {
                $this->_scriptUrl = substr($_SERVER['SCRIPT_NAME'],0,$pos).'/'.$scriptName;
            } elseif(isset($_SERVER['DOCUMENT_ROOT']) && strpos($_SERVER['SCRIPT_FILENAME'],$_SERVER['DOCUMENT_ROOT']) === 0) {
                $this->_scriptUrl = str_replace('\\','/',str_replace($_SERVER['DOCUMENT_ROOT'],'',$_SERVER['SCRIPT_FILENAME']));
            } else {
                throw new TMException(TM::t('taomee', '找不到脚本的URL。'));
            }
        }
        return $this->_scriptUrl;

    }

    /**
     * Redirect to the given url,
     * @param string $url
     * @param boolean $ajax
     * @param integer $statusCode
     */
    public function redirect($url, $ajax = false, $statusCode = 302)
    {
        if ($ajax) {
            header('HTTP_X_REQUESTED_WITH: XMLHttpRequest');
        }
        header("Location: $url", true, $statusCode);
        exit(0);
    }

    /**
     * Record non-ajax request url in session.
     */
    public function recordRequest()
    {
        if (!$this->isAjaxRequest()) {
            $lastUrl = $this->getRecordCurrentUrl();
            if (($currentUrl = $this->getUrl()) !== $lastUrl) {
                TM::app()->session->add('last_request_url', $lastUrl);
                TM::app()->session->add('current_request_url', $currentUrl);
            }
        }
    }

    /**
     * Get complete url of current request.
     * @return string
     */
    public function getUrl()
    {
        return $this->getHostInfo() . $_SERVER['REQUEST_URI'];
    }

    /**
     * Get client's ip.
     * @return string
     */
    public function getIp()
    {
        $ip = '';
        foreach (array('REMOTE_ADDR', 'HTTP_CLIENT_IP', 'HTTP_FROM', 'HTTP_X_FORWARDED_FOR') as $v) {
            if (isset($_SERVER[$v])) {
                if (! preg_match('/^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/', $_SERVER[$v])) {
                    continue;
                } 
                $ip = $_SERVER[$v];
            }
        }
        if ($ip === '') {
            $ip = '127.0.0.1';
        }
        return $ip;
    }

    /**
     * Get client's user agent.
     *
     * @return string
     */
    public function getUserAgent()
    {
        return isset($_SERVER['HTTP_USER_AGENT']) ? $_SERVER['HTTP_USER_AGENT'] : '';
    }

    /**
     * Get record last url from session.
     * @return string
     */
    public function getRecordLastUrl()
    {
        return TM::app()->session->get('last_request_url');
    }

    /**
     * Get record current url from session.
     * @return string
     */
    public function getRecordCurrentUrl()
    {
        return TM::app()->session->get('current_request_url');
    }

    /**
     * @brief header 
     * 获取请求头的值
     *
     * @param {string} $key
     * @param {mixed} $default
     *
     * @return {mixed}
     */
    public function header($key = null, $default = null)
    {
        if ($key === null) {
            return $this->headers->all();
        }
        return $this->headers->get($key, $default);
    }

    /**
     * @brief method 
     * 获取http方式
     *
     * @return {string}
     */
    public function method()
    {
        return $this->server->get('REQUEST_METHOD');
    }

    public function parseParamFromPath($path, $format)
    {
        if (preg_match_all('/\{(\w+)\}/', $format, $m)) {
            $searches = [];
            $replaces = [];
            $params = [];
            foreach ($m[1] as $index => $key) {
                $params[$key] = null;
                $searches[] = $m[0][$index];
                $replaces[] = '(\w+)';
            }
        } else {
            return false;
        }
        $format = '/'.str_replace('/', '\/', str_replace($searches, $replaces, $format)).'/';
        if (preg_match_all($format, $path, $matches)) {
            foreach ($matches[1] as $index => $value) {
                $key = $searches[$index];
                $key = substr($key, 1, -1);
                $params[$key] = $value;
            }
            return $params;
        } else {
            return false;
        }
    }
}
