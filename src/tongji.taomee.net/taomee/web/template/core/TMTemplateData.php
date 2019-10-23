<?php
/*
 * This file contains the basic classes and methods for template and variable creation
 */

/*
 * Base class with template and variable methods
 */
class TMTemplateData
{
    /**
     * @var array Template variables
     */
    public $tplVars = array();

    /**
     * @var TMInternalTemplate Parent template (if any)
     */
    public $parent = null;

    /**
     * @var TMTemplate TMTemplate object
     */
    public $template = null;

    /**
     * Create TMTemplate data object
     *
     * @param mixed      $parent   parent template
     * @param TMTemplate $template global template instance
     */
    public function __construct($parent = null, $template = null)
    {
        $this->template = $template;
        if (is_object($parent)) {
            $this->parent = $parent;
        } elseif (is_array($parent)) {
            foreach ($parent as $key => $val) {
                $this->tplVars[$key] = new TMTemplateVariable($val);
            }
        } elseif ($parent != null) {
            throw new TMTemplateException('Wrong type for template variables');
        }
    }

    /**
     * Assigns a TMTemplate variable
     *
     * @param  mixed          $tplVar  the template variable names
     * @param  mixed          $value   the value to assign
     * @param  boolean        $noCache if true any output of this variable will be not cached
     * @return TMTemplateData
     */
    public function assign($tplVar, $value = null, $noCache = false)
    {
        if (is_array($tplVar)) {
            foreach ($tplVar as $key => $val) {
                $key = (string)$key;
                if ($key === '') continue;
                $this->tplVars[$key] = new TMTemplateVariable($val, $noCache);
            }
        } else {
            $tplVar = (string)$tplVar;
            if ($tplVar !== '') {
                $this->tplVars[$tplVar] = new TMTemplateVariable($value, $noCache);
            }
        }
        return $this;
    }

    /**
     * Assigns a global TMTemplate variable
     *
     * @param  string         $name    the global variable name
     * @param  mixed          $value   the value to assign
     * @param  boolean        $noCache if true any output of this variable will be not cached
     * @return TMTemplateData
     */
    public function assignGlobal($name, $value = null, $noCache = false)
    {
        $name = (string)$name;
        if ($name === '') return $this;

        TMTemplate::$globalTplVars[$name] = new TMTemplateVariable($value, $noCache);
        $ptr = $this;
        while ($ptr instanceof TMInternalTemplate) {
            $ptr->tplVars[$name] = clone TMTemplate::$globalTplVars[$name];
            $ptr = $ptr->parent;
        }
        return $this;
    }

    /**
     * Assigns values to template variables by reference
     *
     * @param  string          $tplVar  the template variable name
     * @param  mixed           &$value  the referenced value to assign
     * @param  boolean         $noCache if true any output of this variable will be not cached
     * @return TMTemplateData
     */
    public function assignByRef($tplVar, &$value, $noCache = false)
    {
        $tplVar = (string)$tplVar;
        if ($tplVar === '') return $this;

        $this->tplVars[$tplVar] = new TMTemplateVariable(null, $noCache);
        $this->tplVars[$tplVar]->value = &$value;
        return $this;
    }

    /**
     * Returns a single or all template variables
     *
     * @param  string         $name          variable name or null
     * @param  TMTemplateData $ptr           optional pointer to data object
     * @param  boolean        $searchParents whether to include parent templates
     * @return string         variable value or array of variables
     */
    public function getTemplateVars($name = null, $ptr = null, $searchParents = true)
    {
        if (isset($name)) {
            $var = $this->getVariable($name, $ptr, $searchParents, false);
            return is_object($var) ? $var->value : null;
        }

        if ($ptr === null) $ptr = $this;
        $result = array();
        while ($ptr !== null) {
            foreach ($ptr->tplVars as $key => $var) {
                if (!array_key_exists($key, $result)) {
                    $result[$key] = $var->value;
                }
            }
            // 在父级中查找
            $ptr = $searchParents ? $ptr->parent : null;
        }
        // 在全局变量中查找
        if ($searchParents && isset(TMTemplate::$globalTplVars)) {
            foreach (TMTemplate::$globalTplVars as $key => $var) {
                if (!array_key_exists($key, $result)) {
                    $result[$key] = $var->value;
                }
            }
        }
        return $result;
    }

    /**
     * Clear the given assigned template variable.
     *
     * @param  mixed          $tplVar the template variables to clear
     * @return TMTemplateData
     */
    public function clearAssign($tplVar)
    {
        if (is_array($tplVar)) {
            foreach ($tplVar as $currVar) {
                unset($this->tplVars[$currVar]);
            }
            return $this;
        }

        unset($this->tplVars[$tplVar]);
        return $this;
    }

    /**
     * Clear all the assigned template variables.
     *
     * @return TMTemplateData
     */
    public function clearAllAssign()
    {
        $this->tplVars = array();
        return $this;
    }

    /**
     * Gets the object of a Template variable
     * @param  string  $variable      the name of the Template variable
     * @param  object  $ptr           optional pointer to data object
     * @param  boolean $searchParents search also in parent data
     * @param  boolean $errorEnable   whether enable error
     * @return object  the object of the variable
     */
    public function getVariable($variable, $ptr = null, $searchParents = true, $errorEnable = true)
    {
        if ($ptr === null) $ptr = $this;
        while ($ptr !== null) {
            if (isset($ptr->tplVars[$variable])) return $ptr->tplVars[$variable];
            $ptr = $searchParents ? $ptr->parent : null;
        }
        if (isset(TMTemplate::$globalTplVars[$variable])) {
            return TMTemplate::$globalTplVars[$variable];
        }
        if ($this->template->errorUnassigned && $errorEnable) {
            // force a notice
            $x = $$variable;
        }

        return new TMUndefinedTemplateVariable();
    }
}

/*
 * Class for the Template variable object
 * This class defines the Template variable object
 */
class TMTemplateVariable
{
    /**
     * @var mixed Template variable.
     */
    public $value = null;

    /**
     * @var boolean Whether the output of this variable  will be cached.
     */
    public $noCache = false;

    /**
     * @var int the scope the variable will have.
     */
    public $scope = TMTemplate::SCOPE_LOCAL;

    /**
     * Create Template variable object
     *
     * @param mixed   $value   the value to assign
     * @param boolean $noCache if true any output of this variable will be not cached
     * @param int     $scope   the scope the variable will have
     */
    public function __construct($value = null, $noCache = false, $scope = TMTemplate::SCOPE_LOCAL)
    {
        $this->value = $value;
        $this->noCache = $noCache;
        $this->scope = $scope;
    }

    /**
     * @return string String conversion.
     */
    public function __toString()
    {
        return (string)$this->value;
    }
}

/*
 * class for undefined variable object
 * This class defines an object for undefined variable handling
 */
class TMUndefinedTemplateVariable
{
    /**
     * Returns FALSE for 'noCache' and NULL otherwise.
     *
     * @param  string  $name
     * @return boolean
     */
    public function __get($name)
    {
        return $name === 'noCache' ? false : null;
    }

    /**
     * Always returns an empty string.
     *
     * @return string
     */
    public function __toString()
    {
        return '';
    }
}
