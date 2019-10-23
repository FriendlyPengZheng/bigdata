<?php
/**
 * Main abstract compiler class
 */
abstract class TMTemplateCompilerBase
{
    /**
     * @var mixed hash for no cache sections
     */
    public $noCacheHash = null;

    /**
     * @var TMInternalTemplate current template
     */
    public $tpl = null;

    /**
     * @var array sources which must be compiled
     */
    public $sources = array();

    /**
     * @var array compile tag objects
     *
     */
    public static $tagObjects = array();

    /**
     * @var array tag stack
     */
    public $tagStack = array();

    /**
     * @var bool flag if compiled template file shall we written
     */
    public $writeCompiledCode = true;

    /**
     * @var bool suppress generation of merged template code
     */
    public $suppressMergedTemplates = false;

    /**
     * @var array merged templates
     */
    public $mergedTemplates = array();

    /**
     * @var bool suppress template property header code in compiled template
     */
    public $suppressTemplatePropertyHeader = false;

    /**
     * @var boolean force compilation of complete template as no cache
     */
    public $forceNoCache = false;

    /**
     * @var bool suppress generation of no cache code
     */
    public $suppressNoCacheProcessing = false;

    /**
     * @var boolean flag that we are inside {block}
     */
    public $inheritance = false;

    /**
     * @var boolean flag when compiling inheritance child template
     */
    public $inheritanceChild = false;

    /**
     * @var array uid of templates called by {extends} for recursion check
     */
    public $extendsUid = array();

    /**
     * Initialize compiler
     */
    public function __construct()
    {
        $this->noCacheHash = str_replace('.', '-', uniqid(rand(), true));
    }

    /**
     * Method to compile a template
     *
     * @param  TMInternalTemplate $tpl     template object to compile
     * @param  boolean            $noCache true is shall be compiled in no cache mode
     * @return boolean            true if compiling succeeded, false if it failed
     */
    public function compileTemplate(TMInternalTemplate $tpl, $noCache = false)
    {
        if (empty($tpl->properties['no_cache_hash']))
            $tpl->properties['no_cache_hash'] = $this->noCacheHash;
        else
            $this->noCacheHash = $tpl->properties['no_cache_hash'];

        // flag for no cache sections
        $this->noCache = $noCache;
        $this->tagNoCache = false;

        // save template object in compiler class
        $this->tpl = $tpl;
        // reset has no cache code flag
        $this->tpl->hasNoCacheCode = false;

        $saveSource = $this->tpl->source;
        $this->sources = array($tpl->source);
        $loop = 0;
        // the $this->sources array can get additional elements while compiling by the {extends} tag
        while ($this->tpl->source = array_shift($this->sources)) {
            $sourcesCount = count($this->sources);
            if ($loop || $sourcesCount)
                $this->tpl->properties['file_dependency'][$this->tpl->source->uid] = array(
                    $this->tpl->source->filepath,
                    $this->tpl->source->timestamp,
                    $this->tpl->source->type
                );
            $loop++;
            if ($sourcesCount)
                $this->inheritanceChild = true;
            else
                $this->inheritanceChild = false;
            do {
                $compiledCode = '';
                // flag for aborting current and start recompile
                $this->abortAndRecompile = false;
                // 获取模板内容，并编译
                $content = $this->tpl->source->content;
                if ($content != '')
                    $compiledCode = $this->doCompile($content);
            } while ($this->abortAndRecompile);
        }
        $this->tpl->source = $saveSource;
        unset($saveSource);

        // free memory
        unset($this->parser->rootBuffer, $this->parser->currentBuffer, $this->parser, $this->lex, $this->tpl);
        self::$tagObjects = array();

        // return compiled code to template object
        $mergedCode = '';
        if (!$this->suppressMergedTemplates && !empty($this->mergedTemplates)) {
            foreach ($this->mergedTemplates as $code) {
                $mergedCode .= $code;
            }
        }
        if ($this->suppressTemplatePropertyHeader) {
            $code = $compiledCode . $mergedCode;
        } else {
            $code = $tpl->createTemplateCodeFrame($compiledCode) . $mergedCode;
        }
        // unset content because template inheritance could have replace source with parent code
        unset ($tpl->source->content);

        return $code;
    }

    /**
     * Method to compile a template
     * @param mixed $content template source
     * @return boolean true if compiling succeeded, false if it failed
     */
    abstract protected function doCompile($content);

    /**
     * Compile Tag
     * This is a call back from the lexer/parser
     * It executes the required compiler for the Template tag
     * @param  string $tag      tag name
     * @param  array  $args      array with tag attributes
     * @param  array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compileTag($tag, $args, $parameter = array())
    {
        // $args contains the attributes parsed and compiled by the lexer/parser
        // assume that tag does compile into code, but creates no HTML output
        $this->hasCode = true;
        $this->hasOutput = false;

        // 检查nocache属性
        if (in_array("'nocache'", $args) || in_array(array('nocache' => 'true'), $args)
            || in_array(array('nocache' => '"true"'), $args) || in_array(array('nocache' => "'true'"), $args))
            $this->tagNoCache = true;

        // 调用相应的标签编译器
        if (($output = $this->callTagCompiler($tag, $args, $parameter)) === false) {
            $this->triggerTemplateError("Couldn't find compiler for tag '$tag'.");
        }
        if ($output !== false) {
            if ($output !== true) {
                if ($this->hasCode) {
                    if ($this->hasOutput) {
                        $output .= "\n";
                    }
                    return $output;
                }
            }
            return null;
        }
    }

    /**
     * Lazy loads compiler for tag and calls the compile method
     * @param string $tag tag name
     * @param array $args list of tag attributes
     * @param mixed $param1 optional parameter
     * @param mixed $param2 optional parameter
     * @param mixed $param3 optional parameter
     * @return string compiled code
     */
    public function callTagCompiler($tag, $args, $param1 = null, $param2 = null, $param3 = null)
    {
        // 若对象存在，直接调用compile方法
        if (isset(self::$tagObjects[$tag])) {
            return self::$tagObjects[$tag]->compile($args, $this, $param1, $param2, $param3);
        }
        // 延时加载编译器
        $className = 'TMTemplateCompile' . ucfirst($tag);
        if (class_exists($className)) {
            self::$tagObjects[$tag] = new $className;
            return self::$tagObjects[$tag]->compile($args, $this, $param1, $param2, $param3);
        }
        return false;
    }

    /**
     * Inject inline code for no cache template sections
     *
     * This method gets the content of each template element from the parser.
     * If the content is compiled code and it should be not cached the code is injected
     * into the rendered output.
     *
     * @param  string  $content content of template element
     * @param  boolean $isCode  true if content is compiled code
     * @return string  content
     */
    public function processNoCacheCode($content, $isCode)
    {
        if ($isCode && !empty($content)) {
            if ((!($this->tpl->source->recompiled) || $this->forceNoCache)
                && $this->tpl->caching && !$this->suppressNoCacheProcessing
                && ($this->noCache || $this->tagNoCache)) {
                $this->tpl->hasNoCacheCode = true;
                $output = addcslashes($content, '\'\\');
                $output = str_replace("^#^", "'", $output);
                $output = "<?php echo '/*%%TemplateNoCache:{$this->noCacheHash}%%*/"
                        . $output
                        . "/*/%%TemplateNoCache:{$this->noCacheHash}%%*/';?>\n";
            } else
                $output = $content;
        } else
            $output = $content;
        $this->suppressNoCacheProcessing = false;
        $this->tagNoCache = false;

        return $output;
    }

    /**
     * Display compiler error messages
     * @param  string $args individual error message or null
     * @param  string $line line number
     * @throws TMCompilerException when an unexpected token is found
     */
    public function triggerTemplateError($args = null, $line = null)
    {
        if (!isset($line)) {
            $line = $this->lex->line;
        }
        $match = preg_split("/\n/", $this->lex->data);
        $errorText = 'Syntax error in template "' . $this->tpl->source->filepath
            . '" on line ' . $line  . ' "'
            . trim(preg_replace('![\t\r\n]+!', ' ', $match[$line - 1])) . '" ';
        if (isset($args)) {
            $errorText .= $args;
        } else {
            // expected token from parser
            $errorText .= ' - Unexpected "' . $this->lex->value . '"';
            if (count($this->parser->yy_get_expected_tokens($this->parser->yymajor)) <= 4) {
                foreach ($this->parser->yy_get_expected_tokens($this->parser->yymajor) as $token) {
                    $expToken = $this->parser->yyTokenName[$token];
                    if (isset($this->lex->tokenNames[$expToken])) {
                        // token type from lexer
                        $expect[] = '"' . $this->lex->tokenNames[$expToken] . '"';
                    } else {
                        // otherwise internal token name
                        $expect[] = $this->parser->yyTokenName[$token];
                    }
                }
                $errorText .= ', expected one of: ' . implode(' , ', $expect);
            }
        }
        $e = new TMCompilerException($errorText);
        $e->line = $line;
        $e->source = trim(preg_replace('![\t\r\n]+!', ' ', $match[$line - 1]));
        $e->desc = $args;
        $e->template = $this->tpl->source->filepath;
        throw $e;
    }
}
