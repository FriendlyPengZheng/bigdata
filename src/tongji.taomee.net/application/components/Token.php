<?php
class Token extends TMComponent
{
    /**
     * @var string namespace.
     */
    public $namespace = null;

    /**
     * @var integer expiration.
     */
    public $expire = 300;

    /**
     * Init the component.
     */
    public function init()
    {
    }

    /**
     * Generate a token.
     * @return string
     */
    public function generate()
    {
        $namespace = md5($this->namespace);
        $value = md5(uniqid(mt_rand(), true));
        TM::app()->getCache()->set($namespace, $value, $this->expire);
        return $this->_build($namespace, $value);
    }

    /**
     * Check if the given token is valid.
     * @param string $token
     * @return boolean
     */
    public function valid($token)
    {
        if (strlen($token) === 64) {
            list($namespace, $value) = $this->_parse($token);
            if (TM::app()->getCache()->get($namespace) === $value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Build token.
     * @param string $namespace
     * @param string $value
     * @return string
     */
    private function _build($namespace, $value)
    {
        return $namespace . $value;
    }

    /**
     * Parse token.
     * @param string $token
     * @return string
     */
    private function _parse($token)
    {
        return array(substr($token, 0, 32), substr($token, 32, 32));
    }
}
