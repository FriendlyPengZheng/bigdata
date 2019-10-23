<?php
/**
 * Template Compiler
 * This file contains the basic classes and methodes for compiling templates with lexer/parser
 */
class TMTemplateCompiler extends TMTemplateCompilerBase
{
    /**
     * @var string Lexer class name
     */
    public $lexerClass;

    /**
     * @var string Parser class name
     */
    public $parserClass;

    /**
     * @var object Template object
     */
    public $template;

    /**
     * @var object Lexer object
     */
    public $lex;

    /**
     * @var object Parser object
     */
    public $parser;

    /**
     * Initialize compiler
     * @param string $lexerClass  class name
     * @param string $parserClass class name
     * @param TMTemplate $template  global instance
     */
    public function __construct($lexerClass, $parserClass, $template)
    {
        $this->template = $template;
        parent::__construct();
        $this->lexerClass = $lexerClass;
        $this->parserClass = $parserClass;
    }

    /**
     * Method to compile a template
     *
     * @param mixed $content template source
     * @return boolean true if compiling succeeded, false if it failed
     */
    protected function doCompile($content)
    {
        $this->lex = new $this->lexerClass($content, $this);
        $this->parser = new $this->parserClass($this->lex, $this);
        if ($this->inheritanceChild)
            // start state on child templates
            $this->lex->yypushstate(TMTemplateLexer::CHILDBODY);
        // get tokens from lexer and parse them
        while ($this->lex->yylex() && !$this->abortAndRecompile) {
            $this->parser->doParse($this->lex->token, $this->lex->value);
        }
        if ($this->abortAndRecompile) {
            // exit here on abort
            return false;
        }
        // finish parsing process
        $this->parser->doParse(0, 0);
        // check for unclosed tags
        if (count($this->tagStack) > 0) {
            // get stacked info
            list($openTag, $data) = array_pop($this->tagStack);
            $this->triggerTemplateError("Unclosed {$this->template->leftDelimiter}"
                . $openTag . "{$this->template->rightDelimiter} tag");
        }
        return $this->parser->retValue;
    }
}
