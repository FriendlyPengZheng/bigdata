<?php
/*
 * Internal Template
 * This file contains the template engine
 */

/*
 * Main class with template data structures and methods
 * @property TMTemplateSource   $source
 * @property TMTemplateCompiled $compiled
 * @property TMTemplateCached   $cached
 */
class TMInternalTemplate extends TMTemplateBase
{
    /**
     * @var string cache id
     */
    public $cacheId = null;

    /**
     * @var string compile id
     */
    public $compileId = null;

    /**
     * @var boolean caching enabled
     */
    public $caching = null;

    /**
     * @var integer cache lifetime in seconds
     */
    public $cacheLifetime = null;

    /**
     * @var string TMTemplate resource
     */
    public $templateResource = null;

    /**
     * @var array blocks for template inheritance
     */
    public $blockData = array();

    /**
     * internal flag to allow relative path in child template blocks
     * @var bool
     */
    public $allowRelativePath = false;

    /**
     * @var boolean flag if template does contain no cache code sections
     */
    public $hasNoCacheCode = false;

    /**
     * @var array special compiled and cached template properties
     */
    public $properties = array(
        'file_dependency' => array(),
        'no_cache_hash'   => ''
    );

    /**
     * @var bool flag if compiled template is invalid and must be (re)compiled
     */
    public $mustCompile = null;

    /**
     * Create template data object
     * Some of the global TMTemplate settings copied to template scope
     * It load the required template resources and cache plugins
     * @param string             $templateResource template resource string
     * @param TMTemplate         $template         TMTemplate instance
     * @param TMInternalTemplate $parent           back pointer to parent object with variables or null
     * @param mixed              $cacheId          cache id or null
     * @param mixed              $compileId        compile id or null
     * @param boolean            $caching          use caching
     * @param integer            $cacheLifetime    cache lifetime in seconds
     */
    public function __construct($templateResource, $template, $parent = null, $cacheId = null,
                                $compileId = null, $caching = null, $cacheLifetime = null)
    {
        $this->template = &$template;
        // 默认参数
        $this->cacheId = $cacheId === null ? $this->template->cacheId : $cacheId;
        $this->compileId = $compileId === null ? $this->template->compileId : $compileId;
        $this->caching = $caching === null ? $this->template->caching : $caching;
        if ($this->caching === true)
            $this->caching = TMTemplate::CACHING_LIFETIME_CURRENT;
        $this->cacheLifetime = $cacheLifetime === null ? $this->template->cacheLifetime : $cacheLifetime;
        $this->parent = $parent;
        // 模板源
        $this->templateResource = $templateResource;
        // copy block data of template inheritance
        if ($this->parent instanceof TMInternalTemplate) {
            $this->blockData = $this->parent->blockData;
        }
    }

     /**
      * Set Template property in template context
      *
      * @param string $propertyName property name
      * @param mixed  $value        value
      */
    public function __set($propertyName, $value)
    {
        switch ($propertyName) {
            case 'source':
            case 'compiled':
            case 'cached':
            case 'compiler':
                $this->$propertyName = $value;
                return;

            default:
                // 直接访问Template属性，代理
                if (property_exists($this->template, $propertyName)) {
                    $this->template->$propertyName = $value;
                    return;
                }
        }
        throw new TMTemplateException("Invalid template property $propertyName");
    }

    /**
     * Get Template property in template context
     *
     * @param string $propertyName property name
     */
    public function __get($propertyName)
    {
        switch ($propertyName) {
            case 'source':
                if (strlen($this->templateResource) == 0) {
                    throw new TMTemplateException('Missing template name.');
                }
                $this->source = TMTemplateResource::source($this);
                // 以唯一ID缓存模板对象，eval例外
                if ($this->source->type != 'eval') {
                    $templateId = $this->template->joinedTemplateDir . '#'
                        . $this->templateResource . $this->cacheId . $this->compileId;
                    if (isset($templateId[150]))
                        $templateId = sha1($templateId);
                    $this->template->templateObjects[$templateId] = $this;
                }
                return $this->source;

            case 'cached':
                $this->cached = new TMTemplateCached($this);
                return $this->cached;

            case 'compiled':
                $this->compiled = $this->source->getCompiled($this);
                return $this->compiled;

            case 'compiler':
                $this->compiler = new $this->source->compilerClass($this->source->templateLexerClass,
                    $this->source->templateParserClass, $this->template);
                return $this->compiler;

            default:
                // 代理Template属性
                if (property_exists($this->template, $propertyName))
                    return $this->template->$propertyName;
        }
        throw new TMTemplateException("Template property '$propertyName' does not exist.");
    }

    /**
     * Create code frame for compiled and cached templates
     *
     * @param  string $content optional template content
     * @param  bool   $cache   flag for cache file
     * @return string
     */
    public function createTemplateCodeFrame($content = '', $cache = false)
    {
        $this->properties['has_no_cache_code'] = $this->hasNoCacheCode;
        $output = '';
        if (!$this->source->recompiled)
            $output = "<?php /*%%TemplateHeaderCode:{$this->properties['no_cache_hash']}%%*/";
        if (!isset($this->properties['unifunc']))
            $this->properties['unifunc'] = 'content_' . str_replace('.', '_', uniqid('', true));
        if (!$this->source->recompiled) {
            $output .= " \$_valid = \$_tpl->decodeProperties(\n"
                    . var_export($this->properties, true)
                    . ",\n"
                    . ($cache ? 'true' : 'false')
                    . "); /*/%%TemplateHeaderCode%%*/?>\n"
                    . '<?php if ($_valid && !is_callable(\'' . $this->properties['unifunc']
                    . '\')) { function ' . $this->properties['unifunc'] . '($_tpl) {?>';
        }
        $output .= $content;
        if (!$this->source->recompiled)
            $output .= "<?php }} ?>\n";
        return $output;
    }

    /**
     * Compiles the template
     */
    public function compileTemplateSource()
    {
        if (!$this->source->recompiled) {
            $this->properties['file_dependency'] = array();
            $this->properties['file_dependency'][$this->source->uid] = array(
                $this->source->filepath,
                $this->source->timestamp,
                $this->source->type
            );
        }
        try {
            $code = $this->compiler->compileTemplate($this);
        } catch (Exception $e) {
            throw $e;
        }
        if (!$this->source->recompiled && $this->compiler->writeCompiledCode) {
            $filepath = $this->compiled->filepath;
            if ($filepath === false)
                throw new TMTemplateException('No destination to save the compiled template to.');
            // 写编译缓存
            TMTemplateUtility::writeFile($filepath, $code, $this->template);
            $this->compiled->exists = true;
            $this->compiled->isCompiled = true;
        }
        unset($this->compiler);
    }

    /**
     * This function is executed automatically when a compiled or cached template file is included
     *
     * - Decode saved properties from compiled template and cache files
     * - Check if compiled or cache file is valid
     *
     * @param  array $properties special template properties
     * @param  bool  $cache      flag if called from cache file
     * @return bool  flag if compiled or cache file is valid
     */
    public function decodeProperties($properties, $cache = false)
    {
        $this->hasNoCacheCode = $properties['has_no_cache_code'];
        $this->properties['no_cache_hash'] = $properties['no_cache_hash'];
        if (isset($properties['cache_lifetime']))
            $this->properties['cache_lifetime'] = $properties['cache_lifetime'];
        if (isset($properties['file_dependency']))
            $this->properties['file_dependency'] = array_merge(
                $this->properties['file_dependency'], $properties['file_dependency']);
        $this->properties['unifunc'] = $properties['unifunc'];

        $isValid = true;
        if (((!$cache && $this->template->compileCheck
                    && empty($this->compiled->properties) && !$this->compiled->isCompiled)
                || $cache && ($this->template->compileCheck === true
                    || $this->template->compileCheck === TMTemplate::COMPILECHECK_ON))
            && !empty($this->properties['file_dependency'])) {
            foreach ($this->properties['file_dependency'] as $fileToCheck) {
                if ($fileToCheck[2] == 'file' || $fileToCheck[2] == 'php') {
                    if ($this->source->filepath == $fileToCheck[0] && isset($this->source->timestamp)) {
                        // do not recheck current template
                        $mtime = $this->source->timestamp;
                    } else {
                        // file and php types can be checked without loading the respective resource handlers
                        $mtime = @filemtime($fileToCheck[0]);
                    }
                } elseif ($fileToCheck[2] == 'string') {
                    continue;
                } else {
                    $source = TMTemplateResource::source(null, $this->template, $fileToCheck[0]);
                    $mtime = $source->timestamp;
                }
                if (!$mtime || $mtime > $fileToCheck[1]) {
                    $isValid = false;
                    break;
                }
            }
        }
        if ($cache) {
            // CACHING_LIFETIME_SAVED cache expiry has to be validated here since otherwise we'd define the unifunc
            if ($this->caching === TMTemplate::CACHING_LIFETIME_SAVED
                && $this->properties['cache_lifetime'] >= 0
                && time() > ($this->cached->timestamp + $this->properties['cache_lifetime'])) {
                $isValid = false;
            }
            $this->cached->valid = $isValid;
        } else {
            $this->mustCompile = !$isValid;
            $this->compiled->properties = $properties;
        }
        return $isValid;
    }

    /**
     * Writes the cached template output
     *
     * @return bool
     */
    public function writeCachedContent($content)
    {
        // 不需要写缓存
        if ($this->source->recompiled
            || !($this->caching == TMTemplate::CACHING_LIFETIME_CURRENT
                    || $this->caching == TMTemplate::CACHING_LIFETIME_SAVED))
            return false;
        $this->properties['cache_lifetime'] = $this->cacheLifetime;
        $this->properties['unifunc'] = 'content_' . str_replace('.', '_', uniqid('', true));
        $content = $this->createTemplateCodeFrame($content, true);
        $_tpl = $this;
        eval("?>" . $content);
        $this->cached->valid = true;
        $this->cached->processed = true;
        return $this->cached->write($this, $content);
    }

    /**
     * Template code runtime function to get subtemplate content
     *
     * @param string  $template       the resource handle of the template file
     * @param mixed   $cacheId        cache id to be used with this template
     * @param mixed   $compileId      compile id to be used with this template
     * @param integer $caching        cache mode
     * @param integer $cacheLifetime  life time of cache data
     * @param array   $vars           optional  variables to assign
     * @param int     $parentScope    scope in which {include} should execute
     * @returns string template content
     */
    public function getSubTemplate($template, $cacheId, $compileId, $caching, $cacheLifetime, $data, $parentScope)
    {
        $templateId = $this->template->joinedTemplateDir . '#' . $template . $cacheId . $compileId;
        if (isset($templateId[150]))
            $templateId = sha1($templateId);
        if (isset($this->template->templateObjects[$templateId])) {
            $tpl = clone $this->template->templateObjects[$templateId];
            $tpl->parent = $this;
            $tpl->caching = $caching;
            $tpl->cacheLifetime = $cacheLifetime;
        } else
            $tpl = new TMInternalTemplate($template, $this->template, $this, $cacheId, $compileId, $caching, $cacheLifetime);
        if ($parentScope == TMTemplate::SCOPE_LOCAL) {
            $tpl->tplVars = $this->tplVars;
            $tpl->tplVars['template'] = clone $this->tplVars['template'];
        } elseif ($parentScope == TMTemplate::SCOPE_PARENT) {
            $tpl->tplVars = &$this->tplVars;
        } elseif ($parentScope == TMTemplate::SCOPE_GLOBAL) {
            $tpl->tplVars = &TMTemplate::$globalTplVars;
        } elseif (($scopePtr = $this->getScopePointer($parentScope)) == null) {
            $tpl->tplVars = &$this->tplVars;
        } else {
            $tpl->tplVars = &$scopePtr->tplVars;
        }
        if (!empty($data)) {
            foreach ($data as $key => $val)
                $tpl->tplVars[$key] = new TMTemplateVariable($val);
        }
        return $tpl->fetch(null, null, null, null, false, false, true);
    }

    /**
     * Template code runtime function to set up an inline subtemplate
     *
     * @param string  $template      the resource handle of the template file
     * @param mixed   $cacheId       cache id to be used with this template
     * @param mixed   $compileId     compile id to be used with this template
     * @param integer $caching       cache mode
     * @param integer $cacheLifetime life time of cache data
     * @param array   $vars          optional  variables to assign
     * @param int     $parentScope   scope in which {include} should execute
     * @param string  $hash          no cache hash code
     * @returns string template content
     */
    public function setupInlineSubTemplate($template, $cacheId, $compileId, $caching, $cacheLifetime, $data, $parentScope, $hash)
    {
        $tpl = new TMInternalTemplate($template, $this->template, $this, $cacheId, $compileId, $caching, $cacheLifetime);
        $tpl->properties['no_cache_hash'] = $hash;
        if ($parentScope == TMTemplate::SCOPE_LOCAL) {
            $tpl->tplVars = $this->tplVars;
            $tpl->tplVars['template'] = clone $this->tplVars['template'];
        } elseif ($parentScope == TMTemplate::SCOPE_PARENT) {
            $tpl->tplVars = &$this->tplVars;
        } elseif ($parentScope == TMTemplate::SCOPE_GLOBAL) {
            $tpl->tplVars = &TMTemplate::$globalTplVars;
        } elseif (($scopePtr = $this->getScopePointer($parentScope)) == null) {
            $tpl->tplVars = &$this->tplVars;
        } else {
            $tpl->tplVars = &$scopePtr->tplVars;
        }
        if (!empty($data)) {
            foreach ($data as $key => $val) {
                $tpl->tplVars[$key] = new TMTemplateVariable($val);
            }
        }
        return $tpl;
    }

    /**
     * Template code runtime function to create a local Template variable for array assignments
     *
     * @param string $tplVar  tempate variable name
     * @param bool   $noCache cache mode of variable
     * @param int    $scope   scope of variable
     */
    public function createLocalArrayVariable($tplVar, $noCache = false, $scope = TMTemplate::SCOPE_LOCAL)
    {
        if (!isset($this->tplVars[$tplVar]))
            $this->tplVars[$tplVar] = new TMTemplateVariable(array(), $noCache, $scope);
        else {
            $this->tplVars[$tplVar] = clone $this->tplVars[$tplVar];
            if ($scope != TMTemplate::SCOPE_LOCAL)
                $this->tplVars[$tplVar]->scope = $scope;
            if (!(is_array($this->tplVars[$tplVar]->value)
                    || $this->tplVars[$tplVar]->value instanceof ArrayAccess))
                settype($this->tplVars[$tplVar]->value, 'array');
        }
    }

    /**
     * Get parent or root of template parent chain
     *
     * @param  int   $scope parent or root scope
     * @return mixed object
     */
    public function getScopePointer($scope)
    {
        if ($scope == TMTemplate::SCOPE_PARENT && !empty($this->parent))
            return $this->parent;
        elseif ($scope == TMTemplate::SCOPE_ROOT && !empty($this->parent)) {
            $ptr = $this->parent;
            while (!empty($ptr->parent)) {
                $ptr = $ptr->parent;
            }
            return $ptr;
        }
        return null;
    }

    /**
     * Counts an array, arrayaccess/traversable or PDOStatement object
     *
     * @param  mixed $value
     * @return int
     * the count for arrays and objects that implement countable,
     * 1 for other objects that don't, and 0 for empty elements
     */
    public function _count($value)
    {
        if (is_array($value) === true || $value instanceof Countable)
            return count($value);
        elseif ($value instanceof IteratorAggregate)
            return iterator_count($value->getIterator());
        elseif ($value instanceof Iterator)
            return iterator_count($value);
        elseif ($value instanceof PDOStatement)
            return $value->rowCount();
        elseif ($value instanceof Traversable)
            return iterator_count($value);
        elseif ($value instanceof ArrayAccess) {
            if ($value->offsetExists(0))
                return 1;
        } elseif (is_object($value))
            return count($value);
        return 0;
    }
}
