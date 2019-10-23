<?php
class TMController extends TMComponent
{
    /**
     * @brief 唯一标志ID
     */
    private $_id;

    /**
     * @brief 默认action
     */
    public $defaultAction = 'index';

    /**
     * @var array the response date for request.
     */
    private $_response = array();

    /**
     * @var Template template instance.
     */
    private $_template;

    /**
     * @var array the header for every response
     */
    private $_headers = array();

    public function __construct($id)
    {
        $this->_id = $id;
    }

    /**
     * 初始化
     */
    public function init()
    {
    }

    /**
     * @brief getId 
     * 获取唯一标志ID 
     *
     * @return {string}
     */
    public function getId()
    {
        return $this->_id;
    }

    /**
     * 过滤参数
     * @return array
     */
    public function filters()
    {
        return array();
    }

    /**
     * action对应的接收参数
     * @return array
     */
    public function actions()
    {
        return array();
    }

    /**
     * 通用参数，所有action都接收
     * @return array
     */
    public function commonParameters()
    {
        return array();
    }

    /**
     * 统一的入口函数
     *
     * @param string                 $sAction
     * @param {boolean} $isErrorHandler
     * @throw TMWebNotFoundException
     */
    public function run($sAction, $isErrorHandler)
    {
        if (($action = $this->createAction($sAction)) === null) {
            throw new TMWebNotFoundException(
                TM::t('taomee', '{controller}=>{action}方法不存在。', array(
                    '{controller}' => get_class($this),
                    '{action}'     => $sAction
                )
            ));
        }

        TM::app()->setAction($action);

        // check authority here.
        // 错误处理方法不进行验证
        if (!$isErrorHandler) {
            foreach (TM::getAuthorization() as $authorization) {
                $authorization->check();
            }
        }

        $this->runActionWithFilters($action, $this->filters());
    }

    /**
     * @brief createAction 
     * 根据给的字符串在controller中找到对应的方法
     *
     * @param {string} $sAction
     *
     * @return null|string
     */
    protected function createAction($sAction)
    {
        if ($sAction === '') $sAction = $this->defaultAction;

        if (method_exists($this, $sAction)) {
            $method = new ReflectionMethod($this, $sAction);
            if ($method->isPublic()) {
                return $sAction;
            }
        }
    }

    public function runActionWithFilters($sAction, $aFilters)
    {
        $this->runAction($sAction);
    }

    /**
     * @brief runAction 
     * 执行方法，之前会调用beforeRunAction
     *
     * @param {string} $sAction
     */
    public function runAction($sAction)
    {
        $aParameters = $this->getActionParams($sAction);
        // 参数过滤
        $aFilterParameters = array_filter($aParameters, 'is_string');
        // 注意这里不要引入不必要的参数，可能用于签名
        $aParameters = array_merge($aParameters, $aFilterParameters);
        $this->beforeRunAction($aParameters);
        $this->filterActionParams($aParameters);
        $this->assign('param', array_merge(array_map(array('TMStringHelper', 'deephtmlspecialchars'), $aParameters), array(
            'script_name' => TM::app()->getHttp()->getScriptUrl(),
            TM::app()->getUrlManager()->getRouteVar() => TM::app()->getUrlManager()->parseUrl(TM::app()->getHttp())
        )));
        $this->$sAction($aParameters);
    }

    /**
     * @brief getActionParams
     * 获取方法的参数
     *
     * @param {string} $sAction 方法名称
     *          可在actions方法中设置所要获取的具体参数及默认值
     *          如果没有设置，默认获取$_GET+$_POST
     *
     * @return {array}
     */
    public function getActionParams($sAction)
    {
        $oRequest = TM::app()->getHttp();
        $aParameters = array();
        $sAction = strtolower($sAction);
        if ($aActions = $this->actions()) {
            $aActions = array_change_key_case($aActions);
        }
        if (isset($aActions[$sAction])) {
            $aWant = array_merge($this->commonParameters(), $aActions[$sAction]);
            foreach ($aWant as $queryKey => $query) {
                $aParameters[$queryKey] = $oRequest->getParameter($queryKey, $query);
            }
        } else {
            $aParameters = $_GET + $_POST;
        }
        return $aParameters;
    }

    /**
     * This method will be called before assign params.
     * @param array $aParameters
     */
    protected function filterActionParams(&$aParameters)
    {
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters)
    {
    }

    /**
     * @brief display
     * 显示模板
     *
     * @param {string} $sTplFile
     */
    public function display($sTplFile)
    {
        $this->beforeReturn();
        $this->assign('res_server', TM::app()->getStaticPath());
        $this->assign('application_info', array(
            'name' => TM::app()->getName(),
            'version' => TM::app()->getVersion(),
            'is_release' => !TAOMEE_DEBUG,
            'locale' => TM::app()->getLocale(),
            'params' => TM::app()->getParams()
        ));
        foreach (TM::app()->getAutoDisplay() as $display) {
            $display->assignListener($this);
        }
        $this->_getTemplate()->assign('response', $this->_response);
        $this->_getTemplate()->display($sTplFile);
        exit(0);
    }

    /**
     * 纯ajax请求返回
     *
     * @param interval $iRst 标志位 0：正确；其他：错误
     * @param mixed $mData 返回数据
     */
    public function ajax($iRst, $mData = null)
    {
        $this->beforeReturn();
        if ($iRst) {
            echo json_encode(array('result' => $iRst, 'err_desc' => $mData));
        } else {
            echo json_encode(array('result' => $iRst, 'data' => $mData));
        }
        exit(0);
    }

    /**
     * @brief jsonp 
     * jsonp格式返回
     *
     * @param {integer} $iRst
     * @param {mixed} $mData
     * @param {string} $callback
     */
    public function jsonp($iRst, $mData = null, $callback = 'callback') 
    {
        $callback = TM::app()->getHttp()->getParameter($callback);
        $result = array(
            'result' => $iRst
        );
        if ($iRst) {
            $result['err_desc'] = $mData;
        } else {
            $result['data'] = $mData;
        }
        $this->beforeReturn();
        header('Content-Type: application/javascript');
        echo $callback . '(' . json_encode($result) . ');';
        exit(0);
    }

    /**
     * @brief _getTemplate
     * 获取模板，若不存在创建
     *
     * @return {object}
     */
    private function _getTemplate()
    {
        if ($this->_template) {
            return $this->_template;
        } else {
            return $this->_template = TM::app()->getTemplate();
        }
    }

    /**
     * 向模板赋值
     *
     * @param {mixed} $key
     * @param {mixed} $value
     * @param {boolean} $merge
     */
    public function assign($key, $value = null, $merge = false)
    {
        if (is_array($key)) {
            foreach ($key as $k => $v) {
                $this->assign($k, $v, $merge);
            }
        } else {
            if ($merge) {
                if (isset($this->_response[$key]) && is_array($this->_response[$key])) {
                    $this->_response[$key] = array_merge($this->_response[$key], $value);
                    return;
                }
            }
            $this->_response[$key] = $value;
        }
    }

    /**
     * @brief beforeReturn 
     * 返回前调用
     */
    protected function beforeReturn()
    {
        foreach ($this->_headers as $header) {
            header($header);
        }
    }

    /**
     * @brief setResponseHeader 
     * 设置响应头信息
     *
     * @param {string} $key
     * @param {string} $value
     */
    public function setResponseHeader($key, $value)
    {
        $this->_headers[$key] = $value;
    }
}
