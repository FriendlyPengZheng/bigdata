<?php
use taomee\base\Application;

class TMWebApplication extends Application
{
    /**
     * @brief 默认路径及controller
     */
    public $defaultController = 'home';

    /**
     * @brief 静态资源地址
     */
    public $staticPath = 'static';

    /**
     * @var string where views are saved in.
     */
    public $viewPath;

    /**
     * @var integer system id.
     */
    public $systemId = null;

    /**
     * @var string system domain name.
     */
    public $systemUrl = null;

    /**
     * @var array route-controller mapping.
     */
    public $routeMapping = array();

    /**
     * @var string current controller.
     */
    private $_sController = '';

    /**
     * @var string current action.
     */
    private $_sAction = '';

    /**
     * @var string current action extra.
     */
    private $_sActionExtra = '';

    /**
     * @var array class listen to display
     */
    private $_aAutoDisplay = array();

    /**
     * @var string
     */
    private $_currentRoute = '';

    /**
     * @var string
     */
    private $_returnType = '';

    /**
     * Init this component.
     */
    public function init()
    {
        TM::addClassDirectory(TAOMEE_PATH . 'web' . DS . 'helpers');
    }

    /**
     * @brief processRequest
     * web访问入口
     */
    public function processRequest()
    {
        $this->_currentRoute = $this->getUrlManager()->parseUrl($this->getHttp());
        $this->runController($this->_currentRoute);
    }

    /**
     * @brief runController
     * 运行controller
     *
     * @param {string} $sRoute
     * @param {boolean} $isErrorHandler
     * @throw TMWebException
     */
    public function runController($sRoute, $isErrorHandler = false)
    {
        if (($ca = $this->createController($sRoute)) !== null) {
            list($oController, $sAction) = $ca;
            $oController->run($sAction, $isErrorHandler);
        } else {
            throw new TMWebNotFoundException(TM::t('taomee', '{route}未找到。', array('{route}' => $sRoute ? $sRoute : $this->defaultController)));
        }
    }

    /**
     * @brief createController
     * 创建controller实例
     *
     * @param {string} $sRoute
     *
     * @return {array}
     */
    public function createController($sRoute)
    {
        $this->setController('')->setAction('')->setActionExtra('');
        if ('' === ($sRoute=trim($sRoute, '/'))) $sRoute = $this->defaultController;
        $sRoute .= DS;
        $sControllerId = $sBasePath = null;
        while (($iDelimiterPos = strpos($sRoute, '/')) !== false) {
            $sPartialId = substr($sRoute, 0, $iDelimiterPos);
            $sRoute     = (string)substr($sRoute, $iDelimiterPos+1);
            if (!preg_match('/^\w+$/', $sPartialId)) return;
            if (!isset($sControllerId)) {
                $sControllerId = $sPartialId;
            } else {
                $sControllerId .= ".$sPartialId";
            }
            if (isset($this->routeMapping[$sControllerId])) {
                $config = $this->routeMapping[$sControllerId];
                if ($component = $this->_getComponent($sControllerId, $config)) {
                    $this->setController(str_replace('.', '/', $sControllerId));
                    return array($component, $this->parseActionParams($sRoute));
                }
                $sRoute = $config . DS . $sRoute;
                $sControllerId = $sBasePath = null;
                continue;
            }
            if (!isset($sBasePath)) $sBasePath = $this->getControllerPath();
            $id = $sClassName = ucfirst($sPartialId);
            $sClassFile = $sBasePath . DS . $sClassName . '.php';
            if (is_file($sClassFile)) {
                if (!class_exists($sClassName)) require($sClassFile);
                if (class_exists($sClassName) && is_subclass_of($sClassName, 'TMController')) {
                    $id[0] = strtolower($id[0]);
                    $id = $sControllerId . DS . $id;
                    $this->setController(str_replace('.', DS, $sControllerId));
                    $oController = new $sClassName($id);
                    $oController->init();
                    return array($oController, $this->parseActionParams($sRoute));
                }
                return;
            }
            $sBasePath .= DS . $sPartialId;
        }
    }

    /**
     * @brief _getComponent
     * 根据配置获取或创建component
     *
     * @param {string} $id
     * @param {mixed} $config
     *
     * @return {TMComponent|false}
     */
    private function _getComponent($id, $config=null)
    {
        if ($this->hasComponent($id)) {
            return $this->getComponent($id);
        } elseif (isset($config['class'])) {
            // component
            return TM::createComponent($config, $id);
        } else {
            // controller
            return false;
        }
    }

    /**
     * @brief getControllerPath
     * 获取controller的基础路径
     *
     * @return {string}
     */
    public function getControllerPath()
    {
        return $this->getBasePath() . DS . 'controller';
    }

    /**
     * @brief parseActionParams
     * 解析action
     *
     * @param {string} $sRoute
     *
     * @return {string}
     */
    public function parseActionParams($sRoute)
    {
        if (false !== ($iDelimiterPos = strpos($sRoute, '/'))) {
            if ($extra = substr($sRoute, $iDelimiterPos + 1)) {
                if ($pos = strpos($extra, '/')) {
                    $extra = substr($extra, 0, $pos);
                }
                $this->setActionExtra($extra);
            }
            return substr($sRoute, 0, $iDelimiterPos);
        } else {
            return $sRoute;
        }
    }

    /**
     * @brief getSystemViewPath
     * 获取view路径
     *
     * @return {string}
     */
    public function getSystemViewPath()
    {
        if ($this->viewPath === null) {
            return $this->getBasePath() . DS . 'view';
        } else {
            $this->viewPath;
        }
    }

    /**
     * @brief registerCoreComponent
     * 注册核心组件
     *
     * @access protected
     */
    protected function registerCoreComponent()
    {
        parent::registerCoreComponent();
        $aComponents = array(
            'http' => array(
                'class' => 'system.web.TMHttpRequest'
            ),
            'urlManager' => array(
                'class' => 'system.web.TMUrlManager'
            ),
            'session' => array(
                'class' => 'system.web.TMHttpSession'
            ),
            'user' => array(
                'class' => 'system.security.TMUser'
            ),
            'template' => array(
                'class'       => 'system.web.template.TMTemplateAdapter',
                'templateDir' => $this->getSystemViewPath(),
                'compileDir'  => $this->getRuntimePath() . DS . 'compiles',
                'cacheDir'    => $this->getRuntimePath() . DS . 'caches'
            )
        );
        $this->setComponents($aComponents);
    }

    /**
     * @brief getTemplate
     * 获取模板对象
     *
     * @return {object}
     */
    public function getTemplate()
    {
        return $this->getComponent('template');
    }

    /**
     * @brief getHttp
     * 获取http模块
     *
     * @return {object}
     */
    public function getHttp()
    {
        return $this->getComponent('http');
    }

    /**
     * @brief getUrlManager
     * 解析url
     *
     * @return {object}
     */
    public function getUrlManager()
    {
        return $this->getComponent('urlManager');
    }

    /**
     * Get user component.
     * return User
     */
    public function getUser()
    {
        return $this->getComponent('user');
    }

    /**
     * Get system id.
     * @return integer
     */
    public function getSystemId()
    {
        return $this->systemId;
    }

    /**
     * Get system domain name.
     * @return string
     */
    public function getSystemUrl()
    {
        return $this->systemUrl;
    }

    /**
     * Set current controller.
     * @param string $controller
     */
    public function setController($controller)
    {
        $this->_sController = $controller;
        return $this;
    }

    /**
     * Get current controller.
     * @return string
     */
    public function getController()
    {
        return $this->_sController;
    }

    /**
     * Set current action.
     * @param string $action
     */
    public function setAction($action)
    {
        $this->_sAction = $action;
        return $this;
    }

    /**
     * Get current action.
     * @return string
     */
    public function getAction()
    {
        return $this->_sAction;
    }

    /**
     * Set current action extra.
     * @param string $actionExtra
     */
    public function setActionExtra($actionExtra)
    {
        $this->_sActionExtra = $actionExtra;
        return $this;
    }

    /**
     * Get current action extra.
     * @return string
     */
    public function getActionExtra()
    {
        return $this->_sActionExtra;
    }

    /**
     * Get complete route.
     * @return string
     */
    public function getCompleteRoute()
    {
        if ($this->_sActionExtra === '') {
            $this->_sActionExtra = '01';
        }
        return strtolower($this->_sController . '/' . $this->_sAction . '/' . $this->_sActionExtra);
    }

    /**
     * @brief setAutoDisplay
     * set class listen to display
     *
     * @param {TMDisplayInterface} $display
     *
     * @return {object} TMWebApplication
     */
    public function setAutoDisplay(TMDisplayInterface $display)
    {
        $this->_aAutoDisplay[] = $display;
        return $this;
    }

    /**
     * @brief getAutoDisplay
     * get class listen to display
     *
     * @return {array}
     */
    public function getAutoDisplay()
    {
        return $this->_aAutoDisplay;
    }

    /**
     * @brief getStaticPath
     * 获取静态资源地址
     *
     * @return {string}
     */
    public function getStaticPath()
    {
        return $this->staticPath;
    }

    public function getCurrentRoute()
    {
        return $this->_currentRoute;
    }
    
    /**
     * @brief setReturnType 
     * 设置返回格式
     *
     * @param {string} $returnType
     */
    public function setReturnType($returnType)
    {
        $this->_returnType = $returnType;
    }

    /**
     * @brief getReturnType 
     * 获取返回格式
     *
     * @return {string}
     */
    public function getReturnType()
    {
        return $this->_returnType;
    }
}
