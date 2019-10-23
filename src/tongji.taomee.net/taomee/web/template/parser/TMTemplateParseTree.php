<?php
/**
 * Template parser parsetrees
 * These are classes to build parsetrees in the template parser
 */
abstract class TMTemplateParseTree
{
    /**
     * @var object Parser object
     */
    public $parser;

    /**
     * @var mixed Buffer content
     */
    public $data;

    /**
     * @return string buffer content
     */
    abstract public function toPHP();
}

/**
 * A complete template tag.
 */
class TMTemplateTag extends TMTemplateParseTree
{
    /**
     * @var int Saved block nesting level
     */
    public $savedBlockNesting;

    /**
     * Create parse tree buffer for Template tag
     * @param object $parser parser object
     * @param string $data   content
     */
    public function __construct($parser, $data)
    {
        $this->parser = $parser;
        $this->data = $data;
        $this->savedBlockNesting = $parser->blockNestingLevel;
    }

    /**
     * Return buffer content
     * @return string content
     */
    public function toPHP()
    {
        return $this->data;
    }

    /**
     * Return complied code that loads the evaluated outout of buffer content
     * into a temporary variable
     * @return string template code
     */
    public function assignToVar()
    {
        $var = sprintf('$_tmp%d', ++TMTemplateParser::$prefixNumber);
        $this->parser->compiler->prefixCode[] = sprintf(
            '<?php ob_start();?>%s<?php %s=ob_get_clean(); ?>', $this->data, $var);
        return $var;
    }
}

/**
 * Code fragment inside a tag.
 */
class TMTemplateCode extends TMTemplateParseTree
{
    /**
     * Create parse tree buffer for code fragment
     * @param object $parser parser object
     * @param string $data   content
     */
    public function __construct($parser, $data)
    {
        $this->parser = $parser;
        $this->data = $data;
    }

    /**
     * Return buffer content in parentheses
     * @return string content
     */
    public function toPHP()
    {
        return sprintf("(%s)", $this->data);
    }
}

/**
 * Double quoted string inside a tag.
 */
class TMTemplateDoubleQuoted extends TMTemplateParseTree
{
    /**
     * @var array array of template elements
     */
    public $subtrees = array();

    /**
     * Create parse tree buffer for double quoted string subtrees
     * @param object              $parser  parser object
     * @param TMTemplateParseTree $subtree parsetree buffer
     */
    public function __construct($parser, TMTemplateParseTree $subtree)
    {
        $this->parser = $parser;
        $this->subtrees[] = $subtree;
        if ($subtree instanceof TMTemplateTag) {
            $this->parser->blockNestingLevel = count($this->parser->compiler->tagStack);
        }
    }

    /**
     * Append buffer to subtree
     * @param TMTemplateParseTree $subtree parsetree buffer
     */
    public function appendSubTree(TMTemplateParseTree $subtree)
    {
        $lastSubTree = count($this->subtrees) - 1;
        if ($lastSubTree >= 0 && $this->subtrees[$lastSubTree] instanceof TMTemplateTag
            && $this->subtrees[$lastSubTree]->savedBlockNesting < $this->parser->blockNestingLevel) {
            if ($subtree instanceof TMTemplateCode)
                $this->subtrees[$lastSubTree]->data .= '<?php echo ' . $subtree->data . ';?>';
            elseif ($subtree instanceof TMTemplateDoubleQuotedContent)
                $this->subtrees[$lastSubTree]->data .= '<?php echo "' . $subtree->data . '";?>';
            else
                $this->subtrees[$lastSubTree]->data .= $subtree->data;
        } else
            $this->subtrees[] = $subtree;
        if ($subtree instanceof TMTemplateTag) {
            $this->parser->blockNestingLevel = count($this->parser->compiler->tagStack);
        }
    }

    /**
     * Merge subtree buffer content together
     * @return string compiled template code
     */
    public function toPHP()
    {
        $code = '';
        foreach ($this->subtrees as $subtree) {
            if ($code !== "") {
                $code .= ".";
            }
            if ($subtree instanceof TMTemplateTag) {
                $morePHP = $subtree->assignToVar();
            } else {
                $morePHP = $subtree->toPHP();
            }
            $code .= $morePHP;
            if (!$subtree instanceof TMTemplateDoubleQuotedContent) {
                $this->parser->compiler->hasVariableString = true;
            }
        }
        return $code;
    }
}

/**
 * Raw chars as part of a double quoted string.
 */
class TMTemplateDoubleQuotedContent extends TMTemplateParseTree
{
    /**
     * Create parse tree buffer with string content
     * @param object $parser parser object
     * @param string $data   string section
     */
    public function __construct($parser, $data)
    {
        $this->parser = $parser;
        $this->data = $data;
    }

    /**
     * Return content as double quoted string
     * @return string doubled quoted string
     */
    public function toPHP()
    {
        return '"' . $this->data . '"';
    }
}

/**
 * Template element
 */
class TMTemplateBuffer extends TMTemplateParseTree
{
    /**
     * @var array Array of template elements
     */
    public $subtrees = array();

    /**
     * Create root of parse tree for template elements
     * @param object $parser parse object
     */
    public function __construct($parser)
    {
        $this->parser = $parser;
    }

    /**
     * Append buffer to subtree
     * @param TMTemplateParseTree $subtree
     */
    public function appendSubTree(TMTemplateParseTree $subtree)
    {
        $this->subtrees[] = $subtree;
    }

    /**
     * Sanitize and merge subtree buffers together
     * @return string template code content
     */
    public function toPHP()
    {
        $code = '';
        for ($key = 0, $cnt = count($this->subtrees); $key < $cnt; $key++) {
            if ($key + 2 < $cnt) {
                if ($this->subtrees[$key] instanceof TMTemplateLineBreak && $this->subtrees[$key + 1] instanceof TMTemplateTag
                    && $this->subtrees[$key + 1]->data == '' && $this->subtrees[$key + 2] instanceof TMTemplateLineBreak) {
                    $key = $key + 1;
                    continue;
                }
                if (substr($this->subtrees[$key]->data, -1) == '<' && $this->subtrees[$key + 1]->data == ''
                    && substr($this->subtrees[$key + 2]->data, -1) == '?') {
                    $key = $key + 2;
                    continue;
                }
            }
            if (substr($code, -1) == '<') {
                $subtree = $this->subtrees[$key]->toPHP();
                if (substr($subtree, 0, 1) == '?')
                    $code = substr($code, 0, strlen($code) - 1) . '<<?php ?>?' . substr($subtree, 1);
                elseif ($this->parser->aspTags && substr($subtree, 0, 1) == '%')
                    $code = substr($code, 0, strlen($code) - 1) . '<<?php ?>%' . substr($subtree, 1);
                else
                    $code .= $subtree;
                continue;
            }
            if ($this->parser->aspTags && substr($code, -1) == '%') {
                $subtree = $this->subtrees[$key]->toPHP();
                if (substr($subtree, 0, 1) == '>')
                    $code = substr($code, 0, strlen($code) - 1) . '%<?php ?>>' . substr($subtree, 1);
                else
                    $code .= $subtree;
                continue;
            }
            if (substr($code, -1) == '?') {
                $subtree = $this->subtrees[$key]->toPHP();
                if (substr($subtree, 0, 1) == '>')
                    $code = substr($code, 0, strlen($code) - 1) . '?<?php ?>>' . substr($subtree, 1);
                else
                    $code .= $subtree;
                continue;
            }
            $code .= $this->subtrees[$key]->toPHP();
        }
        return $code;
    }
}

/**
 * Template text
 */
class TMTemplateText extends TMTemplateParseTree
{
    /**
     * Create template text buffer
     * @param object $parser parser object
     * @param string $data   text
     */
    public function __construct($parser, $data)
    {
        $this->parser = $parser;
        $this->data = $data;
    }

    /**
     * Return buffer content
     * @return strint text
     */
    public function toPHP()
    {
        return $this->data;
    }
}

/**
 * Template linebreaks
 */
class TMTemplateLineBreak extends TMTemplateParseTree
{
    /**
     * Create buffer with linebreak content
     * @param object $parser parser object
     * @param string $data   linebreak string
     */
    public function __construct($parser, $data)
    {
        $this->parser = $parser;
        $this->data = $data;
    }

    /**
     * Return linebrak
     * @return string linebreak
     */
    public function toPHP()
    {
        return $this->data;
    }
}
