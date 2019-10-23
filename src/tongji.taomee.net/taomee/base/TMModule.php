<?php
abstract class TMModule extends \TMComponent
{
    public $preload = array();

    private $_id;

    private $_sBasePath = null;

    private $_components = array();
    private $_componentConfig = array();

    private $_modulePath = null;
    private $_modules = array();
    private $_moduleConfig = array();

    public function __construct($id, $aModuleConfig)
    {
        $this->_id = $id;

        if (is_string($aModuleConfig)) {
            $aModuleConfig = require($aModuleConfig);
        }
        if (isset($aModuleConfig['basePath'])) {
            $this->setBasePath($aModuleConfig['basePath']);
            unset($aModuleConfig['basePath']);
        }

        $this->configure($aModuleConfig);
        $this->preloadComponents();

        $this->init();
    }

    /**
     * @brief getId 
     * 获取ID
     *
     * @return {string}
     */
    public function getId()
    {
        return $this->_id;
    }

    /**
     * @brief setId 
     * 设置ID
     *
     * @param $id
     */
    public function setId($id)
    {
        $this->_id = $id;
    }

    /**
     * @brief __get 
     * 重写后可支持component的获取
     *
     * @param {string} $sName
     *
     * @return {mixed}
     */
    public function __get($sName)
    {
        if ($this->hasComponent($sName)) {
            return $this->getComponent($sName);
        } else {
            return parent::__get($sName);
        }
    }

    /**
     * @brief __isset 
     * 重写后可支持component的存在性判断
     *
     * @param {string} $sName
     *
     * @return {boolean}
     */
    public function __isset($sName)
    {
        if ($this->hasComponent($sName)) {
            return $this->getComponent($sName) !== null;
        } else {
            return parent::__isset($sName);
        }
    }

    /**
     * @brief getBasePath 
     * 获取基础路径
     *
     * @return {string}
     */
    public function getBasePath()
    {
        if ($this->_sBasePath === null) {
            $class = new ReflectionClass(get_class($this));
            return $this->_sBasePath = dirname($class->getFileName());
        } else {
            return $this->_sBasePath;
        }
    }

    /**
     * @brief setBasePath 
     * 设置基础路径
     *
     * @param {string} $sBasePath
     * @throw TMException
     */
    public function setBasePath($sBasePath)
    {
        if (false === ($this->_sBasePath = realpath($sBasePath)) || !is_dir($this->_sBasePath)) {
            throw new TMException(TM::t('taomee', '基础路径{basePath}不是有效的路径。', array('{basePath}' => $sBasePath)));
        }
    }

    /**
     * @brief configure 
     * 设置配置
     *
     * @param {array} $aConfig
     */
    public function configure($aConfig)
    {
        if (is_array($aConfig)) {
            foreach ($aConfig as $key => $value) {
                $this->$key = $value;
            }
        }
    }

    /**
     * @brief hasComponent 
     * 返回component或其配置是否存在
     *
     * @param {string} $key
     *
     * @return {boolean}
     */
    public function hasComponent($key)
    {
        return isset($this->_components[$key]) || isset($this->_componentConfig[$key]);
    }

    /**
     * @brief getComponent 
     * 获取component，不存在时创建
     *
     * @param {string} $key
     * @throw TMException
     *
     * @return {object}
     */
    public function getComponent($key)
    {
        if (isset($this->_components[$key])) {
            return $this->_components[$key];
        } elseif (isset($this->_componentConfig[$key])) {
            $config = $this->_componentConfig[$key];
            $component = TM::createComponent($config, $key);
            $component->init();
            return $this->_components[$key] = $component;
        } else {
            throw new TMException(TM::t('taomee', 'Component({$component})不存在。', array('{$component}' => $key)));
        }
    }

    /**
     * @brief setComponent 
     * 设置component的配置
     *
     * @param {string} $key
     * @param {array} $config
     * @param {boolean} $merge
     */
    public function setComponent($key, $config, $merge=true)
    {
        if ($config === null) {
            unset($this->_components[$key]);
            return;
        } elseif (isset($this->_components[$key])) {
            if (isset($config['class']) && get_class($this->_components[$key]) !== $config['class']) {
                unset($this->_components[$key]);
                $this->_componentConfig[$key] = $config;
                return;
            }
            foreach ($config as $k => $v) {
                if ($k !== 'class') {
                    $this->_components[$key]->$k = $v;
                }
            }
        } elseif (isset($this->_componentConfig[$key]['class'], $config['class']) && $this->_componentConfig[$key]['class'] !== $config['class']) {
            $this->_componentConfig[$key] = $config;
            return;
        }
        if (isset($this->_componentConfig[$key]['class']) && $merge) {
            $this->_componentConfig[$key] = array_merge($this->_componentConfig[$key], $config);
        } else {
            $this->_componentConfig[$key] = $config;
        }
    }

    /**
     * @brief setComponents 
     * 批量设置component
     *
     * @param {array} $configs
     */
    public function setComponents($configs)
    {
        foreach ($configs as $key => $config) {
            $this->setComponent($key, $config);
        }
    }

    /**
     * @brief setAlias 
     * 批量设置路径alias
     *
     * @param {array} $aMappings
     */
    public function setAlias($aMappings)
    {
        foreach ($aMappings as $name => $alias) {
            if (false !== ($sPath = TM::getPathWithAlias($alias))) {
                TM::setPathWithAlias($name, $sPath);
            } else {
                TM::setPathWithAlias($name, $alias);
            }
        }
    }

    /**
     * @brief setImport 
     *
     * @param {array} $aAliases
     */
    public function setImport($aAliases)
    {
        foreach ($aAliases as $alias) {
            TM::import($alias);
        }
    }

    /**
     * @brief preloadComponents 
     * 预加载要加载的component
     */
    protected function preloadComponents()
    {
        if (is_array($this->preload)) {
            foreach ($this->preload as $componentId) {
                $this->getComponent($componentId);
            }
        }
    }

    /**
     * @brief setModulePath 
     * 设置module默认路径
     *
     * @param {string} $modulePath
     */
    public function setModulePath($modulePath)
    {
        if (false === ($this->_modulePath = realpath($modulePath)) || !is_dir($this->_modulePath)) {
            throw new TMException(TM::t('taomee','The module path "{path}" is not a valid directory.',
                array('{path}' => $modulePath)
            ));
        }

    }

    /**
     * @brief getModulePath 
     * 获取module默认路径
     *
     * @return {string}
     */
    public function getModulePath()
    {
        if (null !== $this->_modulePath) {
            return $this->_modulePath;
        } else {
            return $this->_modulePath = $this->getBasePath() . DS . 'modules';
        }
    }

    /**
     * @brief hasModule 
     * 查询是否有指定的module
     *
     * @param {string} $id
     *
     * @return {boolean}
     */
    public function hasModule($id)
    {
        return isset($this->_modulePath[$id]) || isset($this->_modules[$id]);
    }

    /**
     * @brief setModules 
     * 设置modules
     *
     * @param {array} $configs
     * @param {boolean} $merge
     */
    public function setModules($configs, $merge=true)
    {
        foreach ($configs as $id => $config) {
            if (!isset($config['class'])) {
                $config['class'] = $id . '.' . ucfirst($id) . 'Module';
                TM::setPathWithAlias($id, $this->getModulePath() . DS . $id);
            }
            if (isset($this->_moduleConfig[$id]) && $merge) {
                $this->_moduleConfig[$key] = array_merge($this->_moduleConfig[$key], $config);
            } else {
                $this->_moduleConfig[$id] = $config;
            }
        }
    }

    /**
     * @brief getModule 
     * 获取指定的module，并初始化
     *
     * @param {string} $id
     *
     * @return {TMModule}
     */
    public function getModule($id)
    {
        if (isset($this->_modules[$id])) {
            return $this->_modules[$id];
        } else if (isset($this->_moduleConfig[$id])) {
            $config = $this->_moduleConfig[$id];
            $class = $config['class'];
            unset($config['class']);
            $module = TM::createComponent($class, $id, $config);
            return $this->_modules[$id] = $module;
        }
    }
}
