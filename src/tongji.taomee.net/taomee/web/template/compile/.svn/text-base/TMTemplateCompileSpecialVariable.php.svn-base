<?php
/**
 * Compile special Template Variable Class
 */
class TMTemplateCompileSpecialVariable extends TMTemplateCompileBase
{
    /**
     * Compiles code for the speical $template variables
     *
     * @param  array  $args     array with attributes from parser
     * @param  object $compiler compiler object
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $_index = preg_split("/\]\[/", substr($parameter, 1, strlen($parameter) - 2));
        $compiled_ref = ' ';
        $variable = trim($_index[0], "'");
        switch ($variable) {
            case 'lde':
                $_ldelim = $compiler->template->leftDelimiter;
                return "'$_ldelim'";
            case 'rde':
                $_rdelim = $compiler->template->rightDelimiter;
                return "'$_rdelim'";
            default:
                $compiler->triggerTemplateError('$template.' . trim($_index[0], "'") . ' is invalid');
                break;
        }
        if (isset($_index[1])) {
            array_shift($_index);
            foreach ($_index as $_ind) {
                $compiled_ref = $compiled_ref . "[$_ind]";
            }
        }
        return $compiled_ref;
    }
}
