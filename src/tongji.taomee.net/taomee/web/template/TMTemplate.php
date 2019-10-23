<?php
/*
 * 模板类，外界入口，主要定义模板相关的一些配置
 */
class TMTemplate extends TMTemplateBase
{
    /**
     * define variable scopes
     */
    const SCOPE_LOCAL = 0;
    const SCOPE_PARENT = 1;
    const SCOPE_ROOT = 2;
    const SCOPE_GLOBAL = 3;

    /**
     * define caching modes
     */
    const CACHING_OFF = 0;
    const CACHING_LIFETIME_CURRENT = 1;
    const CACHING_LIFETIME_SAVED = 2;

    /**
     * define compile check modes
     */
    const COMPILECHECK_OFF = 0;
    const COMPILECHECK_ON = 1;

    /**
     * @var array template directory
     */
    private $_templateDir = array();

    /**
     * @var string compile directory
     */
    private $_compileDir = null;

    /**
     * @var string cache directory
     */
    private $_cacheDir = null;

    /**
     * The character set to adhere to.
     */
    public static $charset = 'UTF-8';

    /**
     * @var array assigned global tpl vars
     */
    public static $globalTplVars = array();

    /**
     * @var boolean caching enabled
     */
    public $caching = false;

    /**
     * @var integer cache lifetime in seconds
     */
    public $cacheLifetime = 3600;

    /**
     * resource type used if none given
     * @var string Must be an valid key
     */
    public $defaultResourceType = 'file';

    /**
     * @var array resource handler cache
     */
    public $resourceHandlers = array();

    /**
     * @var boolean whether to check template for modifications
     */
    public $compileCheck = true;

    /**
     * caching type
     * @var string Must be an element of $cacheResourceTypes.
     */
    public $cachingType = 'file';

    /**
     * @var array cache resource handler cache
     */
    public $cacheResourceHandlers = array();

    /**
     * @var array 缓存InternalTemplate对象，以templateID为键
     */
    public $templateObjects = array();

    /**
     * @var boolean whether to force template compiling
     */
    public $forceCompile = true;

    /**
     * @var boolean force cache file creation
     */
    public $forceCache = true;

    /**
     * @var boolean display error on not assigned variables
     */
    public $errorUnassigned = false;

    /**
     * @var string joined template directory string used in cache keys
     */
    public $joinedTemplateDir = null;

    /**
     * @var string Set this if you want different sets of cache files for the same templates.
     */
    public $cacheId = null;

    /**
     * @var string Set this if you want different sets of compiled files for the same templates.
     */
    public $compileId = null;

    /**
     * @var string template left delimiter
     */
    public $leftDelimiter = "{";

    /**
     * @var string template rightDelimiter
     */
    public $rightDelimiter = "}";

    /**
     * @var boolean check If-Modified-Since headers
     */
    public $cacheModifiedCheck = false;

    /**
     * @var boolean autoescape variable output
     */
    public $escapeHtml = false;

    /**
     * @var int default file permissions
     */
    public $filePerms = 0644;

    /**
     * @var int default dir permissions
     */
    public $dirPerms = 0771;

    /**
     * @var array block tag hierarchy
     */
    public $tagStack = array();

    /**
     * @var array Saved parameter of merged templates during compilation
     */
    public $mergedTemplatesFunc = array();

    /**
     * @var boolean 是否将被包含文件合并到包含文件中
     */
    public $mergeCompiledIncludes = false;

    /**
     * @var boolean literal "{ $content }" automaticly.
     */
    public $autoLiteral = true;

    /**
     * Initialize new TMTemplate object
     */
    public function __construct()
    {
        $this->template = $this;
        if (!extension_loaded('mbstring')) throw new TMTemplateException('Extension "mbtring" must be loaded.');
        mb_internal_encoding(self::$charset);

        // default directories.
        $this->setTemplateDir('./templates/')->setCompileDir('./compiles/')->setCacheDir('./caches/');
    }

    /**
     * set selfpointer on cloned object
     */
    public function __clone()
    {
        $this->template = $this;
    }

    /**
     * Generic getter.
     * Calls the appropriate getter function.
     *
     * @param  string $name property name
     * @return mixed
     */
    public function __get($name)
    {
        $allowed = array('templateDir' => 'getTemplateDir', 'compileDir' => 'getCompileDir', 'cacheDir' => 'getCacheDir');
        if (isset($allowed[$name])) {
            return $this->{$allowed[$name]}();
        }
    }

    /**
     * Get template directories.
     *
     * @param  mixed index of directory to get, null to get all
     * @return mixed list of template directories, or directory of $index
     */
    public function getTemplateDir($index = null)
    {
        if ($index !== null) {
            return isset($this->_templateDir[$index]) ? $this->_templateDir[$index] : null;
        }
        return (array)$this->_templateDir;
    }

    /**
     * Get compiled directory
     *
     * @return string path to compiled templates
     */
    public function getCompileDir()
    {
        return $this->_compileDir;
    }

    /**
     * Get cache directory
     *
     * @return string path of cache directory
     */
    public function getCacheDir()
    {
        return $this->_cacheDir;
    }

    /**
     * Generic setter.
     * Calls the appropriate setter function.
     *
     * @param string $name  property name
     * @param mixed  $value parameter passed to setter
     */
    public function __set($name, $value)
    {
        $allowed = array('templateDir' => 'setTemplateDir', 'compileDir' => 'setCompileDir', 'cacheDir' => 'setCacheDir');
        if (isset($allowed[$name])) {
            $this->{$allowed[$name]}($value);
        }
    }

    /**
     * Set template directory.
     *
     * @param  mixed      $templateDir directorys of template sources
     * @return TMTemplate
     */
    public function setTemplateDir($templateDir)
    {
        $this->_templateDir = array();
        foreach ((array)$templateDir as $k => $v) {
            $this->_templateDir[$k] = rtrim($v, '/') . '/';
        }
        $this->joinedTemplateDir = join('/', $this->_templateDir);

        return $this;
    }

    /**
     * Set compile directory.
     *
     * @param  string     $compileDir directory to store compiled templates in
     * @return TMTemplate current TMTemplate instance for chaining
     */
    public function setCompileDir($compileDir)
    {
        $this->_compileDir = rtrim($compileDir, '/') . '/';

        return $this;
    }

    /**
     * Set cache directory
     *
     * @param  string     $cacheDir directory to store cached templates in
     * @return TMTemplate current TMTemplate instance for chaining
     */
    public function setCacheDir($cacheDir)
    {
        $this->_cacheDir = rtrim($cacheDir, '/') . '/';

        return $this;
    }

    /**
     * Creates a template object.
     *
     * @param  string  $template  the resource handle of the template file
     * @param  mixed   $cacheId   cache id to be used with this template
     * @param  mixed   $compileId compile id to be used with this template
     * @param  object  $parent    next higher level of TMTemplate variables
     * @return object  template object
     */
    public function createTemplate($template, $cacheId = null, $compileId = null, $parent = null)
    {
        // 若缓存不为空，则以缓存为数据源
        if (!empty($cacheId) && (is_object($cacheId) || is_array($cacheId))) {
            $parent = $cacheId;
            $cacheId = null;
        }
        if (!empty($parent) && is_array($parent)) {
            $data = $parent;
            $parent = null;
        } else {
            $data = null;
        }
        // 默认缓存ID和编译ID
        $cacheId = $cacheId === null ? $this->cacheId : $cacheId;
        $compileId = $compileId === null ? $this->compileId : $compileId;
        // 模板ID
        $templateId = $this->joinedTemplateDir . '#' . $template . $cacheId . $compileId;
        // 长度超过150，sha1
        if (isset($templateId[150])) {
            $templateId = sha1($templateId);
        }
        // 是否缓存
        if (isset($this->templateObjects[$templateId])) {
            $tpl = $this->templateObjects[$templateId];
            $tpl->parent = $parent;
            $tpl->tplVars = array();
        } else {
            $tpl = new TMInternalTemplate($template, $this, $parent, $cacheId, $compileId);
        }
        // 复制父级值到模板对象中
        if (!empty($data) && is_array($data)) {
            foreach ($data as $key => $val) {
                $tpl->tplVars[$key] = new TMTemplateVariable($val);
            }
        }
        return $tpl;
    }
}

/*
 * TMTemplate exception class
 */
class TMTemplateException extends TMException
{
    /**
     * @return string String conversion.
     */
    public function __toString()
    {
        return ' --> Template: ' . $this->message . ' <-- ';
    }
}

/*
 * Compiler exception class
 */
class TMCompilerException extends TMException
{
    /**
     * @var mixed The line number of the template error
     */
    public $line = null;

    /**
     * @var mixed The template source snippet relating to the error
     */
    public $source = null;

    /**
     * @var mixed The raw text of the error message
     */
    public $desc = null;

    /**
     * @var mixed The resource identifier or template name
     */
    public $template = null;

    /**
     * @return string String conversion.
     */
    public function __toString()
    {
        return ' --> Compiler: ' . $this->message . ' <-- ';
    }
}
