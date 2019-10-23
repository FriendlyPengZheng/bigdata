<?php
class TMUrlManager extends TMComponent
{
    /**
     * @const {string} 参数设置方式--GET
     */
    const ROUTE_FORMAT_GET = 'get';

    /**
     * @const {string} 参数设置方式--路径
     */
    const ROUTE_FORMAT_PATH = 'path';

    /**
     * @var {string} 参数GET的名称
     */
    public $routeVar = 'r';

    /**
     * @var {string} 参数设置方式，默认为GET方式
     */
    private $_routeFormat = 'get';

    /**
     * 解析url
     * @param TMHttpRequest $request
     * @return mixed
     */
    public function parseUrl($request)
    {
        return $request->getParameter($this->routeVar, '');
    }

    /**
     * 重建url
     * @param array $sRoute
     * @param array $aParams
     * @return string
     */
    public function rebuildUrl($sRoute, $aParams = array())
    {
        $baseUrl = TM::app()->getHttp()->getBaseUrl();
        if ($this->_routeFormat == self::ROUTE_FORMAT_GET) {
            $aParams[$this->routeVar] = $sRoute;
        } else {
            $baseUrl .= '/' . $sRoute;
        }
        return $baseUrl . (empty($aParams) ? '' : '?' . http_build_query($aParams));
    }

    /**
     * 分解url
     * @param string $sUrl
     * @return string
     */
    public function breakUrl($sUrl, $key = null)
    {
        if (!isset($key)) {
            $key = $this->routeVar;
        }
        $sUrl = urldecode($sUrl);
        $iLen = strlen($key) + 1;
        $iStart = strpos($sUrl, $key . '=');
        if ($iStart === false) {
            return;
        }
        $iEnd = strpos($sUrl, '&', $iStart);
        if ($iEnd === false) {
            return substr($sUrl, $iStart + $iLen);
        }
        return substr($sUrl, $iStart + $iLen, $iEnd - $iStart - $iLen);
    }

    /**
     * 返回默认的route参数名
     * @return string
     */
    public function getRouteVar()
    {
        return $this->routeVar;
    }

    /**
     * 设置参数设置方式
     * @param {string} $value 参数设置方式
     */
    public function setRouteFormat($value)
    {
        if ($value == self::ROUTE_FORMAT_GET || $value == self::ROUTE_FORMAT_PATH) {
            $this->_routeFormat = $value;
        } else {
            throw new TMException(TM::t('taomee', 'TMUrlManager must be either "path" or "get".'));
        }
    }

    /**
     * 获取参数设置方式
     * @return {string}
     */
    public function getRouteFormat()
    {
        return $this->_routeFormat;
    }
}
