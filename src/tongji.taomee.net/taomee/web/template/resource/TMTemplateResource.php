<?php
/**
 * Template Resource
 * Base implementation for resource
 */
abstract class TMTemplateResource
{
    /**
     * @var array cache for TMTemplateSource instances
     */
    public static $sources = array();

    /**
     * @var array cache for TMTemplateCompiled instances
     */
    public static $compileds = array();

    /**
     * @var array cache for TMTemplateResource instances
     */
    public static $resources = array();

    /**
     * @var array resource types provided by the core
     */
    protected static $coreResourceTypes = array(
        'file'    => true,
        'string'  => true,
        'extends' => true,
        'stream'  => true,
        'eval'    => true,
        'php'     => true
    );

    /**
     * @var string Name of the Class to compile this resource's contents with
     */
    public $compilerClass = 'TMTemplateCompiler';

    /**
     * @var string Name of the Class to tokenize this resource's contents with
     */
    public $templateLexerClass = 'TMTemplateLexer';

    /**
     * @var string Name of the Class to parse this resource's contents with
     */
    public $templateParserClass = 'TMTemplateParser';

    /**
     * Initialize Source Object for given resource
     * @param  TMInternalTemplate $tpl              template object
     * @param  TMTemplate         $template         template object
     * @param  string             $templateResource resource identifier
     * @return TMTemplateSource   Source Object
     */
    public static function source(TMInternalTemplate $tpl = null, TMTemplate $template = null, $templateResource = null)
    {
        if ($tpl) {
            $template = $tpl->template;
            $templateResource = $tpl->templateResource;
        }

        // parse resource name, load resource handler, identify unique resource name
        self::parseResourceName($templateResource, $template->defaultResourceType, $name, $type);
        $resource = self::load($template, $type);
        $uniqueResourceName = $resource->buildUniqueResourceName($template, $name);

        // check runtime cache
        $cacheKey = 'template|' . $uniqueResourceName;
        if ($template->compileId)
            $cacheKey .= '|' . $template->compileId;
        if (isset(self::$sources[$cacheKey]))
            return self::$sources[$cacheKey];

        // create source
        $source = new TMTemplateSource($resource, $template, $templateResource, $type, $name, $uniqueResourceName);
        $resource->populate($source, $tpl);

        // runtime cache
        self::$sources[$cacheKey] = $source;

        return $source;
    }

    /**
     * Extract resourceType and resourceName from templateResource
     *
     * @param  string $resourceName     templateResource to parse
     * @param  string $defaultResource  the default resourceType defined in TMTemplate
     * @param  string &$name            the parsed resource name
     * @param  string &$type            the parsed resource type
     * @return void
     */
    protected static function parseResourceName($resourceName, $defaultResource, &$name, &$type)
    {
        $parts = explode(':', $resourceName, 2);
        if (!isset($parts[1])) {
            $type = $defaultResource;
            $name = $resourceName;
        } else {
            $type = $parts[0];
            $name = $parts[1];
        }
    }

    /**
     * Load Resource Handler
     *
     * @param  TMTemplate $template template object
     * @param  string     $type     type of the resource
     * @return TMTemplateResource   Resource Handler
     */
    public static function load(TMTemplate $template, $type)
    {
        // try template's cache
        if (isset($template->resourceHandlers[$type]))
            return $template->resourceHandlers[$type];

        // try core resource types
        if (isset(self::$coreResourceTypes[$type])) {
            if (!isset(self::$resources[$type])) {
                $resourceClass = 'TMTemplateResource' . ucfirst($type);
                if (class_exists($resourceClass)) {
                    self::$resources[$type] = new $resourceClass();
                } else {
                    throw new TMTemplateException("Resource handler '{$resourceClass}' does not exist.");
                }
            }
            return $template->resourceHandlers[$type] = self::$resources[$type];
        }
        throw new TMTemplateException("Unkown resource type '{$type}'.");
    }

    /**
     * Modify resource name according to resource handlers specifications
     *
     * @param  TMTemplate $template     TMTemplate instance
     * @param  string     $resourceName resourceName to make unique
     * @return string     unique resource name
     */
    protected function buildUniqueResourceName(TMTemplate $template, $resourceName)
    {
        return get_class($this) . '#' . $template->joinedTemplateDir . '#' . $resourceName;
    }

    /**
     * Load template's source into current template object
     *
     * @param  TMTemplateSource $source source object
     * @return string                 template source
     * @throws TMTemplateException      if source cannot be loaded
     */
    abstract public function getContent(TMTemplateSource $source);

    /**
     * Populate Source Object with meta data from Resource
     *
     * @param TMTemplateSource   $source    source object
     * @param TMInternalTemplate $template  template object
     */
    abstract public function populate(TMTemplateSource $source, TMInternalTemplate $template = null);

    /**
     * Populate Source Object with timestamp and exists from Resource
     *
     * @param TMTemplateSource $source source object
     */
    public function populateTimestamp(TMTemplateSource $source)
    {
    }

    /**
     * Populate Compiled Object with compiled filepath
     *
     * @param TMTemplateCompiled $compiled compiled object
     * @param TMInternalTemplate $template template object
     */
    public function populateCompiledFilepath(TMTemplateCompiled $compiled, TMInternalTemplate $template)
    {
        $compileId = isset($template->compileId) ? preg_replace('![^\w\|]+!', '_', $template->compileId) : null;
        $filepath = $compiled->source->uid;
        if (isset($compileId)) {
            $filepath = $compileId . '^' . $filepath;
        }
        // caching token
        if ($template->caching) {
            $cache = '.cache';
        } else {
            $cache = '';
        }
        $compileDir = $template->template->getCompileDir();
        // set basename if not specified
        $basename = $this->getBasename($compiled->source);
        if ($basename === null) {
            $basename = basename(preg_replace('![^\w\/]+!', '_', $compiled->source->name));
        }
        // separate (optional) basename by dot
        if ($basename) {
            $basename = '.' . $basename;
        }

        $compiled->filepath = $compileDir . $filepath . '.' . $compiled->source->type . $basename . $cache . '.php';
    }

    /**
     * Normalize Paths "foo/../bar" to "bar"
     *
     * @param  string  $path path to normalize
     * @return string  normalized path
     */
    protected function normalizePath($path)
    {
        $offset = 0;
        // resolve simples
        $path = preg_replace('#/\./(\./)*#', '/', $path);
        // resolve parents
        while (true) {
            $parent = strpos($path, '/../', $offset);
            if (!$parent) {
                break;
            } elseif ($path[$parent - 1] === '.') {
                $offset = $parent + 3;
                continue;
            }
            $pos = strrpos($path, '/', $parent - strlen($path) - 1);
            if ($pos === false) {
                $pos = $parent;
            }
            $path = substr_replace($path, '', $pos, $parent + 3 - $pos);
        }
        return $path;
    }

    /**
     * Build template filepath by traversing the templateDir array
     *
     * @param  TMTemplateSource   $source   source object
     * @param  TMInternalTemplate $template template object
     * @return string                       fully qualified filepath
     * @throws TMTemplateException          if default template handler is registered but not callable
     */
    protected function buildFilepath(TMTemplateSource $source, TMInternalTemplate $template = null)
    {
        $file = $source->name;
        $directories = $source->template->getTemplateDir();

        // go relative to a given template
        $fileIsDotted = $file[0] == '.' && ($file[1] == '.' || $file[1] == '/');
        if ($template && $template->parent instanceof TMInternalTemplate && $fileIsDotted) {
            // 相对父级的路径
            if ($template->parent->source->type != 'file' && $template->parent->source->type != 'extends'
                    && !$template->parent->allowRelativePath) {
                throw new TMTemplateException("Template '{$file}' cannot be relative to template"
                                . " of resource type '{$template->parent->source->type}'");
            }
            $file = dirname($template->parent->source->filepath) . '/' . $file;
            $fileExactMatch = true;
            if (strpos($file, '/') !== 0) {
                $file = getcwd() . '/' . $file;
            }
        }

        // resolve relative path
        if (strpos($file, '/') !== 0) {
            $path = '/' . trim($file, '/');
            $wasRelative = true;
        } else {
            $path = $file;
        }
        $path = $this->normalizePath($path, false);

        // revert to relative
        if (isset($wasRelative)) {
            $path = substr($path, 1);
        }

        // this is only required for directories
        $file = rtrim($path, '/');

        // files relative to a template only get one shot
        if (isset($fileExactMatch)) {
            return $this->fileExists($source, $file) ? $file : false;
        }

        // 支持多个模板路径，如"[2]index.tpl"
        if (preg_match('#^\[(?P<key>[^\]]+)\](?P<file>.+)$#', $file, $match)) {
            $directory = null;
            // try string indexes
            if (isset($directories[$match['key']])) {
                $directory = $directories[$match['key']];
            } elseif (is_numeric($match['key'])) {
                // try numeric index
                $match['key'] = (int)$match['key'];
                if (isset($directories[$match['key']])) {
                    $directory = $directories[$match['key']];
                } else {
                    // try at location index
                    $keys = array_keys($directories);
                    $directory = $directories[$keys[$match['key']]];
                }
            }
            if ($directory) {
                $file = substr($file, strpos($file, ']') + 1);
                $filepath = $directory . $file;
                if ($this->fileExists($source, $filepath)) {
                    return $filepath;
                }
            }
        }

        // relative file name
        if (strpos($file, '/') !== 0) {
            foreach ($directories as $directory) {
                $filepath = $directory . $file;
                if ($this->fileExists($source, $filepath)) {
                    return $this->normalizePath($filepath);
                }
            }
        }

        // try absolute filepath
        if ($this->fileExists($source, $file)) {
            return $file;
        }

        // give up
        return false;
    }

    /**
     * Test is file exists and save timestamp
     *
     * @param  TMTemplateSource $source source object
     * @param  string           $file   file name
     * @return bool             true if file exists
     */
    protected function fileExists(TMTemplateSource $source, $file)
    {
        // 通过文件修改时间来确定文件是否存在
        $source->timestamp = is_file($file) ? @filemtime($file) : false;
        return $source->exists = !!$source->timestamp;
    }

    /**
     * Determine basename for compiled filename
     *
     * @param  TMTemplateSource $source source object
     * @return string           resource's basename
     */
    protected function getBasename(TMTemplateSource $source)
    {
        return null;
    }
}

/**
 * Template Resource Data Object
 * Meta Data Container for Template Files
 * @property integer $timestamp Source Timestamp
 * @property boolean $exists    Source Existence
 * @property boolean $template  Extended Template reference
 * @property string  $content   Source Content
 */
class TMTemplateSource
{
    /**
     * @var string Name of the Class to compile this resource's contents with
     */
    public $compilerClass = null;

    /**
     * @var string Name of the Class to tokenize this resource's contents with
     */
    public $templateLexerClass = null;

    /**
     * @var string Name of the Class to parse this resource's contents with
     */
    public $templateParserClass = null;

    /**
     * @var string Unique Template ID
     */
    public $uid = null;

    /**
     * @var string Template Resource (TMInternalTemplate::$templateResource)
     */
    public $resource = null;

    /**
     * @var string Resource Type
     */
    public $type = null;

    /**
     * @var string Resource Name
     */
    public $name = null;

    /**
     * @var string Unique Resource Name
     */
    public $uniqueResource = null;

    /**
     * @var string Source Filepath
     */
    public $filepath = null;

    /**
     * @var boolean Source is bypassing compiler
     */
    public $uncompiled = null;

    /**
     * @var boolean Source must be recompiled on every occasion
     */
    public $recompiled = null;

    /**
     * @var TMTemplateResource Resource Handler
     */
    public $handler = null;

    /**
     * @var TMTemplate TMTemplate instance
     */
    public $template = null;

    /**
     * create Source Object container
     *
     * @param TMTemplateResource $handler        Resource Handler this source object communicates with
     * @param TMTemplate         $template       TMTemplate instance this source object belongs to
     * @param string             $resource       full template resource
     * @param string             $type           type of resource
     * @param string             $name           resource name
     * @param string             $uniqueResource unqiue resource name
     */
    public function __construct(TMTemplateResource $handler, TMTemplate $template, $resource, $type, $name, $uniqueResource)
    {
        $this->handler = $handler;

        $this->compilerClass = $handler->compilerClass;
        $this->templateLexerClass = $handler->templateLexerClass;
        $this->templateParserClass = $handler->templateParserClass;
        $this->uncompiled = $this->handler instanceof TMTemplateResourceUncompiled;
        $this->recompiled = $this->handler instanceof TMTemplateResourceRecompiled;

        $this->template = $template;
        $this->resource = $resource;
        $this->type = $type;
        $this->name = $name;
        $this->uniqueResource = $uniqueResource;
    }

    /**
     * get a Compiled Object of this source
     *
     * @param  TMInternalTemplate $template template object
     * @return TMTemplateCompiled compiled object
     */
    public function getCompiled(TMInternalTemplate $template)
    {
        // check runtime cache
        $cacheKey = $this->uniqueResource . '#' . $template->compileId;
        if (isset(TMTemplateResource::$compileds[$cacheKey])) {
            return TMTemplateResource::$compileds[$cacheKey];
        }

        $compiled = new TMTemplateCompiled($this);
        $this->handler->populateCompiledFilepath($compiled, $template);
        $compiled->exists = file_exists($compiled->filepath);
        $compiled->exists && ($compiled->timestamp = filemtime($compiled->filepath));
        // runtime cache
        TMTemplateResource::$compileds[$cacheKey] = $compiled;

        return $compiled;
    }

    /**
     * render the uncompiled source
     *
     * @param TMInternalTemplate $template template object
     */
    public function renderUncompiled(TMInternalTemplate $template)
    {
        return $this->handler->renderUncompiled($this, $template);
    }

    /**
     * Generic Setter.
     *
     * @param  string              $propertyName  valid: timestamp, exists, content, template
     * @param  mixed               $value         new value (is not checked)
     * @throws TMTemplateException if $propertyName is not valid
     */
    public function __set($propertyName, $value)
    {
        switch ($propertyName) {
            // regular attributes
            case 'timestamp':
            case 'exists':
            case 'content':
            // required for extends: only
            case 'template':
                $this->$propertyName = $value;
                break;
            default:
                throw new TMTemplateException("Invalid source property '$propertyName'.");
        }
    }

    /**
     * Generic getter.
     *
     * @param  string $propertyName valid: timestamp, exists, content
     * @return mixed
     * @throws TMTemplateException if $propertyName is not valid
     */
    public function __get($propertyName)
    {
        switch ($propertyName) {
            case 'timestamp':
            case 'exists':
                $this->handler->populateTimestamp($this);
                return $this->$propertyName;

            case 'content':
                return $this->content = $this->handler->getContent($this);

            default:
                throw new TMTemplateException("Source property '$propertyName' does not exist.");
        }
    }
}

/**
 * Template Resource Data Object
 * Meta Data Container for Template Files
 * @property string $content compiled content
 */
class TMTemplateCompiled
{
    /**
     * @var string Compiled Filepath
     */
    public $filepath = null;

    /**
     * @var integer Compiled Timestamp
     */
    public $timestamp = null;

    /**
     * @var boolean Compiled Existence
     */
    public $exists = false;

    /**
     * @var boolean Compiled Content Loaded
     */
    public $loaded = false;

    /**
     * @var boolean Template was compiled
     */
    public $isCompiled = false;

    /**
     * @var TMTemplateSource Source Object
     */
    public $source = null;

    /**
     * @var array Metadata properties
     * populated by TMInternalTemplate::decodeProperties()
     */
    public $properties = null;

    /**
     * create Compiled Object container
     * @param TMTemplateSource $source source object this compiled object belongs to
     */
    public function __construct(TMTemplateSource $source)
    {
        $this->source = $source;
    }
}
