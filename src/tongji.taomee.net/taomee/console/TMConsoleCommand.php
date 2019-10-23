<?php
/**
 * TMConsoleCommand represents an executable console command.
 *
 * It works like {@link TMController} by parsing command line options and dispatching
 * the request to a specific action with appropriate option values.
 *
 * Users call a console command via the following command format:
 * <pre>
 * php path/to/entry_script.php CommandName ActionName --Option1=Value1 --Option2=Value2 ...
 * </pre>
 *
 * Child classes mainly needs to implement various action methods.
 * The parameters to an action method are considered as options
 * for that specific action. The action specified as {@link defaultAction} will be invoked
 * when a user does not specify the action name in his command.
 *
 * Options are bound to action parameters via parameter names. For example, the following
 * action method will allow us to run a command with
 * <code>php path/to/entry_script.php sitemap --type=News</code>:
 * <pre>
 * class SitemapCommand extends TMConsoleCommand {
 *     public function index($type) {
 *         ....
 *     }
 * }
 * </pre>
 * @property string $name The command name.
 * @property TMConsoleCommandRunner $commandRunner The command runner instance.
 * @property string $help The command description. Defaults to 'Usage: php entry-script.php command-name'.
 * @property array $optionHelp The command option help information. Each array element describes
 * the help information for a single action.
 */
abstract class TMConsoleCommand extends TMComponent
{
    /**
     * @var string the name of the default action. Defaults to 'index'.
     */
    public $defaultAction = 'index';

    /**
     * @var string The command name.
     */
    private $_name;

    /**
     * @var TMConsoleCommandRunner The command runner instance.
     */
    private $_runner;

    /**
     * Constructor.
     * @param string $name name of the command
     * @param TMConsoleCommandRunner $runner the command runner
     */
    public function __construct($name = null, $runner = null)
    {
        $this->_name = $name;
        $this->_runner = $runner;
    }

    /**
     * Initializes the command object.
     * This method is invoked after a command object is created and initialized with configurations.
     * You may override this method to further customize the command before it executes.
     */
    public function init()
    {
    }

    /**
     * The all actions of the command.
     * Each action must be a method of the command object.
     * @return array
     */
    protected function actions()
    {
        return array();
    }

    /**
     * Executes the command.
     * The default implementation will parse the input parameters and
     * dispatch the command request to an appropriate action with the corresponding
     * option values
     * @param array $args command line parameters for this command.
     * @return integer application exit code, which is returned by the invoked action.
     * 0 if the action did not return anything.
     */
    public function run($args)
    {
        list($action, $options, $args) = $this->resolveRequest($args);
        $actions = $this->actions();
        if (!preg_match('/^\w+$/', $action) || !isset($actions[$action]) || !method_exists($this, $action)) {
            $this->usageError("Unknown action: " . $action);
        }

        $method = new ReflectionMethod($this, $action);
        $params = array();
        // named and unnamed options
        foreach($method->getParameters() as $i => $param) {
            $name = $param->getName();
            if (isset($options[$name])) {
                if ($param->isArray()) {
                    $params[] = is_array($options[$name]) ? $options[$name] : array($options[$name]);
                } elseif (!is_array($options[$name])) {
                    $params[] = $options[$name];
                } else {
                    $this->usageError("Option --$name requires a scalar. Array is given.");
                }
            } elseif ($name === 'args') { // unnamed options passed here as array
                $params[] = $args;
            } elseif($param->isDefaultValueAvailable()) {
                $params[] = $param->getDefaultValue();
            } else {
                $this->usageError("Missing required option --$name.");
            }
            unset($options[$name]);
        }

        // try global options
        if (!empty($options)) {
            $class = new ReflectionClass(get_class($this));
            foreach ($options as $name => $value) {
                if ($class->hasProperty($name)) {
                    $property = $class->getProperty($name);
                    if ($property->isPublic() && !$property->isStatic()) {
                        $this->$name = $value;
                        unset($options[$name]);
                    }
                }
            }
        }

        if (!empty($options)) {
            $this->usageError("Unknown options: " . implode(', ', array_keys($options)));
        }

        return $method->invokeArgs($this, $params);
    }

    /**
     * Parses the command line arguments and determines which action to perform.
     * @param array $args command line arguments
     * @return array the action name, named options (name=>value), and unnamed options
     */
    protected function resolveRequest($args)
    {
        $options = array();   // named parameters
        $params  = array();    // unnamed parameters
        foreach ($args as $arg) {
            if (preg_match('/^--(\w+)(=(.*))?$/', $arg, $matches)) {  // an option
                $name = $matches[1];
                $value = isset($matches[3]) ? $matches[3] : true;
                if (isset($options[$name])) {
                    if (!is_array($options[$name])) {
                        $options[$name] = array($options[$name]);
                    }
                    $options[$name][] = $value;
                } else {
                    $options[$name] = $value;
                }
            } elseif (isset($action)) {
                $params[] = $arg;
            } else {
                $action = $arg;
            }
        }
        if (!isset($action)) {
            $action = $this->defaultAction;
        }

        return array($action, $options, $params);
    }

    /**
     * @return string the command name.
     */
    public function getName()
    {
        return $this->_name;
    }

    /**
     * Set the command name.
     * @return TMConsoleCommand
     */
    public function setName($name)
    {
        $this->_name = $name;
        return $this;
    }

    /**
     * @return TMConsoleCommandRunner the command runner instance
     */
    public function getCommandRunner()
    {
        return $this->_runner;
    }

    /**
     * Set the command runner instance.
     * @return TMConsoleCommand
     */
    public function setCommandRunner($runner)
    {
        $this->_runner = $runner;
        return $this;
    }

    /**
     * Provides the command description.
     * This method may be overridden to return the actual command description.
     * @return string the command description. Defaults to 'Usage: php entry-script.php command-name'.
     */
    public function getHelp()
    {
        $help = 'Usage: ' . $this->getCommandRunner()->getScriptName() . ' ' . $this->getName();
        $options = $this->getOptionHelp();
        if (empty($options)) {
            return $help . "\n";
        }
        if (count($options) === 1) {
            return $help . ' ' . $options[0] . "\n";
        }
        $help .= " <action>\nActions:\n";
        foreach ($options as $option) {
            $help .= '    ' . $option . "\n";
        }
        return $help;
    }

    /**
     * Provides the command option help information.
     * The default implementation will return all available actions together with their
     * corresponding option information.
     * @return array the command option help information. Each array element describes
     * the help information for a single action.
     */
    public function getOptionHelp()
    {
        $options = array();
        $class = new ReflectionClass(get_class($this));
        $actions = $this->actions();
        foreach ($class->getMethods(ReflectionMethod::IS_PUBLIC) as $method) {
            $name = $method->getName();
            if (isset($actions[$name])) {
                $help = $name;
                foreach ($method->getParameters() as $param) {
                    $optional = $param->isDefaultValueAvailable();
                    $defaultValue = $optional ? $param->getDefaultValue() : null;
                    if (is_array($defaultValue)) {
                        $defaultValue = str_replace(array("\r\n", "\n", "\r"), "", print_r($defaultValue, true));
                    }
                    $name = $param->getName();

                    if ($name === 'args') {
                        continue;
                    }

                    if ($optional) {
                        $help .= " [--$name=$defaultValue]";
                    } else {
                        $help .= " --$name=value";
                    }
                }
                $options[] = $help;
            }
        }
        return $options;
    }

    /**
     * Displays a usage error.
     * This method will then terminate the execution of the current application.
     * @param string $message the error message
     * @param integer $exitCode the exit code
     */
    public function usageError($message, $exitCode = 1)
    {
        echo "Error: $message\n\n" . $this->getHelp() . "\n";
        exit($exitCode);
    }

    /**
     * Log a message.
     * @param string $message
     * @param string $type
     * @see TMLog
     */
    public function log($message, $type = 'info')
    {
        TM::app()->getLog()->log('[' . get_class($this) . ']' . $message, $type);
    }
}
