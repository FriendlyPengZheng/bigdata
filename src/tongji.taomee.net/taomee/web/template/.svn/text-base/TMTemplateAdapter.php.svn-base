<?php
/**
 * 模板类适配器，配合框架的模板组件使用
 */
class TMTemplateAdapter extends TMComponent
{
    /**
     * @var string the path which the classes relative to.
     */
    private static $_relativePath = 'system.web.template.';

    /**
     * @var array all classes this component needs.
     */
    private static $_dependentClasses = array(
        'required' => array(
            'core.TMTemplateData',
            'core.TMTemplateBase',
            'core.TMInternalTemplate',
            'core.TMTemplateUtility',
            'TMTemplate',
            'resource.TMTemplateResource',
            'resource.TMTemplateResourceUncompiled',
            'resource.TMTemplateResourceRecompiled',
            'parser.TMTemplateParseTree',
            'parser.TMTemplateParser',
            'parser.TMTemplateLexer',
            'compile.TMTemplateCompilerBase',
            'compile.TMTemplateCompiler',
            'cache.TMTemplateCached'
        ),
        'optional' => array(
            'resource.TMTemplateResourceString',
            'resource.TMTemplateResourceFile',
            'compile.TMTemplateCompileInclude',
            'compile.TMTemplateCompileFor',
            'compile.TMTemplateCompileForeach',
            'compile.TMTemplateCompileIf',
            'compile.TMTemplateCompileBlock',
            'compile.TMTemplateCompileBase',
            'compile.TMTemplateCompileExtends',
            'compile.TMTemplateCompileNocache',
            'compile.TMTemplateCompilePrintExpression',
            'compile.TMTemplateCompileSpecialVariable',
            'cache.TMTemplateCacheResourceFile'
        )
    );

    /**
     * @var TMTemplate the real template instance.
     */
    private $_template = null;

    /**
     * Constructor:
     * load classes necessary;
     * create a TMTemplate instance;
     */
    public function __construct()
    {
        foreach (self::$_dependentClasses as $key => $classes) {
            $autoload = ($key === 'required') ? true : false;
            foreach ($classes as $class) {
                TM::import(self::$_relativePath . $class, $autoload);
            }
        }
        if (!($this->_template instanceof TMTemplate)) {
            $this->_template = new TMTemplate();
        }
    }

    /**
     * Get properties of TMComponent instead.
     */
    public function __get($propertyName)
    {
        if (property_exists($this->_template, $propertyName)) {
            return $this->_template->$propertyName;
        } else {
            return $this->_template->__get($propertyName);
        }
    }

    /**
     * Set properties of TMComponent instead.
     */
    public function __set($propertyName, $value)
    {
        if (property_exists($this->_template, $propertyName)) {
            $this->_template->$propertyName = $value;
            return;
        } else {
            $this->_template->__set($propertyName, $value);
        }
    }

    /**
     * Call TMComponent's methods instead.
     */
    public function __call($method, $arguments)
    {
        $callback = array($this->_template, $method);
        if (is_callable($callback)) {
            return call_user_func_array($callback, $arguments);
        } else {
            // trigger an error
            return $this->$method();
        }
     }
}
