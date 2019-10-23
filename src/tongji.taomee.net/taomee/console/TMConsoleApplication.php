<?php
/**
 * TMConsoleApplication represents a console application.
 *
 * TMConsoleApplication extends {@link TMApplication} by providing functionalities
 * specific to console requests. In particular, it deals with console requests
 * through a command-based approach:
 * <ul>
 * <li>A console application consists of one or several possible user commands;</li>
 * <li>Each user command is implemented as a class extending {@link TMConsoleCommand};</li>
 * <li>User specifies which command to run on the command line;</li>
 * <li>The command processes the user request with the specified parameters.</li>
 * </ul>
 *
 * The command classes reside in the directory {@link getCommandPath commandPath}.
 * The name of the class follows the pattern: &lt;command-name&gt;Command, and its
 * file name is the same as the class name. For example, the 'ShellCommand' class defines
 * a 'shell' command and the class file name is 'ShellCommand.php'.
 *
 * To run the console application, enter the following on the command line:
 * <pre>
 * php path/to/entry_script.php <command name> [param 1] [param 2] ...
 * </pre>
 *
 * You may use the following to see help instructions about a command:
 * <pre>
 * php path/to/entry_script.php help <command name>
 * </pre>
 *
 * @property string $commandPath The directory that contains the command classes. Defaults to 'commands'.
 * @property TMConsoleCommandRunner $commandRunner The command runner.
 * @property TMConsoleCommand $command The currently active command.
 */

use taomee\base\Application;

class TMConsoleApplication extends Application
{
    /**
     * @var array mapping from command name to command configurations.
     * Each command configuration can be either a string or an array.
     * If the former, the string should be the file path of the command class.
     * If the latter, the array must contain a 'class' element which specifies
     * the command's class name or {@link TMBase::getPathOfAlias class path alias}.
     * The rest name-value pairs in the array are used to initialize
     * the corresponding command properties. For example,
     * <pre>
     * array(
     *   'email'=>array(
     *      'class'=>'path.to.Mailer',
     *      'interval'=>3600,
     *   ),
     *   'log'=>'path/to/LoggerCommand.php',
     * )
     * </pre>
     */
    public $commandMap = array();

    /**
     * @var string The directory that contains the command classes. Defaults to 'commands'.
     */
    private $_commandPath;

    /**
     * @var TMConsoleCommandRunner The command runner.
     */
    private $_runner;

    /**
     * Initializes the application by creating the command runner.
     */
    public function init()
    {
        parent::init();
        if (PHP_SAPI !== 'cli') {
            die('This script must be run from the command line.');
        }
        $this->_runner = $this->createCommandRunner();
        $this->_runner->commands = $this->commandMap;
        $this->_runner->addCommands($this->getCommandPath());
    }

    /**
     * Processes the user request.
     * This method uses a console command runner to handle the particular user command.
     */
    public function processRequest()
    {
        $this->_runner->run($_SERVER['argv']);
    }

    /**
     * Creates the command runner instance.
     * @return TMConsoleCommandRunner the command runner
     */
    protected function createCommandRunner()
    {
        return new TMConsoleCommandRunner;
    }

    /**
     * Displays the captured PHP error.
     * This method displays the error in console mode when there is
     * no active error handler.
     * @param integer $code error code
     * @param string $message error message
     * @param string $file error file
     * @param string $line error line
     */
    public function displayError($code, $message, $file, $line)
    {
        echo "PHP Error[$code]: $message\n";
        echo "    in file $file at line $line\n";
        $trace = debug_backtrace();
        // skip the first 4 stacks as they do not tell the error position
        if (count($trace) > 4) {
            $trace = array_slice($trace,4);
        }
        foreach ($trace as $i => $t) {
            if (!isset($t['file'])) {
                $t['file'] = 'unknown';
            }
            if (!isset($t['line'])) {
                $t['line'] = 0;
            }
            if (!isset($t['function'])) {
                $t['function'] = 'unknown';
            }
            echo "#$i {$t['file']}({$t['line']}): ";
            if (isset($t['object']) && is_object($t['object'])) {
                echo get_class($t['object']) . '->';
            }
            echo "{$t['function']}()\n";
        }
    }

    /**
     * Displays the uncaught PHP exception.
     * This method displays the exception in console mode when there is
     * no active error handler.
     * @param Exception $exception the uncaught exception
     */
    public function displayException($exception)
    {
        echo $exception;
    }

    /**
     * @return string the directory that contains the command classes. Defaults to 'commands'.
     */
    public function getCommandPath()
    {
        $applicationCommandPath = $this->getBasePath() . DIRECTORY_SEPARATOR . 'commands';
        if ($this->_commandPath === null && file_exists($applicationCommandPath)) {
            $this->setCommandPath($applicationCommandPath);
        }
        return $this->_commandPath;
    }

    /**
     * @param string $value the directory that contains the command classes.
     * @throws TMException if the directory is invalid
     */
    public function setCommandPath($value)
    {
        if(($this->_commandPath = realpath($value)) === false || !is_dir($this->_commandPath)) {
            throw new TMException(TM::t('taomee',
                'The command path "{path}" is not a valid directory.',
                array('{path}' => $value)
            ));
        }
    }

    /**
     * Returns the command runner.
     * @return TMConsoleCommandRunner the command runner.
     */
    public function getCommandRunner()
    {
        return $this->_runner;
    }

    /**
     * Returns the currently running command.
     * This is shortcut method for {@link TMConsoleCommandRunner::getCommand()}.
     * @return TMConsoleCommand|null the currently active command.
     */
    public function getCommand()
    {
        return $this->getCommandRunner()->getCommand();
    }

    /**
     * This is shortcut method for {@link TMConsoleCommandRunner::setCommand()}.
     * @param TMConsoleCommand $value the currently active command.
     */
    public function setCommand($value)
    {
        $this->getCommandRunner()->setCommand($value);
    }
}
