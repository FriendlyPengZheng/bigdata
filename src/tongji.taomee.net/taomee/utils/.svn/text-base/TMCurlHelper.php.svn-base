<?php
class TMCurlHelper extends TMComponent
{
    /**
     * var {array} 公共设置
     */
    public $options = array();

    /**
     * var {array} 单次请求设置
     */
    public $requestOptions = array();

    /**
     * var {string} 默认协议类型
     */
    public $defaultScheme = 'http';

    /**
     * var {array} 默认设置
     */
    private $_config = array(
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_FOLLOWLOCATION => true,
        CURLOPT_HEADER         => false,
        CURLOPT_VERBOSE        => true,
        CURLOPT_AUTOREFERER    => true,    
        CURLOPT_CONNECTTIMEOUT => 30, 
        CURLOPT_TIMEOUT        => 30
    );

    /**
     * var {string} 请求状态
     */
    private $_status;

    /**
     * var {string} 请求错误信息
     */
    private $_error;

    /**
     * var {string} 请求信息
     */
    private $_info;

    /**
     * Request for data by curl.
     * @param string $url
     * @param mixed $data query string or an array of parameters.
     * @param string $method request method, can be 'get' or 'post'.
     * @param string $dataType the fetched data type, can be 'var_export', 'json' or ''.
     * @param integer $timeout seconds to be timeout.
     * @return mixed
     * @throw TMException
     */
    public static function fetch($url, $data, $method = 'get', $dataType = 'var_export', $timeout = 60)
    {
        if (substr($url, 0, 7) !== 'http://' && substr($url, 0, 8) !== 'https://') {
            $url = 'http://' . $url;
        }
        if (is_array($data)) {
            $data = http_build_query($data);
        }

        $curlHandle = curl_init();
        if ('get' === strtolower($method)) {
            $url = $data ? ($url . '?' . $data) : $url;
            curl_setopt($curlHandle, CURLOPT_URL, $url);
        } else {
            curl_setopt($curlHandle, CURLOPT_URL, $url);
            curl_setopt($curlHandle, CURLOPT_POST, 1);
            curl_setopt($curlHandle, CURLOPT_POSTFIELDS, $data);
        }
        curl_setopt($curlHandle, CURLOPT_CONNECTTIMEOUT, $timeout) ;
        curl_setopt($curlHandle, CURLOPT_FOLLOWLOCATION, true) ;
        curl_setopt($curlHandle, CURLOPT_RETURNTRANSFER, 1);
        if (false === ($output = curl_exec($curlHandle))) {
            throw new TMException(TM::t('taomee', 'TMCurlHelper error [{errno}]{error}',
                        array('{errno}' => curl_errno($curlHandle), '{error}' => curl_error($curlHandle))));
        }
        curl_close($curlHandle);

        $result = '';
        switch ($dataType) {
            case 'var_export':
                @eval('$result=' . $output . ';');
                break;
            case 'json':
                $result = json_decode($output, true);
                break;
            default:
                $result = $output;
                break;
        }
        return $result;
    }

    /**
     * @brief setOption 
     * 设置请求选项
     *
     * @param {string}  $key
     * @param {mixed}   $value
     * @param {boolean} $default
     *
     * @return {$this}
     */
    public function setOption($key, $value, $default = false)
    {
        if ($default) {
            $this->options[$key] = $value;
        } else {
            $this->requestOptions[$key] = $value;
        }
        return $this;
    }

    /**
     * @brief setOptions
     * 批量设置请求选项
     *
     * @param {array}   $options
     * @param {boolean} $default
     *
     * @return {$this}
     */
    public function setOptions($options, $default = false)
    {
        if ($default) {
            $this->options = $options + $this->options;
        } else {
            $this->requestOptions = $options + $this->requestOptions;
        }
        return $this;
    }

    /**
     * @brief resetOption
     * 清空请求选项
     *
     * @param {string} $key
     *
     * @return {$this}
     */
    public function resetOption($key)
    {
        unset($this->requestOptions[$key]);
        return $this;
    }

    /**
     * @brief resetOptions 
     * 清空请求选项
     *
     * @return {$this}
     */
    public function resetOptions()
    {
        $this->requestOptions = array();
        return $this;
    }

    /**
     * @brief fetchOptions 
     * 获取请求选项
     *
     * @return {array}
     */
    public function fetchOptions()
    {
        return TMArrayHelper::recursiveMerge($this->requestOptions, $this->options, $this->_config);
    }

    /**
     * @brief addHeader
     * 添加请求头
     * @param {string} $key
     * @param {string} $value
     * 
     * @return {$this}
     */
    public function addHeader($key, $value)
    {
        $headers = isset($this->requestOptions[CURLOPT_HTTPHEADER]) ? 
            $this->requestOptions[CURLOPT_HTTPHEADER] : array();
        $headers[] = "$key:$value";
        return $this->setHeaders($headers);
    }

    /**
     * @brief setHeaders 
     * 设置请求头
     *
     * @param {array}   $headers
     * @param {boolean} $default
     *
     * @return {$this}
     */
    public function setHeaders($headers, $default = false)
    {
        return $this->setOption(CURLOPT_HTTPHEADER, $headers, $default);
    }

    /**
     * @brief getHeaders
     * 获取已设置的请求头
     *
     * @return {array}
     */
    public function getHeaders()
    {
        return isset($this->requestOptions[CURLOPT_HTTPHEADER]) ?
            $this->requestOptions[CURLOPT_HTTPHEADER] : array();
    }

    /**
     * @brief buildUrl
     * 生成URL
     *
     * @param {string} $url
     * @param {array}  $data
     *
     * @return {string}
     */
    public function buildUrl($url, $data = array())
    {
        $parsed = parse_url($url);

        isset($parsed['query']) ? parse_str($parsed['query'], $parsed['query']) : $parsed['query'] = array();

        $params = $data + $parsed['query'];
        $parsed['query'] = $params ? '?' . http_build_query($params) : '';

        if (!isset($parsed['scheme'])) {
            $parsed['scheme'] = $this->defaultScheme;
        }
        if (!isset($parsed['host'])) {
            $parsed['host'] = '';
        }
        if (!isset($parsed['path'])) {
            $parsed['path'] = '/';
        }

        $parsed['port'] = isset($parsed['port']) ? ':' . $parsed['port'] : '';
        $parsed['fragment'] = isset($parsed['fragment']) ? '#' . $parsed['fragment'] : '';

        return $parsed['scheme'] . '://' . $parsed['host'] . $parsed['port'] . $parsed['path'] . $parsed['query'] . $parsed['fragment'];
    }

    /**
     * @brief exce
     * 发送请求
     * 
     * @param {string}  $url
     * @param {array}   $options
     * @param {boolean} $debug
     *
     * @param {string}
     */
    public function exec($url, $options, $debug=false)
    {
        $this->_status = $this->_error = $this->_info = null;

        $ch = curl_init($url);
        curl_setopt_array($ch, $options);

        $output = curl_exec($ch);

        $this->_status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        if (!$output) {
            $this->_error = curl_error($ch);
            $this->_info = curl_getinfo($ch);
        }
        else if ($debug) {
            $this->_info = curl_getinfo($ch);
        }

        curl_close($ch);

        return $output;
    }

    /**
     * @brief get 
     * 发送GET请求
     *
     * @param {string}  $url
     * @param {array}   $data
     * @param {boolean} $debug
     *
     * @return {string}
     */
    public function get($url, $data = array(), $debug = false)
    {
        $url = $this->buildUrl($url, $data);
        $options = $this->fetchOptions();
        return $this->exec($url, $options, $debug);
    }

    /**
     * @brief post 
     * 发送POST请求
     *
     * @param {string}  $url
     * @param {array}   $data
     * @param {boolean} $debug
     *
     * @return {string}
     */
    public function post($url, $data = array(), $debug = false)
    {
        $url = $this->buildUrl($url);

        $options = $this->fetchOptions();
        $options[CURLOPT_POST] = true;
        $options[CURLOPT_POSTFIELDS] = $data;

        return $this->exec($url, $options, $debug);
    }

    /**
     * @brief getError
     * 获取请求错误信息
     *
     * @return {string|null}
     */
    public function getError()
    {
        return $this->_error;
    }

    /**
     * @brief getInfo
     * 获取请求信息
     *
     * @return {string|null}
     */
    public function getInfo()
    {
        return $this->_info;
    }

    /**
     * @brief getStatus
     * 获取请求状态
     *
     * @return {string}
     */
    public function getStatus()
    {
        return $this->_status;
    }
}
