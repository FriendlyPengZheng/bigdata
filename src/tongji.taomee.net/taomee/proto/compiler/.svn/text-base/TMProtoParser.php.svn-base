<?php
class TMProtoParser
{
    /**
     * @var array Hold a mapping of entity => info
     */
    protected $tokens = array();

    /**
     * @var array Define tokenizer regular expressions
     */
    protected $regexps = array(
        'comment' => '/\*([\S\s]+?)\*/',
        'package' => '(?:package)\s+([A-Z_]+(\.[A-Z0-9_]+)*)',
        'message' => '(?:message)\s+([A-Z0-9_]+)',
        'field'   => '(?:required|repeated)\s+[^=]+=\s*([0-9]+)[^;]*;',
        'close'   => '}'
    );

    /**
     * @var string The regular expresion for the tokenizer
     */
    protected $regexp;

    /**
     * Generate a regular expression for all tokens
     */
    public function __construct()
    {
        $regexp = array();
        foreach ($this->regexps as $token => $exp) {
            $regexp[] = '(?<' . $token . '>' . $exp . ')';
        }
        $this->regexp = '@' . implode('|', $regexp) . '@i';
    }

    /**
     * Parse a Proto file source code to fetch messages
     *
     * @param  string $src
     * @return array
     */
    public function parse($src)
    {
        return $this->parseTokens($this->tokenize($src));
    }

    /**
     * Parse a Proto file source code to fetch tokens
     *
     * @param  string $src
     * @return array
     */
    public function tokenize($src)
    {
        // Build an stream of tokens from the regular expression
        $tokens = array();
        $offset = 0;
        while (preg_match($this->regexp, $src, $m, PREG_OFFSET_CAPTURE, $offset)) {
            foreach ($this->regexps as $k => $v) {
                if (!empty($m[$k]) && 0 < strlen($m[$k][0])) {
                    $tokens[] = array(
                        'token' => $k,
                        'value' => $m[$k][0],
                    );
                }
            }
            $offset = $m[0][1] + strlen($m[0][0]);
        }

        return $tokens;
    }

    /**
     * Parse tokens to fetch messages.
     *
     * @param  array $tokens
     * @return array
     */
    public function parseTokens($tokens)
    {
        $messages = $message = array();
        foreach ($tokens as $t) {
            if ($t['token'] === 'comment') {
                continue;
            }

            if ($t['token'] === 'package') {
                preg_match('@(?:package)\s+([A-Z_]+(\.[A-Z0-9_]+)*)@i', $t['value'], $m);
                $message['package'] = $m[1];
            }

            if ($t['token'] === 'message') {
                preg_match('@(?:message)\s+([A-Z0-9_]+)@i', $t['value'], $m);
                $message['name'] = $m[1];
                $message['fields'] = array();
            } elseif ($t['token'] === 'field') {
                preg_match('@(required|repeated)\s+([^\s]+)\s+([^=\s]+)\s*=\s*([0-9]+)\s*(?:\[([^\]]+)\])?\s*;@i', $t['value'], $m);
                if (!empty($m)) {
                    $field = array('rule' => $m[1], 'type' => $m[2], 'name' => $m[3], 'number' => $m[4]);
                    if (isset($m[5])) {
                        preg_match_all('@([^\s,]+)\s*=\s*([^\s,]+)@i', $m[5], $m1);
                        if (!empty($m1)) {
                            foreach ($m1[1] as $k => $v) {
                                $field[$v] = $m1[2][$k];
                            }
                        }
                    }
                    $message['fields'][] = $field;
                }
            } elseif ($t['token'] === 'close') {
                $messages[] = $message;
                $message = array();
            }
        }

        return $messages;
    }
}
