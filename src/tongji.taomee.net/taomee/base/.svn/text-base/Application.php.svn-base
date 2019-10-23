<?php

namespace taomee\base;

use TMLogException;
use TMLog;
use TMFatalException;
use TMValidatorException;

abstract class Application extends \TMModule
{
    /**
     * @var string the application name
     */
    public $name;

    /**
     * @var string the application version
     */
    public $version;

    /**
     * @var string The application locale
     */
    public $locale;

    /**
     * @var array The application supported locales
     */
    public $supportedLocales = array();

    /**
     * @var string The default timezone
     */
    public $timezone = 'Asia/Shanghai';

    /**
     * @var array The application extra params
     */
    public $params = array();

    private $_id;

    private $_sBasePath;

    private $_sRuntimePath;

    private $_startTime;

    public function __construct($aApplicationConfig=null)
    {
        date_default_timezone_set($this->timezone);

        \TM::setApplication($this);
        if (is_string($aApplicationConfig)) {
            $aApplicationConfig = require($aApplicationConfig);
        }
        if (isset($aApplicationConfig['basePath'])) {
            $this->setBasePath($aApplicationConfig['basePath']);
            unset($aApplicationConfig['basePath']);
        } else {
            $this->setBasePath(dirname(\TM::getFrameworkPath()) . DS . 'application');
        }
        \TM::setPathWithAlias('application', $this->getBasePath());
        \TM::setPathWithAlias('webroot', dirname($_SERVER['SCRIPT_FILENAME']));

        if (isset($aApplicationConfig['aliases'])) {
            $this->setAlias($aApplicationConfig['aliases']);
            unset($aApplicationConfig['aliases']);
        }

        $this->initSystemHandle();
        $this->registerCoreComponent();

        $this->configure($aApplicationConfig);
        $this->preloadComponents();

        $this->init();
    }

    /**
     * @brief run
     * 入口
     */
    public function run()
    {
        $this->_startTime = microtime(true);
        $this->processRequest();
    }

    /**
     * @brief processRequest
     * 必须重加载
     */
    abstract public function processRequest();

    /**
     * @brief initSystemHandle
     * 初始化错误处理函数跟异常处理函数
     */
    public function initSystemHandle()
    {
        if (TAOMEE_DEBUG) {
            error_reporting(E_ALL);
            ini_set('display_errors', 1);
        } else {
            error_reporting(0);
            ini_set('display_errors', 0);
        }
        set_error_handler(array($this, 'handleError'));
        set_exception_handler(array($this, 'handleException'));
    }

    /**
     * @brief handleException
     * 通用异常处理函数
     */
    public function handleException($oException)
    {
        if ($oException instanceof TMFatalException) {
            die('Fatal exception: ' . $oException->getMessage());
        }
        // log the exception
        if (!($oException instanceof TMLogException) && !($oException instanceof TMValidatorException)) {
            $sLog = $oException->getMessage() . "\n"
                  . "Stack trace:\n"
                  . $oException->getTraceAsString() . "\n";
            if (isset($_SERVER['REQUEST_URI'])) {
                $sLog .= 'REQUEST_URI=' . $_SERVER['REQUEST_URI'];
            }
            $this->getLog()->log($sLog,
                $oException instanceof TMValidatorException ? TMLog::TYPE_WARNING : TMLog::TYPE_ERROR);
        }
        // something else
        try {
            \TM::import('system.base.TMExceptionEvent', true);
            $oExceptionEvent = new \TMExceptionEvent($oException);
            if (($oErrorHandler = $this->getErrorHandler()) != null) {
                $oErrorHandler->handle($oExceptionEvent);
            } else {
                $this->displayException($oException);
            }
        } catch (Exception $e) {
            $this->displayException($e);
        }
    }

    /**
     * @brief displayException
     * 通用显示异常
     *
     * @param {object} $oException
     */
    public function displayException($oException)
    {
        if (TAOMEE_DEBUG) {
            if (\TM::app()->getHttp()->isAjaxRequest()) {
                echo json_encode(array('result' => 1, 'err_desc' => $oException->getMessage()));
            } else {
                echo '<h1>' . get_class($oException) . '</h1>';
                echo '<p>' . $oException->getMessage() . '</p>';
                echo '<pre>' . $oException->getTraceAsString() . '</pre>';
            }
            exit(0);
        }
    }

    /**
     * @brief handleError
     * 通用错误处理函数
     */
    public function handleError($iErrorCode, $message, $file, $line)
    {
        $sLog = "$message ($file:$line)\nStack trace:\n";
        $trace = debug_backtrace();
        foreach ($trace as $i => $t) {
            if (!isset($t['file'])) {
                $t['file'] = 'unknown';
            }
            if (!isset($t['line'])) {
                $t['line'] = 0;
            }
            if (!isset($t['function'])) {
                $t['function'] = 'unknown';
            }
            $sLog .= "#$i {$t['file']}({$t['line']}): ";
            if (isset($t['object']) && is_object($t['object'])) {
                $sLog .= get_class($t['object']) . '->';
            }
            $sLog .= "{$t['function']}()\n";
        }
        if (isset($_SERVER['REQUEST_URI'])) {
            $sLog .= 'REQUEST_URI=' . $_SERVER['REQUEST_URI'];
        }
        $this->getLog()->log($sLog, TMLog::TYPE_ERROR);

        if ($iErrorCode & error_reporting()) {
            restore_error_handler();
            restore_exception_handler();
            try {
                \TM::import('system.base.TMErrorEvent', true);
                $oErrorEvent = new \TMErrorEvent($iErrorCode, $message, $file, $line);
                if (($oErrorHandler = $this->getErrorHandler()) != null) {
                    $oErrorHandler->handle($oErrorEvent);
                } else {
                    $this->displayError($iErrorCode, $message, $file, $line);
                }
            } catch (Exception $e) {
                $this->displayException($e);
            }
        }
    }

    /**
     * @brief displayError
     * 通用显示错误
     *
     * @param {mixed} $mErrorCode
     * @param {string} $sErrorMessage
     * @param {string} $sFileName
     * @param {interval} $iErrorLine
     */
    public function displayError($mErrorCode, $sErrorMessage, $sFileName, $iErrorLine)
    {
        if (TAOMEE_DEBUG) {
            if (\TM::app()->getHttp()->isAjaxRequest()) {
                echo json_encode(array('result' => 1, 'err_desc' => $sErrorMessage));
            } else {
                echo '<h1>PHP Error [' . $mErrorCode . ']</h1>';
                echo "<p>$sErrorMessage($sFileName:$iErrorLine)</p>";
                $aErrorTrace = debug_backtrace();
                echo '<pre>';
                foreach ($aErrorTrace as $i => $trace) {
                    if (!isset($trace['file'])) {
                        $trace['file'] = 'unknown';
                    }
                    if (!isset($trace['line'])) {
                        $trace['line'] = 'unknown';
                    }
                    echo "#$i {$trace['file']}({$trace['line']})";
                    if (isset($trace['object']) && is_object($trace['object'])) {
                        echo get_class($trace['object']) . '->';
                    }
                    echo "{$trace['function']}()\n";
                }
                echo '</pre>';
            }
            exit(0);
        }
    }

    /**
     * @brief getErrorHandler
     * 获取错误处理模块
     *
     * @return {object}
     */
    public function getErrorHandler()
    {
        return $this->getComponent('errorHandler');
    }

    /**
     * @brief getLog
     * 日志
     *
     * @return {object}
     */
    public function getLog()
    {
        return $this->getComponent('log');
    }

    /**
     * @brief getDb
     * 获取数据库操作模块
     *
     * @return {object}
     */
    public function getDb()
    {
        return $this->getComponent('db');
    }

    /**
     * 取缓存模块
     * @return TMComponent
     */
    public function getCache()
    {
        return $this->getComponent('cache');
    }

    /**
     * @brief registerCoreComponent
     * 注册核心模块
     *
     * @access protected
     */
    protected function registerCoreComponent()
    {
        $aCoreComponents = array(
            'errorHandler' => array(
                'class' => 'system.base.TMErrorHandler'
            ),
            'log' => array(
                'class' => 'system.log.TMLog'
            ),
            'db'  => array(
                'class' => 'system.db.TMDbConnection'
            ),
            'translator' => array(
                'class'    => 'system.lang.TMTranslator',
                'langPath' => array(
                    \TM::getFrameworkPath() . 'lang',
                    $this->getBasePath() . DS . 'lang'
                )
            ),
            'configure' => array(
                'class'    => 'system.utils.TMConfigure'
            )
        );
        $this->setComponents($aCoreComponents);
    }

    /**
     * @brief getBasePath
     * 获取基础路径
     *
     * @return {string}
     */
    public function getBasePath()
    {
        return $this->_sBasePath;
    }

    /**
     * @brief setBasePath
     * 设置基础路径
     *
     * @param {string} $sPath
     * @throw TMException
     */
    public function setBasePath($sPath)
    {
        if (($this->_sBasePath = realpath($sPath)) === false || !is_dir($this->_sBasePath)) {
            throw new \TMException(\TM::t('taomee', '基本路径{basePath}不存在。', array('{basePath}' => $sPath)));
        }
    }

    /**
     * @brief setRuntimePath
     * 设置runtime路径
     *
     * @param {string} $sPath
     * @throw TMException
     */
    public function setRuntimePath($sPath)
    {
        if (($this->_sRuntimePath = realpath($sPath)) === false || !is_dir($this->_sRuntimePath) || !is_writable($this->_sRuntimePath)) {
            throw new \TMFatalException(\TM::t('taomee', 'Runtime路径{runtimePath}不存在或不可写。', array('{runtimePath}' => $sPath)));
        }
    }

    /**
     * @brief getRuntimePath
     * 获取runtime路径
     *
     * @return {string}
     */
    public function getRuntimePath()
    {
        if (null === $this->_sRuntimePath) {
            $this->setRuntimePath($this->getBasePath() . DS . 'runtime');
            return $this->_sRuntimePath;
        } else {
            return $this->_sRuntimePath;
        }
    }

    /**
     * 设置时区
     *
     * @param string $sTimeZone
     */
    public function setTimeZone($sTimeZone)
    {
        date_default_timezone_set($sTimeZone);
    }

    /**
     * 获取系统时区
     *
     * @return string
     */
    public function getTimeZone()
    {
        return date_default_timezone_get();
    }

    /**
     * Get the application name
     *
     * @return string
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * Get the application version
     *
     * @return string
     */
    public function getVersion()
    {
        return $this->version;
    }

    /**
     * Get the application locale
     *
     * @return string
     */
    public function getLocale()
    {
        return $this->locale;
    }

    /**
     * Get the application supported locales
     *
     * @return array
     */
    public function getSupportedLocales()
    {
        return $this->supportedLocales;
    }

    /**
     * Get the application extra params.
     *
     * @return array
     */
    public function getParams()
    {
        return $this->params;
    }

    /**                                                                                
     * Returns the unique identifier for the application.                              
     * @return string the unique identifier for the application.                       
     */                                                                                
    public function getId()
    {
        if ($this->_id !== null) {
            return $this->_id;
        } else {
            return $this->_id = sprintf('%x', crc32($this->getBasePath() . $this->name));
        }
    }

    public function getStartTime()
    {
        return $this->_startTime;
    }
}
