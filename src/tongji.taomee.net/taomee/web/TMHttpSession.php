<?php
class TMHttpSession extends TMComponent
{
    /**
     * @var boolean whether start session when the component is initialized.
     */
    public $autoStart = true;

    /**
     * @var string specifies the name of the session which is used as cookie name.
     */
    public $name = null;

    /**
     * @var string defines the name of the handler which is used for storing and retrieving data associated with a session.
     */
    public $saveHandler = null;

    /**
     * @var string defines the argument which is passed to the save handler.
     */
    public $savePath = null;

    /**
     * @var string specifies the domain to set in the session cookie.
     */
    public $cookieDomain = null;

    /**
     * @var float the probability (percentage) that the gc (garbage collection) process is started on every session initialization.
     */
    public $gcProbability = null;

    /**
     * @var integer specifies the number of seconds after which data will be seen as 'garbage' and potentially cleaned up.
     */
    public $maxLifeTime = null;

    /**
     * @var {boolean} whether the session is started 
     */
    private $_started = false;

    /**
     * Init this component.
     */
    public function init()
    {
        parent::init();
        if ($this->autoStart) {
            $this->open();
        }
        register_shutdown_function(array($this, 'close'));
    }

    /**
     * Start session after set some configurations.
     */
    public function open()
    {
        if (!$this->_started) {
            // settings for security
            ini_set('session.auto_start', 0);
            ini_set('session.use_cookies', 1);
            ini_set('session.use_only_cookies', 1);
            ini_set('session.use_trans_sid', 0);

            if (extension_loaded('igbinary')) {
                ini_set('session.serialize_handler', 'igbinary');
            }

            if (isset($this->cookieDomain)) {
                ini_set('session.cookie_domain', $this->cookieDomain);
            }
            if (isset($this->gcProbability)) {
                $this->setGcProbability($this->gcProbability);
            }
            if (isset($this->maxLifeTime)) {
                ini_set('session.gc_maxlifetime', $this->maxLifeTime);
            }
            if (isset($this->name)) {
                session_name($this->name);
            }
            if (isset($this->saveHandler)) {
                ini_set('session.save_handler', $this->saveHandler);
            }
            if (isset($this->savePath)) {
                session_save_path($this->savePath);
            }
        }
        if (!session_id()) session_start();
        if (!$this->contains('__token')) $this->regenerateToken();
        $this->_started = true;
    }

    /**
     * @brief regenerate 重新生成session ID
     */
    public function regenerate()
    {
        session_regenerate_id(true);
    }

    /**
     * Ends the current session and store session data.
     */
    public function close()
    {
        if (session_id() !== '') {
            @session_write_close();
        }
    }

    /**
     * Frees all session variables and destroys all data registered to a session.
     */
    public function destroy()
    {
        if (session_id() !== '') {
            @session_unset();
            @session_destroy();
        }
    }

    /**
     * Set the probability (percentage) that the gc (garbage collection) process
     * is started on every session initialization.
     * @param float $value the percentage
     * @throws TMException if the value is beyond [0,100]
     */
    public function setGcProbability($value)
    {
        if ($value >= 0 && $value <= 100) {
            ini_set('session.gc_probability', floor($value * 21474836.47));
            ini_set('session.gc_divisor', 2147483647);
        } else {
            throw new TMException(TM::t('taomee', 'gcProbability "{value}" is invalid. It must be a float between 0 and 100.',
                        array('{value}' => $value)));
        }
    }

    /**
     * Get value from session by key.
     * @param mixed $key
     * @param mixed $defaultValue
     * @return mixed
     */
    public function get($key, $defaultValue = null)
    {
        return isset($_SESSION[$key]) ? $_SESSION[$key] : $defaultValue;
    }

    /**
     * Adds a session variable.
     * @param mixed $key session variable name
     * @param mixed $value session variable value
     *
     * @return $this
     */
    public function add($key, $value)
    {
        $_SESSION[$key] = $value;
        return $this;
    }

    /**
     * Removes a session variable.
     * @param mixed $key the name of the session variable to be removed
     * @return mixed the removed value, null if no such session variable
     */
    public function remove($key)
    {
        if (isset($_SESSION[$key])) {
            $value = $_SESSION[$key];
            unset($_SESSION[$key]);
            return $value;
        }
        return null;
    }

    /**
     * Removes all session variables.
     */
    public function clear()
    {
        foreach (array_keys($_SESSION) as $key) {
            unset($_SESSION[$key]);
        }
    }

    /**
     * Whether there is the named session variable.
     * @param mixed $key session variable name
     * @return boolean
     */
    public function contains($key)
    {
        return isset($_SESSION[$key]);
    }

    /**
     * 生成CSRF token
     *
     * @return {void}
     */
    public function regenerateToken()
    {
        $this->add('__token', TMStringHelper::random(40));
    }
}
