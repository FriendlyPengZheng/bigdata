<?php
/**
* @file ServerBag.php
* @brief $_SERVER操作类
* @author violet violet@taomee.com
* @version 1.0
* @date 2017-08-29
*/

namespace taomee\web\request;

class ServerBag extends ParameterBag
{
    /**
     * @brief getHeaders 
     * 返回http头
     *
     * @return {array}
     */
    public function getHeaders()
    {
        $headers = array();
        $contentHeaders = array('CONTENT_LENGTH' => true, 'CONTENT_MD5' => true, 'CONTENT_TYPE' => true);
        foreach ($this->parameters as $key => $value) {
            if (0 === strpos($key, 'HTTP_')) {
                $headers[substr($key, 5)] = $value;
            }
            // CONTENT_* are not prefixed with HTTP_
            elseif (isset($contentHeaders[$key])) {
                $headers[$key] = $value;
            }       
        }   
        return $headers;
    }
}
