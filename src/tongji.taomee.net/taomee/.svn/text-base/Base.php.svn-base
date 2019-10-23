<?php

namespace taomee;

defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', false);

defined('DS') or define('DS', DIRECTORY_SEPARATOR);
defined('TAOMEE_PATH') or define('TAOMEE_PATH', dirname(__FILE__) . DS);
defined('VENDOR_PATH') or define('VENDOR_PATH', dirname(dirname(__FILE__)));

use taomee\base\Application;


class Base
{
    /**
     * @brief array 存储已找到的类对应路径
     */
    private static $_classMap = array();

    /**
     * @brief array 存储默认的可以寻找类的路径
     */
    private static $_coreDirectory = array();

    /**
     * @brief array 存储已经导入的路径
     */
    private static $_imports = array();

    /**
     * @brief array 路径别名
     */
    private static $_aliases = array('system' => TAOMEE_PATH);

    /**
     * @brief array
     * 自动加载类的路径
     */
    private static $_includePaths = array();

    /**
     * @brief
     * 当前的应用
     */
    private static $_app;

    /**
     * @brief array
     * 权限验证的类
     */
    private static $_authorizations = array();

    /**
     * @brief {array}
     * 存储Psr0路径对应信息
     */
    private static $_prefixesPsr0 = array('t' => array('taomee\\' => array(VENDOR_PATH)));

    /**
     * 自动加载类
     *
     * @param  string $sClassName
     * @return null
     */
    public static function autoload($sClassName)
    {
        if (isset(self::$_classMap[$sClassName])) {
            include(self::$_classMap[$sClassName]);
            return;
        }

        if (isset(self::$_core[$sClassName])) {
            include(TAOMEE_PATH . self::$_core[$sClassName]);
            return;
        }

        foreach (self::$_coreDirectory as $dir) {
            $sClassFile = $dir . DS . $sClassName . '.php';
            if (is_file($sClassFile)) {
                include($sClassFile);
                if (class_exists($sClassName, false)) {
                    return;
                }
            }
        }

        if (($pos = strpos($sClassName, '\\')) === false) {
            $sFilePath = str_replace('_', DS, $sClassName);
            foreach (self::$_includePaths as $path) {
                $sClassFile = $path . DS . $sFilePath . '.php';
                if (is_file($sClassFile)) {
                    include($sClassFile);
                    if (class_exists($sClassName, false)) {
                        return;
                    }
                }
            }
        } 
        // 命名空间
        else {
            $sFilePath = str_replace('\\', DS, $sClassName);
            $logicalPathPsr0 = substr($sFilePath, 0, $pos + 1) . strtr(substr($sFilePath, $pos + 1), '_', DIRECTORY_SEPARATOR);
            $first = $sClassName[0];
            if (isset(self::$_prefixesPsr0[$first])) {
                foreach (self::$_prefixesPsr0[$first] as $prefix => $dirs) {
                    if (0 === strpos($sClassName, $prefix)) {
                        foreach ($dirs as $dir) { 
                            if (file_exists($file = $dir . DS . $logicalPathPsr0 . '.php')) {
                                include($file);
                                if (class_exists($sClassName, false)) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @brief import
     *
     * @param {string} $sAlias
     * @param {boolean} $bAutoload
     *
     * @return {string}
     */
    public static function import($sAlias, $bAutoload=false)
    {
        if (isset(self::$_imports[$sAlias])) {
            return self::$_imports[$sAlias];
        }
        if (class_exists($sAlias, false)) {
            return self::$_imports[$sAlias] = $sAlias;
        }
        if (false === ($iClassPos = strrpos($sAlias, '.'))) {
            if ($bAutoload && self::autoload($sAlias)) {
                self::$_imports[$sAlias] = $sAlias;
            }
            return $sAlias;
        }

        $sClassName = (string)substr($sAlias, $iClassPos + 1);
        $bIsClass   = $sClassName !== '*';
        if ($bIsClass && class_exists($sClassName)) {
            return self::$_imports[$sAlias] = $sClassName;
        }

        if (false !== ($sClassPath = self::getPathWithAlias($sAlias))) {
            if ($bIsClass) {
                if (is_file($sClassPath . '.php')) {
                    if ($bAutoload) {
                        require($sClassPath . '.php');
                        return self::$_imports[$sAlias] = $sClassName;
                    } else {
                        return self::$_classMap[$sClassName] = $sClassPath . '.php';
                    }
                } else {
                    throw new \TMException(TM::t('taomee', '{classPath}.php文件不存在。', array('{classPath}' => $sClassPath)));
                }
            } else {
                if (null === self::$_includePaths) {
                    self::$_includePaths = array();
                }
                array_unshift(self::$_includePaths, $sClassPath);
                return self::$_imports[$sAlias] = $sClassPath;
            }
        } else {
            if (class_exists($sAlias, true)) {
                return self::$_imports[$sAlias] = $sAlias;
            } else {
                throw new \TMException(TM::t('taomee', '类名{alias}不符合命名规范。', array('{alias}' => $sAlias)));
            }
        }
    }

    /**
     * @brief getPathWithAlias
     *
     *
     * @param {string} $sAlias
     *
     * @return
     */
    public static function getPathWithAlias($sAlias)
    {
        if (isset(self::$_aliases[$sAlias])) {
            return self::$_aliases[$sAlias];
        } else if (false !== ($iRootPos = strpos($sAlias, '.'))) {
            $sRootAlias = substr($sAlias, 0, $iRootPos);
            if (isset(self::$_aliases[$sRootAlias])) {
                return self::$_aliases[$sAlias] = rtrim(self::$_aliases[$sRootAlias] . DS . str_replace('.', DS, substr($sAlias, $iRootPos + 1)), '*' . DS);
            }
        }
        return false;
    }

    /**
     * @brief setPathWithAlias
     *
     * @param {string} $sAlias
     * @param {string} $sPath
     */
    public static function setPathWithAlias($sAlias, $sPath)
    {
        if (empty($sPath)) {
            unset(self::$_aliases[$sAlias]);
        } else {
            self::$_aliases[$sAlias] = rtrim($sPath, DS);
            self::$_prefixesPsr0[$sAlias[0]][$sAlias] = (array) $sPath;
        }
    }

    /**
     * @brief addClassDirectory
     * 添加类所在路径
     * 可用于autoload方法
     *
     * @param {string} $sDirectory
     */
    public static function addClassDirectory($sDirectory)
    {
        if (!in_array($sDirectory, self::$_coreDirectory)) {
            self::$_coreDirectory[] = $sDirectory;
        }
    }

    /**
     * @brief addAuthorization
     * 添加权限验证的类
     */
    public static function addAuthorization(\TMAuthorizationInterface $authorization)
    {
        return self::$_authorizations[] = $authorization;
    }

    /**
     * @brief getAuthorization
     * 获取权限验证的类列表
     *
     * @return {array}
     */
    public static function getAuthorization()
    {
        return self::$_authorizations;
    }

    /**
     * @brief createWebApplication
     * 快捷方式创建web应用
     *
     * @param {array} $aApplicationConfig
     *
     * @return {object} TMWebApplication
     */
    public static function createWebApplication($aApplicationConfig=null)
    {
        return self::createApplication('\TMWebApplication', $aApplicationConfig);
    }

    /**
     * @brief createConsoleApplication
     * 快捷方式创建控制台应用
     *
     * @param {array} $aApplicationConfig
     *
     * @return {object} TMConsoleApplication
     */
    public static function createConsoleApplication($aApplicationConfig=null)
    {
        return self::createApplication('\TMConsoleApplication', $aApplicationConfig);
    }

    /**
     * @brief createApplication
     * 创建应用
     *
     * @param {String} $sClassName
     * @param {array} $aApplicationConfig
     *
     * @return {object} Application
     */
    public static function createApplication($sClassName, $aApplicationConfig=null)
    {
        return new $sClassName($aApplicationConfig);
    }

    /**
     * @brief getFrameworkPath
     * 获取框架路径
     *
     * @return {string}
     */
    public static function getFrameworkPath()
    {
        return TAOMEE_PATH;
    }

    /**
     * @brief createComponent
     * 生成组件
     *
     * @param {mixed} $mComponentConfig
     *
     * @return {object} TMComponent
     */
    public static function createComponent($mComponentConfig)
    {
        if (is_string($mComponentConfig)) {
            $sClassName = $mComponentConfig;
            $mComponentConfig = array();
        } else if (isset($mComponentConfig['class'])) {
            $sClassName = $mComponentConfig['class'];
            unset($mComponentConfig['class']);
        } else {
            throw new \TMException(TM::t('taomee', 'Component的配置中必须包含class。'));
        }

        if (!class_exists($sClassName, false)) {
            $sClassName = self::import($sClassName, true);
        }
        if (($n = func_num_args()) >1) {
            $args = func_get_args();
            unset($args[0]);
            $class = new \ReflectionClass($sClassName);
            $object = call_user_func_array(array($class, 'newInstance'), $args);
        } else {
            $object = new $sClassName();
        }  

        foreach ($mComponentConfig as $key => $val) {
            $object->$key = $val;
        }
        return $object;
    }

    /**
     * @brief t
     * 根据category翻译
     *
     * @param {string} $sCategory
     * @param {string} $sMessageId
     * @param {array} $aMessageParam
     *
     * @return {string}
     */
    public static function t($sCategory, $sMessageId, $aMessageParam=array())
    {
        if (!is_array($aMessageParam)) {
            $aMessageParam = array();
        }
        if (isset(self::$_app) && self::$_app->hasComponent('translator')) {
            $sMessageId = self::$_app->translator->translate($sCategory, $sMessageId);
        }
        return $aMessageParam !== array() ? strtr($sMessageId, $aMessageParam) : $sMessageId;
    }

    /**
     * @brief setApplication
     * 设置当前运行的app
     *
     * @param $app
     * @throw TMException
     */
    public static function setApplication($app)
    {
        if ($app instanceof Application) {
            self::$_app = $app;
        } else {
            throw new \TMException(self::t('taomee', '设置的必须为应用。'));
        }
    }

    /**
     * @brief app
     * 返回当前应用
     *
     * @return {object}
     */
    public static function app()
    {
        return self::$_app;
    }

    /**
     * @brief array 核心类的路径对应关系表
     */
    private static $_core = array(
        'TMComponent'              => 'base/TMComponent.php',
        'TMException'              => 'base/TMException.php',
        'TMHttpException'          => 'base/TMHttpException.php',
        'TMLog'                    => 'log/TMLog.php',
        'TMLogException'           => 'log/TMLog.php',
        'TMFatalException'         => 'base/TMException.php',
        'TMWebException'           => 'base/TMException.php',
        'TMWebNotFoundException'   => 'base/TMException.php',
        'TMModule'                 => 'base/TMModule.php',
        'TMFormModel'              => 'base/TMFormModel.php',
        'TMModel'                  => 'base/TMModel.php',
        'TMExceptionEvent'         => 'base/TMExceptionEvent.php',
        'TMDbConnection'           => 'db/TMDbConnection.php',
        'TMDbCommand'              => 'db/TMDbCommand.php',
        'TMDbException'            => 'db/TMDbException.php',
        'TMAuthorizationInterface' => 'security/TMAuthorizationInterface.php',
        'TMUserInterface'          => 'security/TMUserInterface.php',
        'TMValidator'              => 'validator/TMValidator.php',
        'TMValidatorException'     => 'validator/TMValidator.php',
        'TMValidatorInterface'     => 'validator/TMValidator.php',
        'TMExistValidator'         => 'validator/TMExistValidator.php',
        'TMStringValidator'        => 'validator/TMStringValidator.php',
        'TMNumberValidator'        => 'validator/TMNumberValidator.php',
        'TMRequiredValidator'      => 'validator/TMRequiredValidator.php',
        'TMDefaultValidator'       => 'validator/TMDefaultValidator.php',
        'TMEnumValidator'          => 'validator/TMEnumValidator.php',
        'TMInlineValidator'        => 'validator/TMInlineValidator.php',
        'TMRegexValidator'         => 'validator/TMRegexValidator.php',
        'TMUniqueValidator'        => 'validator/TMUniqueValidator.php',
        'TMTimeValidator'          => 'validator/TMTimeValidator.php',
        'TMEmailValidator'         => 'validator/TMEmailValidator.php',
        'TMWebApplication'         => 'web/TMWebApplication.php',
        'TMController'             => 'web/TMController.php',
        'TMDisplayInterface'       => 'web/TMDisplayInterface.php',
        'TMUploadFile'             => 'web/upload/TMUploadFile.php',
        'TMProto'                  => 'proto/TMProto.php',
        'TMArrayHelper'            => 'utils/TMArrayHelper.php',
        'TMCurlHelper'             => 'utils/TMCurlHelper.php',
        'TMFileHelper'             => 'utils/TMFileHelper.php',
        'TMEncode'                 => 'utils/TMEncode.php',
        'TMStringHelper'           => 'utils/TMStringHelper.php',
        'TMCache'                  => 'cache/TMCache.php',
        'TMConsoleApplication'     => 'console/TMConsoleApplication.php',
        'TMConsoleCommandRunner'   => 'console/TMConsoleCommandRunner.php',
        'TMConsoleCommand'         => 'console/TMConsoleCommand.php',
        'TMHelpCommand'            => 'console/TMHelpCommand.php',
        'TMDaemonCommand'          => 'console/TMDaemonCommand.php'
    );
}

spl_autoload_register(array('taomee\Base', 'autoload'));

